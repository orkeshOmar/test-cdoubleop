package com.dz.coop.module.mapper;


import com.dz.coop.module.model.BookTypeTwo;

import java.util.List;
import java.util.Map;

/**
 * @author panqz 2018-12-12 4:09 PM
 */

public interface BookTypeTwoMapper {

    BookTypeTwo getBookTypeTwo(Integer id);

    List<Map<String, String>> getAudioTypeTwoMap();

    List<Map<String, String>> getAudioTypeThreeMap();

}
