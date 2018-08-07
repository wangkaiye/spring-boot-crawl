package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.CoreApplicationTests;
import com.htdata.crawl.core.constant.CommonConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class CrawlParamInfoDaoTest extends CoreApplicationTests {
    @Autowired
    private CrawlParamInfoDao crawlParamInfoDao;

    @Test
    public void init() {
        System.setProperty(CommonConfig.CRAWL_BATCH_ID_KEY,"1");
        crawlParamInfoDao.init();
        System.out.println(crawlParamInfoDao.areaId);
    }
}