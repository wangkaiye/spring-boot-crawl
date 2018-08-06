package com.htdata.crawl.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TimeFormatDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private long miliseconds;
    private List<Map<String, Object>> timeInfoList;

    public List<Map<String, Object>> getTimeFormat() {
        if (miliseconds == 0L || System.currentTimeMillis() - miliseconds > 3600000L) {
            miliseconds = System.currentTimeMillis();
            timeInfoList = jdbcTemplate.queryForList("select * from time_info");
        }
        return timeInfoList;
    }

}
