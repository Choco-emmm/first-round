package com.dems.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairImage {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 关联的报修记录ID (外键)
     */
    private Integer repairId;

    /**
     * 图片访问地址
     */
    private String imageUrl;

    /**
     * 上传时间
     */
    private LocalDateTime createTime;

}
