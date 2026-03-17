package com.dems.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
   private String userId;//工号
   private String username;//用户名
   private String password;//密码
   private Integer role;//用户身份 1-学生 2-管理员
   private Integer buildingId;//楼号
   private String roomId;//房号
   private LocalDateTime createTime;//创建时间
   private LocalDateTime updateTime;//更新时间

}
