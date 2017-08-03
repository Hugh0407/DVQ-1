package com.techscan.dvq.module.otherIn;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.module.materialOut.MyBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.R.id.ed_num;
import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.isNumber;
import static com.techscan.dvq.common.Utils.showToast;

public class OtherInScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText edBarCode;
    @InjectView(R.id.ed_invcode)
    EditText edInvcode;
    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.ed_type)
    EditText edType;
    @InjectView(R.id.ed_spectype)
    EditText edSpectype;
    @InjectView(R.id.ed_batch)
    EditText edBatch;
    @InjectView(R.id.ed_cost_object)
    EditText edCostObject;
    @InjectView(R.id.ed_cost_name)
    EditText edCostName;
    @InjectView(R.id.ed_manual)
    EditText edManual;
    @InjectView(R.id.ed_num)
    EditText edNum;
    @InjectView(R.id.ed_weight)
    EditText edWeight;
    @InjectView(R.id.ed_qty)
    EditText edQty;
    @InjectView(R.id.ed_unit)
    EditText edUnit;
    @InjectView(R.id.btn_overview)
    Button   btnOverview;
    @InjectView(R.id.btn_detail)
    Button   btnDetail;
    @InjectView(R.id.btn_back)
    Button   btnBack;

    public static List<Goods> detailList = new ArrayList<Goods>();
    public static List<Goods> ovList     = new ArrayList<Goods>();
    Activity mActivity;
    String CWAREHOUSEID = "";
    String PK_CALBODY   = "";
    String BATCH        = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_in_scan);
        ButterKnife.inject(this);
        mActivity = this;
        init();
    }

    @Override
    public void onBackPressed() {
    }

    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("其他入库扫描");
        edBarCode.setOnKeyListener(mOnKeyListener);
        edBatch.setOnKeyListener(mOnKeyListener);
        edQty.setOnKeyListener(mOnKeyListener);
        edNum.setOnKeyListener(mOnKeyListener);
        edCostObject.setOnKeyListener(mOnKeyListener);
        edManual.setOnKeyListener(mOnKeyListener);
        edNum.addTextChangedListener(new CustomTextWatcher(edNum));
        edBarCode.addTextChangedListener(new CustomTextWatcher(edBarCode));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                MyBaseAdapter ovAdapter = new MyBaseAdapter(ovList);
                showDialog(ovList, ovAdapter, "扫描总览");
                break;
            case R.id.btn_detail:
                MyBaseAdapter myBaseAdapter = new MyBaseAdapter(detailList);
                showDialog(detailList, myBaseAdapter, "扫描明细");
                break;
            case R.id.btn_back:
                if (ovList.size() > 0) {
                    Intent in     = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) ovList);
                    in.putExtras(bundle);
                    mActivity.setResult(5, in);
                } else {
                    showToast(mActivity, "没有扫描单据");
                }
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
                    setInvBaseToUI((JSONObject) msg.obj);
                    break;
                case 3:
                    //设置成本对象
                    setInvBaseCostObjToUI((JSONObject) msg.obj);
                    break;
            }
        }
    };

    private void showDialog(final List list, final BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(title);
        if (list.size() > 0) {
            View     view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_scan_details, null);
            ListView lv   = (ListView) view.findViewById(R.id.lv);
            if (title.equals("扫描明细")) { //只有明细的页面是可点击的，总览页面是不可点击的
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder delDialog = new AlertDialog.Builder(mActivity);
                        delDialog.setTitle("是否删除该条数据");
                        delDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                adapter.notifyDataSetChanged();
                                addDataToOvList();
                            }
                        });
                        delDialog.setNegativeButton("取消", null);
                        delDialog.show();
                    }
                });
            }
            lv.setAdapter(adapter);
            builder.setView(view);
        } else {
            builder.setMessage("没有扫描内容");
        }
        builder.setPositiveButton("确定", null);
        builder.show();
    }

    /**
     * 条码解析
     */
    private boolean barAnalysis() {
        String bar = edBarCode.getText().toString().trim();
        if (bar.contains("\n")) {
            bar = bar.replace("\n", "");
        }
        edBarCode.setText(bar);
        edBarCode.setSelection(edBarCode.length());//将光标移动到最后的位置
        edBarCode.selectAll();
        SplitBarcode barDecoder = new SplitBarcode(bar);
        if (!barDecoder.creatorOk) {
            showToast(mActivity, "条码有误");
            return false;
        }
        if (barDecoder.BarcodeType.equals("P")) {
            edNum.setEnabled(true);
            edManual.setEnabled(true);
            String invCode = barDecoder.cInvCode;
            if (invCode.contains(",")) {
                invCode = invCode.split(",")[0];
            }
            edInvcode.setText(invCode);
            getInvBaseInfo(invCode);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[0];
            }
            edBatch.setText(batch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edQty.setText("");
            edNum.setText("1");
            edNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
            edNum.setSelection(edNum.length());   //将光标移动到最后的位置
            return true;
        } else if (barDecoder.BarcodeType.equals("TP")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(mActivity, "该托盘已扫描");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            edManual.setEnabled(true);
            edManual.requestFocus();
            String encoding = barDecoder.cInvCode;
            if (encoding.contains(",")) {
                encoding = encoding.split(",")[0];
            }
            edInvcode.setText(encoding);
            getInvBaseInfo(encoding);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[0];
            }
            edBatch.setText(batch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edNum.setText(String.valueOf(barDecoder.iNumber));
            double weight = barDecoder.dQuantity;
            double edNum  = Double.valueOf(barDecoder.iNumber);
            edQty.setText(formatDecimal(weight * edNum));
            return true;
        } else if (barDecoder.BarcodeType.equals("Y")) {
//如果是液体的话需要输入液体总量，将数量设置不可编辑
            edNum.setEnabled(false);
            edBatch.setEnabled(true);
            edQty.setEnabled(true);
            edBatch.requestFocus();  //如果是液体需要手动输入“批次”和“总量”,这里将光标跳到“批次（lot）”
            String invCode = barDecoder.cInvCode;
            edInvcode.setText(invCode);
            getInvBaseInfo(invCode);
            edBatch.setText("");
            edQty.setText("");
            return true;
        } else if (barDecoder.BarcodeType.equals("C")) {
            //如果是包码，批次和总重都改变为不可编辑，数量由员工输入，焦点跳到“数量”
            BATCH = barDecoder.cBatch;
            edBatch.setEnabled(false);
            edQty.setEnabled(false);
            edNum.setEnabled(true);
            edNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
            String invCode = barDecoder.cInvCode;
            edInvcode.setText(invCode);
            getInvBaseInfo(invCode);
            edBatch.setText(barDecoder.cBatch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edQty.setText("");
            edNum.setText("1");
            edNum.selectAll();
            edNum.setSelection(edNum.length());   //将光标移动到最后的位置
            return true;
        } else if (barDecoder.BarcodeType.equals("TC")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(mActivity, "该托盘已扫描");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            //如果是盘码，全都设置为不可编辑
            BATCH = barDecoder.cBatch;
            edQty.setEnabled(false);
            edQty.setEnabled(false);
            edNum.setEnabled(false);
            edManual.setEnabled(true);
            String invCode = barDecoder.cInvCode;
            edInvcode.setText(invCode);
            getInvBaseInfo(invCode);
            edBatch.setText(barDecoder.cBatch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edNum.setText(String.valueOf(barDecoder.iNumber));
            double qty = barDecoder.dQuantity;
            double num = barDecoder.iNumber;
            edQty.setText(formatDecimal(qty * num));
            edManual.requestFocus();
            return true;
        } else {
            showToast(mActivity, "条码有误重新输入");
            SoundHelper.playWarning();
            return false;
        }

//        String[] barCode = bar.split("\\|");
//
//        if (barCode.length == 8 && barCode[0].equals("P")) {// 包码 P|SKU|LOT|WW|TAX|QTY|CW_ONLY|SN 8位
//            edNum.setEnabled(true);
//            edManual.setEnabled(true);
//            edNum.setFocusable(true);
//            String encoding = barCode[1];
//            if (encoding.contains(",")) {
//                encoding = encoding.split(",")[0];
//            }
//            edInvcode.setText(encoding);
//            getInvBaseInfo(encoding);
//            String lot = barCode[2];
//            if (lot.contains(",")) {
//                lot = lot.split(",")[0];
//            }
//            edBatch.setText(lot);
//            edWeight.setText(barCode[5]);
//            edQty.setText("");
//            edNum.setText("1");
//            edNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
//            edNum.setSelection(edNum.length());   //将光标移动到最后的位置
//            return true;
//        } else if (barCode.length == 9 && barCode[0].equals("TP")) {//盘码TP|SKU|LOT|WW|TAX|QTY|NUM|CW_ONLY|SN 9位
//            for (int i = 0; i < detailList.size(); i++) {
//                if (detailList.get(i).getBarcode().equals(bar)) {
//                    showToast(mActivity, "该托盘已扫描");
//                    SoundHelper.playWarning();
//                    return false;
//                }
//            }
//            edManual.setEnabled(true);
//            edManual.requestFocus();
//            String encoding = barCode[1];
//            if (encoding.contains(",")) {
//                encoding = encoding.split(",")[0];
//            }
//            edInvcode.setText(encoding);
//            getInvBaseInfo(encoding);
//            String lot = barCode[2];
//            if (lot.contains(",")) {
//                lot = lot.split(",")[0];
//            }
//            edBatch.setText(lot);
//            edWeight.setText(barCode[5]);
//            edNum.setText(barCode[6]);
//            double weight = Double.valueOf(barCode[5]);
//            double edNum = Double.valueOf(barCode[6]);
//            edQty.setText(formatDecimal(weight * edNum));
//            isBaoBarCode = true;
//            return true;
//        } else {
//            showToast(mActivity, "条码有误重新输入");
//            SoundHelper.playWarning();
//            return false;
//        }
    }

    private void addDataToOvList() {
        ovList.clear();
        int detailSize = detailList.size();
        for (int i = 0; i < detailSize; i++) {
            Goods dtGood = detailList.get(i);
            if (ovList.contains(dtGood)) {
                int   j      = ovList.indexOf(dtGood);
                Goods ovGood = ovList.get(j);
                ovGood.setQty(ovGood.getQty() + dtGood.getQty());
            } else {
                Goods good = new Goods();
                good.setBarcode(dtGood.getBarcode());
                good.setEncoding(dtGood.getEncoding());
                good.setName(dtGood.getName());
                good.setType(dtGood.getType());
                good.setUnit(dtGood.getUnit());
                good.setLot(dtGood.getLot());
                good.setSpec(dtGood.getSpec());
                good.setQty(dtGood.getQty());
                good.setNum(dtGood.getNum());
                good.setPk_invbasdoc(dtGood.getPk_invbasdoc());
                good.setPk_invmandoc(dtGood.getPk_invmandoc());
                good.setCostObject(dtGood.getCostObject());
                good.setManual(dtGood.getManual());
                good.setPk_invmandoc_cost(dtGood.getPk_invmandoc_cost());
                ovList.add(good);
            }
        }
    }

    /**
     * 添加信息到 集合中
     *
     * @return 是否添加完成
     */
    private boolean addDataToDetailList() {
        SoundHelper.playOK();
        Goods goods = new Goods();
        goods.setBarcode(edBarCode.getText().toString());
        goods.setEncoding(edInvcode.getText().toString());
        goods.setName(edName.getText().toString());
        goods.setType(edType.getText().toString());
        goods.setSpec(edSpectype.getText().toString());
        goods.setUnit(edUnit.getText().toString());
        goods.setLot(edBatch.getText().toString());
        goods.setQty(Float.valueOf(edQty.getText().toString()));
        goods.setManual(edManual.getText().toString());
        goods.setCostObject("");    // 默认没有
        goods.setPk_invmandoc_cost(pk_invmandoc_cost);
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        if (edManual.getText().toString().isEmpty()) {
            edManual.setText("");
        }
        goods.setManual(edManual.getText().toString());
        detailList.add(goods);
        addDataToOvList();
        return true;
    }

    /**
     * 清空所有的Edtext
     */
    private void changeAllEdTextToEmpty() {
        edNum.setText("");
        edBarCode.setText("");
        edInvcode.setText("");
        edName.setText("");
        edType.setText("");
        edUnit.setText("");
        edBatch.setText("");
        edQty.setText("");
        edWeight.setText("");
        edSpectype.setText("");
        edManual.setText("");
        edCostName.setText("");
        edCostObject.setText("");
        edNum.setEnabled(false);
    }

    /**
     * 判断所有的edtext是否为空
     *
     * @return true---->所有的ed都不为空,false---->所有的ed都为空
     * 海关手册号 没有做校验
     */
    private boolean isAllEdNotNull() {
        if (!TextUtils.isEmpty(edBarCode.getText())
                && !TextUtils.isEmpty(edInvcode.getText())
                && !TextUtils.isEmpty(edName.getText())
                && !TextUtils.isEmpty(edType.getText())
                && !TextUtils.isEmpty(edSpectype.getText())
                && !TextUtils.isEmpty(edUnit.getText())
                && !TextUtils.isEmpty(edBatch.getText())
                && !TextUtils.isEmpty(edQty.getText())) {
            return true;
        } else {
            showToast(mActivity, "信息不完整，请核对");
            return false;
        }
    }

    /**
     * 获取存货基本信息
     *
     * @param sku 物料编码
     */
    private void getInvBaseInfo(String sku) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", sku);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    /**
     * 获取存货基本信息 海关手册号
     */
    private void getInvBaseVFree4(String batch) {
        if (TextUtils.isEmpty(batch)) {
            return;
        }
        HashMap<String, String> para = new HashMap<String, String>();
        para.put("FunctionName", "GetInvFreeByInvCodeAndLot");
        para.put("CORP", MainLogin.objLog.STOrgCode);
        para.put("BATCH", batch);
        para.put("WAREHOUSEID", CWAREHOUSEID);
        para.put("CALBODYID", PK_CALBODY);
        para.put("CINVBASID", pk_invbasdoc);
        para.put("INVENTORYID", pk_invmandoc);
        para.put("TableName", "vfree4");
        RequestThread requestThread = new RequestThread(para, mHandler, 2);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    /**
     * 获取成品对象
     *
     * @param invCode 物料编码
     */
    private void getInvCostObj(String invCode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", invCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 3);
        Thread        td            = new Thread(requestThread);
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

    private void setInvBaseToUI(JSONObject json) {
        Log.d("TAG", "setInvBaseToUI: " + json);
        try {
            if (json != null && json.getBoolean("Status")) {
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
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取成本对象
     *
     * @param json
     * @throws JSONException
     */
    String pk_invmandoc_cost = "";

    private void setInvBaseCostObjToUI(JSONObject json) {
        try {
            if (json != null && json.getBoolean("Status")) {
                Log.d("", "setInvBaseCostObjToUI: " + json);
                JSONArray               val = json.getJSONArray("baseInfo");
                HashMap<String, Object> map = null;
                for (int i = 0; i < val.length(); i++) {
                    JSONObject tempJso = val.getJSONObject(i);
                    map = new HashMap<String, Object>();
                    map.put("invname", tempJso.getString("invname"));   //橡胶填充油
                    pk_invmandoc_cost = tempJso.getString("pk_invmandoc");
                }
                if (map != null) {
                    edCostName.setText(map.get("invname").toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * edBarCode（条码）的监听
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
                    if (TextUtils.isEmpty(edBarCode.getText().toString())) {
                        edNum.setText("");
                        edInvcode.setText("");
                        edName.setText("");
                        edType.setText("");
                        edUnit.setText("");
                        edBatch.setText("");
                        edQty.setText("");
                        edWeight.setText("");
                        edSpectype.setText("");
                    }
                    break;
                case ed_num:

                    if (TextUtils.isEmpty(edNum.getText())) {
                        edQty.setText("");
                        return;
                    }
                    if (!isNumber(edNum.getText().toString())) {
                        showToast(mActivity, "数量不正确");
                        edNum.setText("");
                        return;
                    }
                    if (Float.valueOf(edNum.getText().toString()) <= 0) {
                        edNum.setText("");
                        showToast(mActivity, "数量不正确");
                        return;
                    }
                    float num = Float.valueOf(edNum.getText().toString());
                    float weight = Float.valueOf(edWeight.getText().toString());
                    edQty.setText(formatDecimal(num * weight));
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
                        if (TextUtils.isEmpty(edBarCode.getText().toString())) {
                            showToast(mActivity, "请输入条码");
                            return true;
                        }
                        barAnalysis();
                        return true;
                    case ed_num:
                        if (TextUtils.isEmpty(edNum.getText().toString())) {
                            showToast(mActivity, "请输入数量");
                            return true;
                        }
                        if (!isNumber(edNum.getText().toString())) {
                            showToast(mActivity, "数量不正确");
                            return true;
                        }
                        //包码需要输入 有多少包，并计算出总数量
                        float num = Float.valueOf(edNum.getText().toString());
                        if (num <= 0) {
                            edNum.setText("");
                            showToast(mActivity, "数量不正确");
                            return true;
                        }

                        float weight = Float.valueOf(edWeight.getText().toString());
                        edQty.setText(String.valueOf(num * weight));
                        addDataToDetailList();
                        edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                        changeAllEdTextToEmpty();
                        return true;
                    case R.id.ed_batch:
                        //液体需要根据批次号获取海关手册号
                        if (TextUtils.isEmpty(edBatch.getText().toString())) {
                            showToast(mActivity, "请输入批次号");
                        } else {
                            BATCH = edBatch.getText().toString();
                            getInvBaseVFree4(BATCH);// 获取海关手册号
                            edQty.requestFocus();  //输入完批次后讲焦点跳到“总量（mEdQty）”
                        }
                        return true;
                    case R.id.ed_qty:
                        if (TextUtils.isEmpty(edQty.getText().toString())) {
                            showToast(mActivity, "请输入数量");
                            return true;
                        }
                        String qty_s = edQty.getText().toString();
                        if (!isNumber(qty_s)) {
                            showToast(mActivity, "总量不正确");
                            edQty.setText("");
                            return true;
                        }
                        float qty_f = Float.valueOf(qty_s);
                        if (qty_f <= 0) {
                            showToast(mActivity, "总量不正确");
                            edQty.setText("");
                            return true;
                        }

                        addDataToDetailList();
                        edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                        changeAllEdTextToEmpty();
                        return true;
                    case R.id.ed_manual:
                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                            changeAllEdTextToEmpty();
                        }
                        return true;
                    case R.id.ed_cost_object:
                        String invCode = edCostObject.getText().toString();
                        getInvCostObj(invCode);
                        edManual.requestFocus();
//                        nextUIGetFocus();
                        return true;
                }
            }
            return false;
        }
    };
}
