package com.platform_lunar.homework.domain;

import lombok.*;

@Data
public class CodeRepository {
    private final String name;
    private final String description;
    private final String licenceName;
    private final String linkToRepo;
    private final Boolean starredByUser;
    private final Integer contributors;
    private final Integer stars;
}
