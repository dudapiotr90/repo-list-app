package pl.dudi.repolistapp.infrastructure.github;

import github.api.responses.branches.BranchesSchema;
import github.api.responses.repos.ReposSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pl.dudi.repolistapp.dto.Branch;
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.infrastructure.exception.RuntimeExecutionException;
import pl.dudi.repolistapp.infrastructure.exception.RuntimeInterruptedException;
import pl.dudi.repolistapp.infrastructure.mapper.GithubResponseMapper;
import pl.dudi.repolistapp.service.ApiService;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Primary
@Service("withRestClient")
@RequiredArgsConstructor
public class GithubApiServiceWithRestClient extends GithubResponseMapper implements ApiService, GithubDetails {

    private final RestClient restClient;

    @Override
    public List<UserRepository> getNonForkRepositories(String username) {
        List<ReposSchema> repoResponse = getGithubReposWithRestClient(username);
        List<UserRepository> repositories = filterAndMap(repoResponse);

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            return repositories.stream()
                .map(r -> getUserRepositoryConcurrently(r, executorService)).toList();
        }
    }

    private UserRepository getUserRepositoryConcurrently(UserRepository r, ExecutorService executorService) {
        Future<List<BranchesSchema>> future = executorService.submit(() -> getBranches(r));
        try {
            return r.withBranches(mapBranches(future.get()));
        } catch (InterruptedException e) {
            future.cancel(true);
            log.error("Execution cancelled");
            throw new RuntimeInterruptedException("Couldn't complete task");
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            throw new RuntimeExecutionException("Couldn't execute task");
        }
    }

    private List<ReposSchema> getGithubReposWithRestClient(String name) {
        return restClient
            .get()
            .uri(USER_REPO_ENDPOINT, name)
            .retrieve()
            .onStatus(s -> s.isSameCodeAs(HttpStatus.NOT_FOUND),
                (request, response) -> {
                    throw getUserNotFoundException(name);
                })
            .onStatus(s -> s.value() == 403,
                handleExceededRequestLimit())
            .body(new ParameterizedTypeReference<>() {
            });
    }

    private List<BranchesSchema> getBranches(UserRepository repository) {
        return restClient.get()
            .uri(REPO_BRANCHES_ENDPOINT, repository.ownerLogin(), repository.repositoryName())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                (request, response) -> {
                    throw getUserNotFoundException(repository.ownerLogin());
                })
            .onStatus(s -> s.value() == 403,
                handleExceededRequestLimit())
            .body(new ParameterizedTypeReference<>() {
            });
    }

    private List<Branch> mapBranches(List<BranchesSchema> branchesSchemas) {
        return branchesSchemas.stream()
            .map(this::map)
            .toList();
    }

}
