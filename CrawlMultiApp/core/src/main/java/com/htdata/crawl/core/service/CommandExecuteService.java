package com.htdata.crawl.core.service;

import com.htdata.crawl.core.entity.request.CrawlParamEntity;

public interface CommandExecuteService {
    /**
     * 分解参数，并调用执行命令的方法
     * @param crawlParamEntity
     * @return
     */
    public int processParamAndExecute(CrawlParamEntity crawlParamEntity);
}
