package com.techscan.dvq.module.saleout.scan;

import android.os.Handler;
import android.support.annotation.Nullable;

import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SplitBarcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Xuhu on 2017/7/8.
 */

public class GetSaleBaseInfo {

    @Nullable
    public HashMap<String, Object> mapSaleBaseInfo = null;
    String InvCode = "";
    Double a=0.0;
    Double b=0.0;

    /**
     * 获取存货基本信息
     */
    public GetSaleBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler, String PK_CORP) {
        //判断invcode是否有逗号。如果有逗号就分割，拿逗号后面的invcode；否则就拿原来的invcode。
        InvCode = cSplitBarcode.cInvCode;
        if (InvCode.contains(",")) {
            String[] incCodeArray = InvCode.split("\\,");
            InvCode = incCodeArray[1];
        } else {
            InvCode = cSplitBarcode.cInvCode;
        }
        mapSaleBaseInfo = new HashMap<String, Object>();
        mapSaleBaseInfo.put("barcodetype", cSplitBarcode.BarcodeType);
        mapSaleBaseInfo.put("batch", cSplitBarcode.cBatch);
        mapSaleBaseInfo.put("serino", cSplitBarcode.cSerino);
        mapSaleBaseInfo.put("quantity", cSplitBarcode.dQuantity);
        mapSaleBaseInfo.put("number", cSplitBarcode.iNumber);
        a = Double.valueOf(cSplitBarcode.dQuantity);
        b = Double.valueOf(cSplitBarcode.iNumber);
        mapSaleBaseInfo.put("barqty", a*b);
        mapSaleBaseInfo.put("cwflag", cSplitBarcode.CWFlag);
        mapSaleBaseInfo.put("onlyflag", cSplitBarcode.OnlyFlag);
        mapSaleBaseInfo.put("taxflag", cSplitBarcode.TaxFlag);
        mapSaleBaseInfo.put("outsourcing", cSplitBarcode.Outsourcing);
        mapSaleBaseInfo.put("barcode", cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", PK_CORP);
        parameter.put("BarCode","");
        parameter.put("InvCode", InvCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    public GetSaleBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler, String PK_CORP,String barcode) {
        //判断invcode是否有逗号。如果有逗号就分割，拿逗号后面的invcode；否则就拿原来的invcode。
        InvCode = cSplitBarcode.cInvCode;
        if (InvCode.contains(",")) {
            String[] incCodeArray = InvCode.split("\\,");
            InvCode = incCodeArray[1];
        } else {
            InvCode = cSplitBarcode.cInvCode;
        }
        mapSaleBaseInfo = new HashMap<String, Object>();
        mapSaleBaseInfo.put("barcodetype", cSplitBarcode.BarcodeType);
        mapSaleBaseInfo.put("batch", cSplitBarcode.cBatch);
        mapSaleBaseInfo.put("serino", cSplitBarcode.cSerino);
        mapSaleBaseInfo.put("quantity", cSplitBarcode.dQuantity);
        mapSaleBaseInfo.put("number", cSplitBarcode.iNumber);
        a = Double.valueOf(cSplitBarcode.dQuantity);
        b = Double.valueOf(cSplitBarcode.iNumber);
        mapSaleBaseInfo.put("barqty", a*b);
        mapSaleBaseInfo.put("cwflag", cSplitBarcode.CWFlag);
        mapSaleBaseInfo.put("onlyflag", cSplitBarcode.OnlyFlag);
        mapSaleBaseInfo.put("taxflag", cSplitBarcode.TaxFlag);
        mapSaleBaseInfo.put("outsourcing", cSplitBarcode.Outsourcing);
        mapSaleBaseInfo.put("barcode", cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", PK_CORP);
        parameter.put("BarCode",barcode);
        parameter.put("InvCode", InvCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 3);
        Thread        td            = new Thread(requestThread);
        td.start();
    }


    /**
     * 通过获取到的json 解析得到物料信息,并设置到UI上
     *
     * @param json
     * @throws JSONException
     */

    public void SetSaleBaseToParam(JSONObject json) throws JSONException {
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                mapSaleBaseInfo.put("invname", tempJso.getString("invname"));   //物料名称
                mapSaleBaseInfo.put("invcode", tempJso.getString("invcode"));   //物料号
                mapSaleBaseInfo.put("measname", tempJso.getString("measname"));   //单位
                mapSaleBaseInfo.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));//物料bas PK
                mapSaleBaseInfo.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));//物料man PK
                mapSaleBaseInfo.put("invtype", tempJso.getString("invtype"));   //型号
                mapSaleBaseInfo.put("invspec", tempJso.getString("invspec"));   //规格
                mapSaleBaseInfo.put("currentweight", tempJso.getString("currentweight"));   //规格
//                mapSaleBaseInfo.put("vfree5", tempJso.getString("vfree5"));   //规格
            }

        }
    }


}
