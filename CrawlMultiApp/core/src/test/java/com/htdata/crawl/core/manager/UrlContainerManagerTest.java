package com.htdata.crawl.core.manager;

import com.htdata.crawl.core.CoreApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UrlContainerManagerTest extends CoreApplicationTests {
@Autowired
    private UrlContainerManager urlContainerManager;

    @Test
    public void urlExists() {
    }

    @Test
    public void storeUrlToSet() {
    }

    @Test
    public void initContainerHashSet() {
        urlContainerManager.initContainerHashSet("url","detail_sichuansheng_kexuejishu_ting2018");
        System.out.println(urlContainerManager.getHashSet().size());
        urlContainerManager.initContainerHashSet("url","detail_sichuansheng_kexuejishu_ting2018");
        System.out.println(urlContainerManager.getHashSet().size());
    }

    @Test
    public void getHashSet() {
    }
}