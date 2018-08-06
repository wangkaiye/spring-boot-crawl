package com.htdata.crawl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.htdata.crawl.core.entity.request.CrawlParamEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;

public interface CommandExecuteService {
    /**
     *
     * @param crawlParamEntity
     * @return 包含是否成功和信息
     */
    BaseResponse processParamAndExecute(CrawlParamEntity crawlParamEntity);
}
