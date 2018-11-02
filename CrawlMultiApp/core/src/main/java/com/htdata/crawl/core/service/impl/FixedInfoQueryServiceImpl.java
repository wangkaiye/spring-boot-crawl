package com.htdata.crawl.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.htdata.crawl.core.dao.SiteCategoryInfoDao;
import com.htdata.crawl.core.dao.TimeInfoDao;
import com.htdata.crawl.core.service.FixedInfoQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.james.mime4j.dom.datetime.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 获取长时间不会更改的数据库信息存放在内存中,在固定时间之后，如果有新的请求就再去数据库请求一次数据
 */
@Slf4j
@Service
public class FixedInfoQueryServiceImpl implements FixedInfoQueryService {
    private static final JSONObject TIME_FORMAT_INFO_LIST = new JSONObject();
    private static final JSONObject CATEGORY_INFO_LIST = new JSONObject();
    private long queryMiliTime = 0L;
    /**
     * 24小时有新的请求时才会去数据库更新
     */
    private final static long MILISECOND_TIME_DELAY = 3600_000L;
    @Autowired
    private TimeInfoDao timeInfoDao;
    @Autowired
    private SiteCategoryInfoDao categoryInfoDao;

    /**
     * 获取时间格式
     *
     * @return
     */
    public JSONObject getAllTimeRealtedInfo() {
        if (conditionForFlush(TIME_FORMAT_INFO_LIST)) {
            synchronized (FixedInfoQueryServiceImpl.class) {
                if (conditionForFlush(TIME_FORMAT_INFO_LIST)) {
                    flushAllInfo();
                }
            }
        }
        return TIME_FORMAT_INFO_LIST;
    }

    /**
     * 获取消息分类
     *
     * @return
     */
    public JSONObject getCategoryInfo() {
        if (conditionForFlush(CATEGORY_INFO_LIST)) {
            synchronized (FixedInfoQueryServiceImpl.class) {
                if (conditionForFlush(CATEGORY_INFO_LIST)) {
                    flushAllInfo();
                }
            }
        }
        return CATEGORY_INFO_LIST;
    }

    /**
     * 根据json和当前时间判断是否需要进行刷新
     *
     * @param jsonObject
     * @return
     */
    private boolean conditionForFlush(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return true;
        }
        if (System.currentTimeMillis() - queryMiliTime > MILISECOND_TIME_DELAY) {
            return true;
        }
        return false;
    }

    /**
     * 刷新所有信息
     */
    private void flushAllInfo() {
        CATEGORY_INFO_LIST.clear();
        List<Map<String, Object>> categoryInfoList = categoryInfoDao.getCategoryInfo();
        for (Map<String, Object> map : categoryInfoList) {
            String id = map.get("category_id").toString();
            String category = map.get("category_name").toString();
            CATEGORY_INFO_LIST.put(id, category);
        }
        List<Map<String, Object>> timeFormatList = timeInfoDao.getTimeFormat();
        TIME_FORMAT_INFO_LIST.clear();
        for (Map<String, Object> map : timeFormatList) {
            String id = map.get("id").toString();
            String time_regix = map.get("time_regex").toString();
            String time_format = map.get("time_format").toString();
            JSONObject json = new JSONObject();
            json.put("time_regex", time_regix);
            json.put("time_format", time_format);
            TIME_FORMAT_INFO_LIST.put(id, json);
        }
        queryMiliTime = System.currentTimeMillis();
        log.info("{}时刻刷新所有信息--时间格式{}，以及分类信息{}", new Date(), TIME_FORMAT_INFO_LIST, CATEGORY_INFO_LIST);
    }

    /**
     * 强制刷新所有信息
     */
    public void flushAllInfoForce() {
        synchronized (FixedInfoQueryServiceImpl.class) {
            flushAllInfo();
        }
    }
}
