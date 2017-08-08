package com.techscan.dvq.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by cloverss on 2017/8/8.
 */

public class RxRequest {
    private HashMap<String, String> parameter;
    JSONObject j;

    public RxRequest(HashMap<String, String> parameter) {
        this.parameter = parameter;
    }

    Observable observable = new Observable() {
        @Override
        protected void subscribeActual(Observer observer) {
            JSONObject resultJson = null;
            try {
                JSONObject para = new JSONObject();
                for (Map.Entry<String, String> entry : parameter.entrySet()) {
                    para.put(entry.getKey(), entry.getValue());
                }
                resultJson = Common.DoHttpQuery(para, "CommonQuery", "");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            observer.onNext(resultJson);
            observer.onComplete();
        }
    };

    Consumer consumer = new Consumer() {
        @Override
        public void accept(Object o) throws Exception {

        }
    };

    public Disposable getInfo() {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    public void doInfo(){
        Disposable info = getInfo();

    }

}
