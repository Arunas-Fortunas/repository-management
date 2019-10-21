package com.platform_lunar.homework;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {
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
