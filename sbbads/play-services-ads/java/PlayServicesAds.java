package org.godotengine.godot;

import com.godot.game.R;
import com.godot.game.BuildConfig;

import org.godotengine.godot.Godot;
import org.godotengine.godot.utils.Crypt;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class PlayServicesAds extends Godot.SingletonBase {

    protected Activity activity;
    private int instanceId;
    private RewardedAd rewardedAd;
    private boolean isTestDevice = false;

    /* PlayServiceAds Init */
    public void init(int p_instance_id) {
        instanceId = p_instance_id;

        if(BuildConfig.DEBUG) {
            isTestDevice = true;
        }

        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("SBBAds", "PlayServiceAds: onInitializationComplete");
                GodotLib.calldeferred(instanceId, "_on_initialization_complete", new Object[] {});
            }
        });
    }

    /* Rewarded Ads */
    public void loadRewardedAd(String p_ad_unit_id) {
        
        if(rewardedAd == null || !rewardedAd.isLoaded()) {
            
            rewardedAd = new RewardedAd(activity, p_ad_unit_id);
            
            rewardedAd.loadAd(
                buildAdRequest(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onRewardedAdLoaded() {
                        Log.d("SBBAds", "PlayServiceAds: onRewardedAdLoaded");
                        GodotLib.calldeferred(instanceId, "_on_rewarded_ad_loaded", new Object[] {});
                    }

                    @Override
                    public void onRewardedAdFailedToLoad(int errorCode) {
                        Log.d("SBBAds", "PlayServiceAds: onRewardedAdFailedToLoad, errorCode: " + errorCode);
                        GodotLib.calldeferred(instanceId, "_on_rewarded_ad_failed_to_loaded", new Object[] { errorCode });
                    }
                }
            );

        }

    }

    /* Godot Singletone Module Init */
    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new PlayServicesAds(p_activity);
    }

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
    
    private AdRequest buildAdRequest() {
        AddRequest.Builder adBuilder = new AdRequest.Builder();

        if(isTestDevice) {
            adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            adBuilder.addTestDevice(getDeviceId());
        }
    }

    private String getDeviceId() {
        String androidId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = Crypt.md5(androidId).toUpperCase();
		return deviceId;
    }

}