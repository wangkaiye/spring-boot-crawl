package com.htdata.crawl.core.task.impl;

import com.htdata.crawl.core.CoreApplication;
import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.task.TableProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class TableProcessServiceImpl implements TableProcessService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Set<String> removeDuplicateSet = new HashSet<>();
    @Override
    public boolean tableInfoFilterProcess() {
        String tableName = CoreApplication.tableNameMap.get("detail");
        log.info("---------------表{}开始进行过滤---------------",tableName);
        List<Map<String,Object>> filterMessageList = jdbcTemplate.queryForList("select filt_message from filter_info where batch_id="+System.getProperty(CommonConfig.CRAWL_BATCH_ID_KEY));
        String filterMessage = filterMessageList.get(0).get("filt_message").toString();
        log.info("过滤内容为:{}",filterMessage);
        List<Map<String,Object>> list = new ArrayList<>();
        boolean begin = false;
        while(!begin||!list.isEmpty()){
            begin = true;
            list = jdbcTemplate.queryForList("select * from "+tableName+" where is_filtered=0 limit 20");
            if(list==null){
                log.info("表{}查询结果为null",tableName);
                return true;
            }
            if(list.isEmpty()){
                log.info("表{}查询结果为空",tableName);
                break;
            }
            List<Long> deleteList = new ArrayList<>();
            List<Long> updateList = new ArrayList<>();
            for (Map<String,Object> map: list) {
                String title = map.get("crawled_title").toString();
                long id = (Long) map.get("id");
                if(isTitlePass(title,filterMessage)){
                    updateList.add(id);
                }else{
                    deleteList.add(id);
                }
            }
            if(!updateList.isEmpty()){
                String updateSQL = "update "+tableName+" set is_filtered=1 where id in"+getSqlPostfix(updateList);
                jdbcTemplate.update(updateSQL);
            }
            if(!deleteList.isEmpty()){
                log.info("删除未通过过滤的内容{}条",deleteList.size());
                String sql = "delete from "+tableName+" where id in"+getSqlPostfix(deleteList);
                jdbcTemplate.update(sql);
            }
        }
        return true;
    }

    private String getSqlPostfix(List<Long> list){
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (long i:list) {
            sb.append(i+",");
        }
        String sqlPostfix = sb.delete(sb.length()-1,sb.length()).append(");").toString();
        return sqlPostfix;
    }

    /**
     * 根据传入的标题和过滤结构判断是否保留该条消息
     * @param title 四川省科学技术厅关于2018年第二批省级科技计划项目预算评审安排的通知
     * @param filter_message 科学|通知|2018
     * @return
     */
    private boolean isTitlePass(String title,String filter_message){
        String[] filterMessageArray = filter_message.split("\\|");
        List<String> list = new ArrayList<>();
        for (String str: filterMessageArray) {
            list.add(str);
        }
        for (String filter:list) {
            if(title.contains(filter)){
                return true;
            }
        }
        return false;
    }
}
