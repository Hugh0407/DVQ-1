package com.techscan.dvq.materialOut.scan;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;

import java.util.List;

/**
 * Created by cloverss on 2017/6/20.
 * “总览” 按钮中的dialog视图的 Adapter
 */

public class OvAdapter extends BaseAdapter {

    Context mContext;
    List<Goods> mList;

    public OvAdapter(Context context, List<Goods> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<Goods> list) {
        mList = list;
    }

    public List<Goods> getList() {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_scan_ov, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.encoding = (TextView) convertView.findViewById(R.id.encoding);
            viewHolder.num = (TextView) convertView.findViewById(R.id.num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(mList.get(position).getName());
        viewHolder.encoding.setText(mList.get(position).getEncoding());
        if (TextUtils.isEmpty(String.valueOf(mList.get(position).getQty()))) {
            viewHolder.num.setText("");
        } else {
            viewHolder.num.setText(String.valueOf(mList.get(position).getQty()));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView encoding;
        TextView num;
    }
}
