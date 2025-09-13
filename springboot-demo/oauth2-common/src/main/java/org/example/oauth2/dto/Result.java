package org.example.oauth2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result() {}

    public static Result<Object> success() {
        return success("success", null);
    }
    public static <T> Result<T> success(T data) {
        return success("success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<T>(200, message, data);
    }

    public static <T> Result<T> error() {
        return error("error");
    }
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message);
    }
}
