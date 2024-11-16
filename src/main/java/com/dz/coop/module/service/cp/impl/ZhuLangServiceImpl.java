package com.dz.coop.module.service.cp.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.DZSignUtils;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.common.util.UrlEncodeUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.ZhuLangBook;
import com.dz.coop.module.model.ZhuLangChapter;
import com.dz.coop.module.model.ZongHengChapter;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.type.JavaType;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * KEY：zhulang_kuaikan
 * secret：dFmN9SLdFVTC5WX
 * url:http://api.zhulang.com/ifbk/
 *
 * @author kangyf
 * @date 2016-03-31
 */
@Service
public class ZhuLangServiceImpl implements ClientService {

    private Logger log = Logger.getLogger(this.getClass());

    private final String CONSUMER_SECRET = "dFmN9SLdFVTC5WX";

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        Map<String, String> parameterMap = new HashMap<String, String>();
        Date date = new Date();//生成10位长日期
        long dateLong = date.getTime();
        parameterMap.put("action", "getBookList");//
        String baseString = oauth1BaseString(owchPartner.getBookListUrl(), "GET", parameterMap);
        String sign = oauth1SignatureHMAC_SHA1(baseString, CONSUMER_SECRET, "");

        parameterMap.put("oauth_consumer_key", owchPartner.getApiKey());//
        parameterMap.put("oauth_timestamp", (dateLong + "").substring(0, 10));
        parameterMap.put("oauth_signature", "getBookList");//
        parameterMap.put("format", "json");//
        //计算机签名
        String params = DZSignUtils.linkFileName(parameterMap);
        String url = owchPartner.getBookListUrl() + "?" + params + "&oauth_signature=" + sign;
        List<CPBook> bookList = getZhuLangBookList(url);
        return bookList;
    }

    //得到书列表
    private List<CPBook> getZhuLangBookList(String url) throws Exception {
        String[] zhuLangBookIdArray = null;
        List<CPBook> listZyBook = null;

        //从网络接口，获取书籍列表
        String resultStr = HttpUtil.sendGet(url);
        try {
            JSONObject jsonObject = JSON.parseObject(resultStr);
            boolean isExistError = StringUtils.isBlank(jsonObject.getString("error_code"));
            if (!isExistError) {
                String re = (String) jsonObject.getString("error_code");
                log.info("ZhuLang bookList【code=" + re + ",message=" + getErrorMessage(re) + "】");
                return null;
            }
            if (resultStr == null || "".equals(resultStr)) {
                log.info("【error  bookList is null or '' 】");
                return null;
            }
        } catch (Exception e) {//未有错误码error_code,转换JSON会出现异常
            zhuLangBookIdArray = JsonUtils.fromJson(resultStr, String[].class, String.class);
            if (zhuLangBookIdArray != null && zhuLangBookIdArray.length > 0) {
                listZyBook = new ArrayList<CPBook>();
                //封装书ID
                for (String zhBookId : zhuLangBookIdArray) {
                    CPBook zyBook = new CPBook();
                    zyBook.setId(zhBookId);
                    listZyBook.add(zyBook);
                }
            }
        }

        return listZyBook;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        Map<String, String> parameterMap = new HashMap<String, String>();
        Date date = new Date();//生成10位长日期
        long dateLong = date.getTime();
        parameterMap.put("action", "getBookInfo");//
        parameterMap.put("bk_id", bookId);//
        String baseString = oauth1BaseString(owchPartner.getBookListUrl(), "GET", parameterMap);
        String sign = oauth1SignatureHMAC_SHA1(baseString, CONSUMER_SECRET, "");

        parameterMap.put("oauth_consumer_key", owchPartner.getApiKey());//
        parameterMap.put("oauth_timestamp", (dateLong + "").substring(0, 10));
        parameterMap.put("oauth_signature", "getBookList");//
        parameterMap.put("format", "json");//
        //计算机签名
        String params = DZSignUtils.linkFileName(parameterMap);
        String url = owchPartner.getBookListUrl() + "?" + params + "&oauth_signature=" + sign;
        CPBook zyBook = getSourceZhuLangBookInfo(url);
        return zyBook;
    }

    //从网络接口，获取某本书的详细信息
    private CPBook getSourceZhuLangBookInfo(String url) {
        ZhuLangBook zhuLangBook = null;
        CPBook cpBook = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            JSONObject jsonObject = JSON.parseObject(resultStr);
            boolean isExistError = StringUtils.isBlank(jsonObject.getString("error_code"));
            if (!isExistError) {
                String re = (String) jsonObject.getString("error_code");
                log.info("ZhuLang bookInfo【code=" + re + ",message=" + getErrorMessage(re) + "】");
                return null;
            }
            if (resultStr == null || "".equals(resultStr)) {
                log.info("【error  bookInfo is null or '' 】");
                return null;
            }
            //封装书籍基本信息
            zhuLangBook = JsonUtils.fromJSON(resultStr, ZhuLangBook.class);
            if (zhuLangBook != null) {
                cpBook = new CPBook();
                cpBook.setId(zhuLangBook.getBk_id());//书籍ID
                cpBook.setName(zhuLangBook.getBk_name());//书籍名称
                cpBook.setAuthor(zhuLangBook.getBk_author());//作者名称
                cpBook.setBrief(zhuLangBook.getBk_intro());//作品简介
                //String status = zhuLangBook.getBk_fullflag();//状态，’0’连载；’1’完本；’2’封笔
                //book.setStatus("1".equals(cpBook.getCompleteStatus()) ? "完本" : "连载");//其它
                cpBook.setCompleteStatus(zhuLangBook.getBk_fullflag());
                cpBook.setCover(zhuLangBook.getBk_cover());//作品封面图片url地址
                cpBook.setCategory(zhuLangBook.getBk_class_a());//一级分类代码
                String totalChapternum = zhuLangBook.getCh_total();//章节总数
//                if (totalChapternum != null && !"".equals(totalChapternum)) {
//                    cpBook.setTotalChapterNum(Integer.parseInt(totalChapternum));//章节总字数
//                }
            }
        } catch (Throwable e) {
            log.error("纵横书籍信息格式错误" + e.getMessage(), e);
        }
        return cpBook;
    }


    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) {

        List<CPVolume> listCPVolume = null;
        Map<String, String> parameterMap = new HashMap<String, String>();
        Date date = new Date();//生成10位长日期
        long dateLong = date.getTime();
        parameterMap.put("action", "getChapterList");//
        parameterMap.put("bk_id", bookId);//
        String baseString = oauth1BaseString(owchPartner.getBookListUrl(), "GET", parameterMap);
        String sign = oauth1SignatureHMAC_SHA1(baseString, CONSUMER_SECRET, "");

        parameterMap.put("oauth_consumer_key", owchPartner.getApiKey());//
        parameterMap.put("oauth_timestamp", (dateLong + "").substring(0, 10));
        parameterMap.put("oauth_signature", "getBookList");//
        parameterMap.put("format", "json");//
        //计算机签名
        String params = DZSignUtils.linkFileName(parameterMap);
        String url = owchPartner.getChapterListUrl() + "?" + params + "&oauth_signature=" + sign;

        List<ZhuLangChapter> list = getSourceZhuLangChapterList(url);
        listCPVolume = packVolumeListForZhuLang(list);
        return listCPVolume;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    //打包卷
    private List<CPVolume> packVolumeListForZhuLang(List<ZhuLangChapter> listZhuLangChapter) {
        List<CPVolume> listCPVolume = null;
        if (CollectionUtils.isNotEmpty(listZhuLangChapter)) {
            //存放卷ID
            List<Long> listVolumeId = new ArrayList<Long>();
            //存放卷名称
            Map<Long, String> volumeIdVolumeNameMap = new HashMap<Long, String>();
            for (ZhuLangChapter zhuLangChapter : listZhuLangChapter) {
                //判断卷集合里，是不已经存放过该卷，如果未存在，刚进行保存
                if (!listVolumeId.contains(Long.parseLong(zhuLangChapter.getCh_roll()))) {
                    listVolumeId.add(Long.parseLong(zhuLangChapter.getCh_roll()));
                    //存放卷名称，key=卷ID，value=卷名称
                    volumeIdVolumeNameMap.put(Long.parseLong(zhuLangChapter.getCh_roll()), zhuLangChapter.getCh_roll_name());
                }
            }
            //装卷信息
            listCPVolume = new ArrayList<CPVolume>();
            if (CollectionUtils.isNotEmpty(listVolumeId)) {
                for (int i = 0; i < listVolumeId.size(); i++) {
                    CPVolume cpVolume = new CPVolume();
                    cpVolume.setId(listVolumeId.get(i).toString());//存纵横自有卷id，入库 b_owchcp_volume会用
                    cpVolume.setName(volumeIdVolumeNameMap.get(listVolumeId.get(i)));
                    for (ZhuLangChapter zhuLangChapter : listZhuLangChapter) {
                        if (listVolumeId.get(i) == Long.parseLong(zhuLangChapter.getCh_roll())) {//把卷Id相同的章节，放到一个卷里
                            CPChapter cpChapter = new CPChapter();
                            cpChapter.setId(zhuLangChapter.getCh_id());//章节ID
                            cpChapter.setName(zhuLangChapter.getCh_name());//章节名称
                            cpVolume.getChapterList().add(cpChapter);//把相同卷的章节放在一起
                        }
                    }
                    listCPVolume.add(cpVolume);
                }
            }
        }
        return listCPVolume;
    }

    //获取章节列表
    private List<ZhuLangChapter> getSourceZhuLangChapterList(String url) {
        List<ZhuLangChapter> list = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            try {
                JSONObject jsonObject = JSON.parseObject(resultStr);
                boolean isExistError = StringUtils.isBlank(jsonObject.getString("error_code"));
                if (!isExistError) {
                    String re = (String) jsonObject.getString("error_code");
                    log.info("ZhuLang chapterList【code=" + re + ",message=" + getErrorMessage(re) + "】");
                    return null;
                }
                if (resultStr == null || "".equals(resultStr)) {
                    log.info("【error  chapterList is null or '' 】");
                    return null;
                }
            } catch (Exception e) {//未有错误码error_code,转换JSON会出现异常
                JavaType javaType2 = JsonUtils.createCollectionType(ArrayList.class, ZhuLangChapter.class);
                list = JsonUtils.fromJson(resultStr, javaType2);
            }
        } catch (Throwable e) {
            log.error("纵横书籍列表格式错误 !!" + e.getMessage(), e);
            return null;
        }
        return list;
    }


    //获取章节内容
    private CPChapter getSourceZhuLangChapterWithContent(String url) {
        CPChapter zyChapter = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            JSONObject jsonObject = JSON.parseObject(resultStr);
            //是否有错误码
            boolean isExistError = StringUtils.isBlank(jsonObject.getString("error_code"));
            if (!isExistError) {
                String re = (String) jsonObject.getString("error_code");
                System.out.println(re);
                log.info("ZhuLang chapter content【code=" + re + ",message=" + getErrorMessage(re) + "】");
                return null;
            }
            if (resultStr == null || "".equals(resultStr)) {
                log.info("【error  content is null or '' 】");
                return null;
            }
            //把json转换为对象
            ZongHengChapter zongHengChapter = JsonUtils.fromJSON(resultStr, ZongHengChapter.class);
            if (zongHengChapter != null) {
                zyChapter = new CPChapter();
                zyChapter.setId(zongHengChapter.getChapterId());
                zyChapter.setName(zongHengChapter.getChapterName());
                zyChapter.setVolumeId(zongHengChapter.getTomeId());
                String content = zongHengChapter.getContent();
                //换行符替换
                content = content.replaceAll("<p>", "").replaceAll("</p>", "\r\n");
                zyChapter.setContent(content);
            }
        } catch (Throwable e) {
            log.error("纵横章节" + e.getMessage(), e);
        }
        return zyChapter;
    }


    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String bookId, String chapterId) throws Exception {
        Map<String, String> parameterMap = new HashMap<String, String>();
        Date date = new Date();//生成10位长日期
        long dateLong = date.getTime();
        parameterMap.put("action", "getChapterContent");//
        parameterMap.put("bk_id", bookId);//书籍ID
        parameterMap.put("ch_id", chapterId);//章节ID
        String baseString = oauth1BaseString(owchPartner.getBookListUrl(), "GET", parameterMap);
        String sign = oauth1SignatureHMAC_SHA1(baseString, CONSUMER_SECRET, "");

        parameterMap.put("oauth_consumer_key", owchPartner.getApiKey());//
        parameterMap.put("oauth_timestamp", (dateLong + "").substring(0, 10));
        parameterMap.put("oauth_signature", "getBookList");//
        parameterMap.put("format", "json");//
        //计算机签名
        String params = DZSignUtils.linkFileName(parameterMap);
        String url = owchPartner.getChapterInfoUrl() + "?" + params + "&oauth_signature=" + sign;
        CPChapter zyChapter = getSourceZhuLangChapterWithContent(url);
        return zyChapter;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.ZHU_LANG};
    }


//    @Override
//    public Long[] getClientId() {
//        return new Long[]{SpecialPartnerEnum.CLIENT_21};
//    }


    /**
     * OAUTH1.0协议，获取 base string值
     *
     * @param url          url地址
     * @param method       请求方法
     * @param parameterMap 参数数组
     * @return
     */
    private String oauth1BaseString(String url, String method, Map<String, String> parameterMap) {
        if (parameterMap == null || parameterMap.size() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        String params = DZSignUtils.oauth1BaseString(parameterMap);
        sb.append(method.toUpperCase());
        sb.append("&" + UrlEncodeUtil.urlencodeRFC3986(url));
        sb.append(params);
        return sb.toString();
    }

    /**
     * OAUTH1.0协议，用 HMAC_SHA1方法签名
     *
     * @param baseString     需要签名的 base string
     * @param consumerSecret 客户安全钥匙
     * @param tokenSecret    令牌安全钥匙
     * @return
     */
    private String oauth1SignatureHMAC_SHA1(String baseString, String consumerSecret, String tokenSecret) {
        String sign = "";
        try {
            sign = DZSignUtils.oauth1SignatureHMAC_SHA1(baseString, consumerSecret, tokenSecret);
        } catch (UnsupportedEncodingException e) {
            log.error("计算【OAUTH1.0协议，用 HMAC_SHA1方法签名】发生异常", e);
            e.printStackTrace();
        }
        return sign;
    }

    private String getErrorMessage(String errorCode) {
        String errorMessage = "";
        switch (errorCode) {
            case "-4001":
                errorMessage = "无效的合作商key";
                break;
            case "-4002":
                errorMessage = "签名校验失败";
                break;
            case "-4003":
                errorMessage = "IP被拒绝访问";
                break;
            case "-4004":
                errorMessage = "时间戳已经失效（已经超过8分钟）";
                break;
            case "-4005":
                errorMessage = "传入的签名验证方法不支持";
                break;
            case "-4006":
                errorMessage = "返回结果的格式参数值不支持";
                break;
            case "-4007":
                errorMessage = "缺少必须的验证参数值";
                break;
            case "-4100":
                errorMessage = "缺少必要的参数";
                break;
            case "-4101":
                errorMessage = "此API接口此action被拒绝";
                break;
            case "-5001":
                errorMessage = "书号不存在";
                break;
            case "-5002":
                errorMessage = "该书号不允许此合作商访问";
                break;
            case "-5003":
                errorMessage = "该书VIP内容不允许此合作商读取";
                break;
            case "-5004":
                errorMessage = "书类API参数不正确";
                break;
            case "-5005":
                errorMessage = "此列表排序方法没有授权";
                break;
            case "-5006":
                errorMessage = "获取章节时章节号和书号传入参数不正确";
                break;
            case "-5007":
                errorMessage = "此合作商不允许获取此章节最新内容";
                break;
        }
        return errorMessage;

    }


}
