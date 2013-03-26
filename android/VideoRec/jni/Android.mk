LOCAL_PATH := $(call my-dir)

include $(all-subdir-makefiles)


#LOCAL_PATH := $(call my-dir)

#include $(CLEAR_VARS)
#LOCAL_MODULE    := OSNetworkSystem
#LOCAL_SRC_FILES := OSNetworkSystem.cpp
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog 
#include $(BUILD_SHARED_LIBRARY)


#include $(CLEAR_VARS)
#LOCAL_MODULE    := h263_jni
#LOCAL_SRC_FILES := h263_jni.cpp 
#LOCAL_SHARED_LIBRARIES :=  libffmpeg
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
#include $(BUILD_SHARED_LIBRARY)
