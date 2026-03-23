package com.dems.service;

import com.dems.pojo.RepairRecord;
import com.dems.pojo.RepairRecordDetail;

import java.util.List;

public interface RepairRecordService {
    void create(RepairRecord rr);

    List<RepairRecord> listById();

    void delete(List<Integer> ids);

    List<RepairRecord> list(RepairRecord repairRecord);

    RepairRecordDetail detail(Integer id);

    void updateStatus(Integer status, Integer id);
}
