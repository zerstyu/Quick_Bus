LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := DaumMapEngineApi
LOCAL_SRC_FILES := E:\Users\swssm\workspace\FindWay\jni\libDaumMapEngineApi.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
                     
LOCAL_MODULE    := mixed_sample
LOCAL_SRC_FILES := jni_part.cpp


LOCAL_SHARED_LIBRARIES := lib/armeabi/libDaumMapEngineApi.so


LOCAL_LDLIBS +=  -lc -llog -ldl

LOCAL_C_INCLUDES += $(JNI_H_INCLUDE)

include $(BUILD_SHARED_LIBRARY)

include $( call import-module, libtestutils ) 


