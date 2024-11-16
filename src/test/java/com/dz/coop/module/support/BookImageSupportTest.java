package com.dz.coop.module.support;

import com.dz.coop.TestBase;
import org.junit.Test;

public class BookImageSupportTest extends TestBase {

    @Test
    public void downloadImage() {
        String bookId = "11000102223";
        String imgUrl = "https://admin.lanmaoyd.com/api/uploads/file/bdde27a08984af62309b0061ee8435ad_20220616133906.jpg";
        BookImageSupport.downloadImage(bookId, imgUrl);
    }
}