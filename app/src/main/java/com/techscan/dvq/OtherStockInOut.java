package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.R.id;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class OtherStockInOut extends Activity {

    ImageButton btnBorwer;
    Button btnSave;
    ImageButton btnType;
    Button btnScan;
    Button btnExit;

    EditText txtOrderType;
    EditText txtOrderNo;
    EditText txtWarehouse;
    String GUIDIn = "";
    String GUIDOut = "";

    int OrderTypeIndex = -1;

    String m_OrderType = "";
    String m_OrderID = "";
    String m_OrderNo = "";
    String m_AccID = "";
    String m_WarehouseID = "";
    String m_Crop = "";

    private AlertDialog SelectButton = null;
    private String[] BillTypeNameList = null;
    private String[] BillTypeCodeList = null;


    JSONObject jsHead;
    JSONObject jsBody;
    JSONObject jsBoxTotal;
    JSONObject jsSerino;

    private writeTxt writeTxt;        //保存LOG文件
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID

    private ButtonOnClick buttonOnClick = new ButtonOnClick(0);


    private void IniMy() {
        txtOrderType.setText("");
        txtOrderNo.setText("");
        txtWarehouse.setText("");

        jsHead = null;
        jsBody = null;
        jsBoxTotal = null;
        jsSerino = null;
        this.m_AccID = "";
        this.m_Crop = "";
        this.m_OrderID = "";
        this.m_OrderNo = "";
        this.m_OrderType = "";

        OrderTypeIndex = -1;
    }

    //private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_stock_in_out);


        //添加手工代码
        btnBorwer = (ImageButton) findViewById(R.id.btnOtOrderNo);
        btnSave = (Button) findViewById(R.id.btnOtinSave);
        btnType = (ImageButton) findViewById(R.id.btnOtType);
        btnScan = (Button) findViewById(R.id.btnOtInScan);
        btnExit = (Button) findViewById(R.id.btnOtInExit);

        txtOrderType = (EditText) findViewById(R.id.txtOtOrderType);
        txtOrderNo = (EditText) findViewById(R.id.txtOtOrderNo);
        txtWarehouse = (EditText) findViewById(R.id.txtOtWarehouse);


        btnBorwer.setOnClickListener(myListner);
        btnSave.setOnClickListener(myListner);
        btnType.setOnClickListener(myListner);
        btnScan.setOnClickListener(myListner);
        btnExit.setOnClickListener(myListner);

        txtOrderType.setOnKeyListener(myTxtListener);
        txtOrderNo.setOnKeyListener(myTxtListener);
        txtWarehouse.setOnKeyListener(myTxtListener);


        txtOrderType.setFocusable(false);
        txtWarehouse.setFocusable(false);

        btnBorwer.setFocusable(false);
        btnSave.setFocusable(false);
        btnType.setFocusable(false);
        btnScan.setFocusable(false);
        btnExit.setFocusable(false);

        SetBillType();//设置单据类型列表

        //this.m_Crop = MainLogin.objLog.CompanyCode;

//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        this.txtOrderNo.requestFocus();
        this.setTitle("特殊业务");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.other_stock_in_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Changeline();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static AlertDialog SelectLine = null;
    private buttonOnClickC buttonOnClickC = new buttonOnClickC(0);
    static String[] LNameList = new String[2];

    private void Changeline() {

        int lsindex = 0;
        if (Common.lsUrl.equals(MainLogin.objLog.LoginString2)) {
            lsindex = 1;
        }

        LNameList[0] = getString(R.string.ZhuWebDiZhi);
        LNameList[1] = getString(R.string.FuWebDiZhi);

        SelectLine = new AlertDialog.Builder(this).setTitle(R.string.QieHuanDiZhi)
                .setSingleChoiceItems(LNameList, lsindex, buttonOnClickC)
                .setPositiveButton(R.string.QueRen, buttonOnClickC)
                .setNegativeButton(R.string.QuXiao, buttonOnClickC).show();
    }

    private void ShowLineChange(String WebName) {

        String CommonUrl = Common.lsUrl;
        CommonUrl = CommonUrl.replace("/service/nihao", "");

        AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
                R.string.QieHuanChengGong).setMessage(R.string.YiJingQieHuanZhi + WebName + "\r\n" + CommonUrl);

        bulider.setPositiveButton(R.string.QueRen, null).setCancelable(false).create()
                .show();
        return;
    }

    private class buttonOnClickC implements DialogInterface.OnClickListener {
        public int index;

        public buttonOnClickC(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
            } else {

                if (dialog.equals(SelectLine)) {
                    if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                        if (index == 0) {

                            Common.lsUrl = MainLogin.objLog.LoginString;
                            ShowLineChange(LNameList[0]);
                            System.gc();
                        } else if (index == 1) {
                            Common.lsUrl = MainLogin.objLog.LoginString2;
                            ShowLineChange(LNameList[1]);
                            System.gc();
                        }
                        return;
                    } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                        return;
                    }
                }
            }
        }
    }


    private class ButtonOnClick implements DialogInterface.OnClickListener {
        public int index;

        public ButtonOnClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
                dialog.cancel();
            } else {
                return;
            }
            if (dialog.equals(SelectButton)) {
//				BillTypeNameList[0]="组装";
//				BillTypeNameList[1]="拆卸";
//				BillTypeNameList[2]="形态转换";

                if (index == 0) {
                    if (!Common.CheckUserRole("", "", "40081004")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(OtherStockInOut.this, "没有使用该模块的权限", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else if (index == 1) {
                    if (!Common.CheckUserRole("", "", "40081006")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(OtherStockInOut.this, "没有使用该模块的权限", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else if (index == 2) {
                    if (!Common.CheckUserRole("", "", "40081008")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(OtherStockInOut.this, "没有使用该模块的权限", Toast.LENGTH_LONG).show();
                        return;
                    }
                }


                txtOrderNo.setText("");
                txtWarehouse.setText("");

                m_AccID = "";
                m_OrderID = "";
                m_WarehouseID = "";
                m_OrderNo = "";
                m_Crop = "";

                txtOrderType.setText(BillTypeNameList[index].toString());
                OrderTypeIndex = index;
                txtOrderNo.requestFocus();
            }
        }
    }

    private void showSingleChoiceDialog() {
        if (jsBoxTotal != null && jsBoxTotal.has("BoxList")) {
            Toast.makeText(this, "该任务已经被扫描,无法修改单据类型。若要修改单据类型请先清除扫描明细", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }
        SelectButton = new AlertDialog.Builder(this).setTitle("选择单据类型").setSingleChoiceItems(
                BillTypeNameList, -1, buttonOnClick).setNegativeButton
                (R.string.QuXiao, buttonOnClick).show();
    }

    private void SetBillType() {
        BillTypeNameList = new String[3];//设置单据类型数量
        //开始设置单据类型名字
        BillTypeNameList[0] = "组装";
        BillTypeNameList[1] = "拆卸";
        BillTypeNameList[2] = "形态转换";

        BillTypeCodeList = new String[3];//设置单据类型数量
        //开始设置单据类型编码
        BillTypeCodeList[0] = "4L";
        BillTypeCodeList[1] = "4M";
        BillTypeCodeList[2] = "4N";

    }

    JSONObject saveOthIn;
    JSONObject saveOthOut;

    private void BuildSaveDate() throws JSONException {
        saveOthIn = new JSONObject();
        saveOthOut = new JSONObject();


        JSONObject inHead = new JSONObject();
        JSONObject outHead = new JSONObject();

        saveOthIn.put("Head", inHead);
        saveOthOut.put("Head", outHead);

        //获取guid

        if (uploadGuid == null) {
            uploadGuid = UUID.randomUUID();
            GUIDIn = uploadGuid.toString();
            uploadGuid = null;
        }

        if (uploadGuid == null) {
            uploadGuid = UUID.randomUUID();
            GUIDOut = uploadGuid.toString();
        }
        saveOthIn.put("GUIDS", GUIDIn);
        saveOthOut.put("GUIDS", GUIDOut);

        inHead.put("CBILLTYPECODE", "4A");            //单据类型
        outHead.put("CBILLTYPECODE", "4I");

        inHead.put("CWAREHOUSEID", m_WarehouseID);                //仓库
        outHead.put("CWAREHOUSEID", m_WarehouseID);

        inHead.put("PK_CALBODY", MainLogin.objLog.STOrgCode);                //库存组织
        outHead.put("PK_CALBODY", MainLogin.objLog.STOrgCode);


        inHead.put("PK_CORP", m_Crop);                //库存组织
        outHead.put("PK_CORP", m_Crop);


        if (this.m_AccID.equals("A")) {
            inHead.put("CBIZID", MainLogin.objLog.UserID);                //业务员
            outHead.put("CBIZID", MainLogin.objLog.UserID);

            inHead.put("CLASTMODIID", MainLogin.objLog.UserID);                //最后修改人
            outHead.put("CLASTMODIID", MainLogin.objLog.UserID);

            inHead.put("COPERATORID", MainLogin.objLog.UserID);                //制单人
            outHead.put("COPERATORID", MainLogin.objLog.UserID);
        } else {
            inHead.put("CBIZID", MainLogin.objLog.UserIDB);                //业务员
            outHead.put("CBIZID", MainLogin.objLog.UserIDB);

            inHead.put("CLASTMODIID", MainLogin.objLog.UserIDB);                //最后修改人
            outHead.put("CLASTMODIID", MainLogin.objLog.UserIDB);

            inHead.put("COPERATORID", MainLogin.objLog.UserIDB);                //制单人
            outHead.put("COPERATORID", MainLogin.objLog.UserIDB);
        }

        m_OrderType = (BillTypeCodeList[OrderTypeIndex]).toString();

        if (this.m_AccID.equals("A")) {
            if (this.m_OrderType.equals("4N")) {
                inHead.put("CDISPATCHERID", "0001AA100000000003UR");                //106收发类别
                outHead.put("CDISPATCHERID", "0001AA100000000003VH");
            } else {
                inHead.put("CDISPATCHERID", "0001AA100000000003UT");                //108收发类别
                outHead.put("CDISPATCHERID", "0001AA100000000003VJ");                //208
            }
        } else {
            if (this.m_OrderType.equals("4N")) {
                inHead.put("CDISPATCHERID", "0001DD10000000000XR1");                //106收发类别
                outHead.put("CDISPATCHERID", "0001DD10000000000XRC");
            } else {
                inHead.put("CDISPATCHERID", "0001DD10000000000XR3");                //108收发类别
                outHead.put("CDISPATCHERID", "0001DD10000000000XRE");                //208
            }
        }

        //================================表体


        JSONArray bodys = jsBody.getJSONArray("PurBody");
        JSONArray snList = jsSerino.getJSONArray("Serino");


        JSONObject sninBody = new JSONObject();
        JSONObject snoutBody = new JSONObject();
        //处理序列号表


        JSONArray inBodys = new JSONArray();
        JSONArray outBodys = new JSONArray();

        saveOthIn.put("Body", inBodys);
        saveOthOut.put("Body", outBodys);


        JSONArray inSn = new JSONArray();
        JSONArray outSn = new JSONArray();


        saveOthIn.put("Serino", inSn);
        saveOthOut.put("Serino", outSn);

        for (int j = 0; j < snList.length(); j++) {

            String sSerial = ((JSONObject) (snList.get(j))).getString("sno");
            String sBatch = ((JSONObject) (snList.get(j))).getString("batch");
            String sInvCode = ((JSONObject) (snList.get(j))).getString("invcode");
            String serino = ((JSONObject) (snList.get(j))).getString("serino");
            serino = serino.replace("\n", "");
            String totalnum = ((JSONObject) (snList.get(j))).getString("totalnum");
            totalnum = Integer.valueOf(totalnum).toString();
            String sbarcode = serino;
            String sfree1 = ((JSONObject) (snList.get(j))).getString("vfree1");
            String rowNo = ((JSONObject) (snList.get(j))).getString("rowno");
            String posID = ((JSONObject) (snList.get(j))).getString("posID");
            String WHID = ((JSONObject) (snList.get(j))).getString("WHID");
            String box = ((JSONObject) (snList.get(j))).getString("box");

            box = Integer.valueOf(box).toString();


            if (box.equals(totalnum)) {
                JSONObject map = new JSONObject();
                map.put("invcode", sInvCode);
                map.put("batch", sBatch);
                map.put("sno", sSerial);
                map.put("free1", sfree1);
                map.put("barcode", sbarcode);
                map.put("totalnum", totalnum);
                map.put("posid", posID);
                map.put("whid", WHID);
                map.put("rowno", rowNo);

                if (this.m_OrderType.equals("4L"))//组装
                {
                    if (snList.getJSONObject(j).getString("fbillrowflag").equals("0")) {
                        //入库
                        inSn.put(map);
                    } else {
                        //出库
                        outSn.put(map);
                    }
                } else if (this.m_OrderType.equals("4M"))//拆卸
                {
                    if (snList.getJSONObject(j).getString("fbillrowflag").equals("1")) {
                        //入库
                        inSn.put(map);
                    } else {
                        //出库
                        outSn.put(map);
                    }
                } else if (this.m_OrderType.equals("4N"))//形态转换
                {
                    if (snList.getJSONObject(j).getString("fbillrowflag").equals("3")) {
                        //入库
                        inSn.put(map);
                    } else {
                        //出库
                        outSn.put(map);
                    }
                }
            }

        }

        sninBody.put("Serino", inSn);
        snoutBody.put("Serino", outSn);
        //保存表体结构


        for (int x = 0; x < inSn.length(); x++) {


            String InvCode = inSn.getJSONObject(x).getString("invcode");
            String RowNo = inSn.getJSONObject(x).getString("rowno");

            for (int i = 0; i < bodys.length(); i++) {
                if (bodys.getJSONObject(i).getString("invcode").equals(InvCode) && bodys.getJSONObject(i).getString("crowno").equals(RowNo)) {
                    JSONObject inbody = new JSONObject();

                    inbody.put("POSITIONID", inSn.getJSONObject(x).getString("posid"));
                    inbody.put("VFREE1", inSn.getJSONObject(x).getString("free1"));
                    inbody.put("VBATCHCODE", inSn.getJSONObject(x).getString("batch"));

                    inbody.put("CBODYWAREHOUSEID", this.m_WarehouseID);        //仓库
                    inbody.put("CBODYBILLTYPECODE", "4A");//其它入库
                    inbody.put("INVCODE", bodys.getJSONObject(i).getString("invcode"));
                    inbody.put("CINVBASID", bodys.getJSONObject(i).getString("pk_invbasdoc"));
                    inbody.put("CINVENTORYID", bodys.getJSONObject(i).getString("cinventoryid"));
                    inbody.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nshouldinnum"));
                    inbody.put("CSOURCETYPE", this.m_OrderType);
                    inbody.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
                    inbody.put("VSOURCEBILLCODE", this.m_OrderNo);
                    inbody.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));

                    inbody.put("CSOURCEBILLHID", bodys.getJSONObject(i).getString("csourcebillhid"));
                    inbody.put("CSOURCEBILLBID", bodys.getJSONObject(i).getString("csourcebillbid"));

                    inBodys.put(inbody);
                }
            }

        }

        for (int x = 0; x < outSn.length(); x++) {


            String InvCode = outSn.getJSONObject(x).getString("invcode");
            String RowNo = outSn.getJSONObject(x).getString("rowno");

            for (int i = 0; i < bodys.length(); i++) {
                if (bodys.getJSONObject(i).getString("invcode").equals(InvCode) && bodys.getJSONObject(i).getString("crowno").equals(RowNo)) {
                    JSONObject outbody = new JSONObject();
                    outbody.put("POSITIONID", outSn.getJSONObject(x).getString("posid"));
                    outbody.put("VFREE1", outSn.getJSONObject(x).getString("free1"));
                    outbody.put("VBATCHCODE", outSn.getJSONObject(x).getString("batch"));
                    outbody.put("CBODYWAREHOUSEID", this.m_WarehouseID);        //仓库
                    outbody.put("CBODYBILLTYPECODE", "4I");//其它出库
                    outbody.put("INVCODE", bodys.getJSONObject(i).getString("invcode"));
                    outbody.put("CINVBASID", bodys.getJSONObject(i).getString("pk_invbasdoc"));
                    outbody.put("CINVENTORYID", bodys.getJSONObject(i).getString("cinventoryid"));
                    outbody.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nshouldinnum"));
                    outbody.put("CSOURCETYPE", this.m_OrderType);
                    outbody.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
                    outbody.put("VSOURCEBILLCODE", this.m_OrderNo);
                    outbody.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));

                    outbody.put("CSOURCEBILLHID", bodys.getJSONObject(i).getString("csourcebillhid"));
                    outbody.put("CSOURCEBILLBID", bodys.getJSONObject(i).getString("csourcebillbid"));

                    outBodys.put(outbody);
                }
            }
        }


        //保存表体结构
//		for(int i = 0;i<bodys.length();i++)
//		{
//			JSONObject inbody = new JSONObject();
//			JSONObject outbody = new JSONObject();
//			if(this.m_OrderType.equals("4L"))
//			{
//				//母件入库
//				if(bodys.getJSONObject(i).getString("fbillrowflag").equals("0"))
//				{
//					inbody.put("CBODYWAREHOUSEID", this.m_WarehouseID);		//仓库
//					inbody.put("CBODYBILLTYPECODE", "4A");//其它入库
//					inbody.put("INVCODE", bodys.getJSONObject(i).getString("invcode"));
//					inbody.put("CINVBASID", bodys.getJSONObject(i).getString("pk_invbasdoc"));
//					inbody.put("CINVENTORYID", bodys.getJSONObject(i).getString("cinventoryid"));
//					inbody.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nshouldinnum"));
//					inbody.put("CSOURCETYPE", this.m_OrderType);
//					inbody.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
//					inbody.put("VSOURCEBILLCODE", this.m_OrderNo);
//					inbody.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
//					inbody.put("POSITIONID",bodys.getJSONObject(i).getString("position"));
//					inbody.put("VFREE1", bodys.getJSONObject(i).getString("vfree1"));
//					inbody.put("VBATCHCODE", bodys.getJSONObject(i).getString("vbatchcode"));
//					
//					
//
//					inbody.put("CSOURCEBILLHID",  bodys.getJSONObject(i).getString("csourcebillhid"));
//					inbody.put("CSOURCEBILLBID",  bodys.getJSONObject(i).getString("csourcebillbid"));
//					
//					inBodys.put(inbody);
//				}
//				else
//				{
//
//					outbody.put("CBODYWAREHOUSEID", this.m_WarehouseID);		//仓库
//					outbody.put("CBODYBILLTYPECODE", "4I");//其它出库
//					outbody.put("INVCODE", bodys.getJSONObject(i).getString("invcode"));
//					outbody.put("CINVBASID", bodys.getJSONObject(i).getString("pk_invbasdoc"));
//					outbody.put("CINVENTORYID", bodys.getJSONObject(i).getString("cinventoryid"));
//					outbody.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nshouldinnum"));
//					outbody.put("CSOURCETYPE", this.m_OrderType);
//					outbody.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
//					outbody.put("VSOURCEBILLCODE", this.m_OrderNo);
//					outbody.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
//					outbody.put("POSITIONID",bodys.getJSONObject(i).getString("position"));
//					outbody.put("VFREE1", bodys.getJSONObject(i).getString("vfree1"));
//					outbody.put("VBATCHCODE", bodys.getJSONObject(i).getString("vbatchcode"));
//					outbody.put("CSOURCEBILLHID",  bodys.getJSONObject(i).getString("csourcebillhid"));
//					outbody.put("CSOURCEBILLBID",  bodys.getJSONObject(i).getString("csourcebillbid"));
//					
//					outBodys.put(outbody);
//				}
//			}
//			else if(this.m_OrderType.equals("4M"))
//			{
//				//母件出库
//				
//				if(bodys.getJSONObject(i).getString("fbillrowflag").equals("0"))
//				{
//					outbody.put("CBODYWAREHOUSEID", this.m_WarehouseID);		//仓库
//					outbody.put("CBODYBILLTYPECODE", "4I");//其它出库
//					outbody.put("INVCODE", bodys.getJSONObject(i).getString("invcode"));
//					outbody.put("CINVBASID", bodys.getJSONObject(i).getString("pk_invbasdoc"));
//					outbody.put("CINVENTORYID", bodys.getJSONObject(i).getString("cinventoryid"));
//					outbody.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nshouldinnum"));
//					outbody.put("CSOURCETYPE", this.m_OrderType);
//					outbody.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
//					outbody.put("VSOURCEBILLCODE", this.m_OrderNo);
//					outbody.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
//					outbody.put("POSITIONID",bodys.getJSONObject(i).getString("position"));
//					outbody.put("VFREE1", bodys.getJSONObject(i).getString("vfree1"));
//					outbody.put("VBATCHCODE", bodys.getJSONObject(i).getString("vbatchcode"));
//					
//					outbody.put("CSOURCEBILLHID",  bodys.getJSONObject(i).getString("csourcebillhid"));
//					outbody.put("CSOURCEBILLBID",  bodys.getJSONObject(i).getString("csourcebillbid"));
//					outBodys.put(outbody);
//				}
//				else
//				{
//					inbody.put("CBODYWAREHOUSEID", this.m_WarehouseID);		//仓库
//					inbody.put("CBODYBILLTYPECODE", "4A");//其它入库
//					inbody.put("INVCODE", bodys.getJSONObject(i).getString("invcode"));
//					inbody.put("CINVBASID", bodys.getJSONObject(i).getString("pk_invbasdoc"));
//					inbody.put("CINVENTORYID", bodys.getJSONObject(i).getString("cinventoryid"));
//					inbody.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nshouldinnum"));
//					inbody.put("CSOURCETYPE", this.m_OrderType);
//					inbody.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
//					inbody.put("VSOURCEBILLCODE", this.m_OrderNo);
//					inbody.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
//					inbody.put("POSITIONID",bodys.getJSONObject(i).getString("position"));
//					inbody.put("VFREE1", bodys.getJSONObject(i).getString("vfree1"));
//					inbody.put("VBATCHCODE", bodys.getJSONObject(i).getString("vbatchcode"));
//					inbody.put("CSOURCEBILLHID",  bodys.getJSONObject(i).getString("csourcebillhid"));
//					inbody.put("CSOURCEBILLBID",  bodys.getJSONObject(i).getString("csourcebillbid"));
//									
//					inBodys.put(inbody);
//				}
//			}
//			else if(this.m_OrderType.equals("4N"))
//			{
//				//转换前出库
//				if(bodys.getJSONObject(i).getString("fbillrowflag").equals("2"))
//				{
//					outbody.put("CBODYWAREHOUSEID", this.m_WarehouseID);		//仓库
//					outbody.put("CBODYBILLTYPECODE", "4I");//其它出库
//					outbody.put("INVCODE", bodys.getJSONObject(i).getString("invcode"));
//					outbody.put("CINVBASID", bodys.getJSONObject(i).getString("pk_invbasdoc"));
//					outbody.put("CINVENTORYID", bodys.getJSONObject(i).getString("cinventoryid"));
//					outbody.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nshouldinnum"));
//					outbody.put("CSOURCETYPE", this.m_OrderType);
//					outbody.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
//					outbody.put("VSOURCEBILLCODE", this.m_OrderNo);
//					outbody.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
//					outbody.put("POSITIONID",bodys.getJSONObject(i).getString("position"));
//					outbody.put("VFREE1", bodys.getJSONObject(i).getString("vfree1"));
//					outbody.put("VBATCHCODE", bodys.getJSONObject(i).getString("vbatchcode"));
//					outbody.put("CSOURCEBILLHID",  bodys.getJSONObject(i).getString("csourcebillhid"));
//					outbody.put("CSOURCEBILLBID",  bodys.getJSONObject(i).getString("csourcebillbid"));
//					outBodys.put(outbody);
//				}
//				else
//				{
//					inbody.put("CBODYWAREHOUSEID", this.m_WarehouseID);		//仓库
//					inbody.put("CBODYBILLTYPECODE", "4A");//其它入库
//					inbody.put("INVCODE", bodys.getJSONObject(i).getString("invcode"));
//					inbody.put("CINVBASID", bodys.getJSONObject(i).getString("pk_invbasdoc"));
//					inbody.put("CINVENTORYID", bodys.getJSONObject(i).getString("cinventoryid"));
//					inbody.put("NSHOULDOUTNUM", bodys.getJSONObject(i).getString("nshouldinnum"));
//					inbody.put("CSOURCETYPE", this.m_OrderType);
//					inbody.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
//					inbody.put("VSOURCEBILLCODE", this.m_OrderNo);
//					inbody.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
//					inbody.put("POSITIONID",bodys.getJSONObject(i).getString("position"));
//					inbody.put("VFREE1", bodys.getJSONObject(i).getString("vfree1"));
//					inbody.put("VBATCHCODE", bodys.getJSONObject(i).getString("vbatchcode"));
//					inbody.put("CSOURCEBILLHID",  bodys.getJSONObject(i).getString("csourcebillhid"));
//					inbody.put("CSOURCEBILLBID",  bodys.getJSONObject(i).getString("csourcebillbid"));
//									
//					inBodys.put(inbody);
//				}
//			}
//		}
    }

    private void Save() throws JSONException, ParseException, IOException {
        if (this.m_OrderID == null ||
                this.m_OrderID.equals("")) {
            Toast.makeText(this, "先选择单据",
                    Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }
        if (jsBody == null || !jsBody.has("PurBody")) {
            Toast.makeText(this, R.string.MeiYouXuYaoBaoCunDeSaoMiaoJiLu, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

        JSONArray bodys = jsBody.getJSONArray("PurBody");
        boolean isNotDone = false;

        try {
            if (!CheckBox())
                return;
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            e.printStackTrace();
        }

        for (int i = 0; i < bodys.length(); i++) {
            if (bodys.getJSONObject(i).getInt("doneqty")
                    < bodys.getJSONObject(i).getInt("nshouldinnum")) {
                isNotDone = true;
                break;
            }
        }

        if (isNotDone) {
            Toast.makeText(this, "请一次扫描完所有的货品后保存",
                    Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END

            return;
        }


        BuildSaveDate();//组织保存数据集

        //SaveOtherInOutBillNew

        JSONObject saveJons = new JSONObject();
        saveJons.put("SaveIn", saveOthIn);
        saveJons.put("Saveout", saveOthOut);

        //uploadGuid = null;

//		if(uploadGuid==null)
//		{
//		   uploadGuid = UUID.randomUUID();
//		   saveJons.put("GUIDS", uploadGuid.toString());			
//		   uploadGuid = null;
//		}

        //调用保存接口
        //saveJons.put("Type", this.m_OrderType);
        JSONObject jas = MainLogin.objLog.DoHttpQuery(saveJons, "SaveOtherInOutBill", this.m_AccID);

        if (jas == null) {
            Toast.makeText(this, "单据保存过程中出现了问题," +
                    "请尝试再次提交!", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
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
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        } else {
            String lsResultBillCode = "";

            if (jas.has("BillCode")) {
                lsResultBillCode = jas.getString("BillCode");
            } else {
                Toast.makeText(this, "单据保存过程中出现了问题," +
                        "请尝试再次提交!", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            Map<String, Object> mapResultBillCode = new HashMap<String, Object>();


            String lsResultBillCodeOut = "";
            String lsResultBillCodeIn = "";

            String[] val;
            if (lsResultBillCode.contains("|")) {
                val = lsResultBillCode.split("\\|");


                lsResultBillCodeOut = val[0];
                lsResultBillCodeIn = val[1];
            }
            lsResultBillCode = lsResultBillCodeOut + "\r\n" + lsResultBillCodeIn;


            mapResultBillCode.put("BillCode", lsResultBillCode);
            ArrayList<Map<String, Object>> lstResultBillCode = new ArrayList<Map<String, Object>>();
            lstResultBillCode.add(mapResultBillCode);

            //Toast.makeText(this, R.string.DanJuBaoCunChengGong, Toast.LENGTH_LONG).show();
            SimpleAdapter listItemAdapter = new SimpleAdapter(OtherStockInOut.this, lstResultBillCode,//数据源
                    android.R.layout.simple_list_item_1,
                    new String[]{"BillCode"},
                    new int[]{android.R.id.text1}
            );
            new AlertDialog.Builder(OtherStockInOut.this).setTitle(R.string.DanJuBaoCunChengGong)
                    .setAdapter(listItemAdapter, null)
                    .setPositiveButton(R.string.QueRen, null).show();


            //写入log文件
            writeTxt = new writeTxt();

            Date day = new Date(System.currentTimeMillis());//获取当前时间

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");

            String UserID = MainLogin.objLog.UserID;

            String BillType = "";
            String LogName = "";
            String LogMsg = "";


            BillType = m_OrderType;
            LogName = BillType + UserID + dfd.format(day) + ".txt";
            LogMsg = df.format(day) + " " + m_AccID + " " + lsResultBillCodeOut;
            writeTxt.writeTxtToFile(LogName, LogMsg);


            LogName = BillType + UserID + dfd.format(day) + ".txt";
            LogMsg = df.format(day) + " " + m_AccID + " " + lsResultBillCodeIn;
            writeTxt.writeTxtToFile(LogName, LogMsg);
        }


        IniMy();
        uploadGuid = null;

    }

    UUID uploadGuid = null;

    private boolean CheckBox() throws JSONException {
        if (jsBoxTotal == null) {
            Toast.makeText(this, R.string.MeiYouXuYaoBaoCunDeSaoMiaoJiLu, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return false;
        }
        if (jsBoxTotal.has("BoxList")) {
            JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");

            for (int i = 0; i < boxs.length(); i++) {
                String serino = boxs.getJSONObject(i).getString("serial");
                String invcode = boxs.getJSONObject(i).getString("invcode");
                String batch = boxs.getJSONObject(i).getString("batch");

                int total = boxs.getJSONObject(i).getInt("total");
                int icurrent = boxs.getJSONObject(i).getInt("current");


                if (icurrent == total) {
                    continue;
                } else {
                    Toast.makeText(this, "存货:" + invcode + "序列号" +
                            serino + " 分包未扫描全，请重新扫描 ", Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return false;
                }
            }
        } else {
            Toast.makeText(this, R.string.MeiYouXuYaoBaoCunDeSaoMiaoJiLu, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return false;
        }

        return true;
    }

    private void ShowOrderList(String lsBillCode) {
        if (this.txtOrderType.getText().toString().equals("")) {
            Toast.makeText(this, "请选择单据类型", Toast.LENGTH_LONG).show();
            this.txtOrderNo.setText("");
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        Intent otherOrder = new Intent(this, OtherOrderList.class);
        otherOrder.putExtra("OrderType", BillTypeCodeList[OrderTypeIndex]);
        otherOrder.putExtra("Typename", this.txtOrderType.getText().toString());
        otherOrder.putExtra("BillCode", lsBillCode);
        startActivityForResult(otherOrder, 92);
    }


    //打开明细界面

    private void ShowScanDetail() {
        if (m_OrderID == null ||
                m_OrderID.equals("")) {
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            Toast.makeText(this, "请选择来源的单据号", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            //MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }
        Intent otherOrderDetail = new Intent(this, OtherStockInDetail.class);
        otherOrderDetail.putExtra("OrderID", m_OrderID);
        otherOrderDetail.putExtra("BillNo", this.m_OrderNo);
        //otherOrderDetail.putExtra("OrderNo", m_OrderNo);
        otherOrderDetail.putExtra("OrderType", BillTypeCodeList[OrderTypeIndex]);
        otherOrderDetail.putExtra("AccID", this.m_AccID);
        otherOrderDetail.putExtra("m_WarehouseID", this.m_WarehouseID);
        otherOrderDetail.putExtra("pk_corp", this.m_Crop);


        if (jsHead != null) {
            otherOrderDetail.putExtra("Tag", "1");
            otherOrderDetail.putExtra("head", jsHead.toString());
        } else {
            otherOrderDetail.putExtra("Tag", "0");
        }
        if (jsBody != null) {
            otherOrderDetail.putExtra("body", jsBody.toString());
        }
        if (jsSerino != null) {
            otherOrderDetail.putExtra("serino", jsSerino.toString());
        }
        if (jsBoxTotal != null) {
            otherOrderDetail.putExtra("box", jsBoxTotal.toString());
        }

        startActivityForResult(otherOrderDetail, 93);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 92)        //单据选择返回
        {
            if (resultCode != 1) {
                return;
            }
            Bundle bundle = data.getExtras();
            String orderNo = bundle.getString("OrderNo");
            String OrderID = bundle.getString("OrderID");
            String warehouseName = bundle.getString("WarehouseName");
            String warehouseID = bundle.getString("WarehouseID");
            String accid = bundle.getString("AccID");

            this.txtOrderNo.setText(orderNo);
            this.txtWarehouse.setText(warehouseName);

            this.m_AccID = accid;
            this.m_OrderID = OrderID;
            this.m_WarehouseID = warehouseID;
            this.m_OrderNo = orderNo;
            this.m_Crop = bundle.getString("pk_corp");
        }
        if (requestCode == 93) {
            if (resultCode == 1) {
                Bundle bundle = data.getExtras();

                String boxJS = bundle.getString("box");
                String serJS = bundle.getString("serino");
                String bodyJS = bundle.getString("body");
                String headJS = bundle.getString("head");

                try {

                    this.jsBody = new JSONObject(bodyJS);
                    this.jsHead = new JSONObject(headJS);
                    this.jsSerino = new JSONObject(serJS);
                    this.jsBoxTotal = new JSONObject(boxJS);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    Toast.makeText(OtherStockInOut.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                }
                return;
            }

        }
    }


    private DialogInterface.OnClickListener listenExit = new
            DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    finish();
                    System.gc();
                }
            };


    private void Exit() {
        AlertDialog.Builder bulider =
                new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage(R.string.NiQueDingYaoTuiChuMa);
        bulider.setNegativeButton(R.string.QuXiao, null);
        bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();

    }

    private OnKeyListener myTxtListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int arg1, KeyEvent arg2) {
            String KeyAction = "0";
            switch (v.getId()) {
                case id.txtOtOrderNo:


                    if (arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP)//&& arg2.getAction() == KeyEvent.ACTION_DOWN
                    {

                        if (jsBoxTotal != null && jsBoxTotal.has("BoxList")) {
                            txtOrderNo.setText(m_OrderNo);
                            Toast.makeText(OtherStockInOut.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
                            //ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            //ADD CAIXY TEST END
                            break;
                        }
                        if (KeyAction.equals("0")) {
                            String lsBillCode = txtOrderNo.getText().toString();
                            txtOrderNo.setText("");
                            KeyAction = "1";
                            ShowOrderList(lsBillCode);
                        }

                    }
                    break;
            }
            return false;
        }
    };

    private Button.OnClickListener myListner = new
            Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case id.btnOtType:
                            showSingleChoiceDialog();
                            break;
                        case id.btnOtOrderNo:
                            if (jsBoxTotal != null && jsBoxTotal.has("BoxList")) {
                                Toast.makeText(OtherStockInOut.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
                                //ADD CAIXY TEST START
                                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                                //ADD CAIXY TEST END
                                break;
                            }
                            ShowOrderList("");
                            break;
                        case id.btnOtInScan:
                            ShowScanDetail();
                            break;
                        case id.btnOtInExit:
                            Exit();
                            break;
                        case id.btnOtinSave:
                            try {
                                Save();
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            break;

                    }
                }
            };

}
