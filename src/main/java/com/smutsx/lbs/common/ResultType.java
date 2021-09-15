package com.smutsx.lbs.common;

/**
 * 请求返回结果类型
 * Created by bill on 2018/10/30.
 */
public enum ResultType {
    SUCCESS(20000, "操作成功"),
    FAIL(5000, "用户名或密码错误"),
    UNAUTHORIZED(401, "权限不足"),
    NOT_FOUND(404, "接口不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    AGAIN_LOGIN(600, "请重新登录");

    private int code;
    private String name;

    ResultType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
