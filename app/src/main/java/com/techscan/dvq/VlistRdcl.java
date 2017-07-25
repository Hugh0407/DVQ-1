package com.techscan.dvq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class VlistRdcl extends Activity {
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//ADD CAIXY TEST END
	Button btRdclReturn =null;

	private List<Map<String, Object>> getData(JSONObject jasA ,JSONObject jasB ,String AccID) 
			throws JSONException 
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		
		JSONObject tempJsoA = null;
		JSONObject tempJsoB = null;

		JSONArray jsarrayA= jasA.getJSONArray("dbHead");
		JSONArray jsarrayB= jasB.getJSONArray("dbHead");
		
		for(int i = 0;i<jsarrayA.length();i++)
		{
			tempJsoA = jsarrayA.getJSONObject(i);

			String CodeA = tempJsoA.getString("rdcode");
			String NameA = tempJsoA.getString("rdname");
//			if(CodeA.equals("201")||CodeA.equals("203")||CodeA.equals("101")||CodeA.equals("103"))
//			{
				for(int x = 0;x<jsarrayB.length();x++)
	    		{
	    			tempJsoB = jsarrayB.getJSONObject(x);
	    			
	    			String CodeB = tempJsoB.getString("rdcode");
	    			String NameB = tempJsoB.getString("rdname");
	    			if(CodeB.equals(CodeA)&&NameB.equals(NameA))
	    			{
	    				map = new HashMap<String, Object>();
	    				map.put("AccID", "A");	
	    				map.put("Code",CodeA );
	    				map.put("Name", NameA);		
	    				map.put("AccID", "A&B");
	    	     		map.put("RdIDA", tempJsoA.getString("pk_rdcl"));
	    				map.put("RdIDB", tempJsoB.getString("pk_rdcl"));
	    				list.add(map);
	    			}
	    		}
//			}
		}
//		for(int x = 0;x<jsarrayB.length();x++)
//		{
//			tempJsoB = jsarrayB.getJSONObject(x);
//
//			String CodeB = tempJsoB.getString("rdcode");
//			String NameB = tempJsoB.getString("rdname");
//			String PutOk = "NG";
//			for(int y = 0;y<list.size();y++)
//			{
//				
//				Map<String, Object>  tmpmap = list.get(y);
//				JSONObject json = Common.MapTOJSONOBject(tmpmap);
//				String Code = ((JSONObject) json).getString("Code").toString();
//				String Name = ((JSONObject) json).getString("Name").toString();
//				
//				if(CodeB.equals(Code)&&NameB.equals(Name))
//				{
//					 PutOk = "OK";
//					 break;
//				}
//			}
//			if(PutOk.equals("NG"))
//			{
//				map = new HashMap<String, Object>();
//				map.put("Code",CodeB );
//				map.put("Name", NameB);
//				map.put("AccID", "B");
//	     		map.put("RdIDA", "");
//				map.put("RdIDB", tempJsoB.getString("pk_rdcl"));
//				list.add(map);
//			}
//
//		}
		return list;
	}

	public List<Map<String, Object>> mData;
	private Handler handler=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vlist_rdcl);
		
		this.setTitle(getString(R.string.ShouFaLeiBie));
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		//ADD CAIXY END
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()  
                .detectNetwork()
                .penaltyLog()  
                .build());  

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
				detectLeakedClosableObjects().penaltyLog().penaltyDeath().build()); 

		
		btRdclReturn = (Button)findViewById(R.id.btRdclReturn);
		btRdclReturn.setOnClickListener(ButtonOnClickListener);
		
		JSONObject para = new JSONObject();
		Intent intent = this.getIntent(); 
		
		String AccID = intent.getStringExtra("AccID");
		String rdflag=intent.getCharSequenceExtra("rdflag").toString();

		
		String FunctionName="";
		FunctionName="CommonQuery";
		
		if(intent.hasExtra("rdcode"))
		{
			//根据Code获取收发类别
			String Code = intent.getStringExtra("rdcode");
			try {
				para.put("rdflag", rdflag);
				para.put("rdcode", Code);
				para.put("FunctionName", "GetRdcl");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			}
		}
		else
		{
			try {
				para.put("rdflag", rdflag);
				para.put("rdcode", "");
				para.put("FunctionName", "GetRdcl");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		
		JSONObject jasA;
		JSONObject jasB;
		try {
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXingHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			jasA = Common.DoHttpQuery(para, FunctionName, "A");
			jasB = Common.DoHttpQuery(para, FunctionName, "B");
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
			if(jasA==null)
			{
				Toast.makeText(this, R.string.WangLuoChuXiangWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			if(!jasA.getBoolean("Status"))
			{
				Toast.makeText(this, jasA.getString("ErrMsg"), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			if(jasB==null)
			{
				Toast.makeText(this, R.string.WangLuoChuXiangWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			if(!jasB.getBoolean("Status"))
			{
				Toast.makeText(this, jasB.getString("ErrMsg"), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			mData = getData(jasA,jasB,AccID);
			
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
		
		ListView list = (ListView) findViewById(R.id.lvRdcl);  
				
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,mData,//数据源   
                R.layout.vlistrd,//ListItem的XML实现  
                //动态数组与ImageItem对应的子项          
                new String[] {"Name","Code","AccID"},   
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] {R.id.tvrdName,R.id.tvrdCode,R.id.tvrdaccid}  
            ); 
        
        //list.addHeaderView()
        
        list.setOnItemClickListener((OnItemClickListener) itemListener);		
        list.setAdapter(listItemAdapter);  			
	}

	
	private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
  			{			//btnSDScanReturn
	  			case id.btRdclReturn:
	  				finish();					
					break;
  			}
		}	    	
    };
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vlist_rdcl, menu);
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
			 Map<String,Object> map=(Map<String, Object>) adapter.getItem(arg2);
             String code = map.get("Code").toString();
             String name=map.get("Name").toString();
             String AccID=map.get("AccID").toString();
             String RdIDA=map.get("RdIDA").toString();
             String RdIDB=map.get("RdIDB").toString();
             
         	//String OrgId=map.get("OrgId").toString();
//         	String companyID=map.get("companyID").toString();
//         	String vCode=map.get("No").toString();
//         	String warehouseID=map.get("warehouseID").toString();
             
             Intent intent = new Intent();      
             intent.putExtra("Name", name);// 把返回数据存入Intent  
             intent.putExtra("Code", code);
             intent.putExtra("AccID", AccID);
             intent.putExtra("RdIDA", RdIDA);
             intent.putExtra("RdIDB", RdIDB);
             //intent.putExtra("OrgId", OrgId);
//             intent.putExtra("companyID", companyID);
//             intent.putExtra("vcode", vCode);
//             intent.putExtra("warehouseID", warehouseID);
             
             VlistRdcl.this.setResult(2, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理      
             VlistRdcl.this.finish();// 关闭子窗口ChildActivity             
     
		}
	};
}
