package com.example.cicdassignment.pojo;

import lombok.Data;

@Data
public class JAccount {

    private Integer id;
    private String name;
    private String contact;
    private String payment;
    private String history;
    private Integer booking;
    private String type;
    private Integer routeId;
    private String departure;
    private String arrival;
    private String vehicle;
    private String expectDeparture;
    private String expectArrival;
    private String price;
    private String deliver;
    private String promotion;
}
