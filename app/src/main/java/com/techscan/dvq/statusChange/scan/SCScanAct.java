package com.techscan.dvq.statusChange.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.bean.PurGood;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.materialOut.MyBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.R.id.ed_num;

/**
 * 形态转换模块下的 扫描界面
 */

public class SCScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;
    @InjectView(R.id.ed_encoding)
    EditText mEdEncoding;
    @InjectView(R.id.ed_name)
    EditText mEdName;
    @InjectView(R.id.ed_type)
    EditText mEdType;
    @InjectView(R.id.ed_spectype)
    EditText mEdSpectype;
    @InjectView(R.id.ed_lot)
    EditText mEdLot;
    @InjectView(R.id.ed_cost_object)
    EditText mEdCostObject;
    @InjectView(ed_num)
    EditText mEdNum;
    @InjectView(R.id.ed_weight)
    EditText mEdWeight;
    @InjectView(R.id.ed_qty)
    EditText mEdQty;
    @InjectView(R.id.ed_unit)
    EditText mEdUnit;
    @InjectView(R.id.btn_task)
    Button mBtnTask;
    @InjectView(R.id.btn_detail)
    Button mBtnDetail;
    @InjectView(R.id.btn_back)
    Button mBtnBack;

    String m_BillNo;
    String m_BillID;
    String m_BillType;
    String m_AccID;
    String m_WarehouseID;
    String m_pk_Corp;
    List<PurGood> dataList;
    ProgressDialog progressDialog;
    Activity activity;
    List<Goods> detailList;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scscan);
        ButterKnife.inject(this);
        activity = this;
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("形态转换扫描");
        mEdBarCode.setOnKeyListener(mOnKeyListener);
        mEdNum.setOnKeyListener(mOnKeyListener);
        mEdNum.addTextChangedListener(new CustomTextWatcher(mEdNum));
        mEdBarCode.addTextChangedListener(new CustomTextWatcher(mEdBarCode));
        dataList = new ArrayList<PurGood>();
        detailList = new ArrayList<Goods>();
        initTaskData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    private void initTaskData() {
        showProgressDialog();
        m_BillNo = this.getIntent().getStringExtra("BillNo");
        m_BillID = this.getIntent().getStringExtra("OrderID");
        m_BillType = this.getIntent().getStringExtra("OrderType");
        m_AccID = this.getIntent().getStringExtra("AccID");
        m_WarehouseID = this.getIntent().getStringExtra("m_WarehouseID");
        m_pk_Corp = this.getIntent().getStringExtra("pk_corp");
        GetOtherInOutHead();
        GetOtherInOutBody();
    }


    @OnClick({R.id.btn_task, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_task:
                ScAdapter scAdapter = new ScAdapter(SCScanAct.this, dataList);
                showDialog(dataList, scAdapter, "任务信息");
                break;
            case R.id.btn_detail:
                MyBaseAdapter myBaseAdapter = new MyBaseAdapter(activity, detailList);
                showDialog(detailList, myBaseAdapter, "扫描明细");
                break;
            case R.id.btn_back:
                finish();
                break;
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
            switch (msg.what) {
                case 1:
                    //表头的请求结果
                    JSONObject jsonHead = (JSONObject) msg.obj;
                    try {
                        if (jsonHead != null) {
                            Log.d("TAG", "jsonHead: " + jsonHead.toString());
                            if (jsonHead.getBoolean("Status")) {
                                Log.d("TAG", "jsonHead: ");
                            }
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    //表体的请求结果
                    JSONObject jsonBody = (JSONObject) msg.obj;
                    try {
                        if (jsonBody != null && jsonBody.getBoolean("Status")) {
                            Log.d("TAG", "jsonBody: " + jsonBody);
                            JSONArray jsonArray = jsonBody.getJSONArray("PurBody");
                            PurGood purGood;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                purGood = new PurGood();
                                purGood.setSourceBill(m_BillNo);
                                purGood.setNshouldinnum(0 + "/" + object.getString("nshouldinnum"));
                                purGood.setInvcode(object.getString("invcode"));
                                purGood.setInvname(object.getString("invname"));
                                purGood.setVbatchcode(object.getString("vbatchcode"));
                                dataList.add(purGood);
                            }
                            progressDialogDismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    //条码解析的请求结果，并且把数据设置到UI上
                    JSONObject json = (JSONObject) msg.obj;
                    if (json != null) {
                        try {
                            SetInvBaseToUI(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                    break;
            }
        }
    };

    /**
     * 通过获取到的json 解析得到物料信息,并设置到UI上
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    private void SetInvBaseToUI(JSONObject json) throws JSONException {
        Log.d("TAG", "SetInvBaseToUI: " + json);
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
                mEdCostObject.setText(map.get("invname").toString());
            }

        }
    }


    private void showDialog(List list, BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SCScanAct.this);
        builder.setTitle(title);
        if (list.size() > 0) {
            View view = LayoutInflater.from(SCScanAct.this).inflate(R.layout.dialog_scan_details, null);
            ListView lv = (ListView) view.findViewById(R.id.lv);
            lv.setAdapter(adapter);
            builder.setView(view);
        } else {
            builder.setMessage("没有扫描内容");
        }
        builder.setPositiveButton("确定", null);
        builder.show();
    }

    /**
     * 获取表头
     */
    private void GetOtherInOutHead() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetOtherInOutHead");
        parameter.put("BillType", m_BillType);
        parameter.put("accId", m_AccID);
        parameter.put("BillCode", m_BillNo);
        parameter.put("pk_corp", m_pk_Corp);
        parameter.put("TableName", "PurHead");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * 获取表体
     */
    private void GetOtherInOutBody() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetOtherInOutBody");
        parameter.put("BillID", m_BillID);
        parameter.put("accId", m_AccID);
        parameter.put("TableName", "PurBody");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 2);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * 保存单据的dialog
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(SCScanAct.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        progressDialog.setTitle("保存单据");
        // dismiss监听
//        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//            @Override
//            public void onDismiss(DialogInterface progressDialog) {
//                // TODO Auto-generated method stub
//
//            }
//        });
        // 监听Key事件被传递给dialog
//        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//
//            @Override
//            public boolean onKey(DialogInterface progressDialog, int keyCode,
//                                 KeyEvent event) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//        });
        // 监听cancel事件
//        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//            @Override
//            public void onCancel(DialogInterface progressDialog) {
//                // TODO Auto-generated method stub
//
//            }
//        });
        //设置可点击的按钮，最多有三个(默认情况下)
//        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface progressDialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface progressDialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//        progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "中立",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface progressDialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
        progressDialog.setMessage("正在保存，请等待...");
        progressDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (progressDialog.isShowing()) {
                        Thread.sleep(30 * 1000);
                        // cancel和dismiss方法本质都是一样的，都是从屏幕中删除Dialog,唯一的区别是
                        // 调用cancel方法会回调DialogInterface.OnCancelListener如果注册的话,dismiss方法不会回掉
                        progressDialog.cancel();
                        // progressDialog.dismiss();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * progressDialog 消失
     */
    private void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
        mEdBarCode.setSelection(mEdBarCode.length());   //将光标移动到最后的位置
        mEdBarCode.selectAll();
        String[] barCode = Bar.split("\\|");
        /*********************************************************************/
        //判断每一段的条码是否正确
        for (int i = 0; i < barCode.length; i++) {
            if (i == 0) {
                continue;
            }
            if (TextUtils.isEmpty(barCode[i])) {
                Utils.showToast(activity, "条码错误");
                return false;
            }
            if (!isNumber(barCode[i])) {
                Utils.showToast(activity, "条码中存在非数字字符");
                return false;
            }
        }
        /*********************************************************************/

        if (barCode.length == 9 && barCode[0].equals("P")) {// 包码 P|SKU|LOT|WW|TAX|QTY|CW|ONLY|SN    9位
            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdLot.setTextColor(Color.WHITE);
            mEdQty.setTextColor(Color.WHITE);
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            mEdLot.setText(barCode[2]);
            mEdWeight.setText(barCode[5]);
            GetInvBaseInfo(encoding);
            mEdQty.setText("");
            mEdNum.setEnabled(true);
            mEdNum.setText("1");
            mEdNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
            mEdNum.setSelection(mEdNum.length());   //将光标移动到最后的位置
            return true;
        } else if (barCode.length == 10 && barCode[0].equals("TP")) {//盘码TP|SKU|LOT|WW|TAX|QTY|NUM|CW|ONLY|SN
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(Bar)) {
                    Utils.showToast(activity, "该托盘已扫描");
                    return false;
                }
            }
            //如果是盘码，全都设置为不可编辑，默认这三个是可编辑的
            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdNum.setEnabled(false);
            mEdLot.setTextColor(Color.WHITE);
            mEdQty.setTextColor(Color.WHITE);
            mEdNum.setTextColor(Color.WHITE);
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            mEdLot.setText(barCode[2]);
            mEdWeight.setText(barCode[5]);
            mEdNum.setText(barCode[6]);
            double weight = Double.valueOf(barCode[5]);
            double mEdNum = Double.valueOf(barCode[6]);
            mEdQty.setText(String.valueOf(weight * mEdNum));
            GetInvBaseInfo(encoding);
            return true;
        } else {
            Utils.showToast(activity, "条码有误重新输入");
            return false;
        }
    }

    /**
     * 通过正则表达式匹配数字
     *
     * @param str
     * @return
     */
    public boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 获取存货基本信息
     *
     * @param sku 物料编码
     */
    private void GetInvBaseInfo(String sku) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", sku);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 3);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * 判断所有的edtext是否为空
     *
     * @return true---->所有的ed都不为空,false---->所有的ed都为空
     */
    private boolean isAllEdNotNull() {
        return (!TextUtils.isEmpty(mEdBarCode.getText())
                && !TextUtils.isEmpty(mEdEncoding.getText())
                && !TextUtils.isEmpty(mEdName.getText())
                && !TextUtils.isEmpty(mEdType.getText())
                && !TextUtils.isEmpty(mEdSpectype.getText())
                && !TextUtils.isEmpty(mEdUnit.getText())
                && !TextUtils.isEmpty(mEdLot.getText())
                && !TextUtils.isEmpty(mEdQty.getText()));
    }

    /**
     * 添加信息到 集合中
     *
     * @return
     */
    private void addDataToDetailList() {
        Goods goods = new Goods();
        goods.setBarcode(mEdBarCode.getText().toString());
        goods.setEncoding(mEdEncoding.getText().toString());
        goods.setName(mEdName.getText().toString());
        goods.setType(mEdType.getText().toString());
        goods.setSpec(mEdSpectype.getText().toString());
        goods.setUnit(mEdUnit.getText().toString());
        goods.setLot(mEdLot.getText().toString());
        goods.setQty(Float.valueOf(mEdQty.getText().toString()));
        goods.setCostObject(mEdCostObject.getText().toString());
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        detailList.add(goods);
    }

    /**
     * 清空所有的Edtext
     */
    private void ChangeAllEdTextToEmpty() {
        mEdNum.setText("");
        mEdBarCode.setText("");
        mEdEncoding.setText("");
        mEdName.setText("");
        mEdType.setText("");
        mEdUnit.setText("");
        mEdLot.setText("");
        mEdQty.setText("");
        mEdWeight.setText("");
        mEdSpectype.setText("");
        mEdCostObject.setText("");
        mEdLot.setEnabled(false);
        mEdNum.setEnabled(false);
        mEdQty.setEnabled(false);
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
                        mEdNum.setText("");
                        mEdEncoding.setText("");
                        mEdName.setText("");
                        mEdType.setText("");
                        mEdUnit.setText("");
                        mEdLot.setText("");
                        mEdQty.setText("");
                        mEdWeight.setText("");
                        mEdSpectype.setText("");
                        mEdCostObject.setText("");
                    }
                    break;
                case R.id.ed_num:
                    if (TextUtils.isEmpty(mEdNum.getText())) {
                        mEdQty.setText("");
                        return;
                    }
                    if (!isNumber(mEdNum.getText().toString())) {
                        Utils.showToast(activity, "数量不正确");
                        mEdNum.setText("");
                        return;
                    }
                    if (Float.valueOf(mEdNum.getText().toString()) < 0) {
                        Utils.showToast(activity, "数量不正确");
                        return;
                    }
                    float num = Float.valueOf(mEdNum.getText().toString());
                    float weight = Float.valueOf(mEdWeight.getText().toString());
                    mEdQty.setText(String.valueOf(num * weight));
                    break;
            }
        }
    }

    /**
     * 回车键的点击事件
     */
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.ed_bar_code:
                        if (!TextUtils.isEmpty(mEdBarCode.getText().toString())) {
                            if (isAllEdNotNull()) {
                                addDataToDetailList();
                                mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                                ChangeAllEdTextToEmpty();
                            } else {
                                BarAnalysis();
                            }
                        } else {
                            Utils.showToast(activity, "请输入条码");
                        }
                        return true;
                    case R.id.ed_lot:
                        //形态转换只有成品部分，而成品部分的批次是不可输入的。
                        return true;
                    case R.id.ed_qty:
                        //形态转换只有成品部分，而成品部分的总量是不可输入的。
                        return true;
                    case ed_num:
                        if (TextUtils.isEmpty(mEdNum.getText().toString())) {
                            Utils.showToast(activity, "请输入数量");
                            return true;
                        }
                        if (!isNumber(mEdNum.getText().toString())) {
                            Utils.showToast(activity, "数量不正确");
                            return true;
                        }
                        //包码需要输入 有多少包，并计算出总数量
                        float num = Float.valueOf(mEdNum.getText().toString());
                        if (num < 0) {
                            Utils.showToast(activity, "数量不正确");
                            return true;
                        }

                        float weight = Float.valueOf(mEdWeight.getText().toString());
                        mEdQty.setText(String.valueOf(num * weight));
                        addDataToDetailList();
                        mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                        ChangeAllEdTextToEmpty();
                        return true;
                }
            }
            return false;
        }
    };
}
