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

package com.volla.spotlight.Constants;

public final class Constants {

    private static final String TAG = "SpotlightConstants";
    private static final boolean DEBUG = true;

    public static float BRIGHTNESS = 1;

    public static final String SPOTLIGHT_ENABLE = "spotlight_enable";
    public static final String SPOTLIGHT_BRIGHTNESS = "spotlight_settings_brightness";
    public static final String SPOTLIGHT_CHARGING_LEVEL_ENABLE = "spotlight_settings_charging_level";
    public static final String SPOTLIGHT_FLASHLIGHT_ENABLE = "spotlight_settings_flashlight_toggle";
    public static final String SPOTLIGHT_MUSIC_ENABLE = "spotlight_settings_music_toggle";
    public static final String SPOTLIGHT_CALL_ENABLE = "spotlight_settings_call_toggle";
    public static final String SPOTLIGHT_NOTIFS_ENABLE = "spotlight_settings_notifs_toggle";
    public static final String SPOTLIGHT_NOTIFS_SUB_CATEGORY = "spotlight_settings_notifs_sub";
    public static final String SPOTLIGHT_NOTIFS_SUB_ENABLE = "spotlight_settings_notifs_sub_toggle";

    public enum SpotlightMode {
        FLASHLIGHT,
        CALLS,
        MUSIC,
        NOTIFICATIONS,
        CHARGING,
    }

    public static final String[] APPSTOIGNORE = {
                                                        "android",
                                                        "com.android.traceur",
                                                        //"com.google.android.dialer",
                                                        "com.google.android.setupwizard",
                                                        "dev.kdrag0n.dyntheme.privileged.sys"
                                                    };
    public static final String[] NOTIFSTOIGNORE = {
                                                        "com.google.android.dialer:phone_incoming_call",
                                                        "com.google.android.dialer:phone_ongoing_call"
                                                    };

    public static void setBrightness(float b) {
        BRIGHTNESS = b;
    }

}
