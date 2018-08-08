package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.entity.CrawlInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Repository
public class CrawlInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 存储
     *
     * @param crawlInfoEntity
     * @param tableName
     * @return
     */
    public boolean insert(CrawlInfoEntity crawlInfoEntity, String tableName) {
        String sql = "insert into " + tableName + "(batch_id,url,category_id,category,crawled_title,crawled_date,crawled_content_html,area,is_filtered,gmt_create,gmt_modified) " +
                "values(?,?,?,?,?,?,?,?,?,?,?)";
        int insertResult = jdbcTemplate.update(sql, crawlInfoEntity.getBatch_id(),
                crawlInfoEntity.getUrl(),crawlInfoEntity.getCategory_id(),crawlInfoEntity.getCategory(),crawlInfoEntity.getCrawled_title(),crawlInfoEntity.getCrawled_date(),
                crawlInfoEntity.getCrawled_content_html(),crawlInfoEntity.getArea(),crawlInfoEntity.getIs_filtered(),crawlInfoEntity.getGmt_create(),crawlInfoEntity.getGmt_modified());
        return insertResult == 1;
    }

    public void createTable(String tableName,String createTableSQL) throws SQLException {
        boolean tableExisted = tableExisted(tableName);
        if (tableExisted) {
            log.info("表{}已存在",tableName);
            return;
        }
        jdbcTemplate.update(createTableSQL);
        log.info("-----create table {}-----{}{}", tableName,System.getProperty("line.separator"),createTableSQL);
    }

    /**
     * 如果该表存在则返回true,不存在则返回false
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public boolean tableExisted(String tableName) throws SQLException {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        ResultSet tabs = null;
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            String[] types = {"TABLE"};
            tabs = dbMetaData.getTables(null, null, tableName, types);
            if (tabs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tabs.close();
            conn.close();
        }
        return false;
    }

}
