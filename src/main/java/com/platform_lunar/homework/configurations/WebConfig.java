package com.platform_lunar.homework.configurations;

import com.platform_lunar.homework.configurations.converters.SortMetricConverter;
import com.platform_lunar.homework.configurations.converters.SortOrderConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SortMetricConverter());
        registry.addConverter(new SortOrderConverter());
    }
}
