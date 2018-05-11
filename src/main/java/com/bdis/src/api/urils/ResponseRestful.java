package com.bdis.src.api.urils;

/**
 * 服务器返回数据模型
 */
public class ResponseRestful {
    /**
     * http 状态码
     */
    private int code;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 返回的数据
     */
    private Object result;

    public ResponseRestful(int code, String msg, Object result){
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
