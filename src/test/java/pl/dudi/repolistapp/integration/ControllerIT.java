package pl.dudi.repolistapp.integration;

import org.junit.jupiter.api.Test;
import pl.dudi.repolistapp.dto.ErrorMessage;
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.integration.configuration.RestAssuredITBase;
import pl.dudi.repolistapp.integration.support.WiremockTestSupport;
import pl.dudi.repolistapp.util.TestResponse;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ControllerIT
    extends RestAssuredITBase
    implements WiremockTestSupport {

    @Test
    void applicationControllerWorksCorrectly() {
        // Given
        String username = "dudapiotr90";
        stubForRepos(wireMockServer, username);
        stubForBranches(wireMockServer, username);
        List<UserRepository> someRepos = TestResponse.getSomeRepos();

        // When
        List<UserRepository> repositories = getRepositories(username);

        // Then
        assertThat(repositories).hasSize(5);
        repositories.forEach(r -> assertThat(r.ownerLogin()).isEqualTo(username));
        assertThat(repositories).usingRecursiveComparison()
            .ignoringFields("branches")
            .ignoringCollectionOrder()
            .isEqualTo(someRepos);
    }

    @Test
    void applicationThrowsNotFoundCorrectly() {
        // Given
        String username = "nonExistingUser";
        stubForNotFoundUser(wireMockServer);

        // When
        ErrorMessage errorResponse = getRepositoriesForNotExistingUser(username);

        // Then
        assertThat(errorResponse.status()).matches(HttpStatus::isClientError);
        assertThat(errorResponse.message()).contains("Not found user with login");
    }
}
