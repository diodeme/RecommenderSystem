package com.thunisoft.web.service;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.thunisoft.web.POJO.webResult;
import com.thunisoft.web.POJO.User;
import com.thunisoft.web.mapper.UserMapper;
import com.thunisoft.web.utils.CookieUtils;
import com.thunisoft.web.utils.webUtils;
import com.thunisoft.web.utils.JsonUtils;
/**
 * @Author: Diodeme
 * @Date: 2019/5/15
 */
/**
 * 注册，登录，注销，根据token(cookie)查询用户信息
 * 主要有四个函数：
 * registerUser：用户注册
 * userLogin：用户登录
 * logout：用户注销
 * queryUserByToken：根据用户token在redis查找用户信息
 */
@Service
@PropertySource(value = "classpath:redis.properties")
public class UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SSO_SESSION_EXPIRE;

    /**
     *
     * @param user
     * @param request
     * @param response
     * @return
     */
    public webResult registerUser(User user,HttpServletRequest request, HttpServletResponse response) {
        if (null != userMapper.findByAccount(user.getAccount())) {
            //如果已经注册，返回报错
            return webResult.build(400, "用户已经注册！！");
        }
		webUtils.entryptPassword(user);
		user.setUsername(nameService.getRandomJianHan(3));
		userMapper.insertUser(user.getAccount(),user.getUsername(),user.getPassword(),user.getSalt());
    	return webResult.build(200, "");
    }

    /**
     * 用户登录，新建session(cookie)写入redis中(返回至浏览器)
     * @param account 账户
     * @param password 密码
     * @param request 客户端请求
     * @param response 服务器响应
     * @return webResult对象，包含状态码
     */
    public webResult userLogin(String account, String password,
			HttpServletRequest request, HttpServletResponse response) {
    	// 判断账号密码是否正确
		User user = userMapper.findByAccount(account);
		//ItdragonUtils用于加密解密
		if (!webUtils.decryptPassword(user, password)) {
			return webResult.build(400, "账号名或密码错误");
		}
		// 生成token
		String token = UUID.randomUUID().toString();
		// 清空密码和盐避免泄漏
		String userPassword = user.getPassword();
		String userSalt = user.getSalt();
		user.setPassword(null);
		user.setSalt(null);
		// 把用户信息写入 redis
		jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token, JsonUtils.objectToJson(user));
		user.setPassword(userPassword);
		user.setSalt(userSalt);
		// 设置 session 的过期时间 expire方法用于设置过期时间 SSO_SESSION_EXPIRE在配置文件中进行配置
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
		// 添加写 cookie 的逻辑，cookie 的有效期是关闭浏览器就失效。
		CookieUtils.setCookie(request, response, "USER_TOKEN", token);
		// 返回token
		return webResult.ok(token);
	}

	/**
	 * 注销删除token
	 * @param token token
	 */
	public Long logout(String token) {
    	return jedisClient.del(REDIS_USER_SESSION_KEY + ":" + token);
    }

	/**
	 * 根据token(cookie)查找对应用户
	 * @param token token
	 * @return webResult对象 包含状态码
	 */
	public webResult queryUserByToken(String token) {
		// 根据token从redis中查询用户信息
		String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
		// 判断是否为空
		if (StringUtils.isEmpty(json)) {
			return webResult.build(400, "此session已经过期，请重新登录");
		}
		// 更新过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
		// 返回用户信息
		return webResult.ok(JsonUtils.jsonToPojo(json, User.class));
	}
    
}
