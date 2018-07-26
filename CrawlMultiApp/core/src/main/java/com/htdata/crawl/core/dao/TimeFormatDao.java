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

    public List<Map<String, Object>> getTimeFormat(){
        return jdbcTemplate.queryForList("select * from abstract_crawl_date_format");
    }

}
