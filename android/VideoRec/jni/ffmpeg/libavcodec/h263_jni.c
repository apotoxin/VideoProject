/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <memory.h>
#include <ctype.h>
#include <jni.h>
#include <android/log.h> 

#include "avcodec.h"

/* Define codec specific settings */
#define G722_SAMPLE_RATE    16000
#define BLOCK_LEN           320

#define LOG_TAG "h263" // text for log tag
#undef DEBUG_G722

// the header length of the RTP frame (must skip when en/decoding)
#define	RTP_HDR_SIZE	12

AVCodec * pEncoderCodec;
AVCodec * pDecoderCodec;

AVCodecContext * context1;
AVCodecContext * context2;

AVFrame * frameEn;
AVFrame * frameDe;

static JavaVM *gJavaVM;

//bit_rate=64000;			// 48000, 56000 or 64000
JNIEXPORT jint JNICALL Java_com_mz_videorec_codecs_H263_open(JNIEnv *env,
		jobject obj, jint bitrate) {
	int ret;
	avcodec_init();

	avcodec_register_all();

	pEncoderCodec = avcodec_find_encoder(CODEC_ID_H263);
	pDecoderCodec = avcodec_find_decoder(CODEC_ID_H263);

	context1 = avcodec_alloc_context();
	context2 = avcodec_alloc_context();

	frameEn = avcodec_alloc_frame();

	context1->codec_id = CODEC_ID_H263;
	/* put sample parameters */
	context1->bit_rate = 200000;
	/* resolution must be a multiple of two */
	context1->width = 176;
	context1->height = 144;

	context1->time_base = (AVRational) {1,15};
			context1->time_base.num = 1;
			context1->time_base.den = 15;
			context1->gop_size = 10; /* emit one intra frame every ten frames */
			context1->pix_fmt = PIX_FMT_YUV420P;
			context1->codec_type = CODEC_TYPE_VIDEO;

			/* open it */
			if (avcodec_open(context1, pEncoderCodec) < 0)
			{
				return (jint)(-1);
			}

			context2->codec_id=CODEC_ID_H263;
			/* put sample parameters */
			context2->bit_rate = 200000;
			/* resolution must be a multiple of two */
			context2->width = 176;
			context2->height = 144;

			context2->time_base= (AVRational) {1,15};
			context2->time_base.num = 1;
			context2->time_base.den = 15;
			context2->gop_size = 10; /* emit one intra frame every ten frames */
			context2->pix_fmt = PIX_FMT_YUV420P;
			context2->codec_type = CODEC_TYPE_VIDEO;

			if (avcodec_open(context2, pDecoderCodec) < 0)
			{
				return (jint)(-1);
			}

			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
					"Java_H263333333333333333333333 : %d ,, %d  , %d , %d  " , &pEncoderCodec , &pDecoderCodec , &context1 , &context2);





	return (jint)0;
}

JNIEXPORT jint JNICALL Java_com_mz_videorec_codecs_H263_encode(JNIEnv *env,
		jobject obj, jshortArray lin, jint offset, jbyteArray encoded,
		jint size) {

	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
			"encode ........ size : %d", size);

	/*
	 jbyte adpcmdata[BLOCK_LEN];
	 jshort indata[BLOCK_LEN];

	 //    jbyte	  enc_payload[ MAX_BYTES_DEC_PER_FRAME * MAX_INPUT_FRAMES ];
	 //   jshort    in[ MAX_FRAME_LENGTH * MAX_INPUT_FRAMES ];
	 int ret,i,frsz=BLOCK_LEN;

	 unsigned int lin_pos = 0;

	 if (!codec_open)
	 return 0;

	 #ifdef DEBUG_G722
	 __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
	 "encoding frame size: %d\toffset: %d\n", size, offset);
	 #endif


	 for (i = 0; i < size; i+=BLOCK_LEN) {
	 #ifdef DEBUG_G722
	 __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
	 "encoding frame size: %d\toffset: %d i: %d\n", size, offset, i);
	 #endif

	 env->GetShortArrayRegion(lin, offset + i,frsz, indata);

	 ret=g722_encode(&enc_state,(uint8_t *) adpcmdata, indata, frsz);

	 #ifdef DEBUG_G722
	 __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
	 "Enocded Bytes: %d\n", ret);
	 #endif

	 env->SetByteArrayRegion(encoded, RTP_HDR_SIZE+ lin_pos, ret, adpcmdata);
	 lin_pos += ret;
	 }
	 #ifdef DEBUG_G722
	 __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
	 "encoding **END** frame size: %d\toffset: %d i: %d lin_pos: %d\n", size, offset, i, lin_pos);
	 #endif

	 return (jint)lin_pos;
	 */
	return (jint) 0;
}

JNIEXPORT jint JNICALL Java_com_mz_videorec_codecs_H263_decode(JNIEnv *env,
		jobject obj, jbyteArray encoded, jshortArray lin, jint size) {

	/*
	 //   jbyte buffer [MAX_BYTES_DEC_PER_FRAME * MAX_INPUT_FRAMES * ( MAX_LBRR_DELAY + 1 ) ];
	 //   jshort output_buffer[( MAX_FRAME_LENGTH << 1 ) * MAX_INPUT_FRAMES ];

	 jbyte adpcmdata[BLOCK_LEN];
	 jshort outdata[BLOCK_LEN];

	 int len,ret;

	 if (!codec_open)
	 return 0;

	 #ifdef DEBUG_G722
	 __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
	 "##### BEGIN DECODE ********  decoding frame size: %d\n", size);
	 #endif

	 env->GetByteArrayRegion(encoded, RTP_HDR_SIZE, size, adpcmdata);
	 len = ff_h263_decode_frame(&av_context, outdata, (uint8_t *) adpcmdata, size);

	 #ifdef DEBUG_G722
	 __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
	 "##### DECODED length: %d\n", len);
	 #endif

	 env->SetShortArrayRegion(lin, 0, len,outdata);
	 return (jint)len;
	 */
	return (jint) 0;
}

JNIEXPORT void JNICALL Java_org_sipdroid_codecs_h263_close
(JNIEnv *env, jobject obj) {

}

