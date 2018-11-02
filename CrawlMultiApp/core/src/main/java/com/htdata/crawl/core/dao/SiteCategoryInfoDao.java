package com.htdata.crawl.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class SiteCategoryInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private long miliseconds;
    private List<Map<String, Object>> categoryInfoList;

    public List<Map<String, Object>> getCategoryInfo() {
        if (miliseconds == 0L || System.currentTimeMillis() - miliseconds > 3600000L) {
            miliseconds = System.currentTimeMillis();
            categoryInfoList = jdbcTemplate.queryForList("select * from site_category_info");
        }
        return categoryInfoList;
    }
}
