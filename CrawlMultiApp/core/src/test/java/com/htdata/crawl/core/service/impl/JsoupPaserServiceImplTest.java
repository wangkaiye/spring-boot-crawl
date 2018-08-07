package com.htdata.crawl.core.service.impl;

import com.htdata.crawl.core.CoreApplicationTests;
import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.entity.request.PageParseEntity;
import com.htdata.crawl.core.entity.response.BaseResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class JsoupPaserServiceImplTest extends CoreApplicationTests {
@Autowired
    private JsoupPaserServiceImpl jsoupPaserService;
    @Test
    public void getParseInfo() {
        PageParseEntity pageParseEntity = new PageParseEntity();
        pageParseEntity.setKeywords("div.newsCon");
        pageParseEntity.setWeburl("http://www.scst.gov.cn/zhuzhan/tz/20180620/30793.html");
        BaseResponse baseResponse = jsoupPaserService.getParseInfo(pageParseEntity,ContentTypeEnum.HTML);
        System.out.println(baseResponse.getMsg());
    }
}