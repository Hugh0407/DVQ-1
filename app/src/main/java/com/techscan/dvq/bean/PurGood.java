package com.techscan.dvq.bean;

/**
 * Created by liuya on 2017/6/30.
 * 形态转换 任务列表的对象
 */

public class PurGood {
    String sourceBill;
    String AccID;
    String vbillcode;
    String nshouldinnum;    //数量
    String invcode;
    String invname;
    String vbatchcode;

    public String getAccID() {
        return AccID;
    }

    public void setAccID(String accID) {
        AccID = accID;
    }

    public String getVbillcode() {
        return vbillcode;
    }

    public void setVbillcode(String vbillcode) {
        this.vbillcode = vbillcode;
    }

    public String getNshouldinnum() {
        return nshouldinnum;
    }

    public void setNshouldinnum(String nshouldinnum) {
        this.nshouldinnum = nshouldinnum;
    }

    public String getInvcode() {
        return invcode;
    }

    public void setInvcode(String invcode) {
        this.invcode = invcode;
    }

    public String getInvname() {
        return invname;
    }

    public void setInvname(String invname) {
        this.invname = invname;
    }

    public String getVbatchcode() {
        return vbatchcode;
    }

    public void setVbatchcode(String vbatchcode) {
        this.vbatchcode = vbatchcode;
    }

    public String getSourceBill() {
        return sourceBill;
    }

    public void setSourceBill(String sourceBill) {
        this.sourceBill = sourceBill;
    }
}
