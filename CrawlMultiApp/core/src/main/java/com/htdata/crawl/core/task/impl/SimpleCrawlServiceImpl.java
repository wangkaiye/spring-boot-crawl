package com.htdata.crawl.core.task.impl;

import com.htdata.crawl.core.CoreApplication;
import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.dao.CrawlContentInfoDao;
import com.htdata.crawl.core.entity.CrawlContentEntity;
import com.htdata.crawl.core.manager.HttpUtil;
import com.htdata.crawl.core.manager.JsoupParseManager;
import com.htdata.crawl.core.manager.UrlContainerManager;
import com.htdata.crawl.core.task.CrawlTaskService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
@ConditionalOnProperty(name = "simpleCrawl",havingValue = "true")
@Service
public class SimpleCrawlServiceImpl implements CrawlTaskService {
    @Autowired
    private UrlContainerManager urlContainerManager;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private JsoupParseManager jsoupParseManager;
    @Autowired
    private CrawlContentInfoDao crawlContentInfoDao;

    @SuppressWarnings("deprecation")
    private static String getEncodeUrl(String url) {
        String proUrl = url;
        Pattern pt = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher mc = pt.matcher(proUrl);
        while (mc.find()) {
            proUrl = proUrl.replace(mc.group(), URLEncoder.encode(mc.group()));
        }
        return proUrl;
    }

    /**
     * 获取页面所有的绝对url
     *
     * @param url
     * @param baseUrl
     * @return
     */
    public static List<String> getUrlList(String url, String baseUrl) {
        List<String> list = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            Elements link = doc.select("a");
            for (Element element : link) {
                String absHref = element.attr("abs:href");
                if (absHref.startsWith(baseUrl)) {
                    list.add(getEncodeUrl(absHref));
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    private static long count = 0L;

    /**
     * 用來爬取一些特殊的網站
     */
    public void crawl() {
        /**
         * 抓的时候填，填完了新的一轮取出来用，用了就删掉
         */
        List<String> preparedList = new ArrayList<>();
        /**
         * 临时放入url，待这一批的抓完就将其放入preparedList中
         */
        List<String> tempList = new ArrayList<>();
        String baseUrl = CoreApplication.config.get(CommonConfig.WEB_URL);
        List<String> firstList = getUrlList(CoreApplication.config.get(CommonConfig.SEED_URL), baseUrl);
        for (String firstUrl : firstList) {
            if (!urlPass(firstUrl, baseUrl)) {
                continue;
            }
            preparedList.add(firstUrl);
        }
        for (int i = 1; i < 100; i++) {
            for (String seedUrl : preparedList) {
                // 将种子url地址放入hashSet中，作为后期判别是否阅览过
                if (!urlContainerManager.urlExists(seedUrl)) {
                    urlContainerManager.storeUrlToSet(seedUrl);
                } else {
                    continue;
                }
                // 处理种子url中的信息
                count++;
                log.info("已存：" + seedUrl);
                try {
                    String html = httpUtil.httpGet(seedUrl);
                    String time = jsoupParseManager.getNewsInfo(html, "time", false);
                    String timeHtml = jsoupParseManager.getNewsInfo(html, "time", true);
                    String title = jsoupParseManager.getNewsInfo(html, "title", false);
                    String titleHtml = jsoupParseManager.getNewsInfo(html, "title", true);
                    String content = jsoupParseManager.getNewsInfo(html, "content", false);
                    String contentHtml = jsoupParseManager.getNewsInfo(html, "content", true);
                    String keyMessage = CoreApplication.config.get(CommonConfig.WEBSITE_INFO);
                    if (content != null && title != null && !content.equals("") && !title.equals("")) {
                        CrawlContentEntity ce = new CrawlContentEntity();
                        ce.setCrawled_title(title.trim());
                        ce.setCrawled_title_html(titleHtml.trim().substring(0,
                                titleHtml.trim().length() > 500 ? 500 : titleHtml.trim().length()));
                        ce.setCrawled_date(time.trim());
                        ce.setCrawled_date_html(timeHtml.trim().substring(0,
                                timeHtml.trim().length() > 500 ? 500 : timeHtml.trim().length()));
                        ce.setCrawled_content(content.trim());
                        ce.setCrawled_content_html(jsoupParseManager
                                .makeUrlInHtmlAbsolute(contentHtml, seedUrl, CoreApplication.config.get(CommonConfig.WEB_URL)).trim());
                        ce.setKey_message(keyMessage);
                        ce.setCategory(CoreApplication.config.get(CommonConfig.CATEGORY_KEY_WORDS));
                        ce.setCategory_id(CoreApplication.config.get(CommonConfig.CATEGORY_ID_KEY_WORDS));
                        ce.setCrawl_store(CoreApplication.config.get(CommonConfig.CRAWL_STORE));
                        ce.setUrl(seedUrl);
                        ce.setJob_date(new Date());
                        crawlContentInfoDao.insert(ce);
                    }
                } catch (Exception e) {
                    log.info("插入数据库失败", e);
                    continue;
                }
                // 对于每一个种子url,循环的时候只负责将其页面的url取出，过滤，放入temp中，作为下一轮的种子url
                List<String> roundList = getUrlList(seedUrl, baseUrl);
                for (String string : roundList) {
                    if (!urlPass(string, baseUrl)) {
                        continue;
                    }
                    if (!urlContainerManager.urlExists(string)) {
                        tempList.add(string);
                    }
                }
            }
            // 结束后，清空preparedList
            preparedList.clear();
            // 将temp中的url放入preparedList中
            preparedList.addAll(tempList);
            // 清空temp
            tempList.clear();
            log.info("=========================第" + i + "轮结束，此时种子urlList中还有" + preparedList.size()
                    + "个url========================");
            if (preparedList.size() == 0) {
                System.exit(1);
            }
        }
        log.info("共爬取了" + count + "个网页。");
    }

    private boolean urlPass(String url, String baseUrl) {
        String repl = "";
        if (url != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(url);
            repl = m.replaceAll("");
        }
        String lower = ".*\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz|pdf|doc|xls|docx|xlsx|rar|tif)$";
        String upper = lower.toUpperCase();
        return !Pattern.compile(lower).matcher(repl).matches() // 正则匹配，过滤掉我们不需要的后缀文件
                && !Pattern.compile(upper).matcher(repl).matches() && repl.startsWith(baseUrl);
    }
}
