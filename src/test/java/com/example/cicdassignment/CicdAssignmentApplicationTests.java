package com.example.cicdassignment;

import com.example.cicdassignment.mapper.AccountMapper;
import com.example.cicdassignment.mapper.RouteMapper;
import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.JAccount;
import com.example.cicdassignment.pojo.Route;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@SpringBootTest
class CicdAssignmentApplicationTests {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RouteMapper routeMapper;

    @BeforeAll
    static void beforeAll () {
        System.out.println("Junit test" );
    }


    @Test
    void testSelectAll(){

        System.out.println(accountMapper.selectAll() );
    }
    @Test
    void  testSelectAllRoute(){
        System.out.println(routeMapper.selectAllRoute() );
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
    void testSelectJoinByID(){
        JAccount account = accountMapper.selectJoinByID( 2 );
        System.out.println(account);

    }

    @Test
    void testAddAccount(){
        Account account = new Account( );
        account.setName( "testName" );
        account.setContact( "1111" );
        account.setPayment( "swish" );
        account.setBooking( 2 );
        account.setType( "user" );
        List <Account> listB = accountMapper.selectAll( );
        Integer i = accountMapper.addAccount( account );
        List <Account> listA = accountMapper.selectAll( );
        assertEquals(1,i);
        assertNotSame( listA, listB );
    }


    @Test
    void testChangeBooking(){
        Account accountB = accountMapper.selectById( 9 );
        Integer i = accountMapper.changeBooking( 9, 0, "0" );
        Account accountA = accountMapper.selectById( 9 );
        assertEquals( 1, i );
        assertNotSame( accountA, accountB );
    }

    @Test
    void testUpdateAccount(){
        Account account = new Account( );
        account.setId( 9 );
        account.setName( "testName2" );
        account.setContact( "2222" );
        account.setPayment( "swish2" );
        account.setBooking( 0 );
        account.setType( "admin" );
        Integer i = accountMapper.updateAccount( account );
        Account account1 = accountMapper.selectById( 9 );
        assertEquals( 1,i );
        assertEquals( account1, account );

    }
//    @Test
//    void testDeleteAccount(){
//
//        Integer i = accountMapper.deleteAccount( 10 );
//        assertEquals( i,1 );
//    }

    @Test
    void testAddRoute(){
        Route route = new Route( );
        route.setDeparture( "stockholm" );
        route.setArrival( "goteborg" );
        route.setVehicle( "train" );
        route.setExpectDeparture( "stockholm c" );
        route.setExpectArrival( "stockholm c" );
        route.setPrice( "300" );
        route.setDeliver( "sl" );
        route.setPromotion( "" );
        List <Route> routesB = routeMapper.selectAllRoute( );

        Integer i = routeMapper.addRoute( route );

        List <Route> routesA = routeMapper.selectAllRoute( );

        assertEquals(1,i);
        assertNotSame( routesA, routesB );
    }
    @Test
    void testupdateRoute(){

        Route route = new Route( );
        route.setId( 4 );
        route.setDeparture( "stockholm" );
        route.setArrival( "goteborg" );
        route.setVehicle( "train" );
        route.setExpectDeparture( "stockholm c" );
        route.setExpectArrival( "stockholm c" );
        route.setPrice( "300" );
        route.setDeliver( "sl" );
        route.setPromotion( "" );
        Integer i = routeMapper.updateRoute( route );
        Route route1 = routeMapper.selectRouteByID( 4 );
        assertEquals( 1,i );
        assertEquals( route1, route );

    }

    @Test
    void contextLoads () {
    }

}
