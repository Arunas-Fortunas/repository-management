# Repository Operations

This is a web service that connects to Github and allows user to anonymously or with provided credentials retrieve 
most popular Java repositories, star and unstar them.

## Getting Started

In order to run web service you need to build and run Docker image:

```
docker build -t repository-operations:1.0.0 .
docker run --rm --name repository-operations -p 8080:8080 repository-operations:1.0.0
```

## Endpoints

### Popular Repositories

Most popular Java repositories that may be ordered by [stars] or [contributors] metric in asc/desc order.

Examples:
``` 
curl -X GET 'localhost:8080/repository-management/popular-repositories?sort_metric=stars&sort_order=desc' 
```
```
curl -X GET -H "login: plhomework555@gmail.com" -H "authorization: Basic cGxob21ld29yazU1NUBnbWFpbC5jb206ZXhwc29vbi01NTU=" 'localhost:8080/repository-management/popular-repositories?sort_metric=stars&sort_order=desc'
```

Returned Data:
* name
* description
* licenceName (if any)
* linkToRepo
* starredByUser (if credentials provided)
* contributors (including anonymous)
* stars

### Star repository

Examples:
```
curl -X PUT -H "login: plhomework555@gmail.com" -H "authorization: Basic cGxob21ld29yazU1NUBnbWFpbC5jb206ZXhwc29vbi01NTU=" 'localhost:8080/repository-management/ReactiveX/RxJava/star'
```

### Unstar repository

Examples:
```
curl -X DELETE -H "login: plhomework555@gmail.com" -H "authorization: Basic cGxob21ld29yazU1NUBnbWFpbC5jb206ZXhwc29vbi01NTU=" 'localhost:8080/repository-management/ReactiveX/RxJava/unstar'
```

## Caching

Contributors and stars data from Github API is cached for 1 hour in order to avoid rate limiting 
(https://developer.github.com/v3/#rate-limiting).

Adding or removing star calls appropriate Github API and updates local cache. 


## Built With

* [Java11](https://openjdk.java.net/projects/jdk/11/)
* [SpringBoot](https://spring.io/projects/spring-boot) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Vavr](https://www.vavr.io/) - Java utils library
* [JUnit5](https://junit.org/junit5/docs/current/user-guide/) - Testing framework


