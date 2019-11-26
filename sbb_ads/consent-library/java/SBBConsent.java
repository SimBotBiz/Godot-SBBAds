package org.godotengine.godot;

import java.net.URL;
import java.net.MalformedURLException;

import com.godot.game.BuildConfig;

import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotLib;
import org.godotengine.godot.Dictionary;

import android.app.Activity;

import com.google.ads.consent.DebugGeography;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;


public class SBBConsent extends Godot.SingletonBase {

    protected Activity activity;
    private int instanceId;

    // instances
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;

    // flags
    private boolean isTestDevice = false;
    private DebugGeography debugGeography = DebugGeography.DEBUG_GEOGRAPHY_DISABLED;

    /**
     * Consent Library Init
     * 
     * @param p_instanceId 
     * @param p_options a dictionary of options, available values are:
     * 
     *      - FORCE_TEST_DEVICE [Boolean] (true, false)
     *          Flag the device as a test device (or not), this will overwrite the
     *          default behavior (test device true if a debug build or runs on AVD).
     *          https://developers.google.com/admob/android/eu-consent#testing
     *       
     *      - DEBUG_GEOGRAPHY [String] ('DEBUG_GEOGRAPHY_DISABLED', 'DEBUG_GEOGRAPHY_EEA', 'DEBUG_GEOGRAPHY_NOT_EEA')
     *          https://developers.google.com/admob/android/eu-consent#testing
     * 
     *      - TAG_FOR_UNDER_AGE_OF_CONSENT [Boolean] (true, false)
     *          If a publisher is aware that the user is under the age of consent,
     *          all ad requests must set TFUA (Tag For Users under the Age of Consent in Europe).
     *          This setting takes effect for all future ad requests.
     *          Once the TFUA setting is enabled, the Google-rendered consent form will fail to load.
     *          https://developers.google.com/admob/android/eu-consent#users_under_the_age_of_consent
     */
    public void init(int p_instanceId, Dictionary p_options) {
        
        // Set godot instance id
        instanceId = p_instanceId;
        SBBUtils.init(instanceId, "SBBConsent");

        // debug
        if (BuildConfig.DEBUG) {
            isTestDevice = true;

            // debug options
            SBBUtils.logVar("p_instanceId", p_instanceId);
            SBBUtils.logDict("p_options", p_options);
        }

        // Get consent information instance
        consentInformation = ConsentInformation.getInstance(activity);


        /* Handle the Options Dictionary */
        
        if (SBBUtils.isValidOpt(p_options, "FORCE_TEST_DEVICE", Boolean.class)) {
            isTestDevice = (boolean) p_options.get("FORCE_TEST_DEVICE");
        }

        if (SBBUtils.isValidOpt(p_options, "DEBUG_GEOGRAPHY", String.class)) {
            if (SBBUtils.anyMatch(DebugGeography.values(), DebugGeography.valueOf((String) p_options.get("DEBUG_GEOGRAPHY")))) {
               debugGeography = DebugGeography.valueOf((String) p_options.get("DEBUG_GEOGRAPHY"));
            } else {
                SBBUtils.log("DEBUG_GEOGRAPHY, value not allowed!");
            }
        }

        // debug options
        if (isTestDevice) {
            // add test device
            consentInformation.addTestDevice(SBBUtils.getDeviceId(activity));
            // set debug geography
            consentInformation.setDebugGeography(debugGeography);
        }

        if (SBBUtils.isValidOpt(p_options, "TAG_FOR_UNDER_AGE_OF_CONSENT", Boolean.class)) {
            consentInformation.setTagForUnderAgeOfConsent((boolean) p_options.get("TAG_FOR_UNDER_AGE_OF_CONSENT"));
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

                    SBBUtils.log("onConsentInfoUpdated, consentStatus: " + consentStatus
                        + ", isRequestLocationInEeaOrUnknown: " + isRequestLocationInEeaOrUnknown);
                    
                    GodotLib.calldeferred(instanceId, "_on_consent_info_updated",
                        new Object[] { consentStatus.name(), isRequestLocationInEeaOrUnknown });
                }
    
                @Override
                public void onFailedToUpdateConsentInfo(String errorDescription) {
                    SBBUtils.log("onFailedToUpdateConsentInfo, errorDescription: " + errorDescription);
                    GodotLib.calldeferred(instanceId, "_on_failed_to_update_consent_info",
                        new Object[] { errorDescription });
                }
            }
        );

    }

    /**
     * Collect Consent
     *
     * @param p_privacyUrl
     * @param p_options a dictionary of options, available values are:
     * 
     *      - PERSONALIZED_ADS [Boolean] (true, false)
     *      - NON_PERSONALIZED_ADS [Boolean] (true, false)
     *      - AD_FREE [Boolean] (true, false)
     *      https://developers.google.com/admob/android/eu-consent#google_rendered_consent_form
     */
    public void collectConsent(String p_privacyUrl, Dictionary p_options) {

        // debug
        if (BuildConfig.DEBUG) {
            // log function parameters
            SBBUtils.logVar("p_privacyUrl", p_privacyUrl);
            SBBUtils.logDict("p_options", p_options);
        }

        URL privacyUrl = null;

        try {
            privacyUrl = new URL(p_privacyUrl);
        } catch (MalformedURLException e) {
            SBBUtils.log("collectConsent, MalformedURLException: " + e.getMessage());
        }

        // Build the consent form with provided options
        ConsentForm.Builder formBuilder = new ConsentForm.Builder(activity, privacyUrl)
            .withListener(new ConsentFormListener() {
                
                @Override
                public void onConsentFormLoaded() {
                    SBBUtils.log("onConsentFormLoaded");
                    GodotLib.calldeferred(instanceId, "_on_consent_form_loaded", new Object[] {});

                    // show consent form
                    consentForm.show();
                }

                @Override
                public void onConsentFormOpened() {
                    SBBUtils.log("onConsentFormOpened");
                    GodotLib.calldeferred(instanceId, "_on_consent_form_opened", new Object[] {});
                }

                @Override
                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                    SBBUtils.log("onConsentFormClosed, consentStatus: " + consentStatus
                        + ", userPrefersAdFree: " + userPrefersAdFree);
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
            if ((Boolean) p_options.get("PERSONALIZED_ADS")) {
                formBuilder.withPersonalizedAdsOption();
            }
        }

        if (SBBUtils.isValidOpt(p_options, "NON_PERSONALIZED_ADS", Boolean.class)) {
            if ((Boolean) p_options.get("NON_PERSONALIZED_ADS")) {
                formBuilder.withNonPersonalizedAdsOption();
            }
        }

        if (SBBUtils.isValidOpt(p_options, "AD_FREE", Boolean.class)) {
            if ((Boolean) p_options.get("AD_FREE")) {
                formBuilder.withAdFreeOption();
            }
        }

        activity.runOnUiThread(new Runnable() {    
            @Override public void run() {
                // build the form
                consentForm = formBuilder.build();

                // load the form
                consentForm.load();
            }
        });
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