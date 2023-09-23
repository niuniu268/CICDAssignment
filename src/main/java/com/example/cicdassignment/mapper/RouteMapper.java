package com.example.cicdassignment.mapper;

import com.example.cicdassignment.pojo.Route;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RouteMapper {
    @Results(
            {
                    @Result(property = "id", column = "id"),

            }

    )

    @Select("select * from route;")
    List <Route> selectAllRoute ();

    @Select("select * from route where id = #{id};")
    Route selectRouteByID (@Param("id") Integer id);

    @Insert("insert into route values (null, #{departure}, #{arrival}, #{vehicle}, #{expectDeparture}," +
            "#{expectArrival}, #{price}, #{deliver}, #{promotion}) ;")
    Integer addRoute (Route route);

    @Delete("delete  from route where id =#{id};")
    Integer deleteRoute (@Param("id") Integer id);

    @Update("update route set departure=#{departure}, arrival=#{arrival}, vehicle=#{vehicle}, " +
            "expectDeparture=#{expectDeparture}, expectArrival=#{expectArrival}, price=#{price}," +
            "deliver=#{deliver}, promotion=#{promotion} where id = #{id}")
    Integer updateRoute (Route route);

}
