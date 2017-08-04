package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.common.Common;
import com.techscan.dvq.login.MainLogin;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Sample_stocking extends Activity {

	ListView lvPDOrder;
    @Nullable
    SimpleAdapter lvDBOrderAdapter;
    @Nullable
    List<Map<String, Object>> lstPDOrder = null;

    @NonNull
    String[] from = { "PosName", "PosCode" };
    @NonNull
    int[]    to   = { R.id.listssposPosName, R.id.listssposPosCode };
	private writeTxt writeTxt ;		//保存LOG文件
    @Nullable
    private String[]      BillCodeList         = null;
    @NonNull
    private ButtonOnClick buttonOnClick        = new ButtonOnClick(0);
    @Nullable
    private AlertDialog   BillCodeSelectButton =null;
    @NonNull
            String        ReOpenBillCode       = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_stocking);

		// 创建控件
		this.setTitle("抽样盘点");

		btnSampleScan = (Button) findViewById(R.id.btnSampleScan);
		btnSampleExit = (Button) findViewById(R.id.btnSampleExit);
		btnSampleSave = (Button) findViewById(R.id.btnSampleSave);
		btnSampleReOpen = (Button) findViewById(R.id.btnSampleReOpen);

		btnOrderNo = (ImageButton) findViewById(R.id.btnSampleOrderNoBrower);
		btnWarehouseBrower = (ImageButton) findViewById(R.id.btnSampleWarehouse);
		// btnLocationBrower =
		// (ImageButton)findViewById(R.id.btnSampleLocation);

		txtOrderNo = (EditText) findViewById(R.id.txtSampOrderNo);
		txtWarehouse = (EditText) findViewById(R.id.txtSampleWarehouse);
		txtLocation = (EditText) findViewById(R.id.txtSampleLoacation1);
		txtSampleCom = (EditText) findViewById(R.id.txtSampleCom);

		btnSampleScan.setOnClickListener(myListner);
		btnSampleExit.setOnClickListener(myListner);
		btnSampleSave.setOnClickListener(myListner);
		btnSampleReOpen.setOnClickListener(myListner);

		lvPDOrder = (ListView) findViewById(R.id.listssposshow);
		lstPDOrder = new ArrayList<Map<String, Object>>();

		//this.txtLocation.addTextChangedListener(watchers);
		txtLocation.setOnKeyListener(myTxtListener);

		btnOrderNo.setOnClickListener(myListner);
		btnWarehouseBrower.setOnClickListener(myListner);
		// btnLocationBrower.setOnClickListener(myListner);
		
		btnSampleScan.setFocusable(false);
		btnSampleExit.setFocusable(false);
		btnSampleSave.setFocusable(false);
		btnSampleReOpen.setFocusable(false);

		btnWarehouseBrower.setFocusable(false);
		btnOrderNo.setFocusable(false);
		txtWarehouse.setFocusableInTouchMode(false);
		txtSampleCom.setFocusableInTouchMode(false);
		txtSampleCom.setText(MainLogin.objLog.CompanyName);

		// ADD CAIXY START
//		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		// ADD CAIXY END
		txtOrderNo.setFocusableInTouchMode(false);
		txtWarehouse.setFocusableInTouchMode(false);
		txtSampleCom.setFocusableInTouchMode(false);
		lvPDOrder.setOnItemLongClickListener(myListItemLongListener);
		lvPDOrder.setOnItemClickListener(myListItemListener);

	}

    @NonNull
    private OnItemLongClickListener myListItemLongListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			Map<String, Object> mapCurrent = lstPDOrder
					.get(arg2);
			String PosCode = mapCurrent.get("PosCode").toString();
			String PosName = mapCurrent.get("PosName").toString();
			try {
				ShowPosScanNum(PosCode,PosName);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

	};

    @NonNull
    private OnItemClickListener myListItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Map<String, Object> mapCurrent = lstPDOrder
					.get(arg2);

			String PosCode = mapCurrent.get("PosCode").toString();

			setLocationTxt(PosCode);
			return;
		}

	};

	public void setLocationTxt(String PosCode) {

		this.txtLocation.setText(PosCode);
		this.m_LocationID = txtLocation.getText().toString().toUpperCase();

	}

    @NonNull
    private OnKeyListener myTxtListener = new OnKeyListener() {
		@Override
		public boolean onKey(@NonNull View v, int arg1, @NonNull KeyEvent arg2) {
			switch (v.getId()) {
			case R.id.txtSampleLoacation1:
				if (arg1 == arg2.KEYCODE_ENTER
						&& arg2.getAction() == KeyEvent.ACTION_UP) {
					try {
						try {
							StartScan();
							txtLocation.requestFocus();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// txtInPos.setText(m_InPosName);
					} catch (ParseException e) {
						txtLocation.setText("");
						txtLocation.requestFocus();
						Toast.makeText(Sample_stocking.this, e.getMessage(),
								Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						txtLocation.setText("");
						txtLocation.requestFocus();
						Toast.makeText(Sample_stocking.this, e.getMessage(),
								Toast.LENGTH_LONG).show();

					}
					try {
						ShowPosInfo();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return true;
		}
	};

    @NonNull
    private TextWatcher watchers = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			// m_OutPosID="";
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sample_stocking, menu);
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

	Button btnSampleScan;
	Button btnSampleExit;
	Button btnSampleSave;
	Button btnSampleReOpen;

	EditText txtOrderNo;
	EditText txtWarehouse;
	EditText txtSampleCom;
	EditText txtLocation;

	ImageButton btnWarehouseBrower;
	// ImageButton btnLocationBrower;
	ImageButton btnOrderNo;
	// 注册控件

	String m_OrderNo=""; // 盘点单号
	String m_OrderID="";
	boolean m_StartScan = false;
	String m_warehouseID;
	String m_LocationID;

	String sWhCode2 = ""; // 现实用的仓库编码
    @NonNull
    String sWhName = ""; // 现实用的仓库编码
	String sWhCode = ""; // 查询用的仓库编码
    @Nullable
    ArrayList IDList = null;

//	private SoundPool sp;// 声明一个SoundPool
//	private int MainLogin.music;// 定义一个int来设置suondID

	private void GetOrderNo() {
		Intent sampList = new Intent(this, Sample_stocking_OrderList.class);
		startActivityForResult(sampList, 91);

	}
	
	private void OpenBillList() {
		 if (!this.m_OrderID.equals("")) {
			 AlertDialog.Builder bulider = new AlertDialog.Builder(this)
			 .setTitle(R.string.XunWen).setMessage("已经开始一个盘点,确认需要选择新的盘点单?");
			 bulider.setNegativeButton(R.string.QuXiao, null);
			 bulider.setPositiveButton(R.string.QueRen, listenOpen).create().show();
		 }
		 else
		 {
				SetNewFrm();
				GetOrderNo();
		 }

	}

    @Nullable
    ArrayList<HashMap<String, String>> array           = null;
    @Nullable
    SimpleAdapter                      listItemAdapter = null;
    @Nullable
    UUID uploadGuid;

    @NonNull
    private DialogInterface.OnClickListener listenExit = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			finish();
			System.gc();
		}
	};



    @NonNull
    private DialogInterface.OnClickListener listenOpen = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			
			SetNewFrm();
			GetOrderNo();
		}
	};
	
	private void SetNewFrm() {
		this.m_OrderID = "";
		this.m_OrderNo = "";
		sWhCode2 = ""; // 现实用的仓库编码
		sWhName = "";
		sWhCode = ""; // 查询用的仓库编码
		lvPDOrder.setAdapter(null);
		this.txtWarehouse.setText("");
		txtSampleCom.setText(MainLogin.objLog.CompanyName);
		this.txtOrderNo.setText("");
		this.txtLocation.setText("");
		ReOpenBillCode="";
		IDList = null;
		this.btnWarehouseBrower.setEnabled(true);
	}
	
	private void ToScan() {

		if (txtWarehouse.getText() == null
				|| txtWarehouse.getText().toString().equals("")) {
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			Toast.makeText(this, "请确认盘点仓库", Toast.LENGTH_LONG).show();
			return;
		}
		if (m_LocationID == null
				|| m_LocationID.equals("")) {
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			Toast.makeText(this, "请确认盘点货位", Toast.LENGTH_LONG).show();
			return;
		}
		Intent sampleDetail = new Intent(this, Sample_stokcing_detail.class);
		sampleDetail.putExtra("OrderNo", this.m_OrderNo);
		sampleDetail.putExtra("OrderID", this.m_OrderID);
		sampleDetail.putExtra("locationID", m_LocationID);
		
		this.txtLocation.setText("");
		ReOpenBillCode="";
		
		startActivityForResult(sampleDetail, 92);
	}

	// 开始扫描
	private void StartScan() throws JSONException, ParseException, IOException {
		if (txtWarehouse.getText() == null
				|| txtWarehouse.getText().toString().equals("")) {
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			Toast.makeText(this, "请确认盘点仓库", Toast.LENGTH_LONG).show();
			txtLocation.setText("");
			return;
		}
		
		// 调用创建盘点单
		if (array != null) {
			array.removeAll(array);
		}

		if (uploadGuid == null) {
			uploadGuid = UUID.randomUUID();
		}

		JSONObject result = null;
		JSONObject para = new JSONObject();

		para.put("FunctionName", "StartStockTacking");
		para.put("TableName", "OrderNo");
		para.put("BinCode", txtLocation.getText().toString().toUpperCase());
		para.put("CreateUser", MainLogin.objLog.UserID.replace("\n", ""));
		para.put("CompanyCode", MainLogin.objLog.CompanyCode);
		para.put("WhCode", sWhCode.toUpperCase());
		para.put("sWhName", sWhName);
		para.put("Guid", uploadGuid);
		if (this.m_OrderID == null || this.m_OrderID.equals("")) {
			para.put("BillID", "");
		} else {
			para.put("BillID", this.m_OrderID);
		}
		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
		result = MainLogin.objLog.DoHttpQuery(para, "CommonQuery", "");
		if (result == null) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}

		if (!result.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
		if (result.getBoolean("Status")) {

			this.m_StartScan = true;
			uploadGuid = null;
			this.m_LocationID = txtLocation.getText().toString().toUpperCase();

			if (this.m_OrderID == null || this.m_OrderID.equals("")) {
				String temp = result.getString("BillCode");
				String[] val;
				val = temp.split("\\|");
				m_OrderNo = val[1].toString();
				m_OrderID = val[0].toString();
				this.txtOrderNo.setText(m_OrderNo);
				txtSampleCom.setText(MainLogin.objLog.CompanyName);
				this.btnWarehouseBrower.setEnabled(false);
				writeTxt = new writeTxt();
		   		
				Date day = new Date(System.currentTimeMillis());//获取当前时间
	  
		   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		   		
		   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
		   		
		   		String BillCode = this.m_OrderNo;
		   		String BillType = "ST";
		   		String UserID = MainLogin.objLog.UserID;
		   		
		   		String LogName = BillType + UserID + dfd.format(day)+".txt";
		   		String LogMsg = df.format(day) + " " + "A" + " " + BillCode; 
		   		writeTxt.writeTxtToFile(LogName,LogMsg);
			}
		} else {
			Toast.makeText(this, "输入的货位不正确", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			this.m_LocationID = "";
			this.txtLocation.setText("");
			return;
		}
	}

	// 呼叫仓库窗体
	private void ShowWarehouseDig() {
		WarehouseMultilist cWarehouseMultilist = new WarehouseMultilist();
		Intent ViewGrid = new Intent(this, cWarehouseMultilist.getClass());

		ViewGrid.putExtra("slocalid", IDList);
		startActivityForResult(ViewGrid, 93);
	}

	// 关闭盘点单
	private void ClosePDOrder() {
		if (!this.m_OrderNo.equals("")) {
			AlertDialog.Builder bulider = new AlertDialog.Builder(this)
					.setTitle(R.string.XunWen).setMessage("您确认要关闭本次盘点吗？");
			bulider.setNegativeButton(R.string.QuXiao, null);
			bulider.setPositiveButton(R.string.QueRen, listenClose).create().show();
		}
	}

	
	private void ReOpenBill() {
		
		ReOpenBillCode = "";
		JSONObject para = new JSONObject();
		try {
			para.put("TableName","OrderList");
			para.put("FunctionName", "GetTS_StoktackingyList");
			para.put("BillCode", "");
			para.put("UserID", MainLogin.objLog.UserID);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
			else
			{				
				JSONArray jsarray = jas.getJSONArray("OrderList");
				BillCodeList =new String[jsarray.length()];//设置仓库名字类型数量

				String UserName = "";
				for (int i = 0; i < jsarray.length(); i++) {

					JSONObject tempJso = jsarray.getJSONObject(i);

					String BillCode = tempJso.getString("pd_order");

					BillCodeList[i]=BillCode;

				}
				showBillCodeChoiceDialog();
				
				
			}
			

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
	private void showBillCodeChoiceDialog()
	{   

//		SelectButton=new AlertDialog.Builder(this).setTitle("选择仓库").setSingleChoiceItems(
//				warehouseNameList, 0, buttonOnClick).setPositiveButton("确认",
//				buttonOnClick).setNegativeButton("取消", buttonOnClick).show();
		
		
		BillCodeSelectButton=new AlertDialog.Builder(this).setTitle("选择再开单据").setSingleChoiceItems(
				//BillCodeList, -1, buttonOnClick).setNegativeButton("取消", buttonOnClick).show();
				BillCodeList, -1, buttonOnClick).setPositiveButton(R.string.QueRen,
				buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
	}
	
	private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		public int index;

		public ButtonOnClick(int index)
		{
			this.index = index;
		}
		
		public void onClick(@NonNull DialogInterface dialog, int whichButton)
		{
			if (whichButton >= 0)
			{
				index = whichButton;
				//dialog.cancel(); 				
			}
			else if(whichButton ==-1)
			{
				try {
					OpenPDMethod();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				return;
			}
			
			if(dialog.equals(BillCodeSelectButton))
			{
				ReOpenBillCode = BillCodeList[index].toString();
			}
		}
	}
//	private void SelectBill(String BillCode) throws ParseException, IOException {
//
//			if (!this.m_OrderNo.equals("")) {
//				AlertDialog.Builder bulider = new AlertDialog.Builder(this)
//						.setTitle("询问").setMessage("您确认要再开该盘点单吗?"+"\r\n"+"单据号："+BillCode);
//				bulider.setNegativeButton("确认", listenReOpen);
//				bulider.setPositiveButton(R.string.QuXiao, null).create().show();
//			}
//		
//		
//	}
	
	
	private void ShowPosInfo() throws ParseException, IOException {
		try {
			JSONObject result = null;
			JSONObject para = new JSONObject();
			para.put("FunctionName", "ShowSSPos");
			para.put("OrderID", m_OrderID);
			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			result = MainLogin.objLog.DoHttpQuery(para, "ShowSSPos", "A");
			if (result == null) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (!result.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			if (result.getBoolean("Status")) {
				JSONArray jsarray = result.getJSONArray("OrderList");
				lstPDOrder = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < jsarray.length(); i++) {

					JSONObject tempJso = jsarray.getJSONObject(i);
					HashMap map = new HashMap<String, Object>();

					String lsPosCode = tempJso.getString("poscode");
					
					map.put("PosName", tempJso.getString("posname"));
					map.put("PosCode", lsPosCode);
					if(lstPDOrder.size()==0)
					{
						lstPDOrder.add(map);
					}
					else
					{
						String FindPos = "";
						for (int x = 0; x < lstPDOrder.size(); x++) {
							
							Map<String, Object> mapCurrent = lstPDOrder.get(x);

							String PosCode = mapCurrent.get("PosCode").toString();
							if(lsPosCode.equals(PosCode))
							{
								FindPos = "1";
							}
						}
						if(FindPos.equals(""))
						{
							lstPDOrder.add(map);
						}
					}
				}

				lvDBOrderAdapter = new SimpleAdapter(Sample_stocking.this,
						lstPDOrder, R.layout.listsspos, from, to);
				lvPDOrder.setAdapter(lvDBOrderAdapter);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void ShowPosScanNum(String PosCode,String PosName) throws ParseException, IOException {
		try {
			JSONObject result = null;
			JSONObject para = new JSONObject();
			para.put("FunctionName", "SSPosScanNum");
			para.put("PosCode", PosCode);
			para.put("OrderID", m_OrderID);
			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			result = MainLogin.objLog.DoHttpQuery(para, "SSPosScanNum", "A");
			if (result == null) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (!result.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			if (result.getBoolean("Status")) {
				JSONArray jsarray = result.getJSONArray("OrderList");

				JSONObject tempJso = jsarray.getJSONObject(0);
						
				String nnum = tempJso.getString("nnum");
				String nscannum = tempJso.getString("nscannum");

				AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
						"货位信息").setMessage(PosName+"\r\n"+"\r\n"+"现存量："+nnum+"件"+"\r\n"+"已扫描："+nscannum+"件");
				bulider.setPositiveButton(R.string.QueRen, null).create().show();



			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void OpenPDMethod() throws ParseException, IOException {
		try {
			JSONObject result = null;
			JSONObject para = new JSONObject();
			para.put("FunctionName", "ReOpenStockTaking");
			para.put("CreateUser", MainLogin.objLog.UserID);
			para.put("OrderCode", this.ReOpenBillCode);
			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			result = MainLogin.objLog
					.DoHttpQuery(para, "ReOpenStockTaking", "A");
			if (result == null) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (!result.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			if (result.getBoolean("Status")) {
				
				AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
						"单据再开成功").setMessage(this.ReOpenBillCode);
				bulider.setPositiveButton(R.string.QueRen, null).create().show();
				SetNewFrm();
			}
			else
			{
				Toast.makeText(this, result.getString("ErrMsg"), Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ClosePDMethod() throws ParseException, IOException {
		try {
			JSONObject result = null;
			JSONObject para = new JSONObject();
			para.put("FunctionName", "CloseStockTaking");
			para.put("CreateUser", MainLogin.objLog.UserID);
			para.put("OrderID", m_OrderID);
			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			result = MainLogin.objLog
					.DoHttpQuery(para, "CloseStockTaking", "A");
			if (result == null) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (!result.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			if (result.getBoolean("Status")) {
				
				AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
						"保存成功").setMessage(this.m_OrderNo);
				bulider.setPositiveButton(R.string.QueRen, null).create().show();
				SetNewFrm();
			}
			else
			{
				Toast.makeText(this, result.getString("ErrMsg"), Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 取消按钮对话框事件
    @NonNull
    private DialogInterface.OnClickListener listenClose = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			try {
				ClosePDMethod();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
	};
	
	

	// 返回
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == 93)// 仓库查询返回
		{
			if (data != null) {
				Bundle bundle = data.getExtras();
				IDList = data.getStringArrayListExtra("slocalid");

				SerializableList serializableList = (SerializableList) bundle
						.get("resultinfo");
				List<Map<String, Object>> resultList = serializableList
						.getList();
				sWhCode = "";
				sWhCode2 = "";
				sWhName = "";
				for (int i = 0; i < resultList.size(); i++) {

					if (sWhCode.equals("")) {
						sWhCode = sWhCode
								+ "'"
								+ ((resultList).get(i))
										.get("storcode") + "'";
						sWhCode2 = sWhCode2
								+ ((resultList).get(i))
										.get("storcode");
						sWhName = sWhName
								+ "'"
								+ ((resultList).get(i))
										.get("storname") + "'";
					} else {
						sWhCode = sWhCode
								+ ",'"
								+ ((resultList).get(i))
										.get("storcode") + "'";
						sWhCode2 = sWhCode2
								+ ","
								+ ((resultList).get(i))
										.get("storcode");
						sWhName = sWhName
								+ ",'"
								+ ((resultList).get(i))
										.get("storname") + "'";
					}

				}
				this.txtWarehouse.setText(sWhCode2);
				this.txtOrderNo.requestFocus();
			}
		}
		if (requestCode == 91) {
			// 获得单据ID
			if (data != null) {
				Bundle bundle = data.getExtras();
				// String orderNo = bundle.getString("orderNo");
				// String orderID = bundle.getString("ID");
				// String warehouseName = bundle.getString("Warehouse");
				this.m_OrderID = bundle.getString("ID");
				this.m_OrderNo = bundle.getString("orderNo");
				this.sWhCode = bundle.getString("Warehouse");
				this.sWhCode2 = bundle.getString("Warehouses2");
				
				
				this.txtWarehouse.setText(sWhCode2);
				this.txtOrderNo.setText(m_OrderNo);
				txtSampleCom.setText(bundle.getString("SampleCom"));

				this.m_StartScan = true;
				try {
					ShowPosInfo();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.btnWarehouseBrower.setEnabled(false);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		System.gc();
	}

	// 按钮事件
    @NonNull
    private Button.OnClickListener myListner = new Button.OnClickListener() {
		@Override
		public void onClick(@NonNull View v) {
			switch (v.getId()) {
			case R.id.btnSampleOrderNoBrower:
				OpenBillList();
				break;
			case R.id.btnSampleScan:
				try {
					ToScan();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case R.id.btnSampleWarehouse:
				ShowWarehouseDig();
				break;
			case R.id.btnSampleExit:
				Exit();
				break;
			case R.id.btnSampleSave:
				ClosePDOrder();
				break;
			case R.id.btnSampleReOpen:
				ReOpenBill();
				break;
			}
			
		}
	};

	private void Exit() {
		AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
				R.string.XunWen).setMessage(R.string.NiQueDingYaoTuiChuMa+"\r\n"+"扫描数据已经被保存。");
		bulider.setNegativeButton(R.string.QuXiao, null);
		bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();

	}

}
