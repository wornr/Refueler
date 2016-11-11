package pl.marek.refueler.car.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
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
        car = (Car) getActivity().getIntent().getExtras().get("car");
        if (car != null) {
            refuels = Query.many(Refuel.class, "SELECT * FROM Refuels WHERE carId = ?", car.getId()).get().asList();
        } else {
            refuels = Query.all(Refuel.class).get().asList();
        }

        if(refuels != null) {
            for(Refuel refuel : refuels) {
                // GENERAL
                totalDistanceCovered = totalDistanceCovered.add(new BigDecimal(refuel.getDistance()));
                totalFuelUsed = totalFuelUsed.add(new BigDecimal(refuel.getVolume()));
                totalCost = totalCost.add(Services.getInstance().multiply(refuel.getPrice(), refuel.getVolume()));

                // FUEL


                // COSTS
                averageCost = Services.getInstance().divide(totalCost.toPlainString(), String.valueOf(refuels.size()));
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
        // TODO add code for fuel statistics
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

        // Smallest fuel price
        Refuel var_cost_smallest_price;
        if(car != null)
            var_cost_smallest_price = Query.one(Refuel.class, "SELECT * FROM Refuels WHERE carId = ? ORDER BY price ASC", car.getId()).get();
        else
            var_cost_smallest_price = Query.one(Refuel.class, "SELECT * FROM Refuels ORDER BY price ASC").get();

        if (var_cost_smallest_price != null)
            cost_smallest_price.setText(addCurrencyUnit(var_cost_smallest_price.getPrice()));
        else
            cost_smallest_price.setText(addCurrencyUnit("0"));

        // Biggest fuel price
        Refuel var_cost_biggest_price;
        if(car != null)
            var_cost_biggest_price = Query.one(Refuel.class, "SELECT * FROM Refuels WHERE carId = ? ORDER BY price DESC", car.getId()).get();
        else
            var_cost_biggest_price = Query.one(Refuel.class, "SELECT * FROM Refuels ORDER BY price DESC").get();

        if (var_cost_biggest_price != null)
            cost_biggest_price.setText(addCurrencyUnit(var_cost_biggest_price.getPrice()));
        else
            cost_biggest_price.setText(addCurrencyUnit("0"));
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
