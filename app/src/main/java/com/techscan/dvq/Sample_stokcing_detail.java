package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

public class Sample_stokcing_detail extends Activity {

	EditText txtBarcode;
	EditText txtInvName;
	EditText txtLocation;

	String m_Location;
	String m_OrderNO;
	String m_OrderID;
	String sWhCodes = "";
	Inventory currentObj; // 当前扫描到的存货信息
	Button btnDetail;
	Button btnCheckBox;
	Button btnReturn;
	@Nullable
	private AlertDialog DeleteButton = null;

	@Nullable
	ArrayList<HashMap<String, String>> array1          = null;
	@Nullable
	SimpleAdapter                      listItemAdapter = null;
	@Nullable
	List<Map<String, Object>>          lstTaskBody     = null;
	@Nullable
	List<Map<String, Object>>          lstTaskBody1    = null;

	public List<Map<String, Object>> mData;
//	private SoundPool sp;// 声明一个SoundPool
//	private int MainLogin.music;// 定义一个int来设置suondID
//	private int MainLogin.music2;// 定义一个int来设置suondID
	boolean m_AvToPD = false; // 是否允许盘点
	@NonNull
	private ButtonOnClick buttonDelOnClick = new ButtonOnClick(0);
	@Nullable
	private AlertDialog   SelectButton     = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_stokcing_detail);
		this.setTitle("抽样盘点扫描明细");
		// ADD CAIXY START
//		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		
		// ADD CAIXY END
		// ADD CAIXY END

		txtBarcode = (EditText) findViewById(R.id.txtsampleBarcode);
		txtInvName = (EditText) findViewById(R.id.txtSampleInvenrotyName);
		txtLocation = (EditText) findViewById(R.id.txtSampleBatch);

		btnDetail = (Button) findViewById(R.id.btnSampDetail);
		btnCheckBox = (Button) findViewById(R.id.btnCheckBox);
		btnReturn = (Button) findViewById(R.id.btnSampreturn);
		txtBarcode.setOnKeyListener(myTxtListener);

		this.m_Location = this.getIntent().getStringExtra("locationID");
		this.sWhCodes = this.getIntent().getStringExtra("Warehouses");
		m_OrderNO = this.getIntent().getStringExtra("OrderNo");
		m_OrderID = this.getIntent().getStringExtra("OrderID");

		txtLocation.setText(m_Location);
		//txtLocation.setEnabled(false);
		txtLocation.setFocusableInTouchMode(false);

		txtBarcode.requestFocus();
		txtInvName.setFocusableInTouchMode(false);
		//txtInvName.setEnabled(false);

		btnDetail.setOnClickListener(myListner);
		btnCheckBox.setOnClickListener(myListner);
		btnReturn.setOnClickListener(myListner);
		
		btnDetail.setFocusable(false);
		btnCheckBox.setFocusable(false);
		btnReturn.setFocusable(false);

		try {
			ShowDetail();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			} else {

				if (dialog.equals(DeleteButton)) {
					if (whichButton == DialogInterface.BUTTON_POSITIVE) {
						return;
					} else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
						// 这里进行数据删除处理
						// ConfirmDelItem(index);
					}
				}
			}
		}

	}

	private boolean InserScanDetail(@NonNull SplitBarcode bar) throws JSONException,
			ParseException, IOException {
		JSONObject result = null;
		JSONObject para = new JSONObject();

		txtInvName.setText(bar.cInvCode + " " + bar.cBatch);
		para.put("TableName", "Insert");
		para.put("BillID", this.m_OrderID);
		para.put("BillCode", this.m_OrderNO);
		para.put("Barcode", bar.CheckBarCode);
		para.put("Invcode", bar.cInvCode);
		para.put("Serino", bar.cSerino);
		para.put("Batch", bar.cBatch);
		para.put("CreateUser", MainLogin.objLog.UserID.replace("\n", ""));
		para.put("CurBag", bar.currentBox);
		para.put("TotBag", bar.TotalBox);
		para.put("Position", this.m_Location);
//		if(!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return false;
//        }
		result = MainLogin.objLog.DoHttpQuery(para, "InsertSampleST", "A");
		if (result == null) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		if (!result.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		if (result.getBoolean("Status")) {
			MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
			ShowDetail();// 查询明细

		} else {
			Toast.makeText(this, result.getString("ErrMsg").toString(),
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return false;

		}
		this.txtBarcode.setText("");
		return true;
	}

	private void ShowDetail() throws JSONException, ParseException, IOException {
		// 获得盘点结
		JSONObject serList = null;
		JSONObject para = new JSONObject();

		para.put("FunctionName", "GetTS_StoktackingyDetal");
		para.put("TableName", "Stockdetail");
		para.put("BillID", this.m_OrderID);
		para.put("PosCode", this.txtLocation.getText().toString());
		para.put("CompanyCode", MainLogin.objLog.CompanyCode);
//		if(!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return ;
//        }
		serList = MainLogin.objLog.DoHttpQuery(para, "CommonQuery", "A");
		if (serList == null) {
			// Toast.makeText(this, serList.getString("获取物料过程中发生了错误"),
			// Toast.LENGTH_LONG).show();
			Toast.makeText(Sample_stokcing_detail.this, R.string.WangLuoChuXianWenTi,
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		if (!serList.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			m_AvToPD = false;
			return;
		}
		if (!serList.getBoolean("Status")) {
			Toast.makeText(Sample_stokcing_detail.this, "找不到对应盘点信息",
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			m_AvToPD = false;
			return;
		}
		// 开始绑定数据
		JSONArray arys = serList.getJSONArray("Stockdetail");

		ArrayList array = new ArrayList<HashMap<String, String>>();
		ArrayList arraySame = new ArrayList<HashMap<String, String>>();
		ArrayList arrayDef = new ArrayList<HashMap<String, String>>();

		for (int i = 0; i < arys.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			String qty = arys.getJSONObject(i).get("qty").toString();
			String sqty = arys.getJSONObject(i).get("sqty").toString();

			map.put("poscode", arys.getJSONObject(i).get("positioncode")
					.toString());
			map.put("invcode", arys.getJSONObject(i).get("inventorycode")
					.toString());
			map.put("batch", arys.getJSONObject(i).get("batch").toString());
			map.put("qty", qty);
			map.put("sqty", sqty);
			map.put("aqty", arys.getJSONObject(i).get("aqty").toString());
			map.put("ACCID", arys.getJSONObject(i).get("accid").toString());
			if (qty.equals(sqty)) {
				arraySame.add(map);
			} else {
				arrayDef.add(map);
			}
		}

		if (arrayDef.size() > 0) {
			for (int i = 0; i < arrayDef.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map = (HashMap<String, String>) arrayDef.get(i);
				array.add(map);
			}
		}

		if (arraySame.size() > 0) {
			for (int i = 0; i < arraySame.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map = (HashMap<String, String>) arraySame.get(i);
				array.add(map);
			}
		}

		listItemAdapter = new SimpleAdapter(this, array,// 数据源
				com.techscan.dvq.R.layout.listssdetail,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ACCID", "invcode", "batch", "qty", "sqty", "aqty" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { 
						com.techscan.dvq.R.id.listssdetailAccCID,
						com.techscan.dvq.R.id.listssdetailinvcode,
						com.techscan.dvq.R.id.listssdetailbatch,
						com.techscan.dvq.R.id.listssdetailqty,
						com.techscan.dvq.R.id.listssdetailsqty,
						com.techscan.dvq.R.id.listssdetailaqty });
		ListView lv = (ListView) findViewById(R.id.gSamplevList);
		lv.setAdapter(listItemAdapter);

		m_AvToPD = true;
	}

	private void Return() {

		Sample_stokcing_detail.this.finish();
	}

	private boolean ScanDetail() {
		String barcode = this.txtBarcode.getText().toString();
		if (barcode == null || barcode.equals(""))
			return false;

		SplitBarcode bar = new SplitBarcode(barcode);
  		if(bar.creatorOk==false)
  		{
  			Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			txtBarcode.setText("");
			txtBarcode.requestFocus();
  			return false;
  		}  	

		try {
			InserScanDetail(bar);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
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
				// 执行删除函数
				if (whichButton == DialogInterface.BUTTON_POSITIVE) {
					try {
						DeleteItem(index);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
					return;
				}
			}
			DeleteButton.cancel();
		}
	}

	private void ConfirmDelItem(int index) {
		ButtonOnClickDelconfirm buttondel = new ButtonOnClickDelconfirm(index);
		SelectButton = new AlertDialog.Builder(this).setTitle(R.string.QueRenShanChu)
				.setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
				.setPositiveButton(R.string.QueRen, buttondel)
				.setNegativeButton(R.string.QuXiao, null).show();
	}

	private void DeleteItem(int index) throws JSONException, ParseException,
			IOException {
		Map<String, Object> mapTemp = lstTaskBody1
				.get(index);
		String barcode = (String) mapTemp.get("barcode");
		// String createuser = (String) mapTemp.get("createuser");
		JSONObject result = null;
		JSONObject para = new JSONObject();

		para.put("TableName", "Insert");
		para.put("BillID", m_OrderID);
		para.put("Barcode", barcode);
		para.put("CreateUser", MainLogin.objLog.UserID);
//		if(!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return ;
//        }
		result = MainLogin.objLog.DoHttpQuery(para, "DeleteSampleST", "A");
		if (result == null) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
		if (!result.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			m_AvToPD = false;
			return;
		}
		if (!result.getBoolean("Status")) {
			Toast.makeText(Sample_stokcing_detail.this,
					result.getString("ErrMsg"), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			m_AvToPD = true;
			return;
		}
		ShowDetail();
	}

	private void ShowScanDetailDig() throws JSONException, ParseException,
			IOException {
		lstTaskBody1 = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1;

		JSONObject scnList1 = null;
		JSONObject para = new JSONObject();
		para.put("FunctionName", "GetTS_StoktackingyScan");
		para.put("TableName", "Stockdetail");
		para.put("BillID", this.m_OrderID);
		para.put("PosCode", this.txtLocation.getText().toString());
		para.put("UserID", MainLogin.objLog.UserID);
//		if(!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return ;
//        }
		
		scnList1 = MainLogin.objLog.DoHttpQuery(para, "CommonQuery", "A");
		if (scnList1 == null) {
			// Toast.makeText(this, serList.getString("获取物料过程中发生了错误"),
			// Toast.LENGTH_LONG).show();
			Toast.makeText(Sample_stokcing_detail.this, R.string.WangLuoChuXianWenTi,
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		if (!scnList1.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

			return;
		}
		if (!scnList1.getBoolean("Status")) {
			Toast.makeText(Sample_stokcing_detail.this,
					scnList1.getString("ErrMsg"), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END

			return;
		}

		JSONArray arrays = scnList1.getJSONArray("Stockdetail");
		array1 = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < arrays.length(); i++) {
			map1 = new HashMap<String, Object>();

			String barcode = ((JSONObject) (arrays.get(i)))
					.getString("barcode");
			String position = ((JSONObject) (arrays.get(i)))
					.getString("positioncode");
			String totBag = ((JSONObject) (arrays.get(i))).getString("totbag");
			String curBag = ((JSONObject) (arrays.get(i)))
					.getString("scanedbag");
			// String createuser =
			// ((JSONObject)(arrays.get(i))).getString("createuser");
			String invcode = ((JSONObject) (arrays.get(i)))
					.getString("invcode");
			String batch = ((JSONObject) (arrays.get(i))).getString("batch");
			String serino = ((JSONObject) (arrays.get(i))).getString("serino");
			String box = "";

			if (totBag.equals(curBag)) {
				box = " ";
			} else {
				box = "分包未完";
			}

			map1.put("barcode", barcode);
			map1.put("position", position);
			map1.put("box", box);
			// map1.put("createuser", createuser);
			map1.put("invcode", invcode);
			map1.put("batch", batch);
			map1.put("serino", serino);

			lstTaskBody1.add(map1);
		}

		listItemAdapter = new SimpleAdapter(
				this,
				lstTaskBody1,// 数据源
				com.techscan.dvq.R.layout.listsscanitem,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "barcode", "invcode", "box", "batch", "serino" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { com.techscan.dvq.R.id.listsscanbarcode,
						com.techscan.dvq.R.id.listsscanInvCode,
						com.techscan.dvq.R.id.listsscanbox,
						com.techscan.dvq.R.id.listsscanBatch,
						com.techscan.dvq.R.id.listsscanSno });
		// ListView lv = (ListView) findViewById(R.id.gSamplevList);
		// lv.setAdapter(listItemAdapter);

		DeleteButton = new AlertDialog.Builder(this).setTitle(getString(R.string.SaoMiaoMingXiXinXi))
				.setSingleChoiceItems(listItemAdapter, 0, buttonDelOnClick)
				.setPositiveButton(R.string.QueRen, null).create();

		DeleteButton.getListView().setOnItemLongClickListener(
				new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						// When clicked, show a toast with the TextView text

						ConfirmDelItem(arg2);

						return false;
					}
				});
		DeleteButton.show();
	}

	private void CheckBox() throws JSONException, ParseException, IOException {
		lstTaskBody1 = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1;

		JSONObject scnList1 = null;
		JSONObject para = new JSONObject();
		para.put("FunctionName", "GetTS_StoktackingyCheckBox");
		para.put("TableName", "Stockdetail");
		para.put("BillID", this.m_OrderID);
		para.put("PosCode", this.txtLocation.getText().toString());
		para.put("UserID", MainLogin.objLog.UserID);
//		if(!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return ;
//        }
		scnList1 = MainLogin.objLog.DoHttpQuery(para, "CommonQuery", "A");
		if (scnList1 == null) {
			// Toast.makeText(this, serList.getString("获取物料过程中发生了错误"),
			// Toast.LENGTH_LONG).show();
			Toast.makeText(Sample_stokcing_detail.this, R.string.WangLuoChuXianWenTi,
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		if (!scnList1.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			m_AvToPD = false;
			return;
		}
		if (!scnList1.getBoolean("Status")) {
			String ErrMsg = scnList1.getString("ErrMsg");

			if (ErrMsg.equals("没有可用数据")) {
				Return();
			} else {
				Toast.makeText(Sample_stokcing_detail.this, ErrMsg,
						Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}
		}

		JSONArray arrays = scnList1.getJSONArray("Stockdetail");

		if (arrays.length() > 0) {
			AlertDialog.Builder bulider = new AlertDialog.Builder(this)
					.setTitle(R.string.XunWen).setMessage("还有分包未扫描完毕的存货,必须扫完其余分包才能返回!");
			//bulider.setNegativeButton("取消", null);
			//bulider.setPositiveButton("确认", listenExit).create().show();
			bulider.setPositiveButton(R.string.QueRen, null).create().show();
		} else {
			Return();
		}

	}

	// 取消按钮对话框事件
	@NonNull
	private DialogInterface.OnClickListener listenExit = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Return();
		}
	};

	private void ShowCheckBoxDig() throws JSONException, ParseException,
			IOException {
		lstTaskBody1 = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1;

		JSONObject scnList1 = null;
		JSONObject para = new JSONObject();
		para.put("FunctionName", "GetTS_StoktackingyCheckBox");
		para.put("TableName", "Stockdetail");
		para.put("BillID", this.m_OrderID);
		para.put("PosCode", this.txtLocation.getText().toString());
		para.put("UserID", MainLogin.objLog.UserID);
//		if(!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return ;
//        }
		scnList1 = MainLogin.objLog.DoHttpQuery(para, "CommonQuery", "A");
		if (scnList1 == null) {
			// Toast.makeText(this, serList.getString("获取物料过程中发生了错误"),
			// Toast.LENGTH_LONG).show();
			Toast.makeText(Sample_stokcing_detail.this, R.string.WangLuoChuXianWenTi,
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			m_AvToPD = false;
			return;
		}
		if (!scnList1.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

			return;
		}
		if (!scnList1.getBoolean("Status")) {
			Toast.makeText(Sample_stokcing_detail.this,
					scnList1.getString("ErrMsg"), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END

			return;
		}

		JSONArray arrays = scnList1.getJSONArray("Stockdetail");
		array1 = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < arrays.length(); i++) {
			map1 = new HashMap<String, Object>();

			String barcode = ((JSONObject) (arrays.get(i)))
					.getString("barcode");
			String position = ((JSONObject) (arrays.get(i)))
					.getString("positioncode");
			String totBag = ((JSONObject) (arrays.get(i))).getString("totbag");
			String curBag = ((JSONObject) (arrays.get(i)))
					.getString("scanedbag");
			// String createuser =
			// ((JSONObject)(arrays.get(i))).getString("createuser");
			String invcode = ((JSONObject) (arrays.get(i)))
					.getString("invcode");
			String batch = ((JSONObject) (arrays.get(i))).getString("batch");
			String serino = ((JSONObject) (arrays.get(i))).getString("serino");
			String box = "";

			if (totBag.equals(curBag)) {
				box = " ";
			} else {
				box = "分包未完";
			}

			map1.put("barcode", barcode);
			map1.put("position", position);
			map1.put("box", box);
			// map1.put("createuser", createuser);
			map1.put("invcode", invcode);
			map1.put("batch", batch);
			map1.put("serino", serino);

			lstTaskBody1.add(map1);
		}

		listItemAdapter = new SimpleAdapter(
				this,
				lstTaskBody1,// 数据源
				com.techscan.dvq.R.layout.listsscanitem,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "barcode", "invcode", "box", "batch", "serino" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { com.techscan.dvq.R.id.listsscanbarcode,
						com.techscan.dvq.R.id.listsscanInvCode,
						com.techscan.dvq.R.id.listsscanbox,
						com.techscan.dvq.R.id.listsscanBatch,
						com.techscan.dvq.R.id.listsscanSno });
		// ListView lv = (ListView) findViewById(R.id.gSamplevList);
		// lv.setAdapter(listItemAdapter);

		DeleteButton = new AlertDialog.Builder(this).setTitle(getString(R.string.SaoMiaoMingXiXinXi))
				.setSingleChoiceItems(listItemAdapter, 0, buttonDelOnClick)
				.setPositiveButton(R.string.QueRen, null).create();

		DeleteButton.getListView().setOnItemLongClickListener(
				new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						// When clicked, show a toast with the TextView text

						ConfirmDelItem(arg2);

						return false;
					}
				});
		DeleteButton.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sample_stokcing_detail, menu);
		return true;
	}

	@Override
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
	private OnKeyListener          myTxtListener = new OnKeyListener() {
		@Override
		public boolean onKey(@NonNull View v, int arg1, @NonNull KeyEvent arg2) {
			switch (v.getId()) {
			case R.id.txtsampleBarcode:
				if (arg1 == arg2.KEYCODE_ENTER
						&& arg2.getAction() == KeyEvent.ACTION_UP) {
					ScanDetail();
				}
				break;
			}

			return true;
		}
	};
	@NonNull
	private Button.OnClickListener myListner     = new Button.OnClickListener() {
		@Override
		public void onClick(@NonNull View v) {
			switch (v.getId()) {
			case R.id.btnSampreturn:
				try {
					CheckBox();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case R.id.btnSampDetail:
				try {
					ShowScanDetailDig();
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
			case R.id.btnCheckBox:
				try {
					ShowCheckBoxDig();
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
