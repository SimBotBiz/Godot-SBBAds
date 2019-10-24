# Godot SBBAds

## Key features over similar godot modules

- Support for play-services-ads v17  
[https://developers.google.com/admob/android/rel-notes/#17.2.1](https://developers.google.com/admob/android/rel-notes/#17.2.1)

- Integration with consent-library v1  
[https://developers.google.com/admob/android/eu-consent](https://developers.google.com/admob/android/eu-consent)

- Targeting configuration via RequestConfiguration  
[https://developers.google.com/admob/android/targeting](https://developers.google.com/admob/android/targeting)

- Support for the new rewarded API  
[https://developers.google.com/admob/android/rewarded-ads](https://developers.google.com/admob/android/rewarded-ads)

---
## How-to compile for Godot 3.1.1

> Enable Java 8 in
>
> `\platform\android\build.gradle.template`
>
> ```gradle
> android {
>   compileOptions {
>     sourceCompatibility JavaVersion.VERSION_1_8
>     targetCompatibility JavaVersion.VERSION_1_8
>   }
> }
> ```
