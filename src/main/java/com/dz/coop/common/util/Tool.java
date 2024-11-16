package com.dz.coop.common.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author panqz 2018-11-28 2:11 PM
 */

public class Tool {

    public static <T> List<List<T>> subList(List<T> list, int pageSize) {

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<List<T>> lists = new ArrayList<>();

        int size = list.size();
        if (size <= pageSize) {
            lists.add(list);
            return lists;
        }

        int page = (size + pageSize - 1) / pageSize;
        for (int i = 0; i < page; i++) {
            int start = pageSize * i;
            int end = start + pageSize;
            if (end > size) {
                end = size;
            }
            lists.add(list.subList(start, end));
        }

        return lists;
    }

}
