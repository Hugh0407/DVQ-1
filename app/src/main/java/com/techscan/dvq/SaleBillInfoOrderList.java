package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleBillInfoOrderList extends Activity {

	//private Button btnSaleBillInfoReturn;
	private ListView lvSaleBillInfoOrderList;

	private String fsFunctionName = "";
	private String fsBillCode = "";
	private String sDate;
	private String sEndDate;
	private String sBillCodes;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	Button btSaleBillInfoOrderListReturn =null;

	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//ADD CAIXY TEST END

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale_bill_info_order_list);

		//设置title
		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle("订单明细");
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		actionBar.show();

		btSaleBillInfoOrderListReturn = (Button)findViewById(R.id.btSaleBillInfoOrderListReturn);
		btSaleBillInfoOrderListReturn.setOnClickListener(ButtonOnClickListener);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
				.detectNetwork()
				.penaltyLog()
				.build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
				detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

		//取得查询用单据表名
		Intent myIntent = this.getIntent();
		if(myIntent.hasExtra("FunctionName"))
			fsFunctionName = myIntent.getStringExtra("FunctionName");
		if(myIntent.hasExtra("BillCodeKey"))
			fsBillCode = myIntent.getStringExtra("BillCodeKey");
		if (myIntent.hasExtra("sBeginDate"))
			sDate = myIntent.getStringExtra("sBeginDdate");
		if (myIntent.hasExtra("sEndDate"))
			sEndDate = myIntent.getStringExtra("sEndDate");
		if (myIntent.hasExtra("sBillCodes"))
			sBillCodes = myIntent.getStringExtra("sBillCodes");




		//取得控件
		//btnSaleBillInfoReturn = (Button)findViewById(R.id.btnSaleBillInfoReturn);
		//btnSaleBillInfoReturn.setOnClickListener(ButtonClickListener);
		lvSaleBillInfoOrderList = (ListView)findViewById(R.id.SaleBillInfoOrderList);

		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		//ADD CAIXY END

		//取得以及绑定显示订单详细
		GetAndBindingBillInfoDetail();

	}

	private OnClickListener ButtonOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{			//btnSDScanReturn
				case id.btSaleBillInfoOrderListReturn:
					finish();
					break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.sale_bill_info_order_list, menu);
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

	//取得以及绑定显示订单详细
	private void GetAndBindingBillInfoDetail()
	{
		//取得所有单据信息
		if(fsFunctionName.equals(""))
		{
			Toast.makeText(this, "没有查询用单据表名", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}

		int x = 1;

		if(fsFunctionName.equals("销售出库"))
		{
			fsFunctionName = "GetSaleOrderList";
		}
//

		for(int i = 0;i<x;i++)
		{

			if(x==2)
			{
				if(i==0)
				{
					fsFunctionName = "GetSaleTakeHead";
				}
				else
				{
					fsFunctionName = "GetSaleOutHead";
				}
			}

			JSONObject para = new JSONObject();
			try {
				para.put("FunctionName", fsFunctionName);
//				para.put("CorpPK", MainLogin.objLog.CompanyID);
				para.put("STOrgCode", MainLogin.objLog.STOrgCode);
				para.put("BillCode", sBillCodes);
				para.put("sDate",sDate);
				para.put("sEndDate",sEndDate);
				//para.put("Wh-CodeA", MainLogin.objLog.WhCodeA);
				//para.put("Wh-CodeB", MainLogin.objLog.WhCodeB);


			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e2.printStackTrace();
				return;
			}
			try {
				para.put("TableName",  "dbHead");
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}

			JSONObject jas;
			try {
				if(!MainLogin.getwifiinfo()) {
					Toast.makeText(this, "WIFI信号差!请保持网络畅通",Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return ;
				}
				jas = Common.DoHttpQuery(para, "CommonQuery", "");
			} catch (Exception ex)
			{
				Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}

			//把取得的单据信息绑定到ListView上
			try
			{
				if(jas==null)
				{
					Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return;
				}

				if(!jas.has("Status"))
				{
					Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}


				if(!jas.getBoolean("Status"))
				{
					String errMsg = "";
					if(jas.has("ErrMsg"))
					{
						errMsg = jas.getString("ErrMsg");
					}
					else
					{
						errMsg = "网络操作出现问题!请稍后再试";
					}
					Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return;
				}
				//绑定到ListView
				BindingBillInfoData(jas);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			catch (Exception ex)
			{
				Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
		}

	}

	//绑定到ListView
	private void BindingBillInfoData(JSONObject jsonBillInfo) throws JSONException
	{

		Map<String, Object> map;

		JSONObject tempJso = null;

		if(!jsonBillInfo.has("Status"))
		{
			Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}

		if(!jsonBillInfo.getBoolean("Status"))
		{
			String errMsg = "";
			if(jsonBillInfo.has("ErrMsg"))
			{
				errMsg = jsonBillInfo.getString("ErrMsg");
			}
			else
			{
				errMsg = "网络操作出现问题!请稍后再试";
			}
			Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			list = null;
		}

		JSONArray jsarray= jsonBillInfo.getJSONArray("dbHead");

		for(int i = 0;i<jsarray.length();i++)
		{
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();
			if(fsFunctionName.equals("GetSalereceiveHead"))//销售出库单
			{
				map.put("pk_corp", tempJso.getString("pk_corp"));
				map.put("custname", tempJso.getString("custname"));
				map.put("pk_cubasdoc", tempJso.getString("pk_cubasdoc"));
				map.put("pk_cumandoc", tempJso.getString("pk_cumandoc"));
				map.put("billID", tempJso.getString("csalereceiveid"));
				map.put("billCode", tempJso.getString("vreceivecode"));
				map.put("dmakedate",tempJso.getString("dmakedate"));
				map.put("AccID", tempJso.getString("AccID"));
				map.put("vdef11", tempJso.getString("vdef11"));
				map.put("vdef12", tempJso.getString("vdef12"));
				map.put("vdef13", tempJso.getString("vdef13"));
				map.put("saleflg", "");
				if(tempJso.getString("AccID").equals("A"))
					map.put("coperatorid", MainLogin.objLog.UserID);//操作者
				else
					map.put("coperatorid", MainLogin.objLog.UserIDB);//操作者
				map.put("ctransporttypeid", tempJso.getString("ctransporttypeid"));//运输方式ID

				map.put("cbiztype", tempJso.getString("cbiztype"));

			}

			else if(fsFunctionName.equals("GetSaledH"))//退回再送单
			{

				map.put("pk_corp", tempJso.getString("pk_corp"));
				map.put("custname", tempJso.getString("custname"));
				map.put("pk_cubasdoc", tempJso.getString("pk_cubasdoc"));
				map.put("pk_cumandoc", tempJso.getString("ccustomerid"));
				map.put("billID", tempJso.getString("cgeneralhid"));
				map.put("billCode", tempJso.getString("vbillcode"));
				map.put("AccID", tempJso.getString("AccID"));
				map.put("vdef11", tempJso.getString("vuserdef11"));
				map.put("vdef12", tempJso.getString("vuserdef12"));
				map.put("vdef13", tempJso.getString("vuserdef13"));
				map.put("saleflg", "");
				if(tempJso.getString("AccID").equals("A"))
				{
					map.put("coperatorid", MainLogin.objLog.UserID);//操作者
					map.put("ctransporttypeid", "0001AA100000000003U7");//运输方式ID
				}
				else
				{
					map.put("coperatorid", MainLogin.objLog.UserIDB);//操作者
					map.put("ctransporttypeid", "0001DD10000000000XQT");//运输方式ID
				}

				map.put("cbiztype", tempJso.getString("cbiztype"));
			}

			else if(fsFunctionName.equals("GetSaleOutHead"))//退回不送单//D
			{

				map.put("pk_corp", tempJso.getString("pk_corp"));
				map.put("custname", tempJso.getString("custname"));
				map.put("pk_cubasdoc", tempJso.getString("pk_cubasdoc"));
				map.put("pk_cumandoc", tempJso.getString("ccustomerid"));
				map.put("billID", tempJso.getString("csaleid"));
				map.put("billCode", tempJso.getString("vreceiptcode"));
				map.put("AccID", tempJso.getString("AccID"));
				map.put("vdef11", tempJso.getString("vdef11"));
				map.put("vdef12", tempJso.getString("vdef12"));
				map.put("vdef13", tempJso.getString("vdef13"));
				map.put("saleflg", "D");
				if(tempJso.getString("AccID").equals("A"))
				{
					map.put("coperatorid", MainLogin.objLog.UserID);//操作者
					map.put("ctransporttypeid", "0001AA100000000003U7");//运输方式ID
				}
				else
				{
					map.put("coperatorid", MainLogin.objLog.UserIDB);//操作者
					map.put("ctransporttypeid", "0001DD10000000000XQT");//运输方式ID
				}
				map.put("cbiztype", tempJso.getString("cbiztype"));


			}

			else if(fsFunctionName.equals("GetSaleTakeHead"))//退回不送单//T
			{
				map.put("pk_corp", tempJso.getString("pk_corp"));
				map.put("custname", tempJso.getString("custname"));
				map.put("pk_cubasdoc", tempJso.getString("pk_cubasdoc"));
				map.put("pk_cumandoc", tempJso.getString("pk_cumandoc"));
				map.put("billID", tempJso.getString("pk_take"));
				map.put("billCode", tempJso.getString("vreceiptcode"));
				map.put("AccID", tempJso.getString("AccID"));
				map.put("vdef11", tempJso.getString("vdef11"));
				map.put("vdef12", tempJso.getString("vdef12"));
				map.put("vdef13", tempJso.getString("vdef13"));
				map.put("saleflg", "T");
				if(tempJso.getString("AccID").equals("A"))
				{
					map.put("coperatorid", MainLogin.objLog.UserID);//操作者
					map.put("ctransporttypeid", "0001AA100000000003U7");//运输方式ID
				}
				else
				{
					map.put("coperatorid", MainLogin.objLog.UserIDB);//操作者
					map.put("ctransporttypeid", "0001DD10000000000XQT");//运输方式ID
				}
				map.put("cbiztype", tempJso.getString("cbiztype"));
			}

			list.add(map);
		}
		SimpleAdapter listItemAdapter = null;

		listItemAdapter = new SimpleAdapter(this,list,
				R.layout.vlistsaledel,
				new String[] {"billCode","AccID","custname"},
				new int[] {R.id.listsaledelorder,
						R.id.listsaledelaccid,
						R.id.listsaledelname});

		if(listItemAdapter == null)
			return;
		lvSaleBillInfoOrderList.setAdapter(listItemAdapter);
		lvSaleBillInfoOrderList.setOnItemClickListener(itemListener);

	}

	//ListView的Item点击监听事件
	private ListView.OnItemClickListener itemListener = new
			ListView.OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
										long arg3) {

					Adapter adapter=arg0.getAdapter();
					Map<String,Object> map=(Map<String, Object>) adapter.getItem(arg2);

					SerializableMap ResultMap = new SerializableMap();
					ResultMap.setMap(map);
//					String lsAccID = map.get("AccID").toString();
//					String lsPk_Corp = map.get("pk_corp").toString();



//					if(!Common.CheckUserRole(lsAccID, lsPk_Corp, "40080802"))
//					{
//						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//						Toast.makeText(SaleBillInfoOrderList.this, "没有使用该单据的权限", Toast.LENGTH_LONG).show();
//						return;
//					}


					Intent intent = new Intent();
					intent.putExtra("ResultBillInfo", ResultMap);

					SaleBillInfoOrderList.this.setResult(1, intent);
					SaleBillInfoOrderList.this.finish();

				}

			};

	//button按钮的监听事件
	private Button.OnClickListener ButtonClickListener = new
			Button.OnClickListener()
			{

				@Override
				public void onClick(View v) {
					switch(v.getId())
					{
//				case R.id.btnSaleBillInfoReturn:
//					SaleBillInfoOrderList.this.finish();
//					break;
					}

				}

			};

}
