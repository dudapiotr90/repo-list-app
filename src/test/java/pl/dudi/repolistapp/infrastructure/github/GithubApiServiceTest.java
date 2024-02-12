package pl.dudi.repolistapp.infrastructure.github;

import github.api.responses.branches.BranchesSchema;
import github.api.responses.repos.ReposSchema;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import pl.dudi.repolistapp.dto.Branch;
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.infrastructure.mapper.GithubResponseMapper;
import pl.dudi.repolistapp.util.GithubResponse;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.*;
import static pl.dudi.repolistapp.infrastructure.github.GithubApiService.REPO_BRANCHES_ENDPOINT;
import static pl.dudi.repolistapp.infrastructure.github.GithubApiService.USER_REPO_ENDPOINT;

@ExtendWith(MockitoExtension.class)
class GithubApiServiceTest {

    @Mock
    private GithubResponseMapper mapper;

    @InjectMocks
    private GithubApiService githubApiService;

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Test
    void getNonForkRepositoriesWorksCorrectly() {
        // Given
        String username = "someName";
        List<ReposSchema> someGithubRepos = GithubResponse.someGithubRepos(username);
        UserRepository repository1 = UserRepository.builder()
            .ownerLogin(username)
            .repositoryName(someGithubRepos.get(0).getName())
            .build();
        UserRepository repository2 = UserRepository.builder()
            .ownerLogin(username)
            .repositoryName(someGithubRepos.get(1).getName())
            .build();
        List<BranchesSchema> someBranches1 = GithubResponse.someBranches1();
        List<BranchesSchema> someBranches2 = GithubResponse.someBranches2();

        Branch branch1 = Branch.builder()
            .build();
        Branch branch2 = Branch.builder()
            .name(someBranches2.get(0).getName())
            .lastCommitSha(someBranches2.get(0).getCommit().getSha())
            .build();
        Branch branch3 = Branch.builder()
            .name(someBranches2.get(1).getName())
            .lastCommitSha(someBranches2.get(1).getCommit().getSha())
            .build();

        mockForMapper(repository1, branch1, repository2, branch2, branch3);
        mockForReposCall(someGithubRepos);
        mockForBranchesCall(someBranches1, someBranches2);

        // When
        List<UserRepository> repositories = githubApiService.getNonForkRepositories(username);

        // Then
        verify(webClient, times(3)).get();
        verify(webClient.get(), times(1)).uri(String.format(USER_REPO_ENDPOINT, username));
        verify(webClient.get(), times(1)).uri(String.format(REPO_BRANCHES_ENDPOINT, repository1.getOwnerLogin(), repository1.getRepositoryName()));
        verify(webClient.get(), times(1)).uri(String.format(REPO_BRANCHES_ENDPOINT, repository2.getOwnerLogin(), repository2.getRepositoryName()));
        Assertions.assertThat(repositories.size()).isLessThan(someGithubRepos.size());
    }

    @SuppressWarnings("unchecked")
    private void mockForReposCall(List<ReposSchema> someGithubRepos) {
        when(webClient.get())
            .thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(ArgumentMatchers.matches("\\/users\\/[^\\/]+\\/repos")))
            .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve())
            .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ReposSchema.class)).thenReturn(Flux.fromIterable(someGithubRepos));
    }

    private void mockForMapper(UserRepository repository1, Branch branch1, UserRepository repository2, Branch branch2, Branch branch3) {
        when(mapper.map(any(ReposSchema.class)))
            .thenReturn(repository1.withBranches(List.of(branch1)),
                repository2.withBranches(List.of(branch2, branch3))
            );
        when(mapper.map(any(BranchesSchema.class)))
            .thenReturn(branch1, branch2, branch3);
    }

    @SuppressWarnings("unchecked")
    private void mockForBranchesCall(List<BranchesSchema> someBranches1, List<BranchesSchema> someBranches2) {
        when(webClient.get())
            .thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(ArgumentMatchers.matches("\\/repos\\/[^\\/]+\\/[^\\/]+\\/branches")))
            .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve())
            .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(BranchesSchema.class)).thenReturn(Flux.fromIterable(someBranches1),Flux.fromIterable(someBranches2));
    }
}