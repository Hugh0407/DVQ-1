package com.techscan.dvq.common;

import android.os.Handler;
import android.os.Message;

import com.techscan.dvq.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by cloverss on 2017/6/28.
 */

public class SaveThread implements Runnable {

    private JSONObject saveInfo;
    private String interfaceName;
    private Handler mHandler;
    private int msgWhat;

    public SaveThread(JSONObject saveInfo, String interfaceName, Handler handler, int msgWhat) {
        this.saveInfo = saveInfo;
        this.interfaceName = interfaceName;
        mHandler = handler;
        this.msgWhat = msgWhat;
    }

    @Override
    public void run() {
        try {
            JSONObject resultJson = Common.DoHttpQuery(saveInfo, interfaceName, "A");
            Message msg = Message.obtain();
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
