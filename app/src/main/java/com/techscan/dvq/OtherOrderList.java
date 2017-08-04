package com.techscan.dvq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherOrderList extends Activity {

    public String                    m_OrderType;
    public String                    m_TypeName;
//	private SoundPool sp;// 声明一个SoundPool
//	private int MainLogin.music;// 定义一个int来设置suondID
    @Nullable
    public List<Map<String, Object>> mData;
    @Nullable
    Button btOtherOrderlistReturn = null;
	public String m_AccID;
	public String m_PkCorp;
	public String m_BillCode;
	//test

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_order_list);

		// ADD CAIXY START
//		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		// ADD CAIXY END

		btOtherOrderlistReturn = (Button) findViewById(R.id.btOtherOrderlistReturn);
		btOtherOrderlistReturn.setOnClickListener(ButtonOnClickListener);

		this.m_OrderType = this.getIntent().getStringExtra("OrderType");
		this.m_TypeName = this.getIntent().getStringExtra("Typename");
		String lsBillCode = this.getIntent().getStringExtra("BillCode");
		lsBillCode = lsBillCode.toUpperCase();
		lsBillCode = lsBillCode.replace("\n", "");
		if (lsBillCode.equals("")) {
			m_AccID = "";
			m_PkCorp = "";
			m_BillCode = "";
		}

		this.setTitle(m_TypeName + "单明细");

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		// this.m_OrderType = this.getIntent().getStringExtra("OrderType");
		// this.m_TypeName = this.getIntent().getStringExtra("Typename");

		// 获得数据
		JSONObject jas = GetOrderList(lsBillCode);

		if (jas == null)
		{
			if(!m_BillCode.equals(""))
			{
				this.finish();
			}
			return;
		}
		// 转换成通用 JSON

		try {
			mData = getData(jas);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		ListView list = (ListView) findViewById(R.id.OtherOrderlist);
		//
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, mData,// 数据源
				R.layout.vlistotherpds,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "No", "AccID", "Warehouse" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.listpdotherorder, R.id.listotheraccid,
						R.id.listotherware });
		
		
		list.setOnItemClickListener(itemListener);
		list.setAdapter(listItemAdapter);
		if(!m_BillCode.equals(""))
		{
			Map<String, Object> map = mData.get(0);

			String orderNo = map.get("No").toString();
			String billId = map.get("BillId").toString();
			String warehouse = map.get("Warehouse").toString();
			String warehouseID = map.get("WarehouseID").toString();
			String accid = map.get("AccID").toString();

			// BillTypeCodeList[8]="4L";
			// BillTypeCodeList[9]="4M";
			// BillTypeCodeList[10]="4N";
			String lsBilltype = "AAA";
			if (m_OrderType.equals("4L")) {
				lsBilltype = "40081004";
			}
			if (m_OrderType.equals("4M")) {
				lsBilltype = "40081006";
			}
			if (m_OrderType.equals("4N")) {
				lsBilltype = "40081008";
			}

			if (!Common.CheckUserRole(accid, map.get("pk_corp").toString(),
                                      lsBilltype)) {
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				Toast.makeText(OtherOrderList.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian,
						Toast.LENGTH_LONG).show();
				this.finish();
				return;
			}

			if (!Common.CheckUserWHRole(accid, map.get("pk_corp").toString(),
					warehouseID)) {
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				Toast.makeText(OtherOrderList.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian,
						Toast.LENGTH_LONG).show();
				this.finish();
				return;
			}

			Intent intent = new Intent();
			intent.putExtra("result", "1");// 把返回数据存入Intent
			intent.putExtra("OrderNo", orderNo);
			intent.putExtra("OrderID", billId);
			intent.putExtra("WarehouseName", warehouse.toString());
			intent.putExtra("WarehouseID", warehouseID);
			intent.putExtra("AccID", accid);
			intent.putExtra("pk_corp", map.get("pk_corp").toString());
			OtherOrderList.this.setResult(1, intent);
			this.finish();
		}
		
		
	}

    @NonNull
    private OnClickListener ButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(@NonNull View v) {
			switch (v.getId()) { // btnSDScanReturn
			case id.btOtherOrderlistReturn:
				finish();
				break;
			}
		}
	};

	private JSONObject GetOrderList(@NonNull String lsBillCode) {
		// 做一个通用的JSON
		if (lsBillCode.length() > 0 && lsBillCode.length() < 5) {
			{
				Toast.makeText(this, R.string.ShuRuDeDanJuHaoBuZhengQue, Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return null;
			}
		} else {
			if (lsBillCode.length() > 5) {
				m_AccID = lsBillCode.substring(0, 1);
				if (!m_AccID.equals("A") && !m_AccID.equals("B")) {
					Toast.makeText(this, R.string.ShuRuDeDanJuHaoBuZhengQue, Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return null;
				}
				m_PkCorp = lsBillCode.substring(1, 5);
				m_BillCode = lsBillCode.substring(5);
			}

			JSONObject para = new JSONObject();

			String FunctionName = "CommonQuery";
			try {
				para.put("BillCode", m_BillCode);
				para.put("accId", m_AccID);
				para.put("pk_corp", m_PkCorp);
				para.put("BillType", m_OrderType);
				para.put("FunctionName", "GetOtherInOutHead");
				para.put("TableName", "PurGood");

			} catch (JSONException e2) {
				e2.printStackTrace();
				Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return null;
			}
			JSONObject jas;
			try {
				jas = Common.DoHttpQuery(para, FunctionName, m_AccID);
			} catch (Exception ex) {
				Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return null;
			}
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
					Toast.makeText(this, jas.getString("ErrMsg"),
							Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return null;
				}
				
				return jas;
				// mData = getData(jas);
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

	}

	private List<Map<String, Object>> getData(@NonNull JSONObject jas)
			throws JSONException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		JSONObject tempJso = null;

		if (!jas.has("PurGood"))
			return null;
		JSONArray jsarray = jas.getJSONArray("PurGood");

		for (int i = 0; i < jsarray.length(); i++) {
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();

			map.put("No", tempJso.getString("vbillcode"));
			map.put("Warehouse", tempJso.getString("storname"));
			// map.put("Vendor", tempJso.getString("ACCID"));
			map.put("BillId", tempJso.getString("cspecialhid"));
			map.put("WarehouseID", tempJso.getString("cwarehouseid"));

			if (tempJso.has("AccID")) {
				map.put("AccID", tempJso.getString("AccID"));
			} else {
				map.put("AccID", this.m_AccID);
			}
			map.put("pk_corp", tempJso.getString("pk_corp"));
			list.add(map);
		}
		return list;
	}

    @NonNull
    private ListView.OnItemClickListener itemListener = new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(@NonNull AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
			Adapter adapter = arg0.getAdapter();
			// @SuppressWarnings("unchecked")
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) adapter
					.getItem(arg2);

			String orderNo = map.get("No").toString();
			String billId = map.get("BillId").toString();
			String warehouse = map.get("Warehouse").toString();
			String warehouseID = map.get("WarehouseID").toString();
			String accid = map.get("AccID").toString();

			// BillTypeCodeList[8]="4L";
			// BillTypeCodeList[9]="4M";
			// BillTypeCodeList[10]="4N";
			String lsBilltype = "AAA";
			if (m_OrderType.equals("4L")) {
				lsBilltype = "40081004";
			}
			if (m_OrderType.equals("4M")) {
				lsBilltype = "40081006";
			}
			if (m_OrderType.equals("4N")) {
				lsBilltype = "40081008";
			}
//
//			if (!Common.CheckUserRole(accid, map.get("pk_corp").toString(),
//					lsBilltype)) {
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				Toast.makeText(OtherOrderList.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian,
//						Toast.LENGTH_LONG).show();
//				return;
//			}

//			if (!Common.CheckUserWHRole(accid, map.get("pk_corp").toString(),
//					warehouseID)) {
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				Toast.makeText(OtherOrderList.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian,
//						Toast.LENGTH_LONG).show();
//				return;
//			}

			Intent intent = new Intent();
			intent.putExtra("result", "1");// 把返回数据存入Intent
			intent.putExtra("OrderNo", orderNo);
			intent.putExtra("OrderID", billId);
			intent.putExtra("WarehouseName", warehouse.toString());
			intent.putExtra("WarehouseID", warehouseID);
			intent.putExtra("AccID", accid);
			intent.putExtra("pk_corp", map.get("pk_corp").toString());
			OtherOrderList.this.setResult(1, intent);
			OtherOrderList.this.finish();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.other_order_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
