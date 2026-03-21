package com.dems.service.impl;

import com.dems.mapper.RepairRecordMapper;
import com.dems.mapper.UserMapper;
import com.dems.pojo.RepairRecord;
import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.RepairRecordService;
import com.dems.utils.UserContext;
import com.dems.utils.jwtUtil;
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
    //待处理
    private static final Integer STATUS_PENDING = 1;

    @Override
    public void create(RepairRecord rr) {
        //先判断rr中的一些数据是否为空
        if(rr.getStuName()==null||rr.getBuildingId()==null||rr.getRoomId()==null||rr.getType()==null||rr.getDetail()==null){
           throw new RuntimeException("数据不能为空！");
        }
        //通过token获取用户信息
        String userId = UserContext.getUserId();

        rr.setStuId(userId);
        //设置一些默认值
        rr.setStatus(STATUS_PENDING);//未处理
        rr.setCreateTime(LocalDateTime.now());
        rr.setUpdateTime(LocalDateTime.now());
        //调用mapper的插入
        repairRecordMapper.insertRecord(rr);
    }

    @Override
    public List<RepairRecord> listById() {
        String userId = UserContext.getUserId();
        //通过userId来获得报修单数据
        return repairRecordMapper.selectByUserId(userId);

    }

    @Transactional
    @Override
    public void delete(List<Integer> ids) {
        //通过报修表的id来批量删除
        repairRecordMapper.batchDelete(ids);
    }

    @Override
    public List<RepairRecord> list(RepairRecord repairRecord) {
        //管理员按条件查询数据
        return  repairRecordMapper.selectAll(repairRecord);
    }

    @Override
    public RepairRecord detail(Integer id) {
       //按id来查询详情数据
        return repairRecordMapper.selectDetail(id);
    }

    @Override
    public void updateStatus(Integer status, Integer id) {
        //查找对应的报修单，如果状态和要修改的相同就不给修改
       RepairRecord repairRecord= repairRecordMapper.selectById(id);
       if(repairRecord.getStatus().equals(status)){
           throw new RuntimeException("状态不是根本没变嘛！");
       }
       if(status==null||id==null){
           throw new RuntimeException("数据不能为空！");
       }
        LocalDateTime updateTime = LocalDateTime.now();
        //按id来修改状态
        repairRecordMapper.updateStatusById(status,id,updateTime);
    }
}
