package com.htdata.crawl.core.dao;

import com.htdata.crawl.core.CoreApplication;
import com.htdata.crawl.core.constant.CommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 运行jar包时传入crawl_id，返回必要的参数
 */
@Slf4j
@Component
public class ParamInfoDao {

    private Pattern timeRegexPattern;
    private String categoryName;
    private String timeRegex;
    private String timeFormat;
    private String areaName;
    private String webUrl;
    private String titleTag;
    private String timeTag;
    private String contentTag;
    private int categoryId;
    private int timeId;
    private String areaId;
    private List<String> seedUrlList=new ArrayList<>();
    private String crawlStorePrefix;
    private String siteDescription;
    private String detailInfoTablePrefix;
    private String filteredInfoTablePrefix;
    private String detailInfoTableName;
    private String filteredInfoTableName;

    public void init(String crawlId){
        JdbcTemplate jdbcTemplate = CoreApplication.configurableApplicationContext.getBean(JdbcTemplate.class);
        int crawlIdInt = 0;
        try {
            //对应数据库中的batch_id，它表示一个批次，但是同一个网站可能有多个批次（比如所用的爬取数据标签不同）
            crawlIdInt = Integer.parseInt(crawlId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from param_info where batch_id = " + crawlIdInt);
        if (mapList == null || mapList.isEmpty()) {
            throw new RuntimeException("param_info must not be empty!");
        }
        int i = 0;
        for (Map<String, Object> map : mapList) {
            if (i == 0) {
                titleTag = map.get("title_tag").toString();
                timeTag = map.get("time_tag").toString();
                contentTag = map.get("content_tag").toString();
                categoryId = (Integer) map.get("category_id");
                timeId = (Integer) map.get("time_id");
                areaId = map.get("area_id").toString();
                seedUrlList.add(map.get("seed_url").toString());
                crawlStorePrefix = map.get("crawl_store_prefix").toString();
                siteDescription = map.get("site_description").toString();
                detailInfoTablePrefix = map.get("detail_info_table_prefix").toString();
                filteredInfoTablePrefix = map.get("filtered_info_table_prefix").toString();
                webUrl = map.get("web_url").toString();
                i++;
            }
            seedUrlList.add(map.get("seed_url").toString());
        }
        List<Map<String, Object>> timeMapList = jdbcTemplate.queryForList("select * from time_info where id = " + timeId);
        if (timeMapList == null || timeMapList.isEmpty()) {
            throw new RuntimeException("time_format must not be empty!");
        }
        Map<String,Object> timeMap = timeMapList.get(0);
        timeFormat = timeMap.get("time_format").toString();
        timeRegex = timeMap.get("time_regex").toString();
        timeRegexPattern = Pattern.compile(timeRegex);
        List<Map<String, Object>> categoryMapList = jdbcTemplate.queryForList("select * from site_category_info where category_id = "+categoryId);
        if (categoryMapList == null || categoryMapList.isEmpty()) {
            throw new RuntimeException("site_category_info must not be empty!");
        }
        Map<String,Object> categoryMap = categoryMapList.get(0);
        categoryName = categoryMap.get("category_name").toString();
        List<Map<String, Object>> areaMapList = jdbcTemplate.queryForList("select * from area_info where area_id = "+areaId);
        if (areaMapList == null || areaMapList.isEmpty()) {
            throw new RuntimeException("area_info must not be empty!");
        }
        Map<String,Object> areaMap = areaMapList.get(0);
        areaName = areaMap.get("area_name").toString();
        detailInfoTableName = detailInfoTablePrefix+"_"+new SimpleDateFormat("yyyy").format(new Date());
        filteredInfoTableName = filteredInfoTablePrefix+"_"+new SimpleDateFormat("yyyy").format(new Date());
    }

    public String getFilteredInfoTableName() {
        return filteredInfoTableName;
    }

    public String getTableSQLbyTableName(String tableName){
        String createTableSQL = "CREATE TABLE `"+tableName+"` (\n" +
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

    public String getDetailInfoTableName() {
        return detailInfoTableName;
    }

    public Pattern getTimeRegexPattern() {
        return timeRegexPattern;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getTimeRegex() {
        return timeRegex;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getTitleTag() {
        return titleTag;
    }

    public String getTimeTag() {
        return timeTag;
    }

    public String getContentTag() {
        return contentTag;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getTimeId() {
        return timeId;
    }

    public String getAreaId() {
        return areaId;
    }

    public List<String> getSeedUrlList() {
        return seedUrlList;
    }

    public String getCrawlStorePrefix() {
        return crawlStorePrefix;
    }

    public String getSiteDescription() {
        return siteDescription;
    }

    public String getDetailInfoTablePrefix() {
        return detailInfoTablePrefix;
    }

    public String getFilteredInfoTablePrefix() {
        return filteredInfoTablePrefix;
    }
}
