package pl.marek.refueler.migration;

import android.database.sqlite.SQLiteDatabase;

import se.emilsjolander.sprinkles.Migration;

public class CreateRefuelsMigration extends Migration {
    @Override
    protected void doMigration(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE refuels ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "price TEXT,"
                + "volume TEXT,"
                + "distance INTEGER,"
                + "fuelType TEXT,"
                + "carId INTEGER"
                + ");");
    }
}