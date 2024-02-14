package pl.dudi.repolistapp.infrastructure.github;

import org.springframework.web.client.RestClient;
import pl.dudi.repolistapp.infrastructure.exception.RequestPerHourExceededException;
import pl.dudi.repolistapp.infrastructure.exception.UserNotFoundException;

public interface GithubDetails {

    String DEFAULT_GITHUB_ACCEPT_HEADER = "application/vnd.github+json";
    String USER_REPO_ENDPOINT = "/users/{username}/repos";
    String REPO_BRANCHES_ENDPOINT = "/repos/{owner}/{repo}/branches";

    default UserNotFoundException getUserNotFoundException(String name) {
        return new UserNotFoundException(
            String.format("Not found user with login: [%s]", name));
    }

    default RequestPerHourExceededException getRequestPerHourExceededException() {
        return new RequestPerHourExceededException(
            "API rate limit exceeded for your ip address. Max 60 requests/hour"
        );
    }

    default RestClient.ResponseSpec.ErrorHandler handleExceededRequestLimit() {
        return (request, response) -> {
            throw getRequestPerHourExceededException();
        };
    }

}
