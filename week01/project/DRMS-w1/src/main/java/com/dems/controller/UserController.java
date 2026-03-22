package com.dems.controller;

import com.dems.pojo.LoginInfo;
import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.UserService;
import com.dems.utils.Constant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    UserService userService;

    /**
     * 登录
     * @param user 内有userId和password
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        LoginInfo loginInfo = userService.login(user.getUserId(), user.getPassword());
            return Result.success(loginInfo);
    }

    /**
     * 注册
     * @param user 内含userId,password,username
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    /**
     * 学生初次登录，绑定宿舍楼号和房号
     * @param user 内有楼号和房号
     */
    @PutMapping("/student")
    public Result bind(@RequestBody User user) {
        userService.bind(user.getBuildingId(), user.getRoomId());
        return Result.success();
    }

    /**
     * 修改密码
     * @param user 里面只有 password
     */
    @PutMapping()
    public Result updatePass(@RequestBody User user) {
        userService.updatePass(user.getPassword());
        //返回结果
        return Result.success();
    }

    /**
     * 查看自己的基本信息
     */
    @GetMapping()
    public Result info() {
        //获取用户信息
        LoginInfo loginInfo = userService.info();
        //返回用户信息给前端
        return Result.success(loginInfo);
    }
}


