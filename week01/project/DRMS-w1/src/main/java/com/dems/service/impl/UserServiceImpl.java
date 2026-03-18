package com.dems.service.impl;

import com.dems.enums.UserRole;
import com.dems.mapper.UserMapper;
import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.UserService;
import com.dems.utils.userUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    public static final String STU_REGEX = "^(3125|3225)\\d{6}$";//学生工号的正则
    public static final String ADMIN_REGEX = "^0025\\d{6}$";//管理员工号的正则
   @Autowired
    private UserMapper userMapper;
    @Override
    public User login(String userId, String password) {
        User user=null;
        //调用mapper的查询
        user=userMapper.selectUserByIdPass(userId,password);
        return user;
    }

    @Transactional
    @Override
    public Result register(User user) {
        String userId = user.getUserId();
        Integer role = user.getRole();
        //验证有无输入其他信息
        if(userId==null||role==null||user.getPassword()==null||user.getUsername()==null){
            return Result.error("信息填写不完整");
        }
        //验证角色是否正确（使用枚举类）
        UserRole userRole = UserRole.getUserRole(role);
        if(userRole==null){
            return Result.error("不存在这个角色");
        }
        //验证工号格式（使用枚举类）
        if(!userUtil.checkUserId(userId,userRole.getIdRegex())){
            return Result.error(userRole.getErrorMsg());
        }
        //检查userId是否已存在
        User u = userMapper.selectUserById(user.getUserId());
        if (u!=null){
            return Result.error("工号已存在！");
        }
        //设置时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        //插入用户
        userMapper.insertUser(user);
        return Result.success();
    }

    @Override
    public Result bind(Integer buildingId, String roomId, String userId) {
        User user=new User();
        if(roomId==null||buildingId==null){
            return Result.error("信息填写不完整");
        }
        user.setBuildingId(buildingId);
        user.setRoomId(roomId);
        user.setUserId(userId);
        //设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        //调用mapper的updateBindById方法
        userMapper.updateBindById(user);

        return Result.success(user);
    }

    @Override
    public Result updatePass(String password, String userId) {
       User user=new User();
       user.setPassword(password);
       user.setUserId(userId);
       user.setUpdateTime(LocalDateTime.now());
       //调用mapper的updatePassById方法
        userMapper.updatePassById(user);
        return Result.success(user);
    }


}
