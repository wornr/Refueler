package pl.marek.refueler.charts;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
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
import pl.marek.refueler.charts.markers.CurrencyMarkerView;
import pl.marek.refueler.database.Car;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class FuelPriceChart extends Fragment {
    private Car car = null;

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
            final long minTimestamp = Services.getInstance().getMinTimestamp(refuels);

            List<Entry> refuelPrice = new ArrayList<>();

            for (Refuel data : refuels)
                refuelPrice.add(new Entry(Services.getInstance().dateToFloat(data.getRefuelDate(), minTimestamp), Float.parseFloat(data.getPrice())));

            Collections.sort(refuelPrice, new EntryXComparator());

            LineDataSet refuelPriceDataSet = new LineDataSet(refuelPrice, getString(R.string.price_per_liter));
            refuelPriceDataSet.setColor(Color.MAGENTA);
            refuelPriceDataSet.setValueTextSize(0f);
            refuelPriceDataSet.setCircleColor(Color.MAGENTA);

            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(refuelPriceDataSet);

            LineData data = new LineData(dataSets);
            chart.setData(data);

            IMarker marker = new CurrencyMarkerView(getContext(), R.layout.chart_marker);
            chart.setMarker(marker);

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
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(4);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(10f);
            xAxis.setTextColor(Color.BLACK);

            Description description = new Description();
            description.setText("");
            chart.setDescription(description);
        }

        chart.setNoDataText(getString(R.string.no_chart_data));
        Paint p = chart.getPaint(Chart.PAINT_INFO);
        p.setTextSize(60f);
        p.setColor(Color.GRAY);
        chart.invalidate();
    }
}
