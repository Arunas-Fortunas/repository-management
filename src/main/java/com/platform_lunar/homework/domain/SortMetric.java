package com.platform_lunar.homework.domain;

import java.util.Comparator;

public enum SortMetric implements Comparator<Repository> {
    CONTRIBUTORS("contributors") {
        @Override
        public int compare(Repository repo1, Repository repo2) {
            return repo1.getContributors().compareTo(repo2.getContributors());
        }
    },
    STARS("stars") {
        @Override
        public int compare(Repository repo1, Repository repo2) {
            return repo1.getStars().compareTo(repo2.getStars());
        }
    };

    private final String name;

    SortMetric(String name) {
        this.name = name;
    }

    public abstract int compare(Repository repo1, Repository repo2);

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
