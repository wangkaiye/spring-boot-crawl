package com.htdata.crawl.core.service.impl;

import com.htdata.crawl.core.CoreApplicationTests;
import com.htdata.crawl.core.service.FixedInfoQueryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class FixedInfoQueryServiceImplTest extends CoreApplicationTests {
    @Autowired
    private FixedInfoQueryService fixedInfoQueryService;

    @Test
    public void getAllTimeRealtedInfo() {
        System.out.println(fixedInfoQueryService.getAllTimeRealtedInfo());
    }

    @Test
    public void getCategoryInfo() {
        System.out.println(fixedInfoQueryService.getCategoryInfo());
    }
}