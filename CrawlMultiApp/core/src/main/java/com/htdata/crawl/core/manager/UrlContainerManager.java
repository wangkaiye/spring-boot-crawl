package com.htdata.crawl.core.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * url去重
 */
@Service
public class UrlContainerManager {

    private static final HashSet<String> hashSet = new HashSet<>();
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public boolean urlExists(String url) {
        return hashSet.contains(url.toLowerCase());
    }

    public void storeUrlToSet(String url) {
        synchronized (UrlContainerManager.class){
            hashSet.add(url.toLowerCase());
        }
    }

    public void initContainerHashSet(String column,String tableName){
        //爬取内容之前要到对应的表中查看是否有之前已经爬取过的内容
        synchronized (UrlContainerManager.class){
            List<Map<String,Object>> list = jdbcTemplate.queryForList("select "+column+" from "+tableName);
            if(list!=null&&!list.isEmpty()){
                for (Map<String,Object> map :list) {
                    hashSet.add(map.get(column).toString().toLowerCase());
                }
            }
        }
    }
    public HashSet<String> getHashSet(){
        return hashSet;
    }
}
