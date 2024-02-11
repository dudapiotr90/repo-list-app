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
@RequestMapping("/users")
public class ApplicationController {

    public static final String USER_REPO = "/{name}/repos";

    private final ApiService apiService;
    @GetMapping(value = USER_REPO,headers = "Accept=application/json")
    public ResponseEntity<List<UserRepository>> getNonForkRepositories(
        @RequestHeader(HttpHeaders.ACCEPT) MimeType accept,
        @PathVariable(name = "name") String name
    ) {
        List<UserRepository> response = apiService.getNonForkRepositories(name);
        return ResponseEntity.ok(response);
    }

}
