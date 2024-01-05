/*
 * Copyright (C) 2024 The VollaOS Project
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
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.volla.spotlight.Manager.LEDManager;
import com.volla.spotlight.Manager.SettingsManager;
import com.volla.spotlightsdk.ISpotlight;
import com.volla.spotlightsdk.SpotlightManager;

public class CustomService extends Service {

    private static final String TAG = "CustomService";
    private static final boolean DEBUG = true;
    private LEDManager mLEDManager;
    private Context mContext;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mLEDManager = new LEDManager();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }

    private final IBinder mService = new ISpotlight.Stub() {

        @Override
        public int isAvailable() throws RemoteException {
            mContext.enforceCallingOrSelfPermission(SpotlightManager.SPOTLIGHT_PERMISSION, TAG);

            boolean ret = false;
            long token = clearCallingIdentity();

            ret = SettingsManager.isSpotlightEnabled(mContext);
            restoreCallingIdentity(token);

            return ret ? 1 : 0;
        }

        @Override
        public int getNumLEDs() throws RemoteException {
            mContext.enforceCallingOrSelfPermission(SpotlightManager.SPOTLIGHT_PERMISSION, TAG);

            int ret = 0;
            long token = clearCallingIdentity();

            ret = mLEDManager.getNumLEDs();
            restoreCallingIdentity(token);

            return ret;
        }

        @Override
        public void enableHW(boolean enable) throws RemoteException {
            mContext.enforceCallingOrSelfPermission(SpotlightManager.SPOTLIGHT_PERMISSION, TAG);
            long token = clearCallingIdentity();

            mLEDManager.enableHW(enable);
            restoreCallingIdentity(token);
        }

        @Override
        public void setLEDBrightness(int ledno, float brightness) throws RemoteException {
            mContext.enforceCallingOrSelfPermission(SpotlightManager.SPOTLIGHT_PERMISSION, TAG);
            long token = clearCallingIdentity();

            mLEDManager.setLEDBrightness(ledno, brightness);
            restoreCallingIdentity(token);
        }

        @Override
        public boolean supportsBlink() throws RemoteException {
            mContext.enforceCallingOrSelfPermission(SpotlightManager.SPOTLIGHT_PERMISSION, TAG);
            long token = clearCallingIdentity();

            boolean ret = false;

            ret = mLEDManager.supportsBlink();
            restoreCallingIdentity(token);

            return ret;
        }

        @Override
        public void setLEDsBlink(boolean enable) throws RemoteException {
            mContext.enforceCallingOrSelfPermission(SpotlightManager.SPOTLIGHT_PERMISSION, TAG);
            long token = clearCallingIdentity();

            mLEDManager.setLEDsBlink(enable);
            restoreCallingIdentity(token);
        }

        @Override
        public void enableAllLEDs(boolean enable) throws RemoteException {
            mContext.enforceCallingOrSelfPermission(SpotlightManager.SPOTLIGHT_PERMISSION, TAG);
            long token = clearCallingIdentity();

            mLEDManager.enableAllLEDs(enable);
            restoreCallingIdentity(token);
        }
    };
}
