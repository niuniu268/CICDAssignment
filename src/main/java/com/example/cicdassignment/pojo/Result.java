package com.example.cicdassignment.pojo;

import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.Route;
import com.example.cicdassignment.service.AccountServiceImpl;
import com.example.cicdassignment.service.RouteServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
public class Result {
    private  Account accountel;
    private  Route routeel;

    public Result (Account accountel, Route routeel) {
        this.accountel = accountel;
        this.routeel = routeel;
    }
}
