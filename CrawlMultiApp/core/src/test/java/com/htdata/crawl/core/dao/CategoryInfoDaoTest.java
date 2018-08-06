package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.CoreApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryInfoDaoTest extends CoreApplicationTests {
    @Autowired
    private CategoryInfoDao categoryInfoDao;

    @Test
    public void getCategoryInfo() {
        System.out.println(categoryInfoDao.getCategoryInfo());
    }

}