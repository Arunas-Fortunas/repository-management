package com.platform_lunar.homework.services;

import com.platform_lunar.homework.domain.PopularityMetric;
import com.platform_lunar.homework.dtos.PopularRepositoryDto;

import java.util.Collection;

public interface GithubService {
    Collection<PopularRepositoryDto> findPopularRepositories(String language, Integer items, PopularityMetric popularityMetric);

    Integer getContributorsCount(String url);

    Boolean isStarredByUser(String login, String authorization, String repoOwner, String repoName);

    void starRepo(String login, String authorization, String repoOwner, String repoName);

    void unstarRepo(String login, String authorization, String repoOwner, String repoName);
}
