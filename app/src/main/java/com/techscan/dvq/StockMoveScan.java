package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Switch;
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
import java.util.List;
import java.util.Map;

public class StockMoveScan extends Activity {

	EditText txtBarcode;
	EditText txtInv;
	EditText txtBatch;
	EditText txtOutPos;
	EditText txtInPos;
	EditText txtSerino;
	Switch swhSavePos;
	// ADD CAIXY START 15/04/14
	Switch swhSavePosIn;
	// ADD CAIXY END 15/04/14
	Button btnRW;
	Button btnDetail;
	Button btnExit;
	Button btnViewStock;

	// ADD BY WUQIONG START
	TextView tvmscount;

	// ADD BY WUQIONG END

	boolean bHaveHead;
	public String wareHousePK = "";
	JSONObject jonsHead; // 源头单据表头
	JSONObject jonsBody; // 源头单据表体

	List<Map<String, String>> jonsScan;
	List<Map<String, String>> jonsSerialNo;

	Inventory currentObj; // 当前扫描到的存货信息

	String m_companyCode;
	String m_companyId;
	String m_AccID;
	String m_OrgID;
	// ADD CAIXY START 15/04/15
	String InOutFlg = "";
	// ADD CAIXY END 15/04/15

	private String m_InPosID;
	private String m_OutPosID;
	private String m_InPosCode;
	private String m_OutPosCode;

	private ButtonOnClick buttonOnClick = new ButtonOnClick(0);

	private ButtonOnClick buttonDelOnClick = new ButtonOnClick(0);
	private String[] warehouseList = null;
	private String[] warehouseNameList = null;
	private String[] vFree1List = null;
	private String[] OrgList = null;
	private String[] companyIdList = null;
	private SplitBarcode bar = null;

	private Hashtable SerialValues = null;

	private AlertDialog SelectButton = null;
	private AlertDialog DeleteButton = null;
	List<Map<String, String>> m_mData = null;

	// ADD CAIXY TEST START
//	private SoundPool sp;// 声明一个SoundPool
//	private int music;// 定义一个int来设置suondID
//	private int music2;// 定义一个int来设置suondID
	// ADD CAIXY TEST END

	String fileNameScan = null;
	String ScanedFileName = null;
	String UserID = null;
	File fileScan = null;
	String ReScanBody = "1";
	private ArrayList<String> ScanedBarcode = new ArrayList<String>();
	private writeTxt writeTxt; // 保存LOG文件
	int wareHouseindex = -99;

	private Button btClear = null;
	public final static String PREFERENCE_SETTING = "Setting";

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			m_OutPosID = "";
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	};

	private TextWatcher watchers = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			// m_OutPosID="";
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

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

	private Button.OnClickListener myListner = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case id.btnMovExit:
				Done();
				break;
			case id.btnMovRW:
				DoShowOrigin();
				break;
			case id.btnMovDetail:
				DoShowScanned();
				break;
			case id.btstmovescanclear:
				DoClearForm();
				break;
			case id.btnstmvscanViewStock:
				DoShowCurrentStock("");
				break;
			}

		}
	};

	void DoShowCurrentStock(String InvCode) {
		System.gc();

		if (this.wareHousePK == null || this.wareHousePK.equals("")) {
			return;
		}
		if (currentObj == null && InvCode.equals("")) {
			return;
		}

		JSONObject para = null;
		para = new JSONObject();
		try {
			para.put("FunctionName", "GetAdvStockbyWhnIvid");
			if (InvCode.equals("")) {
				para.put("InvID", currentObj.Invmandoc());
			} else {
				para.put("InvID", InvCode);
			}
			para.put("whid", this.wareHousePK);
			para.put("AccID", this.m_AccID);
			para.put("TableName", "Body");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}

		JSONObject rev = null;
		try {
			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			rev = Common.DoHttpQuery(para, "CommonQuery", m_AccID);

			if (rev == null) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (!rev.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (rev.getBoolean("Status")) {
				JSONArray val = rev.getJSONArray("Body");
				if (val.length() < 1) {
					Toast.makeText(this, R.string.MeiYouKeYongHuoWeiKuCunShuJu, Toast.LENGTH_LONG)
							.show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return;
				}
				List<Map<String, String>> mData = null;
				try {
					mData = getBinData(rev);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(StockMoveScan.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return;
				}
				if (mData == null) {
					return;
				}

				SimpleAdapter listItemAdapter = new SimpleAdapter(this, mData,// 数据源
						R.layout.showorigin,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "invname", "invcode", "csname", "vlotb",
								"nnum" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.lsorginvname, R.id.lsorginvcode,
								R.id.lsorginvbatch, R.id.lsorginvnum,
								R.id.lsorgspaceid });
				new AlertDialog.Builder(this).setTitle(R.string.KuCunXinXi)
						.setAdapter(listItemAdapter, null)
						.setPositiveButton(R.string.QueRen, null).show();

			} else {
				Toast.makeText(this, R.string.YuanHuoWeiShangMeiYouGaiHuoPin, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
		}
		return;

	}

	void DoClearForm() {
		AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
				R.string.XunWen).setMessage(R.string.QueRenQingPingMa);
		bulider.setNegativeButton(R.string.QuXiao, null);
		bulider.setPositiveButton(R.string.QueRen, listenIniScan).create().show();
		// IniScan();
	}

	private DialogInterface.OnClickListener listenIniScan = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			IniScan();
		}
	};
	private DialogInterface.OnClickListener listenExit = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			finish();
		}
	};

	/**
	 * 显示扫描明细,还没有开发完毕,需要显示扫描详细(这里不显示拆包明细,只到序列号一级),然后提供删除功能.
	 */
	void DoShowScanned() {

		try {
			this.m_mData = getScanData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(StockMoveScan.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		if (m_mData == null) {
			return;
		}
		if (m_mData.size() <= 0) {
			return;
		}

		// SimpleAdapter listItemAdapter = new SimpleAdapter(this,m_mData,//数据源
		// R.layout.vlistpds,//ListItem的XML实现
		// //动态数组与ImageItem对应的子项
		// new String[] {"cBarcode","SerialNo","isfinish"},
		// //ImageItem的XML文件里面的一个ImageView,两个TextView ID
		// new int[] {R.id.listpdorder,R.id.listfromware,R.id.listtoware}
		// );

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, m_mData,// 数据源
				R.layout.showorigin,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "cBarcode", "isfinish", "FromPos", "ToPos" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.lsorginvname, R.id.lsorginvnum,
						R.id.lsorginvcode, R.id.lsorginvbatch });

		// MOD CAIXY START
		// DeleteButton =new
		// AlertDialog.Builder(this).setTitle("扫描明细信息").setSingleChoiceItems(listItemAdapter,
		// 0, buttonDelOnClick).setNegativeButton("删除",
		// buttonDelOnClick).setPositiveButton(R.string.QueRen,null).create();
		DeleteButton = new AlertDialog.Builder(this).setTitle(R.string.SaoMiaoMingXiXinXi)
				.setSingleChoiceItems(listItemAdapter, 0, buttonDelOnClick)
				.setPositiveButton(R.string.QueRen, null).create();
		// MOD CAIXY END

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

		// DeleteButton.getListView().setOnItemClickListener(new
		// OnItemClickListener()
		// {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		//
		// long arg3)
		//
		// {
		// //MOD CAIXY START
		//
		// // for(int i=0;i<arg0.getCount();i++){
		// // View v=arg0.getChildAt(i);
		// //
		// // if(v!=null && v.equals(arg1))
		// // {
		// // v.setBackgroundColor(Color.RED);
		// // buttonDelOnClick.index =arg2;
		// // }
		// // else
		// // {
		// // v.setBackgroundColor(Color.TRANSPARENT);
		// // }
		// // }
		//
		// ConfirmDelItem(arg2);
		// //MOD CAIXY END
		//
		//
		// }
		// });

		DeleteButton.show();

	}

	private List<Map<String, String>> getScanData() throws JSONException {
		// List<Map<String, Object>> list = new ArrayList<Map<String,
		// Object>>();
		// Map<String, Object> map;

		// JSONArray arrays=
		// this.jonsSerialNo.getJSONArray("List");//(JSONArray)
		// //jas.get("Body");
		//
		// for(int i = 0;i<arrays.length();i++)
		// {
		// map = new HashMap<String, Object>();
		// //这里请放置需要的字段.
		// map.put("cBarcode",
		// ((JSONObject)(arrays.get(i))).getString("cBarcode"));
		// map.put("SerialNo",
		// ((JSONObject)(arrays.get(i))).getString("cSerino"));
		// map.put("isfinish",
		// ((JSONObject)(arrays.get(i))).getString("isfinish"));
		// map.put("id", ((JSONObject)(arrays.get(i))).getString("id"));
		// map.put("identity",
		// ((JSONObject)(arrays.get(i))).getString("identity"));
		// map.put("cspaceidf",
		// ((JSONObject)(arrays.get(i))).getString("cspaceidf"));
		// map.put("cspaceidt",
		// ((JSONObject)(arrays.get(i))).getString("cspaceidt"));
		// list.add(map);
		// }
		return jonsSerialNo;
	}

	void DoShowOrigin() {
		List<Map<String, Object>> mData = null;
		try {
			mData = getData(this.jonsBody);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(StockMoveScan.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		if (mData == null) {
			return;
		}

		SimpleAdapter listItemAdapter = new SimpleAdapter(
				this,
				mData,// 数据源
				R.layout.showorigin,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "Desc", "InvCode", "Batch", "csname", "num" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.lsorginvname, R.id.lsorginvcode,
						R.id.lsorginvbatch, R.id.lsorginvnum, R.id.lsorgspaceid });
		new AlertDialog.Builder(this).setTitle(R.string.YuanDanXinXi)
				.setAdapter(listItemAdapter, listenOrigin)
				.setPositiveButton(R.string.QueRen, null).show();

	}

	private DialogInterface.OnClickListener listenOrigin = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			if (whichButton >= 0) {
				JSONArray array = null;
				String invCode = null;
				try {
					array = (JSONArray) StockMoveScan.this.jonsBody
							.getJSONArray("Body");
					invCode = ((JSONObject) array.get(whichButton)).get(
							"coutinvid").toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(StockMoveScan.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return;
				}
				if (invCode == null || invCode.toString() == "") {
					return;
				}
				DoShowCurrentStock(invCode);
			}
		}
	};

	private List<Map<String, String>> getBinData(JSONObject jas)
			throws JSONException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;

		JSONObject tempJso = null;
		if (jas == null) {
			return null;
		}
		if (!jas.has("Status")) {
			return null;
		}

		if (!jas.getBoolean("Status")) {
			String errMsg = jas.getString("ErrMsg");
			Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return null;
		}
		JSONArray arys = (JSONArray) jas.get("Body");

		for (int i = 0; i < arys.length(); i++) {
			map = new HashMap<String, String>();
			// invname
			map.put("invcode", arys.getJSONObject(i).get("invcode").toString());
			map.put("invname", arys.getJSONObject(i).get("invname").toString());
			map.put("csname", arys.getJSONObject(i).get("csname").toString());
			map.put("storname", arys.getJSONObject(i).get("storname")
					.toString());
			// map.put("unitcode",
			// arys.getJSONObject(i).get("unitcode").toString());
			// map.put("unitname",
			// arys.getJSONObject(i).get("unitname").toString());
			map.put("vlotb", arys.getJSONObject(i).get("vlotb").toString());
			map.put("nnum", arys.getJSONObject(i).get("nnum").toString());
			list.add(map);
		}
		return list;
	}

	private List<Map<String, Object>> getData(JSONObject jas)
			throws JSONException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		JSONObject tempJso = null;
		if (jas == null) {
			return null;
		}
		if (!jas.has("Status")) {
			return null;
		}

		if (!jas.getBoolean("Status")) {
			String errMsg = jas.getString("ErrMsg");
			Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return null;
		}
		JSONArray arrays = (JSONArray) jas.get("Body");

		for (int i = 0; i < arrays.length(); i++) {
			map = new HashMap<String, Object>();
			map.put("Desc", ((JSONObject) (arrays.get(i))).getString("invname"));
			map.put("InvCode",
					((JSONObject) (arrays.get(i))).getString("invcode"));
			String batchs = ((JSONObject) (arrays.get(i))).getString("vbatch");
			// invname,invcode
			if (batchs == null || batchs.equals("") || batchs.equals("null")) {
				batchs = getString(R.string.PiCiWeiZhiDing);
			}
			map.put("Batch", "" + batchs);
			map.put("num", ""
					+ ((JSONObject) (arrays.get(i))).get("nnum").toString());
			if (((JSONObject) (arrays.get(i))).has("cscode")) {
				map.put("csname",
						""
								+ ((JSONObject) (arrays.get(i))).get("cscode")
										.toString());
			} else {
				map.put("csname", getString(R.string.WuHuoWeiXinXi));
			}
			list.add(map);
		}
		return list;
	}

	private OnKeyListener myTxtListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int arg1, KeyEvent arg2) {
			{

				switch (v.getId()) {
				case id.txtMoveBarcode:
					if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)// &&
																				// arg2.getAction()
																				// ==
																				// KeyEvent.ACTION_DOWN
					{
						try {
							ScanBarcode(txtBarcode.getText().toString());

						} catch (ParseException e) {
							txtBarcode.setText("");
							txtBarcode.requestFocus();
							Toast.makeText(StockMoveScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							e.printStackTrace();

							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						} catch (JSONException e) {
							txtBarcode.setText("");
							txtBarcode.requestFocus();
							Toast.makeText(StockMoveScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							e.printStackTrace();

							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						} catch (IOException e) {
							txtBarcode.setText("");
							txtBarcode.requestFocus();
							Toast.makeText(StockMoveScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							e.printStackTrace();

							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						}

						return true;

					}

					break;
				case id.txtMovInPos:
					if (arg1 == arg2.KEYCODE_ENTER
							&& arg2.getAction() == KeyEvent.ACTION_UP) {
						try {
							// ADD CAIXY START 15/04/14
							InOutFlg = "In";
							// ADD CAIXY END 15/04/14
							FindPositionByCode(txtInPos.getText().toString(),
									"1");
							// txtInPos.setText(m_InPosName);
						} catch (ParseException e) {
							txtInPos.setText("");
							txtInPos.requestFocus();
							Toast.makeText(StockMoveScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();

							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END

						} catch (JSONException e) {
							txtInPos.setText("");
							txtInPos.requestFocus();
							Toast.makeText(StockMoveScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							// ADD CAIXY TEST START
							MainLogin.objLog.CompanyCode = "";
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END

						}
						return true;
					}
					break;
				case id.txtMovOutPos:
					if (arg1 == arg2.KEYCODE_ENTER
							&& arg2.getAction() == KeyEvent.ACTION_UP) {
						try {
							// ADD CAIXY START 15/04/14
							InOutFlg = "Out";
							// ADD CAIXY END 15/04/14
							FindPositionByCode(txtOutPos.getText().toString(),
									"0");
							// txtOutPos.setText(m_OutPosName);
						} catch (ParseException e) {
							txtOutPos.setText("");
							txtOutPos.requestFocus();
							Toast.makeText(StockMoveScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						} catch (JSONException e) {
							txtOutPos.setText("");
							txtOutPos.requestFocus();
							Toast.makeText(StockMoveScan.this, e.getMessage(),
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						}
						return true;
					}
					break;

				}
			}
			return false;
		}

	};

	private boolean FindInvnBinStockInfo(String BinID) throws JSONException,
			ParseException, IOException {
		JSONObject para = new JSONObject();
		para.put("FunctionName", "GetBinStockByID");
		para.put("InvID", currentObj.Invmandoc());
		para.put("BinID", BinID);
		para.put("LotB", currentObj.GetBatch());
		para.put("TableName", "Stock");

		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", m_AccID);

		if (rev == null) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}
		if (!rev.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}
		if (rev.getBoolean("Status")) {
			JSONArray val = rev.getJSONArray("Stock");
			if (val.length() < 1) {
				Toast.makeText(this, R.string.YuanHuoWeiShangMeiYouGaiHuoPin, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return false;
			}

			double stockCount = 0;
			for (int iv = 0; iv < val.length(); iv++) {
				JSONObject temp = val.getJSONObject(iv);
				if (Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0) {
					stockCount = stockCount
							+ Double.valueOf(temp.getString("nnum"));

				}
			}

			double sancount = getScanCount(currentObj.Invmandoc(), BinID,
					currentObj.GetBatch());
			String Identity = currentObj.AccID() + currentObj.getInvCode()
					+ currentObj.GetBatch() + currentObj.GetSerino();
			if (!getSerialIsScanfinish(Identity)) {
				sancount = sancount + 1;
			}
			if (sancount > stockCount) {
				Toast.makeText(this, R.string.GaiHuoWeiZaiDeFaChuKuCunYiJingBuZule,
						Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				// ADD CAIXY START 15/04/14
				txtOutPos.setText("");
				txtOutPos.requestFocus();
				// ADD CAIXY END 15/04/14
				return false;
			}

			return true;
		} else {
			Toast.makeText(this, R.string.GaiHuoWeiZaiDeFaChuKuCunYiJingBuZule, Toast.LENGTH_LONG)
					.show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			// ADD CAIXY START 15/04/14
			if (InOutFlg.equals("Out")) {
				txtOutPos.setText("");
				txtOutPos.requestFocus();
			} else if (InOutFlg.equals("In")) {
				txtInPos.setText("");
				txtInPos.requestFocus();
			}

			// ADD CAIXY END 15/04/14
			return false;

		}
	}

	/**
	 * 查看序列号是否已经扫描完毕,如果true了则不需要累加了
	 */
	boolean getSerialIsScanfinish(String Identity) {
		if (jonsSerialNo == null) {
			return false;
		}
		for (int i = 0; i < this.jonsSerialNo.size(); i++) {
			Map<String, String> values = jonsSerialNo.get(i);
			if (values.get("identity").equals(Identity)) {
				return true;
			}
		}
		return false;
	}

	// /获取现在已经扫描了多少数量
	double getScanCount(String InvID, String BinID, String BatchCode) {
		if (jonsScan == null) {
			return 0;
		}
		for (int i = 0; i < this.jonsScan.size(); i++) {
			Map<String, String> values = jonsScan.get(i);
			if (values.get("cinventoryid").equals(InvID)
					&& values.get("cspaceidf").equals(BinID)
					&& values.get("clot").equals(BatchCode)) {
				// spacenum货位数量
				double spacenum = Double.valueOf(values.get("spacenum"));
				return spacenum;
			}

		}
		return 0;
	}

	/**
	 * 找到货位ID按照货位号
	 * 
	 * @param posCode
	 *            货位号
	 * @param type
	 *            货位类型
	 */
	private void FindPositionByCode(String posCode, String type)
			throws JSONException {

		try {

			posCode = posCode;
			posCode = posCode.replace("\n", "");
			posCode = posCode.toUpperCase();

			if (m_AccID == null || m_AccID.equals("")) {
				Toast.makeText(this, R.string.CangKuZhangTaoHaiMeiYouQueBuNeng, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				if (type.equals("1")) {
					this.txtInPos.setText("");
					this.txtInPos.requestFocus();
					return;
				} else {
					this.txtOutPos.setText("");
					this.txtOutPos.requestFocus();
					return;
				}
			}

			if (bar.AccID.equals("A")) {
				// m_companyCode="101";
			} else {
				m_companyCode = "1";
			}

			JSONObject para = new JSONObject();
			para.put("FunctionName", "GetBinCodeInfo");
			para.put("CompanyCode", m_companyCode);
			para.put("STOrgCode", MainLogin.objLog.STOrgCode);
			para.put("WareHouse", this.wareHousePK);
			para.put("BinCode", posCode);
			para.put("TableName", "position");

			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", m_AccID);

			if (rev == null) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (!rev.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			if (rev.getBoolean("Status")) {
				JSONArray val = rev.getJSONArray("position");
				if (val.length() < 1) {
					Toast.makeText(this, R.string.HuoQuHuoWeiShiBai, Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					// ADD CAIXY START 15/04/14
					txtOutPos.setText("");
					txtOutPos.requestFocus();
					// ADD CAIXY END 15/04/14

					return;
				}
				String jposName, jposCode, jposID;
				JSONObject temp = val.getJSONObject(0);

				jposName = temp.getString("csname");
				jposCode = temp.getString("cscode");
				jposID = temp.getString("pk_cargdoc");

				if (type.equals("1")) {
					if (this.txtOutPos.getText().toString().equals("")
							|| this.m_OutPosID == null
							|| this.m_OutPosID.equals("")) {
						Toast.makeText(this, R.string.QingXianQueRenFaChuHuoWei, Toast.LENGTH_LONG)
								.show();
						// ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						// ADD CAIXY TEST END
						// ADD CAIXY START 15/04/14
						txtInPos.setText("");
						txtInPos.requestFocus();
						// ADD CAIXY END 15/04/14
						return;
					}
					this.txtInPos.setText(jposCode);
					this.m_InPosCode = jposCode;
					this.m_InPosID = jposID;

					if (txtOutPos.getText().toString().equals("")) {
						// MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
						txtOutPos.requestFocus();
						return;
					}
					if (!FindInvnBinStockInfo(m_OutPosID)) {
						return;
					}

				} else {

					this.txtOutPos.setText(jposCode);
					this.m_OutPosCode = jposCode;
					this.m_OutPosID = jposID;
					if (!FindInvnBinStockInfo(m_OutPosID)) {
						return;
					}
					if (txtInPos.getText().toString().equals("")) {
						MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
						txtInPos.requestFocus();
						return;
					}
					if (m_InPosID.equals("") || m_InPosID == null) {
						txtInPos.requestFocus();
						return;
					}
				}

				if (m_OutPosID.equals(m_InPosID)) {
					Toast.makeText(this, R.string.DiaoRuHeDiaoChuHuoWeiBuNengYiZhi, Toast.LENGTH_LONG)
							.show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					txtInPos.setText("");
					txtInPos.requestFocus();
					return;
				}
				DataBindtoJons();
				// wuqiong

				tvmscount.setText("已扫描" + jonsSerialNo.size() + "件");

				return;
			} else {
				Toast.makeText(this, R.string.HuoQuHuoWeiShiBai, Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				// ADD CAIXY START 15/04/14
				if (InOutFlg.equals("Out")) {
					txtOutPos.setText("");
					txtOutPos.requestFocus();
				} else if (InOutFlg.equals("In")) {
					txtInPos.setText("");
					txtInPos.requestFocus();
				}
				// ADD CAIXY END 15/04/14
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

	private void Done() {

		if (jonsScan == null || jonsSerialNo == null) {
			Intent intent = new Intent();
			this.setResult(13, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理
			this.finish();
			return;
		}

		Intent intent = new Intent();

		String scan = JTFH.ToJSONFMap(jonsScan, "ScanDetail");
		String serial = JTFH.ToJSONFMap(jonsSerialNo, "List");
		intent.putExtra("saveJs", scan);// 把返回数据存入Intent
		intent.putExtra("serJs", serial);// 把返回数据存入Intent

		intent.putExtra("accID", m_AccID);
		intent.putExtra("wareHousePK", wareHousePK);
		intent.putExtra("orgID", m_OrgID);
		intent.putExtra("companyId", m_companyId);
		intent.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);

		try {
			if (SerialValues == null) {
				intent.putExtra("hashTable", "");

			} else {
				intent.putExtra("hashTable", JTFH.FromHashtable(SerialValues)
						.toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return;
		}
		this.setResult(12, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理
		this.finish();// 关闭子窗口ChildActivity
	}

	private void IniForm() {
		txtBarcode = (EditText) findViewById(R.id.txtMoveBarcode);
		txtInv = (EditText) findViewById(R.id.txtMovInv);

		txtBatch = (EditText) findViewById(R.id.txtMovBatch);

		txtOutPos = (EditText) findViewById(R.id.txtMovOutPos);
		txtInPos = (EditText) findViewById(R.id.txtMovInPos);
		txtSerino = (EditText) findViewById(R.id.txtSerino);

		btnRW = (Button) findViewById(R.id.btnMovRW);
		btnDetail = (Button) findViewById(R.id.btnMovDetail);

		btnExit = (Button) findViewById(R.id.btnMovExit);

		swhSavePos = (Switch) findViewById(R.id.swhMovPos);
		swhSavePos.setChecked(false);

		// ADD CAIXY START 15/04/14
		swhSavePosIn = (Switch) findViewById(R.id.swhMovPosIn);
		swhSavePosIn.setChecked(false);
		// ADD CAIXY END 15/04/14

		btnRW.setOnClickListener(myListner);
		btnDetail.setOnClickListener(myListner);
		btnExit.setOnClickListener(myListner);

		txtBarcode.setOnKeyListener(myTxtListener);
		txtOutPos.setOnKeyListener(myTxtListener);
		txtInPos.setOnKeyListener(myTxtListener);

		// txtBarcode.addTextChangedListener(watcher);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_move_scan);

		this.setTitle(getString(R.string.HuoWeiTiaoZhengSaoMiaoMingXi));
		this.bHaveHead = this.getIntent().getBooleanExtra("haveOrder", false);
		this.wareHousePK = this.getIntent().getStringExtra("wareHousePK");
		this.m_AccID = this.getIntent().getStringExtra("AccID");
		this.m_OrgID = this.getIntent().getStringExtra("OrgId");
		this.m_companyId = this.getIntent().getStringExtra("companyID");
		String ScanDetail = this.getIntent().getStringExtra("ScanDetail");
		String SerialNo = this.getIntent().getStringExtra("List");
		ScanedBarcode = this.getIntent().getStringArrayListExtra(
				"ScanedBarcode");

		SharedPreferences sharedPreferences = getSharedPreferences(
				PREFERENCE_SETTING, Activity.MODE_PRIVATE);

		m_companyCode = sharedPreferences.getString("CompanyCode", "");

		if (ScanDetail != null) {
			JSONObject obj = null;
			try {
				obj = new JSONObject(ScanDetail);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
			}
			this.jonsScan = JTFH.FromSONToMap(obj, "ScanDetail");
		}
		if (SerialNo != null) {
			JSONObject obj1 = null;
			try {
				obj1 = new JSONObject(SerialNo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
			}

			this.jonsSerialNo = JTFH.FromSONToMap(obj1, "List");
		}

		String hasTable = this.getIntent().getStringExtra("hashTable");
		if (hasTable == null || hasTable.equals("")) {

		} else {
			try {
				this.SerialValues = JTFH.ToHashtable(new JSONObject(hasTable));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
			}
		}
		if (bHaveHead == true) {
			String OriJosnBody = this.getIntent().getStringExtra("OriJsonBody");
			JSONObject jonob = null;
			try {
				jonob = new JSONObject(OriJosnBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (jonob.has("Body")) {
				this.jonsBody = jonob;
			}
		}
		this.btnRW = (Button) findViewById(R.id.btnMovRW);
		this.btnRW.setOnClickListener(myListner);
		this.txtOutPos = (EditText) findViewById(R.id.txtMovOutPos);
		this.txtOutPos.addTextChangedListener(watcher);

		this.txtBarcode = (EditText) findViewById(R.id.txtMoveBarcode);
		this.txtBarcode.addTextChangedListener(watchers);

		btClear = (Button) findViewById(R.id.btstmovescanclear);
		btClear.setOnClickListener(myListner);

		btnViewStock = (Button) findViewById(R.id.btnstmvscanViewStock);
		btnViewStock.setOnClickListener(myListner);

//		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		music = MainLogin.sp.load(this, R.raw.xxx, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		music2 = MainLogin.sp.load(this, R.raw.yyy, 1);

		IniForm();
		if (this.jonsBody != null) {
			if (m_companyCode == null || m_companyCode.equals(""))
				if (this.m_AccID.equals("A")) {
					// m_companyCode="101";
				} else {
					m_companyCode = "1";
				}
		}

		// ADD BY WUQIONG START

		tvmscount = (TextView) findViewById(R.id.tvmscount);

		if (jonsSerialNo == null) {
			tvmscount.setText("已扫描0件");
		} else {
			// try {
			// this.m_mData = getScanData();
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// return;
			// }
			tvmscount.setText("已扫描" + jonsSerialNo.size() + "件");
		}

		// ADD BY WUQIONG END
		UserID = MainLogin.objLog.UserID;
		// String LogName = BillType + UserID + dfd.format(day)+".txt";
		ScanedFileName = "4QScan" + UserID + ".txt";
		fileNameScan = "/sdcard/DVQ/4QScan" + UserID + ".txt";
		fileScan = new File(fileNameScan);
		txtBarcode.setSelectAllOnFocus(true);

		btnRW.setFocusable(false);
		btnDetail.setFocusable(false);
		btnExit.setFocusable(false);
		btnViewStock.setFocusable(false);

		ReScanBody();

	}

	private void ReScanErr(){
		 AlertDialog.Builder bulider = 
					 new AlertDialog.Builder(this).setTitle(R.string.CuoWu).setMessage("数据加载出现错误"+"\r\n"+"退出该模块并且再次尝试加载");

		 bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false).create().show();
		 MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		 return;
	}

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
			ArrayList<String> ScanedOutPos = new ArrayList<String>();
			ArrayList<String> ScanedInPos = new ArrayList<String>();

			String[] Bars;
			String[] HWAndBar;
			HWAndBar = res.split("\\$");

			String sHWIndex = HWAndBar[0];
			int iHWIndex = Integer.valueOf(sHWIndex);
			res = HWAndBar[1];

			if (res.contains(",")) {
				ReScanBody = "0";
				Bars = res.split("\\,");

				for (int i = 0; i < Bars.length; i++) {
					Bars = res.split("\\,");
					String[] ScanDetal;
					ScanDetal = Bars[i].split("\\!");

					ScanedBillBar.add(ScanDetal[0]);
					ScanedOutPos.add(ScanDetal[1]);
					ScanedInPos.add(ScanDetal[2]);
				}
			} else {
				ReScanBody = "0";
				Bars = res.split("\\,");
				String[] ScanDetal;
				ScanDetal = res.split("\\!");

				ScanedBillBar.add(ScanDetal[0]);
				ScanedOutPos.add(ScanDetal[1]);
				ScanedInPos.add(ScanDetal[2]);
				// ScanedBillBar.add(res);
			}

			if (wareHousePK == null || wareHousePK.equals("")) {
				int xw = 0;
				for (int iw = 0; iw < 1; iw++) {
					if (xw > 0) {
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						Common.ReScanErr = true;
			    		ReScanErr();
			    		return;
					}
					ScanBarcode(ScanedBillBar.get(0).toString());
					
					if(warehouseList!=null&&warehouseList.length>0)
					{
						wareHousePK = warehouseList[iHWIndex];
						m_OrgID = OrgList[iHWIndex];
						currentObj.SetvFree1(vFree1List[iHWIndex]);
						// companyIdList
						m_companyId = companyIdList[iHWIndex];

						if (bar.AccID.equals("A")) {
							// m_companyCode="101";
						} else {
							m_companyCode = "1";
						}
						m_AccID = bar.AccID;

						String invName = currentObj.getInvCode() + ":"
								+ currentObj.getInvName();
						String cBatch = currentObj.GetBatch();
						String cSerino = currentObj.GetSerino();

						txtBatch.setText(cBatch);
						txtSerino.setText(cSerino);
						txtInv.setText(invName);
					}

					if (wareHousePK == null || wareHousePK.equals("")) {
						iw--;
						xw++;
					}
				}

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

				FindPositionByCode(ScanedOutPos.get(i).toString(), "0");

				FindPositionByCode(ScanedInPos.get(i).toString(), "1");

				String OKflg = "ng";

				if (ScanedBarcode == null || ScanedBarcode.size() < 1) {
					i--;
					x++;
				} else {
					for (int j = 0; j < ScanedBarcode.size(); j++) {

						String AAA = ScanedBarcode.get(j);
						String BBB = ScanedBillBar.get(i).toString() + "!"
								+ ScanedOutPos.get(i).toString() + "!"
								+ ScanedInPos.get(i).toString();
						if (BBB.equals(AAA)) {
							OKflg = "ok";
						}

					}
					if (!OKflg.equals("ok")) {
						i--;
						x++;
					}
				}
			}

			this.txtBarcode.requestFocus();

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
		getMenuInflater().inflate(R.menu.stock_move_scan, menu);
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

	private void IniScan() {
		this.currentObj = null;
		this.txtBatch.setText("");
		this.txtSerino.setText("");
		this.txtInv.setText("");
		this.txtBarcode.setText("");
		if (!this.swhSavePos.isChecked()) {
			this.txtOutPos.setText("");
			this.m_OutPosID = "";

		}
		// ADD CAIXY START 15/04/14
		if (!this.swhSavePosIn.isChecked()) {
			this.txtInPos.setText("");
			this.m_InPosID = "";
		}
		// ADD CAIXY END 15/04/14
		// DEL CAIXY START 15/04/14
		// this.txtInPos.setText("");
		// this.m_InPosID = "";
		// DEL CAIXY END 15/04/14

		// if(this.bHaveHead == false)
		// {
		// this.txtInPos.setText("");
		// this.m_InPosID = "";
		// }
		this.txtBarcode.requestFocus();
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

		// String lswareHousePK = "";
		// if(wareHousePK==null||wareHousePK.equals(""))
		// {
		// lswareHousePK = "null";
		// }
		// else
		// {
		// lswareHousePK = wareHousePK;
		// }

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

		writeTxt.writeTxtToFile(ScanedFileName, wareHouseindex + "$"
				+ ScanedBar);

	}

	// 功能函数
	private void ScanBarcode(String barcode) throws JSONException,
			ParseException, IOException {
		if (barcode.equals("")) {
			Toast.makeText(this, R.string.QingSaoMiaoTiaoMa, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			txtBarcode.requestFocus();
			return;
		}

		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
		// IniScan();
		// 条码分析

		bar = new SplitBarcode(barcode);
		if (bar.creatorOk == false) {
			Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			txtBarcode.setText("");
			txtBarcode.requestFocus();
			return;
		}

		if (!Common.CheckUserRole(bar.AccID, "1001", "40081014")) {
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			Toast.makeText(StockMoveScan.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG)
					.show();
			return;
		}

		// if(!CheckHasScaned(bar))
		// {
		//
		// txtBarcode.setText("");
		// txtBarcode.requestFocus();
		// Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG).show();
		// //ADD CAIXY TEST START
		// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// //ADD CAIXY TEST END
		// return;
		// }
		//
		// 判断是否已经有AccID,如果有但是和扫描出来的AccID不一样,提示错误.
		if (m_AccID != null && !m_AccID.equals("")) {
			if (!m_AccID.equals(bar.AccID)) {

				txtBarcode.setText("");
				txtBarcode.requestFocus();
				Toast.makeText(this, "扫描的条码不属于该帐套,该货品不能够扫入", Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}
		}

		String FinishBarCode = bar.FinishBarCode;

		if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
			for (int si = 0; si < ScanedBarcode.size(); si++) {
				String BarCode = ScanedBarcode.get(si).toString();
				String[] ScanDetal;
				ScanDetal = BarCode.split("\\!");

				if (ScanDetal[0].equals(FinishBarCode)) {
					txtBarcode.setText("");
					txtBarcode.requestFocus();
					Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG)
							.show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return;
				}
			}
		}

		//
		if (!ConformDetail(barcode, bar)) {
			txtBarcode.setText("");
			txtBarcode.requestFocus();
			return;
		}

		if (!ConformBatch(bar.cInvCode, bar.cBatch, bar.AccID)) {
			// 表示这个批次这里没有，需要重新打印
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			Toast.makeText(this, "找不到对应的库存信息,请检查批次是否正确", Toast.LENGTH_LONG)
					.show();
			txtBarcode.setText("");
			txtBarcode.requestFocus();
			return;
		}

		if (wareHousePK == null || wareHousePK.equals("")) {
			if (ReScanBody.equals("1")) {
				MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
				showSingleChoiceDialog();

			}
			return;
		} else {

			// if(!ConformSerian(bar,bar.AccID))
			// {
			// //表示这个序列号校验错误
			// txtBarcode.setText("");
			// txtBarcode.requestFocus();
			// return;
			// }

			String invName = currentObj.getInvCode() + ":"
					+ currentObj.getInvName();
			String cBatch = currentObj.GetBatch();
			String cSerino = currentObj.GetSerino();

			txtBatch.setText(cBatch);
			txtSerino.setText(cSerino);
			txtInv.setText(invName);
			MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
			if (txtOutPos.getText().toString().equals("")) {
				txtOutPos.requestFocus();
				return;
			}

			if (txtInPos.getText().toString().equals("")) {
				txtInPos.requestFocus();
				return;
			}
			// ADD CAIXY START 15/04/16
			else {
				if (!FindInvnBinStockInfo(m_OutPosID)) {
					txtOutPos.requestFocus();
					return;
				}
			}
			// ADD CAIXY END 15/04/16

			DataBindtoJons();
			// wuqiong
			// try {
			// this.m_mData = getScanData();
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// return;
			// }
			tvmscount.setText("已扫描" + jonsSerialNo.size() + "件");

			// MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
		}

	}

	//
	private void DataBindtoJons() throws JSONException {

		// JSONObject obj = new JSONObject();
		HashMap<String, String> obj = new HashMap<String, String>();
		if (jonsScan == null)
			jonsScan = new ArrayList<Map<String, String>>();
		if (jonsSerialNo == null)
			jonsSerialNo = new ArrayList<Map<String, String>>();

		String Identity = currentObj.AccID() + currentObj.getInvCode()
				+ currentObj.GetBatch() + currentObj.GetSerino();

		if (jonsScan.size() == 0) {
			putcurrentIDToHashTable(Identity, currentObj.currentID());
			// JSONArray objs = new JSONArray();
			obj.put("cinventoryid", currentObj.Invmandoc());
			obj.put("cinvbasid", currentObj.Invbasdoc());
			obj.put("clot", currentObj.GetBatch());
			obj.put("cinventoryname", currentObj.getInvName());
			obj.put("cspaceidf", this.m_OutPosID);
			obj.put("cspaceidt", this.m_InPosID);
			Double v = 1.0;
			obj.put("spacenum", v.toString());
			obj.put("vfree1", currentObj.vFree1());
			obj.put("id", "0");
			obj.put("accid", currentObj.AccID());
			obj.put("invcode", bar.cInvCode);
			jonsScan.add(obj);

			HashMap<String, String> serObj = new HashMap<String, String>();

			ArrayList array = (ArrayList) SerialValues.get(Identity);
			serObj.put("id", "0");
			serObj.put("identity", Identity);
			serObj.put("cspaceidf", this.m_OutPosID);
			serObj.put("cspaceidt", this.m_InPosID);
			serObj.put("FromPos", m_OutPosCode);
			serObj.put("ToPos", m_InPosCode);
			if (array.size() == Integer.parseInt(currentObj.totalID()
					.replaceFirst("^0*", "")))// 这里来判断打包的是否全部扫描完毕,全部完毕为1,否则为零
			{
				serObj.put("isfinish", " ");
			} else {
				serObj.put("isfinish", "分包未完");
			}
			serObj.put("currentid", currentObj.currentID());
			serObj.put("cSerino", currentObj.GetSerino());
			serObj.put("cBarcode", bar.CheckBarCode);

			jonsSerialNo.add(serObj);
		} else {
			for (int i = 0; i < jonsSerialNo.size(); i++) {
				if (jonsSerialNo.get(i).get("identity").equals(Identity)) {
					if (!jonsSerialNo.get(i).get("cspaceidf")
							.equals(this.m_OutPosID)
							|| !jonsSerialNo.get(i).get("cspaceidt")
									.equals(m_InPosID)) {
						// 这里提示和第一个扫描的不一致,扫描错误.
						Toast.makeText(this, "扫描的分包货品被扫描到不同的货位,不能处理!",
								Toast.LENGTH_LONG).show();
						// ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						// ADD CAIXY TEST END
						return;
					}

					if (jonsSerialNo.get(i).get("cspaceidf")
							.equals(this.m_OutPosID)
							&& jonsSerialNo.get(i).get("cspaceidt")
									.equals(m_InPosID)) {
						jonsSerialNo.remove(i);
					}
				}
			}

			// JSONArray details = jonsScan.getJSONArray("ScanDetail");
			boolean bfindsame = false;
			for (int i = 0; i < jonsScan.size(); i++) {
				if (jonsScan.get(i).get("cinventoryid")
						.equals(currentObj.Invmandoc())
						&& jonsScan.get(i).get("clot")
								.equals(currentObj.GetBatch())
						&& jonsScan.get(i).get("cspaceidf")
								.equals(this.m_OutPosID)
						&& jonsScan.get(i).get("cspaceidt").equals(m_InPosID)) {
					String v = jonsScan.get(i).get("spacenum");
					if (!SerialValues.containsKey(Identity))// 如果该条码序列号之前已经又过了,则数量不累加上去
					{ // 这里是分包的情况,只有在第一次的时候数量累加上去.
						// v = v + 1;
						double vs = Double.valueOf(v);
						vs = vs + 1;
						jonsScan.get(i).put("spacenum", vs + "");
					}
					putcurrentIDToHashTable(Identity, currentObj.currentID());

					// JSONArray serobjs = new JSONArray();

					HashMap<String, String> serObj = new HashMap<String, String>();
					serObj.put("id", i + "");
					ArrayList array = (ArrayList) SerialValues.get(Identity);

					serObj.put("identity", Identity);
					serObj.put("cSerino", currentObj.GetSerino());
					serObj.put("cBarcode", bar.CheckBarCode);
					serObj.put("isfinish", "分包未完");
					serObj.put("currentid", currentObj.currentID());
					serObj.put("cspaceidf", this.m_OutPosID);
					serObj.put("cspaceidt", this.m_InPosID);
					serObj.put("FromPos", m_OutPosCode);
					serObj.put("ToPos", m_InPosCode);
					jonsSerialNo.add(serObj);
					if (array.size() == Integer.parseInt(currentObj.totalID()
							.replaceFirst("^0*", "")))// 这里来判断打包的是否全部扫描完毕,全部完毕为1,否则为零
					{

						for (int j = 0; j < jonsSerialNo.size(); j++) {
							if (jonsSerialNo.get(j).get("identity")
									.equals(Identity)) {
								jonsSerialNo.get(j).put("isfinish", " ");// 设置成已经完成拼箱处理
							}
						}
					}

					bfindsame = true;
					break;
				}

			}

			if (bfindsame == false) {
				putcurrentIDToHashTable(Identity, currentObj.currentID());

				obj.put("cinventoryid", currentObj.Invmandoc());
				obj.put("cinvbasid", currentObj.Invbasdoc());
				obj.put("clot", currentObj.GetBatch());
				obj.put("cinventoryname", currentObj.getInvName());
				obj.put("cspaceidf", this.m_OutPosID);
				obj.put("cspaceidt", this.m_InPosID);
				String v = "1.0";
				obj.put("spacenum", v);
				obj.put("vfree1", currentObj.vFree1());
				obj.put("accid", currentObj.AccID());
				obj.put("id", jonsScan.size() + "");
				obj.put("invcode", bar.cInvCode);
				jonsScan.add(obj);

				// JSONObject serObj = new JSONObject();
				//
				// JSONArray serobjs = new JSONArray();
				HashMap<String, String> serObj = new HashMap<String, String>();

				ArrayList array = (ArrayList) SerialValues.get(Identity);
				serObj.put("id", jonsScan.size() + "");
				serObj.put("identity", Identity);
				serObj.put("cspaceidf", this.m_OutPosID);
				serObj.put("cspaceidt", this.m_InPosID);
				serObj.put("FromPos", m_OutPosCode);
				serObj.put("ToPos", m_InPosCode);
				if (array.size() == Integer.parseInt(currentObj.totalID()
						.replaceFirst("^0*", "")))// 这里来判断打包的是否全部扫描完毕,全部完毕为1,否则为零
				{
					serObj.put("isfinish", " ");
				} else {
					serObj.put("isfinish", "分包未完");
				}
				serObj.put("currentid", currentObj.currentID());
				serObj.put("cSerino", currentObj.GetSerino());
				serObj.put("cBarcode", bar.CheckBarCode);

				jonsSerialNo.add(serObj);
			}

		}
		// MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
		IniScan();

		String lsFinishBarCode = bar.FinishBarCode + "!" + m_OutPosCode + "!"
				+ m_InPosCode;

		ScanedBarcode.add(lsFinishBarCode);
		SaveScanedBody();

		txtBarcode.requestFocus();

	}

	private boolean ConformSerian(SplitBarcode bar, String AccID)
			throws JSONException, ParseException, IOException {
		JSONObject serList = null;
		JSONObject para = new JSONObject();

		para.put("FunctionName", "GetSerialNo");
		para.put("TableNameBind", "Bind");
		para.put("TableNameCV", "CV");
		para.put("SerialNo", bar.cSerino);
		para.put("Invcode", bar.cInvCode);
		para.put("Batch", bar.cBatch);

		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		serList = Common.DoHttpQuery(para, "CommonQuery", AccID);

		if (serList == null) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		if (!serList.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		if (!serList.getBoolean("Status")) {
			// Toast.makeText(this, serList.getString("找不到相关序列号信息"),
			// Toast.LENGTH_LONG).show();
			Toast.makeText(this, "找不到相关序列号信息", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return false;
		}

		JSONArray arys = serList.getJSONArray("Bind");
		JSONObject serino = arys.getJSONObject(0);
		if (!serino.getString("storcode").equals(wareHousePK)) {
			Toast.makeText(this, "该序列号不属于你的仓库", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END

			return false;
		}
		if (serino.getString("status").equals("0")) {
			Toast.makeText(this, "在库状态不对", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return false;
		}
		if (jonsSerialNo == null)
			return true;

		if (jonsSerialNo.size() == 0) {
			return true;
		}

		// 这里不需要判断了
		// for(int i = 0;i<jonsSerialNo.size();i++)
		// {
		// if(jonsSerialNo.get(i).get("serino").equals(bar.cSerino))
		// {
		// Toast.makeText(this, "该序列号已经扫描过了",
		// Toast.LENGTH_LONG).show();
		// return false;
		// }
		// }

		return true;
	}

	private boolean ConformBatch(String invcode, String batch, String AccID)
			throws JSONException, ParseException, IOException {
		// 获得当前存货的库存 Jonson
		JSONObject batchList = null;
		JSONObject para = new JSONObject();
		String CompanyCode = "";
		if (AccID.equals("A")) {
			CompanyCode = m_companyCode;
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
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, "找不到对应的库存信息", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return false;
		}

		JSONArray jsarray = batchList.getJSONArray("batch");
		ArrayList array = new ArrayList();
		ArrayList arrayName = new ArrayList();
		ArrayList arrayFree1 = new ArrayList();
		ArrayList arrayOrg = new ArrayList();
		ArrayList arrayCompanyId = new ArrayList();
		for (int i = 0; i < jsarray.length(); i++) {
			if (batch.equals(jsarray.getJSONObject(i).getString("vlot"))) {
				if (wareHousePK == null || wareHousePK.equals("")) {
					// warehouseList = (String[]) .add(BillItem_olds,
					// BillItem_old);
					if (array.indexOf(jsarray.getJSONObject(i).getString(
							"whname")) == -1) {
						array.add(jsarray.getJSONObject(i).getString("whname"));
						arrayName.add(jsarray.getJSONObject(i).getString(
								"cwarehouseid"));
						arrayFree1.add(jsarray.getJSONObject(i).getString(
								"vfree1"));
						arrayOrg.add(jsarray.getJSONObject(i).getString(
								"ccalbodyid"));
						arrayCompanyId.add(jsarray.getJSONObject(i).getString(
								"pk_corp"));
					} else {
						continue;
					}
				} else {
					if (wareHousePK.equals(jsarray.getJSONObject(i).getString(
							"cwarehouseid"))) {
						currentObj.SetvFree1(jsarray.getJSONObject(i)
								.getString("vfree1"));

						return true;
					}
				}

			}
		}
		if (wareHousePK != null && !wareHousePK.equals("")) {
			// Toast.makeText(this,
			// batchList.getString("该货品在你选择的仓库中不存在,请不要扫描该货品!"),
			// Toast.LENGTH_LONG).show();
			Toast.makeText(this, "该存货不属于你所选择的仓库,不能扫描该存货", Toast.LENGTH_LONG)
					.show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return false;
		}
		// 需要异常处理一下。
		if (array.size() != 0) {
			Object[] objs = array.toArray();
			Object[] objsname = arrayName.toArray();
			Object[] objvFree1List = arrayFree1.toArray();
			Object[] objvOrg = arrayOrg.toArray();
			Object[] objvcompanyId = arrayCompanyId.toArray();

			warehouseList = new String[objs.length];
			warehouseNameList = new String[objs.length];
			vFree1List = new String[objs.length];
			OrgList = new String[objs.length];
			companyIdList = new String[objs.length];

			for (int i = 0; i < objsname.length; i++) {
				warehouseList[i] = objsname[i].toString();
			}

			for (int i = 0; i < objs.length; i++) {
				warehouseNameList[i] = objs[i].toString();
			}
			for (int i = 0; i < objvFree1List.length; i++) {
				vFree1List[i] = objvFree1List[i].toString();
			}
			for (int i = 0; i < objvOrg.length; i++) {
				OrgList[i] = objvOrg[i].toString();
			}

			for (int i = 0; i < objvcompanyId.length; i++) {
				companyIdList[i] = objvcompanyId[i].toString();
			}

			return true;
		}

		return false;
	}

	// 确认存货在上游单据内有
	private boolean ConformDetail(String barcode, SplitBarcode bar)
			throws JSONException, ParseException, IOException {
		if (jonsHead == null && jonsBody == null && bHaveHead == true) {
			Toast.makeText(this, R.string.MeiYouZhaoDaoCanZhao, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return false;
		}
		if (bHaveHead == false) {
			try {

				if (bar.AccID.equals("A")) {
					currentObj = new Inventory(bar.cInvCode, m_companyCode,
							bar.AccID);
				} else {
					currentObj = new Inventory(bar.cInvCode, "1", bar.AccID);
				}
			} catch (Exception ex) {
				Toast.makeText(this, "扫描异常，请重新扫描", Toast.LENGTH_LONG).show();
				return false;
			}
			currentObj.SetSerino(bar.cSerino);
			currentObj.SetBatch(bar.cBatch);
			currentObj.SetcurrentID(bar.currentBox);
			currentObj.SettotalID(bar.TotalBox);
			currentObj.SetAccID(bar.AccID);
			if (currentObj.getErrMsg() != null
					&& !currentObj.getErrMsg().equals("")) {
				Toast.makeText(this, currentObj.getErrMsg(), Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return false;
			}

			return true;// 没有参照源头的货位调整，不需要校验
		}

		JSONArray jsarray = jonsBody.getJSONArray("Body");
		for (int i = 0; i < jsarray.length(); i++) {
			// 确认了存货
			if (jsarray.getJSONObject(i).getString("invcode")
					.equals(bar.cInvCode)) {
				// currentObj = new Inventory(jsarray.getJSONObject(i),1);
				try {
					currentObj = new Inventory(bar.cInvCode, "BADV", bar.AccID);
				} catch (Exception ex) {
					Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG)
							.show();
					return false;
				}
				if (currentObj.getErrMsg() != null
						&& !currentObj.getErrMsg().equals("")) {
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
				Double qty = jsarray.getJSONObject(i).getDouble("nnum");
				if(!ConformDetailQty(qty))
				{
					return false;
				}
				return true;
				
			}
		}
		Toast.makeText(this, "存货在上游单据中不存在,请校验", Toast.LENGTH_LONG).show();
		// ADD CAIXY TEST START
		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		// ADD CAIXY TEST END
		return false;
	}

	// 确认如果有上游单据,那么判断是否超过其数量
	private boolean ConformDetailQty(Double qty) throws JSONException {
		if (jonsScan == null || jonsSerialNo == null) {
			return true;
		}
		for (int i = 0; i < jonsScan.size(); i++) {
			Double inQty = 0.0;
			Map<String, String> temp = (Map<String, String>) jonsScan.get(i);
			if (temp.get("cinvbasid").equals(currentObj.Invbasdoc())) {
				inQty += Double.valueOf(temp.get("spacenum").toString());
			}

			if (inQty >= qty) {
				// 判断是否序列号内控制
				for (int j = 0; j < jonsSerialNo.size(); j++) {
					Map<String, String> temp1 = (Map<String, String>) jonsSerialNo
							.get(i);
					if (temp1.get("cSerino").equals(currentObj.GetSerino())
							&& temp1.get("isfinish").equals("分包未完")) {
						return true;
					}
				}
				//
				Toast.makeText(this, "超过上游单据规定数量,不允许操作", Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return false;
			}

		}

		return true;
	}

	/**
	 * 判断该条码是否已经被扫描过了
	 * 
	 * @return 如果为true 代表没有被扫描过,如果false 代表已经被扫描过了
	 */
	private boolean CheckHasScaned(SplitBarcode bar) {
		String Identity = bar.AccID + bar.cInvCode + bar.cBatch + bar.cSerino;
		if (SerialValues != null) {
			if (SerialValues.containsKey(Identity)) {
				ArrayList array = (ArrayList) SerialValues.get(Identity);
				for (int i = 0; i < array.size(); i++) {
					if (array.get(i).toString().equals(bar.currentBox)) {
						return false;
					}
				}

			}
		}
		return true;
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

	private void showSingleChoiceDialog() {

		SelectButton = new AlertDialog.Builder(this).setTitle(R.string.XuanZeCangKu)
				.setSingleChoiceItems(warehouseNameList, 0, buttonOnClick)
				.setPositiveButton(R.string.QueRen, buttonOnClick)
				.setNegativeButton(R.string.QuXiao, buttonOnClick).show();
	}

	private void ConfirmDelItem(int index) {
		ButtonOnClickDelconfirm buttondel = new ButtonOnClickDelconfirm(index);
		SelectButton = new AlertDialog.Builder(this).setTitle(R.string.QueRenShanChu)
				.setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa).setPositiveButton(R.string.QueRen, buttondel)
				.setNegativeButton(R.string.QuXiao, null).show();
	}

	private class ButtonOnClick implements DialogInterface.OnClickListener {
		public int index;

		public ButtonOnClick(int index) {
			this.index = index;
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			if (whichButton >= 0) {
				index = whichButton;
				// dialog.cancel();
			} else {
				if (dialog.equals(SelectButton)) {
					if (whichButton == DialogInterface.BUTTON_POSITIVE) {

						String lswareHousePK = warehouseList[index];
						if (!Common.CheckUserWHRole(bar.AccID, "1001",
								lswareHousePK)) {
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							Toast.makeText(StockMoveScan.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian,
									Toast.LENGTH_LONG).show();
							return;
						}

						wareHouseindex = index;
						wareHousePK = warehouseList[index];
						m_OrgID = OrgList[index];
						currentObj.SetvFree1(vFree1List[index]);
						// companyIdList
						m_companyId = companyIdList[index];

						if (bar.AccID.equals("A")) {
							// m_companyCode="101";
						} else {
							m_companyCode = "1";
						}
						m_AccID = bar.AccID;

						String invName = currentObj.getInvCode() + ":"
								+ currentObj.getInvName();
						String cBatch = currentObj.GetBatch();
						String cSerino = currentObj.GetSerino();

						txtBatch.setText(cBatch);
						txtSerino.setText(cSerino);
						txtInv.setText(invName);

						if (txtOutPos.getText().toString().equals("")) {
							txtOutPos.requestFocus();
							return;
						}

						if (txtInPos.getText().toString().equals("")) {
							txtInPos.requestFocus();
							return;
						}

						try {
							DataBindtoJons();

							tvmscount
									.setText("已扫描" + jonsSerialNo.size() + "件");

							return;
						} catch (JSONException e) {
							Toast.makeText(StockMoveScan.this,
									"绑定JSON过程中发生了错误,请确认!", Toast.LENGTH_LONG)
									.show();
							// ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							// ADD CAIXY TEST END
						}

					} else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
						return;
					}
				}
			}

		}

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
					String barcode = (String) map.get("cBarcode");
					String Identity = (String) map.get("identity");
					String spf = (String) map.get("cspaceidf");
					String spt = (String) map.get("cspaceidt");

					for (int i = 0; i < jonsSerialNo.size(); i++) {

						if (jonsSerialNo.get(i).get("cBarcode").equals(barcode)) {
							ArrayList lists = (ArrayList) SerialValues
									.get(jonsSerialNo.get(i).get("identity"));
							lists.remove(jonsSerialNo.get(i).get("currentid"));
							if (lists.size() == 0)// 如果全部没有了则把hashtable的key也删除掉
							{
								SerialValues.remove(jonsSerialNo.get(i).get(
										"identity"));
							}
							jonsSerialNo.remove(i);
							// break;
						}

					}

					if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
						for (int si = 0; si < ScanedBarcode.size(); si++) {
							String RemoveBarCode = ScanedBarcode.get(si)
									.toString();

							String[] ScanDetal;
							ScanDetal = RemoveBarCode.split("\\!");

							RemoveBarCode = ScanDetal[0];

							int iBarlenth = RemoveBarCode.length() - 6;
							String RemoveBarCodeF = RemoveBarCode.substring(0,
									iBarlenth);

							if (RemoveBarCodeF.equals(barcode)) {
								ScanedBarcode.remove(si);
								si--;
							}
						}
					}

					// //boolean Stillhas=false;//是否还有其他分包
					// for(int i = 0;i<jonsSerialNo.size();i++)//还要把isfinish改成0
					// {
					//
					// if(jonsSerialNo.get(i).get("identity").equals(Identity))
					// {
					// jonsSerialNo.get(i).put("isfinish", "分包未完");
					// //Stillhas=true;
					// break;
					// }
					//
					// }

					for (int i = 0; i < jonsScan.size(); i++) {
						if (jonsScan.get(i).get("cspaceidf").equals(spf)
								&& jonsScan.get(i).get("cspaceidt").equals(spt)
								&& Identity.startsWith(jonsScan.get(i)
										.get("accid").toString()
										+ jonsScan.get(i).get("invcode")
												.toString()
										+ jonsScan.get(i).get("clot")
												.toString())) {
							// if(Stillhas==false)
							// {

							double values = Double.valueOf(jonsScan.get(i).get(
									"spacenum"));
							values = values - 1;
							if (values == (double) 0) // 如果全部没有了直接删除行
							{
								jonsScan.remove(i);
							} else// 如果只是分包没有了,还有其他值,则数值减一
							{
								jonsScan.get(i).put("spacenum", values + "");
							}
							// }
							// 如果还有分包,则不对Scandetail的值进行处理.
						}
					}

					SaveScanedBody();
					// ADD CAIXY START
					if (jonsSerialNo.size() == 0) {
						if (!bHaveHead) {
							wareHousePK = "";
						}

						m_InPosID = "";
						m_OutPosID = "";
						m_InPosCode = "";
						m_OutPosCode = "";
						txtOutPos.setText("");
						txtInPos.setText("");
						txtBarcode.setText("");
						txtBarcode.requestFocus();
						fileScan.delete();
					}
					DeleteButton.cancel();
					// ADD CAIXY START
					// wuqiong
					tvmscount.setText("已扫描" + jonsSerialNo.size() + "件");

				} else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
					return;
				}
			}
		}
	}

}
