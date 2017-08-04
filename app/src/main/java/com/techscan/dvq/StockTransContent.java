package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.login.MainMenu;

import org.apache.http.ParseException;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StockTransContent extends Activity {

    String tmpWHStatus = "";// 仓库是否启用货位
    String tmpBillStatus = "";// 单据是否正向调拨
    @Nullable
            String        fileName       = null;
    @Nullable
            String        fileNameScan   = null;
    @Nullable
            String        ScanedFileName = null;
    @Nullable
            String        UserID         = null;
    @Nullable
            File          file           = null;
    @Nullable
            File          fileScan       = null;
    @NonNull
            String        ReScanHead     = "1";
    @NonNull
    private ButtonOnClick buttonOnClick  = new ButtonOnClick(0);
    @Nullable
    private AlertDialog   SelectButton   = null;
    @Nullable
    private String[]      ExitNameList   = null;

    EditText txtTTransOutPos;
    EditText txtPDOrder;
    ListView lvPDOrder;
    @Nullable
    SimpleAdapter lvDBOrderAdapter;
    TextView tvTTransOutPos;
    TextView tvPDOrder;
    Button btnTransScan;
    Button btnTransExit;
    Button btnTransSave;
    ImageButton btnWareHouse;
    ImageButton btnPDOrder;
    ImageButton btnCompany;

    // private JSONObject JsonModTaskData = new JSONObject();

    // ADD BY WUQIONG START 2015/04/24
    TextView tvRdcl;
    ImageButton btnRdcl;
    EditText txtRdcl;
    TextView tvManualNo;
    EditText txtManualNo;
    @NonNull
    String rdflag = "1";
    String tmprdCode = "";
    @Nullable
    String tmprdIDA = "";
    @Nullable
    String tmprdIDB = "";
    String tmprdName = "";
    @Nullable
    String tmpmanualNo = "";
    int TaskCount = 0;
    // ADD BY WUQIONG END 2015/04/24

    // ADD CAIXY TEST START
    // private SoundPool sp;//声明一个SoundPool
    // private int MainLogin.music;//定义一个int来设置suondID
    private writeTxt writeTxt; // 保存LOG文件
    @Nullable
    private ArrayList<String> ScanedBarcode     = new ArrayList<String>();
    // 定义是否删除Dialog
    @Nullable
    private AlertDialog       DeleteAlertDialog = null;
    // ADD CAIXY TEST END

    String warehouseCode;
    // String warehouseID;//仓库ID
    String headJons;
    JSONObject jonsHead; // 源头单据表头
    @Nullable
    JSONObject jonsBody; // 源头单据表体

    // JSONObject saveJsonA = new JSONObject();
    // JSONObject saveJsonB = new JSONObject();
    // JSONArray saveJsonHead = new JSONArray();
    // JSONArray saveJsonBody = new JSONArray();
    @NonNull
    JSONObject saveJsonMulti = new JSONObject();

    @NonNull
    JSONObject                sendSaveJson = new JSONObject();
    @Nullable
    List<Map<String, Object>> lstSaveBody  = null;

    // JSONObject currentWarehouse;
    String companyID;// 公司ID
    // String OrgId; //库存组织ID
    String tmpAccIDA = "";// 帐套号
    String tmpAccIDB = "";// 帐套号
    // String vCode="";
    // String tmpBillIDA = "";
    // String tmpBillIDB = "";
    // String tmpAccID = "";
    String tmpposCode = "";
    String tmpposName = "";
    // String tmpposID = "";
    String tmpposIDA = "";
    String tmpposIDB = "";
    String wareHousePKFromA = "";
    String wareHousePKToA = "";

    String wareHousePKFromB = "";
    String wareHousePKToB = "";

    String wareHouseNameFrom = "";
    String wareHouseNameTo = "";
    String PKcorpFrom = "";
    String PKcorpTo = "";

    String lsResultBillCodeA = "";
    String lsResultBillCodeB = "";

    @Nullable
    JSONObject m_SerialNo   = null;
    @Nullable
    JSONObject m_ScanDetail = null;

    @NonNull
    String hstable = "";

    @Nullable
    Intent scanDetail = null;

    @Nullable
    List<Map<String, Object>> lstPDOrder = null;
    // 增加退
    // String[] from = {"No","From", "To","AccID","Dcorp"};
    // int[] to = { R.id.listpdorder, R.id.listfromware, R.id.listtoware,
    // R.id.listaccid, R.id.listpddcorp};
    //
    @NonNull
    String[]                  from       = {"No", "From", "To", "AccID", "Dcorp", "statusE"};
    @NonNull
    int[]                     to         = {R.id.listpdorder, R.id.listfromware, R.id.listtoware,
            R.id.listaccid, R.id.listpddcorp, R.id.listpdbillstatus};

    // 账套号
    @NonNull
    String fsAccIDFlag = "";

    // GUID
    @Nullable
    UUID uploadGuid = null;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {// 拦截meu键事件 //do something...
            return false;
        }
        return keyCode != KeyEvent.KEYCODE_BACK;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trans_content);

        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("调拨出库");

        tvPDOrder = (TextView) findViewById(R.id.tvTPDOrder);
        tvTTransOutPos = (TextView) findViewById(R.id.tvTTransOutPos);

        btnPDOrder = (ImageButton) findViewById(R.id.btnTPDOrder);
        btnPDOrder.setOnClickListener(myListner);

        btnTransScan = (Button) findViewById(R.id.btnTransScan);
        btnTransScan.setOnClickListener(myListner);

        btnTransExit = (Button) findViewById(R.id.btnTransExit);
        btnTransExit.setOnClickListener(myListner);

        btnTransSave = (Button) findViewById(R.id.btnTransSave);
        btnTransSave.setOnClickListener(myListner);

        // 符合条件的调拨订单列表
        lvPDOrder = (ListView) findViewById(R.id.lvPDOrder);
        lstPDOrder = new ArrayList<Map<String, Object>>();
        // add caixy s //添加删除事件
        lvPDOrder.setOnItemLongClickListener(myListItemLongListener);
        // add caixy e

        txtPDOrder = (EditText) findViewById(R.id.txtTPDOrder);
        txtPDOrder.setOnKeyListener(myTxtListener);
        txtPDOrder.requestFocus();

        txtTTransOutPos = (EditText) findViewById(R.id.txtTTransOutPos);
        txtTTransOutPos.setOnKeyListener(myTxtListener);

        txtTTransOutPos.setAllCaps(true);
        txtPDOrder.setAllCaps(true);
        // ADD BY WUQIONG 2015/04/24
        btnRdcl = (ImageButton) findViewById(R.id.btnRdcl);
        btnRdcl.setOnClickListener(myListner);
        txtRdcl = (EditText) findViewById(R.id.txtRdcl);
        txtRdcl.setOnKeyListener(myTxtListener);

        txtRdcl.setFocusable(false);
        txtRdcl.setFocusableInTouchMode(false);

        tvRdcl = (TextView) findViewById(R.id.tvRdcl);

        txtManualNo = (EditText) findViewById(R.id.txtManualNo);
        txtManualNo.setOnKeyListener(myTxtListener);
        // txtManualNo.setEnabled(false);
        tvManualNo = (TextView) findViewById(R.id.tvManualNo);
        // ADD BY WUQIONG 2015/04/24

        // ADD CAIXY START
        // sp= new SoundPool(10, AudioManager.STREAM_SYSTEM,
        // 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        // MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1);
        // //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        // ADD CAIXY END

        btnTransScan.setFocusable(false);
        btnTransExit.setFocusable(false);
        btnTransSave.setFocusable(false);
        btnPDOrder.setFocusable(false);

        UserID = MainLogin.objLog.UserID;
        // String LogName = BillType + UserID + dfd.format(day)+".txt";
        ScanedFileName = "4Y" + UserID + ".txt";
        fileName = "/sdcard/DVQ/4Y" + UserID + ".txt";
        fileNameScan = "/sdcard/DVQ/4YScan" + UserID + ".txt";

        file = new File(fileName);
        fileScan = new File(fileNameScan);
        ReScanHead();
        MainMenu.cancelLoading();
    }

    private void GetWHPosStatus() throws JSONException {

        JSONObject para = new JSONObject();
        para.put("FunctionName", "GetWHPosStatus");

        if (tmpAccIDA.equals("A")) {
            para.put("WareHouse", wareHousePKFromA);
        } else {
            if (tmpAccIDB.equals("B")) {
                para.put("WareHouse", wareHousePKFromB);
            }
        }

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        JSONObject rev = null;
        try {
            // if(tmpAccIDA.equals("A"))
            // {
            // rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccIDA);
            // }
            // else if (tmpAccIDB.equals("B"))
            // {
            // rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccIDB);
            // }
            if (tmpAccIDA.equals("A")) {
                rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccIDA);
            } else {
                if (tmpAccIDB.equals("B")) {
                    rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccIDB);
                }
            }

        } catch (ParseException e) {

            Toast.makeText(StockTransContent.this, "获取仓库状态失败",
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        } catch (IOException e) {

            Toast.makeText(StockTransContent.this, "获取仓库状态失败",
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        if (rev == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }

        if (rev.getBoolean("Status")) {
            JSONArray val = rev.getJSONArray("position");
            if (val.length() < 1) {
                Toast.makeText(StockTransContent.this, "获取仓库状态失败",
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            String WHStatus;
            JSONObject temp = val.getJSONObject(0);

            WHStatus = temp.getString("csflag");

            tmpWHStatus = WHStatus;
            return;
        } else {
            Toast.makeText(StockTransContent.this, "获取仓库状态失败",
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;

        }

    }

    private void TransScan() {
        if (lvPDOrder.getCount() < 1) {
            Toast.makeText(StockTransContent.this, "请先确认需要扫描的订单号",
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        try {
            GetWHPosStatus();
        } catch (JSONException e) {
            Toast.makeText(StockTransContent.this, "获取仓库状态失败",
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END

        }

        if (tmpWHStatus.equals("")) {
            return;
        }

        if (tmpWHStatus.equals("Y")) {
            if (tmpposCode == null || tmpposCode.equals("")) {
                Toast.makeText(StockTransContent.this, R.string.QingShuRuHuoWeiHao,
                        Toast.LENGTH_LONG).show();

                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
        }

        // ADD BY WUQIONG START
        if (tmprdCode == null || tmprdCode.equals("")) {
            Toast.makeText(StockTransContent.this, R.string.QingShuChuKuLeiBie, Toast.LENGTH_LONG)
                    .show();

            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        // ADD BY WUQIONG END

        // txtTTransOutPos.setEnabled(false);
        // txtPDOrder.setEnabled(false);

        SaveScanedHead();

        Intent intTransScan = new Intent(StockTransContent.this,
                StockTransScan.class);

        // intTransScan.putExtra("TaskJonsBody",jonsBody.toString());

        // intTransScan.putExtra("ScanModTask",JsonModTaskData.toString());
        Common.SetBodyTask(jonsBody);
        // intent.putExtra("ScanTaskJson", jonsBody.toString());
        // //ModTask从BODY剥离
        // intent.putExtra("ScanModTask", JsonModTaskData.toString());

        intTransScan.putExtra("AccIDA", tmpAccIDA);
        intTransScan.putExtra("AccIDB", tmpAccIDB);

        intTransScan.putExtra("wareHouseNameFrom", wareHouseNameFrom);
        intTransScan.putExtra("wareHouseNameTo", wareHouseNameTo);

        SerializableList lstScanSaveDetial = new SerializableList();
        lstScanSaveDetial.setList(lstSaveBody);
        intTransScan.putExtra("lstScanSaveDetial", lstScanSaveDetial);
        // add caixy s
        intTransScan.putExtra("PKcorpFrom", PKcorpFrom);
        intTransScan.putExtra("PKcorpTo", PKcorpTo);

        intTransScan.putExtra("wareHousePKFromA", wareHousePKFromA);
        intTransScan.putExtra("wareHousePKToA", wareHousePKToA);

        intTransScan.putExtra("wareHousePKFromB", wareHousePKFromB);
        intTransScan.putExtra("wareHousePKToB", wareHousePKToB);

        if (tmpWHStatus.equals("Y")) {
            intTransScan.putExtra("tmpposIDA", tmpposIDA);
            intTransScan.putExtra("tmpposIDB", tmpposIDB);
        }
        intTransScan.putExtra("tmpWHStatus", tmpWHStatus);
        intTransScan.putExtra("tmpBillStatus", tmpBillStatus);

        // intTransScan.putExtra("tmpBillStatus", tmpBillStatus);

        String sTaskCount = TaskCount + "";
        intTransScan.putExtra("TaskCount", sTaskCount);
        intTransScan.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
        // add caixy e

        startActivityForResult(intTransScan, 96);

    }

    private void ReScanErr() {
        AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
                R.string.CuoWu).setMessage("数据加载出现错误" + "\r\n" + "退出该模块并且再次尝试加载");

        bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false)
                .create().show();
        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        return;
    }

    @NonNull
    private DialogInterface.OnClickListener listenExit = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            finish();
            Common.ReScanErr = false;
            System.gc();
        }
    };

    private void ReScanHead() {
        String res = "";

        if (!file.exists()) {
            ReScanHead = "1";
            return;
        }

        try {

            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];

            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();

            String[] val;
            if (res.contains("|")) {
                ReScanHead = "0";
                val = res.split("\\|");

                if (val.length != 5) {
                    ReScanHead = "1";
                    return;
                }

                String Barcode = val[0];
                String Rdcl = val[1];
                String Pos = val[2];
                String ManualNo = val[3];
                String BillCount = val[4];
                ArrayList<String> ScanedBillBar = new ArrayList<String>();
                ArrayList<String> ScanedRdClBar = new ArrayList<String>();
                ArrayList<String> ScanedPosBar = new ArrayList<String>();

                String[] Bars;
                if (Barcode.contains(",")) {
                    Bars = Barcode.split("\\,");

                    for (int i = 0; i < Bars.length; i++) {
                        ScanedBillBar.add(Bars[i]);
                    }
                } else {
                    ScanedBillBar.add(Barcode);
                }

                String[] RdCls;
                if (Rdcl.contains(",")) {
                    RdCls = Rdcl.split("\\,");

                    for (int i = 0; i < RdCls.length; i++) {
                        // ScanedRdClBar.add(RdCls[i]);
                        if (RdCls[i].equals("null")) {
                            ScanedRdClBar.add("");
                        } else {
                            ScanedRdClBar.add(RdCls[i]);
                        }
                    }
                }

                String[] Poss;
                if (Pos.contains(",")) {
                    Poss = Pos.split("\\,");

                    for (int i = 0; i < Poss.length; i++) {
                        if (Poss[i].equals("null")) {
                            ScanedPosBar.add("");
                        } else {
                            ScanedPosBar.add(Poss[i]);
                        }

                    }
                }

                if (ScanedBillBar.size() < 1) {
                    ReScanHead = "1";
                    return;
                } else {

                    int x = 0;
                    for (int i = 0; i < ScanedBillBar.size(); i++) {

                        if (x > 10) {
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            Common.ReScanErr = true;
                            ReScanErr();
                            return;
                        }

                        FindOnlyBillHeadByBillId(ScanedBillBar.get(i).toString());
                        String OKflg = "ng";
                        if (lstPDOrder == null || lstPDOrder.size() < 1) {
                            x++;
                            i--;
                        } else {
                            for (int j = 0; j < lstPDOrder.size(); j++) {
                                Map<String, Object> SelectedItemMap = (Map<String, Object>) lvPDOrder.getItemAtPosition(j);

                                String AAA = SelectedItemMap.get("AccID")
                                        .toString()
                                        + SelectedItemMap.get("No").toString();
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
                }

                this.tmprdCode = ScanedRdClBar.get(0).toString();
                this.tmprdName = ScanedRdClBar.get(1).toString();
                this.tmprdIDA = ScanedRdClBar.get(2).toString();
                this.tmprdIDB = ScanedRdClBar.get(3).toString();
                this.txtRdcl.setText(this.tmprdName);

                this.tmpposCode = ScanedPosBar.get(0).toString();
                this.tmpposName = ScanedPosBar.get(1).toString();
                this.tmpposIDA = ScanedPosBar.get(2).toString();
                this.tmpposIDB = ScanedPosBar.get(3).toString();
                this.txtTTransOutPos.setText(this.tmpposCode);

                txtManualNo.setText(ManualNo);

                int iBillCount = Integer.valueOf(BillCount.substring(0,
                        BillCount.length() - 2));

                if (iBillCount == lstPDOrder.size() && tmprdCode != null
                        && !tmprdCode.equals("")) {

                    GetWHPosStatus();

                    if (tmpWHStatus.equals("Y")) {
                        if (tmpposCode != null && !tmpposCode.equals("")) {
                            this.txtPDOrder.requestFocus();
                            ReScanHead = "1";
                            TransScan();
                        }
                    } else {
                        this.txtPDOrder.requestFocus();
                        ReScanHead = "1";
                        TransScan();
                    }
                }

                this.txtPDOrder.requestFocus();

                ReScanHead = "1";

            }
        } catch (Exception e) {

            e.printStackTrace();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stock_trans_content, menu);
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

    // 创建对话框的按钮事件侦听
    @NonNull
    private Button.OnClickListener myListner = new Button.OnClickListener() {
        @Override
        public void onClick(@NonNull View v) {
            switch (v.getId()) {
                case R.id.btnTPDOrder: {
                    if (lstSaveBody == null || lstSaveBody.size() < 1) {

                    } else {
                        Toast.makeText(StockTransContent.this,
                                R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG)
                                .show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        txtPDOrder.setText("");
                        break;
                    }

                    try {
                        btnPDOrderClick();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                // ADD BY WUQIONG START
                case R.id.btnRdcl: {
                    try {
                        if (lstPDOrder == null || lstPDOrder.size() < 1) {
                            txtRdcl.setText("");
                            Toast.makeText(StockTransContent.this,
                                    R.string.DanJuXinXiMeiYouBuNengXuanZeShouFaLeiBie, Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }
                        if (tmprdName.equals("跨公司调拨出库")) {
                            Toast.makeText(StockTransContent.this,
                                    "跨公司调时,不能修改收发类别", Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }

                        btnRdclClick("");
                    } catch (ParseException e) {
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();

                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (IOException e) {
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (JSONException e) {
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (Exception e) {
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        break;
                    }
                    break;

                }
                // ADD BY WUQIONG END
                case R.id.btnTransScan: {
                    TransScan();
                    break;
                }
                case R.id.btnTransExit: {

                    Exit();
                    break;
                }
                case R.id.btnTransSave: {
                    try {
                        SaveTransDate();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block

                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        e.printStackTrace();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(StockTransContent.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };

    // 保存数据

    private void SaveTransDate() throws JSONException, ParseException,
            IOException {


        if (tmpWHStatus.equals("Y")) {
            if (tmpAccIDA.equals("A")) {
                if (tmpposIDA == null || tmpposIDA.equals("")) {
                    Toast.makeText(StockTransContent.this, R.string.QingShuRuHuoWeiHao,
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            }

            if (tmpAccIDB.equals("B")) {
                if (tmpposIDB == null || tmpposIDB.equals("")) {
                    Toast.makeText(StockTransContent.this, R.string.QingShuRuHuoWeiHao,
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            }
        }


        JSONObject saveJsonA = new JSONObject();
        JSONObject saveJsonB = new JSONObject();
        String lsResultBillCodeX = "";

        boolean SaveA = false;
        boolean SaveB = false;

        // JSONObject saveJsonBodyLocation = new JSONObject();
        // saveJsonArrMulti = new JSONArray();

        if (lstPDOrder == null || lstPDOrder.size() < 1) {
            Toast.makeText(StockTransContent.this, R.string.WuKeBaoCunShuJu, Toast.LENGTH_LONG)
                    .show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        // ADD BY WUQIONG START
        if (lstSaveBody == null || lstSaveBody.size() < 1) {
            Toast.makeText(StockTransContent.this, R.string.WuKeBaoCunShuJu, Toast.LENGTH_LONG)
                    .show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        // ADD BY WUQIONG END

        if (tmpAccIDA.equals("A") && lsResultBillCodeA.equals("")) {
            JSONArray saveJsonArrMulti = new JSONArray();
            Map<String, Object> sendMapHead = new HashMap<String, Object>();
            JSONArray sendJsonArrHead = new JSONArray();
            JSONObject sendJsonSave = new JSONObject();
            Map<String, Object> sendMapBody = new HashMap<String, Object>();
            JSONArray sendJsonArrBody = new JSONArray();
            JSONArray sendJsonArrBodyLocation = new JSONArray();

            for (int i = 0; i < lstPDOrder.size(); i++) {
                sendMapHead = lstPDOrder.get(i);
                JSONObject jsonSaveHead = new JSONObject();
                jsonSaveHead = Common.MapTOJSONOBject(sendMapHead);
                // jsonSaveHead = Common.MapTOJSONOBject(sendMapBody);
                String LsAccID = jsonSaveHead.getString("AccID");
                if (LsAccID.equals("A")) {
                    sendJsonArrHead.put(jsonSaveHead);
                    sendJsonSave.put("Head", sendJsonArrHead);

                    for (int j = 0; j < lstSaveBody.size(); j++) {
                        sendMapBody = lstSaveBody
                                .get(j);

                        if (!sendMapBody.get("spacenum").toString().equals("1")) {
                            Toast.makeText(StockTransContent.this, R.string.YouWeiSaoWanDeFenBao,
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }

                        String sHBillCode = sendMapHead.get("No").toString();
                        String sBBillCode = sendMapBody.get("BillCode")
                                .toString();
                        // caixy 20160119 修改合并账套单据时只有单账套表体时出现保存失败的错误
                        String sAccID = sendMapBody.get("AccID").toString();

                        if (sHBillCode.equals(sBBillCode)) {
                            JSONObject jsonSaveBody = new JSONObject();
                            if (sendMapHead
                                    .get("No")
                                    .toString()
                                    .equals(sendMapBody.get("BillCode")
                                            .toString())
                                    && sAccID.equals("A")) {

                                SaveA = true;
                                jsonSaveBody = Common
                                        .MapTOJSONOBject(sendMapBody);
                                sendJsonArrBody.put(jsonSaveBody);

                                if (tmpWHStatus.equals("Y")) {
                                    JSONObject saveJsonBodyLocation = new JSONObject();
                                    saveJsonBodyLocation.put("csourcebillbid",
                                            sendMapBody.get("csourcebillbid")
                                                    .toString());
                                    // 货位暂时先写入A
                                    saveJsonBodyLocation.put("cspaceidf",
                                            tmpposIDA);

                                    saveJsonBodyLocation.put("spacenum",
                                            sendMapBody.get("spacenum")
                                                    .toString());
                                    sendJsonArrBodyLocation
                                            .put(saveJsonBodyLocation);
                                }
                            }
                        }
                    }
                }
            }

            sendJsonSave.put("ScanDetail", sendJsonArrBody);
            sendJsonSave.put("ScanDetailLocation", sendJsonArrBodyLocation);

            saveJsonArrMulti.put(sendJsonSave);

            if (saveJsonArrMulti == null || saveJsonArrMulti.length() < 1) {
                Toast.makeText(StockTransContent.this, R.string.WuKeBaoCunShuJu,
                        Toast.LENGTH_LONG).show();
                return;
            }

            // 执行保存
            saveJsonA = new JSONObject();
            saveJsonA.put("MultiTrans", saveJsonArrMulti);

            // 获取guid
            if (uploadGuid == null) {
                uploadGuid = UUID.randomUUID();
            }
            saveJsonA.put("GUIDS", uploadGuid.toString());
            saveJsonA.put("tmpWHStatus", tmpWHStatus);
            saveJsonA.put("tmpBillStatus", tmpBillStatus);

            // ADD BY WUQIONG START
            // 获取收发类别ID
            saveJsonA.put("RdID", tmprdIDA.toString());

            // 获取手工单号
            tmpmanualNo = txtManualNo.getText().toString().toUpperCase();
            saveJsonA.put("ManualNo", tmpmanualNo.toString());
            // ADD BY WUQIONG END
        }

        if (tmpAccIDB.equals("B") && lsResultBillCodeB.equals("")) {
            JSONArray saveJsonBrrMulti = new JSONArray();
            Map<String, Object> sendMapHead = new HashMap<String, Object>();
            JSONArray sendJsonArrHead = new JSONArray();
            JSONObject sendJsonSave = new JSONObject();
            Map<String, Object> sendMapBody = new HashMap<String, Object>();
            JSONArray sendJsonArrBody = new JSONArray();
            JSONArray sendJsonArrBodyLocation = new JSONArray();

            for (int i = 0; i < lstPDOrder.size(); i++) {
                sendMapHead = lstPDOrder.get(i);
                JSONObject jsonSaveHead = new JSONObject();
                jsonSaveHead = Common.MapTOJSONOBject(sendMapHead);
                // jsonSaveHead = Common.MapTOJSONOBject(sendMapBody);
                String LsAccID = jsonSaveHead.getString("AccID");
                if (LsAccID.equals("B")) {
                    sendJsonArrHead.put(jsonSaveHead);
                    sendJsonSave.put("Head", sendJsonArrHead);

                    for (int j = 0; j < lstSaveBody.size(); j++) {
                        sendMapBody = lstSaveBody
                                .get(j);

                        if (!sendMapBody.get("spacenum").toString().equals("1")) {
                            Toast.makeText(StockTransContent.this, R.string.YouWeiSaoWanDeFenBao,
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }

                        String sHBillCode = sendMapHead.get("No").toString();
                        String sBBillCode = sendMapBody.get("BillCode")
                                .toString();

                        // caixy 20160119 修改合并账套单据时只有单账套表体时出现保存失败的错误
                        String sAccID = sendMapBody.get("AccID").toString();

                        if (sHBillCode.equals(sBBillCode)) {
                            JSONObject jsonSaveBody = new JSONObject();
                            if (sendMapHead
                                    .get("No")
                                    .toString()
                                    .equals(sendMapBody.get("BillCode")
                                            .toString())
                                    && sAccID.equals("B")) {

                                SaveB = true;
                                jsonSaveBody = Common
                                        .MapTOJSONOBject(sendMapBody);
                                sendJsonArrBody.put(jsonSaveBody);

                                if (tmpWHStatus.equals("Y")) {
                                    JSONObject saveJsonBodyLocation = new JSONObject();
                                    saveJsonBodyLocation.put("csourcebillbid",
                                            sendMapBody.get("csourcebillbid")
                                                    .toString());
                                    // 货位暂时先写入A
                                    saveJsonBodyLocation.put("cspaceidf",
                                            tmpposIDB);

                                    saveJsonBodyLocation.put("spacenum",
                                            sendMapBody.get("spacenum")
                                                    .toString());
                                    sendJsonArrBodyLocation
                                            .put(saveJsonBodyLocation);
                                }
                            }
                        }
                    }
                }
            }

            sendJsonSave.put("ScanDetail", sendJsonArrBody);
            sendJsonSave.put("ScanDetailLocation", sendJsonArrBodyLocation);

            saveJsonBrrMulti.put(sendJsonSave);

            if (saveJsonBrrMulti == null || saveJsonBrrMulti.length() < 1) {
                Toast.makeText(StockTransContent.this, R.string.WuKeBaoCunShuJu,
                        Toast.LENGTH_LONG).show();
                return;
            }

            // 执行保存
            saveJsonB = new JSONObject();
            saveJsonB.put("MultiTrans", saveJsonBrrMulti);

            // 获取guid
            if (uploadGuid == null) {
                uploadGuid = UUID.randomUUID();
            }
            saveJsonB.put("GUIDS", uploadGuid.toString());
            saveJsonB.put("tmpWHStatus", tmpWHStatus);
            saveJsonB.put("tmpBillStatus", tmpBillStatus);

            // ADD BY WUQIONG START
            // 获取收发类别ID
            saveJsonB.put("RdID", tmprdIDB.toString());

            // 获取手工单号
            tmpmanualNo = txtManualNo.getText().toString().toUpperCase();
            saveJsonB.put("ManualNo", tmpmanualNo.toString());
            // ADD BY WUQIONG END

        }

        // JSONObject jas= Common.DoHttpQuery(saveJson, "SaveAdjOutBill",
        // fsAccIDFlag);
        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }

        if (tmpAccIDA.equals("A") && lsResultBillCodeA.equals("")
                && SaveA == true) {

            // 暂时只写入A
            JSONObject jasA = Common.DoHttpQuery(saveJsonA, "SaveAdjOutBill",
                    "A");

            // JSONObject jasB= Common.DoHttpQuery(saveJsonB, "SaveAdjOutBill",
            // "B");
            if (jasA == null) {
                Toast.makeText(StockTransContent.this,R.string.DanJuBaoCunChuXianWenTi_QingZaiCiTiJiao ,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            if (!jasA.has("Status")) {
                Toast.makeText(StockTransContent.this,
                        R.string.DanJuBaoCunChuXianWenTi_QingZaiCiTiJiao, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            boolean loginStatus = jasA.getBoolean("Status");

            if (loginStatus == true) {

                if (jasA.has("BillCode")) {
                    lsResultBillCodeA = jasA.getString("BillCode");
                    // 写入log文件
                    writeTxt = new writeTxt();

                    Date day = new Date();
                    SimpleDateFormat df = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");

                    SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");

                    String BillType = "4Y";
                    String LogName = BillType + UserID + dfd.format(day)
                            + ".txt";

                    String BillCode = lsResultBillCodeA;
                    String LogMsg = df.format(day) + " " + tmpAccIDA + " "
                            + BillCode;

                    writeTxt.writeTxtToFile(LogName, LogMsg);

                } else {
                    Toast.makeText(this, R.string.DanJuBaoCunChuXianWenTi_QingZaiCiTiJiao,
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }

            } else {
                String ErrMsg = jasA.getString("ErrMsg");
                Toast.makeText(StockTransContent.this, ErrMsg,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
        }

        if (tmpAccIDB.equals("B") && lsResultBillCodeB.equals("")
                && SaveB == true) {
            // 暂时只写入A
            JSONObject jasB = Common.DoHttpQuery(saveJsonB, "SaveAdjOutBill",
                    "B");

            // JSONObject jasB= Common.DoHttpQuery(saveJsonB, "SaveAdjOutBill",
            // "B");
            if (jasB == null) {
                Toast.makeText(StockTransContent.this, R.string.DanJuBaoCunChuXianWenTi_QingZaiCiTiJiao,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            if (!jasB.has("Status")) {
                Toast.makeText(StockTransContent.this,
                        R.string.DanJuBaoCunChuXianWenTi_QingZaiCiTiJiao, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            boolean loginStatus = jasB.getBoolean("Status");

            if (loginStatus == true) {

                if (jasB.has("BillCode")) {
                    lsResultBillCodeB = jasB.getString("BillCode");
                    // 写入log文件
                    writeTxt = new writeTxt();

                    Date day = new Date();
                    SimpleDateFormat df = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");

                    SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");

                    String BillType = "4Y";
                    String LogName = BillType + UserID + dfd.format(day)
                            + ".txt";

                    String BillCode = lsResultBillCodeB;
                    String LogMsg = df.format(day) + " " + tmpAccIDB + " "
                            + BillCode;

                    writeTxt.writeTxtToFile(LogName, LogMsg);

                } else {
                    Toast.makeText(this, "单据保存过程中出现了问题," + "请尝试再次提交!",
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            } else {
                String ErrMsg = jasB.getString("ErrMsg");
                Toast.makeText(StockTransContent.this, ErrMsg,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
        }

        if (SaveA == true && SaveB == true) {
            lsResultBillCodeX = "A " + lsResultBillCodeA + "\r\n" + "B "
                    + lsResultBillCodeB;
        } else if (SaveA == true && SaveB != true) {
            lsResultBillCodeX = lsResultBillCodeA;
        } else if (SaveA != true && SaveB == true) {
            lsResultBillCodeX = lsResultBillCodeB;
        }

        Map<String, Object> mapResultBillCode = new HashMap<String, Object>();
        mapResultBillCode.put("BillCode", lsResultBillCodeX);
        ArrayList<Map<String, Object>> lstResultBillCode = new ArrayList<Map<String, Object>>();
        lstResultBillCode.add(mapResultBillCode);
        // Toast.makeText(StockTransContent.this, "单据保存成功",
        // Toast.LENGTH_LONG).show();
        // IniActivyMemor();
        // return;

        uploadGuid = null;
        // ADD BY WUQIONG START
        tmprdIDA = null;
        tmprdIDB = null;
        tmpmanualNo = null;
        // ADD BY WUQIONG END
        SimpleAdapter listItemAdapter = new SimpleAdapter(
                StockTransContent.this,
                lstResultBillCode,// 数据源
                android.R.layout.simple_list_item_1,
                new String[]{"BillCode"}, new int[]{android.R.id.text1});
        new AlertDialog.Builder(StockTransContent.this).setTitle(R.string.DanJuBaoCunChengGong)
                .setAdapter(listItemAdapter, null)
                .setPositiveButton(R.string.QueRen, null).show();

        // 保存成功后初始化界面和内存数据

        // //写入log文件
        // writeTxt = new writeTxt();
        //
        // Date day=new Date();
        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //
        // SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
        //
        //
        // String BillType = "4Y";
        // String LogName = BillType + UserID + dfd.format(day)+".txt";
        //
        // if(tmpAccIDA.equals("A"))
        // {
        //
        // String BillCode = lsResultBillCodeA;
        // String LogMsg = df.format(day) + " " + tmpAccIDA + " " + BillCode;
        //
        // writeTxt.writeTxtToFile(LogName,LogMsg);
        // }
        // if(tmpAccIDB.equals("B"))
        // {
        // String BillCode = lsResultBillCodeB;
        // String LogMsg = df.format(day) + " " + tmpAccIDB + " " + BillCode;
        //
        // writeTxt.writeTxtToFile(LogName,LogMsg);
        // }

        InitActiveMemor();
        return;

    }

    // 初始化界面和内存数据
    private void InitActiveMemor() {
        this.txtPDOrder.setText("");
        this.txtTTransOutPos.setText("");
        this.txtRdcl.setText("");
        this.txtManualNo.setText("");
        Common.ClearIntDate();
        tmprdCode = "";
        tmprdName = "";
        tmprdIDA = "";
        tmprdIDB = "";
        tmpmanualNo = "";

        this.txtPDOrder.requestFocus();

        lstSaveBody = null;
        lstPDOrder = new ArrayList<Map<String, Object>>();
        // saveJsonArrMulti = new JSONArray();
        jonsBody = new JSONObject();
        // saveJsonA = new JSONObject();
        // saveJsonB = new JSONObject();
        ScanedBarcode = new ArrayList<String>();
        lvPDOrder.setAdapter(null);

        warehouseCode = "";
        // warehouseID="";//仓库ID
        headJons = "";
        jonsHead = new JSONObject(); // 源头单据表头
        // saveJsonHead = new JSONArray();
        // saveJsonBody = new JSONArray();
        saveJsonMulti = new JSONObject();
        // saveJsonArrMulti = new JSONArray();
        sendSaveJson = new JSONObject();
        // JsonModTaskData = new JSONObject();
        lstSaveBody = null;
        // currentWarehouse = null;
        companyID = "";// 公司ID
        // OrgId= ""; //库存组织ID
        // vCode="";
        // tmpBillIDA = "";
        // tmpBillIDB = "";
        tmpAccIDA = "";
        tmpAccIDB = "";
        tmpposCode = "";
        tmpposName = "";
        tmpposIDA = "";
        tmpposIDB = "";
        tmpWHStatus = "";
        tmpBillStatus = "";
        wareHousePKFromA = "";
        wareHousePKToB = "";
        wareHousePKFromB = "";
        wareHousePKToB = "";
        wareHouseNameFrom = "";
        wareHouseNameTo = "";
        lsResultBillCodeA = "";
        lsResultBillCodeB = "";
        m_SerialNo = null;
        m_ScanDetail = null;
        hstable = "";
        fsAccIDFlag = "";
        scanDetail = null;
        txtTTransOutPos.setText("");
        txtPDOrder.setText("");
        txtRdcl.setText("");
        txtManualNo.setText("");
        tmprdIDA = "";
        tmprdIDB = "";
        TaskCount = 0;
        ScanedBarcode = new ArrayList<String>();

        if (file.exists()) {
            file.delete();
        }

        if (fileScan.exists()) {
            fileScan.delete();
        }

        txtPDOrder.requestFocus();

    }

    // 记录表头内容
    private void SaveScanedHead() {

        if (ReScanHead.equals("0")) {
            return;
        }

        if (lstPDOrder == null || lstPDOrder.size() < 1) {
            return;
        }
        String BillBarCode = "";

        writeTxt = new writeTxt();

        // 记录扫描数据
        String ScanedBillBar = "";
        String ScanedRdClBar = "";
        String ScanedPosBar = "";

        for (int i = 0; i < lstPDOrder.size(); i++) {
            Map<String, Object> SelectedItemMap = (Map<String, Object>) lvPDOrder
                    .getItemAtPosition(i);

            if (i == lstPDOrder.size() - 1)
                BillBarCode = BillBarCode
                        + SelectedItemMap.get("AccID").toString()
                        + SelectedItemMap.get("No").toString();
            else
                BillBarCode = BillBarCode
                        + SelectedItemMap.get("AccID").toString()
                        + SelectedItemMap.get("No").toString() + ",";
        }
        ScanedBillBar = BillBarCode;


        String saverdA = tmprdIDA;

        String saverdB = tmprdIDB;
        if (saverdA.equals("")) {
            saverdA = "null";
        }
        if (saverdB.equals("")) {
            saverdB = "null";
        }


        if (tmprdCode == null || tmprdCode.equals("")) {
            ScanedRdClBar = "null,null,null,null";
        } else {
            ScanedRdClBar = tmprdCode + "," + tmprdName + "," + saverdA + ","
                    + saverdB;
        }

        if (tmpposCode == null || tmpposCode.equals("")) {
            ScanedPosBar = "null,null,null,null";
        } else {
            String posIDA = tmpposIDA;
            if (posIDA.equals("")) {
                posIDA = "null";
            }
            String posIDB = tmpposIDB;
            if (posIDB.equals("")) {
                posIDB = "null";
            }
            // ScanedPosBar="\"" +tmpposCode+ "\","+"\"" +tmpposName +
            // "\","+tmpposID;
            ScanedPosBar = tmpposCode + "," + tmpposName + "," + posIDA + ","
                    + posIDB;
        }
        // ScanedHeadInfo.add(ScanedPosBar);
        if (file.exists()) {
            file.delete();
        }

        writeTxt.writeTxtToFile(ScanedFileName,
                ScanedBillBar
                        + "|"
                        + ScanedRdClBar
                        + "|"
                        + ScanedPosBar
                        + "|"
                        + txtManualNo.getText().toString().toUpperCase()
                        .replace("|", "") + "|" + lstPDOrder.size());

    }

    // 通过调拨订单ID得到任务表体
    private void SetTransTaskParam() {

        // tmpBillIDA = "";
        // tmpBillIDB = "";
        tmpAccIDA = "";
        tmpAccIDB = "";
        wareHousePKFromA = "";
        wareHousePKToA = "";
        wareHousePKFromB = "";
        wareHousePKToB = "";
        wareHouseNameFrom = "";
        wareHouseNameTo = "";
        String tmpBillIDA = "";
        String tmpBillIDB = "";

        // lstPDOrder = new ArrayList<Map<String, Object>>();
        ArrayList<String> lstPDOrderA = new ArrayList<String>();
        ArrayList<String> lstPDOrderB = new ArrayList<String>();

        for (int i = 0; i < lstPDOrder.size(); i++) {
            Map<String, Object> SelectedItemMap = (Map<String, Object>) lvPDOrder
                    .getItemAtPosition(i);
            String sAccID = SelectedItemMap.get("AccID").toString();
            String BillID = SelectedItemMap.get("BillId").toString();
            if (sAccID.equals("A")) {
                tmpAccIDA = sAccID;
                wareHousePKFromA = SelectedItemMap.get("warehouseID")
                        .toString();
                wareHousePKToA = SelectedItemMap.get("warehouseToID")
                        .toString();
                lstPDOrderA.add(BillID);

            } else if (sAccID.equals("B")) {
                tmpAccIDB = sAccID;
                wareHousePKFromB = SelectedItemMap.get("warehouseID")
                        .toString();
                wareHousePKToB = SelectedItemMap.get("warehouseToID")
                        .toString();
                lstPDOrderB.add(BillID);
            }

            wareHouseNameFrom = SelectedItemMap.get("From").toString();
            wareHouseNameTo = SelectedItemMap.get("To").toString();

            PKcorpFrom = SelectedItemMap.get("pk_corp").toString();
            PKcorpTo = SelectedItemMap.get("pk_incorp").toString();

        }
        if (tmpAccIDA.equals("A")) {
            for (int i = 0; i < lstPDOrderA.size(); i++) {
                String BillID = lstPDOrderA.get(i).toString();
                if (i == lstPDOrderA.size() - 1)

                    tmpBillIDA = tmpBillIDA + "'" + BillID + "'";
                else
                    tmpBillIDA = tmpBillIDA + "'" + BillID + "',";
            }
        }

        if (tmpAccIDB.equals("B")) {
            for (int i = 0; i < lstPDOrderB.size(); i++) {
                String BillID = lstPDOrderB.get(i).toString();
                if (i == lstPDOrderB.size() - 1)

                    tmpBillIDB = tmpBillIDB + "'" + BillID + "'";
                else
                    tmpBillIDB = tmpBillIDB + "'" + BillID + "',";
            }
        }

        try {
            GetAdjustOrderBillBody(tmpBillIDA, tmpBillIDB);

        } catch (ParseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
    }

    // //仓库列表
    // private void btnWarehouseClick() throws JSONException
    // {
    // try
    // {
    // String lgUser = MainLogin.objLog.LoginUser;
    // String lgPwd = MainLogin.objLog.Password;
    // String LoginString = MainLogin.objLog.LoginString;
    //
    // JSONObject para = new JSONObject();
    //
    // para.put("FunctionName", "GetWareHouseList");
    // para.put("CompanyCode", MainLogin.objLog.CompanyCode);
    // para.put("STOrgCode", MainLogin.objLog.STOrgCode);
    // para.put("TableName", "warehouse");
    //
    //
    // JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
    // if(rev == null)
    // {
    // //网络通讯错误
    // Toast.makeText(this,"错误！网络通讯错误", Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    // return;
    // }
    // if(!rev.has("Status"))
    // {
    // Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // return ;
    // }
    //
    //
    // if(rev.getBoolean("Status"))
    // {
    // JSONArray val = rev.getJSONArray("warehouse");
    //
    // JSONObject temp = new JSONObject();
    // temp.put("warehouse", val);
    //
    // Intent ViewGrid = new Intent(this,ListWarehouse.class);
    // ViewGrid.putExtra("myData",temp.toString());
    //
    // startActivityForResult(ViewGrid,97);
    // }
    // else
    // {
    // String Errmsg = rev.getString("ErrMsg");
    // Toast.makeText(this,Errmsg, Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    //
    // }
    //
    // }
    // catch(Exception e)
    // {
    // Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    // }
    //
    // }
    //
    // add caixy s
    // 长按扫描详细，删除该条记录
    @NonNull
    private OnItemLongClickListener myListItemLongListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
            Map<String, Object> mapCurrent = lstPDOrder
                    .get(arg2);

            String BillId = mapCurrent.get("BillId").toString();
            String BillCode = mapCurrent.get("No").toString();

            ButtonOnClickDelconfirm btnScanItemDelOnClick = new ButtonOnClickDelconfirm(
                    arg2, BillCode);
            DeleteAlertDialog = new AlertDialog.Builder(StockTransContent.this)
                    .setTitle(R.string.QueRenShanChu).setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
                    .setPositiveButton(R.string.QueRen, btnScanItemDelOnClick)
                    .setNegativeButton(R.string.QuXiao, null).show();

            return true;
        }

    };

    // 删除已扫描详细的监听事件
    private class ButtonOnClickDelconfirm implements
            DialogInterface.OnClickListener {

        public int index;
        public String BillCode;

        public ButtonOnClickDelconfirm(int iIndex, String BillCode) {
            this.BillCode = BillCode;
            this.index = iIndex;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                try {
                    ConfirmDelItem(index, BillCode);
                } catch (JSONException e) {
                    Toast.makeText(StockTransContent.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    e.printStackTrace();
                }
            } else
                return;
        }

    }

    // 删除已扫描的内容
    private void ConfirmDelItem(int iIndex, String BillCode)
            throws JSONException {
        // 删除保存在内存的扫描详细
        if (lstSaveBody == null || lstSaveBody.size() < 1) {

        } else {
            Toast.makeText(StockTransContent.this, R.string.GaiRenWuYiJingBeiSaoMiaoWuFaShanChu,
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        lstPDOrder.remove(iIndex);
        JSONArray JsonArrays = new JSONArray();
        JSONArray NewJsonArrays = new JSONArray();
        JsonArrays = (JSONArray) jonsBody.get("TransBillBody");

        for (int i = 0; i < JsonArrays.length(); i++) {
            String vcode = ((JSONObject) (JsonArrays.get(i)))
                    .getString("vcode");
            if (!vcode.equals(BillCode)) {
                JSONObject jObj = new JSONObject();
                jObj = (JSONObject) (JsonArrays.get(i));
                NewJsonArrays.put(jObj);
            }

        }
        JsonArrays = NewJsonArrays;
        jonsBody = new JSONObject();

        if (JsonArrays != null && JsonArrays.length() > 0) {
            jonsBody.put("Status", true);
            jonsBody.put("TransBillBody", JsonArrays);
        } else {
            InitActiveMemor();

        }

        lvPDOrder.setAdapter(lvDBOrderAdapter);
        GetTaskCount();
        SaveScanedHead();

    }

    // 取得总任务数

    private void GetTaskCount() throws JSONException {
        TaskCount = 0;
        tmpWHStatus = "";
        tmpBillStatus = "";
        if (jonsBody == null || jonsBody.equals("")) {
            return;
        }
        // jonsBody
        if (jonsBody.length() > 0) {
            JSONArray JsonArrays = new JSONArray();
            JsonArrays = (JSONArray) jonsBody.get("TransBillBody");

            for (int i = 0; i < JsonArrays.length(); i++) {
                String nnum = ((JSONObject) (JsonArrays.get(i)))
                        .getString("nnum");
                String norderoutnum = ((JSONObject) (JsonArrays.get(i)))
                        .getString("norderoutnum");
                // TaskCount = TaskCount +
                // Integer.valueOf(((JSONObject)(JsonArrays.get(i))).getString("nnum").toString());
                String snnum = "0";
                if (!norderoutnum.equals("null")) {
                    snnum = (norderoutnum.replaceAll("\\.0", ""));
                }
                // 应发未出库数量 shouldoutnum
                int shouldoutnum = Integer.valueOf(nnum)
                        - Integer.valueOf(snnum);

                TaskCount = TaskCount + shouldoutnum;

                if (i == 0) {
                    Map localMap = (Map) this.lvPDOrder.getItemAtPosition(0);
                    String str4 = localMap.get("AccID").toString();
                    String str5 = localMap.get("pk_corp").toString();
                    String str6 = localMap.get("pk_incorp").toString();

                    tmpBillStatus = localMap.get("status").toString();

                    if ((str4.equals("A")) && (!str5.equals(str6))) {
                        this.tmprdCode = "213";
                        this.tmprdName = "跨公司调拨出库";
                        this.tmprdIDA = "0001AA1000000000J55M";
                        this.tmprdIDB = "";
                        this.txtRdcl.setText(this.tmprdName);
                        // txtTTransOutPos.requestFocus();
                        // return;
                    }

                    // GetWHPosStatus();
                }
            }
        }

    }

    // add caixy e

    // 调拨订单列表
    // 获得所有的调出单表头
    private void btnPDOrderClick() throws ParseException, IOException,
            JSONException {
        List<Map<String, Object>> selectedlist = null;

        PdOrderMultilist cPdOrderMultilist = new PdOrderMultilist();
        cPdOrderMultilist.setInit(lvPDOrder.getAdapter());
        Intent ViewGrid = new Intent(this, cPdOrderMultilist.getClass());
        ViewGrid.putExtra("AccIDFlag", fsAccIDFlag);
        ViewGrid.putExtra("InOutFlag", "Out");

        // SerializableList tmplist = new SerializableList();
        // tmplist.setList(ResultList);
        // Bundle ResultBundle = new Bundle();
        // ResultBundle.putSerializable("resultinfo", tmplist);
        // intent.putExtras(ResultBundle);
        txtPDOrder.setText("");
        startActivityForResult(ViewGrid, 98);
    }

    // ADD BY WUQIONG START
    // 打开收发类别画面
    private void btnRdclClick(String Code) throws ParseException, IOException,
            JSONException {
        Intent ViewGrid = new Intent(this, VlistRdcl.class);
        ViewGrid.putExtra("FunctionName", "GetRdcl");
        // ViewGrid.putExtra("AccID", "A");
        // ViewGrid.putExtra("rdflag", "1");
        // ViewGrid.putExtra("rdcode", "202");
        ViewGrid.putExtra("AccID", "");
        ViewGrid.putExtra("rdflag", rdflag);
        ViewGrid.putExtra("rdcode", "");
        startActivityForResult(ViewGrid, 98);
    }

    // ADD BY WUQIONG END

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (Common.ReScanErr == true) {
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            ReScanErr();
            return;
        }

        if (requestCode == 98) {
            switch (resultCode) {
                case 1: // 这是调拨订单单号列表返回的地方
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            try {
                                SerializableList serializableList = (SerializableList) bundle
                                        .get("resultinfo");
                                List<Map<String, Object>> resultList = serializableList
                                        .getList();

							/*
                             * String orderNo = bundle.getString("result");//
							 * 得到子窗口ChildActivity的回传数据
							 * txtPDOrder.setText(orderNo); String BillId=
							 * bundle.getString("BillId"); accID
							 * =bundle.getString("AccID");
							 * warehouseID=(String)bundle.get("warehouseID");
							 * OrgId=(String)bundle.get("orgID");
							 * companyID=(String)bundle.get("companyID");
							 * vCode=(String)bundle.get("vcode");
							 */

                                lstPDOrder = resultList;
                                lvDBOrderAdapter = new SimpleAdapter(
                                        StockTransContent.this, resultList,
                                        R.layout.vlistpds, from, to);
                                lvPDOrder.setAdapter(lvDBOrderAdapter);
                                // // //ADD BY WUQIONG END
                                // //通过调拨订单ID得到任务表体
                                SetTransTaskParam();
                                GetTaskCount();
                                SaveScanedHead();

                                // //货位输入是否可用
                                // if(lvPDOrder.getAdapter().getCount() > 0)
                                // {
                                // //txtTTransOutPos.setEnabled(true);
                                // //txtTTransOutPos.setFocusable(true);
                                // }
                                // else
                                // {
                                // txtTTransOutPos.setText("");
                                // txtTTransOutPos.setFocusable(false);
                                // }

                                // LoadOrderListFromDB(BillId);---暂时注释
                                // btnSAOrder.setEnabled(false);
                                // txtSaleOrder.setEnabled(false);
                                // 这里用来清除之前扫描的信息
                                // this.m_ScanDetail=null;
                                // this.m_SerialNo=null;
                                // this.hstable ="";
                            } catch (Exception e) {
                                Toast.makeText(this, e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                // ADD CAIXY TEST START
                                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                                // ADD CAIXY TEST END
                                jonsBody = null;
                                return;
                            }
                        } else
                            lvPDOrder.setAdapter(null);
                        // IniActivyMemor();
                        break;
                    }
                    // ADD BY WUQIONG 2015/04/27
                case 2: // 这是收发类别返回的地方
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {

                            tmprdCode = data.getStringExtra("Code");
                            tmprdIDA = data.getStringExtra("RdIDA");
                            tmprdIDB = data.getStringExtra("RdIDB");
                            tmprdName = data.getStringExtra("Name");
                            txtRdcl.setText(tmprdName);
                            SaveScanedHead();
                            txtTTransOutPos.requestFocus();
                        } else {
                            txtRdcl.setText("");
                        }
                        if (lvPDOrder.getAdapter().getCount() > 0) {
                            // txtRdcl.setEnabled(true);
                            // txtManualNo.setEnabled(true);
                            // btnRdcl.setEnabled(true);
                        } else {
                            txtRdcl.setText("");

                            txtManualNo.setText("");
                            // txtManualNo.setEnabled(false);
                            // btnRdcl.setEnabled(false);

                        }
                    }
                    // ADD BY WUQIONG 2015/04/27
                    break;
                default:
                    // 其它窗口的回传数据

            }
        } else if (requestCode == 96) {
            if (resultCode == 8) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        SerializableList ResultBodyList = new SerializableList();
                        ResultBodyList = (SerializableList) bundle
                                .get("SaveBodyList");
                        lstSaveBody = ResultBodyList.getList();
                        // add caixy s
                        ScanedBarcode = bundle
                                .getStringArrayList("ScanedBarcode");
                        // add caixy e

                        jonsBody = Common.jsonBodyTask;

                        // try {
                        // jonsBody = new
                        // JSONObject(bundle.getString("ScanTaskJson"));
                        // } catch (JSONException e) {
                        // // TODO Auto-generated catch block
                        // Toast.makeText(this, e.getMessage(),
                        // Toast.LENGTH_LONG).show();
                        // //ADD CAIXY TEST START
                        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // //ADD CAIXY TEST END
                        // e.printStackTrace();
                        // }
                        // ScanModTask剥离
                        // try {
                        // JsonModTaskData = new
                        // JSONObject(bundle.getString("ScanModTask"));
                        // } catch (JSONException e) {
                        // // TODO Auto-generated catch block
                        // Toast.makeText(this, e.getMessage(),
                        // Toast.LENGTH_LONG).show();
                        // //ADD CAIXY TEST START
                        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // //ADD CAIXY TEST END
                        // e.printStackTrace();
                        // }
                    }
                }
            }
        }
        // else if(requestCode==97)
        // {
        // switch (resultCode) {
        //
        // case 13://选择仓库返回
        //
        // if (data != null)
        // {
        // Bundle bundle = data.getExtras();
        // if (bundle != null)
        // {
        // warehouseCode =
        // bundle.getString("result2");// 得到子窗口ChildActivity的回传数据
        // warehouseID =
        // bundle.getString("result1");// 得到子窗口ChildActivity的回传数据
        // String name = bundle.getString("result3");// 得到子窗口ChildActivity的回传数据
        // txtWareHouse.setText(name);
        // }
        // }
        // break;
        // default:
        // //其它窗口的回传数据
        // //IniActivyMemor();
        // break;
        // }
        // }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 获得任务表体从调拨订单明细
    private void GetAdjustOrderBillBody(@NonNull String sBillIDA, @NonNull String sBillIDB)
            throws JSONException {
        if (sBillIDA.equals("") && sBillIDB.equals(""))
            return;

        JSONObject paraA = new JSONObject();
        JSONObject paraB = new JSONObject();

        tmpposCode = "";
        tmpposName = "";
        tmpposIDA = "";
        tmpposIDB = "";
        txtTTransOutPos.setText(tmpposCode);

        if (!sBillIDA.equals("")) {
            paraA.put("FunctionName", "GetAdjustOrderBillBodyList");
            paraA.put("BillID", sBillIDA);
            paraA.put("AccID", "A");
            paraA.put("TableName", "TransBillBody");
        }
        if (!sBillIDB.equals("")) {
            paraB.put("FunctionName", "GetAdjustOrderBillBodyList");
            paraB.put("BillID", sBillIDB);
            paraB.put("AccID", "B");
            paraB.put("TableName", "TransBillBody");
        }

        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
                        .show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            JSONArray JsonArrNew = new JSONArray();// Json瘦身
            if (!sBillIDA.equals("")) {
                JSONObject revA = Common.DoHttpQuery(paraA, "CommonQuery", "");
                if (revA == null) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                if (!revA.has("Status")) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                if (revA.getBoolean("Status")) {
                    JSONArray JsonArrays = revA.getJSONArray("TransBillBody");// Json瘦身

                    if (JsonArrays.length() < 1) {
                        Toast.makeText(this, R.string.DiaoBoDingDanBuZhengQue, Toast.LENGTH_LONG)
                                .show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    for (int i = 0; i < JsonArrays.length(); i++) {
                        JSONObject jObj = new JSONObject();
                        jObj.put("accid", "A");
                        jObj.put("vbatch", ((JSONObject) JsonArrays.get(i))
                                .get("vbatch").toString());
                        jObj.put("crowno", ((JSONObject) JsonArrays.get(i))
                                .get("crowno").toString());
                        jObj.put("cfirstid", ((JSONObject) JsonArrays.get(i))
                                .get("cfirstid").toString());
                        jObj.put("cbillid", ((JSONObject) JsonArrays.get(i))
                                .get("cbillid").toString());
                        jObj.put("cfirstbid", ((JSONObject) JsonArrays.get(i))
                                .get("cfirstbid").toString());
                        jObj.put("cbill_bid", ((JSONObject) JsonArrays.get(i))
                                .get("cbill_bid").toString());
                        jObj.put(
                                "cfirsttypecode",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cfirsttypecode").toString());
                        jObj.put("ctypecode", ((JSONObject) JsonArrays.get(i))
                                .get("ctypecode").toString());
                        jObj.put(
                                "cquoteunitid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cquoteunitid").toString());
                        jObj.put(
                                "nquoteunitnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nquoteunitnum").toString());
                        jObj.put(
                                "nordershouldoutnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nordershouldoutnum").toString());
                        jObj.put(
                                "pk_arrivearea",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "pk_arrivearea").toString());
                        jObj.put("vcode",
                                ((JSONObject) JsonArrays.get(i)).get("vcode")
                                        .toString());
                        jObj.put("cinvbasid", ((JSONObject) JsonArrays.get(i))
                                .get("cinvbasid").toString());
                        jObj.put(
                                "norderoutnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "norderoutnum").toString());
                        jObj.put("invname", ((JSONObject) JsonArrays.get(i))
                                .get("invname").toString());
                        jObj.put("invcode", ((JSONObject) JsonArrays.get(i))
                                .get("invcode").toString());
                        jObj.put(
                                "nquoteunitrate",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nquoteunitrate").toString());
                        jObj.put("nnum",
                                ((JSONObject) JsonArrays.get(i)).get("nnum")
                                        .toString());
                        jObj.put("vbdef1", ((JSONObject) JsonArrays.get(i))
                                .get("vbdef1").toString());
                        JsonArrNew.put(jObj);
                    }
                } else {
                    Toast.makeText(this, R.string.HuoQuDiaoBoDingDanBiaoTiCuoWu, Toast.LENGTH_LONG)
                            .show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            }

            if (!sBillIDB.equals("")) {
                JSONObject revB = Common.DoHttpQuery(paraB, "CommonQuery", "");
                if (revB == null) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                if (!revB.has("Status")) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                if (revB.getBoolean("Status")) {
                    JSONArray JsonArrays = revB.getJSONArray("TransBillBody");// Json瘦身

                    if (JsonArrays.length() < 1) {
                        Toast.makeText(this, R.string.DiaoBoDingDanBuZhengQue, Toast.LENGTH_LONG)
                                .show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    for (int i = 0; i < JsonArrays.length(); i++) {
                        JSONObject jObj = new JSONObject();
                        jObj.put("accid", "B");
                        jObj.put("vbatch", ((JSONObject) JsonArrays.get(i))
                                .get("vbatch").toString());
                        jObj.put("crowno", ((JSONObject) JsonArrays.get(i))
                                .get("crowno").toString());
                        jObj.put("cfirstid", ((JSONObject) JsonArrays.get(i))
                                .get("cfirstid").toString());
                        jObj.put("cbillid", ((JSONObject) JsonArrays.get(i))
                                .get("cbillid").toString());
                        jObj.put("cfirstbid", ((JSONObject) JsonArrays.get(i))
                                .get("cfirstbid").toString());
                        jObj.put("cbill_bid", ((JSONObject) JsonArrays.get(i))
                                .get("cbill_bid").toString());
                        jObj.put(
                                "cfirsttypecode",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cfirsttypecode").toString());
                        jObj.put("ctypecode", ((JSONObject) JsonArrays.get(i))
                                .get("ctypecode").toString());
                        jObj.put(
                                "cquoteunitid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cquoteunitid").toString());
                        jObj.put(
                                "nquoteunitnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nquoteunitnum").toString());
                        jObj.put(
                                "nordershouldoutnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nordershouldoutnum").toString());
                        jObj.put(
                                "pk_arrivearea",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "pk_arrivearea").toString());
                        jObj.put("vcode",
                                ((JSONObject) JsonArrays.get(i)).get("vcode")
                                        .toString());
                        jObj.put("cinvbasid", ((JSONObject) JsonArrays.get(i))
                                .get("cinvbasid").toString());
                        jObj.put(
                                "norderoutnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "norderoutnum").toString());
                        jObj.put("invname", ((JSONObject) JsonArrays.get(i))
                                .get("invname").toString());
                        jObj.put("invcode", ((JSONObject) JsonArrays.get(i))
                                .get("invcode").toString());
                        jObj.put(
                                "nquoteunitrate",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nquoteunitrate").toString());
                        jObj.put("nnum",
                                ((JSONObject) JsonArrays.get(i)).get("nnum")
                                        .toString());
                        jObj.put("vbdef1", ((JSONObject) JsonArrays.get(i))
                                .get("vbdef1").toString());
                        JsonArrNew.put(jObj);
                    }
                } else {
                    Toast.makeText(this, R.string.HuoQuDiaoBoDingDanBiaoTiCuoWu, Toast.LENGTH_LONG)
                            .show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            }
            jonsBody = new JSONObject();
            jonsBody.put("Status", true);
            jonsBody.put("TransBillBody", JsonArrNew);
            return;

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
    }

    @NonNull
    private OnKeyListener myTxtListener = new OnKeyListener() {
        @Override
        public boolean onKey(@NonNull View v, int arg1, @NonNull KeyEvent arg2) {
            switch (v.getId()) {
                case id.txtTPDOrder:
                    if (arg1 == arg2.KEYCODE_ENTER
                            && arg2.getAction() == KeyEvent.ACTION_UP)// &&
                    // arg2.getAction()
                    // ==
                    // KeyEvent.ACTION_DOWN
                    {
                        try {
                            if (lstSaveBody == null || lstSaveBody.size() < 1) {

                            } else {
                                Toast.makeText(StockTransContent.this,
                                        R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan,
                                        Toast.LENGTH_LONG).show();
                                // ADD CAIXY TEST START
                                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                                // ADD CAIXY TEST END
                                txtPDOrder.setText("");
                                break;
                            }
                            String lsBillCode = txtPDOrder.getText().toString();
                            FindOnlyBillHeadByBillId(lsBillCode);
                            txtPDOrder.setText("");
                            txtPDOrder.setFocusable(true);
                            txtPDOrder.setFocusableInTouchMode(true);
                            txtPDOrder.requestFocus();
                            txtPDOrder.requestFocusFromTouch();

                        } catch (ParseException e) {
                            Toast.makeText(StockTransContent.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();

                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                        } catch (JSONException e) {
                            Toast.makeText(StockTransContent.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();

                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                        }
                        return true;
                    }
                    break;
                case id.txtTTransOutPos:
                    if (arg1 == arg2.KEYCODE_ENTER
                            && arg2.getAction() == KeyEvent.ACTION_UP) {

                        if (lstPDOrder == null || lstPDOrder.size() < 1) {
                            txtTTransOutPos.setText("");
                            Toast.makeText(StockTransContent.this,
                                    "单据信息没有获得不能扫描货位", Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            break;
                        }

                        if (lstSaveBody == null || lstSaveBody.size() < 1) {

                        } else {
                            String OldPosName = tmpposCode;
                            Toast.makeText(StockTransContent.this,
                                    "该任务已经被扫描,无法修改货位。若要修改货位请先清除扫描明细",
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            txtTTransOutPos.setText(OldPosName);
                            txtPDOrder.requestFocus();
                            break;
                        }

                        // txtTTransOutPos.requestFocus();
                        try {
                            FindPositionByCode(txtTTransOutPos.getText().toString());
                            // txtTTransOutPos.setText(tmpposName);
                        } catch (ParseException e) {
                            txtTTransOutPos.setText("");
                            tmpposCode = "";
                            tmpposName = "";
                            tmpposIDA = "";
                            tmpposIDB = "";
                            txtTTransOutPos.requestFocus();
                            Toast.makeText(StockTransContent.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();

                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            e.printStackTrace();
                        } catch (JSONException e) {
                            txtTTransOutPos.setText("");
                            tmpposCode = "";
                            tmpposName = "";
                            tmpposIDA = "";
                            tmpposIDB = "";
                            txtTTransOutPos.requestFocus();
                            Toast.makeText(StockTransContent.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();

                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            e.printStackTrace();
                        }
                    }
                    break;
                // ADD BY WUQIONG START
                case id.txtRdcl:
                    if (arg1 == arg2.KEYCODE_ENTER
                            && arg2.getAction() == KeyEvent.ACTION_UP)// &&
                    // arg2.getAction()
                    // ==
                    // KeyEvent.ACTION_DOWN
                    {

                        String ScanCode = txtRdcl.getText().toString();
                        txtRdcl.setText("");
                        if (lstPDOrder == null || lstPDOrder.size() < 1) {
                            txtRdcl.setText("");
                            Toast.makeText(StockTransContent.this,
                                    R.string.DanJuXinXiMeiYouBuNengXuanZeShouFaLeiBie, Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return false;
                        }

                        try {
                            btnRdclClick(ScanCode);

                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(StockTransContent.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(StockTransContent.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            e.printStackTrace();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(StockTransContent.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            e.printStackTrace();
                        }

                        return true;
                    }
                    break;
                // ADD BY WUQIONG END
            }
            return false;
        }
    };

    /**
     * 找到货位ID按照货位号
     *
     * @param posCode 货位号
     */
    // private void FindPositionByCode(String posCode) throws JSONException
    // {
    // String lsCompanyCode = "";
    //
    // try
    // {
    // if(tmpAccID==null||tmpAccID.equals(""))
    // {
    // Toast.makeText(this, "仓库帐套还没有确认,不能先扫描货位", Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    // txtTTransOutPos.setText("");
    // tmpposCode = "";
    // tmpposName = "";
    // tmpposID = "";
    // return;
    // }
    //
    // if(tmpAccID.equals("A"))
    // {
    // //MainLogin.objLog.CompanyCode
    // lsCompanyCode="101";
    // }else
    // {
    // lsCompanyCode="1";
    // }
    //
    // posCode = posCode.trim();
    // posCode = posCode.replace("\n", "");
    // posCode = posCode.toUpperCase();
    //
    // JSONObject para = new JSONObject();
    // para.put("FunctionName", "GetBinCodeInfo");
    // para.put("CompanyCode", lsCompanyCode);
    // para.put("STOrgCode", MainLogin.objLog.STOrgCode);
    // para.put("WareHouse", wareHousePKFrom);
    // para.put("BinCode", posCode);
    // para.put("TableName", "position");
    // if(!MainLogin.getwifiinfo()) {
    // Toast.makeText(this, "WIFI信号差!请保持网络畅通",Toast.LENGTH_LONG).show();
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // return ;
    // }
    // JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);
    //
    // if(rev==null)
    // {
    // Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // return ;
    // }
    //
    //
    // if(rev.getBoolean("Status"))
    // {
    // JSONArray val = rev.getJSONArray("position");
    // if(val.length() < 1)
    // {
    // Toast.makeText(this, "获取货位失败", Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    // return;
    // }
    //
    // String jposName,jposCode,jposID;
    // JSONObject temp = val.getJSONObject(0);
    //
    // jposName = temp.getString("csname");
    // jposCode = temp.getString("cscode");
    // jposID = temp.getString("pk_cargdoc");
    //
    // tmpposCode = jposCode;
    // tmpposName = jposName;
    // tmpposID = jposID;
    // txtTTransOutPos.setText(tmpposName);
    // SaveScanedHead();
    // return;
    // }
    // else
    // {
    // Toast.makeText(this, "获取货位失败", Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    // txtTTransOutPos.setText("");
    // tmpposCode = "";
    // tmpposName = "";
    // tmpposID = "";
    // return;
    //
    // }
    //
    // }
    // catch (JSONException e) {
    //
    // Toast.makeText(this,
    // e.getMessage(), Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    // e.printStackTrace();
    // }
    // catch(Exception e)
    // {
    // Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    // }
    // }
    private void FindPositionByCode(String posCode) throws JSONException {
        // String lsCompanyCode = "";
        String jposName, jposCode, jposID;
        ReScanHead = "1";
        // 增加账套判断

        // if(tmpAccID.equals("A"))
        // {
        // //MainLogin.objLog.CompanyCode
        // lsCompanyCode="101";
        // }else
        // {
        // lsCompanyCode="1";
        // }
        if (tmpAccIDA.equals("") && tmpAccIDB.equals("")) {
            Toast.makeText(this, "仓库帐套还没有确认,不能先扫描货位", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            txtTTransOutPos.setText("");
            tmpposCode = "";
            tmpposName = "";
            tmpposIDA = "";
            tmpposIDB = "";
            return;
        }

        if (tmpAccIDA.equals("A")) {
            try {
                posCode = posCode.trim();
                posCode = posCode.replace("\n", "");
                posCode = posCode.toUpperCase();

                JSONObject para = new JSONObject();
                para.put("FunctionName", "GetBinCodeInfo");
                para.put("CompanyCode", "101");
                para.put("STOrgCode", MainLogin.objLog.STOrgCode);
                para.put("WareHouse", wareHousePKFromA);
                para.put("BinCode", posCode);
                para.put("TableName", "position");

                if (!MainLogin.getwifiinfo()) {
                    Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                JSONObject revA = Common.DoHttpQuery(para, "CommonQuery", "A");

                if (revA == null) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                if (!revA.has("Status")) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                if (revA.getBoolean("Status")) {
                    JSONArray valA = revA.getJSONArray("position");
                    if (valA.length() < 1) {
                        Toast.makeText(this, R.string.HuoQuAZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG)
                                .show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    JSONObject tempA = valA.getJSONObject(0);

                    jposName = tempA.getString("csname");
                    jposCode = tempA.getString("cscode");
                    jposID = tempA.getString("pk_cargdoc");

                    tmpposCode = jposCode;
                    tmpposName = jposName;
                    tmpposIDA = jposID;
                    txtTTransOutPos.setText(tmpposCode);

                    SaveScanedHead();

                } else {
                    Toast.makeText(this, R.string.HuoQuAZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    txtTTransOutPos.setText("");
                    tmpposCode = "";
                    tmpposName = "";
                    tmpposIDA = "";
                    return;

                }

            } catch (JSONException e) {

                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
            }
        }

        if (tmpAccIDB.equals("B")) {
            try {
                posCode = posCode.trim();
                posCode = posCode.replace("\n", "");
                posCode = posCode.toUpperCase();

                JSONObject para = new JSONObject();
                para.put("FunctionName", "GetBinCodeInfo");
                para.put("CompanyCode", "1");
                para.put("STOrgCode", MainLogin.objLog.STOrgCode);
                para.put("WareHouse", wareHousePKFromB);
                para.put("BinCode", posCode);
                para.put("TableName", "position");

                if (!MainLogin.getwifiinfo()) {
                    Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                JSONObject revB = Common.DoHttpQuery(para, "CommonQuery", "B");

                if (revB == null) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                if (!revB.has("Status")) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                if (revB.getBoolean("Status")) {
                    JSONArray valB = revB.getJSONArray("position");
                    if (valB.length() < 1) {
                        Toast.makeText(this, R.string.HuoQuBZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG)
                                .show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    // String jposName,jposCode;
                    JSONObject tempB = valB.getJSONObject(0);

                    jposName = tempB.getString("csname");
                    jposCode = tempB.getString("cscode");
                    jposID = tempB.getString("pk_cargdoc");

                    tmpposCode = jposCode;
                    tmpposName = jposName;
                    tmpposIDB = jposID;
                    // tmpposAccID = bar.AccID;
                    txtTTransOutPos.setText(tmpposCode);

                    SaveScanedHead();

                } else {
                    Toast.makeText(this, "获取B账套货位失败", Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    txtTTransOutPos.setText("");
                    tmpposCode = "";
                    tmpposName = "";
                    tmpposIDB = "";
                    return;

                }

            } catch (JSONException e) {

                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
            }
        }

    }

    // 手输调拨订单时，取得手输调拨订单表头
    private void FindOnlyBillHeadByBillId(String lsBillCode)
            throws JSONException {
        lsBillCode = lsBillCode.toUpperCase();
        lsBillCode = lsBillCode.replace("\n", "");
        if (lsBillCode.equals(""))
            return;

        String lsBillAccID = lsBillCode.substring(0, 1);
        lsBillCode = lsBillCode.substring(1);
        JSONObject para = new JSONObject();
        para.put("FunctionName", "GetAdjustOrderBillHeadOnlyByCode");
        para.put("BillCode", lsBillCode);
        para.put("TableName", "TransOnlyBillHead");
        try {
            Map<String, Object> map = null;
            JSONObject tempJso = null;
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
                        .show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery",
                    lsBillAccID);

            if (rev == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                        .show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            if (rev.getBoolean("Status")) {
                JSONArray jsarray = rev.getJSONArray("TransOnlyBillHead");
                if (jsarray.length() < 1) {
                    Toast.makeText(this, R.string.DiaoBoDingDanBuZhengQue, Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }

                // //保存用JSONObject设置---开始
                //
                // //表头---开始
                // //表头---结束
                // saveJons = new JSONObject();
                // saveJons.put("MultiBill", saveJonsMultiBill);
                //
                // //保存用JSONObject设置---结束

                for (int i = 0; i < jsarray.length(); i++) {
                    tempJso = jsarray.getJSONObject(i);
                    map = new HashMap<String, Object>();
                    map.put("No", tempJso.getString("vcode"));
                    map.put("From", tempJso.getString("cwarehousename")
                            + "     ");
                    map.put("To", tempJso.getString("cotherwhname"));
                    map.put("BillId", tempJso.getString("cbillid"));
                    map.put("AccID", tempJso.getString("AccID"));
                    map.put("OrgId", tempJso.getString("coutcbid"));
                    map.put("companyID", tempJso.getString("coutcorpid"));
                    map.put("warehouseID", tempJso.getString("coutwhid"));
                    map.put("warehouseToID", tempJso.getString("cinwhid"));

                    // 保存用表头JSONObject设置---开始
                    map.put("pk_corp", tempJso.getString("coutcorpid"));

                    if (tempJso.getString("AccID").equals("A"))
                        map.put("coperatorid", MainLogin.objLog.UserID);// 操作者
                    else
                        map.put("coperatorid", MainLogin.objLog.UserIDB);// 操作者

                    map.put("pk_calbody", tempJso.getString("coutcbid"));
                    map.put("pk_stordoc", tempJso.getString("coutwhid"));
                    map.put("fallocflag", tempJso.getString("fallocflag"));
                    map.put("cbiztypeid", tempJso.getString("cbiztypeid"));
                    map.put("pk_instordoc", tempJso.getString("cinwhid"));
                    map.put("pk_incalbody", tempJso.getString("cincbid"));
                    map.put("pk_incorp", tempJso.getString("cincorpid"));
                    map.put("vcode", tempJso.getString("vcode"));
                    // ADD BY WUQIONG START
                    map.put("vdef1", tempJso.getString("vdef1"));
                    // ADD BY WUQIONG END
                    if (tempJso.getString("coutcorpid").equals(
                            tempJso.getString("cincorpid"))) {
                        map.put("Dcorp", "  ");
                    } else {
                        map.put("Dcorp", "跨");
                    }
                    map.put("status", tempJso.getString("status"));

                    // 增加退
                    String billstatus = tempJso.getString("status");
                    if (billstatus.equals("Y")) {
                        map.put("statusE", "  ");
                    } else {
                        map.put("statusE", "退");
                    }

                    String lsAccid = map.get("AccID").toString();
                    String lsFromWH = map.get("warehouseID").toString();
                    String lspk_corp = map.get("pk_corp").toString();

                    if (!Common.CheckUserRole(lsAccid, lspk_corp, "40080820")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(StockTransContent.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!Common.CheckUserWHRole(lsAccid, lspk_corp, lsFromWH)) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(StockTransContent.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 保存用JSONObject设置---结束

                    for (int j = 0; j < lstPDOrder.size(); j++) {
                        Map<String, Object> ItemMap = lstPDOrder
                                .get(j);

                        if (map.get("BillId").toString()
                                .equals(ItemMap.get("BillId").toString())
                                && map.get("AccID")
                                .toString()
                                .equals(ItemMap.get("AccID").toString())) {
                            Toast.makeText(this, R.string.ShuRuDeDiaoBoDingDanHaoChongFu,
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }

                        if (!map.get("From")
                                .toString()
                                .replace(" ", "")
                                .equals(ItemMap.get("From").toString()
                                        .replace(" ", ""))
                                || !map.get("To")
                                .toString()
                                .replace(" ", "")
                                .equals(ItemMap.get("To").toString()
                                        .replace(" ", ""))) {
                            Toast.makeText(this, R.string.ShuRuDeDanJuHaoDeChuKuCangHu,
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }

                        if ((!map.get("pk_corp").toString()
                                .equals(ItemMap.get("pk_corp").toString()))
                                || (!map.get("pk_incorp")
                                .toString()
                                .equals(ItemMap.get("pk_incorp")
                                        .toString()))) {
                            Toast.makeText(this, R.string.ShuRuDeDanJuHaoDeChuKuCangHu,
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }

                        // 合并账套
                        // if(!map.get("AccID").toString().equals(ItemMap.get("AccID").toString()))
                        // {
                        // Toast.makeText(this, "输入的单据号的账套号和之前选择的不一致",
                        // Toast.LENGTH_LONG).show();
                        // //ADD CAIXY TEST START
                        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // //ADD CAIXY TEST END
                        // return;
                        // }

                        if (!map.get("status").toString()
                                .equals(ItemMap.get("status").toString())) {
                            Toast.makeText(this, "输入的单据号的正负结存和之前选择的不一致",
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }
                    }
                    lstPDOrder.add(map);
                }

                lvDBOrderAdapter = new SimpleAdapter(StockTransContent.this,
                        lstPDOrder, R.layout.vlistpds, from, to);
                lvPDOrder.setAdapter(lvDBOrderAdapter);

                // 通过调拨订单ID得到任务表体
                SetTransTaskParam();
                GetTaskCount();
                SaveScanedHead();
                return;
            } else {
                Toast.makeText(this, "获取调拨订单表头错误", Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
    }

    private void Exit() {

        ExitNameList = new String[2];
        ExitNameList[0] = "退出并保留缓存数据";
        ExitNameList[1] = "退出并删除缓存数据";

        SelectButton = new AlertDialog.Builder(this).setTitle(R.string.QueRenTuiChu)
                .setSingleChoiceItems(ExitNameList, -1, buttonOnClick)
                .setPositiveButton(R.string.QueRen, buttonOnClick)
                .setNegativeButton(R.string.QuXiao, buttonOnClick).show();
    }

    private class ButtonOnClick implements DialogInterface.OnClickListener {
        public int index;

        public ButtonOnClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(@NonNull DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton + 3;
                // dialog.cancel();
            } else {

                if (dialog.equals(SelectButton)) {
                    if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                        if (index == 3) {
                            finish();
                            System.gc();
                        } else if (index == 4) {
                            InitActiveMemor();
                            finish();
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

    // //退出按钮对话框事件
    // private DialogInterface.OnClickListener listenExit = new
    // DialogInterface.OnClickListener()
    // {
    // public void onClick(DialogInterface dialog,
    // int whichButton)
    // {
    // finish();
    // System.gc();
    // }
    // };
    //
    // //退出按钮
    // private void Exit()
    // {
    // AlertDialog.Builder bulider =
    // new AlertDialog.Builder(this).setTitle("询问").setMessage("你确认要退出吗?");
    // bulider.setNegativeButton(R.string.QuXiao, null);
    // bulider.setPositiveButton("确认", listenExit).create().show();
    // }

    // 全局变量赋值
    public static class Data {
        private static String fsTransType = "";

        public static String getDataTransType() {
            return fsTransType;
        }

        public static void setDataTransType(String sType) {
            fsTransType = sType;
        }
    }
}
