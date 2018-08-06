package com.htdata.crawl.core.constant;

public interface CommonConfig {
	/**
	 * 分隔符号
	 */
	String SPLIT_CHAR = "split_unique_char";
	/**
	 * 网站url（此url为过滤依据，过滤掉不是以此url开头的资源）--weburl
	 */
	String WEB_URL = "weburl";
	/**
	 * 种子url，目前可配置一个，后期调整为多个--seedurl
	 */
	String SEED_URL = "seedurl";
	/**
	 * 爬取标题的网页关键词--titleKeywords
	 */
	String TITLE_KEY_WORDS = "titleKeywords";
	/**
	 * 爬取时间的网页关键词--timeKeywords
	 */
	String TIME_KEY_WORDS = "timeKeywords";
	/**
	 * 爬取时间的正则表达式--timeRegix
	 */
	String TIME_REGIX = "timeRegix";
	/**
	 * 爬取内容的网页关键词--contentKeywords
	 */
	String CONTENT_KEY_WORDS = "contentKeywords";
	/**
	 * 爬取内容存储的位置--crawlStore
	 */
	String CRAWL_STORE = "crawlStore";
	/**
	 * 用于描述网站的概述
	 */
	String WEBSITE_INFO = "websiteInfo";
	/**
	 * 爬取的消息类型ID（与消息类型category匹配）
	 */
	String CATEGORY_ID_KEY_WORDS = "categoryId";
	/**
	 * 爬取的消息类型（一般根据网站进行分类）
	 */
	String CATEGORY_KEY_WORDS = "category";
	/**
	 * 单次爬取任务的ID
	 */
	String JOB_ID = "jobId";
	/**
	 * 登陆用户
	 */
	String USER = "user";
	/**
	 * 替换所有空格参数的特殊字符，仅限于在controller与service层之间传递
	 */
	String SPACE_CHAR_REPLACE = "htdata_crawl_job_space_char_repalce";
	/**
	 * &的替代符
	 */
	String SPECIAL_CHAR_REPLACE_1 = "htdata_crawl_job_space_char_replace_1";
	/**
	 * #的替代符
	 */
	String SPECIAL_CHAR_REPLACE_2 = "htdata_crawl_job_space_char_replace_2";
	
	/**
	 * 时间格式
	 */
	String TIME_FORMAT_KEY_WORDS = "time_format";


	String CRAWL_ID_KEY = "crawlId";

}
