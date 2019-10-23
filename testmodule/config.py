
def can_build(env, platform):
    return platform == 'android'


def configure(env):

    if env['platform'] == 'android':
        env.android_add_res_dir("test/res")
        # env.android_add_java_dir("test/java")
        # env.android_add_to_manifest("test/AndroidManifestChunk.xml")
        # env.android_add_to_permissions("test/AndroidPermissionsChunk.xml")
