package pl.marek.refueler.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marek.refueler.R;
import pl.marek.refueler.Services;
import pl.marek.refueler.database.Car;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class AddRefuelingActivity extends LocalizationActivity {
    private Refuel refuel = new Refuel();
    private Car car = new Car();
    private String fuelType = "";
    private DialogFragment datePicker = new DatePickerFragment();

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
    Spinner spinnerFuelType;

    @Bind(R.id.set_date)
    EditText refuel_date;

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
        emptyCar.setBrand(getString(R.string.choose_car));

        List<Car> cars = Query.all(Car.class).get().asList();
        cars.add(0, emptyCar);

        ArrayAdapter<Car> spinnerCarAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cars);
        spinnerCarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCar.setAdapter(spinnerCarAdapter);
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
            for (int i = 0; i < spinnerCarAdapter.getCount(); i++) {
                //noinspection ConstantConditions
                if (spinnerCarAdapter.getItem(i).getId() == car.getId()) {
                    spinnerCar.setSelection(i);
                    break;
                }
            }
        }

        ArrayAdapter<CharSequence> spinnerFuelTypeAdapter = ArrayAdapter.createFromResource(this, R.array.fuel_types_array, android.R.layout.simple_spinner_item);
        spinnerFuelTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuelType.setAdapter(spinnerFuelTypeAdapter);
        spinnerFuelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fuelType = (String) spinnerFuelType.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (!fuelType.equals(getResources().getStringArray(R.array.fuel_types_array)[0])) {
            for (int i = 0; i < spinnerFuelTypeAdapter.getCount(); i++) {
                //noinspection ConstantConditions
                if (spinnerFuelTypeAdapter.getItem(i).equals(fuelType)) {
                    spinnerFuelType.setSelection(i);
                    break;
                }
            }
        }

        refuel_date.setInputType(InputType.TYPE_NULL);
        refuel_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        refuel_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(v);
                }
            }
        });
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
            fuel_price.setError(getString(R.string.not_empty));
            valid = false;
        }

        if (!TextUtils.isEmpty(fuel_volume.getText())) {
            refuel.setVolume(fuel_volume.getText().toString());
        } else {
            fuel_volume.setError(getString(R.string.not_empty));
            valid = false;
        }

        if (!TextUtils.isEmpty(distance.getText())) {
            refuel.setDistance(Integer.parseInt(distance.getText().toString()));
        } else {
            distance.setError(getString(R.string.not_empty));
            valid = false;
        }

        View selectedView = spinnerFuelType.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            TextView selectedTextView = (TextView) selectedView;
            if (!fuelType.equals(getResources().getStringArray(R.array.fuel_types_array)[0])) {
                refuel.setFuelType(fuelType);
            } else {
                selectedTextView.setError(getString(R.string.not_chosen));
                valid = false;
            }
        }

        refuel.setCarId(car.getId());
        if (spinnerCar.getVisibility() == View.VISIBLE) {
            selectedView = spinnerCar.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                if (car.getId() != 0) {
                    refuel.setFuelType(((TextView) selectedView).getText().toString());
                } else {
                    selectedTextView.setError(getString(R.string.not_chosen));
                    valid = false;
                }
            }
        }

        if(!TextUtils.isEmpty(refuel_date.getText())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
            try {
                sdf.parse(refuel_date.getText().toString());
            } catch(Exception e) {
                refuel_date.setError(getString(R.string.wrong_date_format));
                valid = false;
            }
            refuel.setRefuelDate(refuel_date.getText().toString());
        } else {
            refuel_date.setError(getString(R.string.not_empty));
            valid = false;
        }

        return valid;
    }

    private void setRefuel() {
        fuel_price.setText(refuel.getPrice(), TextView.BufferType.EDITABLE);
        fuel_volume.setText(refuel.getVolume(), TextView.BufferType.EDITABLE);
        distance.setText(String.valueOf(refuel.getDistance()), TextView.BufferType.EDITABLE);
        refuel_date.setText(refuel.getRefuelDate(), TextView.BufferType.EDITABLE);

        fuelType = refuel.getFuelType();
        car.setId(refuel.getCarId());
    }

    public void showDatePickerDialog(View v) {
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
            Date date = null;
            try {
                date = sdf.parse(((EditText)getActivity().findViewById(R.id.set_date)).getText().toString());
            } catch(Exception ignored) {
            }

            final Calendar c = Calendar.getInstance();
            if(date != null)
                c.setTime(date);

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((EditText)getActivity().findViewById(R.id.set_date)).setText(year + "-" + (month < 9 ? "0" + (month + 1) : (month + 1)) + "-" + (day < 10 ? "0" + day : day));
        }
    }
}