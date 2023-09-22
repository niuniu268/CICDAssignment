package com.example.cicdassignment;

import com.example.cicdassignment.mapper.AccountMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CicdAssignmentApplicationTests {
    @Autowired
    private AccountMapper accountMapper;

    @Test
    void testSelectAll(){

        System.out.println(accountMapper.selectAll() );
    }
    @Test
    void testSelectJoinAll(){
        System.out.println(accountMapper.selectJoinAll() );
    }



    @Test
    void contextLoads () {
    }

}
