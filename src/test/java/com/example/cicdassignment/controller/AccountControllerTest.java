package com.example.cicdassignment.controller;

import com.alibaba.druid.sql.visitor.PrintableVisitor;
import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.service.AccountService;
import com.example.cicdassignment.service.AccountServiceImpl;
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
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import javax.annotation.security.RunAs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith( MockitoExtension.class )
class AccountControllerTest {

    @InjectMocks
    private AccountController accountController = new AccountController();
    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;


    private Account account = new Account();
    List <Account> list = new ArrayList <>(  );

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
    void selectJoinAll () {
    }

    @Test
    void addAccount () {
    }

    @Test
    void updateAccount () {
    }

    @Test
    void delAccount () {
    }

    @Test
    void addBooking () {
    }

    @Test
    void completeBooking () {
    }

    @Test
    void cancelBooking () {
    }
}