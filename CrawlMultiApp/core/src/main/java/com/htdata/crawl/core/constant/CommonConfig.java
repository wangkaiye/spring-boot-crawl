package com.htdata.crawl.core.constant;

public interface CommonConfig {
    /**
     * 网站url（此url为过滤依据，过滤掉不是以此url开头的资源）--weburl
     */
    String WEB_URL = "weburl";
    /**
     * 种子url，目前可配置一个，后期调整为多个--seedurl
     */
    String SEED_URL = "seedurl";

    /**
     * &的替代符
     */
    String SPECIAL_CHAR_REPLACE_1 = "htdata_crawl_job_space_char_replace_1";
    /**
     * #的替代符
     */
    String SPECIAL_CHAR_REPLACE_2 = "htdata_crawl_job_space_char_replace_2";
    /**
     * 初始化时property放入的爬取内容批次号，对应于mysql.param_info中的批次id，便于提取对应的参数
     */
    String CRAWL_BATCH_ID_KEY = "crawlId";
    /**
     * 用于CrawlTaskService接口实现的选取，置于@ConditionOnProperty下的key值设置
     * 初始化时设置
     */
    String CRAWL_SERVICE_KEY = "crawlServiceKey";
    /**
     * 框架配置
     */
    String CRAWL_SERVICE_WITH_FRAMEWORK = "frame";
    /**
     * 简单实现（持续改进）
     */
    String CRAWL_SERVICE_WITH_SIMPLECRAWL = "simple";

}
