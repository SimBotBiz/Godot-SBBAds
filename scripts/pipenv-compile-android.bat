pipenv run scons -j4 platform=android target=release android_arch=armv7
pipenv run scons -j4 platform=android target=release android_arch=arm64v8
pipenv run scons -j4 platform=android target=release android_arch=x86
pipenv run scons -j4 platform=android target=release android_arch=x86_64

pipenv run scons -j4 platform=android target=release_debug android_arch=armv7
pipenv run scons -j4 platform=android target=release_debug android_arch=arm64v8
pipenv run scons -j4 platform=android target=release_debug android_arch=x86
pipenv run scons -j4 platform=android target=release_debug android_arch=x86_64

cd platform\android\java

.\gradlew build