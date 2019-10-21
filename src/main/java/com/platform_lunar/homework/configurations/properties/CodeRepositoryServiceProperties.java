package com.platform_lunar.homework.configurations.properties;

import com.platform_lunar.homework.domain.PopularityMetric;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("service")
public class CodeRepositoryServiceProperties {
    private String language;
    private Integer items;
    private PopularityMetric popularityMetric;
}
