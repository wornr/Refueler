package pl.marek.refueler.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marek.refueler.R;
import pl.marek.refueler.database.Refuel;

public class AddRefuelingActivity extends AppCompatActivity {
    private Refuel refuel = new Refuel();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.set_fuel_price)
    EditText fuel_price;

    @Bind(R.id.set_fuel_volume)
    EditText fuel_volume;

    @Bind(R.id.set_distance)
    EditText distance;

    @Bind(R.id.set_fuel_type)
    EditText fuel_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_refueling_activity);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras() != null) {
            refuel = (Refuel) getIntent().getExtras().get("refuel");
            if (refuel != null) {
                setRefuel();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveRefuel();
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveRefuel() {
        Refuel refuel = new Refuel();

        if (this.refuel != null) {
            refuel.setId(this.refuel.getId());
        }

        refuel.setPrice(this.refuel.getPrice());
        refuel.setVolume(this.refuel.getVolume());
        refuel.setDistance(this.refuel.getDistance());
        refuel.setFuelType(this.refuel.getFuelType());

        if (refuelValidation(refuel)) {
            refuel.save();
            finish();
        }
    }

    private boolean refuelValidation(Refuel refuel) {
        boolean valid = true;

        if (!TextUtils.isEmpty(fuel_price.getText())) {
            refuel.setPrice(fuel_price.getText().toString());
        } else {
            fuel_price.setError("Pole nie może być puste");
            valid = false;
        }

        if (!TextUtils.isEmpty(fuel_volume.getText())) {
            refuel.setVolume(fuel_volume.getText().toString());
        } else {
            fuel_volume.setError("Pole nie może być puste");
            valid = false;
        }

        if (!TextUtils.isEmpty(distance.getText())) {
            refuel.setDistance(Integer.parseInt(distance.getText().toString()));
        } else {
            distance.setError("Pole nie może być puste");
            valid = false;
        }

        if (!TextUtils.isEmpty(fuel_type.getText())) {
            refuel.setFuelType(fuel_type.getText().toString());
        } else {
            fuel_volume.setError("Pole nie może być puste");
            valid = false;
        }

        /*if(!TextUtils.isEmpty(totalDistance.getText())) {
            if (Integer.parseInt(String.valueOf(totalDistance.getText())) > 0) {
                car.setTotalDistance(Integer.parseInt(totalDistance.getText().toString()));
            } else {
                totalDistance.setError("Wartość musi być większa od zera");
                valid = false;
            }
        } else {
            totalDistance.setError("Pole nie może być puste");
            valid = false;
        }*/

        return valid;
    }

    private void setRefuel() {
        fuel_price.setText(refuel.getPrice(), TextView.BufferType.EDITABLE);
        fuel_volume.setText(refuel.getVolume(), TextView.BufferType.EDITABLE);
        distance.setText(String.valueOf(refuel.getDistance()), TextView.BufferType.EDITABLE);
        fuel_type.setText(refuel.getFuelType(), TextView.BufferType.EDITABLE);
    }
}