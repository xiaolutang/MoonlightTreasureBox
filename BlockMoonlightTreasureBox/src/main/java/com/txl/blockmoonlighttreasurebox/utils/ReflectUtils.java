package com.txl.blockmoonlighttreasurebox.utils;

import java.lang.reflect.Field;

public class ReflectUtils {
    public static long reflectLongField(Object o,Class c, String fileName,long defaultValue){
        try {
            Field file = c.getDeclaredField(fileName);
            file.setAccessible(true);
            return file.getLong(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
}
