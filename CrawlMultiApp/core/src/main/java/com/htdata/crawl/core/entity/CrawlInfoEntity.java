package com.htdata.crawl.core.entity;

import lombok.Data;

import java.util.Date;
@Data
public class CrawlInfoEntity {
    private int batch_id;
    private String url;
    private int category_id;
    private String category;
    private String crawled_title;
    private String crawled_date;
    private String crawled_content;
    private String crawled_content_html;
    private String area;
    private int is_filtered;
    private Date gmt_create;
    private Date gmt_modified;
}
