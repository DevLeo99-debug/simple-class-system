package com.classsystem.backend.util;

public class FileCheckUtil {
    // 允许的作业文件后缀
    private static final String[] ALLOW_SUFFIX = {".pdf", ".doc", ".docx", ".zip", ".rar"};
    // 最大 20MB
    private static final long MAX_SIZE = 20 * 1024 * 1024;

    // 校验文件大小
    public static boolean checkSize(long size) {
        return size <= MAX_SIZE;
    }

    // 校验后缀是否合法
    public static boolean checkSuffix(String fileName) {
        if (fileName == null || fileName.isEmpty()) return false;
        String lowerName = fileName.toLowerCase();
        for (String suf : ALLOW_SUFFIX) {
            if (lowerName.endsWith(suf)) {
                return true;
            }
        }
        return false;
    }
}