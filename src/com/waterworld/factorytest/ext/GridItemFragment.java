package com.waterworld.factorytest.ext;

import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.waterworld.factorytest.FactoryBean;
import com.waterworld.factorytest.FactoryDatas;
import com.waterworld.factorytest.Utils;
import com.waterworld.factorytest.R;

import java.util.Iterator;
import java.util.List;

//import androidx.fragment.app.Fragment;

@SuppressLint("NewApi")
public class GridItemFragment  extends Fragment{

    private static final String TAG = Utils.TAG+"GridItemFragment";

    private ScrollGridView      mGridView;
    private FactoryGridAdapter  mAdapter;

    private HandlerThread       mHandlerThread ;
    private Handler             handler;
    private List<FactoryBean>   mDatas;
    private View                mContentView;
    private Button              testAllBtn;
    private Button              resetBtn;
    private Button              reportBtn;
    private Button              exitBtn;
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mContentView = inflater.inflate(R.layout.grid_item_fragment,container, false);

        mDatas = FactoryDatas.getInstance(getContext()).getListFactoryBean();
        initGridView();
        initOtherViews();
        mHandlerThread = new HandlerThread( "handler-thread") ;
        //开启一个线程
        mHandlerThread.start();
        handler =  new Handler(mHandlerThread.getLooper()){
            int  handlerStatus=0;
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                switch ( what ){
                    case Utils.START:
                        Log.d(TAG, "handleMessage: "+what);
                        try {
                            Log.d(TAG, "sleep: ");
//                            Thread.sleep(500);
                            Thread.sleep(0);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if( mDatas != null && (handlerStatus != -1) ) {
                            Iterator<FactoryBean> it = mDatas.iterator();
                            while(it.hasNext()) {
                                FactoryBean bean = it.next();
                                int status  = bean.getStatus();
                                if( status == Utils.NONE ) {
                                    String action = bean.getAction();
                                    Intent intent = new Intent();
                                    intent.setAction(action);
                                    Log.d(TAG, "handleMessage: start->"+action);
                                    startActivityForResult(intent, Utils.REQUEST_CODE_CIRCLE);
                                    break;
                                }
                            }
                            FactoryDatas.getInstance(getContext()).storeDatas(mDatas);
                        }
                        break;
                    case Utils.PAUSE:
                        Log.d(TAG, "handleMessage: "+what);
                        handlerStatus = -1;
                        break;
                    default:
                        break;
                }
            }
        };


        return mContentView;
    }

    public void onStart( ){
        super.onStart();
    }

    private void initGridView() {
        Log.d(TAG, "initGridView: ");
        mGridView = mContentView.findViewById( R.id.grid_factory );

        //为数据绑定适配器
        mAdapter = new FactoryGridAdapter(getContext());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: "+ position+" : " +id);
                FactoryBean bean = mAdapter.getItemBean( position );
                Intent intent = new Intent();
                String action = bean.getAction();
                Log.d(TAG, "onItemClick: "+ action);
                intent.setAction( action );
                startActivityForResult( intent , Utils.REQUEST_CODE);

            }
        });
    }
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
//        changeButtonColor();
//        updateActionBarView();
        mAdapter.notifyDataSetChanged();
    }


    public  void onActivityResult( int requestCode, int resultCode, Intent data ){
        Log.d(TAG, "onActivityResult: requestCode"+requestCode+ " resultCode "+requestCode +" "+data);
        if( data != null ){
            try {
                if (requestCode == Utils.REQUEST_CODE_CIRCLE) {

                    String factory = data.getStringExtra(Utils.NAME);
                    int status = data.getIntExtra(Utils.TEST_RESULT, Utils.NONE);
                    FactoryDatas.updateBeanStatus(factory, status);
                    FactoryDatas.getInstance(getContext()).storeDatas(mDatas);
					//                Sleep();
                    Message message = Message.obtain();
                    message.what = Utils.START;
                    handler.sendMessage(message);
                } else if (requestCode == Utils.REQUEST_CODE) {
                    String factory = data.getStringExtra(Utils.NAME);
                    int status = data.getIntExtra(Utils.TEST_RESULT, Utils.NONE);
                    FactoryDatas.updateBeanStatus(factory, status);
                	FactoryDatas.getInstance(getContext()).storeDatas(mDatas);
				}
            }catch(Exception e){
                Log.d(TAG, "onActivityResult: ", e.fillInStackTrace());
                e.printStackTrace();
            }
        }

    }


    public void onDestroy() {
        // TODO Auto-generated method stub
        //Settings.System.putString(getContentResolver(),Settings.System.HOME_KEY,"no");////fangfengfan delete

        super.onDestroy();

        FactoryDatas.getInstance(getContext()).storeDatas( mDatas );
    }
    private void initOtherViews( ) {


        testAllBtn  = mContentView.findViewById(R.id.btn_test_all );
        testAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = Message.obtain();
                message.what = Utils.START;
                handler.sendMessage( message );
            }
        });


        resetBtn  = mContentView.findViewById(R.id.btn_test_reset );
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FactoryDatas.getInstance(getContext()).cleanDatasStatus();
                mAdapter.notifyDataSetChanged();

            }
        });

        reportBtn = mContentView.findViewById( R.id.btn_test_report );
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent miIntent = new Intent();
                miIntent.setAction("com.waterworld.intent.action.ALL_REPORT");
                //miIntent.putExtra("textall", true);
                startActivityForResult(miIntent, Utils.REQUEST_CODE );

            }
        });

        exitBtn = mContentView.findViewById( R.id.btn_test_exit );
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish( );
                getActivity().finish();
            }
        });

    }




}
