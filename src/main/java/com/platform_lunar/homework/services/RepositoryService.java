package com.platform_lunar.homework.services;

import com.platform_lunar.homework.domain.Repository;
import com.platform_lunar.homework.domain.SortMetric;
import com.platform_lunar.homework.domain.SortOrder;

import java.util.List;

public interface RepositoryService {
    List<Repository> findBy(String login, String authorization, SortMetric sortMetric, SortOrder sortOrder);

    void starRepo(String login, String authorization, String repoOwner, String repoName);

    void unstarRepo(String login, String authorization, String repoOwner, String repoName);
}
