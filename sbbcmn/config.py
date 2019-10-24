
def can_build(env, platform):
    return platform == 'android'


def configure(env):
    if env['platform'] == 'android':
        env.android_add_java_dir("common/java")
