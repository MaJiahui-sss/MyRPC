package com.mjh.example.provider.service.impl;

import com.mjh.common.example.model.User;
import com.mjh.common.example.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        user.setName("wys");
        //System.out.println(user.toString());
        return user;
    }
}
