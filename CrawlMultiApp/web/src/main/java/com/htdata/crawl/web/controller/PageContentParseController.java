package com.htdata.crawl.web.controller;

import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.entity.request.PageParseEntity;
import com.htdata.crawl.core.service.PageContentParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/page")
public class PageContentParseController {

    @Autowired
    private PageContentParseService pageContentParseService;

    /**
     * @param pageParseEntity
     * @return
     */
    @RequestMapping(value = "/text", method = RequestMethod.POST)
    public Object getTextInfo(@RequestBody PageParseEntity pageParseEntity) {
        return pageContentParseService.getParseInfo(pageParseEntity, ContentTypeEnum.TEXT);
    }

    /**
     * @param pageParseEntity
     * @return
     */
    @RequestMapping(value = "/html", method = RequestMethod.POST)
    public Object getHtmlInfo(@RequestBody PageParseEntity pageParseEntity) {
        return pageContentParseService.getParseInfo(pageParseEntity, ContentTypeEnum.HTML);
    }
}
