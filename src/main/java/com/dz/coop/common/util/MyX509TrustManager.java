package com.dz.coop.common.util;

import com.dz.coop.module.service.BookService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @project: coop-client
 * @description: 获取Http和Https文件资源
 * @author: songwj
 * @date: 2019-08-02 11:05
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Component
public class MyX509TrustManager implements X509TrustManager {

    @Resource
    private BookService bookService;

    private static MyX509TrustManager myX509TrustManager;

    @PostConstruct
    public void init() {
        myX509TrustManager = this;
        myX509TrustManager.bookService = this.bookService;
    }

    /**
     * 下载Https地址资源
     * @param urlStr
     * @param filePath
     * @throws Exception
     */
    public static void downLoadFromUrlHttps(String urlStr, String filePath) throws Exception {
        // 创建SSLContext
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] tm = { new MyX509TrustManager() };
        // 初始化
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 获取SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        // url对象
        URL url = new URL(urlStr);
        // 打开连接
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        /**
         * 这一步的原因: 当访问HTTPS的网址。您可能已经安装了服务器证书到您的JRE的keystore
         * 但是服务器的名称与证书实际域名不相等。这通常发生在你使用的是非标准网上签发的证书。
         *
         * 解决方法：让JRE相信所有的证书和对系统的域名和证书域名。
         *
         * 如果少了这一步会报错:java.io.IOException: HTTPS hostname wrong: should be <localhost>
         */
        conn.setHostnameVerifier(new MyX509TrustManager().new TrustAnyHostnameVerifier());
        // 设置一些参数
        //conn.setDoOutput(true);
        //conn.setDoInput(true);
        //conn.setUseCaches(false);
        // 设置当前实例使用的SSLSoctetFactory
        conn.setSSLSocketFactory(ssf);
        conn.connect();

        // 得到输入流
        InputStream is = conn.getInputStream();
        AliOssUtil.setCover(filePath, is);
        conn.disconnect();
    }

    /**
     * 下载Http地址资源
     * @param urlStr
     * @param filePath
     * @throws IOException
     */
    public static void downLoadFromUrlHttp(String urlStr, String filePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        // 防止屏蔽程序抓取而返回403错误
        //conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        conn.connect();

        // 得到输入流
        InputStream is = conn.getInputStream();
        AliOssUtil.setCover(filePath, is);
        conn.disconnect();
    }

    public static void downloadHttpBookCover(String urlStr, String bookId) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        conn.setReadTimeout(30*1000);
        conn.connect();

        // 得到输入流
        InputStream is = conn.getInputStream();
        myX509TrustManager.bookService.uploadCover(bookId, is);
        conn.disconnect();
    }

    public static void downloadHttpsBookCover(String urlStr, String bookId) throws Exception {
        // 创建SSLContext
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] tm = { new MyX509TrustManager() };
        // 初始化
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 获取SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        // url对象
        URL url = new URL(urlStr);
        // 打开连接
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        /**
         * 这一步的原因: 当访问HTTPS的网址。您可能已经安装了服务器证书到您的JRE的keystore
         * 但是服务器的名称与证书实际域名不相等。这通常发生在你使用的是非标准网上签发的证书。
         * 解决方法：让JRE相信所有的证书和对系统的域名和证书域名。
         * 如果少了这一步会报错:java.io.IOException: HTTPS hostname wrong: should be <localhost>
         */
        conn.setHostnameVerifier(new MyX509TrustManager().new TrustAnyHostnameVerifier());
        // 设置当前实例使用的SSLSoctetFactory
        conn.setSSLSocketFactory(ssf);
        conn.setConnectTimeout(10*1000);
        conn.setReadTimeout(30*1000);
        conn.connect();

        // 得到输入流
        InputStream is = conn.getInputStream();
        myX509TrustManager.bookService.uploadCover(bookId, is);
        conn.disconnect();
    }

    /**
     * 校验https网址是否安全
     */
    public class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // 直接返回true:默认所有https请求都是安全的
            return true;
        }
    }

    /**
     * 里面的方法都是空的，当方法为空是默认为所有的链接都为安全，也就是所有的链接都能够访问到 当然这样有一定的安全风险，可以根据实际需要写入内容
     * @param chain
     * @param authType
     * @throws CertificateException
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
