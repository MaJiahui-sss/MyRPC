package com.mjh.consumer;

import com.mjh.common.example.model.User;
import com.mjh.common.example.service.UserService;
import com.mjh.rpc.proxy.ServiceProxyFactory;
import org.junit.Test;

public class ConsumerTest {
    @Test
    public void testComsumer() {
        User user =new User("mjh");
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User userReceive = userService.getUser(user);
        //System.out.println(userReceive.toString());
    }
}
