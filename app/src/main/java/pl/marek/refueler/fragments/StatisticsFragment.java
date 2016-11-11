package pl.marek.refueler.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
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

public class StatisticsFragment extends Fragment {
    private Car car = null;
    private List<Refuel> refuels;
    private BigDecimal totalFuelUsed = new BigDecimal(0);
    private BigDecimal totalDistanceCovered = new BigDecimal(0);
    private BigDecimal totalCost = new BigDecimal(0);
    private BigDecimal averageCost = new BigDecimal(0);
    private BigDecimal thisYearCost = new BigDecimal(0);
    private BigDecimal thisMonthCost = new BigDecimal(0);
    private BigDecimal prevMonthCost = new BigDecimal(0);
    private BigDecimal thisYearFuel = new BigDecimal(0);
    private BigDecimal thisMonthFuel = new BigDecimal(0);
    private BigDecimal prevMonthFuel = new BigDecimal(0);

    @Bind(R.id.distance_covered)
    TextView distance_covered;

    @Bind(R.id.fuel_usage)
    TextView fuel_usage;

    @Bind(R.id.refuel_count)
    TextView refuel_count;

    @Bind(R.id.money_spent)
    TextView money_spent;

    @Bind(R.id.min_consumption)
    TextView min_consumption;

    @Bind(R.id.avg_consumption)
    TextView avg_consumption;

    @Bind(R.id.max_consumption)
    TextView max_consumption;

    @Bind(R.id.fuel_this_year)
    TextView fuel_this_year;

    @Bind(R.id.fuel_this_month)
    TextView fuel_this_month;

    @Bind(R.id.fuel_prev_month)
    TextView fuel_prev_month;

    @Bind(R.id.cost_min_money_spent)
    TextView cost_min_money_spent;

    @Bind(R.id.cost_avg_money_spent)
    TextView cost_avg_money_spent;

    @Bind(R.id.cost_max_money_spent)
    TextView cost_max_money_spent;

    @Bind(R.id.cost_smallest_price)
    TextView cost_smallest_price;

    @Bind(R.id.cost_biggest_price)
    TextView cost_biggest_price;

    @Bind(R.id.cost_this_year)
    TextView cost_this_year;

    @Bind(R.id.cost_this_month)
    TextView cost_this_month;

    @Bind(R.id.cost_prev_month)
    TextView cost_prev_month;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.statistics_fragment, container, false);
        ButterKnife.bind(this, v);

        initView();

        return v;
    }

    private void initView() {
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null)
            car = (Car) extras.get("car");

        if (car != null) {
            refuels = Query.many(Refuel.class, "SELECT * FROM Refuels WHERE carId = ?", car.getId()).get().asList();
        } else {
            refuels = Query.all(Refuel.class).get().asList();
        }

        if(refuels != null) {
            Date actualDate = new Date();
            Calendar actualCalendar = Calendar.getInstance();
            actualCalendar.setTime(actualDate);
            int actualYear = actualCalendar.get(Calendar.YEAR);
            int actualMonth = actualCalendar.get(Calendar.MONTH);

            for(Refuel refuel : refuels) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
                Date date;

                try {
                    date = sdf.parse(refuel.getRefuelDate());
                } catch(Exception e) {
                    throw new RuntimeException(getString(R.string.wrong_date_format));
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);

                // GENERAL
                totalDistanceCovered = totalDistanceCovered.add(new BigDecimal(refuel.getDistance()));
                totalFuelUsed = totalFuelUsed.add(new BigDecimal(refuel.getVolume()));
                totalCost = totalCost.add(Services.getInstance().multiply(refuel.getPrice(), refuel.getVolume()));

                // FUEL & COSTS
                averageCost = Services.getInstance().divide(totalCost.toPlainString(), String.valueOf(refuels.size()));

                if(actualYear == year) {
                    thisYearFuel = thisYearFuel.add(new BigDecimal(refuel.getVolume()));
                    thisYearCost = thisYearCost.add(Services.getInstance().multiply(refuel.getPrice(), refuel.getVolume()));

                    if (actualMonth == month) {
                        thisMonthFuel = thisMonthFuel.add(new BigDecimal(refuel.getVolume()));
                        thisMonthCost = thisMonthCost.add(Services.getInstance().multiply(refuel.getPrice(), refuel.getVolume()));
                    }

                    if (actualMonth - 1 == month) {
                        prevMonthFuel = prevMonthFuel.add(new BigDecimal(refuel.getVolume()));
                        prevMonthCost = prevMonthCost.add(Services.getInstance().multiply(refuel.getPrice(), refuel.getVolume()));
                    }
                }
            }
        }

        initGeneral();
        initFuel();
        initCosts();
    }

    private void initGeneral() {
        // total distance covered
        distance_covered.setText(addDistanceUnit(totalDistanceCovered.toPlainString()));

        // total fuel usage
        fuel_usage.setText(addVolumeUnit(totalFuelUsed.toPlainString()));

        // refuel count
        refuel_count.setText(String.valueOf(refuels.size()));

        // total fuel usage
        money_spent.setText(addCurrencyUnit(totalCost.toPlainString()));
    }

    private void initFuel() {
        // this year cost
        fuel_this_year.setText(addVolumeUnit(thisYearFuel.toPlainString()));

        // this month cost
        fuel_this_month.setText(addVolumeUnit(thisMonthFuel.toPlainString()));

        // prev month cost
        fuel_prev_month.setText(addVolumeUnit(prevMonthFuel.toPlainString()));
    }

    private void initCosts() {
        // minimal money spent for bill
        Refuel var_cost_min_money_spent;
        if(car != null)
            var_cost_min_money_spent = Query.one(Refuel.class, "SELECT * FROM Refuels WHERE carId = ? ORDER BY price ASC", car.getId()).get();
        else
            var_cost_min_money_spent = Query.one(Refuel.class, "SELECT * FROM Refuels ORDER BY price ASC").get();

        if (var_cost_min_money_spent != null)
            cost_min_money_spent.setText(addCurrencyUnit(Services.getInstance().multiplyString(var_cost_min_money_spent.getPrice(), var_cost_min_money_spent.getVolume())));
        else
            cost_min_money_spent.setText(addCurrencyUnit("0"));

        // average money spent for bill
        cost_avg_money_spent.setText(addCurrencyUnit(averageCost.toPlainString()));

        // maximal money spent for bill
        Refuel var_cost_max_money_spent;
        if(car != null)
            var_cost_max_money_spent = Query.one(Refuel.class, "SELECT * FROM Refuels WHERE carId = ? ORDER BY price DESC", car.getId()).get();
        else
            var_cost_max_money_spent = Query.one(Refuel.class, "SELECT * FROM Refuels ORDER BY price DESC").get();

        if (var_cost_max_money_spent != null)
            cost_max_money_spent.setText(addCurrencyUnit(Services.getInstance().multiplyString(var_cost_max_money_spent.getPrice(), var_cost_max_money_spent.getVolume())));
        else
            cost_max_money_spent.setText(addCurrencyUnit("0"));

        // smallest fuel price
        Refuel var_cost_smallest_price;
        if(car != null)
            var_cost_smallest_price = Query.one(Refuel.class, "SELECT * FROM Refuels WHERE carId = ? ORDER BY price ASC", car.getId()).get();
        else
            var_cost_smallest_price = Query.one(Refuel.class, "SELECT * FROM Refuels ORDER BY price ASC").get();

        if (var_cost_smallest_price != null)
            cost_smallest_price.setText(addCurrencyUnit(var_cost_smallest_price.getPrice()));
        else
            cost_smallest_price.setText(addCurrencyUnit("0"));

        // biggest fuel price
        Refuel var_cost_biggest_price;
        if(car != null)
            var_cost_biggest_price = Query.one(Refuel.class, "SELECT * FROM Refuels WHERE carId = ? ORDER BY price DESC", car.getId()).get();
        else
            var_cost_biggest_price = Query.one(Refuel.class, "SELECT * FROM Refuels ORDER BY price DESC").get();

        if (var_cost_biggest_price != null)
            cost_biggest_price.setText(addCurrencyUnit(var_cost_biggest_price.getPrice()));
        else
            cost_biggest_price.setText(addCurrencyUnit("0"));

        // this year cost
        cost_this_year.setText(addCurrencyUnit(thisYearCost.toPlainString()));

        // this month cost
        cost_this_month.setText(addCurrencyUnit(thisMonthCost.toPlainString()));

        // prev month cost
        cost_prev_month.setText(addCurrencyUnit(prevMonthCost.toPlainString()));
    }

    private String addCurrencyUnit(String value) {
        String currencyUnit = "zł";
        return value + " " + currencyUnit;
    }

    private String addVolumeUnit(String value) {
        String volumeUnit = "l";
        return value + " " + volumeUnit;
    }

    private String addDistanceUnit(String value) {
        String distanceUnit = "km";
        return value + " " + distanceUnit;
    }
}
