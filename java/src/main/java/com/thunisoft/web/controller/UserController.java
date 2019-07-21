package com.thunisoft.web.controller;

import com.thunisoft.web.POJO.User;
import com.thunisoft.web.POJO.webResult;
import com.thunisoft.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @Author: Diodeme
 * @Date: 2019/5/14
 */

/**
 * 用户控制类
 * 登录，注册，注销
 */

@RestController
@RequestMapping("/user")
public class UserController {
	/**
     * 注入userService
	 */
	@Autowired
	private UserService userService;
	//TODO 1.注册
	//TODO 3.单点登录

    //TODO 注册
    @RequestMapping(value = "/sign",method = RequestMethod.POST)
    public webResult userSign(@RequestParam(value = "account",defaultValue = "") String account, @RequestParam(value = "password",defaultValue = "") String password,
                               HttpServletRequest request, HttpServletResponse response){
        try {
        	User user=new User();
        	user.setAccount(account);
        	user.setPassword(password);
        	user.setPlainPassword(password);
            webResult result = userService.registerUser(user,request,response);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return webResult.build(500, "");
        }
    }
    /**
     * 用户登录
     * @param account 用户帐号
     * @param password 用户密码
     * @param request http request
     * @param response http response
     * @return webResult对象 含状态码
     */
	@RequestMapping(value="/login", method= RequestMethod.POST)
	public webResult userLogin(@RequestParam(value = "account",defaultValue = "") String account, @RequestParam(value = "password",defaultValue = "") String password,
							   HttpServletRequest request, HttpServletResponse response) {
		try {
			webResult result = userService.userLogin(account, password, request, response);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return webResult.build(500, "");
		}
	}

    /**
     * 页面预加载时传递cookie
     * @param request
     * @param response
     * @return webResult对象 包含状态码
     */
    @RequestMapping(value="/login", method= RequestMethod.GET)
    public webResult cookie(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(null==cookies)
            return webResult.build(250,"无cookie");
        try {
            webResult result = userService.queryUserByToken(cookies[0].getValue());
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            return webResult.build(500, "");
        }

    }

    /**
     * 注销
	 * @return index页面
	 */
	@RequestMapping(value="/logout", method= RequestMethod.GET)
	public Long logout(HttpServletRequest request, HttpServletResponse response) {
		//从redis中删除token
        Cookie[] cookies = request.getCookies();
	    return userService.logout(cookies[0].getValue());
	}

    /**
     * 根据token寻找用户
	 * @param token 用户token
	 * @return webResult对象
	 */
	@RequestMapping("/token/{token}")
	public Object getUserByToken(@PathVariable String token) {
		webResult result = null;
		try {
			result = userService.queryUserByToken(token);
		} catch (Exception e) {
			e.printStackTrace();
			result = webResult.build(500, "");
		}
		return result;
	}
}
