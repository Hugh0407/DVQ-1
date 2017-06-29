package com.techscan.dvq;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.techscan.dvq.common.RequestThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by walter on 2017/6/29.
 */

public class GetInvBaseInfo {

    public HashMap<String,Object> mapInvBaseInfo = null;

    /**
     * 获取存货基本信息
     *
     *
     */
    public GetInvBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler) {
        mapInvBaseInfo = new HashMap<String, Object>();
        mapInvBaseInfo.put("barcodetype",cSplitBarcode.BarcodeType);
        mapInvBaseInfo.put("batch",cSplitBarcode.cBatch);
        mapInvBaseInfo.put("serino",cSplitBarcode.cSerino);
        mapInvBaseInfo.put("quantity",cSplitBarcode.dQuantity);
        mapInvBaseInfo.put("number",cSplitBarcode.iNumber);
        mapInvBaseInfo.put("taxflag",cSplitBarcode.TaxFlag);
        mapInvBaseInfo.put("outsourcing",cSplitBarcode.Outsourcing);
        mapInvBaseInfo.put("barcode",cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
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

    public void SetInvBaseToParam(JSONObject json) throws JSONException {
        //Log.d(TAG, "SetInvBaseToUI: " + json);
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            //mapInvBaseInfo = null;
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                //mapInvBaseInfo = new HashMap<String, Object>();
                mapInvBaseInfo.put("invname", tempJso.getString("invname"));   //物料名称
                mapInvBaseInfo.put("invcode", tempJso.getString("invcode"));   //物料号
                mapInvBaseInfo.put("measname", tempJso.getString("measname"));   //单位
                mapInvBaseInfo.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));//物料bas PK
                //pk_invbasdoc = tempJso.getString("pk_invbasdoc");
                mapInvBaseInfo.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));//物料man PK
                //pk_invmandoc = tempJso.getString("pk_invmandoc");
                mapInvBaseInfo.put("invtype", tempJso.getString("invtype"));   //型号
                mapInvBaseInfo.put("invspec", tempJso.getString("invspec"));   //规格
                mapInvBaseInfo.put("oppdimen", tempJso.getString("oppdimen"));
            }

        }
    }
}
