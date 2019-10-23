package org.godotengine.godot;

import org.godotengine.godot.GodotLib;
import org.godotengine.godot.utils.Crypt;

import android.app.Activity;
import android.provider.Settings;

public class SBBUtils {

    public static String getDeviceId(Activity p_activity) {
		return Crypt.md5(
            Settings.Secure.getString(p_activity.getContentResolver(), Settings.Secure.ANDROID_ID)
        ).toUpperCase();
    }

    public static void pushMessage(int p_instance_id, String p_message) {
        GodotLib.calldeferred(p_instance_id, "_get_message", new Object[] { p_message });
    }
}