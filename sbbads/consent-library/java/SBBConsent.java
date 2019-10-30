package org.godotengine.godot;

import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotLib;

import android.app.Activity;


public class SBBConsent extends Godot.SingletonBase {

    protected Activity activity;
    private int instanceId;

    /**
     * Consent Library Init
     * 
     * @param p_instanceId
     */
    public void init(int p_instanceId) {
        
        // Set godot instance id
        instanceId = p_instanceId;
        
        // TO-DO
    }


    /**
     * Godot Singleton Module Init
     * 
     * @param p_activity
     * @return a PlayServicesAds singleton instance
     */
    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new SBBConsent(p_activity);
    }


    /**
     * SBBConsent constructor
     * 
     * @param p_activity
     */
    public SBBConsent(Activity p_activity) {

        registerClass("SBBConsent", new String[] {
            "init",
        });
        
        activity = p_activity;
    }


    /* Activity States */
    protected void onMainPause() {
        SBBUtils.log(instanceId, "SBBConsent", "onMainPause");
    }

    protected void onMainResume() {
        SBBUtils.log(instanceId, "SBBConsent", "onMainResume");
    }

    protected void onMainDestroy() {
        SBBUtils.log(instanceId, "SBBConsent", "onMainDestroy");
    }

}