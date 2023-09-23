package com.example.cicdassignment.controller;

import com.alibaba.fastjson2.JSON;
import com.example.cicdassignment.pojo.Route;
import com.example.cicdassignment.service.RouteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {
    @Autowired
    private RouteServiceImpl routeService;

    @GetMapping
    @RequestMapping
    public String selectAll(){
        return JSON.toJSONString(routeService.selectAll());
    }


}
