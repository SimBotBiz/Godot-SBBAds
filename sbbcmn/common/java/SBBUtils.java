package org.godotengine.godot;

import java.util.Arrays;
import java.util.Locale;

import org.godotengine.godot.GodotLib;
import org.godotengine.godot.Dictionary;
import org.godotengine.godot.utils.Crypt;

import android.os.Build;
import android.app.Activity;
import android.provider.Settings;


public class SBBUtils {

    private static int instanceId;
    private static String tag;

    
    /**
     * Initialize Utils
     * 
     * @param p_instanceId
     */
    public static void init(int p_instanceId, String p_tag) {
        instanceId = p_instanceId;
        tag = p_tag;
    }


    /**
     * Get a valid Android Device Id
     * 
     * @param p_activity
     * @return DeviceId
     */
    public static String getDeviceId(Activity p_activity) {
		return Crypt.md5(
            Settings.Secure.getString(p_activity.getContentResolver(), Settings.Secure.ANDROID_ID)
        ).toUpperCase(Locale.US);
    }


    /**
     * is Java 8 Language Features Enabled ?
     * https://developer.android.com/studio/write/java8-support
     * 
     * @return true if sdk version is >= 24
     */
     public static boolean isJ8LFE() {

        if (Build.VERSION.SDK_INT >= 24) {
            return true;
        }
            
        return false;
    }


    /**
     * Check if value in array
     * 
     * @param p_array
     * @param p_value
     * @return true if value in array
     */
    public static <T> boolean anyMatch(final T[] p_array, final T p_value) {
        
        if (isJ8LFE()) {
            return Arrays.stream(p_array).anyMatch(p_value::equals);
        } 
        
        return Arrays.asList(p_array).contains(p_value);
    }


    /**
     * Validate dictionary option
     * 
     * @param p_dic
     * @param p_opt
     * @param type
     * @return true if option exists and type is correct
     */
    public static boolean isValidOpt(Dictionary p_dic, String p_opt, Class type) {
        if (p_dic.containsKey(p_opt)) {
            if (type.isInstance(p_dic.get(p_opt))) {
                return true;
            } else {
                SBBUtils.log(p_opt + ", value type not valid! [" + p_dic.get(p_opt).getClass().getName() + "]");
            }
        }

        return false;
    }


    /**
     * Log back to Godot
     * 
     * @param p_message
     */
    public static void log(String p_message) {
        pushMessage("[" + tag + "] " + p_message);
    }


    /**
     * Send a text message back to Godot,
     * messages are received by the _get_message()
     * callback function of the caller script
     * 
     * @param p_message
     */
    public static void pushMessage(String p_message) {
        GodotLib.calldeferred(instanceId, "_get_message", new Object[] { p_message });
    }


}