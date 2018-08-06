package com.htdata.crawl.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.htdata.crawl.core.constant.CommonConfig.CRAWL_ID_KEY;

/**
 * 运行jar包时传入crawl_id，
 */
@Repository
public class CrawlParamInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String crawlId = System.getProperty(CRAWL_ID_KEY);

    private Pattern timeRegexPattern;
    private long miliseconds;
    //TODO
    private int id;
    private String titleTag;
    private String timeTag;
    private String contentTag;
    private String seedUrl;
    private String crawlStorePrefix;
    private String siteDescription;
    private String detailInfoTablePrefix;
    private String filteredInfoTablePrefix;
    private int categoryId;
    private int timeId;
    private String areaId;

    static {

    }


    public Pattern getTimeRegexPatternByTimeId(String timeId) {
        if (miliseconds == 0L || System.currentTimeMillis() - miliseconds > 3600000L) {
            miliseconds = System.currentTimeMillis();
            List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from time_info where id = " + timeId);
            System.out.println(mapList);
            String timeRegex = mapList.get(0).get("time_regex").toString();
            timeRegexPattern = Pattern.compile(timeRegex);
        }
        return timeRegexPattern;
    }

}
