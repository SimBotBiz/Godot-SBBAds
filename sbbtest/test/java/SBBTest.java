package org.godotengine.godot;

import com.godot.game.BuildConfig;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Dictionary;

import android.app.Activity;

public class SBBTest extends Godot.SingletonBase {

    protected Activity activity;
    private int instanceId;
    private boolean isTestDevice = false;
    private Dictionary options;

    /**
     * SBBTest Init
     * @param p_instanceId
     * @param p_options
     */
    public void init(int p_instanceId, Dictionary p_options) {
        
        // Set godot instance id
        instanceId = p_instanceId;
        SBBUtils.init(instanceId, "SBBTest");

        // Auto set isTestDevice if debug build detected
        if (BuildConfig.DEBUG) {
            isTestDevice = true;
        }

        // Instance ID
        SBBUtils.log("Instance ID: " + instanceId);

        // Device ID
        SBBUtils.log("Device ID: " + SBBUtils.getDeviceId(activity));

        // Test Device
        SBBUtils.log("Test Device: " + isTestDevice);

        // Java version
        SBBUtils.log("Java 8 Language Features: " + SBBUtils.isJ8LFE());

        // Options Info
        SBBUtils.logDict("Options", p_options);
    }


    /* Godot Singleton Module Init */
    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new SBBTest(p_activity);
    }

    public SBBTest(Activity p_activity) {

        registerClass("SBBTest", new String[] {
            "init"
        });
        
        activity = p_activity;
    }


    /* Activity States */
    protected void onMainPause() {
        SBBUtils.log("onMainPause");
    }

    protected void onMainResume() {
        SBBUtils.log("onMainResume");
    }
    
    protected void onMainDestroy() {
        SBBUtils.log("onMainDestroy");
    }

}