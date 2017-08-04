package com.techscan.dvq;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class writeTxt {

    @NonNull
    String filePath = "/sdcard/DVQ/";
    //String fileName = "DVQSavelog.txt";
 
 // 将字符串写入到文本文件中
    public void writeTxtToFile(String LogName, @NonNull String strcontent) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, LogName);
        
        String strFilePath = filePath+LogName;
        // 每次写入时，都换行写
        //String strContent = strcontent + "\r\n";
        
        String newStr = null;
		try {
			newStr = new String(strcontent.getBytes(), "UTF-8")+ "\r\n";
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        
        //new String((strcontent + "\r\n").getBytes("iso-8859-1"),"UTF-8");
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(newStr.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }
     
    // 生成文件
    @Nullable
    public File makeFilePath(@NonNull String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
     
    // 生成文件夹
    public static void makeRootDirectory(@NonNull String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
	}
}
