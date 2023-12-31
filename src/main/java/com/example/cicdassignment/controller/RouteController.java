package com.example.cicdassignment.controller;

import com.alibaba.fastjson2.JSON;
import com.example.cicdassignment.pojo.Route;
import com.example.cicdassignment.service.RouteService;
import com.example.cicdassignment.service.RouteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {
    @Autowired
    private RouteService routeService;

    @GetMapping
    @ResponseBody
    public List <Route> selectAll () {
        return  routeService.selectAll( ) ;
    }

    @PutMapping
    public boolean addRoute (@RequestBody Route route) {
        return routeService.addRoute( route );
    }

    @PostMapping("update")
    public boolean updateRoute (@RequestBody Route route) {
        return routeService.updateRoute( route );

    }


}
