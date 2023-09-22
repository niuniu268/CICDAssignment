package com.example.cicdassignment.controller;

import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountServiceImpl accountService;

    @GetMapping
    @ResponseBody
    public List<Account> selectAll(){
        return accountService.selectAll();
    }
}
