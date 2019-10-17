artifacts = {
    "play-services-ads": True,
    "consent-library": True,
}

def can_build(env, platform):
	return platform == "android"

def configure(env):
	if (env['platform'] == "android"):
        pass