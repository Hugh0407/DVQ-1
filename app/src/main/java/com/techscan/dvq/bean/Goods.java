package com.techscan.dvq.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liuya on 2017/6/22.
 * 材料出库 对象
 */

public class Goods implements Parcelable {


    String barcode;     // 条码
    String encoding;    //编码（sku）
    String name;        //名称
    String type;        //类型
    String unit;        //单位
    String lot;         //批次
    String spec;        //规格
    float  qty;         //总量
    int    num;         //数目
    String pk_invbasdoc;
    String pk_invmandoc;
    String pk_invmandoc_cost;
    String costObject;
    String manual;  // 海关手册号

    public Goods() {
    }

    protected Goods(Parcel in) {
        barcode = in.readString();
        encoding = in.readString();
        name = in.readString();
        type = in.readString();
        unit = in.readString();
        lot = in.readString();
        spec = in.readString();
        qty = in.readFloat();
        num = in.readInt();
        pk_invbasdoc = in.readString();
        pk_invmandoc = in.readString();
        pk_invmandoc_cost = in.readString();
        costObject = in.readString();
        manual = in.readString();
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Goods other = (Goods) obj;
        if (pk_invbasdoc == null) {
            if (other.pk_invbasdoc != null)
                return false;
        } else if (!pk_invbasdoc.equals(other.pk_invbasdoc))
            return false;
        if (lot == null) {
            if (other.lot != null)
                return false;
        } else if (!lot.equals(other.lot))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + ((lot == null) ? 0 : lot.hashCode());
        result = prime * result + ((pk_invbasdoc == null) ? 0 : pk_invbasdoc.hashCode());
        return result;
    }

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

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
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

    public String getPk_invmandoc_cost() {
        return pk_invmandoc_cost;
    }

    public void setPk_invmandoc_cost(String pk_invmandoc_cost) {
        this.pk_invmandoc_cost = pk_invmandoc_cost;
    }

    public String getCostObject() {
        return costObject;
    }

    public void setCostObject(String costObject) {
        this.costObject = costObject;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barcode);
        dest.writeString(encoding);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(unit);
        dest.writeString(lot);
        dest.writeString(spec);
        dest.writeFloat(qty);
        dest.writeInt(num);
        dest.writeString(pk_invbasdoc);
        dest.writeString(pk_invmandoc);
        dest.writeString(pk_invmandoc_cost);
        dest.writeString(costObject);
        dest.writeString(manual);
    }
}
