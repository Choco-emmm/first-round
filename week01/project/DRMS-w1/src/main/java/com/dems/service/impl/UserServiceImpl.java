package com.dems.service.impl;

import com.dems.mapper.UserMapper;
import com.dems.pojo.LoginInfo;
import com.dems.pojo.User;
import com.dems.service.UserService;
import com.dems.utils.Constant;
import com.dems.utils.UserContext;
import com.dems.utils.JwtUtil;
import com.dems.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

   @Autowired
    private UserMapper userMapper;
    @Override
    public LoginInfo login(String userId, String password) {
        User user=null;
        //调用mapper的查询
        user=userMapper.selectUserByIdPass(userId,password);
        if(user!=null){
            LoginInfo loginInfo=null;
            String username = user.getUsername();
            Integer role = user.getRole();
            //生成令牌，封装到LoginInfo
            Map<String, Object> map=new HashMap<>();
            map.put("userId",userId);
            map.put("username",username);
            map.put("role",role);
            //生成令牌
            String token = JwtUtil.generateJwtToken(map);
            loginInfo=new LoginInfo(userId,username,token,user.getRole(),user.getBuildingId(),user.getRoomId(),null);
            //看是啥角色
            if(role.equals(Constant.STU_ROLE)){
            //是学生
                if(user.getBuildingId()==null||user.getRoomId()==null){
                    //没绑宿舍
                    loginInfo.setNextStep(Constant.STEP_BIND_ROOM);
                }else {
                    //已绑定宿舍
                    loginInfo.setNextStep(Constant.STEP_STU_HOME);
                }
            }else {
                //是管理员
                loginInfo.setNextStep(Constant.STEP_ADMIN_HOME);
            }
            return loginInfo;
        }
        throw new RuntimeException("用户名或密码错误");
    }

    @Transactional
    @Override
    public void register(User user) {
        String userId = user.getUserId();
        //验证有无输入其他信息
        if(userId==null||user.getPassword()==null||user.getUsername()==null){
            throw new RuntimeException("请填写完整信息");
        }

        //看传入的账号是学生格式还是管理员格式（
        if(UserUtil.checkUserId(userId, Constant.STU_REGEX)){
            //是学生
            user.setRole(Constant.STU_ROLE);
        }else if(UserUtil.checkUserId(userId, Constant.ADMIN_REGEX)){
            //是管理员
            user.setRole(Constant.ADMIN_ROLE);
        }else {
            throw new RuntimeException("用户格式错误");
        }

        //检查userId是否已存在
        User u = userMapper.selectUserById(user.getUserId());
        if (u!=null){
            throw new RuntimeException("用户已存在");
        }

        //设置时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        //插入用户
        userMapper.insertUser(user);
    }

    @Override
    public void bind(Integer buildingId, String roomId) {
        User user=new User();
        if(roomId==null||buildingId==null){
            throw new RuntimeException("请填写完整信息");
        }
        String userId=UserContext.getUserId();
        user.setUserId(userId);
        user.setBuildingId(buildingId);
        user.setRoomId(roomId);
        //设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        //调用mapper的updateBindById方法
        userMapper.updateBindById(user);
    }

    @Override
    public  void updatePass(String password) {
        if(password==null|| password.isEmpty()){
            throw new RuntimeException("密码不能为空");
        }
       //获取用户id
        String userId = UserContext.getUserId();
        //获取原用户数据，看密码跟这个password是否一致
        User user = userMapper.selectUserById(userId);
        if(user.getPassword().equals( password)){
            throw new RuntimeException("新密码不能与原密码一致");
        }
        //修改密码
        user.setPassword(password);
        //设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updatePassById(user);
    }

    @Override
    public LoginInfo info() {
        //获取id，然后带着id查
        String userId =UserContext.getUserId();
        User user = userMapper.selectUserById(userId);
        //选择需要的信息进行组装（token不需要再返回去了，用户端已经存在本地了，也不用下一步了）
        return new LoginInfo(user.getUserId(),user.getUsername(),null,user.getRole(),user.getBuildingId(),user.getRoomId(),null);
    }
}
