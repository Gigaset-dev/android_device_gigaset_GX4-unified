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
import android.media.audiofx.Visualizer;
import android.os.BatteryManager;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.volla.spotlight.R;
import com.volla.spotlight.Constants.Constants;
import com.volla.spotlight.Manager.StatusManager;
import com.volla.spotlight.Utils.FileUtils;

public final class AnimationManager {

    private static final String TAG = "SpotlightAnimationManager";
    private static final boolean DEBUG = true;

    private Context mContext;
    private LEDManager mLEDManager;
    private BatteryManager mBatteryManager;
    private Visualizer mVisualizer;


    public AnimationManager(Context context) {
        mContext = context;

        mLEDManager = new LEDManager();
    }

    private static Future<?> submit(Runnable runnable) {
        ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
        return mExecutorService.submit(runnable);
    }

    private static boolean check(Constants.SpotlightMode mode) {
        switch (mode) {
            case FLASHLIGHT:
                if (!StatusManager.isAllLedsActive())
                    return false;
                break;
            case CALLS:
                if (StatusManager.isAllLedsActive()) {
                    if (DEBUG) Log.d(TAG, "All LEDs are active, can\'t start animation | name: " + mode.name());
                    return false;
                }
                if (!StatusManager.isCallLedsActive())
                    return false;
                break;
            case MUSIC:
                if (StatusManager.isAllLedsActive() || StatusManager.isCallLedsActive()) {
                    if (DEBUG) Log.d(TAG, "Call or All LEDs are active, can\'t start animation | name: " + mode.name());
                    return false;
                }
                if (!StatusManager.isMusicLedsActive())
                    return false;
                break;
            case NOTIFICATIONS:
                if (StatusManager.isAllLedsActive() || StatusManager.isCallLedsActive() || StatusManager.isMusicLedsActive()) {
                    if (DEBUG) Log.d(TAG, "Call, Music or All LEDs are active, can\'t start animation | name: " + mode.name());
                    return false;
                }
                if (!StatusManager.isNotifLedsActive())
                    return false;
                break;
            case CHARGING:
                if (StatusManager.isAllLedsActive() || StatusManager.isCallLedsActive() || StatusManager.isMusicLedsActive() || StatusManager.isNotifLedsActive()) {
                    if (DEBUG) Log.d(TAG, "Call, Music, Notification or All LEDs are active, can\'t start animation | name: " + mode.name());
                    return false;
                }
                if (!StatusManager.isChargingLedsActive())
                    return false;
                break;
        }
        return true;
    }

    private void cleanupAndContinue() {
        mLEDManager.enableAllLEDs(false);

        if (StatusManager.isAllLedsActive()) {
            playFlashlight();
        } else if (StatusManager.isCallLedsActive()) {
            playCall();
        } else if (StatusManager.isMusicLedsActive()) {
            playMusic();
        } else if (StatusManager.isNotifLedsActive()) {
            playNotifications();
        } else if (StatusManager.isChargingLedsActive()) {
            playCharging();
        }
    }

    private int getBatteryLevel() {
        mBatteryManager = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);

        return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    public void enableHW(boolean enable) {
        submit(() -> {
            mLEDManager.enableHW(enable);
        });
    }

    public void playCharging() {
        StatusManager.setChargingLedsActive(false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e(TAG, "Error while playing charging animation", e);
        }

        StatusManager.setChargingLedsActive(true);
        submit(() -> {
            int solid_leds = 0;
            mLEDManager.enableAllLEDs(false);
            try {
                int oldBatteryLevel = -1;
                while (check(Constants.SpotlightMode.CHARGING)) {
                    int batteryLevel = getBatteryLevel();
                    if (oldBatteryLevel != batteryLevel) {
                        solid_leds = Integer.valueOf(batteryLevel / mLEDManager.getNumLEDs());
                        for (int i = 0; i < solid_leds; i++) {
                            mLEDManager.setLEDBrightness(i, Constants.BRIGHTNESS);
                            Thread.sleep(150);
                        }
                        oldBatteryLevel = batteryLevel;
                    }
                    if (100 - solid_leds * mLEDManager.getNumLEDs() > 0) {
                        mLEDManager.setLEDBrightness(solid_leds, Constants.BRIGHTNESS);
                        Thread.sleep(500);
                        mLEDManager.setLEDBrightness(solid_leds, 0);
                        Thread.sleep(500);
                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Error while playing charging animation", e);
            }
            mLEDManager.enableAllLEDs(false);
        });
    }

    public void stopCharging() {
        if (DEBUG) Log.d(TAG, "Done playing animation | name: charging");
        StatusManager.setChargingLedsActive(false);
        cleanupAndContinue();
    }

    public void playCall() {
        StatusManager.setCallLedsActive(false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e(TAG, "Error while playing charging animation", e);
        }

        StatusManager.setCallLedsActive(true);
        submit(() -> {
            mLEDManager.enableAllLEDs(false);
            try {
                boolean enableOdds = false;
                while (check(Constants.SpotlightMode.CALLS)) {
                    for (int i = 0; i < mLEDManager.getNumLEDs(); i++) {
                        if (i % 2 == (enableOdds ? 1 : 0)) {
                            mLEDManager.setLEDBrightness(i, Constants.BRIGHTNESS);
                        } else {
                            mLEDManager.setLEDBrightness(i, 0);
                        }
                    }
                    enableOdds = !enableOdds;
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Error while playing charging animation", e);
            }
            mLEDManager.enableAllLEDs(false);
        });
    }

    public void stopCall() {
        if (DEBUG) Log.d(TAG, "Disabling Call Animation");
        StatusManager.setCallLedsActive(false);
        cleanupAndContinue();
    }

    public void playNotifications() {
        StatusManager.setNotifLedsActive(true);

        submit(() -> {
            if (check(Constants.SpotlightMode.NOTIFICATIONS))
                mLEDManager.setLEDsBlink(true);
        });
    }

    public void stopNotifications() {
        if (DEBUG) Log.d(TAG, "Disabling Notifications Animation");
        StatusManager.setNotifLedsActive(false);
        cleanupAndContinue();
    }

    public void playFlashlight() {
        StatusManager.setAllLedsActive(true);

        submit(() -> {
            if (check(Constants.SpotlightMode.FLASHLIGHT)) {
                mLEDManager.enableAllLEDs(true);
                for (int i = 0; i < mLEDManager.getNumLEDs(); i++) {
                    mLEDManager.setLEDBrightness(i, Constants.BRIGHTNESS);
                }
            }
        });
    }

    public void stopFlashlight() {
        if (DEBUG) Log.d(TAG, "Disabling Flashlight");
        StatusManager.setAllLedsActive(false);
        cleanupAndContinue();
    }

    private Visualizer.OnDataCaptureListener mVisualizerListener =
            new Visualizer.OnDataCaptureListener() {
                float rfk, ifk;
                float dbValue;
                float magnitude;
                boolean isAnimating = false;

                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    if (isAnimating) return;
                    if (!check(Constants.SpotlightMode.MUSIC))
                        return;
                    submit(() -> {
                        isAnimating = true;
                        for (int i = 0; i < mLEDManager.getNumLEDs(); i++) {
                            rfk = fft[i * 2 + 2]/128.0f;
                            ifk = fft[i * 2 + 3]/128.0f;
                            magnitude = rfk * rfk + ifk * ifk;
                            dbValue = magnitude > 1 ? 1 : magnitude;

                            mLEDManager.setLEDBrightness(i, dbValue);
                        }
                        isAnimating = false;
                    });
                }
            };

    public void playMusic() {
        StatusManager.setMusicLedsActive(true);

        try {
            mVisualizer = new Visualizer(0);
        } catch (Exception e) {
            Log.e(TAG, "error initializing visualizer", e);
            return;
        }

        mVisualizer.setEnabled(false);
        mVisualizer.setCaptureSize(66);
        mVisualizer.setDataCaptureListener(mVisualizerListener, Visualizer.getMaxCaptureRate(),
                false, true);
        mVisualizer.setEnabled(true);
    }

    public void stopMusic() {
        if (DEBUG) Log.d(TAG, "Disabling Music animation");
        StatusManager.setMusicLedsActive(false);
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
        cleanupAndContinue();
    }
}
