package pl.dudi.repolistapp.integration.support;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pl.dudi.repolistapp.controller.ApplicationController;
import pl.dudi.repolistapp.dto.ErrorMessage;
import pl.dudi.repolistapp.dto.UserRepository;

import java.util.Arrays;
import java.util.List;

public interface IntegrationTestSupport {

    RequestSpecification requestSpecification();

    default List<UserRepository> getRepositories(
        String username
    ) {
        return Arrays.stream(repoRequest(username)
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .extract()
            .response()
            .as(UserRepository[].class)).toList();
    }

    default ErrorMessage getRepositoriesForNotExistingUser(
        String username
    ) {
        return repoRequest(username)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .and()
            .extract()
            .response()
            .as(ErrorMessage.class);
    }

    private Response repoRequest(String username) {
        return requestSpecification()
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .pathParam("username", username)
            .get(ApplicationController.REPOS + ApplicationController.USER);
    }
}
