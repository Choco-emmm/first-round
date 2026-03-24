package com.dems.controller;

import com.dems.pojo.RepairRecord;
import com.dems.pojo.RepairRecordDetail;
import com.dems.pojo.Result;
import com.dems.service.RepairRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/repairRecord")
public class RepairRecordController {
    @Autowired
    private  RepairRecordService repairRecordService;

    /**
     * 学生创建报修单
     * @param rr 内含stuName，buildingId，roomId，type,detail,imgIds（只有这个可为空）
     */
    @PostMapping("/student")
    public Result createRepairRecord(@RequestBody RepairRecord rr){
        repairRecordService.create(rr);
        return Result.success();
    }

    /**
     * 学生查看自己的报修记录
     * 不带详情信息，防止到时候页面卡顿
     */
    @GetMapping("/student")
    public Result listById(){
        List<RepairRecord> rr = repairRecordService.listById();
        return Result.success(rr);
    }

    /**
     * 批量删除报修单（学生和管理员都能调用）
     * @param ids 要删除的报修单的id（路径参数）
     */
    @DeleteMapping()
    public Result delete(@RequestParam List<Integer> ids){
        //通过报修单id来批量删除
        repairRecordService.delete(ids);
        return Result.success();
    }

    /**
     * 管理员查询所有报修单（支持按学号，姓名（姓名可以模糊查询），楼号，房号，类型，状态来查询）
     * 不带详情信息，防止到时候页面卡顿
     * @param repairRecord 内含查询条件
     */
    @GetMapping("/admin")
    public Result list(@RequestBody(required = false) RepairRecord repairRecord){
        return Result.success(repairRecordService.list(repairRecord));
    }

    /**
     * 查看单个报修单的详情（学生和管理员都能调用）
     * @param id 要查的报修单的id
     */
    @GetMapping()
    public Result detail(@RequestParam Integer id){
        RepairRecordDetail rrd = repairRecordService.detail(id);
        return Result.success(rrd);
    }

    /**
     * 管理员修改报修单状态
     * @param status 状态
     * @param id 报修单id
     */
    @PutMapping("/admin")
    public Result updateStatus(@RequestParam Integer status,@RequestParam Integer id){
        repairRecordService.updateStatus(status,id);
        return Result.success();
    }

}
