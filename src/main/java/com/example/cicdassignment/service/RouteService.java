package com.example.cicdassignment.service;

import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.Route;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RouteService {
    List <Route> selectAll();

    Route selectById(Integer id);

    @Transactional
    Boolean addRoute(Route route);
    @Transactional
    Boolean deleteRoute(Integer id);
    @Transactional
    Boolean updateRoute(Route route);


}
