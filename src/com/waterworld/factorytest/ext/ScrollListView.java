package com.waterworld.factorytest.ext;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


public class ScrollListView extends ListView {
    public ScrollListView(Context context){
        super(context);
    }
    public ScrollListView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public ScrollListView(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        int expandSpec = MeasureSpec.makeMeasureSpec( Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
