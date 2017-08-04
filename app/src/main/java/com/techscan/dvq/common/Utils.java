package com.techscan.dvq.common;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuya on 2017/6/21.
 * 工具类
 */

public class Utils {

    public static final int HANDER_DEPARTMENT   = 1;
    public static final int HANDER_STORG        = 2;
    public static final int HANDER_SAVE_RESULT  = 3;
    public static final int HANDER_POORDER_HEAD = 4;
    public static final int HANDER_POORDER_BODY = 5;

    public static String formatTime(long time) {
        java.util.Date   date = new java.util.Date(time);
        SimpleDateFormat df   = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * ++++++++++++++
     * 格式化小数，一律格式为两位小数
     *
     * @param num 必须为float类型
     * @return 字符串
     */
    public static String formatDecimal(float num) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String        s             = decimalFormat.format(num);
        return s;
    }

    public static String formatDecimal(double num) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String        s             = decimalFormat.format(num);
        return s;
    }

    public static String formatDecimal(String num) {
        float n = Float.valueOf(num);
        return formatDecimal(n);
    }

    /**
     * 判断是否都是数字，使用正则表达式
     *
     * @param str 待匹配的字符串
     * @return 匹配结果, 是数字返回true, 不是数字返回false
     */
    public static boolean isNumber(@NonNull String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum   = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 显示保存的返回结果的信息
     *
     * @param message dialog内容信息
     */
    public static void showResultDialog(Context context, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("保存信息");
        dialog.setMessage(message);
        dialog.setPositiveButton("关闭", null);
        dialog.show();
    }
}
