package com.techscan.dvq;

/**
 * Created by Hugh on 2017/6/21.
 */

//成品
//		单包
//		P|SKU|LOT|WW|TAX|QTY|CW|ONLY|SN
//		托盘
//		TP|SKU|LOT|WW|TAX|QTY|NUM|CW|ONLY|SN

public class SplitTongChengBarCode {
    public String P ;
    public String TP;
    public String WW;//委外
    public String TAX;//完税保税
    public String QTY;//重量
    public String Weights;//总重
    public String CW;
    public String ONLY;
    public String NUM;
    public String AccID = "";
    public String cInvCode = "";//存货编码
    public String cBatch = "";//批次
    public String cSerino = "";//流水号
    public String FinishBarCode = "";
    public String CheckBarCode = "";

    public boolean creatorOk=false;

    public SplitTongChengBarCode(String barcode) {
        String[] val;
        if (barcode.contains("|")) {
            val = barcode.split("\\|");
            if (val.length != 9 && val.length != 10) {
                creatorOk = false;
                return;
            }

            cInvCode = val[1];
            cBatch = val[2];
            WW = val[3];
            TAX = val[4];
            QTY = val[5];
            if (val.length == 9) {
                P = val[0];
                Weights = QTY;
                CW = val[6];
                ONLY = val[7];
                cSerino = val[8];
                FinishBarCode= P + "|" + cInvCode + "|" + cBatch + "|" + WW + "|" +
                        TAX+ "|" +  QTY + "|" + CW + "|" + ONLY + "|"  +  cSerino;

            } else if (val.length == 10) {
                TP=val[0];
                NUM = val[6];
               float qty =  Float.valueOf(val[5]);
                float num =  Float.valueOf(val[6]);
                Weights =String.valueOf(qty*num);
                CW = val[7];
                ONLY = val[8];
                cSerino = val[9];
                FinishBarCode= TP + "|" + cInvCode + "|" + cBatch + "|" + WW + "|" +
                        TAX+ "|" +  QTY + "|" +NUM+"|" + CW + "|" + ONLY + "|"  +  cSerino;

            }

            creatorOk = true;
//            FinishBarCode = AccID + "|" + cInvCode + "|" +
//                    cBatch + "|" + QTY + "|" + "|" + cSerino;
//            CheckBarCode = AccID + "|" + cInvCode + "|" +
//                    cBatch + "|" + cSerino;


        }
    }
}
