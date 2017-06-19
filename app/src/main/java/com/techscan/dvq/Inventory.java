package com.techscan.dvq;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Inventory 
{
	private String m_InvCode;
	private String m_InvName;
	private String m_Batch;
	private String m_Serino;	
	private String m_Invbasdoc;
	private String m_Invmandoc;
	private String m_vFree1;
	
	private String m_currentID;
	
	private String m_totalID;
	private String m_Errmsg;
	
	private String m_AccID;
	
	/**
	 * 获取帐套号
	 */
	public String AccID()
	{
		return m_AccID;
	}
	
	/**
	 * 设置帐套号
	 */
	public void SetAccID(String value)
	{
		m_AccID=value;
	}
	
	public String Invbasdoc()
	{
		return m_Invbasdoc;
	}
	
	public String vFree1()
	{
		//m_vFree1 = "意大利";
		return m_vFree1;
	}
	/**
	 * 返回条码中的当前分包ID
	 */
	public String currentID()
	{
		return m_currentID;
	}
	
	public void  SetcurrentID(String value)
	{
		m_currentID=value;
	}
	/**
	 * 分包的总数量
	 */
	public String totalID()
	{
		return m_totalID;
	}
	
	public void SettotalID(String value)
	{
		m_totalID=value; 
	}
	
	public void SetvFree1(String value)
	{
		m_vFree1=value;
	}
	public String Invmandoc()
	{
		return m_Invmandoc;
	}
	
	public void SetBatch(String value)
	{
		m_Batch = value;
	}
	public String GetBatch()
	{
		return m_Batch;
	}
	public void SetSerino(String value)
	{
		m_Serino = value;
	}
	public String GetSerino()
	{
		return m_Serino;
	}
	public String getInvCode()
	{
		return m_InvCode;
	}
	public String getInvName()
	{
		return m_InvName;
	}
	public String getErrMsg()
	{
		return m_Errmsg;
	}
	/**
	 * 获取物料基本信息,
	 * @param  cInvcode 物料号
	 * @param  BizType 业务类型 BADV 代表货位调整,因为货位调整只有pk_copr=1001公司进行,所以当传入BADV时,代表写死了公司code 当A帐套为101 ,B帐套为1
	 * 其他的业务再考虑
	 */
	public Inventory(String cInvcode,String BizType,String AccID) throws JSONException, ParseException, IOException
	{
		//从数据库里读取
		
//		String lgUser = MainLogin.objLog.LoginUser;
//		String lgPwd = MainLogin.objLog.Password;		
//		String LoginString =  MainLogin.objLog.LoginString;	
		
		JSONObject para = new JSONObject();
		String CompanyCode=BizType;
		if(BizType.equals("BADV"))
		{
			if(AccID.equals("A"))
			{
			CompanyCode="101";
			}
			else if(AccID.equals("B"))
			{
				CompanyCode="1";
			}
		}
		
		if(AccID.equals("B"))
		{
			CompanyCode="1";
		}
		
		para.put("FunctionName", "GetInvBaseInfo");
		para.put("CompanyCode",CompanyCode);
		para.put("InvCode",  cInvcode);
		para.put("TableName",  "inventory");
	
		try
		{		
			JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", AccID);
			
			
			if(rev==null)
			{
				m_Errmsg = "网络操作出现问题!请稍后再试";
				return;
			}
			if(!rev.has("Status"))
				return;
			if(rev.getBoolean("Status"))
			{
				if(!rev.has("inventory"))
					return;
				JSONArray val = rev.getJSONArray("inventory");
				m_InvCode = val.getJSONObject(0).getString("invcode");
				m_InvName = val.getJSONObject(0).getString("invname");
				m_Invbasdoc = val.getJSONObject(0).getString("pk_invbasdoc");
				m_Invmandoc = val.getJSONObject(0).getString("pk_invmandoc");
			}
			else
			{
				if(!rev.has("ErrMsg"))
					return;
				m_Errmsg = rev.getString("ErrMsg");
			}
			
		}
		catch(Exception e)
		{
			//m_Errmsg = "网络操作出现问题!请稍后再试";
		}
		
	}
//	public Inventory(JSONObject obj,int orderType) throws JSONException
//	{
//		switch(orderType)
//		{
//			case 1:	//调出订单
//				m_InvCode = obj.getString("invcode").toString();
//				m_InvName = obj.getString("invname").toString();
//			break;
//		}
//	}
}
