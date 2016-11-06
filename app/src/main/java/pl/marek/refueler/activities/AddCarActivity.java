package pl.marek.refueler.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.marek.refueler.R;
import pl.marek.refueler.database.Car;

public class AddCarActivity extends AppCompatActivity {
    private Car car = new Car();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.set_brand)
    EditText brand;

    @Bind(R.id.set_model)
    EditText model;

    @Bind(R.id.set_registrationNumber)
    EditText registrationNumber;

    @Bind(R.id.set_productionYear)
    EditText productionYear;

    @Bind(R.id.set_totalDistance)
    EditText totalDistance;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_car_activity);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras() != null) {
            car = (Car) getIntent().getExtras().get("car");
            if (car != null) {
                setCar();
                getSupportActionBar().setTitle(car.getBrand() + " " + car.getModel());
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
            saveCar();
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveCar() {
        Car car = new Car();

        if (this.car != null) {
            car.setId(this.car.getId());
        }

        car.setBrand(this.car.getBrand());
        car.setModel(this.car.getModel());
        car.setRegistrationNumber(this.car.getRegistrationNumber());
        car.setTotalDistance(this.car.getTotalDistance());

        if (carValidation(car)) {
            car.save();
            finish();
        }
    }

    private boolean carValidation(Car car) {
        boolean valid = true;

        if (!TextUtils.isEmpty(brand.getText())) {
            car.setBrand(brand.getText().toString());
        } else {
            brand.setError("Pole nie może być puste");
            valid = false;
        }

        if (!TextUtils.isEmpty(model.getText())) {
            car.setModel(model.getText().toString());
        } else {
            model.setError("Pole nie może być puste");
            valid = false;
        }

        if (!TextUtils.isEmpty(registrationNumber.getText())) {
            car.setRegistrationNumber(registrationNumber.getText().toString());
        }

        if(!TextUtils.isEmpty(totalDistance.getText())) {
            if (Integer.parseInt(String.valueOf(totalDistance.getText())) > 0) {
                car.setTotalDistance(Integer.parseInt(totalDistance.getText().toString()));
            } else {
                totalDistance.setError("Wartość musi być większa od zera");
                valid = false;
            }
        } else {
            totalDistance.setError("Pole nie może być puste");
            valid = false;
        }

        return valid;
    }

    private void setCar() {
        brand.setText(car.getBrand(), TextView.BufferType.EDITABLE);
        model.setText(car.getModel(), TextView.BufferType.EDITABLE);
        registrationNumber.setText(car.getRegistrationNumber(), TextView.BufferType.EDITABLE);
        totalDistance.setText(String.valueOf(car.getTotalDistance()), TextView.BufferType.EDITABLE);
    }
}
