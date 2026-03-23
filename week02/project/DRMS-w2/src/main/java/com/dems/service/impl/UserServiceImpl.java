package com.dems.service.impl;

import com.dems.anno.LogOperation;
import com.dems.mapper.UserMapper;
import com.dems.pojo.LoginInfo;
import com.dems.pojo.User;
import com.dems.service.UserService;
import com.dems.utils.Constant;
import com.dems.utils.JwtUtil;
import com.dems.utils.UserContext;
import com.dems.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginInfo login(String userId, String password) {
        log.info("用户尝试登录，userId: {}", userId);

        User user = null;
        // 调用mapper的查询
        user = userMapper.selectUserByIdPass(userId, password);

        if (user != null) {
            log.info("用户登录成功，userId: {}, 角色: {}", userId, user.getRole());

            LoginInfo loginInfo = null;
            String username = user.getUsername();
            Integer role = user.getRole();

            // 生成令牌，封装到LoginInfo
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("username", username);
            map.put("role", role);

            // 生成令牌
            String token = JwtUtil.generateJwtToken(map);
            loginInfo = new LoginInfo(userId, username, token, user.getRole(), user.getBuildingId(), user.getRoomId(), null);

            // 看是啥角色
            if (role.equals(Constant.STU_ROLE)) {
                // 是学生
                if (user.getBuildingId() == null || user.getRoomId() == null) {
                    // 没绑宿舍
                    log.debug("学生用户未绑定宿舍，引导至绑定页面，userId: {}", userId);
                    loginInfo.setNextStep(Constant.STEP_BIND_ROOM);
                } else {
                    // 已绑定宿舍
                    log.debug("学生用户已绑定宿舍，引导至首页，userId: {}", userId);
                    loginInfo.setNextStep(Constant.STEP_STU_HOME);
                }
            } else {
                // 是管理员
                log.debug("管理员用户登录，引导至管理首页，userId: {}", userId);
                loginInfo.setNextStep(Constant.STEP_ADMIN_HOME);
            }
            return loginInfo;
        }

        // 登录失败记录警告日志 (注意：不要打印密码明文)
        log.warn("用户登录失败：用户名或密码错误，userId: {}", userId);
        throw new RuntimeException("用户名或密码错误");
    }

    @Transactional
    @Override
    public void register(User user) {
        String userId = user.getUserId();
        log.info("用户尝试注册，userId: {}", userId);

        // 验证有无输入其他信息
        if (userId == null || user.getPassword() == null || user.getUsername() == null) {
            log.warn("注册失败：必要参数缺失，userId: {}", userId);
            throw new RuntimeException("请填写完整信息");
        }

        // 看传入的账号是学生格式还是管理员格式
        if (UserUtil.checkUserId(userId, Constant.STU_REGEX)) {
            // 是学生
            log.debug("识别为用户类型：学生，userId: {}", userId);
            user.setRole(Constant.STU_ROLE);
        } else if (UserUtil.checkUserId(userId, Constant.ADMIN_REGEX)) {
            // 是管理员
            log.debug("识别为用户类型：管理员，userId: {}", userId);
            user.setRole(Constant.ADMIN_ROLE);
        } else {
            log.warn("注册失败：用户ID格式错误，userId: {}", userId);
            throw new RuntimeException("用户格式错误");
        }

        // 检查userId是否已存在
        User u = userMapper.selectUserById(user.getUserId());
        if (u != null) {
            log.warn("注册失败：用户已存在，userId: {}", userId);
            throw new RuntimeException("用户已存在");
        }

        // 设置时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 插入用户
        userMapper.insertUser(user);
        log.info("用户注册成功，userId: {}, 角色: {}", userId, user.getRole());
    }

    @LogOperation
    @Override
    public void bind(Integer buildingId, String roomId) {
        String userId = UserContext.getUserId();
        log.info("用户尝试绑定宿舍，userId: {}, buildingId: {}, roomId: {}", userId, buildingId, roomId);

        User user = new User();
        if (roomId == null || buildingId == null) {
            log.warn("绑定宿舍失败：参数不完整，userId: {}", userId);
            throw new RuntimeException("请填写完整信息");
        }

        user.setUserId(userId);
        user.setBuildingId(buildingId);
        user.setRoomId(roomId);
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());

        // 调用mapper的updateBindById方法
        userMapper.updateBindById(user);
        log.info("用户宿舍绑定成功，userId: {}, 楼栋: {}, 房间: {}", userId, buildingId, roomId);
    }

    @LogOperation
    @Override
    public void updatePass(String password) {
        String userId = UserContext.getUserId();
        log.info("用户尝试修改密码，userId: {}", userId);

        if (password == null || password.isEmpty()) {
            log.warn("修改密码失败：新密码为空，userId: {}", userId);
            throw new RuntimeException("密码不能为空");
        }

        // 获取原用户数据，看密码跟这个password是否一致
        User user = userMapper.selectUserById(userId);

        if (user == null) {
            log.error("修改密码失败：未找到用户，userId: {}", userId);
            throw new RuntimeException("用户不存在"); // 防止空指针，虽然原逻辑没写，但加上更安全，若必须完全一致可去掉此块直接让下面报NPE
        }

        if (user.getPassword().equals(password)) {
            log.warn("修改密码失败：新密码与原密码一致，userId: {}", userId);
            throw new RuntimeException("新密码不能与原密码一致");
        }

        // 修改密码
        user.setPassword(password);
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updatePassById(user);
        log.info("用户密码修改成功，userId: {}", userId);
    }

    @Override
    public LoginInfo info() {
        String userId = UserContext.getUserId();
        log.debug("获取用户详细信息，userId: {}", userId);

        // 获取id，然后带着id查
        User user = userMapper.selectUserById(userId);

        if (user == null) {
            log.warn("获取用户信息失败：用户不存在，userId: {}", userId);
            return null; // 或者抛异常，视原逻辑隐含意图而定，原代码若user为null会报NPE，这里保持原逻辑风险或返回null
        }

        // 选择需要的信息进行组装（token不需要再返回去了，用户端已经存在本地了，也不用下一步了）
        LoginInfo info = new LoginInfo(user.getUserId(), user.getUsername(), null, user.getRole(), user.getBuildingId(), user.getRoomId(), null);
        log.debug("用户信息获取成功，userId: {}", userId);
        return info;
    }
}