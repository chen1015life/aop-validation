package com.wanchen.aopvalidation.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author Brian Su
 * @description: 包裝輸出到前端的資料，包含success, msg, data
 * @date: 2020/3/3
 */
@Data
@ApiModel(description = "資料傳輸格式")
public class ResponseDTO<T> {
    @ApiModelProperty(value = "此次操作是否成功", example = "true")
    private Boolean success;

    @ApiModelProperty(value = "此次操作後台回應訊息")
    private String msg;

    @ApiModelProperty(value = "後台回應操作時的UTC日期與時間", example = "0")
    private OffsetDateTime responseTime;

    @ApiModelProperty(value = "API操作標題")
    private String title;

    @ApiModelProperty(value = "資料內容")
    private T data;

    private ResponseDTO(Builder<T> builder) {
        this.success = builder.success;
        this.msg = builder.msg;
        this.responseTime = null;
        this.title = builder.title;
        this.data = builder.data;
    }

    public static <T> Builder<T> createSuccessBuilder() {
        return ResponseDTO.<T>createBuilder()
            .setSuccess(true);
    }

    public static <T> Builder<T> createFailureBuilder() {
        return ResponseDTO.<T>createBuilder()
            .setSuccess(false);
    }

    public static <T> ResponseDTO<T> createFailureResponse() {
        return ResponseDTO.<T>createBuilder()
            .setSuccess(false)
            .build();
    }

    public static <T> ResponseDTO<T> createSuccessResponse() {
        return ResponseDTO.<T>createBuilder()
            .setSuccess(true)
            .build();
    }

    public static ResponseDTO<Void> createVoidSuccessResponse() {
        return ResponseDTO.<Void>createBuilder()
            .setSuccess(true)
            .build();
    }

    public static ResponseDTO<Void> createVoidFailureResponse() {
        return ResponseDTO.<Void>createBuilder()
            .setSuccess(false)
            .build();
    }

    public static <T> Builder<T> createBuilder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private Boolean success;
        private String msg;
        private String title;
        private T data;

        public Builder() { }

        public Builder(ResponseDTO<T> copy) {
            this.success = copy.success;
            this.msg = copy.msg;
            this.data = copy.data;
        }

        public Builder<T> setSuccess(Boolean val) {
            success = val;
            return this;
        }

        public Builder<T> setMsg(String val) {
            msg = val;
            return this;
        }

        public Builder<T> setTitle(String val) {
            title = val;
            return this;
        }

        public Builder<T> setData(T val) {
            data = val;
            return this;
        }

        public ResponseDTO<T> build() {
            return new ResponseDTO<>(this);
        }
    }
}
