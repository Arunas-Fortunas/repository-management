package com.platform_lunar.homework.domain;

public enum PopularityMetric {
    STARS("stars");

    private final String name;

    PopularityMetric(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
