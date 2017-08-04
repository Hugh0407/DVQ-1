package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StockInventoryScan extends Activity {

	Button btnStockInventoryScanDetail;
	
	Button btnSIScanClear;
	
	Button btnStockInventoryScanReturn;
	Button btnSIScanCheck;
    @Nullable
    private UUID uploadGuid =null;
	
	EditText tvSIScanInvName;
	EditText tvSIScanBatch;
	EditText tvSIScanSeriNo;
	EditText tvSIBillNO;
	
	TextView tvInvCount;
//	JSONObject saveJonsA;
//	JSONObject saveJonsB;

//	private String[] productAddressList = null;
//	private String[] productAddressNameList = null;
@Nullable
private AlertDialog SelectButton = null;


	//private JSONObject JsonAdds = new JSONObject();

	TextView tvSIScanInvNameView;
	TextView tvSIScanBatchName;
	TextView tvSIScanSeriNoName;

	//Switch swhSIWH;
	EditText txtSIWH;
    @Nullable
            List<Map<String, String>> m_mData      = null;
    @Nullable
    private AlertDialog               DeleteButton = null;

	String tmpposNameA = "";
	String tmpposCodeA = "";
	String tmpposIDA = "";
	
	String tmpposNameB = "";
	String tmpposCodeB = "";
	String tmpposIDB = "";

	String tmpAccIDA = "";
	String tmpAccIDB = "";
	
	String ScanMode = "";
    @NonNull
    private ButtonOnClick buttonOnClick    = new ButtonOnClick(0);
    @NonNull
    private ButtonOnClick buttonDelOnClick = new ButtonOnClick(0);
	//String ChoiceAddress = "0";

	EditText txtSIScanBarcode;

	String tmpAccID = "";
	
	String SameCount = "0";


    @Nullable
    private SplitBarcode bar = null; // 当前扫描条码解析

    @Nullable
    private Hashtable                 SerialValues = null;
    @Nullable
            List<Map<String, String>> jonsScan     = null;
	private ListView lvScanDetail;

//	private SoundPool sp;// 声明一个SoundPool
//	private int MainLogin.music;// 定义一个int来设置suondID
//	private int MainLogin.music2;//定义一个int来设置suondID

    @NonNull
    private List<Map<String, Object>> lstSaveBody = new ArrayList<Map<String, Object>>();
	String warehouseIDA = "";
	String warehouseIDB = "";
	String warehouseCodeA = "";
	String warehouseCodeB = "";
	String BillCodeA = "";
	String BillCodeB = "";
	String BillIdA = "";
	String BillIdB = "";
	String CompanyIDA = "";
	String CompanyIDB = "";
	// 扫描保存分包
    @NonNull
    private List<Map<String, Object>> lstScanBoxNum     = new ArrayList<Map<String, Object>>();
	// 定义是否删除Dialog
    @Nullable
    private AlertDialog               DeleteAlertDialog = null;

	// 判断该扫描是否数据本次扫描任务的帐套
	private String CheckGetAccID(@NonNull SplitBarcode bar) {
		String lsBarAccID = bar.AccID;

		if (tmpAccIDA.equals(lsBarAccID)) {

			
			tmpAccID = tmpAccIDA;
			return tmpAccID;
			// MOD BY WUQIONG E
		} else if (tmpAccIDB.equals(lsBarAccID)) {

			tmpAccID = tmpAccIDB;
			return tmpAccID;

		}



		return "";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		setContentView(R.layout.activity_stock_inventory_scan);
		this.setTitle("库存盘点扫描明细");
		txtSIScanBarcode = (EditText) findViewById(R.id.txtSIBarcode);
		txtSIScanBarcode.setOnKeyListener(myTxtListener);
		Intent intent = this.getIntent();
		

		
		String ScanDetail = this.getIntent().getStringExtra("ScanDetail");



		tvSIScanInvName = (EditText) findViewById(R.id.txtSIScanInvName);
		tvSIScanBatch = (EditText) findViewById(R.id.txtSIScanBatch);
		tvSIScanSeriNo = (EditText) findViewById(R.id.txtSIScanSeriNo);
		tvSIBillNO = (EditText) findViewById(R.id.txtSIBillNO);
		
		
		tvSIScanInvName.setFocusable(false);
		tvSIScanInvName.setFocusableInTouchMode(false);
		
		tvSIScanBatch.setFocusable(false);
		tvSIScanBatch.setFocusableInTouchMode(false);
		
		tvSIScanSeriNo.setFocusable(false);
		tvSIScanSeriNo.setFocusableInTouchMode(false);
		
		tvSIBillNO.setFocusable(false);
		tvSIBillNO.setFocusableInTouchMode(false);
		
		
		tvInvCount = (TextView) findViewById(R.id.tvInvCount);
		//tvSIBill = (TextView) findViewById(R.id.tvSIBill);
		
		tvSIScanInvNameView = (TextView) findViewById(R.id.tvSIScanInvNameView);
		tvSIScanBatchName = (TextView) findViewById(R.id.tvSIScanBatchName);
		tvSIScanSeriNoName = (TextView) findViewById(R.id.tvSIScanSeriNoName);
		
		btnSIScanCheck = (Button) findViewById(R.id.btnSIScanCheck);
		btnSIScanCheck.setOnClickListener(myListner);

		btnStockInventoryScanDetail = (Button) findViewById(R.id.btnSIScanDetail);
		btnStockInventoryScanDetail.setOnClickListener(myListner);
		
		btnSIScanClear = (Button) findViewById(R.id.btnSIScanClear);
		btnSIScanClear.setOnClickListener(myListner);

		btnStockInventoryScanReturn = (Button) findViewById(R.id.btnSIScanReturn);
		btnStockInventoryScanReturn.setOnClickListener(myListner);

//		swhSIWH = (Switch) findViewById(R.id.swhSIWH);
//		swhSIWH.setChecked(true);

		txtSIWH = (EditText) findViewById(R.id.txtSIWH);
		txtSIWH.setOnKeyListener(myTxtListener);
		
		btnStockInventoryScanDetail.setFocusable(false);
		btnSIScanClear.setFocusable(false);
		
		btnStockInventoryScanReturn.setFocusable(false);
		btnSIScanCheck.setFocusable(false);
		
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1);
		
		ScanMode = intent.getStringExtra("ScanMod");
		if(ScanMode.equals("ReScan"))
		{
			this.setTitle("库存盘点扫描明细(复核)");
			SameCount = intent.getStringExtra("SameCount");
			
		}
		ClearScanDetail();

		tmpAccIDA = intent.getStringExtra("AccIDA");
		tmpAccIDB = intent.getStringExtra("AccIDB");
		warehouseIDA = intent.getStringExtra("warehouseIDA");
		warehouseIDB = intent.getStringExtra("warehouseIDB");
		warehouseCodeA = intent.getStringExtra("warehouseCodeA");
		warehouseCodeB = intent.getStringExtra("warehouseCodeB");
		BillCodeA = intent.getStringExtra("vCodeA");
		BillCodeB = intent.getStringExtra("vCodeB");
		BillIdA = intent.getStringExtra("BillIdA");
		BillIdB = intent.getStringExtra("BillIdB");
		CompanyIDA = intent.getStringExtra("companyIDA");
		CompanyIDB = intent.getStringExtra("companyIDB");
		
		// ADD CAIXY END
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_inventory_scan, menu);
		return true;
	}

	// 扫描二维码解析功能函数
	private void ScanBarcode(@NonNull String barcode) throws JSONException,ParseException, IOException {
		if (barcode.equals("")) 
		{
			Toast.makeText(this, "请扫描条码", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			txtSIScanBarcode.requestFocus();
			return;
		}
		
		txtSIScanBarcode.setText("");

		if(tmpposIDA.equals("")&&tmpposIDB.equals(""))
		{
			Toast.makeText(this, "请先确认盘点货位", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			txtSIWH.requestFocus();
			return;
		}
		
		bar = new SplitBarcode(barcode);
		if (bar.creatorOk == false) {
			txtSIScanBarcode.setText("");
			txtSIScanBarcode.requestFocus();
			Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		
		CheckGetAccID(bar);
		
		if(!CheckHasScaned(bar))
		{
			txtSIScanBarcode.setText("");
			txtSIScanBarcode.requestFocus();
			Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		if (!tmpAccID.equals(bar.AccID)) {

			txtSIScanBarcode.setText("");
			txtSIScanBarcode.requestFocus();
			Toast.makeText(this, "扫描的条码不属于该任务,该货品不能够扫入",
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
		DataBindtoJons();
		BindingBillDetailInfo();

		txtSIScanBarcode.requestFocus();
		
		

	}

	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
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

    @NonNull
    private Button.OnClickListener myListner = new Button.OnClickListener() {
		@Override
		public void onClick(@NonNull View v) {
			switch (v.getId()) {
			case id.btnSIScanReturn:
				Done();
				break;
				
			case id.btnSIScanCheck: 
				SIScanCheck();
				break;
				
				
			case id.btnSIScanClear: 
				if(tmpposIDA.equals("")&&tmpposIDB.equals(""))
						return;
					
					ButtonOnClickClearconfirm btnScanItemClearOnClick =new ButtonOnClickClearconfirm();
					DeleteAlertDialog=new AlertDialog.Builder(StockInventoryScan.this).setTitle("确认清空")
							.setMessage("你确认要清空记录吗?")
							.setPositiveButton(R.string.QueRen, btnScanItemClearOnClick).setNegativeButton(R.string.QuXiao,null).show();
				
				
					break;
				
			case id.btnSIScanDetail:
				try {
					DoShowScanned();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}

		}
	};
	

	
	//清空扫描详细的监听事件
    private class ButtonOnClickClearconfirm implements DialogInterface.OnClickListener
    {	
		@Override
		public void onClick(DialogInterface dialog, int whichButton)
		{
			if(whichButton==DialogInterface.BUTTON_POSITIVE)
			{
				ClearScanDetail();
			}	
			else
			{
				return;
			}
		}
    }
	/**
	 * 显示扫描明细
	 */
    private void DoShowScanned() throws JSONException 
	{
		this.m_mData = jonsScan; 
		if (m_mData == null) 
		{
			return;
		}
		if (m_mData.size() <= 0) 
		{
			return;
		}
		
		SimpleAdapter listItemAdapter = new SimpleAdapter(
				this,
				m_mData,// 数据源
				R.layout.vlistsiscanitem,// ListItem的XML实现
			
				new String[] { "InvCode", "Batch", "AccID", "BarCode",
						"SeriNo", "BillCode",
						"isfinish","posName", "currentid" },
				new int[] { R.id.txtSIScanInvName, R.id.txtSIScanBatch,
						R.id.txtSIScanAccId, R.id.txtSINScanBarCode,
						R.id.txtSIScanSeriNo, R.id.txtSIScanBillCode,
						R.id.txtisfinish,R.id.txtSIPosName, });

		DeleteButton = new AlertDialog.Builder(this).setTitle(getString(R.string.SaoMiaoMingXiXinXi))
				.setSingleChoiceItems(listItemAdapter, 0, buttonDelOnClick)
				.setPositiveButton(R.string.QueRen, null).create();
		DeleteButton.getListView().setOnItemLongClickListener(
				new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {

						ConfirmDelItem(arg2);
						return false;
					}
				});
		DeleteButton.show();

	}

	/**
	 * 把扫描到的序列号,添加到明细中
	 */
	private void putcurrentIDToHashTable(String Key, String Value) {
		if (SerialValues == null) {
			SerialValues = new Hashtable();
			ArrayList array = new ArrayList();
			array.add(Value);
			SerialValues.put(Key, array);
		} else {
			if (SerialValues.containsKey(Key)) {
				ArrayList array = (ArrayList) SerialValues.get(Key);
				array.add(Value);
			} else {
				ArrayList array = new ArrayList();
				array.add(Value);
				SerialValues.put(Key, array);
			}
		}
	}

	

	// ADD BY WUQIONG S
	private void ConfirmDelItem(int index) {
		ButtonOnClickDelconfirm buttondel = new ButtonOnClickDelconfirm(index);
		SelectButton = new AlertDialog.Builder(this).setTitle(R.string.QueRenShanChu)
				.setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
				.setPositiveButton(R.string.QueRen, buttondel)
				.setNegativeButton(R.string.QuXiao, null).show();
	}

	private class ButtonOnClickDelconfirm implements
			DialogInterface.OnClickListener {
		public int index;

		public ButtonOnClickDelconfirm(int index) {
			this.index = index;
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			if (whichButton >= 0) {
				index = whichButton;
			} else {

				if (whichButton == DialogInterface.BUTTON_POSITIVE) {

					Map<String, String> map = m_mData.get(index);
					String barcode = map.get("BarCode");
					
					String identity = map.get("AccID") + map.get("InvCode") +
                            map.get("Batch") + map.get("SeriNo");
					for (int i = 0; i < jonsScan.size(); i++) 
					{
						if(barcode.equals(jonsScan.get(i).get("BarCode").toString()))
						{
							jonsScan.remove(i);
							SerialValues.remove(identity);
						}
					}
					//tvInvCount.setText("已扫描" +jonsScan.size()+ "件");
					if(ScanMode.equals("ReScan"))
					{
						tvInvCount.setText("需复核"+SameCount+"件|"+"已扫描" +jonsScan.size()+ "件");
					}
					else
					{
						tvInvCount.setText("已扫描" +jonsScan.size()+ "件");
					}
					DeleteButton.cancel();
					
				
				} else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
					return;
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
		public void onClick(@NonNull DialogInterface dialog, int whichButton) {
			if (whichButton >= 0) {
				index = whichButton;
				// dialog.cancel();
			} 
			else
			{
				 if (dialog.equals(DeleteButton)) 
				 {
					if (whichButton == DialogInterface.BUTTON_POSITIVE)
					{
						return;
					} 
					else if (whichButton == DialogInterface.BUTTON_NEGATIVE) 
					{
						// 这里进行数据删除处理
						ConfirmDelItem(index);
					}
				}
			}
		}
	}


	private void DataBindtoJons() throws JSONException {
		HashMap<String, String> obj = new HashMap<String, String>();
		if (jonsScan == null)
			jonsScan = new ArrayList<Map<String, String>>();
		
		String Identity = bar.AccID + bar.cInvCode
				+ bar.cBatch + bar.cSerino;
		
		putcurrentIDToHashTable(Identity, bar.currentBox);
		ArrayList array=(ArrayList)SerialValues.get(Identity);

		if(array.size()>1)
		{
			for(int i = 0; i < jonsScan.size(); i++)
			{
				String AccID = jonsScan.get(i).get("AccID").toString();
				String BarCode = jonsScan.get(i).get("BarCode").toString();

				if(bar.CheckBarCode.equals(BarCode)&&bar.AccID.equals(AccID))
				{
					jonsScan.remove(i);
					if( bar.AccID.equals("A"))
					{
						obj.put("WHCode", warehouseCodeA);// 仓库号
						obj.put("WHId", warehouseIDA);//仓库ID
						obj.put("BillId", BillIdA);
						obj.put("BillCode", BillCodeA);
						obj.put("posCode", tmpposCodeA);
						obj.put("posID", tmpposIDA);
						obj.put("posName", tmpposNameA);
						obj.put("CompanyID", CompanyIDA);
					}
					else if( bar.AccID.equals("B"))
					{
						obj.put("WHCode", warehouseCodeB);// 仓库号
						obj.put("WHId", warehouseIDB);//仓库ID
						obj.put("BillId", BillIdB);
						obj.put("BillCode", BillCodeB);
						obj.put("posCode", tmpposCodeB);
						obj.put("posID", tmpposIDB);
						obj.put("posName", tmpposNameB);
						obj.put("CompanyID", CompanyIDB);
					}
					if(array.size()==Integer.parseInt(bar.TotalBox.replaceFirst("^0*", "")))//这里来判断打包的是否全部扫描完毕,全部完毕为1,否则为零
					{
						obj.put("isfinish", "");
					}
					else
					{
						obj.put("isfinish", "分包未完");
					}
					obj.put("BarCode", bar.CheckBarCode);
					obj.put("InvCode", bar.cInvCode);
					obj.put("Batch", bar.cBatch);
					obj.put("SeriNo", bar.cSerino);
					obj.put("spacenum", "1");
					obj.put("AccID", bar.AccID);
					obj.put("TotalBox", bar.TotalBox.replaceFirst("^0*", ""));
					jonsScan.add(obj);
				}
			}
		}
		else
		{
			if( bar.AccID.equals("A"))
			{
				obj.put("WHCode", warehouseCodeA);// 仓库号
				obj.put("WHId", warehouseIDA);//仓库ID
				obj.put("BillId", BillIdA);
				obj.put("BillCode", BillCodeA);
				obj.put("posCode", tmpposCodeA);
				obj.put("posID", tmpposIDA);
				obj.put("posName", tmpposNameA);
				obj.put("CompanyID", CompanyIDA);
				
			}
			else if( bar.AccID.equals("B"))
			{
				obj.put("WHCode", warehouseCodeB);// 仓库号
				obj.put("WHId", warehouseIDB);//仓库ID
				obj.put("BillId", BillIdB);
				obj.put("BillCode", BillCodeB);
				obj.put("posCode", tmpposCodeB);
				obj.put("posID", tmpposIDB);
				obj.put("posName", tmpposNameB);
				obj.put("CompanyID", CompanyIDB);
			}
			if(array.size()==Integer.parseInt(bar.TotalBox.replaceFirst("^0*", "")))//这里来判断打包的是否全部扫描完毕,全部完毕为1,否则为零
			{
				obj.put("isfinish", "");
			}
			else
			{
				obj.put("isfinish", "分包未完");
			}
			obj.put("BarCode", bar.CheckBarCode);
			obj.put("InvCode", bar.cInvCode);
			obj.put("Batch", bar.cBatch);
			obj.put("SeriNo", bar.cSerino);
			obj.put("spacenum", "1");
			obj.put("AccID", bar.AccID);
			obj.put("TotalBox", bar.TotalBox.replaceFirst("^0*", ""));
			jonsScan.add(obj);
		}
		//tvInvCount.setText("已扫描"+jonsScan.size()+"件");
		if(ScanMode.equals("ReScan"))
		{
			tvInvCount.setText("需复核"+SameCount+"件|"+"已扫描"+jonsScan.size()+"件");
		}
		else
		{
			tvInvCount.setText("已扫描"+jonsScan.size()+"件");
		}

	}


	// 绑定订单表头信息
	private void BindingBillDetailInfo()
	{
		
		tvSIScanInvName.setText(bar.cInvCode);
		tvSIScanBatch.setText(bar.cBatch);
		tvSIScanSeriNo.setText(bar.cSerino);
		if(tmpAccID.equals("A"))
		{
			tvSIBillNO.setText("A "+BillCodeA);
		}
		else if(tmpAccID.equals("B"))
		{
			tvSIBillNO.setText("B "+BillCodeB);
		}
		
	}
	
	private boolean CheckHasScaned(@NonNull SplitBarcode bar)
	{
		String Identity =bar.AccID+bar.cInvCode+bar.cBatch+bar.cSerino;
		if(SerialValues!=null)
		{
			if(SerialValues.containsKey(Identity))
			{
			  ArrayList array=	(ArrayList)SerialValues.get(Identity);
			  for(int i=0;i< array.size();i++)
			  {
				  if(array.get(i).toString().equals(bar.currentBox))
				  {
					  return false;
				  }
			  }
			  
			}
		}
		return true;
	}

	// 清空订单表头信息
	private void ClearScanDetail() {
		
		uploadGuid=null;
		txtSIScanBarcode.setText("");
		tvSIScanInvName.setText("");
		tvSIScanBatch.setText("");
		tvSIScanSeriNo.setText("");
		tvSIBillNO.setText("");	
		txtSIWH.setText("");
		m_mData = null;
		tmpposNameA = "";
		tmpposCodeA = "";
		tmpposIDA = "";
		tmpposNameB = "";
		tmpposCodeB = "";
		tmpposIDB = "";
		tmpAccID = "";
		SerialValues = null;
		jonsScan = null;
		
		if(ScanMode.equals("ReScan"))
		{
			tvInvCount.setText("需复核"+SameCount+"件|"+"已扫描0件");
		}
		else
		{
			tvInvCount.setText("已扫描0件");
		}
		
		
		
		txtSIWH.setFocusableInTouchMode(true);
		txtSIWH.setFocusable(true);
		txtSIWH.requestFocus();
	}

	/**
	 * 找到货位ID按照货位号
	 * 
	 * @param posCode
	 *            货位号
	 */
	private void FindPositionByCode(String posCode) throws JSONException {
		//String lsCompanyCode = "";
		String jposName, jposCode, jposID;
		//增加账套判断
		
		if(tmpAccIDA.equals("A"))
		{
			try {
				posCode = posCode.trim();
				posCode = posCode.replace("\n", "");
				posCode = posCode.toUpperCase();

				JSONObject para = new JSONObject();
				para.put("FunctionName", "GetBinCodeInfo");
				para.put("CompanyCode", CompanyIDA);
				para.put("STOrgCode", MainLogin.objLog.STOrgCode);
				para.put("WareHouse", warehouseIDA);
				para.put("BinCode", posCode);
				para.put("TableName", "position");
				
				if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return ;
		        }

				JSONObject revA = Common.DoHttpQuery(para, "CommonQuery","A");
				
				if(revA==null)
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}

				if(!revA.has("Status"))
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}


				if (revA.getBoolean("Status")) {
					JSONArray valA = revA.getJSONArray("position");
					if (valA.length() < 1) {
						Toast.makeText(this, R.string.HuoQuAZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG).show();
						// ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						// ADD CAIXY TEST END
						return;
					}
					
					JSONObject tempA = valA.getJSONObject(0);

					jposName = tempA.getString("csname");
					jposCode = tempA.getString("cscode");
					jposID = tempA.getString("pk_cargdoc");

					tmpposCodeA = jposCode;
					tmpposNameA = jposCode;
					tmpposIDA = jposID;
					txtSIWH.setText(tmpposNameA);
				} else {
					Toast.makeText(this, R.string.HuoQuAZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					txtSIWH.setText("");
					tmpposCodeA = "";
					tmpposNameA = "";
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
		
		
		if(tmpAccIDB.equals("B"))
		{
			try {
				posCode = posCode.trim();
				posCode = posCode.replace("\n", "");
				posCode = posCode.toUpperCase();

				JSONObject para = new JSONObject();
				para.put("FunctionName", "GetBinCodeInfo");
				para.put("CompanyCode", CompanyIDB);
				para.put("STOrgCode", MainLogin.objLog.STOrgCode);
				para.put("WareHouse", warehouseIDB);
				para.put("BinCode", posCode);
				para.put("TableName", "position");
				
				if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return ;
		        }

				JSONObject revB = Common.DoHttpQuery(para, "CommonQuery",
						"B");
				
				if(revB==null)
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}

				if(!revB.has("Status"))
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}


				if (revB.getBoolean("Status")) {
					JSONArray valB = revB.getJSONArray("position");
					if (valB.length() < 1) {
						Toast.makeText(this, R.string.HuoQuBZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG).show();
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

					tmpposCodeB = jposCode;
					tmpposNameB = jposCode;
					tmpposIDB = jposID;
					//tmpposAccID = bar.AccID;
					txtSIWH.setText(tmpposNameB);
				} else {
					Toast.makeText(this, R.string.HuoQuBZhangTaoHuoWeiShiBai, Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					txtSIWH.setText("");
					tmpposCodeB = "";
					tmpposNameB = "";
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
		MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
		txtSIWH.setFocusable(false);
		txtSIWH.setFocusableInTouchMode(false);
		txtSIScanBarcode.requestFocus();
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {		if (keyCode == KeyEvent.KEYCODE_MENU) 
		 	{//拦截meu键事件			//do something...	
		       return false;
			 }
         return keyCode != KeyEvent.KEYCODE_BACK;
     }

	private void SIScanCheck() {
//		saveJonsA =new JSONObject();
//		saveJonsB =new JSONObject();
		
		if(jonsScan==null||jonsScan.size()==0)
		{
			
			Toast.makeText(StockInventoryScan.this,"没有需要提交的数据", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		
		for (int i = 0; i < jonsScan.size(); i++)
		{

			if (jonsScan.get(i).get("isfinish").equals("分包未完")) 
			{
				Toast.makeText(StockInventoryScan.this,"还有分包数据没有扫描完毕", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
		}
		
		if(jonsScan==null||jonsScan.equals(""))
		{
			Toast.makeText(StockInventoryScan.this,"没有扫描详细记录", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		else
		{
	        String scan= JTFH.ToJSONFMap(jonsScan, "ScanDetail");
	       
	        try {
				JSONObject ScanDetail=new JSONObject(scan);
				
				JSONArray JArray = ScanDetail.getJSONArray("ScanDetail");
				
				
				
				if(uploadGuid==null)
				{
				   uploadGuid = UUID.randomUUID();
				}

				ScanDetail.put("GUIDS", uploadGuid.toString());
				
				
				ScanDetail.put("UserIDA", MainLogin.objLog.UserID);
				
				ScanDetail.put("UserIDB", MainLogin.objLog.UserIDB);
				
				JSONObject jasA = null;
				JSONObject jasB = null;
				
				List JArrayA = new ArrayList();
				List JArrayB = new ArrayList();
				
				for(int i =0 ; i<JArray.length() ; i++)
				{
					JSONObject Jbody = JArray.getJSONObject(i);
					if (Jbody.get("AccID").equals("A"))
					{
						JArrayA.add(Jbody);
					}
					else if(Jbody.get("AccID").equals("B"))
					{
						JArrayB.add(Jbody);
					}
				}
				if (JArrayA.size()>0)
				{
					//saveJonsA.put("Body", JArrayA);
					try {
						if(!MainLogin.getwifiinfo()) {
				            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
				            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				            return ;
				        }
						
						jasA = Common.DoHttpQuery(ScanDetail, "CheckSTOCKINVENTORY", "A");

						if(jasA==null)
						{
							Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							return ;
						}
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
								
				if (JArrayB.size()>0)
				{
					//saveJonsB.put("Body", JArrayB);
					try {
						if(!MainLogin.getwifiinfo()) {
				            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
				            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				            return ;
				        }
						
						jasB = Common.DoHttpQuery(ScanDetail, "CheckSTOCKINVENTORY", "B");
						
						if(jasB==null)
						{
							Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							return ;
						}
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				

				
				
				if(JArrayA.size()>0&&JArrayB.size()>0)
				{
					String ErrMsg = "";
					if(jasA==null)
					{
						String ErrMsgA = BillCodeA +" ";
						ErrMsg = ErrMsg + ErrMsgA;
					}
					if(jasB==null)
					{
						String ErrMsgB = BillCodeB;
						ErrMsg = ErrMsg + ErrMsgB;
					}
					
					if (!ErrMsg.equals(""))
					{
						
						Toast.makeText(StockInventoryScan.this,ErrMsg+"提交失败,请尝试重新提交", Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						return;
					}
					
					if(jasA!=null&&jasB!=null)
					{
						boolean loginStatusA= jasA.getBoolean("Status");
						boolean loginStatusB= jasB.getBoolean("Status");
						if(loginStatusA==true&&loginStatusB==true)
						{
							JArrayA= new ArrayList();
							JArrayB= new ArrayList();
//							saveJonsA = new JSONObject();
//							saveJonsB = new JSONObject();
							jasA = new JSONObject();
							jasB = new JSONObject();
							//JsonAdds = new JSONObject();
							m_mData = new ArrayList();
						}
						else
						{
							String Errmsg = "";
							if(loginStatusA!=true)
							{
								Errmsg = Errmsg+jasA.get("ErrMsg").toString();
							}
							if(loginStatusB!=true)
							{
								Errmsg = Errmsg+jasB.get("ErrMsg").toString();
							}
							Toast.makeText(StockInventoryScan.this, Errmsg, Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
					}
				} 
				if(JArrayA.size()>0&&!(JArrayB.size()>0))
				{
					if(jasA==null)
					{
						Toast.makeText(StockInventoryScan.this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						return;
					}
					
					if(jasA!=null)
					{
						boolean loginStatusA= jasA.getBoolean("Status");
					    if(loginStatusA==true)
						{
							JArrayA= new ArrayList();
							//saveJonsA = new JSONObject();
							jasA = new JSONObject();
							//JsonAdds = new JSONObject();
							m_mData = new ArrayList();
						}
					    else
					    {
					    	
							Toast.makeText(StockInventoryScan.this, jasA.get("ErrMsg").toString(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
					    }
					}
				}
				if(!(JArrayA.size()>0)&&JArrayB.size()>0)
				{
					if(jasB==null)
					{
						Toast.makeText(StockInventoryScan.this, "提交失败,请尝试重新提交", Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						return;
					}
					if(jasB!=null)
					{
						boolean loginStatusB= jasB.getBoolean("Status");
						if(loginStatusB==true)
						{
							JArrayB= new ArrayList();
							//saveJonsB = new JSONObject();
							jasB = new JSONObject();
							//JsonAdds = new JSONObject();
							m_mData = new ArrayList();
						}
					    else
					    {
							Toast.makeText(StockInventoryScan.this, jasA.get("ErrMsg").toString(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
					    }
					}
				}
				
				//ClearScanDetail();

				Map<String,Object> mapResultBillCode = new HashMap<String,Object>();
				mapResultBillCode.put("BillCode", BillCodeA+"    "+BillCodeB);
				ArrayList<Map<String,Object>> lstResultBillCode = new ArrayList<Map<String,Object>>();
				lstResultBillCode.add(mapResultBillCode);
				
				SimpleAdapter listItemAdapter = new SimpleAdapter(StockInventoryScan.this,lstResultBillCode,//数据源   
		                android.R.layout.simple_list_item_1,       
		                new String[] {"BillCode"},
		                new int[] {android.R.id.text1}  
		            ); 
				new AlertDialog.Builder(StockInventoryScan.this).setTitle("单据提交成功")
								.setAdapter(listItemAdapter, null)
								.setPositiveButton(R.string.QueRen,null).show();
				
				ClearScanDetail();
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
		}
		
	}


    @NonNull
    private DialogInterface.OnClickListener listenExit = new
  			DialogInterface.OnClickListener()
  	{
  		public void onClick(DialogInterface dialog,
  			int whichButton)
  		{
  			finishScan();
  		}
  	};
  	
	private void finishScan() 
	{
		this.finish();
	}
  	
	private void Done() 
	{ 

		if (jonsScan==null||jonsScan.size()<1) 
		{

	 		if(ScanMode.equals("ReScan"))
			{
		 		 AlertDialog.Builder bulider = 
		  				 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("你确认要退出复核吗?");
		  		 bulider.setNegativeButton(R.string.QuXiao, null);
		  		 bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();
			}
	 		else
	 		{
	 			finishScan();// 
	 		}
			
		}
		else
		{
			Toast.makeText(StockInventoryScan.this, "还有未提交的数据,请先完成提交再返回", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
	}

	// 为listview自定义适配器内部类
	public static class MyListAdapterSI extends BaseAdapter {
        @Nullable
        private Context                   context     = null;
        @Nullable
        private LayoutInflater            inflater    = null;
        @Nullable
        private List<Map<String, Object>> list        = null;
        @Nullable
        private String                    keyString[] = null;

        @Nullable
        private int idValue[] = null;// id值

		public MyListAdapterSI(Context context, List<Map<String, Object>> list,
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
			// Map<String,Object> map = (Map<String,Object>)getItem(arg0);
			// if(!map.get("ScanedNum").toString().equals(map.get("TotalNum").toString()))
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
				if (!map.get("ScanedNum").toString()
						.equals(map.get("TotalNum").toString()))
					arg1.setBackgroundResource(R.color.lightpink);
				else
					arg1.setBackgroundColor(Color.TRANSPARENT);
			}

			return arg1;
		}

	}

    @NonNull
    private OnKeyListener myTxtListener = new OnKeyListener() {
		@Override
		public boolean onKey(@NonNull View v, int arg1, @NonNull KeyEvent arg2) {
			{
				switch (v.getId()) {
				case id.txtSIBarcode:
					if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)// KeyEvent.ACTION_DOWN
					{

						try {
							ScanBarcode(txtSIScanBarcode.getText().toString());
						} catch (ParseException e) {
							txtSIScanBarcode.setText("");
							txtSIScanBarcode.requestFocus();
							Toast.makeText(StockInventoryScan.this,
									e.getMessage(), Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							e.printStackTrace();
						} catch (JSONException e) {
							txtSIScanBarcode.setText("");
							txtSIScanBarcode.requestFocus();
							Toast.makeText(StockInventoryScan.this,
									e.getMessage(), Toast.LENGTH_LONG).show();
							e.printStackTrace();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						} catch (IOException e) {
							txtSIScanBarcode.setText("");
							txtSIScanBarcode.requestFocus();
							Toast.makeText(StockInventoryScan.this,
									e.getMessage(), Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							e.printStackTrace();
						}

						// 绑定显示订单信息
						return true;
					}

					break;

				// ADD BY WUQIONG S
				case id.txtSIWH:
					if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)// KeyEvent.ACTION_DOWN
						{
						txtSIWH.requestFocus();
						try {
							FindPositionByCode(txtSIWH.getText().toString());
							// txtTTransOutPos.setText(tmpposNameA);
						} catch (ParseException e) {
							txtSIWH.setText("");
							tmpposCodeA = "";
							tmpposNameA = "";
							tmpposIDA = "";
							//tmpposAccID = "";
							txtSIWH.requestFocus();
							Toast.makeText(StockInventoryScan.this,
									e.getMessage(), Toast.LENGTH_LONG).show();

							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							e.printStackTrace();
						} catch (JSONException e) {
							txtSIWH.setText("");
							tmpposCodeA = "";
							tmpposNameA = "";
							tmpposIDA = "";
							//tmpposAccID = "";
							txtSIWH.requestFocus();
							Toast.makeText(StockInventoryScan.this,
									e.getMessage(), Toast.LENGTH_LONG).show();

							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							e.printStackTrace();
						}
						return true;
					}
					break;
				// ADD BY WUQIONG E

				}
			}
			return false;
		}

	};

}
