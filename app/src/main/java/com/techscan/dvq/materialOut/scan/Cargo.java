package com.techscan.dvq.materialOut.scan;

/**
 * Created by cloverss on 2017/6/22.
 * 货物对象
 */

public class Cargo {
    String name;
    int qty;
    int num;
    String encoding;

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
}
