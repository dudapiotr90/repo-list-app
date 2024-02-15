package pl.dudi.repolistapp.integration.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static pl.dudi.repolistapp.infrastructure.github.GithubDetails.DEFAULT_GITHUB_ACCEPT_HEADER;

public interface WiremockTestSupport {
    Map<String, String> BRANCHES = Map.of(
        "automotive-app-config-server", "branches-1.json",
        "empty-repo", "branches-2.json",
        "food_orders", "branches-3.json",
        "Microservice-Demo", "branches-4.json",
        "repo-list-app", "branches-5.json"
    );
    String USER_REPO_ENDPOINT = "/users/%s/repos";
    String REPO_BRANCHES_ENDPOINT = "/repos/%s/%s/branches";

    default void stubForBranches(
        WireMockServer wireMockServer,
        String ownerLogin
    ) {
        BRANCHES.forEach((repositoryName, fileName) ->
            wireMockServer.stubFor(get(REPO_BRANCHES_ENDPOINT.formatted(ownerLogin, repositoryName))
                .withHeader(HttpHeaders.ACCEPT, including(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(HttpHeaders.CONTENT_TYPE, including(DEFAULT_GITHUB_ACCEPT_HEADER))
                .willReturn(aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("wiremock/branch/%s".formatted(fileName))
                    .withStatus(HttpStatus.OK.value())
                )
            )
        );
    }

    default void stubForRepos(
        WireMockServer wireMockServer,
        String username
    ) {
        wireMockServer.stubFor(get(USER_REPO_ENDPOINT.formatted(username))
            .withHeader(HttpHeaders.ACCEPT, including(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, including(DEFAULT_GITHUB_ACCEPT_HEADER))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("wiremock/repos.json")
                .withStatus(HttpStatus.OK.value())
            )
        );
    }

    default void stubForNotFoundUser(WireMockServer wireMockServer) {
        wireMockServer.stubFor(get(USER_REPO_ENDPOINT.formatted("nonExistingUser"))
            .withHeader(HttpHeaders.ACCEPT, including(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.CONTENT_TYPE, including(DEFAULT_GITHUB_ACCEPT_HEADER))
            .willReturn(aResponse()
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withBodyFile("wiremock/errors/error1.json")
            )
        );
    }
    default void stubForRequestLimitExceeded(WireMockServer wireMockServer) {
        wireMockServer.stubFor(get(urlPathMatching("/users/[^/]+/repos"))
            .withHeader(HttpHeaders.ACCEPT, including(MediaType.APPLICATION_JSON_VALUE) )
            .withHeader(HttpHeaders.CONTENT_TYPE, including(DEFAULT_GITHUB_ACCEPT_HEADER))
            .willReturn(aResponse()
                .withStatus(403)));
    }


}
