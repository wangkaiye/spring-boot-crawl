package com.htdata.crawl.web.controller;

import com.htdata.crawl.core.service.impl.FixedInfoQueryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 固定信息的查询,刷新接口
 */
@Slf4j
@RestController
@RequestMapping("/fixed")
public class FixedInfoController {
    @Autowired
    private FixedInfoQueryServiceImpl fixedInfoService;

    @RequestMapping(value = "/info/category", method = RequestMethod.GET)
    public Object getCategoryInfo() {
        return fixedInfoService.getCategoryInfo();
    }

    @RequestMapping(value = "/info/time", method = RequestMethod.GET)
    public Object getTimeFormatInfo() {
        return fixedInfoService.getAllTimeRealtedInfo();
    }

    //to do
    @RequestMapping(value = "/flush/time", method = RequestMethod.GET)
    public Object flushTimeFormatInfo() {
        return null;
    }

    //to do
    @RequestMapping(value = "/flush/category", method = RequestMethod.GET)
    public Object flushCategoryInfo() {
        return null;
    }
}
