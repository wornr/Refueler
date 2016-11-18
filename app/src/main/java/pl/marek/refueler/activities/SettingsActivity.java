package pl.marek.refueler.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import java.util.Locale;

import pl.marek.refueler.LocaleUtils;
import pl.marek.refueler.R;

public class SettingsActivity extends PreferenceActivity {
    public SettingsActivity() {
        LocaleUtils.updateConfiguration(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewParent parent = findViewById(android.R.id.list).getParent();
        LinearLayout root = (LinearLayout) parent.getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(toolbar, 0);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getFragmentManager().beginTransaction()
                .replace(((ViewGroup) parent).getId(), new SettingsFragment()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final ListPreference languagesPreference = (ListPreference) findPreference("language_preference");
            final EditTextPreference currencyUnitPreference = (EditTextPreference) findPreference("currency_unit_preference");
            final EditTextPreference distanceUnitPreference = (EditTextPreference) findPreference("distance_unit_preference");
            final EditTextPreference volumeUnitPreference = (EditTextPreference) findPreference("volume_unit_preference");

            currencyUnitPreference.setSummary(currencyUnitPreference.getText());
            distanceUnitPreference.setSummary(distanceUnitPreference.getText());
            volumeUnitPreference.setSummary(volumeUnitPreference.getText());

            languagesPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Configuration config = getActivity().getBaseContext().getResources().getConfiguration();
                    Configuration newConfig = new Configuration(config);

                    if (newValue.toString().equals("system")) {
                        newConfig.locale = new Locale(config.locale.getLanguage());
                    } else {
                        newConfig.locale = new Locale(newValue.toString());
                    }
                    LocaleUtils.setLocale(newConfig.locale);
                    LocaleUtils.updateConfig(getActivity().getApplication(), new android.content.res.Configuration(newConfig));

                    restartApp();

                    return true;
                }
            });

            currencyUnitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());

                    return true;
                }
            });

            distanceUnitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());

                    return true;
                }
            });

            volumeUnitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());

                    return true;
                }
            });
        }

        private void restartApp() {
            Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
}
