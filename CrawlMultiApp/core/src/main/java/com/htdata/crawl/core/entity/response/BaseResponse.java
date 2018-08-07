package com.htdata.crawl.core.entity.response;

import lombok.Data;

@Data
public class BaseResponse {

    /**
     * 结果代码，200成功
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 业务数据
     */
    private Object data;

    public BaseResponse() {
        this.code = 404;
    }
}
