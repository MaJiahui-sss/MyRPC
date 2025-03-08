package com.mjh.springbootconsumer;

import com.mjh.common.example.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserCOntrollerTest {

    @Resource
    public UserController userController;
    @Test
    public void test(){
        userController.test(new User("mjh"));
    }
}
