package pl.marek.refueler;

import android.app.Application;

import pl.marek.refueler.migration.CreateCarsMigration;
import pl.marek.refueler.migration.CreateRefuelsMigration;
import se.emilsjolander.sprinkles.Sprinkles;

public class RefuelerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Sprinkles sprinkles = Sprinkles.init(getApplicationContext());
        runMigrations(sprinkles);
    }

    private void runMigrations(Sprinkles sprinkles) {
        sprinkles.addMigration(new CreateCarsMigration());
        sprinkles.addMigration(new CreateRefuelsMigration());
    }
}
