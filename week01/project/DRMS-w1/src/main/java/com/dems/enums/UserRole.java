package com.dems.enums;

/**
 * 用户枚举类
 */
public enum UserRole {
    STUDENT(1,"学生","3125|3225\\d{6}","学号格式错误，应以3125或3225开头"),
    ADMIN(2,"管理员","0025\\d{6}","工号格式错误，应以0025开头");

   private final Integer code;
   private final String name;
   private final String idRegex;
   private final String errorMsg;

    UserRole(Integer code, String name, String idRegex, String errorMsg) {
        this.code = code;
        this.name = name;
        this.idRegex = idRegex;
        this.errorMsg = errorMsg;
    }

    /**
     * 根据code获取用户角色
     * @param role
     * @return
     */
    public static UserRole getUserRole(Integer role) {
        for (UserRole value : values()) {
            if (value.code.equals(role)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getIdRegex() {
        return idRegex;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
