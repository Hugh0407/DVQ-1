package com.techscan.dvq.module.sale.scan;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.SaleOutGoods;

import java.util.List;

/**
 * Created by cloverss on 2017/6/23.
 */

public class SalesOutDetailAdapter extends BaseAdapter {

    private List<SaleOutGoods> mList;

    public SalesOutDetailAdapter(List<SaleOutGoods> list) {
        mList = list;
    }

    public void setList(List<SaleOutGoods> list) {
        mList = list;
    }

    public List<SaleOutGoods> getList() {
        return mList;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_out_details_new, null);
            viewHolder.InvCode = (TextView) convertView.findViewById(R.id.txtInvCode);
            viewHolder.InvName = (TextView) convertView.findViewById(R.id.txtInvName);
            viewHolder.Batch = (TextView) convertView.findViewById(R.id.txtBatch);
            viewHolder.QTY = (TextView) convertView.findViewById(R.id.txtQty);
            viewHolder.Spec = (TextView) convertView.findViewById(R.id.txtSpec);
            viewHolder.Type = (TextView) convertView.findViewById(R.id.txtType);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.InvCode.setText(mList.get(position).getInvCode());
        viewHolder.InvName.setText(mList.get(position).getInvName());
        viewHolder.Batch.setText(mList.get(position).getBatch());
        viewHolder.Spec.setText(mList.get(position).getSpec());
        viewHolder.Type.setText(mList.get(position).getType());
        if (TextUtils.isEmpty(String.valueOf(mList.get(position).getQty()))) {
            viewHolder.QTY.setText("0.00");
        } else {
            viewHolder.QTY.setText(String.valueOf(mList.get(position).getQty()));
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

    }
}
