package com.platform_lunar.homework.services;

import com.platform_lunar.homework.configurations.properties.ServiceProperties;
import com.platform_lunar.homework.controllers.exceptions.DataRetrievalException;
import com.platform_lunar.homework.domain.Repository;
import com.platform_lunar.homework.domain.SortMetric;
import com.platform_lunar.homework.domain.SortOrder;
import com.platform_lunar.homework.services.GithubService;
import com.platform_lunar.homework.services.RepositoryService;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.springframework.util.StringUtils.hasText;

@Component
public class RepositoryService {
    private final GithubService githubService;
    private final ServiceProperties serviceProperties;

    @Autowired
    public RepositoryService(GithubService githubService, ServiceProperties serviceProperties) {
        this.githubService = githubService;
        this.serviceProperties = serviceProperties;
    }

    public List<Repository> findBy(String login, String authorization, SortMetric sortMetric, SortOrder sortOrder) {
        var popularRepos = githubService.findPopularRepositories(
                serviceProperties.getLanguage(),
                serviceProperties.getItems(),
                serviceProperties.getPopularityMetric());

        var result = Collections.synchronizedList(new ArrayList<Repository>(serviceProperties.getItems()));
        var latch = new CountDownLatch(serviceProperties.getItems());

        var executorService = Executors.newFixedThreadPool(serviceProperties.getItems());

        for (var popularRepo : popularRepos) {
            executorService.execute(() -> {
                int contributors = githubService.getContributorsCount(popularRepo.getContributorsUrl());

                Boolean starredByUser = hasText(login) && hasText(authorization)
                        ? githubService.isStarredByUser(login, authorization, popularRepo.getOwnerLogin(), popularRepo.getName())
                            : null;

                result.add(new Repository(
                        popularRepo.getName(),
                        popularRepo.getDescription(),
                        popularRepo.getLicenceName(),
                        popularRepo.getLinkToRepo(),
                        starredByUser,
                        contributors,
                        popularRepo.getStars()));

                latch.countDown();
            });
        }

        Try.run(() -> {
            latch.await();
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }).getOrElseThrow(() -> new DataRetrievalException("could not find repositories"));

        result.sort(sortOrder == SortOrder.ASC ? sortMetric : sortMetric.reversed());
        return result;
    }

    public void starRepo(String login, String authorization, String repoOwner, String repoName) {
        githubService.starRepo(login, authorization, repoOwner, repoName);
    }

    public void unstarRepo(String login, String authorization, String repoOwner, String repoName) {
        githubService.unstarRepo(login, authorization, repoOwner, repoName);
    }
}
