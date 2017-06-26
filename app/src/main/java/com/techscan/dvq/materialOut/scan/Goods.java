package com.techscan.dvq.materialOut.scan;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cloverss on 2017/6/22.
 * 货物对象
 */

public class Goods implements Parcelable{

    String name;
    float qty;
    int num;
    String encoding;

    public Goods() {
    }

    protected Goods(Parcel in) {
        name = in.readString();
        qty = in.readFloat();
        num = in.readInt();
        encoding = in.readString();
    }

    public static final Creator<Goods> CREATOR = new Creator<Goods>() {
        @Override
        public Goods createFromParcel(Parcel in) {
            return new Goods(in);
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeFloat(qty);
        dest.writeInt(num);
        dest.writeString(encoding);
    }
}
