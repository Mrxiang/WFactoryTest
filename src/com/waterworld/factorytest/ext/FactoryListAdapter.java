package com.waterworld.factorytest.ext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waterworld.factorytest.FactoryBean;
import com.waterworld.factorytest.FactoryDatas;
import com.waterworld.factorytest.Utils;

import java.util.ArrayList;
import java.util.List;
import com.waterworld.factorytest.R;

public class FactoryListAdapter extends FactoryBaseAdapter {

    private final  String  TAG = Utils.TAG+"FactoryListAdapter";
    public FactoryListAdapter(Context context) {

        super(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.d(TAG, "getView: "+position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_layout, parent, false); //加载布局
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.image = (ImageView) convertView.findViewById(R.id.status);


            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }

        FactoryBean bean = mFactoryDatas.get(position);
//        Log.d(TAG, "getView: "+bean.toString());
        holder.title.setText( mContext.getResources().getString(bean.getTitleID() ) );
        if( bean.getStatus() == Utils.FAILED ){
            holder.image.setImageResource( R.drawable.ic_fail );
            holder.image.setVisibility( View.VISIBLE );
        } else if (bean.getStatus() == Utils.SUCCESS) {
            holder.image.setImageResource( R.drawable.ic_true );
            holder.image.setVisibility( View.VISIBLE );
        }else{
            holder.image.setVisibility( View.INVISIBLE );
        }

        return convertView;
    }



}
