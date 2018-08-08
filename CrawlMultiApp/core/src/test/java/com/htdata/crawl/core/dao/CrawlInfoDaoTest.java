package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.CoreApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class CrawlInfoDaoTest extends CoreApplicationTests {
@Autowired
    private CrawlInfoDao crawlInfoDao;

    @Test
    public void insert() {
    }
    @Test
    public void tableExisted(){
        try {
            System.out.println(crawlInfoDao.tableExisted("param_info_wo123"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}