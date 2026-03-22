package com.dems.service.impl;

import com.dems.mapper.RepairImageMapper;
import com.dems.pojo.RepairImage;
import com.dems.service.RepairImageService;
import com.dems.utils.ImageNameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class RepairImageServiceImpl implements RepairImageService {
    @Autowired
    RepairImageMapper repairImageMapper;
    @Override
    public Integer uploadRepairImage(MultipartFile file) {
        RepairImage repairImage = new RepairImage();
        if (file==null){
            throw new RuntimeException("请选择图片");
        }
        //生成唯一文件名
        String imageName = ImageNameUtil.getImageName(file.getOriginalFilename());
        //获得访问路径
        String imageUrl =  "D:/upload/"+imageName;
        //存入本地
        try {
            file.transferTo(new java.io.File(imageUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //存到数据库（没关联repair_id版）
        repairImage.setImageUrl(imageUrl);
        repairImage.setCreateTime(LocalDateTime.now());

        repairImageMapper.insertRepairImage(repairImage);
        //返回图片的id（已在mapper中拿到）
        return repairImage.getId();
    }
}
