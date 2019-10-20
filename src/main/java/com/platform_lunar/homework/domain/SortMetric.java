package com.platform_lunar.homework.domain;

import java.util.Comparator;

public enum SortMetric implements Comparator<CodeRepository> {
    CONTRIBUTORS("contributors") {
        @Override
        public int compare(CodeRepository repo1, CodeRepository repo2) {
            return repo1.getContributors().compareTo(repo2.getContributors());
        }
    },
    STARS("stars") {
        @Override
        public int compare(CodeRepository repo1, CodeRepository repo2) {
            return repo1.getStars().compareTo(repo2.getStars());
        }
    };

    private final String name;

    SortMetric(String name) {
        this.name = name;
    }

    public abstract int compare(CodeRepository repo1, CodeRepository repo2);

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
