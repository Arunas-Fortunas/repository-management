package com.platform_lunar.homework.domain;

public enum SortOrder {
    ASC("asc"),
    DESC("desc");

    private final String name;

    SortOrder(String name) {
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
