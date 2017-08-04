package com.techscan.dvq.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Hugh on 2017/7/16.
 */

public class PurSaleOutGoods implements Parcelable{
    String invCode;
    String spec;
    String type;
    String invName;
    String number;
    String num_task = "0";

    public PurSaleOutGoods() {
    }

    public String getInvCode() {
        return invCode;
    }

    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNum_task() {
        return num_task;
    }

    public void setNum_task(String num_task) {
        this.num_task = num_task;
    }

    protected PurSaleOutGoods(@NonNull Parcel in) {
        invCode = in.readString();
        spec = in.readString();
        type = in.readString();
        invName = in.readString();
        number = in.readString();
        num_task = in.readString();
    }

    public static final Creator<PurSaleOutGoods> CREATOR = new Creator<PurSaleOutGoods>() {
        @NonNull
        @Override
        public PurSaleOutGoods createFromParcel(@NonNull Parcel in) {
            return new PurSaleOutGoods(in);
        }

        @NonNull
        @Override
        public PurSaleOutGoods[] newArray(int size) {
            return new PurSaleOutGoods[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(invCode);
        dest.writeString(spec);
        dest.writeString(type);
        dest.writeString(invName);
        dest.writeString(number);
        dest.writeString(num_task);
    }
}
