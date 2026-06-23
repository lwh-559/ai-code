package com.dorr.aicode.service;

import com.dorr.aicode.model.dto.UserQueryRequest;
import com.dorr.aicode.model.vo.LoginUserVO;
import com.dorr.aicode.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.dorr.aicode.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author lwh
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 重写父类方法，用于获取加密后的用户密码
     * @param userPassword 用户输入的原始密码
     * @return 返回经过MD5加密后的密码字符串
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return LoginUserVO
     */
    LoginUserVO getLoginUserVO(User user);


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request request
     * @return User
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 用户注销
     *
     * @param request request
     * @return boolean
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     * @param user 用户信息
     * @return 脱敏后数据
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户列表
     * @param userList 用户实体列表
     * @return 脱敏后数据列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 构建分页查询 QueryWrapper
     * @param userQueryRequest 分页查询数据
     * @return QueryWrapper
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
}
