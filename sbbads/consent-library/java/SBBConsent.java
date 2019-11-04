package org.godotengine.godot;

import java.net.URL;
import java.net.MalformedURLException;

import com.godot.game.BuildConfig;

import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotLib;
import org.godotengine.godot.Dictionary;

import android.app.Activity;
import android.content.Context;

import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;


public class SBBConsent extends Godot.SingletonBase {

    protected Activity activity;
    protected Context context;
    private int instanceId;

    // instances
    private ConsentInformation consentInformation;

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
        SBBUtils.init(instanceId, "SBBConsent");

        // Auto set isTestDevice if debug build detected
        if (BuildConfig.DEBUG) {
            isTestDevice = true;
        }

        // Get consent information instance
        consentInformation = ConsentInformation.getInstance(context);


        /* Handle the Options Dictionary */
        
        if (SBBUtils.isValidOpt(p_options, "FORCE_TEST_DEVICE", Boolean.class)) {
            isTestDevice = p_options.get("FORCE_TEST_DEVICE");
        }

    }


    /**
     * Request Consent Info Update
     * 
     * @param p_publisherIds https://support.google.com/admob/answer/2784578
     * 
     * If the consent information is successfully updated, the updated consent 
     * status and the request location are provided via the
     * _on_consent_info_updated(String consentStatus, Boolean isRequestLocationInEeaOrUnknown) callback.
     * 
     * The returned consentStatus may have the values listed below:
     *  - PERSONALIZED      (The user has granted consent for personalized ads.)
     *  - NON_PERSONALIZED  (The user has granted consent for non-personalized ads.)
     *  - UNKNOWN           (The user has neither granted nor declined consent for personalized or non-personalized ads.)
     * 
     * If isRequestLocationInEeaOrUnknown is false, the user is not located in the European Economic Area and consent
     * is not required under the EU User Consent Policy. You can make ad requests to the Google Mobile Ads SDK.
     * 
     * If isRequestLocationInEeaOrUnknown is true:
     *  - If the returned ConsentStatus is PERSONALIZED or NON_PERSONALIZED,
     *    the user has already provided consent.
     *    You can now forward consent to the Google Mobile Ads SDK.
     * 
     *  - If the returned ConsentStatus is UNKNOWN, use collectConsent() to collect
     *    consent from the user.
     * 
     * https://developers.google.com/admob/android/eu-consent#update_consent_status
     * 
     */
     public void requestConsentInfoUpdate(String[] p_publisherIds) {
        
        consentInformation.requestConsentInfoUpdate(
            p_publisherIds, 
            new ConsentInfoUpdateListener() {

                @Override
                public void onConsentInfoUpdated(ConsentStatus consentStatus) {

                    // Get location
                    Boolean isRequestLocationInEeaOrUnknown = consentInformation.isRequestLocationInEeaOrUnknown();

                    SBBUtils.log(
                        "onConsentInfoUpdated, consentStatus: " + consentStatus + ", isRequestLocationInEeaOrUnknown: " + isRequestLocationInEeaOrUnknown);
                    GodotLib.calldeferred(instanceId, "_on_consent_info_updated",
                        new Object[] { consentStatus.name(), isRequestLocationInEeaOrUnknown });
                }
    
                @Override
                public void onFailedToUpdateConsentInfo(String errorDescription) {
                    SBBUtils.log("onFailedToUpdateConsentInfo, errorDescription: " + errorDescription);
                    GodotLib.calldeferred(instanceId, "_on_failed_to_update_consent_info", new Object[] { errorDescription });
                }
            }
        );

    }

    /**
     * 
     * @param p_privacyUrl
     * @param p_options
     */
    public void collectConsent(String p_privacyUrl, Dictionary p_options) {

        URL privacyUrl = null;

        try {
            privacyUrl = new URL(p_privacyUrl);
        } catch (MalformedURLException e) {
            SBBUtils.log("collectConsent, MalformedURLException: " + e.getMessage());
        }

        // Build the consent form with provided options
        ConsentForm form = new ConsentForm.Builder(context, privacyUrl)
            .withListener(new ConsentFormListener() {
                
                @Override
                public void onConsentFormLoaded() {
                    SBBUtils.log("onConsentFormLoaded");
                    GodotLib.calldeferred(instanceId, "_on_consent_form_loaded", new Object[] {});
                }

                @Override
                public void onConsentFormOpened() {
                    SBBUtils.log("onConsentFormOpened");
                    GodotLib.calldeferred(instanceId, "_on_consent_form_opened", new Object[] {});
                }

                @Override
                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                    SBBUtils.log(
                        "onConsentFormClosed, consentStatus: " + consentStatus + ", userPrefersAdFree: " + userPrefersAdFree);
                    GodotLib.calldeferred(instanceId, "_on_consent_form_closed",
                        new Object[] { consentStatus.name(), userPrefersAdFree });
                }

                @Override
                public void onConsentFormError(String errorDescription) {
                    SBBUtils.log("onConsentFormError, errorDescription: " + errorDescription);
                    GodotLib.calldeferred(instanceId, "_on_consent_form_error", new Object[] { errorDescription });
                }
            });


        if (SBBUtils.isValidOpt(p_options, "PERSONALIZED_ADS", Boolean.class)) {
            if (p_options.get("PERSONALIZED_ADS")) {
                form.withPersonalizedAdsOption();
            }
        }

        if (SBBUtils.isValidOpt(p_options, "NON_PERSONALIZED_ADS", Boolean.class)) {
            if (p_options.get("NON_PERSONALIZED_ADS")) {
                form.withNonPersonalizedAdsOption();
            }
        }

        if (SBBUtils.isValidOpt(p_options, "AD_FREE", Boolean.class)) {
            if (p_options.get("AD_FREE")) {
                form.withAdFreeOption();
            }
        }

        // TO-DO
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
            "requestConsentInfoUpdate",
            "collectConsent",
        });
        
        activity = p_activity;
        context = activity.getApplicationContext();
    }


    /* Activity States */
    protected void onMainPause() {
        SBBUtils.log( "onMainPause");
    }

    protected void onMainResume() {
        SBBUtils.log( "onMainResume");
    }

    protected void onMainDestroy() {
        SBBUtils.log( "onMainDestroy");
    }

}