/*
 * Copyright (C) 2013 SlimRoms Project
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

package com.android.settings.purity;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarSettings";

    private static final String STATUS_BAR_NETWORK_ACTIVITY = "status_bar_network_activity";
    private static final String KEY_STATUS_BAR_CLOCK = "clock_style_pref";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String DOUBLE_TAP_SLEEP_GESTURE = "double_tap_sleep_gesture";

    private CheckBoxPreference mStatusBarNetworkActivity;
    private PreferenceScreen mClockStyle;
    private CheckBoxPreference mStatusBarBrightnessControl;
    private CheckBoxPreference mStatusBarDoubleTapSleepGesture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();


        mStatusBarBrightnessControl = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(resolver,Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));
        mStatusBarBrightnessControl.setOnPreferenceChangeListener(this);

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        mClockStyle = (PreferenceScreen) prefSet.findPreference(KEY_STATUS_BAR_CLOCK);
        if (mClockStyle != null) {
            updateClockStyleDescription();
        }

        mStatusBarNetworkActivity = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_NETWORK_ACTIVITY);
        mStatusBarNetworkActivity.setChecked(Settings.System.getInt(resolver,
            Settings.System.STATUS_BAR_NETWORK_ACTIVITY, 0) == 1);
        mStatusBarNetworkActivity.setOnPreferenceChangeListener(this);

        mStatusBarDoubleTapSleepGesture = (CheckBoxPreference) findPreference(DOUBLE_TAP_SLEEP_GESTURE);
        mStatusBarDoubleTapSleepGesture.setChecked(Settings.System.getInt(getContentResolver(),
            Settings.System.DOUBLE_TAP_SLEEP_GESTURE, 0) == 1);
        mStatusBarDoubleTapSleepGesture.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarBrightnessControl) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
       } else if (preference == mStatusBarNetworkActivity) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                Settings.System.STATUS_BAR_NETWORK_ACTIVITY, value ? 1 : 0);
       } else if (preference == mStatusBarDoubleTapSleepGesture) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                Settings.System.DOUBLE_TAP_SLEEP_GESTURE, value ? 1 : 0);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateClockStyleDescription();
    }

    private void updateClockStyleDescription() {
        if (Settings.System.getInt(getContentResolver(),
               Settings.System.STATUS_BAR_CLOCK, 1) == 1) {
            mClockStyle.setSummary(getString(R.string.enabled));
        } else {
            mClockStyle.setSummary(getString(R.string.disabled));
        }
    }
}