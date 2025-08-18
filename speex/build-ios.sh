#!/usr/bin/env sh
xcodebuild -alltargets
mkdir -p lib/ios
mkdir -p lib/macos
mv build/Release-iphoneos/libspeex.a lib/ios/libspeex.a
mv build/Release/libspeex-macos.a lib/macos/libspeex.a