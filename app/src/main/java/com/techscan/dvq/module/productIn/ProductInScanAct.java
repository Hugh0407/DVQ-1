package com.techscan.dvq.module.productIn;

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
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.common.Utils;
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


public class ProductInScanAct extends Activity {

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
    Button   mBtnOverview;
    @InjectView(R.id.btn_detail)
    Button   mBtnDetail;
    @InjectView(R.id.btn_back)
    Button   mBtnBack;
    @InjectView(R.id.ed_weight)
    EditText mEdWeight;
    @InjectView(R.id.ed_manual)
    EditText mEdManual;
    @InjectView(R.id.ed_num)
    EditText mEdNum;
    @InjectView(R.id.ed_pur_lot)    //生产批次
    EditText edPurLot;

    String TAG = "MaterialOutScanAct";
    public static List<Goods> detailList = new ArrayList<Goods>();
    public static List<Goods> ovList     = new ArrayList<Goods>();
    Activity mActivity;
    String vFree4 = ""; // 海关手册号
    String vFree5 = ""; // 生产批次


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_scan);
        ButterKnife.inject(this);
        mActivity = this;
        initView();
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

    @Override
    public void onBackPressed() {
    }

    private void initView() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("物品扫描");
//        mEdNum.setInputType(InputType.TYPE_NULL); // 设置ed不弹出软键盘, 使用物理键输入
        mEdBarCode.setOnKeyListener(mOnKeyListener);
        mEdLot.setOnKeyListener(mOnKeyListener);
        mEdQty.setOnKeyListener(mOnKeyListener);
        mEdNum.setOnKeyListener(mOnKeyListener);
        edPurLot.setOnKeyListener(mOnKeyListener);
        mEdManual.setOnKeyListener(mOnKeyListener);
        mEdNum.addTextChangedListener(new CustomTextWatcher(mEdNum));
        mEdBarCode.addTextChangedListener(new CustomTextWatcher(mEdBarCode));
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
        String bar = mEdBarCode.getText().toString().trim();
        if (bar.contains("\n")) {
            bar = bar.replace("\n", "");
        }
        mEdBarCode.setText(bar);
        mEdBarCode.setSelection(mEdBarCode.length());//将光标移动到最后的位置
        mEdBarCode.selectAll();
        SplitBarcode barDecoder = new SplitBarcode(bar);
        if (!barDecoder.creatorOk) {
            showToast(mActivity, "条码有误");
            return false;
        }
        if (barDecoder.BarcodeType.equals("P")) {
            mEdNum.setEnabled(true);
            mEdManual.setEnabled(true);
            String invCode = barDecoder.cInvCode;
            if (invCode.contains(",")) {
                invCode = invCode.split(",")[0];
            }
            mEdEncoding.setText(invCode);
            edPurLot.setText(barDecoder.purductBatch);
            getInvBaseInfo(invCode);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[0];
            }
            mEdLot.setText(batch);
            mEdWeight.setText(String.valueOf(barDecoder.dQuantity));
            mEdQty.setText("");
            mEdNum.setText("1");
            mEdNum.requestFocus();  //包码扫描后光标跳到“数量”,输入数量,添加到列表
            mEdNum.setSelection(mEdNum.length());   //将光标移动到最后的位置
            return true;
        } else if (barDecoder.BarcodeType.equals("TP")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(mActivity, "该托盘已扫描");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            mEdManual.setEnabled(true);
            mEdManual.requestFocus();
            String encoding = barDecoder.cInvCode;
            if (encoding.contains(",")) {
                encoding = encoding.split(",")[0];
            }
            mEdEncoding.setText(encoding);
            edPurLot.setText(barDecoder.purductBatch);
            getInvBaseInfo(encoding);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[0];
            }
            mEdLot.setText(batch);
            mEdWeight.setText(String.valueOf(barDecoder.dQuantity));
            mEdNum.setText(String.valueOf(barDecoder.iNumber));
            double weight = barDecoder.dQuantity;
            double mEdNum = Double.valueOf(barDecoder.iNumber);
            mEdQty.setText(formatDecimal(weight * mEdNum));
            return true;
        } else {
            showToast(mActivity, "条码有误重新输入");
            SoundHelper.playWarning();
            return false;
        }

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
                good.setProductLot(dtGood.getProductLot());
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
        goods.setBarcode(mEdBarCode.getText().toString());
        goods.setEncoding(mEdEncoding.getText().toString());
        goods.setName(mEdName.getText().toString());
        goods.setType(mEdType.getText().toString());
        goods.setSpec(mEdSpectype.getText().toString());
        goods.setUnit(mEdUnit.getText().toString());
        goods.setLot(mEdLot.getText().toString());
        goods.setQty(Float.valueOf(mEdQty.getText().toString()));
        goods.setManual(mEdManual.getText().toString());
        goods.setCostObject("");    // 默认没有
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        if (mEdManual.getText().toString().isEmpty()) {
            mEdManual.setText("");
        }
        goods.setManual(mEdManual.getText().toString());
        goods.setProductLot(edPurLot.getText().toString());
        detailList.add(goods);
        addDataToOvList();
        return true;
    }

    /**
     * 清空所有的Edtext mEdBarCode设置的有监听，在监听处全部置空
     */
    private void changeAllEdTextToEmpty() {
        mEdBarCode.setText("");
        mEdBarCode.requestFocus();
        mEdNum.setEnabled(false);
        mEdManual.setEnabled(false);
    }

    /**
     * 判断所有的edtext是否为空
     *
     * @return true---->所有的ed都不为空,false---->所有的ed都为空
     * 海关手册号 没有做校验
     */
    private boolean isAllEdNotNull() {

        if (vFree4.equals("Y") && TextUtils.isEmpty(mEdManual.getText().toString())) {
            showToast(mActivity, "海关手册号不可为空");
            return false;
        }

        if (vFree4.equals("N") && !TextUtils.isEmpty(mEdManual.getText().toString())) {
            showToast(mActivity, "此物料没有海关手册");
            return false;
        }

        if (vFree5.equals("Y") && TextUtils.isEmpty(edPurLot.getText().toString())) {
            showToast(mActivity, "生产批次不可为空");
            return false;
        }

        if (vFree5.equals("N") && !TextUtils.isEmpty(edPurLot.getText().toString())) {
            showToast(mActivity, "此物料没有生产批次");
            return false;
        }

        if (TextUtils.isEmpty(mEdBarCode.getText().toString())) {
            showToast(mActivity, "条码不可为空");
            return false;
        }

        if (TextUtils.isEmpty(mEdEncoding.getText().toString())) {
            showToast(mActivity, "物料编码不可为空");
            return false;
        }

        if (TextUtils.isEmpty(mEdName.getText().toString())) {
            showToast(mActivity, "物料名称不可为空");
            return false;
        }

        if (TextUtils.isEmpty(mEdType.getText().toString())) {
            showToast(mActivity, "物料型号不可为空");
            return false;
        }

        if (TextUtils.isEmpty(mEdSpectype.getText().toString())) {
            showToast(mActivity, "物料规格不可为空");
            return false;
        }

        if (TextUtils.isEmpty(mEdUnit.getText().toString())) {
            showToast(mActivity, "单位不可为空");
            return false;
        }

        if (TextUtils.isEmpty(mEdLot.getText().toString())) {
            showToast(mActivity, "批次不可为空");
            return false;
        }

//        if (TextUtils.isEmpty(mEdCostObject.getText().toString())) {
//            showToast(activity, "成本对象不可为空");
//            return false;
//        }

        if (TextUtils.isEmpty(mEdQty.getText().toString())) {
            showToast(mActivity, "总量不可为空");
            return false;
        }

        return true;
    }

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
        Utils.doRequest(parameter, mHandler, 1);
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
                    map.put("isfree4", tempJso.getString("isfree4"));
                    map.put("isfree5", tempJso.getString("isfree5"));
                }
                if (map != null) {
                    mEdName.setText(map.get("invname").toString());
                    mEdUnit.setText(map.get("measname").toString());
                    mEdType.setText(map.get("invtype").toString());
                    mEdSpectype.setText(map.get("invspec").toString());
                    vFree4 = map.get("isfree4").toString();
                    vFree5 = map.get("isfree5").toString();
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
                        mEdManual.setText("");
                        edPurLot.setText("");
                    }
                    break;
                case ed_num:

                    if (TextUtils.isEmpty(mEdNum.getText())) {
                        mEdQty.setText("");
                        return;
                    }
                    if (!isNumber(mEdNum.getText().toString())) {
                        showToast(mActivity, "数量不正确");
                        mEdNum.setText("");
                        return;
                    }
                    if (Float.valueOf(mEdNum.getText().toString()) <= 0) {
                        mEdNum.setText("");
                        showToast(mActivity, "数量不正确");
                        return;
                    }
                    float num = Float.valueOf(mEdNum.getText().toString());
                    float weight = Float.valueOf(mEdWeight.getText().toString());
                    mEdQty.setText(formatDecimal(num * weight));
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
                        if (TextUtils.isEmpty(mEdBarCode.getText().toString())) {
                            showToast(mActivity, "请输入条码");
                            return true;
                        }
                        barAnalysis();
                        return true;
                    case R.id.ed_num:
                        if (TextUtils.isEmpty(mEdNum.getText().toString())) {
                            showToast(mActivity, "请输入数量");
                            return true;
                        }
                        if (!isNumber(mEdNum.getText().toString())) {
                            showToast(mActivity, "数量不正确");
                            return true;
                        }
                        //包码需要输入 有多少包，并计算出总数量
                        float num = Float.valueOf(mEdNum.getText().toString());
                        if (num <= 0) {
                            mEdNum.setText("");
                            showToast(mActivity, "数量不正确");
                            return true;
                        }

                        float weight = Float.valueOf(mEdWeight.getText().toString());
                        mEdQty.setText(String.valueOf(num * weight));
                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            changeAllEdTextToEmpty();
                        }
                        return true;
                    case R.id.ed_manual:
                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            changeAllEdTextToEmpty();
                        }
                        return true;
                    case R.id.ed_pur_lot:
                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            changeAllEdTextToEmpty();
                        }
                        return true;
                }
            }
            return false;
        }
    };
}

