package org.godotengine.godot;

import com.godot.game.BuildConfig;

import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotLib;
import org.godotengine.godot.Dictionary;

import android.app.Activity;
import android.content.Context;

import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentInfoUpdateListener;

public class SBBConsent extends Godot.SingletonBase {

    protected Activity activity;
    protected Context context;
    private int instanceId;

    // flags
    private Boolean isTestDevice = false;

    /**
     * Consent Library Init
     * 
     * @param p_instanceId 
     * @param p_options a dictionary of options, available values are:
     * 
     *      - FORCE_TEST_DEVICE (Boolean) [true, false]
     *          Flag the device as a test device (or not), this will overwrite the
     *          default behavior (test device true if a debug build or runs on AVD).
     *          https://developers.google.com/admob/android/eu-consent#testing
     */
    public void init(int p_instanceId, Dictionary p_options) {
        
        // Set godot instance id
        instanceId = p_instanceId;
        
        // Auto set isTestDevice if debug build detected
        if (BuildConfig.DEBUG) {
            isTestDevice = true;
        }

        /* Handle the Options Dictionary */
        
        if (p_options.containsKey("FORCE_TEST_DEVICE")) {

            if (p_options.get("FORCE_TEST_DEVICE") instanceof Boolean) {
                isTestDevice = (Boolean) p_options.get("FORCE_TEST_DEVICE");
            } else {
                SBBUtils.log(instanceId, "SBBConsent",
                    "FORCE_TEST_DEVICE, value type not valid! [" + p_options.get("FORCE_TEST_DEVICE").getClass().getName() + "]");
            }
            
        }
    }


    /**
     * Request Consent Info Update
     * 
     * @param p_publisherIds https://support.google.com/admob/answer/2784578
     */
     public void requestConsentInfoUpdate(String[] p_publisherIds) {

        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        
        consentInformation.requestConsentInfoUpdate(
            p_publisherIds, 
            new ConsentInfoUpdateListener() {
                @Override
                public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                    SBBUtils.log(instanceId, "SBBConsent", "onConsentInfoUpdated");
                    GodotLib.calldeferred(instanceId, "_on_rewarded_ad_loaded", new Object[] {});
                }
    
                @Override
                public void onFailedToUpdateConsentInfo(String errorDescription) {
                    SBBUtils.log(instanceId, "SBBConsent", "onFailedToUpdateConsentInfo");
                    GodotLib.calldeferred(instanceId, "_on_rewarded_ad_loaded", new Object[] {});
                }
            }
        );

    }


    /**
     * Godot Singleton Module Init
     * 
     * @param p_activity
     * @return a SBBConsent singleton instance
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
            "requestConsentInfoUpdate"
        });
        
        activity = p_activity;
        context = activity.getApplicationContext();
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