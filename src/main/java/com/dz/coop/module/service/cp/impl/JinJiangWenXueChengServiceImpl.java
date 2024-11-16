package com.dz.coop.module.service.cp.impl;


import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.model.vo.*;
import com.dz.coop.module.service.cp.ClientService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class JinJiangWenXueChengServiceImpl implements ClientService {

    private Logger log = Logger.getLogger(this.getClass());


    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String openid = owchPartner.getAliasId();//Cp渠道ID
        String openkey = owchPartner.getApiKey();//MD5密钥
        String sign = DigestUtils.md5Hex(openid + openkey);//生成签名
        //格式化URL
        String url = MessageFormat.format(owchPartner.getBookListUrl(), String.valueOf(openid), sign);
        List<CPBook> bookList = getSourceJJWXCBookList(url, owchPartner);
        return bookList;
    }


    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String openid = owchPartner.getAliasId();//Cp渠道ID
        String openkey = owchPartner.getApiKey();//MD5密钥
        String sign = DigestUtils.md5Hex(openid + openkey + bookId);//生成签名
        //格式化URL
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), String.valueOf(openid), bookId, sign);
        //获取书籍基本信息
        CPBook zyBook = getSourceJJWXCBookInfo(url, owchPartner, bookId);
        return zyBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> listCPVolume = null;
        String openid = owchPartner.getAliasId();//Cp渠道ID
        String openkey = owchPartner.getApiKey();//MD5密钥
        String sign = DigestUtils.md5Hex(openid + openkey + bookId);//生成签名
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), String.valueOf(openid), bookId, sign);
        List<JingJiangWenXueChengChapterList> list = getSourceJJWXCChapterList(url, owchPartner, bookId);
        listCPVolume = packVolumeListForKanShuWang(list);
        return listCPVolume;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String bookId, String chapterId) throws Exception {
        String openid = owchPartner.getAliasId();//Cp渠道ID
        String openkey = owchPartner.getApiKey();//MD5密钥
        String sign = DigestUtils.md5Hex(openid + openkey + bookId + chapterId);//生成签名
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), String.valueOf(openid), bookId, chapterId, sign);
        CPChapter zyChapter = getSourceJJWXCChapterWithContent(url, owchPartner, bookId, chapterId);
        return zyChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

//    @Override
//    public Long[] getClientId() {
//        return new Long[]{SpecialPartnerEnum.CLIENT_100080665993};
//    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.JIN_JIANG};
    }

    /**
     * 获得CP书籍列表
     *
     * @param url 网络获取书籍的url
     * @param cp  CP配置信息
     * @return
     */
    private List<CPBook> getSourceJJWXCBookList(String url, Partner cp) {
        List<CPBook> listZyBook = null;
        JingJiangWenXueChengResponseInfo jingJiangWenXueChengResponseInfo = null;
        try {
            //请求CP外网访问接口，获取图书列表
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            //XML转换成javabean对象，判断是否存在错误码
            XStream stream = new XStream(new DomDriver());
            //XML未存在error节点，转换对象会报异常。未出现异常证明没有错误代码，有正常结果返回。
            try {
                stream.ignoreUnknownElements();
                stream.aliasType("document", JingJiangWenXueChengResponseInfo.class);
                stream.aliasField("errorCode", JingJiangWenXueChengResponseInfo.class, "errorCode");
                jingJiangWenXueChengResponseInfo = (JingJiangWenXueChengResponseInfo) stream.fromXML(resultStr);
                if (jingJiangWenXueChengResponseInfo == null || jingJiangWenXueChengResponseInfo.getErrorCode() != null || !"".equals(jingJiangWenXueChengResponseInfo.getErrorCode())) {
                    if (jingJiangWenXueChengResponseInfo != null) {
                        log.info("晋江文学城 bookList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + ",error=" + jingJiangWenXueChengResponseInfo.getErrorInfo() + "】");
                    } else {
                        log.info("晋江文学城bookList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + "】");
                    }
                    return null;
                }
            } catch (Exception e) {
                //未有错误，对XML重新进行对象转换
                stream = new XStream(new DomDriver());
                stream.ignoreUnknownElements();
                stream.aliasType("document", JingJiangWenXueChengResponseInfo.class);
                stream.alias("novel", JingJiangWenXueChengBook.class);
                stream.addImplicitCollection(JingJiangWenXueChengResponseInfo.class, "books");
                stream.aliasField("novelid", JingJiangWenXueChengBook.class, "novelid");
                jingJiangWenXueChengResponseInfo = (JingJiangWenXueChengResponseInfo) stream.fromXML(resultStr);
                if (jingJiangWenXueChengResponseInfo.getBooks() == null || jingJiangWenXueChengResponseInfo.getBooks().size() == 0) {
                    log.info("【error result bookList is 】" + (jingJiangWenXueChengResponseInfo.getBooks() == null ? null : jingJiangWenXueChengResponseInfo.getBooks().size()));
                    return null;
                }
                List<JingJiangWenXueChengBook> books = jingJiangWenXueChengResponseInfo.getBooks();//书籍列表
                listZyBook = new ArrayList<CPBook>();
                for (JingJiangWenXueChengBook book : books) {
                    CPBook zyBook = new CPBook();
                    zyBook.setId(book.getNovelid());
                    listZyBook.add(zyBook);
                }
            }
        } catch (Throwable e) {
            log.error("晋江文学城书籍列表格式错误 !!" + e.getMessage(), e);
            return null;
        }
        return listZyBook;
    }

    /**
     * 获取书籍详细信息
     *
     * @param url    网络获取书籍详细信息url
     * @param cp     cp配置信息
     * @param bookId 网络查询书籍ID
     * @return
     */
    private CPBook getSourceJJWXCBookInfo(String url, Partner cp, String bookId) {
        JingJiangWenXueChengResponseInfo respInfo = null;
        JingJiangWenXueChengBookInfo bookInfo = null;
        CPBook cpBook = null;
        try {
            //请求CP外网访问接口，获取书籍基本信息
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            XStream stream = new XStream(new DomDriver());
            try {
                //XML转换成javabean对象
                stream.ignoreUnknownElements();
                stream.aliasType("document", JingJiangWenXueChengResponseInfo.class);
                stream.aliasField("errorCode", JingJiangWenXueChengResponseInfo.class, "errorCode");
                //读XML是是否存在,error节点，未有error节点，会报异常。未出现异常证明没有错误代码，有正常结果返回。
                respInfo = (JingJiangWenXueChengResponseInfo) stream.fromXML(resultStr);
                //判断XML是否存在,error节点，如果存在，证明请求服务器有错误信息
                if (respInfo == null || respInfo.getErrorCode() != null || !"".equals(respInfo.getErrorCode())) {
                    if (respInfo != null) {
                        log.info("晋江文学城 bookInfo【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + ",error=" + respInfo.getErrorInfo() + "】");
                    } else {
                        log.info("晋江文学城 bookInfo【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + "】");
                    }
                    return null;
                }
            } catch (Exception e) {//未有错误代码，进行数据处理
                stream = new XStream(new DomDriver());
                stream.ignoreUnknownElements();
                stream.alias("document", JingJiangWenXueChengBookInfo.class);
                stream.aliasField("novelid", JingJiangWenXueChengBookInfo.class, "novelid");
                stream.aliasField("novelname", JingJiangWenXueChengBookInfo.class, "novelname");
                stream.aliasField("authorname", JingJiangWenXueChengBookInfo.class, "authorname");
                stream.aliasField("novelsize", JingJiangWenXueChengBookInfo.class, "novelsize");
                stream.aliasField("novelclass", JingJiangWenXueChengBookInfo.class, "novelclass");
                stream.aliasField("novelintro", JingJiangWenXueChengBookInfo.class, "novelintro");
                stream.aliasField("novelintroshort ", JingJiangWenXueChengBookInfo.class, "novelintroshort ");
                stream.aliasField("publishdate", JingJiangWenXueChengBookInfo.class, "publishdate");
                stream.aliasField("renewdate", JingJiangWenXueChengBookInfo.class, "renewdate");
                stream.aliasField("befavoritedcount", JingJiangWenXueChengBookInfo.class, "befavoritedcount");
                stream.aliasField("novelstep", JingJiangWenXueChengBookInfo.class, "novelstep");
                stream.aliasField("listurl", JingJiangWenXueChengBookInfo.class, "listurl");
                stream.aliasField("vipflag", JingJiangWenXueChengBookInfo.class, "vipflag");
                stream.aliasField("coverurl ", JingJiangWenXueChengBookInfo.class, "coverurl");
                stream.aliasField("novelscore_real", JingJiangWenXueChengBookInfo.class, "novelscore");
                stream.aliasField("chapterclickright", JingJiangWenXueChengBookInfo.class, "imagePath");
                bookInfo = (JingJiangWenXueChengBookInfo) stream.fromXML(resultStr);
            }

            if (bookInfo == null || StringUtils.isEmpty(bookInfo.getNovelid())) {
                log.info("【error result bookinfo is null】网络需要获取bookId=" + bookId);
                return null;
            }
            cpBook = new CPBook();
            cpBook.setId(bookInfo.getNovelid());//书籍ID
            cpBook.setName(bookInfo.getNovelname());//书籍名称
            cpBook.setAuthor(bookInfo.getAuthorname());//作者名称
            cpBook.setBrief(bookInfo.getNovelintroshort());//简介
            cpBook.setCompleteStatus(String.valueOf(bookInfo.getNovelstep() == 2 ? 1 : 0));//书籍更新状态
            cpBook.setCover(bookInfo.getCoverurl());//封面URL
            cpBook.setCategory(bookInfo.getNovelclass());//分类
        } catch (Throwable e) {
            log.error("晋江文学城书籍信息格式错误" + e.getMessage(), e);
        }
        return cpBook;
    }

    /**
     * 章节分成1卷
     *
     * @param jingJiangWenXueChengChapterList
     * @return
     */
    private List<CPVolume> packVolumeListForKanShuWang(List<JingJiangWenXueChengChapterList> jingJiangWenXueChengChapterList) {
        List<CPVolume> listCPVolume = null;
        //章节列表不为空，进行卷封闭
        if (CollectionUtils.isNotEmpty(jingJiangWenXueChengChapterList)) {
            listCPVolume = new ArrayList<CPVolume>();
            CPVolume cPVolume = null;
            //设置卷里书籍ID
            for (JingJiangWenXueChengChapterList JingJiangWenXueChengChapter : jingJiangWenXueChengChapterList) {
                int chaptertype = JingJiangWenXueChengChapter.getChaptertype();
                //卷在章节前面，先出卷，再出章节
                if (chaptertype == 1) {//该条记录为卷
                    cPVolume = new CPVolume();
                    cPVolume.setBookId(JingJiangWenXueChengChapter.getNovelid());//Cp方书籍ID
                    cPVolume.setId(JingJiangWenXueChengChapter.getChapterid());//卷ID
                    cPVolume.setName(JingJiangWenXueChengChapter.getChaptername());//卷名称
                    listCPVolume.add(cPVolume);
                } else {//为章节
                    if (cPVolume == null) {//Cp方没有分卷的情况下
                        cPVolume = new CPVolume();
                        cPVolume.setId("1");
                        cPVolume.setName("第一卷");
                        cPVolume.setBookId(JingJiangWenXueChengChapter.getNovelid());//Cp方书籍ID
                        listCPVolume.add(cPVolume);
                    }
                    CPChapter cpChapter = new CPChapter();
                    cpChapter.setId(JingJiangWenXueChengChapter.getChapterid());
                    cpChapter.setName(JingJiangWenXueChengChapter.getChaptername());
                    cPVolume.getChapterList().add(cpChapter);
                }
            }

        }
        return listCPVolume;
    }

    /**
     * 网络获取章节列表
     *
     * @param url    网络获取章节列表url
     * @param cp     cp配置信息
     * @param bookId 根据书籍ID，从网络接口获取数据
     * @return
     */
    private List<JingJiangWenXueChengChapterList> getSourceJJWXCChapterList(String url, Partner cp, String bookId) {
        List<JingJiangWenXueChengChapterList> list = null;
        JingJiangWenXueChengResponseInfo respInfo = null;
        try {
            //请求CP外网访问接口，获取图书章节列表
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            XStream stream = new XStream(new DomDriver());
            try {
                //XML转换成javabean对象
                stream.ignoreUnknownElements();
                stream.aliasType("document", JingJiangWenXueChengResponseInfo.class);
                stream.aliasField("errorCode", JingJiangWenXueChengResponseInfo.class, "errorCode");
                //读XML是是否存在,error节点，未有error节点，会报异常。未出现异常证明没有错误代码，有正常结果返回。
                respInfo = (JingJiangWenXueChengResponseInfo) stream.fromXML(resultStr);
                if (respInfo == null || !"".equals(respInfo.getErrorCode())) {
                    if (respInfo != null) {
                        log.info("晋江文学城chapterList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + ",error=" + respInfo.getErrorInfo() + "】");
                    } else {
                        log.info("晋江文学城 chapterList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + "】");
                    }
                    return null;
                }
            } catch (Exception e) {//未存在error错误节点。有正常章节返回

                stream = new XStream(new DomDriver());
                stream.ignoreUnknownElements();
                stream.aliasType("document", JingJiangWenXueChengResponseInfo.class);
                stream.alias("item", JingJiangWenXueChengChapterList.class);
                stream.addImplicitCollection(JingJiangWenXueChengResponseInfo.class, "chapterList");
                stream.aliasField("novelid", JingJiangWenXueChengChapterList.class, "novelid");
                stream.aliasField("chaptertype", JingJiangWenXueChengChapterList.class, "chaptertype");
                stream.aliasField("chapterid", JingJiangWenXueChengChapterList.class, "chapterid");
                stream.aliasField("chaptername", JingJiangWenXueChengChapterList.class, "chaptername");
                stream.aliasField("chapterdate", JingJiangWenXueChengChapterList.class, "chapterdate");
                stream.aliasField("chaptersize", JingJiangWenXueChengChapterList.class, "chaptersize");
                stream.aliasField("chapterurl", JingJiangWenXueChengChapterList.class, "chapterurl");
                stream.aliasField("chapterclick", JingJiangWenXueChengChapterList.class, "chapterclick");
                stream.aliasField("vipflag", JingJiangWenXueChengChapterList.class, "vipflag");

                respInfo = (JingJiangWenXueChengResponseInfo) stream.fromXML(resultStr);
            }

            if (respInfo.getChapterList() == null || respInfo.getChapterList().size() == 0) {
                log.info("晋江文学城【error result Chapter List is null】网络需要获取bookId=" + bookId);
                return null;
            }
            list = respInfo.getChapterList();
        } catch (Throwable e) {
            log.error("晋江文学城列表格式错误 !!" + e.getMessage(), e);
            return null;
        }
        return list;
    }

    /**
     * 网络获取章节内容信息
     *
     * @param url       cp章节内容请求url
     * @param cp        cp配置信息
     * @param bookId    需要获取书籍图书ID
     * @param chapterId 需要获取书籍内容的章节ID
     * @return
     */
    private CPChapter getSourceJJWXCChapterWithContent(String url, Partner cp, String bookId, String chapterId) {
        CPChapter zyChapter = null;
        JingJiangWenXueChengResponseInfo respInfo = null;
        JingJiangWenXueChengChapterContent jingJiangWenXueChengChapterContent = null;
        try {
            //获取响应数据
            //请求CP外网访问接口，获取书籍章节内容
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            XStream stream = new XStream(new DomDriver());
            try {
                stream.ignoreUnknownElements();
                stream.aliasType("document", JingJiangWenXueChengResponseInfo.class);
                stream.aliasField("errorCode", JingJiangWenXueChengResponseInfo.class, "errorCode");
                //读XML是是否存在,error节点，未有error节点，会报异常。未出现异常证明没有错误代码，有正常结果返回。
                respInfo = (JingJiangWenXueChengResponseInfo) stream.fromXML(resultStr);
                if (respInfo == null || !"".equals(respInfo.getErrorCode())) {
                    if (respInfo != null) {
                        log.info("晋江文学城chapterList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + ",bookId=" + bookId + ",error=" + respInfo.getErrorInfo() + "】");
                    } else {
                        log.info("晋江文学城 chapterList【pid=" + cp.getAliasId() + ",bookId=" + bookId + ",cpname=" + cp.getName() + "】");
                    }
                    return null;
                }
            } catch (Exception e) {
                stream = new XStream(new DomDriver());
                stream.ignoreUnknownElements();
                stream.alias("document", JingJiangWenXueChengChapterContent.class);
                stream.aliasField("content", JingJiangWenXueChengChapterContent.class, "content");
                //XML转换成对象
                jingJiangWenXueChengChapterContent = (JingJiangWenXueChengChapterContent) stream.fromXML(resultStr);

                if (jingJiangWenXueChengChapterContent == null) {
                    log.info("晋江文学城【error result Chapter Content is null】网络需要获取bookId=" + bookId + ",chapterId=" + chapterId);
                    return null;
                }
                //得到网络获取章节内容
                if (jingJiangWenXueChengChapterContent.getContent() != null) {
                    zyChapter = new CPChapter();
                    zyChapter.setBookId(bookId);
                    zyChapter.setId(chapterId);
                    zyChapter.setContent(jingJiangWenXueChengChapterContent.getContent().replaceAll("<br>", "\r\n"));
                }
            }
        } catch (Throwable e) {
            log.error("晋江文学城章节" + e.getMessage(), e);
        }
        return zyChapter;
    }


}
