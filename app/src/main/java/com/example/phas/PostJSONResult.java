package com.example.phas;

public class PostJSONResult {
    private int httpStatus;
    private String ResultStr;

    public PostJSONResult(int httpStatus, String resultStr) {
        this.httpStatus = httpStatus;
        ResultStr = resultStr;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getResultStr() {
        return ResultStr;
    }
}
