package com.mjh.springbootconsumer;

import com.mjh.common.example.model.User;
import com.mjh.common.example.service.UserService;
import com.mjh.rpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
    @RpcReference
    UserService userService;

    public void test(User user){
         user = userService.getUser(user);

        System.out.println(user);

    }
}
