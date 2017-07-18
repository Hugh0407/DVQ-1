package com.techscan.dvq.module.query;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.common.RequestThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.showToast;

public class Query extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;
    @InjectView(R.id.ed_name)
    EditText mEdName;
    @InjectView(R.id.ed_spec)
    EditText mEdSpec;
    @InjectView(R.id.ed_lot)
    EditText mEdLot;
    @InjectView(R.id.ed_supplier)
    EditText mEdSupplier;
    @InjectView(R.id.ed_shelf_life)
    EditText mEdShelfLife;
    @InjectView(R.id.ed_manual)
    EditText mEdManual;
    @InjectView(R.id.ed_in_time)
    EditText mEdInTime;
    @InjectView(R.id.btn_back)
    Button mBtnBack;

    Activity mActivity;
    @InjectView(R.id.ed_total_num)
    EditText mEdTotalNum;

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
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject json = (JSONObject) msg.obj;
                    if (json != null) {
                        try {
                            setInvBaseToUI(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
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
        String[] barCode = Bar.split("\\|");
        if (barCode.length == 6 && barCode[0].equals("C")) {   //C|SKU|LOT|TAX|QTY|SN
            mEdLot.setText(barCode[2]);
            String encoding = barCode[1];
            String batchCode = barCode[2];
            getInvBaseInfoByBarcode(encoding, batchCode, MainLogin.objLog.STOrgCode);
            return true;
        } else if (barCode.length == 7 && barCode[0].equals("TC")) {    //TC|SKU|LOT|TAX|QTY|NUM|SN
            mEdLot.setText(barCode[2]);
            float qty = Float.valueOf(barCode[4]);
            float num = Float.valueOf(barCode[5]);
            mEdTotalNum.setText(formatDecimal(qty * num));
            String encoding = barCode[1];
            String batchCode = barCode[2];
            getInvBaseInfoByBarcode(encoding, batchCode, MainLogin.objLog.STOrgCode);
            return true;
        } else if (barCode.length == 9 && barCode[0].equals("P")) {// 包码 P|SKU|LOT|WW|TAX|QTY|CW|ONLY|SN    9位
            String encoding = barCode[1];
//            mEdEncoding.setText(encoding);
//            mEdLot.setText(barCode[2]);
//            mEdWeight.setText(barCode[5]);
//            mEdQty.setText("");
//            mEdNum.setText("1");
//            mEdNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
//            mEdNum.setSelection(mEdNum.length());   //将光标移动到最后的位置
            String batchCode = barCode[2];
            getInvBaseInfoByBarcode(encoding, batchCode, MainLogin.objLog.STOrgCode);
            return true;
        } else if (barCode.length == 10 && barCode[0].equals("TP")) {//盘码TP|SKU|LOT|WW|TAX|QTY|NUM|CW|ONLY|SN

            String encoding = barCode[1];
//            mEdEncoding.setText(encoding);
//            mEdLot.setText(barCode[2]);
//            mEdWeight.setText(barCode[5]);
//            mEdNum.setText(barCode[6]);
//            double weight = Double.valueOf(barCode[5]);
//            double mEdNum = Double.valueOf(barCode[6]);
//            mEdQty.setText(formatDecimal(weight * mEdNum));
            String batchCode = barCode[2];
            getInvBaseInfoByBarcode(encoding, batchCode, MainLogin.objLog.STOrgCode);
            return true;
        } else {
            showToast(mActivity, "条码有误重新输入");
            return false;
        }
    }

    /**
     * 清空所有的Edtext
     */
    private void changeAllEdTextToEmpty() {

    }

    /**
     * 获取存货基本信息
     *
     * @param sku 物料编码
     */
    private void getInvBaseInfoByBarcode(String sku, String batchcode, String crop) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvInfoByBarcode");
        parameter.put("CompanyCode", MainLogin.objLog.STOrgCode);
        parameter.put("Invcode", sku);
        parameter.put("BatchCode", batchcode);
        parameter.put("Crop", crop);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }


    /**
     * 通过获取到的json 解析得到物料信息,并设置到UI上
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    private void setInvBaseToUI(JSONObject json) throws JSONException {
        Log.d("TAG", "setInvBaseToUI: " + json);
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
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
                map.put("createtime", tempJso.getString("createtime"));
            }
            if (map != null) {
                mEdName.setText(map.get("invname").toString());
                mEdSpec.setText(map.get("invspec").toString());
                mEdInTime.setText(map.get("createtime").toString());
            }
        }
    }

    class MyOnKeyListener implements View.OnKeyListener {
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
}
