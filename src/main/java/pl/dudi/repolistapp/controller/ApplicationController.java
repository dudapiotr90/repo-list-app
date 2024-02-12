package pl.dudi.repolistapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.service.ApiService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApplicationController.REPOS)
public class ApplicationController {

    public static final String REPOS = "/repos";
    public static final String USER = "/{username}";

    private final ApiService apiService;
    @GetMapping(value = USER,headers = "Accept=application/json")
    public ResponseEntity<List<UserRepository>> getNonForkRepositories(
        @RequestHeader(HttpHeaders.ACCEPT) MimeType accept,
        @PathVariable(name = "username") String username
    ) {
        List<UserRepository> response = apiService.getNonForkRepositories(username);
        return ResponseEntity.ok(response);
    }

}
