package com.techscan.dvq;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sample_stocking_OrderList extends Activity {

	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//ADD CAIXY TEST END 
	public List<Map<String, Object>> mData;
	String m_OrderNoLike;
	Button btnSSOReturn =null;
	
	private ListView.OnItemClickListener itemListener = new
			ListView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) 
		{
			// TODO Auto-generated method stub
			 Adapter adapter=arg0.getAdapter();
             @SuppressWarnings("unchecked")
			 Map<String,Object> map=(Map<String, Object>) 
					adapter.getItem(arg2);
             String orderNo = map.get("No").toString();
             String Warehouses = map.get("WarehouseShow").toString();
             String WarehousesCode = map.get("Warehouse").toString();
             String orderID = map.get("ID").toString();
             String SampleCom = map.get("SampleCom").toString();
             
             
             Intent intent = new Intent();      
             intent.putExtra("orderNo", orderNo);// 把返回数据存入Intent  
             intent.putExtra("ID", orderID);
             intent.putExtra("Warehouse", "'" + WarehousesCode + "'");
             intent.putExtra("Warehouses2", Warehouses);
             intent.putExtra("SampleCom", SampleCom);
             
             Sample_stocking_OrderList.this.setResult(1, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理      
             Sample_stocking_OrderList.this.finish();// 关闭子窗口ChildActivity 
             
             //Toast.makeText(PdOrderList.this, errMsg, Toast.LENGTH_SHORT).show();
             
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_stocking_order_list);
		
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		//ADD CAIXY END
		
		this.setTitle("抽样盘点单");
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()  
                .detectNetwork()
                .penaltyLog()  
                .build());  

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
				detectLeakedClosableObjects().penaltyLog().penaltyDeath().build()); 

		JSONObject para = new JSONObject();
		Intent intent = this.getIntent(); 
		String FunctionName="";
		
		btnSSOReturn = (Button)findViewById(R.id.btnSSOReturn);
		btnSSOReturn.setOnClickListener(ButtonOnClickListener);

		
		FunctionName="";		//查询单据函数
		try 
		{
			if(m_OrderNoLike == null)
				para.put("BillCode", "");
			else
				para.put("BillCode", m_OrderNoLike);
			
			para.put("TableName","OrderList");
			para.put("FunctionName", "GetTS_StoktackingyList");
			para.put("UserID", "");
					
		}
		catch (JSONException e2) 
		{
			// TODO Auto-generated catch block
			Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			e2.printStackTrace();
		}	
		JSONObject jas;
		try 
		{
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			jas = MainLogin.objLog.
					DoHttpQuery(para, "CommonQuery", "A");

		} catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END

			return;
		}
		
		try 
		{	
			if(jas==null)
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END

				return;
			}
			if(!jas.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
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
					errMsg = getString(R.string.WangLuoChuXianWenTi);
				}
				Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END

				return;
			}
			
			mData = getData(jas);
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
		
		ListView list = (ListView) findViewById(R.id.dgSampDetail);  				
        SimpleAdapter listItemAdapter = new SimpleAdapter
        		(this,mData,//数据源   
                R.layout.listssorder,//ListItem的XML实现  
                //动态数组与ImageItem对应的子项          
                new String[] {"No","WarehouseShow","createdate","user_name"},   
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] {R.id.listssorder,R.id.listsswhcode,R.id.listssDate,R.id.listssName}  
            );       
        list.setOnItemClickListener((OnItemClickListener) itemListener);		
        list.setAdapter(listItemAdapter);  	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sample_stocking__order_list, menu);
		return true;
	}

	private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
  			{			//btnSDScanReturn
	  			case id.btnSSOReturn:
	  				finish();					
					break;
  			}
		}	    	
    };
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private List<Map<String, Object>> getData(JSONObject jas) 
			throws JSONException 
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		
		JSONObject tempJso = null;
		
		if(!jas.has("Status"))
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return null;
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
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END

			return null;
		}
		JSONArray jsarray= jas.getJSONArray("OrderList");
		
		for(int i = 0;i<jsarray.length();i++)
		{
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();
			
			map.put("ID", tempJso.getString("id"));
			map.put("No", tempJso.getString("pd_order"));
			map.put("Warehouse", tempJso.getString("whcode"));
			map.put("WarehouseShow", tempJso.getString("whcode2"));
			map.put("createdate", tempJso.getString("createdate"));
			map.put("user_name", tempJso.getString("user_name"));
			map.put("SampleCom", tempJso.getString("pk_corp"));
			list.add(map);
		}
		return list;
	}
}
