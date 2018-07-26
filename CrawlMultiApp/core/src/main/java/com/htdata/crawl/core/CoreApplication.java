package com.htdata.crawl.core;

import com.htdata.crawl.core.constant.CommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@SpringBootApplication
public class CoreApplication {

    // 默认线程数为5，深度为-1（即不限制）
    public static final int CRAWL_THREAD_NUMBER = 5;
    public static Map<String, String> config = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(CoreApplication.class, args);

        /**
         * 处理参数
         */
        // args的参数检验在controller层中进行，此处需要什么参数就在controller层中添加对应规则
        // 或者提供文本样例，按照样例所示进行内容修改
		/**
		 * 当前需要的参数：<br>
		 * 1.网站url（此url为过滤依据，过滤掉不是以此url开头的资源）--weburl<br>
		 * 2.种子url，可配置多个，用空格分开--seedurl<br>
		 * 3.爬取标题的网页关键词--titleKeywords<br>
		 * 4.爬取时间的网页关键词--timeKeywords<br>
		 * 5.爬取时间的正则表达式--timeRegix<br>
		 * 6.爬取内容的网页关键词--contentKeywords<br>
		 * 7.爬取内容存储的位置--crawlStore<br>
		 * 8.用于描述网站的概述--websiteInfo<br>
		 * 9.登陆用户--user
		 */

		for (int i = 0; i < args.length; i++) {
			int splitCodeAt = args[i].indexOf(CommonConfig.SPLIT_CHAR);
			String key = args[i].substring(0, splitCodeAt);
			String value = args[i].substring(splitCodeAt + CommonConfig.SPLIT_CHAR.length());
			if (key.equals(CommonConfig.SEED_URL)) {
				config.put(key, value);
			} else {
				config.put(key, StringUtils.replace(value, CommonConfig.SPACE_CHAR_REPLACE, " "));
			}
		}
		log.info("网站url:" + config.get(CommonConfig.WEB_URL));
		log.info("titleKeywords:" + config.get(CommonConfig.TITLE_KEY_WORDS));
		log.info("timeKeywords:" + config.get(CommonConfig.TIME_KEY_WORDS));
		log.info("timeRegix:" + config.get(CommonConfig.TIME_REGIX));
		log.info("contentKeywords:" + config.get(CommonConfig.CONTENT_KEY_WORDS));
		log.info("爬取内容存储位置:" + config.get(CommonConfig.CRAWL_STORE));
		log.info("网站描述:" + config.get(CommonConfig.WEBSITE_INFO));
		log.info("消息类型:" + config.get(CommonConfig.CATEGORY_KEY_WORDS));
		log.info("消息类型ID:" + config.get(CommonConfig.CATEGORY_ID_KEY_WORDS));
		log.info("时间格式:" + config.get(CommonConfig.TIME_FORMAT_KEY_WORDS));
		String allSeedUrls = config.get(CommonConfig.SEED_URL);
		String[] seeds = allSeedUrls.split(CommonConfig.SPACE_CHAR_REPLACE);
		for (int i = 0; i < seeds.length; i++) {
			log.info("种子url地址" + i + ":" + seeds[i]);
		}
        /**
         * crawl4j.download
         */
        // CrawlConfig crawlConfig = new CrawlConfig(); // 定义爬虫配置
        // crawlConfig.setCrawlStorageFolder(config.get(CommonConfig.CRAWL_STORE));
        // // 设置爬虫文件存储位置
        // crawlConfig.setUserAgentString(
        // "Mozilla/5.0 (Windows NT 6.3; Win64; x64)
        // AppleWebKit/537.36(KHTML,like Gecko) Chrome/61.0.3163.91
        // Safari/537.36");
        // PageFetcher pageFetcher = new PageFetcher(crawlConfig); // 实例化页面获取器
        // RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        // //
        // 实例化爬虫机器人对目标服务器的配置，每个网站都有一个robots.txt文件,规定了该网站哪些页面可以爬，哪些页面禁止爬，该类是对robots.txt规范的实现
        // RobotstxtServer robotstxtServer = new
        // RobotstxtServer(robotstxtConfig, pageFetcher);
        // // 实例化爬虫控制器
        // CrawlController controller = new CrawlController(crawlConfig,
        // pageFetcher, robotstxtServer);
        // // 配置爬虫种子页面，就是规定的从哪里开始爬，可以配置多个种子页面
        // for (String string : seeds) {
        // controller.addSeed(string);
        // }
        // /**
        // * 启动爬虫，爬虫从此刻开始执行爬虫任务，根据以上配置
        // */
        // controller.start(CrawlServiceImpl.class, CRAWL_THREAD_NUMBER);
//		/**
//		 * crawl4j.selfMade
//		 */
//		 log.info("参数配置完成");
//		 HtmlParser.testUrl();
//		/**
//		 * crawl4j.login
//		 */
//		CrawlConfig crawlConfig = new CrawlConfig(); // 定义爬虫配置
//		crawlConfig.setCrawlStorageFolder("/htcrawl/crawlcore/crawl/test_datatest");
//		// 设置爬虫文件存储位置
//		crawlConfig.setUserAgentString(
//				"Mozilla/5.0 (Windows NT 6.3; Win64; x64)AppleWebKit/537.36(KHTML,like Gecko) Chrome/61.0.3163.91Safari/537.36");
//		AuthInfo authJavaForum = new FormAuthInfo("8dc83b474c", "pw4d9e", "https://www.marklines.com/en/members/login", "profiles_login_login_id", "profiles_login_password");
//		crawlConfig.addAuthInfo(authJavaForum);
//		PageFetcher pageFetcher = new PageFetcher(crawlConfig); // 实例化页面获取器
//		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
//		// 实例化爬虫机器人对目标服务器的配置，每个网站都有一个robots.txt文件,规定了该网站哪些页面可以爬，哪些页面禁止爬，该类是对robots.txt规范的实现
//		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
//		// 实例化爬虫控制器
//		CrawlController controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
//		// 配置爬虫种子页面，就是规定的从哪里开始爬，可以配置多个种子页面
//		controller.addSeed("https://www.marklines.com/en/market_report/2601/");
//		/**
//		 * 启动爬虫，爬虫从此刻开始执行爬虫任务，根据以上配置
//		 */
//		controller.start(CrawlServiceImpl.class, CRAWL_THREAD_NUMBER);
        /**
         * crawl4j.dbProcess
         */
    }
}
