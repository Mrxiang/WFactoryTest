package com.waterworld.factorytest;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static final String  FACTORY_XML = "factory.xml";
    public static final String  ASSETS_FACTORY_XML = "assets/factory.xml";
    public static final String  UTF_8 = "utf-8";
    public static final String  TAG = "FactoryTest.";
    public static final String  FACTORY = "Factory";
    public static final String  DATA_IN_NVRAM = "data_in_nvram";
    public static final String  FRAGMENT_INDEX = "fragmentIndex";
    public static final String  FLASHLIGHT_BOOL = "flashlight";
    public static final String  SYSTEMBACKCAMERA = "systemBackCamera";
    public static final String  SYSTEMFRONTCAMERA = "systemFrontCamera";
    public static final String  SDCARDFAKE = "sdcard_fake";
    public static final String  BATTERY_CURRENT = "batteryCurrent";
    public static final String  ITEM = "Item";
    public static final String  NAME = "name";
    public static final String  TITLE = "title";
    public static final String  ACTION = "action";
    public static final String  VISIBLE = "visible";

    public static final String  TEST_RESULT = "result";
    public static final int  NONE = 0;
    public static final int  SUCCESS = 1;
    public static final int  FAILED = -1;
    public static final int  REQUEST_CODE = 100;
    public static final int  REQUEST_CODE_CIRCLE = 101;

    public static final int START = 1;
    public static final int PAUSE = 2;

    public static final String FM ="fm";

    public static  final String TABLE="factory";
    public static  final String STATUS="status";

    public static final String   LED_BREATH_MODE[] = {"/system/bin/sh","-c","echo 1 > /sys/bus/platform/drivers/mtk-kpd/led_breath_mode"};
    public static final String   LED_LIGHT_MODE[] ={"/system/bin/sh","-c","echo 0 > /sys/bus/platform/drivers/mtk-kpd/led_breath_mode"} ;
    public static final String   LED_RED_ON[] = {"/system/bin/sh","-c","echo 1 > /sys/class/leds/red/brightness"};
    public static final String   LED_RED_OFF[] = {"/system/bin/sh","-c","echo 0 > /sys/class/leds/red/brightness"};
    public static final String   LED_GREEN_ON[] = {"/system/bin/sh","-c","echo 1 > /sys/class/leds/green/brightness"};
    public static final String   LED_GREEN_OFF[] = {"/system/bin/sh","-c","echo 0 > /sys/class/leds/green/brightness"};
    public static final String   LED_YELLOW_ON[] = {"/system/bin/sh","-c","echo 1 > /sys/class/leds/blue/brightness"};
    public static final String   LED_YELLOW_OFF[] = {"/system/bin/sh","-c","echo 0 > /sys/class/leds/blue/brightness"};
    public static final String   SU = "su";
    public static final String   EXIT = "exit";
    public static final String   ONE ="1";
    public static final String   ZERO ="0";
    public static final String   LED_BREATH = "/sys/bus/platform/drivers/mtk-kpd/led_breath_mode";
    public static final String   LED_RED = "/sys/class/leds/red/brightness";
    public static final String   LED_GREEN = "/sys/class/leds/green/brightness";
    public static final String   LED_YELLOW = "/sys/class/leds/blue/brightness";

    public static boolean runRootCommand(String[] command) {
        Log.d(TAG, "runRootCommand: "+command.toString());
        boolean ok = true;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec( command );
            os = new DataOutputStream(process.getOutputStream());
//            os.writeBytes(command + "\n");
//            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d(TAG, "runRootCommand: "+e.toString());
            e.printStackTrace();
            ok = false;
        } finally {
            try {
                if (os != null)
                    os.close();
                if (process != null)
                    process.exitValue();
            } catch (IllegalThreadStateException e) {
                process.destroy();
            } catch (Exception e){
                Log.d(TAG, "runRootCommand: "+e.toString());
                e.printStackTrace();
            }
        }
        return ok;
    }


    public static boolean  writeFile( String fileName,String desc){
        Log.d(TAG, "writeFile: "+fileName+" "+desc);
        try {
            FileOutputStream fos;
            File file = new File(fileName);
            if (!file.exists()) {
                return false;
            }
            fos = new FileOutputStream(file, false);
            byte[] content = desc.getBytes();
            Log.d(TAG, "writeFile: "+content);
            fos.write(content);
            fos.flush();
            fos.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
