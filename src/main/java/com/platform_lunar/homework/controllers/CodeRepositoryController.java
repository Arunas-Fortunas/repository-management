package com.platform_lunar.homework.controllers;

import com.platform_lunar.homework.domain.CodeRepository;
import com.platform_lunar.homework.domain.SortMetric;
import com.platform_lunar.homework.domain.SortOrder;
import com.platform_lunar.homework.services.CodeRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping(path = "/repository-management", produces = APPLICATION_JSON_VALUE)
public class CodeRepositoryController {
    public static final String LOGIN = "login";
    public static final String AUTHORIZATION = "authorization";
    public static final String SORT_METRIC = "sort_metric";
    public static final String SORT_ORDER = "sort_order";

    private static final String OWNER = "owner";
    private static final String REPO = "repo";

    private static final Logger log = LoggerFactory.getLogger(CodeRepositoryController.class);

    private CodeRepositoryService codeRepositoryService;

    @Autowired
    public CodeRepositoryController(CodeRepositoryService codeRepositoryService) {
        this.codeRepositoryService = codeRepositoryService;
    }

    @GetMapping(path = "popular-repositories")
    @ResponseStatus(value = OK)
    public List<CodeRepository> findPopularRepositories(
            @RequestHeader(value = LOGIN, required = false) String login,
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = SORT_METRIC) @NotNull SortMetric sortMetric,
            @RequestParam(value = SORT_ORDER) @NotNull SortOrder sortOrder) {

        log.info("GET popular frameworks; sort metric [{}] and sort order [{}]", sortMetric, sortOrder);
        return codeRepositoryService.findBy(login, authorization, sortMetric, sortOrder);
    }

    @PutMapping(path = "{owner}/{repo}/star")
    @ResponseStatus(value = OK)
    public void starRepo(
            @RequestHeader(LOGIN) @NotEmpty String login,
            @RequestHeader(AUTHORIZATION) @NotEmpty String authorization,
            @PathVariable(OWNER) @NotEmpty String repoOwner,
            @PathVariable(REPO) @NotEmpty String repoName) {

        log.info("star {}:{} repo for user [{}]", repoOwner, repoName, login);
        codeRepositoryService.starRepo(login, authorization, repoOwner, repoName);
    }

    @DeleteMapping(path = "{owner}/{repo}/unstar")
    @ResponseStatus(value = OK)
    public void unstarRepo(
            @RequestHeader(LOGIN) @NotEmpty String login,
            @RequestHeader(AUTHORIZATION) @NotEmpty String authorization,
            @PathVariable(OWNER) @NotEmpty String repoOwner,
            @PathVariable(REPO) @NotEmpty String repoName) {

        log.info("unstar {}:{} repo for user [{}]", repoOwner, repoName, login);
        codeRepositoryService.unstarRepo(login, authorization, repoOwner, repoName);
    }
}
