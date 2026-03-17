package com.dems.mapper;

import com.dems.pojo.RepairRecord;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RepairRecordMapper {
    void insertRecord(RepairRecord rr);

    List<RepairRecord> selectByUserId(String userId);

    void batchDelete(List<Integer> ids);

    List<RepairRecord> selectAll(RepairRecord repairRecord);

    RepairRecord selectDetail(Integer id);

    void updateStatusById(Integer status, Integer id, LocalDateTime updateTime);

    RepairRecord selectById(Integer id);
}
