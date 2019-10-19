package com.platform_lunar.homework.configurations.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("user")
public class UserProperties {
    private String login;
    private String password;
}
