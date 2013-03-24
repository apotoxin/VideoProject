#!/bin/bash

PREBUILT=/home/ami/libs/android-ndk-r7c/toolchains/x86-4.4.3/prebuilt/linux-x86/
PLATFORM=/home/ami/libs/android-ndk-r7c/platforms/android-9/arch-x86

./configure --target-os=linux \
	--arch=x86 \
	--enable-version3 \
	--enable-gpl \
	--enable-nonfree \
	--disable-stripping \
	--disable-ffmpeg \
	--disable-ffplay \
	--disable-ffserver \
	--disable-ffprobe \
    --disable-shared \
    --enable-static \
    --disable-everything \
    --enable-decoder=rawvideo \
    --enable-decoder=h263 \
    --enable-parser=h263 \
    --enable-demuxer=h263 \
	--disable-encoders \
	--disable-muxers \
	--disable-devices \
	--disable-protocols \
	--enable-protocol=file \
	--enable-avfilter \
	--disable-network \
	--disable-mpegaudio-hp \
	--disable-avdevice \
	--enable-cross-compile \
	--cc=$PREBUILT/bin/i686-android-linux-gcc-4.4.3 \
	--cross-prefix=$PREBUILT/bin/i686-android-linux- \
	--nm=$PREBUILT/bin/i686-android-linux-nm \
	--extra-cflags="-fPIC -DANDROID" \
	--disable-asm \
	--enable-debug=DEBUG \
	--extra-ldflags="-Wl,-T,$PREBUILT/i686-android-linux/lib/ldscripts/elf_i386.x -Wl,-rpath-link=$PLATFORM/usr/lib -L$PLATFORM/usr/lib -nostdlib $PREBUILT/lib/gcc/i686-android-linux/4.4.3/crtbegin.o $PREBUILT/lib/gcc/i686-android-linux/4.4.3/crtend.o -lc -lm -ldl -llog"
