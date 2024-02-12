package pl.dudi.repolistapp.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.dudi.repolistapp.controller.ApplicationController;
import pl.dudi.repolistapp.dto.ErrorMessage;
import pl.dudi.repolistapp.service.ApiService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApplicationController.class)
@AutoConfigureMockMvc
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockBean
    private ApiService apiService;

    @Test
    void getRepositoryThrowsBadRequestCorrectly() throws Exception {
        // Given
        String userName = "anyUserName";
        ErrorMessage message = ErrorMessage.of(
            400,
            "Header: [Accept=application/json] is required"
        );

        // When, Then
        mockMvc.perform(get(ApplicationController.REPOS + ApplicationController.USER, userName))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(message)));
    }

}
