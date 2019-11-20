artifacts = {
    'firebase-analytics': True,
}


def can_build(env, platform):
    return platform == 'android'


def configure(env):
    
    if env['platform'] == 'android':

        env.android_add_gradle_classpath("com.google.gms:google-services:4.3.3")
        env.android_add_gradle_plugin("com.google.gms.google-services")
        env.android_add_default_config("applicationId 'my.app.id'")

        # firebase-analytics
        if artifacts['firebase-analytics']:
            env.android_add_dependency("implementation 'com.google.firebase:firebase-analytics:17.2.1'")
