package com.htdata.crawl.core.manager;

import com.htdata.crawl.core.constant.ContentTypeEnum;
import com.htdata.crawl.core.dao.CrawlParamInfoDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Component
public class JsoupParseManager {

    @Autowired
    private CrawlParamInfoDao crawlParamInfoDao;

    /**
     * @param html：页面原文
     * @param htmlTag：关键标签
     * @param contentTypeEnum：纯文本返回还是带标签的内容返回
     * @return 解析失败返回null
     */
    public String getTimeInfo(String html, String htmlTag, ContentTypeEnum contentTypeEnum) {
        String res = null;
        Document doc = Jsoup.parse(html);
        Elements timeElements = doc.select(htmlTag);
        if (timeElements == null) {
            return null;
        }
        //带标签的内容此处返回
        if (contentTypeEnum == ContentTypeEnum.HTML) {
            return timeElements.html().trim();
        }
        String text = timeElements.text();
        if (text == null) {
            return null;
        }
        Matcher matcher = crawlParamInfoDao.getTimeRegexPatternByTimeId("1").matcher(text);
        String matched = null;
        if (matcher.find()) {
            matched = matcher.group(0);
        }
        if (matched != null) {
            try {
                res = formatDate(matched);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public String getTitleInfo(String html, String htmlTag, ContentTypeEnum contentTypeEnum) {
        return null;
    }

    public String getContentInfo(String html, String htmlTag, ContentTypeEnum contentTypeEnum) {
        return null;
    }


    /**
     * 页面的日期均改为yyyy-MM-dd格式存储在数据库中，所以格式需要统一
     *
     * @param dateStr
     * @return
     */
    private String formatDate(String dateStr) throws ParseException {
//        if (CoreApplication.config.get(CommonConfig.TIME_FORMAT_KEY_WORDS).
//                equals("yyyy-MM-dd")) {
//            return dateStr;
//        }
//        if(CoreApplication.config.containsKey(CommonConfig.TIME_REGIX)){
//            needProcessDateFormat = new SimpleDateFormat(CoreApplication.config.get(CommonConfig.TIME_REGIX));
//            Date date = needProcessDateFormat.parse(dateStr);
//            return simpleDateFormat.format(date);
//        }else{
//            return null;
//        }
        return null;
    }


    private String removeColorFromHtml(String html, String head, String tail) {
        // 去除头部元素，如： <style=\"
        String[] notHaveHead = html.split(head);
        // 取出第一次出现的尾部元素之前的元素，如： \"
        List<String> notHaveHeadOrTail = new ArrayList<>();
        List<String> afterTail = new ArrayList<>();
        if (notHaveHead.length > 1) {
            for (int i = 1; i < notHaveHead.length; i++) {
                String string = notHaveHead[i];
                notHaveHeadOrTail.add(string.substring(0, string.indexOf(tail)));
                afterTail.add(string.substring(string.indexOf(tail)));
            }
        }
        for (int j = 0; j < notHaveHeadOrTail.size(); j++) {
            String[] separats = notHaveHeadOrTail.get(j).split(";");
            StringBuilder sb = new StringBuilder();
            for (String string : separats) {
                if (!string.trim().startsWith("COLOR:") && !string.trim().startsWith("color:")) {
                    sb = sb.append(string).append(";");
                }
            }
            if (sb.length() == 0) {
                notHaveHeadOrTail.set(j, "");
            } else {
                notHaveHeadOrTail.set(j, sb.substring(0, sb.length() - 1));
            }
        }
        StringBuilder resultSb = new StringBuilder();
        resultSb.append(notHaveHead[0]);
        if (notHaveHead.length > 1) {
            for (int i = 1; i < notHaveHead.length; i++) {
                resultSb.append(head).append(notHaveHeadOrTail.get(i - 1)).append(afterTail.get(i - 1));
            }
        }
        return resultSb.toString();
    }

}
