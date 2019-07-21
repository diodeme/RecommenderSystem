package com.thunisoft.web.controller;

import com.thunisoft.web.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Diodeme
 * @Date: 2019/5/14
 */
@RestController
@RequestMapping
public class UserController_bak {
    @Autowired
    UserMapper userMapper;
    @GetMapping("/test")
    public String getUser(
                          HttpServletRequest request, HttpServletResponse response)
    {
        Cookie[] cookies = request.getCookies();
        userMapper.insertUser("333","333","123","333");
        return "";
    }

}
