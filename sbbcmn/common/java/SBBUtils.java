package org.godotengine.godot;

import java.util.Arrays;
import java.util.Locale;

import org.godotengine.godot.GodotLib;
import org.godotengine.godot.utils.Crypt;

import android.os.Build;
import android.app.Activity;
import android.provider.Settings;

public class SBBUtils {


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
     * Log back to Godot
     * 
     * @param p_instanceId
     * @param p_tag
     * @param p_message
     */
    public static void log(int p_instanceId, String p_tag, String p_message) {
        pushMessage(p_instanceId, "[" + p_tag + "] " + p_message);
    }


    /**
     * Send a text message back to Godot,
     * messages are received by the _get_message()
     * callback function of the caller script
     * 
     * @param p_instanceId
     * @param p_message
     */
    public static void pushMessage(int p_instanceId, String p_message) {
        GodotLib.calldeferred(p_instanceId, "_get_message", new Object[] { p_message });
    }


}