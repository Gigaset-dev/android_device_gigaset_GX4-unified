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

package com.volla.spotlight.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

import com.volla.spotlight.Constants.Constants;
import com.volla.spotlight.Manager.AnimationManager;
import com.volla.spotlight.Manager.SettingsManager;
import com.volla.spotlight.Services.CallReceiverService;
import com.volla.spotlight.Services.ChargingService;
import com.volla.spotlight.Services.FlashlightService;
import com.volla.spotlight.Services.MusicService;
import com.volla.spotlight.Services.NotificationService;

public final class ServiceUtils {

    private static final String TAG = "SpotlightServiceUtils";
    private static final boolean DEBUG = true;

    public static void startCallReceiverService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting Spotlight call receiver service");
        context.startServiceAsUser(new Intent(context, CallReceiverService.class),
                UserHandle.CURRENT);
    }

    protected static void stopCallReceiverService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping Spotlight call receiver service");
        context.stopServiceAsUser(new Intent(context, CallReceiverService.class),
                UserHandle.CURRENT);
    }

    public static void startChargingService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting Spotlight charging service");
        context.startServiceAsUser(new Intent(context, ChargingService.class),
                UserHandle.CURRENT);
    }

    protected static void stopChargingService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping Spotlight charging service");
        context.stopServiceAsUser(new Intent(context, ChargingService.class),
                UserHandle.CURRENT);
    }

    public static void startNotificationService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting Spotlight notifs service");
        context.startServiceAsUser(new Intent(context, NotificationService.class),
                UserHandle.CURRENT);
    }

    protected static void stopNotificationService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping Spotlight notifs service");
        context.stopServiceAsUser(new Intent(context, NotificationService.class),
                UserHandle.CURRENT);
    }

    public static void startFlashlightService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting Spotlight flashlight service");
        context.startServiceAsUser(new Intent(context, FlashlightService.class),
                UserHandle.CURRENT);
    }

    protected static void stopFlashlightService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping Spotlight flashlight service");
        context.stopServiceAsUser(new Intent(context, FlashlightService.class),
                UserHandle.CURRENT);
    }

    public static void startMusicService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting Spotlight Music service");
        context.startServiceAsUser(new Intent(context, MusicService.class),
                UserHandle.CURRENT);
    }

    protected static void stopMusicService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping Spotlight Music service");
        context.stopServiceAsUser(new Intent(context, MusicService.class),
                UserHandle.CURRENT);
    }

    public static void checkSpotlightService(Context context) {
        AnimationManager animationManager = new AnimationManager(context);
        animationManager.enableHW(SettingsManager.isSpotlightEnabled(context));

        if (SettingsManager.isSpotlightEnabled(context)) {
            Constants.setBrightness(SettingsManager.getSpotlightBrightness(context));
            if (SettingsManager.isSpotlightChargingEnabled(context)) {
                startChargingService(context);
            } else {
                stopChargingService(context);
            }
            if (SettingsManager.isSpotlightCallEnabled(context)) {
                startCallReceiverService(context);
            } else {
                stopCallReceiverService(context);
            }
            if (SettingsManager.isSpotlightNotifsEnabled(context)) {
                startNotificationService(context);
            } else {
                stopNotificationService(context);
            }
            if (SettingsManager.isSpotlightFlashlightEnabled(context)) {
                startFlashlightService(context);
            } else {
                stopFlashlightService(context);
            }
            if (SettingsManager.isSpotlightMusicEnabled(context)) {
                startMusicService(context);
            } else {
                stopMusicService(context);
            }
        } else {
            stopChargingService(context);
            stopCallReceiverService(context);
            stopNotificationService(context);
            stopFlashlightService(context);
            stopMusicService(context);
        }
    }
}
