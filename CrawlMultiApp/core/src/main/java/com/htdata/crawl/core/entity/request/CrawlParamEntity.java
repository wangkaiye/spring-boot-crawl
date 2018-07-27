package com.htdata.crawl.core.entity.request;

import lombok.Data;

@Data
public class CrawlParamEntity {
    String weburl;
    String seedurl;
    String titleKeyWords;
    String timeKeyWords;
    String contentKeyWords;
    String websiteInfo;
    String timeId;
    String categoryId;
}
