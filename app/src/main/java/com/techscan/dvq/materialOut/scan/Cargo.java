package com.techscan.dvq.materialOut.scan;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cloverss on 2017/6/22.
 * 货物对象
 */

public class Cargo implements Parcelable{

    String name;
    int qty;
    int num;
    String encoding;

    public Cargo() {
    }

    protected Cargo(Parcel in) {
        name = in.readString();
        qty = in.readInt();
        num = in.readInt();
        encoding = in.readString();
    }

    public static final Creator<Cargo> CREATOR = new Creator<Cargo>() {
        @Override
        public Cargo createFromParcel(Parcel in) {
            return new Cargo(in);
        }

        @Override
        public Cargo[] newArray(int size) {
            return new Cargo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(qty);
        dest.writeInt(num);
        dest.writeString(encoding);
    }
}
