package waterworld.com.factorytest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import waterworld.com.factorytest.R;

public class MainActivity extends Activity {

    private  String TAG = Utils.TAG+ "MainActivity";
    private  AttributeSet attributeSet;

    private ListView        mListView;
    private FactoryAdapter  mAdapter;

    private HandlerThread       mHandlerThread ;
    private Handler             handler;
    private List<FactoryBean>   mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.main_layout );

        setCustomActionBar( );

        mDatas = FactoryDatas.getInstance(getBaseContext());

        initListView();

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
                            Thread.sleep(1000);
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
                                    startActivityForResult(intent, Utils.REQUEST_CODE_CIRCLE);
                                    break;
                                }
                            }
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
    }

    //方法；初始化Data
    private void initListView() {
        Log.d(TAG, "initListView: ");
        mListView = findViewById( R.id.list_factory );

        //为数据绑定适配器
        mAdapter = new FactoryAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


    private void setCustomActionBar( ) {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.action_bar_layout, null);

        ActionBar actionBar = getActionBar();
        if(actionBar != null ) {
            actionBar.setCustomView(mActionBarView, lp);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);

            TextView mTextView = mActionBarView.findViewById(R.id.action_bar_title);
            Toolbar parent =(Toolbar) mActionBarView.getParent();
            parent.setContentInsetsAbsolute(0,0);
            mTextView.setText( getText(R.string.app_name) );

            ImageView play = mActionBarView.findViewById(R.id.action_bar_play );
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = Message.obtain();
                    message.what = Utils.START;
                    handler.sendMessage( message );
                }
            });

            ImageView pause = mActionBarView.findViewById(R.id.action_bar_pause );
            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = Message.obtain();
                    message.what = Utils.PAUSE;
                    handler.sendMessage( message );

                }
            });
        }
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
//        changeButtonColor();
        mAdapter.notifyDataSetChanged();
    }


    protected  void onActivityResult( int requestCode, int resultCode, Intent data ){
        Log.d(TAG, "onActivityResult: requestCode"+requestCode+ " resultCode "+requestCode +" "+data);
        if( data != null ){
            try {
                if (requestCode == Utils.REQUEST_CODE_CIRCLE) {

                    String factory = data.getStringExtra(Utils.NAME);
                    int status = data.getIntExtra(Utils.TEST_RESULT, Utils.NONE);
                    FactoryDatas.updateBeanStatus(factory, status);
    //                Sleep();
                    Message message = Message.obtain();
                    message.what = Utils.START;
                    handler.sendMessage(message);
                } else if (requestCode == Utils.REQUEST_CODE) {
                    String factory = data.getStringExtra(Utils.NAME);
                    int status = data.getIntExtra(Utils.TEST_RESULT, Utils.NONE);
                    FactoryDatas.updateBeanStatus(factory, status);
                }
            }catch(Exception e){
                Log.d(TAG, "onActivityResult: ", e.fillInStackTrace());
                e.printStackTrace();
            }
        }

    }


    protected void onDestroy() {
        // TODO Auto-generated method stub
        //Settings.System.putString(getContentResolver(),Settings.System.HOME_KEY,"no");////fangfengfan delete

        super.onDestroy();

        FactoryDatas.storeDatasToNvram( mDatas );
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.single_board_test:
                Log.d(TAG, "single board test ");
                return true;
            case R.id.worker_check_switch:
                Log.d(TAG, "work check switch ");
                Toast.makeText(this, getResources().getString(R.string.worker_check_switch_on), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.clear_status:
                Log.d(TAG, "clear status : ");
                FactoryDatas.cleanDatasStatus( mDatas );
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
