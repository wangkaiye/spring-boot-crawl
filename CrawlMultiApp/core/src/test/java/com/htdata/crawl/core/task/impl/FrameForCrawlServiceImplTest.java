package com.htdata.crawl.core.task.impl;

import com.htdata.crawl.core.CoreApplicationTests;
import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.dao.CrawlParamInfoDao;
import com.htdata.crawl.core.task.CrawlTaskService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class FrameForCrawlServiceImplTest extends CoreApplicationTests {
    @Autowired
    private CrawlParamInfoDao crawlParamInfoDao;
    @Autowired
    private CrawlTaskService crawlTaskService;

    @Test
    public void crawl() {
        System.setProperty(CommonConfig.CRAWL_BATCH_ID_KEY, "1");
        crawlParamInfoDao.init();
        System.setProperty("frameCrawl", "true");
        crawlTaskService.crawl();
    }
}