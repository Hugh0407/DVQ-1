package com.techscan.dvq.module.materialOut.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

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

import static com.techscan.dvq.common.Utils.PK_CALBODY;
import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.isNumber;
import static com.techscan.dvq.common.Utils.showToast;


public class MaterialOutScanAct extends Activity {

    @Nullable
    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;    //条码
    @Nullable
    @InjectView(R.id.ed_encoding)
    EditText mEdEncoding;   //编码（Sku）
    @Nullable
    @InjectView(R.id.ed_type)
    EditText mEdType;   // 型号
    @Nullable
    @InjectView(R.id.ed_spectype)
    EditText mEdSpectype;   //规格
    @Nullable
    @InjectView(R.id.ed_lot)
    EditText mEdLot;        //批次
    @Nullable
    @InjectView(R.id.ed_name)
    EditText mEdName;       //物料名
    @Nullable
    @InjectView(R.id.ed_unit)
    EditText mEdUnit;
    @Nullable
    @InjectView(R.id.ed_qty)
    EditText mEdQty;
    @Nullable
    @InjectView(R.id.btn_overview)
    Button   mBtnOverview;
    @Nullable
    @InjectView(R.id.btn_detail)
    Button   mBtnDetail;
    @Nullable
    @InjectView(R.id.btn_back)
    Button   mBtnBack;
    @Nullable
    @InjectView(R.id.ed_num)
    EditText mEdNum;
    @Nullable
    @InjectView(R.id.ed_weight)
    EditText mEdWeight;
    @Nullable
    @InjectView(R.id.ed_cost_object)
    EditText mEdCostObject;
    @Nullable
    @InjectView(R.id.ed_manual)
    EditText mEdManual;
    @Nullable
    @InjectView(R.id.ed_cost_name)
    EditText edCostName;
    @Nullable
    @InjectView(R.id.packed)
    TextView packed;
    @Nullable
    @InjectView(R.id.switch_m)
    Switch   switchM;

    String TAG = this.getClass().getSimpleName();

    public static List<Goods> detailList = new ArrayList<Goods>();

    public static List<Goods> ovList = new ArrayList<Goods>();
    @Nullable
    Activity activity;
    String  CWAREHOUSEID = "";
    String  vFree4       = "";
    boolean isPacked     = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_out_scan);
        ButterKnife.inject(this);
        activity = this;
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
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

    @Override
    public void onBackPressed() {
    }

    private void init() {
        CWAREHOUSEID = getIntent().getStringExtra("CWAREHOUSEID");
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("物品扫描");
        mEdBarCode.setOnKeyListener(mOnKeyListener);
        mEdLot.setOnKeyListener(mOnKeyListener);
        mEdQty.setOnKeyListener(mOnKeyListener);
        mEdNum.setOnKeyListener(mOnKeyListener);
//        mEdManual.setOnKeyListener(mOnKeyListener);
        mEdCostObject.setOnKeyListener(mOnKeyListener);
        mEdNum.addTextChangedListener(new CustomTextWatcher(mEdNum));
        mEdBarCode.addTextChangedListener(new CustomTextWatcher(mEdBarCode));
        switchM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    packed.setText("拆托");
                    isPacked = true;
                } else {
                    packed.setText("不拆托");
                    isPacked = false;
                }
            }
        });
    }

    private void showLv(List<Goods> ovList, String title) {
        MyBaseAdapter adapter = new MyBaseAdapter(ovList);
        showDialog(ovList, adapter, title);
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
                    //获取货物基本信息
                    setInvBaseToUI((JSONObject) msg.obj);
                    getInvBaseVFree4(mEdLot.getText().toString());
                    break;
                case 2:
                    //设置海关手册号
                    setManualToUI((JSONObject) msg.obj);
                    break;
                case 3:
                    //设置成本对象
                    setInvBaseCostObjToUI((JSONObject) msg.obj);
                    break;
                case 4:
                    setPackageNumToUI((JSONObject) msg.obj);
                    break;
            }
        }
    };

    /**
     * 当拆拖的时候，通过网络获取该托盘的数量
     *
     * @param jsonObject 结果信息
     */
    private void setPackageNumToUI(JSONObject jsonObject) {
        Log.d(TAG, "setPackageNumToUI: " + jsonObject.toString());
        try {
            if (jsonObject != null && jsonObject.getBoolean("Status")) {

            } else {
                showToast(activity, "获取数量异常");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置海关手册号
     *
     * @param jsonObject 网络获取的json
     */
    private void setManualToUI(JSONObject jsonObject) {
        Log.d(TAG, "setManualToUI: " + jsonObject.toString());
        try {
            if (jsonObject != null && jsonObject.getBoolean("Status")) {
                Log.d(TAG, "vfree4: " + jsonObject);
                JSONArray jsonArray = jsonObject.getJSONArray("vfree4");
                if (jsonArray.length() > 0) {
                    JSONObject j      = jsonArray.getJSONObject(0);
                    String     vfree4 = j.getString("vfree4");
                    if (vfree4.equals("null")) {
                        mEdManual.setText("");
                    } else {
                        mEdManual.setText(vfree4);
                    }
                }
            } else {
//                showToast(activity, "获取海关手册号异常");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialog(final List list, final BaseAdapter adapter, String title) {
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
    private void barAnalysis() {
        String bar = mEdBarCode.getText().toString().trim();
        if (bar.contains("\n")) {
            bar = bar.replace("\n", "");
        }
        mEdBarCode.setText(bar);
        mEdBarCode.setSelection(mEdBarCode.length());   //将光标移动到最后的位置
        mEdBarCode.selectAll();
        SplitBarcode barDecoder = new SplitBarcode(bar);

        if (!barDecoder.creatorOk) {
            showToast(activity, "条码有误");
            return;
        }
        if (barDecoder.BarcodeType.equals("Y")) {
            //如果是液体的话需要输入液体总量，将数量设置不可编辑
            mEdNum.setEnabled(false);
            mEdLot.setEnabled(true);
            mEdQty.setEnabled(true);
            mEdEncoding.setText(barDecoder.cInvCode);
            mEdLot.setText("");
            mEdQty.setText("");
            getInvBaseInfo(barDecoder.cInvCode);
            mEdLot.requestFocus();  //如果是液体需要手动输入“批次”和“总量”,这里将光标跳到“批次（lot）”
        } else if (barDecoder.BarcodeType.equals("C")) {
            //如果是包码，批次和总重都改变为不可编辑，数量由员工输入，焦点跳到“数量”
            // 获取海关手册号

            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdNum.setEnabled(true);
            mEdEncoding.setText(barDecoder.cInvCode);
            mEdLot.setText(barDecoder.cBatch);
            mEdWeight.setText(String.valueOf(barDecoder.dQuantity));
            mEdQty.setText("");
            mEdNum.setText("1");
            mEdNum.selectAll();
            mEdNum.setSelection(mEdNum.length());   //将光标移动到最后的位置
            getInvBaseInfo(barDecoder.cInvCode);
            mEdNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
        } else if (barDecoder.BarcodeType.equals("TC")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(activity, "该托盘已扫描");
                    SoundHelper.playWarning();
                    return;
                }
            }
            mEdLot.setEnabled(false);
            mEdNum.setEnabled(false);
            mEdEncoding.setText(barDecoder.cInvCode);
            mEdLot.setText(barDecoder.cBatch);
            //对于托码,如果拆过托，只显示总量。未拆托的正常显示
            if (isPacked) {
                getPackageNum(bar);
                mEdWeight.setText("--");
                mEdNum.setText("--");
                mEdQty.setEnabled(true);
                mEdQty.requestFocus();
            } else {
                getInvBaseInfo(barDecoder.cInvCode);
                mEdWeight.setText(String.valueOf(barDecoder.dQuantity));
                mEdNum.setText(String.valueOf(barDecoder.iNumber));
                double qty = barDecoder.dQuantity;
                double num = barDecoder.iNumber;
                mEdQty.setText(formatDecimal(qty * num));
                mEdQty.setEnabled(false);
                mEdCostObject.requestFocus();
            }
        } else {
            showToast(activity, "条码有误");
            SoundHelper.playWarning();
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
        goods.setManual(mEdManual.getText().toString());
        goods.setPk_invmandoc_cost(pk_invmandoc_cost);
        detailList.add(goods);
        addDataToOvList();
        SoundHelper.playOK();
    }

    /**
     * 清空所有的Edtext
     */
    private void changeAllEdTextToEmpty() {
        mEdBarCode.setText(""); // mEdBarCode有一个监听，在那里将所有的ed清空
        mEdLot.setEnabled(false);
        mEdNum.setEnabled(false);
        mEdQty.setEnabled(false);
    }

    /**
     * 判断所有的edtext是否为空
     *
     * @return true---->所有的ed都不为空,false---->所有的ed都为空
     */
    private boolean isAllEdNotNull() {
        if (vFree4.equals("Y")) {
            if (TextUtils.isEmpty(mEdManual.getText().toString())) {
                showToast(activity, "海关手册号不可为空");
                return false;
            }
        }
        if (!TextUtils.isEmpty(mEdBarCode.getText())
                && !TextUtils.isEmpty(mEdEncoding.getText())
                && !TextUtils.isEmpty(mEdName.getText())
                && !TextUtils.isEmpty(mEdType.getText())
                && !TextUtils.isEmpty(mEdSpectype.getText())
                && !TextUtils.isEmpty(mEdUnit.getText())
                && !TextUtils.isEmpty(mEdLot.getText())
                && !TextUtils.isEmpty(mEdCostObject.getText())
                && !TextUtils.isEmpty(mEdQty.getText())) {
            return true;
        } else {
            showToast(activity, "信息不完整，请核对");
            return false;
        }
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
        if (TextUtils.isEmpty(invCode)) {
            return;
        }
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
     * 如果是包码,需要网络访问获取数量，
     *
     * @param barcode 通过barcode来获取哪个包
     */
    private void getPackageNum(String barcode) {
        if (TextUtils.isEmpty(barcode)) {
            return;
        }
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
//        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
//        parameter.put("InvCode", invCode);
        parameter.put("TableName", "package_num");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 4);
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
                    mEdName.setText(map.get("invname").toString());
                    mEdUnit.setText(map.get("measname").toString());
                    mEdType.setText(map.get("invtype").toString());
                    mEdSpectype.setText(map.get("invspec").toString());
                    //海关手册号 有或无的标志位 ，分为 Y 和 N 两种
                    vFree4 = map.get("isfree4").toString();
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
                Log.d(TAG, "InvBaseInfo: " + json.toString());
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
                    textWatcherBarcode();
                    break;
                case R.id.ed_num:
                    textWatcherNum();
                    break;
            }
        }
    }

    private void textWatcherNum() {
        if (TextUtils.isEmpty(mEdNum.getText())) {
            mEdQty.setText("");
            return;
        }
        if (!isNumber(mEdNum.getText().toString())) {
            showToast(activity, "数量不正确");
            mEdNum.setText("");
            return;
        }
        if (Float.valueOf(mEdNum.getText().toString()) <= 0) {
            mEdNum.setText("");
            showToast(activity, "数量不正确");
            return;
        }
        float num    = Float.valueOf(mEdNum.getText().toString());
        float weight = Float.valueOf(mEdWeight.getText().toString());
        mEdQty.setText(formatDecimal(num * weight));
    }

    private void textWatcherBarcode() {
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
            edCostName.setText("");
            mEdManual.setText("");
            edCostName.setText("");
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
                        if (keyEnterBarcode()) return true;
                        break;
                    case R.id.ed_lot:
                        if (keyEnterLot()) return true;
                        break;
                    case R.id.ed_qty:
                        if (keyEnterQty()) return true;
                        break;
                    case R.id.ed_num:
                        if (keyEnterNum()) return true;
                        break;
//                    case R.id.ed_manual:
//                        if (vFree4.equals("Y") && TextUtils.isEmpty(mEdManual.getText().toString())) {
//                            showToast(activity, "海关手册号不可为空");
//                            return true;
//                        }
//                        if (vFree4.equals("N") && !TextUtils.isEmpty(mEdManual.getText().toString())) {
//                            showToast(activity, "此项应该为空值");
//                            return true;
//                        }
//                        if (isAllEdNotNull()) {
//                            addDataToDetailList();
//                            mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
//                            changeAllEdTextToEmpty();
//                        }
//                        return true;
                    case R.id.ed_cost_object:
                        if (keyEnterCostObj()) return true;
                        break;
                }
            }
            return false;
        }
    };

    private boolean keyEnterCostObj() {
        //                        mEdManual.requestFocus();
        // 成本对象为空，说明此项没有，直接入
        if (TextUtils.isEmpty(mEdCostObject.getText())) {
            if (isAllEdNotNull()) {
                addDataToDetailList();
                mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                changeAllEdTextToEmpty();
                return true;
            }
        }
        // 成本对象有，名字也有 ，说明可以此项有值，直接入
        if (!TextUtils.isEmpty(edCostName.getText()) && !TextUtils.isEmpty(mEdCostObject.getText())) {
            if (isAllEdNotNull()) {
                addDataToDetailList();
                mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                changeAllEdTextToEmpty();
                return true;
            }
        }
        String invCode = mEdCostObject.getText().toString();
        getInvCostObj(invCode);
        return true;
    }

    private boolean keyEnterNum() {
        if (TextUtils.isEmpty(mEdNum.getText().toString())) {
            showToast(activity, "请输入数量");
            return true;
        }
        if (!isNumber(mEdNum.getText().toString())) {
            showToast(activity, "数量不正确");
            return true;
        }
        //包码需要输入 有多少包，并计算出总数量
        float num = Float.valueOf(mEdNum.getText().toString());
        if (num <= 0) {
            mEdNum.setText("");
            showToast(activity, "数量不正确");
            return true;
        }

        float weight = Float.valueOf(mEdWeight.getText().toString());
        mEdQty.setText(String.valueOf(num * weight));
        if (isAllEdNotNull()) {
            addDataToDetailList();
            mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
            changeAllEdTextToEmpty();
            return true;
        }
        return true;
    }

    private boolean keyEnterLot() {
        if (TextUtils.isEmpty(mEdLot.getText().toString())) {
            showToast(activity, "请输入批次号");
        } else {
            String batch = mEdLot.getText().toString();
            getInvBaseVFree4(batch);// 获取海关手册号
            mEdQty.requestFocus();  //输入完批次后讲焦点跳到“总量（mEdQty）”
        }
        return true;
    }

    private boolean keyEnterQty() {
        if (TextUtils.isEmpty(mEdQty.getText().toString())) {
            showToast(activity, "请输入数量");
            return true;
        }
        String qty_s = mEdQty.getText().toString();
        if (!isNumber(qty_s)) {
            showToast(activity, "总量不正确");
            mEdQty.setText("");
            return true;
        }
        float qty_f = Float.valueOf(qty_s);
        if (qty_f <= 0) {
            showToast(activity, "总量不正确");
            mEdQty.setText("");
            return true;
        }

        if (isAllEdNotNull()) {
            addDataToDetailList();
            mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
            changeAllEdTextToEmpty();
            return true;
        }
        return true;
    }


    private boolean keyEnterBarcode() {
        if (TextUtils.isEmpty(mEdBarCode.getText().toString())) {
            showToast(activity, "请输入条码");
            return true;
        }
        barAnalysis();
//                        if (isAllEdNotNull()) {
////                            addDataToDetailList();
//                            mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
//                            changeAllEdTextToEmpty();
//                        } else {
//                        }
        return true;
    }
}
