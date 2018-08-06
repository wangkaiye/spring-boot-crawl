package com.htdata.crawl.core.service.impl;

import com.htdata.crawl.core.entity.request.CrawlParamEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;
import com.htdata.crawl.core.service.CommandExecuteService;
import org.springframework.stereotype.Service;

@Service
public class RunTimeCommandExecuteServiceImpl implements CommandExecuteService {
    @Override
    public BaseResponse processParamAndExecute(CrawlParamEntity crawlParamEntity) {
        return new BaseResponse();
    }
}
