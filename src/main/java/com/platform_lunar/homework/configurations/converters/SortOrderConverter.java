package com.platform_lunar.homework.configurations.converters;

import com.platform_lunar.homework.domain.SortOrder;
import org.springframework.core.convert.converter.Converter;

public class SortOrderConverter implements Converter<String, SortOrder> {
    @Override
    public SortOrder convert(String source) {
        return SortOrder.valueOf(source.toUpperCase());
    }
}
