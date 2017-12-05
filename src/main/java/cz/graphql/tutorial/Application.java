package cz.graphql.tutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
@ServletComponentScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}

