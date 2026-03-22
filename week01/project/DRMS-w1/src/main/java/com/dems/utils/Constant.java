package com.dems.utils;

public class Constant {
    //用户角色+正则
    public static final String STU_REGEX = "^(3125|3225)\\d{6}$";//学生工号的正则
    public static final String ADMIN_REGEX = "^0025\\d{6}$";//管理员工号的正则
    public static final Integer STU_ROLE = 1;//学生角色
    public static final Integer ADMIN_ROLE = 2;//管理员角色

    // 登录下一步标识
    public static final String STEP_BIND_ROOM = "STU_BIND";
    public static final String STEP_STU_HOME = "STU_HOME";
    public static final String STEP_ADMIN_HOME = "ADMIN_HOME";

    // 私有构造防止实例化
    private Constant() {}
}
