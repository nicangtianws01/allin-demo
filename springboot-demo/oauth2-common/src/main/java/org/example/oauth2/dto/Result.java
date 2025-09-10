package org.example.oauth2.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class Result<T> {
    private Integer code = 200;
    private String msg;
    private T data;
    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Result(int code) {
        this.code = code;
    }
    public Result() {

    }

    public Result<T> success() {
        return success("success", null);
    }
    public Result<T> success(String msg) {
        return success(msg, null);
    }
    public Result<T> success(String msg, T data) {
        this.msg = msg;
        this.data = data;
        return this;
    }

    public Result<T> error() {
        return error("error");
    }
    public Result<T> error(String msg) {
        this.code = 500;
        this.msg = msg;
        return this;
    }
    public Result<T> error(int code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }
}
