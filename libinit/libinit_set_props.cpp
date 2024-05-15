/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <android-base/properties.h>
#include <libinit_set_props.h>
#include <libinit_utils.h>
#include <string>
#include <sys/sysinfo.h>

#define GIGASET "Gigaset"
#define VOLLA "Volla"

void check_manufacturer_and_set_props() {
    struct sysinfo sys;
    sysinfo(&sys);
    bool is6GB = sys.totalram > GB(5);
    std::string manufacturer = android::base::GetProperty("ro.product.system.manufacturer", VOLLA);

    if (strcmp(manufacturer.c_str(), GIGASET) == 0) {
        if (is6GB) {
            std::string fingerprint = "Gigaset/GX4_PRO_EEA/GX4_PRO:12/SP1A.210812.016/1706147281:user/release-keys";
            set_ro_build_prop("device", "GX4_PRO", true);
            set_ro_build_prop("model", "E940-2797-01", true);
            set_ro_build_prop("fingerprint", fingerprint);
            property_override("ro.bootimage.build.fingerprint", fingerprint);
            property_override("ro.build.description", "GX4_PRO-user 12 SP1A.210812.016 1706147281 release-keys");
        }
    } else if (strcmp(manufacturer.c_str(), VOLLA) == 0) {
        if (!is6GB)
            set_ro_build_prop("model", "Volla Phone X23E", true);
    }
}
