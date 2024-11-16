package com.dz.coop.module.support;

import com.dz.coop.module.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @project: coop-client
 * @description: 书籍封面下载
 * @author: songwj
 * @date: 2022-08-23 2:25 下午
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2022 DIANZHONG TECH. All Rights Reserved.
 */
@Slf4j
@Component
public class BookImageSupport {

    @Resource
    private BookService bookService;

    private static BookImageSupport bookImageSupport;

    private static CloseableHttpClient client;

    @PostConstruct
    public void init() {
        bookImageSupport = this;
        bookImageSupport.bookService = this.bookService;
    }

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10);
        connectionManager.setDefaultMaxPerRoute(10);
        connectionManager.setDefaultSocketConfig(SocketConfig.DEFAULT);

        RequestConfig requestConfig = RequestConfig.custom()
                // 链接超时时间
                .setConnectTimeout(2000)
                // 读超时时间（等待数据超时时间）
                .setSocketTimeout(2000)
                // 从池中获取连接超时时间
                .setConnectionRequestTimeout(1000)
                .build();
        client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setRetryHandler(StandardHttpRequestRetryHandler.INSTANCE)
                .setSSLSocketFactory(getSSLConnectionSocketFactory())
                .build();
    }

    private static SSLConnectionSocketFactory getSSLConnectionSocketFactory() {
        TrustStrategy trustStrategy = ((x509Certificates, s) -> true);
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, trustStrategy).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
        return new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
    }

    public static boolean downloadImage(String bookId, String imgUrl) {
        HttpGet httpGet = new HttpGet(imgUrl);
        httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(httpGet)) {
            String contentType = response.getEntity().getContentType().getValue();
            if (contentType.startsWith("image")) {
                InputStream in = response.getEntity().getContent();
                return bookImageSupport.bookService.uploadCover(bookId, in);
            } else {
                log.error("bookId={},imgUrl={},内容不是图片: {}", bookId, imgUrl, contentType);
                return false;
            }
        } catch (IOException e) {
            log.error("bookId={},imgUrl={},封面下载失败：{}", bookId, imgUrl, e.getMessage(), e);
            return false;
        }
    }
}
