/*
 * Copyright (C) 2022 Paranoid Android
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

package com.volla.spotlight.Manager;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.volla.spotlight.Constants.Constants;

public final class SettingsManager {

    private static final String TAG = "SpotlightSettingsManager";
    private static final boolean DEBUG = true;

    public static boolean enableSpotlight(Context context, boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(),
                Constants.SPOTLIGHT_ENABLE, enable ? 1 : 0);
    }

    public static boolean isSpotlightEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                Constants.SPOTLIGHT_ENABLE, 1) != 0;
    }

    public static float getSpotlightBrightness(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Constants.SPOTLIGHT_BRIGHTNESS, 100) / 100.0f;
    }

    public static boolean isSpotlightChargingEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.SPOTLIGHT_CHARGING_LEVEL_ENABLE, true) && isSpotlightEnabled(context);
    }

    public static boolean isSpotlightCallEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.SPOTLIGHT_CALL_ENABLE, true) && isSpotlightEnabled(context);
    }

    public static boolean isSpotlightNotifsEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                Constants.SPOTLIGHT_NOTIFS_ENABLE, 1) != 0 && isSpotlightEnabled(context);
    }

    public static boolean setSpotlightNotifsEnabled(Context context, boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(),
                Constants.SPOTLIGHT_NOTIFS_ENABLE, enable ? 1 : 0);
    }

    public static boolean isSpotlightNotifsAppEnabled(Context context, String app) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(app, true) && isSpotlightNotifsEnabled(context);
    }

    public static boolean isSpotlightFlashlightEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.SPOTLIGHT_FLASHLIGHT_ENABLE, true) && isSpotlightEnabled(context);
    }

    public static boolean isSpotlightMusicEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.SPOTLIGHT_MUSIC_ENABLE, true) && isSpotlightEnabled(context);
    }
}
