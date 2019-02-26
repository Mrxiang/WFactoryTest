LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)


LOCAL_SRC_FILES := $(call all-java-files-under,src)

LOCAL_PROGUARD_ENABLED := disabled
LOCAL_MODULE_TAGS := optional
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_PACKAGE_NAME := WFactoryTest
LOCAL_CERTIFICATE := platform

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += vendor.mediatek.hardware.nvram-V1.0-java

include $(BUILD_PACKAGE)
#include $(BUILD_MULTI_PREBUILT)
# Use the following include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
