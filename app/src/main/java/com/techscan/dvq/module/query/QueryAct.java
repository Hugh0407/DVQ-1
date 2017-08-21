package com.techscan.dvq.module.query;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.QryGood;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.showToast;

public class QueryAct extends Activity {

    @Nullable
    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;
    @Nullable
    @InjectView(R.id.ed_name)
    EditText mEdName;
    @Nullable
    @InjectView(R.id.ed_spec)
    EditText mEdSpec;
    @Nullable
    @InjectView(R.id.ed_lot)
    EditText mEdLot;
    @Nullable
    @InjectView(R.id.ed_supplier)
    EditText mEdSupplier;
    @Nullable
    @InjectView(R.id.ed_shelf_life)
    EditText mEdShelfLife;
    @Nullable
    @InjectView(R.id.ed_manual)
    EditText mEdManual;
    @Nullable
    @InjectView(R.id.ed_in_time)
    EditText mEdInTime;
    @Nullable
    @InjectView(R.id.ed_total_num)
    EditText mEdTotalNum;
    @Nullable
    @InjectView(R.id.btn_back)
    Button   mBtnBack;

    @Nullable
    Activity mActivity;
    @InjectView(R.id.lv)
    ListView lv;
    List<QryGood> list;
    QueryAdp      adp;
    SplitBarcode  barDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.inject(this);
        mActivity = this;
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public void onBackPressed() {
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("查询扫描");
        mEdBarCode.setOnKeyListener(new MyOnKeyListener(mEdBarCode));
        mEdBarCode.addTextChangedListener(new CustomTextWatcher(mEdBarCode));
        list = new ArrayList<QryGood>();
        adp = new QueryAdp(list);
        lv.setAdapter(adp);
        lv.setDivider(getResources().getDrawable(R.drawable.lv_divider));
    }

    @Nullable
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setInvBaseToUI((JSONObject) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private boolean barAnalysis() {
        String Bar = mEdBarCode.getText().toString().trim();
        if (Bar.contains("\n")) {
            Bar = Bar.replace("\n", "");
        }
        mEdBarCode.setText(Bar);
        mEdBarCode.setSelection(mEdBarCode.length());   //将光标移动到最后的位置
        mEdBarCode.selectAll();

        barDecoder = new SplitBarcode(Bar);
        if (barDecoder.BarcodeType.equals("C")) {   //C|SKU|LOT|TAX|QTY|SN
            mEdLot.setText(barDecoder.cBatch);
            mEdTotalNum.setText(String.valueOf(barDecoder.dQuantity));
            String invcode = barDecoder.cInvCode;
            if (invcode.contains(",")) {
                invcode = invcode.split(",")[0];
            }
            getInvBaseInfoByBarcode(invcode, barDecoder.cBatch, MainLogin.objLog.STOrgCode,barDecoder.purductBatch);
            return true;
        } else if (barDecoder.BarcodeType.equals("TC")) {    //TC|SKU|LOT|TAX|QTY|NUM|SN
            mEdLot.setText(barDecoder.cBatch);
            double qty = barDecoder.dQuantity;
            double num = barDecoder.iNumber;
            mEdTotalNum.setText(formatDecimal(qty * num));
            String invcode = barDecoder.cInvCode;
            if (invcode.contains(",")) {
                invcode = invcode.split(",")[0];
            }
            getInvBaseInfoByBarcode(invcode, barDecoder.cBatch, MainLogin.objLog.STOrgCode,barDecoder.purductBatch);
            return true;
        } else if (barDecoder.BarcodeType.equals("P")) {// 包码 P|SKU|LOT|WW|TAX|QTY|CW|ONLY|SN    9位
            mEdLot.setText(barDecoder.cBatch);
            mEdTotalNum.setText(String.valueOf(barDecoder.iNumber));
            String invcode = barDecoder.cInvCode;
            if (invcode.contains(",")) {
                invcode = invcode.split(",")[0];
            }
            getInvBaseInfoByBarcode(invcode, barDecoder.cBatch, MainLogin.objLog.STOrgCode,barDecoder.purductBatch);
            return true;
        } else if (barDecoder.BarcodeType.equals("TP")) {//盘码TP|SKU|LOT|WW|TAX|QTY|NUM|CW|ONLY|SN
            mEdLot.setText(barDecoder.cBatch);
            double weight = barDecoder.dQuantity;
            double mEdNum = Double.valueOf(barDecoder.iNumber);
            mEdTotalNum.setText(formatDecimal(weight * mEdNum));
            String invcode = barDecoder.cInvCode;
            if (invcode.contains(",")) {
                invcode = invcode.split(",")[0];
            }
            getInvBaseInfoByBarcode(invcode, barDecoder.cBatch, MainLogin.objLog.STOrgCode,barDecoder.purductBatch);
            return true;
        } else {
            showToast(mActivity, "条码有误,重新输入");
            return false;
        }
    }

    /**
     * 清空所有的Edtext
     */
    private void changeAllEdTextToEmpty() {
        mEdName.setText("");
        mEdSpec.setText("");
        mEdLot.setText("");
        mEdTotalNum.setText("");
        mEdSupplier.setText("");
        mEdInTime.setText("");
        mEdShelfLife.setText("");
        mEdManual.setText("");
        list.clear();
        adp.notifyDataSetChanged();
    }

    /**
     * 获取存货基本信息
     *
     * @param invcode 物料编码
     */
    private void getInvBaseInfoByBarcode(String invcode, String batchcode, String crop, String prdlot) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvInfoByBarcode");
        parameter.put("CompanyCode", MainLogin.objLog.STOrgCode);
        parameter.put("Invcode", invcode);
        parameter.put("BatchCode", batchcode);
        parameter.put("Crop", crop);
        parameter.put("Prdlot", prdlot);
        parameter.put("TableName", "baseInfo");
        Utils.doRequest(parameter, mHandler, 1);
    }


    /**
     * 通过获取到的json 解析得到物料信息,并设置到UI上
     *
     * @param json 网络请求结果
     */

    private void setInvBaseToUI(JSONObject json) {
        Log.d("TAG", "setInvBaseToUI: " + json);
        try {
            if (null == json) {
                showToast(mActivity, "数据访问失败");
                return;
            }
            if (json.getBoolean("Status")) {
                JSONArray val = json.getJSONArray("baseInfo");
                if (val.length() <= 0) {
                    showToast(mActivity, "没有该存货");
                    return;
                }
                JSONObject j = val.getJSONObject(0);
                mEdName.setText(j.getString("invname"));
                mEdSpec.setText(j.getString("invspec"));
                String vfree4 = j.getString("vfree4");
                if (!vfree4.equals("null")) {
                    mEdManual.setText(vfree4);
                }

                QryGood good;
                list.clear();
                for (int i = 0; i < val.length(); i++) {
                    JSONObject jo = val.getJSONObject(i);
                    good = new QryGood();
                    good.storname = jo.getString("storname");
                    good.nonhandnum = jo.getString("nonhandnum");
                    String p = jo.getString("vfree5");
                    if (p.equals("null")) {
                        good.purLot = "";
                    } else {
                        good.purLot = p;
                    }
                    if (barDecoder.purductBatch.equals(p)) {
                        good.isItSelt = true;
                    }
                    list.add(good);
                }
                adp.setList(list);
                adp.notifyDataSetChanged();
                SoundHelper.playOK();
            } else {
                Utils.showToast(mActivity, json.getString("ErrMsg"));
                changeAllEdTextToEmpty();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyOnKeyListener implements View.OnKeyListener {
        EditText ed;

        public MyOnKeyListener(EditText ed) {
            this.ed = ed;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.ed_bar_code:
                        barAnalysis();
                        return true;
                }
            }
            return false;
        }
    }

    /**
     * mEdBarCode（条码）的监听
     */
    private class CustomTextWatcher implements TextWatcher {
        EditText ed;

        public CustomTextWatcher(EditText ed) {
            this.ed = ed;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (ed.getId()) {
                case R.id.ed_bar_code:
                    if (TextUtils.isEmpty(mEdBarCode.getText().toString())) {
                        changeAllEdTextToEmpty();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
