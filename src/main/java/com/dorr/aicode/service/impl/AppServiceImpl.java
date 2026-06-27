package com.dorr.aicode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.dorr.aicode.ai.model.enums.CodeGenTypeEnum;
import com.dorr.aicode.constant.AppConstant;
import com.dorr.aicode.core.AiCodeGeneratorFacade;
import com.dorr.aicode.exception.BusinessException;
import com.dorr.aicode.exception.ErrorCode;
import com.dorr.aicode.exception.ThrowUtils;
import com.dorr.aicode.model.dto.app.AppAddRequest;
import com.dorr.aicode.model.dto.app.AppAdminUpdateRequest;
import com.dorr.aicode.model.dto.app.AppQueryRequest;
import com.dorr.aicode.model.dto.app.AppUpdateRequest;
import com.dorr.aicode.mapper.AppMapper;
import com.dorr.aicode.model.entity.App;
import com.dorr.aicode.model.entity.User;
import com.dorr.aicode.model.enums.ChatHistoryMessageTypeEnum;
import com.dorr.aicode.model.vo.app.AppVO;
import com.dorr.aicode.model.vo.user.UserVO;
import com.dorr.aicode.service.AppService;
import com.dorr.aicode.service.ChatHistoryService;
import com.dorr.aicode.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务实现层。
 *
 * @author lwh
 */
@Slf4j
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户提示词不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        // 4. 获取应用的代码生成类型
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        }
        // 5. 通过校验后，添加用户消息到对话历史
        chatHistoryService.addChatMessage(appId, message, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());
        // 6. 调用 AI 生成代码（流式）
        Flux<String> contentFlux = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        // 7. 收集AI响应内容并在完成后记录到对话历史
        StringBuilder aiResponseBuilder = new StringBuilder();
        return contentFlux.map(chunk -> {
            aiResponseBuilder.append(chunk);
            return chunk;
        }).doOnComplete(() -> {
            // 流式响应完成后，添加AI消息到对话历史
            String aiResponse = aiResponseBuilder.toString();
            if (StrUtil.isNotBlank(aiResponse)) {
                chatHistoryService.addChatMessage(appId, aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
            }
        }).doOnError(error -> {
            // 如果AI回复失败，也要记录错误消息
            String errorMessage = "AI回复失败: " + error.getMessage();
            chatHistoryService.addChatMessage(appId, errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
        });
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限部署该应用，仅本人可以部署
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
        }
        // 4. 检查是否已有 deployKey
        String deployKey = app.getDeployKey();
        // 没有则生成 6 位 deployKey（大小写字母 + 数字）
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 5. 获取代码生成类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
        // 7. 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }
        // 8. 更新应用的 deployKey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 9. 返回可访问的 URL
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }


    @Override
    public long addApp(AppAddRequest appAddRequest, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
//        String appName = appAddRequest.getAppName();
        String initPrompt = appAddRequest.getInitPrompt();
//        ThrowUtils.throwIf(StrUtil.isBlank(appName), ErrorCode.PARAMS_ERROR, "应用名称不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化 prompt 不能为空");

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 构建应用对象
        App app = new App();
        BeanUtil.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());

        // 应用名暂时为 initPrompt 前 12 位
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        // 设置默认优先级
        app.setPriority(AppConstant.DEFAULT_APP_PRIORITY);
        // 保存应用
        boolean saveResult = this.save(app);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "创建应用失败");

        return app.getId();
    }

    @Override
    public boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = appUpdateRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "应用 id 不能为空");

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 查询应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 校验是否是应用创建者
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "只能修改自己创建的应用");

        // 更新应用名称
        if (StrUtil.isNotBlank(appUpdateRequest.getAppName())) {
            app.setAppName(appUpdateRequest.getAppName());
            // 设置编辑时间
            app.setEditTime(LocalDateTime.now());
        }

        // 更新应用
        boolean updateResult = this.updateById(app);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用失败");

        return true;
    }

    @Override
    public boolean deleteApp(long id, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "应用 id 不合法");

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 查询应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 校验是否是应用创建者
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "只能删除自己创建的应用");

        // 删除应用
        boolean removeResult = this.removeById(id);
        ThrowUtils.throwIf(!removeResult, ErrorCode.OPERATION_ERROR, "删除应用失败");

        // 删除应用关联的对话历史
        try {
            chatHistoryService.deleteByAppId(id);
        }catch (Exception e){
            log.error("删除应用关联的对话历史失败:{}", e.getMessage());
        }
        return true;
    }

    @Override
    public AppVO getAppVO(long id) {
        // 参数校验：检查传入的id是否合法
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "应用 id 不合法");

        // 查询应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 转换为 VO
        return this.convertToVO(app);
    }

    @Override
    public Page<AppVO> listMyAppByPage(AppQueryRequest appQueryRequest) {
        // 参数校验
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 限制每页最多 20 条
        long pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");

        // 构建查询条件
        QueryWrapper queryWrapper = this.getQueryWrapper(appQueryRequest);

        // 分页查询
        Page<App> page = this.page(new Page<>(appQueryRequest.getPageNum(), pageSize), queryWrapper);

        // 转换为 VO 分页
        return this.convertToVOPage(page);
    }

    @Override
    public Page<AppVO> listFeaturedAppByPage(AppQueryRequest appQueryRequest) {
        // 参数校验
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 限制每页最多 20 条
        long pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        // 只查询精选的应用
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        // 构建查询条件
        QueryWrapper queryWrapper = this.getQueryWrapper(appQueryRequest);
        // 分页查询
        Page<App> page = this.page(new Page<>(appQueryRequest.getPageNum(), pageSize), queryWrapper);
        // 转换为 VO 分页
        return this.convertToVOPage(page);
    }

    @Override
    public boolean adminDeleteApp(long id) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "应用 id 不合法");

        // 查询应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 删除应用
        boolean removeResult = this.removeById(id);
        ThrowUtils.throwIf(!removeResult, ErrorCode.OPERATION_ERROR, "删除应用失败");

        return true;
    }

    @Override
    public boolean adminUpdateApp(AppAdminUpdateRequest appAdminUpdateRequest) {
        // 参数校验
        ThrowUtils.throwIf(appAdminUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = appAdminUpdateRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "应用 id 不能为空");

        // 查询应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 更新应用字段
        if (StrUtil.isNotBlank(appAdminUpdateRequest.getAppName())) {
            app.setAppName(appAdminUpdateRequest.getAppName());
        }
        if (appAdminUpdateRequest.getCover() != null) {
            app.setCover(appAdminUpdateRequest.getCover());
        }
        if (appAdminUpdateRequest.getPriority() != null) {
            app.setPriority(appAdminUpdateRequest.getPriority());
        }

        // 更新应用
        boolean updateResult = this.updateById(app);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用失败");

        return true;
    }

    @Override
    public Page<AppVO> adminListAppByPage(AppQueryRequest appQueryRequest) {
        // 参数校验
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 构建查询条件
        QueryWrapper queryWrapper = this.getQueryWrapper(appQueryRequest);
        // 分页查询（管理员无每页数量限制）
        Page<App> page = this.page(new Page<>(appQueryRequest.getPageNum(), appQueryRequest.getPageSize()), queryWrapper);
        // 转换为 VO 分页
        return this.convertToVOPage(page);
    }

    @Override
    public AppVO adminGetAppVO(long id) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "应用 id 不合法");

        // 查询应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 转换为 VO
        return this.convertToVO(app);
    }

    /**
     * 将实体转换为 VO
     *
     * @param app 应用实体
     * @return 应用 VO
     */
    public AppVO convertToVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    /**
     * 将实体分页转换为 VO 分页
     *
     * @param page 实体分页
     * @return VO 分页
     */
    private Page<AppVO> convertToVOPage(Page<App> page) {
        List<App> appList = page.getRecords();
        Page<AppVO> appVOPage = new Page<>(page.getPageNumber(), page.getPageSize(), page.getTotalRow());
        if (CollUtil.isEmpty(appList)) {
            appVOPage.setRecords(new ArrayList<>());
            return appVOPage;
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        List<AppVO> appVOList = appList.stream().map(app -> {
            AppVO appVO = new AppVO();
            BeanUtil.copyProperties(app, appVO);
            appVO.setUser(userVOMap.get(app.getUserId()));
            return appVO;
        }).collect(Collectors.toList());
        appVOPage.setRecords(appVOList);
        return appVOPage;
    }

    /**
     * 构建应用查询条件
     * @param appQueryRequest 查询请求
     * @return QueryWrapper
     */
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", appQueryRequest.getId(), appQueryRequest.getId() != null)
                .like("app_name", appQueryRequest.getAppName(), StrUtil.isNotBlank(appQueryRequest.getAppName()))
                .eq("code_gen_type", appQueryRequest.getCodeGenType(), StrUtil.isNotBlank(appQueryRequest.getCodeGenType()))
                .eq("deploy_key", appQueryRequest.getDeployKey(), StrUtil.isNotBlank(appQueryRequest.getDeployKey()))
                .eq("priority", appQueryRequest.getPriority(), appQueryRequest.getPriority() != null)
                .eq("user_id", appQueryRequest.getUserId())
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


}
