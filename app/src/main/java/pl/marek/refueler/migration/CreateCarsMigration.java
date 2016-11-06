package pl.marek.refueler.migration;

import android.database.sqlite.SQLiteDatabase;

import se.emilsjolander.sprinkles.Migration;

public class CreateCarsMigration extends Migration {
    @Override
    protected void doMigration(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE cars ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "brand TEXT,"
                + "model TEXT,"
                + "registrationNumber TEXT,"
                + "productionYear INTEGER,"
                + "totalDistance INTEGER,"
                + "fuelType TEXT"
                + ");");
    }
}
