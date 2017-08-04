package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.login.MainMenu;

import org.apache.http.ParseException;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class StockBack extends Activity {

	@Nullable
	private String[]      ExitNameList  = null;
	@NonNull
	private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
	@Nullable
			String        fileNameScan  = null;

	@NonNull
	String tmpPosCode = "";


	@Nullable
			String            fileName        = null;
	@Nullable
			String            ScanedHFileName = null;
	@Nullable
			String            ScanedBFileName = null;
	@Nullable
			String            UserID          = null;
	@Nullable
			File              file            = null;
	@Nullable
			File              fileScan        = null;
	@NonNull
			String            ReScanBody      = "1";
	@NonNull
			String            ReScanHead      = "1";
	@NonNull
	private ArrayList<String> ScanedBarcode   = new ArrayList<String>();
	
	ImageButton btnStockBackWHFrom;
	ImageButton btnStockBackWHTo;
	
	Button btnStkBckSave;
	Button btnStkBckDetail;
	Button btnStkBckReturn;
	
	TextView  tvInvCount;
	private writeTxt writeTxt ;		//保存LOG文件
	EditText txtStockBackWHFrom;
	EditText txtStockBackWHTo;
	EditText txtStockBackBar;
	EditText txtStockBackInvName;
	EditText txtStockBackBatch;
	EditText txtStockBackSeriNo;
	EditText txtStockBackScanCount;
	
	EditText txtStockBackPos;
	Switch sStockBackS;
	Switch sStockBackPos;
	
//	private SoundPool sp;// 声明一个SoundPool
//	private int MainLogin.music;// 定义一个int来设置suondID
//	private int MainLogin.music2;// 定义一个int来设置suondID
	int ScanCount = 0;

	public final static String   PREFERENCE_SETTING = "Setting";
	@Nullable
	private             String[] WHNameList         = null;
	@Nullable
	private             String[] WHCodeList         = null;

	@Nullable
	private AlertDialog WHSelectButtonF =null;
	@Nullable
	private AlertDialog WHSelectButtonT =null;

	@Nullable
	private SplitBarcode              bar             = null; // 当前扫描条码解析
	@Nullable
	private Hashtable                 SerialValues    = null;
	@Nullable
			List<Map<String, String>> jonsScan        = null;
	@Nullable
			List<Map<String, String>> m_mData         = null;
	@Nullable
	private AlertDialog               DeleteButton    = null;
	@NonNull
	private ButtonOnClickWH           ButtonOnClickWH = new ButtonOnClickWH(0);
	@NonNull
	private ButtonOnClickD            ButtonOnClickD  = new ButtonOnClickD(0);
	@Nullable
			String                    WhName          = "";
	String tmpStockBackWHFrom = "";
	String tmpStockBackWHCode = "";
	String tmpStockBackWHTo = "";
	@Nullable
	private AlertDialog SelectButton = null;
	@Nullable
	private UUID        uploadGuid   =null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_back);
		
		this.setTitle("展品回仓");
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1);
		
		btnStockBackWHFrom = (ImageButton) findViewById(R.id.btnStockBackWHFrom);
		btnStockBackWHFrom.setOnClickListener(new OnClickListener());
		btnStockBackWHTo = (ImageButton) findViewById(R.id.btnStockBackWHTo);
		btnStockBackWHTo.setOnClickListener(new OnClickListener());
		
		btnStkBckSave= (Button) findViewById(R.id.btnStkBckSave);
		btnStkBckDetail= (Button) findViewById(R.id.btnStkBckDetail);
		btnStkBckReturn= (Button) findViewById(R.id.btnStkBckReturn);
		
		sStockBackPos = (Switch) findViewById(R.id.sStockBackPos);
		sStockBackPos.setChecked(false);
		sStockBackS = (Switch) findViewById(R.id.sStockBackS);
		sStockBackS.setChecked(false);
		
		tvInvCount= (TextView) findViewById(R.id.tvInvCount);
		
		txtStockBackPos= (EditText) findViewById(R.id.txtStockBackPos);
		txtStockBackPos.setOnKeyListener(myTxtListener);
		
		txtStockBackWHFrom= (EditText) findViewById(R.id.txtStockBackWHFrom);
		txtStockBackWHTo= (EditText) findViewById(R.id.txtStockBackWHTo);
		txtStockBackBar= (EditText) findViewById(R.id.txtStockBackBar);
		txtStockBackInvName= (EditText) findViewById(R.id.txtStockBackInvName);
		txtStockBackBatch= (EditText) findViewById(R.id.txtStockBackBatch);
		txtStockBackSeriNo= (EditText) findViewById(R.id.txtStockBackSeriNo);
		txtStockBackScanCount= (EditText) findViewById(R.id.txtStockBackScanCount);
		
		txtStockBackBar.setOnKeyListener(myTxtListener);
		btnStkBckSave.setOnClickListener(myListner);
		btnStkBckDetail.setOnClickListener(myListner);
		btnStkBckReturn.setOnClickListener(myListner);
		
		
		txtStockBackWHFrom.setFocusable(false);
		txtStockBackWHFrom.setFocusableInTouchMode(false);
		txtStockBackWHTo.setFocusable(false);
		txtStockBackWHTo.setFocusableInTouchMode(false);
		txtStockBackInvName.setFocusable(false);
		txtStockBackInvName.setFocusableInTouchMode(false);
		txtStockBackBatch.setFocusable(false);
		txtStockBackBatch.setFocusableInTouchMode(false);
		txtStockBackSeriNo.setFocusable(false);
		txtStockBackSeriNo.setFocusableInTouchMode(false);
		txtStockBackScanCount.setFocusable(false);
		txtStockBackScanCount.setFocusableInTouchMode(false);
		
		btnStkBckSave.setFocusable(false);
		btnStkBckDetail.setFocusable(false);
		btnStkBckReturn.setFocusable(false);
		
		SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_SETTING, 
				Activity.MODE_PRIVATE); 

		WhName =sharedPreferences.getString("WhCode", ""); 
		
		txtStockBackPos.requestFocus();
		
    	UserID = MainLogin.objLog.UserID;
    	//String LogName = BillType + UserID + dfd.format(day)+".txt";
    	
    	ScanedHFileName = "TB"+UserID+".txt";
    	fileName = "/sdcard/DVQ/TB"+UserID+".txt";

    	
    	ScanedBFileName = "TBScan"+UserID+".txt";
    	fileNameScan = "/sdcard/DVQ/TBScan"+UserID+".txt";
    	file = new File(fileName);
    	fileScan = new File(fileNameScan);

		ReScanHead();
		ReScanBody();
		MainMenu.cancelLoading();
		
	}
	
	class OnClickListener implements

	android.view.View.OnClickListener {

		public void onClick(@NonNull View v) {

			switch (v.getId()) {
			
			case id.btnStockBackWHFrom:
				try {
					GetSalesDelWH("F");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			case id.btnStockBackWHTo:
				try {
					GetSalesDelWH("T");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
					
			}
			
		}
	}
	
	private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		public int index ;

		public ButtonOnClick(int index)
		{
			this.index = index;
		}

		@Override
		public void onClick(@NonNull DialogInterface dialog, int whichButton)
		{
			if (whichButton >=0)
			{
				index = whichButton+3;
				// dialog.cancel(); 				
			}
			else
			{
				
				if(dialog.equals(SelectButton))
				{
					if (whichButton == DialogInterface.BUTTON_POSITIVE)
					{
						if(index == 3)
						{
							finish();	
				  			System.gc();
						}
						else if (index == 4)
						{
							ClearScanDetail();
							finish();	
				  			System.gc();
						}	
						return;
					}
					else if (whichButton == DialogInterface.BUTTON_NEGATIVE)
					{
						return;
					}
				}
			}
		}
	}
	
	private class ButtonOnClickWH implements DialogInterface.OnClickListener
	{
		public int index;

		public ButtonOnClickWH(int index)
		{
			this.index = index;
		}

		
		public void onClick(@NonNull DialogInterface dialog, int whichButton)
		{
			if (whichButton >= 0)
			{
				index = whichButton;
				dialog.cancel(); 				
			}
			else
			{
				return;
			}
		
			if(dialog.equals(WHSelectButtonF))
			{
				txtStockBackWHFrom.setText(WHNameList[index].toString());
				tmpStockBackWHFrom = WHNameList[index].toString();
				tmpStockBackWHCode = WHCodeList[index].toString();
				
				if(sStockBackS.isChecked())
				{
					txtStockBackWHTo.setText(WHNameList[index].toString());
					tmpStockBackWHTo = WHNameList[index].toString();
				}
				
				SaveScanedHead();
				
				txtStockBackPos.requestFocus();
			}
			if(dialog.equals(WHSelectButtonT))
			{
				if(!WHNameList[index].toString().equals(tmpStockBackWHFrom))
				{
					txtStockBackWHTo.setText(WHNameList[index].toString());
					tmpStockBackWHTo = WHNameList[index].toString();
					
					
					if(sStockBackS.isChecked())
					{
						txtStockBackWHFrom.setText(WHNameList[index].toString());
						tmpStockBackWHFrom = WHNameList[index].toString();
						tmpStockBackWHCode = WHCodeList[index].toString();
					}
					
					SaveScanedHead();
				}
				else
				{
		            Toast.makeText(StockBack.this, "调入仓库与调出仓库相同",Toast.LENGTH_LONG).show();
					txtStockBackWHTo.setText("");
					tmpStockBackWHTo = "";
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				}

				txtStockBackPos.requestFocus();
			}
			
		}
	}
	
	private void GetSalesDelWH(@NonNull String WHFlg) throws JSONException
    {
		try
		{	
						
			JSONObject serList = null;
			JSONObject para = new JSONObject();
			
			String sWHName = "";
			String scorpCode = "";

			scorpCode = "101";
			
			if(WHFlg.equals("F"))
			{
				sWHName = WhName;
			}

			para.put("FunctionName", "GetBaseWhCodeByNameAndCorp");
			para.put("TableName","WhCodeByName");
			para.put("Corp",scorpCode);
			para.put("WhName", sWHName.toUpperCase().replace("\n", ""));
		
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			
			serList = Common.
					DoHttpQuery(para, "CommonQuery", "A");
			if(serList==null)
			{
//				Toast.makeText(this, serList.getString("获取仓库过程中发生了错误"), 
//						Toast.LENGTH_LONG).show();
				Toast.makeText(this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			
			if(serList.getBoolean("Status"))
			{
				JSONArray arys = serList.getJSONArray("WhCodeByName");	
				
				WHNameList =new String[arys.length()];//设置仓库名字类型数量
				WHCodeList =new String[arys.length()];//设置仓库名字类型数量
				
				
				for(int i=0;i< arys.length();i++)
				{
					String storname = arys.getJSONObject(i).get("storname").toString();
					String storcode = arys.getJSONObject(i).get("storcode").toString();
					WHNameList[i]=storname;
					WHCodeList[i]=storcode;

				}
				if(WHFlg.equals("F"))
				{
					showWHChoiceDialogF();
				}
				else
				{
					showWHChoiceDialogT();
				}
			}
			else
			{
//				Toast.makeText(this, serList.getString("找不到相关仓库信息"), 
//						Toast.LENGTH_LONG).show();
				Toast.makeText(this,"找不到相关仓库信息", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			}
		}
		

		catch(Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}
    }
	private void ScanPos(@NonNull String PosCode) throws JSONException,ParseException, IOException {
		if(PosCode.length()>=10)
		{
			Toast.makeText(this, "输入的货位号不正确", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			txtStockBackPos.requestFocus();
			return;
		}
		tmpPosCode = PosCode;
		MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
	}
	
	// 扫描二维码解析功能函数
		private void ScanBarcode(@NonNull String barcode) throws JSONException,ParseException, IOException {
			
			if (tmpPosCode.equals("")) 
			{
				Toast.makeText(this, "请先扫描货位", Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				txtStockBackBar.setText("");
				txtStockBackPos.requestFocus();
				return;
			}
			
			
			
			if (barcode.equals("")) 
			{
				Toast.makeText(this, "请扫描条码", Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				txtStockBackBar.requestFocus();
				return;
			}
			
			txtStockBackBar.setText("");

			if(tmpStockBackWHFrom.equals("")||tmpStockBackWHTo.equals(""))
			{
				Toast.makeText(this, "请先选择调出调入仓库", Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				txtStockBackBar.requestFocus();
				return;
			}
			
			bar = new SplitBarcode(barcode);
			if (bar.creatorOk == false) {
				txtStockBackBar.setText("");
				txtStockBackBar.requestFocus();
				Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}
			
			if(!CheckHasScaned(bar))
			{
				txtStockBackBar.setText("");
				txtStockBackBar.requestFocus();
				Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			
			MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
			
			DataBindtoJons();
			BindingBillDetailInfo();
			String FinishBarCode = bar.FinishBarCode;
			ScanedBarcode.add(FinishBarCode+"!"+tmpPosCode);
			SaveScanedBody();
			
			if(sStockBackPos.isChecked())
			{
				txtStockBackBar.setFocusable(true);
				txtStockBackBar.requestFocus();
			}
			else
			{
				txtStockBackPos.setText("");
				tmpPosCode = "";
				txtStockBackPos.setFocusable(true);
				txtStockBackPos.requestFocus();
			}
		}
		
		private void BindingBillDetailInfo()
		{
			txtStockBackInvName.setText(bar.cInvCode);
			txtStockBackBatch.setText(bar.cBatch);
			txtStockBackSeriNo.setText(bar.cSerino);
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
		
		private void DataBindtoJons() throws JSONException {
			HashMap<String, String> obj = new HashMap<String, String>();
			if (jonsScan == null)
				jonsScan = new ArrayList<Map<String, String>>();
			
			String Identity = bar.AccID + bar.cInvCode
					+ bar.cBatch + bar.cSerino;
			
			putcurrentIDToHashTable(Identity, bar.currentBox);
			ArrayList array=(ArrayList)SerialValues.get(Identity);

			if(array.size()>1)
			{
				for(int i = 0; i < jonsScan.size(); i++)
				{
					String AccID = jonsScan.get(i).get("AccID").toString();
					String BarCode = jonsScan.get(i).get("BarCode").toString();
					String lsScanCount = jonsScan.get(i).get("ScanCount").toString();
					
					
					if(bar.CheckBarCode.equals(BarCode)&&bar.AccID.equals(AccID))
					{
						jonsScan.remove(i);
						obj.put("ScanCount", lsScanCount);
						if(sStockBackS.isChecked())
						{
							if(array.size()==Integer.parseInt(bar.TotalBox.replaceFirst("^0*", "")))//这里来判断打包的是否全部扫描完毕,全部完毕为1,否则为零
							{
								obj.put("isfinish", "");
							}
							else
							{
								obj.put("isfinish", "分包未完");
							}
						}
						else
						{
							obj.put("isfinish", "");
						}
//						if(array.size()==Integer.parseInt(bar.TotalBox.replaceFirst("^0*", "")))//这里来判断打包的是否全部扫描完毕,全部完毕为1,否则为零
//						{
//							obj.put("isfinish", "");
//						}
//						else
//						{
//							obj.put("isfinish", "分包未完");
//						}
						obj.put("BarCode", bar.CheckBarCode);
						obj.put("PosCode", tmpPosCode);
						obj.put("InvCode", bar.cInvCode);
						obj.put("Batch", bar.cBatch);
						obj.put("SeriNo", bar.cSerino);
						obj.put("spacenum", "1");
						obj.put("AccID", bar.AccID);
						obj.put("TotalBox", bar.TotalBox.replaceFirst("^0*", ""));
						jonsScan.add(obj);
						txtStockBackScanCount.setText(lsScanCount);
					}
				}
			}
			else
			{

				ScanCount ++;
				obj.put("ScanCount", ScanCount+"");
				if(sStockBackS.isChecked())
				{
					if(array.size()==Integer.parseInt(bar.TotalBox.replaceFirst("^0*", "")))//这里来判断打包的是否全部扫描完毕,全部完毕为1,否则为零
					{
						obj.put("isfinish", "");
					}
					else
					{
						obj.put("isfinish", "分包未完");
					}
				}
				else
				{
					obj.put("isfinish", "");
				}
				obj.put("BarCode", bar.CheckBarCode);
				obj.put("PosCode", tmpPosCode);
				obj.put("InvCode", bar.cInvCode);
				obj.put("Batch", bar.cBatch);
				obj.put("SeriNo", bar.cSerino);
				obj.put("spacenum", "1");
				obj.put("AccID", bar.AccID);
				obj.put("TotalBox", bar.TotalBox.replaceFirst("^0*", ""));
				jonsScan.add(obj);
				txtStockBackScanCount.setText(ScanCount+"");
				 
			}
			//tvInvCount.setText("已扫描"+jonsScan.size()+"件");

			tvInvCount.setText("已扫描"+jonsScan.size()+"件");


		}
		
		private boolean CheckHasScaned(@NonNull SplitBarcode bar)
		{
			String Identity =bar.AccID+bar.cInvCode+bar.cBatch+bar.cSerino;
			if(SerialValues!=null)
			{
				if(SerialValues.containsKey(Identity))
				{
				  ArrayList array=	(ArrayList)SerialValues.get(Identity);
				  for(int i=0;i< array.size();i++)
				  {
					  if(array.get(i).toString().equals(bar.currentBox))
					  {
						  return false;
					  }
				  }
				  
				}
			}
			return true;
		}
	private void showWHChoiceDialogF()
	{   

		WHSelectButtonF=new AlertDialog.Builder(this).setTitle("选择调出仓库").setSingleChoiceItems(
				WHNameList, -1, ButtonOnClickWH).setNegativeButton(R.string.QuXiao, ButtonOnClickWH).show();
	}
	private void showWHChoiceDialogT()
	{   

		WHSelectButtonT=new AlertDialog.Builder(this).setTitle("选择调入仓库").setSingleChoiceItems(
				WHNameList, -1, ButtonOnClickWH).setNegativeButton(R.string.QuXiao, ButtonOnClickWH).show();
	}

	@NonNull
	private OnKeyListener myTxtListener = new OnKeyListener() {
		@Override
		public boolean onKey(@NonNull View v, int arg1, @NonNull KeyEvent arg2) {
			{
				switch (v.getId()) {
				case id.txtStockBackBar:
					if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)// KeyEvent.ACTION_DOWN
					{
						try {
							ScanBarcode(txtStockBackBar.getText().toString());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
//					txtStockBackBar.setFocusable(true);
//					txtStockBackBar.requestFocus();

					break;
					
				case id.txtStockBackPos:
					if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP)// KeyEvent.ACTION_DOWN
					{
						try {
							ScanPos(txtStockBackPos.getText().toString());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
//					txtStockBackBar.setFocusable(true);
//					txtStockBackBar.requestFocus();

					break;

				}
				
				
			}
			return false;
		}

	};
	
//	Button btnStkBckSave;
//	Button btnStkBckDetail;
//	Button btnStkBckReturn;
@NonNull
private Button.OnClickListener myListner = new Button.OnClickListener() {
		@Override
		public void onClick(@NonNull View v) {
			switch (v.getId()) {
			case id.btnStkBckSave:
				Save();
				break;
				
			case id.btnStkBckDetail: 
				try {
					DoShowScanned();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
				
			case id.btnStkBckReturn: 
				Exit();
				break;
			}
		}
	};
	
	private void DoShowScanned() throws JSONException 
	{
		this.m_mData = jonsScan; 
		if (m_mData == null) 
		{
			return;
		}
		if (m_mData.size() <= 0) 
		{
			return;
		}
		
		SimpleAdapter listItemAdapter = new SimpleAdapter(
				this,
				m_mData,// 数据源
				R.layout.vlisttbscanitem,// ListItem的XML实现
			
				new String[] { "InvCode", "Batch", "AccID", "BarCode",
						"SeriNo","ScanCount","isfinish","PosCode" },
				new int[] { R.id.txtTBScanInvName, R.id.txtTBScanBatch,
						R.id.txtTBScanAccId, R.id.txtSINScanBarCode,
						R.id.txtTBScanSeriNo,R.id.txtTBScanCount,R.id.txtTBScanBox,R.id.txtTBScanPos});

		DeleteButton = new AlertDialog.Builder(this).setTitle(getString(R.string.SaoMiaoMingXiXinXi))
				.setSingleChoiceItems(listItemAdapter, 0, ButtonOnClickD)
				.setPositiveButton(R.string.QueRen, null).create();
		DeleteButton.getListView().setOnItemLongClickListener(
				new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {

						ConfirmDelItem(arg2);
						return false;
					}
				});
		DeleteButton.show();
	}
	
	// ADD BY WUQIONG S
		private void ConfirmDelItem(int index) {
			ButtonOnClickDelconfirm buttondel = new ButtonOnClickDelconfirm(index);
			SelectButton = new AlertDialog.Builder(this).setTitle(R.string.QueRenShanChu)
					.setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
					.setPositiveButton(R.string.QueRen, buttondel).setNegativeButton(R.string.QuXiao, null).show();
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

			if (whichButton == DialogInterface.BUTTON_POSITIVE) 
			{
				Map<String, String> map = m_mData.get(index);
				String barcode = map.get("BarCode");
				String Key = map.get("BarCode") +"!"+ map.get("PosCode");
				String identity = map.get("AccID") + map.get("InvCode") +
                        map.get("Batch") + map.get("SeriNo");
				for (int i = 0; i < jonsScan.size(); i++) 
				{
					if(barcode.equals(jonsScan.get(i).get("BarCode").toString()))
					{
						jonsScan.remove(i);
						SerialValues.remove(identity);
					}
				}
				
				
				for (int i = 0; i < ScanedBarcode.size(); i++) 
				{
					//if(barcode.equals(ScanedBarcode.get(i).toString()))
					if(ScanedBarcode.get(i).toString().startsWith(barcode))
					{
						ScanedBarcode.remove(i);
						i--;
					}
				}
				
				SaveScanedBody();
				
				tvInvCount.setText("已扫描" +jonsScan.size()+ "件");

				DeleteButton.cancel();
			
			} 
			else if (whichButton == DialogInterface.BUTTON_NEGATIVE) 
			{
				return;
			}
		}
	}
}
		
		
	 
	  	
		private void Save() {
//			saveJonsA =new JSONObject();
//			saveJonsB =new JSONObject();
			String lsResultBillCodeA = "";
			String lsResultBillCodeB = "";
			String lsResultBillCodeX = "";
			if(jonsScan==null||jonsScan.size()==0)
			{
				
				Toast.makeText(StockBack.this,"没有需要保存的数据", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			
			if(sStockBackS.isChecked())
			{
				for (int i = 0; i < jonsScan.size(); i++)
				{

					if (jonsScan.get(i).get("isfinish").equals("分包未完")) 
					{
						Toast.makeText(StockBack.this,"还有分包数据没有扫描完毕", Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						return;
					}
				}
			}
			
			if(jonsScan==null||jonsScan.equals(""))
			{
				Toast.makeText(StockBack.this,"没有扫描详细记录", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			else
			{
		        String scan= JTFH.ToJSONFMap(jonsScan, "ScanDetail");
		       
		        try {
					JSONObject ScanDetail=new JSONObject(scan);
					
					JSONArray JArray = ScanDetail.getJSONArray("ScanDetail");
					
					if(uploadGuid==null)
					{
					   uploadGuid = UUID.randomUUID();
					}

					ScanDetail.put("GUIDS", uploadGuid.toString());

					ScanDetail.put("UserIDA", MainLogin.objLog.UserID);
					ScanDetail.put("UserIDB", MainLogin.objLog.UserIDB);
					
					ScanDetail.put("HWFrom", this.tmpStockBackWHFrom);
					ScanDetail.put("HWTo", this.tmpStockBackWHTo);
					ScanDetail.put("HWCode", this.tmpStockBackWHCode);
					
					JSONObject jasA = null;
					JSONObject jasB = null;
					
					List JArrayA = new ArrayList();
					List JArrayB = new ArrayList();
					
					for(int i =0 ; i<JArray.length() ; i++)
					{
						JSONObject Jbody = JArray.getJSONObject(i);
						if (Jbody.get("AccID").equals("A"))
						{
							JArrayA.add(Jbody);
						}
						else if(Jbody.get("AccID").equals("B"))
						{
							JArrayB.add(Jbody);
						}
					}
					if (JArrayA.size()>0)
					{
						//saveJonsA.put("Body", JArrayA);
						try {
							if(!MainLogin.getwifiinfo())
							{
					            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
					            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					            return ;
					        }
							
							jasA = Common.DoHttpQuery(ScanDetail, "SaveStockBack", "A");

							if(jasA==null)
							{
								Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								return ;
							}
							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
									
					if (JArrayB.size()>0)
					{
						//saveJonsB.put("Body", JArrayB);
						try {
							if(!MainLogin.getwifiinfo()) {
					            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
					            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					            return ;
					        }
							
							jasB = Common.DoHttpQuery(ScanDetail, "SaveStockBack", "B");
							
							if(jasB==null)
							{
								Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								return ;
							}
							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(JArrayA.size()>0&&JArrayB.size()>0)
					{
						String ErrMsg = "";
						if(jasA==null)
						{
							String ErrMsgA = "A账套" +" ";
							ErrMsg = ErrMsg + ErrMsgA;
						}
						if(jasB==null)
						{
							String ErrMsgB = "B账套";
							ErrMsg = ErrMsg + ErrMsgB;
						}
						
						if (!ErrMsg.equals(""))
						{	
							Toast.makeText(StockBack.this,ErrMsg+"保存失败,请尝试重新保存", Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
						
						if(jasA!=null&&jasB!=null)
						{
							boolean loginStatusA= jasA.getBoolean("Status");
							boolean loginStatusB= jasB.getBoolean("Status");
							if(loginStatusA==true&&loginStatusB==true)
							{
								lsResultBillCodeA = jasA.getString("BillCode");
								lsResultBillCodeB = jasB.getString("BillCode");
								lsResultBillCodeX = "A "+lsResultBillCodeA + "\r\n"+"B "+lsResultBillCodeB;
								
								writeTxt = new writeTxt();
						   		
						   		Date day=new Date();    
						   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
						   		
						   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
						   		

						   		String BillType = "TB";
						   		String LogName = BillType + MainLogin.objLog.UserID + dfd.format(day)+".txt";
						   		
					   	   		String LogMsg = "";
					   	   		LogMsg = df.format(day) + " " + "A" + " " + lsResultBillCodeA; 
					   	   		writeTxt.writeTxtToFile(LogName,LogMsg);
					   	   		LogMsg = df.format(day) + " " + "B" + " " + lsResultBillCodeB; 
					   	   		writeTxt.writeTxtToFile(LogName,LogMsg);
								
								
								JArrayA= new ArrayList();
								JArrayB= new ArrayList();
								jasA = new JSONObject();
								jasB = new JSONObject();
								m_mData = new ArrayList();
							}
							else
							{
								String Errmsg = "";
								if(loginStatusA!=true)
								{
									Errmsg = Errmsg+jasA.get("ErrMsg").toString();
								}
								if(loginStatusB!=true)
								{
									Errmsg = Errmsg+jasB.get("ErrMsg").toString();
								}
								Toast.makeText(StockBack.this, Errmsg, Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								return;
							}
						}
					} 
					if(JArrayA.size()>0&&!(JArrayB.size()>0))
					{
						if(jasA==null)
						{
							Toast.makeText(StockBack.this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
						
						if(jasA!=null)
						{
							boolean loginStatusA= jasA.getBoolean("Status");
						    if(loginStatusA==true)
							{
						    	lsResultBillCodeA = jasA.getString("BillCode");
						    	lsResultBillCodeX = "A "+lsResultBillCodeA ;
								writeTxt = new writeTxt();
						   		
						   		Date day=new Date();    
						   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
						   		
						   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
						   		

						   		String BillType = "TB";
						   		String LogName = BillType + MainLogin.objLog.UserID + dfd.format(day)+".txt";
						   		
					   	   		String LogMsg = "";
					   	   		LogMsg = df.format(day) + " " + "A" + " " + lsResultBillCodeA; 

						    	
								JArrayA= new ArrayList();
								//saveJonsA = new JSONObject();
								jasA = new JSONObject();
								//JsonAdds = new JSONObject();
								m_mData = new ArrayList();
							}
						    else
						    {
						    	
								Toast.makeText(StockBack.this, jasA.get("ErrMsg").toString(), Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								return;
						    }
						}
					}
					if(!(JArrayA.size()>0)&&JArrayB.size()>0)
					{
						if(jasB==null)
						{
							Toast.makeText(StockBack.this, "保存失败,请尝试重新保存", Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
						}
						if(jasB!=null)
						{
							boolean loginStatusB= jasB.getBoolean("Status");
							if(loginStatusB==true)
							{
								lsResultBillCodeB = jasB.getString("BillCode");
								lsResultBillCodeX = "B "+lsResultBillCodeB;
								
								writeTxt = new writeTxt();
						   		
						   		Date day=new Date();    
						   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
						   		
						   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
						   		

						   		String BillType = "TB";
						   		String LogName = BillType + MainLogin.objLog.UserID + dfd.format(day)+".txt";
						   		
					   	   		String LogMsg = "";

					   	   		LogMsg = df.format(day) + " " + "B" + " " + lsResultBillCodeB; 
					   	   		writeTxt.writeTxtToFile(LogName,LogMsg);
								JArrayB= new ArrayList();
								//saveJonsB = new JSONObject();
								jasB = new JSONObject();
								//JsonAdds = new JSONObject();
								m_mData = new ArrayList();
							}
						    else
						    {
								Toast.makeText(StockBack.this, jasA.get("ErrMsg").toString(), Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								return;
						    }
						}
					}
					
					//ClearScanDetail();

					Map<String,Object> mapResultBillCode = new HashMap<String,Object>();
					mapResultBillCode.put("BillCode", lsResultBillCodeX);//暂时修改
					ArrayList<Map<String,Object>> lstResultBillCode = new ArrayList<Map<String,Object>>();
					lstResultBillCode.add(mapResultBillCode);
					
					SimpleAdapter listItemAdapter = new SimpleAdapter(StockBack.this,lstResultBillCode,//数据源   
			                android.R.layout.simple_list_item_1,       
			                new String[] {"BillCode"},
			                new int[] {android.R.id.text1}  
			            ); 
					new AlertDialog.Builder(StockBack.this).setTitle(R.string.DanJuBaoCunChengGong)
									.setAdapter(listItemAdapter, null)
									.setPositiveButton(R.string.QueRen,null).show();
					

					
					ClearScanDetail();
					
					
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
			}
			
		}
		// 清空订单表头信息
		private void ClearScanDetail() {
			
			ScanedBarcode = new ArrayList<String>();
			uploadGuid=null;
			ScanCount = 0;
			txtStockBackWHFrom.setText("");
			txtStockBackWHTo.setText("");
			txtStockBackBar.setText("");
			txtStockBackInvName.setText("");
			txtStockBackBatch.setText("");
			txtStockBackSeriNo.setText("");
			txtStockBackScanCount.setText("");
			
			txtStockBackPos.setText("");
			txtStockBackPos.requestFocus();
			m_mData = null;
			WhName = "";
			tmpPosCode = "";
			tmpStockBackWHFrom = "";
			tmpStockBackWHCode = "";
			tmpStockBackWHTo = "";
			SerialValues = null;
			jonsScan = null;
			
			if(file.exists())
			{
				file.delete();
			}
			
	 		if(fileScan.exists())
	 		{
	 			fileScan.delete();
	 		}

			tvInvCount.setText("已扫描0件");
			
			txtStockBackBar.setFocusableInTouchMode(true);

		}
		
		

private class ButtonOnClickD implements DialogInterface.OnClickListener {
	public int index;

	public ButtonOnClickD(int index) {
		this.index = index;
	}

	@Override
	public void onClick(@NonNull DialogInterface dialog, int whichButton) {
		if (whichButton >= 0) {
			index = whichButton;
			// dialog.cancel();
		} 
		else
		{
			 if (dialog.equals(DeleteButton)) 
			 {
				if (whichButton == DialogInterface.BUTTON_POSITIVE)
				{
					return;
				} 
				else if (whichButton == DialogInterface.BUTTON_NEGATIVE) 
				{
					// 这里进行数据删除处理
					ConfirmDelItem(index);
				}
			}
		}
	}
}
	
  	private void Exit()
  	{
		ExitNameList =new String[2];
		ExitNameList[0]="退出并保留缓存数据";
		ExitNameList[1]="退出并删除缓存数据";
		
		SelectButton=new AlertDialog.Builder(this).setTitle(R.string.QueRenTuiChu).setSingleChoiceItems(
				ExitNameList, -1, buttonOnClick).setPositiveButton(R.string.QueRen,
				buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
  	}
  	
  	private void SaveScanedBody()
    {
		
		if(ReScanBody.equals("0"))
    	{
    		return;
    	}
    	
    	if(ScanedBarcode == null || ScanedBarcode.size() < 1)
    	{
    		if(fileScan.exists())
    		{
    			fileScan.delete();
    		}
    		return;
	   	}
    	
    	String BillBarCode = "";
		
    	writeTxt = new writeTxt();
    	
    	//记录扫描数据
    	String ScanedBar = "";

    	
		for(int i=0;i<ScanedBarcode.size();i++)
		{
			
			if(i==ScanedBarcode.size()-1)
				BillBarCode = BillBarCode  + ScanedBarcode.get(i).toString();
			else
				BillBarCode = BillBarCode  + ScanedBarcode.get(i).toString()+ ",";
		}
		ScanedBar= BillBarCode;
		

		if(fileScan.exists())
		{
			fileScan.delete();
		}
		
		writeTxt.writeTxtToFile(ScanedBFileName,ScanedBar);
		
    }
  	
  //记录表头内容
    private void SaveScanedHead()
    {
    	
    	if(ReScanHead.equals("0"))
    	{
    		return;
    	}

    	
		
    	writeTxt = new writeTxt();
    	
    	//记录扫描数据
    	String ScanedWHCode = "";
    	String ScanedWHFrom = "";
    	String ScanedWHTo = "";
    	
    	String ScanedMode = "0";
    	
    	
    	
    	if(sStockBackS.isChecked())
    	{
    		ScanedMode = "1";
    	}
		
		if(this.tmpStockBackWHCode==null||tmpStockBackWHCode.equals(""))
		{
			ScanedWHCode= "null";
		}
		else
		{
			ScanedWHCode=tmpStockBackWHCode;
		}
    	
    	
    	
    	
		if(this.tmpStockBackWHFrom==null||tmpStockBackWHFrom.equals(""))
		{
			ScanedWHFrom= "null";
		}
		else
		{
			ScanedWHFrom=tmpStockBackWHFrom;
		}
		
		if(this.tmpStockBackWHTo==null||tmpStockBackWHTo.equals(""))
		{
			ScanedWHTo= "null";
		}
		else
		{
			ScanedWHTo=tmpStockBackWHTo;
		}
		
		
		if(file.exists())
		{
			file.delete();
		}
		
		writeTxt.writeTxtToFile(ScanedHFileName,ScanedWHCode+"|"+ScanedWHFrom+"|"+ScanedWHTo+"|"+ScanedMode);
		
    }
  	
  	private void ReScanHead()
    {
    	String res = ""; 
    	
    	
		if(!file.exists())
		{
			ReScanHead = "1";
			return;
		}
		 
        try { 
 
            FileInputStream fin = new FileInputStream(fileName); 
 
            int length = fin.available(); 
 
            byte[] buffer = new byte[length]; 
 
            fin.read(buffer); 
 
            res = EncodingUtils.getString(buffer, "UTF-8"); 
            res = res.replace("\r\n", "");
            fin.close(); 
            
            
            String[] val;
    		if(res.contains("|"))
    		{
    			ReScanHead = "0";
    			val = res.split("\\|");
    			

    			String WHCode = val[0];
    			String WHFrom = val[1];
    			String WHTo = val[2];
    			String Mode = val[3];
    			if(!WHCode.equals("null"))
    			{
    				this.tmpStockBackWHCode = WHCode;
    			}
    			if(!WHFrom.equals("null"))
    			{
    				this.txtStockBackWHFrom.setText(WHFrom);
    				this.tmpStockBackWHFrom = WHFrom;
    			}
    			if(!WHTo.equals("null"))
    			{
    				this.txtStockBackWHTo.setText(WHTo);
    				this.tmpStockBackWHTo = WHTo;
    			}
    			MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
          
            	this.txtStockBackPos.requestFocus();
            	if(Mode.equals("1"))
            	{
            		sStockBackS.setChecked(true);
            	}
            	
            	ReScanHead = "1";

    		}
        } 
 
        catch (Exception e) { 
 
            e.printStackTrace(); 
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

        } 
    	
    }
  	
  	private void ReScanBody()
    {
    	String res = ""; 
    	
    	
		if(ScanedBarcode.size()>0)
		{
			ReScanBody = "1";
			return;
		}
			
    	
		if(!fileScan.exists())
		{
			ReScanBody = "1";
			return;
		}
		 
        try { 
 
            FileInputStream fin = new FileInputStream(fileNameScan); 
 
            int length = fin.available(); 
 
            byte[] buffer = new byte[length]; 
 
            fin.read(buffer); 
 
            res = EncodingUtils.getString(buffer, "UTF-8"); 
            
            res = res.substring(0,res.length()-2);
            
            fin.close(); 
    			
			ArrayList<String> ScanedBillBar = new ArrayList<String>();
        	
        	String[] Bars;
        	if(res.contains(","))
    		{
        		ReScanBody = "0";
        		Bars = res.split("\\,");
    			
        		for(int i=0;i<Bars.length;i++)
        		{
        			ScanedBillBar.add(Bars[i]);
        		}
    		}
        	else
        	{
        		ReScanBody = "0";
        		ScanedBillBar.add(res);
        	}
        	
        	

        	if(ScanedBillBar.size()<1)
        	{
        		ReScanBody = "1";
    			return;
        	}
        	
        	int x=0;
        	
        	for(int i=0;i<ScanedBillBar.size();i++)
    		{
        		
        		if(x>10)
        		{
        			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        			Common.ReScanErr = true;
        			ReScanErr();
        			return;
        		}
        		
        		
        		String Key = ScanedBillBar.get(i).toString();
        		
    			String ScanedBar = "";
    			String ScanedPos = "";
        		
        		
        		String[] Keys;
            	if(Key.contains("!"))
        		{
            		ReScanBody = "0";
            		Keys = Key.split("\\!");
        			
            		ScanedBar = Keys[0];
            		ScanedPos = Keys[1];

        		}

        		
            	ScanPos(ScanedPos);
        		
    			ScanBarcode(ScanedBar);
    			
    			String OKflg = "ng";
    			
				if(ScanedBarcode == null || ScanedBarcode.size() < 1)							
				{
					x++;
					i--;
				}
				else
				{
					for(int j=0;j<ScanedBarcode.size();j++)
	    			{

	    				String AAA = ScanedBarcode.get(j);
	    				if (Key.equals(AAA))
	    				{
	    					OKflg = "ok";
	    				}
	    				
	    			}
	    			if(!OKflg.equals("ok"))
	    			{
	    				x++;
	    				i--;
	    			}
				}
    		}
        	
        	this.txtStockBackPos.requestFocus();
        	ReScanBody = "1";
        	
        } 
 
        catch (Exception e) { 
 
            e.printStackTrace(); 
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

        } 
    	
    }
  	
  	private void ReScanErr(){
  		 AlertDialog.Builder bulider = 
  					 new AlertDialog.Builder(this).setTitle(R.string.CuoWu).setMessage("数据加载出现错误"+"\r\n"+"退出该模块并且再次尝试加载");

  		 bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false).create().show();
  		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
  		 return;
  	}
  	
  	//退出按钮对话框事件
	@NonNull
	private DialogInterface.OnClickListener listenExit = new
  			DialogInterface.OnClickListener()
  	{
  		public void onClick(DialogInterface dialog,
  			int whichButton)
  		{
  			Common.ReScanErr = false;
  			finish();	
  			System.gc();
  		}
  	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_back, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
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

	@Nullable
	private static AlertDialog    SelectLine     = null;
	@NonNull
	private        buttonOnClickC buttonOnClickC = new buttonOnClickC(0);
	@NonNull
	static         String[]       LNameList      = new String[2];
	
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
		public void onClick(@NonNull DialogInterface dialog, int whichButton) {
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
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {		if (keyCode == KeyEvent.KEYCODE_MENU) 
		 	{//拦截meu键事件			//do something...	
		       return false;
			 }
         return keyCode != KeyEvent.KEYCODE_BACK;
     }
}
