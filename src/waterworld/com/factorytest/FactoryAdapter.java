package waterworld.com.factorytest;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FactoryAdapter extends BaseAdapter {

    private final  String  TAG = Utils.TAG+"FactoryAdapter";
    private LayoutInflater mInflater;

    private List<FactoryBean>  mFactoryDatas= new ArrayList<>();
    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public FactoryAdapter(Context context) {

        mInflater = LayoutInflater.from(context);

        mFactoryDatas = FactoryDatas.getInstance(context);
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
        holder.title.setText( bean.getTitle() );
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

    //这个ViewHolder只能服务于当前这个特定的adapter，因为ViewHolder里会指定item的控件，不同的ListView，item可能不同，所以ViewHolder写成一个私有的类
    private class ViewHolder {
        ImageView image;

        TextView  title;
    }


    public FactoryBean  getItemBean(int postion){
        if( mFactoryDatas != null ) {
            return mFactoryDatas.get(postion);
        }else{
            return null;
        }

    }


}
