package com.techscan.dvq;

import android.support.annotation.Nullable;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;


public class MyHttpClient 
{
    @Nullable
    private static       HttpClient mHttpClient = null;
    private static final String     CHARSET     = HTTP.UTF_8;
    //将构造函数封掉，只能通过对外接口来获取HttpClient实例  
    private MyHttpClient(){  
  
    }  
    @Nullable
    public static HttpClient getHttpClient(){
        if(mHttpClient == null){  
            mHttpClient = new DefaultHttpClient();  
        }  
        return mHttpClient;  
    }  
    @Nullable
    public static synchronized HttpClient getSaveHttpClient(){
        if(mHttpClient == null){  
            HttpParams params = new BasicHttpParams();  
            //设置基本参数  
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
            HttpProtocolParams.setContentCharset(params, "gb2312");  
            HttpProtocolParams.setUseExpectContinue(params, true);  
            
            
            //超时设置  
            /*从连接池中取连接的超时时间*/  
            ConnManagerParams.setTimeout(params, 1000);  
            /*连接超时*/  
            HttpConnectionParams.setConnectionTimeout(params, 5000);  
            /*请求超时*/  
            HttpConnectionParams.setSoTimeout(params, 60000);  
            //设置HttpClient支持HTTp和HTTPS两种模式  
            SchemeRegistry schReg = new SchemeRegistry();  
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));  
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));  
            //使用线程安全的连接管理来创建HttpClient  
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);  
            mHttpClient = new DefaultHttpClient(conMgr, params);  
        }  
        return mHttpClient;  
    }  
	
	
	
	  public static void clearHttp()
	  {
		  mHttpClient = null;
	  }
	  
}