package com.techscan.dvq;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;

import java.util.List;
import java.util.Map;

/**
 * Created by cloverss on 2017/6/23.
 */

public class SalesDeliveryAdapter extends BaseAdapter {

    Context mContext;
    List<Map<String, Object>> mList;

    public SalesDeliveryAdapter(Context context, List<Map<String, Object>> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<Map<String, Object>> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_san, null);
            viewHolder.InvCode = (TextView) convertView.findViewById(R.id.txtInvCodes);
            viewHolder.InvName = (TextView) convertView.findViewById(R.id.txtInvCodeName);
            viewHolder.Batch = (TextView) convertView.findViewById(R.id.txtBatch);
            viewHolder.QTY = (TextView) convertView.findViewById(R.id.txtQTY);
            viewHolder.UNIT = (TextView) convertView.findViewById(R.id.txtUnit);
            viewHolder.Spec = (TextView) convertView.findViewById(R.id.txtSpec);
            viewHolder.Type = (TextView) convertView.findViewById(R.id.txtModel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        if (mList.contains("InvCode"))
        viewHolder.InvCode.setText(mList.get(position).get("InvCode").toString());
        viewHolder.InvName.setText(mList.get(position).get("InvName").toString());
        viewHolder.Batch.setText(mList.get(position).get("Batch").toString());
        viewHolder.UNIT.setText(mList.get(position).get("Measname").toString());
        viewHolder.Spec.setText(mList.get(position).get("invspec").toString());
        viewHolder.Type.setText(mList.get(position).get("invtype").toString());
        if (TextUtils.isEmpty(String.valueOf(mList.get(position).get("Weights").toString()))) {
            viewHolder.QTY.setText("0.00");
        } else {
            viewHolder.QTY.setText(String.valueOf(mList.get(position).get("Weights").toString()));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView InvCode;
        TextView InvName;
        TextView Batch;
        TextView Spec;
        TextView Type;
        TextView QTY;
        TextView UNIT;

    }
}
