package com.dorr.aicode.service;

import com.dorr.aicode.model.dto.user.UserAddRequest;
import com.dorr.aicode.model.dto.user.UserQueryRequest;
import com.dorr.aicode.model.dto.user.UserUpdateRequest;
import com.dorr.aicode.model.vo.user.LoginUserVO;
import com.dorr.aicode.model.vo.user.UserVO;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.core.paginate.Page;
import com.dorr.aicode.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;


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
     * 更新用户信息（仅管理员或本人可操作）
     *
     * @param userUpdateRequest 用户更新请求
     * @param request           request
     * @return 是否更新成功
     */
    boolean updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    /**
     * 分页获取用户脱敏列表
     *
     * @param userQueryRequest 分页查询请求
     * @return 分页脱敏用户列表
     */
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    /**
     * 创建用户（仅管理员）
     *
     * @param userAddRequest 用户创建请求
     * @return 新用户 id
     */
    long addUser(UserAddRequest userAddRequest);
}
