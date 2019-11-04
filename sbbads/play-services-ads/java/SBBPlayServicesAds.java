package org.godotengine.godot;

import java.util.Map;

import com.godot.game.R;
import com.godot.game.BuildConfig;

import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotLib;
import org.godotengine.godot.Dictionary;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import com.google.ads.mediation.admob.AdMobAdapter;


public class SBBPlayServicesAds extends Godot.SingletonBase {

    protected Activity activity;
    private int instanceId;

    // flags
    private Boolean isTestDevice = false;
    private Boolean isMobileAdsInit = false;
    private Boolean useTestAds = false;
    private Boolean useNonPersonalizedAds = false;

    // instances
    private RewardedAd rewardedAd;

    // test ad ids
    private final String bannerAd_testId = "ca-app-pub-3940256099942544/6300978111";
    private final String rewardedAd_testId = "ca-app-pub-3940256099942544/5224354917";
    private final String interstitialAd_testId = "ca-app-pub-3940256099942544/1033173712";


    /**
     * Play Services Ads Init
     * 
     * @param p_instanceId
     * @param p_options  a dictionary of options, available values are:
     * 
     *      - FORCE_TEST_DEVICE [Boolean] (true, false)
     *          Flag the device as a test device (or not), this will overwrite the
     *          default behavior (test device true if a debug build or runs on AVD).
     *          https://developers.google.com/admob/android/test-ads#enable_test_devices
     * 
     *      - USE_TEST_ADS [Boolean] (true, false)
     *          Provided ad UID will be replaced by the appropriate test UID,
     *          using these test ads gives a more consistent behavior,
     *          ads are always show, that's not true with only the test device flag.
     *          https://developers.google.com/admob/android/test-ads#sample_ad_units
     * 
     *      - NON_PERSONALIZED_ADS [Boolean] (true, false),
     *          This is what you need to set if using also the consent module!
     *          https://developers.google.com/admob/android/eu-consent#forward_consent_to_the_google_mobile_ads_sdk
     * 
     *      - TAG_FOR_CHILD_DIRECTED_TREATMENT [Integer] (1 = true, 0 = false, -1 = unspecified)
     *          https://developers.google.com/admob/android/targeting#child-directed_setting
     * 
     *      - TAG_FOR_UNDER_AGE_OF_CONSENT [Integer] (1 = true, 0 = false, -1 = unspecified)
     *          https://developers.google.com/admob/android/targeting#users_under_the_age_of_consent
     *          
     *      - MAX_AD_CONTENT_RATING [String] ('', 'G', 'PG', 'T', 'MA')
     *          https://developers.google.com/admob/android/targeting#ad_content_filtering
     */
    public void init(int p_instanceId, Dictionary p_options) {
        
        // Set godot instance id
        instanceId = p_instanceId;
        SBBUtils.init(instanceId, "SBBPlayServicesAds");

        // Auto set isTestDevice if debug build detected
        if (BuildConfig.DEBUG) {
            isTestDevice = true;
        }

        /**
         * Initialize MobileAds
         * 
         * MobileAds is initialized only once, I prefer to save the status
         * in isMobileAdsInit, so I can return a meaningful state when init is 
         * called again
         * 
         */ 
        if (!isMobileAdsInit) {
            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    
                    /**
                     * For some reason this function is called twice upon initialization,
                     * because I want to avoid firing the godot signal twice
                     * I will use isMobileAdsInit flag to avoid that.
                     */

                    // log the status map
                    for (Map.Entry<String, AdapterStatus> status : initializationStatus.getAdapterStatusMap().entrySet())  {
                        SBBUtils.log(
                            "InitializationStatus: " + status.getKey() + " : " + status.getValue().getInitializationState() + " (" + status.getValue().getDescription() + ")");
                    }
                    
                    if (!isMobileAdsInit) {
                        SBBUtils.log("onInitializationComplete");
                        GodotLib.calldeferred(instanceId, "_on_initialization_complete", new Object[] {});
                        isMobileAdsInit = true;
                    }
                    
                }
            });
        } else {
            SBBUtils.log("MobileAds already initialized!");
            GodotLib.calldeferred(instanceId, "_on_initialization_complete", new Object[] {});
        }
        
        

        /* Handle the Options Dictionary */
        
        if (SBBUtils.isValidOpt(p_options, "FORCE_TEST_DEVICE", Boolean.class)) {
            isTestDevice = p_options.get("FORCE_TEST_DEVICE");
        }

        if (SBBUtils.isValidOpt(p_options, "USE_TEST_ADS", Boolean.class)) {
            useTestAds = p_options.get("USE_TEST_ADS");
        }

        if (SBBUtils.isValidOpt(p_options, "NON_PERSONALIZED_ADS", Boolean.class)) {
            useNonPersonalizedAds = p_options.get("NON_PERSONALIZED_ADS");
        }

        if (SBBUtils.isValidOpt(p_options, "TAG_FOR_CHILD_DIRECTED_TREATMENT", Integer.class)) {
            
            Integer[] allowedValues = {
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED,
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE,
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE,
            };

            if (SBBUtils.anyMatch(allowedValues, p_options.get("TAG_FOR_CHILD_DIRECTED_TREATMENT"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setTagForChildDirectedTreatment((Integer) p_options.get("TAG_FOR_CHILD_DIRECTED_TREATMENT"))
                    .build();
                // set configuration
                MobileAds.setRequestConfiguration(requestConfiguration);
            } else {
                SBBUtils.log(
                    "TAG_FOR_CHILD_DIRECTED_TREATMENT, value not allowed!");
            }

        }
        
        if (SBBUtils.isValidOpt(p_options, "TAG_FOR_UNDER_AGE_OF_CONSENT", Integer.class)) {
            
            Integer[] allowedValues = {
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED,
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE,
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE,
            };

            if (SBBUtils.anyMatch(allowedValues, p_options.get("TAG_FOR_UNDER_AGE_OF_CONSENT"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setTagForUnderAgeOfConsent((Integer) p_options.get("TAG_FOR_UNDER_AGE_OF_CONSENT"))
                    .build();
                // set configuration
                MobileAds.setRequestConfiguration(requestConfiguration);
            } else {
                SBBUtils.log(
                    "TAG_FOR_UNDER_AGE_OF_CONSENT, value not allowed!");
            }

        }

        if (SBBUtils.isValidOpt(p_options, "MAX_AD_CONTENT_RATING", String.class)) {
            
            String[] allowedValues = {
                RequestConfiguration.MAX_AD_CONTENT_RATING_UNSPECIFIED,
                RequestConfiguration.MAX_AD_CONTENT_RATING_G,
                RequestConfiguration.MAX_AD_CONTENT_RATING_PG,
                RequestConfiguration.MAX_AD_CONTENT_RATING_T,
                RequestConfiguration.MAX_AD_CONTENT_RATING_MA,
            };

            if (SBBUtils.anyMatch(allowedValues, p_options.get("MAX_AD_CONTENT_RATING"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setMaxAdContentRating((String) p_options.get("MAX_AD_CONTENT_RATING"))
                    .build();
                // set configuration
                MobileAds.setRequestConfiguration(requestConfiguration);
            } else {
                SBBUtils.log(
                    "MAX_AD_CONTENT_RATING, value not allowed!");
            }

        }

    }


    /**
     * Load Rewarded Ad
     * 
     * @param p_adUnitId
     */
    public void loadRewardedAd(String p_adUnitId) {
        
        final String adUnitId;

        if (useTestAds) {
            adUnitId = rewardedAd_testId;
        } else {
            adUnitId = p_adUnitId;
        }

        activity.runOnUiThread(new Runnable() {    
            @Override public void run() {

                if (rewardedAd == null || !rewardedAd.isLoaded()) {
            
                    rewardedAd = new RewardedAd(activity, adUnitId);
                    
                    rewardedAd.loadAd(
                        buildAdRequest(),
                        new RewardedAdLoadCallback() {
                            @Override
                            public void onRewardedAdLoaded() {
                                SBBUtils.log("onRewardedAdLoaded");
                                GodotLib.calldeferred(instanceId, "_on_rewarded_ad_loaded", new Object[] {});
                            }
        
                            @Override
                            public void onRewardedAdFailedToLoad(int p_errorCode) {
                                SBBUtils.log("onRewardedAdFailedToLoad, errorCode: " + p_errorCode);
                                GodotLib.calldeferred(instanceId, "_on_rewarded_ad_failed_to_loaded", new Object[] { p_errorCode });
                            }
                        }
                    );
        
                } else {
                    // ad is already loaded
                    SBBUtils.log("RewardedAd already loaded!");
                    GodotLib.calldeferred(instanceId, "_on_rewarded_ad_loaded", new Object[] {});
                }

            }
        });
        
    }


    public void showRewardedAd() {
        activity.runOnUiThread(new Runnable() {    
            @Override public void run() {

                if (rewardedAd.isLoaded()) {

                    // prepare callback
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            SBBUtils.log("onRewardedAdOpened");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_ad_opened", new Object[] {});
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            SBBUtils.log("onRewardedAdClosed");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_ad_closed", new Object[] {});
                        }

                        @Override
                        public void onUserEarnedReward(RewardItem p_rewardItem) {
                            SBBUtils.log(
                                "onUserEarnedReward, currency: " + p_rewardItem.getType() + ", amount: " + p_rewardItem.getAmount());
                            GodotLib.calldeferred(instanceId, "_on_user_earned_reward",
                                new Object[] { p_rewardItem.getType(), p_rewardItem.getAmount() });
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int p_errorCode) {
                            SBBUtils.log("onRewardedAdFailedToShow, errorCode: " + p_errorCode);
                            GodotLib.calldeferred(instanceId, "_on_rewarded_ad_failed_to_show", new Object[] { p_errorCode });
                        }
                    };

                    // show ad
                    rewardedAd.show(activity, adCallback);

                }
                
            }
        });
    }

    /**
     * Godot Singleton Module Init
     * 
     * @param p_activity
     * @return a SBBPlayServicesAds singleton instance
     */
    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new SBBPlayServicesAds(p_activity);
    }


    /**
     * SBBPlayServicesAds constructor
     * 
     * @param p_activity
     */
    public SBBPlayServicesAds(Activity p_activity) {

        registerClass("SBBPlayServicesAds", new String[] {
            "init",
            "loadRewardedAd", "showRewardedAd",
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


    /* PRIVATE METHODS 
     * ********************************************************************* */
    
    /**
     * Build Ad Request
     * 
     * @return a configured AdRequest
     */
    private AdRequest buildAdRequest() {
        AdRequest.Builder adBuilder = new AdRequest.Builder();

        if (isTestDevice) {
            adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            adBuilder.addTestDevice(SBBUtils.getDeviceId(activity));
        }

        if (useNonPersonalizedAds) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        return adBuilder.build();
    }

}