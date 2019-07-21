package com.thunisoft.web.POJO;



/**
 * @Author: Diodeme
 * @Date: 2019/5/14
 */

/**
 * 用户POJO
 */
public class User {
    //TODO 此处应将jpa格式转换为mybatis格式 自增长主键
   // @Id
   // @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;						// 自增长主键
    private String account;					// 登录的账号
    private String username;				// 注册的昵称
   // @Transient
    private String plainPassword; 			// 登录时的密码，不持久化到数据库
    private String password;				// 加密后的密码
    private String salt;					// 用于加密的盐


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPlainPassword() {
        return plainPassword;
    }
    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", account=" + account + ", username=" + username + ", plainPassword=" + plainPassword
                + ", password=" + password + ", salt=" + salt +"]";
    }
}
