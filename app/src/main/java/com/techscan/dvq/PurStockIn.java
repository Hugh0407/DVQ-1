package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;

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
import java.util.Map;
import java.util.UUID;

public class PurStockIn extends Activity 
{
	private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
	boolean NoScanSave = false;
	
	String PurBillCode = "";
	String DBBillCode = "";
	private String[] ExitNameList = null;
	String fileName = null;
	String fileNameScan  = null;
	String ScanedFileName  = null;
	String UserID = null;
	File file = null;
	File fileScan = null;
	String ReScanHead = "1";
	private AlertDialog SelectButton=null;
	private AlertDialog SelectButtonNoScan=null;
	
	
	private ArrayList<String> ScanedBarcode = new ArrayList<String>();
	Button btnSave;
	Button btnUpdate;
	ImageButton btnBrowOrderNo;
	Button btnExit;
	Button btnScan;
	TextView labVendor;
	TextView labWarehouse;
	TextView labWHName;
	TextView tvbillstatus;
	//EditText txtPosition;
	EditText txtPurOrderNo;
	Intent scanDetail = null;
	JSONObject jsDBBody;
	JSONObject jsDBHead;
	JSONObject jsHead;
	JSONObject jsBody;
	JSONObject jsBoxTotal;
	JSONObject jsSerino;
	
	String m_WarehouseID = "";
	String pk_purcorp = "";
	String pk_calbody = "";
	String m_AccID = "A";
	
	String m_BillID;
	String m_BillNo;
	
	String m_companyCode = "1001"; 
	
	String m_PosCode = "";
	String m_PosName = "";
	String m_PosID = "";
	
	String tmpWHStatus = "";//仓库是否启用货位
	String tmpBillStatus = "";//单据是否正向采购
	
	int SaveFlg = 0;
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
//	private int MainLogin.music2;//定义一个int来设置suondID
	private writeTxt writeTxt ;		//保存LOG文件
	
    //GUID
    UUID uploadGuid=null;

	private Context MyContext = this;
	
	private boolean SaveDBOrder() throws JSONException, ParseException, IOException
	{
		jsDBHead = new JSONObject();
		jsDBBody = new JSONObject();		
		//首先校验是否还有没有完成对箱子
		
//		JSONObject temp = new JSONObject();		
//		JSONArray bodys = jsBody.getJSONArray("PurBody");
//		JSONArray head = new JSONArray();
//		head = jsHead.getJSONArray("PurHead");
		
		//填写调拨订单表头		
		jsDBHead.put("cbiztypeid", "0001ZZ1000000000UFQ0");				//调拨类型标识 
		jsDBHead.put("cincbid", pk_calbody);		//调入库存组织ID 	pk_calbody
		jsDBHead.put("cincorpid", pk_purcorp);			//调入公司ID 		pk_corp 
		//jsDBHead.put("cinwhid", "1001AA100000000FEGIT");				//调入仓库 (零时写一个）			cwarehouseid
		jsDBHead.put("coutcbid", pk_calbody);		//调出库存组织ID 	pk_calbody		
		jsDBHead.put("coutwhid", m_WarehouseID);		//调出仓库
		jsDBHead.put("fallocflag", "1");								//调拨类型标志 
		jsDBHead.put("coperatorid", MainLogin.objLog.UserID);
		jsDBHead.put("BillCode", m_BillNo);
		
//		int x = 0;	
//		//=============Body		
//		for(int i = 0;i<bodys.length();i++)
//		{
//			if(bodys.getJSONObject(i).getInt("doneqty")
//					< bodys.getJSONObject(i).getInt("nshouldinnum"))
//			{
//				
//				int val = bodys.getJSONObject(i).getInt("nshouldinnum") -
//						bodys.getJSONObject(i).getInt("doneqty");
//				JSONObject obj = new JSONObject();
//				obj.put("cininvid", bodys.getJSONObject(i).getString("cinventoryid"));				//调入存货标识    
//				obj.put("cinvbasid", bodys.getJSONObject(i).getString("cinvbasid"));			//存货基本标识   
//				obj.put("cquoteunitid", bodys.getJSONObject(i).getString("cquoteunitid"));		//报价计量单位ID    
//				obj.put("nnum", val);															//数量
//				obj.put("vbatch", bodys.getJSONObject(i).getString("vbatchcode"));					//批次
//				obj.put("vfree1", bodys.getJSONObject(i).getString("vfree1"));
//				
//				jsDBBody.put(x + "", obj);
//				x++;
//			}
//		}
		
		//=====================
		JSONObject saveHeadJons = new JSONObject();
		//saveHeadJons.put("cgeneralhid", this.m_BillID);					//采购入库单ID
		saveHeadJons.put("coperatorid", MainLogin.objLog.UserID);			//操作员
		//saveHeadJons.put("bincode", this.m_PosCode);						//货位号
		saveHeadJons.put("pk_purcorp", pk_purcorp);
		saveHeadJons.put("pk_corp", pk_purcorp);	
		JSONObject saveJons = new JSONObject();
		saveJons.put("Head", jsDBHead);
		saveJons.put("Body", jsDBBody);
		
		//获取guid
		if(uploadGuid==null)
		{
		   uploadGuid = UUID.randomUUID();
		}
		saveJons.put("GUIDS", uploadGuid.toString());
		saveJons.put("tmpWHStatus", tmpWHStatus);
		saveJons.put("tmpBillStatus", tmpBillStatus);
		
		saveJons.put("cgeneralhid", this.m_BillID);//采购入库单ID
					
		if(!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }
		
		JSONObject jas= Common.DoHttpQuery(saveJons, "SaveToBill", "A"); 
		
		if(jas==null)
		{
			Toast.makeText(this, "单据保存过程中出现了问题," +
					"请尝试再次提交!", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
		}
		
		if(!jas.has("Status"))
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
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
			return false;
		}
		
		if(jas.getBoolean("Status"))
		{
			SaveFlg = 1;
			String lsResultBillCode = "";
			
			if(jas.has("BillCode"))
			{
				lsResultBillCode = jas.getString("BillCode");
			}
			else
			{
				Toast.makeText(this, "单据保存过程中出现了问题," +
						"请尝试再次提交!", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			DBBillCode=lsResultBillCode;
			
			
	   		//写入log文件
		}
		
		return true;
	}
	private void SaveOk()
	{
		
		if(!PurBillCode.equals(""))
		{
			Map<String,Object> mapResultBillCode = new HashMap<String,Object>();
			mapResultBillCode.put("BillCode", PurBillCode);
			ArrayList<Map<String,Object>> lstResultBillCode = new ArrayList<Map<String,Object>>();
			lstResultBillCode.add(mapResultBillCode);
			
			SimpleAdapter listItemAdapter = new SimpleAdapter(PurStockIn.this,lstResultBillCode,//数据源   
	                android.R.layout.simple_list_item_1,       
	                new String[] {"BillCode"},
	                new int[] {android.R.id.text1}  
	            ); 
			new AlertDialog.Builder(PurStockIn.this).setTitle("单据签字成功")
							.setAdapter(listItemAdapter, null)
							.setPositiveButton(R.string.QueRen,null).show();
				
			//写入log文件
	   		writeTxt = new writeTxt();
	   		
	   		Date day=new Date();    
	   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
	   		
	   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
	   		
	   		String BillCode = PurBillCode;
	   		String BillType = "45";
	   		String UserID = MainLogin.objLog.UserID;
	   		
	   		String LogName = BillType + UserID + dfd.format(day)+".txt";
	   		String LogMsg = df.format(day) + " " + "A" + " " + BillCode; 
	   		
	   		writeTxt.writeTxtToFile(LogName,LogMsg);
		}
		
		if(!DBBillCode.equals("无采购差异")&&!DBBillCode.equals(""))
		{
			Map<String,Object> mapResultBillCode = new HashMap<String,Object>();
			mapResultBillCode.put("BillCode", "1001 "+DBBillCode);
			ArrayList<Map<String,Object>> lstResultBillCode = new ArrayList<Map<String,Object>>();
			lstResultBillCode.add(mapResultBillCode);
			
			SimpleAdapter listItemAdapter = new SimpleAdapter(PurStockIn.this,lstResultBillCode,//数据源   
	                android.R.layout.simple_list_item_1,       
	                new String[] {"BillCode"},
	                new int[] {android.R.id.text1}  
	            ); 
			new AlertDialog.Builder(PurStockIn.this).setTitle(R.string.DanJuBaoCunChengGong)
							.setAdapter(listItemAdapter, null)
							.setPositiveButton(R.string.QueRen,null).show();
			
			//写入log文件
	   		writeTxt = new writeTxt();
	   		
	   		Date day=new Date();    
	   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  	   		
	   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");	   		
	   		String BillCode = DBBillCode;
	   		String BillType = "5X";
	   		String UserID = MainLogin.objLog.UserID;
	   		
	   		String LogName = BillType + UserID + dfd.format(day)+".txt";
	   		String LogMsg = df.format(day) + " " + "A" + " " + "1001 "+BillCode; 
	   		
	   		writeTxt.writeTxtToFile(LogName,LogMsg);
		}
		
		
	}
	
	private boolean UpdatePurOrder() throws JSONException, 
	ParseException, IOException
	{
		//暂存审核采购入库
		
		JSONObject saveHeadJons = new JSONObject();
		saveHeadJons.put("cgeneralhid", this.m_BillID);					//采购入库单ID
		saveHeadJons.put("coperatorid", MainLogin.objLog.UserID);		//操作员
		saveHeadJons.put("bincode", this.m_PosCode);					//货位号
		saveHeadJons.put("WarehouseID", this.m_WarehouseID);
		saveHeadJons.put("pk_purcorp", pk_purcorp);
		
		JSONArray head = new JSONArray();
		head = jsHead.getJSONArray("PurHead");
		saveHeadJons.put("pk_corp", head.getJSONObject(0).getString("pk_corp"));			
		
		JSONArray arrays =jsSerino.getJSONArray("Serino");
		JSONArray lstSerino = new JSONArray();
    	
		for(int i = 0;i<arrays.length();i++)
		{
			String OKFlg = "0";
			String sSerial = ((JSONObject)(arrays.get(i))).getString("sno");			
			String sBatch = ((JSONObject)(arrays.get(i))).getString("batch");
			String sInvCode = ((JSONObject)(arrays.get(i))).getString("invcode");
			String serino = ((JSONObject)(arrays.get(i))).getString("serino");
			serino = serino.replace("\n", "");		
			String totalnum = ((JSONObject)(arrays.get(i))).getString("box");		
			totalnum = Integer.valueOf(totalnum).toString();			
			String sbarcode = serino;
			String sfree1 = ((JSONObject)(arrays.get(i))).getString("vfree1");
			
			if (lstSerino.length()<1)
			{
				JSONObject map = new JSONObject();
				map.put("invcode",sInvCode) ;
				map.put("batch", sBatch);
				map.put("sno", sSerial);
				map.put("free1", sfree1);
				map.put("barcode", sbarcode);
				map.put("totalnum", totalnum);
				lstSerino.put(map);
			}
			else
			{
				for(int x = 0;x<lstSerino.length();x++)
				{
					String invcode= (String)((JSONObject)(lstSerino).get(x)).get("invcode");
					String batch= (String)((JSONObject)(lstSerino).get(x)).get("batch");
					String Serial= (String)((JSONObject)(lstSerino).get(x)).get("sno");
					
					if(Serial.equals(sSerial)
							&& invcode.equals(sInvCode)
							&& batch.equals(sBatch))
					{
						OKFlg = "1";
					}
				}
				if(OKFlg.equals("0"))
				{
					JSONObject map = new JSONObject();
					map.put("invcode",sInvCode) ;
					map.put("batch", sBatch);
					map.put("sno", sSerial);
					map.put("free1", sfree1);
					map.put("barcode", sbarcode);
					map.put("totalnum", totalnum);
					lstSerino.put(map);
					OKFlg = "1";
				}
			}			
		}
		
		JSONArray bodys = jsBody.getJSONArray("PurBody");
		jsDBBody = new JSONObject();
		int x = 0;
		for(int i = 0;i<bodys.length();i++)
		{		
//				int val = bodys.getJSONObject(i).getInt("nshouldinnum") -
//						bodys.getJSONObject(i).getInt("doneqty");
			
				JSONObject obj = new JSONObject();
				obj.put("cininvid", bodys.getJSONObject(i).getString("cinventoryid"));				//调入存货标识    
				obj.put("cinvbasid", bodys.getJSONObject(i).getString("cinvbasid"));			//存货基本标识   
				obj.put("cquoteunitid", bodys.getJSONObject(i).getString("cquoteunitid"));		//报价计量单位ID    
				obj.put("nnum", bodys.getJSONObject(i).getInt("doneqty"));															//数量
				obj.put("vbatch", bodys.getJSONObject(i).getString("vbatchcode"));					//批次
				obj.put("vfree1", bodys.getJSONObject(i).getString("vfree1"));					//自由项1 产地  	
				jsDBBody.put(x + "", obj);
				x++;
		}
		
		
		JSONObject saveJons = new JSONObject();
		saveJons.put("Head", saveHeadJons);
		saveJons.put("Body", jsDBBody);
		saveJons.put("lstSerino", lstSerino);
		//获取guid
		if(uploadGuid==null)
		{
		   uploadGuid = UUID.randomUUID();
		}
		saveJons.put("GUIDS", uploadGuid.toString());
		saveJons.put("tmpWHStatus", tmpWHStatus);
		saveJons.put("tmpBillStatus", tmpBillStatus);
		
		
		if(!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }
		
		JSONObject jas= Common.DoHttpQuery(saveJons, "UpdateInBound", "A"); 
		
		if(jas==null)
		{
			Toast.makeText(this, "单据保存过程中出现了问题," +
					"请尝试再次提交!", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
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
			return false;
		}
		
		if(jas.getBoolean("Status"))
		{
			SaveFlg = 0;
			String lsResultBillCode = "";
			
			if(jas.has("BillCode"))
			{
				lsResultBillCode = jas.getString("BillCode");
			}
			else
			{
				Toast.makeText(this, "单据保存过程中出现了问题," +
						"请尝试再次提交!", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			
			Map<String,Object> mapResultBillCode = new HashMap<String,Object>();
			mapResultBillCode.put("BillCode", lsResultBillCode);
			ArrayList<Map<String,Object>> lstResultBillCode = new ArrayList<Map<String,Object>>();
			lstResultBillCode.add(mapResultBillCode);
			
			SimpleAdapter listItemAdapter = new SimpleAdapter(PurStockIn.this,lstResultBillCode,//数据源   
	                android.R.layout.simple_list_item_1,       
	                new String[] {"BillCode"},
	                new int[] {android.R.id.text1}  
	            ); 
			new AlertDialog.Builder(PurStockIn.this).setTitle("单据暂存成功")
							.setAdapter(listItemAdapter, null)
							.setPositiveButton(R.string.QueRen,null).show();
				
//			//写入log文件
//	   		writeTxt = new writeTxt();
//	   		
//	   		Date day=new Date();    
//	   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
//	   		
//	   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
//	   		
//	   		String BillCode = lsResultBillCode;
//	   		String BillType = "45";
//	   		String UserID = MainLogin.objLog.UserID;
//	   		
//	   		String LogName = BillType + UserID + dfd.format(day)+".txt";
//	   		String LogMsg = df.format(day) + " " + "A" + " " + BillCode; 
//	   		
//	   		writeTxt.writeTxtToFile(LogName,LogMsg);
   		
	   		//写入log文件
			IniActivyMemor();
	   		
		}
		return true;
	}
	
	private boolean SavePurOrder() throws JSONException, 
				ParseException, IOException
	{
		//保存审核采购入库
		jsDBBody = new JSONObject();	

				
		JSONObject saveHeadJons = new JSONObject();
		saveHeadJons.put("cgeneralhid", this.m_BillID);					//采购入库单ID
		saveHeadJons.put("coperatorid", MainLogin.objLog.UserID);		//操作员
		saveHeadJons.put("bincode", this.m_PosCode);					//货位号
		saveHeadJons.put("WarehouseID", this.m_WarehouseID);
		saveHeadJons.put("pk_purcorp", pk_purcorp);
		saveHeadJons.put("pk_corp", "1001");	
		
		
		JSONArray lstSerino = new JSONArray();
		JSONArray head = new JSONArray();
		
		if(NoScanSave==false)
		{
			
			head = jsHead.getJSONArray("PurHead");
			
			
			JSONArray arrays =jsSerino.getJSONArray("Serino");
			
	    	
			for(int i = 0;i<arrays.length();i++)
			{
				String OKFlg = "0";
				String sSerial = ((JSONObject)(arrays.get(i))).getString("sno");			
				String sBatch = ((JSONObject)(arrays.get(i))).getString("batch");
				String sInvCode = ((JSONObject)(arrays.get(i))).getString("invcode");
				String serino = ((JSONObject)(arrays.get(i))).getString("serino");
				serino = serino.replace("\n", "");		
				String totalnum = ((JSONObject)(arrays.get(i))).getString("box");			
				totalnum = Integer.valueOf(totalnum).toString();			
				String sbarcode = serino;
				String sfree1 = ((JSONObject)(arrays.get(i))).getString("vfree1");
				
				if (lstSerino.length()<1)
				{
//					HashMap<String, Object> map = new HashMap<String, Object>();

					JSONObject map = new JSONObject();
					map.put("invcode",sInvCode) ;
					map.put("batch", sBatch);
					map.put("sno", sSerial);
					map.put("free1", sfree1);
					map.put("barcode", sbarcode);
					map.put("totalnum", totalnum);
					lstSerino.put(map);
				}
				else
				{
					for(int x = 0;x<lstSerino.length();x++)
					{

						//Object temp = lstSerino.get(x);
						String invcode= (String)((JSONObject)(lstSerino).get(x)).get("invcode");
						String batch= (String)((JSONObject)(lstSerino).get(x)).get("batch");
						String Serial= (String)((JSONObject)(lstSerino).get(x)).get("sno");
						
//						String invcode=temp.get("invcode");
//						String batch=(String)temp.get("batch");
//						String Serial=(String)temp.get("sno");
//						
						if(Serial.equals(sSerial)
								&& invcode.equals(sInvCode)
								&& batch.equals(sBatch))
						{
							OKFlg = "1";
						}
					}
					if(OKFlg.equals("0"))
					{
						//HashMap<String, Object> map = new HashMap<String, Object>();
						JSONObject map = new JSONObject();
						map.put("invcode",sInvCode) ;
						map.put("batch", sBatch);
						map.put("sno", sSerial);
						map.put("free1", sfree1);
						map.put("barcode", sbarcode);
						map.put("totalnum", totalnum);
						lstSerino.put(map);
						OKFlg = "1";
					}
				}
			}
		}
		
		 
		
				
		
		JSONObject saveJons = new JSONObject();
		
		saveJons.put("lstSerino", lstSerino);
		saveJons.put("Head", saveHeadJons);
		
		saveJons.put("tmpWHStatus", tmpWHStatus);
		saveJons.put("tmpBillStatus", tmpBillStatus);
		//获取guid
		if(uploadGuid==null)
		{
		   uploadGuid = UUID.randomUUID();
		}
		saveJons.put("GUIDS", uploadGuid.toString());
		
		
		if(NoScanSave==false)
		{
			JSONArray bodys = jsBody.getJSONArray("PurBody");
			if(tmpBillStatus.equals("N"))
			{
				int y = 0;	
				for(int i = 0;i<bodys.length();i++)
				{		

					JSONObject obj = new JSONObject();
					obj.put("cininvid", bodys.getJSONObject(i).getString("cinventoryid"));				//调入存货标识    
					obj.put("cinvbasid", bodys.getJSONObject(i).getString("cinvbasid"));			//存货基本标识   
					obj.put("cquoteunitid", bodys.getJSONObject(i).getString("cquoteunitid"));		//报价计量单位ID    
					obj.put("nnum", bodys.getJSONObject(i).getInt("doneqty")*-1);															//数量
					obj.put("vbatch", bodys.getJSONObject(i).getString("vbatchcode"));					//批次
					obj.put("vfree1", bodys.getJSONObject(i).getString("vfree1"));					//自由项1 产地  	
					jsDBBody.put(y + "", obj);
					y++;
				}
			}
		}
		

		saveJons.put("Body", jsDBBody);
		
		if(!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }
		JSONObject jas= Common.DoHttpQuery(saveJons, "VerifyInBound", "A"); 
		
		if(jas==null)
		{
			Toast.makeText(this, "单据保存过程中出现了问题," +
					"请尝试再次提交或!", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
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
			return false;
		}
		
		if(jas.getBoolean("Status"))
		{
			SaveFlg = 0;
			String lsResultBillCode = "";
			
			if(jas.has("BillCode"))
			{
				lsResultBillCode = jas.getString("BillCode");
			}
			else
			{
				Toast.makeText(this, "单据保存过程中出现了问题," +
						"请尝试再次提交或到电脑系统中确认后再决定是否继续保存!", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			
			PurBillCode = lsResultBillCode;
			
			
	   		
	   		
	   		//写入log文件
	   		
	   		
		}
		return true;
	}
	

	private boolean CheckBox() throws JSONException
	{
		if(jsBoxTotal == null||jsBoxTotal.length() == 0)
		{
			Toast.makeText(this, R.string.MeiYouXuYaoBaoCunDeSaoMiaoJiLu ,Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
		}
		
		JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");
		
		for(int i = 0;i<boxs.length();i++)
		{
			String serino = boxs.getJSONObject(i).getString("serial");
			String invcode = boxs.getJSONObject(i).getString("invcode");
			String batch = boxs.getJSONObject(i).getString("batch");
						
			int total = boxs.getJSONObject(i).getInt("total");
			int icurrent = boxs.getJSONObject(i).getInt("current");
			
			
			if(icurrent == total)
			{
				continue;
			}
			else
			{
				Toast.makeText(this, "存货:" + invcode + "序列号" + 
							serino + " 分包未扫描全，请重新扫描 " ,Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
		}		
		return true;
	}
	
	private DialogInterface.OnClickListener listenUpdate = new 
			DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog,
			int whichButton)
		{
			try {
				UpdatePurOrder();
			} catch (ParseException e) {
				Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage() ,
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e.printStackTrace();
			} catch (JSONException e) {
				Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage() ,
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai)+ e.getMessage()  ,
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e.printStackTrace();
			}
		}
	};
	
	
	private DialogInterface.OnClickListener listenSave2 = new 
			DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog,
			int whichButton)
		{
			NoScanSave = true;
			try {
				Save();
				
			} catch (ParseException e) {
				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
						Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
						Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
						Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	
	private DialogInterface.OnClickListener listenSave = new 
			DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog,
			int whichButton)
		{
			try {
				//SavePurOrder();//测试用
				if(tmpBillStatus.equals("Y"))
				{
					if(SavePurOrder()==true)
					{
						if(SaveDBOrder()==true)
						{
							SaveOk();
							IniActivyMemor();
						}
					}
				}
				//
				else
				{
					SavePurOrder();
				}
			} catch (ParseException e) {
				Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage() ,
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e.printStackTrace();
			} catch (JSONException e) {
				Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage() ,
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai)+ e.getMessage()  ,
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e.printStackTrace();
			}
		}
	};
	
  	private DialogInterface.OnClickListener listenExit = new 
  			DialogInterface.OnClickListener()
  	{
  		public void onClick(DialogInterface dialog,
  			int whichButton)
  		{
  			finish();	
  			Common.ReScanErr = false;
  			System.gc();
  		}
  	};
  	
  	
	private void Save1() throws JSONException, ParseException, IOException
	{
//		GetWHPosStatus();
//		if(tmpWHStatus.equals("Y"))
//		{
//			if(m_PosID.equals(""))
//			{
//				Toast.makeText(PurStockIn.this,R.string.QingShuRuHuoWeiHao, Toast.LENGTH_LONG).show();
//
//				//ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				//ADD CAIXY TEST END
//				return;
//			}
//		}
		Log.d("TAG", "SaveBoxTotal: " + jsBoxTotal);
		if(jsBoxTotal == null||jsBoxTotal.length() == 0)
		{
			 AlertDialog.Builder bulider = 
					 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("本次没有扫描明细" +
					 		"你确认要保存采购入库单吗"+"?");
			 bulider.setNegativeButton(R.string.QuXiao, null);
			 bulider.setPositiveButton(R.string.QueRen, listenSave2).create().show();
		}
		else
		{
			try {
				Save();
				
			} catch (ParseException e) {
				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
						Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
						Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
						Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
  	
  	
  	
	private void Save() throws JSONException, ParseException, IOException
	{
		if(m_BillID==null||m_BillID.equals(""))
		{
			Toast.makeText(this, "请先确认需要扫描的订单号", Toast.LENGTH_LONG).show();

			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		

		if(NoScanSave==true)
		{
			if(SavePurOrder()==true)
			{
				if(SaveDBOrder()==true)
				{
					SaveOk();
					IniActivyMemor();
				}
			}
			
			return;
		}

		

		if(!CheckBox())
			return;
		

		
		
		boolean isHaveDB = false;
		JSONArray bodys = jsBody.getJSONArray("PurBody");
		
		
		for(int i = 0;i<bodys.length();i++)
		{
			if(bodys.getJSONObject(i).getInt("doneqty")
					< bodys.getJSONObject(i).getInt("nshouldinnum"))
			{
				isHaveDB = true;
				break;
			}
		}
		if(isHaveDB&&SaveFlg==0&&tmpBillStatus.equals("Y"))
		{
			 AlertDialog.Builder bulider = 
					 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("有未扫描到的商品," +
					 		"你确认要保存吗"+"?");
			 bulider.setNegativeButton(R.string.QuXiao, null);
			 bulider.setPositiveButton(R.string.QueRen, listenSave).create().show();
		}
		else
		{
			SavePurOrder();
			SaveOk();
			IniActivyMemor();
		}
		 
		
	}
	
	private void Update() throws JSONException, ParseException, IOException
	{
		if(m_BillID==null||m_BillID.equals(""))
		{
			Toast.makeText(this, "请先确认需要扫描的订单号", Toast.LENGTH_LONG).show();

			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		
		if(!CheckBox())
			return;
		
		boolean isHaveDB = false;
		JSONArray bodys = jsBody.getJSONArray("PurBody");
		
		
		for(int i = 0;i<bodys.length();i++)
		{
			if(bodys.getJSONObject(i).getInt("doneqty")
					< bodys.getJSONObject(i).getInt("nshouldinnum"))
			{
				isHaveDB = true;
				break;
			}
		}
		if(isHaveDB&&SaveFlg==0)
		{
			 AlertDialog.Builder bulider = 
					 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage(
					 		"你确认要暂时保存采购入库单吗"+"?");
			 bulider.setNegativeButton(R.string.QuXiao, null);
			 bulider.setPositiveButton(R.string.QueRen, listenUpdate).create().show();
		}
		else
		{
			 AlertDialog.Builder bulider = 
					 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("改单据已经全部扫描完毕" +
					 		"请点击保存按钮");
	  		 bulider.setPositiveButton(R.string.QueRen, null).create().show();
			 
			 
		}
		 
		
	}
	
	  private void FindPositionByCode(String posCode) throws JSONException
	    {
	    		
			try
			{	
				
				if(m_BillID==null||m_BillID.equals(""))
				{
					Toast.makeText(this, "单据信息没有获得不能扫描货位", 
							Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					//this.txtPosition.setText("");
					return;
				}
				if(m_AccID==null||m_AccID.equals(""))
				{
					Toast.makeText(this, "仓库帐套还没有确认,不能先扫描货位", 
							Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					//this.txtPosition.setText("");
					return;
				}
				posCode = posCode.toUpperCase();
				posCode = posCode.replace("\n", "");
				
				JSONObject para = new JSONObject();
				para.put("FunctionName", "GetBinCodeInfo");
				para.put("CompanyCode",  m_companyCode);
				para.put("STOrgCode",  MainLogin.objLog.STOrgCode);
				para.put("WareHouse", this.m_WarehouseID);
				para.put("BinCode", posCode);
				para.put("TableName",  "position");		
				if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return ;
		        }
				JSONObject rev = Common.DoHttpQuery(para,
						"CommonQuery", m_AccID);	
				
				
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
						//txtPosition.setText("");
						Toast.makeText(this, "获取货位失败", Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						return;
					}
					String jposName,jposCode,jposID;
					JSONObject temp = val.getJSONObject(0);
					
					jposName = temp.getString("csname");
					jposCode = temp.getString("cscode");
					jposID = temp.getString("pk_cargdoc");
					
					
					//this.txtPosition.setText(jposCode);
					this.m_PosName = jposName;
					this.m_PosCode = jposCode;
					this.m_PosID = jposID;
					SaveScanedHead();
					
//					if(this.txtPosition.getText().toString().equals(""))
//					{
//						txtPosition.requestFocus();
//						return;
//					}
					return;				
				}
				else
				{
					//txtPosition.setText("");
					Toast.makeText(this, "获取货位失败", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return;
					
				}
				
			}
			 catch (JSONException e) {
					
					Toast.makeText(this, 
							R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();

					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					e.printStackTrace();
				} 
			catch(Exception e)
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();

				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			}
			
	    }
	    	  
	
	private void ShowOrderNoList(String Bar)
	{
		
		Intent PurOrder = new Intent(this,PurOrderList.class);
		
		PurOrder.putExtra("BillCode", Bar);
		startActivityForResult(PurOrder,96);
	}
	
	private void GetWHPosStatus() throws JSONException
    {
		JSONObject para = new JSONObject();
		para.put("FunctionName", "GetWHPosStatus");
		para.put("WareHouse", m_WarehouseID);

		if(!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return ;
        }
		JSONObject rev = null;
		try {
			rev = Common.DoHttpQuery(para, "CommonQuery", "A");
		} catch (ParseException e) {

			Toast.makeText(PurStockIn.this,"获取仓库状态失败", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		} catch (IOException e) {
			
			Toast.makeText(PurStockIn.this,"获取仓库状态失败", Toast.LENGTH_LONG).show();
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
			JSONArray val = rev.getJSONArray("position");				
			if(val.length() < 1)
			{
				Toast.makeText(PurStockIn.this,"获取仓库状态失败", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			
			String WHStatus;
			JSONObject temp = val.getJSONObject(0);
			
			WHStatus = temp.getString("csflag");
			
			tmpWHStatus = WHStatus;
			return;				
		}
		else
		{
			Toast.makeText(PurStockIn.this,"获取仓库状态失败", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
			
		}
		
		
    }
	
	private void ScanDetail()
	{
		if(m_BillID==null||m_BillID.equals(""))
		{
			Toast.makeText(this, "请先确认需要扫描的订单号", Toast.LENGTH_LONG).show();

			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		
		try {
			GetWHPosStatus();
		} catch (JSONException e) {
			Toast.makeText(PurStockIn.this,"获取仓库状态失败", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			
		}
		
		if(tmpWHStatus.equals(""))
		{
			return;
		}
		
		
		
		
		if(tmpWHStatus.equals("Y"))
		{
			if(m_PosID.equals(""))
			{
				Toast.makeText(PurStockIn.this,R.string.QingShuRuHuoWeiHao, Toast.LENGTH_LONG).show();
				
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
		}

		
		scanDetail = new Intent(PurStockIn.this,PurStockInDetail.class);
		
		scanDetail.putExtra("BillNo", this.m_BillNo);
		scanDetail.putExtra("BillID", this.m_BillID);
		
		scanDetail.putExtra("tmpWHStatus", tmpWHStatus);
		scanDetail.putExtra("tmpBillStatus", tmpBillStatus);
		scanDetail.putExtra("tmpWarehouseID", m_WarehouseID);
		
		if(tmpWHStatus.equals("Y"))
		{
			scanDetail.putExtra("tmpposID", m_PosID);
		}
		
		if(jsHead != null)
		{	       	 
			scanDetail.putExtra("Tag", "1");
	       	scanDetail.putExtra("head", jsHead.toString());
		}
		else
		{
			scanDetail.putExtra("Tag", "0");
		}
		if(jsBody != null)
		{
//	       	 SerializableJSONObject sjs = new SerializableJSONObject();
//	       	 sjs.setJs(jsBody);
	       	scanDetail.putExtra("body", jsBody.toString());
		}
		if(jsSerino != null)
		{
//	       	 SerializableJSONObject sjs = new SerializableJSONObject();
//	       	 sjs.setJs(jsSerino);
	       	 scanDetail.putExtra("serino", jsSerino.toString());
		}
		if(jsBoxTotal != null)
		{
//	       	 SerializableJSONObject sjs = new SerializableJSONObject();
//	       	 sjs.setJs(jsBoxTotal);
	       	scanDetail.putExtra("box", jsBoxTotal.toString());
		}
		scanDetail.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
		
		startActivityForResult(scanDetail,35);	
		
	}
	
//	private void Exit()
//	{
//		 AlertDialog.Builder bulider = 
//  				 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("你确认要退出吗?");
//  		 bulider.setNegativeButton("取消", null);
//  		 bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();
//				
//	}
	//创建对话框的按钮事件侦听	
    private Button.OnClickListener  myListner = new 
    		Button.OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				case R.id.btnPurInExit:
				{
					Exit();
					break;
				}
				case R.id.btnPurinUpdate:
				{
					if(tmpBillStatus.equals("Y"))
					{
						try {
							Update();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
									Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
									Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
									Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
						}
						break;
					}
					else
					{
						Toast.makeText(PurStockIn.this, "采购退货时无法使用暂存功能" ,
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						break;
					}
					
				}
				case R.id.btnPurinSave:
				{

					try 
					{
						
						Save1();
					} 
					catch (JSONException e) 
					{
						e.printStackTrace();
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					}
					break;
					
				}
				case R.id.btnPurInScan:
				{
					ScanDetail();
					break;
				}
				case R.id.btnPurBrower:
				{
					Common.ShowLoading(MyContext);
					if(jsDBBody == null || jsDBBody.length() < 1)
			    	{
			    		
			    	}
			    	else
			    	{
			    				Toast.makeText(PurStockIn.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
			    				break;
			    	}
					ShowOrderNoList("");
					//Common.cancelLoading();
					break;
				}
			}
		}    		
    };
    
    

    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pur_stock_in);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle("采购入库");
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		actionBar.show();
		
		SaveFlg=0;
		
		btnSave = (Button)findViewById(R.id.btnPurinSave);
		btnUpdate = (Button)findViewById(R.id.btnPurinUpdate);
		btnExit = (Button)findViewById(R.id.btnPurInExit);
		btnScan = (Button)findViewById(R.id.btnPurInScan);
		btnBrowOrderNo = (ImageButton)findViewById(R.id.btnPurBrower);
		
		labVendor = (TextView)findViewById(R.id.labPurVendor);
		txtPurOrderNo = (EditText)findViewById(R.id.txtPurOrderNo);
//		txtPosition = (EditText)findViewById(R.id.txtPurPosition);

		labWHName = (TextView)findViewById(R.id.labWHName);
		tvbillstatus = (TextView)findViewById(R.id.tvbillstatus);
		this.tvbillstatus.setText("  ");
		
		btnSave.setOnClickListener(myListner);
		btnUpdate.setOnClickListener(myListner);
		btnBrowOrderNo.setOnClickListener(myListner);
		btnExit.setOnClickListener(myListner);
		btnScan.setOnClickListener(myListner);
		
    	//ADD CAIXY START
//    	sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//    	MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//    	MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//    	//ADD CAIXY END
    	//m_companyCode = MainLogin.objLog.CompanyCode;
    	
    	txtPurOrderNo.setAllCaps(true);
    	//txtPosition.setAllCaps(true);
    	
    	btnSave.setFocusable(false);
    	btnUpdate.setFocusable(false);
    	btnBrowOrderNo.setFocusable(false);
    	btnExit.setFocusable(false);
    	btnScan.setFocusable(false);
    	
    	
		
		txtPurOrderNo.requestFocus();
		//this.txtPosition.addTextChangedListener(watcher);
		//this.txtPosition.setOnKeyListener(myTxtListener);
		this.txtPurOrderNo.setOnKeyListener(myTxtListener);	
		
    	UserID = MainLogin.objLog.UserID;
    	//String LogName = BillType + UserID + dfd.format(day)+".txt";
    	ScanedFileName = "45"+UserID+".txt";
    	fileName = "/sdcard/DVQ/45"+UserID+".txt";
    	fileNameScan = "/sdcard/DVQ/45Scan"+UserID+".txt";
    	
    	
    	file = new File(fileName);
    	fileScan = new File(fileNameScan);
    	ReScanHead();
    	MainMenu.cancelLoading();
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
    	String lsBillBarCode = "";
    	String lsm_WarehouseID = "";
    	String lspk_purcorp = "";
    	String lsm_BillID  = "";
    	String lsm_BillNo = "";
    	String lstmpBillStatus = "";
    	String lsPosCode = "";
    	String lsm_PosName = "";
    	String lsm_PosID = "";
    	
    	String lspk_calbody = "";
    	
    	
    	if(this.m_BillNo.equals(""))
    	{
    		lsBillBarCode = "null";
        	lsm_WarehouseID = "null";
        	lspk_purcorp = "null";
        	lsm_BillID  = "null";
        	lsm_BillNo = "null";
        	lstmpBillStatus = "null";
        	lspk_calbody = "null";
    	}
    	else
    	{
    		lsBillBarCode = m_BillNo;
        	lsm_WarehouseID = m_WarehouseID;
        	lspk_purcorp = pk_purcorp;
        	lsm_BillID  = m_BillID;
        	lsm_BillNo = m_BillNo;
        	lstmpBillStatus = tmpBillStatus;
        	lspk_calbody = pk_calbody;
    	}

    	
    	if(this.m_PosCode ==null||m_PosCode.equals(""))
    	{
    		lsPosCode = "null";
    		lsm_PosName = "null";
    		lsm_PosID = "null";
    	}
    	else
    	{
    		lsPosCode = m_PosCode;
    		lsm_PosName = m_PosCode;
    		lsm_PosID = m_PosID;
    	}
    	
		//ScanedHeadInfo.add(ScanedPosBar);
		if(file.exists())
		{
			file.delete();
		}

		//del walter 20170630 暂时不写入本地文本
//		writeTxt.writeTxtToFile(ScanedFileName,
//				lsBillBarCode +"|"
//		    	+lsm_WarehouseID +"|"
//		    	+lspk_purcorp +"|"
//		    	+lsm_BillID  +"|"
//		    	+lsm_BillNo +"|"
//		    	+lstmpBillStatus +"|"
//		    	+lsm_PosName +"|"
//		    	+lsm_PosID +"|"
//				+lsPosCode +"|"
//				+lspk_calbody);
		
    }
    
    private void Exit()
	{
//		ExitNameList =new String[2];
//		ExitNameList[0]="退出并保留缓存数据";
//		ExitNameList[1]="退出并删除缓存数据";
//
//		SelectButton=new AlertDialog.Builder(this).setTitle(R.string.QueRenTuiChu).setSingleChoiceItems(
//				ExitNameList, -1, buttonOnClick).setPositiveButton(R.string.QueRen,
//				buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
			AlertDialog.Builder bulider =
					new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("数据未保存是否退出");
			bulider.setNegativeButton(R.string.QuXiao, null);
			bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					IniActivyMemor();
					finish();
				}
			}).create().show();
	}

    
    private void IniActivyMemor()
	{

    	jsHead = null;
   		jsBody= null;
   		jsBoxTotal= null;
   		jsSerino= null;
   		scanDetail = null;
   		jsDBBody= null;
   		jsDBHead= null;
   		m_WarehouseID = "";
   		m_BillID= null;
   		m_BillNo= null;
   		m_PosCode= null;
   		m_PosName= null;
   		tmpBillStatus = "";
   		tmpWHStatus = "";
   		ScanedBarcode = new ArrayList<String>();
   		//txtPosition.setText("");
   		txtPurOrderNo.setText("");
   		labVendor.setText(" ----");
   		labWHName.setText(" ----");
   		tvbillstatus.setText(" ");
   		uploadGuid=null;
   		PurBillCode = "";
   		DBBillCode = "";
   		txtPurOrderNo.requestFocus();
		ScanedBarcode = new ArrayList<String>();
		if(file.exists())
		{
			file.delete();
		}
		
 		if(fileScan.exists())
 		{
 			fileScan.delete();
 		}
		
 		this.txtPurOrderNo.requestFocus();
		
		System.gc();
	}
    
    private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		public int index ;

		public ButtonOnClick(int index)
		{
			this.index = index;
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton)
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
							IniActivyMemor();
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
    
    private void ReScanErr(){
	 AlertDialog.Builder bulider = 
				 new AlertDialog.Builder(this).setTitle(R.string.CuoWu).setMessage("数据加载出现错误"+"\r\n"+"退出该模块并且再次尝试加载");

	 bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false).create().show();
	 MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	 return;
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
	 
	            fin.close(); 
	            
	            
	            String[] val;
	    		if(res.contains("|"))
	    		{
	    			ReScanHead = "0";
	    			val = res.split("\\|");
	    			
	    			if( val.length != 10)
	    			{
	    				ReScanHead = "1";
	    				return;
	    			}
	    			
	    			String lsBillBarCode = val[0];
	    	    	String lsm_WarehouseID = val[1];
	    	    	String lspk_purcorp = val[2];
	    	    	String lsm_BillID  = val[3];
	    	    	String lsm_BillNo = val[4];
	    	    	String lstmpBillStatus = val[5];
	    	    	String lsPosCode = val[6];
	    	    	String lsm_PosName = val[7];
	    	    	String lsm_PosID = val[8];
	    	    	String lspk_calbody = val[9];
	    	    	
	    	    	lspk_calbody = lspk_calbody.replace("\r\n", "");

	            	if(!lsBillBarCode.equals("null"))
	            	{        
	            		//ShowOrderNoList(lsBillBarCode);
	            		m_BillNo = lsBillBarCode;
	            		m_WarehouseID= lsm_WarehouseID;
	            		pk_purcorp= lspk_purcorp;
	            		m_BillID= lsm_BillID;
	            		m_BillNo= lsm_BillNo;
	            		pk_calbody = lspk_calbody;
	            		tmpBillStatus= lstmpBillStatus;
	            		this.txtPurOrderNo.setText(m_BillNo);
//	        			this.labVendor.setText("  "+vendor);
//	        			this.labWHName.setText("  "+warehouse);
	        			
	        			if(tmpBillStatus.equals("Y"))
	        			{
	        				this.tvbillstatus.setText("  ");
	        			}
	        			else
	        			{
	        				this.tvbillstatus.setText("采 购  退 货");
	        			}
	            		//txtPosition.requestFocus();
	            	}
	            	
	            	if(!lsPosCode.equals("null"))
	            	{
	            		m_PosCode = lsPosCode;
		    	    	m_PosName = lsm_PosName;
		    	    	m_PosID = lsm_PosID;
		    	    	//this.txtPosition.setText(lsPosCode);
	            	}
	            	
	            	int x=0;
	            	for(int i=0;i<1;i++)
            		{
            			
                		if(x>10)
                		{
//                			AlertDialog alertDialog = null;
//                			alertDialog = new AlertDialog.Builder(this).create(); 
//                	        alertDialog.setTitle("数据加载出现错误");  
//                	        alertDialog.setMessage("请在保持网络畅通的情况下尝试再次加载缓存数据");
//                	        alertDialog.setPositiveButton("确认", listenExit).create().show();
//                	        alertDialog.show();
                			
                			Common.ReScanErr = true;
                			ReScanErr();
                			return;
                		}
                		
                		GetWHPosStatus();
						if(tmpWHStatus == null || tmpWHStatus.equals(""))							
						{
							x++;
							i--;
						}
            		}	            	
	            	//tmpWHStatus
	            	
	            	if((!lsPosCode.equals("null")&&!lsBillBarCode.equals("null"))||
	            			(tmpWHStatus.equals("N")&&!lsBillBarCode.equals("null")))
	            	{
	            		ScanDetail();
	            	}
	            	
	            	ReScanHead = "1";

	    		}
	        } 
	 
	        catch (Exception e) { 
	 
	            e.printStackTrace(); 
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

	        } 
	    	
	    
	    }
		
	
	
    private OnKeyListener myTxtListener = new 
    		OnKeyListener()
    {
		@Override
		public boolean onKey(View v, int arg1, KeyEvent arg2) {
			{		
				if(arg1 == 66 && arg2.getAction() 
						== KeyEvent.ACTION_UP)
				{
					switch(v.getId())
					{				
//						case id.txtPurPosition:
//							String val = txtPosition.getText().toString();
//							val = val.toUpperCase();
//							val = val.replace("\n", "");
//
//							if(jsDBBody == null || jsDBBody.length() < 1)
//					    	{
//
//					    	}
//					    	else
//					    	{
//					    		String oldposname = m_PosCode;
//			    				Toast.makeText(PurStockIn.this, "该任务已经被扫描,无法修改货位。若要修改订单请先清除扫描货位", Toast.LENGTH_LONG).show();
//								//ADD CAIXY TEST START
//								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//								//ADD CAIXY TEST END
//								txtPosition.setText(oldposname);
//			    				break;
//					    	}
//							try {
//								FindPositionByCode(val);
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//								Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
//								//ADD CAIXY TEST START
//								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//								//ADD CAIXY TEST END
//							}
//							break;
						case id.txtPurOrderNo:
							if(jsDBBody == null || jsDBBody.length() < 1)
					    	{
					    		
					    	}
					    	else
					    	{
					    				Toast.makeText(PurStockIn.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
										//ADD CAIXY TEST START
										MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
										//ADD CAIXY TEST END
					    				break;
					    	}
							String valbar = txtPurOrderNo.getText().toString();
							valbar = valbar.replace("\r\n", "");
							valbar = valbar.replace("\n", "");
							txtPurOrderNo.setText("");
							ShowOrderNoList(valbar);
							break;
							
					}
				}
			}
			return false;
		}
    };
//	private  TextWatcher watcher = new TextWatcher()
//	{   
//		  
//        @Override  
//        public void afterTextChanged(Editable s) {   
//            // TODO Auto-generated method stub   
//            m_PosID="";
//        }   
//  
//        @Override  
//        public void beforeTextChanged(CharSequence s, int start, int count,   
//                int after) {   
//            // TODO Auto-generated method stub   
//               
//        }   
//  
//        @Override  
//        public void onTextChanged(CharSequence s, int start, int before,   
//                int count) {   
//    
//               
//        }   
//           
//    };
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pur_stock_in, menu);
		return true;
	}
	
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
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	{
		
		if(Common.ReScanErr==true)
    	{
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    		ReScanErr();
    		return;
    	}
    	
		if(requestCode==96)
		{
			if(resultCode != 1)
			{
//				labVendor.setText(" ----");
				txtPurOrderNo.setText("");
				txtPurOrderNo.requestFocus();
				return;
			}
			Bundle bundle = data.getExtras();      
			String orderNo = bundle.getString("result");
			String vendor = bundle.getString("Vendor");
			String warehouse = bundle.getString("Warehouse");
			
			m_WarehouseID = bundle.getString("WarehouseID");
			pk_purcorp = bundle.getString("pk_purcorp");
			pk_calbody = bundle.getString("pk_calbody");
			m_BillID = bundle.getString("BillId");
			m_BillNo = orderNo;

			tmpBillStatus = bundle.getString("billstatus");
			//map.put("billstatus", "N");//红字采购
	   		tmpWHStatus = "";
			m_PosCode = "";
			m_PosName = "";
			m_PosID = "";
			//txtPosition.setText("");
			this.txtPurOrderNo.setText(orderNo);
			this.labVendor.setText("  "+vendor);
			this.labWHName.setText("  "+warehouse);
			
			if(tmpBillStatus.equals("Y"))
			{
				this.tvbillstatus.setText("  ");
			}
			else
			{
				this.tvbillstatus.setText("采 购  退 货");
			}
			//this.txtPosition.requestFocus();
			SaveScanedHead();
		}
		if(requestCode == 35)
		{


			if(resultCode != 1)
			{
				return;
			}
			
			Bundle bundle = data.getExtras();
			
			String boxJS = bundle.getString("box");
			String serJS = bundle.getString("serino");
			String bodyJS = bundle.getString("body");
			String headJS = bundle.getString("head");
			ScanedBarcode = bundle.getStringArrayList("ScanedBarcode");
			
			try 
			{
				
				this.jsBody = new JSONObject(bodyJS);			
				this.jsHead = new JSONObject(headJS);
				this.jsSerino = new JSONObject(serJS);
				this.jsBoxTotal = new JSONObject(boxJS);
			}
			catch (JSONException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();

			Toast.makeText(PurStockIn.this, e.getMessage() ,
					Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			}
		}
	}
}
