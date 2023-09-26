package com.example.cicdassignment.controller;

import com.alibaba.druid.sql.visitor.PrintableVisitor;
import com.alibaba.fastjson2.JSON;
import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.JAccount;
import com.example.cicdassignment.pojo.Payment;
import com.example.cicdassignment.pojo.Route;
import com.example.cicdassignment.service.AccountService;
import com.example.cicdassignment.service.AccountServiceImpl;
import com.example.cicdassignment.service.RouteService;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadRootSmartSoapEndpointInterceptor;


import javax.annotation.security.RunAs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@ExtendWith( MockitoExtension.class )
class AccountControllerTest {

    @InjectMocks
    private AccountController accountController = new AccountController();
    @Mock
    private AccountService accountService;
    @Spy
    private RouteService routeService;

    private MockMvc mockMvc;


    private Account account = new Account();
    private Account account1 = new Account();
    private List <Account> list = new ArrayList <>(  );

    private JAccount jacount = new JAccount();

    private List<JAccount> list2 = new ArrayList<>();

    private Payment payment = new Payment();

    private Route route = new Route();

    @BeforeEach
    void setUp () {

        mockMvc = MockMvcBuilders.standaloneSetup( accountController ).build();

        account.setId( 15 );
        account.setHistory( "500" );
        account.setName( "mockito" );
        account.setContact( "6789" );
        account.setPayment( "swish" );
        account.setBooking( 0 );
        account.setType( "user" );

        Collections.addAll(list,account );

        payment.setUserid( 15 );
        payment.setRouteid( 2 );

        route.setId( 2 );
        route.setPrice( "300" );
//        route.setDeparture( "malmo" );
//        route.setArrival( "stockholm" );
//        route.setVehicle( "sj" );

        account1.setId( 16 );
        account1.setHistory( "600" );
        account1.setName( "mockito" );
        account1.setContact( "6789" );
        account1.setPayment( "credit card" );
        account1.setBooking( 2 );
        account1.setType( "user" );

    }

    @Test
    void selectAll () throws Exception {

        Mockito.when(accountService.selectAll())
                .thenReturn( list );
        mockMvc.perform( MockMvcRequestBuilders.get( "/account" )
                .contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

//        Assertions.assertThat( accountService.selectAll().get( 0 ).equals( account ) );
    }

    @Test
    void selectJoinAll () throws Exception {
        Mockito.when( accountService.selectJoinAll() ).thenReturn( list2 );
        mockMvc.perform( MockMvcRequestBuilders.get( "/account/join" )
                .contentType( MediaType.APPLICATION_JSON ))
                .andExpect( MockMvcResultMatchers.status().isOk() );

    }

    @Test
    void addAccount () throws Exception {
        String jsonString = JSON.toJSONString( account );
        Mockito.when( accountService.addAccount( account ) ).thenReturn( true );
        mockMvc.perform( MockMvcRequestBuilders.put( "/account" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( jsonString ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertThat( accountController.addAccount( account ) ).isTrue();

    }

    @Test
    void updateAccount () throws Exception {

        String jsonString = JSON.toJSONString( account );
        Mockito.when( accountService.updateAccount( account ) ).thenReturn( true );
        mockMvc.perform( MockMvcRequestBuilders.post( "/account/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( jsonString ))
                .andExpect( MockMvcResultMatchers.status().isOk() );
        Assertions.assertThat( accountController.updateAccount( account ) ).isTrue();
    }

    @Test
    void delAccount () throws Exception {
        Mockito.when( accountService.deleteAccount( 15 ) ).thenReturn( true );
        mockMvc.perform( MockMvcRequestBuilders.delete( "/account/{id}", "15" )
                .contentType( MediaType.APPLICATION_JSON )
                ).andExpect( MockMvcResultMatchers.status().isOk() );
        Assertions.assertThat( accountController.delAccount( 15 ) ).isTrue();
    }

    @Test
    void addBooking () throws Exception {

        String jsonString = JSON.toJSONString( payment );

        Mockito.when(routeService.selectById( payment.getRouteid() )).thenReturn( route );
        Mockito.when(accountService.selectById( payment.getUserid() )).thenReturn( account );


        mockMvc.perform( MockMvcRequestBuilders.post( "/account/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( jsonString )).andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertThat( accountController.addBooking( payment ) ).isEqualTo( "swish" );
    }

    @Test
    void completeBooking () throws Exception {
        String jsonString = JSON.toJSONString( payment );

        Mockito.when(routeService.selectById( payment.getRouteid() )).thenReturn( route );
        Mockito.when(accountService.selectById( payment.getUserid() )).thenReturn( account1 );

        mockMvc.perform( MockMvcRequestBuilders.post( "/account/finish" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( jsonString )).andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertThat( accountController.completeBooking( payment ) ).isEqualTo( "credit card" );
    }

    @Test
    void cancelBooking () throws Exception {
        String jsonString = JSON.toJSONString( payment );

        Mockito.when(routeService.selectById( payment.getRouteid() )).thenReturn( route );
        Mockito.when(accountService.selectById( payment.getUserid() )).thenReturn( account1 );

        mockMvc.perform( MockMvcRequestBuilders.post( "/account/cancel" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( jsonString )).andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertThat( accountController.cancelBooking( payment ) ).isEqualTo( "credit card" );
    }
}