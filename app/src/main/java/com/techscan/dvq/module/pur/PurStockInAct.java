package com.techscan.dvq.module.pur;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.techscan.dvq.ListWarehouse;
import com.techscan.dvq.PurOrderList;
import com.techscan.dvq.R;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.module.materialOut.DepartmentListAct;
import com.techscan.dvq.module.materialOut.StorgListAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.common.Utils.HANDER_DEPARTMENT;
import static com.techscan.dvq.common.Utils.HANDER_POORDER_BODY;
import static com.techscan.dvq.common.Utils.HANDER_POORDER_HEAD;
import static com.techscan.dvq.common.Utils.HANDER_STORG;
import static com.techscan.dvq.common.Utils.formatTime;

public class PurStockInAct extends Activity {

    @InjectView(R.id.ed_start_date)
    EditText    edStartDate;
    @InjectView(R.id.ed_end_date)
    EditText    edEndDate;
    @InjectView(R.id.ed_purorder_no)
    EditText    edPurorderNo;
    @InjectView(R.id.btn_purbrower)
    ImageButton btnPurbrower;
    @InjectView(R.id.labpurvendor)
    TextView    labpurvendor;
    @InjectView(R.id.ed_billcode)
    EditText    edBillcode;
    @InjectView(R.id.ed_wh)
    EditText    edWh;
    @InjectView(R.id.wh_refer)
    ImageButton whRefer;
    @InjectView(R.id.org)
    EditText    org;
    @InjectView(R.id.refer_org)
    ImageButton referOrg;
    @InjectView(R.id.remark)
    EditText    remark;
    @InjectView(R.id.btn_scan)
    Button      btnScan;
    @InjectView(R.id.btn_save)
    Button      btnSave;
    @InjectView(R.id.btn_exit)
    Button      btnExit;

    Activity activity;
    String   billId;
    String   WareHouseId;
    String   PK_CALBODY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pur_stock_in2);
        ButterKnife.inject(this);
        activity = this;
        init();
    }

    private void init() {
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("采购入库");
        long time = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000;  //10天前
        edStartDate.setText(formatTime(time));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @Override
    public void onBackPressed() {

    }

    @OnClick({R.id.btn_purbrower, R.id.wh_refer, R.id.refer_org, R.id.btn_scan, R.id.btn_save, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_purbrower:
                Common.ShowLoading(activity);
                ShowOrderNoList("");
                break;
            case R.id.wh_refer:
                Common.ShowLoading(activity);
                btnWarehouseClick();
                break;
            case R.id.refer_org:
                Common.ShowLoading(activity);
                getSTOrgList(HANDER_STORG);
                break;
            case R.id.btn_scan:
                ScanDetail();
                break;
            case R.id.btn_save:
                break;
            case R.id.btn_exit:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDER_DEPARTMENT:
                    JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json.getBoolean("Status")) {
                            JSONArray  val  = json.getJSONArray("department");
                            JSONObject temp = new JSONObject();
                            temp.put("department", val);
                            Intent ViewGrid = new Intent(activity, DepartmentListAct.class);
                            ViewGrid.putExtra("myData", temp.toString());
                            startActivityForResult(ViewGrid, 97);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Common.cancelLoading();
                    break;
                case HANDER_STORG:
                    JSONObject storg = (JSONObject) msg.obj;
                    try {
                        if (storg.getBoolean("Status")) {
                            JSONArray  val  = storg.getJSONArray("STOrg");
                            JSONObject temp = new JSONObject();
                            temp.put("STOrg", val);
                            Intent StorgList = new Intent(activity, StorgListAct.class);
                            StorgList.putExtra("STOrg", temp.toString());
                            startActivityForResult(StorgList, 94);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Common.cancelLoading();
                    break;
                case HANDER_POORDER_HEAD:
//                    try {
//                        jsHead = (JSONObject) msg.obj;
//                        JSONArray head = jsHead.getJSONArray("PurGood");
//                        if (jsHead.getBoolean("Status")) {
//                            labpurvendor.setText(head.getJSONObject(0).getString("custname"));
//                        }
//                        getBillBody(billId, HANDER_POORDER_BODY);
//                    } catch (JSONException e) {
//                        Common.cancelLoading();
//                        e.printStackTrace();
//                    }
                    break;
                case HANDER_POORDER_BODY:
//                    jsBody = (JSONObject) msg.obj;
                    Common.cancelLoading();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 96 && resultCode == 1) {
            edPurorderNo.requestFocus();
            billId = data.getStringExtra("BillId");
            edPurorderNo.setText(data.getStringExtra("BillCode"));
            getBillHead(billId, HANDER_POORDER_HEAD);
        }
        //仓库
        if (requestCode == 99 && resultCode == 13) {
            String warehousePK1  = data.getStringExtra("result1");
            String warehousecode = data.getStringExtra("result2");
            String warehouseName = data.getStringExtra("result3");
            WareHouseId = warehousePK1;
            edWh.requestFocus();
            edWh.setText(warehouseName);
        }
        //库存组织
        if (requestCode == 94 && resultCode == 6) {
            String pk_areacl  = data.getStringExtra("pk_areacl");
            String bodyname   = data.getStringExtra("bodyname");
            String pk_calbody = data.getStringExtra("pk_calbody");
            PK_CALBODY = pk_calbody;
            org.requestFocus();
            org.setText(bodyname);
        }

    }

    private void doRequest(HashMap<String, String> hashMap, int msgWhat) {
        RequestThread requestThread = new RequestThread(hashMap, handler, msgWhat);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    /**
     * 得到采购表体表头
     */
    private void getBillHead(String bill_id, int msgWhat) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetPOHead");
        parameter.put("CORDERID", bill_id);
        parameter.put("CORP", MainLogin.objLog.CompanyCode);
        parameter.put("TableName", "PurGood");
        doRequest(parameter, msgWhat);
    }

    /**
     * 得到采购表体
     */
    private void getBillBody(String billBody, int msgWhat) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetPOBody");
        parameter.put("CORDERID", billBody);
        parameter.put("TableName", "PurBody");
        doRequest(parameter, msgWhat);
    }

    private void getSTOrgList(int msgWhat) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetSTOrgList");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("TableName", "STOrg");
        doRequest(parameter, msgWhat);
    }

    private void ShowOrderNoList(String bar) {
        Intent PurOrder = new Intent(this, PurOrderList.class);
        PurOrder.putExtra("BillCode", bar);
        PurOrder.putExtra("StartDate", edStartDate.getText().toString());
        PurOrder.putExtra("EndDate", edEndDate.getText().toString());
        startActivityForResult(PurOrder, 96);
    }

    private void btnWarehouseClick() {
        try {
            JSONObject para = new JSONObject();
            para.put("FunctionName", "GetWareHouseList");
            para.put("CompanyCode", MainLogin.objLog.CompanyCode);
            para.put("STOrgCode", MainLogin.objLog.STOrgCode);
            para.put("TableName", "warehouse");
            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
            if (rev == null) {
                // 网络通讯错误
                Utils.showToast(activity, "错误！网络通讯错误");
                SoundHelper.playWarning();
                return;
            }
            if (rev.getBoolean("Status")) {
                JSONArray  val  = rev.getJSONArray("warehouse");
                JSONObject temp = new JSONObject();
                temp.put("warehouse", val);
                Intent ViewGrid = new Intent(this, ListWarehouse.class);
                ViewGrid.putExtra("myData", temp.toString());
                startActivityForResult(ViewGrid, 99);
            } else {
                String Errmsg = rev.getString("ErrMsg");
                Utils.showToast(activity, Errmsg);
                SoundHelper.playWarning();
            }

        } catch (Exception e) {
            Utils.showToast(activity, e.getMessage());
            SoundHelper.playWarning();
        }
    }

    private void ScanDetail() {
        if (billId == null || billId.equals("")) {
            Utils.showToast(activity, "请先确认需要扫描的订单号");
            SoundHelper.playWarning();
            return;
        }


        Intent scanDetail = new Intent(activity, PurStockInScanAct.class);

//        scanDetail.putExtra("BillNo", this.m_BillNo);
//        scanDetail.putExtra("BillID", this.m_BillID);
//
//        scanDetail.putExtra("tmpWHStatus", tmpWHStatus);
//        scanDetail.putExtra("tmpBillStatus", tmpBillStatus);
//        scanDetail.putExtra("tmpWarehouseID", m_WarehouseID);
//
//        if (tmpWHStatus.equals("Y")) {
//            scanDetail.putExtra("tmpposID", m_PosID);
//        }
//
//        if (jsSerino != null) {
//            scanDetail.putExtra("Tag", "1");
//        } else {
//            scanDetail.putExtra("Tag", "0");
//        }
//        if (jsHead != null) {
//            scanDetail.putExtra("head", jsHead.toString());
//        } else {
//            Utils.showToast(activity, "没有得到表头数据");
//            SoundHelper.playWarning();
//            return;
//        }
//        Log.d("TAG", "PutScanDetailBody: " + jsBody);
//        if (jsBody != null) {
//            scanDetail.putExtra("body", jsBody.toString());
//        } else {
//            Utils.showToast(activity, "没有得到表体数据");
//            SoundHelper.playWarning();
//            return;
//        }
//
//
//
//        if (jsSerino != null) {
//
//            scanDetail.putExtra("serino", jsSerino.toString());
//        }
//        if (jsBoxTotal != null) {
//            scanDetail.putExtra("box", jsBoxTotal.toString());
//        }
//        scanDetail.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
//        startActivityForResult(scanDetail, 35);
    }

}
