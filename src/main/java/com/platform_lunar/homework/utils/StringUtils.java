package com.platform_lunar.homework.utils;

import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern PAGE_COUNT_PATTERN = Pattern.compile(".+page=(\\d+)>; rel=\"last\"");

    public static Integer extractPageCount(String link) {
        if (link == null)
            throw new IllegalArgumentException("link argument is mandatory");

        var matcher = PAGE_COUNT_PATTERN.matcher(link);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("could not extract page count from: %s", link));
        }

        return Integer.valueOf(matcher.group(1));
    }
}
