package com.techscan.dvq.common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuya on 2017/6/23.
 */
public class RequestThread implements Runnable {

    private HashMap<String, String> parameter;
    private Handler                 mHandler;
    private int                     msgWhat;

    public RequestThread(HashMap<String, String> parameter, Handler handler, int msgWhat) {
        this.parameter = parameter;
        mHandler = handler;
        this.msgWhat = msgWhat;
    }

    @Override
    public void run() {
        JSONObject para = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : parameter.entrySet()) {
                para.put(entry.getKey(), entry.getValue());
            }
            JSONObject resultJson = Common.DoHttpQuery(para, "CommonQuery", "");
            Message    msg        = Message.obtain();
            msg.what = msgWhat;
            msg.obj = resultJson;
            mHandler.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
