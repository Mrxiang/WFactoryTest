package waterworld.com.factorytest;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.RemoteException;
import android.util.Log;

import com.android.internal.util.HexDump;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import vendor.mediatek.hardware.nvram.V1_0.INvram;

public class FactoryDatas {
    private static final String TAG = Utils.TAG+"FactoryDatas";
    public  static List<FactoryBean> mDatas=null;

    private static final String CUSTOM_ADDRESS_FILENAME = "/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";
    private static int FACTORYTEST_VALUE = 342;
//    private static final int TOTAL_BYTE = 29;

    private FactoryDatas( ){

    }

    public static List<FactoryBean> getInstance(Context context ){
        if( mDatas == null ){

            mDatas = parseFactory( context );
            readDatasStatusFromNvram( mDatas );
        }
        Log.d(TAG, "getInstance: "+mDatas);
        return mDatas;
    }

    public static void readDatasStatusFromNvram(List<FactoryBean> mDatas ){
        try {
            INvram agent = INvram.getService();
            if (agent == null) {
                Log.d(TAG,"readFileByNamevec write agent == null");
                return ;
            }
            String buff = agent.readFileByName(CUSTOM_ADDRESS_FILENAME, FACTORYTEST_VALUE + mDatas.size());
            byte[] buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
            Log.d(TAG,"readFileByNamevec read buffArr == "+ Arrays.toString(buffArr));

            int i = FACTORYTEST_VALUE;

            if( mDatas != null ) {
                Iterator<FactoryBean> it = mDatas.iterator();
                while(it.hasNext()) {
                    int status = Integer.parseInt(Byte.toString(buffArr[i]));;
                    FactoryBean bean = it.next();
                    bean.setStatus( status );
                    i++;
                }
            }

        } catch (Exception e) {
            Log.d(TAG,"readFileByNamevec Exception == "+e);
            e.printStackTrace();
        }
    }

    public static void storeDatasToNvram(List<FactoryBean>  mDatas){
        try {
            INvram agent = INvram.getService();
            if (agent == null) {
                Log.d(TAG,"writeFileByNamevec agent == null");
                return;
            }
            String buff = agent.readFileByName(CUSTOM_ADDRESS_FILENAME, FACTORYTEST_VALUE + mDatas.size());
            byte[] buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
            Log.d(TAG,"writeFileByNamevec read buffArr == "+Arrays.toString(buffArr));
            ArrayList<Byte> dataArray = new ArrayList<Byte>(buffArr.length);
            Log.d(TAG,"writeFileByNamevec dataArray.size == "+dataArray.size());
            for(int i = 0; i < buffArr.length; i++){
                if(i >= FACTORYTEST_VALUE){
                    String data = "0";
                    int value = mDatas.get(i - FACTORYTEST_VALUE).getStatus();
                    if(value == 1){
                        data = "1";
                    } else if(value == -1){
                        data = "-1";
                    }
                    dataArray.add(i, new Byte(data));
                }else{
                    dataArray.add(i, buffArr[i]);
                }
            }
            Log.d(TAG,"writeFileByNamevec dataArray == "+dataArray.toString());
            int flag = agent.writeFileByNamevec(CUSTOM_ADDRESS_FILENAME, FACTORYTEST_VALUE + mDatas.size(), dataArray);
            Log.d(TAG,"writeFileByNamevec write flag == "+flag);
        } catch (RemoteException e) {
            Log.d(TAG,"writeFileByNamevec Exception == "+e);
            e.printStackTrace();
        }
    }

    public static void cleanDatasStatus(List<FactoryBean> mDatas){
        Log.d(TAG, "cleanDatasStatus: ");
        if( mDatas != null ) {
            Iterator<FactoryBean> it = mDatas.iterator();
            while(it.hasNext()) {
                FactoryBean bean = it.next();
                bean.setStatus( Utils.NONE );
            }
            storeDatasToNvram( mDatas );
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
                            String factoryTitle="";
                            String factoryAction="";

                            for(int i=0; i<count; i++){
                                attrName = parser.getAttributeName(i);
                                Log.d(TAG, i+" Parser: "+ attrName );
                                if( attrName.equals( Utils.NAME ) ){
                                    factoryName = parser.getAttributeValue(i);
                                    Log.d(TAG, "parse Name : "+ factoryName);
                                } else if( attrName.equals( Utils.TITLE ) ){
                                    int aValue = parser.getAttributeResourceValue(i, 0);
                                    Log.d(TAG, i+" Parser: "+ aValue );
                                    factoryTitle = context.getResources().getString( aValue );
                                    Log.d(TAG, i+"factoryTitle: "+ factoryTitle );
                                }else if( attrName.equals( Utils.ACTION )){
                                    factoryAction = parser.getAttributeValue(i);
                                    Log.d(TAG, i+" factoryAction: "+ factoryAction );
                                }

                            }
                            FactoryBean bean = new FactoryBean(factoryName,factoryTitle, factoryAction, Utils.NONE);
                            datas.add( bean );
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

}
