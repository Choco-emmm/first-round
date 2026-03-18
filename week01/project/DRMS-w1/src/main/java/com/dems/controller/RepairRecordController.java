package com.dems.controller;

import com.dems.pojo.RepairRecord;
import com.dems.pojo.Result;
import com.dems.pojo.User;
import com.dems.service.RepairRecordService;
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
     * @param rr
     * @param session
     * @return
     */
    @PostMapping("/create")
    public Result createRepairRecord(@RequestBody RepairRecord rr, HttpSession  session){
        //从session中获取user
        User user = (User) session.getAttribute("user");
        //调用service的创建方法
       return repairRecordService.create(rr, user);
    }

    /**
     * 学生查看自己的报修记录
     * 不带详情信息，防止到时候页面卡顿
     * @param session
     * @return
     */
    @GetMapping("/listById")
    public Result listById(HttpSession session){
        User user = (User) session.getAttribute("user");
        return repairRecordService.listById(user.getUserId());
    }

    /**
     * 批量删除报修单（学生和管理员都能调用）
     * @param ids
     * @return
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
        return repairRecordService.delete(ids);
    }

    /**
     * 管理员查询所有报修单（支持按学号，姓名（姓名可以模糊查询），楼号，房号，类型，状态来查询）
     * 不带详情信息，防止到时候页面卡顿
     * @param repairRecord
     * @return
     */
    @GetMapping("/list")
    public Result list(@RequestBody(required = false) RepairRecord repairRecord){
        return repairRecordService.list(repairRecord);
    }

    /**
     * 查看单个报修单的详情（学生和管理员都能调用）
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public Result detail(@RequestParam Integer id){
        return repairRecordService.detail(id);
    }

    /**
     * 修改报修单状态
     * @param status
     * @param id
     * @return
     */
    @PutMapping("/updateStatus")
    public Result updateStatus(@RequestParam Integer status,@RequestParam Integer id){
        return repairRecordService.updateStatus(status,id);
    }

}
