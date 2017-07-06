package com.techscan.dvq.productOut.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
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


public class ProductOutScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;    //条码
    @InjectView(R.id.ed_encoding)
    EditText mEdEncoding;   //编码（Sku）
    @InjectView(R.id.ed_type)
    EditText mEdType;   // 型号
    @InjectView(R.id.ed_spectype)
    EditText mEdSpectype;   //规格
    @InjectView(R.id.ed_lot)
    EditText mEdLot;        //批次
    @InjectView(R.id.ed_name)
    EditText mEdName;       //物料名
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
    @InjectView(R.id.ed_weight)
    EditText mEdWeight;
    @InjectView(R.id.ed_manual)
    EditText mEdManual;
    @InjectView(R.id.ed_num)
    EditText mEdNum;

    String TAG = "MaterialOutScanAct";
    List<Goods> detailList;
    List<Goods> ovList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_out_scan);
        ButterKnife.inject(this);
        initView();
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:

                MyBaseAdapter ovAdapter = new MyBaseAdapter(ProductOutScanAct.this, ovList);
                showDialog(ovList, ovAdapter, "扫描总览");
                break;
            case R.id.btn_detail:
                MyBaseAdapter myBaseAdapter = new MyBaseAdapter(ProductOutScanAct.this, detailList);
                showDialog(detailList, myBaseAdapter, "扫描明细");
                break;
            case R.id.btn_back:
                if (ovList.size() > 0) {
                    Intent in = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) ovList);
                    in.putExtras(bundle);
                    ProductOutScanAct.this.setResult(5, in);
                } else {
                    Utils.showToast(ProductOutScanAct.this, "没有扫描单据");
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
        mEdNum.setOnKeyListener(mOnKeyListener);
        mEdManual.setOnKeyListener(mOnKeyListener);
        mEdNum.addTextChangedListener(new CustomTextWatcher(mEdNum));
        mEdBarCode.addTextChangedListener(new CustomTextWatcher(mEdBarCode));
        detailList = new ArrayList<Goods>();
        ovList = new ArrayList<Goods>();
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

    private void showDialog(final List list, final BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductOutScanAct.this);
        builder.setTitle(title);
        if (list.size() > 0) {
            View view = LayoutInflater.from(ProductOutScanAct.this).inflate(R.layout.dialog_scan_details, null);
            ListView lv = (ListView) view.findViewById(R.id.lv);
            if (title.equals("扫描明细")) { //只有明细的页面是可点击的，总览页面是不可点击的
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder delDialog = new AlertDialog.Builder(ProductOutScanAct.this);
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
                Utils.showToast(ProductOutScanAct.this, "条码错误");
                return false;
            }
            if (!isNumber(barCode[i])) {
                Utils.showToast(ProductOutScanAct.this, "条码中存在非数字字符");
                return false;
            }
        }
        /*********************************************************************/

        if (barCode.length == 9 && barCode[0].equals("P")) {// 包码 P|SKU|LOT|WW|TAX|QTY|CW|ONLY|SN    9位
            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdNum.setEnabled(true);
            mEdNum.setFocusable(true);
            mEdLot.setTextColor(Color.WHITE);
            mEdQty.setTextColor(Color.WHITE);
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            mEdLot.setText(barCode[2]);
            mEdWeight.setText(barCode[5]);
            mEdQty.setText("");
            mEdNum.setText("1");
            mEdNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
            mEdNum.setSelection(mEdNum.length());   //将光标移动到最后的位置
            GetInvBaseInfo(encoding);
            return true;
        } else if (barCode.length == 10 && barCode[0].equals("TP")) {//盘码TP|SKU|LOT|WW|TAX|QTY|NUM|CW|ONLY|SN
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(Bar)) {
                    Utils.showToast(ProductOutScanAct.this, "该托盘已扫描");
                    return false;
                }
            }
            //如果是盘码，全都设置为不可编辑，默认这三个是可编辑的
            mEdManual.setEnabled(true);
            mEdManual.requestFocus();
            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdNum.setEnabled(false);
            mEdNum.setFocusable(false);
            mEdQty.setFocusable(false);
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
            Utils.showToast(ProductOutScanAct.this, "条码有误重新输入");
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

    private void addDataToOvList() {
        ovList.clear();
        int detailSize = detailList.size();
        for (int i = 0; i < detailSize; i++) {
            Goods dtGood = detailList.get(i);
            if (ovList.contains(dtGood)) {
                int j = ovList.indexOf(dtGood);
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
                ovList.add(good);
            }
        }
    }

    /**
     * 添加信息到 集合中
     *
     * @return
     */
    private boolean addDataToDetailList() {
        Goods goods = new Goods();
        goods.setBarcode(mEdBarCode.getText().toString());
        goods.setEncoding(mEdEncoding.getText().toString());
        goods.setName(mEdName.getText().toString());
        goods.setType(mEdType.getText().toString());
        goods.setSpec(mEdSpectype.getText().toString());
        goods.setUnit(mEdUnit.getText().toString());
        goods.setLot(mEdLot.getText().toString());
        goods.setQty(Float.valueOf(mEdQty.getText().toString()));
        goods.setCostObject("");    // 默认没有
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        if (mEdManual.getText().toString().isEmpty()) {
            mEdManual.setText("");
        }
        goods.setManual(mEdManual.getText().toString());
        detailList.add(goods);
        addDataToOvList();
        return true;
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
        mEdManual.setText("");
        mEdLot.setEnabled(false);
        mEdNum.setEnabled(false);
        mEdQty.setEnabled(false);
        mEdManual.setEnabled(false);
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

    private void SetInvBaseToUI(JSONObject json) throws JSONException {
        Log.d(TAG, "SetInvBaseToUI: " + json);
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
                    }
                    break;
                case ed_num:

                    if (TextUtils.isEmpty(mEdNum.getText())) {
                        mEdQty.setText("");
                        return;
                    }
                    if (!isNumber(mEdNum.getText().toString())) {
                        Utils.showToast(ProductOutScanAct.this, "数量不正确");
                        mEdNum.setText("");
                        return;
                    }
                    if (Float.valueOf(mEdNum.getText().toString()) < 0) {
                        Utils.showToast(ProductOutScanAct.this, "数量不正确");
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
                            Utils.showToast(ProductOutScanAct.this, "请输入条码");
                        }
                        return true;
                    case R.id.ed_lot:
                        if (TextUtils.isEmpty(mEdLot.getText())) {
                            Utils.showToast(ProductOutScanAct.this, "请输入批次号");
                            return true;
                        } else {
                            mEdQty.requestFocus();  //输入完批次后讲焦点跳到“总量（mEdQty）”
                        }
                        return true;
                    case R.id.ed_qty:

                        if (TextUtils.isEmpty(mEdQty.getText())) {
                            Utils.showToast(ProductOutScanAct.this, "请输入总量");
                        } else {
                            String qty_s = mEdQty.getText().toString();
                            if (!isNumber(qty_s)) {
                                Utils.showToast(ProductOutScanAct.this, "总量不正确");
                                mEdQty.setText("");
                                return true;
                            }
                            float qty_f = Float.valueOf(qty_s);
                            if (qty_f <= 0) {
                                Utils.showToast(ProductOutScanAct.this, "总量不正确");
                                mEdQty.setText("");
                                return true;
                            }

                            if (addDataToDetailList()) {
                                mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                                ChangeAllEdTextToEmpty();
                            }

                        }
                        return true;
                    case ed_num:
                        if (TextUtils.isEmpty(mEdNum.getText().toString())) {
                            Utils.showToast(ProductOutScanAct.this, "请输入数量");
                            return true;
                        }
                        if (!isNumber(mEdNum.getText().toString())) {
                            Utils.showToast(ProductOutScanAct.this, "数量不正确");
                            return true;
                        }
                        //包码需要输入 有多少包，并计算出总数量
                        float num = Float.valueOf(mEdNum.getText().toString());
                        if (num < 0) {
                            Utils.showToast(ProductOutScanAct.this, "数量不正确");
                            return true;
                        }

                        float weight = Float.valueOf(mEdWeight.getText().toString());
                        mEdQty.setText(String.valueOf(num * weight));
//                            if (isAllEdNotNull() && ) {
                        addDataToDetailList();
                        mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                        ChangeAllEdTextToEmpty();
                        return true;
                    case R.id.ed_manual:
                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            mEdBarCode.requestFocus();  //如果添加成功将管标跳到“条码”框
                            ChangeAllEdTextToEmpty();
                        } else {
                            Utils.showToast(ProductOutScanAct.this, "信息不完整，请核对");
                        }
                        return true;
                }
            }
            return false;
        }
    };
}

