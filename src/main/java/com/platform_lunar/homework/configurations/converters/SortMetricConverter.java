package com.platform_lunar.homework.configurations.converters;

import com.platform_lunar.homework.domain.SortMetric;
import org.springframework.core.convert.converter.Converter;

public class SortMetricConverter implements Converter<String, SortMetric> {
    @Override
    public SortMetric convert(String source) {
        return SortMetric.valueOf(source.toUpperCase());
    }
}
