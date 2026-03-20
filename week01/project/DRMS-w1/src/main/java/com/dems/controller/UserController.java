package com.dems.controller;

import com.dems.pojo.LoginInfo;
import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;

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
        if (loginInfo != null) {
            Integer role = loginInfo.getRole();
            //素学生
            if (role == 1) {
                //再判断有没有绑定宿舍
                if (loginInfo.getBuildingId() == null || loginInfo.getRoomId() == null) {
                    loginInfo.setUrl("学生绑定宿舍页面的url");
                } else {
                    //已绑定宿舍
                    loginInfo.setUrl("学生操作页面的url");
                }
            } else {
                //素管理员
                loginInfo.setUrl("管理员操作页面的url");
            }
            return Result.success(loginInfo);
        } else {
            return Result.error("工号或密码错误");
        }
    }

    /**
     * 注册
     * @param user 内含userId,password,role,username
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
    @PutMapping("/student/bind")
    public Result bind(@RequestBody User user, HttpServletRequest httpRequest) {
        //从Request中获取token，把user和token传入Service层
        String token = httpRequest.getHeader("token");
        userService.bind(user.getBuildingId(), user.getRoomId(), token);
        return Result.success();
    }

    /**
     * 修改密码
     * @param user 里面只有 password
     */
    @PutMapping("/updatePass")
    public Result updatePass(@RequestBody User user, HttpServletRequest httpRequest) {
        //从token中获得userId，将user和token一起传入service层
        String token = httpRequest.getHeader("token");
        userService.updatePass(user.getPassword(), token);
        //返回结果
        return Result.success();
    }

    /**
     * 查看自己的基本信息
     */
    @GetMapping("/info")
    public Result info(HttpServletRequest httpRequest) {
        //从Request中获取token，传入Service层
        String token = httpRequest.getHeader("token");
        //获取用户信息
        LoginInfo loginInfo = userService.info(token);
        //返回用户信息给前端
        return Result.success(loginInfo);
    }
}


