package com.htdata.crawl.core;

import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.dao.CrawlInfoDao;
import com.htdata.crawl.core.dao.ParamInfoDao;
import com.htdata.crawl.core.task.CrawlTaskService;
import com.htdata.crawl.core.task.TableProcessService;
import com.htdata.crawl.core.task.impl.TableProcessServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.tool.Extension;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.SQLException;
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
        if (args.length == 0) {
            log.error("未正确设置参数！（如果是爬虫+数据整理需要输入批次ID；如果只是数据整理需要输入\"mysql\"和批次ID）。");
            return;
        }
        if (args.length == 1) {
            log.info("------------------------------project start with param args[0]={}------------------------------", args[0]);
            log.info("------------------------------首先爬取，之后再进行数据处理和存储------------------------------");
            configurableApplicationContext = SpringApplication.run(CoreApplication.class, args);
            //用shell脚本控制爬取param_info中哪个批次的任务
            //设置爬取批次id（param_info.batch_id）,该参数很多地方都会被用到
            System.setProperty(CommonConfig.CRAWL_BATCH_ID_KEY, args[0]);
            ParamInfoDao paramInfoDao = configurableApplicationContext.getBean(ParamInfoDao.class);
            paramInfoDao.init(args[0]);
            String crawlTaskServiceImplBeanName = paramInfoDao.getCrawlStrategy();
            ((CrawlTaskService) configurableApplicationContext.getBean(crawlTaskServiceImplBeanName)).crawl();
            log.info("------------------------------crawl job finished, start table info filter job------------------------------");
            initalTable(paramInfoDao);
            configurableApplicationContext.getBean(TableProcessService.class).tableInfoFilterProcess(args[0]);
        }
        if (args.length == 2 && args[0].equals("mysql")) {
            //此处需要批次id，将param_info中的重要参数置入
            configurableApplicationContext = SpringApplication.run(CoreApplication.class, args);
            log.info("------------------------------单独处理数据库内容 args[0]={},args[1]={}------------------------------", args[0], args[1]);
            ParamInfoDao paramInfoDao = configurableApplicationContext.getBean(ParamInfoDao.class);
            TableProcessService tableProcessService = configurableApplicationContext.getBean(TableProcessService.class);
            //设置爬取批次id（param_info.batch_id）,该参数很多地方都会被用到
            String[] batchIdArray = args[1].split(",");
            for (int i = 0; i < batchIdArray.length; i++) {
                paramInfoDao.init(batchIdArray[i]);
                initalTable(paramInfoDao);
                tableProcessService.tableInfoFilterProcess(batchIdArray[i]);
            }
        }
        if (args.length == 2 && args[0].equals("converge")) {
            configurableApplicationContext = SpringApplication.run(CoreApplication.class, args);
            //生成今日汇聚表（规则：auto_industry_news_yyyyMMdd）
            String convergeTableName = "auto_industry_news_" + new DateTime().toString("yyyyMMdd");
            CrawlInfoDao crawlInfoDao = configurableApplicationContext.getBean(CrawlInfoDao.class);
            String createTableSQL = crawlInfoDao.getTableSQLbyTableName(convergeTableName);
            try {
                crawlInfoDao.createTable(convergeTableName, createTableSQL);
            } catch (SQLException e) {
                log.error("创建表时出现错误：{}", e.getMessage());
                return;
            }
            //找到对应批次的id（传递参数时用","进行分割）所指向的filter表
            ParamInfoDao paramInfoDao = configurableApplicationContext.getBean(ParamInfoDao.class);
            List<String> filterTableNameList = new ArrayList<>();
            String[] batchIdArray = args[1].split(",");
            for (int i = 0; i < batchIdArray.length; i++) {
                paramInfoDao.init(batchIdArray[i]);
                filterTableNameList.add(paramInfoDao.getFilteredInfoTableName());
            }
            //将各个filter表中的内容汇聚到今日表中--注意排除‘标题--时间’一样的内容
            TableProcessServiceImpl tableProcessService = configurableApplicationContext.getBean(TableProcessServiceImpl.class);
            for (int i = 0; i < filterTableNameList.size(); i++) {
                String tableName = filterTableNameList.get(i);
                tableProcessService.filteredTableInfoTransferedIntoConverge(tableName, convergeTableName);
            }
        }
    }

    /**
     * 此处的参数需要被初始化过
     *
     * @param paramInfoDao
     */
    private static void initalTable(ParamInfoDao paramInfoDao) {
        //如果没有对应的表，应该先将表建好
        CrawlInfoDao crawlInfoDao = configurableApplicationContext.getBean(CrawlInfoDao.class);
        String tableName = paramInfoDao.getDetailInfoTableName();
        String filterTableName = paramInfoDao.getFilteredInfoTableName();
        String createTableSQL = crawlInfoDao.getTableSQLbyTableName(tableName);
        String filterTableSQL = crawlInfoDao.getTableSQLbyTableName(filterTableName);
        try {
            //如果表不存在，则会创建
            crawlInfoDao.createTable(tableName, createTableSQL);
            crawlInfoDao.createTable(filterTableName, filterTableSQL);
        } catch (SQLException e) {
            log.error("创建表时出现错误：{}", e.getMessage());
            return;
        }
        //加入表名，后续进行内容过滤转移时需要用到
        tableNameMap.put("detail", tableName);
        tableNameMap.put("filter", filterTableName);
    }

}
