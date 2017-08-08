package com.techscan.dvq.module.otherOut;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.isNumber;
import static com.techscan.dvq.common.Utils.showToast;

public class OtherOutScanAct extends Activity {

    @Nullable
    @InjectView(R.id.ed_bar_code)
    EditText edBarCode;
    @Nullable
    @InjectView(R.id.ed_encoding)
    EditText edEncoding;
    @Nullable
    @InjectView(R.id.ed_name)
    EditText edName;
    @Nullable
    @InjectView(R.id.ed_type)
    EditText edType;
    @Nullable
    @InjectView(R.id.ed_spectype)
    EditText edSpectype;
    @Nullable
    @InjectView(R.id.ed_lot)
    EditText edLot;
    @Nullable
    @InjectView(R.id.ed_cost_object)
    EditText edCostObject;
    @Nullable
    @InjectView(R.id.ed_cost_name)
    EditText edCostName;
    @Nullable
    @InjectView(R.id.ed_manual)
    EditText edManual;
    @Nullable
    @InjectView(R.id.ed_num)
    EditText edNum;
    @Nullable
    @InjectView(R.id.ed_weight)
    EditText edWeight;
    @Nullable
    @InjectView(R.id.ed_qty)
    EditText edQty;
    @Nullable
    @InjectView(R.id.ed_unit)
    EditText edUnit;
    @Nullable
    @InjectView(R.id.btn_overview)
    Button   btnOverview;
    @Nullable
    @InjectView(R.id.btn_detail)
    Button   btnDetail;
    @Nullable
    @InjectView(R.id.btn_back)
    Button   btnBack;

    String TAG = this.getClass().getSimpleName();
    @NonNull
    public static List<Goods> detailList = new ArrayList<Goods>();
    @NonNull
    public static List<Goods> ovList     = new ArrayList<Goods>();
    ArrayList<EditText> edList;

    @Nullable
    Activity activity;
    String CWAREHOUSEID = "";
    String PK_CALBODY = "";
    String vFree4       = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_out_scan);
        ButterKnife.inject(this);
        activity = this;
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @Override
    public void onBackPressed() {
    }

    private void init() {
        CWAREHOUSEID = getIntent().getStringExtra("CWAREHOUSEID");
        PK_CALBODY = getIntent().getStringExtra("PK_CALBODY");
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("其他出库扫描");
        edBarCode.setOnKeyListener(mOnKeyListener);
        edLot.setOnKeyListener(mOnKeyListener);
        edQty.setOnKeyListener(mOnKeyListener);
        edNum.setOnKeyListener(mOnKeyListener);
//        edManual.setOnKeyListener(mOnKeyListener);
        edCostObject.setOnKeyListener(mOnKeyListener);
        edNum.addTextChangedListener(new CustomTextWatcher(edNum));
        edBarCode.addTextChangedListener(new CustomTextWatcher(edBarCode));
        addEdToList();
    }

    /**
     * 按照顺序将所有的输入框加入到集合中
     */
    private void addEdToList() {
        edList = new ArrayList<EditText>();
        edList.add(edBarCode);
        edList.add(edEncoding);
        edList.add(edName);
        edList.add(edType);
        edList.add(edSpectype);
        edList.add(edLot);
//        edList.add(edCostObject);
        edList.add(edCostName);
        edList.add(edManual);
        edList.add(edNum);
        edList.add(edWeight);
        edList.add(edQty);
        edList.add(edUnit);
    }

    /**
     * 遍历集合，如果edtext是 enabled 并且 为空，则该edtext获取焦点
     */
    private void nextUIGetFocus() {
        for (EditText ed : edList) {
            if (ed.isEnabled() && TextUtils.isEmpty(ed.getText().toString())) {
                ed.requestFocus();
                break;
            }
        }
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                showLv(ovList, "扫描总览");
                break;
            case R.id.btn_detail:
                showLv(detailList, "扫描明细");
                break;
            case R.id.btn_back:
                if (ovList.size() > 0) {
                    Intent in     = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) ovList);
                    in.putExtras(bundle);
                    activity.setResult(5, in);
                } else {
                    showToast(activity, "没有扫描单据");
                }
                finish();
                break;
        }
    }

    private void showLv(@NonNull List<Goods> ovList, @NonNull String title) {
        MyBaseAdapter ovAdapter = new MyBaseAdapter(ovList);
        showDialog(ovList, ovAdapter, title);
    }

    /**
     * 网络请求后的线程通信
     * msg.obj 是从子线程传递过来的数据
     */
    @NonNull
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //获取货物基本信息
                    setInvBaseToUI((JSONObject) msg.obj);
                    nextUIGetFocus();
                    // 获取海关手册号
                    getInvBaseVFree4(edLot.getText().toString());
                    break;
                case 2:
                    //设置海关手册号
                    setManualToUI((JSONObject) msg.obj);
                    break;
                case 3:
                    //设置成本对象
                    setInvBaseCostObjToUI((JSONObject) msg.obj);
                    break;
            }
        }
    };

    private void setManualToUI(@Nullable JSONObject jsonObject) {
        try {
            if (jsonObject != null && jsonObject.getBoolean("Status")) {
                Log.d("TAG", "vfree4: " + jsonObject);
                JSONArray jsonArray = jsonObject.getJSONArray("vfree4");
                if (jsonArray.length() > 0) {
                    JSONObject j      = jsonArray.getJSONObject(0);
                    String     vfree4 = j.getString("vfree4");
                    if (vfree4.equals("null")) {
                        edManual.setText("");
                    } else {
                        edManual.setText(vfree4);
                    }
                }
            } else {
                Log.d("TAG", "vfree4: null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showDialog(@NonNull final List list, @NonNull final BaseAdapter adapter, @NonNull String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        if (list.size() > 0) {
            View     view = LayoutInflater.from(activity).inflate(R.layout.dialog_scan_details, null);
            ListView lv   = (ListView) view.findViewById(R.id.lv);
            if (title.equals("扫描明细")) { //只有明细的页面是可点击的，总览页面是不可点击的
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder delDialog = new AlertDialog.Builder(activity);
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
        edBarCode.setSelection(edBarCode.length());   //将光标移动到最后的位置
        edBarCode.selectAll();
        SplitBarcode barDecoder = new SplitBarcode(bar);

        if (!barDecoder.creatorOk) {
            showToast(activity, "条码有误");
            return false;
        }
        if (barDecoder.BarcodeType.equals("Y")) {
            //如果是液体的话需要输入液体总量，将数量设置不可编辑
            edNum.setEnabled(false);
            edLot.setEnabled(true);
            edQty.setEnabled(true);
            edEncoding.setText(barDecoder.cInvCode);
            edLot.setText("");
            edQty.setText("");
            getInvBaseInfo(barDecoder.cInvCode);
            edLot.requestFocus();  //如果是液体需要手动输入“批次”和“总量”,这里将光标跳到“批次（lot）”
            return true;
        } else if (barDecoder.BarcodeType.equals("C")) {
            //如果是包码，批次和总重都改变为不可编辑，数量由员工输入，焦点跳到“数量”
            edLot.setEnabled(false);
            edQty.setEnabled(false);
            edNum.setEnabled(true);
            edEncoding.setText(barDecoder.cInvCode);
            edLot.setText(barDecoder.cBatch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edQty.setText("");
            edNum.setText("1");
            edNum.selectAll();
            edNum.setSelection(edNum.length());   //将光标移动到最后的位置
            getInvBaseInfo(barDecoder.cInvCode);
            edNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量(指多少包),添加到列表
            return true;
        } else if (barDecoder.BarcodeType.equals("TC")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(activity, "该托盘已扫描");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            //如果是盘码，全都设置为不可编辑
            edLot.setEnabled(false);
            edQty.setEnabled(false);
            edNum.setEnabled(false);
//            edManual.setEnabled(true);
            edEncoding.setText(barDecoder.cInvCode);
            edLot.setText(barDecoder.cBatch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edNum.setText(String.valueOf(barDecoder.iNumber));
            double qty = barDecoder.dQuantity;
            double num = barDecoder.iNumber;
            edQty.setText(formatDecimal(qty * num));
            getInvBaseInfo(barDecoder.cInvCode);
//            edCostObject.requestFocus();
            return true;
        } else if (barDecoder.BarcodeType.equals("P")) {
            edNum.setEnabled(true);
//            edManual.setEnabled(true);
            edNum.setFocusable(true);
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
            edNum.setSelection(edNum.length());   //将光标移动到最后的位置
            edNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
            return true;
        } else if (barDecoder.BarcodeType.equals("TP")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(activity, "该托盘已扫描");
                    SoundHelper.playWarning();
                    return false;
                }
            }
//            edManual.setEnabled(true);
//            edManual.requestFocus();
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
            double mEdNum = Double.valueOf(barDecoder.iNumber);
            edQty.setText(formatDecimal(weight * mEdNum));
            return true;
        } else {
            showToast(activity, "条码有误");
            SoundHelper.playWarning();
            return false;
        }
    }


    /**
     * 添加数据到总览列表中，重写了Goods的equals和hashcode
     */
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
     */
    private void addDataToDetailList() {
        Goods goods = new Goods();
        goods.setBarcode(edBarCode.getText().toString());
        goods.setEncoding(edEncoding.getText().toString());
        goods.setName(edName.getText().toString());
        goods.setType(edType.getText().toString());
        goods.setSpec(edSpectype.getText().toString());
        goods.setUnit(edUnit.getText().toString());
        goods.setLot(edLot.getText().toString());
        goods.setQty(Float.valueOf(edQty.getText().toString()));
        goods.setCostObject(edCostObject.getText().toString());
        goods.setManual(edManual.getText().toString());
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        goods.setPk_invmandoc_cost(pk_invmandoc_cost);
        detailList.add(goods);
        addDataToOvList();
        SoundHelper.playOK();
    }

    /**
     * 清空所有的Edtext
     */
    private void changeAllEdTextToEmpty() {
        edBarCode.setText("");  // edBarCode 有一个监听，在那里将全部ed清空
        edLot.setEnabled(false);
        edNum.setEnabled(false);
        edQty.setEnabled(false);
    }

    /**
     * 判断所有的edtext是否为空
     *
     * @return true---->所有的ed都不为空,false---->所有的ed都为空
     */
    private boolean isAllEdNotNull() {

        if (vFree4.equals("Y")) {
            if (TextUtils.isEmpty(edManual.getText().toString())) {
                showToast(activity, "海关手册号不可为空");
                return false;
            }
        }


        if (TextUtils.isEmpty(edBarCode.getText().toString())) {
            showToast(activity, "条码不可为空");
            return false;
        }

        if (TextUtils.isEmpty(edEncoding.getText().toString())) {
            showToast(activity, "物料编码不可为空");
            return false;
        }

        if (TextUtils.isEmpty(edName.getText().toString())) {
            showToast(activity, "物料名称不可为空");
            return false;
        }

        if (TextUtils.isEmpty(edType.getText().toString())) {
            showToast(activity, "物料型号不可为空");
            return false;
        }

        if (TextUtils.isEmpty(edSpectype.getText().toString())) {
            showToast(activity, "物料规格不可为空");
            return false;
        }

        if (TextUtils.isEmpty(edUnit.getText().toString())) {
            showToast(activity, "单位不可为空");
            return false;
        }

        if (TextUtils.isEmpty(edLot.getText().toString())) {
            showToast(activity, "批次不可为空");
            return false;
        }

//        if (TextUtils.isEmpty(edCostObject.getText().toString())) {
//            showToast(activity, "成本对象不可为空");
//            return false;
//        }

        if (TextUtils.isEmpty(edQty.getText().toString())) {
            showToast(activity, "总量不可为空");
            return false;
        }
        return true;
    }

    /**
     * 获取存货基本信息
     *
     * @param invCode 物料编码
     */
    private void getInvBaseInfo(String invCode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", invCode);
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

    private void setInvBaseToUI(@Nullable JSONObject json) {
        try {
            if (json != null && json.getBoolean("Status")) {
                Log.d(TAG, "setInvBaseToUI: " + json);
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
                    map.put("isfree4", tempJso.getString("isfree4"));   //重量
                }
                if (map != null) {
                    edName.setText(map.get("invname").toString());
                    edUnit.setText(map.get("measname").toString());
                    edType.setText(map.get("invtype").toString());
                    edSpectype.setText(map.get("invspec").toString());
                    vFree4 = map.get("isfree4").toString(); //只有Y,两种值
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

    private void setInvBaseCostObjToUI(@Nullable JSONObject json) {
        try {
            if (json != null && json.getBoolean("Status")) {
                Log.d(TAG, "setInvBaseCostObjToUI: " + json);
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
                        edEncoding.setText("");
                        edName.setText("");
                        edType.setText("");
                        edUnit.setText("");
                        edLot.setText("");
                        edQty.setText("");
                        edWeight.setText("");
                        edSpectype.setText("");
                        edCostObject.setText("");
                        edCostName.setText("");
                        edManual.setText("");
                    }
                    break;
                case R.id.ed_num:
                    if (TextUtils.isEmpty(edNum.getText())) {
                        edQty.setText("");
                        return;
                    }
                    if (!isNumber(edNum.getText().toString())) {
                        showToast(activity, "数量不正确");
                        edNum.setText("");
                        return;
                    }
                    if (Float.valueOf(edNum.getText().toString()) <= 0) {
                        edNum.setText("");
                        showToast(activity, "数量不正确");
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
    @NonNull
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(@NonNull View v, int keyCode, @NonNull KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.ed_bar_code:
                        if (TextUtils.isEmpty(edBarCode.getText().toString())) {
                            showToast(activity, "请输入条码");
                            return true;
                        }
                        barAnalysis();
//                        if (isAllEdNotNull()) {
////                            addDataToDetailList();
//                            edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
//                            changeAllEdTextToEmpty();
//                        } else {
//                        }

                        return true;
                    case R.id.ed_lot:
                        if (TextUtils.isEmpty(edLot.getText().toString())) {
                            showToast(activity, "请输入批次号");
                            return true;
                        } else {
                            getInvBaseVFree4(edLot.getText().toString());// 获取海关手册号
                            edQty.requestFocus();  //输入完批次后讲焦点跳到“总量（edQty）”
                            return true;
                        }
                    case R.id.ed_qty:
                        if (TextUtils.isEmpty(edQty.getText().toString())) {
                            showToast(activity, "请输入数量");
                            return true;
                        }
                        String qty_s = edQty.getText().toString();
                        if (!isNumber(qty_s)) {
                            showToast(activity, "总量不正确");
                            edQty.setText("");
                            return true;
                        }
                        float qty_f = Float.valueOf(qty_s);
                        if (qty_f <= 0) {
                            showToast(activity, "总量不正确");
                            edQty.setText("");
                            return true;
                        }

                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                            changeAllEdTextToEmpty();
                            return true;
                        }
                        return true;
                    case R.id.ed_num:
                        if (TextUtils.isEmpty(edNum.getText().toString())) {
                            showToast(activity, "请输入数量");
                            return true;
                        }
                        String num_s = edNum.getText().toString();
                        if (!isNumber(num_s)) {
                            showToast(activity, "数量不正确");
                            return true;
                        }
                        //包码需要输入 有多少包，并计算出总数量
                        float num_f = Float.valueOf(num_s);
                        if (num_f <= 0) {
                            edNum.setText("");
                            showToast(activity, "数量不正确");
                            return true;
                        }

                        float weight = Float.valueOf(edWeight.getText().toString());
                        edQty.setText(String.valueOf(num_f * weight));

                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                            changeAllEdTextToEmpty();
                            return true;
                        }
                        return true;
//                    case R.id.ed_manual:
//                        if (vFree4.equals("Y") && TextUtils.isEmpty(edManual.getText().toString())) {
//                            showToast(activity, "海关手册号不可为空");
//                            return true;
//                        }
//                        if (vFree4.equals("N") && !TextUtils.isEmpty(edManual.getText().toString())) {
//                            showToast(activity, "此项应该为空值");
//                            return true;
//                        }
//                        if (isAllEdNotNull()) {
//                            addDataToDetailList();
////                            edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
//                            changeAllEdTextToEmpty();
//                            nextUIGetFocus();
//                        }
//                        return true;
                    case R.id.ed_cost_object:
//                        edManual.requestFocus();
                        // 成本对象为空，说明此项没有，直接入
                        if (TextUtils.isEmpty(edCostObject.getText())) {
                            if (isAllEdNotNull()) {
                                addDataToDetailList();
                                edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                                changeAllEdTextToEmpty();
                                return true;
                            }
                        }
                        // 成本对象有，名字也有 ，说明可以此项有值，直接入
                        if (!TextUtils.isEmpty(edCostName.getText()) && !TextUtils.isEmpty(edCostObject.getText())) {
                            if (isAllEdNotNull()) {
                                addDataToDetailList();
                                edBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                                changeAllEdTextToEmpty();
                                return true;
                            }
                        }
                        String invCode = edCostObject.getText().toString();
                        getInvCostObj(invCode);
                        return true;
                }
            }
            return false;
        }
    };
}
