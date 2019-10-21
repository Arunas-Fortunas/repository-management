package com.platform_lunar.homework.controllers;

import com.platform_lunar.homework.domain.CodeRepository;
import com.platform_lunar.homework.domain.SortMetric;
import com.platform_lunar.homework.domain.SortOrder;
import com.platform_lunar.homework.services.CodeRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/repository-management", produces = APPLICATION_JSON_VALUE)
public class CodeRepositoryController {
    static final String LOGIN = "login";
    static final String AUTHORIZATION = "authorization";
    static final String SORT_METRIC = "sort_metric";
    static final String SORT_ORDER = "sort_order";

    private static final String OWNER = "owner";
    private static final String REPO = "repo";

    private final CodeRepositoryService codeRepositoryService;

    @GetMapping(path = "popular-repositories")
    List<CodeRepository> findPopularRepositories(
            @RequestHeader(value = LOGIN, required = false) String login,
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = SORT_METRIC, defaultValue = "stars") @NotNull SortMetric sortMetric,
            @RequestParam(value = SORT_ORDER, defaultValue = "desc") @NotNull SortOrder sortOrder) {

        log.info("GET popular repositories; sort metric [{}] and sort order [{}]", sortMetric, sortOrder);
        return codeRepositoryService.findBy(login, authorization, sortMetric, sortOrder);
    }

    @PutMapping(path = "{owner}/{repo}/star")
    void starRepo(
            @RequestHeader(LOGIN) @NotEmpty String login,
            @RequestHeader(AUTHORIZATION) @NotEmpty String authorization,
            @PathVariable(OWNER) @NotEmpty String repoOwner,
            @PathVariable(REPO) @NotEmpty String repoName) {

        log.info("star {}:{} repo for user [{}]", repoOwner, repoName, login);
        codeRepositoryService.starRepo(login, authorization, repoOwner, repoName);
    }

    @DeleteMapping(path = "{owner}/{repo}/unstar")
    void unstarRepo(
            @RequestHeader(LOGIN) @NotEmpty String login,
            @RequestHeader(AUTHORIZATION) @NotEmpty String authorization,
            @PathVariable(OWNER) @NotEmpty String repoOwner,
            @PathVariable(REPO) @NotEmpty String repoName) {

        log.info("unstar {}:{} repo for user [{}]", repoOwner, repoName, login);
        codeRepositoryService.unstarRepo(login, authorization, repoOwner, repoName);
    }
}
