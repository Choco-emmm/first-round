package com.dems.service;

import org.springframework.web.multipart.MultipartFile;

public interface RepairImageService {
    Integer uploadRepairImage(MultipartFile file);
}
