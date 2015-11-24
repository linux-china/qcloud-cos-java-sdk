package com.qcloud.cos.common;

import java.util.Map;

/**
 * COS response
 *
 * @author linux_china
 */
public class CosResponse {
    /**
     * 服务端返回码
     */
    private int code;
    /**
     * 服务端提示内容
     */
    private String message;
    /**
     * 服务器返回的应答数据
     */
    private Map<String, Object> data;

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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 0;
    }
}
