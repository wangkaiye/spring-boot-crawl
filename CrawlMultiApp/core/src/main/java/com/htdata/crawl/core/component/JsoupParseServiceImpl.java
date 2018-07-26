package com.htdata.crawl.core.component;

import com.htdata.crawl.core.CoreApplication;
import com.htdata.crawl.core.constant.CommonConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class JsoupParseServiceImpl {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat needProcessDateFormat = new SimpleDateFormat(CoreApplication.config.get(CommonConfig.TIME_REGIX));
    /**
     *
     * @param html
     * @param category
     * @param wantHtml
     * @return
     */
    public String getNewsInfo(String html, String category, boolean wantHtml) {
        String res = null;
        Document doc = Jsoup.parse(html);
        switch (category) {
            case "time":
                Elements timeElements = doc.select(CoreApplication.config.get(CommonConfig.TIME_KEY_WORDS));
                if (timeElements == null) {
                    return null;
                }
                if (wantHtml) {
                    return timeElements.html().trim();
                }
                String text = timeElements.text();
                if (text == null) {
                    return null;
                }
                if (CoreApplication.config.get(CommonConfig.TIME_REGIX).equals("*")) {
                    res = text.trim();
                } else {
                    Pattern pattern = Pattern.compile(CoreApplication.config.get(CommonConfig.TIME_REGIX));
                    Matcher matcher = pattern.matcher(text);
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
                }
                break;
            case "title":
                Elements titleElements = doc.select(CoreApplication.config.get(CommonConfig.TITLE_KEY_WORDS));
                if (titleElements == null) {
                    return null;
                }
                if (wantHtml) {
                    return titleElements.html().trim();
                }
                String title = titleElements.text();
                System.out.println(title);
                if (title == null) {
                    return null;
                }
                res = title.trim();
                break;
            case "content":
                if (wantHtml) {
                    Document contentDoc = Jsoup.parse(html.replace("&", CommonConfig.SPECIAL_CHAR_REPLACE_1).replace("#",
                            CommonConfig.SPECIAL_CHAR_REPLACE_2));
                    Elements contentElements = contentDoc.select(CoreApplication.config.get(CommonConfig.CONTENT_KEY_WORDS));
                    if (contentElements == null) {
                        return null;
                    }
                    String withColor = contentElements.outerHtml().replace(CommonConfig.SPECIAL_CHAR_REPLACE_1, "&")
                            .replace(CommonConfig.SPECIAL_CHAR_REPLACE_2, "#");
                    String withoutColorResult = removeColorFromHtml(withColor, "<style=\"", "\"").trim();
                    return withoutColorResult;
                } else {
                    Document contentDoc = Jsoup.parse(html);
                    Elements contentElements = contentDoc.select(CoreApplication.config.get(CommonConfig.CONTENT_KEY_WORDS));
                    String content = contentElements.text();
                    if (content == null) {
                        return null;
                    }
                    res = content.trim();
                }
                break;
            default:
                break;
        }
        return res;
    }
    /**
     * 去除Style中的颜色（第一步拆分，第二部处理，第三部拼接，其中第一和第三步可以被复用）
     *
     * @param html
     * @param head
     * @param tail
     * @return
     */
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

    /**
     * 替换标签中的href相对路径为绝对路径
     *
     * @param html
     * @param currentUrl
     * @param baseUrl
     * @return
     */
    public String makeUrlInHtmlAbsolute(String html, String currentUrl, String baseUrl) {
        String proedBaseUrl = null;
        // String proedCurrentUrl = null;
        // boolean isCurrentBaseUrl = false;
        if (baseUrl.endsWith("/")) {
            proedBaseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        } else {
            proedBaseUrl = baseUrl;
        }
        // 处理当前url
        // String withoutTailUrl = null;
        // if (currentUrl.endsWith("/")) {
        // withoutTailUrl = currentUrl.substring(0, currentUrl.length() - 1);
        // } else {
        // withoutTailUrl = currentUrl;
        // }
        // if (withoutTailUrl.equals(baseUrl)) {
        // isCurrentBaseUrl = true;
        // }
        String head = "href=\"";
        String tail = "\"";
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
            String currentValue = notHaveHeadOrTail.get(j);
            String trans = null;
            if (currentValue.startsWith("/")) {
                trans = proedBaseUrl + currentValue;
            } else if (currentValue.startsWith("./")) {
                trans = currentUrl.substring(0, currentUrl.lastIndexOf("/"))
                        + currentValue.substring(1, currentValue.length());
            } else if (currentValue.startsWith("../")) {
                //包含多个的太麻烦了，不处理了
//				String tailUrl = shortUrl(currentValue, "../");
//				int totalCount = this.count;
//				String withoutHeadCurrentUrl = currentUrl;
//				if(currentUrl.contains("//")){
//					withoutHeadCurrentUrl = currentUrl.substring(currentUrl.indexOf("//")+"//".length(),currentUrl.length());
//				}else{
//					withoutHeadCurrentUrl = currentUrl;
//				}
//				String[] s = withoutHeadCurrentUrl.split("/");
//				if(s.length-1<){}
//				for (int i = 0; i < notHaveHead.length; i++) {
//
//				}
                //顶多处理一个
                if (currentValue.substring(3).contains("../")) {
                    //放弃
                } else {
                    String withOutTail = currentUrl.substring(0, currentUrl.lastIndexOf("/"));
                    String proedTail = withOutTail.substring(0, withOutTail.lastIndexOf("/"));
                    trans = proedTail + currentValue.substring(2);
                }
            } else if (currentValue.startsWith("mailto")) {
                // 不处理
            } else if (currentValue.startsWith("http")) {
                // 不处理
            } else {
                // 与./效果相同
                trans = currentUrl.substring(0, currentUrl.lastIndexOf("/")) + currentValue;
            }
            if (trans != null) {
                notHaveHeadOrTail.set(j, trans);
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

    /**
     * 页面的日期均改为yyyy-MM-dd格式存储在数据库中，所以格式需要统一
     *
     * @param dateStr
     * @return
     */
    private String formatDate(String dateStr) throws ParseException {
        if (CoreApplication.config.get(CommonConfig.TIME_FORMAT_KEY_WORDS).
                equals("yyyy-MM-dd")) {
            return dateStr;
        }
        Date date = needProcessDateFormat.parse(dateStr);
        return simpleDateFormat.format(date);
    }

    public static String getTextNewsInfo(String html, String flag) {
        String res = null;
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(flag);
        if (elements == null) {
            return null;
        }
        String content = elements.text();
        if (content == null) {
            return null;
        }
        res = content.trim();
        return res;
    }

    public static String getHtmlNewsInfo(String html, String flag) {
        String res = null;
        Document doc = Jsoup.parse(html.replace("&", CommonConfig.SPECIAL_CHAR_REPLACE_1).replace("#",
                CommonConfig.SPECIAL_CHAR_REPLACE_2));
        Elements elements = doc.select(flag);
        if (elements == null) {
            return null;
        }
        String outerHtml = elements.outerHtml();
        if (outerHtml == null) {
            return null;
        }
        res = outerHtml.replace(CommonConfig.SPECIAL_CHAR_REPLACE_1, "&")
                .replace(CommonConfig.SPECIAL_CHAR_REPLACE_2, "#").trim();
        return res;
    }
}
