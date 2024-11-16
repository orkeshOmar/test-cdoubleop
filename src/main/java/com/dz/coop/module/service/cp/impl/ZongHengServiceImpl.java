package com.dz.coop.module.service.cp.impl;


import com.dz.coop.common.util.DZSignUtils;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.common.util.JsonUtils;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.ZongHengBook;
import com.dz.coop.module.model.ZongHengChapter;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.cp.ClientService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.type.JavaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZongHengServiceImpl implements ClientService {


    private Logger log = Logger.getLogger(this.getClass());

    @Override
    public List<CPBook> getBookList(Partner owchPartner) throws Exception {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("api_key", owchPartner.getApiKey());//百度/纵横 api_key
        parameterMap.put("method", "api.books");
        String sig = getZongHengSign(parameterMap);
        List<CPBook> bookList = getSourceZHBookList(owchPartner.getBookListUrl() + "&api_key=" + owchPartner.getApiKey() + "&sig=" + sig);
        return bookList;
    }

    private List<CPBook> getSourceZHBookList(String url) {
        JavaType javaType = JsonUtils.createCollectionType(HashMap.class, String.class, Object.class);
        List<String> zhBookIdList = null;
        List<CPBook> listZyBook = null;
        Object obj = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            Map<String, Object> map = JsonUtils.fromJson(resultStr, javaType);
            if ((Integer) map.get("code") != 200) {
                log.info("zongheng bookList【code=" + map.get("code") + ",message=" + map.get("message") + "】");
                return null;
            }
            obj = map.get("result") == null ? null : map.get("result");
            if (obj == null) {
                log.info("【error result bookList map is 】" + map);
                return null;
            }
            zhBookIdList = JsonUtils.fromJson(JsonUtils.toJSON(obj), ArrayList.class, String.class);
            if (zhBookIdList != null && zhBookIdList.size() > 0) {
                listZyBook = new ArrayList<CPBook>();
                for (String zhBookId : zhBookIdList) {
                    CPBook zyBook = new CPBook();
                    zyBook.setId(zhBookId);
                    listZyBook.add(zyBook);
                }
            }
        } catch (Throwable e) {
            log.error("纵横书籍列表格式错误 !!" + e.getMessage(), e);
            return null;
        }
        return listZyBook;
    }

    private String getZongHengSign(Map<String, String> parameterMap) {
        if (parameterMap == null || parameterMap.size() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("ur3bqcril30sz71r");
        sb.append(DZSignUtils.linkFileNameNoConnector(parameterMap));
        sb.append("ur3bqcril30sz71r");
        return DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }

    @Override
    public CPBook getBookInfo(Partner owchPartner, String bookId) throws Exception {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("api_key", owchPartner.getApiKey());//百度/纵横 api_key
        parameterMap.put("method", "book");
        parameterMap.put("book_id", bookId);
        String sig = getZongHengSign(parameterMap);
        CPBook zyBook = getSourceZHBookInfo(owchPartner.getBookInfoUrl() + "&book_id=" + bookId + "&api_key=" + owchPartner.getApiKey() + "&sig=" + sig);
        return zyBook;
    }

    private CPBook getSourceZHBookInfo(String url) {
        JavaType javaType = JsonUtils.createCollectionType(HashMap.class, String.class, Object.class);
        ZongHengBook zongHengBook = null;
        CPBook cpBook = null;
        Object obj = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            Map<String, Object> map = JsonUtils.fromJson(resultStr, javaType);
            if ((Integer) map.get("code") != 200) {
                log.info("zongheng bookInfo【code=" + map.get("code") + ",message=" + map.get("message") + "】");
                return null;
            }
            obj = map.get("result") == null ? null : map.get("result");
            if (obj == null) {
                log.info("【error result bookInfo map is】" + map);
                return null;
            }
            zongHengBook = JsonUtils.fromJSON(JsonUtils.toJSON(obj), ZongHengBook.class);
            if (zongHengBook != null) {
                cpBook = new CPBook();
                cpBook.setId(zongHengBook.getBookId());
                cpBook.setName(zongHengBook.getBookName());
                cpBook.setAuthor(zongHengBook.getAuthorName());
                cpBook.setBrief(zongHengBook.getDescription());
                cpBook.setCompleteStatus(zongHengBook.getSerialStatus());
                cpBook.setCover(zongHengBook.getCoverUrl());
                cpBook.setCategory(zongHengBook.getCategoryId());
            }
        } catch (Throwable e) {
            log.error("纵横书籍信息格式错误" + e.getMessage(), e);
        }
        return cpBook;
    }

    @Override
    public List<CPVolume> getVolumeList(Partner owchPartner, String bookId) throws Exception {

        List<CPVolume> listCPVolume = null;
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("api_key", owchPartner.getApiKey());//百度/纵横 api_key
        parameterMap.put("method", "chapter.dir");
        parameterMap.put("book_id", bookId);
        String sig = getZongHengSign(parameterMap);
        String url = owchPartner.getChapterListUrl() + "&book_id=" + bookId + "&api_key=" + owchPartner.getApiKey() + "&sig=" + sig;
        List<ZongHengChapter> list = getSourceZHChapterList(url);
        listCPVolume = packVolumeListForZongHeng(list);
        return listCPVolume;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String volumeId, String chapterId) throws Exception {
        return getCPChapterInfo(owchPartner, cpBookId, chapterId);
    }

    private List<CPVolume> packVolumeListForZongHeng(List<ZongHengChapter> listZHChapter) {
        List<CPVolume> listCPVolume = null;
        if (CollectionUtils.isNotEmpty(listZHChapter)) {
            List<Long> listVolumeId = new ArrayList<Long>();
            Map<Long, String> volumeIdVolumeNameMap = new HashMap<Long, String>();
            for (ZongHengChapter zongHengChapter : listZHChapter) {
                if (!listVolumeId.contains(Long.parseLong(zongHengChapter.getTomeId()))) {
                    listVolumeId.add(Long.parseLong(zongHengChapter.getTomeId()));
                    volumeIdVolumeNameMap.put(Long.parseLong(zongHengChapter.getTomeId()), zongHengChapter.getTomeName());
                }
            }
            listCPVolume = new ArrayList<CPVolume>();
            if (CollectionUtils.isNotEmpty(listVolumeId)) {
                for (int i = 0; i < listVolumeId.size(); i++) {
                    CPVolume cpVolume = new CPVolume();
                    cpVolume.setId(listVolumeId.get(i).toString());//存纵横自有卷id，入库 b_owchcp_volume会用
                    cpVolume.setName(volumeIdVolumeNameMap.get(listVolumeId.get(i)));
                    for (ZongHengChapter zongHengChapter : listZHChapter) {
                        if (listVolumeId.get(i) == Long.parseLong(zongHengChapter.getTomeId())) {
                            CPChapter cpChapter = new CPChapter();
                            cpChapter.setId(zongHengChapter.getChapterId());
                            cpChapter.setName(zongHengChapter.getChapterName());
                            cpVolume.getChapterList().add(cpChapter);
                        }
                    }
                    listCPVolume.add(cpVolume);
                }
            }
        }
        return listCPVolume;
    }

    private List<ZongHengChapter> getSourceZHChapterList(String url) {
        JavaType javaType = JsonUtils.createCollectionType(HashMap.class, String.class, Object.class);
        List<ZongHengChapter> list = null;
        Object obj = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            Map<String, Object> map = JsonUtils.fromJson(resultStr, javaType);
            if ((Integer) map.get("code") != 200) {
                log.info("zongheng volumeList【code=" + map.get("code") + ",message=" + map.get("message") + "】");
                return null;
            }
            obj = map.get("result") == null ? null : map.get("result");
            if (obj == null) {
                log.info("【error result volumeList map is 】" + map);
                return null;
            }
            JavaType javaType2 = JsonUtils.createCollectionType(ArrayList.class, ZongHengChapter.class);
            list = JsonUtils.fromJson(JsonUtils.toJSON(obj), javaType2);
        } catch (Throwable e) {
            log.error("纵横书籍列表格式错误 !!" + e.getMessage(), e);
            return null;
        }
        return list;
    }

    @Deprecated
    public List<CPVolume> getZhongHengVolumeListV1(Partner owchPartner, String bookId) {
        /**获取本书所有章节信息*/
        String chapterUrl = owchPartner.getChapterListUrl();
        List<CPVolume> listZyVolume = null;
        Long cursor = null;
        List<CPChapter> listChapter = new ArrayList<CPChapter>();
        //获取本书章节总数，与本地做对比，不相等才遍历获取数据
//		List<Book> bookList = bookMapper.getLocalPartnerBookList(owchPartner.getId());
//		for (Book book : bookList) {
//			if(book.getCpBookId().equals(bookId)){
        Integer zongHengChapterNum = getZongHengChapterNum(owchPartner, bookId);
//				int localChapterNum = bookMapper.getChapterNumByBookId(book.getBookId());
//				if(zongHengChapterNum != null && zongHengChapterNum == localChapterNum){
//					log.info("【zongheng==bendi="+localChapterNum+" 章】 There is no update");
//					return null;
//				}
//			}
//		}
        //第一章
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("method", "chapter.first");
        parameterMap.put("book_id", bookId);
        parameterMap.put("api_key", owchPartner.getApiKey());
        String sig = getZongHengSign(parameterMap);
        CPChapter firstChapter = getSourceZHChapterWithContent(chapterUrl + "?method=chapter.first&book_id=" + bookId + "&api_key=" + owchPartner.getApiKey() + "&sig=" + sig);
        if (firstChapter != null) {
            cursor = Long.parseLong(firstChapter.getId());
            listChapter.add(firstChapter);
        }
        //第一章之后的章
        parameterMap = new HashMap<String, String>();
        parameterMap.put("method", "chapter.list");
        parameterMap.put("book_id", bookId);
        parameterMap.put("api_key", owchPartner.getApiKey());
        parameterMap.put("cursor", cursor.toString());
        sig = getZongHengSign(parameterMap);

        List<ZongHengChapter> listZongHengChapter = getSourceZHChapterCycle(chapterUrl + "?method=chapter.list&book_id=" + bookId + "&api_key=" + owchPartner.getApiKey() + "&sig=" + sig + "&cursor=" + cursor);
        while (listZongHengChapter != null && listZongHengChapter.size() > 0) {
            for (ZongHengChapter zongHengChapter : listZongHengChapter) {
                CPChapter zyChapter = new CPChapter();
                zyChapter.setId(zongHengChapter.getChapterId());
                zyChapter.setName(zongHengChapter.getChapterName());
                zyChapter.setVolumeId(zongHengChapter.getTomeId());
                listChapter.add(zyChapter);
            }
            //循环条件： 重置startIndex chapterId
            cursor = Long.parseLong(listZongHengChapter.get(listZongHengChapter.size() - 1).getChapterId());
            parameterMap = new HashMap<String, String>();
            parameterMap.put("method", "chapter.list");
            parameterMap.put("book_id", bookId);
            parameterMap.put("api_key", owchPartner.getApiKey());
            parameterMap.put("cursor", cursor.toString());
            sig = getZongHengSign(parameterMap);
            listZongHengChapter = getSourceZHChapterCycle(chapterUrl + "?method=chapter.list&book_id=" + bookId + "&api_key=" + owchPartner.getApiKey() + "&sig=" + sig + "&cursor=" + cursor);
        }
        /**基于本书 listChapter 封装成  List<ZYVolume>
         * eg：根据tomeId排序Collections.sort是错误的，按照章节列表获取顺序附带的volumeId是实际顺序
         * */
        List<Long> listVolumeId = new ArrayList<Long>();
        if (listChapter.size() > 0) {
            for (CPChapter zyChapter : listChapter) {
                if (!listVolumeId.contains(Long.parseLong(zyChapter.getVolumeId()))) {
                    listVolumeId.add(Long.parseLong(zyChapter.getVolumeId()));
                }
            }
        }
        listZyVolume = new ArrayList<CPVolume>();
//		System.out.println("--listVolumeId  sort--"+listVolumeId);
        if (listVolumeId.size() > 0) {
            for (int i = 0; i < listVolumeId.size(); i++) {
                CPVolume zyVolume = new CPVolume();
                zyVolume.setId(listVolumeId.get(i).toString());//存纵横自有卷id，入库 b_owchcp_volume会用
                zyVolume.setName("第" + (i + 1) + "卷");
                for (CPChapter zyChapter : listChapter) {
                    if (listVolumeId.get(i) == Long.parseLong(zyChapter.getVolumeId())) {
                        zyVolume.getChapterList().add(zyChapter);
                    }
                }
                listZyVolume.add(zyVolume);
            }
        }
        return listZyVolume;
    }

    @Deprecated
    private Integer getZongHengChapterNum(Partner owchPartner, String bookId) {
        Integer chapterNum = null;
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("api_key", owchPartner.getApiKey());//百度/纵横 api_key
        parameterMap.put("method", "chapter.count");
        parameterMap.put("book_id", bookId);
        String sig = getZongHengSign(parameterMap);
        String url = owchPartner.getChapterListUrl() + "?method=chapter.count&book_id=" + bookId + "&api_key=" + owchPartner.getApiKey() + "&sig=" + sig;
        try {
            String strResult = HttpUtil.sendGet(url);
            JavaType javaType = JsonUtils.createCollectionType(HashMap.class, String.class, Object.class);
            Map<String, Object> map = JsonUtils.fromJson(strResult, javaType);
            if ((Integer) map.get("code") != 200) {
                log.info("zongheng chapterInfo【code=" + map.get("code") + ",message=" + map.get("message") + "】");
                return null;
            }
            chapterNum = JsonUtils.fromJSON(JsonUtils.toJSON(map.get("result")), Integer.class);
        } catch (Throwable e) {
            log.error("纵横章节总数" + e.getMessage(), e);
        }
        return chapterNum;
    }

    private CPChapter getSourceZHChapterWithContent(String url) {
        JavaType javaType = JsonUtils.createCollectionType(HashMap.class, String.class, Object.class);
        CPChapter zyChapter = null;
        Object obj = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            Map<String, Object> map = JsonUtils.fromJson(resultStr, javaType);
            if ((Integer) map.get("code") != 200) {
                log.info("zongheng chapterInfo【code=" + map.get("code") + ",message=" + map.get("message") + "】");
                return null;
            }
            obj = map.get("result") == null ? null : map.get("result");
            if (obj == null) {
                log.info("【error result chapterInfo map is 】" + map);
                return null;
            }
            ZongHengChapter zongHengChapter = JsonUtils.fromJSON(JsonUtils.toJSON(obj), ZongHengChapter.class);
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

    private List<ZongHengChapter> getSourceZHChapterCycle(String url) {
        JavaType javaType = JsonUtils.createCollectionType(HashMap.class, String.class, Object.class);
        Object obj = null;
        try {
            String resultStr = HttpUtil.sendGet(url);
            Map<String, Object> map = JsonUtils.fromJson(resultStr, javaType);
            if ((Integer) map.get("code") != 200) {
                log.info("zongheng chapterList【code=" + map.get("code") + ",message=" + map.get("message") + "】");
                return null;
            }
            JavaType chapterJavaType = JsonUtils.createCollectionType(ArrayList.class, ZongHengChapter.class);
            obj = map.get("result") == null ? null : map.get("result");
            if (obj == null) {
                log.info("【error result chapterList map is 】" + map);
                return null;
            }
            return JsonUtils.fromJson(JsonUtils.toJSON(obj), chapterJavaType);
        } catch (Throwable e) {
            log.error("纵横某章节列表出错" + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public CPChapter getCPChapterInfo(Partner owchPartner, String bookId, String chapterId) throws Exception {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("api_key", owchPartner.getApiKey());//百度/纵横 api_key
        parameterMap.put("method", "chapter");
        parameterMap.put("book_id", bookId);
        parameterMap.put("chapter_id", chapterId);
        String sig = getZongHengSign(parameterMap);
        CPChapter zyChapter = getSourceZHChapterWithContent(owchPartner.getChapterInfoUrl() + "&book_id=" + bookId + "&chapter_id=" + chapterId + "&api_key=" + owchPartner.getApiKey() + "&sig=" + sig);
        return zyChapter;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.ZONG_HENG};
    }
}
