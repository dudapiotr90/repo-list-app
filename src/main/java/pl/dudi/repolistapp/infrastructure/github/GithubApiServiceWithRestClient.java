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
                .map(r -> getUserRepositoryConcurrently(r, executorService))
                .toList();
        }
    }

    private List<ReposSchema> getGithubReposWithRestClient(String name) {
        log.info("Connecting to Github Api... Fetching repositories");
        return restClient
            .get()
            .uri(USER_REPO_ENDPOINT, name)
            .retrieve()
            .onStatus(s -> s.isSameCodeAs(HttpStatus.NOT_FOUND),
                (request, response) -> {
                    throw getUserNotFoundException(name);
                })
            .onStatus(s -> s.value() == 403,
                (request, response) -> {
                    throw getRequestPerHourExceededException();
                })
            .body(new ParameterizedTypeReference<>() {
            });
    }

    private UserRepository getUserRepositoryConcurrently(UserRepository r, ExecutorService executorService) {
        Future<List<BranchesSchema>> future = executorService.submit(() -> getBranches(r));
        try {
            return r.withBranches(mapBranches(future.get(), executorService));
        } catch (InterruptedException e) {
            throw handleInterruptedException(future);
        } catch (ExecutionException e) {
            throw handleExecutionException(e);
        }
    }

    private List<BranchesSchema> getBranches(UserRepository repository) {
        log.info("New thread created: {}", Thread.currentThread().threadId());
        log.info("Connecting to Github Api... Fetching branches");
        return restClient.get()
            .uri(REPO_BRANCHES_ENDPOINT, repository.ownerLogin(), repository.repositoryName())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                (request, response) -> {
                    throw getUserNotFoundException(repository.ownerLogin());
                })
            .onStatus(s -> s.value() == 403, (request, response) -> {
                throw getRequestPerHourExceededException();
            })
            .body(new ParameterizedTypeReference<>() {
            });
    }

    private List<Branch> mapBranches(List<BranchesSchema> branchesSchemas, ExecutorService executorService) {
        return branchesSchemas.stream()
            .map(b -> executorService.submit(() -> map(b)))
            .map(this::getBranch)
            .toList();
    }

    private Branch getBranch(Future<Branch> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw handleInterruptedException(future);
        } catch (ExecutionException e) {
            throw handleExecutionException(e);
        }
    }

    private RuntimeInterruptedException handleInterruptedException(Future<?> future) {
        future.cancel(true);
        log.error("Execution cancelled for thread: [{}]", Thread.currentThread().threadId());
        return new RuntimeInterruptedException("Task interrupted");
    }

    private RuntimeExecutionException handleExecutionException(ExecutionException e) {
        log.error(
            "ExecutionException occurred for Thread [{}]. Reason: [{}]",
            Thread.currentThread().threadId(),e.getMessage()
        );
        return new RuntimeExecutionException(e.getCause());
    }
}
