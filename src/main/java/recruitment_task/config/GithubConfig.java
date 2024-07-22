package recruitment_task.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(GithubConfigProperties.class)
public class GithubConfig {
    
    @Bean
    public WebClient githubWebClient(WebClient.Builder builder, GithubConfigProperties props) {
        return builder
            .baseUrl(props.getUrl())
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .defaultHeader("Authorization", "Bearer " + props.getKey())
            .build();
    }

}
