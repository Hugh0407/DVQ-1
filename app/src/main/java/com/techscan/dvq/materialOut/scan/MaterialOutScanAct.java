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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.materialOut.MyBaseAdapter;
import com.techscan.dvq.common.RequestThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    EditText mEdType;   // 型号
    @InjectView(R.id.ed_spectype)
    EditText mEdSpectype;   //规格
    @InjectView(R.id.ed_lot)
    EditText mEdLot;        //批次
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_out_scan);
        ButterKnife.inject(this);
        initView();
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                // 将相同货物的数量合并 如 A_01_20 A_02_30 合并为A_50
                Goods goods;
                List<Goods> overViewList = new ArrayList<Goods>();
                for (int i = 0; i < detailList.size(); i++) {
                    goods = new Goods();
                    HashMap<String, String> map = detailList.get(i);
                    goods.setName(String.valueOf(map.get("name")));
                    goods.setEncoding(String.valueOf(map.get("encoding")));
                    String qty = String.valueOf(map.get("qty"));
                    if (TextUtils.isEmpty(qty)) {
                        qty = "0.0";
                    }
                    goods.setQty(Float.valueOf(qty));
                    if (i == 0) {
                        overViewList.add(goods);
                    } else {
                        for (int j = 0; j < overViewList.size(); j++) {
                            Goods existGoods = overViewList.get(j);
                            if (goods.getName().equals(existGoods.getName())) {
                                existGoods.setQty(existGoods.getQty() + goods.getQty());
                            } else {
                                overViewList.add(goods);
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
                int count = detailList.size();
                List<Goods> goodsList = new ArrayList<Goods>();
                HashMap<String, String> map;
                Goods c;
                for (int i = 0; i < count; i++) {
                    map = detailList.get(i);
                    c = new Goods();
                    c.setBarcode(map.get("barcode"));
                    c.setEncoding(map.get("encoding"));
                    c.setName(map.get("name"));
                    c.setType(map.get("type"));
                    c.setUnit(map.get("unit"));
                    c.setLot(map.get("lot"));
                    c.setQty(Float.valueOf(map.get("qty")));
                    c.setPk_invbasdoc(pk_invbasdoc);
                    c.setPk_invmandoc(pk_invmandoc);
                    goodsList.add(c);
                }
                if (goodsList.size() > 0) {
                    Intent in = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) goodsList);
                    in.putExtras(bundle);
                    MaterialOutScanAct.this.setResult(5, in);
                }
                finish();
                break;
        }
    }

    private void initView() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("物品扫描");
        mEdBarCode.setOnKeyListener(mOnKeyListener);
        mEdLot.setOnKeyListener(mOnKeyListener);
        mEdQty.setOnKeyListener(mOnKeyListener);
        mEdBarCode.addTextChangedListener(mTextWatcher);
    }

    /**
     * 网络请求后的线程通信
     * msg.obj 是从子线程传递过来的数据
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject json = (JSONObject) msg.obj;
                    if (json != null) {
                        try {
                            GetInvBaseByJson(json);
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
     * 条码解析
     */
    private boolean BarAnalysis() {
        String Bar = mEdBarCode.getText().toString().trim();
        if (Bar.contains("\n")) {
            Bar = Bar.replace("\n", "");
        }
        mEdBarCode.setText(Bar);
        String[] barCode = Bar.split("\\|");
        if (barCode.length == 2 && barCode[0].equals("Y")) {          //Y|SKU
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);
            mEdType.setText("");
            mEdLot.setText("");
            mEdName.setText("");
            mEdUnit.setText("");
            mEdQty.setText("");
            return true;
        } else if (barCode.length == 6 && barCode[0].equals("C")) {   //C|SKU|LOT|TAX|QTY|SN
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);

            mEdLot.setText(barCode[2]);
            mEdQty.setText(barCode[4]);
            return true;
        } else if (barCode.length == 7 && barCode[0].equals("TC")) {    //TC|SKU|LOT|TAX|QTY|NUM|SN
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);
            mEdLot.setText(barCode[2]);
            float qty = Float.valueOf(barCode[4]);
            float num = Float.valueOf(barCode[5]);
            mEdQty.setText(String.valueOf(qty * num));
            return true;
        } else {
            Toast.makeText(MaterialOutScanAct.this, "条码有误重新输入", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 添加信息到 集合中
     *
     * @return
     */
    private boolean addInfoToDetailList() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("barcode", mEdBarCode.getText().toString());
        hashMap.put("encoding", mEdEncoding.getText().toString());
        hashMap.put("name", mEdName.getText().toString());
        hashMap.put("type", mEdType.getText().toString());
        hashMap.put("unit", mEdUnit.getText().toString());
        hashMap.put("lot", mEdLot.getText().toString());
        hashMap.put("qty", mEdQty.getText().toString());

        hashMap.put("pk_invbasdoc", pk_invbasdoc);
        hashMap.put("pk_invmandoc", pk_invmandoc);

        return detailList.add(hashMap);
    }

    /**
     * 清空所有的Edtext
     */
    private void SetAllEcTextIsEmpty() {
        mEdBarCode.setText("");
        mEdEncoding.setText("");
        mEdName.setText("");
        mEdType.setText("");
        mEdUnit.setText("");
        mEdLot.setText("");
        mEdQty.setText("");
    }

    /**
     * 判断所有的edtext是否为空
     *
     * @return
     */
    private boolean isInfoComplete() {
        return (!TextUtils.isEmpty(mEdBarCode.getText())
                && !TextUtils.isEmpty(mEdEncoding.getText())
                && !TextUtils.isEmpty(mEdName.getText())
                && !TextUtils.isEmpty(mEdType.getText())
                && !TextUtils.isEmpty(mEdUnit.getText())
                && !TextUtils.isEmpty(mEdLot.getText())
                && !TextUtils.isEmpty(mEdQty.getText()));
    }

    /**
     * 获取存货基本信息
     *
     * @param sku
     */
    private void GetInvBaseInfo(String sku) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", sku);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }


    /**
     * 通过获取到的json 解析得到物料信息
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    private void GetInvBaseByJson(JSONObject json) throws JSONException {
        Log.d(TAG, "GetInvBaseByJson: " + json);
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
            }
            if (map != null) {
                mEdName.setText(map.get("invname").toString());
                mEdUnit.setText(map.get("measname").toString());
                mEdType.setText(map.get("invtype").toString());
                mEdSpectype.setText(map.get("invspec").toString());
            }
        }
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


    /**
     * 回车键的点击事件
     */
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.ed_bar_code:
                        BarAnalysis();
                        return true;
                    case R.id.ed_lot:
                        if (TextUtils.isEmpty(mEdLot.getText())) {
                            Toast.makeText(MaterialOutScanAct.this, "请输入批次号", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isInfoComplete()) {
                                if (addInfoToDetailList()) {
                                    SetAllEcTextIsEmpty();
                                }
                            }
                        }
                        return true;
                    case R.id.ed_qty:
                        if (TextUtils.isEmpty(mEdQty.getText())) {
                            Toast.makeText(MaterialOutScanAct.this, "请输入数量", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isInfoComplete()) {
                                if (addInfoToDetailList()) {
                                    SetAllEcTextIsEmpty();
                                }
                            }
                        }
                        return true;
                }
            }
            return false;
        }
    };
}
