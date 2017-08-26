LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files)
#LOCAL_DEX_PREOPT := false
LOCAL_PACKAGE_NAME := SmartGesture
LOCAL_PROPRIETARY_MODULE := true
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
