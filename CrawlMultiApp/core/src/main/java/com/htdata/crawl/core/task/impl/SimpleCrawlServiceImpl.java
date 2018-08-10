package com.htdata.crawl.core.task.impl;

import com.htdata.crawl.core.CoreApplication;
import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.dao.CrawlInfoDao;
import com.htdata.crawl.core.dao.ParamInfoDao;
import com.htdata.crawl.core.entity.CrawlInfoEntity;
import com.htdata.crawl.core.manager.HttpUtil;
import com.htdata.crawl.core.manager.JsoupParseManager;
import com.htdata.crawl.core.manager.UrlContainerManager;
import com.htdata.crawl.core.task.CrawlTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手写爬虫内部管理，在研究透彻crawl4j之前用于补充crawl4j用于爬取可能的设置错误而不能爬取的网站
 */
@Slf4j
@ConditionalOnProperty(name = CommonConfig.CRAWL_SERVICE_KEY, havingValue = CommonConfig.CRAWL_SERVICE_WITH_SIMPLECRAWL)
@Service
public class SimpleCrawlServiceImpl implements CrawlTaskService {
    @Autowired
    private UrlContainerManager urlContainerManager;
    @Autowired
    private JsoupParseManager jsoupParseManager;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private CrawlInfoDao crawlInfoDao;
    @Autowired
    private ParamInfoDao paramInfoDao;

    private Set<String> titleAndTimeSet = new HashSet<>();
    private final Pattern patternLow = Pattern.compile(".*\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz|pdf|doc|xls|docx|xlsx|rar|tif)$");
    //对于某些特殊的URL，提取出来会有分行符等，如果不进行转换会影响程序正常执行
    private final Pattern patternSpecialUrl = Pattern.compile("\\s*|\t|\r|\n");
    //匹配所有汉字
    private final Pattern patternCharacter = Pattern.compile("[\u4e00-\u9fa5]");
    private long count = 0L;

    public void crawl() {
        //参数初始化
        paramInfoDao.init(System.getProperty(CommonConfig.CRAWL_BATCH_ID_KEY));
        FastDateFormat actualFastDateFormat = FastDateFormat.getInstance(paramInfoDao.getTimeFormat());
        String detailedInfoTableName = paramInfoDao.getDetailInfoTableName();
        String baseUrl = paramInfoDao.getWebUrl();
        String tableName = paramInfoDao.getDetailInfoTableName();
        String createTableSQL = paramInfoDao.getTableSQLbyTableName(tableName);
        String filterTableName = paramInfoDao.getFilteredInfoTableName();
        String filterTableSQL = paramInfoDao.getTableSQLbyTableName(filterTableName);
        try {
            //如果表不存在，则会创建
            crawlInfoDao.createTable(tableName, createTableSQL);
//            crawlInfoDao.createTable(filterTableName, filterTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        //正式爬取之前，首先将历史纪录url放入已经爬取的列表中
        urlContainerManager.initContainerHashSet("url", tableName);
        //加入表名，后续进行内容过滤时需要用到
        CoreApplication.tableNameMap.put("detail", tableName);
//        CoreApplication.tableNameMap.put("filter", filterTableName);

        //抓的时候填，填完了新的一轮取出来用，用了就删掉
        List<String> preparedList = getUrlList(paramInfoDao.getSeedUrlList(), baseUrl);
        preparedList.addAll(paramInfoDao.getSeedUrlList());
        while (preparedList.size() > 0) {
            log.info("urlContainerManager.getHashSet().size()===>" + urlContainerManager.getHashSet().size());
            int round = 1;
            for (String seedUrl : preparedList) {
                // 将seedUrl地址放入hashSet中，表示它已经被处理过
                if (!urlContainerManager.urlExists(seedUrl)) {
                    urlContainerManager.storeUrlToSet(seedUrl);
                } else {
                    //如果存在，即被处理过，跳过这次处理
                    log.info("url({})已存在", seedUrl);
                    continue;
                }
                // 处理种子url中的信息
                count++;
                try {
                    String html = httpUtil.httpGet(seedUrl);
                    String title = jsoupParseManager.getTitleInfo(html, paramInfoDao.getTitleTag(), ContentTypeEnum.TEXT);
                    String time = jsoupParseManager.getTimeInfo(html, paramInfoDao.getTimeTag(), ContentTypeEnum.TEXT,
                            paramInfoDao.getTimeRegexPattern(), paramInfoDao.getTimeFormat(), actualFastDateFormat);
                    String contentHtml = jsoupParseManager.getContentInfo(html, paramInfoDao.getContentTag(), ContentTypeEnum.HTML);
                    if (StringUtils.isBlank(title) || StringUtils.isBlank(time) || StringUtils.isBlank(contentHtml)) {
                        log.info("爬取内容/标题/时间为null ===={}", seedUrl);
                        continue;
                    }
                    if (titleAndTimeSet.contains(title + "_" + time)) {
                        log.info("标题和时间==（{}）_（{}）重复", title, time);
                        continue;
                    }
                    titleAndTimeSet.add(title + "_" + time);
                    CrawlInfoEntity crawlInfoEntity = new CrawlInfoEntity();
                    crawlInfoEntity.setUrl(seedUrl);
                    crawlInfoEntity.setBatch_id(Integer.parseInt(System.getProperty(CommonConfig.CRAWL_BATCH_ID_KEY)));
                    crawlInfoEntity.setGmt_create(new Date());
                    crawlInfoEntity.setGmt_modified(new Date());
                    crawlInfoEntity.setCrawled_date(time);
                    crawlInfoEntity.setCrawled_title(title);
                    crawlInfoEntity.setCrawled_content_html(contentHtml);
                    crawlInfoEntity.setArea(paramInfoDao.getAreaId());
                    crawlInfoEntity.setIs_filtered(0);
                    crawlInfoEntity.setCategory_id(paramInfoDao.getCategoryId());
                    crawlInfoEntity.setCategory(paramInfoDao.getCategoryName());
                    crawlInfoDao.insert(crawlInfoEntity, detailedInfoTableName);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("插入数据库失败", e);
                    continue;
                }
            }
            //根据上一轮的所得到的所有url，进行下一轮的url获取
            preparedList.addAll(getUrlList(preparedList, baseUrl));
            //使用getUrlList(List<String> seedUrlList, String baseUrl)后，会将上一轮遍历过的地址存入其中
            removeDuplicatedUrl(preparedList);
            log.info("=====第{}轮结束，此时种子preparedList中还有{}个url=====", round, preparedList.size());
            round++;
        }
        log.info("共处理了{}个网页", count);
    }

    private void removeDuplicatedUrl(List<String> list) {
        if (list.isEmpty()) {
            return;
        }
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            if (this.urlContainerManager.urlExists(url)) {
                iterator.remove();
            }
        }
    }

    /**
     * 获取页面所有的合理的绝对url，并进行预处理
     *
     * @param seedUrlList
     * @param baseUrl
     * @return
     */
    private List<String> getUrlList(List<String> seedUrlList, String baseUrl) {
        if (seedUrlList == null || seedUrlList.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        log.info("开始进行种子url抓取:{}", System.currentTimeMillis());
        final CountDownLatch countDownLatch = new CountDownLatch(seedUrlList.size());
        ExecutorService esThreadPool = Executors.newFixedThreadPool(seedUrlList.size() < 50 ? seedUrlList.size() : 50);
        for (String seedUrl : seedUrlList) {
            esThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    //抓取页面
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(seedUrl).get();
                        //获取所有为a下的链接
                        Elements link = doc.select("a");
                        for (Element element : link) {
                            //获取页面的url
                            String absHref = element.attr("abs:href");
                            //判定url合理性
                            if (urlValid(absHref, baseUrl)) {
                                synchronized (this) {
                                    list.add(getProcessedUrl(absHref));
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("种子url（{}）在抓取时发生异常：{}", seedUrl, e.getMessage());
                    } finally {
                        System.out.println("countDownLatch.countDown()");
                        countDownLatch.countDown();
                    }
                }
            });
        }
        try {
            countDownLatch.await(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("结束本轮种子url抓取:{}", System.currentTimeMillis());
        return list;
    }

    /**
     * 判断准备存入或者爬取的url是否合理,合理则装入准备爬取的容器
     *
     * @param originalUrl
     * @param baseUrl
     * @return
     */
    private boolean urlValid(String originalUrl, String baseUrl) {
        if (originalUrl == null) {
            return false;
        }
        //如果url不是以本网站url开头的pass
        if (!originalUrl.startsWith(baseUrl)) {
            return false;
        }
        //有些特殊的url，会包含分割符，会通过之前的判断，但是很少见，所以单独处理之后再进行一次判断
        String processedUrl = patternSpecialUrl.matcher(originalUrl).replaceAll("").toLowerCase();
        //如果是各种文件(如doc,png,gif结尾等，无需进行后续爬取)，就pass掉该url
        if (patternLow.matcher(processedUrl).matches()) {
            return false;
        }
        return true;
    }

    /**
     * 处理装入容器的url
     *
     * @param originalUrl
     * @return
     */
    private String getProcessedUrl(String originalUrl) {
        String processedUrl = null;
        if (originalUrl == null) {
            return processedUrl;
        } else {
            processedUrl = originalUrl;
        }
        //处理汉字
        Matcher matcher = patternCharacter.matcher(processedUrl);
        while (matcher.find()) {
            processedUrl = processedUrl.replace(matcher.group(), URLEncoder.encode(matcher.group()));
        }
        return processedUrl;
    }

}
