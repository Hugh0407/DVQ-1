package com.techscan.dvq.module.sale.scan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.PurSaleOutGoods;

import java.util.List;

/**
 * Created by Hugh on 2017/7/16.
 */

public class SaleOutTaskAdapter extends BaseAdapter {


    private List<PurSaleOutGoods> mList;

    public SaleOutTaskAdapter(List<PurSaleOutGoods> list) {
        mList = list;
    }

    public void setList(List<PurSaleOutGoods> list) {
        mList = list;
    }

    public List<PurSaleOutGoods> getList() {
        return mList;
    }
    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_out_task, null);
            viewHolder.invname = (TextView) convertView.findViewById(R.id.txtInvName);
            viewHolder.invcode = (TextView) convertView.findViewById(R.id.txtInvCode);
            viewHolder.num_task = (TextView) convertView.findViewById(R.id.num_task);
            viewHolder.num = (TextView) convertView.findViewById(R.id.num);
            viewHolder.spec = (TextView) convertView.findViewById(R.id.txtSpec);
            viewHolder.type = (TextView) convertView.findViewById(R.id.txtType);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.invname.setText(mList.get(position).getInvName());
        viewHolder.num.setText(mList.get(position).getNumber());
        viewHolder.spec.setText(mList.get(position).getSpec());
        viewHolder.type.setText(mList.get(position).getType());
        viewHolder.invcode.setText(mList.get(position).getInvCode());
        viewHolder.num_task.setText(mList.get(position).getNum_task());
        return convertView;

    }

   private static class ViewHolder{
       TextView num;
       TextView num_task;
       TextView invname;
       TextView invcode;
       TextView spec;
       TextView type;
    }

}
