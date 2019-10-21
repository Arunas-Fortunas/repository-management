package com.platform_lunar.homework.services;

import com.platform_lunar.homework.configurations.properties.ServiceProperties;
import com.platform_lunar.homework.controllers.exceptions.DataRetrievalException;
import com.platform_lunar.homework.domain.CodeRepository;
import com.platform_lunar.homework.domain.SortMetric;
import com.platform_lunar.homework.domain.SortOrder;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
public class CodeRepositoryService {
    private final GithubGateway githubGateway;
    private final ServiceProperties serviceProperties;

    public CodeRepositoryService(GithubGateway githubGateway, ServiceProperties serviceProperties) {
        this.githubGateway = githubGateway;
        this.serviceProperties = serviceProperties;
    }

    public List<CodeRepository> findBy(String login, String authorization, SortMetric sortMetric, SortOrder sortOrder) {
        var popularRepos = githubGateway.findPopularRepositories(
                serviceProperties.getLanguage(),
                serviceProperties.getItems(),
                serviceProperties.getPopularityMetric());

        var synchronizedCodeRepoList = Collections.synchronizedList(new ArrayList<CodeRepository>(serviceProperties.getItems()));
        var latch = new CountDownLatch(serviceProperties.getItems());

        var executorService = Executors.newFixedThreadPool(serviceProperties.getItems());

        for (var popularRepo : popularRepos) {
            executorService.execute(() -> {
                int contributors = githubGateway.getContributorsCount(popularRepo.getContributorsUrl());

                Boolean starredByUser = hasText(login) && hasText(authorization)
                        ? githubGateway.isStarredByUser(login, authorization, popularRepo.getOwnerLogin(), popularRepo.getName())
                            : null;

                synchronizedCodeRepoList.add(new CodeRepository(
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

        synchronizedCodeRepoList.sort(sortOrder == SortOrder.ASC ? sortMetric : sortMetric.reversed());
        return synchronizedCodeRepoList;
    }

    public void starRepo(String login, String authorization, String repoOwner, String repoName) {
        githubGateway.starRepo(login, authorization, repoOwner, repoName);
    }

    public void unstarRepo(String login, String authorization, String repoOwner, String repoName) {
        githubGateway.unstarRepo(login, authorization, repoOwner, repoName);
    }
}
