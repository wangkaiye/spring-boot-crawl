package com.htdata.crawl.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.regex.Pattern;

/**
 * 运行jar包时传入crawl_id，
 */
public class CrawlParamInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public final Pattern pattern;

    public final String crawlId;

    CrawlParamInfoDao() {
        crawlId = "";
        pattern = Pattern.compile(getTimeRegixByCrawlId(crawlId));
    }

    private String getTimeRegixByCrawlId(String id) {
        return "";
    }




}
