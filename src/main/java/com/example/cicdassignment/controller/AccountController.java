package com.example.cicdassignment.controller;

import com.example.cicdassignment.pojo.*;
import com.example.cicdassignment.service.AccountServiceImpl;
import com.example.cicdassignment.service.RouteServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private RouteServiceImpl routeService;

    @GetMapping
    @ResponseBody
    public List <Account> selectAll () {

        return accountService.selectAll( );
    }

    @GetMapping("join")
    @ResponseBody
    public List <JAccount> selectJoinAll () {
        return accountService.selectJoinAll( );
    }

    @PostMapping("add")
    public String addBooking (@RequestBody Payment payment) throws Exception {

        System.out.println(payment );

        if (payment.getUserid( ) == null || payment.getRouteid( ) == null) {
            return null;
        }
        Account accountel = accountService.selectById( payment.getUserid( ) );
        Route routeel = routeService.selectById( payment.getRouteid( ) );


        int i = Integer.parseInt( accountel.getHistory() ) + Integer.parseInt( routeel.getPrice() );
        if (i <= 0) {
            return null;
        }
        accountService.changeBooking( accountel.getId( ), routeel.getId( ), Integer.toString( i ) );
        return accountel.getPayment( );


    }


    @PostMapping("/finish")
    public String completeBooking (@RequestBody Payment payment) throws Exception {

        if (payment.getUserid( ) == null || payment.getRouteid( ) == null) {
            return null;
        }
        Account accountel = accountService.selectById( payment.getUserid( ) );
        Route routeel = routeService.selectById( payment.getRouteid( ) );
        Result result = new Result( accountel, routeel );

        if (result.getAccountel( ) == null || result.getRouteel( ) == null || result.getAccountel( ).getBooking( ) != result.getRouteel( ).getId( )) {
            log.debug( "check input payment" );
            return null;
        }
        accountService.changeBooking( result.getAccountel( ).getId( ), 0, result.getAccountel( ).getHistory( ) );

        return result.getAccountel( ).getPayment( );

    }

    @PostMapping("/cancel")
    public String cancelBooking (@RequestBody Payment payment) throws Exception {

        if (payment.getUserid( ) == null || payment.getRouteid( ) == null) {
            return null;
        }
        Account accountel = accountService.selectById( payment.getUserid( ) );
        Route routeel = routeService.selectById( payment.getRouteid( ) );
        Result result = new Result( accountel, routeel );


        if (result.getAccountel( ) == null || result.getRouteel( ) == null || result.getAccountel( ).getBooking( ) != result.getRouteel( ).getId( )) {
            log.debug( "check input payment" );
            return null;
        }
        int i = Integer.parseInt( result.getAccountel( ).getHistory( ) )-Integer.parseInt( result.getRouteel( ).getPrice( ) );

        if (i < 0) {
            log.debug( "check input payment" );
            return null;
        }

        accountService.changeBooking( result.getAccountel( ).getId( ), 0, Integer.toString( i ) );

        return result.getAccountel( ).getPayment( );

    }
//
//    private Result getResult (Payment payment) {
//        if (payment.getUserid( ) == null || payment.getRouteid( ) == null) {
//            log.debug( "check input payment" );
//            return null;
//        }
//
//        Account accountel = accountService.selectById( payment.getUserid( ) );
//        Route routeel = routeService.selectById( payment.getRouteid( ) );
//        Result result = new Result( accountel, routeel );
//        return result;
//    }
//
//    private static class Result {
//        public final Account accountel;
//        public final Route routeel;
//
//        public Result (Account accountel, Route routeel) {
//            this.accountel = accountel;
//            this.routeel = routeel;
//        }
//    }
}
