package com.fhzz.tool;

import java.io.Serializable;

public class Result implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 正常返回true，出现错误false
    private boolean value = true;
    // 异常返回的错误信息
    private String info = "";
    // 错误码
    private int errorCode;
    // 返回值
    private Object response = null;

    public Result() {
    }

    public Result(boolean value, String info, int errorCode, Object response) {
        super();
        this.value = value;
        this.info = info;
        this.errorCode = errorCode;
        this.response = response;
    }

    public Result(boolean value, String info, Object response) {
        super();
        this.value = value;
        this.info = info;
        this.response = response;
    }

    public Result(boolean value) {
        super();
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
		this.response = response;
	}
}
