# Godot SBBAds

A module for **Godot Engine** that provides a selection of functionalities  
from **Google AdMob**, **Google Analytics** and **Consent Library (GDPR)**

This module is compatible with Godot 3.1.1 (AndroidX Mod) and Android v19+

- Godot Engine 🡆 <https://github.com/godotengine/godot>
- AndroidX Mod 🡆 <https://github.com/SimBotBiz/Godot-Engine-for-AndroidX>
- Google AdMob 🡆 <https://developers.google.com/admob/android/quick-start>
- Google AdMob (Firebase) 🡆 <https://firebase.google.com/docs/admob/android/quick-start>
- Google Analytics (Firebase) 🡆 <https://firebase.google.com/docs/analytics>
- Consent Library 🡆 <https://developers.google.com/admob/android/eu-consent>

🚧 Only Rewarded Ad format is implemented, other formats will come next (no ETA) 🚧

⚠ *This project is in alpha state, use at your own risk, you have been warned!* ⚠

If you need something more complete and stable please check this awesome module  
from Kloder Games -> <https://github.com/kloder-games/godot-admob>

## Key features over similar godot modules

- Support for Play Services Ads (AdMob) v18.3.0  
<https://developers.google.com/admob/android/rel-notes/#18.3.0>

- Integration with Consent Library v1.0.7  
<https://developers.google.com/admob/android/eu-consent>

- Targeting configuration via RequestConfiguration  
<https://developers.google.com/admob/android/targeting>

- Support for the newest Rewarded Ads API  
<https://developers.google.com/admob/android/rewarded-ads>

## How to build

To use this module you need to build your own custom template for Android.

1. Get and setup the Godot Engine AndroidX Mod  
<https://github.com/SimBotBiz/Godot-Engine-for-AndroidX>

2. Before going any further, I strongly advise to do a test build to be sure everything is working fine.

3. Clone or download this repo

4. Copy *sbb_ads* and *sbb_cmn* to the `modules\` folder of Godot Engine source

5. If you want to use the **Firebase Analytics** service copy also *sbb_firebase*  
    and add your own *google-services.json* to `platform\android\java\`

6. Check and edit configuration files:

    - `modules\sbb_ads\config.py`  
    You can enable or disable functionalities in the *artifacts* dictionary.  
    If you are using the Firebase module you should pick the `firebase-ads` over the `play-services-ads`

    - `modules\sbb_ads\play-services-ads\AndroidManifestChunk.xml`  
    You need to put there your AdMob App ID -> [(more infos)](https://developers.google.com/admob/android/quick-start#update_your_androidmanifestxml)

    - `modules\sbb_firebase\config.py`  
    Put your App ID (aka package_name) in -> `env.android_add_default_config("applicationId 'my.app.id'")`  
    this value must be the same of the one on your *google-services.json*

7. Rebuild the export templates

8. Use the export templates in your Godot App
    <https://docs.godotengine.org/en/3.1/development/compiling/compiling_for_android.html#using-the-export-templates>

9. Add the modules in the [android] section of your `project.godot` file

    ```py
    [android]
    modules="org/godotengine/godot/SBBPlayServicesAds,org/godotengine/godot/SBBConsent"
    ```

## How to use

A working Godot example is provided in the *example* folder of this repo,  
if you want to play with it remember that you need to:

1. Update the paths for the export templates and the debug keystore.

    You can do that easily editing the `export_presets.cfg` file:

    ```py
    [preset.0.options]
    custom_package/debug="C:/my/path/to/android_debug.apk"
    custom_package/release="C:/my/path/to/android_release.apk"
    keystore/debug="C:/my/path/to/debug.keystore"
    ```

2. Use your Publisher Id in `ConsentTest.gd`

    ```py
    var publisher_ids: Array = ["pub-0123456789012345"]
    ```

    <https://support.google.com/admob/answer/2784578>

## API Reference

### SBBPlayServicesAds

```py
# Play Services Ads Init
#
# @param int instanceId
# @param Dictionary options a dictionary of options, available values are:
#
#     - FORCE_TEST_DEVICE [bool] (true, false)
#         Flag the device as a test device (or not), this will overwrite the
#         default behavior (test device true if a debug build or runs on AVD).
#         https://developers.google.com/admob/android/test-ads#enable_test_devices
#
#     - USE_TEST_ADS [bool] (true, false)
#         Provided ad UID will be replaced by the appropriate test UID,
#         using these test ads gives a more consistent behavior,
#         ads are always show, that's not true with only the test device flag.
#         https://developers.google.com/admob/android/test-ads#sample_ad_units
#
#     - NON_PERSONALIZED_ADS [bool] (true, false),
#         This is what you need to set if using also the consent module!
#         https://developers.google.com/admob/android/eu-consent#forward_consent_to_the_google_mobile_ads_sdk
#
#     - TAG_FOR_CHILD_DIRECTED_TREATMENT [int] (1 = true, 0 = false, -1 = unspecified)
#         https://developers.google.com/admob/android/targeting#child-directed_setting
#
#     - TAG_FOR_UNDER_AGE_OF_CONSENT [int] (1 = true, 0 = false, -1 = unspecified)
#         https://developers.google.com/admob/android/targeting#users_under_the_age_of_consent
#
#     - MAX_AD_CONTENT_RATING [String] ('', 'G', 'PG', 'T', 'MA')
#         https://developers.google.com/admob/android/targeting#ad_content_filtering
#
init(instanceId: int, options: Dictionary)

# Play Services Ads Init Callbacks
_on_initialization_complete()
_on_rewarded_ad_loaded()
_on_rewarded_ad_failed_to_loaded(error_code: int)


# Load Rewarded Ad
#
# @param String adUnitId ie ca-app-pub-3940256099942544/5224354917
#
loadRewardedAd(adUnitId)

# Load Rewarded Ad Callbacks
_on_rewarded_ad_loaded()
_on_rewarded_ad_failed_to_loaded(error_code: int)

# Show Rewarded Ad
#
showRewardedAd()

# Show Rewarded Ad Callbacks
_on_rewarded_ad_opened()
_on_rewarded_ad_closed()
_on_user_earned_reward(currency: String, amount: int)
_on_rewarded_ad_failed_to_show(error_code: int)
```

### SBBConsent

```py
# Consent Library Init
#
# @param int instanceId
# @param Dictionary options a dictionary of options, available values are:
#
#     - FORCE_TEST_DEVICE [bool] (true, false)
#         Flag the device as a test device (or not), this will overwrite the
#         default behavior (test device true if a debug build or runs on AVD).
#         https://developers.google.com/admob/android/eu-consent#testing
#
#     - DEBUG_GEOGRAPHY [String] ('DEBUG_GEOGRAPHY_DISABLED', 'DEBUG_GEOGRAPHY_EEA', 'DEBUG_GEOGRAPHY_NOT_EEA')
#         https://developers.google.com/admob/android/eu-consent#testing
#
#     - TAG_FOR_UNDER_AGE_OF_CONSENT [bool] (true, false)
#         If a publisher is aware that the user is under the age of consent,
#         all ad requests must set TFUA (Tag For Users under the Age of Consent in Europe).
#         This setting takes effect for all future ad requests.
#         Once the TFUA setting is enabled, the Google-rendered consent form will fail to load.
#         https://developers.google.com/admob/android/eu-consent#users_under_the_age_of_consent
#
init(instanceId: int, options: Dictionary)


# Request Consent Info Update
#
# @param Array[String] publisherIds https://support.google.com/admob/answer/2784578
#
# If the consent information is successfully updated, the updated consent
# status and the request location are provided via the
# _on_consent_info_updated(String consentStatus, Boolean isRequestLocationInEeaOrUnknown) callback.
#
# The returned consentStatus may have the values listed below:
#     - PERSONALIZED      (The user has granted consent for personalized ads)
#     - NON_PERSONALIZED  (The user has granted consent for non-personalized ads)
#     - UNKNOWN           (The user has neither granted nor declined consent for personalized or non-personalized ads)
#
# If isRequestLocationInEeaOrUnknown is false, the user is not located in the European Economic Area and consent
# is not required under the EU User Consent Policy. You can make ad requests to the Google Mobile Ads SDK.
#
# If isRequestLocationInEeaOrUnknown is true:
#     - If the returned ConsentStatus is PERSONALIZED or NON_PERSONALIZED,
#       the user has already provided consent.
#       You can now forward consent to the Google Mobile Ads SDK.
#
#     - If the returned ConsentStatus is UNKNOWN, use collectConsent() to collect
#       consent from the user.
#
# https://developers.google.com/admob/android/eu-consent#update_consent_status
#
requestConsentInfoUpdate(publisherIds: Array)

# Request Consent Info Update Callbacks
_on_consent_info_updated(consent_status: String, isrlin_eea_or_unknown: bool)
_on_failed_to_update_consent_info(error_description: String)


# Collect Consent
#
# @param String privacyUrl
# @param Dictionary options a dictionary of options, available values are:
#
#      - PERSONALIZED_ADS [bool] (true, false)
#      - NON_PERSONALIZED_ADS [bool] (true, false)
#      - AD_FREE [bool] (true, false)
#      https://developers.google.com/admob/android/eu-consent#google_rendered_consent_form
#
collectConsent(privacyUrl: String, options: Dictionary)

# Collect Consent Callbacks
_on_consent_form_loaded()
_on_consent_form_opened()
_on_consent_form_closed(consent_status: String, user_prefer_adfree: bool)
_on_consent_form_error(error_description: String)
```

## Is my work useful for you?

[![PayPal Donate](https://img.shields.io/badge/PayPal-Donate-blue)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=3FBWGFBP8SUL4)
