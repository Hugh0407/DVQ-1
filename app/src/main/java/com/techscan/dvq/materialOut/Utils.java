package com.techscan.dvq.materialOut;

import java.text.SimpleDateFormat;

/**
 * Created by cloverss on 2017/6/21.
 */

public class Utils {
    public static String formatTime(long time) {
        java.util.Date date = new java.util.Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}
