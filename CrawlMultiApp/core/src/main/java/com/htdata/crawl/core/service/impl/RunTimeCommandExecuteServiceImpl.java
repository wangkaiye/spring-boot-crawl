package com.htdata.crawl.core.service.impl;

import com.htdata.crawl.core.entity.request.CrawlParamEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;
import com.htdata.crawl.core.service.CommandExecuteService;
import org.springframework.stereotype.Service;

/**
 * 该模块只支持后台专人爬取，暂时不对外开放
 */
@Service
public class RunTimeCommandExecuteServiceImpl implements CommandExecuteService {
    @Override
    public BaseResponse processParamAndExecute(CrawlParamEntity crawlParamEntity) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(200);
        baseResponse.setMsg("请联系管理员沟通爬取需求，制定爬取计划并分配对应的数据存储资源！");
        return baseResponse;
    }

}
