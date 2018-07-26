package com.htdata.crawl.core.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据需求修改页面爬取数据并做后续处理（页面颜色，URL处理）
 */
@Service
public class HtmlDetailAdjustServiceImpl {
    /**
     * 去除Style中的颜色（第一步拆分，第二部处理，第三部拼接，其中第一和第三步可以被复用）
     *
     * @param html
     * @param head
     * @param tail
     * @return
     */
    public String transHtml(String html, String head, String tail) {
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
    public static String transHtmlHref(String html, String currentUrl, String baseUrl) {
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
                if(currentValue.substring(3).contains("../")){
                    //放弃
                }else{
                    String withOutTail = currentUrl.substring(0, currentUrl.lastIndexOf("/"));
                    String proedTail = withOutTail.substring(0, withOutTail.lastIndexOf("/"));
                    trans = proedTail+currentValue.substring(2);
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

}
