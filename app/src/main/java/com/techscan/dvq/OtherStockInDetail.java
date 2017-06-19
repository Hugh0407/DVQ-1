package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//import com.techscan.dvq.StockMoveScan.ButtonOnClickDelconfirm;
//import com.techscan.dvq.StockMoveScan.ButtonOnClick;

public class OtherStockInDetail extends Activity {

	String m_BillNo = "";
	String m_BillID = "";
	String m_BillType = "";
	
	String m_pk_Corp = "";
	String m_WarehouseID = "";
	String m_AccID;
	
	String m_PosCode;
	String m_PosID;
	
	JSONObject jsHead;
	JSONObject jsBody;
	
	Switch swhOthPos;
	
	private SplitBarcode bar = null;            //当前扫描条码解析
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
//	private int MainLogin.music2;//定义一个int来设置suondID
	//ADD CAIXY TEST END 
	
	Inventory currentObj;		//当前扫描到的存货信息
	
	JSONObject jsBoxTotal;
	JSONObject jsSerino;
	
	
	EditText txtBarcode;
	EditText txtInvName;
	EditText txtInvSerino;
	EditText txtInvCode;
	EditText txtBatch;
	EditText txtPosition;
	//Map<String, Object> map;
	
	TextView tvPurcount;
	
	Button btnTask;
	Button btnDetail;
	Button btnExit;
	
	int ishouldinnum = 0;
	int iinnum = 0;
	
	List<Map<String,Object>> lstTaskBody = null;
	
	private AlertDialog DeleteButton =null;
	private ButtonOnClick buttonDelOnClick = new ButtonOnClick(0);
	private AlertDialog SelectButton=null;
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {		if (keyCode == KeyEvent.KEYCODE_MENU) 
		 	{//拦截meu键事件			//do something...	
		       return false;
			 }		
	 if (keyCode == KeyEvent.KEYCODE_BACK) 
	 {//拦截返回按钮事件			//do something...	
		 return false;
	 }		
	 return true;
	 }
	
	private void LoadOtherOrder() throws ParseException, IOException
	{
		if(m_BillNo == null
				|| m_BillNo.equals(""))
		{
			Toast.makeText(this, "请先确认需要扫描的订单号", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		JSONObject para = new JSONObject();		 
		String FunctionName="";		
		FunctionName="CommonQuery";		
		//获取表头
		try 
		{
			para.put("BillType", this.m_BillType);
			para.put("accId", m_AccID);
			para.put("BillCode", this.m_BillNo);
			para.put("TableName", "PurHead");
			para.put("pk_corp", m_pk_Corp);
			para.put("FunctionName","GetOtherInOutHead");
			
		} 
		catch (JSONException e2) 
		{
			e2.printStackTrace();
			Toast.makeText(OtherStockInDetail.this, e2.getMessage() ,
					Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}
		try 
		{
			jsHead = Common.DoHttpQuery(para, FunctionName, m_AccID);
		} 
		catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		
		//表头获得完毕
		para = new JSONObject();		
		FunctionName = "CommonQuery";
		try
		{
			para.put("BillID", m_BillID);
			para.put("accId", m_AccID);
			para.put("FunctionName","GetOtherInOutBody");
			para.put("TableName", "PurBody");	
			
			jsBody = Common.DoHttpQuery(para, FunctionName, m_AccID);
		}
		catch (JSONException e2) 
		{
//				
//				Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
		}
		
		try 
		{	
			if(jsHead==null || jsBody == null)
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			if(!jsHead.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			if(!jsHead.getBoolean("Status"))
			{
				String errMsg = "";
				if(jsHead.has("ErrMsg"))
				{
					errMsg = jsHead.getString("ErrMsg");
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
			if(!jsBody.getBoolean("Status"))
			{
				String errMsg = "";
				if(jsBody.has("ErrMsg"))
				{
					errMsg = jsBody.getString("ErrMsg");
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
	}
	
	
	

    private boolean FindInvnBinStockInfo(String Free1)throws JSONException, ParseException, IOException
    {

    	    JSONObject para = new JSONObject();
    	    para.put("FunctionName", "GetBinStockByID");
    	    para.put("InvID", this.currentObj.Invmandoc());
    	    if(bar.AccID.equals("A"))
    	    {
        		para.put("BinID", this.m_PosID);
    	    }
    	    if(bar.AccID.equals("B"))
    	    {
        		para.put("BinID", this.m_PosID);
    	    }

    		
    	    
    	    para.put("LotB", this.currentObj.GetBatch());
    	    para.put("TableName", "Stock");
    	    if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return false;
	        }
    	    JSONObject StockInfo = Common.DoHttpQuery(para, "CommonQuery", bar.AccID);
    	    
    	    if (StockInfo == null)
    	    {
    	      Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
    	      MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    	      return false;
    	    }
    	    
    	    if (!StockInfo.has("Status"))
    	    {
    	      Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
    	      MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    	      return false;
    	    }
    	    
    	    if (StockInfo.getBoolean("Status"))
    	    {
    	    	JSONArray val = StockInfo.getJSONArray("Stock");
				if (val.length() < 1)
				{
				    Toast.makeText(this, "获取货位库存数据信息失败,货位上没有该货品,不能调出", Toast.LENGTH_LONG).show();
				    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				    return false;
				}
				
				double stockCount = 0.0;
				
				for(int iv = 0; iv < val.length(); iv++)
				{
					JSONObject temp = val.getJSONObject(iv);
					String vfree1 = temp.getString("vfreeb1");
					currentObj.SetvFree1(vfree1);
					
					if(Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0)
					{
						stockCount = stockCount + Double.valueOf(temp.getString("nnum")).doubleValue();
					}
				}
				
				double sancount = getScanCount(bar.cInvCode,bar.cBatch,bar.cSerino);
				sancount += 1.0;
				if(sancount > stockCount) 
				{
                    Toast.makeText(this, "该货品在该货位的发出库存已经不足了,不能发出", Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return false;
                }
                
    	    }
			else
			{
                Toast.makeText(this, "该货品在该仓库中没有库存信息,不能发出", Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
			}
    	    
    	    return true;
    }
    
    double getScanCount(String InvCode, String Batch,String cSerino) 
    {
    	double spacenum = 0.0;
    	
    	if(jsSerino!=null&&jsSerino.has("Serino"))
    	{
    	   	JSONArray serinos;
    		try {
    			serinos = jsSerino.getJSONArray("Serino");
    			
    			for(int i = 0;i<serinos.length();i++)
    			{
    				JSONObject temp = new JSONObject();
    				temp = serinos.getJSONObject(i);
    				if(temp.getString("invcode").equals(InvCode)
    						&&temp.getString("batch").equals(Batch)&&temp.getString("poscode").equals(this.m_PosCode)
    						&&!temp.getString("sno").equals(cSerino))
    				{
    					spacenum = spacenum+1.0;
    				}
    			}

    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
 
		

        
//		JSONArray bodys = null;
//		try {
//			bodys = jsBody.getJSONArray("PurBody");
//		} catch (JSONException e) {
//
//			e.printStackTrace();
//		}
//		boolean isFind = false;
//		for(int i = 0;i<bodys.length();i++)
//		{
//			try {
//				JSONObject temp = bodys.getJSONObject(i);
//				
//				String donenum = temp.getString("doneqty");
//				String invcode = temp.getString("invcode");
//				String batch = temp.getString("vbatchcode");
//				
//				if(invcode.equals(InvCode)&&batch.equals(Batch))
//				{
//					spacenum += Double.valueOf(donenum);
//				}
//				
//				//int doneqty = temp.getInt("doneqty");
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	
//		}

        	
//            if((values.get("cinventoryid").equals(InvID)) && (values.get("vbatchcode").equals(BatchCode))) 
//            {
//                spacenum += Double.valueOf((String)values.get("spacenum")).doubleValue();
//            }
        
        return spacenum;
    }
    
	
	
	private boolean ScanSerial(String serino,String Free1,String rowNo,String fbillrowflag,String CheckFlg) throws JSONException, ParseException, IOException
	//private boolean ScanSerial(String serino,String Free1,String fbillrowflag,String CheckFlg) throws JSONException, ParseException, IOException
	
	{
		if(jsSerino == null)
			jsSerino = new JSONObject();

		if(CheckFlg.equals("2"))
		{
			if((m_BillType.equals("4L")&&fbillrowflag.equals("1"))
					||(m_BillType.equals("4M")&&fbillrowflag.equals("0"))
					||(m_BillType.equals("4N")&&fbillrowflag.equals("2")))
			{
				if(!FindInvnBinStockInfo(Free1))
				{
					return false;
				}
			}
		}
		
		if(!jsSerino.has("Serino"))
		{
			if(CheckFlg.equals("2"))
			{
				JSONArray serinos = new JSONArray();
				jsSerino.put("Serino", serinos);
				JSONObject temp = new JSONObject();
				temp.put("serino", bar.CheckBarCode);
				temp.put("invcode", currentObj.getInvCode());
				temp.put("invname",currentObj.getInvName());
				temp.put("batch",currentObj.GetBatch());
				temp.put("sno", currentObj.GetSerino());
				//caixy 需要增加产地
				if(Free1.equals("null"))
				{
					temp.put("vfree1", currentObj.vFree1());
				}
				else
				{
					temp.put("vfree1", Free1);
				}
				temp.put("rowno", rowNo);
				temp.put("poscode", this.m_PosCode);
				temp.put("posID", this.m_PosID);
				temp.put("WHID", this.m_WarehouseID);
				temp.put("fbillrowflag", fbillrowflag);
				temp.put("box", bar.currentBox);
				temp.put("totalnum", bar.TotalBox);
				
				serinos.put(temp);
				return true;
			}
		}
		else
		{

			JSONArray serinos = jsSerino.getJSONArray("Serino");
			
			for(int i = 0;i<serinos.length();i++)
			{
				JSONObject temp = new JSONObject();
				temp = serinos.getJSONObject(i);
				if(temp.getString("serino").equals(bar.CheckBarCode)&&temp.getString("box").equals(bar.currentBox)&&CheckFlg.equals("1"))
				{
					Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", 
							Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return false;
				}
				
				if(temp.getString("serino").equals(bar.CheckBarCode)&&!temp.getString("poscode").equals(this.m_PosCode)&&CheckFlg.equals("2"))
				{
					Toast.makeText(this, "同一件存货的分包不能存放在不同货位,之前扫入的货位为："+temp.getString("poscode"), 
							Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return false;
				}
			}
			

			
			if(CheckFlg.equals("2"))
			{
				JSONObject temp = new JSONObject();
				
				temp.put("serino", bar.CheckBarCode);
				temp.put("invcode", currentObj.getInvCode());
				temp.put("invname",currentObj.getInvName());
				temp.put("batch",currentObj.GetBatch());
				temp.put("sno", currentObj.GetSerino());
				//caixy 需要增加产地
				if(Free1.equals("null"))
				{
					temp.put("vfree1", currentObj.vFree1());
				}
				else
				{
					temp.put("vfree1", Free1);
				}
				
				temp.put("rowno", rowNo);
				temp.put("poscode", this.m_PosCode);
				temp.put("posID", this.m_PosID);
				temp.put("WHID", this.m_WarehouseID);
				temp.put("fbillrowflag", fbillrowflag);
				temp.put("box", bar.currentBox);
				temp.put("totalnum", bar.TotalBox);
				serinos.put(temp);
				return true;
			}
		}
		
		return true;
		
	}
	
	private boolean ScanBoxTotal(String serino,String total,String current) throws JSONException
	{
		if(jsBoxTotal == null)
			jsBoxTotal = new JSONObject();
		
		
		int stotal = Integer.valueOf(total);
		
		if(!jsBoxTotal.has("BoxList"))
		{
			JSONArray boxs = new JSONArray();
			jsBoxTotal.put("BoxList", boxs);
			
			JSONObject temp = new JSONObject();
			temp.put("serial", serino);
			temp.put("total", total);
			temp.put("current", 1);		
			temp.put("invcode",currentObj.getInvCode());
			temp.put("batch", currentObj.GetBatch());
			boxs.put(temp);
			
			if(stotal == 1)
				return true;
			else
				return false;
		}
		JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");				
		for(int i = 0;i<boxs.length();i++)
		{
			JSONObject temp = boxs.getJSONObject(i);			
			if(temp.getString("serial").equals(serino)
					&& temp.getString("invcode").equals(currentObj.getInvCode())
					&& temp.getString("batch").equals(currentObj.GetBatch()))
			{
				int icurrent;
				int itotal;
				icurrent = temp.getInt("current");
				itotal = temp.getInt("total");
				
				if(icurrent == itotal)
				{
					Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", 
							Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return false;
				}	
				if(itotal == icurrent + 1)
				{
					temp.put("current", icurrent + 1);
					return true;
				}
				
				temp.put("current", icurrent + 1);
					return false;				
				//13770529941
			}
		}
		
		JSONObject temp = new JSONObject();
		temp.put("serial", serino);
		temp.put("total", total);
		temp.put("invcode",currentObj.getInvCode());
		temp.put("batch", currentObj.GetBatch());
		temp.put("current", 1);			
		boxs.put(temp);		
		
		if(stotal == 1)
			return true;
		else
			return false;		
	}
		
	private boolean ScanDetail(String barcode)
	{
		if(barcode == null || barcode.equals(""))
			return false;	
		
		if(this.m_PosID == null ||
				this.m_PosID.equals(""))
		{
			Toast.makeText(this, "请先扫描货位", 
					Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			this.txtBarcode.setText("");			
			this.txtPosition.setText("");
			this.txtPosition.requestFocus();
			return false;
		}
		
		//SplitBarcode bar = new SplitBarcode(barcode);
		
		bar = new SplitBarcode(barcode);
  		if(bar.creatorOk==false)
  		{
  			Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			txtBarcode.setText("");
			txtBarcode.requestFocus();
  			return false;
  		}  
		
		
		
		
		txtBarcode.setText("");
		IniDetail();		
		try
		{
			try
			{
				 currentObj = new Inventory(bar.cInvCode,this.m_pk_Corp,bar.AccID);
			}
			catch(Exception ex)
			{
				Toast.makeText(this, ex.getMessage(), 
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			currentObj.SetSerino(bar.cSerino);
			currentObj.SetBatch(bar.cBatch);
			currentObj.SetcurrentID(bar.currentBox);
			currentObj.SettotalID(bar.TotalBox);
			currentObj.SetAccID(bar.AccID);
			
			if(!bar.AccID.equals(m_AccID))
			{
				Toast.makeText(this, "该存货的帐套不符合", 
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
						
			if(currentObj.getErrMsg() != null
					&& !currentObj.getErrMsg().equals("") )
			{
				Toast.makeText(this, currentObj.getErrMsg(), 
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			//获得存货完毕，校验上游单据			
			JSONArray bodys = jsBody.getJSONArray("PurBody");
			boolean isFind = false;
			boolean isOver = false;
			
			for(int i = 0;i<bodys.length();i++)
			{
				JSONObject temp = bodys.getJSONObject(i);
				JSONObject tempOld = bodys.getJSONObject(i);
				//int doneqty = temp.getInt("doneqty");
				if((temp.getString("invcode").equals(currentObj.getInvCode())
						&& temp.getString("vbatchcode").equals(currentObj.GetBatch()))
						||(m_BillType.equals("4L")&&temp.getString("fbillrowflag").equals("1")&&temp.getString("invcode").equals(currentObj.getInvCode())
								&& temp.getString("vbatchcode").equals("null")))
				{
					isFind = true;
					ShowDetail();
					
					String Free1 = temp.getString("vfree1");
					if(!Free1.equals("null"))
					{
						currentObj.SetvFree1(Free1);
					}
					String fbillrowflag = temp.getString("fbillrowflag");
					
					if(ScanSerial(barcode,Free1,temp.getString("crowno"),fbillrowflag,"1") == false)
					//if(ScanSerial(barcode,Free1,fbillrowflag,"1") == false)
					{
						txtBarcode.setText("");
						txtBarcode.requestFocus();
						return false;
					}
					
					//寻找到了对应存货
					if(temp.getInt("doneqty") >= temp.getInt("nshouldinnum"))
					{
						isOver = true;
					}
					else
					{
						isOver = false;
					}
					
					if(isOver == false)
					{
						if(ScanSerial(barcode,Free1,temp.getString("crowno"),fbillrowflag,"2") == false)
						//if(ScanSerial(barcode,Free1,fbillrowflag,"2") == false)
								
						{
							txtBarcode.setText("");
							txtBarcode.requestFocus();
							return false;
						}
						
						//判断判断箱子是否装满。
						if(ScanBoxTotal(bar.cSerino,
								bar.TotalBox,bar.currentBox) == true)
						{
							int doneqty = temp.getInt("doneqty");
							//增加货位操作
							
							//结束货位增加										
							temp.put("doneqty", doneqty + 1);
							temp.put("position", this.m_PosID);							
							//temp.put("vfree1", currentObj.vFree1());
						}
//						if(temp.getString("vbatchcode").equals("null"))
//						{
//							JSONArray bodysNew = bodys;
//							JSONObject tempNew = tempOld;
//							
//							tempNew.put("vbatchcode",currentObj.GetBatch());
//							tempNew.put("batchflg", "2");
//							bodysNew.put(tempNew);
//							jsBody = new JSONObject();
//							jsBody.put("PurBody", bodysNew);
//							jsBody.put("Status", true);
//						}
						break;
					}
				}
			}
			
			if(isOver == true)
			{
				Toast.makeText(this, "这个存货已经超过任务数量了,不允扫入!", Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END

				txtBarcode.setText("");
				txtBarcode.requestFocus();
				return false;
			}
			
			if(isFind == false)
			{
				Toast.makeText(this, "该存货不在本次扫描任务中", 
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			
		}
		catch(Exception ex)
		{
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		}
		
		JSONArray arrays;
		try {
			arrays = jsBody.getJSONArray("PurBody");
			ishouldinnum = 0;
			iinnum = 0;
			for(int i = 0;i<arrays.length();i++)
			{
				String sshouldinnum = ((JSONObject)(arrays.get(i))).getString("nshouldinnum");
				String sinnum = ((JSONObject)(arrays.get(i))).getString("doneqty");
				
				ishouldinnum = ishouldinnum + Integer.valueOf(sshouldinnum);
				iinnum = iinnum + Integer.valueOf(sinnum);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}

		tvPurcount.setText("总共"+ishouldinnum+"件 | "+"已扫"+iinnum+"件 | "+"未扫"+(ishouldinnum-iinnum)+"件");
		
//		txtPosition.requestFocus();
//		txtPosition.setText("");
//		txtPosition.setSelectAllOnFocus(true);	
		MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
		
		if(!this.swhOthPos.isChecked())
		{
			this.txtPosition.setText("");
			m_PosID = "";
			txtPosition.setFocusable(true);
			txtPosition.setFocusableInTouchMode(true);
			txtPosition.requestFocus();
			txtPosition.requestFocusFromTouch();
			

		}

		return true;		
	}
		
	
	
	
	private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		public int index;
		public ButtonOnClick(int index)
		{
			this.index = index;
		}
		@Override
		public void onClick(DialogInterface dialog, int whichButton)
		{
			if (whichButton >= 0)
			{
				index = whichButton;
				// dialog.cancel(); 				
			}
			else
			{
				
				if(dialog.equals(DeleteButton))
				{
					if(whichButton == DialogInterface.BUTTON_POSITIVE)
					{
						return;
					}
					else if (whichButton == DialogInterface.BUTTON_NEGATIVE)
					{
						//这里进行数据删除处理
						//ConfirmDelItem(index);
					}
				}
			}

		}

	}
	
	
	
	
	private void ShowDetail()
	{
		if(currentObj == null)
			return;
		
		txtInvName.setText(currentObj.getInvName());
		txtInvCode.setText(currentObj.getInvCode());
		txtBatch.setText(currentObj.GetBatch());
		txtInvSerino.setText(currentObj.GetSerino());
		
 
		
	}
	
	private void IniDetail()
	{
		currentObj = null;
		txtInvName.setText("");
		txtInvCode.setText("");
		txtBatch.setText("");
		txtInvSerino.setText("");
//		txtPosition.setText("");
//		this.m_PosID = "";
//		this.m_PosCode = "";
	}
	
	private Button.OnClickListener  myButtonListner1 = new 
    		Button.OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				case id.btnOthTask:
					try 
					{
						ShowTaskDig();
					} 
					catch (JSONException e) 
					{
						e.printStackTrace();		//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					}
					break;
				case id.btnOthDetail:
					try {
						ShowDetailDig();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					}

					break;
				case id.btnOthReturn:
					Return();
					break;
			}		
		}
    };
    
    private void FindPositionByCode(String posCode,String type) throws JSONException
    {
    		
		try
		{	
			if(m_AccID==null||m_AccID.equals(""))
			{
				Toast.makeText(this, "仓库,帐套还没有确认,不能先扫描货位", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				txtPosition.setText("");
				txtPosition.requestFocus();
			}

			JSONObject para = new JSONObject();
			para.put("FunctionName", "GetBinCodeInfo");
			para.put("CompanyCode",  m_pk_Corp);
			para.put("STOrgCode",  MainLogin.objLog.STOrgCode);
			para.put("WareHouse", this.m_WarehouseID);
			para.put("BinCode", posCode.toUpperCase());
			para.put("TableName",  "position");	
			
			JSONObject rev = MainLogin.objLog.DoHttpQuery
					(para, "CommonQuery", m_AccID);		
			
			if(rev==null)
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return ;
			}

			if(!rev.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return ;
			}
			if(rev.getBoolean("Status"))
			{
				JSONArray val = rev.getJSONArray("position");				
				if(val.length() < 1)
				{
					Toast.makeText(this, "获取货位失败", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					//ADD CAIXY START 15/04/14
					txtPosition.setText("");
					txtPosition.requestFocus();
					//ADD CAIXY END   15/04/14
					
					return;
				}
				String jposName,jposCode,jposID;
				JSONObject temp = val.getJSONObject(0);
				
				jposName = temp.getString("csname");
				jposCode = temp.getString("cscode");
				jposID = temp.getString("pk_cargdoc");
				

					
				this.txtPosition.setText(jposCode);
				this.m_PosCode = jposCode;
				this.m_PosID = jposID;

				MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
				this.txtBarcode.setText("");
				this.txtBarcode.requestFocus();
				return;				
			}
			else
			{
				Toast.makeText(this, "获取货位失败", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				//ADD CAIXY START 15/04/14
				txtPosition.setText("");
				txtPosition.requestFocus();
				//ADD CAIXY END   15/04/14
				return;
				
			}
			
		}
		 catch (JSONException e) {
				
				Toast.makeText(this, 
						e.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e.printStackTrace();
			} 
		catch(Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}
    }
  
    
    private void Return()
    {
        if(jsBoxTotal != null && jsSerino != null)
        {        	
        	Intent intent = new Intent();

        	intent.putExtra("box", jsBoxTotal.toString());
        	intent.putExtra("head", jsHead.toString());
        	intent.putExtra("body", jsBody.toString());
        	intent.putExtra("serino", jsSerino.toString());        	       
        	OtherStockInDetail.this.setResult(1, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理              	 
        	OtherStockInDetail.this.finish();

        }
        else
        {
        	Intent intent = new Intent();
        	OtherStockInDetail.this.setResult(2, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理              	 
        	OtherStockInDetail.this.finish();
        }
    }
    private void ShowDetailDig() throws JSONException
    {
    	lstTaskBody	= new ArrayList<Map<String, Object>>();
    	
    	Map<String, Object> map;
    	if(jsSerino == null||!jsSerino.has("Serino"))
    	{
			Toast.makeText(this, "还没有扫描到的记录", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;	
    	}
    	JSONArray arrays =jsSerino.getJSONArray("Serino");
    	
		for(int i = 0;i<arrays.length();i++)
		{
			map = new HashMap<String, Object>();

			String sSerial = ((JSONObject)(arrays.get(i))).getString("sno");
			String sBatch = ((JSONObject)(arrays.get(i))).getString("batch");
			String sInvCode = ((JSONObject)(arrays.get(i))).getString("invcode");
			String serino = ((JSONObject)(arrays.get(i))).getString("serino");
			
			String isShow = "0";
			
			if(lstTaskBody.size()>0)
			{
				for(int z = 0;z<lstTaskBody.size();z++)
				{
					
					Map<String, Object> mapShow = lstTaskBody.get(z);
					String lsSerial = (String)mapShow.get("sno");
					String lsBatch = (String)mapShow.get("batch");
					String lsInvCode = (String)mapShow.get("invcode");
					String lserino = (String)mapShow.get("serino");
					
					if(sSerial.equals(lsSerial)&&sBatch.equals(lsBatch)&&sInvCode.equals(lsInvCode)&&serino.equals(lserino))
					{
						isShow = "1";
					}
				}
			}
			if(isShow.equals("0"))
			{
				map.put("invcode",sInvCode) ;
				map.put("invname", ((JSONObject)(arrays.get(i))).getString("invname"));
				map.put("batch", sBatch);
				map.put("sno", sSerial);
				map.put("serino", serino);
				JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");				
				for(int x = 0;x<boxs.length();x++)
				{
					JSONObject temp = boxs.getJSONObject(x);			
					if(temp.getString("serial").equals(sSerial)
							&& temp.getString("invcode").equals(sInvCode)
							&& temp.getString("batch").equals(sBatch))
					{
						int icurrent;
						int itotal;
						icurrent = temp.getInt("current");
						itotal = temp.getInt("total");					
						if(icurrent == itotal)
						{
							map.put("okflg", " ");
						}	
						else
						{
							map.put("okflg", "分包未完");
						}
					}
				}
				lstTaskBody.add(map);
			}
		}
		//jsBoxTotal
		SimpleAdapter listItemAdapter = 
				new SimpleAdapter(OtherStockInDetail.this,lstTaskBody,//数据源   
                R.layout.vlistpurdetail,//ListItem的XML实现  
                //动态数组与ImageItem对应的子项          
                new String[] {"invname","invcode","batch","sno","okflg"},   
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] { R.id.listpurdetailinvName,
							R.id.listpurdetailinvCode,
							R.id.listpurdetailinvBatch,
							R.id.listpurdetailSno,
							R.id.listpurdetailinvok}  
            ); 
		
		DeleteButton =new AlertDialog.Builder(this).setTitle("扫描明细信息").setSingleChoiceItems(listItemAdapter, 0, buttonDelOnClick).setPositiveButton(R.string.QueRen,null).create();
		//MOD CAIXY END  
		
		DeleteButton.getListView().setOnItemLongClickListener(new OnItemLongClickListener(){  
            @Override  
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
                    int arg2, long arg3) {  
                // TODO Auto-generated method stub  
                // When clicked, show a toast with the TextView text  
            	
            	ConfirmDelItem(arg2);
                
                return false;  
            }  
        });
		
		DeleteButton.show();
    	
    }
    
	private void ConfirmDelItem(int index)
	{   
		ButtonOnClickDelconfirm buttondel =new ButtonOnClickDelconfirm(index);
		SelectButton=new AlertDialog.Builder(this).setTitle(R.string.QueRenShanChu).setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
				.setPositiveButton(R.string.QueRen, buttondel).setNegativeButton(R.string.QuXiao,null).show();
	}
	
	private class ButtonOnClickDelconfirm implements DialogInterface.OnClickListener
	{
		public int index;
		public ButtonOnClickDelconfirm(int index)
		{
			this.index = index;
		}
		@Override
		public void onClick(DialogInterface dialog, int whichButton)
		{
			if (whichButton >= 0)
			{
				index = whichButton;
			}
			else
			{
				
					if(whichButton == DialogInterface.BUTTON_POSITIVE)
					{

						
						Map<String, Object> mapTemp= (Map<String, Object>) lstTaskBody.get(index);
//						
//						//new String[] {"invname","invcode","batch","sno","okflg"}, 
						String invcode=(String)mapTemp.get("invcode");
						String batch=(String)mapTemp.get("batch");
						String sno=(String)mapTemp.get("sno");
						String serino=(String)mapTemp.get("serino");
						//String fbillrowflag=(String)mapTemp.get("fbillrowflag");
						
						JSONArray arrays;
						try {
							arrays = jsSerino.getJSONArray("Serino");
							
							HashMap<String, Object>Temp = new HashMap<String, Object>();
							JSONArray serinos = new JSONArray();
							
							for(int i = 0;i<arrays.length();i++)
							{
								String serino1 = ((JSONObject)(arrays.get(i))).getString("serino");
								if(!serino1.equals(serino))
								{
									JSONObject temp = new JSONObject();
									temp = arrays.getJSONObject(i);
									serinos.put(temp);
								}
							}
							
							jsSerino = new JSONObject();
							
							if(serinos.length()>0)
							{
								jsSerino.put("Serino", serinos);
							}
							
							int it = 0;
							JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");
							JSONArray newboxs = new JSONArray();
							for(int x = 0;x<boxs.length();x++)
							{
								JSONObject tempBox = new JSONObject();
								JSONObject temp = boxs.getJSONObject(x);			
								if(temp.getString("serial").equals(sno)
										&& temp.getString("invcode").equals(invcode)
										&& temp.getString("batch").equals(batch))
										
								{
									it = 1;
								}
								else
								{
									
									tempBox = temp;
									newboxs.put(tempBox);
								}
								
							}
							
	
							jsBoxTotal = new JSONObject();
							
							if(serinos.length()>0)
							{
								jsBoxTotal.put("BoxList", newboxs);
							}
								
							boolean isDel = false;
							if(it == 1)
							{
								
								JSONArray bodys = jsBody.getJSONArray("PurBody");
								JSONArray bodynews = new JSONArray();
								//JSONArray serinos = new JSONArray();
								for(int i = 0;i<bodys.length();i++)
								{
									JSONObject temp = bodys.getJSONObject(i);	
									
									
									String invcodeold = ((JSONObject)(bodys.get(i))).getString("invcode");
									String batchcodeold = ((JSONObject)(bodys.get(i))).getString("vbatchcode");
									String fbillrowflag =  ((JSONObject)(bodys.get(i))).getString("fbillrowflag");
									if((invcodeold.equals(invcode)&&batchcodeold.equals(batch)&&isDel==false
											&&!((JSONObject)(bodys.get(i))).getString("doneqty").equals("0"))
											||(invcodeold.equals(invcode)&&batchcodeold.equals("null")&&isDel==false
											&&fbillrowflag.equals("1")&&m_BillType.equals("4L")
													&&!((JSONObject)(bodys.get(i))).getString("doneqty").equals("0")))
									{
										int doneqty = temp.getInt("doneqty");
										temp.put("doneqty", doneqty - 1);
										isDel = true;
									}
									
									bodynews.put(temp);
								}
								
								jsBody = new JSONObject();
								jsBody.put("Status", "true");
								jsBody.put("PurBody", bodynews);
							}
							
							
							
							JSONArray arraysCount;
							try {
								arraysCount = jsBody.getJSONArray("PurBody");
								ishouldinnum = 0;
								iinnum = 0;
								for(int i = 0;i<arraysCount.length();i++)
								{
									String sshouldinnum = ((JSONObject)(arraysCount.get(i))).getString("nshouldinnum");
									String sinnum = ((JSONObject)(arraysCount.get(i))).getString("doneqty");
									
									ishouldinnum = ishouldinnum + Integer.valueOf(sshouldinnum);
									iinnum = iinnum + Integer.valueOf(sinnum);
								}
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							tvPurcount.setText("总共"+ishouldinnum+"件 | "+"已扫"+iinnum+"件 | "+"未扫"+(ishouldinnum-iinnum)+"件");
							
							IniDetail();
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(OtherStockInDetail.this, e.getMessage() ,
									Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
						}

						DeleteButton.cancel();
				
					}
					else if (whichButton == DialogInterface.BUTTON_NEGATIVE)
					{
						return;
					}
			}
	    }
	}
    
    private void ShowTaskDig() throws JSONException
    {
    	lstTaskBody
    		= new ArrayList<Map<String, Object>>();
    	//purBody
    	Map<String, Object> map;
    	
    	if(jsBody == null)
    	{
			Toast.makeText(this, R.string.MeiYouDeDaoBiaoTiShuJu, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
    	}
    	JSONArray arrays =jsBody.getJSONArray("PurBody");
    	
		for(int i = 0;i<arrays.length();i++)
		{
			map = new HashMap<String, Object>();
			map.put("BillCode",m_BillNo);
			map.put("InvName", ((JSONObject)(arrays.get(i))).getString("invname"));
			map.put("InvCode", ((JSONObject)(arrays.get(i))).getString("invcode"));
			String batchs=((JSONObject)(arrays.get(i))).getString("vbatchcode");
			if(batchs==null||batchs.equals("")||batchs.equals("null"))
			{
				batchs="批次未指定";
			}
			map.put("Batch", batchs);//test caixy
			map.put("InvNum", ((JSONObject)(arrays.get(i))).getString("doneqty") + " / " +((JSONObject)(arrays.get(i))).getString("nshouldinnum"));
			//map.put("DoneQty", )
			lstTaskBody.add(map);
		}
		
    	
		SimpleAdapter listItemAdapter = 
				new SimpleAdapter(OtherStockInDetail.this,lstTaskBody,//数据源   
                R.layout.vlisttranstask,//ListItem的XML实现  
                //动态数组与ImageItem对应的子项          
                new String[] {"BillCode","InvCode","InvName","Batch","InvNum"},   
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] { R.id.txtTranstaskBillCode,
							R.id.txtTranstaskInvCode,
							R.id.txtTranstaskInvName,
							R.id.txtTranstaskBatch,
							R.id.txtTranstaskInvNum}  
            ); 
		new AlertDialog.Builder(OtherStockInDetail.this).setTitle("源单信息")
						.setAdapter(listItemAdapter, null)
						.setPositiveButton(R.string.QueRen,null).show();
		
    }
    private  TextWatcher watchers = new TextWatcher(){   
		  
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
    private OnKeyListener myTxtListener = new 
    		OnKeyListener()
    {
		@Override
		public boolean onKey(View v, int arg1, KeyEvent arg2) 
		{
			switch(v.getId())
			{
				case id.txtOthBarcode:
				if(arg1 == 66 && arg2.getAction() 
						== KeyEvent.ACTION_UP)
				{
					ScanDetail(txtBarcode.getText().toString());
					return true;
				}
				break;
				case id.txtOthPosition:
					if(arg1 == arg2.KEYCODE_ENTER && arg2.getAction() 
					== KeyEvent.ACTION_UP)
					{
						try 
						{
							FindPositionByCode
							(txtPosition.getText().toString().trim().toUpperCase(),"1");
							return true;
							//txtInPos.setText(m_InPosName);
						} catch (ParseException e) {
							txtPosition.setText("");
							txtPosition.requestFocus();
							Toast.makeText(OtherStockInDetail.this, 
									e.getMessage(), Toast.LENGTH_LONG).show();

							
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							
						} catch (JSONException e) {
							txtPosition.setText("");
							txtPosition.requestFocus();
							Toast.makeText(OtherStockInDetail.this, 
									e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							
						} 
					}
					break;
			}
			return false;
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_stock_in_detail);
		
		String tag = this.getIntent().getStringExtra("Tag");
		this.m_BillNo = this.getIntent().getStringExtra("BillNo");
		this.m_BillID = this.getIntent().getStringExtra("OrderID");
		this.m_BillType = this.getIntent().getStringExtra("OrderType");
		this.m_AccID = this.getIntent().getStringExtra("AccID");
		this.m_WarehouseID = this.getIntent().getStringExtra("m_WarehouseID");
		this.m_pk_Corp = this.getIntent().getStringExtra("pk_corp");
		
		
		swhOthPos = (Switch)findViewById(R.id.swhOthPos);		
		swhOthPos.setChecked(false);
		
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1);
//		
		if(tag.equals("1"))
		{
			
			try
			{
				String temp = this.getIntent().getStringExtra("head");
				jsHead = new JSONObject(temp);
				
				temp = this.getIntent().getStringExtra("body");
				jsBody = new JSONObject(temp);	
				
				temp = this.getIntent().getStringExtra("serino");
				jsSerino = new JSONObject(temp);
				
				temp = this.getIntent().getStringExtra("box");
				jsBoxTotal = new JSONObject(temp);	
				
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
				Toast.makeText(OtherStockInDetail.this, e.getMessage() ,
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			}
		}
		

    	
		
		
		tvPurcount = (TextView)findViewById(R.id.tvOthcount);
		txtBarcode = (EditText)findViewById(R.id.txtOthBarcode);
		txtInvName = (EditText)findViewById(R.id.txtOthInvName);
		txtInvSerino = (EditText)findViewById(R.id.txtOthSerino);
		txtInvCode = (EditText)findViewById(R.id.txtOthInvCode);
		txtBatch = (EditText)findViewById(R.id.txtOthBatch);
		txtPosition = (EditText)findViewById(R.id.txtOthPosition);
		
		btnTask = (Button)findViewById(R.id.btnOthTask);		
		btnExit = (Button)findViewById(R.id.btnOthReturn);
		
		btnTask.setOnClickListener(myButtonListner1);
		btnExit.setOnClickListener(myButtonListner1);
		
		btnDetail = (Button)findViewById(R.id.btnOthDetail);
		btnDetail.setOnClickListener(myButtonListner1);
		
		//this.txtBarcode.addTextChangedListener(watchers);
		txtBarcode.setOnKeyListener(myTxtListener);
		
		//this.txtPosition.addTextChangedListener(watchers);
		txtPosition.setOnKeyListener(myTxtListener);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle("特殊业务扫描明细");
		//Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
		//actionBar.setBackgroundDrawable(TitleBar);
		actionBar.show();
		

		btnTask.setFocusable(false);
		btnDetail.setFocusable(false);
		btnExit.setFocusable(false);
		
		//获得并创建传递过来的参数
		
		
		//===============
		try
		{
			if(jsHead == null && jsBody == null)
				LoadOtherOrder();
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(OtherStockInDetail.this, e.getMessage() ,
					Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(OtherStockInDetail.this, e.getMessage() ,
					Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}
		
		JSONArray arrays;
		try {
			ishouldinnum = 0;
			iinnum = 0;
			if(jsBody==null)
			{
				return;
			}
			arrays = jsBody.getJSONArray("PurBody");
			for(int i = 0;i<arrays.length();i++)
			{
				String sshouldinnum = ((JSONObject)(arrays.get(i))).getString("nshouldinnum");
				String sinnum = ((JSONObject)(arrays.get(i))).getString("doneqty");
				
				ishouldinnum = ishouldinnum + Integer.valueOf(sshouldinnum);
				iinnum = iinnum + Integer.valueOf(sinnum);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Toast.makeText(OtherStockInDetail.this, e1.getMessage() ,
					Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}
		tvPurcount.setText("总共"+ishouldinnum+"件 | "+"已扫"+iinnum+"件 | "+"未扫"+(ishouldinnum-iinnum)+"件");
		
		txtPosition.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pur_stock_in_detail, menu);
		return true;
	}

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
