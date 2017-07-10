package com.techscan.dvq;

import android.os.Handler;

import com.techscan.dvq.common.RequestThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Xuhu on 2017/7/8.
 */

public class GetSaleBaseInfo {

    public HashMap<String,Object> mapSaleBaseInfo = null;

    /**
     * 获取存货基本信息
     *
     *
     */
    public GetSaleBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler,String PK_CORP) {
        mapSaleBaseInfo = new HashMap<String, Object>();
        mapSaleBaseInfo.put("barcodetype",cSplitBarcode.BarcodeType);
        mapSaleBaseInfo.put("batch",cSplitBarcode.cBatch);
        mapSaleBaseInfo.put("serino",cSplitBarcode.cSerino);
        mapSaleBaseInfo.put("cinvcode",cSplitBarcode.cInvCode);
        mapSaleBaseInfo.put("quantity",cSplitBarcode.dQuantity);
        mapSaleBaseInfo.put("number",cSplitBarcode.iNumber);
        mapSaleBaseInfo.put("cwflag",cSplitBarcode.CWFlag);
        mapSaleBaseInfo.put("onlyflag",cSplitBarcode.OnlyFlag);
        mapSaleBaseInfo.put("taxflag",cSplitBarcode.TaxFlag);
        mapSaleBaseInfo.put("outsourcing",cSplitBarcode.Outsourcing);
        mapSaleBaseInfo.put("barcode",cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", PK_CORP);
        parameter.put("InvCode", cSplitBarcode.cInvCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * 通过获取到的json 解析得到物料信息,并设置到UI上
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    public void SetSaleBaseToParam(JSONObject json) throws JSONException {
        //Log.d(TAG, "SetInvBaseToUI: " + json);
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            //mapInvBaseInfo = null;
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                //mapInvBaseInfo = new HashMap<String, Object>();
                mapSaleBaseInfo.put("invname", tempJso.getString("invname"));   //物料名称
                mapSaleBaseInfo.put("invcode", tempJso.getString("invcode"));   //物料号
                mapSaleBaseInfo.put("measname", tempJso.getString("measname"));   //单位
                mapSaleBaseInfo.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));//物料bas PK
                //pk_invbasdoc = tempJso.getString("pk_invbasdoc");
                mapSaleBaseInfo.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));//物料man PK
                //pk_invmandoc = tempJso.getString("pk_invmandoc");
                mapSaleBaseInfo.put("invtype", tempJso.getString("invtype"));   //型号
                mapSaleBaseInfo.put("invspec", tempJso.getString("invspec"));   //规格
            }

        }
    }
}
