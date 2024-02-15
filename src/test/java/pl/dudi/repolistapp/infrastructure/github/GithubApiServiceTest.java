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
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.util.GithubResponse;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.regex.Pattern;

import static org.mockito.Mockito.*;
import static pl.dudi.repolistapp.infrastructure.github.GithubDetails.REPO_BRANCHES_ENDPOINT;
import static pl.dudi.repolistapp.infrastructure.github.GithubDetails.USER_REPO_ENDPOINT;

@ExtendWith(MockitoExtension.class)
class GithubApiServiceTest {

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

        UserRepository repository1 = new UserRepository(someGithubRepos.get(0).getName(),username, null);
        UserRepository repository2 = repository1.withRepositoryName(someGithubRepos.get(1).getName());

        List<BranchesSchema> someBranches1 = GithubResponse.someBranches1();
        List<BranchesSchema> someBranches2 = GithubResponse.someBranches2();

        mockForReposCall(someGithubRepos);
        mockForBranchesCall(someBranches1, someBranches2);

        // When
        List<UserRepository> repositories = githubApiService.getNonForkRepositories(username);

        // Then
        verify(webClient, times(3)).get();
        verify(webClient.get(), times(1)).uri(USER_REPO_ENDPOINT, username);
        verify(webClient.get(), times(1)).uri(REPO_BRANCHES_ENDPOINT, username, repository1.repositoryName());
        verify(webClient.get(), times(1)).uri(REPO_BRANCHES_ENDPOINT, username, repository2.repositoryName());
        Assertions.assertThat(repositories.size()).isLessThan(someGithubRepos.size());
    }

    @SuppressWarnings("unchecked")
    private void mockForReposCall(List<ReposSchema> someGithubRepos) {
        when(webClient.get())
            .thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(ArgumentMatchers.matches(Pattern.compile("/users/[^/]+/repos")),anyString()))
            .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve())
            .thenReturn(responseSpec);
        when(responseSpec.onStatus(ArgumentMatchers.any(),any())).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ReposSchema.class)).thenReturn(Flux.fromIterable(someGithubRepos));
    }


    @SuppressWarnings("unchecked")
    private void mockForBranchesCall(List<BranchesSchema> someBranches1, List<BranchesSchema> someBranches2) {
        when(webClient.get())
            .thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(ArgumentMatchers.matches(Pattern.compile("/repos/[^/]+/[^/]+/branches")),anyString(),anyString()))
            .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve())
            .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(BranchesSchema.class)).thenReturn(Flux.fromIterable(someBranches1),Flux.fromIterable(someBranches2));
    }
}