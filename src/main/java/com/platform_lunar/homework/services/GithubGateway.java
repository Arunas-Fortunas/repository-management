package com.platform_lunar.homework.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.platform_lunar.homework.configurations.properties.GithubProperties;
import com.platform_lunar.homework.controllers.exceptions.DataRetrievalException;
import com.platform_lunar.homework.domain.PopularityMetric;
import com.platform_lunar.homework.dtos.PopularRepositoryDto;
import com.platform_lunar.homework.utils.GithubPageCountResolver;
import com.platform_lunar.homework.utils.HttpEntityCreator;
import io.vavr.control.Try;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.platform_lunar.homework.utils.HttpEntityCreator.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpMethod.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class GithubGateway {
    private final GithubProperties githubProperties;
    private final RestTemplate restTemplate;

    Collection<PopularRepositoryDto> findPopularRepositories(String language, Integer items, PopularityMetric popularityMetric) {
        var builder = UriComponentsBuilder.fromUriString(String.format("%s/search/repositories", githubProperties.getBaseUrl()))
                .queryParam(QUERY, String.format("language:%s", language))
                .queryParam(PAGE, 1)
                .queryParam(PER_PAGE, items)
                .queryParam(SORT, popularityMetric.getName())
                .queryParam(ORDER, "desc");

        var res = restTemplate.exchange(builder.buildAndExpand(Map.of()).toUri(),
                GET, HttpEntityCreator.create(), RepositoryInfoDto.class);

        final var repositoryData = res.getBody();
        if (!res.getStatusCode().is2xxSuccessful() || repositoryData == null)
            throw new DataRetrievalException(String.format("could not retrieve most popular %s repositories", language));

        return repositoryData.getItems().stream()
                .map(item -> new PopularRepositoryDto(
                        item.getName(),
                        item.getOwner().getLogin(),
                        item.getDescription(),
                        item.getLanguage(),
                        item.getLicense() != null ? item.getLicense().getName() : null,
                        item.getUrl(),
                        item.getStargazers_count(),
                        item.getContributors_url()))
                .collect(toList());
    }

    private static final int CACHE_SIZE = 1000;
    private Cache<String, Integer> contributorsCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(CACHE_SIZE)
            .build();

    Integer getContributorsCount(String url) {
        return contributorsCache.get(url, this::doGetContributorsCount);
    }

    private Integer doGetContributorsCount(String url) {
        log.debug("get contributors with: {}", url);

        var builder = UriComponentsBuilder.fromUriString(url)
                .queryParam(PER_PAGE, 1)
                .queryParam(ANONYMOUS, true);

        var res = restTemplate.exchange(builder.buildAndExpand(Map.of()).toUri(),
                HEAD, HttpEntityCreator.create(), Object.class);

        if (!res.getStatusCode().is2xxSuccessful())
            throw new DataRetrievalException(String.format("could not retrieve contributors count with url: %s", url));

        var link = res.getHeaders().getFirst(LINK);

        return GithubPageCountResolver.extractPageCount(link);
    }

    private Cache<String, Boolean> starredCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(CACHE_SIZE)
            .build();

    public Boolean isStarredByUser(String login, String authorization, String repoOwner, String repoName) {
        return starredCache.get(String.format("%s/%s/%s", login, repoOwner, repoName),
                x -> doIsStarredByUser(login, authorization, repoOwner, repoName));
    }

    private Boolean doIsStarredByUser(String login, String authorization, String repoOwner, String repoName) {
        log.debug("checking if starred [{}/{}] by {}", repoOwner, repoName, login);

        var url = String.format("%s/user/starred/{owner}/{name}", githubProperties.getBaseUrl());
        var builder = UriComponentsBuilder.fromUriString(url);
        var urlParams = Map.of(REPO_OWNER, repoOwner, REPO_NAME, repoName);

        return Try.of(() -> restTemplate.exchange(builder.buildAndExpand(urlParams).toUri(), GET,
                HttpEntityCreator.create(login, authorization), Object.class))
                .map(res -> res.getStatusCode().is2xxSuccessful())
                .onFailure(ex -> {
                    if (ex instanceof HttpClientErrorException) {
                        // if repository is not starred by user then NOT_FOUND is returned by Github API
                        if (((HttpClientErrorException) ex).getStatusCode() != HttpStatus.NOT_FOUND) {
                            throw new DataRetrievalException(
                                    String.format("could not determine if %s is starred by %s", repoName, login));
                        }
                    }
                })
                .getOrElse(false);
    }

    void starRepo(String login, String authorization, String repoOwner, String repoName) {
        doStarOrUnstarOperation(PUT, login, authorization, repoOwner, repoName);
    }

    void unstarRepo(String login, String authorization, String repoOwner, String repoName) {
        doStarOrUnstarOperation(DELETE, login, authorization, repoOwner, repoName);
    }

    private void doStarOrUnstarOperation(HttpMethod httpMethod, String login, String authorization, String repoOwner,
                                         String repoName) {

        // update the cache
        starredCache.put(String.format("%s/%s/%s", login, repoOwner, repoName), PUT == httpMethod);

        var url = String.format("%s/user/starred/{owner}/{name}", githubProperties.getBaseUrl());
        var builder = UriComponentsBuilder.fromUriString(url);
        var urlParams = Map.of(REPO_OWNER, repoOwner, REPO_NAME, repoName);

        restTemplate.exchange(builder.buildAndExpand(urlParams).toUri(),
                httpMethod, HttpEntityCreator.create(login, authorization), Object.class);
    }

    @Data
    private static final class RepositoryInfoDto {
        Collection<Item> items;

        @Data
        private final static class Item {
            String name;
            Owner owner;
            String description;
            String language;
            License license;
            String url;
            Integer stargazers_count;
            String contributors_url;
        }

        @Data
        private final static class License {
            String name;
        }

        @Data
        private final static class Owner {
            String login;
        }
    }
}
