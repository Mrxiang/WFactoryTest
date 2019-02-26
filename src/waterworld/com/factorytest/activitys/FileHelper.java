package waterworld.com.factorytest.activitys;

import android.content.Context;
import android.os.Environment;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import waterworld.com.factorytest.FactoryTestFeatureoption.FeatureOption;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//import com.mediatek.common.featureoption.FeatureOption;
/**
 * M: MTK_2SDCARD_SWAP @{  @}
 */
// import com.mediatek.common.featureoption.FeatureOption;
//import com.mediatek.common.MediatekClassFactory;
//import com.mediatek.common.storage.IStorageManagerEx;
/** @} */

public class FileHelper { 



   private Context context; 

   /** SD卡是否存在**/ 

   private boolean hasSD = false; 

   /** SD卡的路径**/ 

   private String SDPATH; 

   /** 当前程序包的路径**/ 

   private String FILESPATH;

//wlf add start 20150227
    private static final String PROP_SD_SWAP = "vold.swap.state";//"vold_swap_state"; //wlf 20150612
    private static final String PROP_SD_SWAP_TRUE = "1";
    private static final String PROP_SD_SWAP_FALSE = "0";

    private String TAG ="FileHelper";
    /**
        * Returns the sd swap state.
        * @hide
        * @internal
        */
    public static boolean getSdSwapState() {
        String sdSwap = PROP_SD_SWAP_FALSE;
        
        try {
            sdSwap = SystemProperties.get(PROP_SD_SWAP, PROP_SD_SWAP_FALSE);
        } catch (IllegalArgumentException e) {
            Log.e("wlf", "IllegalArgumentException when get sdExist:" + e);
        }
        Log.d("wlf", "getSdSwapState = " + sdSwap);
        return sdSwap.equals(PROP_SD_SWAP_TRUE);
    }
//wlf add end.
   
   private boolean isSDExistWhenSwap() {
	   //return (Boolean)MediatekClassFactory.createInstance(IStorageManagerEx.class, IStorageManagerEx.GET_SWAP_STATE);

  //		IStorageManagerEx sm = MediatekClassFactory.createInstance(IStorageManagerEx.class);
//		return sm.getSdSwapState();
		return getSdSwapState();

   }

   public FileHelper(Context context) { 

       this.context = context; 

		// wanghe 2013-08-22 for emmc & nand T card Test
		System.out.println("<<<< wanghe FileHelper() : sd state = " + Environment.getExternalStorageState());
		System.out.println("<<<< wanghe FileHelper() : sd path = " + Environment.getExternalStorageDirectory().getPath());
		System.out.println("<<<< wanghe FileHelper() : isSDExistWhenSwap() = " + isSDExistWhenSwap());
		if (FeatureOption.MTK_EMMC_SUPPORT)
		{
			// wanghe 2013-08-14 for sd test
	       hasSD = Environment.getExternalStorageState().equals("mounted");//"/mnt/sdcard2"

	//       SDPATH = Environment.getExternalStorageDirectory(); 
	//       if(FeatureOption.MTK_FAT_ON_NAND){
	       //SDPATH ="/mnt/sdcard/sdcard2";
	       if (FeatureOption.MTK_2SDCARD_SWAP)// no function in M version.
	       {
	       		SDPATH ="/storage/sdcard1";//sdcard0
	       }
		   else
		   {
		   	    SDPATH ="/storage/sdcard1";
		   }
		}
		else
		{
			// hasSD = Environment.getExternalStorageState().equals("/mnt/sdcard/sdcard2");
			// if (FeatureOption.MTK_FAT_ON_NAND)  // wanghe 2013-08-29
			if (FeatureOption.MTK_2SDCARD_SWAP) // wanghe 2013-09-07
			{
				SDPATH ="/mnt/sdcard";
			}
			else
			{
				SDPATH ="/mnt/sdcard/sdcard2";
			}
		}
		

//       }else{
//    	   SDPATH ="/mnt/sdcard";
//       }
//       if(deleteSDFile("test.txt")){
//    	   SDPATH ="/mnt/sdcard";
//    	   hasSD = true;
//       }else{
//    	   hasSD = false;
//       }
       
		// yangkun add for T-Card test start 2018/04/11
		StorageManager storageManager = (StorageManager) context.getSystemService(
			Context.STORAGE_SERVICE);
		String[] volumePath = storageManager.getVolumePaths();
		int length = volumePath.length;
		if (length > 1) {
			SDPATH = volumePath[length - 1];
		} 
		// add end
       FILESPATH = this.context.getFilesDir().getPath(); 

   } 



   /**

    * 在SD卡上创建文件

    * 

    * @throws IOException

    */ 

   public File createSDFile(String fileName) throws IOException { 

		/*if (FeatureOption.MTK_2SDCARD_SWAP) // added by wanghe 2013-09-07
		{
			if (false == isSDExistWhenSwap())
			{
				return null;
			}
		}*/

       File file = new File(SDPATH + File.separator + fileName);

       if (!file.exists()) {

           file.createNewFile();

       }
       return file;

}



   /**

    * 删除SD卡上的文件

    * 

    * @param fileName

    */ 

   public boolean deleteSDFile(String fileName) { 

       File file = new File(SDPATH + "/" + fileName); 
       if (file == null || !file.exists() || file.isDirectory()) 
       {
    	   //hasSD = false;
           return false; 
           
       }else{
       file.delete(); 
       //hasSD = true;
       return true;
       }
   } 



   /**

    * 读取SD卡中文本文件

    * 

    * @param fileName

    * @return

    */ 

   public String readSDFile(String fileName) { 

       StringBuffer sb = new StringBuffer(); 

       File file = new File(SDPATH + "//" + fileName); 

       try { 

           FileInputStream fis = new FileInputStream(file); 

           int c; 

           while ((c = fis.read()) != -1) { 

               sb.append((char) c); 

           } 

           fis.close(); 

       } catch (FileNotFoundException e) { 

           e.printStackTrace(); 

       } catch (IOException e) { 

           e.printStackTrace(); 

       } 

       return sb.toString(); 

   } 



   public String getFILESPATH() { 

       return FILESPATH; 

   } 



   public String getSDPATH() { 

       return SDPATH; 

   } 

   private StorageManager mStorageManager = null;

   public boolean hasSD() {
       Log.d(TAG, "hasSD: ");
       mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
       StorageVolume[] storageVolumeList = mStorageManager.getVolumeList();
       if (storageVolumeList != null) {
           for (StorageVolume volume : storageVolumeList) {
               String   mDescription = volume.getDescription(context);
               String   mPath = volume.getPath();
               long      mMaxFileSize = volume.getMaxFileSize();
               boolean mIsExternal = volume.isRemovable();
               if( mIsExternal ){
                   hasSD = true;
               }
               Log.d(TAG, "init,description :" + mDescription + ",path : "
                       + mPath + ",isMounted : " + ",isExternal : " + mIsExternal + ", mMaxFileSize: " + mMaxFileSize);
           }
       }
       return hasSD;
   } 

} 
