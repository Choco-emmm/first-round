package com.dems.controller;

import com.dems.pojo.Result;
import com.dems.service.RepairImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class RepairImageController {
    @Autowired
    RepairImageService repairImageService;

    /**
     * 上传报修图片
     * @param file 上传的图片文件
     * @return 上传成功后的图片在数据库的id
     */
    @PostMapping
    public Result uploadRepairImage(MultipartFile file){
        Integer id= repairImageService.uploadRepairImage(file);
        return Result.success(id);
    }

}
