package com.example.cicdassignment.service;

import com.example.cicdassignment.mapper.AccountMapper;
import com.example.cicdassignment.mapper.RouteMapper;
import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RouteServiceImpl implements RouteService{
    @Autowired
    private RouteMapper routeMapper;
    @Override
    public List <Route> selectAll () {
        return routeMapper.selectAllRoute();
    }

    @Override
    public Route selectById (Integer id) {
        return routeMapper.selectRouteByID( id );
    }

    @Override
    public Boolean addRoute (Route route) {
        return routeMapper.addRoute( route )>0;
    }

    @Override
    public Boolean deleteRoute (Integer id) {
        return routeMapper.deleteRoute( id )>0;
    }

    @Override
    public Boolean updateRoute (Route route) {
        return routeMapper.updateRoute( route )>0;
    }
}
