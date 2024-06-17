#!/bin/bash
#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017-2023 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

set -e

DEVICE=GX4
VENDOR=gigaset

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "${MY_DIR}" ]]; then MY_DIR="${PWD}"; fi

ANDROID_ROOT="${MY_DIR}/../../.."

HELPER="${ANDROID_ROOT}/tools/extract-utils/extract_utils.sh"
if [ ! -f "${HELPER}" ]; then
    echo "Unable to find helper script at ${HELPER}"
    exit 1
fi
source "${HELPER}"

# Initialize the helper
setup_vendor "${DEVICE}" "${VENDOR}" "${ANDROID_ROOT}"

# Warning headers and guards
write_headers

write_makefiles "${MY_DIR}/proprietary-files.txt" true

append_firmware_calls_to_makefiles "${MY_DIR}/proprietary-firmware.txt"

sed -i "/TARGET_DEVICE/d" "$ANDROIDMK"
sed -i "/add-radio-file/d" "$ANDROIDMK"

cat << EOF >> "$ANDROIDMK"
ifneq (\$(filter GX4 vidofnir,\$(TARGET_DEVICE)),)

RADIO_FILES := \$(wildcard \$(LOCAL_PATH)/radio/\$(TARGET_DEVICE)/*)
\$(foreach f, \$(notdir \$(RADIO_FILES)), \\
    \$(call add-radio-file,radio/\$(TARGET_DEVICE)/\$(f)))

EOF

# Finish
write_footers
