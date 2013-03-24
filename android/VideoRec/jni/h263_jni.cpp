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

#include "ffmpeg/libavcodec/avcodec.h"

/* Define codec specific settings */
#define G722_SAMPLE_RATE    16000
#define BLOCK_LEN           320

#define LOG_TAG "h263" // text for log tag

#undef DEBUG_G722

// the header length of the RTP frame (must skip when en/decoding)
#define	RTP_HDR_SIZE	12


static JavaVM *gJavaVM;
const char *kInterfacePath = "org/sipdroid/pjlib/g722";

extern "C"{
#ifdef __cplusplus
 #define __STDC_CONSTANT_MACROS
 #ifdef _STDINT_H
  #undef _STDINT_H
 #endif
 # include <stdint.h>
#endif
}


//bit_rate=64000;			// 48000, 56000 or 64000
extern "C"
JNIEXPORT jint JNICALL Java_com_mz_videorec_codecs_H263_open
  (JNIEnv *env, jobject obj, jint bitrate) {
	int ret;
	avcodec_init();
	avcodec_register_all();
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
	            "Java_H263333333333333333333333");

	return (jint)0;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_mz_videorec_codecs_H263_encode
    (JNIEnv *env, jobject obj, jshortArray lin, jint offset, jbyteArray encoded, jint size) {
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
    return (jint) 0 ;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_mz_videorec_codecs_H263_decode
    (JNIEnv *env, jobject obj, jbyteArray encoded, jshortArray lin, jint size) {
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
	return (jint) 0 ;
}


extern "C"
JNIEXPORT void JNICALL Java_org_sipdroid_codecs_h263_close
    (JNIEnv *env, jobject obj) {

	}


