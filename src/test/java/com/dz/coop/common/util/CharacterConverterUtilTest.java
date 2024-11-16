package com.dz.coop.common.util;

import com.dz.coop.TestBase;
import org.junit.Test;

public class CharacterConverterUtilTest extends TestBase {

    @Test
    public void convertchar2number() {
        String str = "xroZW0";
        String newStr = CharacterConverterUtil.convertchar2number(str);
        System.out.println(newStr);
    }

}