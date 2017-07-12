package com.techscan.dvq;

import android.widget.Switch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public String ScanBarCode = "";

    public String LoginString = "";
    public String LoginString2 = "";
    public static String lsUrl = "";
    //public static String lsUrl = "";
    private static writeTxt writeTxt;
    public String LoginUser = "";
    public String LoginDate;
    public String Password = "";
    public String CompanyCode = "";
    public static boolean ReScanErr = false;
    public static JSONArray arysUserRole = new JSONArray();
    public static JSONArray arysUserWHRole = new JSONArray();
    public static JSONObject jsonBodyTask = new JSONObject();
    public static JSONObject JsonModTaskData = new JSONObject();

    public String UserID = ""; // A帐套对应的用户ID
    public String UserName = "";
    public String CompanyID = "";
    public String STOrgCode = ""; // 库存组织编码

    public String WhCodeA = "";// A帐套仓库过滤条件

    public String WhCodeB = ""; // //B帐套仓库过滤条件
    public String UserIDB = "";// B帐套对应的用户ID

    public String VersionCode = "";// 程序版本号
    public String CompanyName = "";// 公司名称

    static android.app.ProgressDialog PD;

    public static JSONArray RemoveItem(JSONArray list, int index)
            throws JSONException {
        JSONArray result = new JSONArray();

        for (int i = 0; i < list.length(); i++) {
            if (i == index)
                continue;
            result.put(list.getJSONObject(i));
        }
        return result;
    }

    //static int UrlErr = 0;   //暂时关闭

    public static JSONObject DoHttpQuery(JSONObject para, String funcationName,
                                         String AccID) throws JSONException, ParseException, IOException {
        // String ErrMsg="{\"Status\":false,\"ErrMsg\":'执行数据处理错误'}";
        JSONObject resultJos = new JSONObject();
        //lsUrl = MainLogin.objLog.LoginString;


        if (lsUrl.equals("")) {
            lsUrl = MainLogin.objLog.LoginString;
        }

        //暂时关闭
//		if(UrlErr > 0)
//		{
//			if(lsUrl.equals(MainLogin.objLog.LoginString))
//			{
//				lsUrl = MainLogin.objLog.LoginString2;
//			}
//			else if(lsUrl.equals(MainLogin.objLog.LoginString2))
//			{
//				lsUrl = MainLogin.objLog.LoginString;
//			}
//		}	


        String lgUser = MainLogin.objLog.LoginUser;
        String lgPwd = MainLogin.objLog.Password;
        // String LoginString = MainLogin.objLog.LoginString;
        String LoginString = lsUrl;

        HttpPost httpPost = new HttpPost(LoginString);
        httpPost.addHeader("Self-Test", funcationName);
        httpPost.addHeader("User-Code", lgUser);
        httpPost.addHeader("User-Pwd", lgPwd);
        httpPost.addHeader("Data-Source", AccID);
        httpPost.addHeader("User-Company", MainLogin.objLog.CompanyCode);
        httpPost.addHeader("Org-Code", MainLogin.objLog.STOrgCode);
        httpPost.addHeader("Version-Code", MainLogin.objLog.VersionCode);

        HttpEntity entity = null;
        HttpResponse httpResponse = null;
        try {
            entity = new StringEntity(para.toString(), "gb2312");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            // e1.printStackTrace();
            // resultJos.put("Status", false);
            // resultJos.put("ErrMsg", "网络操作出现问题!请稍后再试");
            // return resultJos;
        } catch (Exception ex) {
            // //mod start 20150417 walter
            // //resultJos=new JSONObject(ErrMsg);
            // resultJos.put("Status", false);
            // resultJos.put("ErrMsg", "网络操作出现问题!请稍后再试");
            // //mod end 20150417 walter
            // return resultJos;
        }

        HttpClient defaults = null;

        try {
            defaults = MyHttpClient.getSaveHttpClient();
            httpPost.setEntity(entity);
            httpResponse = defaults.execute(httpPost);
        } catch (ClientProtocolException e) {
            // e.printStackTrace();
            // resultJos.put("Status", false);
            // resultJos.put("ErrMsg", "网络操作出现问题!请稍后再试");
            // return resultJos;
        } catch (IOException e) {
            return null;
//			if(UrlErr>2)
//			{
//				UrlErr = 0;
//
//				return null;
//			}
//			else
//			{
//				UrlErr++;
//				DoHttpQuery(para, funcationName,AccID);
//			}
        } catch (Exception e) {
            return null;
//			if(UrlErr>2)
//			{
//				UrlErr = 0;
//
//				return null;
//			}
//			else
//			{
//				UrlErr++;
//				DoHttpQuery(para, funcationName,AccID);
//			}
        }
        try {
            System.gc();
            int res = 0;
            if (httpResponse != null) {
                res = httpResponse.getStatusLine().getStatusCode();
            }
            if (res == 200) {
                //UrlErr = 0;
                String result = EntityUtils.toString(httpResponse.getEntity());
                String jasstr = EncodingUtils.getString(EncodingUtils.getBytes(result, "ISO8859-1"), "gb2312");

                JSONObject jas = null;
                try {
                    jas = new JSONObject(jasstr);
                } catch (Exception ex) {
                    // //mod start 20150417 walter
                    // //String a="";
                    // resultJos.put("Status", false);
                    // resultJos.put("ErrMsg", "网络操作出现问题!请稍后再试");
                    // return resultJos;
                    // //mod end 20150417 walter

                }

                // boolean status= jas.getBoolean("Status");
                return jas;
                // if(!status)
                // {

                // }
            }
//			else {
//				return null;
//			}
        } catch (Exception e) {

//			if(UrlErr>2)
//			{
//				UrlErr = 0;
//
//				return null;
//			}
//			else
//			{
//				UrlErr++;
//				DoHttpQuery(para, funcationName,AccID);
//			}

            // e.printStackTrace();
            // resultJos.put("Status", false);
            // resultJos.put("ErrMsg", e.getMessage());
            // return resultJos;
        } finally {
            if (defaults != null) {
                defaults.getConnectionManager().shutdown();
                MyHttpClient.clearHttp();
            }
        }
        return null;
    }

    public static void SetBodyTask(JSONObject jsonBodyTask1) {
        jsonBodyTask = new JSONObject();
        jsonBodyTask = jsonBodyTask1;
    }

    public static void SetModTaskData(JSONObject JsonModTaskData1) {
        JsonModTaskData = new JSONObject();
        JsonModTaskData = JsonModTaskData1;
    }

    public static void ClearIntDate() {
        jsonBodyTask = new JSONObject();
        JsonModTaskData = new JSONObject();
    }

    // Map转JSONObject
    public static JSONObject MapTOJSONOBject(Map<String, Object> map)
            throws JSONException {
        JSONObject jsonResult = new JSONObject();
        Set<Map.Entry<String, Object>> entryseSet = map.entrySet();
        for (Map.Entry<String, Object> entry : entryseSet) {
            jsonResult.put(entry.getKey(), entry.getValue());
        }
        return jsonResult;
    }

    public static boolean CheckUserRole(String AccID, String Pk_Corp,
                                        String Fun_Code) {
        //return true;

        String UserRole = "On";
        if (UserRole.equals("Off")) {
            return true;
        }

        for (int i = 0; i < arysUserRole.length(); i++) {
            try {
                String lsAccID = (String) arysUserRole.getJSONObject(i).get(
                        "AccID");
                String lsPk_Corp = (String) arysUserRole.getJSONObject(i).get(
                        "pk_corp");
                String lsFun_Code = (String) arysUserRole.getJSONObject(i).get(
                        "fun_code");
                if (AccID.equals("") && Pk_Corp.equals("")) {
                    if (lsFun_Code.equals(Fun_Code)) {
                        return true;
                    }
                } else {
                    if (lsAccID.equals(AccID) && lsPk_Corp.equals(Pk_Corp) && lsFun_Code.equals(Fun_Code)) {
                        return true;
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;

    }

    public static boolean CheckUserWHRole(String AccID, String Pk_Corp,
                                          String pk_stordoc) {
        String UserWHRole = "On";
        if (UserWHRole.equals("Off")) {
            return true;
        }

        if (arysUserWHRole == null) {
            return true;
        }

        String isFind_Corp = "";
        for (int i = 0; i < arysUserWHRole.length(); i++) {
            try {
                String lsAccID = (String) arysUserWHRole.getJSONObject(i).get("AccID");
                String lspk_Corp = (String) arysUserWHRole.getJSONObject(i).get("pk_corp");

                if (lsAccID.equals(AccID) && lspk_Corp.equals(Pk_Corp)) {
                    isFind_Corp = Pk_Corp;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (isFind_Corp.equals("")) {
            return true;
        } else {
            for (int i = 0; i < arysUserWHRole.length(); i++) {
                try {
                    String lsAccID = (String) arysUserWHRole.getJSONObject(i).get("AccID");
                    String lspk_Corp = (String) arysUserWHRole.getJSONObject(i).get("pk_corp");
                    String lspk_stordoc = (String) arysUserWHRole.getJSONObject(i).get("pk_stordoc");
                    //String isorgcontrolpower = (String) arysUserWHRole.getJSONObject(i).get("isorgcontrolpower");

                    if (lsAccID.equals(AccID) && lspk_Corp.equals(Pk_Corp) & lspk_stordoc.equals(pk_stordoc)) {
                        return true;
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


        return false;

    }


    public static void ShowLoading(android.content.Context context) {
//		alertDialog.setTitle("数据加载中,请稍等。。。");
//		alertDialog.setMessage("");
//		alertDialog.show();
        PD = android.app.ProgressDialog.show(context, "Loading...", "数据加载中,请稍等。。。", true, false);
    }

    public static void cancelLoading() {

//		alertDialog.cancel();
        PD.dismiss();
    }

    //日期比较
    public static Boolean CompareDate(String sStartDate, String sEndDate){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dStartDate = sdf.parse(sStartDate);
            Date dEndDate = sdf.parse(sEndDate);
            if(dStartDate.getTime() <= dEndDate.getTime())
                return true;
            else
                return false;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

}
