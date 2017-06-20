package com.techscan.dvq.MaterialOut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;

import java.util.List;

/**
 * Created by cloverss on 2017/6/20.
 */

public class MyBaseAdapter extends BaseAdapter {

    Context mContext;
    List<String> mList;

    public MyBaseAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<String> list) {
        mList = list;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameView.setText(mList.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView nameView;
        TextView signView;
    }
}
