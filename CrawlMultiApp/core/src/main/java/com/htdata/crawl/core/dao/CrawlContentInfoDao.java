package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.entity.CrawlContentEntity;
import com.htdata.crawl.core.entity.TestContentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CrawlContentInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean insert(CrawlContentEntity cce) {
        String sql = "insert into auto_crawl(crawled_title,crawled_content,crawled_date,url,job_date,category,category_id, crawled_date_html,crawled_content_html,crawled_title_html,key_message,crawl_store) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        int res = jdbcTemplate.update(sql, cce.getCrawled_title(), cce.getCrawled_content(), cce.getCrawled_date(),
                cce.getUrl(), cce.getJob_date(), cce.getCategory(), cce.getCategory_id(), cce.getCrawled_date_html(),
                cce.getCrawled_content_html(), cce.getCrawled_title_html(), cce.getKey_message(), cce.getCrawl_store());
        return res == 1;
    }

    public boolean insertTest(TestContentEntity tce) {
        String sql = "insert into crawl_2018_01_29(url,crawled_content_html,key_message) values(?,?,?)";
        int res = jdbcTemplate.update(sql, tce.getUrl(), tce.getCrawled_content_html(), tce.getKey_message());
        return res == 1;
    }

}
