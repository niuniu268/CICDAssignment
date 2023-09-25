package com.example.cicdassignment.service;

import com.example.cicdassignment.mapper.AccountMapper;
import com.example.cicdassignment.pojo.Account;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class AccountServiceImplTest {

    @InjectMocks
    AccountServiceImpl accountService ;

    @Mock
    AccountMapper accountMapper;

    Account account = new Account();
    List <Account> list = new ArrayList <>(  );

    @BeforeEach
    void setUp () throws Exception {

        MockitoAnnotations.initMocks(this);
        account.setId( 15 );
        account.setHistory( "500" );
        account.setName( "mockito" );
        account.setContact( "6789" );
        account.setPayment( "swish" );
        account.setBooking( 0 );
        account.setType( "user" );

        Collections.addAll(list,account );


    }

    @AfterEach
    void tearDown () {
    }

    @Test
    void selectAll () {

        Mockito.when(accountMapper.selectAll())
                .thenReturn( list );

        Assertions.assertThat( accountService.selectAll().get( 0 ).equals( account ) );

    }

    @Test
    void selectById () {
    }

    @Test
    void addAccount () {
    }

    @Test
    void deleteAccount () {
    }

    @Test
    void updateAccount () {
    }

    @Test
    void changeBooking () {
    }

    @Test
    void selectJoinAll () {
    }

    @Test
    void selectJoinByID () {
    }
}