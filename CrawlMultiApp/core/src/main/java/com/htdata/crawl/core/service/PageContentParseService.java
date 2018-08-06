package com.htdata.crawl.core.service;

import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.entity.request.PageParseEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;

public interface PageContentParseService {

    BaseResponse getParseInfo(PageParseEntity pageParseEntity, ContentTypeEnum contentType);

}
