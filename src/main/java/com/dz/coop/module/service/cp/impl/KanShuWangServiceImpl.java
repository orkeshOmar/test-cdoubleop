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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class KanShuWangServiceImpl implements ClientService {

    private Logger log = Logger.getLogger(this.getClass());

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String cono = owchPartner.getAliasId();
        //格式化URL
        String url = MessageFormat.format(owchPartner.getBookListUrl(), String.valueOf(cono));
        List<CPBook> bookList = getSourceKanShuWangBookList(url, owchPartner);
        return bookList;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        String cono = owchPartner.getAliasId();
        //格式化URL
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), String.valueOf(cono), bookId);
        //获取书籍基本信息
        CPBook zyBook = getSourceKanShuWangBookInfo(url, owchPartner, bookId);
        return zyBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {
        List<CPVolume> listCPVolume = null;
        String cono = owchPartner.getAliasId();
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), String.valueOf(cono), bookId);
        List<KanShuWangChapterList> list = getSourceKanShuWangChapterList(url, owchPartner, bookId);
        listCPVolume = packVolumeListForKanShuWang(list, bookId);
        return listCPVolume;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String bookId, String chapterId) throws Exception {
        String cono = owchPartner.getAliasId();
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), String.valueOf(cono), bookId, chapterId);
        CPChapter zyChapter = getSourceKanShuWangChapterWithContent(url, owchPartner, bookId, chapterId);
        return zyChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.SHU_XIANG, ThirdPart.DAN_DING, ThirdPart.JU_DIAN};
    }

    /**
     * 获得CP书籍列表
     *
     * @param url 网络获取书籍的url
     * @param cp  CP配置信息
     * @return
     */
    private List<CPBook> getSourceKanShuWangBookList(String url, Partner cp) {
        List<CPBook> listZyBook = null;
        KanShuWangResponseInfo respInfo = null;
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
                stream.alias("error", KanShuWangResponseInfo.class);
                stream.aliasField("code", KanShuWangResponseInfo.class, "code");
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);
                if (respInfo == null || respInfo.getCode() != null || !"".equals(respInfo.getCode())) {
                    if (respInfo != null) {
                        log.info("看书网 bookList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + ",error=" + respInfo.getErrorInfo() + "】");
                    } else {
                        log.info("看书网 bookList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + "】");
                    }
                    return null;
                }
            } catch (Exception e) {
                //未有错误，对XML重新进行对象转换
                stream = new XStream(new DomDriver());
                stream.ignoreUnknownElements();
                stream.alias("books", KanShuWangResponseInfo.class);
                stream.alias("book", KanShuWangBook.class);
                stream.addImplicitCollection(KanShuWangResponseInfo.class, "books");
                stream.aliasField("id", KanShuWangBook.class, "id");
                stream.aliasField("bookName", KanShuWangBook.class, "bookName");
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);
                if (respInfo.getBooks() == null || respInfo.getBooks().size() == 0) {
                    log.info("【error result bookList is 】" + (respInfo.getBookInfo() == null ? null : respInfo.getBooks().size()));
                    return null;
                }
                List<KanShuWangBook> books = respInfo.getBooks();//书籍列表
                listZyBook = new ArrayList<CPBook>();
                for (KanShuWangBook book : books) {
                    CPBook zyBook = new CPBook();
                    zyBook.setId(book.getId() + "");
                    zyBook.setName(book.getBookName());
                    listZyBook.add(zyBook);
                }
            }
        } catch (Throwable e) {
            log.error("看书网书籍列表格式错误 !!" + e.getMessage(), e);
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
    private CPBook getSourceKanShuWangBookInfo(String url, Partner cp, String bookId) {
        KanShuWangResponseInfo respInfo = null;
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
                stream.alias("error", KanShuWangResponseInfo.class);
                stream.aliasField("code", KanShuWangResponseInfo.class, "code");
                //读XML是是否存在,error节点，未有error节点，会报异常。未出现异常证明没有错误代码，有正常结果返回。
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);
                //判断XML是否存在,error节点，如果存在，证明请求服务器有错误信息
                if (respInfo == null || respInfo.getCode() != null || !"".equals(respInfo.getCode())) {
                    if (respInfo != null) {
                        log.info("看书网 bookInfo【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + ",error=" + respInfo.getErrorInfo() + "】");
                    } else {
                        log.info("看书网 bookInfo【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + "】");
                    }
                    return null;
                }
            } catch (Exception e) {//未有错误代码，进行数据处理
                stream = new XStream(new DomDriver());
                stream.ignoreUnknownElements();
                stream.alias("books", KanShuWangResponseInfo.class);
                stream.aliasField("bookinfo", KanShuWangResponseInfo.class, "bookInfo");
                stream.aliasField("id", KangShuWangBookInfo.class, "id");
                stream.aliasField("bookName", KangShuWangBookInfo.class, "bookName");
                stream.aliasField("subTitle", KangShuWangBookInfo.class, "subTitle");
                stream.aliasField("detail", KangShuWangBookInfo.class, "detail");
                stream.aliasField("bookType", KangShuWangBookInfo.class, "bookType");
                stream.aliasField("keyWord", KangShuWangBookInfo.class, "keyWord");
                stream.aliasField("bookStatus", KangShuWangBookInfo.class, "bookStatus");
                stream.aliasField("size", KangShuWangBookInfo.class, "size");
                stream.aliasField("author", KangShuWangBookInfo.class, "author");
                stream.aliasField("isVip", KangShuWangBookInfo.class, "isVip");
                stream.aliasField("weekVisit", KangShuWangBookInfo.class, "weekVisit");
                stream.aliasField("monthVisit", KangShuWangBookInfo.class, "monthVisit");
                stream.aliasField("allVisit", KangShuWangBookInfo.class, "allVisit");
                stream.aliasField("bookTypeName", KangShuWangBookInfo.class, "bookTypeName");
                stream.aliasField("imagePath", KangShuWangBookInfo.class, "imagePath");
                stream.aliasField("isFee", KangShuWangBookInfo.class, "isFree");
                stream.aliasField("maxFree", KangShuWangBookInfo.class, "maxFree");
                stream.aliasField("price", KangShuWangBookInfo.class, "price");
                stream.aliasField("chapterCount", KangShuWangBookInfo.class, "chapterCount");
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);
            }

            if (respInfo.getBookInfo() == null || StringUtils.isEmpty(respInfo.getBookInfo().getId())) {
                log.info("【error result bookinfo is null】网络需要获取bookId=" + bookId);
                return null;
            }
            KangShuWangBookInfo bookInfo = respInfo.getBookInfo();
            cpBook = new CPBook();
            cpBook.setId(bookInfo.getId());
            cpBook.setName(bookInfo.getBookName());
            cpBook.setAuthor(bookInfo.getAuthor());
            cpBook.setBrief(bookInfo.getDetail());
            cpBook.setCompleteStatus(bookInfo.getBookStatus());
            cpBook.setCover(bookInfo.getImagePath());
            cpBook.setCategory(bookInfo.getBookType());
        } catch (Throwable e) {
            log.error("看书网书籍信息格式错误" + e.getMessage(), e);
        }
        return cpBook;
    }

    /**
     * 章节分成1卷
     *
     * @param kanShuWangChapterList
     * @return
     */
    private List<CPVolume> packVolumeListForKanShuWang(List<KanShuWangChapterList> kanShuWangChapterList, String bookId) {
        List<CPVolume> listCPVolume = null;
        //章节列表不为空，进行卷封闭
        if (CollectionUtils.isNotEmpty(kanShuWangChapterList)) {
            listCPVolume = new ArrayList<CPVolume>();
            CPVolume cPVolume = new CPVolume();
            //设置卷里书籍ID
            cPVolume.setBookId(bookId);
            List<CPChapter> chapterlist = new ArrayList<CPChapter>();
            for (KanShuWangChapterList kangShuWangChapter : kanShuWangChapterList) {
                CPChapter cpChapter = new CPChapter();
                cpChapter.setId(kangShuWangChapter.getChapterId());
                cpChapter.setName(kangShuWangChapter.getChapterName());
                chapterlist.add(cpChapter);
            }
            //卷里填加章节
            cPVolume.setChapterList(chapterlist);
            cPVolume.setName("第1卷");
            cPVolume.setId("1");
            listCPVolume.add(cPVolume);
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
    private List<KanShuWangChapterList> getSourceKanShuWangChapterList(String url, Partner cp, String bookId) {
        List<KanShuWangChapterList> list = null;
        KanShuWangResponseInfo respInfo = null;
        try {
            //请求CP外网访问接口，获取图书章节列表
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            XStream stream = new XStream(new DomDriver());
            try {
                //XML转换成javabean对象
                stream.alias("error", KanShuWangResponseInfo.class);
                stream.aliasField("code", KanShuWangResponseInfo.class, "code");
                //读XML是是否存在,error节点，未有error节点，会报异常。未出现异常证明没有错误代码，有正常结果返回。
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);
                //判断XML是否存在,error节点，如果存在，证明请求服务器有错误信息
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);
                if (respInfo == null || !"".equals(respInfo.getCode())) {
                    if (respInfo != null) {
                        log.info("看书网chapterList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + ",error=" + respInfo.getErrorInfo() + "】");
                    } else {
                        log.info("看书网 chapterList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + "】");
                    }
                    return null;
                }
            } catch (Exception e) {//未存在error错误节点。有正常章节返回

                stream = new XStream(new DomDriver());
                stream.ignoreUnknownElements();
                stream.alias("chapters", KanShuWangResponseInfo.class);
                stream.alias("chapter", KanShuWangChapterList.class);

                stream.addImplicitCollection(KanShuWangResponseInfo.class, "chapterList");
                stream.aliasField("chapter", KanShuWangResponseInfo.class, "chapterList");
                stream.aliasField("lastnum", KanShuWangResponseInfo.class, "lastnum");
                stream.aliasField("chapterId", KanShuWangChapterList.class, "chapterId");
                stream.aliasField("chapterName", KanShuWangChapterList.class, "chapterName");
                stream.aliasField("chapterSize", KanShuWangChapterList.class, "chapterSize");
                stream.aliasField("isVip", KanShuWangChapterList.class, "isVip");
                stream.aliasField("price", KanShuWangChapterList.class, "price");
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);
            }

            if (respInfo.getChapterList() == null || respInfo.getChapterList().size() == 0) {
                log.info("【error result Chapter List is null】网络需要获取bookId=" + bookId);
                return null;
            }
            list = respInfo.getChapterList();
        } catch (Throwable e) {
            log.error("看书网列表格式错误 !!" + e.getMessage(), e);
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
    private CPChapter getSourceKanShuWangChapterWithContent(String url, Partner cp, String bookId, String chapterId) {
        CPChapter zyChapter = null;
        KanShuWangResponseInfo respInfo = null;
        try {
            //获取响应数据
            //请求CP外网访问接口，获取书籍章节内容
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            XStream stream = new XStream(new DomDriver());
            try {
                //XML转换成javabean对象
                stream.alias("error", KanShuWangResponseInfo.class);
                stream.aliasField("code", KanShuWangResponseInfo.class, "code");
                //读XML是是否存在,error节点，未有error节点，会报异常。未出现异常证明没有错误代码，有正常结果返回。
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);
                if (respInfo == null || !"".equals(respInfo.getCode())) {
                    if (respInfo != null) {
                        log.info("看书网chapterList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + ",error=" + respInfo.getErrorInfo() + "】");
                    } else {
                        log.info("看书网 chapterList【pid=" + cp.getAliasId() + ",cpname=" + cp.getName() + "】");
                    }
                    return null;
                }
            } catch (Exception e) {
                stream = new XStream(new DomDriver());
                stream.ignoreUnknownElements();
                stream.alias("chapters", KanShuWangResponseInfo.class);
                stream.aliasField("chapter", KanShuWangResponseInfo.class, "chapter");
                stream.aliasField("chapterid", KanShuWangChapterContent.class, "chapterid");
                stream.aliasField("content", KanShuWangChapterContent.class, "content");
                //XML转换成对象
                respInfo = (KanShuWangResponseInfo) stream.fromXML(resultStr);

                if (respInfo.getChapter() == null) {
                    log.info("【error result Chapter Content is null】网络需要获取bookId=" + bookId + ",chapterId=" + chapterId);
                    return null;
                }
                //得到网络获取章节内容
                KanShuWangChapterContent kanShuWangChapterContent = respInfo.getChapter();
                if (kanShuWangChapterContent != null) {
                    zyChapter = new CPChapter();
                    zyChapter.setId(kanShuWangChapterContent.getChapterid());
                    zyChapter.setVolumeId("1");
                    zyChapter.setContent(kanShuWangChapterContent.getContent());
                }
            }
        } catch (Throwable e) {
            log.error("看书网章节" + e.getMessage(), e);
        }
        return zyChapter;
    }

}
