package com.htdata.crawl.core.service.impl;

import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.entity.request.PageParseEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;
import com.htdata.crawl.core.service.PageContentParseService;
import org.springframework.stereotype.Service;

@Service
public class JsoupPaserServiceImpl implements PageContentParseService {
    @Override
    public BaseResponse getParseInfo(PageParseEntity pageParseEntity, ContentTypeEnum contentType) {
        return null;
    }
}
