package com.wanchen.aopvalidation.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SysConfigDTO extends Request {
    private Long configId;

    @NotBlank(message = "參數名稱不能為空")
    @Size(min = 0, max = 100, message = "參數名稱不能超過10個字元")
    private String configName;
}
