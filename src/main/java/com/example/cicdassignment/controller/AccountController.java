package com.example.cicdassignment.controller;

import com.example.cicdassignment.pojo.*;
import com.example.cicdassignment.service.AccountServiceImpl;
import com.example.cicdassignment.service.RouteServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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

    @PutMapping
    public boolean addAccount (@RequestBody Account account) {
        return accountService.addAccount( account );
    }

    @PostMapping("update")
    public boolean updateAccount (@RequestBody Account account) {
        return accountService.updateAccount( account );

    }
    @DeleteMapping("{id}")
    public boolean delAccount(@PathVariable Integer id){
        return accountService.deleteAccount( id );
    }

    @PostMapping("add")
    public String addBooking (@RequestBody Payment payment) throws Exception {


        if (payment.getUserid( ) == null || payment.getRouteid( ) == null) {
            return null;
        }
        Account accountel = accountService.selectById( payment.getUserid( ) );
        Route routeel = routeService.selectById( payment.getRouteid( ) );

        Result result = new Result( accountel, routeel );


        int i = Integer.parseInt( result.getAccountel().getHistory() ) + Integer.parseInt( result.getRouteel().getPrice() );
        if (i <= 0) {
            return null;
        }
        accountService.changeBooking( result.getAccountel().getId( ), result.getRouteel().getId( ), Integer.toString( i ) );
        return result.getAccountel().getPayment( );


    }


    @PostMapping("/finish")
    public String completeBooking (@RequestBody Payment payment) throws Exception {

        if (payment.getUserid( ) == null || payment.getRouteid( ) == null) {
            return null;
        }
        Account accountel = accountService.selectById( payment.getUserid( ) );
        Route routeel = routeService.selectById( payment.getRouteid( ) );
        Result result = new Result( accountel, routeel );

        if (result.getAccountel( ) == null || result.getRouteel( ) == null ||
                !Objects.equals( result.getAccountel( ).getBooking( ), result.getRouteel( ).getId( ) )) {
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


        if (result.getAccountel( ) == null || result.getRouteel( ) == null ||
                !Objects.equals( result.getAccountel( ).getBooking( ), result.getRouteel( ).getId( ) )) {
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

}
