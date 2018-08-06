package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.CoreApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class CrawlParamInfoDaoTest extends CoreApplicationTests {
    @Autowired
    private CrawlParamInfoDao crawlParamInfoDao;

    @Test
    public void getTimeRegexPatternByTimeId() {
        System.out.println(crawlParamInfoDao.getTimeRegexPatternByTimeId("1"));
    }
}