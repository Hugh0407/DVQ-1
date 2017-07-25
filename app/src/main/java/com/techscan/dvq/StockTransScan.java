package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StockTransScan extends Activity {

	// String fileName = "/sdcard/DVQ/4YScan.txt";
	// String ScanedFileName = "4YScan.txt";

	String fileNameScan = null;
	String ScanedFileName = null;
	String UserID = null;
	File fileScan = null;

	String tmpWHStatus = "";

	String tmpBillStatus = "";

	String ReScanBody = "1";
	private writeTxt writeTxt; // 保存LOG文件

	private Button btnTransTask;
	private Button btnTransReturn;
	private Button btnTransScanClear;
	String ScanInvOK = "0";
	private EditText txtTSBarcode;
	private ListView lvScanDetail;
	// ADD BY WUQIONG START
	TextView tvscancount = null;
	int listcount = 0;
	int Tasknnum = 0;
	// ADD BY WUQIONG END

	// ADD CAIXY TEST START
	// private SoundPool sp;//声明一个SoundPool
	// private int MainLogin.music;//定义一个int来设置suondID
	// private int MainLogin.music2;//定义一个int来设置suondID
	private Integer ScanedQty;

	// ADD CAIXY TEST END
	// add caixy s
	private String OkFkg = "ng";

	private ArrayList<String> ScanedBarcode = new ArrayList<String>();
	// add caixy e
	// EditText txtTSAccId;
	// EditText txtTSInvCode;
	// EditText txtTSInvName;
	// EditText txtTSBatch;
	// EditText txtTSSeriNo;
	// EditText txtTSWH;
	// EditText txtTSPos;

	private String tmpAccIDA = "";
	private String tmpAccIDB = "";

	private JSONObject jonsHead; // 源头单据表头
	private JSONObject jonsBody; // 源头单据表体
	private List<Map<String, Object>> lstTaskBody = null;

	private List<Map<String, Object>> lstSaveBody = new ArrayList<Map<String, Object>>();
	private JSONArray jsonArrSaveBody = new JSONArray();
	private JSONObject jsonSaveBody = new JSONObject();
	private int iScanedNum = 0;
	private SplitBarcode bar = null; // 当前扫描条码解析
	private Inventory currentObj = null; // 当前扫描到的存货信息
	private Hashtable SerialValues = null;
	List<Map<String, String>> jonsScan;
	List<Map<String, String>> jonsSerialNo;
	// private String[] warehouseList = null;
	// private String[] warehouseNameList = null;
	// private String[] vFree1List =null;
	// private String[] OrgList =null;
	// private String[] companyIdList =null;

	private String tmpposIDA = "";
	private String tmpposIDB = "";
	private String wareHousePKFromA = "";
	private String wareHousePKToA = "";
	private String wareHousePKFromB = "";
	private String wareHousePKToB = "";
	private String wareHouseNameFrom = "";
	private String wareHouseNameTo = "";

	String PKcorpFrom = "";
	String PKcorpTo = "";

	private List<Map<String, Object>> lstScanDetail = new ArrayList<Map<String, Object>>();

	// 扫描保存分包
	private List<Map<String, Object>> lstScanBoxNum = new ArrayList<Map<String, Object>>();

	// 记录任务中扫描成功后的数据
	// private ArrayList jsonArrRemove = new ArrayList();

	// DEL BY WUQIONG S
	// 删除的任务内容临时保存住
	// private JSONObject JsonRemoveTaskData = new JSONObject();
	// DEL BY WUQIONG E

	// ADD BY WUQIONG START
	// 删除的任务内容临时保存住
	private JSONObject JsonModTaskData = new JSONObject();
	// ADD BY WUQIONG END

	// 定义是否删除Dialog
	private AlertDialog DeleteAlertDialog = null;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_trans_scan);

		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(R.string.DiaoBoChuKuSaoMiaoMingXi);
		// Drawable TitleBar =
		// this.getResources().getDrawable(R.drawable.bg_barbackgroup);
		// actionBar.setBackgroundDrawable(TitleBar);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.show();

		Intent myintent = getIntent();
		tmpAccIDA = myintent.getStringExtra("AccIDA");
		tmpAccIDB = myintent.getStringExtra("AccIDB");

		wareHousePKFromA = myintent.getStringExtra("wareHousePKFromA");
		wareHousePKToA = myintent.getStringExtra("wareHousePKToA");
		wareHousePKFromB = myintent.getStringExtra("wareHousePKFromB");
		wareHousePKToB = myintent.getStringExtra("wareHousePKToB");

		wareHouseNameFrom = myintent.getStringExtra("wareHouseNameFrom");
		wareHouseNameTo = myintent.getStringExtra("wareHouseNameTo");

		PKcorpFrom = myintent.getStringExtra("PKcorpFrom");
		PKcorpTo = myintent.getStringExtra("PKcorpTo");
		tmpWHStatus = myintent.getStringExtra("tmpWHStatus");
		tmpBillStatus = myintent.getStringExtra("tmpBillStatus");

		if (tmpWHStatus.equals("Y")) {
			tmpposIDA = myintent.getStringExtra("tmpposIDA");
			tmpposIDB = myintent.getStringExtra("tmpposIDB");
		}

		lvScanDetail = (ListView) findViewById(R.id.lstTransScanDetail);
		lvScanDetail.setOnItemClickListener(myListItemListener);
		lvScanDetail.setOnItemLongClickListener(myListItemLongListener);

		// ADD BY WUQIONG START
		tvscancount = (TextView) findViewById(R.id.tvSTScounts);
		// ADD BY WUQIONG END

		// ADD CAIXY START
		// sp= new SoundPool(10, AudioManager.STREAM_SYSTEM,
		// 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
		// MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1);
		// //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		// MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1);
		// //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		// ADD CAIXY END

		// add caixy s
		ScanedBarcode = myintent.getStringArrayListExtra("ScanedBarcode");
		Tasknnum = Integer.valueOf(myintent.getStringExtra("TaskCount")
				.toString());
		// add caixy e

		SerializableList lstScanSaveDetial = new SerializableList();
		lstScanSaveDetial = (SerializableList) myintent
				.getSerializableExtra("lstScanSaveDetial");
		lstSaveBody = lstScanSaveDetial.getList();
		if (lstSaveBody != null) {
			if (lstSaveBody.size() > 0) {
				MyListAdapter listItemAdapter = new MyListAdapter(
						StockTransScan.this, lstSaveBody,// 数据源
						R.layout.vlisttransscanitem, new String[] { "InvCode",
								"InvName", "Batch", "AccID", "TotalNum",
								"BarCode", "SeriNo", "BillCode", "ScanedNum",
								"box" }, new int[] { R.id.txtTransScanInvCode,
								R.id.txtTransScanInvName,
								R.id.txtTransScanBatch, R.id.txtTransScanAccId,
								R.id.txtTransScanTotalNum,
								R.id.txtTransScanBarCode,
								R.id.txtTransScanSeriNo,
								R.id.txtTransScanBillCode,
								R.id.txtTransScanScanCount, R.id.txtTransBox });
				lvScanDetail.setAdapter(listItemAdapter);
				// //ADD BY WUQIONG START
				listcount = lstSaveBody.size();
				// //ADD BY WUQIONG END
			}
		}

		btnTransTask = (Button) findViewById(R.id.btnTransTask);
		btnTransTask.setOnClickListener(myBtnListner);

		btnTransReturn = (Button) findViewById(R.id.btnTransReturn);
		btnTransReturn.setOnClickListener(myBtnListner);

		btnTransScanClear = (Button) findViewById(R.id.btnTransScanClear);
		btnTransScanClear.setOnClickListener(myBtnListner);

		txtTSBarcode = (EditText) findViewById(R.id.txtTSBarcode);
		txtTSBarcode.setOnKeyListener(myTxtListener);

		btnTransTask.setFocusable(false);
		btnTransReturn.setFocusable(false);
		btnTransScanClear.setFocusable(false);

		// txtTSAccId = (EditText)findViewById(R.id.txtTSAccId);
		// txtTSInvCode = (EditText)findViewById(R.id.txtTSInvCode);
		// txtTSInvName = (EditText)findViewById(R.id.txtTSInvName);
		// txtTSBatch = (EditText)findViewById(R.id.txtTSBatch);
		// txtTSSeriNo = (EditText)findViewById(R.id.txtTSSeriNo);
		// txtTSWH = (EditText)findViewById(R.id.txtTSWH);
		// txtTSPos = (EditText)findViewById(R.id.txtTSPos);

		// txtTSAccId.setFocusable(false);
		// txtTSInvCode.setFocusable(false);
		// txtTSInvName.setFocusable(false);
		// txtTSBatch.setFocusable(false);
		// txtTSSeriNo.setFocusable(false);
		// txtTSWH.setFocusable(false);

		// 取得任务详细

		// jonsTaskBody=null;
		jonsBody = Common.jsonBodyTask;
		JsonModTaskData = Common.JsonModTaskData;

		// String lsTaskJosnBody = myintent.getStringExtra("TaskJonsBody");
		//
		//
		//
		// JSONObject jonsTaskBody=null;
		// try {
		// jonsTaskBody = new JSONObject(lsTaskJosnBody);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// return;
		// }
		// if(jonsTaskBody.has("TransBillBody"))
		// {
		// this.jonsBody=jonsTaskBody;
		// }
		//
		// //ModTask剥离
		//
		// String lsScanModTask = myintent.getStringExtra("ScanModTask");
		//
		// try {
		// jonsTaskBody = new JSONObject(lsScanModTask);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// return;
		// }
		// // if(jonsTaskBody.has("TransBillBody"))123
		// // {
		// this.JsonModTaskData=jonsTaskBody;
		// }

		// DEL BY WUQIONG S
		// if(jonsBody.has("RemoveTaskData"))
		// {
		// try {
		// JsonRemoveTaskData=(JSONObject)jonsBody.get("RemoveTaskData");
		// } catch (JSONException e) {
		// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		// e.printStackTrace();
		// }
		// }
		// DEL BY WUQIONG E

		// //ADD BY caixy START
		// Tasknnum = 0;
		// JSONArray JsonArrays = new JSONArray();
		// try {
		// JsonArrays=(JSONArray)jonsBody.get("TransBillBody");
		// for (int i =0; i<JsonArrays.length();i++)
		// {
		// Tasknnum = Tasknnum +
		// Integer.valueOf(((JSONObject)(JsonArrays.get(i))).getString("nnum"));
		// }
		//
		// } catch (JSONException e1) {
		// // TODO Auto-generated catch block
		// Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
		// e1.printStackTrace();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// return;
		// }
		tvscancount.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount
				+ "件 | " + "未扫" + (Tasknnum - listcount) + "件");
		txtTSBarcode.requestFocus();
		// //ADD BY caixy END

		// ModTaskData从BODY剥离
		// ADD BY WUQIONG START
		// if(jonsBody.has("ModTaskData"))
		// {
		// try {
		// JsonModTaskData=(JSONObject)jonsBody.get("ModTaskData");
		// } catch (JSONException e) {
		// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// e.printStackTrace();
		// }
		// }
		// ADD BY WUQIONG END

		try {
			getTaskListData(this.jonsBody);
		} catch (JSONException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			e.printStackTrace();
		}
		UserID = MainLogin.objLog.UserID;
		// String LogName = BillType + UserID + dfd.format(day)+".txt";
		ScanedFileName = "4YScan" + UserID + ".txt";
		fileNameScan = "/sdcard/DVQ/4YScan" + UserID + ".txt";
		fileScan = new File(fileNameScan);
		ReScanBody();
	}

	private void ReScanErr() {
		AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
				R.string.CuoWu).setMessage(R.string.ShuJuJiaZaiCuoWu + "\r\n" + R.string.TuiChuGaiMoKuai);

		bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false)
				.create().show();
		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		return;
	}

	private DialogInterface.OnClickListener listenExit = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
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
					i--;
					x++;
				} else {
					for (int j = 0; j < ScanedBarcode.size(); j++) {

						String AAA = ScanedBarcode.get(j);
						if (ScanedBillBar.get(i).toString().equals(AAA)) {
							OKflg = "ok";
						}

					}
					if (!OKflg.equals("ok")) {
						i--;
						x++;
					}
				}
			}

			this.txtTSBarcode.requestFocus();

			ReScanBody = "1";

		}

		catch (Exception e) {

			e.printStackTrace();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_trans_scan, menu);
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

	// 取得任务LIST
	private void getTaskListData(JSONObject jas) throws JSONException {
		lstTaskBody = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		JSONObject tempJso = null;
		if (jas == null) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		if (!jas.has("Status")) {
			Toast.makeText(this,R.string.MeiYouDeDaoBiaoTiShuJu, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
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
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		JSONArray arrays = (JSONArray) jas.get("TransBillBody");

		for (int i = 0; i < arrays.length(); i++) {
			map = new HashMap<String, Object>();
			map.put("InvName",
					((JSONObject) (arrays.get(i))).getString("invname"));
			map.put("InvCode",
					((JSONObject) (arrays.get(i))).getString("invcode"));
			String batchs = ((JSONObject) (arrays.get(i))).getString("vbatch");
			// invname,invcode
			if (batchs == null || batchs.equals("") || batchs.equals("null")) {
				batchs = getString(R.string.PiCiWeiZhiDing);
			}
			map.put("Batch", batchs);
			map.put("AccID", ((JSONObject) (arrays.get(i))).getString("accid"));
			// if(currentObj.totalID()==lstCurrentBox.size())

			// caixy 修改任务数量（应出数量-已出数量）
			// map.put("InvNum",
			// ((JSONObject)(arrays.get(i))).getString("nnum"));

			String nnum = ((JSONObject) (arrays.get(i))).getString("nnum");
			String norderoutnum = ((JSONObject) (arrays.get(i)))
					.getString("norderoutnum");

			String snnum = "0";
			if (!norderoutnum.equals("null")) {
				snnum = (norderoutnum.replaceAll("\\.0", ""));
			}
			// 应发未出库数量 shouldoutnum
			int shouldoutnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

			String Tasknnum = shouldoutnum + "";

			map.put("InvNum", Tasknnum);
			// caixy 修改任务数量（应出数量-已出数量）
			map.put("BillCode",
					((JSONObject) (arrays.get(i))).getString("vcode"));
			lstTaskBody.add(map);
		}
	}

	// 创建对话框的按钮事件侦听
	private Button.OnClickListener myBtnListner = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnTransTask: {
				// try {
				// ReSetTaskListData();
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// Toast.makeText(StockTransScan.this, e.getMessage(),
				// Toast.LENGTH_LONG).show();
				// }
				if (lstTaskBody == null || lstTaskBody.size() < 1)
					return;
				
				List<Map<String, Object>> lsTaskBody = new ArrayList<Map<String, Object>>();
				
				
				for(int j = 0;j<lstTaskBody.size();j++)
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map=(HashMap<String, Object>) lstTaskBody.get(j);
					String lsInvNum = (String) map.get("InvNum");
					if(!lsInvNum.equals("0"))
					{
						lsTaskBody.add(map);
					}
				}
				
				for(int j = 0;j<lstTaskBody.size();j++)
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map=(HashMap<String, Object>) lstTaskBody.get(j);
					String lsInvNum = (String) map.get("InvNum");
					if(lsInvNum.equals("0"))
					{
						lsTaskBody.add(map);
					}
				}

				SimpleAdapter listItemAdapter = new SimpleAdapter(
						StockTransScan.this, lsTaskBody,// 数据源
						R.layout.vlisttranstask,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "InvCode", "InvName", "Batch",
								"InvNum", "BillCode" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.txtTranstaskInvCode,
								R.id.txtTranstaskInvName,

								R.id.txtTranstaskInvNum,
								 });
				new AlertDialog.Builder(StockTransScan.this).setTitle(R.string.YuanDanXinXi)
						.setAdapter(listItemAdapter, null)
						.setPositiveButton(R.string.QueRen, null).show();
				break;
			}
			case R.id.btnTransReturn: {
				Intent intent = new Intent();

				SerializableList ResultBodyList = new SerializableList();
				ResultBodyList.setList(lstSaveBody);
				intent.putExtra("SaveBodyList", ResultBodyList);

				// ResultBodyList);
				// intent.putExtra("ScanTaskJson", jsonBodyTask.toString());
				Common.SetBodyTask(jonsBody);
				// ModTask从BODY剥离
				// intent.putExtra("ScanModTask", JsonModTaskData.toString());
				Common.SetModTaskData(JsonModTaskData);

				// intent.putExtra("ScanTaskJson", jonsBody.toString());
				// //ModTask从BODY剥离
				// intent.putExtra("ScanModTask", JsonModTaskData.toString());
				// add caixy s
				intent.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
				// add caixy e
				StockTransScan.this.setResult(8, intent);
				finish();
				break;
			}
			case R.id.btnTransScanClear: {
				// MOD BY WUQIONG START
				// if(lvScanDetail.getAdapter().getCount()<1)
				if (lvScanDetail.getCount() < 1)
					// MOD BY WUQIONG END
					return;

				ButtonOnClickClearconfirm btnScanItemClearOnClick = new ButtonOnClickClearconfirm();
				DeleteAlertDialog = new AlertDialog.Builder(StockTransScan.this)
						.setTitle(R.string.QueRenQingKong).setMessage(R.string.NiQueRenQingKongJiLuMa)
						.setPositiveButton(R.string.QueRen, btnScanItemClearOnClick)
						.setNegativeButton(R.string.QuXiao, null).show();

			}
			}
		}
	};

	// lvScanDetail.setOnItemClickListener(myListItemListener);

	private boolean FindInvnBinStockInfo() throws JSONException,
			ParseException, IOException {
		if (ReScanBody.equals("0")) {
			return true;
		}
		JSONObject para = new JSONObject();
		para.put("FunctionName", "GetBinStockByID");
		para.put("InvID", this.currentObj.Invmandoc());
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
		JSONObject StockInfo = Common.DoHttpQuery(para, "CommonQuery",
				bar.AccID);

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
				Toast.makeText(this, R.string.HuoQuHuoWeiKuCunShuJu,
						Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return false;
			}

			double stockCount = 0.0;

			for (int iv = 0; iv < val.length(); iv++) {
				JSONObject temp = val.getJSONObject(iv);
				if (Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0) {
					stockCount = stockCount
							+ Double.valueOf(temp.getString("nnum"))
									.doubleValue();
				}
			}

			double sancount = getScanCount(currentObj.Invmandoc(),
					currentObj.GetBatch());
			sancount += 1.0;
			if (sancount > stockCount) {
				Toast.makeText(this, R.string.GaiHuoWeiZaiDeFaChuKuCunYiJingBuZule,
						Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return false;
			}

		} else {
			Toast.makeText(this, R.string.GaiHuoPinZaiGaiCangHuZhongMeiYouKuCunXinXi, Toast.LENGTH_LONG)
					.show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		return true;
	}

	private boolean FindInvnNoBinStockInfo() throws JSONException,
			ParseException, IOException {
		if (ReScanBody.equals("0")) {
			return true;
		}

		JSONObject para = new JSONObject();
		para.put("FunctionName", "SearchGetStockInfo");
		para.put("TableName", "InvInfo");
		para.put("InvCode", currentObj.getInvCode());
		para.put("InvBatch", currentObj.GetBatch());

		String CompanyCode = "";
		if (tmpBillStatus.equals("Y")) {
			CompanyCode = PKcorpFrom;
		} else {
			CompanyCode = PKcorpTo;
		}

		para.put("CompanyCode", CompanyCode);

		para.put("accId", bar.AccID);

		// para.put("LotB", this.currentObj.GetBatch());
		// para.put("TableName", "Stock");
		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}
		JSONObject StockInfo = Common.DoHttpQuery(para, "CommonQuery",
				bar.AccID);

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
				Toast.makeText(this, R.string.HuoQuHuoWeiKuCunShuJu,
						Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return false;
			}

			double stockCount = 0.0;

			for (int iv = 0; iv < val.length(); iv++) {
				JSONObject temp = val.getJSONObject(iv);

				if (tmpBillStatus.equals("Y")) {
					if (bar.AccID.equals("A")) {
						if (temp.getString("pk_stordoc").equals(
								wareHousePKFromA)) {
							if (Double.valueOf(temp.getString("nnum"))
									.doubleValue() > 0.0) {
								stockCount = stockCount
										+ Double.valueOf(temp.getString("nnum"))
												.doubleValue();
							}
						}
					}
					if (bar.AccID.equals("B")) {
						if (temp.getString("pk_stordoc").equals(
								wareHousePKFromB)) {
							if (Double.valueOf(temp.getString("nnum"))
									.doubleValue() > 0.0) {
								stockCount = stockCount
										+ Double.valueOf(temp.getString("nnum"))
												.doubleValue();
							}
						}
					}

				} else {
					if (bar.AccID.equals("A")) {
						if (temp.getString("pk_stordoc").equals(wareHousePKToA)) {
							if (Double.valueOf(temp.getString("nnum"))
									.doubleValue() > 0.0) {
								stockCount = stockCount
										+ Double.valueOf(temp.getString("nnum"))
												.doubleValue();
							}
						}
					}
					if (bar.AccID.equals("B")) {
						if (temp.getString("pk_stordoc").equals(wareHousePKToB)) {
							if (Double.valueOf(temp.getString("nnum"))
									.doubleValue() > 0.0) {
								stockCount = stockCount
										+ Double.valueOf(temp.getString("nnum"))
												.doubleValue();
							}
						}
					}

				}
			}

			double sancount = getScanCount(currentObj.Invmandoc(),
					currentObj.GetBatch());
			sancount += 1.0;
			if (sancount > stockCount) {
				Toast.makeText(this, R.string.GaiHuoWeiZaiDeFaChuKuCunYiJingBuZule,
						Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return false;
			}

		} else {
			Toast.makeText(this, R.string.GaiHuoWeiZaiDeFaChuKuCunYiJingBuZule, Toast.LENGTH_LONG)
					.show();
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
			if ((values.get("cinventoryid").equals(InvID))
					&& (values.get("vbatchcode").equals(BatchCode))) {
				spacenum += Double.valueOf((String) values.get("spacenum"))
						.doubleValue();
			}
		}
		return spacenum;
	}

	// 清空扫描详细的监听事件
	private class ButtonOnClickClearconfirm implements
			DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			if (whichButton == DialogInterface.BUTTON_POSITIVE) {
				try {
					ClearAllScanDetail();
				} catch (JSONException e) {
					Toast.makeText(StockTransScan.this, e.getMessage(),
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

	// 清空扫描详细的内容
	private void ClearAllScanDetail() throws JSONException {

		// DEL BY WUQIONG S
		// if(JsonRemoveTaskData == null || JsonRemoveTaskData.length() < 1)
		// return;

		// 恢复之前删除的任务数据
		// Iterator itKeys = JsonRemoveTaskData.keys();
		// DEL BY WUQIONG E
		// ADD BY WUQIONG START
		Iterator itModKeys = JsonModTaskData.keys();
		// ADD BY WUQIONG END
		JSONArray JsonArrays = new JSONArray();

		JsonArrays = (JSONArray) jonsBody.get("TransBillBody");

		// DEL BY WUQIONG S
		// while(itKeys.hasNext())
		// {
		// String lsKey = itKeys.next().toString();
		// JsonArrays.put((JSONObject)JsonRemoveTaskData.get(lsKey));
		// }
		// DEL BY WUQIONG E

		// ADD BY WUQIONG S
		while (itModKeys.hasNext()) {
			String lsKey = itModKeys.next().toString();
			if (JsonModTaskData.has(lsKey)) {
				// JsonArrays.put((JSONObject)JsonModTaskData.get(sKey));
				// JSONArray
				// JsonArraysMod=(JSONArray)jonsBody.get("TransBillBody");
				JSONObject JsonReMod = (JSONObject) JsonModTaskData.get(lsKey);
				JSONObject jObj = new JSONObject();

				String csourcebillhid = JsonReMod.getString("cbill_bid")
						.toString();
				String InvCode = JsonReMod.getString("invcode").toString();
				String csourcerowno = JsonReMod.getString("crowno").toString();
				String nnum = JsonReMod.getString("nnum").toString();
				String Tasknnum = "0";

				for (int i = 0; i < JsonArrays.length(); i++) {
					String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i)))
							.getString("cbill_bid");
					String InvCodeaDel = ((JSONObject) (JsonArrays.get(i)))
							.getString("invcode");
					String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i)))
							.getString("crowno");

					if (csourcebillhidDel.equals(csourcebillhid)
							&& InvCodeaDel.equals(InvCode)
							&& csourcerownoaDel.equals(csourcerowno)) {
						Tasknnum = ((JSONObject) (JsonArrays.get(i)))
								.getString("nnum");
					}
				}

				// jObj.put("vbatch",JsonReMod.getString("vbatch").toString());
				// jObj.put("naddpricerate",JsonReMod.getString("naddpricerate").toString());
				// jObj.put("coutcbid",JsonReMod.getString("coutcbid").toString());
				// jObj.put("cinwhid",JsonReMod.getString("cinwhid").toString());
				// jObj.put("cbillid",JsonReMod.getString("cbillid").toString());
				// jObj.put("measname",JsonReMod.getString("measname").toString());
				// jObj.put("coutcorpid",JsonReMod.getString("coutcorpid").toString());
				// jObj.put("cbill_bid",JsonReMod.getString("cbill_bid").toString());
				// jObj.put("cquoteunitid",JsonReMod.getString("cquoteunitid").toString());
				// jObj.put("fallocflag",JsonReMod.getString("fallocflag").toString());
				// jObj.put("vbdef1",JsonReMod.getString("vbdef1").toString());
				// jObj.put("cininvid",JsonReMod.getString("cininvid").toString());
				// jObj.put("crelation_bid",JsonReMod.getString("crelation_bid").toString());
				// jObj.put("ctakeoutcorpid",JsonReMod.getString("ctakeoutcorpid").toString());
				// jObj.put("ctakeoutinvid",JsonReMod.getString("ctakeoutinvid").toString());
				// jObj.put("crelationid",JsonReMod.getString("crelationid").toString());
				// jObj.put("vcode",JsonReMod.getString("vcode").toString());
				// jObj.put("invname",JsonReMod.getString("invname").toString());
				// jObj.put("nquoteunitnum",JsonReMod.getString("nquoteunitnum").toString());
				// jObj.put("invcode",JsonReMod.getString("invcode").toString());
				// jObj.put("coutwhid",JsonReMod.getString("coutwhid").toString());
				// jObj.put("ctakeoutwhid",JsonReMod.getString("ctakeoutwhid").toString());
				// jObj.put("cinvbasid",JsonReMod.getString("cinvbasid").toString());
				//
				// jObj.put("cincbid",JsonReMod.getString("cincbid").toString());
				// jObj.put("ctakeoutcbid",JsonReMod.getString("ctakeoutcbid").toString());
				// jObj.put("pk_arrivearea",JsonReMod.getString("pk_arrivearea").toString());
				// jObj.put("cincorpid",JsonReMod.getString("cincorpid").toString());
				// jObj.put("crowno",JsonReMod.getString("crowno").toString());
				// jObj.put("coutinvid",JsonReMod.getString("coutinvid").toString());
				//
				// jObj.put("norderoutnum",JsonReMod.getString("norderoutnum").toString());
				// jObj.put("cfirstid",JsonReMod.getString("cfirstid").toString());
				// jObj.put("cfirsttypecode",JsonReMod.getString("cfirsttypecode").toString());
				// jObj.put("ctypecode",JsonReMod.getString("ctypecode").toString());
				// jObj.put("cfirstbid",JsonReMod.getString("cfirstbid").toString());
				// jObj.put("nquoteunitrate",JsonReMod.getString("nquoteunitrate").toString());
				// jObj.put("nordershouldoutnum",JsonReMod.getString("nordershouldoutnum").toString());

				jObj.put("vbdef1", JsonReMod.getString("vbdef1").toString());
				jObj.put("nquoteunitrate", JsonReMod
						.getString("nquoteunitrate").toString());
				jObj.put("accid", JsonReMod.getString("accid").toString());
				jObj.put("vbatch", JsonReMod.getString("vbatch").toString());
				jObj.put("crowno", JsonReMod.getString("crowno").toString());
				jObj.put("cfirstid", JsonReMod.getString("cfirstid").toString());
				jObj.put("cbillid", JsonReMod.getString("cbillid").toString());
				jObj.put("cfirstbid", JsonReMod.getString("cfirstbid")
						.toString());
				jObj.put("cbill_bid", JsonReMod.getString("cbill_bid")
						.toString());
				jObj.put("cfirsttypecode", JsonReMod
						.getString("cfirsttypecode").toString());
				jObj.put("ctypecode", JsonReMod.getString("ctypecode")
						.toString());
				jObj.put("cquoteunitid", JsonReMod.getString("cquoteunitid")
						.toString());
				jObj.put("nquoteunitnum", JsonReMod.getString("nquoteunitnum")
						.toString());
				jObj.put("nordershouldoutnum",
						JsonReMod.getString("nordershouldoutnum").toString());
				jObj.put("pk_arrivearea", JsonReMod.getString("pk_arrivearea")
						.toString());
				jObj.put("vcode", JsonReMod.getString("vcode").toString());
				jObj.put("cinvbasid", JsonReMod.getString("cinvbasid")
						.toString());
				jObj.put("norderoutnum", JsonReMod.getString("norderoutnum")
						.toString());
				jObj.put("invname", JsonReMod.getString("invname").toString());
				jObj.put("invcode", JsonReMod.getString("invcode").toString());

				// String nnum = (JsonReMod.get("nnum").toString()); //减已扫描件数
				int iTasknnum = Integer.valueOf(Tasknnum);

				String snnum = (nnum.replaceAll("\\.0", ""));

				int innum = Integer.valueOf(snnum);

				int inewnnum = iTasknnum + innum;
				String snewnnum = inewnnum + "";

				jObj.put("nnum", snewnnum);

				JSONArray JsonArraysRemod = new JSONArray();
				JSONObject jObjReMod = new JSONObject();
				for (int i = 0; i < JsonArrays.length(); i++) {
					String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i)))
							.getString("cbill_bid");
					String InvCodeaDel = ((JSONObject) (JsonArrays.get(i)))
							.getString("invcode");
					String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i)))
							.getString("crowno");

					if (!csourcebillhidDel.equals(csourcebillhid)
							|| !InvCodeaDel.equals(InvCode)
							|| !csourcerownoaDel.equals(csourcerowno)) {
						jObjReMod = (JSONObject) JsonArrays.get(i);
						JsonArraysRemod.put(jObjReMod);
					}
				}

				JsonArrays = JsonArraysRemod;
				JsonArrays.put(jObj);
			}

			jonsBody = new JSONObject();
			jonsBody.put("Status", true);
			jonsBody.put("TransBillBody", JsonArrays);

		}

		JsonModTaskData = new JSONObject();

		// 重新绑定任务ListView
		getTaskListData(jonsBody);

		// DEL BY WUQIONG S
		// JsonRemoveTaskData = new JSONObject();
		// DEL BY WUQIONG E

		// ADD BY WUQIONG START
		while (itModKeys.hasNext())
			JsonModTaskData = new JSONObject();
		ScanedBarcode = new ArrayList<String>();
		SaveScanedBody();
		// ADD BY WUQIONG END

		// 清空保存在内存的扫描详细
		lstSaveBody = new ArrayList<Map<String, Object>>();
		lvScanDetail.setAdapter(null);
		txtTSBarcode.setText("");
		// ADD BY WUQIONG START
		// Tasknnum = 0;
		// JSONArray JsonArraysA = new JSONArray();
		// try {
		// JsonArraysA=(JSONArray)jonsBody.get("TransBillBody");
		// for (int i =0; i<JsonArraysA.length();i++)
		// {
		// Tasknnum = Tasknnum +
		// Integer.valueOf(((JSONObject)(JsonArraysA.get(i))).getString("nnum"));
		// }
		//
		// } catch (JSONException e1) {
		// // TODO Auto-generated catch block
		// Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
		// e1.printStackTrace();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// return;
		// }
		listcount = lstSaveBody.size();
		tvscancount.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount
				+ "件 | " + "未扫" + (Tasknnum - listcount) + "件");

		// ADD BY WUQIONG END

	}

	// 长按扫描详细，删除该条记录
	private OnItemLongClickListener myListItemLongListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			Map<String, Object> mapCurrent = (Map<String, Object>) lvScanDetail
					.getAdapter().getItem(arg2);
			String lsKey = mapCurrent.get("csourcebillbid").toString()
					+ mapCurrent.get("InvCode").toString()
					+
					// MOD BY WUQIONG S
					// mapCurrent.get("csourcerowno").toString();
					mapCurrent.get("csourcerowno").toString()
					+ mapCurrent.get("SeriNo").toString();
			// MOD BY WUQIONG E

			// 需要修改
			// add caixy s 取得barcode
			String Barcode = mapCurrent.get("BarCode").toString();

			// add caixy e

			ButtonOnClickDelconfirm btnScanItemDelOnClick = new ButtonOnClickDelconfirm(
					arg2, lsKey, Barcode);
			DeleteAlertDialog = new AlertDialog.Builder(StockTransScan.this)
					.setTitle(R.string.QueRenShanChu).setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
					.setPositiveButton(R.string.QueRen, btnScanItemDelOnClick)
					.setNegativeButton(R.string.QuXiao, null).show();

			return true;
		}

	};

	// 删除已扫描的内容
	private void ConfirmDelItem(int iIndex, String sKey, String BarCode)
			throws JSONException {
		// 删除保存在内存的扫描详细

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

		// ADD WUQIONG START
		// Tasknnum = 0;
		//
		// JSONArray JsonArraysA = new JSONArray();
		// try {
		// JsonArraysA=(JSONArray)jonsBody.get("TransBillBody");
		// for (int i =0; i<JsonArraysA.length();i++)
		// {
		// Tasknnum = Tasknnum +
		// Integer.valueOf(((JSONObject)(JsonArraysA.get(i))).getString("nnum"));
		// }
		//
		// } catch (JSONException e1) {
		// // TODO Auto-generated catch block
		// Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
		// e1.printStackTrace();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// return;
		// }
		listcount = lstSaveBody.size();
		tvscancount.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount
				+ "件 | " + "未扫" + (Tasknnum - listcount) + "件");
		// ADD WUQIONG END

		// lvScanDetail.removeViewAt(iIndex);
		// lvScanDetail.getAdapter().notifyAll();
		MyListAdapter listItemAdapter = (MyListAdapter) lvScanDetail
				.getAdapter();
		listItemAdapter.notifyDataSetChanged();
		lvScanDetail.setAdapter(listItemAdapter);
		// MOD BY WUQIONG START

		// DEL BY WUQIONG S
		// if(JsonRemoveTaskData == null || JsonRemoveTaskData.length() < 1)
		// return;
		//
		// if(!JsonRemoveTaskData.has(sKey))
		// return;

		// DEL BY WUQIONG E

		if (JsonModTaskData == null || JsonModTaskData.length() < 1)
			return;

		if (!JsonModTaskData.has(sKey))
			return;

		// MOD BY WUQIONG END

		// 恢复之前删除的任务数据
		JSONArray JsonArrays = (JSONArray) jonsBody.get("TransBillBody");
		// DEL BY WUQIONG S
		// JsonArrays.put((JSONObject)JsonRemoveTaskData.get(sKey));
		// JsonRemoveTaskData.remove(sKey);
		// jonsBody = new JSONObject();
		// jonsBody.put("Status", true);
		// jonsBody.put("TransBillBody", JsonArrays);
		// jonsBody.put("RemoveTaskData", JsonRemoveTaskData);
		// getTaskListData(jonsBody);

		// if (JsonRemoveTaskData.has(sKey))
		// {
		// JsonArrays.put((JSONObject)JsonRemoveTaskData.get(sKey));
		// JsonRemoveTaskData.remove(sKey);
		// }
		// DEL BY WUQIONG E

		// MOD BY WUQIONG START
		if (JsonModTaskData.has(sKey)) {
			// JsonArrays.put((JSONObject)JsonModTaskData.get(sKey));
			// JSONArray JsonArraysMod=(JSONArray)jonsBody.get("TransBillBody");
			JSONObject JsonReMod = (JSONObject) JsonModTaskData.get(sKey);
			JSONObject jObj = new JSONObject();

			String csourcebillhid = JsonReMod.getString("cbill_bid").toString();
			String InvCode = JsonReMod.getString("invcode").toString();
			String csourcerowno = JsonReMod.getString("crowno").toString();
			String nnum = JsonReMod.getString("nnum").toString();// 修改数量问题
			String Tasknnum = "0";

			for (int i = 0; i < JsonArrays.length(); i++) {
				String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i)))
						.getString("cbill_bid");
				String InvCodeaDel = ((JSONObject) (JsonArrays.get(i)))
						.getString("invcode");
				String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i)))
						.getString("crowno");

				if (csourcebillhidDel.equals(csourcebillhid)
						&& InvCodeaDel.equals(InvCode)
						&& csourcerownoaDel.equals(csourcerowno)) {
					Tasknnum = ((JSONObject) (JsonArrays.get(i)))
							.getString("nnum");
				}
			}

			// jObj.put("vbatch",JsonReMod.getString("vbatch").toString());
			// jObj.put("naddpricerate",JsonReMod.getString("naddpricerate").toString());
			// jObj.put("coutcbid",JsonReMod.getString("coutcbid").toString());
			// jObj.put("cinwhid",JsonReMod.getString("cinwhid").toString());
			// jObj.put("cbillid",JsonReMod.getString("cbillid").toString());
			// jObj.put("measname",JsonReMod.getString("measname").toString());
			// jObj.put("coutcorpid",JsonReMod.getString("coutcorpid").toString());
			// jObj.put("cbill_bid",JsonReMod.getString("cbill_bid").toString());
			// jObj.put("cquoteunitid",JsonReMod.getString("cquoteunitid").toString());
			// jObj.put("fallocflag",JsonReMod.getString("fallocflag").toString());
			// jObj.put("vbdef1",JsonReMod.getString("vbdef1").toString());
			// jObj.put("cininvid",JsonReMod.getString("cininvid").toString());
			// jObj.put("crelation_bid",JsonReMod.getString("crelation_bid").toString());
			// jObj.put("ctakeoutcorpid",JsonReMod.getString("ctakeoutcorpid").toString());
			// jObj.put("ctakeoutinvid",JsonReMod.getString("ctakeoutinvid").toString());
			// jObj.put("crelationid",JsonReMod.getString("crelationid").toString());
			// jObj.put("vcode",JsonReMod.getString("vcode").toString());
			// jObj.put("invname",JsonReMod.getString("invname").toString());
			// jObj.put("nquoteunitnum",JsonReMod.getString("nquoteunitnum").toString());
			// jObj.put("invcode",JsonReMod.getString("invcode").toString());
			// jObj.put("coutwhid",JsonReMod.getString("coutwhid").toString());
			// jObj.put("ctakeoutwhid",JsonReMod.getString("ctakeoutwhid").toString());
			// jObj.put("cinvbasid",JsonReMod.getString("cinvbasid").toString());
			//
			// jObj.put("cincbid",JsonReMod.getString("cincbid").toString());
			// jObj.put("ctakeoutcbid",JsonReMod.getString("ctakeoutcbid").toString());
			// jObj.put("pk_arrivearea",JsonReMod.getString("pk_arrivearea").toString());
			// jObj.put("cincorpid",JsonReMod.getString("cincorpid").toString());
			// jObj.put("crowno",JsonReMod.getString("crowno").toString());
			// jObj.put("coutinvid",JsonReMod.getString("coutinvid").toString());
			//
			// jObj.put("norderoutnum",JsonReMod.getString("norderoutnum").toString());
			// jObj.put("cfirstid",JsonReMod.getString("cfirstid").toString());
			// jObj.put("cfirsttypecode",JsonReMod.getString("cfirsttypecode").toString());
			// jObj.put("ctypecode",JsonReMod.getString("ctypecode").toString());
			// jObj.put("cfirstbid",JsonReMod.getString("cfirstbid").toString());
			// jObj.put("nquoteunitrate",JsonReMod.getString("nquoteunitrate").toString());
			// jObj.put("nordershouldoutnum",JsonReMod.getString("nordershouldoutnum").toString());

			jObj.put("vbdef1", JsonReMod.getString("vbdef1").toString());
			jObj.put("nquoteunitrate", JsonReMod.getString("nquoteunitrate")
					.toString());
			jObj.put("accid", JsonReMod.getString("accid").toString());
			jObj.put("vbatch", JsonReMod.getString("vbatch").toString());
			jObj.put("crowno", JsonReMod.getString("crowno").toString());
			jObj.put("cfirstid", JsonReMod.getString("cfirstid").toString());
			jObj.put("cbillid", JsonReMod.getString("cbillid").toString());
			jObj.put("cfirstbid", JsonReMod.getString("cfirstbid").toString());
			jObj.put("cbill_bid", JsonReMod.getString("cbill_bid").toString());
			jObj.put("cfirsttypecode", JsonReMod.getString("cfirsttypecode")
					.toString());
			jObj.put("ctypecode", JsonReMod.getString("ctypecode").toString());
			jObj.put("cquoteunitid", JsonReMod.getString("cquoteunitid")
					.toString());
			jObj.put("nquoteunitnum", JsonReMod.getString("nquoteunitnum")
					.toString());
			jObj.put("nordershouldoutnum",
					JsonReMod.getString("nordershouldoutnum").toString());
			jObj.put("pk_arrivearea", JsonReMod.getString("pk_arrivearea")
					.toString());
			jObj.put("vcode", JsonReMod.getString("vcode").toString());
			jObj.put("cinvbasid", JsonReMod.getString("cinvbasid").toString());
			jObj.put("norderoutnum", JsonReMod.getString("norderoutnum")
					.toString());
			jObj.put("invname", JsonReMod.getString("invname").toString());
			jObj.put("invcode", JsonReMod.getString("invcode").toString());

			// String nnum = (JsonReMod.get("nnum").toString()); //减已扫描件数
			int iTasknnum = Integer.valueOf(Tasknnum);

			String snnum = (nnum.replaceAll("\\.0", ""));

			int innum = Integer.valueOf(snnum);

			int inewnnum = iTasknnum + innum;
			String snewnnum = inewnnum + "";

			jObj.put("nnum", snewnnum);

			JSONArray JsonArraysRemod = new JSONArray();
			JSONObject jObjReMod = new JSONObject();
			for (int i = 0; i < JsonArrays.length(); i++) {
				String csourcebillhidDel = ((JSONObject) (JsonArrays.get(i)))
						.getString("cbill_bid");
				String InvCodeaDel = ((JSONObject) (JsonArrays.get(i)))
						.getString("invcode");
				String csourcerownoaDel = ((JSONObject) (JsonArrays.get(i)))
						.getString("crowno");

				if (!csourcebillhidDel.equals(csourcebillhid)
						|| !InvCodeaDel.equals(InvCode)
						|| !csourcerownoaDel.equals(csourcerowno)) {
					jObjReMod = (JSONObject) JsonArrays.get(i);
					JsonArraysRemod.put(jObjReMod);
				}
			}

			JsonArrays = JsonArraysRemod;
			JsonArrays.put(jObj);
			JsonModTaskData.remove(sKey);
		}

		jonsBody = new JSONObject();
		jonsBody.put("Status", true);
		jonsBody.put("TransBillBody", JsonArrays);

		// DEL BY WUQIONG S
		// if (JsonRemoveTaskData.has(sKey))
		// {
		// jonsBody.put("RemoveTaskData", JsonRemoveTaskData);
		// }
		// DEL BY WUQIONG E

		// ModTask从body剥离
		// if(JsonModTaskData.has(sKey))
		// {
		// jonsBody.put("ModTaskData", JsonModTaskData);
		// }

		getTaskListData(jonsBody);

		// MOD BY WUQIONG END

	}

	// 删除已扫描详细的监听事件
	private class ButtonOnClickDelconfirm implements
			DialogInterface.OnClickListener {

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
					Toast.makeText(StockTransScan.this, e.getMessage(),
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

	private OnItemClickListener myListItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Map<String, Object> mapCurrent = (Map<String, Object>) lvScanDetail
					.getAdapter().getItem(arg2);
			String lsKey = mapCurrent.get("BillCode").toString()
					+ mapCurrent.get("AccID").toString()
					+ mapCurrent.get("InvCode").toString()
					+ mapCurrent.get("Batch").toString()
					+ mapCurrent.get("SeriNo").toString();
			ArrayList<Map<String, Object>> lstCurrent = (ArrayList<Map<String, Object>>) mapCurrent
					.get(lsKey);
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					StockTransScan.this, lstCurrent,// 数据源
					android.R.layout.simple_list_item_2, new String[] {
							"BoxNum", "FinishBarCode" }, new int[] {
							android.R.id.text1, android.R.id.text2 });
			new AlertDialog.Builder(StockTransScan.this).setTitle(R.string.FenBaoXiangXiXinXi)
					.setAdapter(listItemAdapter, null)
					.setPositiveButton(R.string.QueRen, null).show();
		}

	};

	private OnKeyListener myTxtListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int arg1, KeyEvent arg2) {
			{
				switch (v.getId()) {
				case id.txtTSBarcode:
					if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)// &&
																				// arg2.getAction()
																				// ==
																				// KeyEvent.ACTION_DOWN
					{
						try {
							// txtTSBarcode.setText(txtTSBarcode.getText().toString().replace("\n",
							// ""));
							ScanBarcode(txtTSBarcode.getText().toString());
						} catch (ParseException e) {
							txtTSBarcode.setText("");
							txtTSBarcode.requestFocus();
							Toast.makeText(StockTransScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							e.printStackTrace();
						} catch (JSONException e) {
							txtTSBarcode.setText("");
							txtTSBarcode.requestFocus();
							Toast.makeText(StockTransScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							e.printStackTrace();
						} catch (IOException e) {
							txtTSBarcode.setText("");
							txtTSBarcode.requestFocus();
							Toast.makeText(StockTransScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							e.printStackTrace();
						}
						return true;
					}

					break;

				}
			}
			return false;
		}

	};

	// 扫描二维码解析功能函数
	private void ScanBarcode(String barcode) throws JSONException,
			ParseException, IOException {
		if (barcode.equals("")) {
			Toast.makeText(this, R.string.QingSaoMiaoTiaoMa, Toast.LENGTH_LONG).show();
			txtTSBarcode.requestFocus();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		txtTSBarcode.setText("");
		// IniScan();
		// 条码分析

		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}

		bar = new SplitBarcode(barcode);
		if (bar.creatorOk == false) {
			Toast.makeText(this, R.string.SaoMiaoDeBuShiZhengQueHuoPinTiaoMa, Toast.LENGTH_LONG).show();
			txtTSBarcode.setText("");
			txtTSBarcode.requestFocus();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		//
		// 判断是否已经有AccID,如果有但是和扫描出来的AccID不一样,提示错误.
		// if(tmpAccID!=null && !tmpAccID.equals(""))
		// {
		// if(!tmpAccID.equals(bar.AccID))
		// {
		//
		// txtTSBarcode.setText("");
		// txtTSBarcode.requestFocus();
		// Toast.makeText(this, "扫描的条码不属于该任务帐套,该货品不能够扫入",
		// Toast.LENGTH_LONG).show();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// return;
		// }
		// }

		// add caixys
		String FinishBarCode = bar.FinishBarCode;

		if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
			for (int si = 0; si < ScanedBarcode.size(); si++) {
				String BarCode = ScanedBarcode.get(si).toString();

				if (BarCode.equals(FinishBarCode)) {
					txtTSBarcode.setText("");
					txtTSBarcode.requestFocus();
					Toast.makeText(this, R.string.GaiTiaoMaYiJingBeiSaoMiaoGuoLe, Toast.LENGTH_LONG)
							.show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return;
				}
			}
		}

		// ScanedBarcode.add(FinishBarCode);
		// add caixy e

		//
		if (!ConformDetail(barcode, bar)) {
			txtTSBarcode.setText("");
			txtTSBarcode.requestFocus();
			return;
		}

		if (OkFkg.equals("ng")) {
			// 该任务已经全部扫描完毕
			Toast.makeText(this, R.string.ChaoChuShangYouDanJuRenWuShuLiang, Toast.LENGTH_LONG)
					.show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			txtTSBarcode.setText("");
			txtTSBarcode.requestFocus();
			return;
		}

		if (!ConformBatch(bar.cInvCode, bar.cBatch, bar.AccID)) {
			// 表示这个批次这里没有，需要重新打印
			// Toast.makeText(this, "扫描的条码批次号不正确,需要重新打印",
			// Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			txtTSBarcode.setText("");
			txtTSBarcode.requestFocus();
			return;
		}

		if (tmpWHStatus.equals("Y") && tmpBillStatus.equals("Y")) {
			if (!FindInvnBinStockInfo()) {
				txtTSBarcode.setText("");
				txtTSBarcode.requestFocus();
				return;
			}
		} else {
			if (!FindInvnNoBinStockInfo()) {
				txtTSBarcode.setText("");
				txtTSBarcode.requestFocus();
				return;
			}
		}

		// if(wareHousePK==null||wareHousePK.equals(""))
		// {
		// showSingleChoiceDialog();
		// return;
		// }
		// else
		// {

		ScanInvOK = "0";
		JSONObject jsonCheckGetBillCode = CheckGetBillCode(bar);
		if (jsonCheckGetBillCode == null || jsonCheckGetBillCode.length() < 1) {
			txtTSBarcode.setText("");
			txtTSBarcode.requestFocus();
			if (ScanInvOK.equals("1")) {
				// 任务已经扫描完毕，但是还有分包未完成
				Toast.makeText(this, R.string.ChaoChuShangYouDanJuRenWuShuLiang, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}
			Toast.makeText(this, R.string.GaiTiaoMaBuFuHeRenWuXiangMu, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		if (!CheckHasScaned(jsonCheckGetBillCode, bar)) {
			txtTSBarcode.setText("");
			txtTSBarcode.requestFocus();
			Toast.makeText(this, R.string.GaiTiaoMaYiJingBeiSaoMiaoGuoLe, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		// GetRemovedTaskList(bar);
		// ADD BY WUQIONG START
		GetModTaskList(bar, jsonCheckGetBillCode);
		// ADD BY WUQIONG END
		ReSetTaskListData();
		ScanedBarcode.add(FinishBarCode);

		SaveScanedBody();
		MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
		txtTSBarcode.setText("");

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

		// 记录扫描数据
		String ScanedBar = "";

		for (int i = 0; i < ScanedBarcode.size(); i++) {

			if (i == ScanedBarcode.size() - 1)
				BillBarCode = BillBarCode + ScanedBarcode.get(i).toString();
			else
				BillBarCode = BillBarCode + ScanedBarcode.get(i).toString()
						+ ",";
		}
		ScanedBar = BillBarCode;

		if (fileScan.exists()) {
			fileScan.delete();
		}

		writeTxt.writeTxtToFile(ScanedFileName, ScanedBar);

	}

	// 确认存货在上游单据内有
	private boolean ConformDetail(String barcode, SplitBarcode bar)
			throws JSONException, ParseException, IOException {
		if (jonsHead == null && jonsBody == null) {
			Toast.makeText(this, R.string.MeiYouZhaoDaoCanZhao, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return false;
		}

		JSONArray jsarray = jonsBody.getJSONArray("TransBillBody");
		OkFkg = "ng";
		String invFlg = "ng";
		for (int i = 0; i < jsarray.length(); i++) {
			// 确认了存货
			if (jsarray.getJSONObject(i).getString("invcode")
					.equals(bar.cInvCode)
					&& jsarray.getJSONObject(i).getString("accid")
							.equals(bar.AccID)) {
				// currentObj = new Inventory(jsarray.getJSONObject(i),1);

				// caixy 修改任务数量（应出数量-已出数量）
				// map.put("InvNum",
				// ((JSONObject)(arrays.get(i))).getString("nnum"));

				// String invFlg = "ng";
				invFlg = "ok";
				String nnum = ((JSONObject) (jsarray.get(i))).getString("nnum");
				String norderoutnum = ((JSONObject) (jsarray.get(i)))
						.getString("norderoutnum");

				String snnum = "0";
				if (!norderoutnum.equals("null")) {
					snnum = (norderoutnum.replaceAll("\\.0", ""));
				}
				// 应发未出库数量 shouldoutnum
				int shouldoutnum = Integer.valueOf(nnum)
						- Integer.valueOf(snnum);

				String Tasknnum = shouldoutnum + "";

				if (!Tasknnum.equals("0"))
				// if(!jsarray.getJSONObject(i).getString("nnum").equals("0"))
				// caixy 修改任务数量（应出数量-已出数量）
				{
					// mod caixy s
					// Toast.makeText(this, "存货已经扫描完成",
					// Toast.LENGTH_LONG).show();
					// //ADD CAIXY TEST START
					// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// //ADD CAIXY TEST END
					// return false;
					OkFkg = "ok";
					// mod caixy e
				}
			}
		}
		if (OkFkg.equals("ok")) {
			currentObj = new Inventory(bar.cInvCode, "BADV", bar.AccID);
			if (currentObj.getErrMsg() != null
					&& currentObj.getErrMsg().equals("")) {
				Toast.makeText(this, currentObj.getErrMsg(), Toast.LENGTH_LONG)
						.show();
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
			// Double qty = jsarray.getJSONObject(i).getDouble("nnum");
			// ConformDetailQty(qty);
			return true;
		} else {
			// String invFlg = "ng";
			// invFlg = "ok";
			if (invFlg.equals("ok")) {
				Toast.makeText(this,R.string.ChaoChuShangYouDanJuRenWuShuLiang, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return false;
			}
		}

		Toast.makeText(this, R.string.CunHuoZaiShangYouDanJuRenWuZhongBuCunZai, Toast.LENGTH_LONG).show();
		// ADD CAIXY TEST START
		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// ADD CAIXY TEST END
		return false;
	}

	// DEL BY WUQIONG S
	// 完成扫描后删除任务里的项目
	// private void GetRemovedTaskList(SplitBarcode bar) throws JSONException
	// {
	// String lsBarInvCode = bar.cInvCode;
	// String lsBarBacth = bar.cBatch;
	// String lsBillCode = "";
	// //add wuqiong s
	// String lsSerino = bar.cSerino;
	// //add wuqiong e
	//
	//
	// JSONArray JsonArrays=(JSONArray)jonsBody.get("TransBillBody");
	// //jsonArrRemove = new ArrayList();
	//
	// for(int i = 0;i<JsonArrays.length();i++)
	// {
	// String lsJsonInvCode =
	// ((JSONObject)(JsonArrays.get(i))).getString("invcode");
	// String lsJsonInvBatch =
	// ((JSONObject)(JsonArrays.get(i))).getString("vbatch");
	// Double ldJsonInvQty =
	// ((JSONObject)(JsonArrays.get(i))).getDouble("nnum");
	// if(lsJsonInvBatch==null||lsJsonInvBatch.equals("")||lsJsonInvBatch.equals("null"))
	// {
	// lsJsonInvBatch="批次未指定";
	// }
	// if(lsBarInvCode.equals(lsJsonInvCode))
	// {
	// if(lsBarBacth.equals(lsJsonInvBatch))
	// {
	// GetRemovedTaskQty(ldJsonInvQty,
	// ((JSONObject)(JsonArrays.get(i))).getString("cbillid"),i,lsSerino);
	//
	// }
	// }
	// }
	//
	// for(int j = 0;j<JsonArrays.length();j++)
	// {
	// String lsJsonInvCode =
	// ((JSONObject)(JsonArrays.get(j))).getString("invcode");
	// String lsJsonInvBatch =
	// ((JSONObject)(JsonArrays.get(j))).getString("vbatch");
	// Double ldJsonInvQty =
	// ((JSONObject)(JsonArrays.get(j))).getDouble("nnum");
	// if(lsJsonInvBatch==null||lsJsonInvBatch.equals("")||lsJsonInvBatch.equals("null"))
	// {
	// lsJsonInvBatch="批次未指定";
	// }
	// if(lsBarInvCode.equals(lsJsonInvCode))
	// {
	// GetRemovedTaskQty(ldJsonInvQty,
	// ((JSONObject)(JsonArrays.get(j))).getString("cbillid"),j,lsSerino);
	// }
	// }
	//
	// }
	// DEL BY WUQIONG E

	// ADD BY WUQIONG START
	// 完成扫描后修改任务里的项目
	private void GetModTaskList(SplitBarcode bar,
			JSONObject jsonCheckGetBillCode) throws JSONException {
		String lsBarInvCode = bar.cInvCode;
		String lsBarBacth = bar.cBatch;
		String lsBillCode = "";
		String lsSerino = bar.cSerino;

		JSONArray JsonArrays = (JSONArray) jonsBody.get("TransBillBody");
		// jsonArrRemove = new ArrayList();

		String Taskhid = jsonCheckGetBillCode.getString("cbillid");
		String Taskbid = jsonCheckGetBillCode.getString("cbill_bid");

		for (int i = 0; i < JsonArrays.length(); i++) {
			String lsaccid = ((JSONObject) (JsonArrays.get(i)))
					.getString("accid");
			String lsJsonInvCode = ((JSONObject) (JsonArrays.get(i)))
					.getString("invcode");
			String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(i)))
					.getString("vbatch");
			// Double ldJsonInvQty =
			// ((JSONObject)(JsonArrays.get(i))).getDouble("nnum");//CAIXY
			String nnum = ((JSONObject) (JsonArrays.get(i))).getString("nnum");
			String norderoutnum = ((JSONObject) (JsonArrays.get(i)))
					.getString("norderoutnum");
			String snnum = "0";

			if (!norderoutnum.equals("null")) {
				snnum = (norderoutnum.replaceAll("\\.0", ""));
			}

			int shouldoutnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

			String Tasknnum = shouldoutnum + "";

			if (lsJsonInvBatch == null || lsJsonInvBatch.equals("")
					|| lsJsonInvBatch.equals("null")) {
				lsJsonInvBatch = getString(R.string.PiCiWeiZhiDing);
			}
			if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0")
					&& lsaccid.equals(bar.AccID))// caixy
			{
				if (lsBarBacth.equals(lsJsonInvBatch)) {
					if (((JSONObject) (JsonArrays.get(i))).getString(
							"cbill_bid").equals(Taskbid)
							&& ((JSONObject) (JsonArrays.get(i))).getString(
									"cbillid").equals(Taskhid)) {
						GetModTaskQty(Double.valueOf(Tasknnum),
								((JSONObject) (JsonArrays.get(i)))
										.getString("cbill_bid"),
								((JSONObject) (JsonArrays.get(i)))
										.getString("cbillid"), i, lsSerino);
						return;
					}

				}
			}
		}

		for (int j = 0; j < JsonArrays.length(); j++) {
			String lsJsonInvCode = ((JSONObject) (JsonArrays.get(j)))
					.getString("invcode");
			String lsaccid = ((JSONObject) (JsonArrays.get(j)))
					.getString("accid");
			String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(j)))
					.getString("vbatch");
			String nnum = ((JSONObject) (JsonArrays.get(j))).getString("nnum");
			String norderoutnum = ((JSONObject) (JsonArrays.get(j)))
					.getString("norderoutnum");
			String snnum = "0";

			if (!norderoutnum.equals("null")) {
				snnum = (norderoutnum.replaceAll("\\.0", ""));
			}

			int shouldoutnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

			String Tasknnum = shouldoutnum + "";

			if (lsJsonInvBatch == null || lsJsonInvBatch.equals("")
					|| lsJsonInvBatch.equals("null")) {
				lsJsonInvBatch = getString(R.string.PiCiWeiZhiDing);
				if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0")
						&& lsaccid.equals(bar.AccID)) {
					if (((JSONObject) (JsonArrays.get(j))).getString(
							"cbill_bid").equals(Taskbid)
							&& ((JSONObject) (JsonArrays.get(j))).getString(
									"cbillid").equals(Taskhid)) {
						GetModTaskQty(Double.valueOf(Tasknnum),
								((JSONObject) (JsonArrays.get(j)))
										.getString("cbill_bid"),
								((JSONObject) (JsonArrays.get(j)))
										.getString("cbillid"), j, lsSerino);
						return;
					}

				}
			}

		}

	}

	// ADD BY WUQIONG END

	// DEL BY WUQIONG S
	// private String iRemoveTaskIndex = "";

	// private void GetRemovedTaskQty(Double Qty, String sBillID, int
	// iIndex,String lsSerino) throws JSONException
	// {
	// iRemoveTaskIndex="";
	// if(lstSaveBody==null || lstSaveBody.size() < 1)
	// {
	// return;
	// }
	//
	// for(int i = 0;i<lstSaveBody.size();i++)
	// {
	// Double inQty = 0.0;
	// Map<String,Object> temp = (Map<String,Object>) lstSaveBody.get(i);
	// if(temp.get("cinvbasid").equals(currentObj.Invbasdoc()))
	// {
	// if(temp.get("csourcebillhid").equals(sBillID))
	// inQty += Double.valueOf(temp.get("spacenum").toString());
	// }
	//
	// if(inQty.toString().equals(Qty.toString()))
	// {
	// //jsonArrRemove.add(iIndex);
	// iRemoveTaskIndex = String.valueOf(iIndex);
	//
	// JSONArray JsonTaskArrays=(JSONArray)jonsBody.get("TransBillBody");
	// JSONObject JsonRemoveTaskItem = (JSONObject)JsonTaskArrays.get(iIndex);
	// String lsKey = JsonRemoveTaskItem.getString("cbillid") +
	// JsonRemoveTaskItem.getString("invcode") +
	// //JsonRemoveTaskItem.getString("vbatch") +
	// JsonRemoveTaskItem.getString("crowno")+
	// lsSerino;
	// JsonRemoveTaskData.put(lsKey, JsonRemoveTaskItem);
	// }
	//
	// }
	// }
	// DEL BY WUQIONG E

	// ADD BY WUQIONG START
	private String iModTaskIndex = "";

	private void GetModTaskQty(Double Qty, String sBillBID, String sBillHID,
			int iIndex, String lsSerino) throws JSONException {
		// iModTaskIndex="";
		JSONObject JsonModTaskItem = new JSONObject();
		if (lstSaveBody == null || lstSaveBody.size() < 1) {
			return;
		}

		for (int i = 0; i < lstSaveBody.size(); i++) {
			Double inQty = 0.0;
			Map<String, Object> temp = (Map<String, Object>) lstSaveBody.get(i);
			if (temp.get("cinvbasid").equals(currentObj.Invbasdoc())) {

				// mod caixy s 解决扫描数量错误
				// if(temp.get("csourcebillhid").equals(sBillID))
				String str = temp.get("BarCode").toString();

				String[] val = str.split("\\|");
				String SaveSerino = (val[3]);//
				String Serino = currentObj.GetSerino().toString();

				if (temp.get("csourcebillbid").equals(sBillBID)
						&& temp.get("csourcebillhid").equals(sBillHID)
						&& SaveSerino.equals(Serino))
				// mod caixy e 解决扫描数量错误
				{
					inQty += Double.valueOf(temp.get("spacenum").toString());
					ScanedQty = Integer
							.valueOf(temp.get("spacenum").toString());// add
																		// caixy
																		// e
																		// 解决扫描数量错误
				}
			}

			if (inQty.toString().equals(Qty.toString())) {
				iModTaskIndex = String.valueOf(iIndex);
				JSONArray JsonTaskArrays = (JSONArray) jonsBody
						.get("TransBillBody");
				JsonModTaskItem = (JSONObject) JsonTaskArrays.get(iIndex);
				String lsKey = JsonModTaskItem.getString("cbill_bid")
						+ JsonModTaskItem.getString("invcode") +
						// JsonRemoveTaskItem.getString("vbatch") +
						JsonModTaskItem.getString("crowno") + lsSerino;
				JsonModTaskData.put(lsKey, JsonModTaskItem);

			} else if (inQty != 0.0) {

				iModTaskIndex = String.valueOf(iIndex);

				JSONArray JsonTaskArrays = (JSONArray) jonsBody
						.get("TransBillBody");
				// JSONObject JsonModTaskItem =
				// (JSONObject)JsonTaskArrays.get(iIndex);

				// String lsReModKey =
				// ((JSONObject)JsonTaskArrays.get(iIndex)).get("cbillid").toString()
				// +
				// ((JSONObject)JsonTaskArrays.get(iIndex)).get("invcode").toString()
				// +
				// ((JSONObject)JsonTaskArrays.get(iIndex)).get("crowno").toString();
				//
				// if (JsonModTaskData.length()>0)
				// {
				// if(!JsonModTaskData.getString(lsReModKey).toString().equals(""))
				// {
				// Newnnum = Newnnum + 1;
				// }
				// }
				//
				JsonModTaskItem = new JSONObject();

				// JsonModTaskItem.put("vbatch",((JSONObject)JsonTaskArrays.get(iIndex)).get("vbatch").toString());
				// JsonModTaskItem.put("naddpricerate",((JSONObject)JsonTaskArrays.get(iIndex)).get("naddpricerate").toString());
				// JsonModTaskItem.put("coutcbid",((JSONObject)JsonTaskArrays.get(iIndex)).get("coutcbid").toString());
				// JsonModTaskItem.put("cinwhid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cinwhid").toString());
				// JsonModTaskItem.put("cbillid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cbillid").toString());
				// JsonModTaskItem.put("measname",((JSONObject)JsonTaskArrays.get(iIndex)).get("measname").toString());
				// JsonModTaskItem.put("coutcorpid",((JSONObject)JsonTaskArrays.get(iIndex)).get("coutcorpid").toString());
				// JsonModTaskItem.put("cbill_bid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cbill_bid").toString());
				// JsonModTaskItem.put("cquoteunitid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cquoteunitid").toString());
				// JsonModTaskItem.put("fallocflag",((JSONObject)JsonTaskArrays.get(iIndex)).get("fallocflag").toString());
				// JsonModTaskItem.put("vbdef1",((JSONObject)JsonTaskArrays.get(iIndex)).get("vbdef1").toString());
				// JsonModTaskItem.put("cininvid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cininvid").toString());
				// JsonModTaskItem.put("crelation_bid",((JSONObject)JsonTaskArrays.get(iIndex)).get("crelation_bid").toString());
				// JsonModTaskItem.put("ctakeoutcorpid",((JSONObject)JsonTaskArrays.get(iIndex)).get("ctakeoutcorpid").toString());
				// JsonModTaskItem.put("ctakeoutinvid",((JSONObject)JsonTaskArrays.get(iIndex)).get("ctakeoutinvid").toString());
				// JsonModTaskItem.put("crelationid",((JSONObject)JsonTaskArrays.get(iIndex)).get("crelationid").toString());
				// JsonModTaskItem.put("vcode",((JSONObject)JsonTaskArrays.get(iIndex)).get("vcode").toString());
				// JsonModTaskItem.put("invname",((JSONObject)JsonTaskArrays.get(iIndex)).get("invname").toString());
				// JsonModTaskItem.put("nquoteunitnum",((JSONObject)JsonTaskArrays.get(iIndex)).get("nquoteunitnum").toString());
				// JsonModTaskItem.put("invcode",((JSONObject)JsonTaskArrays.get(iIndex)).get("invcode").toString());
				// JsonModTaskItem.put("coutwhid",((JSONObject)JsonTaskArrays.get(iIndex)).get("coutwhid").toString());
				// JsonModTaskItem.put("ctakeoutwhid",((JSONObject)JsonTaskArrays.get(iIndex)).get("ctakeoutwhid").toString());
				// JsonModTaskItem.put("cinvbasid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cinvbasid").toString());
				//
				// JsonModTaskItem.put("cincbid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cincbid").toString());
				// JsonModTaskItem.put("ctakeoutcbid",((JSONObject)JsonTaskArrays.get(iIndex)).get("ctakeoutcbid").toString());
				// JsonModTaskItem.put("pk_arrivearea",((JSONObject)JsonTaskArrays.get(iIndex)).get("pk_arrivearea").toString());
				// JsonModTaskItem.put("cincorpid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cincorpid").toString());
				// JsonModTaskItem.put("crowno",((JSONObject)JsonTaskArrays.get(iIndex)).get("crowno").toString());
				// JsonModTaskItem.put("coutinvid",((JSONObject)JsonTaskArrays.get(iIndex)).get("coutinvid").toString());
				//
				//
				// JsonModTaskItem.put("norderoutnum",((JSONObject)JsonTaskArrays.get(iIndex)).get("norderoutnum").toString());
				// JsonModTaskItem.put("cfirstid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cfirstid").toString());
				// JsonModTaskItem.put("cfirsttypecode",((JSONObject)JsonTaskArrays.get(iIndex)).get("cfirsttypecode").toString());
				// JsonModTaskItem.put("ctypecode",((JSONObject)JsonTaskArrays.get(iIndex)).get("ctypecode").toString());
				// JsonModTaskItem.put("cfirstbid",((JSONObject)JsonTaskArrays.get(iIndex)).get("cfirstbid").toString());
				// JsonModTaskItem.put("nquoteunitrate",((JSONObject)JsonTaskArrays.get(iIndex)).get("nquoteunitrate").toString());
				// JsonModTaskItem.put("nordershouldoutnum",((JSONObject)JsonTaskArrays.get(iIndex)).get("nordershouldoutnum").toString());
				//
				JsonModTaskItem.put("vbdef1", ((JSONObject) JsonTaskArrays
						.get(iIndex)).get("vbdef1").toString());
				JsonModTaskItem.put(
						"nquoteunitrate",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"nquoteunitrate").toString());
				JsonModTaskItem.put("accid", ((JSONObject) JsonTaskArrays
						.get(iIndex)).get("accid").toString());
				JsonModTaskItem.put("vbatch", ((JSONObject) JsonTaskArrays
						.get(iIndex)).get("vbatch").toString());
				JsonModTaskItem.put("crowno", ((JSONObject) JsonTaskArrays
						.get(iIndex)).get("crowno").toString());
				JsonModTaskItem.put(
						"cfirstid",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"cfirstid").toString());
				JsonModTaskItem.put("cbillid", ((JSONObject) JsonTaskArrays
						.get(iIndex)).get("cbillid").toString());
				JsonModTaskItem.put(
						"cfirstbid",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"cfirstbid").toString());
				JsonModTaskItem.put(
						"cbill_bid",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"cbill_bid").toString());
				JsonModTaskItem.put(
						"cfirsttypecode",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"cfirsttypecode").toString());
				JsonModTaskItem.put(
						"ctypecode",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"ctypecode").toString());
				JsonModTaskItem.put(
						"cquoteunitid",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"cquoteunitid").toString());
				JsonModTaskItem.put(
						"nquoteunitnum",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"nquoteunitnum").toString());
				JsonModTaskItem.put(
						"nordershouldoutnum",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"nordershouldoutnum").toString());
				JsonModTaskItem.put(
						"pk_arrivearea",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"pk_arrivearea").toString());
				JsonModTaskItem.put("vcode", ((JSONObject) JsonTaskArrays
						.get(iIndex)).get("vcode").toString());
				JsonModTaskItem.put(
						"cinvbasid",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"cinvbasid").toString());
				JsonModTaskItem.put(
						"norderoutnum",
						((JSONObject) JsonTaskArrays.get(iIndex)).get(
								"norderoutnum").toString());
				JsonModTaskItem.put("invname", ((JSONObject) JsonTaskArrays
						.get(iIndex)).get("invname").toString());
				JsonModTaskItem.put("invcode", ((JSONObject) JsonTaskArrays
						.get(iIndex)).get("invcode").toString());

				// 需要修改
				// String Newnnum =
				// ((JSONObject)JsonTaskArrays.get(iIndex)).get("nnum").toString();
				// //减已扫描件数
				// JsonModTaskItem.put("nnum",Newnnum);
				// mod caixy s 解决扫描数量问题
				// JsonModTaskItem.put("nnum",temp.get("spacenum").toString());
				JsonModTaskItem.put("nnum", inQty);
				// mod caixy s 解决扫描数量问题

				String lsKey = JsonModTaskItem.getString("cbill_bid")
						+ JsonModTaskItem.getString("invcode") +
						// JsonRemoveTaskItem.getString("vbatch") +
						JsonModTaskItem.getString("crowno") + lsSerino;
				JsonModTaskData.put(lsKey, JsonModTaskItem);
			}

		}

	}

	// ADD BY WUQIONG END

	// 确认如果有上游单据,那么判断是否超过其数量
	private boolean ConformDetailQty(Double Qty, String sBillBID,
			String sBillHID, int iIndex) throws JSONException {
		ScanInvOK = "1";
		if (lstSaveBody == null || lstSaveBody.size() < 1) {
			return true;
		}

		// String lsBillID = jsonBillBody.getString("cbillid");

		// jonsBody = new JSONObject();
		// JSONArray jsonArrNew = new JSONArray();
		Double inQty = 1.0;

		// 判断是否序列号内控制
		for (int j = 0; j < lstSaveBody.size(); j++) {
			Map<String, Object> temp1 = (Map<String, Object>) lstSaveBody
					.get(j);
			if (temp1.get("SeriNo").equals(currentObj.GetSerino())
					&& temp1.get("InvCode").equals(currentObj.getInvCode())
					&& temp1.get("vbatchcode").equals(currentObj.GetBatch())
					&& temp1.get("spacenum").equals("0")
					&& temp1.get("csourcebillbid").equals(sBillBID)
					&& temp1.get("csourcebillhid").equals(sBillHID)) {
				return true;
			}
		}

		for (int i = 0; i < lstSaveBody.size(); i++) {

			Map<String, Object> temp = (Map<String, Object>) lstSaveBody.get(i);
			if (temp.get("cinvbasid").equals(currentObj.Invbasdoc())) {
				if (temp.get("csourcebillbid").equals(sBillBID)
						&& temp.get("csourcebillhid").equals(sBillHID)
						&& temp.get("spacenum").equals("0"))
					// inQty += Double.valueOf(temp.get("spacenum").toString());
					inQty += 1;
			}

		}

		if (inQty > Qty) {
			return false;
		}
		return true;
	}

	// 判断该扫描条码属于哪个单据号
	private JSONObject CheckGetBillCode(SplitBarcode bar) throws JSONException {
		String lsBarInvCode = bar.cInvCode;
		String lsBarBacth = bar.cBatch;
		String lsBillCode = "";

		JSONArray JsonArrays = (JSONArray) jonsBody.get("TransBillBody");
		// jsonArrRemove = new ArrayList();

		for (int i = 0; i < JsonArrays.length(); i++) {

			String lsJsonInvCode = ((JSONObject) (JsonArrays.get(i)))
					.getString("invcode");
			String lsaccid = ((JSONObject) (JsonArrays.get(i)))
					.getString("accid");
			String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(i)))
					.getString("vbatch");
			// Double ldJsonInvQty =
			// ((JSONObject)(JsonArrays.get(i))).getDouble("nnum");
			String nnum = ((JSONObject) (JsonArrays.get(i))).getString("nnum");
			String norderoutnum = ((JSONObject) (JsonArrays.get(i)))
					.getString("norderoutnum");
			String snnum = "0";

			if (!norderoutnum.equals("null")) {
				snnum = (norderoutnum.replaceAll("\\.0", ""));
			}

			int shouldoutnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

			String Tasknnum = shouldoutnum + "";

			if (lsJsonInvBatch == null || lsJsonInvBatch.equals("")
					|| lsJsonInvBatch.equals("null")) {
				lsJsonInvBatch = getString(R.string.PiCiWeiZhiDing);
			}
			if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0")
					&& lsaccid.equals(bar.AccID)) {
				if (lsBarBacth.equals(lsJsonInvBatch)) {
					// lsCheckGetBillCode =
					// ((JSONObject)(JsonArrays.get(i))).getString("vcode");
					if (ConformDetailQty(Double.valueOf(Tasknnum),
							((JSONObject) (JsonArrays.get(i)))
									.getString("cbill_bid"),
							((JSONObject) (JsonArrays.get(i)))
									.getString("cbillid"), i)) {
						// jonsBody.remove();
						// jonsBody.put("TransBillBody", jsonArrNew);
						// jsonArrRemove.add(jsonArrRemove.size(), i);
						return (JSONObject) JsonArrays.get(i);
					}
					// else
					// jsonArrNew.put((JSONObject)JsonArrays.get(i));
				}
			}
		}

		for (int j = 0; j < JsonArrays.length(); j++) {

			String lsJsonInvCode = ((JSONObject) (JsonArrays.get(j)))
					.getString("invcode");
			String lsaccid = ((JSONObject) (JsonArrays.get(j)))
					.getString("accid");
			String lsJsonInvBatch = ((JSONObject) (JsonArrays.get(j)))
					.getString("vbatch");
			// Double ldJsonInvQty =
			// ((JSONObject)(JsonArrays.get(j))).getDouble("nnum");
			String nnum = ((JSONObject) (JsonArrays.get(j))).getString("nnum");
			String norderoutnum = ((JSONObject) (JsonArrays.get(j)))
					.getString("norderoutnum");
			String snnum = "0";

			if (!norderoutnum.equals("null")) {
				snnum = (norderoutnum.replaceAll("\\.0", ""));
			}

			int shouldoutnum = Integer.valueOf(nnum) - Integer.valueOf(snnum);

			String Tasknnum = shouldoutnum + "";

			if (lsJsonInvBatch == null || lsJsonInvBatch.equals("")
					|| lsJsonInvBatch.equals("null")) {
				lsJsonInvBatch = getString(R.string.PiCiWeiZhiDing);
				if (lsBarInvCode.equals(lsJsonInvCode) && !Tasknnum.equals("0")
						&& lsaccid.equals(bar.AccID)) {
					// lsCheckGetBillCode =
					// ((JSONObject)(JsonArrays.get(j))).getString("vcode");
					if (ConformDetailQty(Double.valueOf(Tasknnum),
							((JSONObject) (JsonArrays.get(j)))
									.getString("cbill_bid"),
							((JSONObject) (JsonArrays.get(j)))
									.getString("cbillid"), j)) {
						// jonsBody.put("TransBillBody", jsonArrNew);
						// jsonArrRemove.add(jsonArrRemove.size(), j);
						return (JSONObject) JsonArrays.get(j);
					}
					// else
					// jsonArrNew.put((JSONObject)JsonArrays.get(j));
				}
			}

			// else
			// jsonArrNew.put((JSONObject)JsonArrays.get(j));
		}

		// jonsBody = new JSONObject();
		// jonsBody.put("TransBillBody", jsonArrNew);

		return null;
	}

	private void BindingScanDetail(JSONObject jsonCheckGetBillCode,
			SplitBarcode bar, String sType,
			Map<String, Object> mapGetScanedDetail) throws JSONException {
		ArrayList<Map<String, Object>> lstCurrentBox = null;
		Map<String, Object> mapCurrentBox = new HashMap<String, Object>();
		Map<String, Object> mapScanDetail = new HashMap<String, Object>();

		if (lstSaveBody == null || lstSaveBody.size() < 1)
			lstSaveBody = new ArrayList<Map<String, Object>>();

		mapCurrentBox.put("CurrentBox", bar.currentBox);
		mapCurrentBox.put("TotalBox", bar.TotalBox);
		mapCurrentBox.put("FinishBarCode", bar.FinishBarCode);
		mapCurrentBox.put("BoxNum", Integer.parseInt(bar.currentBox) + "/"
				+ Integer.parseInt(bar.TotalBox));

		String lsKey = jsonCheckGetBillCode.getString("vcode") + bar.AccID
				+ bar.cInvCode + bar.cBatch + bar.cSerino;
		if (sType.equals("MOD") && mapGetScanedDetail != null) {
			lstCurrentBox = (ArrayList<Map<String, Object>>) mapGetScanedDetail
					.get(lsKey);
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
			mapScanDetail.put("TotalNum",
					Integer.parseInt(currentObj.totalID()));
			mapScanDetail.put("ScanedNum", lstCurrentBox.size());

			// 源单单据行号
			mapScanDetail.put("csourcerowno",
					jsonCheckGetBillCode.getString("crowno"));
			// 开始单表头
			mapScanDetail.put("cfirstbillhid",
					jsonCheckGetBillCode.getString("cfirstid"));
			// 源单表头
			mapScanDetail.put("csourcebillhid",
					jsonCheckGetBillCode.getString("cbillid"));
			// 开始单表体
			mapScanDetail.put("cfirstbillbid",
					jsonCheckGetBillCode.getString("cfirstbid"));
			// 源单表体
			mapScanDetail.put("csourcebillbid",
					jsonCheckGetBillCode.getString("cbill_bid"));
			// 开始单据类型
			mapScanDetail.put("cfirsttypecode",
					jsonCheckGetBillCode.getString("cfirsttypecode"));
			// 源单据类型
			mapScanDetail.put("csourcetypecode",
					jsonCheckGetBillCode.getString("ctypecode"));
			// 报价计量单位ID
			mapScanDetail.put("cquoteunitid",
					jsonCheckGetBillCode.getString("cquoteunitid"));
			// 报价计量单位换算率x
			if (jsonCheckGetBillCode.getString("nquoteunitrate") == null
					|| jsonCheckGetBillCode.getString("nquoteunitrate").equals(
							"")
					|| jsonCheckGetBillCode.getString("nquoteunitrate").equals(
							"null"))
				mapScanDetail.put("nquoteunitrate", 0);
			else
				mapScanDetail.put("nquoteunitrate",
						jsonCheckGetBillCode.getDouble("nquoteunitrate"));
			// 报价计量单位数量
			if (jsonCheckGetBillCode.getString("nquoteunitnum") == null
					|| jsonCheckGetBillCode.getString("nquoteunitnum").equals(
							"")
					|| jsonCheckGetBillCode.getString("nquoteunitnum").equals(
							"null"))
				mapScanDetail.put("nquoteunitnum", 0);
			else
				mapScanDetail.put("nquoteunitnum",
						jsonCheckGetBillCode.getDouble("nquoteunitnum"));
			// 订单累计应发未出库数量
			if (jsonCheckGetBillCode.getString("nordershouldoutnum") == null
					|| jsonCheckGetBillCode.getString("nordershouldoutnum")
							.equals("")
					|| jsonCheckGetBillCode.getString("nordershouldoutnum")
							.equals("null"))
				mapScanDetail.put("nordershouldoutnum", 0);
			else
				mapScanDetail.put("nordershouldoutnum",
						jsonCheckGetBillCode.getDouble("nordershouldoutnum"));
			// 收货地区
			mapScanDetail.put("pk_arrivearea",
					jsonCheckGetBillCode.getString("pk_arrivearea"));
			// 单据号
			mapScanDetail.put("vsourcebillcode",
					jsonCheckGetBillCode.getString("vcode"));
			// 存货基本标识
			mapScanDetail.put("cinvbasid",
					jsonCheckGetBillCode.getString("cinvbasid"));
			// 存货管理ID
			mapScanDetail.put("cinventoryid", currentObj.Invmandoc());
			// 自由项一
			mapScanDetail.put("vfree1", currentObj.vFree1());
			// 单据批次
			mapScanDetail.put("vbillbatchcode",
					jsonCheckGetBillCode.getString("vbatch"));
			// 批次
			mapScanDetail.put("vbatchcode", currentObj.GetBatch());

			// 该货位该存货编码批次有几件货?
			if (Integer.parseInt(currentObj.totalID()) == lstCurrentBox.size()) {
				mapScanDetail.put("spacenum", "1");
				mapScanDetail.put("box", "");
			}
			// mapScanDetail.put("spacenum", "1");
			else {
				mapScanDetail.put("spacenum", "0");
				mapScanDetail.put("box", "分包未完");

			}

			// mapScanDetail.put("spacenum", "1");

			// 单据号
			mapScanDetail.put("BillCode",
					jsonCheckGetBillCode.getString("vcode"));
			// ADD BY WUQIONG START
			// 自定义项1
			mapScanDetail.put("vbdef1",
					jsonCheckGetBillCode.getString("vbdef1"));
			// ADD BY WUQIONG END

			lstSaveBody.add(mapScanDetail);
			// jsonArrSaveBody.put(mapScanDetail);

			// ADD BY WUQIONG START
			// Tasknnum = 0;
			// listcount = lstSaveBody.size();JSONArray JsonArraysA = new
			// JSONArray();
			// try {
			// JsonArraysA=(JSONArray)jonsBody.get("TransBillBody");
			// for (int i =0; i<JsonArraysA.length();i++)
			// {
			// Tasknnum = Tasknnum +
			// Integer.valueOf(((JSONObject)(JsonArraysA.get(i))).getString("nnum"));
			// }
			//
			// } catch (JSONException e1) {
			// // TODO Auto-generated catch block
			// Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
			// e1.printStackTrace();
			// //ADD CAIXY TEST START
			// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// //ADD CAIXY TEST END
			// return;
			// }
			listcount = lstSaveBody.size();
			tvscancount.setText("总共" + Tasknnum + "件 | " + "已扫" + listcount
					+ "件 | " + "未扫" + (Tasknnum - listcount) + "件");
			// ADD BY WUQIONG END
		}

		MyListAdapter listItemAdapter = new MyListAdapter(StockTransScan.this,
				lstSaveBody,// 数据源
				R.layout.vlisttransscanitem, new String[] { "InvCode",
						"InvName", "Batch", "AccID", "TotalNum", "BarCode",
						"SeriNo", "BillCode", "ScanedNum", "box" }, new int[] {
						R.id.txtTransScanInvCode, R.id.txtTransScanInvName,
						R.id.txtTransScanBatch, R.id.txtTransScanAccId,
						R.id.txtTransScanTotalNum, R.id.txtTransScanBarCode,
						R.id.txtTransScanSeriNo, R.id.txtTransScanBillCode,
						R.id.txtTransScanScanCount, R.id.txtTransBox });
		// SimpleAdapter listItemAdapter = new
		// SimpleAdapter(StockTransScan.this,lstSaveBody,//数据源
		// R.layout.vlisttransscanitem,
		// new String[] {"InvCode","InvName","Batch","AccID","TotalNum",
		// "BarCode","SeriNo","BillCode","ScanedNum"},
		// new int[] {R.id.txtTransScanInvCode,R.id.txtTransScanInvName,
		// R.id.txtTransScanBatch,R.id.txtTransScanAccId,
		// R.id.txtTransScanTotalNum,R.id.txtTransScanBarCode,
		// R.id.txtTransScanSeriNo,R.id.txtTransScanBillCode,
		// R.id.txtTransScanScanCount}
		// );
		lvScanDetail.setAdapter(listItemAdapter);
		// SetListViewBgColor();
	}

	private void ReSetTaskListData() throws JSONException {
		JSONArray JsonArrays = (JSONArray) jonsBody.get("TransBillBody");
		JSONArray JsonArrNew = new JSONArray();
		JSONArray JsonArrMod = new JSONArray();
		// jsonArrRemove = new ArrayList();

		for (int i = 0; i < JsonArrays.length(); i++) {
			// DEL BY WUQIONG S
			// if(!iRemoveTaskIndex.equals(""))
			// {
			// if(i!=Integer.parseInt(iRemoveTaskIndex))
			// {
			// JsonArrNew.put((JSONObject)JsonArrays.get(i));
			// }
			// }
			// DEL BY WUQIONG E

			// ADD BY WUQIONG START
			if (!iModTaskIndex.equals("")) {
				if (i != Integer.parseInt(iModTaskIndex)) {
					JsonArrNew.put((JSONObject) JsonArrays.get(i));
				} else {

					JSONObject jObj = new JSONObject();
					// jObj.put("vbatch",((JSONObject)JsonArrays.get(i)).get("vbatch").toString());
					// jObj.put("naddpricerate",((JSONObject)JsonArrays.get(i)).get("naddpricerate").toString());
					// jObj.put("coutcbid",((JSONObject)JsonArrays.get(i)).get("coutcbid").toString());
					// jObj.put("cinwhid",((JSONObject)JsonArrays.get(i)).get("cinwhid").toString());
					// jObj.put("cbillid",((JSONObject)JsonArrays.get(i)).get("cbillid").toString());
					// jObj.put("measname",((JSONObject)JsonArrays.get(i)).get("measname").toString());
					// jObj.put("coutcorpid",((JSONObject)JsonArrays.get(i)).get("coutcorpid").toString());
					// jObj.put("cbill_bid",((JSONObject)JsonArrays.get(i)).get("cbill_bid").toString());
					// jObj.put("cquoteunitid",((JSONObject)JsonArrays.get(i)).get("cquoteunitid").toString());
					// jObj.put("fallocflag",((JSONObject)JsonArrays.get(i)).get("fallocflag").toString());
					// jObj.put("vbdef1",((JSONObject)JsonArrays.get(i)).get("vbdef1").toString());
					// jObj.put("cininvid",((JSONObject)JsonArrays.get(i)).get("cininvid").toString());
					// jObj.put("crelation_bid",((JSONObject)JsonArrays.get(i)).get("crelation_bid").toString());
					// jObj.put("ctakeoutcorpid",((JSONObject)JsonArrays.get(i)).get("ctakeoutcorpid").toString());
					// jObj.put("ctakeoutinvid",((JSONObject)JsonArrays.get(i)).get("ctakeoutinvid").toString());
					// jObj.put("crelationid",((JSONObject)JsonArrays.get(i)).get("crelationid").toString());
					// jObj.put("vcode",((JSONObject)JsonArrays.get(i)).get("vcode").toString());
					// jObj.put("invname",((JSONObject)JsonArrays.get(i)).get("invname").toString());
					// jObj.put("nquoteunitnum",((JSONObject)JsonArrays.get(i)).get("nquoteunitnum").toString());
					// jObj.put("invcode",((JSONObject)JsonArrays.get(i)).get("invcode").toString());
					// jObj.put("coutwhid",((JSONObject)JsonArrays.get(i)).get("coutwhid").toString());
					// jObj.put("ctakeoutwhid",((JSONObject)JsonArrays.get(i)).get("ctakeoutwhid").toString());
					// jObj.put("cinvbasid",((JSONObject)JsonArrays.get(i)).get("cinvbasid").toString());
					// jObj.put("cincbid",((JSONObject)JsonArrays.get(i)).get("cincbid").toString());
					// jObj.put("ctakeoutcbid",((JSONObject)JsonArrays.get(i)).get("ctakeoutcbid").toString());
					// jObj.put("pk_arrivearea",((JSONObject)JsonArrays.get(i)).get("pk_arrivearea").toString());
					// jObj.put("cincorpid",((JSONObject)JsonArrays.get(i)).get("cincorpid").toString());
					// jObj.put("crowno",((JSONObject)JsonArrays.get(i)).get("crowno").toString());
					// jObj.put("coutinvid",((JSONObject)JsonArrays.get(i)).get("coutinvid").toString());
					//
					// jObj.put("norderoutnum",((JSONObject)JsonArrays.get(i)).get("norderoutnum").toString());
					// jObj.put("cfirstid",((JSONObject)JsonArrays.get(i)).get("cfirstid").toString());
					// jObj.put("cfirsttypecode",((JSONObject)JsonArrays.get(i)).get("cfirsttypecode").toString());
					// jObj.put("ctypecode",((JSONObject)JsonArrays.get(i)).get("ctypecode").toString());
					// jObj.put("cfirstbid",((JSONObject)JsonArrays.get(i)).get("cfirstbid").toString());
					// jObj.put("nquoteunitrate",((JSONObject)JsonArrays.get(i)).get("nquoteunitrate").toString());
					// jObj.put("nordershouldoutnum",((JSONObject)JsonArrays.get(i)).get("nordershouldoutnum").toString());

					jObj.put("vbdef1",
							((JSONObject) JsonArrays.get(i)).get("vbdef1")
									.toString());
					jObj.put("nquoteunitrate", ((JSONObject) JsonArrays.get(i))
							.get("nquoteunitrate").toString());
					jObj.put("accid",
							((JSONObject) JsonArrays.get(i)).get("accid")
									.toString());
					jObj.put("vbatch",
							((JSONObject) JsonArrays.get(i)).get("vbatch")
									.toString());
					jObj.put("crowno",
							((JSONObject) JsonArrays.get(i)).get("crowno")
									.toString());
					jObj.put("cfirstid",
							((JSONObject) JsonArrays.get(i)).get("cfirstid")
									.toString());
					jObj.put("cbillid",
							((JSONObject) JsonArrays.get(i)).get("cbillid")
									.toString());
					jObj.put("cfirstbid",
							((JSONObject) JsonArrays.get(i)).get("cfirstbid")
									.toString());
					jObj.put("cbill_bid",
							((JSONObject) JsonArrays.get(i)).get("cbill_bid")
									.toString());
					jObj.put("cfirsttypecode", ((JSONObject) JsonArrays.get(i))
							.get("cfirsttypecode").toString());
					jObj.put("ctypecode",
							((JSONObject) JsonArrays.get(i)).get("ctypecode")
									.toString());
					jObj.put("cquoteunitid", ((JSONObject) JsonArrays.get(i))
							.get("cquoteunitid").toString());
					jObj.put("nquoteunitnum", ((JSONObject) JsonArrays.get(i))
							.get("nquoteunitnum").toString());
					jObj.put(
							"nordershouldoutnum",
							((JSONObject) JsonArrays.get(i)).get(
									"nordershouldoutnum").toString());
					jObj.put("pk_arrivearea", ((JSONObject) JsonArrays.get(i))
							.get("pk_arrivearea").toString());
					jObj.put("vcode",
							((JSONObject) JsonArrays.get(i)).get("vcode")
									.toString());
					jObj.put("cinvbasid",
							((JSONObject) JsonArrays.get(i)).get("cinvbasid")
									.toString());
					jObj.put("norderoutnum", ((JSONObject) JsonArrays.get(i))
							.get("norderoutnum").toString());
					jObj.put("invname",
							((JSONObject) JsonArrays.get(i)).get("invname")
									.toString());
					jObj.put("invcode",
							((JSONObject) JsonArrays.get(i)).get("invcode")
									.toString());

					// 需要修改
					String nnum = (((JSONObject) JsonArrays.get(i)).get("nnum")
							.toString()); // 减已扫描件数
					// Double ldJsonInvQty =
					// ((JSONObject)JsonArrays.get(i)).get("nnum");
					// String sScanedQty = ScanedQty.toString();
					int innum = Integer.valueOf(nnum) - ScanedQty;

					String inewnnum = innum + "";

					jObj.put("nnum", inewnnum.toString());

					// JsonArrMod.put(jObj);
					JsonArrNew.put(jObj);
				}
			}
			// ADD BY WUQIONG END
		}
		// MOD BY WUQIONG S
		if (!iModTaskIndex.equals("")) {
			jonsBody = new JSONObject();
			jonsBody.put("Status", true);
			jonsBody.put("TransBillBody", JsonArrNew);
			// if(!iRemoveTaskIndex.equals(""))
			// {
			// jonsBody.put("RemoveTaskData", JsonRemoveTaskData);
			// }
			// if(!iModTaskIndex.equals(""))
			// {
			// jonsBody.put("ModTaskData", JsonModTaskData);
			// }

			getTaskListData(jonsBody);
		}
		// MOD BY WUQIONG E

	}

	private void SetListViewBgColor() {
		// ListView lstView = new ListView(StockTransScan.this);
		ListAdapter lstAdapter = lvScanDetail.getAdapter();
		// View lstItemView = (View)lstAdapter.getItem(0);
		View lstItemView = lstAdapter.getView(0, null, lvScanDetail);
		lstItemView.setBackgroundColor(Color.RED);
		lstItemView.refreshDrawableState();
		lvScanDetail.refreshDrawableState();
	}

	/**
	 * 判断该条码是否已经被扫描过了
	 * 
	 * @return 如果为true 代表没有被扫描过,如果false 代表已经被扫描过了
	 * @throws JSONException
	 */
	private Boolean CheckHasScaned(JSONObject jsonCheckGetBillCode,
			SplitBarcode bar) throws JSONException {

		ListAdapter ScanDetailAdapter = lvScanDetail.getAdapter();

		String lsKey = jsonCheckGetBillCode.getString("vcode") + bar.AccID
				+ bar.cInvCode + bar.cBatch + bar.cSerino;
		if (ScanDetailAdapter == null || ScanDetailAdapter.getCount() < 1) {
			BindingScanDetail(jsonCheckGetBillCode, bar, "ADD", null);
			return true;
		}
		for (int i = 0; i < ScanDetailAdapter.getCount(); i++) {
			Map<String, Object> mapScanDetail = (Map<String, Object>) ScanDetailAdapter
					.getItem(i);
			if (mapScanDetail.containsKey(lsKey)) {

				ArrayList<Map<String, Object>> lstCurrentDetail = (ArrayList<Map<String, Object>>) mapScanDetail
						.get(lsKey);
				for (int j = 0; j < lstCurrentDetail.size(); j++) {
					if (lstCurrentDetail.get(j).get("FinishBarCode").toString()
							.equals(bar.FinishBarCode))
						return false;
				}
				BindingScanDetail(jsonCheckGetBillCode, bar, "MOD",
						mapScanDetail);
				return true;
			}
			//增加扫描数量控制  蔡晓勇 s
			
			else
			{
				if(listcount>=500)
				{
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

	private boolean ConformBatch(String invcode, String batch, String AccID)
			throws JSONException, ParseException, IOException {
		// 获得当前存货的库存 Jonson
		JSONObject batchList = null;
		JSONObject para = new JSONObject();
		String CompanyCode = "";
		if (AccID.equals("A")) {
			if (tmpBillStatus.equals("Y")) {
				CompanyCode = PKcorpFrom;
			} else {
				CompanyCode = PKcorpTo;
			}
		} else if (AccID.equals("B")) {
			CompanyCode = "1";
		}
		para.put("FunctionName", "GetCurrentStock");
		para.put("CompanyCode", CompanyCode);
		para.put("STOrgCode", MainLogin.objLog.STOrgCode);
		// 这里的WareHouse需要让操作员选择.
		para.put("InvCode", invcode);
		para.put("TableName", "batch");

		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, "R.string.WiFiXinHaoCha", Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}
		batchList = Common.DoHttpQuery(para, "CommonQuery", AccID);

		if (batchList == null) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		if (!batchList.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		if (!batchList.getBoolean("Status")) {
			// Toast.makeText(this, batchList.getString("找不到对应的库存信息"),
			// Toast.LENGTH_LONG).show();
			Toast.makeText(this, R.string.ZhaoBuDaoDuiYingDeKuCunXinXi, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return false;
		}

		JSONArray jsarray = batchList.getJSONArray("batch");
		// ArrayList array =new ArrayList();
		// ArrayList arrayName=new ArrayList();
		// ArrayList arrayFree1=new ArrayList();
		// ArrayList arrayOrg=new ArrayList();
		// ArrayList arrayCompanyId=new ArrayList();
		for (int i = 0; i < jsarray.length(); i++) {
			if (batch.equals(jsarray.getJSONObject(i).getString("vlot"))) {
				if (tmpBillStatus.equals("Y")) {
					if (bar.AccID.equals("A")) {
						if (wareHousePKFromA.equals(jsarray.getJSONObject(i)
								.getString("cwarehouseid"))) {
							currentObj.SetvFree1(jsarray.getJSONObject(i)
									.getString("vfree1"));

							return true;
						}
					}
					if (bar.AccID.equals("B")) {
						if (wareHousePKFromB.equals(jsarray.getJSONObject(i)
								.getString("cwarehouseid"))) {
							currentObj.SetvFree1("");

							return true;
						}
					}

				} else {
					if (bar.AccID.equals("A")) {
						if (wareHousePKToA.equals(jsarray.getJSONObject(i)
								.getString("cwarehouseid"))) {
							currentObj.SetvFree1(jsarray.getJSONObject(i)
									.getString("vfree1"));
							return true;
						}
					}
					if (bar.AccID.equals("B")) {
						if (wareHousePKToB.equals(jsarray.getJSONObject(i)
								.getString("cwarehouseid"))) {
							currentObj.SetvFree1("");
							return true;
						}
					}

				}
			}
		}

		Toast.makeText(this, R.string.GaiHuoPinZaiNiXuanZeDeCangKuZhongBuCunZai, Toast.LENGTH_LONG)
				.show();
		// ADD CAIXY TEST START
		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// ADD CAIXY TEST END
		return false;

		// if(wareHousePKFrom!=null && !wareHousePKFrom.equals(""))
		// {
		// // Toast.makeText(this,
		// batchList.getString("该货品在你选择的仓库中不存在,请不要扫描该货品!"),
		// // Toast.LENGTH_LONG).show();
		// Toast.makeText(this,"该货品在你选择的仓库中不存在,请不要扫描该货品",
		// Toast.LENGTH_LONG).show();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// return false;
		// }

		// //需要异常处理一下。
		// if(array.size()!=0)
		// {
		// Object[] objs=array.toArray();
		// Object[] objsname=arrayName.toArray();
		// Object[] objvFree1List =arrayFree1.toArray();
		// Object[] objvOrg =arrayOrg.toArray();
		// Object[] objvcompanyId =arrayCompanyId.toArray();
		//
		// warehouseList = new String[objs.length];
		// warehouseNameList =new String[objs.length];
		// vFree1List =new String[objs.length];
		// OrgList=new String[objs.length];
		// companyIdList=new String[objs.length];
		//
		// for(int i=0;i< objsname.length;i++)
		// {
		// warehouseList[i]=objsname[i].toString();
		// }
		//
		// for(int i=0;i< objs.length;i++)
		// {
		// warehouseNameList[i]=objs[i].toString();
		// }
		// for(int i=0;i< objvFree1List.length;i++)
		// {
		// vFree1List[i]=objvFree1List[i].toString();
		// }
		// for(int i=0;i< objvOrg.length;i++)
		// {
		// OrgList[i]=objvOrg[i].toString();
		// }
		//
		// for(int i=0;i< objvcompanyId.length;i++)
		// {
		// companyIdList[i]=objvcompanyId[i].toString();
		// }
		//
		// return true;
		// }

		// return false;
	}

	// 为listview自定义适配器内部类
	public static class MyListAdapter extends BaseAdapter {
		private Context context = null;
		private LayoutInflater inflater = null;
		private List<Map<String, Object>> list = null;
		private String keyString[] = null;
		private String itemString0 = null; // 记录每个item中textview的值
		private String itemString1 = null;
		private String itemString2 = null;
		private int idValue[] = null;// id值

		public MyListAdapter(Context context, List<Map<String, Object>> list,
				int resource, String[] from, int[] to) {
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

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
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
				// if(!map.get("ScanedNum").toString().equals(map.get("TotalNum").toString()))
				// arg1.setBackgroundResource(R.color.lightpink);
				// else
				// arg1.setBackgroundColor(Color.TRANSPARENT);
			}

			return arg1;
		}

	}

}
