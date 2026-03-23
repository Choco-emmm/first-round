package com.dems.mapper;

import com.dems.pojo.RepairImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RepairImageMapper {
    public void insertRepairImage(RepairImage repairImage) ;

    void batchUpdate(Integer repairId, List<Integer> imgIds);

    List<String> selectImageUrlsByRecordId (Integer repairId);
}
