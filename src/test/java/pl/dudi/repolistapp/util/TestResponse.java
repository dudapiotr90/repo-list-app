package pl.dudi.repolistapp.util;

import pl.dudi.repolistapp.dto.UserRepository;

import java.util.List;

public class TestResponse {

    public static final String SOME_USER = "dudapiotr90";
    public static List<UserRepository> getSomeRepos() {
        return List.of(repo1(), repo2(), repo3(), repo4(), repo5());
    }

    private static UserRepository repo1() {
        return new UserRepository(
            "empty-repo",
            SOME_USER,
            null
        );
    }
    private static UserRepository repo2() {
        return new UserRepository(
            "food_orders",
            SOME_USER,
            null
        );
    }
    private static UserRepository repo3() {
        return new UserRepository(
            "Microservice-Demo",
            SOME_USER,
            null
        );
    }
    private static UserRepository repo4() {
        return new UserRepository(
            "repo-list-app",
            SOME_USER,
            null
        );
    }
    private static UserRepository repo5() {
        return new UserRepository(
            "automotive-app-config-server",
            SOME_USER,
            null
        );
    }

}
