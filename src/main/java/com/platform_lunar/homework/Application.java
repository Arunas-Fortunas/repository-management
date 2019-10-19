package com.platform_lunar.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try {
            var app = new SpringApplication(Application.class);
            app.run(args);
        } catch (Exception ex) {
            log.error("Failure in main application", ex);
            throw ex;
        }
    }
}
