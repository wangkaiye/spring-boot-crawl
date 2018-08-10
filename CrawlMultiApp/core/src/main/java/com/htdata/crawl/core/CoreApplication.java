package com.htdata.crawl.core;

import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.dao.ParamInfoDao;
import com.htdata.crawl.core.task.CrawlTaskService;
import com.htdata.crawl.core.task.TableProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class CoreApplication {

    // 默认线程数为5，深度为-1（即不限制）
    public static final int CRAWL_THREAD_NUMBER = 5;
    //爬虫初始化时需要将数据库表名加入，后续的数据库清理任务从其中拿到表名
    public static final Map<String, String> tableNameMap = new HashMap<>();
    public static ConfigurableApplicationContext configurableApplicationContext;

    public static void main(String[] args) {
        log.info("------------------------------project start with param args[0]={}, args[1]={}------------------------------", args[0], args[1]);
        //设置用哪个实现
        System.setProperty(CommonConfig.CRAWL_SERVICE_KEY, args[0]);
        //设置爬去哪个批次
        System.setProperty(CommonConfig.CRAWL_BATCH_ID_KEY, args[1]);
        configurableApplicationContext = SpringApplication.run(CoreApplication.class, args);
        configurableApplicationContext.getBean(CrawlTaskService.class).crawl();
//        log.info("------------------------------crawl job finished, start table info filter job------------------------------");
//        configurableApplicationContext.getBean(TableProcessService.class).tableInfoFilterProcess();

//        log.info("---------------------单独处理数据库---------project start with param args[0]={}, args[1]={}------------------------------", args[0], args[1]);
//        //设置用哪个实现
//        System.setProperty(CommonConfig.CRAWL_SERVICE_KEY, args[0]);
//        //设置爬去哪个批次
//        System.setProperty(CommonConfig.CRAWL_BATCH_ID_KEY, args[1]);
//        log.info("------------------------------crawl job finished, start table info filter job------------------------------");
//        configurableApplicationContext = SpringApplication.run(CoreApplication.class, args);
//        configurableApplicationContext.getBean(ParamInfoDao.class).init(System.getProperty(CommonConfig.CRAWL_BATCH_ID_KEY));
//        String tableName = configurableApplicationContext.getBean(ParamInfoDao.class).getDetailInfoTableName();
//        tableNameMap.put("detail",tableName);
//        configurableApplicationContext.getBean(TableProcessService.class).tableInfoFilterProcess();
    }
}
