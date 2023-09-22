package com.example.cicdassignment.pojo;

import lombok.Data;

@Data
public class Route {
    private Integer id;
    private String departure;
    private String arrival;
    private String vehicle;
    private String expectDeparture;
    private String expectArrival;
    private String price;
    private String deliver;
    private String promotion;
}
