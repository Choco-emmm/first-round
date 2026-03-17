package com.dems.service.impl;

import com.dems.mapper.RepairRecordMapper;
import com.dems.pojo.RepairRecord;
import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.RepairRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepairRecordServiceImpl implements RepairRecordService {
    @Autowired
    RepairRecordMapper repairRecordMapper;

    @Override
    public Result create(RepairRecord rr, User user) {
        //先判断rr中的一些数据是否为空
        if(rr.getStuName()==null||rr.getBuildingId()==null||rr.getRoomId()==null||rr.getType()==null||rr.getDetail()==null){
            return Result.error("信息没有填完整");
        }
        //将user中的所需数据添入rr中
        rr.setStuId(user.getUserId());
        //设置一些默认值
        rr.setStatus(1);
        rr.setCreateTime(LocalDateTime.now());
        rr.setUpdateTime(LocalDateTime.now());
        //调用mapper的插入
        repairRecordMapper.insertRecord(rr);
        return Result.success();
    }

    @Override
    public Result listById(String userId) {
        //通过userId来获得报修单数据
        List<RepairRecord> list = repairRecordMapper.selectByUserId(userId);
        return Result.success(list);

    }

    @Transactional
    @Override
    public Result delete(List<Integer> ids) {
        //通过报修表的id来批量删除
        repairRecordMapper.batchDelete(ids);
        return Result.success();
    }

    @Override
    public Result list(RepairRecord repairRecord) {
        //按条件查询数据
       List<RepairRecord> list= repairRecordMapper.selectAll(repairRecord);
       return Result.success(list);
    }

    @Override
    public Result detail(Integer id) {
       //按id来查询详情数据
        RepairRecord repairRecord=repairRecordMapper.selectDetail(id);
        return Result.success(repairRecord);
    }

    @Override
    public Result updateStatus(Integer status, Integer id) {
        //查找对应的报修单，如果状态和要修改的相同就不给修改
       RepairRecord repairRecord= repairRecordMapper.selectById(id);
       if(repairRecord.getStatus().equals(status)){
           return Result.error("状态不是根本没变嘛！");
       }
       if(status==null){
           return Result.error("数据不能为空！");
       }
        LocalDateTime updateTime = LocalDateTime.now();
        //按id来修改状态
        repairRecordMapper.updateStatusById(status,id,updateTime);
        return Result.success();
    }
}
