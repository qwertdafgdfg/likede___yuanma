package com.lkd.http.controller;

import com.lkd.entity.UserEntity;
import com.lkd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 该controller不向web前端暴露，只给内部调用，会在zuul做拦截
 */
@RestController
@RequestMapping("/auth")
@CacheConfig(cacheNames = "auth")
public class AuthenticationController{
    @Autowired
    @Lazy
    private UserService userService;

    @Cacheable(cacheNames = "user",key = "targetClass + methodName + #p0")
    @GetMapping("/user/{userId}")
    public UserEntity getUser(@PathVariable("userId") int userId){
        return userService.getById(userId);
    }
}
