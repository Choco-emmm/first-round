package com.dems.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairRecord {
    private Integer id;//报修单id
    private String stuId;//报修学生的工号
    private String stuName;//报修学生的姓名
    private Integer buildingId;//楼号
    private String roomId;//房号
    private Integer type;//报修类型 1-水电器 2-家具 3-其他
    private String detail;//详情描述 250字以内
    private Integer status;//状态 1-待处理 2-处理中 3-已完成
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间

}
