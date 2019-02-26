package waterworld.com.factorytest;

import android.os.SystemProperties;

public class FactoryTestFeatureoption {

    public interface FeatureOption {
  //  boolean  = false;//com.mediatek.common.featureoption.FeatureOption.
		boolean MTK_GPS_SUPPORT = SystemProperties.getBoolean("ro.mtk_gps_support", false);// SystemProperties.getBoolean("ro.mtk_oma_drm_support", false);
		boolean MTK_GEMINI_SUPPORT = !SystemProperties.get("persist.radio.multisim.config", "ss").equals("ss");//fangfengfan add
		boolean MTK_2SDCARD_SWAP = SystemProperties.getBoolean("ro.mtk_2sdcard_swap", false);
		boolean MTK_EMMC_SUPPORT = SystemProperties.getBoolean("ro.mtk_emmc_support", false);
 
		boolean LCD_320_480 = SystemProperties.getBoolean("ro.lcd_320_480", false);
		boolean LCD_480_800 = SystemProperties.getBoolean("ro.lcd_480_800", false);
		boolean LCD_480_854 = SystemProperties.getBoolean("ro.lcd_480_854", false);
		boolean LCD_540_960 = SystemProperties.getBoolean("ro.lcd_540_960", false);
		boolean LCD_720_1280 = SystemProperties.getBoolean("ro.lcd_720_1280", false);
		boolean LCD_1080_1920 = SystemProperties.getBoolean("ro.lcd_1080_1920", false);
		boolean YKQ_SILENT_INSTALLER = SystemProperties.getBoolean("ro.ykq_silent_installer", false);
		boolean MTK_MULTI_STORAGE_SUPPORT = SystemProperties.getBoolean("ro.mtk_multi_storage_support", false);
		boolean HX_FACTORYTEST_CAMERAKEY = SystemProperties.getBoolean("ro.hx_factorytest_camerakey", false);
		boolean HX_FACTORYTEST_ALS = SystemProperties.getBoolean("ro.hx_factorytest_als", false);
		boolean HX_FACTORYTEST_PS = SystemProperties.getBoolean("ro.hx_factorytest_ps", false);
		boolean HX_FACTORYTEST_BATTERY = SystemProperties.getBoolean("ro.hx_factorytest_battery", false);
		boolean HX_FACTORYTEST_SAVE_ALL_STATUS = SystemProperties.getBoolean("ro.hx_factorytest_save_status", false);
		boolean TOUCHPANEL_TOUCHFELL_IN_PHONE_CALL = SystemProperties.getBoolean("ro.tp_touchfell_in_phone", false);
		boolean HX_SHOW_KEYTEST_SHOW_MENU = SystemProperties.getBoolean("qemu.hw.mainkeys", true);
		boolean HX_FACTORYTEST_BACKLIGHT = SystemProperties.getBoolean("ro.hx_factorytest_backlight", false);
		boolean HX_FACTORYTEST_INFRARED = SystemProperties.getBoolean("ro.hx_factorytest_infrared", false);//FACTORYMODE_INFRARED 
		boolean HX_FACTORYTEST_OTG = SystemProperties.getBoolean("ro.hx_factorytest_otg", false);
		/*boolean  = com.mediatek.common.featureoption.FeatureOption.;
		boolean = SystemProperties.getBoolean("", false);*/
		//zam add start
		boolean HX_CAM_FOCUS_MODE_COMP = SystemProperties.getBoolean("ro.hx_cam_focus_mode", false);
		//zam add end
		boolean HX_FACTORYTEST_AGEING = SystemProperties.getBoolean("ro.hx_factorytest_ageing", false);
		boolean HX_FINGERPRINT_MA = SystemProperties.getBoolean("ro.hx_using_fp_microarray", false) 
				|| SystemProperties.getBoolean("ro.hx_using_fp_blestech", false)
				|| SystemProperties.getBoolean("ro.hx_using_fingerprint_silead", false);
		boolean HX_LCD_TEST_CONTAINS_WHITE_BLACK = SystemProperties.getBoolean("ro.hx_lcdtest_5_colors", false);
		boolean HALL_FUNCTION_SUPPORT = SystemProperties.getBoolean("ro.hall_function_test_support", false);

		boolean HX_DEPUTY_CAMERA_TEST = SystemProperties.getBoolean("ro.hx_deputy_camera_test", false);
    }

}
