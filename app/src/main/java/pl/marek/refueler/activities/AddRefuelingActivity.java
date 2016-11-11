package pl.marek.refueler.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marek.refueler.R;
import pl.marek.refueler.database.Car;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class AddRefuelingActivity extends AppCompatActivity {
    private Refuel refuel = new Refuel();
    private Car car = new Car();

    @Bind(R.id.car_layout)
    LinearLayout car_layout;

    @Bind(R.id.set_car)
    Spinner spinnerCar;

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
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().get("refuel") != null) {
                refuel = (Refuel) getIntent().getExtras().get("refuel");
                setRefuel();
            }
            if (getIntent().getExtras().get("car") != null) {
                car = (Car) getIntent().getExtras().get("car");
                car_layout.setVisibility(View.GONE);
            }
        }

        Car emptyCar = new Car();
        emptyCar.setBrand("Choose car");

        List<Car> cars = Query.all(Car.class).get().asList();
        cars.add(0, emptyCar);

        ArrayAdapter<Car> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCar.setAdapter(spinnerAdapter);
        spinnerCar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car = (Car) spinnerCar.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (car.getId() != 0) {
            for (int i = 0; i < spinnerAdapter.getCount(); i++) {
                //noinspection ConstantConditions
                if (spinnerAdapter.getItem(i).getId() == car.getId()) {
                    spinnerCar.setSelection(i);
                    break;
                }
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

        if(id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_save) {
            saveRefuel();
        }

        return true;
    }

    public void saveRefuel() {
        Refuel refuel = new Refuel();

        refuel.setId(this.refuel.getId());

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

        // TODO car selection validation (set error message)
        refuel.setCarId(car.getId());
        if (spinnerCar.getVisibility() == View.VISIBLE) {
            if(car.getId() == 0)
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
        car.setId(refuel.getCarId());
    }
}