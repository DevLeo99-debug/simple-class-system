package com.classsystem.backend.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 格式化时间
    public static String format(Date date) {
        if (date == null) return null;
        return FORMAT.format(date);
    }

    // 判断当前时间是否超过截止时间
    public static boolean isExpire(Date deadline) {
        if (deadline == null) return false;
        return new Date().after(deadline);
    }
}