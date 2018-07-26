package com.htdata.crawl.core.service;

import com.htdata.crawl.core.component.UrlStoreServiceImpl;
import com.htdata.crawl.core.entity.TestContentEntity;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
@Slf4j
@Service
public class Crawl4jOriginalServiceImpl  extends WebCrawler {

    @Autowired
    private UrlStoreServiceImpl urlStoreServiceImpl;

    /**
     * 正则匹配指定的后缀文件
     */
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz|pdf|doc))$");

    /**
     * 这个方法主要是决定哪些url我们需要抓取，返回true表示是我们需要的，返回false表示不是我们需要的Url
     * 第一个参数referringPage封装了当前爬取的页面信息 第二个参数url封装了当前爬取的页面url信息
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase(); // 得到小写的url
        return !FILTERS.matcher(href).matches() // 正则匹配，过滤掉我们不需要的后缀文件
//				&& href.startsWith(Main.config.get(CommonConfig.WEB_URL));
                && href.startsWith("https://www.marklines.com");// url必须是http://www.java1234.com/开头，规定站点
    }

    /**
     * 当我们爬到我们需要的页面，这个方法会被调用，我们可以尽情的处理这个页面 page参数封装了所有页面信息
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL(); // 获取url
        boolean exist = urlStoreServiceImpl.urlExists(url);
        if (!exist) {
            urlStoreServiceImpl.storeUrlToSet(url);
            logger.info("url:"+url);
        } else {
            logger.info("url已存在：" + url);
            return;
        }
        if(!url.startsWith("https://www.marklines.com/cn")){
            return;
        }
        if (page.getParseData() instanceof HtmlParseData) { // 判断是否是html数据
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData(); // 强制类型转换，获取html数据对象
            String html = htmlParseData.getHtml(); // 获取页面Html
//            TestContentEntity tce = new TestContentEntity();
//            tce.setCrawled_content_html(html);
//            tce.setKey_message("数据分析组车桥数据");
//            tce.setUrl(url);
//            new CrawlContentDaoImpl().insertTest(tce);
//			JsoupParseModel jpm = new JsoupParseModel();
//			String time = jpm.getNewsInfo(html, "time", false);
//			String timeHtml = jpm.getNewsInfo(html, "time", true);
//			String title = jpm.getNewsInfo(html, "title", false);
//			String titleHtml = jpm.getNewsInfo(html, "title", true);
//			String content = jpm.getNewsInfo(html, "content", false);
//			String contentHtml = jpm.getNewsInfo(html, "content", true);
//			String keyMessage = "keyMessage";
//			if (content != null && title != null && !content.equals("") && !title.equals("")) {
//				logger.info("url:" + url);
//				try {
//					CrawlContentEntity ce = new CrawlContentEntity();
//					ce.setCrawled_title(title.trim());
//					ce.setCrawled_title_html(titleHtml.trim());
//					ce.setCrawled_date(time.trim());
//					ce.setCrawled_date_html(timeHtml.trim());
//					ce.setCrawled_content(content.trim());
//					ce.setCrawled_content_html(
//							ChangeHtml.transHtmlHref(contentHtml, url, Main.config.get(CommonConfig.WEB_URL)).trim());
//					ce.setKey_message(keyMessage);
//					ce.setCategory(Main.config.get(CommonConfig.CATEGORY_KEY_WORDS));
//					ce.setCategory_id(Main.config.get(CommonConfig.CATEGORY_ID_KEY_WORDS));
//					ce.setCrawl_store(Main.config.get(CommonConfig.CRAWL_STORE));
//					ce.setUrl(url);
//					ce.setJob_date(new Date());
//					boolean insertResult = new CrawlContentDaoImpl().insert(ce);
//					if (!insertResult) {
//						logger.info("插入数据库失败：" + ce.toString());
//					}
//				} catch (Exception e) {
//					logger.info("插入数据库失败", e);
//				}
//			}
        }
    }
}
