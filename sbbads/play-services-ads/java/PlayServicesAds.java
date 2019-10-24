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
import jdk.nashorn.internal.ir.BlockLexicalContext;
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

import com.google.ads.mediation.admob.AdMobAdapter;


public class PlayServicesAds extends Godot.SingletonBase {

    protected Activity activity;
    private int instanceId;
    private Dictionary adOptions;
    private RewardedAd rewardedAd;
    private Boolean isTestDevice = false;
    private Boolean useTestAds = false;
    private Boolean useNonPersAds = false;

    // test ad ids
    private final String bannerAd_testId = "ca-app-pub-3940256099942544/6300978111";
    private final String rewardedAd_testId = "ca-app-pub-3940256099942544/5224354917";
    private final String interstitialAd_testId = "ca-app-pub-3940256099942544/1033173712";


    /**
     * Play Services Ads Init
     * 
     * @param p_instanceId
     * @param p_adOptions  a dictionary of ad options, available values are:
     *      - FORCE_TEST_DEVICE [Boolean] (true, false)
     *      - USE_TEST_ADS [Boolean] (true, false)
     *      - NON_PERSONALIZED_ADS [Boolean] (true, false)
     *      - TAG_FOR_CHILD_DIRECTED_TREATMENT [Integer] (1 = true, 0 = false, -1 = unspecified)
     *      - TAG_FOR_UNDER_AGE_OF_CONSENT [Integer] (1 = true, 0 = false, -1 = unspecified)
     *      - MAX_AD_CONTENT_RATING [String] ('', 'G', 'PG', 'T', 'MA')
     */
    public void init(int p_instanceId, Dictionary p_adOptions) {
        
        // Set godot instance id
        instanceId = p_instanceId;

        // Autoset isTestDevice if debug build detected
        if (BuildConfig.DEBUG) {
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
        
        if (p_adOptions.containsKey("FORCE_TEST_DEVICE")) {

            if (p_adOptions.get("FORCE_TEST_DEVICE") instanceof Boolean) {
                isTestDevice = (Boolean) p_adOptions.get("FORCE_TEST_DEVICE");
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "FORCE_TEST_DEVICE, value type not valid! [" + p_adOptions.get("FORCE_TEST_DEVICE").getClass().getName() + "]");
            }
            
        }

        if (p_adOptions.containsKey("USE_TEST_ADS")) {

            if (p_adOptions.get("USE_TEST_ADS") instanceof Boolean) {
                useTestAds = (Boolean) p_adOptions.get("USE_TEST_ADS");
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "USE_TEST_ADS, value type not valid! [" + p_adOptions.get("USE_TEST_ADS").getClass().getName() + "]");
            }

        }

        if (p_adOptions.containsKey("NON_PERSONALIZED_ADS")) {

            if (p_adOptions.get("NON_PERSONALIZED_ADS") instanceof Boolean) {
                useNonPersAds = (Boolean) p_adOptions.get("NON_PERSONALIZED_ADS");
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "NON_PERSONALIZED_ADS, value type not valid! [" + p_adOptions.get("NON_PERSONALIZED_ADS").getClass().getName() + "]");
            }

        }

        if (p_adOptions.containsKey("TAG_FOR_CHILD_DIRECTED_TREATMENT")) {
            
            Integer[] allowedValues = {
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED,
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE,
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE,
            };

            if (SBBUtils.anyMatch(allowedValues, p_adOptions.get("TAG_FOR_CHILD_DIRECTED_TREATMENT"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setTagForChildDirectedTreatment((Integer) p_adOptions.get("TAG_FOR_CHILD_DIRECTED_TREATMENT"))
                    .build();
                // set configuration
                MobileAds.setRequestConfiguration(requestConfiguration);
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "TAG_FOR_CHILD_DIRECTED_TREATMENT, value not allowed!");
            }

        }
        
        if (p_adOptions.containsKey("TAG_FOR_UNDER_AGE_OF_CONSENT")) {
            
            Integer[] allowedValues = {
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED,
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE,
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE,
            };

            if (SBBUtils.anyMatch(allowedValues, p_adOptions.get("TAG_FOR_UNDER_AGE_OF_CONSENT"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setTagForUnderAgeOfConsent((Integer) p_adOptions.get("TAG_FOR_UNDER_AGE_OF_CONSENT"))
                    .build();
                // set configuration
                MobileAds.setRequestConfiguration(requestConfiguration);
            } else {
                SBBUtils.log(instanceId, "PlayServiceAds",
                    "TAG_FOR_UNDER_AGE_OF_CONSENT, value not allowed!");
            }

        }

        if (p_adOptions.containsKey("MAX_AD_CONTENT_RATING")) {
            
            String[] allowedValues = {
                RequestConfiguration.MAX_AD_CONTENT_RATING_UNSPECIFIED,
                RequestConfiguration.MAX_AD_CONTENT_RATING_G,
                RequestConfiguration.MAX_AD_CONTENT_RATING_PG,
                RequestConfiguration.MAX_AD_CONTENT_RATING_T,
                RequestConfiguration.MAX_AD_CONTENT_RATING_MA,
            };

            if (SBBUtils.anyMatch(allowedValues, p_adOptions.get("MAX_AD_CONTENT_RATING"))) {
                // get and update actual configuration
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setMaxAdContentRating((String) p_adOptions.get("MAX_AD_CONTENT_RATING"))
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
     * @param p_adUnitId
     */
    public void loadRewardedAd(String p_adUnitId) {
        
        // override ad id if test ad option is true
        if (useTestAds) {
            p_adUnitId = rewardedAd_testId;
        }

        if (rewardedAd == null || !rewardedAd.isLoaded()) {
            
            rewardedAd = new RewardedAd(activity, p_adUnitId);
            
            rewardedAd.loadAd(
                buildAdRequest(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onRewardedAdLoaded() {
                        SBBUtils.log(instanceId, "PlayServiceAds", "onRewardedAdLoaded");
                        GodotLib.calldeferred(instanceId, "_on_rewarded_ad_loaded", new Object[] {});
                    }

                    @Override
                    public void onRewardedAdFailedToLoad(int p_errorCode) {
                        SBBUtils.log(instanceId, "PlayServiceAds", "onRewardedAdFailedToLoad, errorCode: " + p_errorCode);
                        GodotLib.calldeferred(instanceId, "_on_rewarded_ad_failed_to_loaded", new Object[] { p_errorCode });
                    }
                }
            );

        }
    
    }


    public void showRewardedAd() {
        activity.runOnUiThread(new Runnable() {    
            @Override public void run() {

                if (rewardedAd.isLoaded()) {

                    // prepare callback
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            SBBUtils.log(instanceId, "PlayServiceAds", "onRewardedAdOpened");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_ad_opened", new Object[] {});
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            SBBUtils.log(instanceId, "PlayServiceAds", "onRewardedAdClosed");
                            GodotLib.calldeferred(instanceId, "_on_rewarded_ad_closed", new Object[] {});
                        }

                        @Override
                        public void onUserEarnedReward(RewardItem p_rewardItem) {
                            SBBUtils.log(instanceId, "PlayServiceAds",
                                "onUserEarnedReward, currency: " + p_rewardItem.getType() + ", ammount: " + p_rewardItem.getAmount());
                            GodotLib.calldeferred(instanceId, "_on_user_earned_reward",
                                new Object[] { p_rewardItem.getType(), p_rewardItem.getAmount() });
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int p_errorCode) {
                            SBBUtils.log(instanceId, "PlayServiceAds", "onRewardedAdFailedToShow, errorCode: " + p_errorCode);
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
            "loadRewardedAd", "showRewardedAd",
        });
        
        activity = p_activity;
    }


    /* Activity States */
    protected void onMainPause() {
        SBBUtils.log(instanceId, "PlayServiceAds", "onMainPause");
    }

    protected void onMainResume() {
        SBBUtils.log(instanceId, "PlayServiceAds", "onMainResume");
    }

    protected void onMainDestroy() {
        SBBUtils.log(instanceId, "PlayServiceAds", "onMainDestroy");
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

        if (useNonPersAds) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        return adBuilder.build();
    }

}