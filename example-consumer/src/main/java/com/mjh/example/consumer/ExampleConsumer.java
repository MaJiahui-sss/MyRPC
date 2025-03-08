package com.mjh.example.consumer;

import com.mjh.common.example.model.User;
import com.mjh.common.example.service.UserService;
import com.mjh.example.consumer.proxy.UserServiceProxy;
import com.mjh.rpc.boot.ConsumerBoot;
import com.mjh.rpc.proxy.ServiceProxyFactory;


public class ExampleConsumer {
    UserService userService;
    public static void main(String[] args) {
        ConsumerBoot.init();
        User user = new User("mjh");
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User userReceive = userService.getUser(user);
        System.out.println(userReceive.toString());

    }
}
