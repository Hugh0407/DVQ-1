package com.techscan.dvq.module.statusChange;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.techscan.dvq.OtherOrderList;
import com.techscan.dvq.R;
import com.techscan.dvq.bean.PurGood;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.module.statusChange.tab.StatusChangeScanAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.common.Utils.showResultDialog;
import static com.techscan.dvq.common.Utils.showToast;
import static com.techscan.dvq.module.statusChange.tab.StatusChangeScanAct.taskList;


public class StatusChangeAct extends Activity {

    @Nullable
    @InjectView(R.id.ed_bill_type)
    EditText    mEdBillType;
    @Nullable
    @InjectView(R.id.btn_bill_type)
    ImageButton mBtnBillType;
    @Nullable
    @InjectView(R.id.ed_source_bill)
    EditText    mEdSourceBill;
    @Nullable
    @InjectView(R.id.btn_source_bill)
    ImageButton mBtnSourceBill;
    @Nullable
    @InjectView(R.id.ed_select_wh)
    EditText    mEdSelectWh;
    @Nullable
    @InjectView(R.id.btn_select_wh)
    ImageButton mBtnSelectWh;

    @Nullable
    @InjectView(R.id.sacn)
    Button   mSacn;
    @Nullable
    @InjectView(R.id.save)
    Button   mSave;
    @Nullable
    @InjectView(R.id.back)
    Button   mBack;
    @InjectView(R.id.ed_bill_date)
    EditText edBillDate;

    String result        = "";
    String OrderNo       = "";
    String OrderID       = "";
    String WarehouseName = "";
    String WarehouseID   = "";
    String AccID         = "";
    String pk_corp       = "";
    @Nullable
    ProgressDialog progressDialog;
    @Nullable
    Activity       activity;


    int      year;
    int      month;
    int      day;
    Calendar mycalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("形态转换");
        activity = this;
        mycalendar = Calendar.getInstance();//初始化Calendar日历对象
        year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
        edBillDate.setText(MainLogin.appTime);
        edBillDate.setInputType(InputType.TYPE_NULL);
        edBillDate.setOnFocusChangeListener(myFocusListener);
        mBtnBillType.setVisibility(View.INVISIBLE);
        mBtnSelectWh.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @OnClick({R.id.btn_bill_type, R.id.btn_source_bill, R.id.btn_select_wh,
            R.id.sacn, R.id.save, R.id.back, R.id.ed_bill_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bill_type:
                break;
            case R.id.btn_source_bill:
                ShowOrderList("");
                break;
            case R.id.btn_select_wh:
                break;
            case R.id.sacn:
                ShowScanDetail();
                break;
            case R.id.save:
                if (!checkSaveInfo()) {
                    Utils.showToast(activity, "请核对单据!");
                    return;
                }

                if (null == taskList || taskList.size() < 0) {
                    Utils.showToast(activity, "没有需要保存的数据");
                    return;
                }
                saveInfo();
                showProgressDialog("提示", "正在保存,请稍后...");
                break;
            case R.id.back:
                if (taskList != null && taskList.size() > 0) {
                    AlertDialog.Builder bulider =
                            new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("数据未保存是否退出");
                    bulider.setNegativeButton(R.string.QuXiao, null);
                    bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StatusChangeScanAct.taskList.clear();
                            StatusChangeScanAct.detailList.clear();
                            dialog.dismiss();
                            finish();
                        }
                    }).create().show();
                } else {
                    StatusChangeScanAct.taskList.clear();
                    StatusChangeScanAct.detailList.clear();
                    finish();
                }
                break;
            case R.id.ed_bill_date:
                DatePickerDialog dpd = new DatePickerDialog(activity, Datelistener, year, month, day);
                dpd.show();//显示DatePickerDialog组件
                break;
        }
    }

    /**
     * 网络请求后的线程通信
     * msg.obj 是从子线程传递过来的数据
     */
    @Nullable
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject info = (JSONObject) msg.obj;
                    if (null == info) {
                        Log.d("TAG", "info=null ");
                        progressDialogDismiss();
                        showResultDialog(activity, "数据提交失败!");
                        return;
                    }
                    try {
                        if (info.getBoolean("Status")) {
                            Log.d("TAG", "保存" + info.toString());
                            showResultDialog(activity, info.getString("ErrMsg"));
                            taskList.clear();
                            StatusChangeScanAct.detailList.clear();
                            changeAllEdToEmpty();
                            mEdSourceBill.requestFocus();
                        } else {
                            showResultDialog(activity, info.getString("ErrMsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialogDismiss();
                    break;
            }
        }
    };

    private void changeAllEdToEmpty() {
        mEdSourceBill.setText("");
        mEdSelectWh.setText("");
    }

    private void saveInfo() {
        String           CFIRSTBILLBID = null;
        String           CFIRSTBILLHID = null;
        final JSONObject table         = new JSONObject();
        JSONObject       saveOut       = new JSONObject();
        JSONObject       saveOutHead   = new JSONObject();
        try {
            saveOutHead.put("CWAREHOUSEID", WarehouseID);
            saveOutHead.put("PK_CALBODY", MainLogin.objLog.STOrgCode);
            saveOutHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
            saveOutHead.put("CLASTMODIID", MainLogin.objLog.UserID);
            saveOutHead.put("COPERATORID", MainLogin.objLog.UserID);
            saveOutHead.put("CDISPATCHERID", "0001TC100000000011Q8");
            JSONObject saveOutBody  = new JSONObject();
            JSONArray  saveOutArray = new JSONArray();
            for (int i = 0; i < taskList.size(); i++) {
                PurGood purGood = taskList.get(i);
                if (purGood.getFbillrowflag().equals("3")) {
                    saveOutBody.put("CSOURCEBILLBID", purGood.getCsourcebillbid());
                    saveOutBody.put("CSOURCEBILLHID", purGood.getCsourcebillhid());
                    CFIRSTBILLBID = purGood.getCsourcebillbid();
                    CFIRSTBILLHID = purGood.getCsourcebillhid();
                    saveOutBody.put("CFIRSTBILLBID", CFIRSTBILLBID);
                    saveOutBody.put("CFIRSTBILLHID", CFIRSTBILLHID);
                    saveOutBody.put("CBODYWAREHOUSEID", WarehouseID);
                    saveOutBody.put("INVCODE", purGood.getInvcode());
                    saveOutBody.put("CINVBASID", purGood.getPk_invbasdoc());
                    saveOutBody.put("CINVENTORYID", purGood.getCinventoryid());
                    saveOutBody.put("NSHOULDOUTNUM", purGood.getNshouldinnum());
                    saveOutBody.put("NSHOULDOUTNUM", "100.00");
                    saveOutBody.put("NOUTNUM", purGood.getNum_task());
                    saveOutBody.put("PK_BODYCALBODY", "1011TC100000000000KV");
                    saveOutBody.put("VSOURCEBILLCODE", purGood.getSourceBill());
                    saveOutBody.put("VSOURCEROWNO", purGood.getVsourcerowno());
                    saveOutBody.put("VBATCHCODE", purGood.getVbatchcode());
                    saveOutBody.put("CSOURCETYPE", "4N");
                }
            }
            saveOutArray.put(saveOutBody);
            saveOut.put("Head", saveOutHead);
            saveOut.put("Body", saveOutArray);
            saveOut.put("GUIDS", UUID.randomUUID().toString());
            table.put("SaveOut", saveOut);

            JSONObject saveIn     = new JSONObject();
            JSONObject saveInHead = new JSONObject();
            saveInHead.put("CWAREHOUSEID", WarehouseID);     //仓库
            saveInHead.put("PK_CALBODY", MainLogin.objLog.STOrgCode);   //库存组织
            saveInHead.put("PK_CORP", MainLogin.objLog.STOrgCode);   //库存组织
            saveInHead.put("CLASTMODIID", MainLogin.objLog.UserID);  //操作人id  ？
            saveInHead.put("COPERATORID", MainLogin.objLog.UserID);  //操作人id   ？
            saveInHead.put("CDISPATCHERID", "0001TC100000000011QO");//收发类别,邹俊豪说的先写死
            JSONObject saveInBody  = new JSONObject();
            JSONArray  saveInArray = new JSONArray();
            for (int i = 0; i < taskList.size(); i++) {
                PurGood purGood = taskList.get(i);
                if (purGood.getFbillrowflag().equals("2")) {
                    saveInBody.put("CSOURCEBILLBID", purGood.getCsourcebillbid());
                    saveInBody.put("CSOURCEBILLHID", purGood.getCsourcebillhid());
                    saveInBody.put("CFIRSTBILLBID", CFIRSTBILLBID);
                    saveInBody.put("CFIRSTBILLHID", CFIRSTBILLHID);
                    saveInBody.put("CBODYWAREHOUSEID", WarehouseID);
                    saveInBody.put("INVCODE", purGood.getInvcode());
                    saveInBody.put("CINVBASID", purGood.getPk_invbasdoc());
                    saveInBody.put("CINVENTORYID", purGood.getCinventoryid());
//                saveInBody.put("NSHOULDOUTNUM", purGood.getNshouldinnum());
                    saveInBody.put("NSHOULDOUTNUM", "100.00");
                    saveInBody.put("NINNUM", purGood.getNum_task());
                    saveInBody.put("VSOURCEBILLCODE", purGood.getSourceBill());
                    saveInBody.put("PK_BODYCALBODY", "1011TC100000000000KV");
                    saveInBody.put("VSOURCEBILLCODE", purGood.getSourceBill());
                    saveInBody.put("VSOURCEROWNO", purGood.getVsourcerowno());
                    saveInBody.put("VBATCHCODE", purGood.getVbatchcode());
                    saveInBody.put("CSOURCETYPE", "4N");
                }
            }
            saveInArray.put(saveInBody);
            saveIn.put("Head", saveInHead);
            saveIn.put("Body", saveInArray);
            saveIn.put("GUIDS", UUID.randomUUID().toString());
            table.put("SaveIn", saveIn);
            table.put("OPDATE", edBillDate.getText().toString());
            table.put("AUDITORID", MainLogin.objLog.UserID);
            table.put("ORDERID", OrderID);
            Log.d("TAG", "saveInfo: " + table.toString());
            SaveThread saveThread = new SaveThread(table, "SaveOtherInOutBill", mHandler, 1);
            Thread     thread     = new Thread(saveThread);
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存单据的dialog
     */
    private void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
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

    private boolean checkSaveInfo() {
        if (TextUtils.isEmpty(mEdSourceBill.getText().toString())) {
            showToast(activity, "来源单号不可为空");
            mEdSourceBill.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mEdSelectWh.getText().toString())) {
            showToast(activity, "仓库不可为空");
            mEdSelectWh.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 回传数据<----OtherOrderList.class，从ShowOrderList(); 中跳过去
        if (requestCode == 99 && resultCode == 1) {
            result = data.getStringExtra("result");
            OrderNo = data.getStringExtra("OrderNo");
            OrderID = data.getStringExtra("OrderID");
            WarehouseName = data.getStringExtra("WarehouseName");
            WarehouseID = data.getStringExtra("WarehouseID");
            AccID = data.getStringExtra("AccID");
            pk_corp = data.getStringExtra("pk_corp");
            mEdSourceBill.setText(OrderNo);
            mEdSelectWh.setText(WarehouseName);
            Log.d("TAG", "result: " + result);// result: 1
            Log.d("TAG", "OrderNo: " + OrderNo);// OrderNo: ZH17062800001
            Log.d("TAG", "OrderID: " + OrderID);//OrderID: 1011AA1000000004KP6R
            Log.d("TAG", "WarehouseName: " + WarehouseName);// WarehouseName: 华奇工厂成品库
            Log.d("TAG", "WarehouseID: " + WarehouseID);// WarehouseID: 1011TC100000000000LF
            Log.d("TAG", "AccID: " + AccID);// AccID: B
            Log.d("TAG", "pk_corp: " + pk_corp);// pk_corp: 1011
        }
        // 回传数据<----OtherStockInDetail.class，从ShowScanDetail(); 中跳过去
        if (requestCode == 93 && resultCode == 7) {
//
        }
    }

    /**
     * 选择需要转换的order
     *
     * @param lsBillCode
     */
    private void ShowOrderList(String lsBillCode) {
        if (this.mEdBillType.getText().toString().equals("")) {
            showToast(StatusChangeAct.this, "请选择单据类型");
            return;
        }
        Intent otherOrder = new Intent(this, OtherOrderList.class);
        otherOrder.putExtra("OrderType", "4N");
        otherOrder.putExtra("Typename", mEdBillType.getText().toString());
        otherOrder.putExtra("BillCode", lsBillCode);
        startActivityForResult(otherOrder, 99);
    }

    //打开扫描界面
    private void ShowScanDetail() {

        if (OrderID == null || OrderID.equals("")) {
            showToast(StatusChangeAct.this, "请选择来源的单据号");
        } else {
            Intent otherOrderDetail = new Intent(this, StatusChangeScanAct.class);
            otherOrderDetail.putExtra("OrderID", OrderID);
            otherOrderDetail.putExtra("BillNo", OrderNo);
            otherOrderDetail.putExtra("OrderType", "4N");
            otherOrderDetail.putExtra("AccID", AccID);
            otherOrderDetail.putExtra("m_WarehouseID", WarehouseID);
            otherOrderDetail.putExtra("pk_corp", pk_corp);
            startActivityForResult(otherOrderDetail, 93);
        }
    }

    private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(activity, Datelistener, year, month, day);
                dpd.show();//显示DatePickerDialog组件
            }
        }
    };

    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            updateDate();
        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDate() {
            //在TextView上显示日期
            edBillDate.setText(year + "-" + (month + 1) + "-" + day);
        }

    };
}

