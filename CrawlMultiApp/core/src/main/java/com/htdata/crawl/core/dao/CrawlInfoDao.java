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
        try {
            jdbcTemplate.update(sql, crawlInfoEntity.getBatch_id(),
                    crawlInfoEntity.getUrl(), crawlInfoEntity.getCategory_id(), crawlInfoEntity.getCategory(), crawlInfoEntity.getCrawled_title(), crawlInfoEntity.getCrawled_date(),
                    crawlInfoEntity.getCrawled_content_html(), crawlInfoEntity.getArea(), crawlInfoEntity.getIs_filtered(), crawlInfoEntity.getGmt_create(), crawlInfoEntity.getGmt_modified());
        } catch (Exception e) {
            log.error("CrawlInfoDao.insert crawledData exception:{}!", e.getMessage());
        }
        return true;
    }

    public void createTable(String tableName, String createTableSQL) throws SQLException {
        boolean tableExisted = tableExisted(tableName);
        if (tableExisted) {
            log.info("表{}已存在", tableName);
            return;
        }
        jdbcTemplate.update(createTableSQL);
        log.info("-----create table {}-----{}{}", tableName, System.getProperty("line.separator"), createTableSQL);
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

    public String getTableSQLbyTableName(String tableName) {
        String createTableSQL = "CREATE TABLE `" + tableName + "` (\n" +
                "  `id` int(8) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `batch_id` tinyint(3) unsigned NOT NULL,\n" +
                "  `url` varchar(200) NOT NULL,\n" +
                "  `category_id` tinyint(3) unsigned NOT NULL COMMENT 'elasticsearch需要字段',\n" +
                "  `category` varchar(10) NOT NULL COMMENT 'elasticseach需求字段，映射为category_name',\n" +
                "  `crawled_title` varchar(200) NOT NULL COMMENT '爬取的新闻标题（纯文本）',\n" +
                "  `crawled_date` char(10) NOT NULL COMMENT '爬取的新闻时间（纯文本格式：yyyy-MM-dd）',\n" +
                "  `crawled_content` char(1) NOT NULL DEFAULT '无' COMMENT 'elasticsearch需求字段，无内容',\n" +
                "  `crawled_content_html` text NOT NULL COMMENT '爬取的新闻内容（带html标签）',\n" +
                "  `area` char(6) NOT NULL COMMENT 'elasticsearch需求字段，此处映射为area_id',\n" +
                "  `is_filtered` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0表示未过滤，1表示已过滤',\n" +
                "  `gmt_create` datetime NOT NULL,\n" +
                "  `gmt_modified` datetime NOT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        return createTableSQL;
    }

}
