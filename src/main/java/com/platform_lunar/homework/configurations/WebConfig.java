package com.platform_lunar.homework.configurations;

import com.platform_lunar.homework.domain.SortMetric;
import com.platform_lunar.homework.domain.SortOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, SortMetric.class, source -> SortMetric.valueOf(source.toUpperCase()));
        registry.addConverter(String.class, SortOrder.class, source -> SortOrder.valueOf(source.toUpperCase()));
    }
}
