package com.techscan.dvq.materialOut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liuya on 2017/6/20.
 */

public class MyBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<HashMap<String, String>> mList;

    public MyBaseAdapter(Context context, List<HashMap<String, String>> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<HashMap<String, String>> list) {
        mList = list;
    }

    public List<HashMap<String, String>> getList() {
        return mList;
    }

    /**
     * @return 返回子项目的个数，也就是有多少条
     */
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

    /**
     * @param position--> int 类型 ，指当前的位置，从“0” 开始
     * @param convertView
     * @param parent
     * @return 返回子项的布局
     * 每次生成一次子项，该方法执行一次
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_scan_details, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.encoding = (TextView) convertView.findViewById(R.id.encoding);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.lot = (TextView) convertView.findViewById(R.id.lot);
            viewHolder.qty = (TextView) convertView.findViewById(R.id.qty);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(mList.get(position).get("name"));
        viewHolder.encoding.setText(mList.get(position).get("encoding"));
        viewHolder.type.setText(mList.get(position).get("type"));
        viewHolder.lot.setText(mList.get(position).get("lot"));
        viewHolder.qty.setText(mList.get(position).get("qty"));
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView encoding;
        TextView type;
        TextView lot;
        TextView qty;
    }
}
