package com.techscan.dvq.module.statusChange.tab;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.techscan.dvq.R;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.showToast;

/**
 * Created by cloverss on 2017/8/14.
 */

public class FragmentBefore extends Fragment {


    @InjectView(R.id.ed_encoding)
    EditText edEncoding;
    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.ed_type)
    EditText edType;
    @InjectView(R.id.ed_spectype)
    EditText edSpectype;
    @InjectView(R.id.ed_lot)
    EditText edLot;
    @InjectView(R.id.ed_cost_object)
    EditText edCostObject;
    @InjectView(R.id.ed_num)
    EditText edNum;
    @InjectView(R.id.ed_weight)
    EditText edWeight;
    @InjectView(R.id.ed_qty)
    EditText edQty;
    @InjectView(R.id.ed_unit)
    EditText edUnit;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_before, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        Log.d("TAG", "onMessageEvent: " + str);
        barAnalysis(str);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 3:
                    //条码解析的请求结果，并且把数据设置到UI上
                    JSONObject json = (JSONObject) msg.obj;
                    if (null == json) {
                        Log.d("TAG", "handleMessage: null");
                        return;
                    }
                    setInvBaseToUI(json);
                    break;

            }
        }
    };

    /**
     * 获取存货基本信息
     *
     * @param invcode 物料编码
     */
    private void getInvBaseInfo(String invcode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", invcode);
        parameter.put("TableName", "baseInfo");
        Utils.doRequest(parameter, handler, 3);
    }

    /**
     * 通过获取到的json 解析得到物料信息,并设置到UI上
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    private void setInvBaseToUI(JSONObject json) {
        Log.d("TAG", "setInvBaseToUI: " + json);
        try {
            if (json.getBoolean("Status")) {
                JSONArray               val = json.getJSONArray("baseInfo");
                HashMap<String, Object> map = null;
                for (int i = 0; i < val.length(); i++) {
                    JSONObject tempJso = val.getJSONObject(i);
                    map = new HashMap<String, Object>();
                    map.put("invname", tempJso.getString("invname"));   //橡胶填充油
                    map.put("invcode", tempJso.getString("invcode"));   //00179
                    map.put("measname", tempJso.getString("measname"));   //千克
                    map.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));
                    pk_invbasdoc = tempJso.getString("pk_invbasdoc");
                    map.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));
                    pk_invmandoc = tempJso.getString("pk_invmandoc");
                    map.put("invtype", tempJso.getString("invtype"));   //型号
                    map.put("invspec", tempJso.getString("invspec"));   //规格
                    map.put("oppdimen", tempJso.getString("oppdimen"));   //重量
                }
                if (map != null) {
                    edName.setText(map.get("invname").toString());
                    edUnit.setText(map.get("measname").toString());
                    edType.setText(map.get("invtype").toString());
                    edSpectype.setText(map.get("invspec").toString());
                    edCostObject.setText(map.get("invname").toString());
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean barAnalysis(String bar) {
        bar = bar.trim();
        if (bar.contains("\n")) {
            bar = bar.replace("\n", "");
        }
        edEncoding.setText(bar);
        edEncoding.setSelection(edEncoding.length());//将光标移动到最后的位置
        edEncoding.selectAll();
        SplitBarcode barDecoder = new SplitBarcode(bar);
        if (!barDecoder.creatorOk) {
            showToast(getActivity(), "条码有误");
            return false;
        }
        if (barDecoder.BarcodeType.equals("P")) {
            edNum.setEnabled(true);
//            mEdManual.setEnabled(true);
            String invCode = barDecoder.cInvCode;
            if (invCode.contains(",")) {
                invCode = invCode.split(",")[0];
            }
            edEncoding.setText(invCode);
            getInvBaseInfo(invCode);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[0];
            }
            edLot.setText(batch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edQty.setText("");
            edNum.setText("1");
            edNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
            edNum.setSelection(edNum.length());   //将光标移动到最后的位置
            edQty.setText(String.format("%.2f",barDecoder.dQuantity));
            return true;
        } else if (barDecoder.BarcodeType.equals("TP")) {
//            for (int i = 0; i < detailList.size(); i++) {
//                if (detailList.get(i).getBarcode().equals(bar)) {
//                    showToast(getActivity(), "该托盘已扫描");
//                    SoundHelper.playWarning();
//                    return false;
//                }
//            }
//            mEdManual.setEnabled(true);
//            mEdManual.requestFocus();
            String encoding = barDecoder.cInvCode;
            if (encoding.contains(",")) {
                encoding = encoding.split(",")[0];
            }
            edEncoding.setText(encoding);
            getInvBaseInfo(encoding);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[0];
            }
            edLot.setText(batch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edNum.setText(String.valueOf(barDecoder.iNumber));
            double weight = barDecoder.dQuantity;
            double edNum  = Double.valueOf(barDecoder.iNumber);
            edQty.setText(formatDecimal(weight * edNum));
            return true;
        } else {
            showToast(getActivity(), "条码有误重新输入");
            SoundHelper.playWarning();
            return false;
        }
    }
}
