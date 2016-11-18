package pl.marek.refueler.charts;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marek.refueler.R;
import pl.marek.refueler.Services;
import pl.marek.refueler.charts.formatters.CurrencyFormatter;
import pl.marek.refueler.database.Car;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class MoneySpentChart extends Fragment {
    private Car car = null;

    @Bind(R.id.chart_bar)
    BarChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chart_bar, container, false);
        ButterKnife.bind(this, v);

        initView();

        return v;
    }

    private void initView() {
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null)
            car = (Car) extras.get("car");

        List<Refuel> refuels;
        if (car != null) {
            refuels = Query.many(Refuel.class, "SELECT * FROM Refuels WHERE carId = ?", car.getId()).get().asList();
        } else {
            refuels = Query.all(Refuel.class).get().asList();
        }

        if(refuels != null && !refuels.isEmpty()) {
            List<Integer> xVals = new ArrayList<>();
            List<Float> yVals = new ArrayList<>();

            Date date = new Date();
            for (Refuel refuel : refuels) {
                if (Services.getInstance().getYear(date) == Services.getInstance().getYear(refuel.getRefuelDate())) {
                    int month = Services.getInstance().getMonth(refuel.getRefuelDate());
                    if (!xVals.contains(month)) {
                        xVals.add(month);
                        yVals.add(Float.parseFloat(Services.getInstance().multiply(refuel.getPrice(), refuel.getVolume()).toPlainString()));
                    } else {
                        int index = xVals.indexOf(month);
                        float temp = yVals.get(index);
                        yVals.remove(index);
                        xVals.remove(index);
                        xVals.add(month);
                        yVals.add(temp + Float.parseFloat(Services.getInstance().multiply(refuel.getPrice(), refuel.getVolume()).toPlainString()));
                    }
                }
            }

            List<BarEntry> refuelCost = new ArrayList<>();
            for (int i = 0; i < xVals.size(); i++)
                refuelCost.add(new BarEntry(xVals.get(i), yVals.get(i)));

            Collections.sort(refuelCost, new EntryXComparator());

            BarDataSet refuelCostDataSet = new BarDataSet(refuelCost, getString(R.string.refuel_cost));
            refuelCostDataSet.setValueTextSize(12f);
            refuelCostDataSet.setValueFormatter(new CurrencyFormatter());
            refuelCostDataSet.setColors(ColorTemplate.COLORFUL_COLORS);


            BarData data = new BarData(refuelCostDataSet);
            chart.setData(data);

            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return getResources().getStringArray(R.array.month)[(int) value];
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });
            xAxis.setGranularity(1f);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(10f);
            xAxis.setTextColor(Color.BLACK);

            Description description = new Description();
            description.setText("");
            chart.setDescription(description);
            chart.setFitBars(true);
        }

        chart.setNoDataText(getString(R.string.no_chart_data));
        Paint p = chart.getPaint(Chart.PAINT_INFO);
        p.setTextSize(60f);
        p.setColor(Color.GRAY);
        chart.invalidate();
    }
}
