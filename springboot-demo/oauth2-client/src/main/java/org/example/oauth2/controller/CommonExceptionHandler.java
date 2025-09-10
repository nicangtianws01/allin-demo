package org.example.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.oauth2.dto.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.NoPermissionException;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(value = NoPermissionException.class)
    public Result<String> noPermissionException(Exception e) {
        log.error(e.getMessage(), e);
        return new Result<String>().error(403, e.getMessage());
    }
}
