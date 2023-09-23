package com.example.cicdassignment.pojo;


import lombok.Data;

@Data
public class Result {
    private  Account accountel;
    private  Route routeel;

    public Result (Account accountel, Route routeel) {
        this.accountel = accountel;
        this.routeel = routeel;
    }
}
