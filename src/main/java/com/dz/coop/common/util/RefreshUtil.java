package com.dz.coop.common.util;

import com.dz.coop.common.SpringContextHolder;
import com.dz.coop.conf.properties.QiNiuConf;
import com.qiniu.util.Auth;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Deprecated
public class RefreshUtil {

    public static final Auth auth;

    static {
        QiNiuConf bean = SpringContextHolder.getBean(QiNiuConf.class);
        auth = Auth.create(bean.getAccesskey(), bean.getSecretkey());

    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final Logger logger = LoggerFactory.getLogger((MethodHandles.lookup().lookupClass()));

    public static boolean fresh(String str) {
        boolean flag = true;
        Response re = null;
        try {
            String signingStr = "/refresh\n";
            String url = "http://fusion.qiniuapi.com/refresh";
            String access_token = auth.sign(signingStr);
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, str);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "QBox " + access_token)
                    .post(body)
                    .build();
            re = client.newCall(request).execute();
            if (re.code() != 200) {
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
            logger.info(e.getMessage(), e);
        } finally {
            if (re != null) {
                if (re.body() != null) {
                    re.body().close();
                }
            }
        }
        return flag;
    }
}
