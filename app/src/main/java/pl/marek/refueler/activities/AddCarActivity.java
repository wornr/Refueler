package pl.marek.refueler.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marek.refueler.R;
import pl.marek.refueler.Services;
import pl.marek.refueler.database.Car;

public class AddCarActivity extends AppCompatActivity {
    private Car car = new Car();

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
            if(getIntent().getExtras().get("car") != null) {
                car = (Car) getIntent().getExtras().get("car");
                getSupportActionBar().setTitle(car.getBrand() + " " + car.getModel());
                totalDistance.setVisibility(View.GONE);
                setCar();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);

        MenuItem menuItem = menu.findItem(R.id.action_save);

        if (menuItem != null) {
            Services.getInstance().tintMenuIcon(this, menuItem, android.R.color.white);
        }

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

    private void saveCar() {
        Car car = new Car();

        car.setId(this.car.getId());

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
            brand.setError(getString(R.string.not_empty));
            valid = false;
        }

        if (!TextUtils.isEmpty(model.getText())) {
            car.setModel(model.getText().toString());
        } else {
            model.setError(getString(R.string.not_empty));
            valid = false;
        }

        if (!TextUtils.isEmpty(registrationNumber.getText())) {
            car.setRegistrationNumber(registrationNumber.getText().toString().toUpperCase());
        }

        if(!TextUtils.isEmpty(totalDistance.getText())) {
            if (Integer.parseInt(String.valueOf(totalDistance.getText())) > 0) {
                car.setTotalDistance(Integer.parseInt(totalDistance.getText().toString()));
            } else {
                totalDistance.setError(getString(R.string.gt_zero));
                valid = false;
            }
        } else {
            totalDistance.setError(getString(R.string.not_empty));
            valid = false;
        }

        // not obligatory fields
        if(productionYear.getText().length() == 4) {
            car.setProductionYear(Integer.parseInt(productionYear.getText().toString()));
        } else {
            if(!TextUtils.isEmpty(productionYear.getText())) {
                productionYear.setError(getString(R.string.incorrect_year));
                valid = false;
            }
        }

        return valid;
    }

    private void setCar() {
        brand.setText(car.getBrand(), TextView.BufferType.EDITABLE);
        model.setText(car.getModel(), TextView.BufferType.EDITABLE);
        registrationNumber.setText(car.getRegistrationNumber(), TextView.BufferType.EDITABLE);
        totalDistance.setText(String.valueOf(car.getTotalDistance()), TextView.BufferType.EDITABLE);
        productionYear.setText(String.valueOf(car.getProductionYear()), TextView.BufferType.EDITABLE);
    }
}
