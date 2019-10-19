package integration;

import com.platform_lunar.homework.Application;
import com.platform_lunar.homework.configurations.properties.ServiceProperties;
import com.platform_lunar.homework.configurations.properties.UserProperties;
import com.platform_lunar.homework.domain.SortMetric;
import com.platform_lunar.homework.domain.SortOrder;
import com.platform_lunar.homework.services.GithubService;
import com.platform_lunar.homework.utils.AuthorizationUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.platform_lunar.homework.controllers.RepositoryController.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class StoreControllerITest {
    private MockMvc mvc;
    private GithubService githubService;
    private UserProperties userProperties;
    private ServiceProperties serviceProperties;

    @Autowired
    public StoreControllerITest(MockMvc mvc, GithubService githubService, UserProperties userProperties,
                                ServiceProperties serviceProperties) {
        this.mvc = mvc;
        this.githubService = githubService;
        this.userProperties = userProperties;
        this.serviceProperties = serviceProperties;
    }

    // I assume RxJava will stay popular repo for a while ;)
    private static final String REPO_OWNER = "ReactiveX";
    private static final String REPO_NAME = "RxJava";

    @Test
    void unstarRepo() throws Exception {
        mvc.perform(delete("/repository-management/{owner}/{repo}/unstar", REPO_OWNER, REPO_NAME)
                    .header(LOGIN, userProperties.getLogin())
                    .header(AUTHORIZATION, getAuthorization()))
                .andExpect(status().isOk());

        Assert.assertFalse(
                githubService.isStarredByUser(userProperties.getLogin(), getAuthorization(), REPO_OWNER, REPO_NAME));
    }

    @Test
    void starRepo() throws Exception {
        mvc.perform(put("/repository-management/{owner}/{repo}/star", REPO_OWNER, REPO_NAME)
                    .header(LOGIN, userProperties.getLogin())
                    .header(AUTHORIZATION, getAuthorization()))
                .andExpect(status().isOk());

        Assert.assertTrue(
                githubService.isStarredByUser(userProperties.getLogin(), getAuthorization(), REPO_OWNER, REPO_NAME));
    }

    @Test
    void findPopularRepositories_WithAuthorization() throws Exception {
        mvc.perform(get("/repository-management/popular-repositories")
                .header(LOGIN, userProperties.getLogin())
                .header(AUTHORIZATION, getAuthorization())
                .param(SORT_METRIC, SortMetric.STARS.getName())
                .param(SORT_ORDER, SortOrder.DESC.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(serviceProperties.getItems())))
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].description").exists())
                .andExpect(jsonPath("$[*].linkToRepo").exists())
                .andExpect(jsonPath("$[*].starredByUser").exists())
                .andExpect(jsonPath("$[*].contributors").exists())
                .andExpect(jsonPath("$[*].stars").exists());
    }

    @Test
    void findPopularRepositories_WithoutAuthorization() throws Exception {
        mvc.perform(get("/repository-management/popular-repositories")
                .param(SORT_METRIC, SortMetric.STARS.getName())
                .param(SORT_ORDER, SortOrder.DESC.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(serviceProperties.getItems())))
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].description").exists())
                .andExpect(jsonPath("$[*].linkToRepo").exists())
                .andExpect(jsonPath("$[*].starredByUser").doesNotExist())
                .andExpect(jsonPath("$[*].contributors").exists())
                .andExpect(jsonPath("$[*].stars").exists());
    }

    @Test
    void badRequestStatus() throws Exception {
        mvc.perform(get("/repository-management/popular-repositories"))
                .andExpect(status().is4xxClientError());

        mvc.perform(put("/repository-management/{owner}/{repo}/star", REPO_OWNER, REPO_NAME))
                .andExpect(status().is4xxClientError());

        mvc.perform(delete("/repository-management/{owner}/{repo}/unstar", REPO_OWNER, REPO_NAME))
                .andExpect(status().is4xxClientError());

        mvc.perform(delete("/repository-management/{owner}/{repo}/unstar", "<unknown>", "<unknown>")
                .header(LOGIN, userProperties.getLogin())
                .header(AUTHORIZATION, getAuthorization()))
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/repository-management/popular-repositories")
                .header(LOGIN, userProperties.getLogin())
                .header(AUTHORIZATION, getAuthorization())
                .param(SORT_METRIC, "<unknown>")
                .param(SORT_ORDER, "<unknown>"))
                .andExpect(status().is5xxServerError());
    }

    // utils
    private String authorization;
    private String getAuthorization() {
        if (authorization == null) {
            authorization = AuthorizationUtils.createEncodedAuthorization(
                    userProperties.getLogin(),
                    userProperties.getPassword());
        }
        return authorization;
    }
}

