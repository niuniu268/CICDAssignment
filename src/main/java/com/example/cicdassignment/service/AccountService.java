package com.example.cicdassignment.service;

import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.JAccount;
import org.springframework.data.relational.core.sql.In;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {
    List <Account> selectAll();

    Account selectById(Integer id);

    @Transactional
    Boolean addAccount(Account account);
    @Transactional
    Boolean deleteAccount(Integer id);
    @Transactional
    Boolean updateAccount(Account account);
    @Transactional
    Boolean changeBooking(Integer id, Integer booking, String history);

    List<JAccount> selectJoinAll();

    JAccount selectJoinByID(Integer id);

}
