package com.techscan.dvq.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by liuya on 2017/6/22.
 * 材料出库 对象
 */

public class Goods implements Parcelable {

    //Y | C | TC   g
    //原总重        g
    String  barcode;     // 条码 g
    String  encoding;    //编码（sku） g
    String  name;        //名称
    String  type;        //类型
    String  unit;        //单位
    String  lot;         //批次
    String  productLot;  //生产批次
    String  spec;        //规格
    float   qty;         //总量 g
    int     num;         //数目
    String  codeType;    // g
    boolean isDoPacked;  //是否拆包 g
    String  barQty;       //条码上显示的总重 g
    String  pk_invbasdoc;
    String  pk_invmandoc;
    String  pk_invmandoc_cost;
    String  costObject;
    String  costObjName;
    String  manual;  // 海关手册号

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
        codeType = in.readString();
        isDoPacked = in.readByte() != 0;
        barQty = in.readString();
        pk_invbasdoc = in.readString();
        pk_invmandoc = in.readString();
        pk_invmandoc_cost = in.readString();
        costObject = in.readString();
        manual = in.readString();
        productLot = in.readString();
        costObjName = in.readString();
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
    public boolean equals(@Nullable Object obj) {
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

        if (productLot == null) {
            if (other.productLot != null)
                return false;
        } else if (!productLot.equals(other.productLot))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = encoding != null ? encoding.hashCode() : 0;
        result = 31 * result + (lot != null ? lot.hashCode() : 0);
        result = 31 * result + (productLot != null ? productLot.hashCode() : 0);
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

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public boolean isDoPacked() {
        return isDoPacked;
    }

    public void setDoPacked(boolean doPacked) {
        isDoPacked = doPacked;
    }

    public String getBarQty() {
        return barQty;
    }

    public void setBarQty(String barQty) {
        this.barQty = barQty;
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

    public String getProductLot() {
        return productLot;
    }

    public void setProductLot(String productLot) {
        this.productLot = productLot;
    }

    public String getCostObjName() {
        return costObjName;
    }

    public void setCostObjName(String costObjName) {
        this.costObjName = costObjName;
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
        dest.writeString(codeType);
        dest.writeByte((byte) (isDoPacked ? 1 : 0));
        dest.writeString(barQty);
        dest.writeString(pk_invbasdoc);
        dest.writeString(pk_invmandoc);
        dest.writeString(pk_invmandoc_cost);
        dest.writeString(costObject);
        dest.writeString(manual);
        dest.writeString(productLot);
        dest.writeString(costObjName);
    }
}
