package com.bosch.eet.skill.management.rest;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.FileStorageDTO;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.enums.Layout;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.LayoutService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequestMapping(Routes.URI_REST_LAYOUT)
@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LayoutRest {

    LayoutService layoutService;

    @GetMapping("/{name}")
    public Resource getObject(@PathVariable("name") Layout layout) throws FileNotFoundException {
        return layoutService.getLayoutByName(layout);
    }

    @PostMapping
    public GenericResponseDTO<FileStorageDTO> upload(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("layout") Layout layout) throws IOException {
        return GenericResponseDTO.<FileStorageDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(layoutService.saveLayout(file, layout))
                .build();
    }
}
