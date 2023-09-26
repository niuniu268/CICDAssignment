package com.example.cicdassignment.controller;

import com.alibaba.fastjson2.JSON;
import com.example.cicdassignment.pojo.Route;
import com.example.cicdassignment.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@ExtendWith( MockitoExtension.class )
class RouteControllerTest {
    @InjectMocks
    private RouteController routeController = new RouteController();
    @Mock
    private RouteService routeService;

    private MockMvc mockMvc;


    Route route = new Route();

    List <Route> list = new ArrayList <>();

    @BeforeEach
    void setUp () {

        mockMvc = MockMvcBuilders.standaloneSetup( routeController ).build();
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
    void selectAll () throws Exception {

        Mockito.when(routeService.selectAll())
                .thenReturn( list );
        mockMvc.perform( MockMvcRequestBuilders.get( "/route" )
                        .contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );


    }

    @Test
    void addRoute () throws Exception {
        String jsonString = JSON.toJSONString( route );
        Mockito.when( routeService.addRoute( route ) ).thenReturn( true );
        mockMvc.perform( MockMvcRequestBuilders.put( "/route" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( jsonString ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertThat( routeController.addRoute( route ) ).isTrue();
    }

    @Test
    void updateRoute () throws Exception {

        String jsonString = JSON.toJSONString( route );
        Mockito.when( routeService.updateRoute( route ) ).thenReturn( true );
        mockMvc.perform( MockMvcRequestBuilders.post( "/route/update" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( jsonString ))
                .andExpect( MockMvcResultMatchers.status().isOk() );
//        Assertions.assertThat( routeController.updateRoute( route ) ).isTrue();

    }
}