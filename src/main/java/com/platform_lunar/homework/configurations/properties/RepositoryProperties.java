package com.platform_lunar.homework.configurations.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("repository")
public class RepositoryProperties {
    private String baseUrl;
}