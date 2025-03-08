package com.mjh.springbootprovider.service.impl;

import com.mjh.common.example.model.User;
import com.mjh.common.example.service.UserService;
import com.mjh.rpcspringbootstarter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        user.setName("工藤云舒是大鱼塘主");
        return user;
    }
}
