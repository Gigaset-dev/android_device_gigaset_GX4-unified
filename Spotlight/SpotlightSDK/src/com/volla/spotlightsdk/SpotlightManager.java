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

package com.volla.spotlightsdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


public class SpotlightManager {
    /**
     * Callback interface when service is connected/disconnected.
     */
    public interface ServiceConnectionListener {
        /**
         * Called when service is connected.
         */
        void onServiceConnected();

        /**
         * Called when service is disconnected.
         */
        void onServiceDisconnected();
    }

    public static final String SPOTLIGHT_PERMISSION = "com.volla.permission.SPOTLIGHT";

    private static final String TAG = "SpotlightManager";
    private static final boolean DEBUG = false;

    private ISpotlight sService;
    private Context mContext;
    private final Intent mServiceIntent;

    private ServiceConnectionListener mConnectionListener;
    private ServiceConnection mServiceConnection;


    public SpotlightManager(Context context, ServiceConnectionListener listener) {
        Context appContext = context.getApplicationContext();
        mContext = appContext == null ? context : appContext;
        mConnectionListener = listener;
        mServiceIntent = new Intent("SpotlightService");
        mServiceIntent.setPackage("com.volla.spotlight");
        mServiceConnection = createServiceConnection();
    }

    /**
     * Start the controller.
     */
    public void start() {
        mContext.bindServiceAsUser(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE,
                android.os.Process.myUserHandle());
    }

    /**
     * Stop the controller.
     */
    public void stop() {
        if (sService != null) {
            sService = null;
            mContext.unbindService(mServiceConnection);
        }
    }

    /**
     * @return true if service is valid
     */
    private boolean checkService() {
        if (sService == null) {
            Log.w(TAG, "not connected to SpotlightService");
            return false;
        }
        return true;
    }

    public int isAvailable() {
        try {
            if (checkService())
                return sService.isAvailable();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        return 0;
    }

    public int getNumLEDs() {
        try {
            if (checkService())
                return sService.getNumLEDs();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        return 0;
    }

    public void enableHW(boolean enable) {
        try {
            if (checkService())
                sService.enableHW(enable);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void setLEDBrightness(int ledno, float brightness) {
        try {
            if (checkService())
                sService.setLEDBrightness(ledno, brightness);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public boolean supportsBlink() {
        try {
            if (checkService())
                return sService.supportsBlink();
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        return false;
    }

    public void setLEDsBlink(boolean enable) {
        try {
            if (checkService())
                sService.setLEDsBlink(enable);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void enableAllLEDs(boolean enable) {
        try {
            if (checkService())
                sService.enableAllLEDs(enable);
        } catch (RemoteException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    /**
     * Create a new {@link ServiceConnection} object to handle service connect/disconnect event.
     */
    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (DEBUG) {
                    Log.d(TAG, "Service is connected");
                }
                sService = ISpotlight.Stub.asInterface(service);
                if (mConnectionListener != null) {
                    mConnectionListener.onServiceConnected();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (mConnectionListener != null) {
                    sService = null;
                    mConnectionListener.onServiceDisconnected();
                }
            }
        };
    }
}
