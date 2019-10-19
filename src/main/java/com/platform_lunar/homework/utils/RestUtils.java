package com.platform_lunar.homework.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public class RestUtils {
    public static final String QUERY = "q";
    public static final String PAGE = "page";
    public static final String PER_PAGE = "per_page";
    public static final String SORT = "sort";
    public static final String ORDER = "order";
    public static final String ANONYMOUS = "anon";
    public static final String LINK = "link";
    public static final String REPO_OWNER = "owner";
    public static final String REPO_NAME = "name";

    public static HttpEntity createHttpEntity(String login, String authorization) {
        return new HttpEntity(
                new HttpHeaders() {{
                    if (StringUtils.hasText(login)) {
                        set(USER_AGENT, login);
                        if (StringUtils.hasText(authorization)) {
                            set(AUTHORIZATION, authorization);
                        }
                    } else {
                        set(USER_AGENT, "<dummy>");
                    }
                    setContentLength(0);
                }}
        );
    }

    public static HttpEntity createHttpEntity() {
        return createHttpEntity(null, null);
    }
}
