package org.godotengine.godot;

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
        this.instanceId = p_instance_id;

        // Autoset isTestDevice if debug build detected
        if(BuildConfig.DEBUG) {
            this.isTestDevice = true;
        }

        // Instance ID
        SBBUtils.pushMessage(this.instanceId, "[SBBTest::init] Instance ID: " + this.instanceId);

        // Device ID
        SBBUtils.pushMessage(this.instanceId, "[SBBTest::init] Device ID: " + SBBUtils.getDeviceId(this.appActivity));

        // Test Device
        SBBUtils.pushMessage(this.instanceId, "[SBBTest::init] Test Device: " + this.isTestDevice);

        // Options Info
        p_options.forEach((k,v) -> SBBUtils.pushMessage(this.instanceId, 
            "[SBBTest::init] " + k + " : " + v + " (" + v.getClass().getName() + ")"
        ));
    }

    /* Godot Singletone Module Init */
    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new SBBTest(p_activity);
    }

    public SBBTest(Activity p_activity) {

        registerClass("SBBTest", new String[] {
            "init"
        });
        
        this.appActivity = p_activity;
    }

    /* Activity States */
    protected void onMainPause() {
        SBBUtils.pushMessage(this.instanceId, "[SBBTest::onMainPause]");
    }
    protected void onMainResume() {
        SBBUtils.pushMessage(this.instanceId, "[SBBTest::onMainResume]");
    }
    protected void onMainDestroy() {
        SBBUtils.pushMessage(this.instanceId, "[SBBTest::onMainDestroy]");
    }

}