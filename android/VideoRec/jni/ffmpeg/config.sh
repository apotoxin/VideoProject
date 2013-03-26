#!/bin/bash
HOME=/home/b395
PREBUILT=$HOME/libs/android-ndk-r8d/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86
PLATFORM=$HOME/libs/android-ndk-r8d/platforms/android-9/arch-arm

./configure --target-os=linux \
	--arch=arm \
	--enable-version3 \
	--enable-gpl \
	--enable-nonfree \
	--disable-stripping \
	--disable-ffmpeg \
	--disable-ffplay \
	--disable-ffserver \
	--disable-ffprobe \
	--disable-muxers \
	--disable-devices \
	--disable-protocols \
	--enable-protocol=file \
	--enable-decoder=rawvideo \
	--enable-encoder=rawvideo \
	--enable-decoder=h263 \
	--enable-encoder=h263 \
	--enable-parser=h263 \
	--enable-demuxer=h263 \
	--enable-decoder=h264 \
	--enable-encoder=h264 \
	--enable-parser=h264 \
	--enable-demuxer=h264 \
	--enable-avfilter \
	--disable-network \
	--disable-mpegaudio-hp \
	--disable-avdevice \
	--enable-cross-compile \
	--cc=$PREBUILT/bin/arm-linux-androideabi-gcc \
	--cross-prefix=$PREBUILT/bin/arm-linux-androideabi- \
	--nm=$PREBUILT/bin/arm-linux-androideabi-nm \
	--extra-cflags="-fPIC -DANDROID" \
	--disable-asm \
	--enable-neon \
	--extra-ldflags="-Wl,-T,$PREBUILT/arm-linux-androideabi/lib/ldscripts/armelfb_linux_eabi.x -Wl,-rpath-link=$PLATFORM/usr/lib -L$PLATFORM/usr/lib -nostdlib $PREBUILT/lib/gcc/arm-linux-androideabi/4.6/crtbegin.o $PREBUILT/lib/gcc/arm-linux-androideabi/4.6/crtend.o -lc -lm -ldl"
