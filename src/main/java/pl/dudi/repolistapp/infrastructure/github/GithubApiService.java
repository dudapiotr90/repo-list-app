package pl.dudi.repolistapp.infrastructure.github;

import github.api.responses.branches.BranchesSchema;
import github.api.responses.repos.ReposSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.dudi.repolistapp.dto.Branch;
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.infrastructure.mapper.GithubResponseMapper;
import pl.dudi.repolistapp.service.ApiService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubApiService implements ApiService {

    public static final String USER_REPO_ENDPOINT = "/users/%s/repos";
    public static final String REPO_BRANCHES_ENDPOINT = "/repos/%s/%s/branches";

    private final WebClient webClient;
    private final GithubResponseMapper mapper;

    @Override
    public List<UserRepository> getNonForkRepositories(String name) {
        List<ReposSchema> repoResponse = getGithubRepos(name);
        List<UserRepository> repositories = filterAndMap(repoResponse);

        return repositories.stream()
            .map(repo -> repo.withBranches(assignBranches(repo)))
            .toList();
    }

    private List<Branch> assignBranches(UserRepository repo) {
        List<BranchesSchema> branches = webClient.get()
            .uri(String.format(REPO_BRANCHES_ENDPOINT, repo.getOwnerLogin(), repo.getRepositoryName()))
            .retrieve()
            .bodyToFlux(BranchesSchema.class)
            .collectList()
            .block();

        if (Objects.isNull(branches)) {
            return List.of();
        }
        return branches.stream()
            .map(mapper::map)
            .toList();
    }


    private List<ReposSchema> getGithubRepos(String name) {
        List<ReposSchema> githubRepos = webClient
            .get()
            .uri(String.format(USER_REPO_ENDPOINT, name))
            .retrieve()
            .bodyToFlux(ReposSchema.class)
            .collectList()
            .block();

        if (Objects.isNull(githubRepos)) {
            return List.of();
        }
        return githubRepos;
    }

    private List<UserRepository> filterAndMap(List<ReposSchema> repoResponse) {
        return repoResponse.stream()
            .filter(repo -> !repo.getFork())
            .map(mapper::map)
            .toList();
    }
}
