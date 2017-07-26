package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.SplitBarcode;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StockMove extends Activity {

	// Button btnDBOrder;

	ImageButton imgDBOrder;
	//Button btnSAOrder;
	Button btnScan;
	Button btnExit;
	Button btnList;
	Button btnSave;
	EditText txtDbInvCode;
	EditText txtSaleOrder;
	Button btnRW; // 任务按钮


	// String headJons;
	// JSONObject jonsHead; //源头单据表头
	JSONObject jonsBody; // 源头单据表体

	JSONObject saveJons;
	JSONObject currentWarehouse;

	String warehouseCode;
	String warehouseID = "";// 仓库ID
	String companyID;// 公司ID
	String OrgId; // 库存组织ID
	String accID = "";// 帐套号
	String vCode = "";

	JSONObject m_SerialNo = null;
	JSONObject m_ScanDetail = null;

	String hstable = "";

	Intent scanDetail = null;
	UUID uploadGuid = null;

	// ADD CAIXY TEST START
	// private SoundPool sp;//声明一个SoundPool
	// private int MainLogin.music;//定义一个int来设置suondID
	private writeTxt writeTxt; // 保存LOG文件
	// ADD CAIXY TEST END

	File file = null;
	File fileScan = null;
	String fileName = null;
	String fileNameScan = null;
	String ScanedFileName = null;
	String UserID = "";
	String ReScanHead = "1";
	String BillId = "";
	private String[] ExitNameList = null;
	private AlertDialog SelectButton = null;
	private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
	private ArrayList<String> ScanedBarcode = new ArrayList<String>();

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
							IniActivyMemor();
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

	// 获得所有的调出单表头
	private void btnDBOrderClick() throws ParseException, IOException,
			JSONException {
		Intent ViewGrid = new Intent(this, PdOrderList.class);
		startActivityForResult(ViewGrid, 98);
	}

	private void btnWarehouseClick() throws JSONException {
		String lgUser = MainLogin.objLog.LoginUser;
		String lgPwd = MainLogin.objLog.Password;
		String LoginString = MainLogin.objLog.LoginString;

		JSONObject para = new JSONObject();

		para.put("FunctionName", "GetWareHouseList");
		para.put("CompanyCode", MainLogin.objLog.CompanyCode);
		para.put("STOrgCode", MainLogin.objLog.STOrgCode);
		para.put("TableName", "warehouse");

		try {
			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
			if (rev == null) {
				// 网络通讯错误
				Toast.makeText(this, "错误！网络通讯错误", Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
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
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
			}

		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
		}

	}

	private void btnScanDetail() {
		// if(this.warehouseID == null || this.warehouseID == "")
		// {
		// Toast.makeText(this, "请先确认仓库", Toast.LENGTH_LONG).show();
		// return;
		// }

		scanDetail = new Intent(StockMove.this, StockMoveScan.class);

		if ( // 有来源单据的
		jonsBody != null) {
			if (saveJons == null) {
				saveJons = new JSONObject();
			}

			scanDetail.putExtra("haveOrder", true);
			// scanDetail.putExtra("jonSave", this.saveJons.toString());
			scanDetail.putExtra("wareHousePK", warehouseID);
			scanDetail.putExtra("AccID", accID);
			scanDetail.putExtra("OrgId", OrgId);
			scanDetail.putExtra("companyID", companyID);
			scanDetail.putExtra("OriJsonBody", jonsBody.toString());

			if (m_SerialNo != null) {
				scanDetail.putExtra("List", m_SerialNo.toString());// scanDetail.putExtra("ScanDetail",
																	// m_ScanDetail);
			}
			if (m_ScanDetail != null) {
				scanDetail.putExtra("ScanDetail", m_ScanDetail.toString());
			}
			scanDetail.putExtra("hashTable", hstable);
			scanDetail.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);

		} else// 无来源单据的
		{
			scanDetail.putExtra("haveOrder", false);
			if (this.saveJons == null) {
				this.saveJons = new JSONObject();
			}

			if (m_SerialNo != null) {
				scanDetail.putExtra("List", m_SerialNo.toString());// scanDetail.putExtra("ScanDetail",
																	// m_ScanDetail);
			}
			if (m_ScanDetail != null) {
				scanDetail.putExtra("ScanDetail", m_ScanDetail.toString());
			}

			scanDetail.putExtra("wareHousePK", warehouseID);
			scanDetail.putExtra("AccID", accID);
			scanDetail.putExtra("OrgId", OrgId);
			scanDetail.putExtra("companyID", companyID);
			scanDetail.putExtra("hashTable", hstable);
			scanDetail.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);

		}
		startActivityForResult(scanDetail, 1);
	}

	private Button.OnClickListener myListner = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgDBOrder: {
				try {
					btnDBOrderClick();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(StockMove.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(StockMove.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(StockMove.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case R.id.btnStockMoveSave: {

				try {
					SaveMoveStock();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(StockMove.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(StockMove.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(StockMove.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
				}
				break;
			}
			case R.id.btnScan: {
				SaveScanedHead();
				btnScanDetail();
				break;
			}
			
			
			case R.id.btnStockMoveExit: {
				Exit();
				break;
			}

			}
		}
	};

	// 取消按钮对话框事件
	private DialogInterface.OnClickListener listenExit = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			StockMove.this.finish();
			Common.ReScanErr = false;
			System.gc();
		}
	};

	// 取消按钮
	private void Exit() {

		ExitNameList = new String[2];
		ExitNameList[0] = "退出并保留缓存数据";
		ExitNameList[1] = "退出并删除缓存数据";

		SelectButton = new AlertDialog.Builder(this).setTitle(R.string.QueRenTuiChu)
				.setSingleChoiceItems(ExitNameList, -1, buttonOnClick)
				.setPositiveButton(R.string.QueRen, buttonOnClick)
				.setNegativeButton(R.string.QuXiao, buttonOnClick).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_move);
		// 13818762623
		imgDBOrder = (ImageButton) findViewById(R.id.imgDBOrder);
		this.setTitle("货位调整");
		imgDBOrder.setOnClickListener(myListner);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		txtDbInvCode = (EditText) findViewById(R.id.txtdbInvCode);

		// btnRW.setOnClickListener(myListner);


		btnScan = (Button) findViewById(R.id.btnScan);
		btnScan.setOnClickListener(myListner);

		btnSave = (Button) findViewById(R.id.btnStockMoveSave);
		btnSave.setOnClickListener(myListner);





		btnExit = (Button) findViewById(R.id.btnStockMoveExit);
		btnExit.setOnClickListener(myListner);

		// txtWarehouseName.setText(MainLogin.objLog.WhName);
		// warehouseCode =MainLogin.objLog.WhCode;
		// warehouseID=MainLogin.objLog.WhID;
		// txtWarehouseName.setEnabled(false);
		// btnWarehouseSelect.setEnabled(false);


		// btnSAOrder.setEnabled(false);
		txtDbInvCode.setOnKeyListener(listener);
		txtDbInvCode.requestFocus();
		// ADD CAIXY START
		// sp= new SoundPool(10, AudioManager.STREAM_SYSTEM,
		// 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
		// MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1);
		// //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		// ADD CAIXY END

		UserID = MainLogin.objLog.UserID;
		// String LogName = BillType + UserID + dfd.format(day)+".txt";
		ScanedFileName = "4Q" + UserID + ".txt";
		fileName = "/sdcard/DVQ/4Q" + UserID + ".txt";
		fileNameScan = "/sdcard/DVQ/4QScan" + UserID + ".txt";

		file = new File(fileName);
		fileScan = new File(fileNameScan);

		imgDBOrder.setFocusable(false);
		//btnSAOrder.setFocusable(false);
		btnScan.setFocusable(false);
		btnExit.setFocusable(false);
		btnSave.setFocusable(false);

		ReScanHead();
		MainMenu.cancelLoading();
	}

	OnKeyListener listener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int arg1, KeyEvent arg2) {
			{

				switch (v.getId()) {
				case id.txtdbInvCode:
					if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)// &&
																				// arg2.getAction()
																				// ==
																				// KeyEvent.ACTION_DOWN
					{
						if (txtDbInvCode.getText().toString().startsWith("A")
								|| txtDbInvCode.getText().toString()
										.startsWith("B")) {
							Intent ViewGrid = new Intent(StockMove.this,
									PdOrderList.class);
							ViewGrid.putExtra("BillCode", txtDbInvCode
									.getText().toString().replace("\n", ""));
							startActivityForResult(ViewGrid, 98);
							return true;
						} else {
							txtDbInvCode.setText("");
							Toast.makeText(StockMove.this, "扫描的单号有问题",
									Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							return true;

						}
					}

					break;
				}
			}
			return false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_move, menu);

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

	private void IniActivyMemor() {

		// headJons = null;
		// jonsHead = null; //源头单据表头
		jonsBody = null; // 源头单据表体
		saveJons = null;
		currentWarehouse = null;
		accID = null;
		companyID = null;
		BillId = "";
		OrgId = null;
		this.warehouseID = "";
		this.txtDbInvCode.setText("");
		
		this.m_ScanDetail = null;
		this.m_SerialNo = null;
		this.hstable = null;
		ScanedBarcode = new ArrayList<String>();
		if (file.exists()) {
			file.delete();
		}

		if (fileScan.exists()) {
			fileScan.delete();
		}

		txtDbInvCode.requestFocus();

		System.gc();
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

	private void SaveMoveStock() throws JSONException, ParseException,
			IOException {
		if (this.m_ScanDetail == null || this.m_SerialNo == null) {
			Toast.makeText(this, "错误！没有找到需要保存的数据", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		if (m_ScanDetail.has("ScanDetail")) {
			JSONArray detail = m_ScanDetail.getJSONArray("ScanDetail");
			if (detail.length() == 0) {
				Toast.makeText(this, "错误！没有找到需要保存的数据", Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}
		} else {
			Toast.makeText(this, "错误！没有找到需要保存的数据", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		if (this.m_SerialNo.has("List")) {
			JSONArray detail = m_SerialNo.getJSONArray("List");
			if (detail.length() == 0) {
				Toast.makeText(this, "错误！没有找到需要保存的数据", Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}
		} else {
			Toast.makeText(this, "错误！没有找到需要保存的数据", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		// 在提交前,先要看是否都已经扫描完成了
		saveJons = new JSONObject();
		saveJons.put("Body", m_ScanDetail);
		saveJons.put("SerialNo", m_SerialNo);
		String abc = IsFinishScan(null, saveJons);
		if (abc != null) {
			Toast.makeText(this, abc, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		// 开始组织表头数据

		JSONObject saveHeadJons = new JSONObject();

		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日    HH:mm:ss ");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String curDatestr = formatter.format(curDate);

		saveHeadJons.put("pk_calbody", OrgId); // 库存组织
		saveHeadJons.put("pk_stordoc", this.warehouseID);

		saveHeadJons.put("dbilldate", curDatestr);
		if (accID.equals("A")) {
			saveHeadJons.put("pk_psnbasdoc", MainLogin.objLog.UserID);
			saveHeadJons.put("coperatorid", MainLogin.objLog.UserID);
		} else {
			saveHeadJons.put("pk_psnbasdoc", MainLogin.objLog.UserIDB);
			saveHeadJons.put("coperatorid", MainLogin.objLog.UserIDB);
		}
		saveHeadJons.put("pk_corp", companyID);
		if (this.jonsBody == null || vCode == null || vCode.equals("")) {
			saveHeadJons.put("memo", "无参照货位调整");
		} else {
			saveHeadJons.put("memo", vCode);// 后面把单据号放入
		}
		// 这个代码可以获取guid
		if (uploadGuid == null) {
			uploadGuid = UUID.randomUUID();
		}

		this.saveJons.put("Head", saveHeadJons);
		this.saveJons.put("GUIDS", uploadGuid.toString());
		// this.saveJons.put("GUIDS", "b67eb2c2-2cdc-4776-9fc5-715fb8bad3b3");

		// String abcd= (String)saveJons.get("GUIDS");
		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}

		JSONObject jas = Common.DoHttpQuery(saveJons, "SaveAdjBill", accID);
		if (jas == null) {
			Toast.makeText(this, R.string.DanJuBaoCunChuXianWenTi_QingZaiCiTiJiao, Toast.LENGTH_LONG)
					.show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		boolean loginStatus = false;
		if (jas.has("Status")) {
			loginStatus = jas.getBoolean("Status");
		} else {
			Toast.makeText(this, R.string.DanJuBaoCunChuXianWenTi_QingZaiCiTiJiao,
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		// String lsResultBillCode = jas.getString("BillCode");
		String lsResultBillCode = "";

		if (jas.has("BillCode")) {
			lsResultBillCode = jas.getString("BillCode");
		} else {
			Toast.makeText(this, R.string.DanJuBaoCunChuXianWenTi_QingZaiCiTiJiao,
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		if (loginStatus == true) {
			Map<String, Object> mapResultBillCode = new HashMap<String, Object>();
			mapResultBillCode.put("BillCode", lsResultBillCode);
			ArrayList<Map<String, Object>> lstResultBillCode = new ArrayList<Map<String, Object>>();
			lstResultBillCode.add(mapResultBillCode);

			// Toast.makeText(this, "单据保存成功", Toast.LENGTH_LONG).show();
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					StockMove.this,
					lstResultBillCode,// 数据源
					android.R.layout.simple_list_item_1,
					new String[] { "BillCode" },
					new int[] { android.R.id.text1 });
			new AlertDialog.Builder(StockMove.this).setTitle(R.string.DanJuBaoCunChengGong)
					.setAdapter(listItemAdapter, null)
					.setPositiveButton(R.string.QueRen, null).show();

			// 写入log文件
			writeTxt = new writeTxt();

			Date day = new Date(System.currentTimeMillis());// 获取当前时间

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");

			String BillCode = lsResultBillCode;
			String BillType = "4Q";
			String UserID = MainLogin.objLog.UserID;

			String LogName = BillType + UserID + dfd.format(day) + ".txt";
			String LogMsg = df.format(day) + " " + accID + " " + BillCode;

			writeTxt.writeTxtToFile(LogName, LogMsg);
			// 写入log文件

			uploadGuid = null;

			IniActivyMemor();

			imgDBOrder.setEnabled(true);
			txtDbInvCode.setText("");
			txtDbInvCode.setFocusableInTouchMode(true);
			txtDbInvCode.setFocusable(true);
			txtDbInvCode.requestFocus();
			// btnSAOrder.setEnabled(true);
			// txtSaleOrder.setEnabled(true);
			// txtSaleOrder.setFocusable(true);
			return;
		} else {
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
		//
	}

	// 获得任务从调拨订单明细
	private void LoadOrderListFromDB(String BillId) throws ParseException,
			IOException {
		if (!BillId.equals("")) {
			try {
				// String valStr = "{\"BillID\":'" + BillId +
				// "',\"TableName\":'Body'}";

				JSONObject para = new JSONObject();
				para.put("BillID", BillId);
				para.put("TableName", "Body");
				para.put("AccId", accID);
				if (!MainLogin.getwifiinfo()) {
					Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
							.show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}

				JSONObject jas = Common.DoHttpQuery(para,
						"GetAdjustOrderBillBody", "");
				if (jas == null) {
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
							.show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return;
				}
				if (!jas.has("Status")) {
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
							.show();
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
				// jonsBody=
				jonsBody = jas;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}

		} else {
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
						String orderNo = bundle.getString("result");// 得到子窗口ChildActivity的回传数据
						txtDbInvCode.setText(orderNo);
						BillId = bundle.getString("BillId");
						accID = bundle.getString("AccID");
						warehouseID = (String) bundle.get("warehouseID");
						OrgId = (String) bundle.get("OrgId");
						companyID = (String) bundle.get("companyID");
						vCode = (String) bundle.get("vcode");
						SaveScanedHead();
						try {
							LoadOrderListFromDB(BillId);
							imgDBOrder.setEnabled(false);

							txtDbInvCode.setFocusable(false);
							txtDbInvCode.setFocusableInTouchMode(false);

							// btnSAOrder.setEnabled(false);
							// txtSaleOrder.setEnabled(false);
							// txtSaleOrder.setFocusable(false);
							// 这里用来清除之前扫描的信息
							this.m_ScanDetail = null;
							this.m_SerialNo = null;
							this.hstable = "";
						} catch (Exception e) {
							Toast.makeText(this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
							jonsBody = null;
							return;
						}
					}
				}
				break;
			default:
				// 其它窗口的回传数据
				// IniActivyMemor();
				break;
			}
		} else if (requestCode == 97) {
			switch (resultCode) {
			case 13:// 选择仓库返回
				if (data != null) {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						warehouseCode = bundle.getString("result2");// 得到子窗口ChildActivity的回传数据
						warehouseID = bundle.getString("result1");// 得到子窗口ChildActivity的回传数据
						String name = bundle.getString("result3");// 得到子窗口ChildActivity的回传数据
						
					}
				}
				break;
			default:
				// 其它窗口的回传数据
				// IniActivyMemor();
				break;
			}
		} else if (requestCode == 1) {
			switch (resultCode) {
			case 12: // 这是调拨订单单号列表返回的地方
				if (data != null) {

					Bundle bundle = data.getExtras();
					if (bundle != null) {

						ScanedBarcode = bundle
								.getStringArrayList("ScanedBarcode");
						accID = (String) bundle.get("accID");
						warehouseID = (String) bundle.get("wareHousePK");
						OrgId = (String) bundle.get("orgID");
						companyID = (String) bundle.get("companyId");
						hstable = (String) bundle.get("hashTable");

						try {
							m_ScanDetail = new JSONObject(bundle.get("saveJs")
									.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(StockMove.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						}
						try {
							m_SerialNo = new JSONObject(bundle.get("serJs")
									.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(StockMove.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						}
					}
				}
				break;
			default:
				// 其它窗口的回传数据
				// IniActivyMemor();
				break;
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
		System.gc();
	}

	/**
	 * 判断扫描是否已经完毕,
	 * 
	 * @param Source 传递原单据数据信息
	 * @param scanBody 专递扫描明细
	 * @throws JSONException
	 */
	String IsFinishScan(JSONObject Source, JSONObject scanBody)
			throws JSONException {

		JSONArray details = null;
		try {
			details = scanBody.getJSONObject("SerialNo").getJSONArray("List");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			return e.getMessage();
		}
		for (int i = 0; i < details.length(); i++) {
			String isfinish = "";

			try {
				isfinish = details.getJSONObject(i).get("isfinish").toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				// Toast.makeText(this, e.getMessage(),
				// Toast.LENGTH_LONG).show();
				return e.getMessage();
			}
			if (isfinish.equals("分包未完")) {
				String cinventoryInfo = details.getJSONObject(i)
						.get("cBarcode").toString();
				String       ErrMsg = "还有分包数据没有扫描完毕";
				SplitBarcode sb     = new SplitBarcode(cinventoryInfo);
				String       total  = sb.TotalBox.replaceFirst("^0*", "");
				ArrayList    arrays = new ArrayList();
				// for(int s=0;s<details.length();s++)
				// {
				// if(details.getJSONObject(s).get("identity").toString().equals(details.getJSONObject(i).get("identity").toString()))
				// {
				// SplitBarcode sba=new
				// SplitBarcode(details.getJSONObject(s).get("cBarcode").toString());
				// String current= sba.currentBox;
				// arrays.add(Integer.parseInt(current.replaceFirst("^0*",
				// "")));
				// }
				// }
				// ArrayList noIn=new ArrayList();
				// for(int s=1;s<=Integer.parseInt(total);s++)
				// {
				// if(arrays.indexOf(s)!=-1)
				// {
				// continue;
				// }
				// else
				// {
				// noIn.add(s);
				// }
				// }
				// String noInstr="";
				// for(int s=0;s< noIn.size();s++)
				// {
				// if(s==0)
				// {
				// noInstr="明细:"+noIn.get(s).toString()+",";
				// }
				// else
				// {
				// noInstr+=noIn.get(s).toString()+",";
				// }
				// }
				// return ErrMsg+noInstr ;
				return ErrMsg;
			}
		}

		if (Source != null) {

			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < details.length(); i++) {
				String cinvbasid = ((JSONObject) details.get(i))
						.getString("cinvbasid");
				if (map.containsKey(cinvbasid)) {

				}
			}
		}

		return null;
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

				if (val.length != 7) {
					ReScanHead = "1";
					return;
				}

				String lsBillBarCode = val[0];
				String lsBillId = val[1];
				String lsaccID = val[2];
				String lswarehouseID = val[3];
				String lsOrgId = val[4];
				String lscompanyID = val[5];
				String lsvCode = val[6];

				if (!lsBillBarCode.equals("null")) {
					try {
						String orderNo = lsBillBarCode;// 得到子窗口ChildActivity的回传数据
						txtDbInvCode.setText(orderNo);
						BillId = lsBillId;
						accID = lsaccID;
						warehouseID = lswarehouseID;
						OrgId = lsOrgId;
						companyID = lscompanyID;
						vCode = lsvCode;

						int x = 0;
						for (int i = 0; i < 1; i++) {

							if (x > 10) {
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0,
                                                  1);
								Common.ReScanErr = true;
								ReScanErr();
								return;
							}

							LoadOrderListFromDB(BillId);

							if (jonsBody == null) {
								x++;
								i--;
							}
						}

						imgDBOrder.setEnabled(false);
						txtDbInvCode.setFocusable(false);
						txtDbInvCode.setFocusableInTouchMode(false);

						// btnSAOrder.setEnabled(false);
						// txtSaleOrder.setEnabled(false);
						// txtSaleOrder.setFocusable(false);
						// 这里用来清除之前扫描的信息
						this.m_ScanDetail = null;
						this.m_SerialNo = null;
						this.hstable = "";
					} catch (Exception e) {
						Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
								.show();
						// ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						// ADD CAIXY TEST END
						jonsBody = null;
						return;
					}
				}
				btnScanDetail();
				ReScanHead = "1";

			}
		}

		catch (Exception e) {

			e.printStackTrace();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

		}

	}

	// 记录表头内容
	private void SaveScanedHead() {

		if (ReScanHead.equals("0")) {
			return;
		}

		writeTxt = new writeTxt();

		// 记录扫描数据
		String lsBillBarCode = "";
		String lsBillId = "";
		String lsaccID = "";
		String lswarehouseID = "";
		String lsOrgId = "";
		String lscompanyID = "";
		String lsvCode = "";

		if (txtDbInvCode.getText().toString().equals("")) {
			lsBillBarCode = "null";
		} else {
			lsBillBarCode = txtDbInvCode.getText().toString();
		}

		if (BillId == null || BillId.equals("")) {
			lsBillId = "null";
		} else {
			lsBillId = BillId;
		}

		if (accID == null || accID.equals("")) {
			lsaccID = "null";
		} else {
			lsaccID = accID;
		}

		if (warehouseID == null || warehouseID.equals("")) {
			lswarehouseID = "null";
		} else {
			lswarehouseID = warehouseID;
		}

		if (OrgId == null || OrgId.equals("")) {
			lsOrgId = "null";
		} else {
			lsOrgId = OrgId;
		}

		if (companyID == null || companyID.equals("")) {
			lscompanyID = "null";
		} else {
			lscompanyID = companyID;
		}

		if (vCode == null || vCode.equals("")) {
			lsvCode = "null";
		} else {
			lsvCode = vCode;
		}

		// ScanedHeadInfo.add(ScanedPosBar);
		if (file.exists()) {
			file.delete();
		}
		// String lsBillBarCode = "";
		// String lsBillId = "";
		// String lsaccID = "";
		// String lswarehouseID = "";
		// String lsOrgId = "";
		// String lscompanyID = "";
		// String lsvCode = "";
		writeTxt.writeTxtToFile(ScanedFileName, lsBillBarCode + "|" + lsBillId
				+ "|" + lsaccID + "|" + lswarehouseID + "|" + lsOrgId + "|"
				+ lscompanyID + "|" + lsvCode);

	}

}
