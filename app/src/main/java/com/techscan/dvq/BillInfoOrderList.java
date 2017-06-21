package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillInfoOrderList extends Activity {

	private Button btnBillInfoReturn;
	private ListView lvBillInfoOrderList;
	private String fsFunctionName = "";


	//ADD CAIXY TEST START
	private SoundPool sp;//明一个SoundPool
	private int music;//定义一个int来设置suondID
	private String WhNameA = "";
	private String WhNameB = "";
	private String corpincode = "";
	private String billcodeKey="";
	//ADD CAIXY TEST END

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_info_order_list);

		//设置title
		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(R.string.orderInfo);
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		actionBar.show();

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

		//add caixy s
		if(myIntent.hasExtra("WhNameA"))
			WhNameA = myIntent.getStringExtra("WhNameA");
		if(myIntent.hasExtra("WhNameB"))
			WhNameB = myIntent.getStringExtra("WhNameB");
		if(myIntent.hasExtra("corpincode"))
			corpincode = myIntent.getStringExtra("corpincode");
		if(myIntent.hasExtra("billcodeKey"))
			billcodeKey = myIntent.getStringExtra("billcodeKey");
		//add caixy e

		//取得控件
		btnBillInfoReturn = (Button)findViewById(R.id.btnBillInfoReturn);
		btnBillInfoReturn.setOnClickListener(ButtonClickListener);
		lvBillInfoOrderList = (ListView)findViewById(R.id.BillInfoOrderList);

		//ADD CAIXY START
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
		music = sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		//ADD CAIXY END

		//取得以及绑定显示订单详细
		GetAndBindingBillInfoDetail();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.bill_info_order_list, menu);
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
			Toast.makeText(this, R.string.MeiYouChaXunDaoDanJuBiaoMing, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			sp.play(music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}

		JSONObject para = new JSONObject();
		try {
			para.put("FunctionName", fsFunctionName);
			//add caixy s
			para.put("WhNameA", WhNameA);
			para.put("WhNameB", WhNameB);
			para.put("corpincode", corpincode);
			para.put("billcodeKey", billcodeKey);
			//add caixy e
			para.put("CorpPK", "");
			para.put("BillCode", "");
			//para.put("Wh-CodeA", MainLogin.objLog.WhCodeA);
			//para.put("Wh-CodeB", MainLogin.objLog.WhCodeB);


		} catch (JSONException e2) {
			return;
		}
		try {
			para.put("TableName",  "dbHead");
		} catch (JSONException e2) {
			return;
		}

		JSONObject jas;
		try {
			if(!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
				sp.play(music, 1, 1, 0, 0, 1);
				return;
			}
			jas = Common.DoHttpQuery(para, "CommonQuery", "");
		} catch (Exception ex)
		{
			return;
		}

		//把取得的单据信息绑定到ListView上
		try
		{
			if(jas==null)
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				sp.play(music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			if(!jas.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				sp.play(music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
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
					errMsg = getString(R.string.WangLuoChuXianWenTi);
				}
				Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				sp.play(music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			//绑定到ListView
			BindingBillInfoData(jas);
		}
		catch (JSONException e)
		{
			return;
		}
		catch (Exception ex)
		{
			return;
		}
	}

	//绑定到ListView
	private void BindingBillInfoData(JSONObject jsonBillInfo) throws JSONException
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		JSONObject tempJso = null;

		if(jsonBillInfo == null)
			return;
		if(!jsonBillInfo.has("Status"))
			return;
		if(!jsonBillInfo.getBoolean("Status"))
		{
			String errMsg = "获取数据时发生错误,请再次尝试";
			if(jsonBillInfo.has("Status"))
			{
				errMsg = jsonBillInfo.getString("ErrMsg");
			}

			Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			sp.play(music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			list = null;
		}
		if(!jsonBillInfo.has("dbHead"))
			return;
		JSONArray jsarray= jsonBillInfo.getJSONArray("dbHead");

		for(int i = 0;i<jsarray.length();i++)
		{
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();
			if(fsFunctionName.equals("GetAdjustOutBillHead"))//调拨出库单
			{
				map.put("BillCode", tempJso.getString("vbillcode"));//单据号
				map.put("WHOut", tempJso.getString("cwarehousename") + "     ");//调出仓库名
				map.put("WHIn",tempJso.getString("cotherwhname"));//调入仓库名
				//map.put("BillID",tempJso.getString("vbillcode"));
				map.put("AccID", tempJso.getString("AccID"));
				map.put("CorpSOut", tempJso.getString("corpoutshortname") + "     ");//调出公司简称
				map.put("CorpSIn", tempJso.getString("corpinshortname"));//调入公司简称
				map.put("CorpOut", tempJso.getString("corpoutname"));//调出公司名称
				map.put("CorpIn", tempJso.getString("corpinname"));//调入公司名称

				//保存用表头JSONObject设置---开始
				map.put("pk_corp", tempJso.getString("cothercorpid"));//公司ID//caixy
				if(tempJso.getString("AccID").equals("A"))
					map.put("coperatorid", MainLogin.objLog.UserID);//操作者
				else
					map.put("coperatorid", MainLogin.objLog.UserIDB);//操作者

				map.put("pk_calbody", tempJso.getString("cothercalbodyid"));//调入库存组织
				map.put("pk_stordoc", tempJso.getString("cotherwhid"));//调入仓库
				map.put("fallocflag", tempJso.getString("fallocflag"));//调拨类型标志
				map.put("cbiztype", tempJso.getString("cbiztype"));//业务类型ID
				map.put("pk_outstordoc", tempJso.getString("cwarehouseid"));//调出仓库
				map.put("pk_outcalbody", tempJso.getString("pk_calbody"));//调出库存组织
				map.put("pk_outcorp", tempJso.getString("pk_corp"));//调出公司
				map.put("vcode", tempJso.getString("vbillcode"));//单据号
				map.put("cgeneralhid", tempJso.getString("cgeneralhid"));//单据ID
				//cgeneralhid
				//保存用JSONObject设置---结束
			}

			list.add(map);
		}
		SimpleAdapter listItemAdapter = null;
		if(fsFunctionName.equals("GetAdjustOutBillHead"))//调拨出库单
		{
			listItemAdapter = new SimpleAdapter(this,list,
					R.layout.vlisttransin,
					new String[] {"BillCode","WHOut", "WHIn","AccID","CorpSOut","CorpSIn"},
					new int[] {R.id.listtransinpdorder,
							R.id.listtransinfromware,
							R.id.listtransintoware,
							R.id.listtransinaccid,
							R.id.listtransincorpoutname,
							R.id.listtransincorpinname});
		}

		if(listItemAdapter == null)
			return;
		lvBillInfoOrderList.setAdapter(listItemAdapter);
		lvBillInfoOrderList.setOnItemClickListener(itemListener);

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

					Intent intent = new Intent();
					intent.putExtra("ResultBillInfo", ResultMap);

					BillInfoOrderList.this.setResult(1, intent);
					BillInfoOrderList.this.finish();

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
						case R.id.btnBillInfoReturn:
							BillInfoOrderList.this.finish();
							break;
					}

				}

			};

}
