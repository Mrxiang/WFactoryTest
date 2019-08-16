package com.waterworld.factorytest.data;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.internal.util.HexDump;
import com.waterworld.factorytest.FactoryBean;
import com.waterworld.factorytest.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import vendor.mediatek.hardware.nvram.V1_0.INvram;

public class NvramManger implements DataInterface {
    private static final String TAG = Utils.TAG+"NvramManger";
    private static NvramManger nvramManger;

    private static final String CUSTOM_ADDRESS_FILENAME = "/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";
    private static int FACTORYTEST_VALUE = 342;

    private NvramManger(Context context){

    }


    public synchronized static NvramManger getInstance(Context context) {
        if (nvramManger == null) {
            nvramManger = new NvramManger(context);
        }
        return nvramManger;
    }
    @Override
    public void readDatasStatus(List<FactoryBean> mDatas) {
        readDatasStatusFromNvram(mDatas);
    }

    @Override
    public void storeDatas(List<FactoryBean> mDatas) {
        storeDatasToNvram(mDatas);
    }

    public  void readDatasStatusFromNvram(List<FactoryBean> mDatas ){
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

    public  void storeDatasToNvram(List<FactoryBean>  mDatas){
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
        } catch (Exception e) {
            Log.d(TAG,"writeFileByNamevec Exception == "+e);
            e.printStackTrace();
        }
    }


}
