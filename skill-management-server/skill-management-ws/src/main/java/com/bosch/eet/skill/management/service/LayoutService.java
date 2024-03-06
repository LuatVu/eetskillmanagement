package com.bosch.eet.skill.management.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.enums.Layout;

public interface LayoutService {
    FileStorageDTO saveLayout(MultipartFile file, Layout layoutName) throws IOException;

    Resource getLayoutByName(Layout layout) throws FileNotFoundException;
}
