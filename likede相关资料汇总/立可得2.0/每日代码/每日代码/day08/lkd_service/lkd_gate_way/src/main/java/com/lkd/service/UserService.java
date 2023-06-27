package com.lkd.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user-service",fallbackFactory = UserServiceFallbackFactory.class)
public interface UserService{
    @GetMapping("/auth/user/{userId}")
    String getUser(@PathVariable("userId") int userId);
}
