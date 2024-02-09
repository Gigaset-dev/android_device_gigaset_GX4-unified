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

package com.volla.spotlight.Settings;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import com.volla.spotlight.Manager.AnimationManager;
import com.volla.spotlight.Manager.LEDManager;
import com.volla.spotlight.R;
import com.volla.spotlight.Constants.Constants;
import com.volla.spotlight.Manager.SettingsManager;
import com.volla.spotlight.Services.NotificationService;
import com.volla.spotlight.Utils.ServiceUtils;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener,
        OnMainSwitchChangeListener {

    /** Hidden field Settings.Secure.ENABLED_NOTIFICATION_LISTENERS */
    private static final String NOTIFICATION_ENABLED_LISTENERS = "enabled_notification_listeners";

    private MainSwitchPreference mSwitchBar;

    private SeekBarPreference mBrightnessPreference;
    private SwitchPreference mNotifsPreference;
    private SwitchPreference mCallPreference;
    private SwitchPreference mChargingLevelPreference;
    private SwitchPreference mFlashlightPreference;
    private SwitchPreference mMusicPreference;

    private ContentResolver mContentResolver;
    private SettingObserver mSettingObserver;

    private Handler mHandler = new Handler();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.spotlight_settings);

        if (!isNotifListenerEnabled())
            SettingsManager.setSpotlightNotifsEnabled(getActivity(), false);

        mContentResolver = getActivity().getContentResolver();
        mSettingObserver = new SettingObserver();
        mSettingObserver.register(mContentResolver);

        boolean spotlightEnabled = SettingsManager.isSpotlightEnabled(getActivity());

        mSwitchBar = (MainSwitchPreference) findPreference(Constants.SPOTLIGHT_ENABLE);
        mSwitchBar.addOnSwitchChangeListener(this);
        mSwitchBar.setChecked(spotlightEnabled);

        mBrightnessPreference = (SeekBarPreference) findPreference(Constants.SPOTLIGHT_BRIGHTNESS);
        mBrightnessPreference.setEnabled(spotlightEnabled);
        mBrightnessPreference.setMin(1);
        mBrightnessPreference.setMax(100);
        mBrightnessPreference.setUpdatesContinuously(true);
        mBrightnessPreference.setOnPreferenceChangeListener(this);

        mNotifsPreference = (SwitchPreference) findPreference(Constants.SPOTLIGHT_NOTIFS_ENABLE);
        mNotifsPreference.setChecked(SettingsManager.isSpotlightNotifsEnabled(getActivity()));
        mNotifsPreference.setEnabled(spotlightEnabled);
        mNotifsPreference.setOnPreferenceChangeListener(this);

        mCallPreference = (SwitchPreference) findPreference(Constants.SPOTLIGHT_CALL_ENABLE);
        mCallPreference.setEnabled(spotlightEnabled);
        mCallPreference.setOnPreferenceChangeListener(this);

        mChargingLevelPreference = (SwitchPreference) findPreference(Constants.SPOTLIGHT_CHARGING_LEVEL_ENABLE);
        mChargingLevelPreference.setEnabled(spotlightEnabled);
        mChargingLevelPreference.setOnPreferenceChangeListener(this);

        mFlashlightPreference = (SwitchPreference) findPreference(Constants.SPOTLIGHT_FLASHLIGHT_ENABLE);
        mFlashlightPreference.setEnabled(spotlightEnabled);
        mFlashlightPreference.setOnPreferenceChangeListener(this);

        mMusicPreference = (SwitchPreference) findPreference(Constants.SPOTLIGHT_MUSIC_ENABLE);
        mMusicPreference.setEnabled(spotlightEnabled);
        mMusicPreference.setOnPreferenceChangeListener(this);
    }

    private boolean isNotifListenerEnabled() {
        boolean serviceEnabled = true;
        String enabledListeners = Settings.Secure.getString(
                getContext().getContentResolver(), NOTIFICATION_ENABLED_LISTENERS);
        ComponentName myListener =
                new ComponentName(getContext(), NotificationService.class);
        serviceEnabled = enabledListeners != null &&
                (enabledListeners.contains(myListener.flattenToString()) ||
                        enabledListeners.contains(myListener.flattenToShortString()));

        return serviceEnabled;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String preferenceKey = preference.getKey();

        if (preferenceKey.equals(Constants.SPOTLIGHT_NOTIFS_ENABLE)) {
            if ((boolean) newValue) {
                if (isNotifListenerEnabled()) {
                    SettingsManager.setSpotlightNotifsEnabled(getActivity(), true);

                    Intent intent = new Intent(getActivity(), NotifsSettingsActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    SettingsManager.setSpotlightNotifsEnabled(getActivity(), false);
                    mNotifsPreference.setChecked(false);

                    // Show a dialog to the user
                    final Context context = getActivity();
                    String msg = context.getString(R.string.msg_missing_notification_access,
                            context.getString(R.string.spotlight_settings_app_name));
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.title_missing_notification_access)
                            .setMessage(msg)
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton(R.string.title_change_settings, (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().startActivity(intent);
                            }).create().show();
                }
            } else {
                SettingsManager.setSpotlightNotifsEnabled(getActivity(), false);
            }
        }

        if (preferenceKey.equals(Constants.SPOTLIGHT_BRIGHTNESS)) {
            Constants.setBrightness(((int) newValue) / 100.0f);
        } else {
            mHandler.post(() -> ServiceUtils.checkSpotlightService(getActivity()));
        }

        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        SettingsManager.enableSpotlight(getActivity(), isChecked);
        ServiceUtils.checkSpotlightService(getActivity());

        mSwitchBar.setChecked(isChecked);

        mBrightnessPreference.setEnabled(isChecked);
        mNotifsPreference.setEnabled(isChecked);
        mCallPreference.setEnabled(isChecked);
        mChargingLevelPreference.setEnabled(isChecked);
        mFlashlightPreference.setEnabled(isChecked);
        mMusicPreference.setEnabled(isChecked);
    }

    @Override
    public void onDestroy() {
        mSettingObserver.unregister(mContentResolver);
        super.onDestroy();
    }

    private class SettingObserver extends ContentObserver {
        public SettingObserver() {
            super(new Handler());
        }

        public void register(ContentResolver cr) {
            cr.registerContentObserver(Settings.Secure.getUriFor(
                Constants.SPOTLIGHT_NOTIFS_ENABLE), false, this);
        }

        public void unregister(ContentResolver cr) {
            cr.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.equals(Settings.Secure.getUriFor(Constants.SPOTLIGHT_NOTIFS_ENABLE))) {
                mNotifsPreference.setChecked(SettingsManager.isSpotlightNotifsEnabled(getActivity()));
            }
        }
    }
}
