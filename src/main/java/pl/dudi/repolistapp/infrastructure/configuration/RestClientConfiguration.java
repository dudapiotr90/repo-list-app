package pl.dudi.repolistapp.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.nio.charset.Charset;

import static pl.dudi.repolistapp.infrastructure.github.GithubDetails.DEFAULT_GITHUB_ACCEPT_HEADER;

@Configuration
public class RestClientConfiguration {

    @Value("${api.github.url}")
    private String GITHUB_API_URL;
    @Bean
    public RestClient restClient(ObjectMapper objectMapper) {
        return RestClient.builder()
            .baseUrl(GITHUB_API_URL)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, DEFAULT_GITHUB_ACCEPT_HEADER)
            .messageConverters(httpMessageConverters -> {
                httpMessageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));
                httpMessageConverters.add(new StringHttpMessageConverter(Charset.defaultCharset()));
            })
            .build();
    }
}
