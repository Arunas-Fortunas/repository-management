package com.platform_lunar.homework.domain;

import lombok.Getter;

@Getter
public enum SortOrder {
    ASC("asc"),
    DESC("desc");

    private final String name;

    SortOrder(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
