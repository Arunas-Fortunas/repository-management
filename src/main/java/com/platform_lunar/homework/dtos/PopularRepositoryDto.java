package com.platform_lunar.homework.dtos;

import lombok.Data;

@Data
public class PopularRepositoryDto {
    private final String name;
    private final String ownerLogin;
    private final String description;
    private final String language;
    private final String licenceName;
    private final String linkToRepo;
    private final Integer stars;
    private final String contributorsUrl;
}
