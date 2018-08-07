package com.htdata.crawl.core.service.impl;

import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.entity.request.PageParseEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;
import com.htdata.crawl.core.manager.HttpUtil;
import com.htdata.crawl.core.manager.JsoupParseManager;
import com.htdata.crawl.core.service.PageContentParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsoupPaserServiceImpl implements PageContentParseService {
    @Autowired
    private JsoupParseManager jsoupParseManager;
    @Autowired
    private HttpUtil httpUtil;


    @Override
    public BaseResponse getParseInfo(PageParseEntity pageParseEntity, ContentTypeEnum contentType) {
        String webUrl = pageParseEntity.getWeburl();
        String htmlTag = pageParseEntity.getKeywords();
        String html = httpUtil.httpGet(webUrl);
        String msg = jsoupParseManager.getContentInfo(html, htmlTag, contentType);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(200);
        baseResponse.setMsg(msg);
        return baseResponse;
    }
}
