package com.techscan.dvq.module.statusChange;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.techscan.dvq.MainLogin;
import com.techscan.dvq.OtherOrderList;
import com.techscan.dvq.R;
import com.techscan.dvq.bean.PurGood;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.module.statusChange.scan.SCScanAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.techscan.dvq.common.Utils.showToast;


public class StatusChangeAct extends Activity {

    @InjectView(R.id.ed_bill_type)
    EditText mEdBillType;
    @InjectView(R.id.btn_bill_type)
    ImageButton mBtnBillType;
    @InjectView(R.id.ed_source_bill)
    EditText mEdSourceBill;
    @InjectView(R.id.btn_source_bill)
    ImageButton mBtnSourceBill;
    @InjectView(R.id.ed_select_wh)
    EditText mEdSelectWh;
    @InjectView(R.id.btn_select_wh)
    ImageButton mBtnSelectWh;

    @InjectView(R.id.sacn)
    Button mSacn;
    @InjectView(R.id.save)
    Button mSave;
    @InjectView(R.id.back)
    Button mBack;

    String result = "";
    String OrderNo = "";
    String OrderID = "";
    String WarehouseName = "";
    String WarehouseID = "";
    String AccID = "";
    String pk_corp = "";
    List<PurGood> dataList;
    ProgressDialog progressDialog;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("形态转换");
        activity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @OnClick({R.id.btn_bill_type, R.id.btn_source_bill, R.id.btn_select_wh,
            R.id.sacn, R.id.save, R.id.back})
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
                if (checkSaveInfo()) {
                    if (dataList != null && dataList.size() > 0) {
//                        try {
//                            SaveInfo(dataList);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        showProgressDialog();
                        Utils.showToast(activity,"等待接口");
                    } else {
                        showToast(activity, "没有需要保存的数据");
                    }
                }
                break;
            case R.id.back:
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
                    break;
            }
        }
    };

    private void SaveInfo(List<PurGood> list) throws JSONException {
        final JSONObject table = new JSONObject();
        JSONObject tableHead = new JSONObject();
        tableHead.put("CUSER", MainLogin.objLog.UserID);
        table.put("Head", tableHead);
        JSONObject tableBody = new JSONObject();
        JSONArray bodyArray = new JSONArray();
        for (PurGood pur : list) {
            JSONObject object = new JSONObject();
            object.put("CINVBASID", pur.getInvcode());
            bodyArray.put(object);
        }
        tableBody.put("ScanDetails", bodyArray);
        table.put("Body", tableBody);
        table.put("GUIDS", UUID.randomUUID().toString());
        Log.d(TAG, "SaveInfo: " + table.toString());
        SaveThread saveThread = new SaveThread(table, "SaveMaterialOut", mHandler, 1);
        Thread thread = new Thread(saveThread);
        thread.start();
    }

    /**
     * 保存单据的dialog
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(activity);
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
                        finish();
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
        return !TextUtils.isEmpty(mEdSourceBill.getText().toString())
                && !TextUtils.isEmpty(mEdSelectWh.getText().toString());
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
        }
        // 回传数据<----OtherStockInDetail.class，从ShowScanDetail(); 中跳过去
        if (requestCode == 93 && resultCode == 7) {
            dataList = data.getParcelableArrayListExtra("dataList");
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
        otherOrder.putExtra("Typename", this.mEdBillType.getText().toString());
        otherOrder.putExtra("BillCode", lsBillCode);
        startActivityForResult(otherOrder, 99);
    }

    //打开扫描界面
    private void ShowScanDetail() {

        if (OrderID == null || OrderID.equals("")) {
            showToast(StatusChangeAct.this, "请选择来源的单据号");
        } else {
            Intent otherOrderDetail = new Intent(this, SCScanAct.class);
            otherOrderDetail.putExtra("OrderID", OrderID);
            otherOrderDetail.putExtra("BillNo", OrderNo);
            otherOrderDetail.putExtra("OrderType", "4N");
            otherOrderDetail.putExtra("AccID", AccID);
            otherOrderDetail.putExtra("m_WarehouseID", WarehouseID);
            otherOrderDetail.putExtra("pk_corp", pk_corp);
            startActivityForResult(otherOrderDetail, 93);
        }
    }
}

