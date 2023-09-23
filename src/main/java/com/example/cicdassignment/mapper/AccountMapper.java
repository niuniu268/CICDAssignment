package com.example.cicdassignment.mapper;

import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.JAccount;
import com.example.cicdassignment.pojo.Route;
import org.apache.ibatis.annotations.*;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

@Mapper
public interface AccountMapper {

    @Results(
            {
                    @Result(property =  "id", column = "id"),

            }

    )


    @Select("select * from account;")
    List <Account> selectAll();
    @Select("select * from account where id = #{id};")
    Account selectById(@Param( "id" ) Integer id);
    @Insert("insert into account values (null, #{name}, #{contact}, #{payment}, " +
            "#{history},#{booking},#{type}); ")
    Integer addAccount(Account account);
    @Update("update account set booking=#{booking}, history=#{history}  where id = #{id};" )
    Integer changeBooking(@Param( "id" ) Integer id, @Param( "booking" ) Integer booking, @Param( "history" ) String history);
    @Delete( "delete from account where id = #{id};" )
    Integer deleteAccount(@Param( "id" ) Integer id);

    @Update("update account set name=#{name}, contact=#{contact}, " +
            "payment=#{payment},history=#{history}, " +
            "booking=#{booking}, type=#{type} where id = #{id};")
    Integer updateAccount(Account account);

    @Select("select * from account left join route on account.booking=route.id;")
    List <JAccount> selectJoinAll();
    @Select( "select * from account left join route on account.booking=route.id where account.id=#{id};" )
    JAccount selectJoinByID(@Param( ("id") ) Integer id);



}
