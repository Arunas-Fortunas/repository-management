package com.platform_lunar.homework.domain;

import lombok.Getter;

@Getter
public enum PopularityMetric {
    STARS("stars");

    private final String name;

    PopularityMetric(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
