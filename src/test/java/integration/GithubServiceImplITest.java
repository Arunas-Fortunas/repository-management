package integration;

import com.platform_lunar.homework.Application;
import com.platform_lunar.homework.configurations.properties.ServiceProperties;
import com.platform_lunar.homework.configurations.properties.UserProperties;
import com.platform_lunar.homework.services.GithubService;
import com.platform_lunar.homework.utils.AuthorizationUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class GithubServiceImplITest {
    private GithubService githubService;
    private UserProperties userProperties;
    private ServiceProperties serviceProperties;

    @Autowired
    GithubServiceImplITest(GithubService githubService, UserProperties userProperties, ServiceProperties serviceProperties) {
        this.githubService = githubService;
        this.userProperties = userProperties;
        this.serviceProperties = serviceProperties;
    }

    @Test
    void findPopularRepositories() {
        var popularRepos = this.githubService.findPopularRepositories(
                serviceProperties.getLanguage(),
                serviceProperties.getItems(),
                serviceProperties.getPopularityMetric());

        Assert.assertEquals(serviceProperties.getItems().intValue(), popularRepos.size());

        popularRepos.forEach(popularRepo ->
                Assert.assertEquals(serviceProperties.getLanguage(), popularRepo.getLanguage()));
    }

    @Test
    void getContributorsCount() {
        var contributorsUrl = "https://api.github.com/repos/iluwatar/java-design-patterns/contributors";
        var contributors = this.githubService.getContributorsCount(contributorsUrl);

        Assert.assertTrue(contributors > 200); // I assume contributors count cannot get less than it is now
    }

    private static final String REPO_OWNER = "ReactiveX";
    private static final String REPO_NAME = "RxJava";

    @Test
    void whenStarCompleted_ThenStarredByUser() {
        var login = userProperties.getLogin();
        var authorization = AuthorizationUtils.createEncodedAuthorization(login, userProperties.getPassword());

        githubService.starRepo(login, authorization, REPO_OWNER, REPO_NAME);

        Assert.assertTrue(githubService.isStarredByUser(login, authorization, REPO_OWNER, REPO_NAME));
    }

    @Test
    void whenUnstarCompleted_ThenNotStarredByUser() {
        var login = userProperties.getLogin();
        var authorization = AuthorizationUtils.createEncodedAuthorization(login, userProperties.getPassword());

        githubService.unstarRepo(login, authorization, REPO_OWNER, REPO_NAME);

        Assert.assertFalse(githubService.isStarredByUser(login, authorization, REPO_OWNER, REPO_NAME));
    }
}
