package com.htdata.crawl.core.entity.response;

import com.alibaba.fastjson.JSONObject;
import com.htdata.crawl.core.constant.ResponseInfo;
import lombok.Data;

@Data
public class BaseResponse {

    private String msg;
    private int code;
    public JSONObject getFailedJson() {
        JSONObject json = new JSONObject();
        json.put(ResponseInfo.RESULT_KEY, ResponseInfo.PARAM_ERROR);
        return json;
    }

}
