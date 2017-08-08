package com.techscan.dvq.module.multilateraltrade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.techscan.dvq.GetInvBaseInfo;
import com.techscan.dvq.ListWarehouse;
import com.techscan.dvq.R;
import com.techscan.dvq.SerializableMap;
import com.techscan.dvq.VlistRdcl;
import com.techscan.dvq.common.Base64Encoder;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.module.materialOut.DepartmentListAct;
import com.techscan.dvq.module.materialOut.StorgListAct;
import com.techscan.dvq.module.multilateraltrade.scan.MultilateralTradeDetail;
import com.techscan.dvq.module.saleout.SaleChooseTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.techscan.dvq.R.string.WangLuoChuXiangWenTi;
import static com.techscan.dvq.common.Utils.HANDER_DEPARTMENT;
import static com.techscan.dvq.common.Utils.HANDER_SAVE_RESULT;
import static com.techscan.dvq.common.Utils.HANDER_STORG;
import static com.techscan.dvq.common.Utils.showResultDialog;

public class MultilateralTrade extends Activity {

    @InjectView(R.id.txtMultilateralTrade)
    EditText txtMultilateralTrade;
    @InjectView(R.id.txtDocument)
    EditText txtDocument;
    @InjectView(R.id.txtSendAndTake)
    EditText txtSendAndTake;
    @InjectView(R.id.imageSendAndTake)
    ImageButton imageSendAndTake;
    @InjectView(R.id.txtOrg)
    EditText txtOrg;
    @InjectView(R.id.imageOrg)
    ImageButton imageOrg;
    @InjectView(R.id.txtDocumentNumber)
    EditText txtDocumentNumber;
    @InjectView(R.id.imageDocumentNumber)
    ImageButton imageDocumentNumber;
    @InjectView(R.id.txtWareHouse)
    EditText txtWareHouse;
    @InjectView(R.id.imageWareHouse)
    ImageButton imageWareHouse;
    @InjectView(R.id.imageDepartment)
    ImageButton imageDepartment;
    @InjectView(R.id.txtOutCompany)
    EditText txtOutCompany;
    @InjectView(R.id.txtOutOrg)
    EditText txtOutOrg;
    @InjectView(R.id.txtInCompany)
    EditText txtInCompany;
    @InjectView(R.id.txtInOrg)
    EditText txtInOrg;
    @InjectView(R.id.txtBillDate)
    EditText txtBillDate;
    @InjectView(R.id.txtDepartment)
    EditText txtDepartment;
    @InjectView(R.id.btnScan)
    Button btnScan;
    @InjectView(R.id.btnSave)
    Button btnSave;
    @InjectView(R.id.btnExit)
    Button btnExit;

    String sBillCodes = "";
    String sBeginDate = "";
    String sEndDate = "";
    String CheckBillCode = "";
    String SaleFlg = "";
    private String tmpBillCode = "";
    private String tmpCustName = "";
    private String tmpBillDate = "";
    String CBILLID = "";
    String CBIZTYPE = "";
    String  CSALECORPID = "";
    String PK_CORP = MainLogin.objLog.CompanyCode;
    String  VDEF1 = "";
    String VDEF2 = "";
    String VDEF5 = "";
    String  CCUSTBASDOCID ="";
    String  CRECEIVECUSTBASID ="";
    String CCUSTOMERID = "";
    String CDISPATCHERID ="";
    String CWAREHOUSEID = "";//仓库id
    String NTOTALNUMBER = "";
    String CDPTID = "";//部门id
    String PK_CALBODY = "";//库存组织id
    String OutCompany = "";//调出公司
    String OutCompanyID = "";//调出公司ID
    String OutOrgID = "";
    String InCompany = "";//调入公司
    String InCompanyID = "";//调入公司ID
    String InOrgID = "";
    String OutOrgName = "";
    String InOrgName = "";
    String CINWHID = "";
    String PK_CUBASDOC = "";
    String COUTCOMPANYID="";
    String CINCOMPANYID="";
    HashMap<String, String> checkInfo = new HashMap<String, String>();
    private ArrayList<String> ScanedBarcode = new ArrayList<String>();
    private JSONObject jsonSaveHead = null;
    private JSONObject jsonBillHead = null;
    JSONObject table = null;
    JSONObject jsBody = null;
    JSONObject jsSerino = null;
    private JSONObject jsTotal=null;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multilateral_trade);
        this.setTitle("多角贸易");
//        initView();
        ButterKnife.inject(this);
//        txtCustomer.setFocusable(false);
//        txtCustomer.setFocusableInTouchMode(false);
        txtOutCompany.setFocusable(false);
        txtOutCompany.setFocusableInTouchMode(false);
        txtOutOrg.setFocusable(false);
        txtOutOrg.setFocusableInTouchMode(false);
        txtInCompany.setFocusable(false);
        txtInCompany.setFocusableInTouchMode(false);
        txtInOrg.setFocusable(false);
        txtInOrg.setFocusableInTouchMode(false);
        txtBillDate.setFocusable(false);
        txtBillDate.setFocusableInTouchMode(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 44) {
            if (resultCode == 4) {
                if (data != null) {
                        try {
                            sBillCodes = data.getStringExtra("sBillCodes");
                            sBeginDate = data.getStringExtra("sBeginDate");
                            sEndDate = data.getStringExtra("sEndDate");
                            String BillCodeKey = "";
                            btnSalesDelPDOrderClick(BillCodeKey);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }
        }
          else if (requestCode == 88) {
            switch (resultCode) {
                case 1:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            SerializableMap ResultMap = new SerializableMap();
                            ResultMap = (SerializableMap) bundle.get("ResultBillInfo");
                            Map<String, Object> mapBillInfo = ResultMap.getMap();
                            try {
                                jsonSaveHead = new JSONObject();
                                jsonSaveHead = Common.MapTOJSONOBject(mapBillInfo);
                                CheckBillCode = jsonSaveHead.getString("vbillcode");
                                CBILLID = jsonSaveHead.getString("cbillid");
                                checkInfo.put("BillCode",CheckBillCode);
                                SaleFlg = jsonSaveHead.getString("saleflg");
//                                BindingBillDetailInfo(mapBillInfo);
                            } catch (Exception e) {
                                Toast.makeText(MultilateralTrade.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            }
                            try {
                                //获得表头信息
                                GetBillHeadDetailInfo(SaleFlg);
                                BindingBillDetailInfo(mapBillInfo);
                                //获得表体信息
//                                GetBillBodyDetailInfo(SaleFlg);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        else if (requestCode==42){
            if (resultCode==24){
                if (data != null) {
                    try
                    {
                        Bundle bundle = data.getExtras();
                        String saleTotalBox = bundle.getString("box");
                        String saleSerinno = bundle.getString("serino");
                        String dbBody = bundle.getString("body");
                        CINWHID = bundle.getString("cinwhid");
                        ScanedBarcode = bundle.getStringArrayList("ScanedBarcode");
                        this.jsBody = new JSONObject(dbBody);
                        Log.d(TAG, "AAAAAA: "+jsBody.toString());
                        this.jsSerino = new JSONObject(saleSerinno);
                        Log.d(TAG, "AAAAAA: "+jsSerino.toString());
                    } catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(MultilateralTrade.this, e.getMessage() ,
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    }
                }
            }
        }
        //仓库列表信息
        else if (requestCode == 97) {
            if (resultCode == 13) {
                String warehousePK1 = data.getStringExtra("result1");
                String warehousecode = data.getStringExtra("result2");
                String warehouseName = data.getStringExtra("result3");
                CWAREHOUSEID = warehousePK1;
                txtWareHouse.setText(warehouseName);
                checkInfo.put("WHName",warehouseName);
            }

        }
        else if (requestCode == 96 && resultCode == 4) {
            String deptname   = data.getStringExtra("deptname");
            String pk_deptdoc = data.getStringExtra("pk_deptdoc");
            String deptcode   = data.getStringExtra("deptcode");
            CDPTID = pk_deptdoc;
//            department.requestFocus();
            txtDepartment.setText(deptname);
            checkInfo.put("Department", deptname);
        }
        //收发类别
        // 收发类别的回传数据 <----VlistRdcl.class
        else if (requestCode == 98 && resultCode == 2) {
            String code  = data.getStringExtra("Code");
            String name  = data.getStringExtra("Name");
            String AccID = data.getStringExtra("AccID");
            String RdIDA = data.getStringExtra("RdIDA");    //需要回传的id
            String RdIDB = data.getStringExtra("RdIDB");
            CDISPATCHERID = RdIDA;
            txtSendAndTake.setText(name);
            checkInfo.put("LeiBie", name);
        }
        //材料出库库存组织的回传数据 <----StorgListAct.class
        if (requestCode == 94 && resultCode == 6) {
            String pk_areacl  = data.getStringExtra("pk_areacl");
            String bodyname   = data.getStringExtra("bodyname");
            String pk_calbody = data.getStringExtra("pk_calbody");
            txtOrg.setText(bodyname);
            PK_CALBODY = pk_calbody;
            checkInfo.put("Organization", bodyname);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.imageDocumentNumber, R.id.imageWareHouse,R.id.imageOrg,R.id.imageDepartment,R.id.imageSendAndTake, R.id.btnScan, R.id.btnSave, R.id.btnExit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageDocumentNumber://单据号
                try{
                    Intent intent = new Intent(MultilateralTrade.this, SaleChooseTime.class);
                    startActivityForResult(intent, 44);
                    txtDocument.requestFocus();

                }catch (Exception e){
                    Toast.makeText(MultilateralTrade.this, WangLuoChuXiangWenTi, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                }
                break;
            case R.id.imageSendAndTake://收发类别
                btnRdclClick("");
                break;
            case R.id.imageOrg://库存组织
                btnReferSTOrgList();
                break;
            case R.id.imageWareHouse://仓库
                try {
                    //获取仓库
                    btnWarehouseClick();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MultilateralTrade.this, "获取仓库失败", Toast.LENGTH_SHORT).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                }
                break;
            case R.id.imageDepartment://部门
                btnReferDepartment();
            break;
            case R.id.btnScan:
                if (txtMultilateralTrade.getText().toString() == null || txtMultilateralTrade.getText().toString().equals("")) {
                    Toast.makeText(MultilateralTrade.this, "请输入来源单据",
                            Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                if (txtDocumentNumber.getText().toString() == null || txtDocumentNumber.getText().toString().equals("")) {
                    Toast.makeText(MultilateralTrade.this, "没有选择单据号",
                            Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                if (txtOrg.getText().toString() == null || txtOrg.getText().toString().equals("")) {
                    Toast.makeText(MultilateralTrade.this, "库存组织没有选择",
                            Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                if (txtWareHouse.getText().toString() == null || txtWareHouse.getText().toString().equals("")) {
                    Toast.makeText(MultilateralTrade.this, "仓库没有选择",
                            Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                SaleScan();
                break;
            case R.id.btnSave://保存按钮
                try
                {
                    if (jsBody==null||jsBody.equals("")){
                        Toast.makeText(MultilateralTrade.this, "没有要保存的数据",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;

                    }
                    if (txtMultilateralTrade.getText().toString() == null || txtMultilateralTrade.getText().toString().equals("")) {
                        Toast.makeText(MultilateralTrade.this, "请输入来源单据",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        txtDocumentNumber.requestFocus();
                        return;
                    }


                    if (txtDocumentNumber.getText().toString() == null || (!txtDocumentNumber.getText().toString().equals(checkInfo.get("BillCode")))) {
                        Toast.makeText(MultilateralTrade.this, "请选择单据号",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }
                    if (txtDocument.getText().toString() == null || txtDocument.getText().toString().equals("")) {
                        Toast.makeText(MultilateralTrade.this, "请输入出库单号",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        txtDocument.requestFocus();
                        return;
                    }


                    if (txtOrg.getText().toString() == null || (!txtOrg.getText().toString().equals(checkInfo.get("Organization")))) {
                        Toast.makeText(MultilateralTrade.this, "请选择库存组织",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }

                    if (txtWareHouse.getText().toString() == null || (!txtWareHouse.getText().toString().equals(checkInfo.get("WHName")))) {
                        Toast.makeText(MultilateralTrade.this, "请选择仓库",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }
                    if (txtSendAndTake.getText().toString() == null || txtSendAndTake.getText().toString().equals("")) {
                        Toast.makeText(MultilateralTrade.this, "请选择收发类别",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        txtSendAndTake.requestFocus();
                        return;
                    }
                    if (txtDepartment.getText().toString() == null || txtDepartment.getText().toString().equals("")) {
                        Toast.makeText(MultilateralTrade.this, "请选择部门",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        txtDepartment.requestFocus();
                        return;
                    }
                    SaveSaleOrder();
                    showProgressDialog();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MultilateralTrade.this, R.string.WangLuoChuXianWenTi ,
                            Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                }

                break;
            case R.id.btnExit:
                Exit();
                break;
        }
    }

    // 打开收发类别画面
    private void btnRdclClick(String Code) {
        Intent ViewGrid = new Intent(this, VlistRdcl.class);
        ViewGrid.putExtra("FunctionName", "GetRdcl");
        // ViewGrid.putExtra("AccID", "A");
        // ViewGrid.putExtra("rdflag", "1");
        // ViewGrid.putExtra("rdcode", "202");
        ViewGrid.putExtra("AccID", "");
        ViewGrid.putExtra("rdflag", "1");
        ViewGrid.putExtra("rdcode", "");
        startActivityForResult(ViewGrid, 98);
    }

    /**
     * 获取库存组织参照的网络请求
     */
    private void btnReferSTOrgList() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetSTOrgList");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("TableName", "STOrg");
        RequestThread requestThread = new RequestThread(parameter, mHandler, HANDER_STORG);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    /**
     * 获取部门列表信息的网络请求
     */
    private void btnReferDepartment() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetDeptList");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("TableName", "department");
        RequestThread requestThread = new RequestThread(parameter, mHandler, HANDER_DEPARTMENT);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    /**
 * 扫描按钮
 */

    private void SaleScan(){
        Intent intDeliveryScan = new Intent(MultilateralTrade.this, MultilateralTradeDetail.class);
        intDeliveryScan.putExtra("PK_CALBODY", PK_CALBODY);
        intDeliveryScan.putExtra("PK_CORP", PK_CORP);
        intDeliveryScan.putExtra("CBILLID", CBILLID);
        intDeliveryScan.putExtra("CWAREHOUSEID", CWAREHOUSEID);
        intDeliveryScan.putExtra("ScanType", txtMultilateralTrade.getText().toString());
        if (jsBody!=null){
            intDeliveryScan.putExtra("jsbody",jsBody.toString());
        }  if (jsSerino!=null){
            intDeliveryScan.putExtra("jsserino",jsSerino.toString());
        }
        intDeliveryScan.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
        startActivityForResult(intDeliveryScan, 42);
    }

    //退出按钮
    private void Exit() {
        if (jsSerino == null || jsSerino.length() < 1) {
            deleteInfo();
            finish();
        } else {
            AlertDialog.Builder bulider =
                    new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("数据未保存是否退出");
            bulider.setNegativeButton(R.string.QuXiao, null);
            bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    deleteInfo();
                    finish();
                }
            }).create().show();
        }

    }

    private void initView(){
//        txtCustomer.setFocusable(false);
//        txtCustomer.setFocusableInTouchMode(false);
        txtBillDate.setFocusable(false);
        txtBillDate.setFocusableInTouchMode(false);
    }

    /**
     * 打开订单列表画面
     */

    private void btnSalesDelPDOrderClick(String BillCodeKey) throws ParseException, IOException, JSONException {
            Intent ViewGrid = new Intent(this, GetInvBaseInfo.SaleBillInfoOrderList.class);
            ViewGrid.putExtra("FunctionName", "多角贸易");//GetSalereceiveHead
            ViewGrid.putExtra("sBeginDate", sBeginDate);
            ViewGrid.putExtra("sBillCodes", sBillCodes);
            ViewGrid.putExtra("sEndDate", sEndDate);
            ViewGrid.putExtra("whId",CWAREHOUSEID);
            startActivityForResult(ViewGrid, 88);

    }

    /**
     * 绑定订单表头信息
     */
    private boolean BindingBillDetailInfo(Map<String, Object> mapBillInfo) {
//        CBILLID = mapBillInfo.get("cbillid").toString();
        OutOrgID = mapBillInfo.get("coutcompanyid").toString();
        InOrgID = mapBillInfo.get("coincompanyid").toString();
        tmpBillCode = mapBillInfo.get("vbillcode").toString();
        OutOrgName = mapBillInfo.get("OutCompany").toString();
        InOrgName = mapBillInfo.get("InCompany").toString();
        tmpBillDate = mapBillInfo.get("BillDate").toString();
        txtOutCompany.setText(OutCompany);
        txtOutOrg.setText(OutOrgName);
        txtInCompany.setText(InCompany);
        txtInOrg.setText(InOrgName);
        txtDocumentNumber.setText(tmpBillCode);
        txtBillDate.setText(tmpBillDate);
        txtBillDate.setFocusable(false);
        txtBillDate.setFocusableInTouchMode(false);
        return true;
    }

    /**
     * 获取订单表头信息
     */

    private void GetBillHeadDetailInfo(String sSaleFlg) {
        JSONObject para = new JSONObject();
        try {
            para.put("FunctionName", "GetAdjustOrderBillHead");
//            para.put("BILLCODE", CheckBillCode);
            para.put("ORDERID", CBILLID);
//            para.put("COMPANYCODE",  MainLogin.objLog.CompanyCode);
//            para.put("WHID",CWAREHOUSEID);
//            para.put("BillCode", tmpBillCode);
//            para.put("STORGCODE",  MainLogin.objLog.STOrgCode);
            para.put("TableName", "dbHead");
        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            e2.printStackTrace();
            return;
        }

        JSONObject jas;
        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            jas = Common.DoHttpQuery(para, "CommonQuery", "");
            Log.d(TAG, "GetBillHeadDetailInfo: " + jas.toString());
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        try {
            if (jas == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            if (!jas.has("Status")) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            if (!jas.getBoolean("Status")) {
                String errMsg = "";
                if (jas.has("ErrMsg")) {
                    errMsg = jas.getString("ErrMsg");
                } else {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            jsonBillHead = new JSONObject();
            JSONArray jsarray = jas.getJSONArray("dbHead");
            JSONArray newHeadArray = new JSONArray();
            JSONObject newHeadJSON = null;
            for (int i = 0; i < jsarray.length(); i++) {
                JSONObject tempJso = jsarray.getJSONObject(i);
                newHeadJSON = new JSONObject();
                if (txtMultilateralTrade.getText().toString().equals("多角贸易")) {
                    newHeadJSON.put("VDEF1", tempJso.getString("vdef1"));
                    newHeadJSON.put("VDEF2", tempJso.getString("vdef2"));
                    newHeadJSON.put("outcorpname", tempJso.getString("outcorpname"));
                    newHeadJSON.put("incorpname", tempJso.getString("incorpname"));
                    newHeadJSON.put("coutcorpid", tempJso.getString("coutcorpid"));//调出公司ID
                    newHeadJSON.put("coutcbid", tempJso.getString("coutcbid"));//调出组织ID
                    newHeadJSON.put("outcalname", tempJso.getString("outcalname"));//调出组织名
                    newHeadJSON.put("cincorpid", tempJso.getString("cincorpid"));//调入公司ID
                    newHeadJSON.put("cincbid", tempJso.getString("cincbid"));//调入组织ID
                    newHeadJSON.put("incalname", tempJso.getString("incalname"));//调入组织名
                    newHeadJSON.put("billcode", tempJso.getString("vcode"));
//                    newHeadJSON.put("billcode", tmpBillCode);
                    OutCompanyID = tempJso.getString("coutcorpid");
                    CBIZTYPE = tempJso.getString("cbiztypeid");
//                    PK_CUBASDOC = tempJso.getString("pk_cubasdoc");
                    InCompanyID = tempJso.getString("cincorpid");
                    VDEF1 = tempJso.getString("vdef1").toString();
                    VDEF2 = tempJso.getString("vdef2").toString();
                    OutCompany = tempJso.getString("outcorpname");
                    InCompany = tempJso.getString("incorpname");
                }
                newHeadArray.put(newHeadJSON);
            }
            jsonBillHead.put("Status", true);
            jsonBillHead.put("dbHead", newHeadArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }

    }

    /**
     * 获取仓库信息
     * by Xuhu
     * 点击仓库列表参照
     */
    private void btnWarehouseClick() throws JSONException {
           JSONObject para = new JSONObject();
        try {
            para.put("FunctionName", "GetWareHouseList");
            para.put("CompanyCode", MainLogin.objLog.CompanyCode);
            para.put("STOrgCode", MainLogin.objLog.STOrgCode);
            para.put("TableName", "warehouse");
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
            Log.d(TAG, "btnWarehouseClick: " + rev.toString());
            if (rev == null) {
                Toast.makeText(this, "错误！网络通讯错误", Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            if (rev.getBoolean("Status")) {
                JSONArray val = rev.getJSONArray("warehouse");
                JSONObject temp = new JSONObject();
                temp.put("warehouse", val);
                Intent ViewGrid = new Intent(this, ListWarehouse.class);
                ViewGrid.putExtra("myData", temp.toString());
                startActivityForResult(ViewGrid, 97);
            } else {
                String Errmsg = rev.getString("ErrMsg");
                Toast.makeText(this, Errmsg, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        }


    }
    private  void deleteInfo() {
        try {
            jsTotal = null;
            jsSerino = null;
            jsBody = null;
            jsonSaveHead = null;
            jsonBillHead = null;
            changeAllEdToEmpty();
            txtDocument.requestFocus();
            //SaveOk();
            //            IniActivyMemor();// TODO: 2017/7/10 XUHU
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //保存数据
    private void SaveSaleOrder() throws JSONException,
            org.apache.http.ParseException, IOException {
        if (txtMultilateralTrade.getText().toString().equals("多角贸易")) {
            table = new JSONObject();
            JSONArray arrayss = null;
            JSONArray arrayMerge = null;
            arrayss = jsSerino.getJSONArray("Serino");
            arrayMerge = merge(arrayss);
            jsTotal = new JSONObject();
            jsTotal.put("Serino", arrayMerge);
            JSONObject map = new JSONObject();
            Double notnum = 0.0;
            JSONArray arrays = jsTotal.getJSONArray("Serino");
//            for (int i = 0; i < arrays.length(); i++) {
////                String totalnum = ((JSONObject) (arrays.get(i))).getString("box");
////                totalnum = Double.valueOf(totalnum).toString();
//                Double box = arrays.getJSONObject(i).getDouble("box");
//                DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
//                String totalBox = decimalFormat.format(box);
//                notnum += Double.valueOf(totalBox);
////                map.put("NNUMBER",notnum);
//                NTOTALNUMBER = decimalFormat.format(notnum);
//            }
//            ===表头
//            COTHERCALBODYID == CINCBID
//            COTHERCORPID == CINCORPID
//            COTHERWHID == B.CINVWH ID
//            COUTCALBODYID == COUTCBID
//            COUTCORPID == COUTCORPID
//            CWAREHOUSEID == 仓库ID
//            DBILLDATE
//            PK_CALBODY = '组织'
//            PK_CORP = '公司'
//            VBILLCODE = ''
//            VUSERDEF1 = VDEF1
//            VUSERDEF2 = VDEF2
//            pk_cubasdoc
//                    SaveOutAdjBillNew
            JSONObject tableHead = new JSONObject();
            tableHead.put("COTHERWHID", CINWHID);//其它仓库ID
            tableHead.put("DEPARTMENTID", CDPTID);//部门ID
            tableHead.put("VNOTE", "");//
            tableHead.put("CDISPATCHERID", CDISPATCHERID);//
            tableHead.put("FREPLENISHFLAG", "N");//
            tableHead.put("CBIZTYPE", CBIZTYPE);//
            tableHead.put("DBILLDATE", tmpBillDate);//其它仓库ID
            tableHead.put("COTHERCALBODYID", InOrgID);//对方库存组织PK
            tableHead.put("COTHERCORPID", InCompanyID);//对方公司ID
            tableHead.put("COUTCALBODYID", OutOrgID);//调出库存组织ID
            tableHead.put("COUTCORPID", OutCompanyID);//调出公司ID
            tableHead.put("CWAREHOUSEID", CWAREHOUSEID);//仓库id
            tableHead.put("PK_CALBODY",PK_CALBODY);//组织
            tableHead.put("PK_CORP", MainLogin.objLog.CompanyCode);//公司
            tableHead.put("VBILLCODE", txtDocument.getText().toString());//单据号
            String vd1 = Base64Encoder.encode(VDEF1.getBytes("gb2312"));
            String vd2 = Base64Encoder.encode(VDEF2.getBytes("gb2312"));
            tableHead.put("VUSERDEF1", vd1);//
            tableHead.put("VUSERDEF2", vd2);//
//            tableHead.put("RECEIVECODE", tmpBillCode);
//            tableHead.put("CBIZTYPE", CBIZTYPE);
            tableHead.put("CUSER", MainLogin.objLog.UserID);
//            tableHead.put("CRECEIPTTYE", "4331");
//            tableHead.put("CSALECORPID", CSALECORPID);
//            tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
//            tableHead.put("VBILLCODE", "");
            String login_user = MainLogin.objLog.LoginUser.toString();
            String cuserName = Base64Encoder.encode(login_user.getBytes("gb2312"));
            tableHead.put("CUSERNAME", cuserName);
//            String vd3 = Base64Encoder.encode(VDEF5.getBytes("gb2312"));
//            tableHead.put("VDEF5", vd3);
//            tableHead.put("NTOTALNUMBER", NTOTALNUMBER);
            table.put("Head", tableHead);
            JSONObject tableBody = new JSONObject();
            JSONArray bodyArray = new JSONArray();

            JSONArray bodys = jsBody.getJSONArray("dbBody");
            JSONArray arraysSerino = jsTotal.getJSONArray("Serino");
//            int y = 0;
            for (int j = 0; j < arraysSerino.length(); j++) {

                for (int i = 0; i < bodys.length(); i++) {

                    if (arraysSerino.getJSONObject(j).getString("invcode").toLowerCase().equals(
                            bodys.getJSONObject(i).getString("invcode"))) {
                        Double box = arraysSerino.getJSONObject(j).getDouble("box");
                        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                        String totalBox = decimalFormat.format(box);//format 返回的是字符串
                        JSONObject object = new JSONObject();
//
//                        CBODYWAREHOUSEID = '仓库'
//                        CFIRSTBILLBID == CBILL_BID
//                        CFIRSTBILLHID == CBILLID
//                        CFIRSTTYPE = CTYPECODE
//                        CINVBASID = CINVBASID
//                        CINVENTORYID = COUTINVID
//                        CQUOTEUNITID = CQUOTEUNITID
//                        CRECEIEVEID =收货单位
//                                CRECEIVEAREAID = PK_ARRIVEAREA
//                        CSOURCEBILLBID = CBILL_BID
//                        CSOURCEBILLHID = CBILLID
//                        CSOURCETYPE = CTYPECODE

//                        DBIZDATE = NOW
//                        DDELIVERDATE = NOW
//                        NSHOULDOUTNUM =
                        // TODO: 2017/8/6 库存组织
//                                PK_BODYCALBODY
//                        PK_CORP
//                                VBATCHCODE
//                        VFIRSTBILLCODE = VCODE
//                        VRECEIVEADDRESS =
//                                VSOURCEBILLCODE = VCODE
//                        VSOUREROWNO = CROWNO
                        object.put("CBODYWAREHOUSEID",CWAREHOUSEID);//库存仓库
                        object.put("CFIRSTBILLBID", bodys.getJSONObject(i).getString("cbill_bid"));//源头单据表体ID
                        object.put("CFIRSTBILLHID", bodys.getJSONObject(i).getString("cbillid"));//源头单据表头ID
                        object.put("CFIRSTTYPE", bodys.getJSONObject(i).getString("ctypecode"));//源头单据类型
                        object.put("CINVBASID", bodys.getJSONObject(i).getString("cinvbasid"));//存货基本ID
                        object.put("CINVENTORYID", bodys.getJSONObject(i).getString("coutinvid"));//存货ID
                        object.put("CQUOTEUNITID", bodys.getJSONObject(i).getString("cquoteunitid"));//报价计量单位ID
                        // TODO: 2017/8/6
                        object.put("CRECEIEVEID", bodys.getJSONObject(i).getString("pk_cubasdoc"));//收货单位todo
                        object.put("CRECEIVEAREAID", bodys.getJSONObject(i).getString("pk_arrivearea"));//收货地区
                        object.put("CSOURCEBILLBID", bodys.getJSONObject(i).getString("cbill_bid"));//来源单据表体序列号
                        object.put("CSOURCEBILLHID", bodys.getJSONObject(i).getString("cbillid"));//来源单据表头序列号
                        object.put("CSOURCETYPE", bodys.getJSONObject(i).getString("ctypecode"));
                        object.put("PK_AREACL", bodys.getJSONObject(i).getString("pk_areacl"));
                        // /来源单据类型
                        object.put("DBIZDATE", MainLogin.appTime);//
                        object.put("DDELIVERDATE", MainLogin.appTime);//
                        object.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nnum"));
                        object.put("NNUM", totalBox);
                        // TODO: 2017/8/7
                        object.put("PK_BODYCALBODY", OutOrgID);
                        object.put("PK_CORP", PK_CORP);//公司主键
                        object.put("VBATCHCODE",  arraysSerino.getJSONObject(j).getString("batch"));//批次
                        // TODO: 2017/8/6
//                        String add = bodys.getJSONObject(i).getString("vreceiveaddress");
//                        String adds = Base64Encoder.encode(add.getBytes("gb2312"));
//                        object.put("VRECEIVEADDRESS", adds);
//                        object.put("VRECEIVEADDRESS", bodys.getJSONObject(i).getString(""));//地址
                        object.put("VSOURCEBILLCODE", CheckBillCode);//来源单据号
                        object.put("VSOUREROWNO", bodys.getJSONObject(i).getString("crowno"));//单据行号


//                        object.put("CROWNO", bodys.getJSONObject(i).getString("crowno"));
                        object.put("VFREE4", arraysSerino.getJSONObject(j).getString("vfree4"));//海关手册号
//                        object.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
//                        object.put("VSOURCERECEIVECODE", tmpBillCode);
//                        object.put("VRECEIVEPOINTID", bodys.getJSONObject(i).getString("crecaddrnode"));
//                        object.put("CRECEIVECUSTID", bodys.getJSONObject(i).getString("creceiptcorpid"));
//                        object.put("CRECEIVEAREAID", bodys.getJSONObject(i).getString("creceiptareaid"));
////                        object.put("DDELIVERDATE", bodys.getJSONObject(i).getString("ddeliverdate"));
//                        object.put("CBIZTYPE", CBIZTYPE);//表头
//                        object.put("CCUSTBASDOCID", CCUSTBASDOCID);
//                        object.put("CCUSTMANDOCID", CCUSTOMERID);//表头customerID
//                        object.put("CINVBASDOCID", bodys.getJSONObject(i).getString("cinvbasdocid"));
//                        object.put("CINVMANDOCID", bodys.getJSONObject(i).getString("cinventoryid"));
//                        object.put("CRECEIVECUSTBASID", CCUSTBASDOCID);//自己获取
//                        object.put("CSENDCALBODYID", bodys.getJSONObject(i).getString("cadvisecalbodyid"));
//                        object.put("CSENDWAREID", CWAREHOUSEID);//仓库
//                        object.put("CSOURCEBILLBODYID", bodys.getJSONObject(i).getString("corder_bid"));
//                        object.put("CSOURCEBILLID", bodys.getJSONObject(i).getString("csaleid"));
//                        object.put("NNUMBER", totalBox);
//                        object.put("PK_SENDCORP", bodys.getJSONObject(i).getString("pk_corp"));
//                        object.put("VBATCHCODE", arraysSerino.getJSONObject(j).getString("batch"));
////                        String add = bodys.getJSONObject(i).getString("vreceiveaddress");
////                        String adds = Base64Encoder.encode(add.getBytes("gb2312"));
//                        object.put("VRECEIVEADDRESS", adds);
//                        object.put("VRECEIVEPERSON", MainLogin.objLog.LoginUser);
                        bodyArray.put(object);
//                        y++;
                    }
                }
            }
            tableBody.put("ScanDetails", bodyArray);
            table.put("Body", tableBody);
            table.put("GUIDS", UUID.randomUUID().toString());
            Log.d(TAG, "SaveSaleOrder: " + MainLogin.appTime);
            table.put("OPDATE", MainLogin.appTime);
            Log.d(TAG, "XXXXXX: " + table.toString());
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            SaveThread saveThread = new SaveThread(table, "SaveAdjOutBillNew", mHandler, HANDER_SAVE_RESULT);
            Thread thread = new Thread(saveThread);
            thread.start();
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
                case HANDER_DEPARTMENT:
                    JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json.getBoolean("Status")) {
                            JSONArray  val  = json.getJSONArray("department");
                            JSONObject temp = new JSONObject();
                            temp.put("department", val);
                            Intent ViewGrid = new Intent(MultilateralTrade.this, DepartmentListAct.class);
                            ViewGrid.putExtra("myData", temp.toString());
                            startActivityForResult(ViewGrid, 96);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDER_STORG:
                    JSONObject storg = (JSONObject) msg.obj;
                    try {
                        if (storg.getBoolean("Status")) {
                            JSONArray  val  = storg.getJSONArray("STOrg");
                            JSONObject temp = new JSONObject();
                            temp.put("STOrg", val);
                            Intent StorgList = new Intent(MultilateralTrade.this, StorgListAct.class);
                            StorgList.putExtra("STOrg", temp.toString());
                            startActivityForResult(StorgList, 94);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDER_SAVE_RESULT:
                    JSONObject saveResult = (JSONObject) msg.obj;
                    try {
                        if (saveResult != null) {
                            if (saveResult.getBoolean("Status")) {
                                Log.d(TAG, "保存" + saveResult.toString());
                                showResultDialog(MultilateralTrade.this, saveResult.getString("ErrMsg"));
                                deleteInfo();
                                Log.d(TAG, "messageTrue" + saveResult.getString("ErrMsg"));
                            } else {
                                showResultDialog(MultilateralTrade.this, saveResult.getString("ErrMsg"));
                                Log.d(TAG, "messageFALSE" + saveResult.getString("ErrMsg"));
                            }
                        } else {
                            showResultDialog(MultilateralTrade.this, "数据提交失败!");
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            return;
                        }
                        progressDialogDismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * progressDialog 消失
     */
    private void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 清空
     */
    private  void changeAllEdToEmpty(){
        txtDocument.setText("");
        txtSendAndTake.setText("");
        txtDepartment.setText("");
        txtInCompany.setText("");
        txtOutCompany.setText("");
        txtOutOrg.setText("");
        txtInOrg.setText("");
        txtWareHouse.setText("");
        txtDocumentNumber.setText("");
        txtBillDate.setText("");
        txtOrg.setText("");
//        txtCustomer.setText("");

    }

    /**
     * 根据批次sku相同合并数量
     */
    public  JSONArray merge(JSONArray array) {

        JSONArray arrayTemp = new JSONArray();
        int num = 0;
        for(int i = 0;i < array.length();i++) {
            if (num == 0) {
                try {
                    arrayTemp.put(array.get(i));
                    num++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    int numJ = 0;
                    Log.d(TAG, "Merge: "+arrayTemp.length());
                    for (int j = 0; j < arrayTemp.length(); j++) {
                        JSONObject newJsonObjectI = (JSONObject) array.get(i);
                        JSONObject newJsonObjectJ = (JSONObject) arrayTemp.get(j);
                        String invcode = newJsonObjectI.get("invcode").toString();
                        String invname = newJsonObjectI.get("invname").toString();
                        String batch = newJsonObjectI.get("batch").toString();
                        String box = newJsonObjectI.get("box").toString();
                        String sno = newJsonObjectI.get("sno").toString();
                        String invtype = newJsonObjectI.get("invtype").toString();
                        String invspec = newJsonObjectI.get("invspec").toString();
                        String serino = newJsonObjectI.get("serino").toString();
                        String vfree4 = newJsonObjectI.get("vfree4").toString();

                        String invcodeJ = newJsonObjectJ.get("invcode").toString();
                        String batchJ = newJsonObjectJ.get("batch").toString();
                        String boxJ = newJsonObjectJ.get("box").toString();

                        if (invcode.equals(invcodeJ)&&batch.equals(batchJ)) {
                            double newValue = Double.parseDouble(box) + Double.parseDouble(boxJ);
                            JSONObject newObject = new JSONObject();
//                            if (Build.VERSION.SDK_INT >= 19) {
//                               arrayTemp.remove(j);
//                            }else{
//
//                            }
                            Utils.removeJsonArray(j,arrayTemp);
                            newObject.put("invcode", invcode);
                            newObject.put("batch", batch);
                            newObject.put("invname", invname);
                            newObject.put("serino", serino);
                            newObject.put("sno", sno);
                            newObject.put("invtype", invtype);
                            newObject.put("invspec", invspec);
                            newObject.put("vfree4", vfree4);
                            newObject.put("box", String.valueOf(newValue));
                            arrayTemp.put(newObject);
                            break;
                        }

                        numJ++;

                        String a = numJ+"";
                        Log.d(TAG, "Merge: "+a);


                    }
                    if (numJ - 1 == arrayTemp.length() - 1) {
                        arrayTemp.put(array.get(i));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        Log.d(TAG, "DDDDD: "+arrayTemp.toString());
        return arrayTemp;
    }


    /**
     * 保存弹出提醒
     */

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(MultilateralTrade.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        progressDialog.setTitle("保存单据");
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

}
