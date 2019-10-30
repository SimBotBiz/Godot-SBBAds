artifacts = {
    'play-services-ads': True,
    'consent-library': False,
}


def can_build(env, platform):
    return platform == 'android'


def configure(env):

    if env['platform'] == 'android':

        # play-service-ads
        if artifacts['play-services-ads']:
            env.android_add_dependency(
                "implementation 'com.google.android.gms:play-services-ads:18.2.0'"
            )
            env.android_add_java_dir("play-services-ads/java")
            env.android_add_to_manifest("play-services-ads/AndroidManifestChunk.xml")
            env.android_add_to_permissions("play-services-ads/AndroidPermissionsChunk.xml")

        # consent-library
        if artifacts['consent-library']:
            env.android_add_dependency(
                "implementation 'com.google.android.ads.consent:consent-library:1.0.7'"
            )
            env.android_add_java_dir("consent-library")
