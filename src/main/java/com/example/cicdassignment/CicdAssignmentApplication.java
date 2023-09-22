package com.example.cicdassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class CicdAssignmentApplication {

    public static void main (String[] args) {
        SpringApplication.run( CicdAssignmentApplication.class, args );
    }

}
