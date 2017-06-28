package com.techscan.dvq.common;

import java.text.SimpleDateFormat;

/**
 * Created by liuya on 2017/6/21.
 * π§æﬂ¿‡
 */

public class Utils {
    public static final int HANDER_DEPARTMENT = 1;
    public static final int HANDER_STORG = 2;
    public static final int HANDER_SAVE_RESULT = 3;

    public static String formatTime(long time) {
        java.util.Date date = new java.util.Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}
