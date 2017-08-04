package com.techscan.dvq.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by XuHu on 2017/6/27.
 * 成品销售出库
 */

public class SaleOutGoods implements Parcelable {
    String invCode;
    String spec;
    String type;
    String invName;
    String unit;
    String batch;
    String barcode;
    float qty;
    String crowno;
    String cinvbasdocid;
    String cinventoryid;
    String cadvisecalbodyid;
    String corder_bid;
    String csaleid;
    String pk_corp;
    String number;
    String pk_invbasdoc;
    String pk_invmandoc;
    String vreceiveaddress;
    String vfree4;  // 海关手册号
    String vsourcerowno;//来源单据行号


    public SaleOutGoods() {
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public float getQty() {
        return qty;
    }

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
        if (batch == null) {
            if (other.lot != null)
                return false;
        } else if (!batch.equals(other.lot))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batch == null) ? 0 : batch.hashCode());
        result = prime * result + ((pk_invbasdoc == null) ? 0 : pk_invbasdoc.hashCode());
        return result;
    }
    public void setQty(float qty) {
        this.qty = qty;
    }

    public String getCrowno() {
        return crowno;
    }

    public void setCrowno(String crowno) {
        this.crowno = crowno;
    }

    public String getCinvbasdocid() {
        return cinvbasdocid;
    }

    public void setCinvbasdocid(String cinvbasdocid) {
        this.cinvbasdocid = cinvbasdocid;
    }

    public String getCinventoryid() {
        return cinventoryid;
    }

    public void setCinventoryid(String cinventoryid) {
        this.cinventoryid = cinventoryid;
    }

    public String getCadvisecalbodyid() {
        return cadvisecalbodyid;
    }

    public void setCadvisecalbodyid(String cadvisecalbodyid) {
        this.cadvisecalbodyid = cadvisecalbodyid;
    }

    public String getCorder_bid() {
        return corder_bid;
    }

    public void setCorder_bid(String corder_bid) {
        this.corder_bid = corder_bid;
    }

    public String getCsaleid() {
        return csaleid;
    }

    public void setCsaleid(String csaleid) {
        this.csaleid = csaleid;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getVreceiveaddress() {
        return vreceiveaddress;
    }

    public void setVreceiveaddress(String vreceiveaddress) {
        this.vreceiveaddress = vreceiveaddress;
    }

    protected SaleOutGoods(@NonNull Parcel in) {
        invCode = in.readString();
        spec = in.readString();
        type = in.readString();
        invName = in.readString();
        unit = in.readString();
        batch = in.readString();
        barcode = in.readString();
        qty = in.readFloat();
        crowno = in.readString();
        cinvbasdocid = in.readString();
        cinventoryid = in.readString();
        cadvisecalbodyid = in.readString();
        corder_bid = in.readString();
        csaleid = in.readString();
        pk_corp = in.readString();
        number = in.readString();
        pk_invbasdoc = in.readString();
        pk_invmandoc = in.readString();
        vreceiveaddress = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(invCode);
        dest.writeString(spec);
        dest.writeString(type);
        dest.writeString(invName);
        dest.writeString(unit);
        dest.writeString(batch);
        dest.writeString(barcode);
        dest.writeFloat(qty);
        dest.writeString(crowno);
        dest.writeString(cinvbasdocid);
        dest.writeString(cinventoryid);
        dest.writeString(cadvisecalbodyid);
        dest.writeString(corder_bid);
        dest.writeString(csaleid);
        dest.writeString(pk_corp);
        dest.writeString(number);
        dest.writeString(pk_invbasdoc);
        dest.writeString(pk_invmandoc);
        dest.writeString(vreceiveaddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SaleOutGoods> CREATOR = new Creator<SaleOutGoods>() {
        @NonNull
        @Override
        public SaleOutGoods createFromParcel(@NonNull Parcel in) {
            return new SaleOutGoods(in);
        }

        @NonNull
        @Override
        public SaleOutGoods[] newArray(int size) {
            return new SaleOutGoods[size];
        }
    };
}
