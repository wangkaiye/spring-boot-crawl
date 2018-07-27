package com.htdata.crawl.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.constant.ResponseInfo;
import com.htdata.crawl.core.entity.request.CrawlParamEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;
import com.htdata.crawl.core.service.CommandExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/execute")
public class CommandExecuteController {
    @Autowired
    private CommandExecuteService commandExecuteService;

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public Object getCrawlInfo(@RequestBody CrawlParamEntity crawlParamEntity)
            throws IOException {
        /**
         * 当前需要的参数：<br>
         * 1.网站url（此url为过滤依据，过滤掉不是以此url开头的资源）--weburl<br>
         * 2.种子url，目前可配置一个，后期调整为多个--seedurl<br>
         * 3.爬取标题的网页关键词--titleKeywords<br>
         * 4.爬取时间的网页关键词--timeKeywords<br>
         * 5.爬取时间的正则表达式--timeRegix<br>
         * 6.爬取内容的网页关键词--contentKeywords<br>
         * 7.爬取内容存储的位置--crawlStore<br>
         * 8.用于描述网站的概述--websiteInfo<br>
         * 9.登陆用户--user<br>
         * 10.描述任务的ID--jobId<br>
         * 11.爬取时间的格式--timeFormat
         */
        String timeRegix = "";
        String timeFormat = "";
        String category = "";
        JSONObject json = new JSONObject();
        if (StringUtils.isEmpty(weburl.trim())) {
            return new BaseResponse().getFailedJson();
        } else {
            weburl = CommonConfig.WEB_URL + CommonConfig.SPLIT_CHAR
                    + weburl.replace(" ", CommonConfig.SPACE_CHAR_REPLACE);
        }
        if (StringUtils.isEmpty(seedurl.trim())) {
            return new BaseResponse().getFailedJson();
        } else {
            seedurl = CommonConfig.SEED_URL + CommonConfig.SPLIT_CHAR
                    + seedurl.replace(" ", CommonConfig.SPACE_CHAR_REPLACE);
        }
        if (StringUtils.isEmpty(titleKeyWords.trim())) {
            return new BaseResponse().getFailedJson();
        } else {
            titleKeyWords = CommonConfig.TITLE_KEY_WORDS + CommonConfig.SPLIT_CHAR
                    + titleKeyWords.replace(" ", CommonConfig.SPACE_CHAR_REPLACE);
        }
        if (StringUtils.isEmpty(timeKeyWords.trim())) {
            return new BaseResponse().getFailedJson();
        } else {
            timeKeyWords = CommonConfig.TIME_KEY_WORDS + CommonConfig.SPLIT_CHAR
                    + timeKeyWords.replace(" ", CommonConfig.SPACE_CHAR_REPLACE);
        }
        if (StringUtils.isEmpty(contentKeyWords.trim())) {
            return new BaseResponse().getFailedJson();
        } else {
            contentKeyWords = CommonConfig.CONTENT_KEY_WORDS + CommonConfig.SPLIT_CHAR
                    + contentKeyWords.replace(" ", CommonConfig.SPACE_CHAR_REPLACE);
        }
        if (StringUtils.isEmpty(timeId.trim())) {
            return new BaseResponse().getFailedJson();
        } else {
            JSONObject timeJson = fixedInfoService.getAllTimeRealtedInfo();
            String timeFormatInJson = timeJson.getJSONObject(timeId).getString("time_format");
            String timeRegixInJson = timeJson.getJSONObject(timeId).getString("time_regix");
            timeRegix = CommonConfig.TIME_REGIX + CommonConfig.SPLIT_CHAR
                    + timeRegixInJson.replace(" ", CommonConfig.SPACE_CHAR_REPLACE);
            timeFormat = CommonConfig.TIME_FORMAT_KEY_WORDS + CommonConfig.SPLIT_CHAR + timeFormatInJson;
        }
        if (StringUtils.isEmpty(categoryId.trim())) {
            json.put(ResponseInfo.RESULT_KEY, ResponseInfo.PARAM_ERROR);
            return json;
        } else {
            category = CommonConfig.CATEGORY_KEY_WORDS + CommonConfig.SPLIT_CHAR
                    + fixedInfoService.getCategoryInfo().getString(categoryId);
            categoryId = CommonConfig.CATEGORY_ID_KEY_WORDS + CommonConfig.SPLIT_CHAR
                    + categoryId.replace(" ", CommonConfig.SPACE_CHAR_REPLACE);
        }
        String user = "test";
        String jobId = user + "_" + Long.toString(System.currentTimeMillis());
        /**
         * 这个存储位置包含了用户与时间，最终放入数据库中
         */
        String crawlStore = "/htcrawl/crawlcore/crawl/" + jobId;
        /**
         * 还需要将category与category_id上传
         */
        String cmd = "nohup java -jar /htcrawl/crawlcore/CrawlCore-0.0.1-SNAPSHOT.jar \'" + weburl + "\' \'" + seedurl
                + "\' \'" + titleKeyWords + "\' \'" + categoryId + "\' \'" + timeFormat + "\' \'" + timeKeyWords
                + "\' \'" + contentKeyWords + "\' \'" + category + "\' \'crawlStoresplit_unique_char" + crawlStore
                + "\' \'" + timeRegix + "\' \'websiteInfosplit_unique_char" + websiteInfo
                + "\' > /htcrawl/crawlcore/crawljoblog/nohup" + "_" + Long.toString(System.currentTimeMillis())
                + ".log 2>&1 &";
        // int result = 1;
        int result = commandExecuteService.execute(cmd);
        if (result == 1) {
            json.put(ResponseInfo.RESULT_KEY, ResponseInfo.SUCCESS);
        } else {
            json.put(ResponseInfo.RESULT_KEY, ResponseInfo.FAILED);
        }
        json.put(ResponseInfo.COMMAND_KEY, "[" + cmd + "]");
        return json;
    }

}
