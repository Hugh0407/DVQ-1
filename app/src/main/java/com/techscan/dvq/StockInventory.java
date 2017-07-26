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

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class StockInventory extends Activity {
	

	String ReSaveFlgA = "";
	String ReSaveFlgB = "";
	EditText txtInvOrderA;
	EditText txtInvOrderB;
	ListView lvSIOrder;
	Button btnStockInventoryScan;
	Button btnStockInventoryReScan;
	Button btnStockInventoryExit;
	Button btnStockInventorySave;
	
	ImageButton btnInvOrderA;
	ImageButton btnInvOrderB;
	
	String headJons;
	JSONObject jonsHead;		//源头单据表头
	JSONObject jonsBody;		//源头单据表体
	
	JSONObject saveJons;
	JSONObject currentWarehouse;
	
	String ReScanMode = "0";
	int SameBarCount = 0;
	//int SameBarCount2 = 0;
	
	//String warehouseCode="";
//	String warehouseID="";//仓库ID
//	String companyID="";//公司ID
//	String OrgId=""; //库存组织ID
//	String accID="";//帐套号
//	String vCode="";//盘点单号
	//String BillId="";//盘点单ID
	
	String accIDA="";//帐套号A
	String vCodeA="";//盘点单号A
	String BillIdA="";//盘点单IDA
	String warehouseIDA="";//仓库IDA
	String warehouseCodeA="";//仓库CodeA
	
	String accIDB="";//帐套号B
	String vCodeB="";//盘点单号B
	String BillIdB="";//盘点单IDB
	String warehouseIDB="";//仓库IDB
	String warehouseCodeB="";//仓库CodeB
	String warehouseNameA="";//仓库名称A
	String warehouseNameB="";//仓库名称B
	String  companyIDA ="";
	String  companyIDB ="";
	
	// 定义是否删除Dialog
	private AlertDialog DeleteAlertDialog = null;
//	private ButtonOnClick buttonDelOnClick = new ButtonOnClick(0);
	
	//ADD BY WUQIONG S
	//String productAddress="";//产地
	JSONObject m_JsonAdds=null;
	
	List<Map<String, Object>> lstSIOrder = new ArrayList<Map<String, Object>>();
	Map<String, Object> objA = new HashMap<String, Object>();
	Map<String, Object> objB = new HashMap<String, Object>();
	
	
	
	SimpleAdapter lvDBOrderAdapter;
	String[] from = {"Bill","AccID", "WhName"};
    int[] to = { R.id.tvSIBill1, R.id.tvSIAccId, R.id.tvWhName1};
	//ADD BY WUQIONG E
	
	JSONObject m_SerialNo=null;
	JSONObject m_ScanDetail=null;
	
	String hstable="";
	Intent scanDetail =null;
	//UUID uploadGuid=null;
	String siaddress="";
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//ADD CAIXY TEST END

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("库存盘点");
		setContentView(R.layout.activity_stock_inventory);
		
		btnStockInventoryScan = (Button)findViewById(R.id.btnStockInventoryScan);
		btnStockInventoryReScan = (Button)findViewById(R.id.btnStockInventoryReScan);
		btnStockInventoryExit = (Button)findViewById(R.id.btnStockInventoryExit);
		btnStockInventorySave = (Button)findViewById(R.id.btnStockInventorySave);
		btnStockInventoryScan.setOnClickListener(myListner);
		btnStockInventoryReScan.setOnClickListener(myListner);
		btnStockInventoryExit.setOnClickListener(myListner);
		btnStockInventorySave.setOnClickListener(myListner);
		
		btnInvOrderA = (ImageButton)findViewById(R.id.btnInvOrderA);
		btnInvOrderA.setOnClickListener(myListner);
		
		btnInvOrderB = (ImageButton)findViewById(R.id.btnInvOrderB);
		btnInvOrderB.setOnClickListener(myListner);

		txtInvOrderA = (EditText)findViewById(R.id.txtInvOrderA);
		txtInvOrderA.setOnKeyListener(myTxtListener);
		txtInvOrderA.requestFocus();
		
		txtInvOrderB = (EditText)findViewById(R.id.txtInvOrderB);
		txtInvOrderB.setOnKeyListener(myTxtListener);
		
		lvSIOrder = (ListView)findViewById(R.id.lvSIOrder);
		
		btnStockInventoryScan.setFocusable(false);
		btnStockInventoryReScan.setFocusable(false);
		btnStockInventoryExit.setFocusable(false);
		btnStockInventorySave.setFocusable(false);
		
		btnInvOrderA.setFocusable(false);
		btnInvOrderB.setFocusable(false);
		
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		//ADD CAIXY END
	}
	
		
		@Override  
		protected void onActivityResult(int requestCode, int resultCode, Intent data)  
		{     
			if(requestCode==98)
			{
		    switch (resultCode) {          
		        case 1:         // 这是盘点订单单号列表返回的地方 
		        if (data != null) 
		        {             
		            Bundle bundle = data.getExtras();             
		            if (bundle != null) 
		            {    
		            	// 得到子窗口ChildActivity的回传数据
		            	String AccID = bundle.getString("AccID");
		            	String orderNo = bundle.getString("result");
		            	String billId = bundle.getString("BillId");
		            	String CheckwarehouseID = (String)bundle.get("warehouseID");
		            	String CheckwarehouseCode = (String)bundle.get("warehouseCode");
		            	String CheckcompanyID = (String)bundle.get("companyID");
		            	String warehouseName = (String)bundle.get("warehouseName");
		            	hstable =(String)bundle.get("hashTable");
		            	//vCode = (String)bundle.get("vcode");
			           	if(AccID.equals("A"))
			            {
			           		if(warehouseNameB.equals(warehouseName)||warehouseNameB.equals(""))
			           		{
			           			txtInvOrderA.setText(orderNo);
			           			accIDA = "A";//帐套号A
			           			vCodeA = orderNo;//盘点单号A
			           			BillIdA = billId;//盘点单IDA
			           			warehouseIDA=CheckwarehouseID;//仓库IDA
			           			warehouseNameA=warehouseName;
			           			warehouseCodeA=CheckwarehouseCode;
			           			companyIDA=CheckcompanyID;
			           			
		           			
			           			//Map<String, Object> obj = new HashMap<String, Object>();
			           			
			           			objA.put("Bill", orderNo);
			           			objA.put("AccID", AccID);
			           			objA.put("WhName", warehouseName);
			           			objA.put("WhCode", CheckwarehouseCode);
			           			objA.put("WhID", CheckwarehouseID);
			           			objA.put("BillID", billId);
			           			objA.put("companyID", CheckcompanyID);
			           			
			           			lstSIOrder = new ArrayList<Map<String, Object>>();
			           			txtInvOrderA.setText("");
			           			
			           			lstSIOrder.add(objA);
			           			
			           			if(objB.size()>0)
			           			{
			           				lstSIOrder.add(objB);
			           			}
								
						        lvDBOrderAdapter = new SimpleAdapter(StockInventory.this, lstSIOrder, R.layout.vlistsi, from, to);
						        lvSIOrder.setAdapter(lvDBOrderAdapter);

			           		}
			           		else
			           		{
			           			Toast.makeText(StockInventory.this,"不能同时选择不同仓库的盘点单", Toast.LENGTH_LONG).show();
			           			//ADD CAIXY TEST START
			        			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			        			//ADD CAIXY TEST END
			           			txtInvOrderA.setText("");
			           		}
			            }
			            else if(AccID.equals("B"))
			            {
			           		if(warehouseNameA.equals(warehouseName)||warehouseNameA.equals(""))
			           		{
			           			txtInvOrderB.setText(orderNo);
			           			accIDB = "B";//帐套号B
			           			vCodeB = orderNo;//盘点单号B
			           			BillIdB = billId;//盘点单IDB
			           			warehouseIDB = CheckwarehouseID;//仓库IDB
			           			warehouseCodeB=CheckwarehouseCode;
			           			warehouseNameB=warehouseName;
			           			companyIDB=CheckcompanyID;
			           			//ADD BY WUQIONG S
			           			
			           			objB.put("Bill", orderNo);
			           			objB.put("AccID", AccID);
			           			objB.put("WhName", warehouseName);
			           			objB.put("WhCode", CheckwarehouseCode);
			           			objB.put("WhID", CheckwarehouseID);
			           			objB.put("BillID", billId);
			           			objB.put("companyID", CheckcompanyID);
			           			
			           			
			           			
			           			lstSIOrder = new ArrayList<Map<String, Object>>();
			           			txtInvOrderB.setText("");
			           			
			           			if(objA.size()>0)
			           			{
			           				lstSIOrder.add(objA);
			           			}
								lstSIOrder.add(objB);
								
								
								lvDBOrderAdapter = new SimpleAdapter(StockInventory.this, lstSIOrder, R.layout.vlistsi, from, to);
						        lvSIOrder.setAdapter(lvDBOrderAdapter);

			           		}
			           		else
			           		{
			           			Toast.makeText(StockInventory.this,"不能同时选择不同仓库的盘点单", Toast.LENGTH_LONG).show();
			           			//ADD CAIXY TEST START
			        			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			        			//ADD CAIXY TEST END
			        			txtInvOrderB.setText("");
			           		}
			           		
			            }	   
		            	try 
		            	{
							//LoadOrderListFromDB(BillId);

//							//这里用来清除之前扫描的信息
							this.m_ScanDetail=null;
							this.m_SerialNo=null;
							this.hstable ="";
						} catch (Exception e) {
							Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							jonsBody=null;
							return;
						} 
		            }          
		        }         
		        break;
		        default:              
		            //其它窗口的回传数据           
		        	//IniActivyMemor();
		        break;          
		        } 
			}
			//ADD BY WUQIONG S
			else if(requestCode==1)
			{
				switch (resultCode) { 
				case 12:         // 这是调拨订单单号列表返回的地方 
			        if (data != null) 
			        {             
			            Bundle bundle = data.getExtras();             
			            if (bundle != null) 
			            {          
//				            	accID =(String)bundle.get("AccID");
//				            	warehouseID=(String)bundle.get("wareHousePK");
//				            	OrgId=(String)bundle.get("orgID");
//				            	companyID=(String)bundle.get("companyId");
				            	
				            	String AccID = bundle.getString("AccID");
				            	String orderNo = bundle.getString("result");
				            	String CheckwarehouseID = (String)bundle.get("warehouseID");
				            	String CheckwarehouseCode = (String)bundle.get("warehouseCode");
				            	
				            	hstable=(String)bundle.get("hashTable");
				            	try {
									m_JsonAdds=new JSONObject(bundle.get("saveJsAdd").toString());
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
									//ADD CAIXY TEST START
									MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
									//ADD CAIXY TEST END
								}
				            	
				            	try {
				            		m_ScanDetail=new JSONObject(bundle.get("saveJs").toString());
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
									//ADD CAIXY TEST START
									MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
									//ADD CAIXY TEST END
								}

				            	 try {
				            		 m_SerialNo=new JSONObject(bundle.get("serJs").toString());
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
									//ADD CAIXY TEST START
									MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
									//ADD CAIXY TEST END
								}
			            	}
			        	}
			        }
//			       break;
//				default:              
		            //其它窗口的回传数据           
		        	//IniActivyMemor();
//		        break;  
			}
				
//			}
			//ADD BY WUQIONG E

		        super.onActivityResult(requestCode, resultCode, data);  
		        System.gc();
		}   
  	
	private OnKeyListener myTxtListener = new OnKeyListener()
    {
		@Override
		public boolean onKey(View v, int arg1, KeyEvent arg2) {
			switch(v.getId())
			{
				case id.txtInvOrderA:
					if(arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)//&& arg2.getAction() == KeyEvent.ACTION_DOWN
					{
						
					String BillCode = txtInvOrderA.getText().toString();
					if(BillCode.equals(""))
					{
						txtInvOrderA.setText("");
						Toast.makeText(StockInventory.this,"扫描的单号不正确", Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						return false;
					}
					BillCode = BillCode.replace("\n", "");
					String lsBillAccID = BillCode.substring(0,1);
					
					if (lsBillAccID.equals("A")||lsBillAccID.equals("B")) 
					{
						
						lvDBOrderAdapter = new SimpleAdapter(
								StockInventory.this, lstSIOrder,
								R.layout.vlistsi, from, to);						
						lvSIOrder.setAdapter(lvDBOrderAdapter);
						Intent ViewGrid = new Intent(StockInventory.this,InvOrderList.class);
						ViewGrid.putExtra("BillCode", BillCode);
						ViewGrid.putExtra("AccID", lsBillAccID);
						txtInvOrderA.setText("");
						startActivityForResult(ViewGrid, 98);
						return true;
					}
						else
						{
							txtInvOrderA.setText("");
							Toast.makeText(StockInventory.this,"扫描的单号不正确", Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return true;
							
						}
					}
				case id.txtInvOrderB:
					if(arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)//&& arg2.getAction() == KeyEvent.ACTION_DOWN
				{
						
						String BillCode = txtInvOrderB.getText().toString();
						if(BillCode.equals(""))
						{
							txtInvOrderA.setText("");
							Toast.makeText(StockInventory.this,"扫描的单号不正确", Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return false;
						}
						BillCode = BillCode.replace("\n", "");
						String lsBillAccID = BillCode.substring(0,1);
						
						if (lsBillAccID.equals("A")||lsBillAccID.equals("B")) 
						{
							lvDBOrderAdapter = new SimpleAdapter(
									StockInventory.this, lstSIOrder,
									R.layout.vlistsi, from, to);
							lvSIOrder.setAdapter(lvDBOrderAdapter);
							Intent ViewGrid = new Intent(StockInventory.this,InvOrderList.class);
							ViewGrid.putExtra("BillCode", BillCode);
							ViewGrid.putExtra("AccID", lsBillAccID);
							txtInvOrderB.setText("");
							startActivityForResult(ViewGrid, 98);
							return true;
						}
							else
							{
								txtInvOrderB.setText("");
								Toast.makeText(StockInventory.this,"扫描的单号不正确", Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								return true;
								
							}
						}
					break;

			}
			return false;
		}	    	
    };	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_inventory, menu);
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
	
	//复核按钮对话框事件
  	private DialogInterface.OnClickListener listenReScan = new 
  			DialogInterface.OnClickListener()
  	{
  		public void onClick(DialogInterface dialog,
  			int whichButton)
  		{
  			btnInvScanClick("2");
  		}
  	};
	
	//取消按钮对话框事件
  	private DialogInterface.OnClickListener listenExit = new 
  			DialogInterface.OnClickListener()
  	{
  		public void onClick(DialogInterface dialog,
  			int whichButton)
  		{
  			StockInventory.this.finish();	
  			System.gc();
  		}
  	};
	 @Override	
	 public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {		if (keyCode == KeyEvent.KEYCODE_MENU) 
		 	{//拦截菜单键事件			//do something...	
		       return false;
			 }		
	 if (keyCode == KeyEvent.KEYCODE_BACK) 
	 {//拦截返回按钮事件			//do something...	
		 return false;
	 }		
	 return true;
	 }	 

  	//取消按钮
  	private void Exit()
  	{
  		 AlertDialog.Builder bulider = 
  				 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage(R.string.NiQueDingYaoTuiChuMa);
  		 bulider.setNegativeButton(R.string.QuXiao, null);
  		 bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();
  	}
  	
  	private void btnInvOrderAClick() throws ParseException, IOException, JSONException
	{
		Intent ViewGrid = new Intent(this,InvOrderList.class);
		ViewGrid.putExtra("AccID", "A");	
		startActivityForResult(ViewGrid,98);
	}
  	
  	private void btnInvOrderBClick() throws ParseException, IOException, JSONException
	{
		Intent ViewGrid = new Intent(this,InvOrderList.class);
		ViewGrid.putExtra("AccID", "B");
		startActivityForResult(ViewGrid,98);
	}
  	
  	private  void ReScan()
  	{
  		if((this.accIDA == null||this.accIDA.equals("")) && (this.accIDB == ""||this.accIDB.equals("")))
		{
			Toast.makeText(this, "请先选择盘点单", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
  		

		GetSameBarCount();
		if(SameBarCount==0)
		{
			Toast.makeText(this, "没有需要复核的数据", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}

		
  		AlertDialog.Builder bulider = 
  			 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("你确认要进行复核吗?" +
  					"\r\n"+"\r\n"+"本次复核共有" + SameBarCount +"条重复条码记录 "+
  					"\r\n"+"\r\n"+"若进行复核操作重复条码记录将会被删除,请在打印重复条码明细后再进行该操作 ");
  		bulider.setNegativeButton(R.string.QuXiao, null);
  		bulider.setPositiveButton(R.string.QueRen, listenReScan).create().show();
  		
  	}
  	
  	private  void GetSameBarCount()
  	{
  		if(ReScanMode.equals("1"))
  		{
  			return;
  		}
  		
  		SameBarCount = 0;
  		if(accIDA.equals("A"))
		{
			try {

				JSONObject para = new JSONObject();
				para.put("FunctionName", "GetSameBarCount");
				para.put("BillId", BillIdA);
				para.put("TableName", "SameCount");
				
				if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return ;
		        }

				JSONObject revA = Common.DoHttpQuery(para, "CommonQuery","A");
				
				if(revA==null)
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}

				if(!revA.has("Status"))
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}


				if (revA.getBoolean("Status")) {
					JSONArray valA = revA.getJSONArray("SameCount");
					if (valA.length() < 1) {
						Toast.makeText(this, "获取A账套重复条码数量失败", Toast.LENGTH_LONG).show();
						// ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						// ADD CAIXY TEST END
						return;
					}
					
					JSONObject tempA = valA.getJSONObject(0);

					String sSameCount = tempA.getString("samecount");
					
					SameBarCount = SameBarCount +Integer.valueOf(sSameCount);
				
				} else {
					Toast.makeText(this, "获取A账套重复条码数量失败", Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
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
		
		
		if(accIDB.equals("B"))
		{
			try {

				JSONObject para = new JSONObject();
				para.put("FunctionName", "GetSameBarCount");
				para.put("BillId", BillIdB);
				para.put("TableName", "SameCount");
				
				if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return ;
		        }

				JSONObject revB = Common.DoHttpQuery(para, "CommonQuery",
						"B");
				
				if(revB==null)
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}

				if(!revB.has("Status"))
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return;
				}

				//para.put("TableName", "SameCount");
				if (revB.getBoolean("Status")) {
					JSONArray valB = revB.getJSONArray("SameCount");
					if (valB.length() < 1) {
						Toast.makeText(this, "获取B账套重复条码数量失败", Toast.LENGTH_LONG).show();
						// ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						// ADD CAIXY TEST END
						return;
					}
					
					// String jposName,jposCode;
					JSONObject tempB = valB.getJSONObject(0);

					String sSameCount = tempB.getString("samecount");
					
					SameBarCount = SameBarCount +Integer.valueOf(sSameCount);
				
				} else {
					Toast.makeText(this, "获取B账套重复条码数量失败", Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
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
  	}
  	
	private boolean DelSameBar()
	{
		if(accIDA.equals("A"))
		{
			try {

				JSONObject para = new JSONObject();
				para.put("FunctionName", "DelSameBar");
				para.put("BillId", BillIdA);
				para.put("TableName", "DelSameBar");
				
				if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return false;
		        }

				JSONObject revA = Common.DoHttpQuery(para, "CommonQuery","A");
				
				if(revA==null)
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return false;
				}

				if(!revA.has("Status"))
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return false;
				}


				if (!revA.getBoolean("Status")) {
					
					Toast.makeText(this, "删除重复条码失败", Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return false;

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
		
		
		if(accIDB.equals("B"))
		{
			try {

				JSONObject para = new JSONObject();
				para.put("FunctionName", "DelSameBar");
				para.put("BillId", BillIdB);
				para.put("TableName", "DelSameBar");
				
				if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return false ;
		        }

				JSONObject revB = Common.DoHttpQuery(para, "CommonQuery",
						"B");
				
				if(revB==null)
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return false;
				}

				if(!revB.has("Status"))
				{
					Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					return false;
				}

				//para.put("TableName", "SameCount");
				if (!revB.getBoolean("Status")) {
					
					Toast.makeText(this, "删除重复条码失败", Toast.LENGTH_LONG).show();
					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return false;

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
		
		return true;
	}
  	
  	private void btnInvScanClick(String ScanMode)
	{
		if((this.accIDA == null||this.accIDA.equals("")) && (this.accIDB == ""||this.accIDB.equals("")))
		{
			Toast.makeText(this, "请先选择盘点单", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		if(ScanMode.equals("2"))
		{
			if(!DelSameBar())
			{
				//Toast.makeText(this, "删除重复条码记录时发生了错误,请再次点击复核按钮", Toast.LENGTH_LONG).show();
				ReScanMode = "1";
				//SameBarCount2 = SameBarCount;
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
		}
		
		scanDetail = new Intent(StockInventory.this,StockInventoryScan.class);
		
		//ADD BY WUQIONG S
		scanDetail.putExtra("hashTable", hstable);
//		scanDetail.putExtra("SIAddress", siaddress);
		if(m_SerialNo!=null)
		{
			scanDetail.putExtra("List", m_SerialNo.toString());
		}
		
		if(m_ScanDetail!=null)
		{
			scanDetail.putExtra("ScanDetail", m_ScanDetail.toString());
		}
		
		if(m_JsonAdds!=null)
		{
			scanDetail.putExtra("JsonAddspos", m_JsonAdds.toString());
		}
		//ADD BY WUQIONG E
		
		if(accIDA.equals("A"))
		{
			scanDetail.putExtra("AccIDA", "A");
			scanDetail.putExtra("warehouseIDA", warehouseIDA);
			scanDetail.putExtra("warehouseCodeA", warehouseCodeA);
			scanDetail.putExtra("vCodeA", vCodeA);
			scanDetail.putExtra("BillIdA", BillIdA);
			scanDetail.putExtra("companyIDA", companyIDA);
		}
		else
		{
			scanDetail.putExtra("AccIDA", "");
			scanDetail.putExtra("warehouseIDA", "");
			scanDetail.putExtra("warehouseCodeA", "");
			scanDetail.putExtra("vCodeA", "");
			scanDetail.putExtra("BillIdA", "");
			scanDetail.putExtra("companyIDA", "");
		}
		
		if(accIDB.equals("B"))
		{
			scanDetail.putExtra("AccIDB", "B");
			scanDetail.putExtra("warehouseIDB", warehouseIDB);
			scanDetail.putExtra("warehouseCodeB", warehouseCodeB);
			scanDetail.putExtra("vCodeB", vCodeB);
			scanDetail.putExtra("BillIdB", BillIdB);
			scanDetail.putExtra("companyIDB", companyIDB);
			
		}
		else
		{
			scanDetail.putExtra("AccIDB", "");
			scanDetail.putExtra("warehouseIDB", "");
			scanDetail.putExtra("warehouseCodeB", "");
			scanDetail.putExtra("vCodeB", "");
			scanDetail.putExtra("BillIdB", "");
			scanDetail.putExtra("companyIDB", "");
		}
		if(ScanMode.equals("2"))
		{
			scanDetail.putExtra("ScanMod","ReScan");
		}
		else
		{
			scanDetail.putExtra("ScanMod","Scan");
		}
		
		String sSameBarCount = SameBarCount + "" ;
		scanDetail.putExtra("SameCount",sSameBarCount);
		ReScanMode = "0";
		startActivityForResult(scanDetail,1);	
	}
  	
  	//ADD BY WUQIONG S
	private void SaveInventoryStock() throws JSONException, ParseException, IOException
	{
		if(vCodeA.equals("")&&vCodeB.equals(""))
		{
            Toast.makeText(this, "没有需要保存的单据",Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return ;
		}
		
		JSONObject jasA = null;
		JSONObject jasB = null;
		
		saveJons = new JSONObject();
		
		if(!vCodeA.equals("")&&ReSaveFlgA.equals(""))
		{
			saveJons.put("VbillCode", vCodeA);
			saveJons.put("VbillID", BillIdA);
			saveJons.put("companyID", companyIDA);
			saveJons.put("coperatorid", MainLogin.objLog.UserID);
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			
			jasA= Common.DoHttpQuery(saveJons, "SaveSTOCKINVENTORY", "A"); 
			if(jasA==null)
			{
				Toast.makeText(this, R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			
			if(!jasA.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
		}
		
		if(!vCodeB.equals("")&&ReSaveFlgB.equals(""))
		{
			saveJons.put("VbillCode", vCodeB);
			saveJons.put("VbillID", BillIdB);
			saveJons.put("companyID", companyIDB);
			saveJons.put("coperatorid", MainLogin.objLog.UserIDB);
			
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			
			jasB= Common.DoHttpQuery(saveJons, "SaveSTOCKINVENTORY", "B"); 
			if(jasB==null)
			{
				Toast.makeText(this, R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			
			if(!jasB.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
		}
		

		boolean loginStatusA= true;
		boolean loginStatusB= true;
		
		if(!vCodeA.equals("")&&ReSaveFlgA.equals(""))
		{
			loginStatusA= jasA.getBoolean("Status");
		}
		if(!vCodeB.equals("")&&ReSaveFlgB.equals(""))
		{
			loginStatusB= jasB.getBoolean("Status");
		}
		
		if(loginStatusA==true&&loginStatusB==true)
		{
			Map<String,Object> mapResultBillCode = new HashMap<String,Object>();
			mapResultBillCode.put("BillCode", vCodeA+"   "+vCodeB);
			ArrayList<Map<String,Object>> lstResultBillCode = new ArrayList<Map<String,Object>>();
			lstResultBillCode.add(mapResultBillCode);
			
			GetSameBarCount();
			if(SameBarCount>0)
			{
				mapResultBillCode.put("BillCode", vCodeA+"    "+vCodeB
						+ "\r\n"+ "\r\n"+"本次盘点共有" + SameBarCount +"条重复条码记录");
			}
			else
			{

				mapResultBillCode.put("BillCode", vCodeA+" "+vCodeB
						+ "\r\n"+ "\r\n"+"本次盘点无条重复条码记录");

			}
			
			SimpleAdapter listItemAdapter = new SimpleAdapter(StockInventory.this,lstResultBillCode,//数据源   
	                android.R.layout.simple_list_item_1,       
	                new String[] {"BillCode"},
	                new int[] {android.R.id.text1}  
	            ); 
			new AlertDialog.Builder(StockInventory.this).setTitle(R.string.DanJuBaoCunChengGong)
							.setAdapter(listItemAdapter, null)
							.setPositiveButton(R.string.QueRen,null).show();
			
			ReSaveFlgA = "";
			ReSaveFlgB = "";
			
			objA = new HashMap<String, Object>();
			objB = new HashMap<String, Object>();
			
			List<Map<String, Object>> lstSIOrder = new ArrayList<Map<String, Object>>();
			lvDBOrderAdapter = new SimpleAdapter(
					StockInventory.this, lstSIOrder,
					R.layout.vlistsi, from, to);						
			lvSIOrder.setAdapter(lvDBOrderAdapter);
			
			accIDA="";//帐套号A
			vCodeA="";//盘点单号A
			BillIdA="";//盘点单IDA
			warehouseIDA="";//仓库IDA
			warehouseCodeA="";//仓库CodeA
			
			accIDB="";//帐套号B
			vCodeB="";//盘点单号B
			BillIdB="";//盘点单IDB
			warehouseIDB="";//仓库IDB
			warehouseCodeB="";//仓库CodeB
			warehouseNameA="";//仓库名称A
			warehouseNameB="";//仓库名称B
			companyIDA ="";
			companyIDB ="";
			
			return;
		}
		else
		{
			String errMsg = "";
			if(loginStatusA!=true)
			{
				if(jasA.has("ErrMsg"))
				{
					errMsg = jasA.getString("ErrMsg");
				}
				else
				{
					errMsg = "单据保存失败";
				}
			}
			else
			{
				ReSaveFlgA = "1";
			}
			if(loginStatusB!=true)
			{
				if(jasB.has("ErrMsg"))
				{
					errMsg = errMsg+jasB.getString("ErrMsg");
				}
				else
				{
					errMsg = errMsg+"单据保存失败";
				}
			}
			else
			{
				ReSaveFlgB = "1";
			}

			Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
	}
	
	/**
	 * 判断扫描是否已经完毕,
	 * @param Source 传递原单据数据信息
	 * @param scanBody专递扫描明细
	 * @throws JSONException 
	 */
	String IsFinishScan(JSONObject Source,JSONObject scanBody) throws JSONException
	{
		
			JSONArray details=null;
			try {
				details = scanBody.getJSONObject("SerialNo").getJSONArray("List");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				return e.getMessage();
			}
			for(int i = 0;i<details.length();i++)
			{
				String isfinish="";
				
				 try {
					 isfinish=details.getJSONObject(i).get("isfinish").toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
					return e.getMessage();
				}
				 if(isfinish.equals("0"))
				 {
					 String       cinventoryInfo =details.getJSONObject(i).get("cBarcode").toString();
					 String       ErrMsg         =cinventoryInfo +"还有分包数据没有扫描完毕";
					 SplitBarcode sb             =new SplitBarcode(cinventoryInfo);
					 String       total          =sb.TotalBox.replaceFirst("^0*", "");
					 ArrayList    arrays         =new ArrayList();
					 for(int s=0;s<details.length();s++)
					 {
						 if(details.getJSONObject(s).get("identity").toString().equals(details.getJSONObject(i).get("identity").toString()))
						 {
							SplitBarcode sba=new SplitBarcode(details.getJSONObject(s).get("cBarcode").toString());
							String current= sba.currentBox;
							arrays.add(Integer.parseInt(current.replaceFirst("^0*", "")));
						 }
					 }
					 ArrayList noIn=new ArrayList();
					 for(int s=1;s<=Integer.parseInt(total);s++)
					 {
						 if(arrays.indexOf(s)!=-1)
						 {
							 continue;
						 }
						 else
						 {
							 noIn.add(s);
						 }
					 }
					 String noInstr="";
					 for(int s=0;s< noIn.size();s++)
					 {
						 if(s==0)
						 {
							 noInstr="明细:"+noIn.get(s).toString()+",";
						 }
						 else
						 {
							 noInstr+=noIn.get(s).toString()+",";
						 }
					 }
					 return ErrMsg+noInstr ;
				 }
			}
		    
			if(Source!=null)
			{

				Map<String, Object> map=new HashMap<String, Object>();
				for(int i=0;i<details.length();i++)
				{
					String cinvbasid=((JSONObject)details.get(i)).getString("cinvbasid");
					if(map.containsKey(cinvbasid))
					{
						
					}
				}
			}
			
		return null;
	}
	
	//ADD BY WUQIONG E
  	
	//创建对话框的按钮事件侦听	
    private Button.OnClickListener  myListner = new 
    		Button.OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				
				case R.id.btnInvOrderA:
				{				
					try {
						btnInvOrderAClick();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					}
					catch(Exception  e)
					{e.printStackTrace();}
					break;
				}
				case R.id.btnInvOrderB:
				{				
					try {
						btnInvOrderBClick();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					}
					catch(Exception  e)
					{e.printStackTrace();}
					break;
				}
				case R.id.btnStockInventorySave:
				{
					//保存单据
					//ADD BY WUQIONG S
					try {
						SaveInventoryStock();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(StockInventory.this, e.getMessage(), Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					}
					//ADD BY WUQIONG E
					break;
				}
				
				case R.id.btnStockInventoryScan:
				{
					//打开扫描画面
					btnInvScanClick("1");
					break;
				}
				case R.id.btnStockInventoryReScan:
				{
					
					ReScan();
					break;
				}
				
				
				case R.id.btnStockInventoryExit:
				{
					Exit();
					break;
				}

			}  		
		}
    };
}
