#!/bin/sh
set -eu
# Set paths to your Android SDK / JDK. Signing key remains on the developer's machine.
: "${ANDROID_JAR:?Set path to Android android.jar}"
: "${BUILD_TOOLS:?Set path to Android build tools}"
: "${SIGNING_KEY:?Set local keystore path}"
: "${SIGNING_PASSWORD:?Set keystore password}"
mkdir -p classes dex
javac -source 8 -target 8 -cp "$ANDROID_JAR" -d classes src/com/gamesplanet/tv/*.java
"$BUILD_TOOLS/d8" --lib "$ANDROID_JAR" --min-api 23 --output dex classes/com/gamesplanet/tv/*.class
"$BUILD_TOOLS/aapt" package -f -0 mp4 -M AndroidManifest.xml -S res -A assets -I "$ANDROID_JAR" -F resources.apk
python3 - <<'PY'
import zipfile
with zipfile.ZipFile('resources.apk') as src,zipfile.ZipFile('unsigned.apk','w') as dst:
 for i in src.infolist():dst.writestr(i,src.read(i.filename))
 dst.write('dex/classes.dex','classes.dex')
PY
"$BUILD_TOOLS/zipalign" -f 4 unsigned.apk aligned.apk
"$BUILD_TOOLS/apksigner" sign --ks "$SIGNING_KEY" --ks-pass "pass:$SIGNING_PASSWORD" --out Games_Planet_TV.apk aligned.apk
"$BUILD_TOOLS/apksigner" verify --verbose Games_Planet_TV.apk
