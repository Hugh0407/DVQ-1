package com.techscan.dvq.bean;

import android.os.Parcel;
import android.os.Parcelable;

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

    public PurGood() {
    }

    protected PurGood(Parcel in) {
        sourceBill = in.readString();
        AccID = in.readString();
        vbillcode = in.readString();
        nshouldinnum = in.readString();
        invcode = in.readString();
        invname = in.readString();
        vbatchcode = in.readString();
        fbillrowflag = in.readString();
        num_task = in.readString();
    }

    public static final Creator<PurGood> CREATOR = new Creator<PurGood>() {
        @Override
        public PurGood createFromParcel(Parcel in) {
            return new PurGood(in);
        }

        @Override
        public PurGood[] newArray(int size) {
            return new PurGood[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sourceBill);
        dest.writeString(AccID);
        dest.writeString(vbillcode);
        dest.writeString(nshouldinnum);
        dest.writeString(invcode);
        dest.writeString(invname);
        dest.writeString(vbatchcode);
        dest.writeString(fbillrowflag);
        dest.writeString(num_task);
    }
}
