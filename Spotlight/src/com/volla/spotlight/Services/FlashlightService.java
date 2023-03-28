/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2018 The LineageOS Project
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

package com.volla.spotlight.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import com.volla.spotlight.Constants.Constants;
import com.volla.spotlight.Manager.AnimationManager;
import com.volla.spotlight.Utils.FileUtils;

public class FlashlightService extends Service {

    private static final String TAG = "SpotlightFlashlightService";
    private static final boolean DEBUG = true;
    private AnimationManager mAnimationManager;
    private CameraManager mCameraManager;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mAnimationManager = new AnimationManager(this);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mCameraManager.registerTorchCallback(mTorchCallback, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");

        onTorchDisabled();
        mCameraManager.unregisterTorchCallback(mTorchCallback);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final CameraManager.TorchCallback mTorchCallback = new CameraManager.TorchCallback() {
        @Override
        public void onTorchModeUnavailable(String cameraId) {
            onTorchDisabled();
        }

        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            if (enabled) {
                onTorchEnabled();
            } else {
                onTorchDisabled();
            }
        }
    };

    private void onTorchEnabled() {
        if (DEBUG) Log.d(TAG, "Flashlight enabled");
        mAnimationManager.playFlashlight();
    }

    private void onTorchDisabled() {
        if (DEBUG) Log.d(TAG, "Flashlight disabled");
        mAnimationManager.stopFlashlight();
    }
}
