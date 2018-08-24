package com.htdata.crawl.web.controller;

import com.htdata.crawl.core.entity.response.BaseResponse;
import com.htdata.crawl.core.service.impl.FixedInfoQueryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ucar.units.Base;

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

    @RequestMapping(value = "/flush/all", method = RequestMethod.GET)
    public Object flushTimeFormatInfo() {
        fixedInfoService.flushAllInfoForce();
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(200);
        baseResponse.setMsg("已执行刷新操作，请查看日志是否有刷新记录！");
        return baseResponse;
    }

}
