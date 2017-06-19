package com.techscan.dvq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 用来把表Jason转到Hashtable 或者把hashtable转到Jason中,这个我主要是使用在把分包的数据记录起来传递用的
 */
public class JTFH {

	public static List<Map<String,String>> FromSONToMap(JSONObject json,String JasonName)
	{
		List<Map<String,String>> lists=new  ArrayList<Map<String, String>>();
		try {
			JSONArray jarray=(JSONArray)json.get(JasonName);
			for(int i=0;i<jarray.length();i++)
			{
				Map<String,String> map = new HashMap<String, String>();
			
				  Iterator<String> keys=((JSONObject)jarray.get(i)).keys();
				  String key="";
				  while(keys.hasNext()){  
			           key=keys.next();  
			           try
			           {
			         String o=((JSONObject)jarray.get(i)).get(key).toString();  
			         map.put(key, o);
			         
			           }
			           catch(Exception ex)
			           {
			        	   json.put(key, "null"); 
			           }
				  }
				  lists.add(map);
			}
			return lists;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static String ToJSONFMap(List<Map<String,String>> maps,String JasonName)
	{
		if(maps==null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
			sb.append("{\"Status\":true,");
			sb.append("\""+JasonName+"\":[");
			for(int  i=0;i< maps.size();i++)
			{
				JSONObject objects=new JSONObject(maps.get(i));
				objects=decodeJSONObject(objects);
				sb.append(objects.toString());
				if(i!=maps.size()-1)
				{sb.append(",");}
			}
			sb.append("]");
			sb.append("}");   
			return sb.toString();
	}
	
	public static JSONObject decodeJSONObject(JSONObject json){  
	       Iterator<String> keys=json.keys();  
  JSONObject jo=null;  
	       Object o;  
	        String key;  
	      while(keys.hasNext()){  
	           key=keys.next();  
	           try
	           {
	          o=json.get(key);  
	           }
	           catch(Exception ex)
	           {
	        	   try {
					json.put(key, "null");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	           }
       } 
	      return json;
	  } 
	/**
	 * 用来把jobject转化为hashtable
	 */
	public static Hashtable ToHashtable(JSONObject jobj)
	{
		Iterator itor=jobj.keys();
		Hashtable hs =null;
		
    	while(itor.hasNext())
    	{
    		if(hs==null)
    		{
    			hs=new Hashtable();
    		}
	    	String Key=itor.next().toString();
	    	try {
				JSONArray arrays =jobj.getJSONArray(Key);
			    ArrayList array =new ArrayList();
			  for(int i=0;i<arrays.length();i++)
			  {
				array.add(arrays.get(i).toString());
			  }
			  hs.put(Key, array);
			}
	    	catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return hs;
		
	}
	/**
	 * 把hashTable中的值转换为JSON
	 */
	public static JSONObject FromHashtable(Hashtable hs) throws JSONException
	{
		if(hs==null)
		{
			return null;
		}
		Enumeration  enu= hs.keys();
		JSONObject jobject=new JSONObject();
		Object obj =null;
		
		while(enu.hasMoreElements())
		{obj=enu.nextElement();
			ArrayList list=(ArrayList)hs.get(obj.toString());
			JSONArray  jarray =new JSONArray();
			for(int i=0;i<list.size();i++)
			{
				jarray.put(list.get(i).toString());
			}
			jobject.put(obj.toString(), jarray);
		}
		return jobject;
		//JSONObject has=(JSONObject)bundle.getSerializable("hashTable");
//    	Iterator itor=has.keys();
//    	while(itor.hasNext())
//    	{
//    	String Key=itor.next().toString();
//    	try {
//			JSONArray arrays =has.getJSONArray(Key);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
