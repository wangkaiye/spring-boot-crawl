package com.htdata.crawl.core.manager;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
@Component
public class HttpUtil {
    public String httpGet(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                if (entity != null) {
                    return EntityUtils.toString(entity,"UTF-8");
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String httpsGet(String urlStr) {
        URL url;

        try {
            url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            };
            TrustManager[] tm = { xtm };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tm, null);
            con.setSSLSocketFactory(ctx.getSocketFactory());
            con.setRequestProperty("Cookie",
                    "PLATFORM_SESSION=a3c43665de7dfdfe3d4b21c5d32c70d94beaf39c-_ldi=473033&_lsh=1e698d2145381266650adff2d3a99d6113cc8dfd&csrfToken=bd11b6fb8985ece0faaac6f0067549952be3b7fa-1517197351576-50dfd3a75a090031e720fdd1&_lpt=%2Fcn%2Fgreen_vehicles%2Fmodel%2F588&_lsi=1604404; _plh=5165068c35dcaa1959b1f421da622c4f5555913b; PLAY_LANG=cn");
            con.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    // TODO Auto-generated method stub
                    return true;
                }
            });
            // con.setHostnameVerifier((arg0, arg1) -> true);
            BufferedReader read = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String temp;
            StringBuilder re = new StringBuilder();
            while ((temp = read.readLine()) != null) {
                re.append(temp);
            }
            return re.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
