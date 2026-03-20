package com.dems.controller;

import com.dems.pojo.RepairRecord;
import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.RepairRecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repairRecord")
public class RepairRecordController {
    @Autowired
    RepairRecordService repairRecordService;

    /**
     * 学生创建报修单
     * @param rr 内含stuName，buildingId，roomId，type,detail
     */
    @PostMapping("/student/create")
    public Result createRepairRecord(@RequestBody RepairRecord rr, HttpServletRequest request){
        //获取token，把token和rr传到Service
        String token = request.getHeader("token");
        repairRecordService.create(rr, token);
        return Result.success();
    }

    /**
     * 学生查看自己的报修记录
     * 不带详情信息，防止到时候页面卡顿
     * @param session
     * @return
     */
    @GetMapping("/student/listById")
    public Result listById(HttpServletRequest request){
        //获取token，把token和rr传到Service
        String token = request.getHeader("token");
        //将token传入Service
        List<RepairRecord> rr = repairRecordService.listById(token);
        return Result.success(rr);
    }

    /**
     * 批量删除报修单（学生和管理员都能调用）
     * @param ids 要删除的报修单的id（路径参数）
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestParam List<Integer> ids){
        /**
         *  我的想法是，到时候无论是学生还是管理员在页面上看到的都是自己能操作的报修单
         *  到时候选中的时候就会给服务端发送选中的报修单相应的ids
         *  然后进行删除就行
         *  所以删除的方法都是通用的
         */
        //通过报修单id来批量删除
        repairRecordService.delete(ids);
        return Result.success();
    }

    /**
     * 管理员查询所有报修单（支持按学号，姓名（姓名可以模糊查询），楼号，房号，类型，状态来查询）
     * 不带详情信息，防止到时候页面卡顿
     * @param repairRecord 内含查询条件
     */
    @GetMapping("/admin/list")
    public Result list(@RequestBody(required = false) RepairRecord repairRecord){
        return Result.success(repairRecordService.list(repairRecord));
    }

    /**
     * 查看单个报修单的详情（学生和管理员都能调用）
     * @param id 要查的报修单的id
     */
    @GetMapping("/detail")
    public Result detail(@RequestParam Integer id){
        RepairRecord rr = repairRecordService.detail(id);
        return Result.success(rr);
    }

    /**
     * 管理员修改报修单状态
     * @param status
     * @param id
     * @return
     */
    @PutMapping("/admin/updateStatus")
    public Result updateStatus(@RequestParam Integer status,@RequestParam Integer id){
        repairRecordService.updateStatus(status,id);
        return Result.success();
    }

}
