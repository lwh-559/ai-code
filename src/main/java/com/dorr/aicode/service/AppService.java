package com.dorr.aicode.service;

import com.dorr.aicode.model.dto.app.AppAddRequest;
import com.dorr.aicode.model.dto.app.AppAdminUpdateRequest;
import com.dorr.aicode.model.dto.app.AppQueryRequest;
import com.dorr.aicode.model.dto.app.AppUpdateRequest;
import com.dorr.aicode.model.entity.app.App;
import com.dorr.aicode.model.entity.user.User;
import com.dorr.aicode.model.vo.app.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

/**
 * 应用 服务层。
 *
 * @author lwh
 */
public interface AppService extends IService<App> {

    /**
     * AI生成代码
     *
     * @param appId    应用 id
     * @param message  用户提示词
     * @param loginUser 登录用户
     * @return 生成代码的流
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 部署应用
     *
     * @param appId    应用 id
     * @param loginUser 登录用户
     * @return 部署结果
     */
    String deployApp(Long appId, User loginUser);

    /**
     * 创建应用
     *
     * @param appAddRequest 创建应用请求
     * @param request       request
     * @return 新应用 id
     */
    long addApp(AppAddRequest appAddRequest, HttpServletRequest request);

    /**
     * 用户更新自己的应用
     *
     * @param appUpdateRequest 更新应用请求
     * @param request          request
     * @return 是否更新成功
     */
    boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    /**
     * 用户删除自己的应用
     *
     * @param id      应用 id
     * @param request request
     * @return 是否删除成功
     */
    boolean deleteApp(long id, HttpServletRequest request);

    /**
     * 获取应用详情
     *
     * @param id      应用 id
     * @return 应用详情
     */
    AppVO getAppVO(long id);

    /**
     * 分页查询用户自己的应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 分页应用列表
     */
    Page<AppVO> listMyAppByPage(AppQueryRequest appQueryRequest);

    /**
     * 分页查询精选应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 分页应用列表
     */
    Page<AppVO> listFeaturedAppByPage(AppQueryRequest appQueryRequest);

    /**
     * 管理员删除任意应用
     *
     * @param id 应用 id
     * @return 是否删除成功
     */
    boolean adminDeleteApp(long id);

    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 管理员更新请求
     * @return 是否更新成功
     */
    boolean adminUpdateApp(AppAdminUpdateRequest appAdminUpdateRequest);

    /**
     * 管理员分页查询应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 分页应用列表
     */
    Page<AppVO> adminListAppByPage(AppQueryRequest appQueryRequest);

    /**
     * 管理员获取应用详情
     *
     * @param id 应用 id
     * @return 应用详情
     */
    AppVO adminGetAppVO(long id);
}