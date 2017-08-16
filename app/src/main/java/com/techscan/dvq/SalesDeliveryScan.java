package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.bean.SaleOutGoods;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.login.MainLogin;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.techscan.dvq.R.id.lstSDScanDetail;

//import com.techscan.dvq.StockTransScan.ButtonOnClickClearconfirm;
//import com.techscan.dvq.StockTransScanIn.MyListAdapter;

public class SalesDeliveryScan extends Activity {
    private Integer ScanedQty;
    @Nullable
            EditText txtSDScanBarcode   = null;
    @Nullable
            ListView listViewScanDetail = null;
    @Nullable
            Button   btnSDScanTask      = null;
    @Nullable
            Button   btnSDScanClear     = null;
    @Nullable
            Button   btnSDScanReturn    = null;
    @Nullable
            TextView tvSDScanBarCode    = null;
    @Nullable
            TextView tvSDcounts         = null;
    @NonNull
    private String   OkFkg              = "ng";
    int listcount = 0;
    int Tasknnum = 0;
    @NonNull
    String        ScanInvOK           = "0";
    @Nullable
    SimpleAdapter listItemTaskAdapter =null;

    @Nullable
    SalesDeliveryAdapter salesDeliveryAdapter = null;

    private ArrayList<String>     ScanedBarcode = new ArrayList<String>();
    @Nullable
    private SplitTongChengBarCode bar           = null;            //当前扫描条码解析
    @Nullable
    private Inventory             currentObj    = null;        //当前扫描到的存货信息

    @NonNull
    private String     tmpAccID     = "";
    @NonNull
    private String     tmpPK_corp   = "";
    @Nullable
    private JSONObject jsonBodyTask = null;

    @Nullable
    private List<Map<String, Object>> lstBodyTask   = null;
    @Nullable
    private List<Map<String, Object>> lstSaveBody   = null;
    @Nullable
    private Map<String, Object>       mapScanDetail = null;
    List<SaleOutGoods> saleGoodsLists ;


    //删除的任务内容临时保存住
    @NonNull
    private JSONObject JsonRemoveTaskData = new JSONObject();
    //ADD BY WUQIONG START
    //删除的任务内容临时保存住
    @NonNull
    private JSONObject JsonModTaskData    = new JSONObject();
    //ADD BY WUQIONG END

    //定义是否删除Dialog
    @Nullable
    private AlertDialog DeleteAlertDialog = null;
    @NonNull
            String      wareHouseID       = "";
    @Nullable
    private String[]    warehouseList     = null;
    @Nullable
    private String[]    warehouseNameList = null;
    @Nullable
    private String[]    vFree1List        = null;
    @Nullable
    private String[]    OrgList           = null;
    @Nullable
    private String[]    companyIdList     = null;
    String ScanType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_delivery_scan);
        saleGoodsLists =  new ArrayList<SaleOutGoods>();
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("销售出库扫描明细");

        //设置控件
        txtSDScanBarcode = (EditText) findViewById(R.id.txtSDScanBarcode);
        txtSDScanBarcode.setOnKeyListener(EditTextOnKeyListener);

        listViewScanDetail = (ListView) findViewById(lstSDScanDetail);
//        listViewScanDetail.setOnItemClickListener(myListItemListener);
//        listViewScanDetail.setOnItemLongClickListener(myListItemLongListener);

        btnSDScanTask = (Button) findViewById(R.id.btnSDScanTask);
        btnSDScanTask.setOnClickListener(ButtonOnClickListener);
        btnSDScanClear = (Button) findViewById(R.id.btnSDScanClear);
        btnSDScanClear.setOnClickListener(ButtonOnClickListener);
        btnSDScanReturn = (Button) findViewById(R.id.btnSDScanReturn);
        btnSDScanReturn.setOnClickListener(ButtonOnClickListener);


        //获得父画面传过来的数据
        Intent myintent = getIntent();
        Tasknnum = Integer.valueOf(myintent.getStringExtra("TaskCount").toString());
        ScanedBarcode = myintent.getStringArrayListExtra("ScanedBarcode");
        tvSDcounts = (TextView) findViewById(R.id.tvSDcounts);

        btnSDScanTask.setFocusable(false);
        btnSDScanClear.setFocusable(false);
        btnSDScanReturn.setFocusable(false);

        ScanType = myintent.getStringExtra("ScanType");
//        if(lstBodyTask==null||lstBodyTask.size()<1){
//            lstBodyTask = new ArrayList<Map<String, Object>>();
//            listItemTaskAdapter = new SimpleAdapter(SalesDeliveryScan.this,lstBodyTask,0,null,null);
//        }
        //获得父画面传过来的扫描详细数据
        listcount = 0;
        SerializableList lstScanSaveDetial = new SerializableList();
        lstScanSaveDetial = (SerializableList) myintent.getSerializableExtra("lstScanSaveDetial");
        lstSaveBody = lstScanSaveDetial.getList();
        if (lstSaveBody == null || lstSaveBody.size() < 1) {
            lstSaveBody = new ArrayList<Map<String, Object>>();
            salesDeliveryAdapter = new SalesDeliveryAdapter(SalesDeliveryScan.this, lstSaveBody);
            listViewScanDetail.setAdapter(salesDeliveryAdapter);
        }


//        tvSDcounts.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount + "件 | " + "未扫" + (Tasknnum - listcount) + "件");


        //获得父画面传过来的任务详细数据
        String lsTaskJosnBody = myintent.getStringExtra("TaskJonsBody");
        JSONObject jonsTaskBody = null;
        try {
            jonsTaskBody = new JSONObject(lsTaskJosnBody);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }
        if (jonsTaskBody.has("dbBody")) {
            this.jsonBodyTask = jonsTaskBody;

        }

        if (jsonBodyTask.has("ModTaskData")) {
            try {
                JsonModTaskData = (JSONObject) jsonBodyTask.get("ModTaskData");
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                e.printStackTrace();
            }
        }

//				if(jsonBodyTask.has("RemoveTaskData"))
//				{
//					try {
//						JsonRemoveTaskData=(JSONObject)jsonBodyTask.get("RemoveTaskData");
//					} catch (JSONException e) {
//						Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//						//ADD CAIXY TEST START
//						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//						//ADD CAIXY TEST END
//						e.printStackTrace();
//					}
//				}
        try {
            getTaskListData(this.jsonBodyTask);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            e.printStackTrace();
        }


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {//拦截meu键事件			//do something...
            return false;
        }
        return keyCode != KeyEvent.KEYCODE_BACK;
    }

    //取得任务LIST
    private void getTaskListData(@Nullable JSONObject jas) throws JSONException {
        lstBodyTask = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        JSONObject tempJso = null;
        if (jas == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }
        if (!jas.has("Status")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

        if (!jas.has("dbBody")) {
            Toast.makeText(this, R.string.MeiYouDeDaoBiaoTiShuJu, Toast.LENGTH_LONG).show();
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
        }
        JSONArray arrays = (JSONArray) jas.get("dbBody");

        for (int i = 0; i < arrays.length(); i++) {
            map = new HashMap<String, Object>();
            tmpPK_corp =( (JSONObject) (arrays.get(i))).getString("pk_sendcorp").toString();
            map.put("InvName", ((JSONObject) (arrays.get(i))).getString("invname"));
            map.put("InvCode", ((JSONObject) (arrays.get(i))).getString("invcode"));
            map.put("invspec", ((JSONObject) (arrays.get(i))).getString("invspec"));
            map.put("invtype", ((JSONObject) (arrays.get(i))).getString("invtype"));
            map.put("numChange","0");
            map.put("InvNum","/"+ ((JSONObject) arrays.get(i)).getString("number"));
//            map.put("BillCode", ((JSONObject) (arrays.get(i))).getString("billcode"));
            lstBodyTask.add(map);
        }
    }


    //确认存货在上游单据内有
    private boolean ConformDetail(String barcode, @NonNull SplitTongChengBarCode bar) throws JSONException, ParseException, IOException {
        if (jsonBodyTask == null || jsonBodyTask.length() < 1) {
            Toast.makeText(this, R.string.MeiYouZhaoDaoCanZhao, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }
        JSONArray jsarray = jsonBodyTask.getJSONArray("dbBody");
        OkFkg = "ng";
        String Free1 = "";
        String invFlg = "ng";
        for (int i = 0; i < jsarray.length(); i++) {
            String TaskBatch = ((JSONObject) (jsarray.get(i))).getString("batchcode");
            if (!TaskBatch.equals("")) {
                if (TaskBatch.equals(bar.cBatch.trim())) {
                    //确认了存货
                    if (jsarray.getJSONObject(i).getString("invcode").equals(bar.cInvCode.trim())) {
                        String nnum = ((JSONObject) (jsarray.get(i))).getString("number");
//                        String ntranoutnum = ((JSONObject) (jsarray.get(i))).getString("ntotaloutinvnum");
                        String snnum = "0";
//                        if (!ntranoutnum.equals("")) {
//                            snnum = (ntranoutnum.replaceAll("\\.0", ""));
//                        }
                        int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);
                        String Tasknnum = shouldinnum + "";


                        invFlg = "ok";
//                        if(!Tasknnum.equals("0"))
//                        {
////		  		  				if(!ScanType.equals("销售出库"))
////		  		  				{
////		  		  					currentObj.SetvFree1(jsarray.getJSONObject(i).getString("vfree1"));//产地需要修改
////		  		  				}
//                            OkFkg = "ok";
//                            if(!ScanType.equals("销售出库"))
//                            {
//                                Free1 = jsarray.getJSONObject(i).getString("vfree1");
//                            }
//
////		  		  				return true;
//                        }
                    }
                }
            } else {
                if (jsarray.getJSONObject(i).getString("invcode").equals(bar.cInvCode.trim())) {
                    String nnum = ((JSONObject) (jsarray.get(i))).getString("number");
//                    String ntranoutnum = ((JSONObject) (jsarray.get(i))).getString("ntotaloutinvnum");

                    String snnum = "0";
//                    if (!ntranoutnum.equals("null")) {
//                        snnum = (ntranoutnum.replaceAll("\\.0", ""));
//                    }
                    int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);
                    String Tasknnum = shouldinnum + "";
                    invFlg = "ok";
                    if (!Tasknnum.equals("0")) {
                        OkFkg = "ok";
                    }
                }
            }
        }

        if (invFlg.equals("ok")) {
            currentObj = new Inventory(bar.cInvCode, tmpPK_corp,"");
            if (currentObj.getErrMsg() != null && !currentObj.getErrMsg().equals(""))

            {
                Toast.makeText(this, currentObj.getErrMsg(),
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return false;
            }
            currentObj.SetSerino(bar.cSerino.trim());
            currentObj.SetBatch(bar.cBatch.trim());
            return true;
        } else {
            //String invFlg = "ng";
            //invFlg = "ok";
            if (invFlg.equals("ok")) {
                //存货在上游单据任务中已经扫描完毕
                Toast.makeText(this, "超出上游单据任务数量,该条码不能被扫入",
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return false;
            }
        }

//        Toast.makeText(this, "存货在上游单据任务中不存在",
//                Toast.LENGTH_LONG).show();
//        // ADD CAIXY TEST START
//        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // ADD CAIXY TEST END
        return false;
//         return true;
//	  		JSONArray jsarray= jsonBodyTask.getJSONArray("dbBody");
//	  		OkFkg = "ng";
//	  		for(int i = 0;i<jsarray.length();i++)
//	  		{
//	  			//确认了存货
//	  			if(jsarray.getJSONObject(i).getString("invcode").equals(bar.cInvCode))
//	  			{
//	  				currentObj = new Inventory(bar.cInvCode,"",bar.AccID);
//	  				if(currentObj.getErrMsg() != null	&& !currentObj.getErrMsg().equals(""))
//	  				{
//	  					Toast.makeText(this, currentObj.getErrMsg(),
//	  							Toast.LENGTH_LONG).show();
//						// ADD CAIXY TEST START
//						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//						// ADD CAIXY TEST END
//	  					return false;
//	  				}
//	  				currentObj.SetSerino(bar.cSerino);
//	  				currentObj.SetBatch(bar.cBatch);
//	  				currentObj.SetcurrentID(bar.currentBox);
//	  				currentObj.SettotalID(bar.TotalBox);
//	  				currentObj.SetAccID(bar.AccID);
//	  				if(!ScanType.equals("销售出库"))
//	  				{
//	  					currentObj.SetvFree1(jsarray.getJSONObject(i).getString("vfree1"));//产地需要修改
//	  				}
//
//
//	  				return true;
//	  			}
//	  		}
//	  		Toast.makeText(this, "存货在上游单据任务中不存在，请校验",
//	  				Toast.LENGTH_LONG).show();
//			// ADD CAIXY TEST START
//			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//			// ADD CAIXY TEST END
//	  		return false;
    }


    private class ButtonOnClickClearconfirm implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                try {
                    lstSaveBody.clear();
                    salesDeliveryAdapter.notifyDataSetChanged() ;
                    ClearAllScanDetail();
                } catch (JSONException e) {
                    Toast.makeText(SalesDeliveryScan.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    e.printStackTrace();
                }
            } else
                return;
        }

    }


    private void ClearAllScanDetail() throws JSONException {


        Iterator itModKeys = JsonModTaskData.keys();
        JSONArray JsonArrays = new JSONArray();
        JsonArrays = (JSONArray) jsonBodyTask.get("dbBody");


        while (itModKeys.hasNext()) {
            String lsKey = itModKeys.next().toString();
            if (JsonModTaskData.has(lsKey)) {

                JSONObject JsonReMod = (JSONObject) JsonModTaskData.get(lsKey);
                JSONObject jObj = new JSONObject();

                String csourcebillhid = JsonReMod.getString("billbid").toString();
                String InvCode = JsonReMod.getString("invcode").toString();
                String csourcerowno = JsonReMod.getString("crowno").toString();
                String nnum = JsonReMod.getString("number").toString();
                String Tasknnum = "0";

                for (int i = 0; i < JsonArrays.length(); i++) {
                    String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i))).getString("billbid");
                    String InvCodeaDel = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
                    String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i))).getString("crowno");


                    if (csourcebillhidDel.equals(csourcebillhid) && InvCodeaDel.equals(InvCode) && csourcerownoaDel.equals(csourcerowno)) {
                        Tasknnum = ((JSONObject) (JsonArrays.get(i))).getString("number");
                    }
                }

                //jObj.put("vbdef1",JsonReMod.getString("vbdef1").toString());

                jObj.put("vfree1", JsonReMod.getString("vfree1").toString());
                jObj.put("pk_measdoc", JsonReMod.getString("pk_measdoc").toString());
                jObj.put("measname", JsonReMod.getString("measname").toString());
                jObj.put("invcode", JsonReMod.getString("invcode").toString());
                jObj.put("invname", JsonReMod.getString("invname").toString());
                jObj.put("invspec", JsonReMod.getString("invspec").toString());
                jObj.put("invtype", JsonReMod.getString("invtype").toString());
                jObj.put("billcode", JsonReMod.getString("billcode").toString());
                jObj.put("batchcode", JsonReMod.getString("batchcode").toString());
                jObj.put("invbasdocid", JsonReMod.getString("invbasdocid").toString());
                jObj.put("invmandocid", JsonReMod.getString("invmandocid").toString());
                jObj.put("number", JsonReMod.getString("number").toString());
                jObj.put("outnumber", JsonReMod.getString("outnumber").toString());
                jObj.put("sourcerowno", JsonReMod.getString("sourcerowno").toString());
                jObj.put("sourcehid", JsonReMod.getString("sourcehid").toString());
                jObj.put("sourcebid", JsonReMod.getString("sourcebid").toString());
                jObj.put("sourcehcode", JsonReMod.getString("sourcehcode").toString());
                jObj.put("sourcetype", JsonReMod.getString("sourcetype").toString());
                jObj.put("crowno", JsonReMod.getString("crowno").toString());
                jObj.put("billhid", JsonReMod.getString("billhid").toString());
                jObj.put("billbid", JsonReMod.getString("billbid").toString());
                jObj.put("billhcode", JsonReMod.getString("billhcode").toString());
                jObj.put("billtype", JsonReMod.getString("billtype").toString());
                jObj.put("def6", JsonReMod.getString("def6").toString());
                jObj.put("ddeliverdate", JsonReMod.getString("ddeliverdate").toString());
                jObj.put("pk_defdoc6", JsonReMod.getString("pk_defdoc6").toString());
                //需要修改
                //修改数量问题
                int iTasknnum = Integer.valueOf(Tasknnum);
                String snnum = (nnum.replaceAll("\\.0", ""));
                int innum = Integer.valueOf(snnum);
                int inewnnum = iTasknnum + innum;
                String snewnnum = inewnnum + "";
                jObj.put("number", snewnnum);//修改数量问题
                JSONArray JsonArraysRemod = new JSONArray();
                JSONObject jObjReMod = new JSONObject();
                for (int i = 0; i < JsonArrays.length(); i++) {
                    String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i))).getString("billbid");
                    String InvCodeaDel = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
                    String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i))).getString("crowno");
                    if (!csourcebillhidDel.equals(csourcebillhid) || !InvCodeaDel.equals(InvCode) || !csourcerownoaDel.equals(csourcerowno)) {
                        jObjReMod = (JSONObject) JsonArrays.get(i);
                        JsonArraysRemod.put(jObjReMod);
                    }
                }
                JsonArrays = JsonArraysRemod;
                JsonArrays.put(jObj);
            }
            jsonBodyTask = new JSONObject();
            jsonBodyTask.put("Status", true);
            jsonBodyTask.put("dbBody", JsonArrays);
        }
        JsonModTaskData = new JSONObject();
        getTaskListData(jsonBodyTask);
        while (itModKeys.hasNext())
            JsonModTaskData = new JSONObject();
        ScanedBarcode = new ArrayList<String>();
        lstSaveBody = new ArrayList<Map<String, Object>>();
//        listViewScanDetail.setAdapter(null);
        txtSDScanBarcode.setText("");
        listcount = lstSaveBody.size();
//        tvSDcounts.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount + "件 | " + "未扫" + (Tasknnum - listcount) + "件");

    }


    //扫描二维码解析功能函数
    private void ScanBarcode(@NonNull String barcode) throws JSONException, ParseException, IOException {
        if (barcode.equals("")) {
            Toast.makeText(this, "请扫描条码", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            txtSDScanBarcode.requestFocus();
            return;
        }
        txtSDScanBarcode.setText("");
        txtSDScanBarcode.requestFocus();
        //IniScan();
        //条码分析

//        if (!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return;
//        }

        bar = new SplitTongChengBarCode(barcode);
        if (bar.creatorOk == false) {
            Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            txtSDScanBarcode.setText("");
            txtSDScanBarcode.requestFocus();
            return;
        }

        String FinishBarCode = bar.FinishBarCode;

        if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
            for (int si = 0; si < ScanedBarcode.size(); si++) {
                String BarCode = ScanedBarcode.get(si).toString();
                if (BarCode.equals(FinishBarCode)) {
                    txtSDScanBarcode.setText("");
                    txtSDScanBarcode.requestFocus();
                    Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }
            }
        }

//	  		//校验流水号
//	  		if(!ConformGetSERINO(barcode,bar))
//	  		{
//	  			txtSDScanBarcode.setText("");
//	  			txtSDScanBarcode.requestFocus();
//	  			return;
//	  		}


        //先注销
        //判断上游数据是否存在
        if(!ConformDetail(barcode,bar))
        {
            txtSDScanBarcode.setText("");
            txtSDScanBarcode.requestFocus();
            return;
        }
//先注销

        if(OkFkg.equals("ng"))
        {
            Toast.makeText(this, "超出上游单据任务数量,该条码不能被扫入",
                    Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            txtSDScanBarcode.setText("");
            txtSDScanBarcode.requestFocus();
            return;
        }
//先注销
//        if(!ScanType.equals("销售出库"))
//        {
//            if(!ConformGetSERINOInfo())
//            {
//                txtSDScanBarcode.setText("");
//                txtSDScanBarcode.requestFocus();
//                return;
//            }
//        }


        ScanInvOK = "0";
        JSONObject jsonCheckGetBillCode = CheckGetBillCode(bar);
        if (jsonCheckGetBillCode == null || jsonCheckGetBillCode.length() < 1) {
            txtSDScanBarcode.setText("");
            txtSDScanBarcode.requestFocus();
            if (ScanInvOK.equals("1")) {
                //存货在上游单据任务中已经扫描完毕,但是还有未扫完的分包
                Toast.makeText(this, "超出上游单据任务数量,该条码不能被扫入", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            Toast.makeText(this, "该条码不符合任务项目", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        if (!CheckHasScaned(jsonCheckGetBillCode, bar)) {
            txtSDScanBarcode.setText("");
            txtSDScanBarcode.requestFocus();
            Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        //GetRemovedTaskList(bar);
        //ADD BY WUQIONG START
//        GetModTaskList(bar,jsonCheckGetBillCode);
        //ADD BY WUQIONG END
//        ReSetTaskListData();
        ScanedBarcode.add(FinishBarCode);
//        MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
    }

    //ADD BY WUQIONG START
    //完成扫描后修改任务里的项目
    private void GetModTaskList(@NonNull SplitTongChengBarCode bar, @NonNull JSONObject jsonCheckGetBillCode) throws JSONException {
        String lsBarInvCode = bar.cInvCode;
        String lsBarBacth = bar.cBatch;
        String lsBillCode = "";
        String lsSerino = bar.cSerino;

        JSONArray JsonArrays = (JSONArray) jsonBodyTask.get("dbBody");
        //jsonArrRemove = new ArrayList();


        String Taskhid = jsonCheckGetBillCode.getString("billhid");
        String Taskbid = jsonCheckGetBillCode.getString("billbid");

        for (int i = 0; i < JsonArrays.length(); i++) {
            String lsJsonInvCode = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
            String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(i))).getString("batchcode");
            //add caixy 解决扫描任务匹配不正确问题
//            Double ldJsonInvQty = ((JSONObject)(JsonArrays.get(i))).getDouble("number");
            String nnum = ((JSONObject) (JsonArrays.get(i))).getString("number");
//            String ntranoutnum = ((JSONObject) (JsonArrays.get(i))).getString("outnumber");
            String snnum = "0";

//            if (!ntranoutnum.equals("null")) {
//                snnum = (ntranoutnum.replaceAll("\\.0", ""));
//            }

            int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

            String Tasknnum = shouldinnum + "";


            //add caixy 解决扫描任务匹配不正确问题
            if (lsJsonInvBatch == null || lsJsonInvBatch.equals("") || lsJsonInvBatch.equals("null")) {
                lsJsonInvBatch = "批次未指定";
            }
            if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0"))//caixy
            {
                if (lsBarBacth.equals(lsJsonInvBatch)) {
                    if (((JSONObject) (JsonArrays.get(i))).getString("billbid").equals(Taskbid) && ((JSONObject) (JsonArrays.get(i))).getString("billhid").equals(Taskhid)) {
                        GetModTaskQty(Double.valueOf(Tasknnum),
                                ((JSONObject) (JsonArrays.get(i))).getString("billbid"), ((JSONObject) (JsonArrays.get(i))).getString("billhid"), i, lsSerino);
                        return;
                    }


                }
            }
        }

        for (int j = 0; j < JsonArrays.length(); j++) {
            String lsJsonInvCode = ((JSONObject) (JsonArrays.get(j))).getString("invcode");
            String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(j))).getString("batchcode");

            String nnum = ((JSONObject) (JsonArrays.get(j))).getString("number");
            String ntranoutnum = ((JSONObject) (JsonArrays.get(j))).getString("outnumber");
            String snnum = "0";

            if (!ntranoutnum.equals("null")) {
                snnum = (ntranoutnum.replaceAll("\\.0", ""));
            }

            int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

            String Tasknnum = shouldinnum + "";

            if (lsJsonInvBatch == null || lsJsonInvBatch.equals("") || lsJsonInvBatch.equals("null")) {
                lsJsonInvBatch = "批次未指定";
                if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0")) {
                    if (((JSONObject) (JsonArrays.get(j))).getString("billbid").equals(Taskbid) && ((JSONObject) (JsonArrays.get(j))).getString("billhid").equals(Taskhid)) {
                        GetModTaskQty(Double.valueOf(Tasknnum),
                                ((JSONObject) (JsonArrays.get(j))).getString("billbid"), ((JSONObject) (JsonArrays.get(j))).getString("billhid"), j, lsSerino);
                        return;
                    }

                }
            }
        }
    }

    //ADD BY WUQIONG START
    @NonNull
    private String iModTaskIndex = "";

    private void GetModTaskQty(@NonNull Double Qty, String sBillBID, String sBillHID, int iIndex, String lsSerino) throws JSONException {
        //iModTaskIndex="";
        JSONObject JsonModTaskItem = new JSONObject();
        if (lstSaveBody == null || lstSaveBody.size() < 1) {
            return;
        }


        for (int i = 0; i < lstSaveBody.size(); i++) {
            Double inQty = 0.0;
            Map<String, Object> temp = lstSaveBody.get(i);
//            if(temp.get("invbasdocid").equals(currentObj.Invbasdoc()))
//            {
//                if(temp.get("billbid").equals(sBillBID)&&temp.get("billhid").equals(sBillHID))
//                {
//                    inQty += Double.valueOf(temp.get("spacenum").toString());
//                    ScanedQty = Integer.valueOf(temp.get("spacenum").toString());//add caixy e 解决扫描数量错误
//
//                }
//            }

            if (inQty.toString().equals(Qty.toString())) {
                iModTaskIndex = String.valueOf(iIndex);
                JSONArray JsonTaskArrays = (JSONArray) jsonBodyTask.get("dbBody");
                JsonModTaskItem = (JSONObject) JsonTaskArrays.get(iIndex);
                String lsKey = JsonModTaskItem.getString("billbid") +
                        JsonModTaskItem.getString("invcode") +
                        //JsonRemoveTaskItem.getString("vbatch") +
                        JsonModTaskItem.getString("crowno") + lsSerino;
                JsonModTaskData.put(lsKey, JsonModTaskItem);

            } else if (inQty != 0.0) {

                iModTaskIndex = String.valueOf(iIndex);
                JSONArray JsonTaskArrays = (JSONArray) jsonBodyTask.get("dbBody");
                JsonModTaskItem = new JSONObject();

                JsonModTaskItem.put("vfree1", ((JSONObject) JsonTaskArrays.get(iIndex)).get("vfree1").toString());
                JsonModTaskItem.put("pk_measdoc", ((JSONObject) JsonTaskArrays.get(iIndex)).get("pk_measdoc").toString());
                JsonModTaskItem.put("measname", ((JSONObject) JsonTaskArrays.get(iIndex)).get("measname").toString());
                JsonModTaskItem.put("invcode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("invcode").toString());
                JsonModTaskItem.put("invname", ((JSONObject) JsonTaskArrays.get(iIndex)).get("invname").toString());
                JsonModTaskItem.put("invspec", ((JSONObject) JsonTaskArrays.get(iIndex)).get("invspec").toString());
                JsonModTaskItem.put("invtype", ((JSONObject) JsonTaskArrays.get(iIndex)).get("invtype").toString());
                JsonModTaskItem.put("billcode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("billcode").toString());
                JsonModTaskItem.put("batchcode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("batchcode").toString());
                JsonModTaskItem.put("invbasdocid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("invbasdocid").toString());
                JsonModTaskItem.put("invmandocid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("invmandocid").toString());
                JsonModTaskItem.put("number", ((JSONObject) JsonTaskArrays.get(iIndex)).get("number").toString());
                JsonModTaskItem.put("outnumber", ((JSONObject) JsonTaskArrays.get(iIndex)).get("outnumber").toString());
                JsonModTaskItem.put("sourcerowno", ((JSONObject) JsonTaskArrays.get(iIndex)).get("sourcerowno").toString());
                JsonModTaskItem.put("sourcehid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("sourcehid").toString());
                JsonModTaskItem.put("sourcebid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("sourcebid").toString());
                JsonModTaskItem.put("sourcehcode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("sourcehcode").toString());
                JsonModTaskItem.put("sourcetype", ((JSONObject) JsonTaskArrays.get(iIndex)).get("sourcetype").toString());
                JsonModTaskItem.put("crowno", ((JSONObject) JsonTaskArrays.get(iIndex)).get("crowno").toString());
                JsonModTaskItem.put("billhid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("billhid").toString());
                JsonModTaskItem.put("billbid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("billbid").toString());
                JsonModTaskItem.put("billhcode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("billhcode").toString());
                JsonModTaskItem.put("billtype", ((JSONObject) JsonTaskArrays.get(iIndex)).get("billtype").toString());
                JsonModTaskItem.put("def6", ((JSONObject) JsonTaskArrays.get(iIndex)).get("def6").toString());
                JsonModTaskItem.put("ddeliverdate", ((JSONObject) JsonTaskArrays.get(iIndex)).get("ddeliverdate").toString());
                JsonModTaskItem.put("pk_defdoc6", ((JSONObject) JsonTaskArrays.get(iIndex)).get("pk_defdoc6").toString());
                //需要修改
                JsonModTaskItem.put("number", inQty);

                String lsKey = JsonModTaskItem.getString("billbid") +
                        JsonModTaskItem.getString("invcode") +
                        //JsonRemoveTaskItem.getString("vbatch") +
                        JsonModTaskItem.getString("crowno") + lsSerino;
                JsonModTaskData.put(lsKey, JsonModTaskItem);
            }

        }

    }


    //    private void ReSetTaskListData() throws JSONException
//    {
//        JSONArray JsonArrays = (JSONArray)jsonBodyTask.get("dbBody");
//        JSONArray JsonArrNew = new JSONArray();
//        for(int i=0;i<JsonArrays.length();i++)
//        {
//            if (!iModTaskIndex.equals(""))
//            {
//                if(i!=Integer.parseInt(iModTaskIndex))
//                {
//                    JsonArrNew.put((JSONObject)JsonArrays.get(i));
//                }
//                else
//                {
//                    JSONObject jObj = new JSONObject();
//                    jObj.put("vfree1", ((JSONObject) JsonArrays.get(i)).get("vfree1").toString());
//                    jObj.put("pk_measdoc", ((JSONObject) JsonArrays.get(i)).get("pk_measdoc").toString());
//                    jObj.put("measname", ((JSONObject) JsonArrays.get(i)).get("measname").toString());
//                    jObj.put("invcode", ((JSONObject) JsonArrays.get(i)).get("invcode").toString());
//                    jObj.put("invname", ((JSONObject) JsonArrays.get(i)).get("invname").toString());
//                    jObj.put("invspec", ((JSONObject) JsonArrays.get(i)).get("invspec").toString());
//                    jObj.put("invtype", ((JSONObject) JsonArrays.get(i)).get("invtype").toString());
//                    jObj.put("billcode", ((JSONObject) JsonArrays.get(i)).get("billcode").toString());
//                    jObj.put("batchcode", ((JSONObject) JsonArrays.get(i)).get("batchcode").toString());
//                    jObj.put("invbasdocid", ((JSONObject) JsonArrays.get(i)).get("invbasdocid").toString());
//                    jObj.put("invmandocid", ((JSONObject) JsonArrays.get(i)).get("invmandocid").toString());
//                    jObj.put("number", ((JSONObject) JsonArrays.get(i)).get("number").toString());
//                    jObj.put("outnumber", ((JSONObject) JsonArrays.get(i)).get("outnumber").toString());
//                    jObj.put("sourcerowno", ((JSONObject) JsonArrays.get(i)).get("sourcerowno").toString());
//                    jObj.put("sourcehid", ((JSONObject) JsonArrays.get(i)).get("sourcehid").toString());
//                    jObj.put("sourcebid", ((JSONObject) JsonArrays.get(i)).get("sourcebid").toString());
//                    jObj.put("sourcehcode", ((JSONObject) JsonArrays.get(i)).get("sourcehcode").toString());
//                    jObj.put("sourcetype", ((JSONObject) JsonArrays.get(i)).get("sourcetype").toString());
//                    jObj.put("crowno", ((JSONObject) JsonArrays.get(i)).get("crowno").toString());
//                    jObj.put("billhid", ((JSONObject) JsonArrays.get(i)).get("billhid").toString());
//                    jObj.put("billbid", ((JSONObject) JsonArrays.get(i)).get("billbid").toString());
//                    jObj.put("billhcode", ((JSONObject) JsonArrays.get(i)).get("billhcode").toString());
//                    jObj.put("billtype", ((JSONObject) JsonArrays.get(i)).get("billtype").toString());
//                    jObj.put("def6", ((JSONObject) JsonArrays.get(i)).get("def6").toString());
//                    jObj.put("ddeliverdate", ((JSONObject) JsonArrays.get(i)).get("ddeliverdate").toString());
//                    jObj.put("pk_defdoc6", ((JSONObject) JsonArrays.get(i)).get("pk_defdoc6").toString());
//                    String snumber = ((JSONObject) JsonArrays.get(i)).get("number").toString();
//                    int innum = Integer.valueOf(snumber).intValue() - ScanedQty;
//
//                    String snnum = innum +"";
//                    jObj.put("number", snnum);
//
//                    JsonArrNew.put(jObj);
//
//                }
//            }
//            //ADD BY WUQIONG END
//        }
//        //MOD BY WUQIONG S
//        if(!iModTaskIndex.equals(""))
//        {
//            jsonBodyTask = new JSONObject();
//            jsonBodyTask.put("Status", true);
//            jsonBodyTask.put("dbBody", JsonArrNew);
////	    	  		if(!iRemoveTaskIndex.equals(""))
////	    	  		{
////	    	  			jonsBody.put("RemoveTaskData", JsonRemoveTaskData);
////	    	  		}
//            if(!iModTaskIndex.equals(""))
//            {
//                jsonBodyTask.put("ModTaskData", JsonModTaskData);
//            }
//
//            getTaskListData(jsonBodyTask);
//        }
//
//    }


//	  	//完成扫描后删除任务里的项目
//	  	private void GetRemovedTaskList(SplitBarcode bar) throws JSONException
//	  	{
//	  		String lsBarInvCode = bar.cInvCode;
//	  		String lsBarBacth = bar.cBatch;
//	  		String lsBillCode = "";
//
//	  		JSONArray JsonArrays=(JSONArray)jsonBodyTask.getJSONArray("dbBody");
//	  		//jsonArrRemove = new ArrayList();
//
//			for(int i = 0;i<JsonArrays.length();i++)
//			{
//				String lsJsonInvCode = ((JSONObject)(JsonArrays.get(i))).getString("invcode");
//				String lsJsonInvBatch = ((JSONObject)(JsonArrays.get(i))).getString("batchcode");
//				Double ldJsonInvQty = ((JSONObject)(JsonArrays.get(i))).getDouble("shouldoutnum");
//				if(lsJsonInvBatch==null||lsJsonInvBatch.equals("")||lsJsonInvBatch.equals("null"))
//				{
//					lsJsonInvBatch="批次未指定";
//				}
//				if(lsBarInvCode.equals(lsJsonInvCode))
//				{
//					if(lsBarBacth.equals(lsJsonInvBatch))
//					{
//						GetRemovedTaskQty(ldJsonInvQty,
//								((JSONObject)(JsonArrays.get(i))).getString("billhid"),i);
//
//					}
//				}
//			}
//
//			for(int j = 0;j<JsonArrays.length();j++)
//			{
//				String lsJsonInvCode = ((JSONObject)(JsonArrays.get(j))).getString("invcode");
//				String lsJsonInvBatch = ((JSONObject)(JsonArrays.get(j))).getString("billcode");
//				Double ldJsonInvQty = ((JSONObject)(JsonArrays.get(j))).getDouble("shouldoutnum");
//				if(lsJsonInvBatch==null||lsJsonInvBatch.equals("")||lsJsonInvBatch.equals("null"))
//				{
//					lsJsonInvBatch="批次未指定";
//				}
//				if(lsBarInvCode.equals(lsJsonInvCode))
//				{
//					GetRemovedTaskQty(ldJsonInvQty,
//							((JSONObject)(JsonArrays.get(j))).getString("billhid"),j);
//				}
//			}
//
//	  	}

    private boolean ConformGetSERINOInfo() throws JSONException, ParseException, IOException
    {



        //获得当前单据的流水号
        JSONObject SERINOList = null;
        JSONObject para = new JSONObject();

        //,bar.cBatch,,bar.AccID
        para.put("FunctionName", "GetSERINOCVInfo");
        para.put("INVCODE",bar.cInvCode);
        para.put("CLOT",bar.cBatch);
        para.put("SERINO",bar.cSerino);
        para.put("WHID",wareHouseID);

        para.put("TableName","SERINO");

//        if(!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return false;
//        }
        SERINOList = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);


        if(SERINOList==null)
        {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }


        if(!SERINOList.getBoolean("Status"))
        {
            return true;
        }
        else
        {
            JSONArray jsarraySERINO = null;
            jsarraySERINO = SERINOList.getJSONArray("SERINO");

            String bsttype = jsarraySERINO.getJSONObject(0).getString("bsttype").toString();

            if(bsttype.equals("0"))
            {
                return true;
            }
            else
            {
                Toast.makeText(this, "扫描的条码流水号在仓库中已经存在,该条码不能被扫入",
                        Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }
        }
    }

    //确认如果有上游单据,那么判断是否超过其数量
    private boolean ConformDetailQty(Double Qty) throws JSONException {
//        ScanInvOK = "1";
        if (lstSaveBody == null || lstSaveBody.size() < 1) {
            return true;
        }

        for (int j = 0; j < lstSaveBody.size(); j++) {
            Map<String, Object> temp1 = lstSaveBody.get(j);
            if (temp1.get("SeriNo").equals(currentObj.GetSerino()) && temp1.get("InvCode").equals(currentObj.getInvCode())
                    && temp1.get("Batch").equals(currentObj.GetBatch()) ) {
                return true;
            }
        }
        Double inQty = 1.0;
        for (int i = 0; i < lstSaveBody.size(); i++) {
            Map<String, Object> temp = lstSaveBody.get(i);
            if (temp.get("invbasdocid").equals(currentObj.Invbasdoc())) {
                    inQty += 1;
            }
        }

        return inQty <= Qty;

    }

    //判断该扫描条码属于哪条单据行
    @Nullable
    private JSONObject CheckGetBillCode(@NonNull SplitTongChengBarCode bar) throws JSONException {
        String lsBarInvCode = bar.cInvCode;
        String lsBarBacth = bar.cBatch;

        JSONArray JsonArrays = jsonBodyTask.getJSONArray("dbBody");
        JSONObject obj = null;
        for (int i = 0; i < JsonArrays.length(); i++) {
            String lsJsonInvCode = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
            String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(i))).getString("batchcode");
            String Outnum = "0";
//            String sOutnum = ((JSONObject) (JsonArrays.get(i))).getString("ntotaloutinvnum");
//            if (!sOutnum.equals("")) {
//                Outnum = sOutnum;
//            }
            Double ldJsonInvQty = ((JSONObject) (JsonArrays.get(i))).getDouble("number")- Double.valueOf(Outnum);
            if (lsJsonInvBatch == null || lsJsonInvBatch.equals("") || lsJsonInvBatch.equals("null")) {
                lsJsonInvBatch = "批次未指定";
            }
            if (lsBarInvCode.equals(lsJsonInvCode) && ldJsonInvQty > 0) {
//                if(lsBarBacth.equals(lsJsonInvBatch))
//                {
//                    if(ConformDetailQty(ldJsonInvQty))
//                    {
//
//                    }
//                }
                obj = (JSONObject) JsonArrays.get(i);
                return obj;
            }
        }

        if (ScanType.equals("销售出库")) {
            for (int j = 0; j < JsonArrays.length(); j++) {
                String lsJsonInvCode = ((JSONObject) (JsonArrays.get(j))).getString("invcode");
                String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(j))).getString("batchcode");

                String Outnum = "0";

//                String sOutnum = ((JSONObject) (JsonArrays.get(j))).getString("ntotaloutinvnum");

//                if (!sOutnum.equals("")) {
//                    Outnum = sOutnum;
//                }

                Double ldJsonInvQty = ((JSONObject) (JsonArrays.get(j))).getDouble("number")- Double.valueOf(Outnum);

                if (lsJsonInvBatch == null || lsJsonInvBatch.equals("") || lsJsonInvBatch.equals("null")) {
                    lsJsonInvBatch = "批次未指定";
                }
                if (lsBarInvCode.equals(lsJsonInvCode) && ldJsonInvQty > 0 && lsJsonInvBatch.equals("批次未指定")) {
//                    if(ConformDetailQty(ldJsonInvQty)
//                    {
//                        return (JSONObject)JsonArrays.get(j);
//                    }
                    obj = (JSONObject) JsonArrays.get(j);
                    return obj;
                }
            }
        }
        return obj;
    }

    /**
     * 判断该条码是否已经被扫描过了
     *
     * @return 如果为true 代表没有被扫描过,如果false 代表已经被扫描过了
     * @throws JSONException
     */
    private Boolean CheckHasScaned(@NonNull JSONObject jsonCheckGetBillCode, @NonNull SplitTongChengBarCode bar) throws JSONException {

//        ListAdapter ScanDetailAdapter = listViewScanDetail.getAdapter();

//        String lsKey = jsonCheckGetBillCode.getString("billcode")
//                + bar.AccID + bar.cInvCode + bar.cBatch + bar.cSerino;
//        if(ScanDetailAdapter==null || ScanDetailAdapter.getCount() < 1)
//        {
//            BindingScanDetail(jsonCheckGetBillCode,bar,"ADD",null);
//            return true;
//        }
//        for(int i=0;i<ScanDetailAdapter.getCount();i++)
//        {
//            Map<String,Object> mapScanDetail =
//                    (Map<String,Object>)ScanDetailAdapter.getItem(i);
//            if(mapScanDetail.containsKey(lsKey))
//            {
//
//                ArrayList<Map<String,Object>> lstCurrentDetail = (ArrayList<Map<String,Object>>)mapScanDetail.get(lsKey);
//                for(int j=0;j<lstCurrentDetail.size();j++)
//                {
//                    if(lstCurrentDetail.get(j).get("FinishBarCode").toString().equals(bar.FinishBarCode))
//                        return false;
//                }
//                BindingScanDetail(jsonCheckGetBillCode,bar,"MOD",mapScanDetail);
//                return true;
//            }
//        }

        BindingScanDetail(jsonCheckGetBillCode, bar);
        return true;
    }

//绑定数据
    private void BindingScanDetail(@NonNull JSONObject jsonCheckGetBillCode, @NonNull SplitTongChengBarCode bar
                                  ) throws JSONException {
//        ArrayList<Map<String, Object>> lstCurrentBox = null;
//        Map<String,Object> mapCurrentBox = new HashMap<String,Object>();
        mapScanDetail = new HashMap<String, Object>();
//        if(lstSaveBody==null || lstSaveBody.size()<1)
//        {
//            lstSaveBody = new ArrayList<Map<String, Object>>();
//        }
//
//        mapCurrentBox.put("CurrentBox", bar.currentBox);
//        mapCurrentBox.put("TotalBox", bar.TotalBox);
//        mapCurrentBox.put("FinishBarCode", bar.FinishBarCode);
//        mapCurrentBox.put("BoxNum", Integer.parseInt(bar.currentBox) + "/" + Integer.parseInt(bar.TotalBox));
//
//        String lsKey = jsonCheckGetBillCode.getString("billcode")
//                + bar.AccID + bar.cInvCode + bar.cBatch + bar.cSerino;

//            lstCurrentBox = new ArrayList<Map<String,Object>>();
//            lstCurrentBox.add(mapCurrentBox);

//            mapScanDetail.put(lsKey,lstCurrentBox);  //销售订单附表ID
//        newBodyJSON.put("csourcebillbodyid", tempJso.getString("corder_bid"));
//        //销售主表ID
//        newBodyJSON.put("csourcebillid", tempJso.getString("csaleid"));
//
//        newBodyJSON.put("pk_sendcorp", tempJso.getString("pk_corp"));
//        //注册地址
//        newBodyJSON.put("vreceiveaddress", tempJso.getString("vreceiveaddress"));
//        //存货ID
//        newBodyJSON.put("cinvmandocid", tempJso.getString("cinventoryid"));
//        //建议发货库存组织
//        newBodyJSON.put("csendcalbodyid", tempJso.getString("cadvisecalbodyid"));
//        newBodyJSON.put("cinvbasdocid", tempJso.getString("cinvbasdocid"));
        mapScanDetail.put("InvName",currentObj.getInvName() );
        mapScanDetail.put("InvCode", currentObj.getInvCode());
        mapScanDetail.put("Weights", bar.Weights.trim());
        mapScanDetail.put("numChange",bar.Weights.trim());
        mapScanDetail.put("Number", jsonCheckGetBillCode.getString("number"));
        mapScanDetail.put("Measname", jsonCheckGetBillCode.getString("measname"));
        mapScanDetail.put("Batch", currentObj.GetBatch());
        mapScanDetail.put("csourcebillbodyid", jsonCheckGetBillCode.getString("csourcebillbodyid"));
        mapScanDetail.put("csourcebillid", jsonCheckGetBillCode.getString("csourcebillid"));
        mapScanDetail.put("pk_sendcorp", jsonCheckGetBillCode.getString("pk_sendcorp"));
        mapScanDetail.put("vreceiveaddress", jsonCheckGetBillCode.getString("vreceiveaddress"));
        mapScanDetail.put("cinventoryid", jsonCheckGetBillCode.getString("cinvmandocid"));
        mapScanDetail.put("cadvisecalbodyid", jsonCheckGetBillCode.getString("csendcalbodyid"));
        mapScanDetail.put("cinvbasdocid", jsonCheckGetBillCode.getString("cinvbasdocid"));
        mapScanDetail.put("SeriNo", currentObj.GetSerino());
        mapScanDetail.put("BarCode", bar.FinishBarCode.trim());
        mapScanDetail.put("pk_invbasdoc",currentObj.Invbasdoc());
        mapScanDetail.put("pk_invmandoc",currentObj.Invmandoc());
        mapScanDetail.put("invspec",currentObj.Invspec());
        mapScanDetail.put("invtype",currentObj.Invtype());
//            mapScanDetail.put("TotalNum", Integer.parseInt(currentObj.totalID()));
//            mapScanDetail.put("ScanedNum", lstCurrentBox.size());
        //开始单据行号
//        mapScanDetail.put("sourcerowno", jsonCheckGetBillCode.getString("sourcerowno"));
        //源单单据行号(参照单 )
        mapScanDetail.put("crowno", jsonCheckGetBillCode.getString("crowno"));
        //开始单表头
//        mapScanDetail.put("sourcehid", jsonCheckGetBillCode.getString("sourcehid"));
//        //源单表头(参照单 )
//        mapScanDetail.put("billhid", jsonCheckGetBillCode.getString("billhid"));
//        //开始单表体
//        mapScanDetail.put("sourcebid", jsonCheckGetBillCode.getString("sourcebid"));
        //源单表体(参照单 )
//        mapScanDetail.put("billbid", jsonCheckGetBillCode.getString("billbid"));
        //开始单据类型
//        mapScanDetail.put("sourcetype", jsonCheckGetBillCode.getString("sourcetype"));
        //源单据类型(参照单 )
//        mapScanDetail.put("billtype", jsonCheckGetBillCode.getString("billtype"));
        //开始单据号
        //单据号(参照单 )
        mapScanDetail.put("ddeliverdate", jsonCheckGetBillCode.getString("ddeliverdate"));
        //存货基本标识
//        mapScanDetail.put("invbasdocid", jsonCheckGetBillCode.getString("invbasdocid"));
        //存货管理ID
//            mapScanDetail.put("invmandocid", currentObj.Invmandoc());
        //自由项一
//            mapScanDetail.put("free1", currentObj.vFree1());
        //单据批次
        //批次

        //该货位该存货编码批次有几件货?
//            if(Integer.parseInt(currentObj.totalID())==lstCurrentBox.size())
//            {
//                mapScanDetail.put("spacenum", "1");
//                mapScanDetail.put("box", "");
//            }

//            else
//            {
//                mapScanDetail.put("spacenum", "0");
//                mapScanDetail.put("box", "分包未完");
//            }
        //
        lstBodyTask .add(mapScanDetail);
//        if (listItemTaskAdapter==null){
//            return;
//        }else{
//            listItemTaskAdapter.notifyDataSetInvalidated();
//        }
//        listItemTaskAdapter.notifyDataSetInvalidated();
        lstSaveBody.add(mapScanDetail);
//        addItemsTo(bar.Weights.trim(),currentObj.getInvName(),jsonCheckGetBillCode.getString("number"),currentObj.Invspec(),currentObj.Invtype(), currentObj.getInvCode());
        int count = lstSaveBody.size();
        Log.d(TAG, "COUNT: "+count);
        Map<String, Object> mapScanDetails;
        for (int i = 0;i<count; i++){
            mapScanDetails = lstSaveBody.get(i);
            if (saleGoodsLists.size()==0){
                SaleOutGoods saleOutGoods = new SaleOutGoods();
                saleOutGoods.setInvName((String) mapScanDetails.get("InvName"));
                saleOutGoods.setInvCode((String) mapScanDetails.get("InvCode"));
                String qty = String.valueOf(mapScanDetails.get("Weights"));
                saleOutGoods.setBatch((String) mapScanDetails.get("Batch"));
                saleOutGoods.setUnit((String) mapScanDetails.get("Measname"));
//                saleOutGoods.setNum(Integer.valueOf((String) mapScanDetails.get("Number")));
//                saleOutGoods.setCORDER_BID((String) mapScanDetails.get("csourcebillbodyid"));
//                saleOutGoods.setCSALEID((String) mapScanDetails.get("csourcebillid"));
//                saleOutGoods.setPK_CORP((String) mapScanDetails.get("pk_sendcorp"));
//                saleOutGoods.setVRECEIVEADDRESS((String) mapScanDetails.get("vreceiveaddress"));
//                saleOutGoods.setCINVENTORYID((String) mapScanDetails.get("cinventoryid"));
//                saleOutGoods.setCADVISECALBODYID((String) mapScanDetails.get("cadvisecalbodyid"));
//                saleOutGoods.setCINVBASDOCID((String) mapScanDetails.get("cinvbasdocid"));
                saleOutGoods.setPk_invbasdoc((String) mapScanDetails.get("pk_invbasdoc"));
                saleOutGoods.setPk_invmandoc((String) mapScanDetails.get("pk_invmandoc"));
                if (TextUtils.isEmpty(qty)) {
                    qty = "0.0";
                }
                saleOutGoods.setQty(Float.valueOf(qty));
                saleGoodsLists.add(saleOutGoods);
            }else {
                for (int j = 0; j< saleGoodsLists.size(); j++) {
                    SaleOutGoods existGoods = saleGoodsLists.get(j);
                    //相同物料相同批次的要合并，通过名字和批次比较
                    if (mapScanDetails.get("InvName").equals(existGoods.getInvName())&&mapScanDetails.get("InvCode").equals(existGoods.getInvCode())) {
                        existGoods.setQty(existGoods.getQty() + Float.valueOf((String )mapScanDetails.get("Weights")));
                    } else {
                        SaleOutGoods saleOutGoods_c = new SaleOutGoods();
                        saleOutGoods_c.setInvName((String) mapScanDetails.get("InvName"));
                        saleOutGoods_c.setInvCode((String) mapScanDetails.get("InvCode"));
                        saleOutGoods_c.setBatch((String) mapScanDetails.get("Batch"));
                        saleOutGoods_c.setUnit((String) mapScanDetails.get("Measname"));
//                        saleOutGoods_c.setNum((Integer.valueOf((String) mapScanDetails.get("Number"))));
//                        saleOutGoods_c.setCORDER_BID((String) mapScanDetails.get("csourcebillbodyid"));
//                        saleOutGoods_c.setCSALEID((String) mapScanDetails.get("csourcebillid"));
//                        saleOutGoods_c.setPK_CORP((String) mapScanDetails.get("pk_sendcorp"));
//                        saleOutGoods_c.setVRECEIVEADDRESS((String) mapScanDetails.get("vreceiveaddress"));
//                        saleOutGoods_c.setCINVENTORYID((String) mapScanDetails.get("cinventoryid"));
//                        saleOutGoods_c.setCADVISECALBODYID((String) mapScanDetails.get("cadvisecalbodyid"));
//                        saleOutGoods_c.setCINVBASDOCID((String) mapScanDetails.get("cinvbasdocid"));
                        saleOutGoods_c.setPk_invbasdoc((String) mapScanDetails.get("pk_invbasdoc"));
                        saleOutGoods_c.setPk_invmandoc((String) mapScanDetails.get("pk_invmandoc"));
                        String qty = String.valueOf(mapScanDetails.get("Weights"));
                        if (TextUtils.isEmpty(qty)) {
                            qty = "0.0";
                        }
                        saleOutGoods_c.setQty(Float.valueOf(qty));
                        saleGoodsLists.add(saleOutGoods_c);
                    }
                }
            }
        }
//        for (int i = 0; i < count; i++) {
//            map = (HashMap<String, Object>) lstSaveBody.get(i);
//            saleOutGoods = new SaleOutGoods();
//            saleOutGoods.setBarcode((String) map.get("BarCode"));
//            saleOutGoods.setInvCode((String) map.get("InvCode"));
//            saleOutGoods.setInvName((String) map.get("InvName"));
//            saleOutGoods.setBatch((String) map.get("Batch"));
//            saleOutGoods.setPk_invbasdoc((String) map.get("BarCode"));
//            saleOutGoods.setPk_invmandoc((String) map.get("BarCode"));
//            saleOutGoods.setUnit((String) map.get("Measname"));
//            String qty = String.valueOf(map.get("qty"));
//            if (TextUtils.isEmpty(qty)) {
//                qty = "0.0";
//            }
//            saleOutGoods.setQty(Float.valueOf((String) map.get("Weights")));
//            saleGoodsLists.add(saleOutGoods);
//        }
//        salesDeliveryAdapter = new SalesDeliveryAdapter(SalesDeliveryScan.this, lstSaveBody);
//        listViewScanDetail.setAdapter(salesDeliveryAdapter);
        salesDeliveryAdapter.notifyDataSetInvalidated();
        listcount = lstSaveBody.size();
//        tvSDcounts.setText("总重" + Tasknnum + "件 | " + "已扫" + listcount + "件 | " + "未扫" + (Tasknnum - listcount) + "件");
//        MyListAdapter listItemAdapter = new MyListAdapter(SalesDeliveryScan.this,lstSaveBody,//数据源
//                R.layout.vlisttransscanitem,
////					new String[] {"InvCode","InvName","Batch","AccID","TotalNum",
////							"BarCode","SeriNo","BillCode","ScanedNum"},
////                new String[] {"InvCode","InvName","Batch","AccID","TotalNum",
////                        "BarCode","SeriNo","BillCode","ScanedNum","box"},
////
////                new int[] {R.id.txtTransScanInvCode,R.id.txtTransScanInvName,
////                        R.id.txtTransScanBatch,R.id.txtTransScanAccId,
////                        R.id.txtTransScanTotalNum,R.id.txtTransScanBarCode,
////                        R.id.txtTransScanSeriNo,R.id.txtTransScanBillCode,
////                        R.id.txtTransScanScanCount,R.id.txtTransBox}
//        new String[] {"InvCode","InvName","Batch","AccID"},
//
//                new int[] {R.id.txtTransScanInvCode,R.id.txtTransScanInvName,
//                        R.id.txtTransScanBatch,R.id.txtTransScanAccId,
//                       }
//        );
//        listViewScanDetail.setAdapter(listItemAdapter);
    }

//    private OnItemClickListener myListItemListener =
//            new OnItemClickListener()
//            {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                        long arg3) {
//                    Adapter adapter = arg0.getAdapter();
//                    Map<String,Object> mapCurrent = (Map<String, Object>) adapter.getItem(arg2);
//                    Log.d(TAG, "onItemClick: "+ mapCurrent.toString());
////                    Map<String,Object> mapCurrent = (Map<String,Object>)listViewScanDetail.getAdapter().getItem(arg2);
//                    String lsKey = mapCurrent.get("BillCode").toString() +
//                            mapCurrent.get("AccID").toString() +
//                            mapCurrent.get("InvCode").toString() +
//                            mapCurrent.get("Batch").toString() +
//                            mapCurrent.get("SeriNo").toString();
//                    ArrayList<Map<String,Object>> lstCurrent =
//                            (ArrayList<Map<String,Object>>)mapCurrent.get(lsKey);
//                    SimpleAdapter listItemAdapter = new SimpleAdapter(SalesDeliveryScan.this,lstCurrent,//数据源
//                            android.R.layout.simple_list_item_2,
//                            new String[] {"BoxNum","FinishBarCode"},
//                            new int[] {android.R.id.text1,android.R.id.text2}
//                    );
//                    new AlertDialog.Builder(SalesDeliveryScan.this).setTitle("分包详细信息")
//                            .setAdapter(listItemAdapter, null)
//                            .setPositiveButton(R.string.QueRen,null).show();
//                }
//
//            };
//
//    //长按扫描详细，删除该条记录
//    private OnItemLongClickListener myListItemLongListener =
//            new OnItemLongClickListener()
//            {
//
//                @Override
//                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//                                               int arg2, long arg3) {
//
//                    Map<String,Object> mapCurrent = (Map<String,Object>)listViewScanDetail.getAdapter().getItem(arg2);
//                    String lsKey = mapCurrent.get("billbid").toString() +
//                            mapCurrent.get("InvCode").toString() +
//                            mapCurrent.get("crowno").toString()+
//                            mapCurrent.get("SeriNo").toString();
//
//
//                    String Barcode = mapCurrent.get("BarCode").toString();
//
//                    ButtonOnClickDelconfirm btnScanItemDelOnClick =new ButtonOnClickDelconfirm(arg2,lsKey,Barcode);
//                    DeleteAlertDialog=new AlertDialog.Builder(SalesDeliveryScan.this).setTitle(R.string.QueRenShanChu)
//                            .setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
//                            .setPositiveButton(R.string.QueRen, btnScanItemDelOnClick).setNegativeButton(R.string.QuXiao,null).show();
//
//                    return true;
//                }
//
//            };


    //删除已扫描的内容
    //int iIndex,String sKey
    private void ConfirmDelItem(int iIndex, String sKey, String BarCode) throws JSONException {
        //删除保存在内存的扫描详细
        lstSaveBody.remove(iIndex);

        if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
            for (int si = 0; si < ScanedBarcode.size(); si++) {
                String RemoveBarCode = ScanedBarcode.get(si).toString();
                int iBarlenth = RemoveBarCode.length() - 6;
                String RemoveBarCodeF = RemoveBarCode.substring(0, iBarlenth);

                if (RemoveBarCodeF.equals(BarCode)) {
                    ScanedBarcode.remove(si);
                    si--;
                }
            }
        }


        listcount = lstSaveBody.size();
//        tvSDcounts.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount + "件 | " + "未扫" + (Tasknnum - listcount) + "件");


        MyListAdapter listItemAdapter = (MyListAdapter) listViewScanDetail.getAdapter();
        listItemAdapter.notifyDataSetChanged();
        listViewScanDetail.setAdapter(listItemAdapter);


        if (JsonModTaskData == null || JsonModTaskData.length() < 1)
            return;

        if (!JsonModTaskData.has(sKey))
            return;


        //恢复之前删除的任务数据
        JSONArray JsonArrays = jsonBodyTask.getJSONArray("dbBody");

        if (JsonModTaskData.has(sKey)) {
            JSONObject JsonReMod = (JSONObject) JsonModTaskData.get(sKey);
            JSONObject jObj = new JSONObject();

            String csourcebillhid = JsonReMod.getString("billbid").toString();
            String InvCode = JsonReMod.getString("invcode").toString();
            String csourcerowno = JsonReMod.getString("crowno").toString();
            String nnum = JsonReMod.getString("number").toString();
            String Tasknnum = "0";

            for (int i = 0; i < JsonArrays.length(); i++) {
                String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i))).getString("billbid");
                String InvCodeaDel = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
                String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i))).getString("crowno");


                if (csourcebillhidDel.equals(csourcebillhid) && InvCodeaDel.equals(InvCode) && csourcerownoaDel.equals(csourcerowno)) {
                    Tasknnum = ((JSONObject) (JsonArrays.get(i))).getString("number");
                }
            }
            //修改
            jObj.put("vfree1", JsonReMod.getString("vfree1").toString());
            jObj.put("pk_measdoc", JsonReMod.getString("pk_measdoc").toString());
            jObj.put("measname", JsonReMod.getString("measname").toString());
            jObj.put("invcode", JsonReMod.getString("invcode").toString());
            jObj.put("invname", JsonReMod.getString("invname").toString());
            jObj.put("invspec", JsonReMod.getString("invspec").toString());
            jObj.put("invtype", JsonReMod.getString("invtype").toString());
            jObj.put("billcode", JsonReMod.getString("billcode").toString());
            jObj.put("batchcode", JsonReMod.getString("batchcode").toString());
            jObj.put("invbasdocid", JsonReMod.getString("invbasdocid").toString());
            jObj.put("invmandocid", JsonReMod.getString("invmandocid").toString());
            jObj.put("number", JsonReMod.getString("number").toString());
            jObj.put("outnumber", JsonReMod.getString("outnumber").toString());
            jObj.put("sourcerowno", JsonReMod.getString("sourcerowno").toString());
            jObj.put("sourcehid", JsonReMod.getString("sourcehid").toString());
            jObj.put("sourcebid", JsonReMod.getString("sourcebid").toString());
            jObj.put("sourcehcode", JsonReMod.getString("sourcehcode").toString());
            jObj.put("sourcetype", JsonReMod.getString("sourcetype").toString());
            jObj.put("crowno", JsonReMod.getString("crowno").toString());
            jObj.put("billhid", JsonReMod.getString("billhid").toString());
            jObj.put("billbid", JsonReMod.getString("billbid").toString());
            jObj.put("billhcode", JsonReMod.getString("billhcode").toString());
            jObj.put("billtype", JsonReMod.getString("billtype").toString());
            jObj.put("def6", JsonReMod.getString("def6").toString());
            jObj.put("ddeliverdate", JsonReMod.getString("ddeliverdate").toString());
            jObj.put("pk_defdoc6", JsonReMod.getString("pk_defdoc6").toString());

            //需要修改

            //修改数量问题
            int iTasknnum = Integer.valueOf(Tasknnum);

            String snnum = (nnum.replaceAll("\\.0", ""));

            int innum = Integer.valueOf(snnum);

            int inewnnum = iTasknnum + innum;
            String snewnnum = inewnnum + "";

            jObj.put("number", snewnnum);//修改数量问题

            JSONArray JsonArraysRemod = new JSONArray();
            JSONObject jObjReMod = new JSONObject();
            for (int i = 0; i < JsonArrays.length(); i++) {
                String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i))).getString("billbid");
                String InvCodeaDel = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
                String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i))).getString("crowno");


                if (!csourcebillhidDel.equals(csourcebillhid) || !InvCodeaDel.equals(InvCode) || !csourcerownoaDel.equals(csourcerowno)) {
                    jObjReMod = (JSONObject) JsonArrays.get(i);
                    JsonArraysRemod.put(jObjReMod);
                }
            }

            JsonArrays = JsonArraysRemod;
            JsonArrays.put(jObj);
            JsonModTaskData.remove(sKey);
        }


        jsonBodyTask = new JSONObject();
        jsonBodyTask.put("Status", true);
        jsonBodyTask.put("dbBody", JsonArrays);


        if (JsonModTaskData.has(sKey)) {
            jsonBodyTask.put("ModTaskData", JsonModTaskData);
        }
        getTaskListData(jsonBodyTask);

    }

    //删除已扫描详细的监听事件
    private class ButtonOnClickDelconfirm implements DialogInterface.OnClickListener {

        public int index;
        public String key;
        public String BarCode;

        public ButtonOnClickDelconfirm(int iIndex, String sKey, String BarCode) {
            this.index = iIndex;
            this.key = sKey;
            this.BarCode = BarCode;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                try {
                    ConfirmDelItem(index, key, BarCode);
                } catch (JSONException e) {
                    Toast.makeText(SalesDeliveryScan.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                }
            } else
                return;
        }

    }

    //EditText输入后回车的监听事件
    @NonNull
    private OnKeyListener EditTextOnKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(@NonNull View v, int arg1, @NonNull KeyEvent arg2) {
            switch (v.getId()) {
                case id.txtSDScanBarcode:
                    if (arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP) {
                        try {

                            String Bar = txtSDScanBarcode.getText().toString().replace("\n", "");
                            //txtSDScanBarcode.setText(txtSDScanBarcode.getText().toString().replace("\n", ""));
                            ScanBarcode(Bar);
                        } catch (ParseException e) {
                            txtSDScanBarcode.setText("");
                            txtSDScanBarcode.requestFocus();
                            Toast.makeText(SalesDeliveryScan.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            //ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            //ADD CAIXY TEST END
                        } catch (JSONException e) {
                            txtSDScanBarcode.setText("");
                            txtSDScanBarcode.requestFocus();
                            Toast.makeText(SalesDeliveryScan.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            //ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            //ADD CAIXY TEST END
                        } catch (IOException e) {
                            txtSDScanBarcode.setText("");
                            txtSDScanBarcode.requestFocus();
                            Toast.makeText(SalesDeliveryScan.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            //ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            //ADD CAIXY TEST END
                        }
                        return true;
                    }
                    break;
            }
            return true;
        }
    };

    //Button按下后的监听事件
    @NonNull
    private OnClickListener ButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(@NonNull View v) {
            switch (v.getId()) {
                case id.btnSDScanTask:

                    if (lstBodyTask == null || lstBodyTask.size() < 1)
                        return;

                    listItemTaskAdapter  = new SimpleAdapter(SalesDeliveryScan.this, lstBodyTask,
                            R.layout.saleouttask,
                            new String[]{"InvName","InvNum","InvCode", "invspec", "invtype","numChange"},
                            new int[]{R.id.txtTranstaskInvName, R.id.txtTranstaskInvNum,
                                    R.id.txtTranstaskInvCode,R.id.txtSpec,
                                    R.id.txtType,R.id.txtScanNum}
                    );
                    new AlertDialog.Builder(SalesDeliveryScan.this).setTitle("源单信息")
                            .setAdapter(listItemTaskAdapter, null)
                            .setPositiveButton(R.string.QueRen, null).show();
                    break;
                case id.btnSDScanClear:

                    if (listViewScanDetail.getCount() < 1)
                        //MOD BY WUQIONG END
                        return;

                    ButtonOnClickClearconfirm btnScanItemClearOnClick = new ButtonOnClickClearconfirm();
                    DeleteAlertDialog = new AlertDialog.Builder(SalesDeliveryScan.this).setTitle("确认清空")
                            .setMessage("你确认要清空记录吗?")
                            .setPositiveButton(R.string.QueRen, btnScanItemClearOnClick).setNegativeButton(R.string.QuXiao, null).show();

                    break;
                //返回按钮
                case id.btnSDScanReturn:


//                    int count = lstSaveBody.size();
//                    List<SaleOutGoods> saleGoodsLists = new ArrayList<SaleOutGoods>();
//                    HashMap<String, Object> map;
//                    SaleOutGoods saleOutGoods = null;
//                    for (int i = 0; i < count; i++) {
//                        map = (HashMap<String, Object>) lstSaveBody.get(i);
//                        saleOutGoods = new SaleOutGoods();
//                        saleOutGoods.setBarcode((String) map.get("BarCode"));
//                        saleOutGoods.setInvCode((String) map.get("InvCode"));
//                        saleOutGoods.setInvName((String) map.get("InvName"));
//                        saleOutGoods.setBatch((String) map.get("Batch"));
//                        saleOutGoods.setPk_invbasdoc((String) map.get("BarCode"));
//                        saleOutGoods.setPk_invmandoc((String) map.get("BarCode"));
//                        saleOutGoods.setUnit((String) map.get("Measname"));
//                        saleOutGoods.setQty(Float.valueOf((String) map.get("Weights")));
//                        saleGoodsLists.add(saleOutGoods);
//                    }
                    if (saleGoodsLists.size() > 0) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("SaleGoodsLists", (ArrayList<? extends Parcelable>) saleGoodsLists);
                        intent.putExtras(bundle);
//                        SerializableList ResultBodyList = new SerializableList();
//                        ResultBodyList.setList(lstSaveBody);
//                        intent.putExtra("SaveBodyList", ResultBodyList);
//                        intent.putExtra("ScanTaskJson", jsonBodyTask.toString());
//                        intent.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
                        SalesDeliveryScan.this.setResult(6, intent);

                    }else {
                        Toast.makeText(SalesDeliveryScan.this, "没有扫描单据", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    break;
            }
        }
    };

    public static class MyListAdapter extends BaseAdapter {
        @Nullable
        private Context                   context     = null;
        @Nullable
        private LayoutInflater            inflater    = null;
        @Nullable
        private List<Map<String, Object>> list        = null;
        @NonNull
        private String                    keyString[] = new String[]{};
        @Nullable
        private String                    itemString0 = null; // 记录每个item中textview的值
        @Nullable
        private String                    itemString1 = null;
        @Nullable
        private String                    itemString2 = null;
        @Nullable
        private int                       idValue[]   = null;// id值

        public MyListAdapter(Context context, List<Map<String, Object>> list,
                             int resource, @NonNull String[] from, @NonNull int[] to) {
            this.context = context;
            this.list = list;
            keyString = new String[from.length];
            idValue = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, idValue, 0, to.length);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Nullable
        @Override
        public View getView(int arg0, @Nullable View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub
            //Map<String,Object> map = (Map<String,Object>)getItem(arg0);
            //if(!map.get("ScanedNum").toString().equals(map.get("TotalNum").toString()))
            if (arg1 == null) {
                arg1 = inflater.inflate(R.layout.vlisttransscanitem, null);
            }
            Map<String, Object> map = list.get(arg0);
            TextView tvItem = null;
            if (map != null) {
                for (int i = 0; i < keyString.length; i++) {
                    tvItem = (TextView) arg1.findViewById(idValue[i]);
                    tvItem.setText(map.get(keyString[i]).toString());
                }
//	                if(!map.get("ScanedNum").toString().equals(map.get("TotalNum").toString()))
//	    	    		arg1.setBackgroundResource(R.color.lightpink);
//	    	    	else
//	    	    		arg1.setBackgroundColor(Color.TRANSPARENT);
            }

            return arg1;
        }

    }

//    public void addItemsTo(String InvName,String InvCode,String Spec,String Type,String Number,String numChange){
//        Map<String,Object> map= new HashMap<String, Object>();
//        map.put("numChange", numChange);
//        map.put("InvCode",InvCode);
//        map.put("InvName",InvName);
//        map.put("inspec",Spec);
//        map.put("intype",Type);
//        map.put("InNum",Number);
//        lstBodyTask.clear();
//        lstBodyTask.add(map);
//        listViewScanDetail.invalidateViews(); //listView刷新
//    }
}
