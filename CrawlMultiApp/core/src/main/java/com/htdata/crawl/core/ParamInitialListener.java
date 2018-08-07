package com.htdata.crawl.core;

import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.dao.CrawlParamInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
@Slf4j
@WebListener
public class ParamInitialListener implements ServletContextListener {
    @Autowired
    private CrawlParamInfoDao crawlParamInfoDao;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        log.info("crawl job finished!");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
//        crawlParamInfoDao.init();
        log.info("=====================");
    }

}
