package com.example.cicdassignment.service;

import com.example.cicdassignment.mapper.AccountMapper;
import com.example.cicdassignment.pojo.Account;
import com.example.cicdassignment.pojo.JAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{
    @Autowired
    private AccountMapper accountMapper;


    @Override
    public List <Account> selectAll () {
        return accountMapper.selectAll( );
    }

    @Override
    public Account selectById (Integer id) {

        return accountMapper.selectById( id );
    }

    @Override
    public Boolean addAccount (Account account) {

        return accountMapper.addAccount( account ) > 0 ;
    }

    @Override
    public Boolean deleteAccount (Integer id) {

        return accountMapper.deleteAccount(id) > 0;
    }

    @Override
    public Boolean updateAccount (Account account) {

        return accountMapper.updateAccount( account )>0;
    }

    @Override
    public Boolean changeBooking (Integer id, Integer booking, String history) {

        return accountMapper.changeBooking(id, booking, history)>0;}

    @Override
    public List <JAccount> selectJoinAll () {
        return accountMapper.selectJoinAll();
    }

    @Override
    public JAccount selectJoinByID (Integer id) {
        return accountMapper.selectJoinByID( id );
    }
}
