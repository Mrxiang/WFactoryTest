package com.waterworld.factorytest;

import android.app.Activity;

import com.waterworld.factorytest.R;
import com.waterworld.factorytest.ext.GridItemFragment;
import com.waterworld.factorytest.ext.ListItemFragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.support.design.widget.NavigationView;
import android.widget.Toast;


public class FactoryTest extends Activity implements NavigationView.OnNavigationItemSelectedListener {

    private ListItemFragment listItemFragment;
    private GridItemFragment gridItemFragment;


    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private int    fragmentIndex;
    private final String TAG = Utils.TAG + "FactoryTest";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        listItemFragment = new ListItemFragment();
        gridItemFragment = new GridItemFragment();

        drawerLayout = findViewById(R.id.drawer_layout);

        fragmentIndex  =FactoryDatas.parseFragmentIndex( getBaseContext() );
        if( fragmentIndex ==0 ){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame,  gridItemFragment).commit();
        }else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame,  listItemFragment).commit();

//            drawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED );
        }

    }

    protected void onResume(){
        super.onResume();

    }
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: ");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (item.getItemId()) {
            case R.id.grid_fragment:
                transaction.replace(R.id.content_frame,  gridItemFragment).commit();
                break;
            case R.id.list_fragment:
                transaction.replace(R.id.content_frame,  listItemFragment).commit();
                break;
            case R.id.hardware_info:
                Intent miIntent = new Intent();
                miIntent.setAction("com.waterworld.intent.action.HardwareInformationActivity");
                //miIntent.putExtra("textall", true);
                startActivityForResult(miIntent, Utils.REQUEST_CODE );

                break;
            case R.id.exit_test:
                finish();
                break;

        }
        drawerLayout.closeDrawers();
        return true;
    }

    private long                firstTime = 0;
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, getResources().getString(R.string.sure_exit), Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            finish();
        }
    }


}
