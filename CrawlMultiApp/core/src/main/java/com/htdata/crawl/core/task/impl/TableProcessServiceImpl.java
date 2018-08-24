package com.htdata.crawl.core.task.impl;

import com.htdata.crawl.core.CoreApplication;
import com.htdata.crawl.core.task.TableProcessService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
public class TableProcessServiceImpl implements TableProcessService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String timePrefix = new DateTime().toString("yyyy");

    @Transactional
    @Override
    public void tableInfoFilterProcess(String batchId) {
        String tableName = CoreApplication.tableNameMap.get("detail");
        String filterTableName = CoreApplication.tableNameMap.get("filter");
        log.info("---------------表{}开始进行过滤---------------", tableName);
        //获取过滤关键字
        List<Map<String, Object>> filterMessageList = jdbcTemplate.queryForList("select filt_message from filter_info where batch_id=" + batchId);
        String filterMessage = filterMessageList.get(0).get("filt_message").toString();
        log.info("过滤关键字为:{}（规则：爬取标题包含这些关键字的新闻会被留下）", filterMessage);
        List<Map<String, Object>> tempDetailedInfoList = new ArrayList<>();
        boolean begin = false;
        while (!begin || !tempDetailedInfoList.isEmpty()) {
            begin = true;
            tempDetailedInfoList = jdbcTemplate.queryForList("select * from " + tableName + " where is_filtered=0 limit 20");
            if (tempDetailedInfoList == null) {
                log.info("表{}查询结果为null", tableName);
                return;
            }
            if (tempDetailedInfoList.isEmpty()) {
                log.info("表{}查询结果为空", tableName);
                break;
            }
            log.info("detailTableName:{},filteredTableName:{},本批次处理共{}条数据", tableName, filterTableName, tempDetailedInfoList.size());
            List<Map<String, Object>> transferedList = new ArrayList<>();
            List<Long> updateList = new ArrayList<>();
            for (Map<String, Object> map : tempDetailedInfoList) {
                String title = map.get("crawled_title").toString();
                long id = (Long) map.get("id");
                if (isMessagePassed(title, filterMessage)) {
                    //如果通过，则需要存入新的数据表--filter_xxx
                    transferedList.add(map);
                }
                //不论是否通过，这些内容在进行了一次过滤后都将改为is_filtered=1表示已经被过滤过
                updateList.add(id);
            }
            if (!updateList.isEmpty()) {
                updateConfirmedInfo(updateList, tableName);
            }
            if (!transferedList.isEmpty()) {
                InsertTransferedListToFiteredTable(filterTableName, transferedList);
            }
        }
        return;
    }

    private void updateConfirmedInfo(List<Long> ids, String tableName) {
        String updateSQL = "update " + tableName + " set is_filtered=1 where id in" + getSqlPostfix(ids);
        jdbcTemplate.update(updateSQL);
    }

    /**
     * 将需要转移的内容放入新的表中
     *
     * @param tableName
     * @param toBeTransferedList
     */
    private void InsertTransferedListToFiteredTable(String tableName, List<Map<String, Object>> toBeTransferedList) {
        String sql = "insert into " + tableName + "(batch_id,url,category_id,category,crawled_title,crawled_date,crawled_content,crawled_content_html,area,is_filtered,gmt_create,gmt_modified)" +
                "values(?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                Map<String, Object> stringObjectMap = toBeTransferedList.get(i);
                preparedStatement.setInt(1, Integer.parseInt(stringObjectMap.get("batch_id").toString()));
                preparedStatement.setString(2, stringObjectMap.get("url").toString());
                preparedStatement.setInt(3, Integer.parseInt(stringObjectMap.get("category_id").toString()));
                preparedStatement.setString(4, stringObjectMap.get("category").toString());
                preparedStatement.setString(5, stringObjectMap.get("crawled_title").toString());
                preparedStatement.setString(6, stringObjectMap.get("crawled_date").toString());
                preparedStatement.setString(7, stringObjectMap.get("crawled_content").toString());
                preparedStatement.setString(8, stringObjectMap.get("crawled_content_html").toString());
                preparedStatement.setString(9, stringObjectMap.get("area").toString());
                preparedStatement.setInt(10, 0);
                preparedStatement.setString(11, stringObjectMap.get("gmt_create").toString());
                preparedStatement.setString(12, stringObjectMap.get("gmt_modified").toString());
            }

            @Override
            public int getBatchSize() {
                return toBeTransferedList.size();
            }
        });
    }

    /**
     * 根据原始含有id的list组成一个置于SQL语句之后的id集合
     * 如：(1,2,3,4)
     *
     * @param list
     * @return
     */
    private String getSqlPostfix(List<Long> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (long i : list) {
            sb.append(i + ",");
        }
        String sqlPostfix = sb.delete(sb.length() - 1, sb.length()).append(");").toString();
        return sqlPostfix;
    }

    /**
     * 根据传入的标题和过滤结构判断是否保留该条消息
     *
     * @param title          四川省科学技术厅关于2018年第二批省级科技计划项目预算评审安排的通知
     * @param filter_message 科学|通知|2018
     * @return
     */
    private boolean isMessagePassed(String title, String filter_message) {
        String[] filterMessageArray = filter_message.split("\\|");
        List<String> list = new ArrayList<>();
        for (String str : filterMessageArray) {
            list.add(str);
        }
        for (String filter : list) {
            if (title.contains(filter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将filtered表中的内容转入合并表中
     *
     * @param filteredTableName
     * @param convergeTableName
     */
    public void filteredTableInfoTransferedIntoConverge(String filteredTableName, String convergeTableName) {
        //原表中取出1000条数据（is_filtered），转移，更新
        List<Map<String, Object>> filterMessageList = jdbcTemplate.queryForList("select * from " + filteredTableName + " where is_filtered=0 limit 1000");
        if (filterMessageList.isEmpty()) {
            log.info("表({})中已经没有符合转移条件的内容！", filteredTableName);
        }
        //除重机制
        Set<String> titleAndTimeSet = new HashSet<>(2048);
        while (!filterMessageList.isEmpty()) {
            log.info("filteredTableName:{},convergeTableName:{},本批次处理共{}条数据", filteredTableName, convergeTableName, filterMessageList.size());
            List<Long> updateList = new ArrayList<>();
            List<Map<String, Object>> convergeList = new ArrayList<>();
            for (int i = 0; i < filterMessageList.size(); i++) {
                Map<String, Object> stringObjectMap = filterMessageList.get(i);
                long id = (Long) stringObjectMap.get("id");
                updateList.add(id);
                //如果重复，那么不用放入聚合表中（存在url不一样，但消息一样的情况）
                String title = stringObjectMap.get("crawled_title").toString();
                String time = stringObjectMap.get("crawled_date").toString();
                if (titleAndTimeSet.contains(title + time)) {
                    continue;
                }
                titleAndTimeSet.add(title + time);
                if (time.startsWith(timePrefix)) {
                    convergeList.add(stringObjectMap);
                }
            }
            if (!updateList.isEmpty()) {
                //此表中is_filtered字段改为1表示已经转移到了聚合表中；
                updateConfirmedInfo(updateList, filteredTableName);
            }
            //过滤内容转到聚合表中
            InsertTransferedListToFiteredTable(convergeTableName, convergeList);
            //转移完毕后继续获取下一批1000个数据
            filterMessageList = jdbcTemplate.queryForList("select * from " + filteredTableName + " where is_filtered=0 limit 1000");
        }
        log.info("聚合完毕！");
    }
}
