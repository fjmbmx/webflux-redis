package com.santander.redis;

import com.santander.model.User;

import java.util.List;

public interface UserRedisService {

    public List<User> getUser(String username);
    public User getUser2(String username) ;

    }
