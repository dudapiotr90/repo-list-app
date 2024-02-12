package pl.dudi.repolistapp.util;

import pl.dudi.repolistapp.dto.UserRepository;

import java.util.List;

public class TestResponse {

    public static List<UserRepository> getSomeRepos() {
        return List.of(repo1(), repo2(), repo3(), repo4(), repo5());
    }

    private static UserRepository repo1() {
        return UserRepository.builder()
            .ownerLogin("dudapiotr90")
            .repositoryName("empty-repo")
            .build();
    }
    private static UserRepository repo2() {
        return UserRepository.builder()
            .ownerLogin("dudapiotr90")
            .repositoryName("food_orders")
            .build();
    }
    private static UserRepository repo3() {
        return UserRepository.builder()
            .ownerLogin("dudapiotr90")
            .repositoryName("Microservice-Demo")
            .build();
    }
    private static UserRepository repo4() {
        return UserRepository.builder()
            .ownerLogin("dudapiotr90")
            .repositoryName("repo-list-app")
            .build();
    }
    private static UserRepository repo5() {
        return UserRepository.builder()
            .ownerLogin("dudapiotr90")
            .repositoryName("automotive-app-config-server")
            .build();
    }

}
