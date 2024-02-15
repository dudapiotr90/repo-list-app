package pl.dudi.repolistapp;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.dudi.repolistapp.dto.ErrorMessage;
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.integration.support.WiremockTestSupport;
import pl.dudi.repolistapp.service.ApiService;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.dudi.repolistapp.controller.ApplicationController.REPOS;
import static pl.dudi.repolistapp.controller.ApplicationController.USER;
import static pl.dudi.repolistapp.util.TestResponse.getSomeRepos;

@ActiveProfiles("test")
@SpringBootTest(
    classes = RepoListApiApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationIT implements WiremockTestSupport {

    private static WireMockServer wireMockServer;

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    protected int port;
    @Autowired
    @Qualifier("withRestClient")
    private ApiService apiService;
    @BeforeAll
    static void beforeAll() {
        wireMockServer= new WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(9999)
                .notifier(new ConsoleNotifier(true))
                .asynchronousResponseEnabled(true)
                .jettyAcceptors(10)
        );
        wireMockServer.start();
    }

    @AfterEach
    void afterEach() {
        wireMockServer.resetAll();
    }

    @Test
    void userRepositoriesRetrievedCorrectly() {
        // Given
        String username = "dudapiotr90";
        stubForRepos(wireMockServer,username);
        stubForBranches(wireMockServer,username);

        // When
        List<UserRepository> repositories = webTestClient
            .get()
            .uri(REPOS + USER, username)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<List<UserRepository>>() {
            })
            .returnResult()
            .getResponseBody();

        // Then
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isEqualTo(5);
        assertThat(repositories).usingRecursiveComparison()
            .ignoringFields("branches")
            .ignoringCollectionOrder()
            .isEqualTo(getSomeRepos());
    }

    @ParameterizedTest
    @MethodSource("limitExceededParams")
    void applicationThrowsCorrectly(String expectedMessage, String username) {
        // Given
        stubForRequestLimitExceeded(wireMockServer);

        // When
        ErrorMessage resultMessage = webTestClient.get()
            .uri(REPOS + USER, username)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
            .expectBody(ErrorMessage.class)
            .returnResult()
            .getResponseBody();

        // Then
        assertThat(resultMessage).isNotNull();
        assertThat(resultMessage.message()).contains(expectedMessage);
    }

    private static Stream<Arguments> limitExceededParams() {
        return Stream.of(
            Arguments.of("API rate limit exceeded", "someUser"),
            Arguments.of("API rate limit exceeded", "anyUserNameThatExists")
        );
    }
    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

}
