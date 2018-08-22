package com.htdata.crawl.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.entity.ParseContentRes;
import com.htdata.crawl.core.entity.request.PageParseEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;
import com.htdata.crawl.core.manager.HttpUtil;
import com.htdata.crawl.core.manager.JsoupParseManager;
import com.htdata.crawl.core.service.PageContentParseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class JsoupPaserServiceImpl implements PageContentParseService {
    @Autowired
    private JsoupParseManager jsoupParseManager;
    @Autowired
    private HttpUtil httpUtil;

    private static final Pattern PATTERN = Pattern.compile("https?://[\\w./]+");

    @Override
    public BaseResponse getParseInfo(PageParseEntity pageParseEntity, ContentTypeEnum contentType) {
        String webUrl = pageParseEntity.getWeburl();
        String htmlTag = pageParseEntity.getKeywords();
        if (!PATTERN.matcher(webUrl).matches()) {
            ParseContentRes parseContentRes = new ParseContentRes();
            parseContentRes.setOriginal("url不符合规范，无法解析");
            parseContentRes.setParsed("url不符合规范，无法解析");
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setCode(200);
            baseResponse.setData(parseContentRes);
            return baseResponse;
        }
        String originalPageText = httpUtil.httpGet(webUrl);
        String msg = jsoupParseManager.getContentInfo(originalPageText, htmlTag, contentType);
        ParseContentRes parseContentRes = new ParseContentRes();
        if (StringUtils.isBlank(originalPageText)) {
            parseContentRes.setOriginal("获取不到该URL所指向页面的具体信息！");
        } else {
            parseContentRes.setOriginal(originalPageText);
        }
        if (StringUtils.isBlank(msg)) {
            parseContentRes.setParsed("未解析出有效结果，请检查标签是否规范！");
        } else {
            parseContentRes.setParsed(msg);
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(200);
        baseResponse.setData(parseContentRes);
        return baseResponse;
    }
}
