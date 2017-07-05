package com.techscan.dvq.common;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;

/**
 * Created by liuya on 2017/6/21.
 * π§æﬂ¿‡
 */

public class Utils {
    public static final int HANDER_DEPARTMENT = 1;
    public static final int HANDER_STORG = 2;
    public static final int HANDER_SAVE_RESULT = 3;
    public static final int HANDER_POORDER_HEAD= 4;
    public static final int HANDER_POORDER_BODY= 5;

    public static String formatTime(long time) {
        java.util.Date date = new java.util.Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
