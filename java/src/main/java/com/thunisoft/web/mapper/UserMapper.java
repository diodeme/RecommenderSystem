package com.thunisoft.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.thunisoft.web.POJO.User;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Diodeme
 * @Date: 2019/5/14
 */
@Mapper
public interface UserMapper {
    // 通过ID查用户信息
    User getUserById(int id);
    // 通过账号查用户信息
    User findByAccount(String account);
    // 添加用户
    void insertUser(@Param("account") String account, @Param("username") String username,@Param("password") String password, @Param("salt") String salt);
}
