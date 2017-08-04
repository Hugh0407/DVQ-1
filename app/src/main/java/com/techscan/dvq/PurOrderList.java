package com.techscan.dvq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.techscan.dvq.common.Common;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurOrderList extends Activity {

	@Nullable
	public List<Map<String, Object>> mData;
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//ADD CAIXY TEST END 
	@Nullable
	Button btPurOrderListReturn =null;

	@Nullable
	private Handler handler =null;
	private List<Map<String, Object>> getData(@NonNull JSONObject jas) throws JSONException
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
		JSONArray jsarray= jas.getJSONArray("PurGood");
		for(int i = 0;i<jsarray.length();i++) {
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();
			map.put("busicode",tempJso.get("busicode"));
			map.put("billcode",tempJso.get("billcode"));
			map.put("corderid",tempJso.get("corderid"));
			map.put("businame",tempJso.get("businame"));
			map.put("purorgname",tempJso.get("purorgname"));
			map.put("dorderdate",tempJso.get("dorderdate"));
			list.add(map);
		}
		return list;
//			String instorname = tempJso.getString("outstorname");
//			if (instorname.equals("null"))
//			{
//				instorname = " ";
//			}
//
//			map = new HashMap<String, Object>();
//
//			String freplenishflag = tempJso.getString("freplenishflag");
//
//			if(freplenishflag.equals("Y"))//判断是否为退货
//			{
//				map.put("billstatus", "N");//红字采购
//				map.put("billstatusE", "退货");//红字采购
//			}
//			else
//			{
//				map.put("billstatus", "Y");//蓝字采购
//				map.put("billstatusE", " ");//蓝字采购
//			}
//
//			map.put("No", tempJso.getString("vbillcode"));
//			map.put("Vendor", tempJso.getString("custname") + "     ");
//			map.put("Warehouse",instorname);
//			map.put("BillId",tempJso.getString("cgeneralhid"));
//			map.put("WarehouseID", tempJso.getString("cwarehouseid"));
//			map.put("pk_purcorp", tempJso.getString("pk_purcorp"));
//			map.put("pk_calbody", tempJso.getString("pk_calbody"));
//
//
//			if(tempJso.has("accid"))
//			{
//				map.put("AccID", tempJso.getString("accid"));
//			}
//			else
//			{
//				map.put("AccID", tempJso.getString("AccID"));
//			}
//			list.add(map);
//		}
//		return list;
	}

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setTitle("采购订单列表");
		setContentView(R.layout.activity_pur_order_list);
		ListView listView  = (ListView)findViewById(id.purlist);
//		listView.setDivider(new ColorDrawable(Color.GRAY));
//		listView.setDividerHeight(1);

		JSONObject para = new JSONObject();
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		//ADD CAIXY END
		btPurOrderListReturn = (Button)findViewById(R.id.btPurOrderListReturn);
		btPurOrderListReturn.setOnClickListener(ButtonOnClickListener);
		
		String FunctionName="";
		
		String BillHead="";
		
		BillHead = this.getIntent().getStringExtra("BillCode");

		String sStartDate = this.getIntent().getStringExtra("StartDate");
		String sEndDate = this.getIntent().getStringExtra("EndDate");
		
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()  
                .detectNetwork()
                .penaltyLog()  
                .build());  

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
				detectLeakedClosableObjects().penaltyLog().penaltyDeath().build()); 
		
		

		FunctionName="CommonQuery";
		try 
		{
			para.put("CompanyCode", MainLogin.objLog.CompanyCode);
			para.put("BillCode", BillHead);
			para.put("OrgId", MainLogin.objLog.STOrgCode);
			para.put("sDate", sStartDate);
			para.put("sEndDate", sEndDate);
			para.put("FunctionName","GetPOList");
			para.put("TableName", "PurGood");
			
		} 
		catch (JSONException e2) 
		{
				e2.printStackTrace();
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
		        if(!BillHead.equals(""))
		        {
		        	this.finish();
		        }
				Common.cancelLoading();
				return;
		}
		JSONObject jas;
		try 
		{
//	        if(!MainLogin.getwifiinfo()) {
//	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
//	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//	            if(!BillHead.equals(""))
//	            {
//	            	this.finish();
//	            }
//				Common.cancelLoading();
//	            return ;
//	        }
			jas = Common.DoHttpQuery(para, FunctionName, "");
			Log.d("TAG", "GetPOList: " + jas);
		} catch (Exception ex)
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
	        if(!BillHead.equals(""))
	        {
	        	this.finish();
	        }
			Common.cancelLoading();
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
		        if(!BillHead.equals(""))
		        {
		        	this.finish();
		        }
				Common.cancelLoading();
				return;
			}
			
			if(!jas.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		        if(!BillHead.equals(""))
		        {
		        	this.finish();
		        }
				Common.cancelLoading();
				return;
			}
			
			if(!jas.getBoolean("Status"))
			{
				Toast.makeText(this, jas.getString("ErrMsg"), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
		        if(!BillHead.equals(""))
		        {
		        	this.finish();
		        }
				Common.cancelLoading();
				return;
			}
			
			mData = getData(jas);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
	        if(!BillHead.equals(""))
	        {
	        	this.finish();
	        }
			Common.cancelLoading();
			return;
		}
		catch (Exception ex)
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
	        if(!BillHead.equals(""))
	        {
	        	this.finish();
	        }
			Common.cancelLoading();
			return;
		}
		
		ListView list = (ListView) findViewById(R.id.purlist);  
//		
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,mData,//数据源   
                R.layout.vlistpurs,//ListItem的XML实现  
                //动态数组与ImageItem对应的子项          
                new String[] {"billcode","dorderdate", "businame","purorgname","corderid"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] {R.id.listorder,R.id.listorderdate,R.id.listbusiname,R.id.listpurorgname, R.id.listcorderid}
            ); 
                
        list.setOnItemClickListener(itemListener);
        list.setAdapter(listItemAdapter);  	
        
        if(!BillHead.equals(""))
        {
        	
        	Map<String,Object> map= mData.get(0);
            
            String orderNo = map.get("billcode").toString();
            String billId = map.get("corderid").toString();
//            String vendor = map.get("Vendor").toString();
//            String warehouse = map.get("Warehouse").toString();
//            String warehouseID = map.get("WarehouseID").toString();
//            String pk_purcorp =  map.get("pk_purcorp").toString();
//            String pk_calbody =  map.get("pk_calbody").toString();

//            String billstatus =  map.get("billstatus").toString();
            
//del walter todo 20170616 暂时删除 ----->>>>>
//			if(!Common.CheckUserRole("A", "1001", "40080602"))
//	   		{
//	   			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//	   			Toast.makeText(PurOrderList.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG).show();
//	   			return;
//	   		}
//
//
//	   		if(!Common.CheckUserWHRole("A", "1001", warehouseID))
//	   		{
//	   		 	MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//	   		 	Toast.makeText(PurOrderList.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian, Toast.LENGTH_LONG).show();
//	   		 	return;
//	   		}
//del walter todo 20170616 暂时删除 <<<<<-----
            
            
            
            Intent intent = new Intent();      
            intent.putExtra("BillCode", orderNo);// 把返回数据存入Intent
            intent.putExtra("BillId", billId);
//            intent.putExtra("Vendor", vendor);
//            intent.putExtra("Warehouse", warehouse.toString());
//            intent.putExtra("WarehouseID", warehouseID);
//            intent.putExtra("AccID", "A");
//            intent.putExtra("pk_purcorp", pk_purcorp);
//            intent.putExtra("pk_calbody", pk_calbody);
//            intent.putExtra("billstatus", billstatus);
            
            PurOrderList.this.setResult(1,intent);
            PurOrderList.this.finish();
        	
        	this.finish();
        }
		Common.cancelLoading();
	}



	@NonNull
	private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(@NonNull View v)
		{
			switch(v.getId())
  			{			//btnSDScanReturn
	  			case id.btPurOrderListReturn:
	  				finish();					
					break;
  			}
		}	    	
    };


	@NonNull
	private ListView.OnItemClickListener itemListener = new
			ListView.OnItemClickListener()
	{
		@Override
		public void onItemClick(@NonNull AdapterView<?> arg0, View arg1, int arg2,
								long arg3)
		{
			Adapter adapter=arg0.getAdapter();
            // @SuppressWarnings("unchecked")
			Map<String,Object> map=(Map<String, Object>) adapter.getItem(arg2);
             
             String orderNo = map.get("billcode").toString();
             String billId=map.get("corderid").toString();
//             String vendor = map.get("Vendor").toString();
//             String warehouse = map.get("Warehouse").toString();
//             String warehouseID = map.get("WarehouseID").toString();
//             String pk_purcorp =  map.get("pk_purcorp").toString();
//             String pk_calbody =  map.get("pk_calbody").toString();
//
//             String billstatus =  map.get("billstatus").toString();

//del walter todo 20170616 暂时删除 ----->>>>>
//     		if(!Common.CheckUserRole("A", "1001", "40080602"))
//    		{
//    			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//    			Toast.makeText(PurOrderList.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG).show();
//    			return;
//    		}
//
//
//    		if(!Common.CheckUserWHRole("A", "1001", warehouseID))
//    		{
//    		 	MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//    		 	Toast.makeText(PurOrderList.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian, Toast.LENGTH_LONG).show();
//    		 	return;
//    		}
//del walter todo 20170616 暂时删除 <<<<<-----
             
             
             
             Intent intent = new Intent();      
             intent.putExtra("BillCode", orderNo);// 把返回数据存入Intent
             intent.putExtra("BillId", billId);
//             intent.putExtra("Vendor", vendor);
//             intent.putExtra("Warehouse", warehouse.toString());
//             intent.putExtra("WarehouseID", warehouseID);
//             intent.putExtra("AccID", "A");
//             intent.putExtra("pk_purcorp", pk_purcorp);
//             intent.putExtra("pk_calbody", pk_calbody);
//
//             intent.putExtra("billstatus", billstatus);
             
             PurOrderList.this.setResult(1,intent);
             PurOrderList.this.finish();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.pur_order_list, menu);
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
