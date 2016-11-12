package pl.marek.refueler.charts;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marek.refueler.R;
import pl.marek.refueler.Services;
import pl.marek.refueler.charts.formatters.CurrencyFormatter;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class FuelPriceChart extends Fragment {
    @Bind(R.id.chart_line)
    LineChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chart_line, container, false);
        ButterKnife.bind(this, v);

        initView();

        return v;
    }

    private void initView() {
        List<Refuel> refuels = Query.all(Refuel.class).get().asList();
        final long minTimestamp = Services.getInstance().getMinTimestamp(refuels);

        List<Entry> refuelPrice = new ArrayList<>();

        for (Refuel data : refuels) {
            refuelPrice.add(new Entry(Services.getInstance().dateToFloat(data.getRefuelDate(), minTimestamp), Float.parseFloat(data.getPrice())));
        }

        Collections.sort(refuelPrice, new EntryXComparator());

        LineDataSet refuelPriceDataSet = new LineDataSet(refuelPrice, "Cena paliwa");
        refuelPriceDataSet.setColor(Color.GREEN,100);
        refuelPriceDataSet.setValueTextColor(Color.GREEN);
        refuelPriceDataSet.setValueTextSize(10f);
        refuelPriceDataSet.setValueFormatter(new CurrencyFormatter());

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(refuelPriceDataSet);

        LineData data = new LineData(dataSets);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Services.getInstance().floatToDate(value, minTimestamp);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        chart.invalidate(); // refresh
    }
}
