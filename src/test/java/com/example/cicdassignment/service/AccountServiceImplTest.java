package com.example.cicdassignment.service;

import com.example.cicdassignment.mapper.AccountMapper;
import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.JAccount;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jdk.security.jarsigner.JarSigner;
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
import org.springframework.data.relational.core.sql.In;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService ;

    @Mock
    private AccountMapper accountMapper;

    private Account account = new Account();
    private List <Account> list = new ArrayList <>(  );

    private JAccount jacount = new JAccount();

    private List<JAccount> list2 = new ArrayList<>();

    private Integer id = 15;
    private Integer booking = 2;
    private String history = "300";




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


    @Test
    void selectAll () {

        Mockito.when(accountMapper
                        .selectAll() )
                .thenReturn( list );

        Assertions.assertThat( accountService
                .selectAll()
                .get( 0 )
                .equals( account )
        );

    }

    @Test
    void selectById () {
        Mockito.when( accountMapper.selectById( 15 ) )
                .thenReturn( account );
        Assertions.assertThat( accountService
                .selectById( 15 )
                .equals( account ));
    }

    @Test
    void addAccount () {
        Mockito.when( accountMapper.addAccount( account ) ).thenReturn( 1 );
        Assertions.assertThat( accountService.addAccount( account ).equals( true ) );
    }

    @Test
    void deleteAccount () {

        Mockito.when( accountMapper.deleteAccount( 1 ) ).thenReturn( 1 );
        Assertions.assertThat( accountService.deleteAccount( 1 ).equals( true ) );
    }

    @Test
    void updateAccount () {

        Mockito.when( accountMapper.updateAccount( account ) ).thenReturn( 1 );
        Assertions.assertThat( accountService.updateAccount( account ).equals( true ) );
    }

    @Test
    void changeBooking () {

        Mockito.when(accountMapper.changeBooking( id, booking, history )).thenReturn( 1 );
        Assertions.assertThat( accountService.changeBooking( id,booking,history ).equals( true ) );
    }

    @Test
    void selectJoinAll () {
        Mockito.when( accountMapper.selectJoinAll() ).thenReturn( list2 );
        Assertions.assertThat( accountService.selectJoinAll() .equals( list2 ));
    }

    @Test
    void selectJoinByID () {
        Mockito.when( accountMapper.selectJoinByID( 15 ) ).thenReturn( jacount );
        Assertions.assertThat( accountService.selectJoinByID( 15 ).equals( jacount ) );
    }
}