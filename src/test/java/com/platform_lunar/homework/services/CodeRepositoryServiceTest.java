package com.platform_lunar.homework.services;

import com.platform_lunar.homework.configurations.properties.CodeRepositoryServiceProperties;
import com.platform_lunar.homework.domain.SortMetric;
import com.platform_lunar.homework.dtos.PopularRepositoryDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.platform_lunar.homework.domain.PopularityMetric.STARS;
import static com.platform_lunar.homework.domain.SortMetric.CONTRIBUTORS;
import static com.platform_lunar.homework.domain.SortOrder.ASC;
import static com.platform_lunar.homework.domain.SortOrder.DESC;

class CodeRepositoryServiceTest {
    private GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
    private CodeRepositoryServiceProperties codeRepositoryServiceProperties = new CodeRepositoryServiceProperties();
    private List<PopularRepositoryDto> popularRepos = createPopularRepositories(codeRepositoryServiceProperties.getLanguage());

    @BeforeEach
    void init() {
        codeRepositoryServiceProperties.setItems(10);
        codeRepositoryServiceProperties.setLanguage("java");
        codeRepositoryServiceProperties.setPopularityMetric(STARS);

        Mockito.when(
                githubGateway.findPopularRepositories(
                        codeRepositoryServiceProperties.getLanguage(),
                        codeRepositoryServiceProperties.getItems(),
                        codeRepositoryServiceProperties.getPopularityMetric()))
                .thenReturn(popularRepos);

        IntStream.rangeClosed(0, 9).forEach(
                i -> Mockito.when(githubGateway.getContributorsCount("contributors_url" + i))
                        .thenReturn(i));

        IntStream.rangeClosed(0, 9).forEach(
                i -> Mockito.when(githubGateway.isStarredByUser(
                        "login",
                        "authorization",
                        "owner" + i,
                        "name" + i))
                        .thenReturn(i % 2 == 0));
    }

    @Test
    void findBy_ContributorsDesc() {
        var repositoryService = new CodeRepositoryService(githubGateway, codeRepositoryServiceProperties);
        var repositories = repositoryService.findBy("login", "authorization", CONTRIBUTORS, DESC);

        IntStream.rangeClosed(0, 8).forEach(i -> {
            var effeRepository = repositories.get(i);
            var nextRepository = repositories.get(i + 1);
            Assert.assertTrue(effeRepository.getContributors() >= nextRepository.getContributors());
        });
    }

    @Test
    void findBy_StarsAsc() {
        var repositoryService = new CodeRepositoryService(githubGateway, codeRepositoryServiceProperties);
        var repositories = repositoryService.findBy("login", "authorization", SortMetric.STARS, ASC);

        IntStream.rangeClosed(0, 8).forEach(i -> {
            var effeRepository = repositories.get(i);
            var nextRepository = repositories.get(i + 1);
            Assert.assertTrue(effeRepository.getStars() <= nextRepository.getStars());
        });
    }

    // utils
    private static List<PopularRepositoryDto> createPopularRepositories(String language) {
        return IntStream.rangeClosed(0, 9)
                .mapToObj(i ->
                        new PopularRepositoryDto(
                                "name" + i,
                                "owner" + i,
                                "description",
                                language,
                                "licence",
                                "link",
                                i,
                                "contributors_url" + i))
                .collect(Collectors.toList());
    }
}
