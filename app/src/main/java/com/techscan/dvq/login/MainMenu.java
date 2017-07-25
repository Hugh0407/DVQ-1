package com.techscan.dvq.login;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.Common;
import com.techscan.dvq.R;
import com.techscan.dvq.R.id;
import com.techscan.dvq.Sample_stocking;
import com.techscan.dvq.SearchmainActivity;
import com.techscan.dvq.StockBack;
import com.techscan.dvq.StockTransContent;
import com.techscan.dvq.StockTransContentIn;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.module.materialOut.MaterialOutAct;
import com.techscan.dvq.module.productIn.ProductInAct;
import com.techscan.dvq.module.purStockIn.PurStockIn;
import com.techscan.dvq.module.query.Query;
import com.techscan.dvq.module.statusChange.StatusChangeAct;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;

public class MainMenu extends Activity {

    //Button btnLogOut = null;
//    @InjectView(id.btn_xun)
//    Button mBtnXun;
//    @InjectView(id.btn_hu)
//    Button mBtnHu;
    private String[] BillTypeNameList = null;
    private String[] BillTypeCodeList = null;

    String fileName = null;
    String fileNameScan = null;
    String ScanedFileName = null;
    String isReturn;//是否退货的标志位，N 不退货，Y退货
    String UserID = null;
    File file = null;
    File fileScan = null;

    private static AlertDialog SelectLine = null;
    private buttonOnClickC buttonOnClickC = new buttonOnClickC(0);
    private buttonOnClickD buttonOnClickD = new buttonOnClickD(0);
    private AlertDialog SelectButton = null;
    static String[] LNameList = new String[2];
    GridView gridview = null;
    TextView tvLoginCompanyName = null;

    static AlertDialog alertDialog = null;
    static ProgressDialog PD = null;

    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioGroup RadioGroupType;

    // private SoundPool sp;// 声明一个SoundPool
    // private int MainLogin.music;// 定义一个int来设置suondID

    private void ShowSearchMain() {
        // SearchmainActivity search= new SearchmainActivity
        Intent search = new Intent(this, SearchmainActivity.class);
        startActivity(search);
    }

    // ADD CAIXY START
    private void ShowStockInventory() {
        // SearchmainActivity search= new SearchmainActivity
        Intent StockInventory = new Intent(this, com.techscan.dvq.StockInventory.class);
        startActivity(StockInventory);
    }

    private void ShowSearchBillActivity() {
        // SearchmainActivity search= new SearchmainActivity
        Intent SearchBillActivity = new Intent(this, com.techscan.dvq.SearchBillActivity.class);
        startActivity(SearchBillActivity);
    }

    private void ShowStockBack() {
        ShowLoading();
        // SearchmainActivity search= new SearchmainActivity
        Intent StockInventory = new Intent(this, StockBack.class);
        startActivity(StockInventory);
    }

    // ADD CAIXY END

    private void ShowStockMove() {
        ShowLoading();
        Intent StockMove = new Intent(this, com.techscan.dvq.StockMove.class);
        startActivity(StockMove);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.inject(this);

        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("主菜单");
        // actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setDisplayShowHomeEnabled(true);
        // Drawable TitleBar =
        // this.getResources().getDrawable(R.drawable.bg_barbackgroup);
        // actionBar.setBackgroundDrawable(TitleBar);
        // actionBar.show();
        alertDialog = new AlertDialog.Builder(this).create();
        // ADD CAIXY START
        // sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//
        // 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        // MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //
        // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        // // ADD CAIXY END
        RadioGroupType = (RadioGroup) findViewById(id.TypeBar);
        RadioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case id.radioButton1:
                        setMenuItem(1);
                        break;
                    case id.radioButton2:
                        setMenuItem(2);
                        break;
                    case id.radioButton3:
                        setMenuItem(3);
                        break;
                }
            }
        });
        radioButton1 = (RadioButton) findViewById(id.radioButton1);
        radioButton2 = (RadioButton) findViewById(id.radioButton2);
        radioButton3 = (RadioButton) findViewById(id.radioButton3);

        radioButton1.setSelected(true);

        tvLoginCompanyName = (TextView) findViewById(id.tvLoginCompanyName);
        String sCompanyName = MainLogin.objLog.CompanyName;
        tvLoginCompanyName.setText(" " + sCompanyName);
        gridview = (GridView) findViewById(id.gvMainMenu);
        //btnLogOut = (Button) findViewById(id.btnLogOut);
        //btnLogOut.setOnClickListener(ButtonOnClickListener);

        setMenuItem(1);
//        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
//
//        HashMap<String, Object> map;
//        if(radioButton1.isSelected()){
//            map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.icon_rk_cg);
//            map.put("ItemText", "采购入库");
//            lstImageItem.add(map);
//
//            //*********************************************************************
//            // by liuya 时间06.19
//            map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.icon_ck_cl);
//            map.put("ItemText", "材料出库");
//            lstImageItem.add(map);
//            //*********************************************************************
//
//            //*********************************************************************
//            // by liuya 时间06.27
//            map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.icon_rk_cp);
//            map.put("ItemText", "成品入库");
//            lstImageItem.add(map);
//            //*********************************************************************
//
//            map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.icon_ck_xs);
//            map.put("ItemText", "销售出库");
//            lstImageItem.add(map);
//        }
//        else if(radioButton2.isSelected()){
//            //*********************************************************************
//            // by walter 时间07.11
//            map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.ic_launcher);
//            map.put("ItemText", "采购退库");
//            lstImageItem.add(map);
//            //*********************************************************************
//
//            //*********************************************************************
//            // by liuya 时间07.06
//            map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.ic_launcher);
//            map.put("ItemText", "材料退库");
//            lstImageItem.add(map);
//            //*********************************************************************
//        }
//        else if(radioButton3.isSelected()) {
//            //*********************************************************************
//            // by liuya 时间06.27
//            map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.icon_xt_zh);
//            map.put("ItemText", "形态转换");
//            lstImageItem.add(map);
//            //*********************************************************************
//        }
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_cgdh);
//		map.put("ItemText", "单据查询");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_search);
//		map.put("ItemText", "存货查询");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_hwtz);
//		map.put("ItemText", "货位调整");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_dbck);
//		map.put("ItemText", "调拨出库");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_dbrk);
//		map.put("ItemText", "调拨入库");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_rk);
//		map.put("ItemText", "采购入库");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_xxck);
//		map.put("ItemText", "销售出库");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_zphc);
//		map.put("ItemText", "展品回仓");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_xxpd);
//		map.put("ItemText", "抽样盘点");
//		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_qtrc);
//		map.put("ItemText", "特殊业务");
//		lstImageItem.add(map);
//
////		map = new HashMap<String, Object>();
////		map.put("ItemImage", R.drawable.icon_pd);
////		map.put("ItemText", "库存盘点");
////		lstImageItem.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("ItemImage", R.drawable.icon_qchc);
//		map.put("ItemText", "清除缓存");
//		lstImageItem.add(map);

//        SimpleAdapter saImageItems = new SimpleAdapter(this,
//                lstImageItem,// 数据来源
//                R.layout.girdviewitem,
//                new String[]{"ItemImage", "ItemText"}, new int[]{
//                id.gvimgItem, id.gvtvItem});
//        gridview.setAdapter(saImageItems);
//        gridview.setOnItemClickListener(new ItemClickListener());

    }

    private void setMenuItem(int irbType) {
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map;

        switch (irbType) {
            case 1:
                map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.icon_rk_cg);
                map.put("ItemText", "采购入库");
                lstImageItem.add(map);

                //*********************************************************************
                // by liuya 时间06.19
                map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.icon_ck_cl);
                map.put("ItemText", "材料出库");
                lstImageItem.add(map);
                //*********************************************************************

                //*********************************************************************
                // by liuya 时间06.27
                map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.icon_rk_cp);
                map.put("ItemText", "成品入库");
                lstImageItem.add(map);
                //*********************************************************************

                map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.icon_ck_xs);
                map.put("ItemText", "销售出库");
                lstImageItem.add(map);

                map = new HashMap<String, Object>();
                lstImageItem.add(map);

                map = new HashMap<String, Object>();
                lstImageItem.add(map);

                break;

            case 2:
                //*********************************************************************
                // by walter 时间07.11
                map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.ic_launcher);
                map.put("ItemText", "采购退库");
                lstImageItem.add(map);
                //*********************************************************************

                //*********************************************************************
                // by liuya 时间07.06
                map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.ic_launcher);
                map.put("ItemText", "材料退库");
                lstImageItem.add(map);
                //*********************************************************************

                map = new HashMap<String, Object>();
                lstImageItem.add(map);

                break;

            case 3:
                //*********************************************************************
                // by liuya 时间06.27
                map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.icon_xt_zh);
                map.put("ItemText", "形态转换");
                lstImageItem.add(map);
                //*********************************************************************

                //*********************************************************************
                // by liuya 时间07.17
                map = new HashMap<String, Object>();
                map.put("ItemImage", R.drawable.icon_cx_all);
                map.put("ItemText", "查询");
                lstImageItem.add(map);
                //*********************************************************************

                map = new HashMap<String, Object>();
                lstImageItem.add(map);

                break;

        }
        SimpleAdapter saImageItems = new SimpleAdapter(this,
                lstImageItem,// 数据来源
                R.layout.girdviewitem,
                new String[]{"ItemImage", "ItemText"}, new int[]{
                id.gvimgItem, id.gvtvItem});
        gridview.setAdapter(saImageItems);
        gridview.setOnItemClickListener(new ItemClickListener());
    }

//    private OnClickListener ButtonOnClickListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) { // btnSDScanReturn
//                case id.btnLogOut:
//
//                    AlertDialog.Builder bulider = new AlertDialog.Builder(MainMenu.this).setTitle(R.string.XunWen).setMessage("是否确定退出系统");
//                    bulider.setNegativeButton(R.string.QuXiao, null);
//                    bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Common.lsUrl = MainLogin.objLog.LoginString;
//                            finish();
//                        }
//                    }).create().show();
//
//                    break;
//            }
//        }
//    };
    // private writeTxt writeTxt ;
    // private void UpdateLog()
    // {
    // String fileName = "/sdcard/DVQ/Log.txt";
    // writeTxt = new writeTxt();
    //
    // Date day=new Date();
    // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //
    // writeTxt.writeTxtToFile("log.txt","UPdate:" + df.format(day));
    // try
    // {
    //
    // File file = new File(fileName);
    //
    // if(file.exists())
    // {
    // file.delete();
    // }
    //
    // }
    //
    // catch (Exception e) {
    //
    // e.printStackTrace();
    // Toast.makeText(this, "未查询到保存记录", Toast.LENGTH_LONG).show();
    // //ADD CAIXY TEST START
    // //MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    // //ADD CAIXY TEST END
    // }
    // }

    public Context context;

    class ItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            switch (arg2) {
                // 权限CODE
                // 40080802 销售出库
                // 40081004 组装
                // 40081006 拆卸
                // 40081008 形态转换
                // 40081014 货位调整
                // 40081016 盘点
                // 40080618 调拨入库
                // 40080820 调拨出库
                // 40080602 采购入库

                case 0:
                    //采购入库
//del walter todo 20170615 权限判断暂时删除 ----->>>>>
//				if (!Common.CheckUserRole("", "", "40080602")) {
//					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					Toast.makeText(MainMenu.this, "没有使用该模块的权限",
//							Toast.LENGTH_LONG).show();
//					break;
//				}
//del walter todo 20170615 权限判断暂时删除 <<<<<-----
//                    ShowPurIn();
//                    break;
//				ShowSearchBillActivity();
//				break;
                    if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton1) {
                        //采购入库
                        ShowPurIn();
                    } else if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton2) {
                        //采购退库
                        Utils.showToast(MainMenu.this, "采购退库待加");
                    } else if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton3) {
                        //*********************************************************************
                        //形态转换模块  时间06.28  by liuya
                        ShowStatusChange();
                        //*********************************************************************
                    }
                    break;
                case 1:
                    //销售出库
//del walter todo 20170615 权限判断暂时删除 ----->>>>>
//				if (!Common.CheckUserRole("", "", "40080802")) {
//					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					Toast.makeText(MainMenu.this, "没有使用该模块的权限",
//							Toast.LENGTH_LONG).show();
//					break;
//				}
//del walter todo 20170615 权限判断暂时删除 <<<<<-----
//                    ShowSaleOut();
//                    break;
//				ShowSearchMain();
//				break;
                    if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton1) {
                        //*********************************************************************
                        //材料出库模块  时间06.19  by liuya
                        ShowMaterialIn();
                        //*********************************************************************
                    } else if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton2) {
                        //材料退
                        Utils.showToast(MainMenu.this, "材料退库待加");
                    } else if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton3) {
                        ShowQuery();
                    }
                    break;
                case 2:
                    if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton1) {
                        //*********************************************************************
                        //成品入库模块  时间06.27  by liuya
                        ShowProductOut();
                        //*********************************************************************
                    } else if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton2) {

                    } else if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton3) {
                    }
                    break;
//                    ShowMaterialIn();
//                    break;
//*********************************************************************
//                    if (!Common.CheckUserRole("", "", "40081014")) {
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        Toast.makeText(MainMenu.this, "没有使用该模块的权限",
//                                Toast.LENGTH_LONG).show();
//                        break;
//                    }
//                    ShowStockMove();
//*********************************************************************
                case 3:
//                    Utils.showToast(MainMenu.this, "待加");
//                    break;
                    if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton1) {
                        //销售出库
                        ShowSaleOut();
                    } else if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton2) {

                    } else if (RadioGroupType.getCheckedRadioButtonId() == id.radioButton3) {
                    }
                    break;


                case 4:
                    //ShowProductOut();
                    break;
                case 11:
                    if (!Common.CheckUserRole("", "", "40080820")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(MainMenu.this, "没有使用该模块的权限",
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    ShowStockTransOut();
                    break;
                case 12:
                    if (!Common.CheckUserRole("", "", "40080618")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(MainMenu.this, "没有使用该模块的权限",
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    ShowStockTransIn();
                    break;
                case 13:
                    if (!Common.CheckUserRole("", "", "40080602")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(MainMenu.this, "没有使用该模块的权限",
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    ShowPurIn();
                    break;
                case 6:
                    if (!Common.CheckUserRole("", "", "40080802")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(MainMenu.this, "没有使用该模块的权限",
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    ShowSaleOut();
                    break;
                case 7:
                    ShowStockBack();
                    break;
                case 8:
                    ShowSampleStock();
                    break;
                case 9:
                    // // 40081004 组装
                    // // 40081006 拆卸
                    // // 40081008 形态转换

                    if (!Common.CheckUserRole("", "", "40081004")
                            && !Common.CheckUserRole("", "", "40081006")
                            && !Common.CheckUserRole("", "", "40081008")) {
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        Toast.makeText(MainMenu.this, "没有使用该模块的权限",
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    ShowOther();
                    break;

//			case 10:
//				if (!Common.CheckUserRole("", "", "40081016")) {
//					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					Toast.makeText(MainMenu.this, "没有使用该模块的权限",
//							Toast.LENGTH_LONG).show();
//					break;
//				}
//				ShowStockInventory();
//				break;

//			case 11:
                case 10:
                    DelSavedFile();
                    break;

            }

        }

    }

    private void DelSavedFile() {

        int lsindex = 0;

        BillTypeNameList = new String[5];// 设置单据类型数量
        // 开始设置单据类型名字
        BillTypeNameList[0] = "货位调整";
        BillTypeNameList[1] = "调拨出库";
        BillTypeNameList[2] = "调拨入库";
        BillTypeNameList[3] = "采购入库";
        BillTypeNameList[4] = "展品回仓";

        BillTypeCodeList = new String[5];// 设置单据类型数量
        // 开始设置单据类型编码
        BillTypeCodeList[0] = "4Q";
        BillTypeCodeList[1] = "4Y";
        BillTypeCodeList[2] = "4E";
        BillTypeCodeList[3] = "45";
        BillTypeCodeList[4] = "TB";

        SelectButton = new AlertDialog.Builder(this).setTitle("删除缓存")
                .setSingleChoiceItems(BillTypeNameList, -1, buttonOnClickD)
                .setPositiveButton(R.string.QueRen, buttonOnClickD)
                .setNegativeButton(R.string.QuXiao, buttonOnClickD).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {// 拦截meu键事件 //do something...
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {// 拦截返回按钮事件 //do something...
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_exit) {
            //
            AlertDialog.Builder bulider = new AlertDialog.Builder(MainMenu.this).setTitle(R.string.XunWen).setMessage("是否确定退出系统");
            bulider.setNegativeButton(R.string.QuXiao, null);
            bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Common.lsUrl = MainLogin.objLog.LoginString;
                    MainLogin.objLog.UserID = "";
                    MainLogin.objLog.UserName = "";
                    MainLogin.objLog.LoginUser = "";
                    MainLogin.objLog.Password = "";
                    finish();
                }
            }).create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    private class buttonOnClickD implements DialogInterface.OnClickListener {
        public int index;

        public buttonOnClickD(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
            } else {

                if (dialog.equals(SelectButton)) {
                    if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                        String BillType = BillTypeCodeList[index].toString();

                        String BillName = BillTypeNameList[index].toString();

                        UserID = MainLogin.objLog.UserID;

                        fileName = "/sdcard/DVQ/" + BillType + UserID + ".txt";
                        fileNameScan = "/sdcard/DVQ/" + BillType + "Scan"
                                + UserID + ".txt";

                        file = new File(fileName);
                        fileScan = new File(fileNameScan);

                        if (file.exists()) {
                            file.delete();
                        }

                        if (fileScan.exists()) {
                            fileScan.delete();
                        }
                        ShowDelFile(BillName);


                        return;
                    } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                        return;
                    }
                }
            }
        }
    }

    private void ShowDelFile(String FileName) {


        AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
                R.string.ShanChuChengGong).setMessage(FileName + "模块的缓存文件删除成功");

        bulider.setPositiveButton(R.string.QueRen, null).setCancelable(false).create()
                .show();
        return;
    }

    // 显示调拨出库画面
    private void ShowStockTransOut() {
        ShowLoading();
        Intent StockTransOut = new Intent(this, StockTransContent.class);
        startActivity(StockTransOut);
    }

    // 显示调拨入库画面
    private void ShowStockTransIn() {
        ShowLoading();
        Intent StockTransIn = new Intent(this, StockTransContentIn.class);
        startActivity(StockTransIn);
    }

    // 显示材料出库画面
    private void ShowMaterialIn() {
        ShowLoading();
        Intent MaterialOut = new Intent(this, MaterialOutAct.class);
        startActivity(MaterialOut);
        cancelLoading();
    }

    // 显示材料出库画面
    private void ShowQuery() {
        ShowLoading();
        Intent query = new Intent(this, Query.class);
        startActivity(query);
        cancelLoading();
    }


    // 显示成品入库画面
    private void ShowProductOut() {
        ShowLoading();
        Intent ProductOut = new Intent(this, ProductInAct.class);
        startActivity(ProductOut);
        cancelLoading();
    }

    /**
     * 是否退货的dialog
     */
    private void showIsReturnDialog(final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setTitle("是否退货");
        builder.setItems(new String[]{"退货", "不退货"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 1:
                        isReturn = "Y";
                        break;
                    case 2:
                        isReturn = "N";
                        break;
                }
                intent.putExtra("isReturn", isReturn);
                startActivity(intent);
            }
        });
        builder.show();
    }

    // 显示形态转换画面
    private void ShowStatusChange() {
        ShowLoading();
        Intent statusChange = new Intent(this, StatusChangeAct.class);
        startActivity(statusChange);
        cancelLoading();
    }

    // ADD WUQ START
    private void ShowSaleOut() {
        // SearchmainActivity search= new SearchmainActivity
        Intent SalesDelivery = new Intent(this, com.techscan.dvq.SalesDelivery.class);
        startActivity(SalesDelivery);
    }

    // ADD WUQ START
    private void ShowOther() {
        // SearchmainActivity search= new SearchmainActivity
        Intent OtherStockInOut = new Intent(this, com.techscan.dvq.OtherStockInOut.class);
        startActivity(OtherStockInOut);
    }

    // ADD WUQ END

    private void ShowSampleStock() {
        // SearchmainActivity search= new SearchmainActivity
        Intent aaa = new Intent(this, Sample_stocking.class);
        startActivity(aaa);
    }

    // 显示采购入库画面
    private void ShowPurIn() {

        ShowLoading();
        Intent SaleIn = new Intent(this, PurStockIn.class);
        SaleIn.putExtra("freplenishflag", "N");
        startActivity(SaleIn);
    }

    private void ShowLoading() {
//		alertDialog.setTitle("数据加载中,请稍等。。。");
//		alertDialog.setMessage("");
//		alertDialog.show();
        PD = ProgressDialog.show(this, "Loading...", "数据加载中,请稍等。。。", true, false);
    }

    public static void cancelLoading() {

//		alertDialog.cancel();
        PD.dismiss();
    }

}
