package com.dz.coop.module.service.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.ZhangZhongBook;
import com.dz.coop.module.model.ZhangZhongChapter;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.JavaType;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ZhangZhongServiceImpl implements ClientService {

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(1970, 1, 1);//计算机开始日期
        Long begintime = c.getTime().getTime() / 1000;
        Long endtime = System.currentTimeMillis() / 1000;//当前日期
        String url = MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey(), String.valueOf(begintime), String.valueOf(endtime));
        List<CPBook> bookList = getSourceZhangZhongBookList(url, owchPartner);
        return bookList;
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        //格式化URL
        String url = MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId);
        //获取书籍基本信息
        CPBook zyBook = getSourceZhangZhongBookInfo(url, owchPartner, bookId);
        return zyBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner,
                                        String bookId) throws Exception {
        //格式化URL
        List<ZhangZhongChapter> zhangZhongChapterList = getSourceZhangZhongChapterList(owchPartner, bookId);
        return packVolumeListForZhangZhong(zhangZhongChapterList, bookId);
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String bookId,
                                      String chapterId) throws Exception {
        String url = MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), bookId, chapterId);
        CPChapter cpChapter = getSourceZhangZhongChapterWithContent(url, bookId, chapterId);
        return cpChapter;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.ZHANG_ZHONG_WEN_XUE};
    }

    private List<CPBook> getSourceZhangZhongBookList(String url, Partner owchPartner) {
        List<CPBook> listZyBook = new ArrayList<CPBook>();
        String resultStr = HttpUtil.sendGet(url);
        if (StringUtils.isNotBlank(resultStr)) {
            JSONObject jsonObject = JSON.parseObject(resultStr);
            String status = jsonObject.getString("status");
            if (StringUtils.isNotBlank(status)) {
                JSONObject jsonStatus = JSON.parseObject(status);
                String code = jsonStatus.getString("code");
                if (StringUtils.equals(code, "0")) {
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if (jsonData != null) {
                        JSONObject jsonReturndata = jsonData.getJSONObject("returndata");
                        if (jsonReturndata != null) {
                            JavaType javaType = JsonUtils.createCollectionType(ArrayList.class, ZhangZhongBook.class);
                            //读取掌中json书籍列表
                            String jsonBooklist = jsonReturndata.getString("booklist");
                            List<ZhangZhongBook> zhangZhongBookList = JsonUtils.fromJson(jsonBooklist, javaType);
                            //判断书籍列表是否为空
                            if (CollectionUtils.isNotEmpty(zhangZhongBookList)) {
                                for (ZhangZhongBook zhangZhongBook : zhangZhongBookList) {
                                    CPBook cpBook = new CPBook();
                                    cpBook.setId(String.valueOf(zhangZhongBook.getBookid()));
                                    cpBook.setName(zhangZhongBook.getBookname());
                                    listZyBook.add(cpBook);
                                }
                            }
                        }
                    }
                }
            }
        }
        return listZyBook;
    }

    public CPBook getSourceZhangZhongBookInfo(String url, Partner owchPartner, String bookId) {
        CPBook cpBook = new CPBook();
        String resultStr = HttpUtil.sendGet(url);

        if (StringUtils.isNotBlank(resultStr)) {
            JSONObject jsonObject = JSON.parseObject(resultStr);
            String status = jsonObject.getString("status");
            if (StringUtils.isNotBlank(status)) {
                JSONObject jsonStatus = JSON.parseObject(status);
                String code = jsonStatus.getString("code");
                if (StringUtils.equals(code, "0")) {
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if (jsonData != null) {
                        String bookInfo = jsonData.getString("returndata");
                        if (bookInfo != null) {
                            ZhangZhongBook zhangZhongBook = JsonUtils.fromJSON(bookInfo, ZhangZhongBook.class);
                            cpBook.setId(String.valueOf(zhangZhongBook.getBookid()));//书籍ID
                            cpBook.setName(zhangZhongBook.getBookname());//书籍名称
                            cpBook.setAuthor(zhangZhongBook.getAuthorname());//作者
                            cpBook.setBrief(zhangZhongBook.getIntroduce());//简介
                            cpBook.setCompleteStatus(String.valueOf(zhangZhongBook.getWritestatus()));//完本状态   0:未完本 1 :完本
                            cpBook.setCover(zhangZhongBook.getImgsrc());//封面URL
                        }
                    }
                }
            }
        }

        return cpBook;
    }

    private List<ZhangZhongChapter> getSourceZhangZhongChapterList(Partner owchPartner, String bookId) {
        List<ZhangZhongChapter> chapters = new ArrayList<>();
        // 开始页
        int pageIndex = 1;
        // 每页记录数
        int pageSize = 1000;
        String queryUrl = MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), String.valueOf(pageIndex), String.valueOf(pageSize), bookId);
        String resultStr = HttpUtil.sendGet(queryUrl);
        if (StringUtils.isNotBlank(resultStr)) {
            JSONObject jsonObject = JSON.parseObject(resultStr);
            JSONObject status = jsonObject.getJSONObject("status");
            if (status != null) {
                String code = status.getString("code");
                if (StringUtils.equals(code, "0")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data != null) {
                        JSONObject pageinfo = data.getJSONObject("pageinfo");
                        // 总页数
                        int pageTotal = pageinfo.getIntValue("pagetotal");
                        log.info("[掌中文学]cpBookId={},书籍章节列表共有{}页,当前为第1页", bookId, pageTotal);
                        JSONObject returndata = data.getJSONObject("returndata");
                        if (returndata != null) {
                            JavaType javaType = JsonUtils.createCollectionType(ArrayList.class, ZhangZhongChapter.class);
                            chapters = JsonUtils.fromJson(returndata.getString("chapterlist"), javaType);

                            if (pageTotal > pageIndex) {
                                for (pageIndex = 2; pageIndex <= pageTotal; pageIndex++) {
                                    queryUrl = MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), String.valueOf(pageIndex), String.valueOf(pageSize), bookId);
                                    List<ZhangZhongChapter> chapterList = getChapterList(queryUrl);
                                    if (CollectionUtils.isNotEmpty(chapterList)) {
                                        chapters.addAll(chapterList);
                                        log.info("[掌中文学]cpBookId={},pageSize={},获取访问书籍章节列表第{}页成功", bookId, pageSize, pageIndex);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return chapters;
    }

    private List<ZhangZhongChapter> getChapterList(String url) {
        String resultStr = HttpUtil.sendGet(url);
        if (StringUtils.isNotBlank(resultStr)) {
            JSONObject jsonObject = JSON.parseObject(resultStr);
            JSONObject status = jsonObject.getJSONObject("status");
            if (status != null) {
                String code = status.getString("code");
                if (StringUtils.equals(code, "0")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data != null) {
                        JSONObject returndata = data.getJSONObject("returndata");
                        if (returndata != null) {
                            JavaType javaType = JsonUtils.createCollectionType(ArrayList.class, ZhangZhongChapter.class);
                            return JsonUtils.fromJson(returndata.getString("chapterlist"), javaType);
                        }
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * 网络获取章节内容
     *
     * @param url
     * @param bookId
     * @param chapterId
     * @return
     */
    private CPChapter getSourceZhangZhongChapterWithContent(String url, String bookId, String chapterId) {
        CPChapter cpChapter = new CPChapter();
        String resultStr = HttpUtil.sendGet(url);
        if (StringUtils.isNotBlank(resultStr)) {
            cpChapter.setContent(resultStr);
            cpChapter.setBookId(bookId);
            cpChapter.setId(chapterId);
        }
        return cpChapter;
    }


    /**
     * 封装卷
     *
     * @param listZZChapter
     * @param bookId
     * @return
     */
    private List<CPVolume> packVolumeListForZhangZhong(List<ZhangZhongChapter> listZZChapter, String bookId) {
        List<CPVolume> listCPVolume = null;
        if (CollectionUtils.isNotEmpty(listZZChapter)) {
            listCPVolume = new ArrayList<CPVolume>();
            CPVolume cpVolume = new CPVolume();
            cpVolume.setBookId(bookId);
            cpVolume.setName("第一卷");
            cpVolume.setId("1");
            int i = 1;
            for (ZhangZhongChapter zhangZhongChapter : listZZChapter) {
                CPChapter cpChapter = new CPChapter();
                cpChapter.setBookId(String.valueOf(zhangZhongChapter.getBookid()));
                cpChapter.setId(String.valueOf(zhangZhongChapter.getChapterid()));
                cpChapter.setName(zhangZhongChapter.getChaptername());
                if (i <= 20) {
                    cpChapter.setIsFree(0);
                } else {
                    cpChapter.setIsFree(1);
                }
                cpVolume.getChapterList().add(cpChapter);
                i++;
            }
            listCPVolume.add(cpVolume);
        }
        return listCPVolume;
    }
}
