#!/usr/bin/env sh
set -e

# Build for iOS device (arm64)
xcodebuild -target speex -sdk iphoneos -configuration Release ARCHS=arm64

# Build for iOS simulator (arm64 + x86_64)
xcodebuild -target speex -sdk iphonesimulator -configuration Release ARCHS="arm64 x86_64"

# Build for macOS
xcodebuild -target speex-macos -configuration Release

mkdir -p lib/ios-device
mkdir -p lib/ios-simulator
mkdir -p lib/macos

mv build/Release-iphoneos/libspeex.a lib/ios-device/libspeex.a
mv build/Release-iphonesimulator/libspeex.a lib/ios-simulator/libspeex.a
mv build/Release/libspeex-macos.a lib/macos/libspeex.a
