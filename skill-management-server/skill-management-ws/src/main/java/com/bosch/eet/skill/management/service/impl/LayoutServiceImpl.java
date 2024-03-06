package com.bosch.eet.skill.management.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.entity.FileStorage;
import com.bosch.eet.skill.management.enums.Layout;
import com.bosch.eet.skill.management.repo.FileStorageRepository;
import com.bosch.eet.skill.management.service.LayoutService;
import com.bosch.eet.skill.management.service.ObjectStorageService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LayoutServiceImpl implements LayoutService {

    ObjectStorageService objectStorageService;

    FileStorageRepository fileStorageRepository;

    ModelMapper mapper;
    
    @Value("${common.path}")
	private String projectStorageDir;

    /**
     * Save or update frontend layout
     *
     * @param file
     * @param layoutName
     * @return
     * @throws IOException
     */
	@Override
	public FileStorageDTO saveLayout(MultipartFile file, Layout layoutName) throws IOException {
		String layout = layoutName.getValue();
		FileStorage fileStorage = fileStorageRepository.findByNameStartingWith(layout);
		if (fileStorage == null || StringUtils.isBlank(fileStorage.getName())) {
			String prefix = UUID.randomUUID().toString();
			layout = layout + Constants.LINE + prefix + Constants.LINE + file.getOriginalFilename();
			fileStorage = FileStorage.builder().name(layout).size(file.getSize()).deleted(false).build();
		} else {
			fileStorage.setSize(file.getSize());
			layout = fileStorage.getName();
		}

		objectStorageService.putObject(file, layout, projectStorageDir);
		fileStorageRepository.save(fileStorage);

		return mapper.map(fileStorage, FileStorageDTO.class);
	}

    /**
     * Get layout Byname(For ex: overview_layout)
     *
     * @param layoutName
     * @return
     * @throws FileNotFoundException
     */
    @Override
    public Resource getLayoutByName(Layout layoutName) throws FileNotFoundException {
        String layout = layoutName.getValue();
        FileStorage fileStorage = fileStorageRepository.findByNameStartingWith(layout);
        if (fileStorage == null) {
//            throw new ResourceNotFoundException("Layout not found with type: " + layoutName.toString());
            return null;
        }
        String objectName = fileStorage.getName();
        return objectStorageService.getObject(objectName, projectStorageDir);
    }

    //    @Override
//    public ResponseEntity<Resource> getLayoutByName(Layout layoutName) throws FileNotFoundException {
//        String layout = layoutName.getValue();
//        FileStorage fileStorage = fileStorageRepository.findByNameStartingWith(layout);
//        if (fileStorage == null) {
//            throw new ResourceNotFoundException("Layout not found with type: " + layoutName.toString());
//        }
//        String objectName = fileStorage.getName();
//        Resource resource = objectStorageService.getObject(objectName);
//        if (resource.exists()) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + objectName);
//            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(resource);
//        }
//        return ResponseEntity.notFound().build();
//    }


}
