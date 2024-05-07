#include <android-base/properties.h>
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>
#include <libinit_set_props.h>
#include <sys/sysinfo.h>
#include <string>
#define GB(b) (b * 1024ull * 1024 * 1024)
#define GIGASET "Gigaset"
#define VOLLA "Volla"
#define VIDOFNIR "Volla Phone X23E"
#define VIDOFNIR_ESIM "Volla Phone X23"
#define GX4 "Gigaset GX4"
#define GX4_PRO "Gigaset GX4 PRO"

using android::base::GetProperty;

static const std::string ro_props_sources[] = {
    "",
    "odm.",
    "product.",
    "system.",
    "system_ext.",
    "vendor.",
    "vendor_dlkm."
};

void property_override(const std::string &name, const std::string &value, bool add = true)
{
    auto pi = const_cast<prop_info *>(__system_property_find(name.c_str()));

    if (pi != nullptr)
    {
        __system_property_update(pi, value.c_str(), value.size());
    }
    else if (add)
    {
        __system_property_add(name.c_str(), name.size(), value.c_str(), value.size());
    }
}

void set_modelname(const std::string &model)
{
    for (const std::string &source : ro_props_sources)
    {
        std::string prop = "ro.product." + source + "model";
        property_override(prop, model);
    }
}

void set_volla_props()
{
    struct sysinfo sys;
    sysinfo(&sys);
    if (sys.totalram > GB(5))
    {
        set_modelname(VIDOFNIR_ESIM);
    }
    else
    {
        set_modelname(VIDOFNIR);
    }
}
void set_gigaset_props()
{
    struct sysinfo sys;
    sysinfo(&sys);
    if (sys.totalram > GB(5))
    {
        set_modelname(GX4_PRO);
    }
    else
    {
        set_modelname(GX4);
    }
}
void check_manufacturer_and_set_props() {
    std::string manufacturer = GetProperty("ro.product.manufacturer", "");
    if (strcmp(manufacturer.c_str(), GIGASET) == 0) {
        set_gigaset_props();
    } else if (strcmp(manufacturer.c_str(), VOLLA) == 0) {
        set_volla_props();
    }
}
