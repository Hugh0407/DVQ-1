package com.techscan.dvq.materialOut.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.techscan.dvq.Common;
import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.materialOut.MyBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MaterialOutScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;
    @InjectView(R.id.ed_encoding)
    EditText mEdEncoding;
    @InjectView(R.id.ed_type)
    EditText mEdType;
    @InjectView(R.id.ed_lot)
    EditText mEdLot;
    @InjectView(R.id.ed_name)
    EditText mEdName;
    @InjectView(R.id.ed_unit)
    EditText mEdUnit;
    @InjectView(R.id.ed_qty)
    EditText mEdQty;
    @InjectView(R.id.btn_overview)
    Button mBtnOverview;
    @InjectView(R.id.btn_detail)
    Button mBtnDetail;
    @InjectView(R.id.btn_back)
    Button mBtnBack;

    String TAG = "MaterialOutScanAct";
    List<HashMap<String, String>> detailList = new ArrayList<HashMap<String, String>>();
    List<Cargo> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_out_scan);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("扫描");
//        mEdBarCode.addTextChangedListener(mTextWatcher);
        mEdBarCode.setOnKeyListener(mOnKeyListener);
        mEdBarCode.addTextChangedListener(mTextWatcher);

    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                Cargo cargo;
                List<Cargo> overViewList = new ArrayList<Cargo>();
                tempList = overViewList;
                for (int i = 0; i < detailList.size(); i++) {
                    cargo = new Cargo();
                    HashMap<String, String> map = detailList.get(i);
                    cargo.setName(String.valueOf(map.get("name")));
                    cargo.setEncoding(String.valueOf(map.get("encoding")));
                    String qty = String.valueOf(map.get("qty"));
                    if (TextUtils.isEmpty(qty)) {
                        qty = "0";
                    }
                    cargo.setQty(Integer.valueOf(qty));
                    if (i == 0) {
                        overViewList.add(cargo);
                    } else {
                        for (int j = 0; j < overViewList.size(); j++) {
                            Cargo existCargo = overViewList.get(j);
                            if (cargo.getName().equals(existCargo.getName())) {
                                existCargo.setQty(existCargo.getQty() + cargo.getQty());
                            } else {
                                overViewList.add(cargo);
                            }
                        }
                    }
                }
                OvAdapter ovAdapter = new OvAdapter(MaterialOutScanAct.this, overViewList);
                showDialog(overViewList, ovAdapter, "扫描总览");
                break;
            case R.id.btn_detail:
                MyBaseAdapter myBaseAdapter = new MyBaseAdapter(MaterialOutScanAct.this, detailList);
                showDialog(detailList, myBaseAdapter, "扫描明细");
                break;
            case R.id.btn_back:
                if (tempList != null) {
                    Intent in = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) tempList);
                    in.putExtras(bundle);
                    MaterialOutScanAct.this.setResult(5, in);
                }
                finish();
                break;
        }
    }

    private void showDialog(List list, BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialOutScanAct.this);
        builder.setTitle(title);
        if (list.size() > 0) {
            View view = LayoutInflater.from(MaterialOutScanAct.this).inflate(R.layout.dialog_scan_details, null);
            ListView lv = (ListView) view.findViewById(R.id.lv);
            lv.setAdapter(adapter);
            builder.setView(view);
        } else {
            builder.setMessage("没有扫描内容");
        }
        builder.setPositiveButton("取消", null);
        builder.show();
    }

    /**
     * mEdBarCode（条码）的监听
     */
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mEdBarCode.getText().toString())) {
                mEdEncoding.setText("");
                mEdType.setText("");
                mEdLot.setText("");
                mEdName.setText("");
                mEdUnit.setText("");
                mEdQty.setText("");
            }
        }
    };
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                String[] barCode = mEdBarCode.getText().toString().split("\\|");
                if (barCode.length == 2) {          //Y|SKU
                    String encoding = barCode[1];
                    mEdEncoding.setText(encoding);
                    GetInvBaseInfo(encoding);
                    mEdType.setText("");
                    mEdLot.setText("");
                    mEdName.setText("");
                    mEdUnit.setText("");
                    mEdQty.setText("");
                } else if (barCode.length == 6) {   //C|SKU|LOT|TAX|QTY|SN
                    String encoding = barCode[1];
                    mEdEncoding.setText(encoding);
                    GetInvBaseInfo(encoding);

                    mEdLot.setText(barCode[2]);
                    mEdQty.setText(barCode[4]);
                } else if (barCode.length == 7) {    //TC|SKU|LOT|TAX|QTY|NUM|SN
                    String encoding = barCode[1];
                    mEdEncoding.setText(encoding);
                    GetInvBaseInfo(encoding);

                    mEdLot.setText(barCode[2]);
                    float qty = Float.valueOf(barCode[4]);
                    float num = Float.valueOf(barCode[5]);
                    mEdQty.setText(String.valueOf(qty * num));
                }
                return true;
            }
            return false;
        }
    };

    /**
     * 获取存货基本信息
     *
     * @param sku
     */
    private void GetInvBaseInfo(String sku) {
        MyThread myThread = new MyThread(sku);
        new Thread(myThread).start();
    }

    /**
     * 获取存货信息的线程
     */
    private class MyThread implements Runnable {
        String sku;

        MyThread(String sku) {
            this.sku = sku;
        }

        @Override
        public void run() {
            JSONObject para = new JSONObject();
            try {
                para.put("FunctionName", "GetInvBaseInfo");
                para.put("CompanyCode", MainLogin.objLog.CompanyCode);
                para.put("InvCode", sku);
                para.put("TableName", "baseInfo");
                JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
                if (rev != null) {
                    if (rev.getBoolean("Status")) {
                        JSONArray val = rev.getJSONArray("baseInfo");
                        HashMap<String, Object> map = null;
                        for (int i = 0; i < val.length(); i++) {
                            JSONObject tempJso = val.getJSONObject(i);
                            map = new HashMap<String, Object>();
                            map.put("invname", tempJso.getString("invname"));   //橡胶填充油
                            map.put("invcode", tempJso.getString("invcode"));   //00179
                            map.put("measname", tempJso.getString("measname"));   //千克
                            map.put("oppdimen", tempJso.getString("oppdimen"));   //重量
                        }
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = map;
                        mHandler.sendMessage(msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 网络请求后的线程通信
     * msg.obj 是从子线程传递过来的数据
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
                mEdName.setText(map.get("invname").toString());
                mEdUnit.setText(map.get("measname").toString());
                mEdType.setText("待加");

                if (mEdBarCode.getText() != null && mEdEncoding.getText() != null
                        && mEdName.getText() != null && mEdType.getText() != null
                        && mEdUnit.getText() != null && mEdLot.getText() != null
                        && mEdQty.getText() != null) {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("barcode", mEdBarCode.getText().toString());
                    hashMap.put("encoding", mEdEncoding.getText().toString());
                    hashMap.put("name", mEdName.getText().toString());
                    hashMap.put("type", mEdType.getText().toString());
                    hashMap.put("unit", mEdUnit.getText().toString());
                    hashMap.put("lot", mEdLot.getText().toString());
                    hashMap.put("qty", mEdQty.getText().toString());
                    detailList.add(hashMap);
                }
            }
        }
    };
}
