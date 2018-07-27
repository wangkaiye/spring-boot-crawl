package com.htdata.crawl.core.entity.response;

import com.alibaba.fastjson.JSONObject;
import com.htdata.crawl.core.constant.ResponseInfo;

public class BaseResponse {

    public JSONObject getFailedJson() {
        JSONObject json = new JSONObject();
        json.put(ResponseInfo.RESULT_KEY, ResponseInfo.PARAM_ERROR);
        return json;
    }

}
