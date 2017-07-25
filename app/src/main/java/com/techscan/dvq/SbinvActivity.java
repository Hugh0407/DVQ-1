package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.login.MainLogin;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import android.R;

public class SbinvActivity extends Activity {

	EditText edtBarcode=null;
	EditText edtInv=null;
	EditText edtBatch=null;
	Button btSearch=null;
	TextView tvInvName=null;
	private SplitBarcode bar = null;
	SimpleAdapter listItemAdapter=null;
	ArrayList<HashMap<String,String>> array=null;
	ListView lvlist=null;
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//private writeTxt writeTxt ;	
	//ADD CAIXY TEST END 
	Button btnsbinvReturn = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sbinv);
		tvInvName=(TextView)findViewById(R.id.tvsbinvName);
		tvInvName.setText("");
		lvlist=(ListView)findViewById(R.id.lvsbinvList);
		btSearch=(Button)findViewById(R.id.btsearchinvSearch);
		btSearch.setOnClickListener(myClickListten);
		edtBarcode=(EditText)findViewById(R.id.edtsbinvBarcode);
		edtInv=(EditText)findViewById(R.id.edtsbinvInv);
		edtBatch=(EditText)findViewById(R.id.edtsbinvBatch);
		edtBarcode.setOnKeyListener(myTxtListener);
		edtInv.setOnKeyListener(myTxtListener);
		edtBatch.setOnKeyListener(myTxtListener);
		edtBarcode.requestFocus();
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		writeTxt = new writeTxt();
		//ADD CAIXY END
		edtInv.setSelectAllOnFocus(true);
		edtBatch.setSelectAllOnFocus(true);
		edtBarcode.setSelectAllOnFocus(true);
		btnsbinvReturn = (Button)findViewById(R.id.btnsbinvReturn);
		btnsbinvReturn.setOnClickListener(myClickListten);
		lvlist.setOnItemClickListener(myListItemListener); 
		
		btSearch.setFocusable(false);
		btnsbinvReturn.setFocusable(false);
	}
	
	
	private OnClickListener myClickListten =new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				case id.btnsbinvReturn:

					finish();					
					break;
				case id.btsearchinvSearch:
					if(!edtInv.getText().toString().equals("")) 
					{
						try {
							GetStockInfo(edtInv.getText().toString(),edtBatch.getText().toString(),"");
							edtBarcode.setText("");
//							edtInv.setText("");
//							edtBatch.setText("");
						} catch (ParseException e) {
							Toast.makeText(SbinvActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (JSONException e) {
							Toast.makeText(SbinvActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						} catch (IOException e) {
							Toast.makeText(SbinvActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
					}
					else
					{
						Toast.makeText(SbinvActivity.this,"请至少输入物料号", Toast.LENGTH_LONG).show();
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						return;
					}
					 
					break;
				
			}
			
		}
	};
	private OnKeyListener myTxtListener = new 
    		OnKeyListener()
    {
		@Override
		public boolean onKey(View v, int arg1, KeyEvent arg2) {
			{
				switch(v.getId())
				{
					case id.edtsbinvBarcode:
						if(arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)//&& arg2.getAction() == KeyEvent.ACTION_DOWN
						{
							try {
								ScanBarcode(edtBarcode.getText().toString());
								edtBarcode.setText("");
//								edtInv.setText("");
//								edtBatch.setText("");
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								Toast.makeText(SbinvActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Toast.makeText(SbinvActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Toast.makeText(SbinvActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								e.printStackTrace();
							}
							return true;
						}
						
					break;
					case id.edtsbinvInv:
						if(arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP)
						{
							return true;
						}
						break;
					case id.edtsbinvBatch:
						if(arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP)
						{
							return true;
						}
						break;
				}
				return false;
		}
     }
    };
    
	private OnItemClickListener myListItemListener = 
    		new OnItemClickListener()
    {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Map<String,Object> mapCurrent = (Map<String,Object>)lvlist.getAdapter().getItem(arg2);
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
    
    
    
    private void ScanBarcode(String barcode) throws JSONException, ParseException, IOException
	{
    	
//    	writeTxt.writeTxtToFile("log.txt","1：");
		if(barcode.equals(""))
		{
			Toast.makeText(this, "请扫描条码", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			edtBarcode.requestFocus();
			return;
		}
		bar = new SplitBarcode(barcode);
		if(bar.creatorOk==false)
		{
			Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			edtBarcode.requestFocus();
			return;
		}
//		writeTxt.writeTxtToFile("log.txt","2：");
		if(GetStockInfo(bar.cInvCode,bar.cBatch,bar.AccID))
		{
//			writeTxt.writeTxtToFile("log.txt","18：");
		}
		else
		{
//			writeTxt.writeTxtToFile("log.txt","19：");
		}
		
	}
    
    private boolean GetStockInfo(String InvCode,String Batch,String AccID) throws JSONException, ParseException, IOException
    {
		edtInv.setText(InvCode);
		edtBatch.setText(Batch);
//		writeTxt.writeTxtToFile("log.txt","2.5：");
		
    	if(array!=null)
		{array.removeAll(array);}
		if(listItemAdapter!=null)
		{
		listItemAdapter.notifyDataSetChanged();
		lvlist.setAdapter(listItemAdapter);
		}
		tvInvName.setText("");
		
//		writeTxt.writeTxtToFile("log.txt","3：");
		
    	JSONObject serList = null;
		JSONObject para = new JSONObject();
		
		para.put("FunctionName", "SearchGetStockInfo");
		para.put("TableName","InvInfo");
		para.put("InvCode", InvCode.toUpperCase());
		para.put("InvBatch", Batch.toUpperCase());
		para.put("CompanyCode", MainLogin.objLog.CompanyCode);
		
//		writeTxt.writeTxtToFile("log.txt","4：");
		
		if(AccID!=null&& !AccID.equals(""))
		{
			para.put("accId", AccID);
		}
		try
		{
		
//			writeTxt.writeTxtToFile("log.txt","4.1:");
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return false ;
	        }
			
		serList = Common.
				DoHttpQuery(para, "CommonQuery", AccID);
		
//			writeTxt.writeTxtToFile("log.txt","5：");
		}
		catch(Exception e)
		{
//			writeTxt.writeTxtToFile("log.txt","6：");
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}
		
//		writeTxt.writeTxtToFile("log.txt","7：");
		if(serList==null)
		{
//			writeTxt.writeTxtToFile("log.txt","8：");
//			Toast.makeText(this, serList.getString("获取物料过程中发生了错误"), 
//					Toast.LENGTH_LONG).show();
			
			Toast.makeText(this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
		}
//		writeTxt.writeTxtToFile("log.txt","9：");		
		if(!serList.has("Status"))
		{
//			writeTxt.writeTxtToFile("log.txt","10：");
//			Toast.makeText(this, serList.getString("网络异常，请再试一次"), 
//					Toast.LENGTH_LONG).show();
			Toast.makeText(this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
		}
//		writeTxt.writeTxtToFile("log.txt","11：");
		if(!serList.getBoolean("Status"))
		{
			array=new ArrayList<HashMap<String,String>>();

				tvInvName.setText("该存货暂未入库");
				HashMap<String,String> map =new HashMap<String,String>();
				map.put("invcode", InvCode);
				map.put("csname","无货位信息");
				map.put("storname", "无仓库信息");
				map.put("vlotb", Batch);
				map.put("nnum", "0");
				array.add(map);
				
				 listItemAdapter = new SimpleAdapter(this,array,//数据源   
							com.techscan.dvq.R.layout.searchforinv,//ListItem的XML实现  
			                //动态数组与ImageItem对应的子项          
			                new String[] {"invcode","vlotb","csname","storname","nnum"},   
			                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
			                new int[] {com.techscan.dvq.R.id.tvsearchInvcorpcode,com.techscan.dvq.R.id.tvsearchInvUnitName,com.techscan.dvq.R.id.tvsearchunitcode,com.techscan.dvq.R.id.tvSearchInvcsCode,com.techscan.dvq.R.id.tvsearchInvCode}  
			            ); 
					ListView lv=(ListView)findViewById(R.id.lvsbinvList);
					lv.setAdapter(listItemAdapter);
//					writeTxt.writeTxtToFile("log.txt","16：");

					return true;
		}
		
//		writeTxt.writeTxtToFile("log.txt","13：");
		JSONArray arys = serList.getJSONArray("InvInfo");
		
		array=new ArrayList<HashMap<String,String>>();
		
//		writeTxt.writeTxtToFile("log.txt","14：");
		for(int i=0;i< arys.length();i++)
		{
			//vlotb, a.nnum,invb.invcode,invb.invname,carg.cscode,carg.csname,stor.storcode,stor.storname,corp.unitcode,corp.unitname
			tvInvName.setText(arys.getJSONObject(i).get("invname").toString());
			HashMap<String,String> map =new HashMap<String,String>();
//			map.put("invname", arys.getJSONObject(i).get("invname").toString());
			map.put("invcode", arys.getJSONObject(i).get("invcode").toString());
			//map.put("cscode", arys.getJSONObject(i).get("cscode").toString());
			map.put("csname", arys.getJSONObject(i).get("cscode").toString());
			//map.put("storcode", arys.getJSONObject(i).get("storcode").toString());
			map.put("storname", arys.getJSONObject(i).get("storname").toString());
			//map.put("unitcode", arys.getJSONObject(i).get("unitcode").toString());
			//map.put("unitname", arys.getJSONObject(i).get("unitname").toString());
			map.put("vlotb", arys.getJSONObject(i).get("vlotb").toString());
			map.put("nnum", arys.getJSONObject(i).get("nnum").toString());
			array.add(map);
			//map.put("invname", value)arys.getJSONObject(i).get("invname");
		}
		
//		writeTxt.writeTxtToFile("log.txt","15：");
		 listItemAdapter = new SimpleAdapter(this,array,//数据源   
				com.techscan.dvq.R.layout.searchforinv,//ListItem的XML实现  
                //动态数组与ImageItem对应的子项          
                new String[] {"invcode","vlotb","csname","storname","nnum"},   
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] {com.techscan.dvq.R.id.tvsearchInvcorpcode,com.techscan.dvq.R.id.tvsearchInvUnitName,com.techscan.dvq.R.id.tvsearchunitcode,com.techscan.dvq.R.id.tvSearchInvcsCode,com.techscan.dvq.R.id.tvsearchInvCode}  
            ); 
		ListView lv=(ListView)findViewById(R.id.lvsbinvList);
		lv.setAdapter(listItemAdapter);
//		writeTxt.writeTxtToFile("log.txt","16：");

		return true;
		
		
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sbinv, menu);
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
