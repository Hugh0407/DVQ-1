package com.techscan.dvq.module.query;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.QryGood;
import com.techscan.dvq.common.BaseAdp;

import java.util.Collections;
import java.util.List;

/**
 * Created by cloverss on 2017/8/8.
 */

public class QueryAdp extends BaseAdp {

    List<QryGood> list;

    public QueryAdp(List<QryGood> list) {
        super(list);
        this.list = list;
    }

    @Override
    public View getItemView(int posi, View convertView, Context context) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_view_query, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.num = (TextView) convertView.findViewById(R.id.num);
            viewHolder.purLot = (TextView) convertView.findViewById(R.id.pur_lot);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(list.get(posi).storname);
        viewHolder.num.setText(list.get(posi).nonhandnum);
        viewHolder.purLot.setText(list.get(posi).purLot);
        if (list.get(posi).isItSelt) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setChecked(true);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView num;
        TextView purLot;
        CheckBox checkBox;
    }
}
