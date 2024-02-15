package pl.dudi.repolistapp.infrastructure.github;

import github.api.responses.branches.BranchesSchema;
import github.api.responses.repos.ReposSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.dudi.repolistapp.dto.Branch;
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.infrastructure.mapper.GithubResponseMapper;
import pl.dudi.repolistapp.service.ApiService;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service("withWebClient")
@RequiredArgsConstructor
public class GithubApiService extends GithubResponseMapper implements ApiService, GithubDetails {

    private final WebClient webClient;

    @Override
    public List<UserRepository> getNonForkRepositories(String username) {
        List<ReposSchema> repoResponse = getGithubRepos(username);
        List<UserRepository> repositories = filterAndMap(repoResponse);

        return repositories.stream()
            .map(repo -> repo.withBranches(getBranches(repo)))
            .toList();
    }


    private List<ReposSchema> getGithubRepos(String name){
        log.info("Connecting to Github Api...");
        return webClient
            .get()
            .uri(USER_REPO_ENDPOINT, name)
            .retrieve()
            .onStatus(statusCode -> statusCode.isSameCodeAs(HttpStatus.NOT_FOUND),
                response -> Mono.error(getUserNotFoundException(name)))
            .onStatus(s -> s.value() == 403,
                response -> Mono.error(getRequestPerHourExceededException()))
            .bodyToFlux(ReposSchema.class)
            .collectList()
            .block();

    }

    private List<Branch> getBranches(UserRepository repo) {
        log.info("Connecting to Github Api...");
        List<BranchesSchema> branches = webClient
            .get()
            .uri(REPO_BRANCHES_ENDPOINT, repo.ownerLogin(), repo.repositoryName())
            .retrieve()
            .onStatus(s -> s.value() == 403,
                response -> Mono.error(getRequestPerHourExceededException()))
            .bodyToFlux(BranchesSchema.class)
            .collectList()
            .blockOptional()
            .orElseGet(List::of);

        return branches.stream()
            .map(this::map)
            .toList();
    }
}
