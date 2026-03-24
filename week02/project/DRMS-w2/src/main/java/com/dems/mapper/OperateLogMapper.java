package com.dems.mapper;

import com.dems.pojo.OperateLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperateLogMapper {
    //插入操作日志
    void insertLog(OperateLog operateLog);

    List<OperateLog> selectAll();
}
