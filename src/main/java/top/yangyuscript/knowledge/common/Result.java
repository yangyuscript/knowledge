package top.yangyuscript.knowledge.common;

import org.springframework.data.util.Pair;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 16:17 2020/8/8
 */
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result() {

    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result(200, "success", data);
    }


    public static <T> Result<T> fail(T data) {
        return new Result(500, "fail", data);
    }

    public static <T> Result<T> build(Pair<Boolean, String> pair, T data) {
        return pair.getFirst() ? new Result<>(200, pair.getSecond(), data) : new Result<>(500, pair.getSecond(), data);
    }

    public static <T> Result<T> build(Pair<Boolean, String> pair) {
        return build(pair, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
