package com.wanchen.aopvalidation.controller;

import com.wanchen.aopvalidation.model.ResponseDTO;
import com.wanchen.aopvalidation.model.SysConfigDTO;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysConfigController {
    @PostMapping("/sysconfig")
    public ResponseDTO<SysConfigDTO> add(@Validated @RequestBody SysConfigDTO sysConfigDTO, BindingResult bindingResult) {
        return ResponseDTO.<SysConfigDTO>createSuccessBuilder()
                .setData(sysConfigDTO)
                .build();
    }
}
