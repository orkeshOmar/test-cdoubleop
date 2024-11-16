package com.dz.coop.module.service.cp.impl;

import com.dz.coop.common.exception.BookException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
public class ZhongWenServiceImpl implements ClientService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    //不更新外传的书籍
    public static final ArrayList<String> NO_UPDATE_WAIZHUAN_BOOK = new ArrayList<String>() {{
        add("60681523");
    }};

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        String cpid = owchPartner.getApiKey();
        //格式化URL
        String url = MessageFormat.format(owchPartner.getBookListUrl(), cpid);
        List<CPBook> bookList = getSourceZhongWenBookList(url, owchPartner);
        return bookList;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookid) throws Exception {
        String cpid = owchPartner.getApiKey();
        //格式化URL
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), cpid, bookid);
        //获取书籍基本信息
        CPBook zyBook = getSourceZhongWenBookInfo(url, owchPartner, bookid);
        return zyBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookid) throws Exception {
        List<CPVolume> listCPVolume = null;
        String cpid = owchPartner.getApiKey();
        String url = MessageFormat.format(owchPartner.getChapterListUrl(), cpid, bookid);
        List<ZhongWenVolume> list = getSourceZhongWenChapterList(url, owchPartner, bookid);
        listCPVolume = packVolumeListForZhongWen(list, bookid);
        return listCPVolume;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String bookid, String chapterid) throws Exception {
        String cpid = owchPartner.getApiKey();
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), cpid, bookid, chapterid);
        CPChapter zyChapter = getSourceZhongWenChapterWithContent(url, owchPartner, bookid, chapterid);
        return zyChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.ZHONG_WEN};
    }

    /**
     * 获得CP书籍列表
     *
     * @param url 网络获取书籍的url
     * @param cp  CP配置信息
     * @return
     */
    private List<CPBook> getSourceZhongWenBookList(String url, Partner cp) {
        List<CPBook> listZyBook = null;
        ZhongWenResponseInfo respInfo = null;
        try {
            //请求CP外网访问接口，获取图书列表
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            //XML转换成javabean对象
            XStream stream = new XStream(new DomDriver());
            stream.ignoreUnknownElements();
            stream.alias("datas", ZhongWenResponseInfo.class);
            stream.alias("item", ZhongWenBook.class);
            stream.addImplicitCollection(ZhongWenResponseInfo.class, "books");
            stream.aliasField("id", ZhongWenBook.class, "id");
            stream.aliasField("bookname", ZhongWenBook.class, "bookname");
            respInfo = (ZhongWenResponseInfo) stream.fromXML(resultStr);
            if (respInfo.getBooks() == null || respInfo.getBooks().size() == 0) {
                log.info(url + "【error result bookList is 】" + (respInfo.getBookInfo() == null ? null : respInfo.getBooks().size()));
                return null;
            }
            List<ZhongWenBook> books = respInfo.getBooks();//书籍列表
            listZyBook = new ArrayList<CPBook>();
            for (ZhongWenBook book : books) {
                CPBook zyBook = new CPBook();
                zyBook.setId(book.getId());//书籍ID
                zyBook.setName(book.getBookname());//书籍名称
                listZyBook.add(zyBook);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            log.error("url=" + url + "中文新接口书籍列表格式错误 !!" + e.getMessage(), e);
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
    private CPBook getSourceZhongWenBookInfo(String url, Partner cp, String bookId) {
        ZhongWenBookInfo zhongWenBookInfo = null;
        CPBook cpBook = null;
        try {
            //请求CP外网访问接口，获取书籍基本信息
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            XStream stream = new XStream(new DomDriver());
            stream.ignoreUnknownElements();
            stream.alias("data", ZhongWenBookInfo.class);
            stream.aliasField("cname", ZhongWenBookInfo.class, "cname");
            stream.aliasField("bookname", ZhongWenBookInfo.class, "bookname");
            stream.aliasField("bookid", ZhongWenBookInfo.class, "bookid");
            stream.aliasField("bookpic", ZhongWenBookInfo.class, "bookpic");
            stream.aliasField("zzjs", ZhongWenBookInfo.class, "zzjs");
            stream.aliasField("authorname", ZhongWenBookInfo.class, "authorname");
            stream.aliasField("createtime", ZhongWenBookInfo.class, "createtime");
            stream.aliasField("bksize", ZhongWenBookInfo.class, "bksize");
            stream.aliasField("wordcount", ZhongWenBookInfo.class, "wordcount");
            stream.aliasField("weekvisit", ZhongWenBookInfo.class, "weekvisit");
            stream.aliasField("monthvisit", ZhongWenBookInfo.class, "monthvisit");
            stream.aliasField("allvisit", ZhongWenBookInfo.class, "allvisit");
            stream.aliasField("writestatus", ZhongWenBookInfo.class, "writestatus");
            stream.aliasField("license", ZhongWenBookInfo.class, "license");
            stream.aliasField("wholeprice", ZhongWenBookInfo.class, "wholeprice");
            stream.aliasField("booktype", ZhongWenBookInfo.class, "booktype");

            zhongWenBookInfo = (ZhongWenBookInfo) stream.fromXML(resultStr);

            if (zhongWenBookInfo == null || StringUtils.isEmpty(zhongWenBookInfo.getBookid())) {
                log.info(url + "【error result bookinfo is null】网络需要获取bookId=" + bookId);
                return null;
            }
            cpBook = new CPBook();
            cpBook.setId(zhongWenBookInfo.getBookid());
            cpBook.setName(zhongWenBookInfo.getBookname());
            cpBook.setAuthor(zhongWenBookInfo.getAuthorname());
            cpBook.setBrief(zhongWenBookInfo.getZzjs());
            if (zhongWenBookInfo.getWritestatus() != null && "1".equals(zhongWenBookInfo.getWritestatus())) {
                cpBook.setCompleteStatus(zhongWenBookInfo.getWritestatus());//完本状态
            } else {
                cpBook.setCompleteStatus("0");
            }
            cpBook.setCover(zhongWenBookInfo.getBookpic());
            cpBook.setCategory(zhongWenBookInfo.getCname());//所属分类
//            cpBook.setTotalChapterNum(Integer.parseInt(zhongWenBookInfo.getBksize()));//总章节数
        } catch (Throwable e) {
            log.error("url=" + url + "中文新接口书籍信息格式错误" + e.getMessage(), e);
        }
        return cpBook;
    }

    /**
     * @param zhongWenVolumeList
     * @param bookId
     * @return
     */
    private List<CPVolume> packVolumeListForZhongWen(List<ZhongWenVolume> zhongWenVolumeList, String bookId) {
        List<CPVolume> listCPVolume = null;
        //章节列表不为空，进行卷封闭
        if (CollectionUtils.isNotEmpty(zhongWenVolumeList)) {
            Collections.sort(zhongWenVolumeList, new Comparator<ZhongWenVolume>() {
                @Override
                public int compare(ZhongWenVolume o1, ZhongWenVolume o2) {
                    return o1.getVolumeorder() - o2.getVolumeorder();
                }
            });//对卷进行排序

            listCPVolume = new ArrayList<CPVolume>();
            for (ZhongWenVolume zhongWenVolume : zhongWenVolumeList) {//接口卷格式转为本地格式
                List<ZhongWenChapter> zhongWenChapterList = zhongWenVolume.getChapter();
                if (zhongWenChapterList != null && zhongWenChapterList.size() > 0) {

                    CPVolume cPVolume = new CPVolume();
                    List<CPChapter> chapterlist = new ArrayList<CPChapter>();

                    for (ZhongWenChapter zhongWenChapter : zhongWenChapterList) {
                        CPChapter cpChapter = new CPChapter();
                        cpChapter.setId(zhongWenChapter.getChapterid());//章节ID
                        cpChapter.setName(zhongWenChapter.getChaptername());//章节名称
                        cpChapter.setVolumeId(zhongWenVolume.getVolumecode());//卷ID
                        cpChapter.setBookId(bookId);//书籍ID
                        chapterlist.add(cpChapter);
                    }
                    cPVolume.setBookId(bookId);//设置书籍ID
                    cPVolume.setChapterList(chapterlist);//设置章节集合
                    cPVolume.setId(zhongWenVolume.getVolumecode());//设置卷ID
                    cPVolume.setName(zhongWenVolume.getVolumename());//设置卷名称
                    listCPVolume.add(cPVolume);
                }
            }

        }
        return listCPVolume;
    }

    /**
     * 网络获取章节列表
     * @param url    网络获取章节列表url
     * @param cp     cp配置信息
     * @param bookId 根据书籍ID，从网络接口获取数据
     * @return
     */
    private List<ZhongWenVolume> getSourceZhongWenChapterList(String url, Partner cp, String bookId) {
        List<ZhongWenVolume> list = null;
        ZhongWenResponseInfo respInfo = null;
        try {
            //请求CP外网访问接口，获取图书章节列表
            String resultStr = HttpUtil.sendGet(url);
            if (resultStr == null || "".equals(resultStr)) {
                return null;
            }
            XStream stream = new XStream(new DomDriver());

            stream.ignoreUnknownElements();
            stream.alias("data", ZhongWenResponseInfo.class);
            stream.alias("vol", ZhongWenVolume.class);
            stream.alias("chapter", ZhongWenChapter.class);
            stream.addImplicitCollection(ZhongWenResponseInfo.class, "volumes");
            stream.addImplicitCollection(ZhongWenVolume.class, "chapter");

            stream.aliasField("vol", ZhongWenResponseInfo.class, "volumes");
            stream.aliasField("volumename", ZhongWenVolume.class, "volumename");
            stream.aliasField("volumecode", ZhongWenVolume.class, "volumecode");
            stream.aliasField("volumeorder", ZhongWenVolume.class, "volumeorder");
            stream.aliasField("chapter", ZhongWenVolume.class, "chapter");
            stream.aliasField("url", ZhongWenChapter.class, "url");
            stream.aliasField("chaptername", ZhongWenChapter.class, "chaptername");
            stream.aliasField("chapterid", ZhongWenChapter.class, "chapterid");
            stream.aliasField("license", ZhongWenChapter.class, "license");
            respInfo = (ZhongWenResponseInfo) stream.fromXML(resultStr);

            if (respInfo.getVolumes() == null || respInfo.getVolumes().size() == 0) {
                log.info(url + "【error result Chapter List is null】网络需要获取bookId=" + bookId);
                return null;
            }
            list = respInfo.getVolumes();
            Iterator<ZhongWenVolume> iterator = list.iterator();
            while (iterator.hasNext()) {
                ZhongWenVolume volume = iterator.next();
                if ("作品相关".equals(volume.getVolumename())) {
                    iterator.remove();
                }
                if (StringUtils.equals("外传", volume.getVolumename()) && NO_UPDATE_WAIZHUAN_BOOK.contains(bookId)) {
                    iterator.remove();
                }
            }
        } catch (Throwable e) {
            log.error("url=" + url + "中文在线新接口列表格式错误 !!" + e.getMessage(), e);
            return null;
        }
        return list;
    }

    /**
     * 网络获取章节内容信息
     * @param url       cp章节内容请求url
     * @param cp        cp配置信息
     * @param bookId    需要获取书籍图书ID
     * @param chapterId 需要获取书籍内容的章节ID
     * @return
     */
    private CPChapter getSourceZhongWenChapterWithContent(String url, Partner cp, String bookId, String chapterId) {
        CPChapter zyChapter = null;

        //获取响应数据
        //请求CP外网访问接口，获取书籍章节内容
        String resultStr = HttpUtil.sendGet(url);
        if (StringUtils.isBlank(resultStr)) {
            throw new BookException("[中文在线]章节内容接口返回为空");
        }

        XStream stream = new XStream(new DomDriver());

        stream = new XStream(new DomDriver());
        stream.ignoreUnknownElements();
        stream.alias("content", String.class);
        //XML转换成对象
        String content = (String) stream.fromXML(resultStr);

        //得到网络获取章节内容
        zyChapter = new CPChapter();
        zyChapter.setId(chapterId);//章节ID
        zyChapter.setContent(content);//章节内容
        zyChapter.setBookId(bookId);//书籍ID

        return zyChapter;
    }

}
