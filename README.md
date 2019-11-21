# Godot SBBAds

A module for **Godot Engine** that provides a selection of functionalities from **Google AdMob**, **Google Analytics** and **Consent Library (GDPR)**

This module is compatible with Godot 3.1.1 (AndroidX Mod) and Android v19+

- Godot Engine 🡆 <https://github.com/godotengine/godot>
- AndroidX Mod 🡆 <https://github.com/SimBotBiz/Godot-Engine-for-AndroidX>
- Google AdMob 🡆 <https://developers.google.com/admob/android/quick-start>
- Google AdMob (Firebase) 🡆 <https://firebase.google.com/docs/admob/android/quick-start>
- Google Analytics (Firebase) 🡆 <https://firebase.google.com/docs/analytics>
- Consent Library 🡆 <https://developers.google.com/admob/android/eu-consent>

🚧 Only Rewarded Ad format is implemented, other formats will come next (no ETA) 🚧

⚠ *This project is in alpha state, use at your own risk, you have been warned!* ⚠

If you need something more complete and stable please check this awesome module from Kloder Games -> <https://github.com/kloder-games/godot-admob>

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

A working Godot example is provided in the *example* folder of this repo, I will write a more detailed documentations soon.

In the meantime if you want to play with the example remember that you need to:

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

## Is my work useful for you?

[![PayPal Donate](https://img.shields.io/badge/PayPal-Donate-blue)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=3FBWGFBP8SUL4)
