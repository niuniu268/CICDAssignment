package com.example.cicdassignment.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.function.impl.ToInteger;
import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.JAccount;
import com.example.cicdassignment.pojo.Payment;
import com.example.cicdassignment.pojo.Route;
import com.example.cicdassignment.service.AccountServiceImpl;
import com.example.cicdassignment.service.RouteServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
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

    @PostMapping
    public String addBooking (@RequestBody Payment payment) throws Exception {
        Result result = getResult( payment );
        if (result == null) return null;
        int i = Integer.parseInt( result.routeel.getPrice( ) ) + Integer.parseInt( result.accountel.getHistory( ) );
        if (i <= 0) {
            log.debug( "check input payment" );
            return null;
        }
        accountService.changeBooking( result.accountel.getId( ), result.routeel.getId( ), Integer.toString( i ) );
        return result.accountel.getPayment( );


    }


    @PostMapping
    public String completeBooking (@RequestBody Payment payment) throws Exception {
        Result result = getResult( payment );
        if (result == null) return null;

        if (result.accountel == null || result.routeel == null || result.accountel.getBooking( ) != result.routeel.getId( )) {
            log.debug( "check input payment" );
            return null;
        }
        accountService.changeBooking( result.accountel.getId( ), 0, result.accountel.getHistory( ) );

        return result.accountel.getPayment( );

    }

    @PostMapping
    public String cancelBooking (@RequestBody Payment payment) throws Exception {
        Result result = getResult( payment );
        if (result == null) return null;

        if (result.accountel == null || result.routeel == null || result.accountel.getBooking( ) != result.routeel.getId( )) {
            log.debug( "check input payment" );
            return null;
        }
        int i = Integer.parseInt( result.routeel.getPrice( ) ) + Integer.parseInt( result.accountel.getHistory( ) );

        if (i < 0) {
            log.debug( "check input payment" );
            return null;
        }

        accountService.changeBooking( result.accountel.getId( ), 0, Integer.toString( i ) );

        return result.accountel.getPayment( );

    }

    private Result getResult (Payment payment) {
        if (payment.getUserid( ) == null || payment.getRouteid( ) == null) {
            log.debug( "check input payment" );
            return null;
        }

        Account accountel = accountService.selectById( payment.getUserid( ) );
        Route routeel = routeService.selectById( payment.getRouteid( ) );
        Result result = new Result( accountel, routeel );
        return result;
    }

    private static class Result {
        public final Account accountel;
        public final Route routeel;

        public Result (Account accountel, Route routeel) {
            this.accountel = accountel;
            this.routeel = routeel;
        }
    }
}
