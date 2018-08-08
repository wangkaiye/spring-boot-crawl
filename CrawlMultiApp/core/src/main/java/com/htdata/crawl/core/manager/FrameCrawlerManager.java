package com.htdata.crawl.core.manager;

import com.htdata.crawl.core.CoreApplication;
import com.htdata.crawl.core.constant.CommonConfig;
import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.dao.CrawlInfoDao;
import com.htdata.crawl.core.dao.ParamInfoDao;
import com.htdata.crawl.core.entity.CrawlInfoEntity;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.regex.Pattern;

public class FrameCrawlerManager extends WebCrawler {

    private static final Pattern filters = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf|doc))$");

    private UrlContainerManager urlContainerManager=
            CoreApplication.configurableApplicationContext.getBean(UrlContainerManager.class);

    private ParamInfoDao paramInfoDao =
            CoreApplication.configurableApplicationContext.getBean(ParamInfoDao.class);

    private JsoupParseManager jsoupParseManager=
            CoreApplication.configurableApplicationContext.getBean(JsoupParseManager.class);
    private FastDateFormat actualFastDateFormat;

    private CrawlInfoDao crawlInfoDao =
            CoreApplication.configurableApplicationContext.getBean(CrawlInfoDao.class);

    private String detailedInfoTableName;

    @Override
    public void onStart() {
        paramInfoDao.init(System.getProperty(CommonConfig.CRAWL_BATCH_ID_KEY));
        actualFastDateFormat = FastDateFormat.getInstance(paramInfoDao.getTimeFormat());
        detailedInfoTableName = paramInfoDao.getDetailInfoTableName();
        logger.info("urlContainerManager.getHashSet().size()===>"+urlContainerManager.getHashSet().size());
    }

    /**
     * 这个方法主要是决定哪些url我们需要抓取，返回true表示是我们需要的，返回false表示不是我们需要的Url
     * 第一个参数referringPage封装了当前爬取的页面信息 第二个参数url封装了当前爬取的页面url信息
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase(); // 得到小写的url
        return !filters.matcher(href).matches() // 正则匹配，过滤掉我们不需要的后缀文件
                && href.startsWith(paramInfoDao.getWebUrl());
    }

    /**
     * 当我们爬到我们需要的页面，这个方法会被调用，我们可以尽情的处理这个页面 page参数封装了所有页面信息
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL(); // 获取url
        if (!url.startsWith(paramInfoDao.getWebUrl())) {
            return;
        }
        boolean exist = urlContainerManager.urlExists(url);
        if (!exist) {
            urlContainerManager.storeUrlToSet(url);
        } else {
            logger.info("排除已存在的url=={}",url);
            return;
        }
        logger.info("通过过滤，准备进行爬取的url:{}",url);
        if (page.getParseData() instanceof HtmlParseData) { // 判断是否是html数据
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData(); // 强制类型转换，获取html数据对象
            String html = htmlParseData.getHtml(); // 获取页面Html
            String title = jsoupParseManager.getTitleInfo(html, paramInfoDao.getTitleTag(),ContentTypeEnum.TEXT);
            String time = jsoupParseManager.getTimeInfo(html, paramInfoDao.getTimeTag(),ContentTypeEnum.TEXT,
                    paramInfoDao.getTimeRegexPattern(), paramInfoDao.getTimeFormat(),actualFastDateFormat);
            String contentHtml = jsoupParseManager.getContentInfo(html,paramInfoDao.getContentTag(),ContentTypeEnum.HTML);
            if(title==null||time==null||contentHtml==null){
                logger.info("爬取内容/标题/时间为null ===={}",url);
                return;
            }
            logger.info(title+ time);
            CrawlInfoEntity crawlInfoEntity = new CrawlInfoEntity();
            crawlInfoEntity.setUrl(url);
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
            crawlInfoDao.insert(crawlInfoEntity,detailedInfoTableName);
        }
    }
}
