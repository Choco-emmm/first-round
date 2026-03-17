package com.dems.service;

import com.dems.pojo.RepairRecord;
import com.dems.pojo.Result;
import com.dems.pojo.User;

import java.util.List;

public interface RepairRecordService {
    Result create(RepairRecord rr, User user);

    Result listById(String userId);

    Result delete(List<Integer> ids);

    Result list(RepairRecord repairRecord);

    Result detail(Integer id);


    Result updateStatus(Integer status, Integer id);
}
