package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;

import org.apache.http.ParseException;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StockTransScanIn extends Activity {


    @Nullable
    String fileNameScan   = null;
    @Nullable
    String ScanedFileName = null;
    @Nullable
    String UserID         = null;
    @Nullable
    File   fileScan       = null;
    @NonNull
    String ReScanBody     = "1";
    private writeTxt writeTxt;        //保存LOG文件
    //String tmpCorpPK = "";


    @Nullable
    JSONArray jsarraySERINO = null;

    @Nullable
            EditText          txtTIScanBarcode = null;
    @Nullable
            ListView          lvTIScanDetail   = null;
    @Nullable
            Button            btnTIScanTask    = null;
    @Nullable
            Button            btnTIScanReturn  = null;
    @Nullable
            Button            btnTIScanClear   = null;
    @NonNull
    private String            OkFkg            = "ng";
    @NonNull
            String            ScanInvOK        = "0";
    private ArrayList<String> ScanedBarcode    = new ArrayList<String>();

    @Nullable
    private SplitBarcode bar        = null;            //当前扫描条码解析
    @Nullable
    private Inventory    currentObj = null;        //当前扫描到的存货信息

    private String     tmpAccIDA    = "";
    private String     tmpAccIDB    = "";
    @Nullable
    private JSONObject jsonBodyTask = null;

    @Nullable
    private List<Map<String, Object>> lstBodyTask = null;
    @Nullable
    private List<Map<String, Object>> lstSaveBody = null;

    //ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
//	private int MainLogin.music2;//定义一个int来设置suondID
    private Integer ScanedQty;
    //private String cgeneralhid = "";
    //ADD CAIXY TEST END
    //ADD BY WUQIONG START
    @Nullable
    TextView tvscancount = null;
    int listcount = 0;
    int Tasknnum = 0;

    private String wareHousePKFromA = "";
    private String wareHousePKFromB = "";
    private String wareHousePKToA = "";
    private String wareHousePKToB = "";
    String PKcorpFrom = "";
    String PKcorpTo = "";

    String tmpWHStatus = "";
    String tmpBillStatus = "";

    String tmpposIDA = "";
    String tmpposIDB = "";


    //定义是否删除Dialog
    @Nullable
    private AlertDialog DeleteAlertDialog = null;

    //ADD BY WUQIONG START
    //删除的任务内容临时保存住
    private JSONObject JsonModTaskData = new JSONObject();
    //ADD BY WUQIONG END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trans_scan_in);

        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(R.string.DiaoBoRuKuSaoMiaoMingXi);
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		actionBar.show();

        //设置控件
        txtTIScanBarcode = (EditText) findViewById(R.id.txtTIScanBarcode);
        txtTIScanBarcode.setOnKeyListener(EditTextOnKeyListener);

        lvTIScanDetail = (ListView) findViewById(R.id.lstTIScanDetail);
        lvTIScanDetail.setOnItemClickListener(myListItemListener);
        lvTIScanDetail.setOnItemLongClickListener(myListItemLongListener);

        btnTIScanTask = (Button) findViewById(R.id.btnTIScanTask);
        btnTIScanTask.setOnClickListener(ButtonOnClickListener);
        btnTIScanClear = (Button) findViewById(R.id.btnTIScanClear);
        btnTIScanClear.setOnClickListener(ButtonOnClickListener);
        btnTIScanReturn = (Button) findViewById(R.id.btnTIScanReturn);
        btnTIScanReturn.setOnClickListener(ButtonOnClickListener);

        //ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1);
        //ADD CAIXY END

        //获得父画面传过来的数据
        Intent myintent = getIntent();
        //tmpAccID = myintent.getStringExtra("AccID");
        tmpAccIDA = myintent.getStringExtra("AccIDA");
        tmpAccIDB = myintent.getStringExtra("AccIDB");

        //ADD BY WUQIONG START
        tvscancount = (TextView) findViewById(R.id.tvSTSIncounts);
        Tasknnum = Integer.valueOf(myintent.getStringExtra("TaskCount").toString());
        ScanedBarcode = myintent.getStringArrayListExtra("ScanedBarcode");
        //tmpCorpPK = myintent.getStringExtra("CorpPK");

//		wareHousePKFrom = myintent.getStringExtra("wareHousePKFrom");
//		wareHousePKTo = myintent.getStringExtra("wareHousePKTo");
        wareHousePKFromA = myintent.getStringExtra("wareHousePKFromA");
        wareHousePKToA = myintent.getStringExtra("wareHousePKToA");
        wareHousePKFromB = myintent.getStringExtra("wareHousePKFromB");
        wareHousePKToB = myintent.getStringExtra("wareHousePKToB");
        PKcorpFrom = myintent.getStringExtra("PKcorpFrom");
        PKcorpTo = myintent.getStringExtra("PKcorpTo");
        tmpWHStatus = myintent.getStringExtra("tmpWHStatus");
        tmpBillStatus = myintent.getStringExtra("tmpBillStatus");

        if (tmpWHStatus.equals("Y")) {
            tmpposIDA = myintent.getStringExtra("tmpposIDA");
            tmpposIDB = myintent.getStringExtra("tmpposIDB");
        }


        //cgeneralhid = myintent.getStringExtra("cgeneralhid").toString();
        //ADD BY WUQIONG END

        //获得父画面传过来的扫描详细数据
        SerializableList lstScanSaveDetial = new SerializableList();
        lstScanSaveDetial = (SerializableList) myintent.getSerializableExtra("lstScanSaveDetial");
        lstSaveBody = lstScanSaveDetial.getList();
        if (lstSaveBody != null) {
            if (lstSaveBody.size() > 0) {
                MyListAdapter listItemAdapter = new MyListAdapter(StockTransScanIn.this, lstSaveBody,//数据源
                        R.layout.vlisttransscanitem,
                        new String[]{"InvCode", "InvName", "Batch", "AccID", "TotalNum",
                                "BarCode", "SeriNo", "BillCode", "ScanedNum", "box"},
                        new int[]{R.id.txtTransScanInvCode, R.id.txtTransScanInvName,
                                R.id.txtTransScanBatch, R.id.txtTransScanAccId,
                                R.id.txtTransScanTotalNum, R.id.txtTransScanBarCode,
                                R.id.txtTransScanSeriNo, R.id.txtTransScanBillCode,
                                R.id.txtTransScanScanCount, R.id.txtTransBox}
                );
                lvTIScanDetail.setAdapter(listItemAdapter);
//				//ADD BY WUQIONG START
                listcount = lstSaveBody.size();

//				//ADD BY WUQIONG END
            }
        }
        //JSONObject jonsTaskBody=null;
        jsonBodyTask = Common.jsonBodyTask;
        JsonModTaskData = Common.JsonModTaskData;

        //获得父画面传过来的任务详细数据
//        String lsTaskJosnBody = myintent.getStringExtra("TaskJonsBody");
//		JSONObject jonsTaskBody=null;
//		try {
//			jonsTaskBody = new JSONObject(lsTaskJosnBody);
//		} catch (JSONException e) {
//			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//			//ADD CAIXY TEST START
//			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//			//ADD CAIXY TEST END
//			return;
//		}
//		if(jonsTaskBody.has("dbBody"))
//		{
//			this.jsonBodyTask=jonsTaskBody;
//		}


        //ModTask剥离

//		 String lsScanModTask = myintent.getStringExtra("ScanModTask");
//
//		 try {
//				jonsTaskBody = new JSONObject(lsScanModTask);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//				//ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				//ADD CAIXY TEST END
//				return;
//			}
////			if(jonsTaskBody.has("TransBillBody"))
////			{
//			this.JsonModTaskData=jonsTaskBody;
////			}


        tvscancount.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount + "件 | " + "未扫" + (Tasknnum - listcount) + "件");


        //		//ADD BY caixy END
        //DEL BY WUQIONG S
//		if(jsonBodyTask.has("RemoveTaskData"))
//		{
//			try {
//				JsonRemoveTaskData=(JSONObject)jsonBodyTask.get("RemoveTaskData");
//			} catch (JSONException e) {
//				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//				//ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				//ADD CAIXY TEST END
//				e.printStackTrace();
//			}
//		}
        //DEL BY WUQIONG E

        //ADD BY WUQIONG START
//		if(jonsTaskBody.has("ModTaskData"))
//		{
//			try {
//				JsonModTaskData=(JSONObject)jonsTaskBody.get("ModTaskData");
//			} catch (JSONException e) {
//				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//				//ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				//ADD CAIXY TEST END
//				e.printStackTrace();
//			}
//		}
        //ADD BY WUQIONG END
        txtTIScanBarcode.requestFocus();
        try {
            getTaskListData(this.jsonBodyTask);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            e.printStackTrace();
        }

        UserID = MainLogin.objLog.UserID;
        //String LogName = BillType + UserID + dfd.format(day)+".txt";
        ScanedFileName = "4EScan" + UserID + ".txt";
        fileNameScan = "/sdcard/DVQ/4EScan" + UserID + ".txt";
        fileScan = new File(fileNameScan);
        try {
            GetSerNoInfo();
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        btnTIScanTask.setFocusable(false);
        btnTIScanReturn.setFocusable(false);
        btnTIScanClear.setFocusable(false);
        ReScanBody();
    }

    private void ReScanErr() {
        AlertDialog.Builder bulider =
                new AlertDialog.Builder(this).setTitle(R.string.CuoWu).setMessage(getString(R.string.ShuJuJiaZaiCuoWu)+"\r\n"+getString(R.string.TuiChuGaiMoKuai));

        bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false).create().show();
        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        return;
    }

    @NonNull
    private DialogInterface.OnClickListener listenExit = new
            DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    finish();
                    System.gc();
                }
            };

    private void ReScanBody() {
        String res = "";


        if (ScanedBarcode.size() > 0) {
            ReScanBody = "1";
            return;
        }


        if (!fileScan.exists()) {
            ReScanBody = "1";
            return;
        }

        try {

            FileInputStream fin = new FileInputStream(fileNameScan);

            int length = fin.available();

            byte[] buffer = new byte[length];

            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            res = res.substring(0, res.length() - 2);

            fin.close();

            ArrayList<String> ScanedBillBar = new ArrayList<String>();

            String[] Bars;
            if (res.contains(",")) {
                ReScanBody = "0";
                Bars = res.split("\\,");

                for (int i = 0; i < Bars.length; i++) {
                    ScanedBillBar.add(Bars[i]);
                }
            } else {
                ReScanBody = "0";
                ScanedBillBar.add(res);
            }


            if (ScanedBillBar.size() < 1) {
                ReScanBody = "1";
                return;
            }

            int x = 0;

            for (int i = 0; i < ScanedBillBar.size(); i++) {

                if (x > 10) {
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    Common.ReScanErr = true;
                    ReScanErr();
                    return;
                }

                ScanBarcode(ScanedBillBar.get(i).toString());

                String OKflg = "ng";

                if (ScanedBarcode == null || ScanedBarcode.size() < 1) {
                    x++;
                    i--;
                } else {
                    for (int j = 0; j < ScanedBarcode.size(); j++) {

                        String AAA = ScanedBarcode.get(j);
                        if (ScanedBillBar.get(i).toString().equals(AAA)) {
                            OKflg = "ok";
                        }

                    }
                    if (!OKflg.equals("ok")) {
                        x++;
                        i--;
                    }
                }
            }

            this.txtTIScanBarcode.requestFocus();
            ReScanBody = "1";

        } catch (Exception e) {

            e.printStackTrace();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

        }

    }

    private boolean FindInvnNoBinStockInfo() throws JSONException, ParseException, IOException {
        if (ReScanBody.equals("0")) {
            return true;
        }

        JSONObject para = new JSONObject();
        para.put("FunctionName", "SearchGetStockInfo");
        para.put("TableName", "InvInfo");
        para.put("InvCode", currentObj.getInvCode());
        para.put("InvBatch", currentObj.GetBatch());

        String CompanyCode = PKcorpTo;

        para.put("CompanyCode", CompanyCode);

        para.put("accId", this.bar.AccID);

//    	    para.put("LotB", this.currentObj.GetBatch());
//    	    para.put("TableName", "Stock");
        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }
        JSONObject StockInfo = Common.DoHttpQuery(para, "CommonQuery", this.bar.AccID);

        if (StockInfo == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!StockInfo.has("Status")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (StockInfo.getBoolean("Status")) {
            JSONArray val = StockInfo.getJSONArray("InvInfo");
            if (val.length() < 1) {
                Toast.makeText(this, R.string.HuoQuHuoWeiKuCunShuJu, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }

            double stockCount = 0.0;

            for (int iv = 0; iv < val.length(); iv++) {
                JSONObject temp = val.getJSONObject(iv);

//					if(tmpBillStatus.equals("Y"))
//					{
//						if(temp.getString("pk_stordoc").equals(wareHousePKFrom))
//						{
//							if(Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0)
//							{
//								stockCount = stockCount + Double.valueOf(temp.getString("nnum")).doubleValue();
//							}
//						}
//					}
//					else
//					{
//						if(temp.getString("pk_stordoc").equals(wareHousePKTo))
//						{
//							if(Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0)
//							{
//								stockCount = stockCount + Double.valueOf(temp.getString("nnum")).doubleValue();
//							}
//						}
                if (bar.AccID.equals("A")) {
                    if (temp.getString("pk_stordoc").equals(wareHousePKToA)) {
                        if (Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0) {
                            stockCount = stockCount + Double.valueOf(temp.getString("nnum")).doubleValue();
                        }
                    }
                }
                if (bar.AccID.equals("B")) {
                    if (temp.getString("pk_stordoc").equals(wareHousePKToB)) {
                        if (Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0) {
                            stockCount = stockCount + Double.valueOf(temp.getString("nnum")).doubleValue();
                        }
                    }
                }
//					}
            }

            double sancount = getScanCount(currentObj.Invmandoc(), currentObj.GetBatch());
            sancount += 1.0;
            if (sancount > stockCount) {
                Toast.makeText(this, R.string.GaiHuoWeiZaiDeFaChuKuCunYiJingBuZule, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }
        } else {
            Toast.makeText(this, R.string.GaiHuoPinZaiGaiCangHuZhongMeiYouKuCunXinXi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        return true;
    }

    double getScanCount(String InvID, String BatchCode) {
        double spacenum = 0.0;
        if (lstSaveBody == null) {
            return spacenum;
        }

        for (int i = 0; i < lstSaveBody.size(); i++) {
            Map<String, Object> values = lstSaveBody.get(i);
            if ((values.get("cinventoryid").equals(InvID)) && (values.get("vbatchcode").equals(BatchCode))) {
                spacenum += Double.valueOf((String) values.get("spacenum")).doubleValue();
            }
        }
        return spacenum;
    }

    private void SaveScanedBody() {

        if (ReScanBody.equals("0")) {
            return;
        }

        if (ScanedBarcode == null || ScanedBarcode.size() < 1) {
            if (fileScan.exists()) {
                fileScan.delete();
            }
            return;
        }

        String BillBarCode = "";

        writeTxt = new writeTxt();

        //记录扫描数据
        String ScanedBar = "";


        for (int i = 0; i < ScanedBarcode.size(); i++) {

            if (i == ScanedBarcode.size() - 1)
                BillBarCode = BillBarCode + ScanedBarcode.get(i).toString();
            else
                BillBarCode = BillBarCode + ScanedBarcode.get(i).toString() + ",";
        }
        ScanedBar = BillBarCode;


        if (fileScan.exists()) {
            fileScan.delete();
        }

        writeTxt.writeTxtToFile(ScanedFileName, ScanedBar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stock_trans_scan_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

    @Nullable
    private static AlertDialog    SelectLine     = null;
    @NonNull
    private        buttonOnClickC buttonOnClickC = new buttonOnClickC(0);
    @NonNull
    static         String[]       LNameList      = new String[2];

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
                R.string.QieHuanChengGong).setMessage(getString(R.string.YiJingQieHuanZhi) + WebName + "\r\n" + CommonUrl);

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
        public void onClick(@NonNull DialogInterface dialog, int whichButton) {
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
            Toast.makeText(this, R.string.MeiYouDeDaoBiaoTiShuJu, Toast.LENGTH_LONG).show();
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
            map.put("InvName", ((JSONObject) (arrays.get(i))).getString("invname"));
            map.put("InvCode", ((JSONObject) (arrays.get(i))).getString("invcode"));
            String batchs = ((JSONObject) (arrays.get(i))).getString("vbatchcode");
            if (batchs == null || batchs.equals("") || batchs.equals("null")) {
                batchs = getString(R.string.PiCiWeiZhiDing);
            }
            map.put("Batch", batchs);
            //map.put("AccID", tmpAccID);
            map.put("AccID", ((JSONObject) (arrays.get(i))).getString("accid"));


            String nnum = ((JSONObject) (arrays.get(i))).getString("noutnum");
            String ntranoutnum = ((JSONObject) (arrays.get(i))).getString("ntranoutnum");
            //TaskCount = TaskCount + Integer.valueOf(((JSONObject)(JsonArrays.get(i))).getString("nnum").toString());
            String snnum = "0";
            if (!ntranoutnum.equals("null")) {
                snnum = (ntranoutnum.replaceAll("\\.0", ""));
            }

            int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

            String Tasknnum = shouldinnum + "";

            map.put("InvNum", Tasknnum);
            //map.put("InvNum", ((JSONObject)(arrays.get(i))).getString("noutnum"));

            map.put("BillCode", ((JSONObject) (arrays.get(i))).getString("vbillcode"));
            lstBodyTask.add(map);
        }
    }

    //确认存货流水号是否和上游单据一致
    private void ConformGetSERINO(@NonNull String cgeneralhidA, @NonNull String cgeneralhidB) throws JSONException, ParseException, IOException {
        if (cgeneralhidA.equals("") && cgeneralhidB.equals("")) {
            Toast.makeText(this, R.string.MeiYouZhaoDaoCanZhao, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }


        //获得当前单据的流水号
        JSONObject SERINOListA = null;
        JSONObject SERINOListB = null;
        JSONObject para = new JSONObject();


        para.put("FunctionName", "GetSERINO");

        para.put("INVCODE", "");
        para.put("CLOT", "");
        para.put("TableName", "SERINO");

        if (!cgeneralhidA.equals("")) {
            para.put("CBUSNO", cgeneralhidA);
            SERINOListA = Common.
                    DoHttpQuery(para, "CommonQuery", "A");


            if (SERINOListA == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
        }

        if (!cgeneralhidB.equals("")) {
            para.put("CBUSNO", cgeneralhidB);
            SERINOListB = Common.
                    DoHttpQuery(para, "CommonQuery", "B");


            if (SERINOListB == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
        }

        jsarraySERINO = new JSONArray();
        if (!cgeneralhidA.equals("") && !cgeneralhidB.equals("")) {
            if (!SERINOListA.getBoolean("Status") && !SERINOListB.getBoolean("Status")) {
                //整单 无流水号记录
                jsarraySERINO = null;
                return;
            }
        } else {
            if (!cgeneralhidA.equals("") && cgeneralhidB.equals("")) {
                if (!SERINOListA.getBoolean("Status")) {
                    //整单 无流水号记录
                    jsarraySERINO = null;
                    return;
                }
            }
            if (cgeneralhidA.equals("") && !cgeneralhidB.equals("")) {
                if (!SERINOListB.getBoolean("Status")) {
                    //整单 无流水号记录
                    jsarraySERINO = null;
                    return;
                }
            }
        }


        if (!cgeneralhidA.equals("")) {
            if (SERINOListA.getBoolean("Status")) {
                JSONArray jsarraySerNoItem = SERINOListA.getJSONArray("SERINO");
                for (int i = 0; i < jsarraySerNoItem.length(); i++) {
                    JSONObject SerNoItem = (JSONObject) jsarraySerNoItem.get(i);
                    SerNoItem.put("accid", "A");
                    jsarraySERINO.put(SerNoItem);
                }

            }
        }

        if (!cgeneralhidB.equals("")) {
            if (SERINOListB.getBoolean("Status")) {
                JSONArray jsarraySerNoItem = SERINOListB.getJSONArray("SERINO");
                for (int i = 0; i < jsarraySerNoItem.length(); i++) {
                    JSONObject SerNoItem = (JSONObject) jsarraySerNoItem.get(i);
                    SerNoItem.put("accid", "B");
                    jsarraySERINO.put(SerNoItem);
                }

            }
        }

        return;

        //jsarraySERINO = SERINOListA.getJSONArray("SERINO");

//			for(int i = 0;i<jsarray.length();i++)
//			{
//				String CBUSNO = ((JSONObject)jsarray.getJSONObject(i)).getString("cbusno").toString();
//				String INVCODE = ((JSONObject)jsarray.getJSONObject(i)).getString("invcode").toString();
//				String CLOT = ((JSONObject)jsarray.getJSONObject(i)).getString("clot").toString();
//				String SERINO = ((JSONObject)jsarray.getJSONObject(i)).getString("serino").toString();
//			}

    }

    //确认存货在上游单据内有
    private boolean ConformDetail(String barcode, @NonNull SplitBarcode bar) throws JSONException, ParseException, IOException {
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

            //TaskCount = TaskCount + Integer.valueOf(((JSONObject)(JsonArrays.get(i))).getString("nnum").toString());

            String TaskBatch = ((JSONObject) (jsarray.get(i))).getString("vbatchcode");
            if (TaskBatch.equals(bar.cBatch)) {
                //确认了存货

                if (jsarray.getJSONObject(i).getString("invcode").equals(bar.cInvCode)
                        && jsarray.getJSONObject(i).getString("accid").equals(bar.AccID)) {
                    String nnum = ((JSONObject) (jsarray.get(i))).getString("noutnum");
                    String ntranoutnum = ((JSONObject) (jsarray.get(i))).getString("ntranoutnum");

                    String snnum = "0";
                    if (!ntranoutnum.equals("null")) {
                        snnum = (ntranoutnum.replaceAll("\\.0", ""));
                    }
                    int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);
                    String Tasknnum = shouldinnum + "";


                    invFlg = "ok";


                    //String Taskbatch = ((JSONObject)(jsarray.get(i))).getString("noutnum");
                    if (!Tasknnum.equals("0")) {
                        OkFkg = "ok";

                        Free1 = jsarray.getJSONObject(i).getString("vfree1");
//
//  		  				return true;
                    }
                }
            }
        }

        if (OkFkg.equals("ok")) {
            currentObj = new Inventory(bar.cInvCode, PKcorpTo, bar.AccID);
            if (currentObj.getErrMsg() != null && !currentObj.getErrMsg().equals(""))

            {
                Toast.makeText(this, currentObj.getErrMsg(),
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return false;
            }
            currentObj.SetSerino(bar.cSerino);
            currentObj.SetBatch(bar.cBatch);
            currentObj.SetcurrentID(bar.currentBox);
            currentObj.SettotalID(bar.TotalBox);
            currentObj.SetAccID(bar.AccID);
            if (bar.AccID.equals("A")) {
                currentObj.SetvFree1(Free1);
            } else {
                currentObj.SetvFree1("");
            }

            return true;
        } else {
            //String invFlg = "ng";
            //invFlg = "ok";
            if (invFlg.equals("ok")) {
                //该任务已经全部扫描完成
                Toast.makeText(this, R.string.ChaoChuShangYouDanJuRenWuShuLiang,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return false;
            }
        }

        Toast.makeText(this, R.string.CunHuoZaiShangYouDanJuRenWuZhongBuCunZai,
                Toast.LENGTH_LONG).show();
        // ADD CAIXY TEST START
        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // ADD CAIXY TEST END
        return false;
    }

    //扫描二维码解析功能函数
    private void ScanBarcode(@NonNull String barcode) throws JSONException, ParseException, IOException {
        if (barcode.equals("")) {
            Toast.makeText(this, R.string.QingSaoMiaoTiaoMa, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            txtTIScanBarcode.requestFocus();
            return;
        }
        txtTIScanBarcode.setText("");
        txtTIScanBarcode.requestFocus();
        //IniScan();
        //条码分析

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        bar = new SplitBarcode(barcode);
        if (bar.creatorOk == false) {
            Toast.makeText(this, R.string.SaoMiaoDeBuShiZhengQueHuoPinTiaoMa, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            txtTIScanBarcode.setText("");
            txtTIScanBarcode.requestFocus();
            return;
        }
        //
        //判断是否已经有AccID,如果有但是和扫描出来的AccID不一样,提示错误.
//  		if(tmpAccID!=null && !tmpAccID.equals(""))
//  		{
//  			if(!tmpAccID.equals(bar.AccID))
//  			{
//
//  				txtTIScanBarcode.setText("");
//  				txtTIScanBarcode.requestFocus();
//  				Toast.makeText(this, "扫描的条码不属于该任务帐套,该货品不能够扫入", Toast.LENGTH_LONG).show();
//				// ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				// ADD CAIXY TEST END
//  				return;
//  			}
//  		}
        //

        String FinishBarCode = bar.FinishBarCode;

        if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
            for (int si = 0; si < ScanedBarcode.size(); si++) {
                String BarCode = ScanedBarcode.get(si).toString();

                if (BarCode.equals(FinishBarCode)) {
                    txtTIScanBarcode.setText("");
                    txtTIScanBarcode.requestFocus();
                    Toast.makeText(this, R.string.GaiTiaoMaYiJingBeiSaoMiaoGuoLe, Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }
            }
        }

        if (!ConformDetail(barcode, bar)) {
            txtTIScanBarcode.setText("");
            txtTIScanBarcode.requestFocus();
            return;
        }

        //ScanedBarcode.add(FinishBarCode);
        //add caixy e

        if (OkFkg.equals("ng")) {
            //该任务已经全部扫描完毕
            Toast.makeText(this, R.string.ChaoChuShangYouDanJuRenWuShuLiang,
                    Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            txtTIScanBarcode.setText("");
            txtTIScanBarcode.requestFocus();
            return;
        }


        if (tmpWHStatus.equals("Y") && tmpBillStatus.equals("N")) {
            if (!FindInvnBinStockInfo()) {
                txtTIScanBarcode.setText("");
                txtTIScanBarcode.requestFocus();
                return;
            }
        } else if (tmpWHStatus.equals("N") && tmpBillStatus.equals("N")) {
            if (!FindInvnNoBinStockInfo()) {
                txtTIScanBarcode.setText("");
                txtTIScanBarcode.requestFocus();
                return;
            }
        }


        //SERINOFlg = "0";
        //SERINOOK = "0";
        //JSONObject jsonCheckGetBillCode = CheckGetBillCodeBySerNo(bar);

        String ScanType = "";
        ScanInvOK = "0";
        JSONObject jsonCheckGetBillCode = null;
        if (jsarraySERINO == null || jsarraySERINO.length() < 1) {
            ScanType = "1";
            jsonCheckGetBillCode = CheckGetBillCode(bar, "0");
        } else {
            ScanType = "2";
            jsonCheckGetBillCode = CheckGetBillCode(bar, "1");
            if (jsonCheckGetBillCode == null || jsonCheckGetBillCode.length() < 1) {
                ScanType = "3";
                jsonCheckGetBillCode = CheckGetBillCode(bar, "2");
            }
        }

        if (jsonCheckGetBillCode == null || jsonCheckGetBillCode.length() < 1) {
            txtTIScanBarcode.setText("");
            txtTIScanBarcode.requestFocus();
            if (ScanInvOK.equals("1")) {

                if (ScanType.equals("3")) {
                    //流水号是否和上游单据一致
                    Toast.makeText(this, R.string.CunHuoDeLiuShuiHaoYuShangYouBuYiZhi, Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }
                //存货在上游单据任务中已经扫描完毕,但是还有未扫完的分包
                Toast.makeText(this, R.string.ChaoChuShangYouDanJuRenWuShuLiang, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            Toast.makeText(this, R.string.GaiTiaoMaBuFuHeRenWuXiangMu, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

        if (!CheckHasScaned(jsonCheckGetBillCode, bar)) {
            txtTIScanBarcode.setText("");
            txtTIScanBarcode.requestFocus();
            Toast.makeText(this, R.string.GaiTiaoMaYiJingBeiSaoMiaoGuoLe, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        //GetRemovedTaskList(bar);
        //ADD BY WUQIONG START
        //String CheckGethid = ((JSONObject)jsonCheckGetBillCode).getString("cgeneralhid");

        GetModTaskList(bar, jsonCheckGetBillCode);
        //ADD BY WUQIONG END
        ReSetTaskListData();
        ScanedBarcode.add(FinishBarCode);
        MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
        SaveScanedBody();

    }

    //ADD BY WUQIONG START
    //完成扫描后修改任务里的项目
    private void GetModTaskList(@NonNull SplitBarcode bar, @NonNull JSONObject jsonCheckGetBillCode) throws JSONException {
        String lsBarInvCode = bar.cInvCode;
        String lsBarBacth = bar.cBatch;
        String lsBillCode = "";
        String lsSerino = bar.cSerino;

        JSONArray JsonArrays = (JSONArray) jsonBodyTask.get("dbBody");
        //jsonArrRemove = new ArrayList();

        String Taskhid = jsonCheckGetBillCode.getString("cgeneralhid");
        String Taskbid = jsonCheckGetBillCode.getString("cgeneralbid");

        for (int i = 0; i < JsonArrays.length(); i++) {
            String lsJsonInvCode = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
            String lsaccid = ((JSONObject) (JsonArrays.get(i))).getString("accid");
            String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(i))).getString("vbatchcode");
            //add caixy 解决扫描任务匹配不正确问题
            //Double ldJsonInvQty = ((JSONObject)(JsonArrays.get(i))).getDouble("noutnum");
            String nnum = ((JSONObject) (JsonArrays.get(i))).getString("noutnum");
            String ntranoutnum = ((JSONObject) (JsonArrays.get(i))).getString("ntranoutnum");
            String snnum = "0";

            //String cgeneralhid = ((JSONObject)(JsonArrays.get(i))).getString("cgeneralhid");

            if (!ntranoutnum.equals("null")) {
                snnum = (ntranoutnum.replaceAll("\\.0", ""));
            }

            int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

            String Tasknnum = shouldinnum + "";


            //add caixy 解决扫描任务匹配不正确问题
            if (lsJsonInvBatch == null || lsJsonInvBatch.equals("") || lsJsonInvBatch.equals("null")) {
                lsJsonInvBatch = "批次未指定";
            }
            if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0") && lsaccid.equals(bar.AccID))//caixy
            {
                if (lsBarBacth.equals(lsJsonInvBatch)) {
                    if (((JSONObject) (JsonArrays.get(i))).getString("cgeneralbid").equals(Taskbid) && ((JSONObject) (JsonArrays.get(i))).getString("cgeneralhid").equals(Taskhid)) {
                        GetModTaskQty(Double.valueOf(Tasknnum),
                                ((JSONObject) (JsonArrays.get(i))).getString("cgeneralbid"), ((JSONObject) (JsonArrays.get(i))).getString("cgeneralhid"), i, lsSerino);
                        return;
                    }


                }
            }
        }

        for (int j = 0; j < JsonArrays.length(); j++) {
            String lsJsonInvCode = ((JSONObject) (JsonArrays.get(j))).getString("invcode");
            String lsaccid = ((JSONObject) (JsonArrays.get(j))).getString("accid");
            String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(j))).getString("vbatchcode");

            String nnum = ((JSONObject) (JsonArrays.get(j))).getString("noutnum");
            String ntranoutnum = ((JSONObject) (JsonArrays.get(j))).getString("ntranoutnum");
            String snnum = "0";
            //String cgeneralhid = ((JSONObject)(JsonArrays.get(j))).getString("cgeneralhid");
            if (!ntranoutnum.equals("null")) {
                snnum = (ntranoutnum.replaceAll("\\.0", ""));
            }

            int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

            String Tasknnum = shouldinnum + "";


            if (lsJsonInvBatch == null || lsJsonInvBatch.equals("") || lsJsonInvBatch.equals("null")) {
                lsJsonInvBatch = "批次未指定";
                if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0") && lsaccid.equals(bar.AccID)) {

                    if (((JSONObject) (JsonArrays.get(j))).getString("cgeneralbid").equals(Taskbid) && ((JSONObject) (JsonArrays.get(j))).getString("cgeneralhid").equals(Taskhid)) {
                        GetModTaskQty(Double.valueOf(Tasknnum),
                                ((JSONObject) (JsonArrays.get(j))).getString("cgeneralbid"), ((JSONObject) (JsonArrays.get(j))).getString("cgeneralhid"), j, lsSerino);
                        return;
                    }

                }
            }
        }

    }

    //ADD BY WUQIONG END


    private boolean FindInvnBinStockInfo() throws JSONException, ParseException, IOException {
        if (ReScanBody.equals("0")) {
            return true;
        }
        JSONObject para = new JSONObject();
        para.put("FunctionName", "GetBinStockByID");
        para.put("InvID", this.currentObj.Invmandoc());

        //para.put("BinID", this.tmpposID);
        if (bar.AccID.equals("A")) {
            para.put("BinID", this.tmpposIDA);
        }
        if (bar.AccID.equals("B")) {
            para.put("BinID", this.tmpposIDB);
        }

        para.put("LotB", this.currentObj.GetBatch());
        para.put("TableName", "Stock");
        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }
        JSONObject StockInfo = Common.DoHttpQuery(para, "CommonQuery", this.bar.AccID);

        if (StockInfo == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!StockInfo.has("Status")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (StockInfo.getBoolean("Status")) {
            JSONArray val = StockInfo.getJSONArray("Stock");
            if (val.length() < 1) {
                Toast.makeText(this, R.string.HuoQuHuoWeiKuCunShuJu, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }

            double stockCount = 0.0;

            for (int iv = 0; iv < val.length(); iv++) {
                JSONObject temp = val.getJSONObject(iv);
                if (Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0) {
                    stockCount = stockCount + Double.valueOf(temp.getString("nnum")).doubleValue();
                }
            }

            double sancount = getScanCount(currentObj.Invmandoc(), currentObj.GetBatch());
            sancount += 1.0;
            if (sancount > stockCount) {
                Toast.makeText(this, R.string.GaiHuoWeiZaiDeFaChuKuCunYiJingBuZule, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }

        } else {
            Toast.makeText(this, R.string.GaiHuoPinZaiGaiCangHuZhongMeiYouKuCunXinXi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        return true;
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
            if (temp.get("cinvbasid").equals(currentObj.Invbasdoc()) && temp.get("csourcebillhid").equals(sBillHID) && temp.get("csourcebillbid").equals(sBillBID)) {

                //mod caixy s 解决扫描数量错误
                //if(temp.get("csourcebillhid").equals(sBillID))
                String str = temp.get("BarCode").toString();

                String[] val = str.split("\\|");
                String SaveSerino = (val[3]);//
                String Serino = currentObj.GetSerino().toString();

                if (SaveSerino.equals(Serino))
                //mod caixy e 解决扫描数量错误
                {
                    inQty += Double.valueOf(temp.get("spacenum").toString());
                    ScanedQty = Integer.valueOf(temp.get("spacenum").toString());//add caixy e 解决扫描数量错误
                }
            }

            if (inQty.toString().equals(Qty.toString())) {
                iModTaskIndex = String.valueOf(iIndex);
                JSONArray JsonTaskArrays = (JSONArray) jsonBodyTask.get("dbBody");
                JsonModTaskItem = (JSONObject) JsonTaskArrays.get(iIndex);
                String lsKey = JsonModTaskItem.getString("cgeneralbid") +
                        JsonModTaskItem.getString("invcode") +
                        //JsonRemoveTaskItem.getString("vbatch") +
                        JsonModTaskItem.getString("crowno") + lsSerino;
                JsonModTaskData.put(lsKey, JsonModTaskItem);

            } else if (inQty != 0.0) {

                iModTaskIndex = String.valueOf(iIndex);

                JSONArray JsonTaskArrays = (JSONArray) jsonBodyTask.get("dbBody");

//
                JsonModTaskItem = new JSONObject();
                //已修改
                JsonModTaskItem.put("vfree1", ((JSONObject) JsonTaskArrays.get(iIndex)).get("vfree1").toString());
                JsonModTaskItem.put("invname", ((JSONObject) JsonTaskArrays.get(iIndex)).get("invname").toString());
                JsonModTaskItem.put("invcode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("invcode").toString());
                JsonModTaskItem.put("accid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("accid").toString());

                JsonModTaskItem.put("crowno", ((JSONObject) JsonTaskArrays.get(iIndex)).get("crowno").toString());
                JsonModTaskItem.put("cfirstbillhid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("cfirstbillhid").toString());
                JsonModTaskItem.put("cgeneralhid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("cgeneralhid").toString());
                JsonModTaskItem.put("cfirstbillbid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("cfirstbillbid").toString());
                JsonModTaskItem.put("cgeneralbid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("cgeneralbid").toString());
                JsonModTaskItem.put("cfirsttype", ((JSONObject) JsonTaskArrays.get(iIndex)).get("cfirsttype").toString());
                JsonModTaskItem.put("cbodybilltypecode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("cbodybilltypecode").toString());
                JsonModTaskItem.put("cquoteunitid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("cquoteunitid").toString());
                JsonModTaskItem.put("nquoteunitrate", ((JSONObject) JsonTaskArrays.get(iIndex)).get("nquoteunitrate").toString());
                JsonModTaskItem.put("nquoteunitnum", ((JSONObject) JsonTaskArrays.get(iIndex)).get("nquoteunitnum").toString());
                JsonModTaskItem.put("nshouldoutnum", ((JSONObject) JsonTaskArrays.get(iIndex)).get("nshouldoutnum").toString());
                JsonModTaskItem.put("creceiveareaid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("creceiveareaid").toString());
                JsonModTaskItem.put("vbillcode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("vbillcode").toString());
                JsonModTaskItem.put("cinvbasid", ((JSONObject) JsonTaskArrays.get(iIndex)).get("cinvbasid").toString());
                JsonModTaskItem.put("vbatchcode", ((JSONObject) JsonTaskArrays.get(iIndex)).get("vbatchcode").toString());
                JsonModTaskItem.put("vuserdef1", ((JSONObject) JsonTaskArrays.get(iIndex)).get("vuserdef1").toString());
                JsonModTaskItem.put("vuserdef20", ((JSONObject) JsonTaskArrays.get(iIndex)).get("vuserdef20").toString());
                JsonModTaskItem.put("noutnum", ((JSONObject) JsonTaskArrays.get(iIndex)).get("noutnum").toString());
                JsonModTaskItem.put("ntranoutnum", ((JSONObject) JsonTaskArrays.get(iIndex)).get("ntranoutnum").toString());


                JsonModTaskItem.put("noutnum", inQty);//修改数量问题


                String lsKey = JsonModTaskItem.getString("cgeneralbid") +
                        JsonModTaskItem.getString("invcode") +
                        //JsonRemoveTaskItem.getString("vbatch") +
                        JsonModTaskItem.getString("crowno") + lsSerino;
                JsonModTaskData.put(lsKey, JsonModTaskItem);
            }

        }

    }
    //ADD BY WUQIONG END

    private void ReSetTaskListData() throws JSONException {
        JSONArray JsonArrays = (JSONArray) jsonBodyTask.get("dbBody");
        JSONArray JsonArrNew = new JSONArray();
        JSONArray JsonArrMod = new JSONArray();
        //jsonArrRemove = new ArrayList();

        for (int i = 0; i < JsonArrays.length(); i++) {
            //DEL BY WUQIONG S
//    			if(!iRemoveTaskIndex.equals(""))
//    			{
//      				if(i!=Integer.parseInt(iRemoveTaskIndex))
//      				{
//      					JsonArrNew.put((JSONObject)JsonArrays.get(i));
//       				}
//    			}
            //DEL BY WUQIONG E

            //ADD BY WUQIONG START
            if (!iModTaskIndex.equals("")) {
                if (i != Integer.parseInt(iModTaskIndex)) {
                    JsonArrNew.put(JsonArrays.get(i));
                } else {

                    JSONObject jObj = new JSONObject();
                    jObj.put("vfree1", ((JSONObject) JsonArrays.get(i)).get("vfree1").toString());
                    jObj.put("invname", ((JSONObject) JsonArrays.get(i)).get("invname").toString());
                    jObj.put("invcode", ((JSONObject) JsonArrays.get(i)).get("invcode").toString());
                    jObj.put("accid", ((JSONObject) JsonArrays.get(i)).get("accid").toString());


                    jObj.put("crowno", ((JSONObject) JsonArrays.get(i)).get("crowno").toString());
                    jObj.put("cfirstbillhid", ((JSONObject) JsonArrays.get(i)).get("cfirstbillhid").toString());
                    jObj.put("cgeneralhid", ((JSONObject) JsonArrays.get(i)).get("cgeneralhid").toString());
                    jObj.put("cfirstbillbid", ((JSONObject) JsonArrays.get(i)).get("cfirstbillbid").toString());
                    jObj.put("cgeneralbid", ((JSONObject) JsonArrays.get(i)).get("cgeneralbid").toString());
                    jObj.put("cfirsttype", ((JSONObject) JsonArrays.get(i)).get("cfirsttype").toString());
                    jObj.put("cbodybilltypecode", ((JSONObject) JsonArrays.get(i)).get("cbodybilltypecode").toString());
                    jObj.put("cquoteunitid", ((JSONObject) JsonArrays.get(i)).get("cquoteunitid").toString());
                    jObj.put("nquoteunitrate", ((JSONObject) JsonArrays.get(i)).get("nquoteunitrate").toString());
                    jObj.put("nquoteunitnum", ((JSONObject) JsonArrays.get(i)).get("nquoteunitnum").toString());
                    jObj.put("nshouldoutnum", ((JSONObject) JsonArrays.get(i)).get("nshouldoutnum").toString());
                    jObj.put("creceiveareaid", ((JSONObject) JsonArrays.get(i)).get("creceiveareaid").toString());
                    jObj.put("vbillcode", ((JSONObject) JsonArrays.get(i)).get("vbillcode").toString());
                    jObj.put("cinvbasid", ((JSONObject) JsonArrays.get(i)).get("cinvbasid").toString());
                    jObj.put("vbatchcode", ((JSONObject) JsonArrays.get(i)).get("vbatchcode").toString());
                    jObj.put("vuserdef1", ((JSONObject) JsonArrays.get(i)).get("vuserdef1").toString());
                    jObj.put("vuserdef20", ((JSONObject) JsonArrays.get(i)).get("vuserdef20").toString());
                    jObj.put("noutnum", ((JSONObject) JsonArrays.get(i)).get("noutnum").toString());
                    jObj.put("ntranoutnum", ((JSONObject) JsonArrays.get(i)).get("ntranoutnum").toString());


                    String noutnum = (((JSONObject) JsonArrays.get(i)).get("noutnum").toString()); //减已扫描件数
                    //Double ldJsonInvQty = ((JSONObject)JsonArrays.get(i)).get("nnum");

                    int innum = Integer.valueOf(noutnum) - ScanedQty;

                    String inewnnum = innum + "";

                    jObj.put("noutnum", inewnnum.toString());


                    JsonArrNew.put(jObj);


                }
            }
            //ADD BY WUQIONG END
        }
        //MOD BY WUQIONG S
        if (!iModTaskIndex.equals("")) {
            jsonBodyTask = new JSONObject();
            jsonBodyTask.put("Status", true);
            jsonBodyTask.put("dbBody", JsonArrNew);
//    	  		if(!iRemoveTaskIndex.equals(""))
//    	  		{
//    	  			jonsBody.put("RemoveTaskData", JsonRemoveTaskData);
//    	  		}
//    	  		if(!iModTaskIndex.equals(""))
//    	  		{
//    	  			jsonBodyTask.put("ModTaskData", JsonModTaskData);
//    	  		}

            getTaskListData(jsonBodyTask);
        }
        //MOD BY WUQIONG E

    }

//  	private void ReSetTaskListData() throws JSONException
//  	{
//  		JSONArray JsonArrays=(JSONArray)jsonBodyTask.getJSONArray("dbBody");
//  		JSONArray JsonArrNew = new JSONArray();
//  		//jsonArrRemove = new ArrayList();
//
//  		for(int i=0;i<JsonArrays.length();i++)
//  		{
//  		//DEL BY WUQIONG S
////			if(!iRemoveTaskIndex.equals(""))
////			{
////  				if(i!=Integer.parseInt(iRemoveTaskIndex))
////  				{
////  					JsonArrNew.put((JSONObject)JsonArrays.get(i));
////   				}
////			}
//  		//DEL BY WUQIONG E
//
//			//ADD BY WUQIONG START
//			if (!iModTaskIndex.equals(""))
//			{
//  				if(i!=Integer.parseInt(iModTaskIndex))
//  				{
//  					JsonArrNew.put((JSONObject)JsonArrays.get(i));
//  				}
//  				else
//  				{
//					JSONObject jObj = new JSONObject();
//					//已经修改
//
//
//					jObj.put("vfree1",((JSONObject)JsonArrays.get(i)).get("vfree1").toString());
//					jObj.put("invname",((JSONObject)JsonArrays.get(i)).get("invname").toString());
//					jObj.put("invcode",((JSONObject)JsonArrays.get(i)).get("invcode").toString());
//
//
//					jObj.put("crowno",((JSONObject)JsonArrays.get(i)).get("crowno").toString());
//					jObj.put("cfirstbillhid",((JSONObject)JsonArrays.get(i)).get("cfirstbillhid").toString());
//					jObj.put("cgeneralhid",((JSONObject)JsonArrays.get(i)).get("cgeneralhid").toString());
//					jObj.put("cfirstbillbid",((JSONObject)JsonArrays.get(i)).get("cfirstbillbid").toString());
//					jObj.put("cgeneralbid",((JSONObject)JsonArrays.get(i)).get("cgeneralbid").toString());
//					jObj.put("cfirsttype",((JSONObject)JsonArrays.get(i)).get("cfirsttype").toString());
//					jObj.put("cbodybilltypecode",((JSONObject)JsonArrays.get(i)).get("cbodybilltypecode").toString());
//					jObj.put("cquoteunitid",((JSONObject)JsonArrays.get(i)).get("cquoteunitid").toString());
//					jObj.put("nquoteunitrate",((JSONObject)JsonArrays.get(i)).get("nquoteunitrate").toString());
//					jObj.put("nquoteunitnum",((JSONObject)JsonArrays.get(i)).get("nquoteunitnum").toString());
//					jObj.put("nshouldoutnum",((JSONObject)JsonArrays.get(i)).get("nshouldoutnum").toString());
//					jObj.put("creceiveareaid",((JSONObject)JsonArrays.get(i)).get("creceiveareaid").toString());
//					jObj.put("vbillcode",((JSONObject)JsonArrays.get(i)).get("vbillcode").toString());
//					jObj.put("cinvbasid",((JSONObject)JsonArrays.get(i)).get("cinvbasid").toString());
//					jObj.put("vbatchcode",((JSONObject)JsonArrays.get(i)).get("vbatchcode").toString());
//					jObj.put("vuserdef1",((JSONObject)JsonArrays.get(i)).get("vuserdef1").toString());
//					jObj.put("vuserdef20",((JSONObject)JsonArrays.get(i)).get("vuserdef20").toString());
//					jObj.put("noutnum",((JSONObject)JsonArrays.get(i)).get("noutnum").toString());
//					jObj.put("ntranoutnum",((JSONObject)JsonArrays.get(i)).get("ntranoutnum").toString());
//
//
//
//
//
//					String noutnum = (((JSONObject)JsonArrays.get(i)).get("noutnum").toString()); //减已扫描件数
//										//Double ldJsonInvQty = ((JSONObject)JsonArrays.get(i)).get("nnum");
//
//					int innum = Integer.valueOf(noutnum)-ScanedQty;
//
//					String inewnnum = innum+"" ;
//
//					jObj.put("noutnum",inewnnum.toString());
//
////
//					JsonArrNew.put(jObj);
//   				}
//			}
//			//ADD BY WUQIONG END
//  		}
//  		//MOD BY WUQIONG S
//  		if(!iModTaskIndex.equals(""))
//  		{
//  			jsonBodyTask = new JSONObject();
//  			jsonBodyTask.put("Status", true);
//  			jsonBodyTask.put("dbBody", JsonArrNew);
////	  		if(!iRemoveTaskIndex.equals(""))
////	  		{
////	  			jonsBody.put("RemoveTaskData", JsonRemoveTaskData);
////	  		}
//	  		if(!iModTaskIndex.equals(""))
//	  		{
//	  			jsonBodyTask.put("ModTaskData", JsonModTaskData);
//	  		}
//
//	  		getTaskListData(jsonBodyTask);
//  		}
//  		//MOD BY WUQIONG E
//  	}

//  	//完成扫描后删除任务里的项目
//  	private void GetRemovedTaskList(SplitBarcode bar) throws JSONException
//  	{
//  		String lsBarInvCode = bar.cInvCode;
//  		String lsBarBacth = bar.cBatch;
//  		String lsBillCode = "";
//
//  		JSONArray JsonArrays=(JSONArray)jsonBodyTask.getJSONArray("dbBody");
//  		//jsonArrRemove = new ArrayList();
//
//		for(int i = 0;i<JsonArrays.length();i++)
//		{
//			String lsJsonInvCode = ((JSONObject)(JsonArrays.get(i))).getString("invcode");
//			String lsJsonInvBatch = ((JSONObject)(JsonArrays.get(i))).getString("vbillcode");
//			Double ldJsonInvQty = ((JSONObject)(JsonArrays.get(i))).getDouble("noutnum");
//			if(lsJsonInvBatch==null||lsJsonInvBatch.equals("")||lsJsonInvBatch.equals("null"))
//			{
//				lsJsonInvBatch="批次未指定";
//			}
//			if(lsBarInvCode.equals(lsJsonInvCode))
//			{
//				if(lsBarBacth.equals(lsJsonInvBatch))
//				{
//					GetRemovedTaskQty(ldJsonInvQty,
//							((JSONObject)(JsonArrays.get(i))).getString("cgeneralhid"),i);
//
//				}
//			}
//		}
//
//		for(int j = 0;j<JsonArrays.length();j++)
//		{
//			String lsJsonInvCode = ((JSONObject)(JsonArrays.get(j))).getString("invcode");
//			String lsJsonInvBatch = ((JSONObject)(JsonArrays.get(j))).getString("vbillcode");
//			Double ldJsonInvQty = ((JSONObject)(JsonArrays.get(j))).getDouble("noutnum");
//			if(lsJsonInvBatch==null||lsJsonInvBatch.equals("")||lsJsonInvBatch.equals("null"))
//			{
//				lsJsonInvBatch="批次未指定";
//			}
//			if(lsBarInvCode.equals(lsJsonInvCode))
//			{
//				GetRemovedTaskQty(ldJsonInvQty,
//						((JSONObject)(JsonArrays.get(j))).getString("cgeneralhid"),j);
//			}
//		}
//
//  	}

    //private String iRemoveTaskIndex = "";

//  	private void GetRemovedTaskQty(Double Qty, String sBillID, int iIndex) throws JSONException
//  	{
//  		//iRemoveTaskIndex="";
//  		if(lstSaveBody==null || lstSaveBody.size() < 1)
//  		{
//  			return;
//  		}
//
//  		for(int i = 0;i<lstSaveBody.size();i++)
//  		{
//  			Double inQty = 0.0;
//  			Map<String,Object> temp = (Map<String,Object>) lstSaveBody.get(i);
//  			if(temp.get("cinvbasid").equals(currentObj.Invbasdoc()))
//  			{
//  				if(temp.get("csourcebillhid").equals(sBillID))
//  					inQty += Double.valueOf(temp.get("spacenum").toString());
//  			}
//
//  			if(inQty.toString().equals(Qty.toString()))
//  			{
//  				iRemoveTaskIndex = String.valueOf(iIndex);
//
//  				JSONArray JsonTaskArrays=(JSONArray)jsonBodyTask.getJSONArray("dbBody");
//  				JSONObject JsonRemoveTaskItem = (JSONObject)JsonTaskArrays.get(iIndex);
//  				String lsKey = JsonRemoveTaskItem.getString("cgeneralhid") +
//  						JsonRemoveTaskItem.getString("invcode") +
//						JsonRemoveTaskItem.getString("crowno");
//  				JsonRemoveTaskData.put(lsKey, JsonRemoveTaskItem);
//  			}
////  			if(inQty < Qty)
////  			{
////  				JSONArray JsonTaskArrays=(JSONArray)jsonBodyTask.getJSONArray("dbBody");
////  				JSONObject JsonRemoveTaskItem = (JSONObject)JsonTaskArrays.get(iIndex);
////  			}
//
//  		}
//  	}

    //确认如果有上游单据,那么判断是否超过其数量
    private boolean ConformDetailQty(Double Qty, String sBillBID, String sBillHID) throws JSONException {
        ScanInvOK = "1";
        if (lstSaveBody == null || lstSaveBody.size() < 1) {
            return true;
        }

        for (int j = 0; j < lstSaveBody.size(); j++) {
            Map<String, Object> temp1 = lstSaveBody.get(j);
            if (temp1.get("SeriNo").equals(currentObj.GetSerino()) && temp1.get("InvCode").equals(currentObj.getInvCode())
                    && temp1.get("vbatchcode").equals(currentObj.GetBatch()) && temp1.get("spacenum").equals("0")
                    && temp1.get("csourcebillbid").equals(sBillBID)
                    && temp1.get("csourcebillhid").equals(sBillHID)) {
                return true;
            }
        }
        Double inQty = 1.0;
        for (int i = 0; i < lstSaveBody.size(); i++) {

            Map<String, Object> temp = lstSaveBody.get(i);
            if (temp.get("cinvbasid").equals(currentObj.Invbasdoc())) {
                if (temp.get("csourcebillbid").equals(sBillBID) && temp.get("csourcebillhid").equals(sBillHID) && temp.get("spacenum").equals("0"))
                    //inQty += Double.valueOf(temp.get("spacenum").toString());
                    inQty += 1;
            }
        }

        return inQty <= Qty;

    }

    //判断该扫描条码属于哪条单据行
    private JSONObject CheckGetBillCode(@NonNull SplitBarcode bar, @NonNull String SERNoFlg) throws JSONException {
        String lsBarInvCode = bar.cInvCode;
        String lsBarBacth = bar.cBatch;

        JSONArray JsonArrays = jsonBodyTask.getJSONArray("dbBody");

        for (int i = 0; i < JsonArrays.length(); i++) {
            String lsJsonInvCode = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
            String lsaccid = ((JSONObject) (JsonArrays.get(i))).getString("accid");
            String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(i))).getString("vbatchcode");
            //add caixy 解决扫描任务匹配不正确问题
            //Double ldJsonInvQty = ((JSONObject)(JsonArrays.get(i))).getDouble("noutnum");
            String nnum = ((JSONObject) (JsonArrays.get(i))).getString("noutnum");
            String ntranoutnum = ((JSONObject) (JsonArrays.get(i))).getString("ntranoutnum");
            String snnum = "0";

            if (!ntranoutnum.equals("null")) {
                snnum = (ntranoutnum.replaceAll("\\.0", ""));
            }

            int shouldinnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

            String Tasknnum = shouldinnum + "";


            //add caixy 解决扫描任务匹配不正确问题


            if (lsJsonInvBatch == null || lsJsonInvBatch.equals("") || lsJsonInvBatch.equals("null")) {
                lsJsonInvBatch = "批次未指定";
            }
            //add caixy 解决扫描任务匹配不正确问题
            //if(lsBarInvCode.equals(lsJsonInvCode)&&!ldJsonInvQty.equals(0.0))
            if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0") && lsaccid.equals(bar.AccID))
            //add caixy 解决扫描任务匹配不正确问题

            {
                if (lsBarBacth.equals(lsJsonInvBatch)) {
                    if (ConformDetailQty(Double.valueOf(Tasknnum), ((JSONObject) (JsonArrays.get(i))).getString("cgeneralbid"), ((JSONObject) (JsonArrays.get(i))).getString("cgeneralhid"))) {
                        if (SERNoFlg.equals("0")) {
                            return (JSONObject) JsonArrays.get(i);
                        } else if (SERNoFlg.equals("1")) {
                            if (ChkSerNo(bar, ((JSONObject) (JsonArrays.get(i))).getString("cgeneralhid"))) {
                                return (JSONObject) JsonArrays.get(i);
                            }
                        } else if (SERNoFlg.equals("2")) {
                            if (!ChkSerNo(null, ((JSONObject) (JsonArrays.get(i))).getString("cgeneralhid"))) {
                                return (JSONObject) JsonArrays.get(i);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    private Boolean ChkSerNo(@Nullable SplitBarcode bar, String BillHID) throws JSONException {
        if (bar == null) {
            for (int i = 0; i < jsarraySERINO.length(); i++) {
                String CBUSNO = jsarraySERINO.getJSONObject(i).getString("cbusno").toString();

                if (CBUSNO.equals(BillHID)) {
                    return true;
                }
            }
        } else {
            String lsBarInvCode = bar.cInvCode;
            String lsBarBacth = bar.cBatch;
            String lsBarSerNo = bar.cSerino;

            //jsarraySERINO = SERINOList.getJSONArray("SERINO");

            for (int i = 0; i < jsarraySERINO.length(); i++) {
                String CBUSNO = jsarraySERINO.getJSONObject(i).getString("cbusno").toString();
                String INVCODE = jsarraySERINO.getJSONObject(i).getString("invcode").toString();
                String CLOT = jsarraySERINO.getJSONObject(i).getString("clot").toString();
                String SERINO = jsarraySERINO.getJSONObject(i).getString("serino").toString();
                String accid = jsarraySERINO.getJSONObject(i).getString("accid").toString();

                if (CBUSNO.equals(BillHID) && INVCODE.equals(lsBarInvCode) && CLOT.equals(lsBarBacth) && SERINO.equals(lsBarSerNo) && accid.equals(bar.AccID)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void GetSerNoInfo() throws JSONException {

        JSONArray JsonArraysAll = jsonBodyTask.getJSONArray("dbBody");

        JSONArray JsonArraysAA = new JSONArray();
        JSONArray JsonArraysBB = new JSONArray();


        for (int i = 0; i < JsonArraysAll.length(); i++) {
            JSONObject SelectedItem = (JSONObject) JsonArraysAll.get(i);
            String sAccID = SelectedItem.getString("accid").toString();
            if (sAccID.equals("A")) {
                JsonArraysAA.put(SelectedItem);
            }
            if (sAccID.equals("B")) {
                JsonArraysBB.put(SelectedItem);
            }

        }


        String BillHIDA = "";
        String BillHIDB = "";
        if (JsonArraysAA.length() > 0) {
            for (int i = 0; i < JsonArraysAA.length(); i++) {
                if (JsonArraysAA.length() == 1) {
                    BillHIDA = ((JSONObject) (JsonArraysAA.get(i))).getString("cgeneralhid");
                } else {
                    if (i == JsonArraysAA.length() - 1) {
                        BillHIDA = BillHIDA + ((JSONObject) (JsonArraysAA.get(i))).getString("cgeneralhid");
                    } else {
                        BillHIDA = BillHIDA + ((JSONObject) (JsonArraysAA.get(i))).getString("cgeneralhid") + "','";
                    }
                }

            }
        }

        if (JsonArraysBB.length() > 0) {
            for (int i = 0; i < JsonArraysBB.length(); i++) {
                if (JsonArraysBB.length() == 1) {
                    BillHIDB = ((JSONObject) (JsonArraysBB.get(i))).getString("cgeneralhid");
                } else {
                    if (i == JsonArraysBB.length() - 1) {
                        BillHIDB = BillHIDB + ((JSONObject) (JsonArraysBB.get(i))).getString("cgeneralhid");
                    } else {
                        BillHIDB = BillHIDB + ((JSONObject) (JsonArraysBB.get(i))).getString("cgeneralhid") + "','";
                    }
                }
            }
        }


        try {
            ConformGetSERINO(BillHIDA, BillHIDB);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 判断该条码是否已经被扫描过了
     *
     * @return 如果为true 代表没有被扫描过,如果false 代表已经被扫描过了
     * @throws JSONException
     */
    private Boolean CheckHasScaned(@NonNull JSONObject jsonCheckGetBillCode, @NonNull SplitBarcode bar) throws JSONException {

        ListAdapter ScanDetailAdapter = lvTIScanDetail.getAdapter();
        String lsKey = jsonCheckGetBillCode.getString("vbillcode")
                + bar.AccID + bar.cInvCode + bar.cBatch + bar.cSerino;
        if (ScanDetailAdapter == null || ScanDetailAdapter.getCount() < 1) {
            BindingScanDetail(jsonCheckGetBillCode, bar, "ADD", null);
            return true;
        }
        for (int i = 0; i < ScanDetailAdapter.getCount(); i++) {
            Map<String, Object> mapScanDetail =
                    (Map<String, Object>) ScanDetailAdapter.getItem(i);
            if (mapScanDetail.containsKey(lsKey)) {

                ArrayList<Map<String, Object>> lstCurrentDetail = (ArrayList<Map<String, Object>>) mapScanDetail.get(lsKey);
                for (int j = 0; j < lstCurrentDetail.size(); j++) {
                    if (lstCurrentDetail.get(j).get("FinishBarCode").toString().equals(bar.FinishBarCode))
                        return false;
                }
                BindingScanDetail(jsonCheckGetBillCode, bar, "MOD", mapScanDetail);
                return true;
            }
            //增加扫描数量控制  蔡晓勇 s
            else {
                if (listcount >= 500) {
                    // TODO: 2017/6/2
                    Toast.makeText(this, "超过最大扫描数量，请先将本次扫描数据保存" + "\r\n" + "（无法继续扫入新存货,未完成的分包可以继续扫描）", Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return false;
                }
            }
            //增加扫描数量控制  蔡晓勇 e
        }

        BindingScanDetail(jsonCheckGetBillCode, bar, "ADD", null);
        return true;

    }

    private void BindingScanDetail(@NonNull JSONObject jsonCheckGetBillCode, @NonNull SplitBarcode bar,
                                   @NonNull String sType, @Nullable Map<String, Object> mapGetScanedDetail) throws JSONException {
        ArrayList<Map<String, Object>> lstCurrentBox = null;
        Map<String, Object> mapCurrentBox = new HashMap<String, Object>();
        Map<String, Object> mapScanDetail = new HashMap<String, Object>();

        if (lstSaveBody == null || lstSaveBody.size() < 1)
            lstSaveBody = new ArrayList<Map<String, Object>>();

        mapCurrentBox.put("CurrentBox", bar.currentBox);
        mapCurrentBox.put("TotalBox", bar.TotalBox);
        mapCurrentBox.put("FinishBarCode", bar.FinishBarCode);
        mapCurrentBox.put("BoxNum", Integer.parseInt(bar.currentBox) + "/" + Integer.parseInt(bar.TotalBox));

        String lsKey = jsonCheckGetBillCode.getString("vbillcode")
                + bar.AccID + bar.cInvCode + bar.cBatch + bar.cSerino;
        if (sType.equals("MOD") && mapGetScanedDetail != null) {
            lstCurrentBox = (ArrayList<Map<String, Object>>) mapGetScanedDetail.get(lsKey);
            lstCurrentBox.add(mapCurrentBox);

            mapScanDetail = mapGetScanedDetail;
            mapScanDetail.remove(lsKey);
            mapScanDetail.put(lsKey, lstCurrentBox);
            mapScanDetail.remove("ScanedNum");
            mapScanDetail.put("ScanedNum", lstCurrentBox.size());
            if (Integer.parseInt(currentObj.totalID()) == lstCurrentBox.size()) {
                mapScanDetail.remove("spacenum");
                mapScanDetail.put("spacenum", "1");
                mapScanDetail.put("box", "");
            }
        } else {
            lstCurrentBox = new ArrayList<Map<String, Object>>();
            lstCurrentBox.add(mapCurrentBox);

            mapScanDetail.put(lsKey, lstCurrentBox);
            mapScanDetail.put("InvName", currentObj.getInvName());
            mapScanDetail.put("InvCode", currentObj.getInvCode());
            mapScanDetail.put("Batch", currentObj.GetBatch());
            mapScanDetail.put("AccID", bar.AccID);
            mapScanDetail.put("SeriNo", currentObj.GetSerino());
            mapScanDetail.put("BarCode", bar.CheckBarCode);
            mapScanDetail.put("TotalNum", Integer.parseInt(currentObj.totalID()));
            mapScanDetail.put("ScanedNum", lstCurrentBox.size());

            //源单单据行号
            mapScanDetail.put("csourcerowno", jsonCheckGetBillCode.getString("crowno"));
            //开始单表头
            mapScanDetail.put("cfirstbillhid", jsonCheckGetBillCode.getString("cfirstbillhid"));
            //源单表头
            mapScanDetail.put("csourcebillhid", jsonCheckGetBillCode.getString("cgeneralhid"));
            //开始单表体
            mapScanDetail.put("cfirstbillbid", jsonCheckGetBillCode.getString("cfirstbillbid"));
            //源单表体
            mapScanDetail.put("csourcebillbid", jsonCheckGetBillCode.getString("cgeneralbid"));
            //开始单据类型
            mapScanDetail.put("cfirsttypecode", jsonCheckGetBillCode.getString("cfirsttype"));
            //源单据类型
            mapScanDetail.put("csourcetypecode", jsonCheckGetBillCode.getString("cbodybilltypecode"));
            //报价计量单位ID
            mapScanDetail.put("cquoteunitid", jsonCheckGetBillCode.getString("cquoteunitid"));
            //报价计量单位换算率
            if (jsonCheckGetBillCode.getString("nquoteunitrate") == null ||
                    jsonCheckGetBillCode.getString("nquoteunitrate").equals("") ||
                    jsonCheckGetBillCode.getString("nquoteunitrate").equals("null"))
                mapScanDetail.put("nquoteunitrate", 0);
            else
                mapScanDetail.put("nquoteunitrate", jsonCheckGetBillCode.getDouble("nquoteunitrate"));
            //报价计量单位数量
            if (jsonCheckGetBillCode.getString("nquoteunitnum") == null ||
                    jsonCheckGetBillCode.getString("nquoteunitnum").equals("") ||
                    jsonCheckGetBillCode.getString("nquoteunitnum").equals("null"))
                mapScanDetail.put("nquoteunitnum", 0);
            else
                mapScanDetail.put("nquoteunitnum", jsonCheckGetBillCode.getDouble("nquoteunitnum"));
            //订单累计应发未出库数量
            if (jsonCheckGetBillCode.getString("nshouldoutnum") == null ||
                    jsonCheckGetBillCode.getString("nshouldoutnum").equals("") ||
                    jsonCheckGetBillCode.getString("nshouldoutnum").equals("null"))
                mapScanDetail.put("nordershouldoutnum", 0);
            else
                mapScanDetail.put("nordershouldoutnum", jsonCheckGetBillCode.getDouble("nshouldoutnum"));
            //收货地区
            mapScanDetail.put("pk_arrivearea", jsonCheckGetBillCode.getString("creceiveareaid"));
            //单据号
            mapScanDetail.put("vsourcebillcode", jsonCheckGetBillCode.getString("vbillcode"));
            //存货基本标识
            mapScanDetail.put("cinvbasid", jsonCheckGetBillCode.getString("cinvbasid"));
            //存货管理ID
            mapScanDetail.put("cinventoryid", currentObj.Invmandoc());
            //自由项一
            mapScanDetail.put("vfree1", currentObj.vFree1());
            //单据批次
            mapScanDetail.put("vbillbatchcode", jsonCheckGetBillCode.getString("vbatchcode"));
            //批次
            mapScanDetail.put("vbatchcode", currentObj.GetBatch());

            //该货位该存货编码批次有几件货?
            if (Integer.parseInt(currentObj.totalID()) == lstCurrentBox.size()) {
                mapScanDetail.put("spacenum", "1");
                mapScanDetail.put("box", "");
            } else {
                mapScanDetail.put("spacenum", "0");
                mapScanDetail.put("box", "分包未完");
            }


            //单据号
            mapScanDetail.put("BillCode", jsonCheckGetBillCode.getString("vbillcode"));

            mapScanDetail.put("vbdef1", jsonCheckGetBillCode.getString("vuserdef1"));

            mapScanDetail.put("vbdef20", jsonCheckGetBillCode.getString("vuserdef20"));


            lstSaveBody.add(mapScanDetail);

            //ADD BY WUQIONG START
            listcount = lstSaveBody.size();

            tvscancount.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount + "件 | " + "未扫" + (Tasknnum - listcount) + "件");
            //ADD BY WUQIONG END


        }

        MyListAdapter listItemAdapter = new MyListAdapter(StockTransScanIn.this, lstSaveBody,//数据源
                R.layout.vlisttransscanitem,
                new String[]{"InvCode", "InvName", "Batch", "AccID", "TotalNum",
                        "BarCode", "SeriNo", "BillCode", "ScanedNum", "box"},
                new int[]{R.id.txtTransScanInvCode, R.id.txtTransScanInvName,
                        R.id.txtTransScanBatch, R.id.txtTransScanAccId,
                        R.id.txtTransScanTotalNum, R.id.txtTransScanBarCode,
                        R.id.txtTransScanSeriNo, R.id.txtTransScanBillCode,
                        R.id.txtTransScanScanCount, R.id.txtTransBox}
        );
        lvTIScanDetail.setAdapter(listItemAdapter);
    }


    @NonNull
    private OnItemClickListener myListItemListener =
            new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    Map<String, Object> mapCurrent = (Map<String, Object>) lvTIScanDetail.getAdapter().getItem(arg2);
                    String lsKey = mapCurrent.get("BillCode").toString() +
                            mapCurrent.get("AccID").toString() +
                            mapCurrent.get("InvCode").toString() +
                            mapCurrent.get("Batch").toString() +
                            mapCurrent.get("SeriNo").toString();
                    ArrayList<Map<String, Object>> lstCurrent =
                            (ArrayList<Map<String, Object>>) mapCurrent.get(lsKey);
                    SimpleAdapter listItemAdapter = new SimpleAdapter(StockTransScanIn.this, lstCurrent,//数据源
                            android.R.layout.simple_list_item_2,
                            new String[]{"BoxNum", "FinishBarCode"},
                            new int[]{android.R.id.text1, android.R.id.text2}
                    );
                    new AlertDialog.Builder(StockTransScanIn.this).setTitle(R.string.FenBaoXiangXiXinXi)
                            .setAdapter(listItemAdapter, null)
                            .setPositiveButton(R.string.QueRen, null).show();
                }

            };

    //长按扫描详细，删除该条记录
    @NonNull
    private OnItemLongClickListener myListItemLongListener =
            new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                    Map<String, Object> mapCurrent = (Map<String, Object>) lvTIScanDetail.getAdapter().getItem(arg2);
                    String lsKey = mapCurrent.get("csourcebillbid").toString() +
                            mapCurrent.get("InvCode").toString() +
                            //MOD BY WUQIONG S
                            //mapCurrent.get("csourcerowno").toString();
                            mapCurrent.get("csourcerowno").toString() +
                            mapCurrent.get("SeriNo").toString();
                    //MOD BY WUQIONG E

                    String Barcode = mapCurrent.get("BarCode").toString();

                    ButtonOnClickDelconfirm btnScanItemDelOnClick = new ButtonOnClickDelconfirm(arg2, lsKey, Barcode);
                    DeleteAlertDialog = new AlertDialog.Builder(StockTransScanIn.this).setTitle(R.string.QueRenShanChu)
                            .setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
                            .setPositiveButton(R.string.QueRen, btnScanItemDelOnClick).setNegativeButton(R.string.QuXiao, null).show();

                    return true;
                }

            };

    //删除已扫描的内容
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

        SaveScanedBody();


        //ADD WUQIONG START
        listcount = lstSaveBody.size();
//    	Tasknnum = 0;
//		JSONArray JsonArraysA = new JSONArray();
//    	try {
//			JsonArraysA=(JSONArray)jsonBodyTask.get("TransBillBody");
//			for (int i =0; i<JsonArraysA.length();i++)
//			{
//				Tasknnum = Tasknnum + Integer.valueOf(((JSONObject)(JsonArraysA.get(i))).getString("nnum"));
//			}
//
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
//			e1.printStackTrace();
//			//ADD CAIXY TEST START
//			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//			//ADD CAIXY TEST END
//			return;
//		}
        tvscancount.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount + "件 | " + "未扫" + (Tasknnum - listcount) + "件");

        //ADD WUQIONG END

        //lvScanDetail.removeViewAt(iIndex);
        //lvScanDetail.getAdapter().notifyAll();
        MyListAdapter listItemAdapter = (MyListAdapter) lvTIScanDetail.getAdapter();
        listItemAdapter.notifyDataSetChanged();
        lvTIScanDetail.setAdapter(listItemAdapter);


        //   	if(JsonRemoveTaskData == null || JsonRemoveTaskData.length() < 1)
//    		return;
//
//    	if(!JsonRemoveTaskData.has(sKey))
//    		return;

        //DEL BY WUQIONG E

        if (JsonModTaskData == null || JsonModTaskData.length() < 1)
            return;

        if (!JsonModTaskData.has(sKey))
            return;


        //恢复之前删除的任务数据
        JSONArray JsonArrays = jsonBodyTask.getJSONArray("dbBody");
        //DEL BY WUQIONG S
        //JsonArrays.put((JSONObject)JsonRemoveTaskData.get(sKey));
        //JsonRemoveTaskData.remove(sKey);
        //jonsBody = new JSONObject();
        //jonsBody.put("Status", true);
        //jonsBody.put("TransBillBody", JsonArrays);
        //jonsBody.put("RemoveTaskData", JsonRemoveTaskData);
        //getTaskListData(jonsBody);

//    	if (JsonRemoveTaskData.has(sKey))
//    	{
//        	JsonArrays.put((JSONObject)JsonRemoveTaskData.get(sKey));
//        	JsonRemoveTaskData.remove(sKey);
//    	}
        //DEL BY WUQIONG E

        //MOD BY WUQIONG START
        if (JsonModTaskData.has(sKey)) {
            //JsonArrays.put((JSONObject)JsonModTaskData.get(sKey));
            //JSONArray JsonArraysMod=(JSONArray)jonsBody.get("dbBody");
            JSONObject JsonReMod = (JSONObject) JsonModTaskData.get(sKey);
            JSONObject jObj = new JSONObject();

            String csourcebillhid = JsonReMod.getString("cgeneralbid").toString();
            String InvCode = JsonReMod.getString("invcode").toString();
            String csourcerowno = JsonReMod.getString("crowno").toString();
            String nnum = JsonReMod.getString("noutnum").toString();
            String Tasknnum = "0";

            for (int i = 0; i < JsonArrays.length(); i++) {
                String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i))).getString("cgeneralbid");
                String InvCodeaDel = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
                String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i))).getString("crowno");


                if (csourcebillhidDel.equals(csourcebillhid) && InvCodeaDel.equals(InvCode) && csourcerownoaDel.equals(csourcerowno)) {
                    Tasknnum = ((JSONObject) (JsonArrays.get(i))).getString("noutnum");
                }
            }
            //已经修改
//

            jObj.put("vfree1", JsonReMod.getString("vfree1").toString());
            jObj.put("invname", JsonReMod.getString("invname").toString());
            jObj.put("invcode", JsonReMod.getString("invcode").toString());
            jObj.put("accid", JsonReMod.getString("accid").toString());

            jObj.put("crowno", JsonReMod.getString("crowno").toString());
            jObj.put("cfirstbillhid", JsonReMod.getString("cfirstbillhid").toString());
            jObj.put("cgeneralhid", JsonReMod.getString("cgeneralhid").toString());
            jObj.put("cfirstbillbid", JsonReMod.getString("cfirstbillbid").toString());
            jObj.put("cgeneralbid", JsonReMod.getString("cgeneralbid").toString());
            jObj.put("cfirsttype", JsonReMod.getString("cfirsttype").toString());
            jObj.put("cbodybilltypecode", JsonReMod.getString("cbodybilltypecode").toString());
            jObj.put("cquoteunitid", JsonReMod.getString("cquoteunitid").toString());
            jObj.put("nquoteunitrate", JsonReMod.getString("nquoteunitrate").toString());
            jObj.put("nquoteunitnum", JsonReMod.getString("nquoteunitnum").toString());
            jObj.put("nshouldoutnum", JsonReMod.getString("nshouldoutnum").toString());
            jObj.put("creceiveareaid", JsonReMod.getString("creceiveareaid").toString());
            jObj.put("vbillcode", JsonReMod.getString("vbillcode").toString());
            jObj.put("cinvbasid", JsonReMod.getString("cinvbasid").toString());
            jObj.put("vbatchcode", JsonReMod.getString("vbatchcode").toString());
            jObj.put("vuserdef1", JsonReMod.getString("vuserdef1").toString());
            jObj.put("vuserdef20", JsonReMod.getString("vuserdef20").toString());
            jObj.put("noutnum", JsonReMod.getString("noutnum").toString());
            jObj.put("ntranoutnum", JsonReMod.getString("ntranoutnum").toString());


            //修改数量问题
            int iTasknnum = Integer.valueOf(Tasknnum);

            String snnum = (nnum.replaceAll("\\.0", ""));

            int innum = Integer.valueOf(snnum);

            int inewnnum = iTasknnum + innum;
            String snewnnum = inewnnum + "";

            jObj.put("noutnum", snewnnum);//修改数量问题


//			jObj.put("bonroadflag",JsonReMod.getString("bonroadflag").toString());
//			jObj.put("vsourcewastcode",JsonReMod.getString("vsourcewastcode").toString());
//			jObj.put("nplannedprice",JsonReMod.getString("nplannedprice").toString());
//			jObj.put("naccumtonum",JsonReMod.getString("naccumtonum").toString());
//			jObj.put("pk_reqcorp",JsonReMod.getString("pk_reqcorp").toString());
//			jObj.put("nreplenishedastnum",JsonReMod.getString("nreplenishedastnum").toString());
//			jObj.put("noutgrossnum",JsonReMod.getString("noutgrossnum").toString());
//			jObj.put("nretastnum",JsonReMod.getString("nretastnum").toString());
//			jObj.put("vbillcode",JsonReMod.getString("vbillcode").toString());
//			jObj.put("csignwasttype",JsonReMod.getString("csignwasttype").toString());
//			jObj.put("cworkcenterid",JsonReMod.getString("cworkcenterid").toString());
//			jObj.put("invspec",JsonReMod.getString("invspec").toString());
//			jObj.put("btooutzgflag",JsonReMod.getString("btooutzgflag").toString());
//			jObj.put("dfirstbilldate",JsonReMod.getString("dfirstbilldate").toString());
//			jObj.put("nneedinassistnum",JsonReMod.getString("nneedinassistnum").toString());
//			jObj.put("pk_bodycalbody",JsonReMod.getString("pk_bodycalbody").toString());
//			jObj.put("nsaleprice",JsonReMod.getString("nsaleprice").toString());
//			jObj.put("cfreezeid",JsonReMod.getString("cfreezeid").toString());
//			jObj.put("dvalidate",JsonReMod.getString("dvalidate").toString());
//			jObj.put("vbodynote2",JsonReMod.getString("vbodynote2").toString());
//			jObj.put("btoouttoiaflag",JsonReMod.getString("btoouttoiaflag").toString());
//			jObj.put("invname",JsonReMod.getString("invname").toString());
//			jObj.put("csourcebillbid",JsonReMod.getString("csourcebillbid").toString());
//			jObj.put("vfirstbillcode",JsonReMod.getString("vfirstbillcode").toString());
//			jObj.put("nprice",JsonReMod.getString("nprice").toString());
//			jObj.put("vbilltypeu8rm",JsonReMod.getString("vbilltypeu8rm").toString());
//			jObj.put("pk_defdoc1",JsonReMod.getString("pk_defdoc1").toString());
//			jObj.put("vfree10",JsonReMod.getString("vfree10").toString());
//			jObj.put("pk_defdoc2",JsonReMod.getString("pk_defdoc2").toString());
//			jObj.put("nquoteunitnum",JsonReMod.getString("nquoteunitnum").toString());
//			jObj.put("pk_defdoc3",JsonReMod.getString("pk_defdoc3").toString());
//			jObj.put("ntarenum",JsonReMod.getString("ntarenum").toString());
//			jObj.put("pk_defdoc4",JsonReMod.getString("pk_defdoc4").toString());
//			jObj.put("pk_defdoc5",JsonReMod.getString("pk_defdoc5").toString());
//			jObj.put("pk_defdoc6",JsonReMod.getString("pk_defdoc6").toString());
//			jObj.put("flargess",JsonReMod.getString("flargess").toString());
//			jObj.put("nquotentprice",JsonReMod.getString("nquotentprice").toString());
//			jObj.put("invtype",JsonReMod.getString("invtype").toString());
//			jObj.put("vsignwastrowno",JsonReMod.getString("vsignwastrowno").toString());
//			jObj.put("btou8rm",JsonReMod.getString("btou8rm").toString());
//			jObj.put("csumid",JsonReMod.getString("csumid").toString());
//			jObj.put("pk_cubasdoc",JsonReMod.getString("pk_cubasdoc").toString());
//			jObj.put("csourcebillhid",JsonReMod.getString("csourcebillhid").toString());
//			jObj.put("breturnprofit",JsonReMod.getString("breturnprofit").toString());
//			jObj.put("vsourcebillcode",JsonReMod.getString("vsourcebillcode").toString());
//			jObj.put("vuserdef16",JsonReMod.getString("vuserdef16").toString());
//			jObj.put("vuserdef15",JsonReMod.getString("vuserdef15").toString());
//			jObj.put("vuserdef18",JsonReMod.getString("vuserdef18").toString());
//			jObj.put("vuserdef17",JsonReMod.getString("vuserdef17").toString());
//			jObj.put("nquoteunitrate",JsonReMod.getString("nquoteunitrate").toString());
//			jObj.put("drequiredate",JsonReMod.getString("drequiredate").toString());
//			jObj.put("nbarcodenum",JsonReMod.getString("nbarcodenum").toString());
//			jObj.put("vuserdef19",JsonReMod.getString("vuserdef19").toString());
//			jObj.put("pk_defdoc9",JsonReMod.getString("pk_defdoc9").toString());
//			jObj.put("cbodywarehouseid",JsonReMod.getString("cbodywarehouseid").toString());
//			jObj.put("pk_defdoc8",JsonReMod.getString("pk_defdoc8").toString());
//			jObj.put("pk_defdoc7",JsonReMod.getString("pk_defdoc7").toString());
//			jObj.put("nretnum",JsonReMod.getString("nretnum").toString());
//			jObj.put("vuserdef10",JsonReMod.getString("vuserdef10").toString());
//			jObj.put("vuserdef12",JsonReMod.getString("vuserdef12").toString());
//			jObj.put("vuserdef11",JsonReMod.getString("vuserdef11").toString());
//			jObj.put("wholemanaflag",JsonReMod.getString("wholemanaflag").toString());
//			jObj.put("vuserdef14",JsonReMod.getString("vuserdef14").toString());
//			jObj.put("cparentid",JsonReMod.getString("cparentid").toString());
//			jObj.put("cgeneralbid",JsonReMod.getString("cgeneralbid").toString());
//			jObj.put("vuserdef13",JsonReMod.getString("vuserdef13").toString());
//			jObj.put("crowno",JsonReMod.getString("crowno").toString());
//			jObj.put("pk_creqwareid",JsonReMod.getString("pk_creqwareid").toString());
//			jObj.put("csourcewasthid",JsonReMod.getString("csourcewasthid").toString());
//			jObj.put("fchecked",JsonReMod.getString("fchecked").toString());
//			jObj.put("nmny",JsonReMod.getString("nmny").toString());
//			jObj.put("vuserdef20",JsonReMod.getString("vuserdef20").toString());


            JSONArray JsonArraysRemod = new JSONArray();
            JSONObject jObjReMod = new JSONObject();
            for (int i = 0; i < JsonArrays.length(); i++) {
                String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i))).getString("cgeneralbid");
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


        //DEL BY WUQIONG S
//  		if (JsonRemoveTaskData.has(sKey))
//    	{
//  			jonsBody.put("RemoveTaskData", JsonRemoveTaskData);
//    	}
        //DEL BY WUQIONG E

//    	if(JsonModTaskData.has(sKey))
//    	{
//    		jsonBodyTask.put("ModTaskData", JsonModTaskData);
//    	}

        getTaskListData(jsonBodyTask);

        //MOD BY WUQIONG END


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
                    Toast.makeText(StockTransScanIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                case id.txtTIScanBarcode:
                    if (arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP) {
                        try {
                            //txtTIScanBarcode.setText(txtTIScanBarcode.getText().toString().replace("\n", ""));
                            ScanBarcode(txtTIScanBarcode.getText().toString());
                        } catch (ParseException e) {
                            txtTIScanBarcode.setText("");
                            txtTIScanBarcode.requestFocus();
                            Toast.makeText(StockTransScanIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            //ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            //ADD CAIXY TEST END
                        } catch (JSONException e) {
                            txtTIScanBarcode.setText("");
                            txtTIScanBarcode.requestFocus();
                            Toast.makeText(StockTransScanIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            //ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            //ADD CAIXY TEST END
                        } catch (IOException e) {
                            txtTIScanBarcode.setText("");
                            txtTIScanBarcode.requestFocus();
                            Toast.makeText(StockTransScanIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            //ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            //ADD CAIXY TEST END
                        }
                        return true;
                    }
                    break;
            }
            return false;
        }
    };

    //Button按下后的监听事件
    @NonNull
    private OnClickListener ButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(@NonNull View v) {
            switch (v.getId()) {
                case id.btnTIScanTask:

                    if (lstBodyTask == null || lstBodyTask.size() < 1)
                        return;


                    List<Map<String, Object>> lsTaskBody = new ArrayList<Map<String, Object>>();


                    for (int j = 0; j < lstBodyTask.size(); j++) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map = (HashMap<String, Object>) lstBodyTask.get(j);
                        String lsInvNum = (String) map.get("InvNum");
                        if (!lsInvNum.equals("0")) {
                            lsTaskBody.add(map);
                        }
                    }

                    for (int j = 0; j < lstBodyTask.size(); j++) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map = (HashMap<String, Object>) lstBodyTask.get(j);
                        String lsInvNum = (String) map.get("InvNum");
                        if (lsInvNum.equals("0")) {
                            lsTaskBody.add(map);
                        }
                    }


                    SimpleAdapter listItemAdapter = new SimpleAdapter(StockTransScanIn.this, lsTaskBody,
                            R.layout.vlisttranstask,
                            new String[]{"InvCode", "InvName", "Batch",  "InvNum", "BillCode"},
                            new int[]{R.id.txtTranstaskInvCode, R.id.txtTranstaskInvName,

                                    R.id.txtTranstaskInvNum}
                    );
                    new AlertDialog.Builder(StockTransScanIn.this).setTitle(R.string.YuanDanXinXi)
                            .setAdapter(listItemAdapter, null)
                            .setPositiveButton(R.string.QueRen, null).show();
                    break;
                case id.btnTIScanClear:

                    //MOD BY WUQIONG START
//					if(lvScanDetail.getAdapter().getCount()<1)
                    if (lvTIScanDetail.getCount() < 1)
                        //MOD BY WUQIONG END
                        return;

                    ButtonOnClickClearconfirm btnScanItemClearOnClick = new ButtonOnClickClearconfirm();
                    DeleteAlertDialog = new AlertDialog.Builder(StockTransScanIn.this).setTitle(R.string.QueRenQingKong)
                            .setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
                            .setPositiveButton(R.string.QueRen, btnScanItemClearOnClick).setNegativeButton(R.string.QuXiao, null).show();


                    break;
                case id.btnTIScanReturn:
                    Intent intent = new Intent();

                    SerializableList ResultBodyList = new SerializableList();
                    ResultBodyList.setList(lstSaveBody);
                    intent.putExtra("SaveBodyList", ResultBodyList);
                    //intent.putExtra("ScanTaskJson", jsonBodyTask.toString());
                    Common.SetBodyTask(jsonBodyTask);
                    //ModTask从BODY剥离
                    //intent.putExtra("ScanModTask", JsonModTaskData.toString());
                    Common.SetModTaskData(JsonModTaskData);
                    //add caixy s
                    intent.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
                    StockTransScanIn.this.setResult(6, intent);
                    finish();
                    break;
            }
        }
    };

    //清空扫描详细的监听事件
    private class ButtonOnClickClearconfirm implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                try {
                    ClearAllScanDetail();
                } catch (JSONException e) {
                    Toast.makeText(StockTransScanIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    e.printStackTrace();
                }
            } else
                return;
        }

    }

    //清空扫描详细的内容
    private void ClearAllScanDetail() throws JSONException {

        //DEL BY WUQIONG S
        //if(JsonRemoveTaskData == null || JsonRemoveTaskData.length() < 1)
        //	return;

        //恢复之前删除的任务数据
        //Iterator itKeys = JsonRemoveTaskData.keys();
        //DEL BY WUQIONG E
        //ADD BY WUQIONG START
        Iterator itModKeys = JsonModTaskData.keys();
        //ADD BY WUQIONG END
        JSONArray JsonArrays = new JSONArray();


        JsonArrays = (JSONArray) jsonBodyTask.get("dbBody");

        //DEL BY WUQIONG S
//    	while(itKeys.hasNext())
//    	{
//	    	String lsKey = itKeys.next().toString();
//	    	JsonArrays.put((JSONObject)JsonRemoveTaskData.get(lsKey));
//    	}
        //DEL BY WUQIONG E

        //ADD BY WUQIONG S
        while (itModKeys.hasNext()) {
            String lsKey = itModKeys.next().toString();
            if (JsonModTaskData.has(lsKey)) {
                //JsonArrays.put((JSONObject)JsonModTaskData.get(sKey));
                //JSONArray JsonArraysMod=(JSONArray)jonsBody.get("dbBody");
                JSONObject JsonReMod = (JSONObject) JsonModTaskData.get(lsKey);
                JSONObject jObj = new JSONObject();

                String csourcebillhid = JsonReMod.getString("cgeneralbid").toString();
                String InvCode = JsonReMod.getString("invcode").toString();
                String csourcerowno = JsonReMod.getString("crowno").toString();
                String nnum = JsonReMod.getString("noutnum").toString();
                String Tasknnum = "0";

                for (int i = 0; i < JsonArrays.length(); i++) {
                    String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i))).getString("cgeneralbid");
                    String InvCodeaDel = ((JSONObject) (JsonArrays.get(i))).getString("invcode");
                    String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i))).getString("crowno");


                    if (csourcebillhidDel.equals(csourcebillhid) && InvCodeaDel.equals(InvCode) && csourcerownoaDel.equals(csourcerowno)) {
                        Tasknnum = ((JSONObject) (JsonArrays.get(i))).getString("noutnum");
                    }
                }

                jObj.put("vfree1", JsonReMod.getString("vfree1").toString());
                jObj.put("invname", JsonReMod.getString("invname").toString());
                jObj.put("invcode", JsonReMod.getString("invcode").toString());

                jObj.put("accid", JsonReMod.getString("accid").toString());

                jObj.put("crowno", JsonReMod.getString("crowno").toString());
                jObj.put("cfirstbillhid", JsonReMod.getString("cfirstbillhid").toString());
                jObj.put("cgeneralhid", JsonReMod.getString("cgeneralhid").toString());
                jObj.put("cfirstbillbid", JsonReMod.getString("cfirstbillbid").toString());
                jObj.put("cgeneralbid", JsonReMod.getString("cgeneralbid").toString());
                jObj.put("cfirsttype", JsonReMod.getString("cfirsttype").toString());
                jObj.put("cbodybilltypecode", JsonReMod.getString("cbodybilltypecode").toString());
                jObj.put("cquoteunitid", JsonReMod.getString("cquoteunitid").toString());
                jObj.put("nquoteunitrate", JsonReMod.getString("nquoteunitrate").toString());
                jObj.put("nquoteunitnum", JsonReMod.getString("nquoteunitnum").toString());
                jObj.put("nshouldoutnum", JsonReMod.getString("nshouldoutnum").toString());
                jObj.put("creceiveareaid", JsonReMod.getString("creceiveareaid").toString());
                jObj.put("vbillcode", JsonReMod.getString("vbillcode").toString());
                jObj.put("cinvbasid", JsonReMod.getString("cinvbasid").toString());
                jObj.put("vbatchcode", JsonReMod.getString("vbatchcode").toString());
                jObj.put("vuserdef1", JsonReMod.getString("vuserdef1").toString());
                jObj.put("vuserdef20", JsonReMod.getString("vuserdef20").toString());
                jObj.put("noutnum", JsonReMod.getString("noutnum").toString());
                jObj.put("ntranoutnum", JsonReMod.getString("ntranoutnum").toString());


                //修改数量问题
                int iTasknnum = Integer.valueOf(Tasknnum);

                String snnum = (nnum.replaceAll("\\.0", ""));

                int innum = Integer.valueOf(snnum);

                int inewnnum = iTasknnum + innum;
                String snewnnum = inewnnum + "";

                jObj.put("noutnum", snewnnum);//修改数量问题


                JSONArray JsonArraysRemod = new JSONArray();
                JSONObject jObjReMod = new JSONObject();
                for (int i = 0; i < JsonArrays.length(); i++) {
                    String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i))).getString("cgeneralbid");
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


        //重新绑定任务ListView
        getTaskListData(jsonBodyTask);

        while (itModKeys.hasNext())
            JsonModTaskData = new JSONObject();
        ScanedBarcode = new ArrayList<String>();
        SaveScanedBody();
        //ADD BY WUQIONG END


        //清空保存在内存的扫描详细
        lstSaveBody = new ArrayList<Map<String, Object>>();
        lvTIScanDetail.setAdapter(null);
        txtTIScanBarcode.setText("");
        //ADD BY WUQIONG START
        listcount = lstSaveBody.size();

        tvscancount.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount + "件 | " + "未扫" + (Tasknnum - listcount) + "件");
        //ADD BY WUQIONG END
    }


    public static class MyListAdapter extends BaseAdapter {
        @Nullable
        private Context                   context     = null;
        @Nullable
        private LayoutInflater            inflater    = null;
        @Nullable
        private List<Map<String, Object>> list        = null;
        @Nullable
        private String                    keyString[] = null;
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
                    tvItem = null;
                    tvItem = (TextView) arg1.findViewById(idValue[i]);
                    tvItem.setText(map.get(keyString[i]).toString());
                }

            }

            return arg1;
        }

    }
}
