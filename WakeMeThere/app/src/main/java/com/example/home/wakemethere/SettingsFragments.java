package com.example.home.wakemethere;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

public class SettingsFragments extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.locationpreferences);

        String defaultValue = getArguments().getString("defaultName");
        String defaultDist = getArguments().getString("defaultDist","1");
        String defaultMessage = getArguments().getString("defaultMessage","Reached Destination!!!");

        EditTextPreference namePref = (EditTextPreference) findPreference("example_text");
        namePref.setSummary(defaultValue);
        namePref.setText(defaultValue);
        namePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                ((EditTextPreference) preference).setText(newValue.toString());
                return false;
            }
        });

        EditTextPreference kmPref = (EditTextPreference) findPreference("distkms");
        kmPref.setSummary(defaultDist);
        kmPref.setText(defaultDist);
        kmPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                ((EditTextPreference) preference).setText(newValue.toString());
                return false;
            }
        });

        EditTextPreference alertMessage = (EditTextPreference) findPreference("message");
        alertMessage.setSummary(defaultMessage);
        alertMessage.setText(defaultMessage);
        alertMessage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                ((EditTextPreference) preference).setText(newValue.toString());
                return false;
            }
        });
    }


}
