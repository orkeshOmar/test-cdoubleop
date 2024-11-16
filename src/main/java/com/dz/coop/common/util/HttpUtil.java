package com.dz.coop.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dz.coop.module.model.Partner;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.util.*;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContexts;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static RequestConfig requestConfig;

    static {
        int seconds = 30;
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(seconds * 1000).setConnectTimeout(seconds * 1000)
                .setConnectionRequestTimeout(seconds * 1000)
                .build();


    }

    /**
     * @param httpUrl   发送的地址
     * @param paramMap  发送的内容
     * @param headerMap 增加的Http头信息
     * @return 返回HTTP SERVER的处理结果,如果返回null,发送失败
     */
    public static String sentPost(String httpUrl, Map<String, String> paramMap, Map<String, String> headerMap) {
        TraceKeyHolder.setUserKey("url", httpUrl);
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            //配置，发送https请求时，忽略ssl证书认证（否则会报错没有证书）
            SSLContext sslContext = null;
            try {
                sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        return true;
                    }
                }).build();
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                logger.error("sentPost 获取 sslContext 异常，url={}, message: {}", httpUrl, e.getMessage(), e);
            }

            httpclient = HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).setDefaultRequestConfig(requestConfig).setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
            HttpPost httpPost = new HttpPost(httpUrl);

            if (MapUtils.isNotEmpty(paramMap)) {
                List<NameValuePair> nvps = new ArrayList<>(paramMap.size());
                for (String key : paramMap.keySet()) {
                    nvps.add(new BasicNameValuePair(key, paramMap.get(key)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }
            if (MapUtils.isNotEmpty(headerMap)){
                for (String key : headerMap.keySet()) {
                    httpPost.addHeader(key, headerMap.get(key));
                }
            }

            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String ret = EntityUtils.toString(entity, "UTF-8");
            String retMsg = StringUtils.isNotBlank(ret) ? (ret.length() > 1000 ? ret.substring(0, 1000) : ret) : ret;
            TraceKeyHolder.setUserKey("ret", retMsg);
            return ret;
        } catch (Exception e) {
            logger.error("url={}, message: {}", httpUrl, e.getMessage(), e);
            String ret = e.toString();
            TraceKeyHolder.setUserKey("ret", StringUtils.isNotBlank(ret) ? (ret.length() > 1000 ? ret.substring(0, 1000) : ret) : ret);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String doPost(String url, Map<String, String> paramMap) {
        TraceKeyHolder.setUserKey("url", url);
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
            HttpPost httpPost = new HttpPost(url);

            if (MapUtils.isNotEmpty(paramMap)) {
                List<NameValuePair> nvps = new ArrayList<>(paramMap.size());
                for (String key : paramMap.keySet()) {
                    nvps.add(new BasicNameValuePair(key, paramMap.get(key)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }

            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String ret = EntityUtils.toString(entity, "UTF-8");
            String retMsg = StringUtils.isNotBlank(ret) ? (ret.length() > 1000 ? ret.substring(0, 1000) : ret) : ret;
            TraceKeyHolder.setUserKey("ret", retMsg);
            return ret;
        } catch (Exception e) {
            logger.error("url={}, message: {}", url, e.getMessage(), e);
            String ret = e.toString();
            TraceKeyHolder.setUserKey("ret", StringUtils.isNotBlank(ret) ? (ret.length() > 1000 ? ret.substring(0, 1000) : ret) : ret);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String sendGet(String urlStr) {
        TraceKeyHolder.setUserKey("url", urlStr);
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse httpResponse = null;

        try {

            // 创建信任所有证书的SSLContext
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true)
                    .build();

            httpclient = HttpClients.custom().setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setDefaultRequestConfig(requestConfig).setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
            httpResponse = httpclient.execute(new HttpGet(urlStr));
            HttpEntity entity = httpResponse.getEntity();
            String ret = EntityUtils.toString(entity, "UTF-8");
            String retMsg = StringUtils.isNotBlank(ret) ? (ret.length() > 1000 ? ret.substring(0, 1000) : ret) : ret;
            TraceKeyHolder.setUserKey("ret", retMsg);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("url={}, message: {}", urlStr, e.getMessage(), e);
            String ret = e.toString();
            TraceKeyHolder.setUserKey("ret", StringUtils.isNotBlank(ret) ? (ret.length() > 1000 ? ret.substring(0, 1000) : ret) : ret);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String sendGet(String urlStr, Map<String, String> headerMap) {
        TraceKeyHolder.setUserKey("url", urlStr);
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse httpResponse = null;

        try {
            httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
            HttpGet httpGet = new HttpGet(urlStr);

            if (MapUtils.isNotEmpty(headerMap)) {
                Set<Map.Entry<String, String>> entries = headerMap.entrySet();
                for (Map.Entry<String, String> me : entries) {
                    httpGet.addHeader(me.getKey(), me.getValue());
                }
            }

            httpResponse = httpclient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String ret = EntityUtils.toString(entity, "UTF-8");
            String retMsg = StringUtils.isNotBlank(ret) ? (ret.length() > 1000 ? ret.substring(0, 1000) : ret) : ret;
            TraceKeyHolder.setUserKey("ret", retMsg);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("url={}, message: {}", urlStr, e.getMessage(), e);
            String ret = e.toString();
            TraceKeyHolder.setUserKey("ret", StringUtils.isNotBlank(ret) ? (ret.length() > 1000 ? ret.substring(0, 1000) : ret) : ret);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 发送Get请求并获取指定类型数据
     * @param url 原始访问的url
     * @param returnClassType 返回的类对象
     * @param owchPartner
     * @param code 接口响应状态码参数名
     * @param msg 接口响应提示信息参数
     * @param data 接口响应内容参数
     * @param successCode 接口响应成功的状态码值
     * @param <T> 返回的类型
     * @return
     * @throws Exception
     */
    public static  <T> T doGet(String url, Class<T> returnClassType, Partner owchPartner, String code, String msg, String data, String successCode) throws Exception {
        String json = HttpUtil.sendGet(url);
        if (StringUtils.isBlank(json)) {
            throw new Exception("[" + owchPartner.getName() + "]返回json串为空");
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        if (!StringUtils.equals(jsonObject.getString(code), successCode)) {
            throw new Exception("[" + owchPartner.getName() + "]接口状态码异常,code=" + jsonObject.getString(code) + ",message=" + jsonObject.getString(msg));
        }

        return jsonObject.getObject(data, returnClassType);
    }

    /**
     * 发送Get请求并获取指定类型数据
     * @param url 原始访问的url
     * @param headerMap 请求header部分
     * @param returnClassType 返回的类对象
     * @param owchPartner
     * @param code 接口响应状态码参数名
     * @param msg 接口响应提示信息参数
     * @param data 接口响应内容参数
     * @param successCode 接口响应成功的状态码值
     * @param <T> 返回的类型
     * @return
     * @throws Exception
     */
    public static  <T> T doGet(String url, Map<String, String> headerMap, Class<T> returnClassType, Partner owchPartner, String code, String msg, String data, String successCode) throws Exception {
        String json = sendGet(url, headerMap);
        if (StringUtils.isBlank(json)) {
            throw new Exception("[" + owchPartner.getName() + "]返回json串为空");
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        if (!StringUtils.equals(jsonObject.getString(code), successCode)) {
            throw new Exception("[" + owchPartner.getName() + "]接口状态码异常,code=" + jsonObject.getString(code) + ",message=" + jsonObject.getString(msg));
        }

        return jsonObject.getObject(data, returnClassType);
    }

//    public static void main(String[] args) {
//
//        String url = "https://api.mengyouxiaoshuo.com/outputs/dianzhong/getBookList?client_id=20434&sign=fea038a83d660a36f610793358974655";
//        String res = sendGet(url);
//        System.out.println(res);
//        JSONArray books = JSON.parseArray(res);
//        System.out.println(books.toString());
//    }

}


