package com.waterworld.factorytest;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.RemoteException;
import android.util.Log;

import com.android.internal.util.HexDump;
import com.waterworld.factorytest.data.DBManager;
import com.waterworld.factorytest.data.DataInterface;
import com.waterworld.factorytest.data.NvramManger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import vendor.mediatek.hardware.nvram.V1_0.INvram;

public class FactoryDatas implements DataInterface{
    private static final String TAG = Utils.TAG+"FactoryDatas";
    public  static List<FactoryBean> mDatas=null;
    public  Context  mContext;

    private static FactoryDatas mFactoryDatas;

//    private static final int TOTAL_BYTE = 29;

    private FactoryDatas(Context context ){
        mContext = context;
        if( mDatas == null ){
            mDatas = parseFactory( context );
            readDatasStatus( mDatas );
        }
    }

    public static FactoryDatas getInstance(Context context ){

        Log.d(TAG, "getInstance: "+mDatas);
        if (mFactoryDatas == null) {
            mFactoryDatas = new FactoryDatas(context);
        }
        return mFactoryDatas;
    }

    public List<FactoryBean> getListFactoryBean( ){
        return mDatas;
    }


    @Override
    public void readDatasStatus(List<FactoryBean> mDatas) {
        if(parseDataInNvram(mContext) ){
            NvramManger.getInstance(mContext).readDatasStatus( mDatas);
        }else{
            DBManager.getInstance(mContext).readDatasStatus(mDatas);
        }

    }

    @Override
    public void storeDatas(List<FactoryBean> mDatas) {
        if(parseDataInNvram(mContext) ){
            NvramManger.getInstance(mContext).storeDatas( mDatas);
        }else{
            DBManager.getInstance(mContext).storeDatas(mDatas);
        }
    }
    public  void cleanDatasStatus(List<FactoryBean> mDatas){
        Log.d(TAG, "cleanDatasStatus: ");
        if( mDatas != null ) {
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                bean.setStatus( Utils.NONE );
            }
            storeDatas( mDatas );
        }

    }

    public  void cleanDatasStatus( ){
        Log.d(TAG, "cleanDatasStatus: ");
        if( mDatas != null ) {
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                bean.setStatus( Utils.NONE );
            }
            storeDatas( mDatas );
        }

    }
    public static List<FactoryBean> parseFactory(Context context){
        Log.d(TAG, "parseFactory: ");
        List<FactoryBean> datas = new ArrayList<>() ;
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.factory);

            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                Log.d(TAG, "Parser: " + event);
                switch (event) {
                    case XmlResourceParser.START_DOCUMENT:
                        Log.d(TAG, "start: ");
                        break;
                    case XmlResourceParser.START_TAG:
                        String name = parser.getName();
                        Log.d(TAG, "start tag: " + name);
                        if( name.equals( Utils.ITEM ) ){
                            int count = parser.getAttributeCount();
                            String attrName="";
                            String factoryName="";
                            int factoryTitle=0;
                            String factoryAction="";
                            boolean visible = true;
                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.NAME ) ){
                                    factoryName = parser.getAttributeValue(i);
                                    Log.d(TAG, "factoryName : "+ factoryName);
                                } else if( attrName.equals( Utils.TITLE ) ){
                                    factoryTitle = parser.getAttributeResourceValue(i,0);
                                    Log.d(TAG, i+"factoryTitle: "+ factoryTitle );
                                }else if( attrName.equals( Utils.ACTION )){
                                    factoryAction = parser.getAttributeValue(i);
                                    Log.d(TAG, i+" factoryAction: "+ factoryAction );
                                }else if( attrName.equals( Utils.VISIBLE ) ){
                                    visible = parser.getAttributeBooleanValue(i, true);
                                    Log.d(TAG, i+" factoryAction: "+ factoryAction );

                                }

                            }
                            if( visible ) {
                                FactoryBean bean = new FactoryBean(factoryName, factoryTitle, factoryAction, Utils.NONE);
                                datas.add(bean);
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        Log.d(TAG, "end tag: ");
                        break;
                    default:
                        Log.d(TAG, "default: ");
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "getAttr: "+e.toString());
            e.printStackTrace();
        }
        return datas;
    }

    public static boolean parseDataInNvram(Context context ){
        Log.d(TAG, "parseFactory: ");
        boolean    data_in_nvram=true;
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.factory);

            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                Log.d(TAG, "Parser: " + event);
                switch (event) {
                    case XmlResourceParser.START_DOCUMENT:
                        Log.d(TAG, "start: ");
                        break;
                    case XmlResourceParser.START_TAG:
                        String name = parser.getName();
                        Log.d(TAG, "start tag: " + name);
                        if( name.equals( Utils.FACTORY ) ){
                            int count = parser.getAttributeCount();
                            String attrName="";
                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.DATA_IN_NVRAM ) ){
                                    data_in_nvram = parser.getAttributeBooleanValue(i, true);
                                    Log.d(TAG, "parse Name : "+ data_in_nvram);
                                }
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        Log.d(TAG, "end tag: ");
                        break;
                    default:
                        Log.d(TAG, "default: ");
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "getAttr: "+e.toString());
            e.printStackTrace();
        }
        return data_in_nvram;
    }
    public static int parseFragmentIndex(Context context){
        Log.d(TAG, "parseFactory: ");
        int    fragmentIndex=0;
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.factory);

            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                Log.d(TAG, "Parser: " + event);
                switch (event) {
                    case XmlResourceParser.START_DOCUMENT:
                        Log.d(TAG, "start: ");
                        break;
                    case XmlResourceParser.START_TAG:
                        String name = parser.getName();
                        Log.d(TAG, "start tag: " + name);
                        if( name.equals( Utils.FACTORY ) ){
                            int count = parser.getAttributeCount();
                            String attrName="";
                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.FRAGMENT_INDEX ) ){
                                    fragmentIndex = parser.getAttributeIntValue(i,0);
                                    Log.d(TAG, "parse Name : "+ fragmentIndex);
                                }
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        Log.d(TAG, "end tag: ");
                        break;
                    default:
                        Log.d(TAG, "default: ");
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "getAttr: "+e.toString());
            e.printStackTrace();
        }
        return fragmentIndex;
    }

    public static boolean parseFlashLightValue(Context context){
        Log.d(TAG, "parseFactory: ");
        boolean    flashlight_bool=false;
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.factory);

            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                Log.d(TAG, "Parser: " + event);
                switch (event) {
                    case XmlResourceParser.START_DOCUMENT:
                        Log.d(TAG, "start: ");
                        break;
                    case XmlResourceParser.START_TAG:
                        String name = parser.getName();
                        Log.d(TAG, "start tag: " + name);
                        if( name.equals( Utils.FACTORY ) ){
                            int count = parser.getAttributeCount();
                            String attrName="";
                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.FLASHLIGHT_BOOL ) ){
                                    flashlight_bool = parser.getAttributeBooleanValue(i, false);
                                    Log.d(TAG, "parse value : "+attrName +" "+flashlight_bool);
                                }
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        Log.d(TAG, "end tag: ");
                        break;
                    default:
                        Log.d(TAG, "default: ");
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "getAttr: "+e.toString());
            e.printStackTrace();
        }
        return flashlight_bool;
    }

    public static boolean parseSystemBackCameraValue(Context context){
        Log.d(TAG, "parseFactory: ");
        boolean    boolValue =false;
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.factory);

            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                Log.d(TAG, "Parser: " + event);
                switch (event) {
                    case XmlResourceParser.START_DOCUMENT:
                        Log.d(TAG, "start: ");
                        break;
                    case XmlResourceParser.START_TAG:
                        String name = parser.getName();
                        Log.d(TAG, "start tag: " + name);
                        if( name.equals( Utils.FACTORY ) ){
                            int count = parser.getAttributeCount();
                            String attrName="";
                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.SYSTEMBACKCAMERA ) ){
                                    boolValue = parser.getAttributeBooleanValue(i, false);
                                    Log.d(TAG, "parse value : "+attrName +" "+boolValue);
                                }
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        Log.d(TAG, "end tag: ");
                        break;
                    default:
                        Log.d(TAG, "default: ");
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "getAttr: "+e.toString());
            e.printStackTrace();
        }
        return boolValue;
    }

    public static boolean parseSystemFrontCameraValue(Context context){
        Log.d(TAG, "parseFactory: ");
        boolean    boolValue =false;
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.factory);

            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                Log.d(TAG, "Parser: " + event);
                switch (event) {
                    case XmlResourceParser.START_DOCUMENT:
                        Log.d(TAG, "start: ");
                        break;
                    case XmlResourceParser.START_TAG:
                        String name = parser.getName();
                        Log.d(TAG, "start tag: " + name);
                        if( name.equals( Utils.FACTORY ) ){
                            int count = parser.getAttributeCount();
                            String attrName="";
                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.SYSTEMFRONTCAMERA ) ){
                                    boolValue = parser.getAttributeBooleanValue(i, false);
                                    Log.d(TAG, "parse value : "+attrName +" "+boolValue);
                                }
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        Log.d(TAG, "end tag: ");
                        break;
                    default:
                        Log.d(TAG, "default: ");
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "getAttr: "+e.toString());
            e.printStackTrace();
        }
        return boolValue;
    }

    public static boolean parseSDCardFakeValue(Context context){
        Log.d(TAG, "parseFactory: ");
        boolean    boolValue =false;
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.factory);

            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                Log.d(TAG, "Parser: " + event);
                switch (event) {
                    case XmlResourceParser.START_DOCUMENT:
                        Log.d(TAG, "start: ");
                        break;
                    case XmlResourceParser.START_TAG:
                        String name = parser.getName();
                        Log.d(TAG, "start tag: " + name);
                        if( name.equals( Utils.FACTORY ) ){
                            int count = parser.getAttributeCount();
                            String attrName="";
                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.SDCARDFAKE ) ){
                                    boolValue = parser.getAttributeBooleanValue(i, false);
                                    Log.d(TAG, "parse value : "+attrName +" "+boolValue);
                                }
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        Log.d(TAG, "end tag: ");
                        break;
                    default:
                        Log.d(TAG, "default: ");
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "getAttr: "+e.toString());
            e.printStackTrace();
        }
        return boolValue;
    }

    public static boolean parseBatteryCurrentValue(Context context){
        Log.d(TAG, "parseFactory: ");
        boolean    boolValue =false;
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.factory);

            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                Log.d(TAG, "Parser: " + event);
                switch (event) {
                    case XmlResourceParser.START_DOCUMENT:
                        Log.d(TAG, "start: ");
                        break;
                    case XmlResourceParser.START_TAG:
                        String name = parser.getName();
                        Log.d(TAG, "start tag: " + name);
                        if( name.equals( Utils.FACTORY ) ){
                            int count = parser.getAttributeCount();
                            String attrName="";
                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.BATTERY_CURRENT ) ){
                                    boolValue = parser.getAttributeBooleanValue(i, false);
                                    Log.d(TAG, "parse value : "+attrName +" "+boolValue);
                                }
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        Log.d(TAG, "end tag: ");
                        break;
                    default:
                        Log.d(TAG, "default: ");
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "getAttr: "+e.toString());
            e.printStackTrace();
        }
        return boolValue;
    }

    public static boolean updateBeanStatus( String key, int status  ){
        Log.d(TAG, "updateBeanStatus: "+key+" "+status);
        if( mDatas != null ) {
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                if( bean.getName().equals( key )){
                    bean.setStatus( status );
                    return true;
                }
            }
        }
        return false;
    }

    public static int getPassedBeanSize( ){
        Log.d(TAG, "getPassedBeanSize: ");
        int size =0;
        if( mDatas != null ) {
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                if(bean.getStatus( ) == Utils.SUCCESS  ){
                    size ++;
                }
            }
        }
        return size;
    }
    public static int getPassedBeans(ArrayList<Integer> list){
        Log.d(TAG, "getPassedBeanSize: ");
        int size =0;
        if( mDatas != null && list !=null) {
            list.clear();
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                if(bean.getStatus( ) == Utils.SUCCESS  ){
                    list.add( bean.getTitleID());
                }
            }
        }
        Log.d(TAG, "getPassedBeans: "+list.size());
        return list.size();
    }

    public static int getFailedBeans(ArrayList<Integer> list){
        Log.d(TAG, "getFailedBeans: ");
        int size =0;
        if( mDatas != null && list !=null) {
            list.clear();
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                if(bean.getStatus( ) == Utils.FAILED  ){
                    list.add( bean.getTitleID());
                }
            }
        }
        Log.d(TAG, "getFailedBeans: "+list.size());
        return list.size();
    }

    public static  boolean allPassed( ){
        if( mDatas !=null ) {
            if (getPassedBeanSize() == mDatas.size()){
                return true;
            }
        }
        return false;
    }

    public static int getDataItemStatus( String name ){
        Log.d(TAG, "getDataItemStatus: "+name);
        int status=0;
        if( mDatas != null ) {
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                if( bean.getName().equals( name )){
                    status = bean.getStatus(  );
                    return status;
                }
            }
        }
        return status;
    }
}
