#!/usr/bin/env sh
set -e

# Build iOS device
xcodebuild -target speex -sdk iphoneos -configuration Release

# Build iOS simulator
xcodebuild -target speex -sdk iphonesimulator -configuration Release

# Build macOS
xcodebuild -target speex-macos -configuration Release

mkdir -p lib/ios
mkdir -p lib/ios-simulator
mkdir -p lib/macos

cp build/Release-iphoneos/libspeex.a lib/ios/libspeex.a
cp build/Release-iphonesimulator/libspeex.a lib/ios-simulator/libspeex.a
cp build/Release/libspeex-macos.a lib/macos/libspeex.a
