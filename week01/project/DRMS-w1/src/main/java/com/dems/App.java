package com.dems;

import com.dems.pojo.User;
import com.dems.service.RepairRecordService;
import com.dems.service.UserService;
import com.dems.service.impl.RepairRecordServiceImpl;
import com.dems.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Scanner;


public class App {
    static User user = null;
    static UserService userService ;
    static RepairRecordService repairRecordService;
    static Scanner sc = new Scanner(System.in);
    static ConfigurableApplicationContext context;


    public static void main(String[] args) {
        context = SpringApplication.run(DrmsW1Application.class, args);
        userService=context.getBean(UserService.class);
        repairRecordService=context.getBean(RepairRecordService.class);
        while (true) {
            if (user == null) {
                //登录界面
                user = loginMenu();
            } else {
//                if (user.getRole() == 1) {
//                    //学生菜单
//                    studentMenu();
//                }
                /*else {
                    //管理员菜单
                    adminMenu();
                }*/
            }
        }
    }

    //登录界面
    private static User loginMenu() {
        User user = null;
        while (user == null) {

            System.out.println("===========================\n" +
                    "\uD83C\uDFE0 宿舍报修管理系统\n" +
                    "===========================\n" +
                    "1. 登录\n" +
                    "2. 注册\n" +
                    "3. 退出\n" +
                    "请选择操作（输入 1-3）：");
            int choose = sc.nextInt();
            switch (choose) {
                case 1 -> user = login();
                case 2 -> register();
                case 3 -> System.exit(0);
            }
        }
        return user;
    }

    //登录
    private static User login() {
        String userId;
        String password;
        User user = null;
        System.out.println("请输入工号：");
        userId = sc.next();
        System.out.println("请输入密码：");
        password = sc.next();
        //调用service的登录服务
        user = userService.login(userId, password);
        if (user == null) {
            System.out.println("工号或密码错误，登录失败！");
        } else {
            System.out.println("登录成功！");
        }
        return user;
    }

    //注册
    private static void register() {
        Integer role;
        String userId = "";
        String username;
        String password;
        String regex1 = "^(3125|3225)\\d{6}$";//学生工号的正则
        String regex2 = "^0025\\d{6}$";//管理员工号的正则
        System.out.println("请选择角色（1-学生，2-维修人员）：");
        role = sc.nextInt();
        if (role == 1) {
            System.out.println("请输入学号（前缀3125或3225）：");
            userId = sc.next();
            if (!userId.matches(regex1)) {
                System.out.println("学号格式错误！");
                return;
            }
        } else if (role == 2) {
            System.out.println("请输入工号（前缀0025）：");
            userId = sc.next();
            if (!userId.matches(regex2)) {
                System.out.println("工号格式错误！");
                return;
            }
        }
        System.out.println("请输入你的名字：");
        username = sc.next();
        System.out.println("请输入登录密码：");
        password = sc.next();

        User u = new User();
        System.out.println("注册成功！");

    }


}
