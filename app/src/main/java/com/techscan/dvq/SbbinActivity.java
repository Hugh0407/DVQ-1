package com.techscan.dvq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.WarehouseMultilist.MyAdapter;

public class SbbinActivity extends Activity {
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//ADD CAIXY TEST END 

	EditText etWhCode=null;
	EditText etBinCode=null;
	ListView lvListView=null;
	Button btSearch=null;
	Button btnsbbinReturn = null;
public final static String PREFERENCE_SETTING = "Setting";

	ImageButton btnWhcode=null;    //仓库选择按钮
    String fsAccIDFlag = "";
    private MyAdapter adapter;
	//ADD BY WUQIONG 2015/04/15
	//ADD BY WUQIONG 2015/04/14
	int searchCount=0;
	TextView tvCount=null;
	//ADD BY WUQIONG 2015/04/14
	//ADD BY WUQIONG
	String sWhCode = "";
	String sWhName = "";
	//String CompanyCode = ""; 
	//String OrgCode = ""; 
	//String companyCode="";
	//String CompanyId ="";
	//String OrgId="";
	String WhNameA = "";
	String WhNameB = "";
	String sWhCode2 = "";
	//ADD BY WUQIONG
	
	ArrayList IDList = null;
    //仓库号
    String whhouseCode = "";
	SimpleAdapter listItemAdapter =null;
	ArrayList<HashMap<String,String>> array=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sbbin);
		
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		//ADD CAIXY END
		
		etWhCode=(EditText)findViewById(R.id.etsbbinWhcode);
		etBinCode=(EditText)findViewById(R.id.etsbbinBinCode);
		etBinCode.setOnKeyListener(myTxtListener);
		btSearch=(Button)findViewById(R.id.btsbbinSearch);
		lvListView=(ListView)findViewById(R.id.lvsbbinListView);
		btSearch.setOnClickListener(myClickListten);
		//ADD BY WUQIONG
		tvCount=(TextView)findViewById(R.id.tvCount);
		tvCount.setText("");
		etBinCode.setSelectAllOnFocus(true);
		//ADD BY WUQIONG 2015/04/15
		btnWhcode = (ImageButton)findViewById(R.id.btnWhcode);
		btnWhcode.setOnClickListener(myClickListten);
		etWhCode.setFocusable(false);
		etWhCode.setFocusableInTouchMode(false);
		etBinCode.requestFocus();
		//ADD BY WUQIONG 2015/04/15
		lvListView.setOnItemClickListener(myListItemListener);      
		
		

		
		SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_SETTING, 
				Activity.MODE_PRIVATE); 
				
				WhNameA =sharedPreferences.getString("WhCode", ""); 
				WhNameB =sharedPreferences.getString("AccId", ""); 
//				WhNameA = MainLogin.objLog.WhCodeA;
//				WhNameB = MainLogin.objLog.WhCodeB;
		btnsbbinReturn = (Button)findViewById(R.id.btnsbbinReturn);
		btnsbbinReturn .setOnClickListener(myClickListten);
				
		btSearch.setFocusable(false);
		btnsbbinReturn.setFocusable(false);
				
		//ADD BY WUQIONG
	}

	
	
	private OnItemClickListener myListItemListener = 
    		new OnItemClickListener()
    {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Map<String,Object> mapCurrent = (Map<String,Object>)lvListView.getAdapter().getItem(arg2);
			String lsKey = mapCurrent.get("invcode").toString();
			
			try {
				GetInvImg(lsKey);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				e.printStackTrace();
			}
			
		}
    	
    };
    

private void GetInvImg(String InvCode) throws JSONException
{
	
	JSONObject para = new JSONObject();
	para.put("FunctionName", "GetInvImg");
	para.put("InvCode", InvCode);
	

	

	if(!MainLogin.getwifiinfo()) {
        Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        return ;
    }
	JSONObject rev = null;
	try {
		
		rev = Common.DoHttpQuery(para, "CommonQuery", "A");
		
	} catch (ParseException e) {

		Toast.makeText(this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		return;
	} catch (IOException e) {
		
		Toast.makeText(this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
		//ADD CAIXY TEST START
		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		//ADD CAIXY TEST END
		return;
	}		
	
	if(rev==null)
	{
		Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		return ;
	}
	
	
	if(rev.getBoolean("Status"))
	{
		
		String ErrMsg = rev.getString("ErrMsg");
		
		if(ErrMsg.equals("Ok"))
		{
	    	Intent ShowImg =new Intent(this,LoadImg.class);
	    	ShowImg.putExtra("InvCode",InvCode);
	    	startActivity(ShowImg);
			return;
		}
		else if(ErrMsg.equals("Err"))
		{
			Toast.makeText(this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		else if(ErrMsg.equals("NoImg"))
		{
			Toast.makeText(this,"该存货暂无图片", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}			
	}
	else
	{
		Toast.makeText(this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
		//ADD CAIXY TEST START
		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		//ADD CAIXY TEST END
		return;
	}
}
	
	
	private OnClickListener myClickListten =new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
	//ADD BY WUQIONG 2015/04/15
			case R.id.btnWhcode:
			{				
				try {
					btnWhcodeClick();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
				}
				catch(Exception  e)
				{e.printStackTrace();}
				break;
			}
			//ADD BY WUQIONG 2015/04/15
				case id.btsbbinSearch:
					
					if(!etWhCode.getText().toString().equals("")&&!etBinCode.getText().toString().equals("")) 
					{
						try {
							//MOD BY WUQIONG
							//GetStockInfo(etWhCode.getText().toString(),etBinCode.getText().toString());
							//etWhCode.setText("");
							//etBinCode.setText("");
							tvCount.setText("");
							//sWhCode = "'"+etWhCode.getText().toString()+"'";
							GetStockInfo(sWhCode,etBinCode.getText().toString());
							//MOD BY WUQIONG
							etBinCode.requestFocus();
							//ADD BY WUQIONG 2015/04/14
							GetStockInfoSum(sWhCode,etBinCode.getText().toString());
							//ADD BY WUQIONG 2015/04/14

						} catch (ParseException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (JSONException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (IOException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
					}
					//ADD BY WUQIONG
					else if(etWhCode.getText().toString().equals("")&&!etBinCode.getText().toString().equals("")) 
					{
						try {
							tvCount.setText("");
							//GetBaseWhCd(CompanyId,OrgId,sWhName);
							if (WhNameA.equals("")||WhNameB.equals(""))
							{
								Toast.makeText(SbbinActivity.this,"基础设置中的仓库未设置", Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								return;
							}
							else
							{
								sWhCode = "";
								sWhCode2 = "";
								sWhName = "";
								if(WhNameA.equals(WhNameB))
								{

									//sWhName = WhNameA;
									//GetBaseWhCd(CompanyId,OrgId,WhNameA);
									GetBaseWhCdByName(WhNameA);

								}
								else
								{

									//GetBaseWhCd(CompanyId,OrgId,WhNameA);
									//GetBaseWhCd(CompanyId,OrgId,WhNameB);
									
									GetBaseWhCdByName(WhNameA);
									GetBaseWhCdByName(WhNameB);
								}
							}
							
							//etWhCode.setText(sWhCode2);
							GetStockInfo(sWhCode,etBinCode.getText().toString());
							etBinCode.requestFocus();
							//ADD BY WUQIONG 2015/04/14
							GetStockInfoSum(sWhCode,etBinCode.getText().toString());
							//tvCount.setText("合计查询件数："+searchCount+"件");

							//ADD BY WUQIONG 2015/04/14
							
							return;
						} catch (ParseException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (JSONException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (IOException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
					}
					//ADD BY WUQIONG
					else
					{
						Toast.makeText(SbbinActivity.this,R.string.QingShuRuHuoWeiHao, Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						
						tvCount.setText("");
						if(array!=null)
						{array.removeAll(array);}
						if(listItemAdapter!=null)
						{
						listItemAdapter.notifyDataSetChanged();
						lvListView.setAdapter(listItemAdapter);
						}
						return;
					}
					 
					break;
				case id.btnsbbinReturn:

					finish();					
					break;
			}
		}
	};
	
//ADD BY WUQIONG 2015/04/15	
	//获得所有的仓库信息
	private void btnWhcodeClick() throws ParseException, IOException, JSONException
	{
		List<Map<String, Object>> selectedlist = null;		
		WarehouseMultilist cWarehouseMultilist = new WarehouseMultilist();		
//		cWarehouseMultilist.get();
//		cWarehouseMultilist.setInit(lvListView.getAdapter());
//		cWarehouseMultilist.setIntent(getIntent());

		Intent ViewGrid = new Intent(this,cWarehouseMultilist.getClass());
//		ViewGrid.putExtra("AccIDFlag", fsAccIDFlag);

//		ViewGrid.putExtra("storecode", sWhCode2);
//		etWhCode.setText(sWhCode2.toString());
//		this.getIntent().getStringExtra("resultinfo");
		ViewGrid.getExtras();
		
		//IDList
		//ViewGrid.p
		
		ViewGrid.putExtra("slocalid", IDList);
//		
		
		startActivityForResult(ViewGrid,98);
		
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	{     
		if(requestCode==98)
		{
	    switch (resultCode) {          
	        case 1:         // 这是列表返回的地方 
	        if (data != null) 
	        {             
	            Bundle bundle = data.getExtras(); 
	            IDList = data.getStringArrayListExtra("slocalid");
                
	            try 
            	{
	            	
	            	SerializableList serializableList = (SerializableList) bundle.get("resultinfo");
	            	List<Map<String, Object>> resultList = serializableList.getList();
//	            	lvListView.setAdapter(null);

	            	
//	            	String IDList ="";	
	            	sWhCode="";
	            	sWhCode2="";
	            	
	            	for(int i=0;i<resultList.size();i++)
	    			{
	    	
	    				
	    				//IDList = IDList + (String)((resultList).get(i)).get("storcode");
//	    				sWhCode = sWhCode + "'"+(String)((resultList).get(i)).get("storcode")+"'";
//						sWhCode2 = sWhCode2 + "," + (String)((resultList).get(i)).get("storcode");
						
						if (sWhCode.equals(""))
						{
							sWhCode = sWhCode + "'"+(String)((resultList).get(i)).get("storcode")+"'";
							sWhCode2 = sWhCode2 + (String)((resultList).get(i)).get("storcode");
						}
						else 
						{
							sWhCode = sWhCode + ",'"+(String)((resultList).get(i)).get("storcode")+"'";
							sWhCode2 = sWhCode2 + ","+(String)((resultList).get(i)).get("storcode");
						}
						
	    			}
	            	etWhCode.setText(sWhCode2);
	            	
//	            	if(!etBinCode.getText().toString().equals(""))
//					{
//						GetStockInfo(sWhCode,etBinCode.getText().toString());
//						GetStockInfoSum(sWhCode,etBinCode.getText().toString());
//					}
					if(!etWhCode.getText().toString().equals("")&&!etBinCode.getText().toString().equals("")) 
					{
						try {
							//MOD BY WUQIONG
							//GetStockInfo(etWhCode.getText().toString(),etBinCode.getText().toString());
							//etWhCode.setText("");
							//etBinCode.setText("");
							tvCount.setText("");
							//sWhCode = "'"+etWhCode.getText().toString()+"'";
							GetStockInfo(sWhCode,etBinCode.getText().toString());
							//MOD BY WUQIONG
//							etBinCode.requestFocus();
							//ADD BY WUQIONG 2015/04/14
							GetStockInfoSum(sWhCode,etBinCode.getText().toString());
							//ADD BY WUQIONG 2015/04/14

						} catch (ParseException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END							
							return;
						} catch (JSONException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (IOException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
					}
					//ADD BY WUQIONG
					else if(etWhCode.getText().toString().equals("")&&!etBinCode.getText().toString().equals("")) 
					{
						try {
							tvCount.setText("");
							//GetBaseWhCd(CompanyId,OrgId,sWhName);
							if (WhNameA.equals("")||WhNameB.equals(""))
							{
								Toast.makeText(SbbinActivity.this,"基础设置中的仓库未设置", Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								return;
							}
							else
							{
								sWhCode = "";
								sWhCode2 = "";
								sWhName = "";
								if(WhNameA.equals(WhNameB))
								{

									//sWhName = WhNameA;
									//GetBaseWhCd(CompanyId,OrgId,WhNameA);
									GetBaseWhCdByName(WhNameA);

								}
								else
								{

									//GetBaseWhCd(CompanyId,OrgId,WhNameA);
									//GetBaseWhCd(CompanyId,OrgId,WhNameB);
									
									GetBaseWhCdByName(WhNameA);
									GetBaseWhCdByName(WhNameB);
								}
							}
							
							//etWhCode.setText(sWhCode2);
							GetStockInfo(sWhCode,etBinCode.getText().toString());
//							etBinCode.requestFocus();
							//ADD BY WUQIONG 2015/04/14
							GetStockInfoSum(sWhCode,etBinCode.getText().toString());
							//tvCount.setText("合计查询件数："+searchCount+"件");

							//ADD BY WUQIONG 2015/04/14
							
							return;
						} catch (ParseException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (JSONException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (IOException e) {
							Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
					}
//					etBinCode.requestFocus();
            	
				} catch (Exception e) {
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return;
				} 
	        }         
	        break;
	        default:              
	            //其它窗口的回传数据           
	        	//IniActivyMemor();
	        break;          
	        }  
		}		

	        super.onActivityResult(requestCode, resultCode, data);  
	        System.gc();
	}   
	
	
	//获得所有的仓库信息
//	private void btnWhcodeClick() throws ParseException, IOException, JSONException
//	{
//		try
//		{
////		String lgUser = MainLogin.objLog.LoginUser;
////		String lgPwd = MainLogin.objLog.Password;		
////		String LoginString =  MainLogin.objLog.LoginString;	
//		
//		JSONObject para = new JSONObject();
//		
//		para.put("FunctionName", "GetWareHouseList");
//		para.put("CompanyCode",  MainLogin.objLog.CompanyCode);
//		para.put("STOrgCode",  MainLogin.objLog.STOrgCode);
//		para.put("TableName",  "warehouse");
//		
//				
//			JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");		
//			if(rev == null)
//			{
//				//网络通讯错误
//				Toast.makeText(this,"错误！网络通讯错误", Toast.LENGTH_LONG).show();
//				return;
//			}
//			if(rev.getBoolean("Status"))
//			{
//				JSONArray val = rev.getJSONArray("warehouse");
//				
//				JSONObject temp = new JSONObject();
//				temp.put("warehouse", val);
//				
//				Intent ViewGrid = new Intent(this,WarehouseMultilist.class);
//				ViewGrid.putExtra("myData",temp.toString());
//
//				startActivityForResult(ViewGrid,97);		
//			}
//			else
//			{
//				String Errmsg = rev.getString("ErrMsg");
//				Toast.makeText(this,Errmsg, Toast.LENGTH_LONG).show();
//				
//			}
//			
//		}
//		catch(Exception e)
//		{
//			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//		}
//		
//	}
//ADD BY WUQIONG 2015/04/15
	
//    public boolean getwifiinfo() 
//    {
//		   //WifiManager wifi_service = (WifiManager)getSystemService("wifi");
//		   
//		   WifiManager wifi_service = (WifiManager)getSystemService(WIFI_SERVICE);
//		   //WifiInfo wifiInfo = wifi_service.getConnectionInfo();
//		   
//		   SharedPreferences mySharedPreferences = getSharedPreferences(
//					SettingActivity.PREFERENCE_SETTING, Activity.MODE_PRIVATE);
//		   
//		   String sWIFIMin = mySharedPreferences.getString("WIFIMin", "");
//		   String sWIFIMax = mySharedPreferences.getString("WIFIMax", "");
//	       
//        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
//        
//        int iWIFIMin = Integer.valueOf(sWIFIMin).intValue();
//        int iWIFIMax = Integer.valueOf(sWIFIMax).intValue();
//        int Rssi = wifiInfo.getRssi() * -1;
//        if((iWIFIMin < Rssi) && (Rssi < iWIFIMax)) {
//            return true;
//        }
//        return false;
//    }
	
	 private boolean GetStockInfo(String WhCode,String BinCode) throws JSONException, ParseException, IOException
	    {
		 if(array!=null)
			{array.removeAll(array);}
			if(listItemAdapter!=null)
			{
			listItemAdapter.notifyDataSetChanged();
			lvListView.setAdapter(listItemAdapter);
			}
	    	JSONObject serList = null;
			JSONObject para = new JSONObject();
			
			para.put("FunctionName", "SearchGetStockBinInfo");
			para.put("TableName","InvInfo");
			para.put("WhCode", WhCode.toUpperCase().replace("\n", ""));
			para.put("BinCode", BinCode.toUpperCase().replace("\n", ""));
			para.put("CompanyCode", MainLogin.objLog.CompanyCode);
			
			
	        if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return false;
	        }
			
	        
			serList = Common.DoHttpQuery(para, "CommonQuery", "");
			if(serList==null)
			{
//				Toast.makeText(this, serList.getString("获取物料过程中发生了错误"), 
//						Toast.LENGTH_LONG).show();
				
				Toast.makeText(SbbinActivity.this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			if(!serList.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return false;
			}
			
			if(!serList.getBoolean("Status"))
			{
//				Toast.makeText(this, serList.getString("找不到相关物料的信息"), 
//						Toast.LENGTH_LONG).show();
				Toast.makeText(SbbinActivity.this,"找不到相关物料的信息", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			JSONArray arys = serList.getJSONArray("InvInfo");
			
			array=new ArrayList<HashMap<String,String>>();
			for(int i=0;i< arys.length();i++)
			{
				//vlotb, a.nnum,invb.invcode,invb.invname,carg.cscode,carg.csname,stor.storcode,stor.storname,corp.unitcode,corp.unitname
				
				HashMap<String,String> map =new HashMap<String,String>();
				map.put("invname", arys.getJSONObject(i).get("invname").toString());
				map.put("invcode", arys.getJSONObject(i).get("invcode").toString());
				//map.put("cscode", arys.getJSONObject(i).get("cscode").toString());
				map.put("csname", arys.getJSONObject(i).get("cscode").toString());
//				map.put("storcode", arys.getJSONObject(i).get("storcode").toString());
				map.put("storname", arys.getJSONObject(i).get("storname").toString());
//				map.put("unitcode", arys.getJSONObject(i).get("unitcode").toString());
//				map.put("unitname", arys.getJSONObject(i).get("unitname").toString());
				map.put("vlotb", arys.getJSONObject(i).get("vlotb").toString());
				map.put("nnum", arys.getJSONObject(i).get("nnum").toString());
				array.add(map);
			}
			 listItemAdapter = new SimpleAdapter(this,array,//数据源   
					com.techscan.dvq.R.layout.searchforinv,//ListItem的XML实现  
	                //动态数组与ImageItem对应的子项          
	                new String[] {"invcode","invname","vlotb","csname","nnum"},   
	                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
	                new int[] {com.techscan.dvq.R.id.tvsearchInvcorpcode,com.techscan.dvq.R.id.tvsearchInvUnitName,com.techscan.dvq.R.id.tvsearchunitcode,com.techscan.dvq.R.id.tvSearchInvcsCode,com.techscan.dvq.R.id.tvsearchInvCode}  
	            ); 
			ListView lv=(ListView)findViewById(R.id.lvsbbinListView);
			lv.setAdapter(listItemAdapter);
			
			return true;			
			
	    }
	//ADD BY WUQIONG 2015/04/14 
	 private boolean GetStockInfoSum(String WhCode,String BinCode) throws JSONException, ParseException, IOException
	    {
//		 if(array!=null)
//			{array.removeAll(array);}
//			if(listItemAdapter!=null)
//			{
//			listItemAdapter.notifyDataSetChanged();
//			lvListView.setAdapter(listItemAdapter);
//			}
	    	JSONObject serList = null;
			JSONObject para = new JSONObject();
			tvCount.setText("");
			
			para.put("FunctionName", "SearchGetStockBinInfoSum");
			para.put("TableName","InvInfoSum");
			para.put("WhCode", WhCode.toUpperCase().replace("\n", ""));
			para.put("BinCode", BinCode.toUpperCase().replace("\n", ""));
			para.put("CompanyCode", MainLogin.objLog.CompanyCode);
	        if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return false;
	        }
			serList = Common.
					DoHttpQuery(para, "CommonQuery", "");
			if(serList==null)
			{
//				Toast.makeText(this, serList.getString("获取物料过程中发生了错误"), 
//						Toast.LENGTH_LONG).show();
				Toast.makeText(SbbinActivity.this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			if(!serList.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return false;
			}


			if(!serList.getBoolean("Status"))
			{
//				Toast.makeText(this, serList.getString("找不到相关物料的信息"), 
//						Toast.LENGTH_LONG).show();
				Toast.makeText(SbbinActivity.this,"找不到相关物料的信息", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			searchCount = 0;
			//ADD CAIXY START 15/04/15
			String sCount = "";
			//ADD CAIXY END   15/04/15
			JSONArray arys = serList.getJSONArray("InvInfoSum");
			for(int i=0;i< arys.length();i++)
			{
				//MOD CAIXY START 15/04/15
				//searchCount = searchCount + (((Integer)arys.getJSONObject(i).get("sum(nnum)")).intValue());
				int Count = 0;
				sCount = arys.getJSONObject(i).get("sum(b.nnum)").toString();
				if (!sCount.equals("null"))
				{
					
					Count = ((Integer) arys.getJSONObject(i).get("sum(b.nnum)")).intValue();
				}
								
				searchCount = searchCount + Count;
				//MOD CAIXY END   15/04/15
			}
			tvCount.setText("合计查询件数："+searchCount+"件");
			
			return true;			
			
	    }
	 
	//ADD BY WUQIONG 2015/04/14
	 
	//ADD BY WUQIONG
	 //获取默认登录时的仓库名字做查询
	 
//	 private boolean GetBaseWhCd(String CompanyId,String OrgId,String WhName) throws JSONException, ParseException, IOException
//	 {
//		 if(etWhCode.getText().toString().equals("")&&!etBinCode.getText().toString().equals(""))
//		 {
//			 if(array!=null)
//				{array.removeAll(array);}
//				if(listItemAdapter!=null)
//				{
//				listItemAdapter.notifyDataSetChanged();
//				lvListView.setAdapter(listItemAdapter);
//				}
//		    	JSONObject serList = null;
//				JSONObject para = new JSONObject();
//				
//
//
//				para.put("FunctionName", "GetBaseWhCode");
//				para.put("TableName","WhCode");
//				para.put("CompanyId", CompanyId.toUpperCase().replace("\n", ""));
//				para.put("OrgId", OrgId.toUpperCase().replace("\n", ""));
//				para.put("WhName", WhName.toUpperCase().replace("\n", ""));
//			
//				serList = MainLogin.objLog.
//						DoHttpQuery(para, "CommonQuery", "");
//				if(serList==null)
//				{
//					Toast.makeText(this, serList.getString("获取仓库过程中发生了错误"), 
//							Toast.LENGTH_LONG).show();
//					return false;
//				}
//				JSONArray arys = serList.getJSONArray("WhCode");				
//
//
//		
//				for(int i=0;i< arys.length();i++)
//				{
//					//storcode  仓库编码		storname  仓库名称 
//					if (sWhCode.equals(""))
//					{
//						sWhCode = sWhCode + "'"+arys.getJSONObject(i).get("storcode").toString()+"'";
//						sWhCode2 = sWhCode2 + arys.getJSONObject(i).get("storcode").toString();
//						sWhName = sWhName + arys.getJSONObject(i).get("storname").toString();
//					}
//					else 
//					{
//						sWhCode = sWhCode + ",'"+arys.getJSONObject(i).get("storcode").toString()+"'";
//						sWhCode2 = sWhCode2 + ","+arys.getJSONObject(i).get("storcode").toString();
//						sWhName = sWhName + ","+arys.getJSONObject(i).get("storname").toString();
//					}
//				}
//				return true;
//		 }
//		return true; 
//	 }
	 private boolean GetBaseWhCdByName(String WhName) throws JSONException, ParseException, IOException
	 {

//			 if(array!=null)
//				{array.removeAll(array);}
//				if(listItemAdapter!=null)
//				{
//				listItemAdapter.notifyDataSetChanged();
//				lvListView.setAdapter(listItemAdapter);
//				}
		    	JSONObject serList = null;
				JSONObject para = new JSONObject();
				


				para.put("FunctionName", "GetBaseWhCodeByName");
				para.put("TableName","WhCodeByName");
				para.put("WhName", WhName.toUpperCase().replace("\n", ""));
		        if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return false;
		        }
				serList = Common.
						DoHttpQuery(para, "CommonQuery", "");
				if(serList==null)
				{
//					Toast.makeText(this, serList.getString("获取仓库过程中发生了错误"), 
//							Toast.LENGTH_LONG).show();
					Toast.makeText(SbbinActivity.this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return false;
				}
				JSONArray arys = serList.getJSONArray("WhCodeByName");				


		
				for(int i=0;i< arys.length();i++)
				{
					//storcode  仓库编码		storname  仓库名称 
					if (sWhCode.equals(""))
					{
						sWhCode = sWhCode + "'"+arys.getJSONObject(i).get("storcode").toString()+"'";
						sWhCode2 = sWhCode2 + arys.getJSONObject(i).get("storcode").toString();
						sWhName = sWhName + arys.getJSONObject(i).get("storname").toString();
					}
					else 
					{
						sWhCode = sWhCode + ",'"+arys.getJSONObject(i).get("storcode").toString()+"'";
						sWhCode2 = sWhCode2 + ","+arys.getJSONObject(i).get("storcode").toString();
						sWhName = sWhName + ","+arys.getJSONObject(i).get("storname").toString();
					}
				}
				return true;
		 
		
	 }
	//ADD BY WUQIONG
	 
	 //ADD BY WUQIONG
	 //获取公司信息
	 
//	 private boolean GetComanyInfo(String CompanyCode) throws JSONException, ParseException, IOException
//	 {
//		 if(array!=null)
//			{array.removeAll(array);}
////			if(listItemAdapter!=null)
////			{
////			listItemAdapter.notifyDataSetChanged();
////			lvListView.setAdapter(listItemAdapter);
////			}
//	    	JSONObject serList = null;
//			JSONObject para = new JSONObject();
//
//
//			para.put("FunctionName","GetComanyInfo");
//			para.put("TableName","ComanyInfo");
//			para.put("CompanyCode", CompanyCode.toUpperCase().replace("\n", ""));
//
//		
//			serList = MainLogin.objLog.
//					DoHttpQuery(para, "CommonQuery", "");
//			if(serList==null)
//			{
//				Toast.makeText(this, serList.getString("获取公司信息过程中发生了错误"), 
//						Toast.LENGTH_LONG).show();
//				return false;
//			}
//			JSONArray arys = serList.getJSONArray("ComanyInfo");
//			for(int i=0;i< arys.length();i++)
//			{
//				//pk_corp  公司ID 
//
//				CompanyId = arys.getJSONObject(i).get("pk_corp").toString();
//
//			}
//			return true;
//	 }
//	//ADD BY WUQIONG
//	 //ADD BY WUQIONG
//	 //获取组织信息
//	 
//	 private boolean GetBaseOrgCode (String CompanyId,String OrgCode) throws JSONException, ParseException, IOException
//	 {
//		 if(array!=null)
//			{array.removeAll(array);}
////			if(listItemAdapter!=null)
////			{
////			listItemAdapter.notifyDataSetChanged();
////			lvListView.setAdapter(listItemAdapter);
////			}
//	    	JSONObject serList = null;
//			JSONObject para = new JSONObject();
//			
//
//
//			para.put("FunctionName", "GetBaseOrgCode");
//			para.put("TableName","BaseOrgCode");
//			para.put("CompanyId", CompanyId.toUpperCase().replace("\n", ""));
//			para.put("OrgCode", OrgCode.toUpperCase().replace("\n", ""));
//		
//			serList = MainLogin.objLog.
//					DoHttpQuery(para, "CommonQuery", "");
//			if(serList==null)
//			{
//				Toast.makeText(this, serList.getString("获取仓库过程中发生了错误"), 
//						Toast.LENGTH_LONG).show();
//				return false;
//			}
//			JSONArray arys = serList.getJSONArray("BaseOrgCode");
//			for(int i=0;i< arys.length();i++)
//			{
//				OrgId = arys.getJSONObject(i).get("pk_calbody").toString();
//			}
//			return true;
//	 }
	 //ADD BY WUQIOING
	 private OnKeyListener myTxtListener = new 
	    		OnKeyListener()
	    {
			@Override
			public boolean onKey(View v, int arg1, KeyEvent arg2) {
				{
					switch(v.getId())
					{
						case id.etsbbinBinCode:
							if(arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)
							{
								if(!etWhCode.getText().toString().equals("")&&!etBinCode.getText().toString().equals("")) 
								{
									try {
										//MOD BY WUQIONG
										//GetStockInfo(etWhCode.getText().toString(),etBinCode.getText().toString());
										//etWhCode.setText("");
										//etBinCode.setText("");
										tvCount.setText("");
										sWhCode = "'"+etWhCode.getText().toString()+"'";
										GetStockInfo(sWhCode,etBinCode.getText().toString());	
										etBinCode.requestFocus();									
										//MOD BY WUQIONG 
										//ADD BY WUQIONG 2015/04/14
										GetStockInfoSum(sWhCode,etBinCode.getText().toString());
										//tvCount.setText("合计查询件数："+searchCount+"件");
										//ADD BY WUQIONG 2015/04/14
										return true;
									} catch (ParseException e) {
										Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
										//ADD CAIXY TEST START
										MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
										//ADD CAIXY TEST END
										return false;
									} catch (JSONException e) {
										Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
										//ADD CAIXY TEST START
										MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
										//ADD CAIXY TEST END
										return false;
									} catch (IOException e) {
										Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
										//ADD CAIXY TEST START
										MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
										//ADD CAIXY TEST END
										return false;
									}
								}
								//ADD BY WUQIONG
								else if(etWhCode.getText().toString().equals("")&&!etBinCode.getText().toString().equals("")) 
								{
									try {
										tvCount.setText("");
										//GetBaseWhCd(CompanyId,OrgId,sWhName);
										if (WhNameA.equals("")||WhNameB.equals(""))
										{
											Toast.makeText(SbbinActivity.this,"基础设置中的仓库未设置", Toast.LENGTH_LONG).show();
											//ADD CAIXY TEST START
											MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
											//ADD CAIXY TEST END
											return false;
										}
										else
										{
											sWhCode = "";
											sWhCode2 = "";
											sWhName = "";
											if(WhNameA.equals(WhNameB))
											{
												sWhName = WhNameA;
												GetBaseWhCdByName(sWhName);

											}
											else
											{
												//yaoxiugai
												//sWhName = WhNameA;
												GetBaseWhCdByName(WhNameA);
												GetBaseWhCdByName(WhNameB);
											}
										}
										//etWhCode.setText(sWhCode2);
										GetStockInfo(sWhCode,etBinCode.getText().toString());
										//return true;
										
										//ADD BY WUQIONG 2015/04/14
										GetStockInfoSum(sWhCode,etBinCode.getText().toString());
										//tvCount.setText("合计查询件数："+searchCount+"件");
										//ADD BY WUQIONG 2015/04/14
										
									} catch (ParseException e) {
										Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
										//ADD CAIXY TEST START
										MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
										//ADD CAIXY TEST END
										return false;
									} catch (JSONException e) {
										Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
										//ADD CAIXY TEST START
										MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
										//ADD CAIXY TEST END
										return false;
									} catch (IOException e) {
										Toast.makeText(SbbinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
										//ADD CAIXY TEST START
										MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
										//ADD CAIXY TEST END
										return false;
									}
								}
								//ADD BY WUQIONG
								else
								{
									Toast.makeText(SbbinActivity.this,R.string.QingShuRuHuoWeiHao, Toast.LENGTH_LONG).show();
									//ADD CAIXY TEST START
									MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
									//ADD CAIXY TEST END
									tvCount.setText("");
									if(array!=null)
									{array.removeAll(array);}
									if(listItemAdapter!=null)
									{
									listItemAdapter.notifyDataSetChanged();
									lvListView.setAdapter(listItemAdapter);
									}
									return false;
								}
							}				
					}
					etBinCode.requestFocus();
					return false;
			}
	     }
	    };
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sbbin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
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
}
