package com.example.cicdassignment.service;

import com.example.cicdassignment.pojo.Account;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {
    List <Account> selectAll();
    @Transactional
    Boolean addAccount(Account account);
    @Transactional
    Boolean deleteAccount(Integer id);
    @Transactional
    Boolean updateAccount(Account account);
    @Transactional
    Boolean changeBooking(Integer id, Integer booking, String history);

}
