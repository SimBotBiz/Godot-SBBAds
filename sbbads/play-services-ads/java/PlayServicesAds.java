package org.godotengine.godot;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.godot.game.R;
import com.godot.game.BuildConfig;

import org.godotengine.godot.Godot;
import org.godotengine.godot.Dictionary;
import org.godotengine.godot.utils.Crypt;

import android.app.Activity;
import android.util.Log;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;


public class PlayServicesAds extends Godot.SingletonBase {

    protected Activity activity;
    private int instanceId;
    private Dictionary adOptions;
    private RewardedAd rewardedAd;
    private boolean isTestDevice = false;
    private boolean useTestAds = false;


    /**
     * Play Services Ads Init
     * 
     * @param p_instance_id
     * @param p_ad_options  a dictionary of ad options, available values are:
     *      - FORCE_TEST_DEVICE [Boolean] (true, false)
     *      - USE_TEST_ADS [Boolean] (true, false) !not implemented yet
     *      - NON_PERSONALIZED_ADS [Boolean] (true, false)
     *      - TAG_FOR_CHILD_DIRECTED_TREATMENT [Integer] (1 = true, 0 = false, -1 = unspecified)
     *      - TAG_FOR_UNDER_AGE_OF_CONSENT [Integer] (1 = true, 0 = false, -1 = unspecified)
     *      - MAX_AD_CONTENT_RATING [String] ('', 'G', 'PG', 'T', 'MA')
     */
    public void init(int p_instance_id, Dictionary p_ad_options) {
        
        // Set godot instance id
        instanceId = p_instance_id;

        // Autoset isTestDevice if debug build detected
        if(BuildConfig.DEBUG) {
            isTestDevice = true;
        }

        // Initialize MobileAds
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                SBBUtils.log(instanceId, "PlayServiceAds", "onInitializationComplete");
                GodotLib.calldeferred(instanceId, "_on_initialization_complete", new Object[] {});
            }
        });

        /* Handle Ad Options Dictionary */
        
        if(p_ad_options.containsKey("FORCE_TEST_DEVICE")) {

            if(p_ad_options.get("FORCE_TEST_DEVICE") instanceof Boolean) {
                isTestDevice = p_ad_options.get("FORCE_TEST_DEVICE");
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "FORCE_TEST_DEVICE, value type not valid! [" + p_ad_options.get("FORCE_TEST_DEVICE").getClass().getName() + "]");
            }
            
        }

        if(p_ad_options.containsKey("USE_TEST_ADS")) {

            if(p_ad_options.get("USE_TEST_ADS") instanceof Boolean) {
                useTestAds = p_ad_options.get("USE_TEST_ADS");
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "USE_TEST_ADS, value type not valid! [" + p_ad_options.get("USE_TEST_ADS").getClass().getName() + "]");
            }

        }

        if(p_ad_options.containsKey("NON_PERSONALIZED_ADS")) {
            // TODO
        }

        if(p_ad_options.containsKey("TAG_FOR_CHILD_DIRECTED_TREATMENT")) {
            
            Integer[] allowed_values = {
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED,
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE,
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE,
            };

            if(SBBUtils.anyMatch(allowed_values, p_ad_options.get("TAG_FOR_CHILD_DIRECTED_TREATMENT"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setTagForChildDirectedTreatment(p_ad_options.get("TAG_FOR_CHILD_DIRECTED_TREATMENT"))
                    .build();
                // set configuration
                MobileAds.setRequestConfiguration(requestConfiguration);
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "TAG_FOR_CHILD_DIRECTED_TREATMENT, value not allowed!");
            }            
        }
        
        if (p_ad_options.containsKey("TAG_FOR_UNDER_AGE_OF_CONSENT")) {
            
            Integer[] allowed_values = {
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED,
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE,
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE,
            };

            if(SBBUtils.anyMatch(allowed_values, p_ad_options.get("TAG_FOR_UNDER_AGE_OF_CONSENT"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setTagForUnderAgeOfConsent(p_ad_options.get("TAG_FOR_UNDER_AGE_OF_CONSENT"))
                    .build();
                // set configuration
                MobileAds.setRequestConfiguration(requestConfiguration);
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "TAG_FOR_UNDER_AGE_OF_CONSENT, value not allowed!");
            }
        }

        if (p_ad_options.containsKey("MAX_AD_CONTENT_RATING")) {
            
            String[] allowed_values = {
                RequestConfiguration.MAX_AD_CONTENT_RATING_UNSPECIFIED,
                RequestConfiguration.MAX_AD_CONTENT_RATING_G,
                RequestConfiguration.MAX_AD_CONTENT_RATING_PG,
                RequestConfiguration.MAX_AD_CONTENT_RATING_T,
                RequestConfiguration.MAX_AD_CONTENT_RATING_MA,
            };

            if(SBBUtils.anyMatch(allowed_values, p_ad_options.get("MAX_AD_CONTENT_RATING"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setMaxAdContentRating(p_ad_options.get("MAX_AD_CONTENT_RATING"))
                    .build();
                // set configuration
                MobileAds.setRequestConfiguration(requestConfiguration);
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "MAX_AD_CONTENT_RATING, value not allowed!");
            }
        }
    }


    /**
     * Load Rewarded Ad
     * 
     * @param p_ad_unit_id
     */
    public void loadRewardedAd(String p_ad_unit_id) {
        
        if (rewardedAd == null || !rewardedAd.isLoaded()) {
            
            rewardedAd = new RewardedAd(activity, p_ad_unit_id);
            
            rewardedAd.loadAd(
                buildAdRequest(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onRewardedAdLoaded() {
                        SBBUtils.log(instanceId, "PlayServiceAds", "onRewardedAdLoaded");
                        GodotLib.calldeferred(instanceId, "_on_rewarded_ad_loaded", new Object[] {});
                    }

                    @Override
                    public void onRewardedAdFailedToLoad(int errorCode) {
                        SBBUtils.log(instanceId, "PlayServiceAds", "onRewardedAdFailedToLoad, errorCode: " + errorCode);
                        GodotLib.calldeferred(instanceId, "_on_rewarded_ad_failed_to_loaded", new Object[] { errorCode });
                    }
                }
            );

        }
    
    }


    /**
     * Godot Singletone Module Init
     * 
     * @param p_activity
     * @return a PlayServicesAds singleton instance
     */
    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new PlayServicesAds(p_activity);
    }


    /**
     * PlayServicesAds contructor
     * 
     * @param p_activity
     */
    public PlayServicesAds(Activity p_activity) {

        registerClass("PlayServicesAds", new String[] {
            "init",
            "loadRewardedAd"
        });
        
        appActivity = p_activity;
    }


    /* Activity States */
    protected void onMainPause() {}
    protected void onMainResume() {}
    protected void onMainDestroy() {}


    /* PRIVATE METHODS 
     * ********************************************************************* */
    
    /**
     * Build Ad Request
     * 
     * @return a configured AdRequest
     */
    private AdRequest buildAdRequest() {
        AddRequest.Builder adBuilder = new AdRequest.Builder();

        if(isTestDevice) {
            adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            adBuilder.addTestDevice(SBBUtils.getDeviceId(activity));
        }

        return adBuilder.Build();
    }

}