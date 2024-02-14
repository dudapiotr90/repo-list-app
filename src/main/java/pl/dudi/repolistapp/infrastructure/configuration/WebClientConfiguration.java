package pl.dudi.repolistapp.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static pl.dudi.repolistapp.infrastructure.github.GithubDetails.DEFAULT_GITHUB_ACCEPT_HEADER;

@Configuration
public class WebClientConfiguration {


    @Value("${api.github.url}")
    private String GITHUB_API_URL;
    @Bean
    public WebClient webClient(final ObjectMapper objectMapper) {
        return WebClient.builder()
            .baseUrl(GITHUB_API_URL)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, DEFAULT_GITHUB_ACCEPT_HEADER)
            .exchangeStrategies(exchangeStrategies(objectMapper))
            .build();
    }

    private ExchangeStrategies exchangeStrategies(ObjectMapper objectMapper) {
        return ExchangeStrategies.builder()
            .codecs(config -> {
                config
                    .defaultCodecs()
                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                config
                    .defaultCodecs()
                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            })
            .build();
    }
}
