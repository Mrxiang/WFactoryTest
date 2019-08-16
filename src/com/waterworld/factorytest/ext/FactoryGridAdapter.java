package com.waterworld.factorytest.ext;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.waterworld.factorytest.FactoryBean;
import com.waterworld.factorytest.FactoryDatas;
import com.waterworld.factorytest.Utils;

import java.util.ArrayList;
import java.util.List;
import com.waterworld.factorytest.R;

public class FactoryGridAdapter extends FactoryBaseAdapter  {

    private final  String  TAG = Utils.TAG+"FactoryGridAdapter";

    public FactoryGridAdapter(Context context) {

        super(context);
    }


    //这个方法才是重点，我们要为它编写一个ViewHolder
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.d(TAG, "getView: "+position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item_layout, parent, false); //加载布局
            holder = new ViewHolder();
            holder.linearLayout = convertView.findViewById( R.id.item_layout);
            holder.title = (TextView) convertView.findViewById(R.id.item_title);

            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }

        FactoryBean bean = mFactoryDatas.get(position);
//        Log.d(TAG, "getView: "+bean.toString());
        holder.title.setText( mContext.getResources().getString(bean.getTitleID() ) );
        if( bean.getStatus() == Utils.FAILED ){
//            holder.linearLayout.setBackgroundResource( R.drawable.bk_fail );
            holder.linearLayout.setBackgroundColor(Color.RED  );
        } else if (bean.getStatus() == Utils.SUCCESS) {
//            holder.linearLayout.setBackgroundResource( R.drawable.bk_true );
            holder.linearLayout.setBackgroundColor( Color.GREEN );

        }else{
//            holder.linearLayout.setBackgroundResource( R.drawable.bk_press );
            holder.linearLayout.setBackgroundColor( Color.GRAY );


        }

        return convertView;
    }

}
