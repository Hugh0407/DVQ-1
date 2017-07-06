package com.techscan.dvq.common;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;

/**
 * Created by liuya on 2017/6/21.
 * 工具类
 */

public class Utils {
    public static final int HANDER_DEPARTMENT = 1;
    public static final int HANDER_STORG = 2;
    public static final int HANDER_SAVE_RESULT = 3;
    public static final int HANDER_POORDER_HEAD = 4;
    public static final int HANDER_POORDER_BODY = 5;

    public static String formatTime(long time) {
        java.util.Date date = new java.util.Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static final String jsonXu = "{\"Head\":{\"COPERATORID\":\"0001AA1000000005M8V8\",\"CBIZTYPE\":\"0001TC100000000003AM\",\"VDEF5\":\"\",\"PK_CORP\":\"4100\",\"CRECEIPTTYE\":\"4331\",\"CSALECORPID\":\"0001TC1000000000033A\",\"VDEF1\":\"外销部\",\"VDEF2\":\"朱佳球\",\"NOTOTALNUMBER\":\"200.00\"},\"GUIDS\":\"ebd3e8c9-ab85-415b-a26e-41737d2a379d\",\"Body\":{\"ScanDetails\":[{\"NTOTALOUTINVNUM\":10,\"CSOURCEBILLBODYID\":\"1011AA1000000004KWR9\",\"CRECEIVECUSTBASID\":\"0001TC10000000003WSN\",\"CINVBASDOCID\":\"0001TC100000000013T9\",\"NNUMBER\":\"1.00\",\"CBIZTYPE\":\"0001TC100000000003AM\",\"CSOURCEBILLID\":\"1011AA1000000004KWR8\",\"VRECEIVEADDRESS\":\"送货地址：江苏省淮安市经济开发区韩泰南路1号\",\"CRECEIVEAREAID\":\"\",\"CCUSTMANDOCID\":\"0001TC10000000004N2L\",\"CSENDWAREID\":\"1011TC100000000000LF\",\"CSENDCALBODYID\":\"1011TC100000000000KV\",\"VBATCHCODE\":\"123\",\"CINVMANDOCID\":\"0001TC10000000001OQP\",\"CCUSTBASDOCID\":\"0001TC10000000003WSN\",\"VRECEIVEPERSON\":\"a1\",\"PK_SENDCORP\":\"1011\"}]}}";
    public static final String jsonWang ="{\"tmpWHStatus\":\"\",\"Body\":{\"ScanDetails\":[{\"VSOURCEBILLROWNO\":\"10\",\"NORDERNUM\":100,\"BLOTMGT\":\"1\",\"SOURCCEBILLHID\":\"1011AA1000000004KS57\",\"VENDORID\":\"0001TC1000000000XYU6\",\"PK_BODYCALBODY\":\"1011TC100000000000KV\",\"CINVENTORYID\":\"0001TC10000000001OO2\",\"VENDORBASID\":\"0001TC1000000000XYIE\",\"SOURCCEBILLBID\":\"1011AA1000000004KS58\",\"VSOURCEBILLCODE\":\"OP41001706301603\",\"VBATCHCODE\":\"R1404088-2\",\"NINNUM\":60,\"NPRICE\":\"0.00\",\"CINVCODE\":\"02011\",\"CINVBASID\":\"0001TC1000000000148D\"}]},\"GUIDS\":\"88f1e3ae-e0b1-4e43-af09-e72735b244f3\",\"lstSerino\":[{\"barcode\":\"TC|02011|R1404088-2|0|10|6|0001\",\"invcode\":\"02011\",\"sno\":\"0001\",\"totalnum\":\"60.0\",\"free1\":\"\",\"batch\":\"R1404088-2\"}],\"Head\":{\"PK_CALBODY\":\"1011TC100000000000KV\",\"CBIZTYPE\":\"0001TC100000000003AE\",\"PK_CORP\":\"4100\",\"CDISPATCHERID\":\"0001TC100000000011QH\",\"VBILLCODE\":\"PI201707060003\",\"VNOTE\":\"333\",\"CDPTID\":\"1011TC100000000000LU\",\"CUSERNAME\":\"a1\",\"CWAREHOUSEID\":\"1011TC100000000000LH\",\"FREPLENISHFLAG\":\"N\",\"CUSER\":\"0001AA1000000005M8V8\"},\"tmpBillStatus\":\"\"}";
}
