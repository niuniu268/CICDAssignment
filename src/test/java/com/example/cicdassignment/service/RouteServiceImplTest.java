package com.example.cicdassignment.service;

import com.example.cicdassignment.mapper.RouteMapper;
import com.example.cicdassignment.pojo.Route;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class RouteServiceImplTest {
    @InjectMocks
    RouteServiceImpl routeService;
    @Mock
    RouteMapper routeMapper;

    Route route = new Route();

    List <Route> list = new ArrayList <>();

    @BeforeEach
    void setUp () {

        MockitoAnnotations.initMocks( this );
        route.setId( 23 );
        route.setArrival( "malmo" );
        route.setDeparture( "ytad" );
        route.setExpectDeparture( "ystad c" );
        route.setExpectArrival( "malmo c" );
        route.setVehicle( "train" );
        route.setPrice( "100" );
        route.setDeliver( "sj" );
        route.setPromotion( "" );

        Collections.addAll( list, route );
    }

    @Test
    void selectAll () {
        Mockito.when( routeMapper.selectAllRoute() ).thenReturn( list );
        Assertions.assertThat( routeService.selectAll().get(0).equals( route ) );
    }

    @Test
    void selectById () {
        Mockito.when( routeMapper.selectRouteByID( 23 ) ).thenReturn( route );
        Assertions.assertThat( routeService.selectById( 23 ).equals( route ) );
    }

    @Test
    void addRoute () {
        Mockito.when( routeMapper.addRoute( route ) ).thenReturn( 1 );
        Assertions.assertThat( routeService.addRoute( route ) ).isTrue();
    }

    @Test
    void deleteRoute () {
        Mockito.when( routeMapper.deleteRoute( 1 ) ).thenReturn( 1 );
        Assertions.assertThat( routeService.deleteRoute( 1 ) ).isTrue();
    }

    @Test
    void updateRoute () {
        Mockito.when( routeMapper.updateRoute( route ) ).thenReturn( 1 );
        Assertions.assertThat( routeService.updateRoute( route ) ).isTrue();
    }
}