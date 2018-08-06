package com.htdata.crawl.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.htdata.crawl.core.dao.CategoryInfoDao;
import com.htdata.crawl.core.dao.TimeFormatDao;
import com.htdata.crawl.core.service.FixedInfoQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 获取长时间不会更改的数据库信息存放在内存中,在固定时间之后，如果有新的请求就再去数据库请求一次数据
 */
@Service
public class FixedInfoQueryServiceImpl implements FixedInfoQueryService {
    private JSONObject timeFormatResult = new JSONObject();
    private JSONObject categoryInfoResult = new JSONObject();
    private long queryMiliTime = System.currentTimeMillis();
    /**
     * 24小时有新的请求时才会去数据库更新
     */
    private long miliTimeSeperate = 24 * 3600 * 1000;
    @Autowired
    private TimeFormatDao timeFormatDao;
    @Autowired
    private CategoryInfoDao categoryInfoDao;

    /**
     * 获取时间格式
     *
     * @return
     */
    public JSONObject getAllTimeRealtedInfo() {
        boolean ifQueryFromMysql = false;
        if (timeFormatResult.isEmpty()) {
            ifQueryFromMysql = true;
        } else {
            if (System.currentTimeMillis() - queryMiliTime >= miliTimeSeperate) {
                ifQueryFromMysql = true;
            }
        }
        if (ifQueryFromMysql) {
            List<Map<String, Object>> list = timeFormatDao.getTimeFormat();
            timeFormatResult.clear();
            for (Map<String, Object> map : list) {
                String id = map.get("id").toString();
                String time_regix = map.get("time_regix").toString();
                String time_format = map.get("time_format").toString();
                String time_example = map.get("time_example").toString();
                JSONObject json = new JSONObject();
                json.put("time_regix", time_regix);
                json.put("time_format", time_format);
                json.put("time_example", time_example);
                timeFormatResult.put(id, json);
            }
        }
        return timeFormatResult;
    }

    /**
     * 获取消息分类
     *
     * @return
     */
    public JSONObject getCategoryInfo() {
        boolean ifQueryFromMysql = false;
        if (categoryInfoResult.isEmpty()) {
            ifQueryFromMysql = true;
        } else {
            if (System.currentTimeMillis() - queryMiliTime >= miliTimeSeperate) {
                ifQueryFromMysql = true;
            }
        }
        if (ifQueryFromMysql) {
            categoryInfoResult.clear();
            List<Map<String, Object>> list = categoryInfoDao.getCategoryInfo();
            for (Map<String, Object> map : list) {
                String id = map.get("category_id").toString();
                String category = map.get("category").toString();
                categoryInfoResult.put(id, category);
            }
        }
        return categoryInfoResult;
    }
}
