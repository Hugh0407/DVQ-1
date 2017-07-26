package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

public class StockTransContentIn extends Activity {

    // private TextView tvTransOutBillCodeName = null;
    // private TextView tvTransInAccIDName = null;iaoti
    // private TextView tvTransInFromWareName = null;
    // private TextView tvTransInToWareName = null;
    // private TextView tvTransInFromCorpName = null;
    // private TextView tvTransInToCorpName = null;
    //

    // private TextView tvTransOutBillCode = null;
    // private TextView tvTransInAccID = null;
    // private TextView tvTransInFromWare = null;
    // private TextView tvTransInToWare = null;
    // private TextView tvTransInFromCorp = null;
    // private TextView tvTransInToCorp = null;

    String tmpWHStatus = "";
    String tmpBillStatus = "";

    String lsResultBillCodeA = "";
    String lsResultBillCodeB = "";
    String fileName = null;
    String fileNameScan = null;
    String ScanedFileName = null;
    String UserID = null;
    File file = null;
    File fileScan = null;
    String ReScanHead = "1";
    private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
    private AlertDialog SelectButton = null;
    private String[] ExitNameList = null;
    // 增加本地数据读取

    // private JSONObject JsonModTaskData = new JSONObject();
    String fsAccIDFlag = "";
    SimpleAdapter lvDBOrderAdapter;
    private EditText txtOutPDOrder = null;
    private EditText txtTTransInPos = null;

    // JSONArray saveJsonArrMulti = new JSONArray();

    private ImageButton btnTOutPDOrder = null;
    private Button btnTransInScan = null;
    private Button btnTransInSave = null;
    private Button btnTransInExit = null;

    // ADD BY WUQIONG 2015/04/24
    TextView tvOutRdcl;
    ImageButton btnOutRdcl1;
    EditText txtOutRdcl;
    TextView tvOutManualNo;
    EditText txtOutManualNo;
    String rdflag = "0";
    String tmprdInCode = "";
    String tmprdInName = "";
    String tmpInManualNo = "";
    String tmprdinIDA = "";
    String tmprdinIDB = "";
    String cgeneralhid = "";

    private ArrayList<String> ScanedBarcode = new ArrayList<String>();
    List<Map<String, Object>> lstPDOrder = null;
    ListView lvPDOrder;
    private AlertDialog DeleteAlertDialog = null;
    // String GetBillHFlg = "0";
    // String GetBillBFlg = "0";
    // ADD BY WUQIONG 2015/04/24

    // 增加退
    // String[] from = {"No","From", "To","AccID","Dcorp"};
    // int[] to = { R.id.listpdorder, R.id.listfromware, R.id.listtoware,
    // R.id.listaccid, R.id.listpddcorp};
    String[] from = {"No", "From", "To", "AccID", "Dcorp", "statusE"};
    int[] to = {R.id.listpdorder, R.id.listfromware, R.id.listtoware,
            R.id.listaccid, R.id.listpddcorp, R.id.listpdbillstatus};

    // ADD CAIXY TEST START
    // private SoundPool sp;//声明一个SoundPool
    // private int MainLogin.music;//定义一个int来设置suondID
    private writeTxt writeTxt; // 保存LOG文件
    String WhNameA = "";
    String WhNameB = "";
    String CompanyCode = "";// corpincode
    String OrgCode = "";
    String BillAccID = "";
    // String BillCorpPK="";
    public final static String PREFERENCE_SETTING = "Setting";
    int TaskCount = 0;
    // ADD CAIXY TEST END

    // 保存用表头信息
    private JSONObject jsonSaveHead = null;

    // 表体任务
    private JSONObject jsonBillBodyTask = null;

    private List<Map<String, Object>> lstSaveBody = null;

    private String tmpAccIDA = "";
    private String tmpAccIDB = "";
    private String tmpposCode = "";
    private String tmpposName = "";
    private String tmpposIDA = "";
    private String tmpposIDB = "";
    // private String tmpWarehousePK = "";
    // private String tmpCorpPKA = "";
    // private String tmpCorpPKB = "";
    // private String tmpBillCode = "";
    // String tmpBillID = "";
    String wareHousePKFromA = "";
    String wareHousePKToA = "";
    String wareHousePKFromB = "";
    String wareHousePKToB = "";
    String wareHouseNameFrom = "";
    String wareHouseNameTo = "";
    String PKcorpFrom = "";
    String PKcorpTo = "";

    // GUID
    private UUID uploadGuid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trans_content_in);

        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(R.string.DiaoBoRuKu);
        // Drawable TitleBar =
        // this.getResources().getDrawable(R.drawable.bg_barbackgroup);
        // actionBar.setBackgroundDrawable(TitleBar);
        // actionBar.show();

        // StrictMode.setThreadPolicy(new
        // StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
        // .detectNetwork()
        // .penaltyLog()
        // .build());
        //
        // StrictMode.setVmPolicy(new
        // StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
        // detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        //
        // tvTransOutBillCodeName =
        // (TextView)findViewById(R.id.tvTransOutBillCodeName);
        // tvTransInAccIDName = (TextView)findViewById(R.id.tvTransInAccIDName);
        // tvTransInFromWareName =
        // (TextView)findViewById(R.id.tvTransInFromWareName);
        // tvTransInToWareName =
        // (TextView)findViewById(R.id.tvTransInToWareName);
        // tvTransInFromCorpName =
        // (TextView)findViewById(R.id.tvTransInFromCorpName);
        // tvTransInToCorpName =
        // (TextView)findViewById(R.id.tvTransInToCorpName);
        //
        // tvTransOutBillCode = (TextView)findViewById(R.id.tvTransOutBillCode);
        // tvTransInAccID = (TextView)findViewById(R.id.tvTransInAccID);
        // tvTransInFromWare = (TextView)findViewById(R.id.tvTransInFromWare);
        // tvTransInToWare = (TextView)findViewById(R.id.tvTransInToWare);
        // tvTransInFromCorp = (TextView)findViewById(R.id.tvTransInFromCorp);
        // tvTransInToCorp = (TextView)findViewById(R.id.tvTransInToCorp);

        ClearBillDetailInfoShow();

        // 符合条件的调拨订单列表
        lvPDOrder = (ListView) findViewById(R.id.lvPDOrderIn);
        lstPDOrder = new ArrayList<Map<String, Object>>();
        // add caixy s //添加删除事件
        lvPDOrder.setOnItemLongClickListener(myListItemLongListener);
        // add caixy e

        txtOutPDOrder = (EditText) findViewById(R.id.txtOutPDOrder);//
        txtOutPDOrder.setOnKeyListener(EditTextOnKeyListener);
        txtOutPDOrder.requestFocus();

        btnTOutPDOrder = (ImageButton) findViewById(R.id.btnTOutPDOrder);
        btnTOutPDOrder.setOnClickListener(ButtonOnClickListener);
        btnTransInScan = (Button) findViewById(R.id.btnTransInScan);
        btnTransInScan.setOnClickListener(ButtonOnClickListener);
        btnTransInSave = (Button) findViewById(R.id.btnTransInSave);
        btnTransInSave.setOnClickListener(ButtonOnClickListener);
        btnTransInExit = (Button) findViewById(R.id.btnTransInExit);
        btnTransInExit.setOnClickListener(ButtonOnClickListener);

        txtTTransInPos = (EditText) findViewById(R.id.txtTTransInPos);
        txtTTransInPos.setOnKeyListener(EditTextOnKeyListener);

        txtOutPDOrder.setAllCaps(true);
        txtTTransInPos.setAllCaps(true);

        // ADD BY WUQIONG 2015/04/24
        txtOutRdcl = (EditText) findViewById(R.id.txtOutRdcl);
        txtOutRdcl.setOnKeyListener(EditTextOnKeyListener);

        txtOutRdcl.setFocusable(false);
        txtOutRdcl.setFocusableInTouchMode(false);

        tvOutRdcl = (TextView) findViewById(R.id.tvOutRdcl);
        btnOutRdcl1 = (ImageButton) findViewById(R.id.btnOutRdcl1);
        btnOutRdcl1.setOnClickListener(ButtonOnClickListener);

        txtOutManualNo = (EditText) findViewById(R.id.txtOutManualNo);
        txtOutManualNo.setOnKeyListener(EditTextOnKeyListener);
        tvOutManualNo = (TextView) findViewById(R.id.tvOutManualNo);
        // ADD BY WUQIONG 2015/04/24
        // add BY CAIXY S
        SharedPreferences sharedPreferences = getSharedPreferences(
                PREFERENCE_SETTING, Activity.MODE_PRIVATE);

        CompanyCode = sharedPreferences.getString("CompanyCode", "");
        OrgCode = sharedPreferences.getString("OrgCode", "");
        WhNameA = sharedPreferences.getString("WhCode", "");
        WhNameB = sharedPreferences.getString("AccId", "");
        // add BY CAIXY S

        // ADD CAIXY START
        // sp= new SoundPool(10, AudioManager.STREAM_SYSTEM,
        // 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        // MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1);
        // //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        // ADD CAIXY END

        jsonSaveHead = new JSONObject();

        // 增加本地数据读取
        UserID = MainLogin.objLog.UserID;
        // String LogName = BillType + UserID + dfd.format(day)+".txt";
        ScanedFileName = "4E" + UserID + ".txt";
        fileName = "/sdcard/DVQ/4E" + UserID + ".txt";
        fileNameScan = "/sdcard/DVQ/4EScan" + UserID + ".txt";

        file = new File(fileName);
        fileScan = new File(fileNameScan);

        btnTOutPDOrder.setFocusable(false);
        btnTransInScan.setFocusable(false);
        btnTransInSave.setFocusable(false);
        btnTransInExit.setFocusable(false);

        ReScanHead();
        MainMenu.cancelLoading();
        // 增加本地数据读取
    }

    private void ReScanErr() {
        AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
                R.string.CuoWu).setMessage("数据加载出现错误" + "\r\n" + "退出该模块并且再次尝试加载");

        bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false)
                .create().show();
        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        return;
    }

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

                        ScanBarCode(ScanedBillBar.get(i).toString());
                        String OKflg = "ng";
                        if (lstPDOrder == null || lstPDOrder.size() < 1) {
                            x++;
                            i--;
                        } else {
                            for (int j = 0; j < lstPDOrder.size(); j++) {
                                Map<String, Object> SelectedItemMap = (Map<String, Object>) lvPDOrder
                                        .getItemAtPosition(j);

                                String AAA = SelectedItemMap.get("AccID")
                                        .toString()
                                        + SelectedItemMap.get("pk_outcorp")
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

                this.tmprdInCode = ScanedRdClBar.get(0).toString();
                this.tmprdInName = ScanedRdClBar.get(1).toString();
                this.tmprdinIDA = ScanedRdClBar.get(2).toString();
                this.tmprdinIDB = ScanedRdClBar.get(3).toString();
                this.txtOutRdcl.setText(this.tmprdInName);

                this.tmpposCode = ScanedPosBar.get(0).toString();
                this.tmpposName = ScanedPosBar.get(1).toString();
                this.tmpposIDA = ScanedPosBar.get(2).toString();
                this.tmpposIDB = ScanedPosBar.get(3).toString();
                this.txtTTransInPos.setText(this.tmpposCode);

                txtOutManualNo.setText(ManualNo);

                int iBillCount = Integer.valueOf(BillCount.substring(0,
                        BillCount.length() - 2));

                if (iBillCount == lstPDOrder.size() && tmprdInCode != null
                        && !tmprdInCode.equals("")) {

                    GetWHPosStatus();

                    if (tmpWHStatus.equals("Y")) {
                        if (tmpposCode != null && !tmpposCode.equals("")) {
                            this.txtOutPDOrder.requestFocus();
                            ReScanHead = "1";
                            TransScan();
                        }

                    } else {
                        this.txtOutPDOrder.requestFocus();
                        ReScanHead = "1";
                        TransScan();
                    }

                }

                this.txtOutPDOrder.requestFocus();

                ReScanHead = "1";

            }
        } catch (Exception e) {

            e.printStackTrace();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        }

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
                        + SelectedItemMap.get("pk_outcorp").toString()
                        + SelectedItemMap.get("No").toString();
            else
                BillBarCode = BillBarCode
                        + SelectedItemMap.get("AccID").toString()
                        + SelectedItemMap.get("pk_outcorp").toString()
                        + SelectedItemMap.get("No").toString() + ",";
        }
        ScanedBillBar = BillBarCode;


        String saverdA = tmprdinIDA;

        String saverdB = tmprdinIDB;
        if (saverdA.equals("")) {
            saverdA = "null";
        }
        if (saverdB.equals("")) {
            saverdB = "null";
        }


        if (tmprdInCode == null || tmprdInCode.equals("")) {
            ScanedRdClBar = "null,null,null,null";
        } else {
            ScanedRdClBar = tmprdInCode + "," + tmprdInName + "," + saverdA + ","
                    + saverdB;
        }


//		if (tmprdInCode == null || tmprdInCode.equals("")) {
//			ScanedRdClBar = "null,null,null,null";
//		} else {
//			ScanedRdClBar = tmprdInCode + "," + tmprdInName + "," + tmprdinIDA
//					+ "," + tmprdinIDB;
//		}

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
                        + txtOutManualNo.getText().toString().toUpperCase()
                        .replace("|", "") + "|" + lstPDOrder.size());

    }

    private void GetWHPosStatus() throws JSONException {
        JSONObject para = new JSONObject();
        para.put("FunctionName", "GetWHPosStatus");
        // para.put("WareHouse", wareHousePKTo);

        para.put("FunctionName", "GetWHPosStatus");

        if (tmpAccIDA.equals("A")) {
            para.put("WareHouse", wareHousePKToA);
        } else {
            if (tmpAccIDB.equals("B")) {
                para.put("WareHouse", wareHousePKToB);
            }
        }

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        JSONObject rev = null;
        try {
            // rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);
            if (tmpAccIDA.equals("A")) {
                rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccIDA);
            } else {
                if (tmpAccIDB.equals("B")) {
                    rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccIDB);
                }
            }
        } catch (ParseException e) {

            Toast.makeText(StockTransContentIn.this, R.string.HuoQuCangKuZhuangTaiShiBai,
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        } catch (IOException e) {

            Toast.makeText(StockTransContentIn.this, R.string.HuoQuCangKuZhuangTaiShiBai,
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
                Toast.makeText(StockTransContentIn.this, R.string.HuoQuCangKuZhuangTaiShiBai,
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
            Toast.makeText(StockTransContentIn.this, R.string.HuoQuCangKuZhuangTaiShiBai,
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;

        }
    }

    private void TransScan() {
        // if(jsonSaveHead == null || jsonSaveHead.length() < 1)
        if (lstPDOrder == null || lstPDOrder.size() < 1) {
            Toast.makeText(StockTransContentIn.this, R.string.QingXiangQueRenXuYaoDeDingDanHao,
                    Toast.LENGTH_LONG).show();

            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        if (jsonBillBodyTask == null || jsonBillBodyTask.length() < 1) {
            Toast.makeText(StockTransContentIn.this, R.string.MeiYouDeDaoRenWuMingXi,
                    Toast.LENGTH_LONG).show();

            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        try {
            GetWHPosStatus();
        } catch (JSONException e) {
            Toast.makeText(StockTransContentIn.this, R.string.HuoQuCangKuZhuangTaiShiBai,
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
                Toast.makeText(StockTransContentIn.this, R.string.QingShuRuHuoWeiHao,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
        }

        if (tmprdInCode == null || tmprdInCode.equals("")) {
            Toast.makeText(StockTransContentIn.this, R.string.QingShuRuRuKuLeiBie,
                    Toast.LENGTH_LONG).show();

            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        SaveScanedHead();

        Intent intTransInScan = new Intent(StockTransContentIn.this,
                StockTransScanIn.class);
        // intTransInScan.putExtra("AccID",tmpAccID);
        intTransInScan.putExtra("AccIDA", tmpAccIDA);
        intTransInScan.putExtra("AccIDB", tmpAccIDB);

        // intTransInScan.putExtra("TaskJonsBody",jsonBillBodyTask.toString());
        Common.SetBodyTask(jsonBillBodyTask);

        // 剥离
        // intTransInScan.putExtra("ScanModTask",JsonModTaskData.toString());

        SerializableList lstScanSaveDetial = new SerializableList();
        lstScanSaveDetial.setList(lstSaveBody);
        intTransInScan.putExtra("lstScanSaveDetial", lstScanSaveDetial);

        // intTransInScan.putExtra("tmpWHStatus", tmpWHStatus);
        // intTransInScan.putExtra("CorpPK", tmpCorpPK);

        intTransInScan.putExtra("PKcorpFrom", PKcorpFrom);
        intTransInScan.putExtra("PKcorpTo", PKcorpTo);

        String sTaskCount = TaskCount + "";
        intTransInScan.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
        cgeneralhid = "";

        try {
            JSONArray JsonArrays = (JSONArray) jsonBillBodyTask.get("dbBody");
            cgeneralhid = ((JSONObject) (JsonArrays.get(0)))
                    .getString("cgeneralhid");
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        intTransInScan.putExtra("cgeneralhid", cgeneralhid);
        intTransInScan.putExtra("TaskCount", sTaskCount);

        // intTransInScan.putExtra("wareHousePKFrom", wareHousePKFrom);
        // intTransInScan.putExtra("wareHousePKTo", wareHousePKTo);

        intTransInScan.putExtra("wareHousePKFromA", wareHousePKFromA);
        intTransInScan.putExtra("wareHousePKToA", wareHousePKToA);

        intTransInScan.putExtra("wareHousePKFromB", wareHousePKFromB);
        intTransInScan.putExtra("wareHousePKToB", wareHousePKToB);

        if (tmpWHStatus.equals("Y")) {
            intTransInScan.putExtra("tmpposIDA", tmpposIDA);
            intTransInScan.putExtra("tmpposIDB", tmpposIDB);
        }
        intTransInScan.putExtra("tmpWHStatus", tmpWHStatus);

        intTransInScan.putExtra("tmpBillStatus", tmpBillStatus);

        startActivityForResult(intTransInScan, 86);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stock_trans_content_in, menu);
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

    // 增加本地数据读取
    private void Exit() {

        ExitNameList = new String[2];
        ExitNameList[0] = getString(R.string.TuiChuBingBaoLiuHuanCunShuJu);
        ExitNameList[1] = getString(R.string.TuiChuBingShanChuHuanCunShuJu);

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
        public void onClick(DialogInterface dialog, int whichButton) {
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

    // 增加本地数据读取

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Common.ReScanErr == true) {
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            ReScanErr();
            return;
        }

        if (requestCode == 98) {
            switch (resultCode) {
                case 1: // 这是调拨出库单单号列表返回的地方
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            SerializableList serializableList = (SerializableList) bundle
                                    .get("resultinfo");
                            List<Map<String, Object>> resultList = serializableList
                                    .getList();

                            lstPDOrder = resultList;
                            lvDBOrderAdapter = new SimpleAdapter(
                                    StockTransContentIn.this, resultList,
                                    R.layout.vlistpds, from, to);
                            lvPDOrder.setAdapter(lvDBOrderAdapter);

                            SetTransTaskParam();

                            // try {
                            // jsonSaveHead = Common.MapTOJSONOBject(mapBillInfo);
                            // } catch (JSONException e) {
                            // Toast.makeText(StockTransContentIn.this,
                            // e.getMessage(), Toast.LENGTH_LONG).show();
                            // e.printStackTrace();
                            // //ADD CAIXY TEST START
                            // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // //ADD CAIXY TEST END
                            // }
                            //
                            // //绑定显示订单信息
                            // BindingBillDetailInfo(mapBillInfo);
                            // //GetBillBFlg="1";
                            //
                            //
                            // GetBillBodyDetailInfo();
                            //
                            try {
                                GetTaskCount();
                                SaveScanedHead();
                            } catch (JSONException e) {
                                Toast.makeText(StockTransContentIn.this,
                                        e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                // ADD CAIXY TEST START
                                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                                // ADD CAIXY TEST END
                                return;
                            }

                        }
                    } else
                        lvPDOrder.setAdapter(null);
                    break;
                // ADD BY WUQIONG 2015/04/27

                default:
                    // 其它窗口的回传数据
                    // IniActivyMemor();
                    break;
            }
        } else if (requestCode == 88) {
            switch (resultCode) {

                case 2: // 这是收发类别返回的地方
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {

                            tmprdInCode = data.getStringExtra("Code");
                            tmprdinIDA = data.getStringExtra("RdIDA");
                            tmprdinIDB = data.getStringExtra("RdIDB");
                            tmprdInName = data.getStringExtra("Name");
                            txtOutRdcl.setText(tmprdInName);

                            SaveScanedHead();
                            txtTTransInPos.requestFocus();
                        } else {
                            txtOutRdcl.setText("");
                            txtOutRdcl.requestFocus();
                        }

                    }
                    // ADD BY WUQIONG 2015/04/27
                    break;
                default:
                    // 其它窗口的回传数据
                    // IniActivyMemor();
                    break;
            }
        } else if (requestCode == 86) {
            if (resultCode == 6) {
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

                        // jsonBillBodyTask = new
                        // JSONObject(bundle.getString("ScanTaskJson"));
                        jsonBillBodyTask = Common.jsonBodyTask;

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
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 通过调拨订单ID得到任务表体
    private void SetTransTaskParam() {
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

            PKcorpFrom = SelectedItemMap.get("pk_outcorp").toString();
            PKcorpTo = SelectedItemMap.get("pk_corp").toString();

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
            try {
                GetBillBodyDetailInfo(tmpBillIDA, tmpBillIDB);
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            }

        } catch (ParseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
        // for(int i=0;i<lstPDOrder.size();i++)
        // {
        // Map<String,Object> SelectedItemMap =
        // (Map<String,Object>)lvPDOrder.getItemAtPosition(i);
        // tmpAccID = SelectedItemMap.get("AccID").toString();
        // wareHousePKFrom = SelectedItemMap.get("warehouseID").toString();
        // wareHousePKTo = SelectedItemMap.get("warehouseToID").toString();
        // wareHouseNameFrom = SelectedItemMap.get("From").toString();
        // wareHouseNameTo = SelectedItemMap.get("To").toString();
        // tmpCorpPK = SelectedItemMap.get("pk_corp").toString();
        // if(i==lstPDOrder.size()-1)
        // tmpBillID = tmpBillID + "'" +
        // SelectedItemMap.get("BillId").toString() + "'";
        // else
        // tmpBillID = tmpBillID + "'" +
        // SelectedItemMap.get("BillId").toString() + "',";
        // }
        //
        // try
        // {
        // GetBillBodyDetailInfo(tmpBillID,tmpAccID);
        // } catch (ParseException e) {
        // Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // }
    }

    private Map<String, Object> GetBillDetailInfoByBillCode(String sAccID,
                                                            String sCorpPK, String sBillCode) {
        JSONObject para = new JSONObject();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            para.put("FunctionName", "GetAdjustOutBillHead");
            para.put("CorpPK", sCorpPK);
            para.put("BillCode", sBillCode);

        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            e2.printStackTrace();
            return null;
        }
        try {
            para.put("TableName", "dbHead");
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return null;
        }

        JSONObject jas;
        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
                        .show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return null;
            }
            jas = Common.DoHttpQuery(para, "CommonQuery", sAccID);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return null;
        }

        // 把取得的单据信息绑定到ListView上
        try {
            if (jas == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                        .show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return null;
            }
            if (!jas.has("Status")) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                        .show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return null;
            }

            if (!jas.getBoolean("Status")) {
                String errMsg = "";
                if (jas.has("ErrMsg")) {
                    errMsg = jas.getString("ErrMsg");
                } else {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return null;
            }

            // 绑定到map
            map = new HashMap<String, Object>();

            JSONArray jsarray = jas.getJSONArray("dbHead");

            JSONObject tempJso = jsarray.getJSONObject(0);

            map.put("No", tempJso.getString("vbillcode"));
            map.put("From", tempJso.getString("cwarehousename") + "     ");
            map.put("To", tempJso.getString("cotherwhname"));
            map.put("BillId", tempJso.getString("cgeneralhid"));
            map.put("AccID", sAccID);
            map.put("OrgId", tempJso.getString("cothercalbodyid"));
            map.put("companyID", tempJso.getString("cothercorpid"));
            map.put("warehouseID", tempJso.getString("cwarehouseid"));
            map.put("warehouseToID", tempJso.getString("cotherwhid"));

            // 保存用表头JSONObject设置---开始
            map.put("pk_corp", tempJso.getString("cothercorpid"));// 公司ID//caixy
            if (sAccID.equals("A"))
                map.put("coperatorid", MainLogin.objLog.UserID);// 操作者
            else
                map.put("coperatorid", MainLogin.objLog.UserIDB);// 操作者

            map.put("pk_calbody", tempJso.getString("cothercalbodyid"));// 调入库存组织
            map.put("pk_stordoc", tempJso.getString("cotherwhid"));// 调入仓库
            map.put("fallocflag", tempJso.getString("fallocflag"));// 调拨类型标志
            map.put("cbiztype", tempJso.getString("cbiztype"));// 业务类型ID
            map.put("pk_outstordoc", tempJso.getString("cwarehouseid"));// 调出仓库
            map.put("pk_outcalbody", tempJso.getString("pk_calbody"));// 调出库存组织
            map.put("pk_outcorp", tempJso.getString("pk_corp"));// 调出公司
            map.put("vcode", tempJso.getString("vbillcode"));// 单据号
            map.put("cgeneralhid", tempJso.getString("cgeneralhid"));// 单据ID
            // ADD BY WUQIONG END
            if (tempJso.getString("cothercorpid").equals(
                    tempJso.getString("pk_corp"))) {
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

            // 保存用表头JSONObject设置---结束
            // lstPDOrder.add(map);
            return map;

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return null;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return null;
        }
    }

    // 根据订单表头得到表体详细
    private void GetBillBodyDetailInfo(String sBillIDA, String sBillIDB)
            throws JSONException {
        if (sBillIDA.equals("") && sBillIDB.equals(""))
            return;

        JSONObject paraA = new JSONObject();
        JSONObject paraB = new JSONObject();

        tmpposCode = "";
        tmpposName = "";
        tmpposIDA = "";
        tmpposIDB = "";
        txtTTransInPos.setText(tmpposCode);
        if (!sBillIDA.equals("")) {
            paraA.put("FunctionName", "GetAdjustOutBillBody");
            paraA.put("BillCode", sBillIDA);
            paraA.put("CorpPK", PKcorpFrom);
            paraA.put("AccID", "A");
            paraA.put("TableName", "dbBody");
        }
        if (!sBillIDB.equals("")) {
            paraB.put("FunctionName", "GetAdjustOutBillBody");
            paraB.put("BillCode", sBillIDB);
            paraB.put("CorpPK", PKcorpFrom);
            paraB.put("AccID", "B");
            paraB.put("TableName", "dbBody");
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
                JSONObject revA = Common.DoHttpQuery(paraA, "CommonQuery", "A");
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
                    JSONArray JsonArrays = revA.getJSONArray("dbBody");// Json瘦身

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
                        jObj.put("vfree1", ((JSONObject) JsonArrays.get(i))
                                .get("vfree1").toString());
                        jObj.put("crowno", ((JSONObject) JsonArrays.get(i))
                                .get("crowno").toString());
                        jObj.put(
                                "cfirstbillhid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cfirstbillhid").toString());
                        jObj.put(
                                "cgeneralhid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cgeneralhid").toString());
                        jObj.put(
                                "cfirstbillbid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cfirstbillbid").toString());
                        jObj.put(
                                "cgeneralbid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cgeneralbid").toString());
                        jObj.put("cfirsttype", ((JSONObject) JsonArrays.get(i))
                                .get("cfirsttype").toString());
                        jObj.put(
                                "cbodybilltypecode",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cbodybilltypecode").toString());
                        jObj.put(
                                "cquoteunitid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cquoteunitid").toString());
                        jObj.put(
                                "nquoteunitrate",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nquoteunitrate").toString());
                        jObj.put(
                                "nquoteunitnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nquoteunitnum").toString());
                        jObj.put(
                                "nshouldoutnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nshouldoutnum").toString());
                        jObj.put(
                                "creceiveareaid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "creceiveareaid").toString());
                        jObj.put("vbillcode", ((JSONObject) JsonArrays.get(i))
                                .get("vbillcode").toString());
                        jObj.put("cinvbasid", ((JSONObject) JsonArrays.get(i))
                                .get("cinvbasid").toString());
                        jObj.put("vbatchcode", ((JSONObject) JsonArrays.get(i))
                                .get("vbatchcode").toString());
                        jObj.put("vuserdef1", ((JSONObject) JsonArrays.get(i))
                                .get("vuserdef1").toString());
                        jObj.put("vuserdef20", ((JSONObject) JsonArrays.get(i))
                                .get("vuserdef20").toString());
                        jObj.put("noutnum", ((JSONObject) JsonArrays.get(i))
                                .get("noutnum").toString());
                        jObj.put(
                                "ntranoutnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "ntranoutnum").toString());
                        jObj.put("invname", ((JSONObject) JsonArrays.get(i))
                                .get("invname").toString());
                        jObj.put("invcode", ((JSONObject) JsonArrays.get(i))
                                .get("invcode").toString());
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
                JSONObject revB = Common.DoHttpQuery(paraB, "CommonQuery", "B");
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
                    JSONArray JsonArrays = revB.getJSONArray("dbBody");// Json瘦身

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
                        jObj.put("vfree1", "");
                        jObj.put("crowno", ((JSONObject) JsonArrays.get(i))
                                .get("crowno").toString());
                        jObj.put(
                                "cfirstbillhid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cfirstbillhid").toString());
                        jObj.put(
                                "cgeneralhid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cgeneralhid").toString());
                        jObj.put(
                                "cfirstbillbid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cfirstbillbid").toString());
                        jObj.put(
                                "cgeneralbid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cgeneralbid").toString());
                        jObj.put("cfirsttype", ((JSONObject) JsonArrays.get(i))
                                .get("cfirsttype").toString());
                        jObj.put(
                                "cbodybilltypecode",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cbodybilltypecode").toString());
                        jObj.put(
                                "cquoteunitid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "cquoteunitid").toString());
                        jObj.put(
                                "nquoteunitrate",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nquoteunitrate").toString());
                        jObj.put(
                                "nquoteunitnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nquoteunitnum").toString());
                        jObj.put(
                                "nshouldoutnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "nshouldoutnum").toString());
                        jObj.put(
                                "creceiveareaid",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "creceiveareaid").toString());
                        jObj.put("vbillcode", ((JSONObject) JsonArrays.get(i))
                                .get("vbillcode").toString());
                        jObj.put("cinvbasid", ((JSONObject) JsonArrays.get(i))
                                .get("cinvbasid").toString());
                        jObj.put("vbatchcode", ((JSONObject) JsonArrays.get(i))
                                .get("vbatchcode").toString());
                        jObj.put("vuserdef1", ((JSONObject) JsonArrays.get(i))
                                .get("vuserdef1").toString());
                        jObj.put("vuserdef20", ((JSONObject) JsonArrays.get(i))
                                .get("vuserdef20").toString());
                        jObj.put("noutnum", ((JSONObject) JsonArrays.get(i))
                                .get("noutnum").toString());
                        jObj.put(
                                "ntranoutnum",
                                ((JSONObject) JsonArrays.get(i)).get(
                                        "ntranoutnum").toString());
                        jObj.put("invname", ((JSONObject) JsonArrays.get(i))
                                .get("invname").toString());
                        jObj.put("invcode", ((JSONObject) JsonArrays.get(i))
                                .get("invcode").toString());
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
            jsonBillBodyTask = new JSONObject();
            jsonBillBodyTask.put("Status", true);
            jsonBillBodyTask.put("dbBody", JsonArrNew);
            return;

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }

        // JSONObject para = new JSONObject();
        // Map<String,Object> mapBillBody = new HashMap<String,Object>();

        // try {
        // para.put("FunctionName", "GetAdjustOutBillBody");
        // para.put("CorpPK", tmpCorpPK);
        // para.put("BillCode", BillID);
        //
        // } catch (JSONException e2) {
        // Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // e2.printStackTrace();
        // return;
        // }
        // try {
        // para.put("TableName", "dbBody");
        // } catch (JSONException e2) {
        // Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // return;
        // }
        //
        // JSONObject jas;
        // try {
        // if(!MainLogin.getwifiinfo()) {
        // Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // return ;
        // }
        //
        // jas = Common.DoHttpQuery(para, "CommonQuery", AccID);
        // // txtOutManualNo.setEnabled(true);
        // // txtTTransInPos.setEnabled(true);
        // // txtOutRdcl.setEnabled(true);
        // // btnOutRdcl1.setEnabled(true);
        // } catch (Exception ex)
        // {
        // Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // return;
        // }
        // try
        // {
        // if(jas==null)
        // {
        // Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // return;
        // }
        // if(!jas.has("Status"))
        // {
        // Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // return;
        // }
        //
        //
        //
        // if(!jas.getBoolean("Status"))
        // {
        // Toast.makeText(this, jas.getString("ErrMsg"),
        // Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // return;
        // }
        //
        // jsonBillBodyTask = new JSONObject();
        //
        //
        // JSONArray JsonArrays = jas.getJSONArray("dbBody");//Json瘦身
        // JSONArray JsonArrNew = new JSONArray();//Json瘦身
        // if(JsonArrays.length() < 1)
        // {
        // Toast.makeText(this, "调拨订单不正确", Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // return;
        // }
        // //Toast.makeText(this, "调拨订单取得正确", Toast.LENGTH_LONG).show();
        //
        //
        // //Json瘦身
        // for(int i = 0;i<JsonArrays.length();i++)
        // {
        // JSONObject jObj = new JSONObject();
        //
        // jObj.put("vfree1",((JSONObject)JsonArrays.get(i)).get("vfree1").toString());
        // jObj.put("crowno",((JSONObject)JsonArrays.get(i)).get("crowno").toString());
        // jObj.put("cfirstbillhid",((JSONObject)JsonArrays.get(i)).get("cfirstbillhid").toString());
        // jObj.put("cgeneralhid",((JSONObject)JsonArrays.get(i)).get("cgeneralhid").toString());
        // jObj.put("cfirstbillbid",((JSONObject)JsonArrays.get(i)).get("cfirstbillbid").toString());
        // jObj.put("cgeneralbid",((JSONObject)JsonArrays.get(i)).get("cgeneralbid").toString());
        // jObj.put("cfirsttype",((JSONObject)JsonArrays.get(i)).get("cfirsttype").toString());
        // jObj.put("cbodybilltypecode",((JSONObject)JsonArrays.get(i)).get("cbodybilltypecode").toString());
        // jObj.put("cquoteunitid",((JSONObject)JsonArrays.get(i)).get("cquoteunitid").toString());
        // jObj.put("nquoteunitrate",((JSONObject)JsonArrays.get(i)).get("nquoteunitrate").toString());
        // jObj.put("nquoteunitnum",((JSONObject)JsonArrays.get(i)).get("nquoteunitnum").toString());
        // jObj.put("nshouldoutnum",((JSONObject)JsonArrays.get(i)).get("nshouldoutnum").toString());
        // jObj.put("creceiveareaid",((JSONObject)JsonArrays.get(i)).get("creceiveareaid").toString());
        // jObj.put("vbillcode",((JSONObject)JsonArrays.get(i)).get("vbillcode").toString());
        // jObj.put("cinvbasid",((JSONObject)JsonArrays.get(i)).get("cinvbasid").toString());
        // jObj.put("vbatchcode",((JSONObject)JsonArrays.get(i)).get("vbatchcode").toString());
        // jObj.put("vuserdef1",((JSONObject)JsonArrays.get(i)).get("vuserdef1").toString());
        // jObj.put("vuserdef20",((JSONObject)JsonArrays.get(i)).get("vuserdef20").toString());
        // jObj.put("noutnum",((JSONObject)JsonArrays.get(i)).get("noutnum").toString());
        // jObj.put("ntranoutnum",((JSONObject)JsonArrays.get(i)).get("ntranoutnum").toString());
        // jObj.put("invname",((JSONObject)JsonArrays.get(i)).get("invname").toString());
        // jObj.put("invcode",((JSONObject)JsonArrays.get(i)).get("invcode").toString());
        //
        // JsonArrNew.put(jObj);
        // }
        //
        // jsonBillBodyTask = new JSONObject();
        // jsonBillBodyTask.put("Status", true);
        // jsonBillBodyTask.put("dbBody", JsonArrNew);
        // //jsonBillBodyTask = jas;
        //
        //
        // }
        // catch (JSONException e)
        // {
        // e.printStackTrace();
        // Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // return;
        // }
        // catch (Exception ex)
        // {
        // Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // return;
        // }

    }

    // 绑定订单表头信息
    // private void BindingBillDetailInfo(Map<String,Object> mapBillInfo)
    // {
    // // tvTransOutBillCodeName.setText("单据号：");
    // // tvTransInAccIDName.setText("账套号：");
    // // tvTransInFromWareName.setText("转出仓库：");
    // // tvTransInToWareName.setText("转入仓库：");
    // // tvTransInFromCorpName.setText("转出公司：");
    // // tvTransInToCorpName.setText("转入公司：");
    // //
    // // tvTransOutBillCode.setText(mapBillInfo.get("BillCode").toString());
    // // tvTransInAccID.setText(mapBillInfo.get("AccID").toString());
    // // tvTransInFromWare.setText(mapBillInfo.get("WHOut").toString());
    // // tvTransInToWare.setText(mapBillInfo.get("WHIn").toString());
    // // tvTransInFromCorp.setText(mapBillInfo.get("CorpOut").toString());
    // // tvTransInToCorp.setText(mapBillInfo.get("CorpIn").toString());
    //
    // tmpAccID = mapBillInfo.get("AccID").toString();
    // //tmpWarehousePK = mapBillInfo.get("pk_stordoc").toString();
    // tmpCorpPK = mapBillInfo.get("pk_corp").toString();
    // tmpBillCode = mapBillInfo.get("BillCode").toString();
    // }

    // 清空订单表头信息
    private void ClearBillDetailInfoShow() {
        // tvTransOutBillCodeName.setText("");
        // tvTransInAccIDName.setText("");
        // tvTransInFromWareName.setText("");
        // tvTransInToWareName.setText("");
        // tvTransInFromCorpName.setText("");
        // tvTransInToCorpName.setText("");
        //
        // tvTransOutBillCode.setText("");
        // tvTransInAccID.setText("");
        // tvTransInFromWare.setText("");
        // tvTransInToWare.setText("");
        // tvTransInFromCorp.setText("");
        // tvTransInToCorp.setText("");

    }

    // 找到货位ID按照货位号
    private void FindPositionByCode(String posCode) throws JSONException {

        String jposName, jposCode, jposID;
        ReScanHead = "1";
        // 增加账套判断
        if (tmpAccIDA.equals("") && tmpAccIDB.equals("")) {
            Toast.makeText(this, R.string.CangKuZhangTaoHaiMeiYouQueRen, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            txtTTransInPos.setText("");
            tmpposCode = "";
            tmpposName = "";
            tmpposIDA = "";
            tmpposIDB = "";
            return;
        }

        // if(tmpAccID.equals("A"))
        // {
        // //MainLogin.objLog.CompanyCode
        // lsCompanyCode="101";
        // }else
        // {
        // lsCompanyCode="1";
        // }

        if (tmpAccIDA.equals("A")) {
            try {
                posCode = posCode.trim();
                posCode = posCode.replace("\n", "");
                posCode = posCode.toUpperCase();

                JSONObject para = new JSONObject();
                para.put("FunctionName", "GetBinCodeInfo");
                para.put("CompanyCode", PKcorpTo);
                // lsCompanyCode=tmpCorpPK;
                para.put("STOrgCode", MainLogin.objLog.STOrgCode);
                para.put("WareHouse", wareHousePKToA);
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
                    txtTTransInPos.setText(tmpposCode);

                    SaveScanedHead();

                } else {
                    Toast.makeText(this, R.string.HuoQuAZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    txtTTransInPos.setText("");
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
                para.put("WareHouse", wareHousePKToB);
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
                    txtTTransInPos.setText(tmpposCode);

                    SaveScanedHead();

                } else {
                    Toast.makeText(this, R.string.HuoQuBZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    txtTTransInPos.setText("");
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
        // String lsCompanyCode = "";
        //
        // try
        // {
        // if(tmpAccID==null || tmpAccID.equals(""))
        // {
        // Toast.makeText(this, "仓库帐套还没有确认,不能先扫描货位", Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // txtTTransInPos.setText("");
        // tmpposCode = "";
        // tmpposName = "";
        // tmpposID = "";
        // return;
        // }
        //
        //
        //
        // if(tmpAccID.equals("A"))
        // {
        // lsCompanyCode=tmpCorpPK;
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
        // para.put("WareHouse", wareHousePKTo);
        // para.put("BinCode", posCode);
        // para.put("TableName", "position");
        // if(!MainLogin.getwifiinfo()) {
        // Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
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
        // if(!rev.has("Status"))
        // {
        // Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // return;
        // }
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
        // txtTTransInPos.setText(tmpposName);
        //
        // SaveScanedHead();
        // //txtOutRdcl.requestFocus();
        // return;
        // }
        // else
        // {
        // Toast.makeText(this, "获取货位失败", Toast.LENGTH_LONG).show();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        // txtTTransInPos.setText("");
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
    }

    // 打开调拨订单列表画面
    private void btnPDOrderClick() throws ParseException, IOException,
            JSONException {

        List<Map<String, Object>> selectedlist = null;

        PdOrderMultilist cPdOrderMultilist = new PdOrderMultilist();
        cPdOrderMultilist.setInit(lvPDOrder.getAdapter());
        Intent ViewGrid = new Intent(this, cPdOrderMultilist.getClass());
        ViewGrid.putExtra("AccIDFlag", fsAccIDFlag);
        ViewGrid.putExtra("InOutFlag", "In");

        txtOutPDOrder.setText("");
        startActivityForResult(ViewGrid, 98);
    }

    // wuqiong
    // 打开收发类别画面
    private void btnOutRdcl1Click(String Code) throws ParseException,
            IOException, JSONException {
        Intent ViewGrid = new Intent(this, VlistRdcl.class);
        ViewGrid.putExtra("FunctionName", "GetRdcl");

        // ViewGrid.putExtra("AccID", "A");
        // ViewGrid.putExtra("rdflag", "1");
        // ViewGrid.putExtra("rdcode", "202");
        ViewGrid.putExtra("AccID", "");
        ViewGrid.putExtra("rdflag", rdflag);
        ViewGrid.putExtra("rdcode", "");
        startActivityForResult(ViewGrid, 88);
    }

    // wuqiong

    // 退出按钮
    // private void Exit()
    // {
    // AlertDialog.Builder bulider =
    // new AlertDialog.Builder(this).setTitle("询问").setMessage("你确认要退出吗?");
    // bulider.setNegativeButton("取消", null);
    // bulider.setPositiveButton("确认", listenExit).create().show();
    // }
    // add caixy s

    private void ScanBarCode(String lsBillCode) {
        if (lsBillCode.length() < 5) {
            Toast.makeText(StockTransContentIn.this, R.string.ShuRuDeDanJuHaoBuZhengQue,
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            txtOutPDOrder.setText("");
            return;
        }
        lsBillCode = lsBillCode.toUpperCase();
        lsBillCode = lsBillCode.replace("\n", "");
        if (lsBillCode.equals(""))
            return;
        String lsBillAccID = lsBillCode.substring(0, 1);
        String lsBillCorpPK = lsBillCode.substring(1, 5);
        lsBillCode = lsBillCode.substring(5);
        BillAccID = lsBillAccID;
        // BillCorpPK = lsBillCorpPK;
        Map<String, Object> mapBillInfo = GetBillDetailInfoByBillCode(
                lsBillAccID, lsBillCorpPK, lsBillCode);
        txtOutPDOrder.setText("");
        if (mapBillInfo == null) {
            Toast.makeText(StockTransContentIn.this, R.string.MeiYouZhaoDaoXiangDuiYingDeDanJuXiangXinXinXi,
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            txtOutPDOrder.requestFocus();
            return;
        }

        String lsAccid = mapBillInfo.get("AccID").toString();
        String lsFromWH = mapBillInfo.get("warehouseToID").toString();
        String lspk_corp = mapBillInfo.get("pk_corp").toString();

        if (!Common.CheckUserRole(lsAccid, lspk_corp, "40080618")) {
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            Toast.makeText(StockTransContentIn.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!Common.CheckUserWHRole(lsAccid, lspk_corp, lsFromWH)) {
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            Toast.makeText(StockTransContentIn.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian,
                    Toast.LENGTH_LONG).show();
            return;
        }

        for (int j = 0; j < lstPDOrder.size(); j++) {
            Map<String, Object> ItemMap = (Map<String, Object>) lstPDOrder
                    .get(j);
            if (mapBillInfo.get("BillId").toString()
                    .equals(ItemMap.get("BillId").toString())
                    && mapBillInfo.get("AccID").toString()
                    .equals(ItemMap.get("AccID").toString())) {
                Toast.makeText(StockTransContentIn.this, R.string.ShuRuDeDanJuHaoChongFu,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
            if (!mapBillInfo.get("From").toString().replace(" ", "")
                    .equals(ItemMap.get("From").toString().replace(" ", ""))
                    || !mapBillInfo
                    .get("To")
                    .toString()
                    .replace(" ", "")
                    .equals(ItemMap.get("To").toString()
                            .replace(" ", ""))) {
                Toast.makeText(StockTransContentIn.this,
                        R.string.ShuRuDeDanJuHaoDeChuKuCangHu, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START 选择的单据号的
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            if ((!mapBillInfo.get("pk_corp").toString()
                    .equals(ItemMap.get("pk_corp").toString()))
                    || (!mapBillInfo.get("pk_outcorp").toString()
                    .equals(ItemMap.get("pk_outcorp").toString()))) {
                Toast.makeText(StockTransContentIn.this,
                        R.string.ShuRuDeDanJuHaoDeChuKuCangHu, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            // if(!mapBillInfo.get("AccID").toString().equals(ItemMap.get("AccID").toString()))
            // {
            // Toast.makeText(StockTransContentIn.this, "输入的单据号的账套号和之前选择的不一致",
            // Toast.LENGTH_LONG).show();
            // //ADD CAIXY TEST START
            // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // //ADD CAIXY TEST END
            // return;
            // }

            if (!mapBillInfo.get("status").toString()
                    .equals(ItemMap.get("status").toString())) {
                Toast.makeText(this, "输入的的单据号的正负结存和之前选择的不一致", Toast.LENGTH_LONG)
                        .show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
        }
        lstPDOrder.add(mapBillInfo);

        // //绑定保存用表头
        // jsonSaveHead = new JSONObject();
        // try {
        // jsonSaveHead = Common.MapTOJSONOBject(mapBillInfo);
        // } catch (JSONException e) {
        // Toast.makeText(StockTransContentIn.this, e.getMessage(),
        // Toast.LENGTH_LONG).show();
        // e.printStackTrace();
        // //ADD CAIXY TEST START
        // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        // //ADD CAIXY TEST END
        //
        // }
        // 绑定显示订单信息
        // BindingBillDetailInfo(mapBillInfo);
        //
        //
        // GetBillBodyDetailInfo();

        lvDBOrderAdapter = new SimpleAdapter(StockTransContentIn.this,
                lstPDOrder, R.layout.vlistpds, from, to);
        lvPDOrder.setAdapter(lvDBOrderAdapter);

        // 通过调拨订单ID得到任务表体
        SetTransTaskParam();

        // txtTTransInPos.requestFocus();
        try {
            GetTaskCount();
            SaveScanedHead();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Toast.makeText(StockTransContentIn.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }

        return;

    }

    // 取得总任务数

    private void GetTaskCount() throws JSONException {
        TaskCount = 0;
        tmpWHStatus = "";
        tmpBillStatus = "";
        if (jsonBillBodyTask == null || jsonBillBodyTask.equals("")) {
            return;
        }

        if (jsonBillBodyTask.length() > 0) {
            JSONArray JsonArrays = new JSONArray();
            JsonArrays = (JSONArray) jsonBillBodyTask.get("dbBody");

            for (int i = 0; i < JsonArrays.length(); i++) {
                // TaskCount = TaskCount +
                // Integer.valueOf(((JSONObject)(JsonArrays.get(i))).getString("noutnum").toString());
                String nnum = ((JSONObject) (JsonArrays.get(i)))
                        .getString("noutnum");
                String ntranoutnum = ((JSONObject) (JsonArrays.get(i)))
                        .getString("ntranoutnum");
                // TaskCount = TaskCount +
                // Integer.valueOf(((JSONObject)(JsonArrays.get(i))).getString("nnum").toString());
                String snnum = "0";
                if (!ntranoutnum.equals("null")) {
                    snnum = (ntranoutnum.replaceAll("\\.0", ""));
                }

                int shouldinnum = Integer.valueOf(nnum)
                        - Integer.valueOf(snnum);

                TaskCount = TaskCount + shouldinnum;

                if (i == 0) {
                    Map localMap = (Map) this.lvPDOrder.getItemAtPosition(0);

                    tmpBillStatus = localMap.get("status").toString();

                    String ssAccID = localMap.get("AccID").toString();
                    String incorpid = localMap.get("pk_corp").toString();
                    String outcorpid = localMap.get("pk_outcorp").toString();
                    if ((ssAccID.equals("A")) && (!incorpid.equals(outcorpid))) {
                        this.tmprdInCode = "113";
                        this.tmprdInName = "跨公司调拨入库";
                        this.tmprdinIDA = "0001AA1000000000J55L";
                        this.txtOutRdcl.setText(this.tmprdInName);
                        txtTTransInPos.requestFocus();
                        // return;
                    }
                }
            }
        }

    }

    // add caixy e
    // add caixy e

    // 退出按钮对话框事件
    private DialogInterface.OnClickListener listenExit = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            Common.ReScanErr = false;
            finish();
            System.gc();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {// 拦截meu键事件 //do something...
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {// 拦截返回按钮事件 //do something...
            return false;
        }
        return true;
    }

    // EditText输入后回车的监听事件
    private OnKeyListener EditTextOnKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int arg1, KeyEvent arg2) {
            switch (v.getId()) {
                case id.txtOutPDOrder:
                    if (arg1 == arg2.KEYCODE_ENTER
                            && arg2.getAction() == KeyEvent.ACTION_UP)// &&
                    // arg2.getAction()
                    // ==
                    // KeyEvent.ACTION_DOWN
                    {

                        if (lstSaveBody == null || lstSaveBody.size() < 1) {

                        } else {
                            Toast.makeText(StockTransContentIn.this,
                                    R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan,
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            txtOutPDOrder.setText("");
                            break;
                        }
                        // GetBillBFlg="0";
                        String lsBillCode = txtOutPDOrder.getText().toString();

                        ScanBarCode(lsBillCode);
                        txtOutPDOrder.setText("");
                        txtOutPDOrder.setFocusable(true);
                        txtOutPDOrder.setFocusableInTouchMode(true);
                        txtOutPDOrder.requestFocus();
                        txtOutPDOrder.requestFocusFromTouch();
                        return true;

                    }
                    break;

                case id.txtTTransInPos:
                    if (arg1 == arg2.KEYCODE_ENTER
                            && arg2.getAction() == KeyEvent.ACTION_UP) {

                        if (lstPDOrder == null || lstPDOrder.size() < 1) {
                            txtTTransInPos.setText("");
                            Toast.makeText(StockTransContentIn.this,
                                    "单据信息没有获得不能扫描货位", Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            break;
                        }

                        if (lstSaveBody == null || lstSaveBody.size() < 1) {

                        } else {
                            String OldPosName = tmpposCode;
                            Toast.makeText(StockTransContentIn.this,
                                    "该任务已经被扫描,无法修改货位。若要修改货位请先清除扫描明细",
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            txtTTransInPos.setText(OldPosName);
                            txtOutPDOrder.requestFocus();
                            break;
                        }

                        try {
                            FindPositionByCode(txtTTransInPos.getText().toString());

                            return true;
                        } catch (ParseException e) {
                            txtTTransInPos.setText("");
                            tmpposCode = "";
                            tmpposName = "";
                            tmpposIDA = "";
                            tmpposIDB = "";
                            txtTTransInPos.requestFocus();
                            Toast.makeText(StockTransContentIn.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                        } catch (JSONException e) {
                            txtTTransInPos.setText("");
                            tmpposCode = "";
                            tmpposName = "";
                            tmpposIDA = "";
                            tmpposIDB = "";
                            txtTTransInPos.requestFocus();
                            Toast.makeText(StockTransContentIn.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                        }
                    }
                    break;
                // wuqiong
                case id.txtOutRdcl:
                    if (arg1 == arg2.KEYCODE_ENTER
                            && arg2.getAction() == KeyEvent.ACTION_UP)// &&
                    // arg2.getAction()
                    // ==
                    // KeyEvent.ACTION_DOWN
                    {

                        String ScanCode = txtOutRdcl.getText().toString();
                        txtOutRdcl.setText("");
                        // if(jsonSaveHead == null || jsonSaveHead.length() < 1)
                        if (lstPDOrder == null || lstPDOrder.size() < 1) {
                            txtOutRdcl.setText("");
                            Toast.makeText(StockTransContentIn.this,
                                    R.string.DanJuXinXiMeiYouBuNengXuanZeShouFaLeiBie, Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return false;
                        }

                        try {
                            btnOutRdcl1Click(ScanCode);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(StockTransContentIn.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(StockTransContentIn.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            e.printStackTrace();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(StockTransContentIn.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            e.printStackTrace();
                        }
                        return true;
                    }
                    break;
                // wuqiong
            }
            return false;
        }
    };

    // Button按下后的监听事件
    private OnClickListener ButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case id.btnTOutPDOrder:
                    if (lstSaveBody == null || lstSaveBody.size() < 1) {

                    } else {
                        Toast.makeText(StockTransContentIn.this,
                                R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG)
                                .show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        txtOutPDOrder.setText("");
                        break;
                    }

                    try {
                        btnPDOrderClick();
                    } catch (ParseException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (IOException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (JSONException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (Exception e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    }
                    break;
                // wuqiong
                case id.btnOutRdcl1:
                    try {
                        // if(jsonSaveHead == null || jsonSaveHead.length() < 1)

                        if (lstPDOrder == null || lstPDOrder.size() < 1) {
                            txtOutRdcl.setText("");
                            Toast.makeText(StockTransContentIn.this,"单据信息没有获得不能选择收发类别"
                                    , Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }

                        btnOutRdcl1Click("");
                    } catch (ParseException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (IOException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (JSONException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    } catch (Exception e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    }
                    break;
                // wuqiong

                case id.btnTransInScan:

                    TransScan();
                    break;
                case id.btnTransInSave:
                    try {
                        SaveTransData();
                    } catch (JSONException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END

                    } catch (ParseException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END

                    } catch (IOException e) {
                        Toast.makeText(StockTransContentIn.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END

                    }
                    break;
                case id.btnTransInExit:
                    Exit();
                    break;
            }
        }
    };

    // 保存数据
    private void SaveTransData() throws JSONException, ParseException,
            IOException {


        if (tmpWHStatus.equals("Y")) {
            if (tmpAccIDA.equals("A")) {
                if (tmpposIDA == null || tmpposIDA.equals("")) {
                    Toast.makeText(StockTransContentIn.this, R.string.QingShuRuHuoWeiHao,
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            }

            if (tmpAccIDB.equals("B")) {
                if (tmpposIDB == null || tmpposIDB.equals("")) {
                    Toast.makeText(StockTransContentIn.this, R.string.QingShuRuHuoWeiHao,
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            }
        }


        String lsResultBillCodeX = "";
        JSONObject saveJsonA = new JSONObject();
        JSONObject saveJsonB = new JSONObject();

        boolean SaveA = false;
        boolean SaveB = false;

        // JSONObject sendJsonSave = new JSONObject();
        // JSONArray sendJsonArrBody = new JSONArray();
        // JSONArray sendJsonArrHead = new JSONArray();
        // JSONArray sendJsonArrBodyLocation = new JSONArray();
        // Map<String,Object> sendMapHead = new HashMap<String,Object>();
        // Map<String,Object> sendMapBody = new HashMap<String,Object>();
        // //JSONObject saveJsonBodyLocation = new JSONObject();
        // saveJsonArrMulti = new JSONArray();

        if (lstPDOrder == null || lstPDOrder.size() < 1) {
            Toast.makeText(StockTransContentIn.this, R.string.WuKeBaoCunShuJu,
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        if (lstSaveBody == null || lstSaveBody.size() < 1) {
            Toast.makeText(StockTransContentIn.this, R.string.WuKeBaoCunShuJu,
                    Toast.LENGTH_LONG).show();

            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        if (tmpAccIDA.equals("A") && lsResultBillCodeA.equals("")) {
            JSONObject sendJsonSave = new JSONObject();
            JSONArray sendJsonArrBody = new JSONArray();
            JSONArray sendJsonArrHead = new JSONArray();
            JSONArray sendJsonArrBodyLocation = new JSONArray();
            Map<String, Object> sendMapHead = new HashMap<String, Object>();
            Map<String, Object> sendMapBody = new HashMap<String, Object>();
            // JSONObject saveJsonBodyLocation = new JSONObject();
            JSONArray saveJsonArrMulti = new JSONArray();

            for (int i = 0; i < lstPDOrder.size(); i++) {
                sendMapHead = (HashMap<String, Object>) lstPDOrder.get(i);
                JSONObject jsonSaveHead = new JSONObject();
                jsonSaveHead = Common.MapTOJSONOBject(sendMapHead);

                String LsAccID = jsonSaveHead.getString("AccID");
                if (LsAccID.equals("A")) {
                    sendJsonArrHead.put(jsonSaveHead);
                    sendJsonSave.put("Head", sendJsonArrHead);

                    for (int j = 0; j < lstSaveBody.size(); j++) {
                        sendMapBody = (HashMap<String, Object>) lstSaveBody
                                .get(j);

                        if (!sendMapBody.get("spacenum").toString().equals("1")) {
                            Toast.makeText(StockTransContentIn.this, R.string.YouWeiSaoWanDeFenBao,
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
                Toast.makeText(StockTransContentIn.this, R.string.WuKeBaoCunShuJu,
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

            // 获取收发类别ID
            saveJsonA.put("RdID", tmprdinIDA.toString());
            // 获取手工单号
            tmpInManualNo = txtOutManualNo.getText().toString().toUpperCase();
            saveJsonA.put("ManualNo", tmpInManualNo.toString());
        }

        if (tmpAccIDB.equals("B") && lsResultBillCodeB.equals("")) {
            JSONObject sendJsonSave = new JSONObject();
            JSONArray sendJsonArrBody = new JSONArray();
            JSONArray sendJsonArrHead = new JSONArray();
            JSONArray sendJsonArrBodyLocation = new JSONArray();
            Map<String, Object> sendMapHead = new HashMap<String, Object>();
            Map<String, Object> sendMapBody = new HashMap<String, Object>();
            // JSONObject saveJsonBodyLocation = new JSONObject();
            JSONArray saveJsonArrMulti = new JSONArray();

            for (int i = 0; i < lstPDOrder.size(); i++) {
                sendMapHead = (HashMap<String, Object>) lstPDOrder.get(i);
                JSONObject jsonSaveHead = new JSONObject();
                jsonSaveHead = Common.MapTOJSONOBject(sendMapHead);

                String LsAccID = jsonSaveHead.getString("AccID");
                if (LsAccID.equals("B")) {
                    sendJsonArrHead.put(jsonSaveHead);
                    sendJsonSave.put("Head", sendJsonArrHead);

                    for (int j = 0; j < lstSaveBody.size(); j++) {
                        sendMapBody = (HashMap<String, Object>) lstSaveBody
                                .get(j);

                        if (!sendMapBody.get("spacenum").toString().equals("1")) {
                            Toast.makeText(StockTransContentIn.this, R.string.YouWeiSaoWanDeFenBao,
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

            saveJsonArrMulti.put(sendJsonSave);

            if (saveJsonArrMulti == null || saveJsonArrMulti.length() < 1) {
                Toast.makeText(StockTransContentIn.this, R.string.WuKeBaoCunShuJu,
                        Toast.LENGTH_LONG).show();
                return;
            }

            // 执行保存
            saveJsonB = new JSONObject();
            saveJsonB.put("MultiTrans", saveJsonArrMulti);

            // 获取guid
            if (uploadGuid == null) {
                uploadGuid = UUID.randomUUID();
            }
            saveJsonB.put("GUIDS", uploadGuid.toString());
            saveJsonB.put("tmpWHStatus", tmpWHStatus);
            saveJsonB.put("tmpBillStatus", tmpBillStatus);

            // 获取收发类别ID
            saveJsonB.put("RdID", tmprdinIDB.toString());
            // 获取手工单号
            tmpInManualNo = txtOutManualNo.getText().toString().toUpperCase();
            saveJsonB.put("ManualNo", tmpInManualNo.toString());
        }

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }

        if (tmpAccIDA.equals("A") && lsResultBillCodeA.equals("")
                && SaveA == true) {
            JSONObject jasA = Common.DoHttpQuery(saveJsonA, "SaveAdjInBill",
                    tmpAccIDA);
            if (jasA == null) {
                Toast.makeText(StockTransContentIn.this,
                        R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            if (!jasA.has("Status")) {
                Toast.makeText(StockTransContentIn.this,
                        R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            boolean loginStatus = jasA.getBoolean("Status");

            if (loginStatus == true) {

                String lsResultBillCode = "";

                if (jasA.has("BillCode")) {
                    lsResultBillCodeA = jasA.getString("BillCode");
                    writeTxt = new writeTxt();

                    Date day = new Date();
                    SimpleDateFormat df = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");

                    SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");

                    // String BillCode = lsResultBillCode;
                    String BillType = "4E";
                    String LogName = BillType + UserID + dfd.format(day)
                            + ".txt";

                    String BillCode = lsResultBillCodeA;
                    String LogMsg = df.format(day) + " " + tmpAccIDA + " "
                            + BillCode;

                    writeTxt.writeTxtToFile(LogName, LogMsg);

                } else {
                    Toast.makeText(this,
                            R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi,
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            } else {
                String errMsg = "";
                if (jasA.has("ErrMsg")) {
                    errMsg = jasA.getString("ErrMsg");
                } else {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                // Toast.makeText(StockTransContentIn.this, ErrMsg,
                // Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (tmpAccIDB.equals("B") && lsResultBillCodeB.equals("")
                && SaveB == true) {
            JSONObject jasB = Common.DoHttpQuery(saveJsonB, "SaveAdjInBill",
                    tmpAccIDB);
            if (jasB == null) {
                Toast.makeText(StockTransContentIn.this,
                        R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            if (!jasB.has("Status")) {
                Toast.makeText(StockTransContentIn.this,
                        R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi,
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            boolean loginStatus = jasB.getBoolean("Status");

            if (loginStatus == true) {

                String lsResultBillCode = "";

                if (jasB.has("BillCode")) {
                    lsResultBillCodeB = jasB.getString("BillCode");

                    writeTxt = new writeTxt();

                    Date day = new Date();
                    SimpleDateFormat df = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");

                    SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");

                    // String BillCode = lsResultBillCode;
                    String BillType = "4E";
                    String LogName = BillType + UserID + dfd.format(day)
                            + ".txt";

                    String BillCode = lsResultBillCodeB;
                    String LogMsg = df.format(day) + " " + tmpAccIDB + " "
                            + BillCode;

                    writeTxt.writeTxtToFile(LogName, LogMsg);

                } else {
                    Toast.makeText(this,
                            R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi,
                            Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
            } else {
                String errMsg = "";
                if (jasB.has("ErrMsg")) {
                    errMsg = jasB.getString("ErrMsg");
                } else {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                // Toast.makeText(StockTransContentIn.this, ErrMsg,
                // Toast.LENGTH_LONG).show();
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

        uploadGuid = null;
        SimpleAdapter listItemAdapter = new SimpleAdapter(
                StockTransContentIn.this,
                lstResultBillCode,// 数据源
                android.R.layout.simple_list_item_1,
                new String[]{"BillCode"}, new int[]{android.R.id.text1});
        new AlertDialog.Builder(StockTransContentIn.this).setTitle(R.string.DanJuBaoCunChengGong)
                .setAdapter(listItemAdapter, null)
                .setPositiveButton(R.string.QueRen, null).show();

        // //写入log文件
        // writeTxt = new writeTxt();
        //
        // Date day=new Date();
        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //
        // SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
        //
        // //String BillCode = lsResultBillCode;
        // String BillType = "4E";
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

        // writeTxt.writeTxtToFile(LogName,LogMsg);
        // 写入log文件

        // 保存成功后初始化界面和内存数据
        InitActiveMemor();
        return;

    }

    // 初始化界面和内存数据
    private void InitActiveMemor() {
        // tvTransOutBillCodeName.setText("");
        // tvTransInAccIDName.setText("");
        // tvTransInFromWareName.setText("");
        // tvTransInToWareName.setText("");
        // tvTransInFromCorpName.setText("");
        // tvTransInToCorpName.setText("");
        //
        // tvTransOutBillCode.setText("");
        // tvTransInAccID.setText("");
        // tvTransInFromWare.setText("");
        // tvTransInToWare.setText("");
        // tvTransInFromCorp.setText("");
        // tvTransInToCorp.setText("");

        Common.ClearIntDate();
        txtOutPDOrder.setText("");
        txtTTransInPos.setText("");
        txtOutRdcl.setText("");
        txtOutManualNo.setText("");
        // JsonModTaskData = new JSONObject();
        tmpWHStatus = "";
        tmpBillStatus = "";
        tmpAccIDA = "";
        tmpAccIDB = "";
        tmpposCode = "";
        tmpposName = "";
        tmpposIDA = "";
        tmpposIDB = "";
        lsResultBillCodeA = "";
        lsResultBillCodeB = "";
        // tmpCorpPKA = "";
        // tmpCorpPKB = "";
        // tmpBillCode = "";
        // tmpBillIDA = "";
        // tmpBillIDB = "";
        wareHousePKFromA = "";
        wareHousePKToB = "";
        wareHousePKFromA = "";
        wareHousePKToB = "";
        wareHouseNameFrom = "";
        wareHouseNameTo = "";

        lstSaveBody = null;
        uploadGuid = null;
        jsonSaveHead = null;
        jsonBillBodyTask = null;

        lstSaveBody = null;
        lstPDOrder = new ArrayList<Map<String, Object>>();
        // saveJsonArrMulti = new JSONArray();
        jsonBillBodyTask = new JSONObject();
        jsonSaveHead = new JSONObject();
        ScanedBarcode = new ArrayList<String>();
        lvPDOrder.setAdapter(null);
        tmprdInCode = "";
        tmprdInName = "";
        tmprdinIDA = "";
        tmprdinIDB = "";
        cgeneralhid = "";
        tmpInManualNo = "";
        ScanedBarcode = new ArrayList<String>();

        if (file.exists()) {
            file.delete();
        }

        if (fileScan.exists()) {
            fileScan.delete();
        }

        txtOutPDOrder.requestFocus();

    }

    // add caixy s
    // 长按扫描详细，删除该条记录
    private OnItemLongClickListener myListItemLongListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
            Map<String, Object> mapCurrent = (Map<String, Object>) lstPDOrder
                    .get(arg2);

            String BillId = mapCurrent.get("BillId").toString();
            String BillCode = mapCurrent.get("No").toString();

            ButtonOnClickDelconfirm btnScanItemDelOnClick = new ButtonOnClickDelconfirm(
                    arg2, BillCode);
            DeleteAlertDialog = new AlertDialog.Builder(
                    StockTransContentIn.this).setTitle(R.string.QueRenShanChu)
                    .setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
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
                    Toast.makeText(StockTransContentIn.this, e.getMessage(),
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
            Toast.makeText(StockTransContentIn.this,
                    R.string.GaiRenWuYiJingBeiSaoMiaoWuFaShanChu, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        lstPDOrder.remove(iIndex);
        JSONArray JsonArrays = new JSONArray();
        JSONArray NewJsonArrays = new JSONArray();
        JsonArrays = (JSONArray) jsonBillBodyTask.get("dbBody");

        for (int i = 0; i < JsonArrays.length(); i++) {
            String vcode = ((JSONObject) (JsonArrays.get(i)))
                    .getString("vbillcode");
            if (!vcode.equals(BillCode)) {
                JSONObject jObj = new JSONObject();
                jObj = (JSONObject) (JsonArrays.get(i));
                NewJsonArrays.put(jObj);
            }

        }
        JsonArrays = NewJsonArrays;
        jsonBillBodyTask = new JSONObject();

        if (JsonArrays != null && JsonArrays.length() > 0) {
            jsonBillBodyTask.put("Status", true);
            jsonBillBodyTask.put("dbBody", JsonArrays);
        } else {
            InitActiveMemor();
        }

        lvPDOrder.setAdapter(lvDBOrderAdapter);
        GetTaskCount();
        SaveScanedHead();

    }
}
