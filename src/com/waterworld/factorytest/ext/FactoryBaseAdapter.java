package com.waterworld.factorytest.ext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waterworld.factorytest.FactoryBean;
import com.waterworld.factorytest.FactoryDatas;
import com.waterworld.factorytest.Utils;

import java.util.ArrayList;
import java.util.List;
import com.waterworld.factorytest.R;

public class FactoryBaseAdapter extends BaseAdapter {

    private final  String  TAG = Utils.TAG+"FactoryBaseAdapter";
    public LayoutInflater mInflater;
    public Context            mContext;

    public List<FactoryBean>  mFactoryDatas= new ArrayList<>();
    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public FactoryBaseAdapter(Context context) {
        mContext = context;

        mInflater = LayoutInflater.from(context);

        mFactoryDatas = FactoryDatas.getInstance(context).getListFactoryBean();
        Log.d(TAG, "Data Size: "+mFactoryDatas.size());
    }

    //返回数据集的长度
    @Override
    public int getCount() {
        if( mFactoryDatas != null) {
            return mFactoryDatas.size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mFactoryDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //这个方法才是重点，我们要为它编写一个ViewHolder
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.d(TAG, "getView: "+position);
//        ViewHolder holder = null;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.item_layout, parent, false); //加载布局
//            holder = new ViewHolder();
//            holder.title = (TextView) convertView.findViewById(R.id.item_title);
//            holder.image = (ImageView) convertView.findViewById(R.id.status);
//
//
//            convertView.setTag(holder);
//        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        FactoryBean bean = mFactoryDatas.get(position);
////        Log.d(TAG, "getView: "+bean.toString());
//        holder.title.setText( bean.getTitle() );
//        if( bean.getStatus() == Utils.FAILED ){
//            holder.image.setImageResource( R.drawable.ic_fail );
//            holder.image.setVisibility( View.VISIBLE );
//        } else if (bean.getStatus() == Utils.SUCCESS) {
//            holder.image.setImageResource( R.drawable.ic_true );
//            holder.image.setVisibility( View.VISIBLE );
//        }else{
//            holder.image.setVisibility( View.INVISIBLE );
//        }
//
//        return convertView;
        return this.getView( position,  convertView,  parent);
    }

    //这个ViewHolder只能服务于当前这个特定的adapter，因为ViewHolder里会指定item的控件，不同的ListView，item可能不同，所以ViewHolder写成一个私有的类
    public class ViewHolder {
        ImageView image;

        TextView  title;

        LinearLayout    linearLayout;
    }


    public FactoryBean  getItemBean(int postion){
        if( mFactoryDatas != null ) {
            return mFactoryDatas.get(postion);
        }else{
            return null;
        }

    }


}
