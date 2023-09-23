package com.example.cicdassignment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@MapperScan("com.example.cicdassignment.mapper")
@SpringBootApplication
public class CicdAssignmentApplication {

    public static void main (String[] args) {
        SpringApplication.run( CicdAssignmentApplication.class, args );
    }

}
