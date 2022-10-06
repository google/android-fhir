LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_CFLAGS += -Wno-unused-parameter -Wno-int-to-pointer-cast
LOCAL_CFLAGS += -Wno-uninitialized -Wno-parentheses
LOCAL_CPPFLAGS += -Wno-conversion-null


ifeq ($(TARGET_ARCH), arm)
	LOCAL_CFLAGS += -DPACKED="__attribute__ ((packed))"
else
	LOCAL_CFLAGS += -DPACKED=""
endif

LOCAL_SRC_FILES:= \
	spellfix.c

LOCAL_C_INCLUDES += $(LOCAL_PATH)

LOCAL_MODULE:= libspellfix3
LOCAL_LDLIBS += -ldl -llog -latomic

include $(BUILD_SHARED_LIBRARY)

