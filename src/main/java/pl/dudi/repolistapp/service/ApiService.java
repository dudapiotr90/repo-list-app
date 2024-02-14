package pl.dudi.repolistapp.service;

import pl.dudi.repolistapp.dto.UserRepository;

import java.util.List;

public interface ApiService {
    List<UserRepository> getNonForkRepositories(String username);
}
