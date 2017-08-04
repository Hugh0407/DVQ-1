package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

public class PdOrderMultilist extends Activity {
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//ADD CAIXY TEST END 

    @Nullable
    String            InOutFlag     = null;
    @Nullable
    TextView          tvMSelCount   = null;
    @Nullable
    ListView          lvPDMultiList = null;
    @Nullable
    Button            btnMListAll   = null;
    @Nullable
    Button            btnMListClear = null;
    @Nullable
    Button            btnMListOK    = null;
	//Button btnMListReturn = null;
    @Nullable
    ArrayList<String> listStr       = null;
    @Nullable
    private List<Map<String, Object>> mData;
    @Nullable
    private Handler                       handler   = null;
    @Nullable
    private List<HashMap<String, Object>> MultiList = null;
    @Nullable
    private MyAdapter adapter;
    @Nullable
    private Adapter                   ItemAdapter              = null;
    @Nullable
    private Map<String,Object>        ItemMap                  = null;
    @NonNull
    private Map<String,Object>        ResultMap                = new ArrayMap<String, Object>();
    @NonNull
    private List<Map<String, Object>> ResultList               = new ArrayList<Map<String, Object>>();
    private String                    fsAccIDFlag              = "";
    @Nullable
            Button                    btPdOrderMultilistReturn =null;
        

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pd_order_multilist);
		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle("调拨订单明细");
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		//actionBar.setDisplayHomeAsUpEnabled(true);
//		//actionBar.setDisplayShowHomeEnabled(true);
//		actionBar.show();
		btPdOrderMultilistReturn = (Button)findViewById(R.id.btPdOrderMultilistReturn);
		btPdOrderMultilistReturn.setOnClickListener(ButtonOnClickListener);
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		fsAccIDFlag = this.getIntent().getStringExtra("AccIDFlag");
		InOutFlag = this.getIntent().getStringExtra("InOutFlag");
		
		tvMSelCount = (TextView)findViewById(R.id.tvMSelCount);
		lvPDMultiList = (ListView)findViewById(R.id.pdmultilist);
		btnMListClear = (Button)findViewById(R.id.btnMListClear);
		btnMListOK = (Button)findViewById(R.id.btnMListOK);
		if(InOutFlag.equals("Out"))
		{
			actionBar.setTitle("调拨订单明细");
		}
		else
		{
			actionBar.setTitle("调拨出库单明细");
		}
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()  
                .detectNetwork()
                .penaltyLog()  
                .build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
				detectLeakedClosableObjects().penaltyLog().penaltyDeath().build()); 

		showCheckBoxListView();
		
		//确定按钮  
		btnMListOK.setOnClickListener(new OnClickListener(){  
            @Override  
            public void onClick(View arg0) { 
            	if(ResultList==null || ResultList.size()<1)
            	{
            		Toast.makeText(PdOrderMultilist.this, "没有选择项目", Toast.LENGTH_LONG).show();
        			//ADD CAIXY TEST START
        			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        			//ADD CAIXY TEST END
            		return;
            	}
            	Intent intent = new Intent();                
                /*SerializableMap tmpmap=new SerializableMap();  
                tmpmap.setMap(ResultMap);
                Bundle ResultBundle = new Bundle();
                ResultBundle.putSerializable("resultinfo", tmpmap);
                intent.putExtras(ResultBundle);// 把返回数据存入Intent */
            	
            	SerializableList tmplist = new SerializableList();
            	tmplist.setList(ResultList);
            	Bundle ResultBundle = new Bundle();
                ResultBundle.putSerializable("resultinfo", tmplist);
                intent.putExtras(ResultBundle);
                PdOrderMultilist.this.setResult(1, intent);
                finish();
            }  
        });
		
		//返回按钮
		//btnMListReturn.setOnClickListener(new OnClickListener(){  
            //@Override  
            //public void onClick(View arg0) { 
             //   finish();
            //}  
        //});
		
		//全选按钮
//		btnMListAll.setOnClickListener(new OnClickListener(){  
//            @Override  
//            public void onClick(View arg0) {            	
//            	adapter.checkAll();
//            }  
//        });
		
		//全删除按钮
		btnMListClear.setOnClickListener(new OnClickListener(){  
            @Override  
            public void onClick(View arg0) {            	
            	adapter.noCheckAll();
            	ResultList.clear();
            	listStr.clear();
            	tvMSelCount.setText("已选中0项");
            }  
        });
		
	}


    @NonNull
    private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(@NonNull View v)
		{
			switch(v.getId())
  			{			//btnSDScanReturn
	  			case id.btPdOrderMultilistReturn:
	  				finish();					
					break;
  			}
		}	    	
    };
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.pd_order_multilist, menu);
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
		//返回
		if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
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
		JSONArray jsarray= jas.getJSONArray("dbHead");
		
		
		
		if(InOutFlag.equals("Out"))
		{
			for(int i = 0;i<jsarray.length();i++)
			{
				tempJso = jsarray.getJSONObject(i);
				map = new HashMap<String, Object>();
				map.put("No", tempJso.getString("vcode"));
				map.put("From", tempJso.getString("cwarehousename") + "     ");
				map.put("To",tempJso.getString("cotherwhname"));
				map.put("BillId",tempJso.getString("cbillid"));
				map.put("AccID", tempJso.getString("accid"));
				map.put("OrgId", tempJso.getString("coutcbid"));
				map.put("companyID", tempJso.getString("coutcorpid"));
				map.put("warehouseID", tempJso.getString("coutwhid"));
				map.put("warehouseToID", tempJso.getString("cinwhid"));
				
				//保存用表头JSONObject设置---开始
				map.put("pk_corp", tempJso.getString("coutcorpid"));
				
				if(tempJso.getString("accid").equals("A"))
					map.put("coperatorid", MainLogin.objLog.UserID);//操作者
				else
					map.put("coperatorid", MainLogin.objLog.UserIDB);//操作者
				
				map.put("pk_calbody", tempJso.getString("coutcbid"));
				map.put("pk_stordoc", tempJso.getString("coutwhid"));
				map.put("fallocflag", tempJso.getString("fallocflag"));
				map.put("cbiztypeid", tempJso.getString("cbiztypeid"));
				map.put("pk_instordoc", tempJso.getString("cinwhid"));
				map.put("pk_incalbody", tempJso.getString("cincbid"));
				map.put("pk_incorp", tempJso.getString("cincorpid"));
				map.put("vcode", tempJso.getString("vcode"));
				//ADD BY WUQIONG START
				map.put("vdef1", tempJso.getString("vdef1"));
				//ADD BY WUQIONG END
				if(tempJso.getString("coutcorpid").equals(tempJso.getString("cincorpid")))
				{
					map.put("Dcorp", "  ");
				}
				else
				{
					map.put("Dcorp", "跨");
				}
				//DCorp夸公司标识
				
				//保存用JSONObject设置---结束 
				map.put("status", tempJso.getString("status"));
				//增加退
				String billstatus = tempJso.getString("status");
				if(billstatus.equals("Y"))
				{
					map.put("statusE", "  ");
				}
				else
				{
					map.put("statusE", "退");
				}
				
				list.add(map);
			}
		}
		else
		{
			for(int i = 0;i<jsarray.length();i++)
			{
				tempJso = jsarray.getJSONObject(i);
				map = new HashMap<String, Object>();
				map.put("No", tempJso.getString("vbillcode"));
				map.put("From", tempJso.getString("cwarehousename") + "     ");
				map.put("To",tempJso.getString("cotherwhname"));
				map.put("BillId",tempJso.getString("cgeneralhid"));
				map.put("AccID", tempJso.getString("AccID"));
				map.put("OrgId", tempJso.getString("cothercalbodyid"));
				map.put("companyID", tempJso.getString("cothercorpid"));
				map.put("warehouseID", tempJso.getString("cwarehouseid"));
				map.put("warehouseToID", tempJso.getString("cotherwhid"));
				
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
				if(tempJso.getString("cothercorpid").equals(tempJso.getString("pk_corp")))
				{
					map.put("Dcorp", "  ");
				}
				else
				{
					map.put("Dcorp", "跨");
				}
				map.put("status", tempJso.getString("status"));
				//保存用JSONObject设置---结束
				//增加退
				String billstatus = tempJso.getString("status");
				if(billstatus.equals("Y"))
				{
					map.put("statusE", "  ");
				}
				else
				{
					map.put("statusE", "退");
				}
				list.add(map);
			}
		}
		
		
		return list;
	}
	
	/*private ListView.OnItemClickListener itemListener = new
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
			String orderNo = map.get("No").toString();
			String billId=map.get("BillId").toString();
			String AccId=map.get("AccID").toString();
             
			String OrgId=map.get("OrgId").toString();
			String companyID=map.get("companyID").toString();
			String vCode=map.get("No").toString();
			String warehouseID=map.get("warehouseID").toString();
             
            Intent intent = new Intent();
            intent.putExtra("result", orderNo);// 把返回数据存入Intent  
            intent.putExtra("BillId", billId);
            intent.putExtra("AccID", AccId);
            intent.putExtra("OrgId", OrgId);
            intent.putExtra("companyID", companyID);
            intent.putExtra("vcode", vCode);
            intent.putExtra("warehouseID", warehouseID);
             
            PdOrderMultilist.this.setResult(1, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理      
            //PdOrderMultilist.this.finish();// 关闭子窗口ChildActivity 
             
            //Toast.makeText(PdOrderList.this, errMsg, Toast.LENGTH_SHORT).show();
             
		}
	};*/
	
	// 显示带有checkbox的listview  
    public void showCheckBoxListView() {  
    	MultiList = new ArrayList<HashMap<String, Object>>();
    	JSONObject para = new JSONObject();
		try {
			para.put("BillCode", "null");
			para.put("Wh-CodeA", MainLogin.objLog.WhCodeA);
			para.put("Wh-CodeB", MainLogin.objLog.WhCodeB);
			
			
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
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
			if(InOutFlag.equals("Out"))
			{
		        if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return ;
		        }
				jas = Common.DoHttpQuery(para, "GetAdjustOrderBillHead", "");
			}
			else
			{
		        if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return ;
		        }
				jas = Common.DoHttpQuery(para, "GetAdjustOutBillHead", "");
			}
			
		} catch (Exception ex)
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END

			return;
		}
		catch (Exception ex)
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END

			return;
		}        
        //list.addHeaderView()        
        //list.setOnItemClickListener((OnItemClickListener) itemListener);--暂时注销	
        adapter = new MyAdapter(this, mData, R.layout.vlistmultipds,  
        		//增加退
        		//new String[] {"No", "From", "To", "AccID", "Dcorp"},  
        		//new int[] {R.id.mlistpdorder,R.id.mlistfromware,R.id.mlisttoware,R.id.mlistaccid,R.id.mlistdcorp}  
        		new String[] {"No", "From", "To", "AccID", "Dcorp","statusE"},  
        		new int[] {R.id.mlistpdorder,R.id.mlistfromware,R.id.mlisttoware,R.id.mlistaccid,R.id.mlistdcorp,R.id.mlistbillstatus}  

        		); 
        
        lvPDMultiList.setAdapter(adapter); 
        listStr = new ArrayList<String>(); 
                
        lvPDMultiList.setOnItemClickListener(new OnItemClickListener() {  

            @Override  
            public void onItemClick(@NonNull AdapterView<?> arg0, @NonNull View view,
                                    int position, long arg3)
            {            	
            	ViewHolder holder = (ViewHolder) view.getTag();                
                ItemAdapter=arg0.getAdapter();
            	ItemMap=(Map<String, Object>) ItemAdapter.getItem(position);
            	
        		if(InOutFlag.equals("Out"))
        		{
        			String lsAccid = ItemMap.get("AccID").toString();
    				String lsFromWH = ItemMap.get("warehouseID").toString();
    				String lspk_corp = ItemMap.get("pk_corp").toString();
    				
    				if(!Common.CheckUserRole(lsAccid, lspk_corp, "40080820"))
    				{
    					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    					Toast.makeText(PdOrderMultilist.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG).show();
    					return;
    				}
    				

    				if(!Common.CheckUserWHRole(lsAccid, lspk_corp, lsFromWH))
    				{
    				 	MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    				 	Toast.makeText(PdOrderMultilist.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian, Toast.LENGTH_LONG).show();
    				 	return;
    				}
        		}
        		else
        		{
        			String lsAccid = ItemMap.get("AccID").toString();
    				String lsFromWH = ItemMap.get("warehouseToID").toString();
    				String lspk_corp = ItemMap.get("pk_corp").toString();
    				
    				if(!Common.CheckUserRole(lsAccid, lspk_corp, "40080618"))
    				{
    					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    					Toast.makeText(PdOrderMultilist.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG).show();
    					return;
    				}
    				

    				if(!Common.CheckUserWHRole(lsAccid, lspk_corp, lsFromWH))
    				{
    				 	MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    				 	Toast.makeText(PdOrderMultilist.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian, Toast.LENGTH_LONG).show();
    				 	return;
    				}
        		}
				
            	
            	//比较数据的出库仓库和入库仓库是否和之前的数据一致，一致才可以被选择
            	if(ResultList.size() > 0)
            	{
            		Map<String,Object> SelectedItemMap = ResultList.get(0);
            		if(!SelectedItemMap.get("From").toString().equals(ItemMap.get("From").toString())
            				|| !SelectedItemMap.get("To").toString().equals(ItemMap.get("To").toString()))
            		{
            			Toast.makeText(PdOrderMultilist.this, 
            					"选择的单据号的出库仓库或入库仓库和之前选择的不一致", 
            					Toast.LENGTH_LONG).show(); 
            			//ADD CAIXY TEST START
            			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            			//ADD CAIXY TEST END
            			return;
	            	}
            		
            		
            		if(InOutFlag.equals("Out"))
            		{
            			if ((!SelectedItemMap.get("pk_corp").toString().equals(ItemMap.get("pk_corp").toString())) || (!SelectedItemMap.get("pk_incorp").toString().equals(ItemMap.get("pk_incorp").toString())))
				        {
				          Toast.makeText(PdOrderMultilist.this, "选择的单据号的出库公司或入库公司和之前选择的不一致", Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
				        }
            		}
            		else
            		{
            			if ((!SelectedItemMap.get("pk_corp").toString().equals(ItemMap.get("pk_corp").toString())) || (!SelectedItemMap.get("pk_outcorp").toString().equals(ItemMap.get("pk_outcorp").toString())))
	                    {
	                      Toast.makeText(PdOrderMultilist.this, "选择的单据号的出库公司或入库公司和之前选择的不一致", Toast.LENGTH_LONG).show();
	                      	//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							return;
	                    }
            		}
            		
            		if(!SelectedItemMap.get("status").toString().equals(ItemMap.get("status").toString()))
					{
						Toast.makeText(PdOrderMultilist.this, 
								"选择的单据号的正负结存和之前选择的不一致", 
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						return;
					}
            		
//            		if(!SelectedItemMap.get("AccID").toString().equals(ItemMap.get("AccID").toString()))
//					{
//						Toast.makeText(PdOrderMultilist.this, 
//								"选择的单据号的账套号和之前选择的不一致", 
//								Toast.LENGTH_LONG).show();
//						//ADD CAIXY TEST START
//						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//						//ADD CAIXY TEST END
//						return;
//					}
            	}
	            	
            	holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态  
                MyAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态  
            	
                if (holder.cb.isChecked() == true) 
                {
                	//ResultMap.putAll(ItemMap);
                	ResultList.add(ItemMap);
                	listStr.add(holder.tvpdorder.getText().toString());
                	//view.setBackgroundColor(Color.parseColor("#33b5e5"));
                } 
                else 
                {  
                	ResultList.remove(ItemMap);
                	listStr.remove(holder.tvpdorder.getText().toString());
                	//view.setBackgroundColor(Color.TRANSPARENT);
                }
                
                
                
                tvMSelCount.setText("已选中"+listStr.size()+"项");
            }

        });
        
      //判断是否要被选中
        Map<String,Object> PDMultiMap=null;
        Map<String,Object> SelectedMap=null;
        String PDMultiorderNo;
		String PDMultibillId;
		String PDMultiAccId;
		String SelectedorderNo;
		String SelectedbillId;
		String SelectedAccId;
		if(Data.getSelListAdapter()==null)
			return;
        for(int i=0;i<adapter.getCount();i++)
        {
        	PDMultiMap = (Map<String, Object>) adapter.getItem(i);
        	PDMultiorderNo = PDMultiMap.get("No").toString();  //单据号
        	PDMultibillId=PDMultiMap.get("BillId").toString(); //单据ID
        	PDMultiAccId=PDMultiMap.get("AccID").toString();   //单据账套
        	for(int j=0;j<Data.getSelListAdapter().getCount();j++)
        	{
        		SelectedMap = (Map<String, Object>) Data.getSelListAdapter().getItem(j);
        		SelectedorderNo = SelectedMap.get("No").toString();  //单据号
        		SelectedbillId=SelectedMap.get("BillId").toString(); //单据ID
        		SelectedAccId=SelectedMap.get("AccID").toString();   //单据账套
        		if(PDMultiorderNo.equals(SelectedorderNo) && 
        				PDMultibillId.equals(SelectedbillId) && 
        				PDMultiAccId.equals(SelectedAccId))
        		{
        			ViewHolder holder = (ViewHolder) adapter.getView(i, null, null).getTag();
                    holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态  
                    MyAdapter.isSelected.put(i, holder.cb.isChecked()); // 同时修改map的值保存状态  
                    ResultList.add(SelectedMap);
                	listStr.add(holder.tvpdorder.getText().toString());
                	//adapter.getView(i, null, null).setBackgroundColor(Color.parseColor("#33b5e5"));                   
        		}
        	}
        }
        tvMSelCount.setText("已选中"+listStr.size()+"项"); 
    }
	
    public void setInit(ListAdapter initListAdapter)
    {
    	Data.setSelListAdapter(initListAdapter);
    }
    
  //全局变量赋值
  	public static class Data{
      @Nullable
      private static ListAdapter SelListAdapter = null;
  	      
  	    @Nullable
        public static ListAdapter getSelListAdapter() {
  	        return SelListAdapter;  
  	    }  
  	      
  	    public static void setSelListAdapter(ListAdapter pListAdapter) {  
  	    	SelListAdapter = pListAdapter;  
  	    }  
  	}
    
	//为listview自定义适配器内部类  
    public static class MyAdapter extends BaseAdapter {  
        public static HashMap<Integer, Boolean> isSelected;
        @Nullable
        private Context                   context     = null;
        @Nullable
        private LayoutInflater            inflater    = null;
        @Nullable
        private List<Map<String, Object>> list        = null;
        @Nullable
        private String                    keyString[] = null;
        @Nullable
        private String                    itemString0 = null; // 记录每个item中textview的值
        @Nullable
        private String                    itemString1 = null;
        @Nullable
        private String                    itemString2 = null;
        @Nullable
        private String                    itemString3 = null;
        @Nullable
        private String                    itemString4 = null;
        //增加退
        @Nullable
        private String                    itemString5 = null;

        @Nullable
        private int idValue[] = null;// id值
  
        public MyAdapter(Context context, List<Map<String, Object>> list,
                         int resource, @NonNull String[] Key, @NonNull int[] Id) {
            this.context = context;  
            this.list = list;  
            keyString = new String[Key.length];  
            idValue = new int[Id.length];  
            System.arraycopy(Key, 0, keyString, 0, Key.length);  
            System.arraycopy(Id, 0, idValue, 0, Id.length);  
            inflater = LayoutInflater.from(context);  
            init();  
        }  
  
        // 初始化 设置所有checkbox都为未选择  
        public void init() {  
            isSelected = new HashMap<Integer, Boolean>();  
            for (int i = 0; i < list.size(); i++) {  
                isSelected.put(i, false);  
            }  
        }
  
        @Override  
        public int getCount() {  
            return list.size();  
        }  
  
        @Override  
        public Object getItem(int arg0) {  
            return list.get(arg0);  
        }  
  
        @Override  
        public long getItemId(int arg0) {  
            return 0;  
        }  
  
        @Nullable
        @Override
        public View getView(int position, @Nullable View view, ViewGroup arg2) {
            ViewHolder holder = null;  
            if (holder == null) 
            {  
                holder = new ViewHolder();  
                if (view == null) {
                    view = inflater.inflate(R.layout.vlistmultipds, null);  
                }  
                holder.tvpdorder = (TextView) view.findViewById(R.id.mlistpdorder);  
                holder.tvfromware = (TextView) view.findViewById(R.id.mlistfromware);
                holder.tvtoware = (TextView) view.findViewById(R.id.mlisttoware);
                holder.tvaccid = (TextView) view.findViewById(R.id.mlistaccid);
                holder.dcorp = (TextView) view.findViewById(R.id.mlistdcorp);
                //增加退
                holder.statuse = (TextView) view.findViewById(R.id.mlistbillstatus);
                
                holder.cb = (CheckBox) view.findViewById(R.id.chbmitemselect);  
                view.setTag(holder);  
            }
            else 
            	holder = (ViewHolder) view.getTag();
            
            Map<String, Object> map = list.get(position);  
            if (map != null) {  
                itemString0 = (String) map.get(keyString[0]);
                itemString1 = (String) map.get(keyString[1]);
                itemString2 = (String) map.get(keyString[2]);
                itemString3 = (String) map.get(keyString[3]);
                itemString4 = (String) map.get(keyString[4]);
                //增加退
                itemString5 = (String) map.get(keyString[5]);
                
                holder.tvpdorder.setText(itemString0);
                holder.tvfromware.setText(itemString1);
                holder.tvtoware.setText(itemString2);
                holder.tvaccid.setText(itemString3);
                holder.dcorp.setText(itemString4);
                //增加退
                holder.statuse.setText(itemString5);
            }  
            holder.cb.setChecked(isSelected.get(position));  
            return view;
        }  
        
//        public void checkAll(){  
//	       	 for (int i = 0; i < list.size(); i++) {  
//	             isSelected.put(i, true);  
//	         } 
//            notifyDataSetChanged();  
//	    }  
	    public void noCheckAll(){
			for (int i = 0; i < list.size(); i++) 
			{  
				isSelected.put(i, false);
				 Map<String, Object> map = list.get(i);
		    }
		    notifyDataSetChanged();  
	    }
  
    } 
}
