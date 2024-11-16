package com.dz.coop.common.entity;

/**
 * @author panqz 2018-10-27 1:13 PM
 */

public class ClientResponse<T> {

    public static final int SUCCESS_CODE = 0;

    private Integer code;
    private String msg;
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }
}
