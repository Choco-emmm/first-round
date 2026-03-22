package com.dems.mapper;

import com.dems.pojo.RepairImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepairImageMapper {
    public void insertRepairImage(RepairImage repairImage) ;
}
