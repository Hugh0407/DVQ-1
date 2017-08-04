package com.techscan.dvq.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by liuya on 2017/6/30.
 * 形态转换 任务列表的对象
 */

public class PurGood implements Parcelable {
    String sourceBill;
    String AccID;
    String vbillcode;
    String nshouldinnum;    //数量
    String invcode;
    String invname;
    String vbatchcode;
    String fbillrowflag;
    String num_task = "0";
    String cwarehouseid;
    String pk_calbody;
    String coperatorid;
    String csourcebillbid;
    String csourcebillhid;
    String CINVBASID;
    String cinventoryid;
    String pk_bodycalbody;  //业务类型
    String vsourcebillcode;  //转换单单号
    String vsourcerowno;  //行号
    String pk_invbasdoc;


    public PurGood() {
    }

    protected PurGood(@NonNull Parcel in) {
        sourceBill = in.readString();
        AccID = in.readString();
        vbillcode = in.readString();
        nshouldinnum = in.readString();
        invcode = in.readString();
        invname = in.readString();
        vbatchcode = in.readString();
        fbillrowflag = in.readString();
        num_task = in.readString();
        cwarehouseid = in.readString();
        pk_calbody = in.readString();
        coperatorid = in.readString();
        csourcebillbid = in.readString();
        csourcebillhid = in.readString();
        CINVBASID = in.readString();
        cinventoryid = in.readString();
        pk_bodycalbody = in.readString();
        vsourcebillcode = in.readString();
        vsourcerowno = in.readString();
        pk_invbasdoc = in.readString();
    }

    public static final Creator<PurGood> CREATOR = new Creator<PurGood>() {
        @NonNull
        @Override
        public PurGood createFromParcel(@NonNull Parcel in) {
            return new PurGood(in);
        }

        @NonNull
        @Override
        public PurGood[] newArray(int size) {
            return new PurGood[size];
        }
    };

    public String getSourceBill() {
        return sourceBill;
    }

    public void setSourceBill(String sourceBill) {
        this.sourceBill = sourceBill;
    }

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

    public String getFbillrowflag() {
        return fbillrowflag;
    }

    public void setFbillrowflag(String fbillrowflag) {
        this.fbillrowflag = fbillrowflag;
    }

    public String getNum_task() {
        return num_task;
    }

    public void setNum_task(String num_task) {
        this.num_task = num_task;
    }

    public String getCwarehouseid() {
        return cwarehouseid;
    }

    public void setCwarehouseid(String cwarehouseid) {
        this.cwarehouseid = cwarehouseid;
    }

    public String getPk_calbody() {
        return pk_calbody;
    }

    public void setPk_calbody(String pk_calbody) {
        this.pk_calbody = pk_calbody;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public String getCsourcebillbid() {
        return csourcebillbid;
    }

    public void setCsourcebillbid(String csourcebillbid) {
        this.csourcebillbid = csourcebillbid;
    }

    public String getCsourcebillhid() {
        return csourcebillhid;
    }

    public void setCsourcebillhid(String csourcebillhid) {
        this.csourcebillhid = csourcebillhid;
    }

    public String getCINVBASID() {
        return CINVBASID;
    }

    public void setCINVBASID(String CINVBASID) {
        this.CINVBASID = CINVBASID;
    }

    public String getCinventoryid() {
        return cinventoryid;
    }

    public void setCinventoryid(String cinventoryid) {
        this.cinventoryid = cinventoryid;
    }

    public String getPk_bodycalbody() {
        return pk_bodycalbody;
    }

    public void setPk_bodycalbody(String pk_bodycalbody) {
        this.pk_bodycalbody = pk_bodycalbody;
    }

    public String getVsourcebillcode() {
        return vsourcebillcode;
    }

    public void setVsourcebillcode(String vsourcebillcode) {
        this.vsourcebillcode = vsourcebillcode;
    }

    public String getVsourcerowno() {
        return vsourcerowno;
    }

    public void setVsourcerowno(String vsourcerowno) {
        this.vsourcerowno = vsourcerowno;
    }

    public String getPk_invbasdoc() {
        return pk_invbasdoc;
    }

    public void setPk_invbasdoc(String pk_invbasdoc) {
        this.pk_invbasdoc = pk_invbasdoc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(sourceBill);
        dest.writeString(AccID);
        dest.writeString(vbillcode);
        dest.writeString(nshouldinnum);
        dest.writeString(invcode);
        dest.writeString(invname);
        dest.writeString(vbatchcode);
        dest.writeString(fbillrowflag);
        dest.writeString(num_task);
        dest.writeString(cwarehouseid);
        dest.writeString(pk_calbody);
        dest.writeString(coperatorid);
        dest.writeString(csourcebillbid);
        dest.writeString(csourcebillhid);
        dest.writeString(CINVBASID);
        dest.writeString(cinventoryid);
        dest.writeString(pk_bodycalbody);
        dest.writeString(vsourcebillcode);
        dest.writeString(vsourcerowno);
        dest.writeString(pk_invbasdoc);
    }
}
