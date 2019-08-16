package com.waterworld.factorytest.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.util.Log;

import com.waterworld.factorytest.FactoryBean;
import com.waterworld.factorytest.Utils;

import java.util.Iterator;
import java.util.List;

public class DBManager implements  DataInterface{

    public  static String TAG =Utils.TAG+"DBManager";
    private SQLiteDatabase db;

    private static DBManager dbManager;

    private DBManager(Context context){
        /** 初始化数据库 */
        DBHelper dbHelper = new DBHelper( context);
        /** 获取db */
        db = dbHelper.getWritableDatabase();
    }


    public synchronized static DBManager getInstance(Context context) {
        Log.d(TAG, "getInstance: ");
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    @Override
    public void readDatasStatus(List<FactoryBean> mDatas) {
        //1.if db not exist create db, and init db status
        //2.if db exist , read status  from db
        Log.d(TAG, "readDatasStatus: ");
        int count =getAllCount();
        if( count != mDatas.size() ){
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Utils.NAME, bean.getName());
                contentValues.put(Utils.STATUS, bean.getStatus());
                insertBean( contentValues);
            }
        }else {
            Iterator<FactoryBean> it = mDatas.iterator();
            while (it.hasNext()) {
                FactoryBean bean = it.next();
                String name = bean.getName();
                int status = queryStatus( name);
                bean.setStatus( status );
            }
        }

    }

    @Override
    public void storeDatas(List<FactoryBean> mDatas) {
        //1.if db not exist , create db ,store data to db

        //2. if db exist, store data to db
        Log.d(TAG, "storeDatas: ");
        Iterator<FactoryBean> it = mDatas.iterator();
        while (it.hasNext()) {
            FactoryBean bean = it.next();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Utils.NAME, bean.getName());
            contentValues.put(Utils.STATUS, bean.getStatus());
            updateBean(contentValues);

        }
    }

    public int getAllCount( ){
        int count=0;
        Cursor cursor=null;
        try {
            cursor = db.query(Utils.TABLE, null, null,
                    null, null, null, null);
            count = cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if( cursor != null ){
                cursor.close();
            }
        }
        return count;
    }


    public long insertBean( ContentValues values ){
        Log.d(TAG, "insertBean: ");
        long retValue=0;
        try {
            retValue = db.insert(Utils.TABLE, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        return retValue;
    }

    public int  queryStatus(String name){
        Log.d(TAG, "queryStatus: "+name);
        String[] columns = {Utils.STATUS};
        String selection = Utils.NAME+"=?";
        String[] selectionArgs ={ name};
        Cursor cursor=null;
        int status=0;
        try {
            cursor = db.query(Utils.TABLE, columns, selection, selectionArgs,
                    null, null, null, null);
            if (cursor != null || cursor.getCount() != 0) {
                cursor.moveToFirst();
                status = cursor.getInt(0);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if( cursor !=null) {
                cursor.close();
            }
        }
        return status;
    }
    public int updateBean(ContentValues values ) {
        int retValue =0;
        String name =values.getAsString(Utils.NAME);
        int    status = values.getAsInteger(Utils.STATUS);
//        update factory set status ="1" where name ="lcd";
        ContentValues mValue = new ContentValues();
        mValue.put(Utils.STATUS, status);

        String where = Utils.NAME + "=?";
        String[] whereArgs = new String[] { name  };

        try {
             retValue = db.update(Utils.TABLE, mValue, where, whereArgs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return retValue;
    }

}
