package com.techscan.dvq.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by XuHu on 2017/6/27.
 * 成品销售出库
 */

public class SaleOutGoods implements Parcelable{
    String invCode;
    String invName;
    String unit;
    String batch;
    float qty;
    int num;
    String barcode;
    String pk_invbasdoc;
    String pk_invmandoc;
//    String

    public SaleOutGoods() {
    }

    protected SaleOutGoods(Parcel in) {
        invCode = in.readString();
        invName = in.readString();
        unit = in.readString();
        batch = in.readString();
        qty = in.readFloat();
        num = in.readInt();
        barcode = in.readString();
        pk_invbasdoc = in.readString();
        pk_invmandoc = in.readString();
    }

    public static final Creator<SaleOutGoods> CREATOR = new Creator<SaleOutGoods>() {
        @Override
        public SaleOutGoods createFromParcel(Parcel in) {
            return new SaleOutGoods(in);
        }

        @Override
        public SaleOutGoods[] newArray(int size) {
            return new SaleOutGoods[size];
        }
    };

    public String getInvCode() {
        return invCode;
    }

    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPk_invbasdoc() {
        return pk_invbasdoc;
    }

    public void setPk_invbasdoc(String pk_invbasdoc) {
        this.pk_invbasdoc = pk_invbasdoc;
    }

    public String getPk_invmandoc() {
        return pk_invmandoc;
    }

    public void setPk_invmandoc(String pk_invmandoc) {
        this.pk_invmandoc = pk_invmandoc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(invCode);
        dest.writeString(invName);
        dest.writeString(unit);
        dest.writeString(batch);
        dest.writeFloat(qty);
        dest.writeInt(num);
        dest.writeString(barcode);
        dest.writeString(pk_invbasdoc);
        dest.writeString(pk_invmandoc);
    }
}
