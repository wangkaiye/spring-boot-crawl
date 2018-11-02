package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.CoreApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TimeInfoDaoTest extends CoreApplicationTests {
@Autowired
    private TimeInfoDao timeInfoDao;

    @Test
    public void getTimeFormat() {
        System.out.println(timeInfoDao.getTimeFormat());
    }
}