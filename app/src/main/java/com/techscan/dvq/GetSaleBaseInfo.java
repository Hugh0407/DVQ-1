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
    String InvCode = "";
    String Batch = "";

    /**
     * 获取存货基本信息
     *
     *
     */
    public GetSaleBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler,String PK_CORP) {
        //判断invcode是否有逗号。如果有逗号就分割，拿逗号后面的invcode；否则就拿原来的invcode。
        InvCode = cSplitBarcode.cInvCode;
        if (InvCode.contains(",")){
            String[] invCodeArray = InvCode.split("\\,");
            InvCode = invCodeArray[1];
        }else{
            InvCode = cSplitBarcode.cInvCode;
        }
        //判断batch是否有逗号。如果有逗号就分割，拿逗号后面的batch；否则就拿原来的batch
        Batch = cSplitBarcode.cBatch;
        if (Batch.contains(",")){
            String[] batchArray = Batch.split("\\,");
            Batch = batchArray[1];
        }else{
            Batch = cSplitBarcode.cBatch;
        }
        mapSaleBaseInfo = new HashMap<String, Object>();
        mapSaleBaseInfo.put("barcodetype",cSplitBarcode.BarcodeType);
        mapSaleBaseInfo.put("batch",Batch);
        mapSaleBaseInfo.put("serino",cSplitBarcode.cSerino);
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
        parameter.put("InvCode", InvCode);
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
