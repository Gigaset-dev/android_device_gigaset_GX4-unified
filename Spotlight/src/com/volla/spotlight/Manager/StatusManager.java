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

import android.util.Log;

public final class StatusManager {

    private static final String TAG = "SpotlightStatusManager";
    private static final boolean DEBUG = true;

    private static boolean allLedActive = false;
    private static boolean callLedActive = false;
    private static boolean musicLedActive = false;
    private static boolean notifLedActive = false;
    private static boolean chargingLedActive = false;

    public static boolean isAllLedsActive() {
        return allLedActive;
    }

    public static void setAllLedsActive(boolean status) {
        allLedActive = status;
    }

    public static boolean isCallLedsActive() {
        return callLedActive;
    }

    public static void setCallLedsActive(boolean status) {
        callLedActive = status;
    }

    public static boolean isMusicLedsActive() {
        return musicLedActive;
    }

    public static void setMusicLedsActive(boolean status) {
        musicLedActive = status;
    }

    public static boolean isNotifLedsActive() {
        return notifLedActive;
    }

    public static void setNotifLedsActive(boolean status) {
        notifLedActive = status;
    }

    public static boolean isChargingLedsActive() {
        return chargingLedActive;
    }

    public static void setChargingLedsActive(boolean status) {
        chargingLedActive = status;
    }
}
