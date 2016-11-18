package pl.marek.refueler;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

import pl.marek.refueler.migration.CreateCarsMigration;
import pl.marek.refueler.migration.CreateRefuelsMigration;
import se.emilsjolander.sprinkles.Sprinkles;

public class RefuelerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Sprinkles sprinkles = Sprinkles.init(getApplicationContext());
        runMigrations(sprinkles);
        initializeSettings();

        LocaleUtils.setLocale(new Locale(PreferenceManager.getDefaultSharedPreferences(this).getString("language_preference", null)));
        LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());
    }

    private void runMigrations(Sprinkles sprinkles) {
        sprinkles.addMigration(new CreateCarsMigration());
        sprinkles.addMigration(new CreateRefuelsMigration());
    }

    private void initializeSettings() {
        if(PreferenceManager.getDefaultSharedPreferences(this).getString("language_preference", null) == null) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("language_preference", "en").apply();
        }

        if(PreferenceManager.getDefaultSharedPreferences(this).getString("currency_unit_preference", null) == null) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("currency_unit_preference", "$").apply();
        }

        if(PreferenceManager.getDefaultSharedPreferences(this).getString("distance_unit_preference", null) == null) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("distance_unit_preference", "km").apply();
        }

        if(PreferenceManager.getDefaultSharedPreferences(this).getString("volume_unit_preference", null) == null) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("volume_unit_preference", "l").apply();
        }
    }
}
