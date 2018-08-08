package com.htdata.crawl.core.task.impl;

import com.htdata.crawl.core.CoreApplication;
import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.dao.CrawlInfoDao;
import com.htdata.crawl.core.dao.ParamInfoDao;
import com.htdata.crawl.core.manager.FrameCrawlerManager;
import com.htdata.crawl.core.manager.UrlContainerManager;
import com.htdata.crawl.core.task.CrawlTaskService;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.sql.SQLException;

@Slf4j
@ConditionalOnProperty(name = CommonConfig.CRAWL_SERVICE_KEY, havingValue = CommonConfig.CRAWL_SERVICE_WITH_FRAMEWORK)
@Service
public class FrameForCrawlServiceImpl implements CrawlTaskService {
    @Autowired
    private ParamInfoDao paramInfoDao;
    @Autowired
    private CrawlInfoDao crawlInfoDao;
    @Autowired
    private UrlContainerManager urlContainerManager;

    private int CRAWL_THREAD_NUMBER = CoreApplication.CRAWL_THREAD_NUMBER;

    @Override
    public void crawl() {
        paramInfoDao.init(System.getProperty(CommonConfig.CRAWL_BATCH_ID_KEY));
        CrawlConfig crawlConfig = new CrawlConfig(); // 定义爬虫配置
        crawlConfig.setCrawlStorageFolder(paramInfoDao.getCrawlStorePrefix()+ paramInfoDao.getSiteDescription());
        // 设置爬虫文件存储位置
        crawlConfig.setUserAgentString(
                "Mozilla/5.0 (Windows NT 6.3; Win64; x64)AppleWebKit / 537.36 (KHTML, like Gecko)Chrome / 61.0 .3163 .91Safari / 537.36 ");
        PageFetcher pageFetcher = new PageFetcher(crawlConfig); // 实例化页面获取器
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        //实例化爬虫机器人对目标服务器的配置，每个网站都有一个robots.txt文件, 规定了该网站哪些页面可以爬，哪些页面禁止爬，该类是对robots.txt规范的实现
        RobotstxtServer robotstxtServer = new
                RobotstxtServer(robotstxtConfig, pageFetcher);
        // 实例化爬虫控制器
        CrawlController controller = null;
        try {
            controller = new CrawlController(crawlConfig,pageFetcher, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 配置爬虫种子页面，就是规定的从哪里开始爬，可以配置多个种子页面
        for (String string : paramInfoDao.getSeedUrlList()) {
            controller.addSeed(string);
        }
        String tableName = paramInfoDao.getDetailInfoTableName();
        String createTableSQL = "CREATE TABLE `"+tableName+"` (\n" +
                "  `id` int(8) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `batch_id` tinyint(3) unsigned NOT NULL,\n" +
                "  `url` varchar(200) NOT NULL,\n" +
                "  `category_id` tinyint(3) unsigned NOT NULL COMMENT 'elasticsearch需要字段',\n" +
                "  `category` varchar(10) NOT NULL COMMENT 'elasticseach需求字段，映射为category_name',\n" +
                "  `crawled_title` varchar(200) NOT NULL COMMENT '爬取的新闻标题（纯文本）',\n" +
                "  `crawled_date` char(10) NOT NULL COMMENT '爬取的新闻时间（纯文本格式：yyyy-MM-dd）',\n" +
                "  `crawled_content` char(1) NOT NULL DEFAULT '无' COMMENT 'elasticsearch需求字段，无内容',\n" +
                "  `crawled_content_html` text NOT NULL COMMENT '爬取的新闻内容（带html标签）',\n" +
                "  `area` char(6) NOT NULL COMMENT 'elasticsearch需求字段，此处映射为area_id',\n" +
                "  `is_filtered` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0表示未过滤，1表示已过滤',\n" +
                "  `gmt_create` datetime NOT NULL,\n" +
                "  `gmt_modified` datetime NOT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try {
            //如果表不存在，则会创建
            crawlInfoDao.createTable(tableName,createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        urlContainerManager.initContainerHashSet("url",tableName);
        //加入表名，后续进行内容过滤时需要用到
        CoreApplication.tableNameList.add(tableName);
        /**
         * 启动爬虫，爬虫从此刻开始执行爬虫任务，根据以上配置
         */
        controller.start(FrameCrawlerManager.class, CRAWL_THREAD_NUMBER);
    }
}
