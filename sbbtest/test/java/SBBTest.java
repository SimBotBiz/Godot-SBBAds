package org.godotengine.godot;

import java.util.Map;

// import com.godot.game.R;
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
     * @param p_instance_id
     * @param p_options
     */
    public void init(int p_instance_id, Dictionary p_options) {
        
        // Set godot instance id
        instanceId = p_instance_id;

        // Autoset isTestDevice if debug build detected
        if(BuildConfig.DEBUG) {
            isTestDevice = true;
        }

        // Instance ID
        SBBUtils.pushMessage(instanceId, "[SBBTest::init] Instance ID: " + instanceId);

        // Device ID
        SBBUtils.pushMessage(instanceId, "[SBBTest::init] Device ID: " + SBBUtils.getDeviceId(activity));

        // Test Device
        SBBUtils.pushMessage(instanceId, "[SBBTest::init] Test Device: " + isTestDevice);

        // Java version
        SBBUtils.pushMessage(instanceId, "[SBBTest::init] Java 8 Language Features: " + SBBUtils.isJ8LFE());

        // Options Info (java 8 - min sdk 24)
        if(SBBUtils.isJ8LFE()) {
            p_options.forEach((k,v) -> SBBUtils.pushMessage(instanceId, 
                "[SBBTest::init] " + k + " : " + v + " (" + v.getClass().getName() + ")"
            ));
        } else {
            for(Map.Entry<String, Object> entry : p_options.entrySet())  {
                SBBUtils.pushMessage(instanceId, 
                    "[SBBTest::init] " + entry.getKey() + " : " + entry.getValue() + " (" + entry.getValue().getClass().getName() + ")"
                );
            }
        }
    }

    /* Godot Singletone Module Init */
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
        SBBUtils.pushMessage(instanceId, "[SBBTest::onMainPause]");
    }
    protected void onMainResume() {
        SBBUtils.pushMessage(instanceId, "[SBBTest::onMainResume]");
    }
    protected void onMainDestroy() {
        SBBUtils.pushMessage(instanceId, "[SBBTest::onMainDestroy]");
    }

}