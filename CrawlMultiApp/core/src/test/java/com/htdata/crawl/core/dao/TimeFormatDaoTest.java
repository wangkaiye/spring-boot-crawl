package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.CoreApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class TimeFormatDaoTest extends CoreApplicationTests {
@Autowired
    private TimeFormatDao timeFormatDao;

    @Test
    public void getTimeFormat() {
        System.out.println(timeFormatDao.getTimeFormat());
    }
}