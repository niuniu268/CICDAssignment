package com.example.cicdassignment;

import com.example.cicdassignment.mapper.AccountMapper;
import com.example.cicdassignment.pojo.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.colorchooser.AbstractColorChooserPanel;

@SpringBootTest
class CicdAssignmentApplicationTests {
    @Autowired
    private AccountMapper accountMapper;

    @Test
    void testSelectAll(){

        System.out.println(accountMapper.selectAll() );
    }
    @Test
    void  testSelectAllRoute(){
        System.out.println(accountMapper.selectAllRoute() );
    }
    @Test
    void testSelectJoinAll(){
        System.out.println(accountMapper.selectJoinAll() );
    }

    @Test
    void testSelectById(){
        Account account = accountMapper.selectById( 1 );
        System.out.println(account );

    }

    @Test
    void testAddAccount(){
        Account account = new Account( );
        account.setName( "testName" );
        account.setContact( "1111" );
        account.setPayment( "swish" );
        account.setBooking( 2 );
        account.setType( "user" );
        System.out.println(accountMapper.selectAll() );
        accountMapper.addAccount( account );
        System.out.println(accountMapper.selectAll() );
    }

    @Test
    void testChangeBooking(){
        System.out.println(accountMapper.selectAll() );
        accountMapper.changeBooking( 5,0,"0" );
        System.out.println(accountMapper.selectAll() );
    }

    @Test
    void testUpdateAccount(){
        Account account = new Account( );
        account.setId( 6 );
        account.setName( "testName2" );
        account.setContact( "2222" );
        account.setPayment( "swish2" );
        account.setBooking( 0 );
        account.setType( "admin" );
        accountMapper.updateAccount( account );
        System.out.println(accountMapper.selectAll() );

    }
    @Test
    void testDeleteAccount(){
        System.out.println(accountMapper.selectAll() );
        Integer i = accountMapper.deleteAccount( 6 );
        System.out.println(i );
        System.out.println(accountMapper.selectAll() );
    }

    @Test
    void contextLoads () {
    }

}
