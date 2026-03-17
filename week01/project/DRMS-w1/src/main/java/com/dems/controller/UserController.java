package com.dems.controller;

import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpSession session){
        user = userService.login(user.getUserId(), user.getPassword());
        if(user!=null){
            session.setAttribute("user",user);
            Integer role = user.getRole();
            //素学生
            if(role==1){
               //再判断有没有绑定宿舍
                if(user.getBuildingId()==null||user.getRoomId()==null){
                    return Result.success("学生绑定宿舍页面的url");
                }else {
                    //已绑定宿舍
                    return Result.success("学生操作页面的url");
                }
            }else {
                //素管理员
                return Result.success("管理员操作页面的url");
            }
        }else {
            return Result.error("工号或密码错误");
        }
    }

    //注册
    @PostMapping("/register")
    public Result register (@RequestBody User user){

        return userService.register(user);
    }

    //初次登录，绑定宿舍楼号和房号
    @PutMapping("/bind")
    public Result bind(@RequestBody User user, HttpSession session){
        //从session中获取user
        User u = (User) session.getAttribute("user");
        //调用service的绑定
        Result r1= userService.bind(user.getBuildingId(),user.getRoomId(),u.getUserId());

        if(r1.getCode()==1){
            //绑定成功，更新session中的数据
            User uu = (User) r1.getData();
            u.setBuildingId(uu.getBuildingId());
            u.setRoomId(uu.getRoomId());
            u.setUpdateTime(uu.getUpdateTime());
            session.setAttribute("user",u);
            return Result.success("学生操作页面的url");
        }
        return r1;
    }

    //修改密码
    @PutMapping("/updatePass")
    public Result updatePass(@RequestBody User user, HttpSession session){
        //从session中获取user
        User u = (User) session.getAttribute("user");
        //调用service的修改密码
        Result r= userService.updatePass(user.getPassword(),u.getUserId());
        if(r.getCode()==1){
            User uu = (User) r.getData();
            //成功，则修改session的数据
            u.setPassword(uu.getPassword());
            u.setUpdateTime(uu.getUpdateTime());
            session.setAttribute("user",u);
            //获得身份，根据身份决定跳转的页面
            Integer role = u.getRole();
            if(role==1){
                //素学生
                return Result.success("学生操作页面的url");
            }else {
                //素管理员
                return Result.success("管理员操作页面的url");
            }
        }
        return r;
    }

    //查看自己的基本信息
    @GetMapping("/info")
    public Result info(HttpSession session){
        User user = (User) session.getAttribute("user");
        User u=new User();//决定要显示的信息
        u.setUserId(user.getUserId());
        u.setUsername(user.getUsername());
        u.setRole(user.getRole());
        u.setBuildingId(user.getBuildingId());
        u.setRoomId(user.getRoomId());
        return Result.success(u);
    }


}
