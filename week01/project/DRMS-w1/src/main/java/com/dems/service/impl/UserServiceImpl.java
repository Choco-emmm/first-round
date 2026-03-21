package com.dems.service.impl;

import com.dems.enums.UserRole;
import com.dems.mapper.UserMapper;
import com.dems.pojo.LoginInfo;
import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.UserService;
import com.dems.utils.UserContext;
import com.dems.utils.jwtUtil;
import com.dems.utils.userUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    public static final String STU_REGEX = "^(3125|3225)\\d{6}$";//学生工号的正则
    public static final String ADMIN_REGEX = "^0025\\d{6}$";//管理员工号的正则
   @Autowired
    private UserMapper userMapper;
    @Override
    public LoginInfo login(String userId, String password) {
        User user=null;
        //调用mapper的查询
        user=userMapper.selectUserByIdPass(userId,password);
        if(user!=null){
            String username = user.getUsername();
            //生成令牌，封装到LoginInfo
            Map<String, Object> map=new HashMap<>();
            map.put("userId",userId);
            map.put("username",username);
            map.put("role",user.getRole());
            String token = jwtUtil.generateJwtToken(map);
            return new LoginInfo(userId,username,token,user.getRole(),user.getBuildingId(),user.getRoomId(),null);
        }
        return null;
    }

    @Transactional
    @Override
    public void register(User user) {
        String userId = user.getUserId();
        Integer role = user.getRole();
        //验证有无输入其他信息
        if(userId==null||role==null||user.getPassword()==null||user.getUsername()==null){
            throw new RuntimeException("请填写完整信息");
        }
        //验证角色是否正确（使用枚举类）
        UserRole userRole = UserRole.getUserRole(role);
        if(userRole==null){
            throw new RuntimeException("用户角色错误");
        }
        //验证工号格式（使用枚举类）
        if(!userUtil.checkUserId(userId,userRole.getIdRegex())){
            throw new RuntimeException("用户角色错误");
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
        //选择需要的信息进行组装（token不需要再返回去了，用户端已经存在本地了）
        return new LoginInfo(user.getUserId(),user.getUsername(),null,user.getRole(),user.getBuildingId(),user.getRoomId(),null);
    }
}
