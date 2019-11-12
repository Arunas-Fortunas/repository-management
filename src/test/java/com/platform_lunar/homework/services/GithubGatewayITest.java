package com.platform_lunar.homework.services;

import com.platform_lunar.homework.Application;
import com.platform_lunar.homework.configurations.properties.CodeRepositoryServiceProperties;
import com.platform_lunar.homework.configurations.properties.CredentialsProperties;
import com.platform_lunar.homework.utils.AuthorizationUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = { "service.items=2" })
class GithubGatewayITest {
    private GithubGateway githubGateway;
    private CodeRepositoryServiceProperties codeRepositoryServiceProperties;
    private String login;
    private String authorization;

    @Autowired
    GithubGatewayITest(GithubGateway githubGateway, CredentialsProperties credentialsProperties, CodeRepositoryServiceProperties codeRepositoryServiceProperties) {
        this.githubGateway = githubGateway;
        this.codeRepositoryServiceProperties = codeRepositoryServiceProperties;
        this.login = credentialsProperties.getLogin();
        this.authorization = AuthorizationUtils.createEncodedAuthorization(login, credentialsProperties.getPassword());
    }

    @Test
    void findPopularRepositories() {
        var popularRepos = this.githubGateway.findPopularRepositories(
                codeRepositoryServiceProperties.getLanguage(),
                codeRepositoryServiceProperties.getItems(),
                codeRepositoryServiceProperties.getPopularityMetric());

        Assert.assertEquals(codeRepositoryServiceProperties.getItems().intValue(), popularRepos.size());

        popularRepos.forEach(popularRepo ->
                Assert.assertEquals(codeRepositoryServiceProperties.getLanguage(), popularRepo.getLanguage()));
    }

    @Test
    void getContributorsCount() {
        var contributorsUrl = "https://api.github.com/repos/iluwatar/java-design-patterns/contributors";
        var contributors = this.githubGateway.getContributorsCount(contributorsUrl);

        Assert.assertTrue(contributors > 200); // I assume contributors count cannot get less than it is now
    }

    private static final String REPO_OWNER = "ReactiveX";
    private static final String REPO_NAME = "RxJava";

    @Test
    void whenStarCompleted_ThenStarredByUser() {
        githubGateway.unstarRepo(login, authorization, REPO_OWNER, REPO_NAME);
        Assert.assertFalse(isStarred());

        githubGateway.starRepo(login, authorization, REPO_OWNER, REPO_NAME);
        Assert.assertTrue(isStarred());
    }

    @Test
    void whenUnstarCompleted_ThenNotStarredByUser() {
        githubGateway.starRepo(login, authorization, REPO_OWNER, REPO_NAME);
        Assert.assertTrue(isStarred());

        githubGateway.unstarRepo(login, authorization, REPO_OWNER, REPO_NAME);
        Assert.assertFalse(isStarred());
    }

    private boolean isStarred() {
        return githubGateway.isStarredByUser(login, authorization, REPO_OWNER, REPO_NAME);
    }
}
