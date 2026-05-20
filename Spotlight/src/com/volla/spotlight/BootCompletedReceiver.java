/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2020-2022 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.volla.spotlight;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.volla.spotlight.Services.NotificationService;
import com.volla.spotlight.Utils.ServiceUtils;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final boolean DEBUG = true;
    private static final String TAG = "ParanoidSpotlight";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (DEBUG) Log.d(TAG, "Received boot completed intent");
        // Enable the notification listener only after the system is far enough along
        // that PackageManager is ready. Declaring it disabled in the manifest avoids
        // NotificationManagerService binding it during early system_server startup,
        // which races StorageManagerService.getMountModeInternal and NPEs.
        PackageManager pm = context.getPackageManager();
        ComponentName cn = new ComponentName(context, NotificationService.class);
        if (pm.getComponentEnabledSetting(cn)
                != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            pm.setComponentEnabledSetting(cn,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
        ServiceUtils.checkSpotlightService(context);
    }
}
