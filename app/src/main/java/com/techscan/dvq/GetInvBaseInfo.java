package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by walter on 2017/6/29.
 */

public class GetInvBaseInfo {

    public HashMap<String,Object> mapInvBaseInfo = null;

    /**
     * 获取存货基本信息
     *
     *
     */
    public GetInvBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler) {
        mapInvBaseInfo = new HashMap<String, Object>();
        mapInvBaseInfo.put("barcodetype",cSplitBarcode.BarcodeType);
        mapInvBaseInfo.put("batch",cSplitBarcode.cBatch);
        mapInvBaseInfo.put("serino",cSplitBarcode.cSerino);
        mapInvBaseInfo.put("quantity",cSplitBarcode.dQuantity);
        mapInvBaseInfo.put("number",cSplitBarcode.iNumber);
        mapInvBaseInfo.put("taxflag",cSplitBarcode.TaxFlag);
        mapInvBaseInfo.put("outsourcing",cSplitBarcode.Outsourcing);
        mapInvBaseInfo.put("barcode",cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", cSplitBarcode.cInvCode);
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

    public void SetInvBaseToParam(JSONObject json) throws JSONException {
        //Log.d(TAG, "SetInvBaseToUI: " + json);
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            //mapInvBaseInfo = null;
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                //mapInvBaseInfo = new HashMap<String, Object>();
                mapInvBaseInfo.put("invname", tempJso.getString("invname"));   //物料名称
                mapInvBaseInfo.put("invcode", tempJso.getString("invcode"));   //物料号
                mapInvBaseInfo.put("measname", tempJso.getString("measname"));   //单位
                mapInvBaseInfo.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));//物料bas PK
                //pk_invbasdoc = tempJso.getString("pk_invbasdoc");
                mapInvBaseInfo.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));//物料man PK
                //pk_invmandoc = tempJso.getString("pk_invmandoc");
                mapInvBaseInfo.put("invtype", tempJso.getString("invtype"));   //型号
                mapInvBaseInfo.put("invspec", tempJso.getString("invspec"));   //规格
                mapInvBaseInfo.put("oppdimen", tempJso.getString("oppdimen"));
            }

        }
    }

    public static class SaleBillInfoOrderList extends Activity {

        //private Button btnSaleBillInfoReturn;
        private ListView lvSaleBillInfoOrderList;

        private String fsFunctionName = "";
        private String fsBillCode     = "";
        private String sDate;
        private String sEndDate;
        private String sBillCodes;
        private String whID;
        List<Map<String, Object>> list                          = new ArrayList<Map<String, Object>>();
        Button                    btSaleBillInfoOrderListReturn = null;

        //ADD CAIXY TEST START
    //	private SoundPool sp;//声明一个SoundPool
    //	private int MainLogin.music;//定义一个int来设置suondID
        //ADD CAIXY TEST END

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sale_bill_info_order_list);

            //设置title
            ActionBar actionBar = this.getActionBar();
            actionBar.setTitle("订单明细");
    //		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
    //		actionBar.setBackgroundDrawable(TitleBar);
    //		actionBar.show();

            btSaleBillInfoOrderListReturn = (Button) findViewById(R.id.btSaleBillInfoOrderListReturn);
            btSaleBillInfoOrderListReturn.setOnClickListener(ButtonOnClickListener);

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                                               .detectNetwork()
                                               .penaltyLog()
                                               .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
                    detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

            //取得查询用单据表名
            Intent myIntent = this.getIntent();
            if (myIntent.hasExtra("FunctionName"))
                fsFunctionName = myIntent.getStringExtra("FunctionName");
            if (myIntent.hasExtra("BillCodeKey"))
                fsBillCode = myIntent.getStringExtra("BillCodeKey");
            if (myIntent.hasExtra("sBeginDate"))
                sDate = myIntent.getStringExtra("sBeginDate");
            if (myIntent.hasExtra("sEndDate"))
                sEndDate = myIntent.getStringExtra("sEndDate");
            if (myIntent.hasExtra("sBillCodes"))
                sBillCodes = myIntent.getStringExtra("sBillCodes");
            if (myIntent.hasExtra("whId"))
                whID = myIntent.getStringExtra("whId");


            //取得控件
            //btnSaleBillInfoReturn = (Button)findViewById(R.id.btnSaleBillInfoReturn);
            //btnSaleBillInfoReturn.setOnClickListener(ButtonClickListener);
            lvSaleBillInfoOrderList = (ListView) findViewById(R.id.SaleBillInfoOrderList);

            //ADD CAIXY START
    //		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
    //		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
            //ADD CAIXY END

            //取得以及绑定显示订单详细
            GetAndBindingBillInfoDetail();

        }

        private View.OnClickListener ButtonOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {            //btnSDScanReturn
                    case R.id.btSaleBillInfoOrderListReturn:
                        finish();
                        break;
                }
            }
        };

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
    //		getMenuInflater().inflate(R.menu.sale_bill_info_order_list, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        //取得以及绑定显示订单详细
        private void GetAndBindingBillInfoDetail() {
            //取得所有单据信息
            if (fsFunctionName.equals("")) {
                Toast.makeText(this, "没有查询用单据表名", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }

//            int x = 1;


//            GetAdjustOrderBillHead(String CompanyCode,String BillCode, String OrgId, String WhID, String TableName)

//            for (int i = 0; i < x; i++) {
//
//                if (x == 2) {
//                    if (i == 0) {
//                        fsFunctionName = "GetSaleTakeHead";
//                    } else {
//                        fsFunctionName = "GetSaleOutHead";
//                    }
//                }

                JSONObject para = new JSONObject();
                try {
                    if (fsFunctionName.equals("销售出库")) {
                        fsFunctionName = "GetSaleOrderList";
                        para.put("FunctionName", fsFunctionName);
                        para.put("CorpPK", MainLogin.objLog.CompanyCode);
                        para.put("STOrgCode", MainLogin.objLog.STOrgCode);
                        para.put("BillCode", sBillCodes);
                        para.put("sDate", sDate);
                        para.put("sEndDate", sEndDate);
                    }
                    //多角贸易获取list TODO: 2017/7/31
                    else if (fsFunctionName.equals("多角贸易")) {
                        fsFunctionName = "GetAdjListOrder";
                        para.put("FunctionName", fsFunctionName);
                        para.put("COMPANYCODE", MainLogin.objLog.CompanyCode);
                        para.put("WHID",whID);
                        para.put("VBILLCODE", sBillCodes);
                        para.put("STORGCODE", MainLogin.objLog.STOrgCode);
                        para.put("SDATE", sDate);
                        para.put("EDATE", sEndDate);
                    }

                } catch (JSONException e2) {
                    // TODO Auto-generated catch block
                    Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    e2.printStackTrace();
                    return;
                }
                try {
                    para.put("TableName", "dbHead");
                } catch (JSONException e2) {
                    // TODO Auto-generated catch block
                    Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }

                JSONObject jas;
                try {
//                    if (!MainLogin.getwifiinfo()) {
//                        Toast.makeText(this, "WIFI信号差!请保持网络畅通", Toast.LENGTH_LONG).show();
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        return;
//                    }
                    jas = Common.DoHttpQuery(para, "CommonQuery", "");
                    Log.d(TAG, "JAS: " + jas.toString());

                } catch (Exception ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }

                //把取得的单据信息绑定到ListView上
                try {
                    if (jas == null) {
                        Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        //ADD CAIXY TEST END
                        return;
                    }

                    if (!jas.has("Status")) {
                        Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }


                    if (!jas.getBoolean("Status")) {
                        String errMsg = "";
                        if (jas.has("ErrMsg")) {
                            errMsg = jas.getString("ErrMsg");
                        } else {
                            errMsg = "网络操作出现问题!请稍后再试";
                        }
                        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        //ADD CAIXY TEST END
                        return;
                    }
                    //绑定到ListView
                    BindingBillInfoData(jas);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                } catch (Exception ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }
//            }

        }

        //绑定到ListView
        private void BindingBillInfoData(JSONObject jsonBillInfo) throws JSONException {

            Map<String, Object> map;

            JSONObject tempJso = null;

            if (!jsonBillInfo.has("Status")) {
                Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            if (!jsonBillInfo.getBoolean("Status")) {
                String errMsg = "";
                if (jsonBillInfo.has("ErrMsg")) {
                    errMsg = jsonBillInfo.getString("ErrMsg");
                } else {
                    errMsg = "网络操作出现问题!请稍后再试";
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                list = null;
            }

            JSONArray jsarray = jsonBillInfo.getJSONArray("dbHead");

            for (int i = 0; i < jsarray.length(); i++) {
                tempJso = jsarray.getJSONObject(i);
                map = new HashMap<String, Object>();
                if (fsFunctionName.equals("GetSaleOrderList"))//销售出库单
                {
                    map.put("BillDate", tempJso.getString("dbilldate"));
                    map.put("BillCode", tempJso.getString("vreceiptcode"));
                    map.put("CustName", tempJso.getString("custname"));
                    map.put("Csaleid", tempJso.getString("csaleid"));
                    map.put("saleflg", "");

                }

                if (fsFunctionName.equals("GetAdjListOrder"))//多角贸易
                {
//                    map.put("BillDate", "2017-08-09" );
                    map.put("BillDate", tempJso.getString("dbilldate"));
                    map.put("OutCompany", tempJso.getString("coutcompanyname"));
                    map.put("InCompany", tempJso.getString("coincompanycode"));
                    map.put("cbillid", tempJso.getString("cbillid"));
                    map.put("coutcompanyid", tempJso.getString("coutcompanyid"));
                    map.put("coincompanyid", tempJso.getString("coincompanyid"));
                    map.put("vbillcode", tempJso.getString("vbillcode"));
                    map.put("saleflg", "");

                }

                list.add(map);
            }
            SimpleAdapter listItemAdapter = null;
            if(fsFunctionName.equals("GetSaleOrderList")){
                listItemAdapter = new SimpleAdapter(this, list,
                        R.layout.vlistsaledel,
                        new String[]{"BillCode", "CustName", "BillDate"},
                        new int[]{R.id.tvBillCode,
                                R.id.tvCustomer,
                                R.id.tvBillDate});

                if (listItemAdapter == null)
                    return;
                lvSaleBillInfoOrderList.setAdapter(listItemAdapter);
                lvSaleBillInfoOrderList.setOnItemClickListener(itemListener);
            }
             else if (fsFunctionName.equals("GetAdjListOrder")){
                listItemAdapter = new SimpleAdapter(this, list,
                        R.layout.multilateral_trade,
                        new String[]{"vbillcode", "BillDate","OutCompany","InCompany"},
                        new int[]{R.id.tvBillCode,
                                R.id.tvBillDate,
                                R.id.tvCOutCompany,
                                R.id.tvCInCompany,});

                if (listItemAdapter == null)
                    return;
                lvSaleBillInfoOrderList.setAdapter(listItemAdapter);
                lvSaleBillInfoOrderList.setOnItemClickListener(itemListener);
            }


        }

        //ListView的Item点击监听事件
        private ListView.OnItemClickListener itemListener = new
                ListView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {

                        if (fsFunctionName.equals("GetSaleOrderList"))//多角贸易
                        {

                            Adapter             adapter = arg0.getAdapter();
                            Map<String, Object> map     = (Map<String, Object>) adapter.getItem(arg2);

                            SerializableMap ResultMap = new SerializableMap();
                            ResultMap.setMap(map);
                            Intent intent = new Intent();
                            intent.putExtra("ResultBillInfo", ResultMap);

                            SaleBillInfoOrderList.this.setResult(1, intent);
                            SaleBillInfoOrderList.this.finish();
                        }

                        if (fsFunctionName.equals("GetAdjListOrder"))//多角贸易
                        {

                            Adapter             adapter = arg0.getAdapter();
                            Map<String, Object> map     = (Map<String, Object>) adapter.getItem(arg2);

                            SerializableMap ResultMap = new SerializableMap();
                            ResultMap.setMap(map);
                            Intent intent = new Intent();
                            intent.putExtra("ResultBillInfo", ResultMap);

                            SaleBillInfoOrderList.this.setResult(1, intent);
                            SaleBillInfoOrderList.this.finish();
                        }


                    }

                };

        //button按钮的监听事件
        private Button.OnClickListener ButtonClickListener = new
                Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
    //				case R.id.btnSaleBillInfoReturn:
    //					SaleBillInfoOrderList.this.finish();
    //					break;
                        }

                    }

                };

    }
}
