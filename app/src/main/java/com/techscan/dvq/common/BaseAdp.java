package com.techscan.dvq.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.techscan.dvq.bean.QryGood;

import java.util.List;

/**
 * Created by cloverss on 2017/8/8.
 */

public abstract class BaseAdp extends BaseAdapter {

    List<QryGood> list;

    public BaseAdp(List<QryGood> list) {
        this.list = list;
    }

    public void setList(List<QryGood> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.getItemView(position, convertView, parent.getContext());
    }

    public abstract View getItemView(int posi, View convertView, Context context);
}
