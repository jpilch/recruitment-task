package recruitment_task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "github-api")
public class GithubConfigProperties {
    
    private String url;
    private String key;
}
