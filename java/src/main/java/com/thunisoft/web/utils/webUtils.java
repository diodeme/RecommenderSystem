package com.thunisoft.web.utils;

import com.thunisoft.web.POJO.User;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
/**
 * @Author: Diodeme
 * @Date: 2019/5/15
 */

/**
 * 工具类
 * 加密解密
 */
public class webUtils {
	
	/**
	 * 加盐加密的策略非常多,根据实际业务来
	 */
	public static void entryptPassword(User user) {
		String salt = UUID.randomUUID().toString();
		String temPassword = salt + user.getPlainPassword();
		String md5Password = DigestUtils.md5DigestAsHex(temPassword.getBytes());
		user.setSalt(salt);
		user.setPassword(md5Password);
	}
	//解密并判断是否相等
	public static boolean decryptPassword(User user, String plainPassword) {
		if(null==user)
			return false;
		String temPassword = user.getSalt() + plainPassword;
		String md5Password = DigestUtils.md5DigestAsHex(temPassword.getBytes());
		return user.getPassword().equals(md5Password);
	}
	
	public static String getCurrentDateTime() {
		TimeZone zone = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(zone);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}

}
