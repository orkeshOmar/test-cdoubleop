package com.dz.coop.common.util;

import com.dz.coop.common.exception.BookException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

/**
 * @author panqz 2018-10-29 5:38 PM
 */

public class CheckUtil extends com.dz.utils.CheckUtil {

    public static void check(Object object) {
        Class aClass = object.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        if (ArrayUtils.isNotEmpty(declaredFields)) {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object val = null;
                try {
                    val = field.get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (field.isAnnotationPresent(NotNull.class)) {
                    if (val == null || val instanceof String && StringUtils.isBlank((String) val)) {
                        throw new BookException(aClass.getSimpleName() + " field=" + field.getName() + " is null");
                    }
                }
            }
        }

    }
}

