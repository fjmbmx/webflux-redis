package com.santander.controller;


import com.santander.model.Customer;
import com.santander.model.User;
import com.santander.redis.UserRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Controlador
 * Una pequeña red tutorial-www.yiidian.com
 */
@Controller
public class CustomerController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserRedisService userRedisService;

    /**
     * Depositar objetos en Redis
     */
    @RequestMapping("/put")
    @ResponseBody
    public String put() {
        Customer customer = new Customer();
        customer.setId(2);
        customer.setName("XXXX");
        customer.setGender("Hombre");
        customer.setTelephone("2r35235");
        // Llame a Redis API para almacenar datos
        redisTemplate.opsForValue().set("customer", customer);
        return "success";
    }

    /**
     * Recuperar objetos de Redis
     */
    @RequestMapping("/get")
    @ResponseBody
    public Customer get() {
        return (Customer) redisTemplate.opsForValue().get("customer");
    }

    /**
     * Depositar lista de objetos en Redis
     */
    @RequestMapping("/cacheList")
    @ResponseBody
    public List<User> getUserList() {

        return userRedisService.getUser("test");
    }

    /**
     * Depositar lista de objetos en Redis
     */
    @RequestMapping("/cache")
    @ResponseBody
    public User getUser() {
        return userRedisService.getUser2("Uriel");
    }
}