package com.techscan.dvq.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cloverss on 2017/6/22.
 * 货物对象
 */

public class Goods implements Parcelable{


    String barcode;
    String encoding;
    String name;
    String type;
    String unit;
    String lot;
    float qty;
    int num;
    String pk_invbasdoc;
    String pk_invmandoc;


    public Goods() {

    }

    protected Goods(Parcel in) {
        barcode = in.readString();
        encoding = in.readString();
        name = in.readString();
        type = in.readString();
        unit = in.readString();
        lot = in.readString();
        qty = in.readFloat();
        num = in.readInt();
        pk_invbasdoc = in.readString();
        pk_invmandoc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barcode);
        dest.writeString(encoding);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(unit);
        dest.writeString(lot);
        dest.writeFloat(qty);
        dest.writeInt(num);
        dest.writeString(pk_invbasdoc);
        dest.writeString(pk_invmandoc);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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
}
