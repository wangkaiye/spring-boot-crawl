package com.htdata.crawl.core.entity;

import lombok.Data;

import java.util.Date;
@Data
public class CrawlContentEntity {
	private String crawled_title;
	private String crawled_content;
	private String crawled_date;
	private String url;
	private Date job_date;
	private String category;
	private String category_id;
	private String crawled_date_html;
	private String crawled_content_html;
	private String crawled_title_html;
	private String key_message;
	private String crawl_store;
}
