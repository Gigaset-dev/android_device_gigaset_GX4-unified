#
# Copyright (C) 2020-2022 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
ifneq ($(filter vidofnir GX4, $(TARGET_DEVICE)),)
include $(call all-makefiles-under,$(LOCAL_PATH))

define generate-symlinks
$(subst vendor/,,$(subst /$(2),,$(foreach file,$(shell cat $(1)),$(if $(findstring :,$(file)),,$(if $(findstring $(2),$(file)),$(file))))))
endef

GX4_SYMLINK := $(addprefix $(TARGET_OUT_VENDOR)/, $(call generate-symlinks,device/gigaset/GX4-unified/proprietary-files.txt,mt6789))
$(GX4_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@mkdir -p $(dir $@)
	$(hide) ln -sf mt6789/$(notdir $@) $@

VENDOR_SYMLINKS := \
    $(TARGET_OUT_VENDOR)/lib/hw \
    $(TARGET_OUT_VENDOR)/lib64/hw

$(VENDOR_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	$(hide) echo "Making vendor symlinks"
	@mkdir -p $(TARGET_OUT_VENDOR)/lib/hw
	@mkdir -p $(TARGET_OUT_VENDOR)/lib64/hw
	@ln -sf libSoftGatekeeper.so $(TARGET_OUT_VENDOR)/lib/hw/gatekeeper.default.so
	@ln -sf libSoftGatekeeper.so $(TARGET_OUT_VENDOR)/lib64/hw/gatekeeper.default.so
	$(hide) touch $@

ALL_DEFAULT_INSTALLED_MODULES += $(GX4_SYMLINK) $(VENDOR_SYMLINKS)
endif
