package com.dems.service.impl;

import com.dems.anno.LogOperation;
import com.dems.mapper.RepairImageMapper;
import com.dems.pojo.RepairImage;
import com.dems.service.RepairImageService;
import com.dems.utils.ImageNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class RepairImageServiceImpl implements RepairImageService {
    @Autowired
    RepairImageMapper repairImageMapper;
    @LogOperation
    @Override
    public Integer uploadRepairImage(MultipartFile file) {
        // 日志：方法开始执行
        log.info("Starting to process image upload.");

        RepairImage repairImage = new RepairImage();
        if (file == null) {
            log.error("No file was selected for uploading.");
            throw new RuntimeException("请选择图片");
        }

        // 日志：生成文件名
        String imageName = ImageNameUtil.getImageName(file.getOriginalFilename());
        log.info("Generated unique image name: {}", imageName);

        // 获得访问路径
        String imageUrl = "D:/drms/upload/" + imageName;
        log.info("Target image URL is: {}", imageUrl);

        // 存入本地
        try {
            file.transferTo(new java.io.File(imageUrl));
            log.info("File has been successfully saved to local disk.");
        } catch (IOException e) {
            log.error("Failed to save file to local disk.", e);
            throw new RuntimeException(e);
        }

        // 存到数据库（没关联repair_id版）
        repairImage.setImageUrl(imageName);
        repairImage.setCreateTime(LocalDateTime.now());

        repairImageMapper.insertRepairImage(repairImage);
        log.info("Image information has been saved to database. Image ID: {}", repairImage.getId());

        // 返回图片的id（已在mapper中拿到）
        return repairImage.getId();
    }
}
