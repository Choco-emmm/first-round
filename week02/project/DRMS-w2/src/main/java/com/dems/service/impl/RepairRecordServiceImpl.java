package com.dems.service.impl;

import com.dems.anno.LogOperation;
import com.dems.mapper.RepairImageMapper;
import com.dems.mapper.RepairRecordMapper;
import com.dems.pojo.RepairRecord;
import com.dems.pojo.RepairRecordDetail;
import com.dems.service.RepairRecordService;
import com.dems.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RepairRecordServiceImpl implements RepairRecordService {
    @Autowired
    RepairRecordMapper repairRecordMapper;
    @Autowired
    RepairImageMapper repairImageMapper;
    //待处理
    private static final Integer STATUS_PENDING = 1;
    @LogOperation
    public void create(RepairRecord rr) {
        // 记录开始执行
        log.info("开始创建报修单，用户ID: {}", UserContext.getUserId());

        //先判断rr中的一些数据是否为空
        if(rr.getStuName()==null||rr.getBuildingId()==null||rr.getRoomId()==null||rr.getType()==null||rr.getDetail()==null){
            // 记录警告日志
            log.warn("创建报修单失败：必要参数为空。用户ID: {}", UserContext.getUserId());
            throw new RuntimeException("数据不能为空！");
        }

        //通过token获取用户信息
        String userId = UserContext.getUserId();

        rr.setStuId(userId);
        //设置一些默认值
        rr.setStatus(STATUS_PENDING);//未处理
        rr.setCreateTime(LocalDateTime.now());
        rr.setUpdateTime(LocalDateTime.now());

        //调用mapper的插入，将id返回对象中
        repairRecordMapper.insertRecord(rr);
        log.info("报修单主表插入成功，生成ID: {}", rr.getId());

        //再去更新图片的repair_id
        repairImageMapper.batchUpdate(rr.getId(),rr.getImgIds());
        log.info("报修单图片关联更新完成，报修单ID: {}", rr.getId());
    }

    @Override
    public List<RepairRecord> listById() {
        String userId = UserContext.getUserId();
        log.debug("查询用户报修单列表，用户ID: {}", userId);

        //通过userId来获得报修单数据
        List<RepairRecord> result = repairRecordMapper.selectByUserId(userId);

        log.debug("用户ID: {} 查询完成，共找到 {} 条记录", userId, result == null ? 0 : result.size());
        return result;
    }

    @LogOperation
    @Transactional
    @Override
    public void delete(List<Integer> ids) {
        log.info("开始批量删除报修单，IDs: {}", ids);
        try {
            //通过报修表的id来批量删除
            repairRecordMapper.batchDelete(ids);
            log.info("批量删除报修单成功，IDs: {}", ids);
        } catch (Exception e) {
            log.error("批量删除报修单失败，IDs: {}, 错误: {}", ids, e.getMessage());
            throw e; // 保持原有事务回滚行为
        }
    }

    @Override
    public List<RepairRecord> list(RepairRecord repairRecord) {
        log.debug("管理员条件查询报修单，查询条件: {}", repairRecord);
        //管理员按条件查询数据
        return  repairRecordMapper.selectAll(repairRecord);
    }

    @Override
    public RepairRecordDetail detail(Integer id) {
        log.debug("查询报修单详情，ID: {}", id);
        //按id来查询详情数据
        RepairRecordDetail result = repairRecordMapper.selectDetail(id);
        if (result == null) {
            log.warn("未找到报修单详情，ID: {}", id);
        }
        return result;
    }

    @LogOperation
    @Override
    public void updateStatus(Integer status, Integer id) {
        log.info("尝试更新报修单状态，ID: {}, 目标状态: {}", id, status);

        //查找对应的报修单，如果状态和要修改的相同就不给修改
        RepairRecord repairRecord = repairRecordMapper.selectById(id);

        if(repairRecord.getStatus().equals(status)){
            log.warn("状态未变更，拒绝更新。ID: {}, 当前状态: {}, 目标状态: {}", id, repairRecord.getStatus(), status);
            throw new RuntimeException("状态不是根本没变嘛！");
        }

        if(status==null||id==null){
            log.warn("更新状态参数为空，status: {}, id: {}", status, id);
            throw new RuntimeException("数据不能为空！");
        }

        LocalDateTime updateTime = LocalDateTime.now();
        //按id来修改状态
        repairRecordMapper.updateStatusById(status,id,updateTime);
        log.info("报修单状态更新成功，ID: {}, 新状态: {}", id, status);
    }
}
