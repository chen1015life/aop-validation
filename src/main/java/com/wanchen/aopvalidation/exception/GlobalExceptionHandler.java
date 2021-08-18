package com.wanchen.aopvalidation.exception;

import com.wanchen.aopvalidation.model.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 統一異常攔截
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDTO<?>> badRequestErrorHandler(BadRequestException e) {
        return new ResponseEntity<>(
               ResponseDTO.createFailureBuilder()
                       .setMsg(e.getMessage())
                       .build(),
               HttpStatus.BAD_REQUEST
        );
   }
}
