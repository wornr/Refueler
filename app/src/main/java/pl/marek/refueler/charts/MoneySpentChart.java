package pl.marek.refueler.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marek.refueler.R;
import pl.marek.refueler.Services;
import pl.marek.refueler.charts.formatters.CurrencyFormatter;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class MoneySpentChart extends Fragment {
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
        List<Refuel> refuels = Query.all(Refuel.class).get().asList();
        final long minTimestamp = Services.getInstance().getMinTimestamp(refuels);

        List<BarEntry> refuelCost = new ArrayList<>();

        for (Refuel data : refuels) {
            refuelCost.add(new BarEntry(Services.getInstance().dateToFloat(data.getRefuelDate(), minTimestamp), Float.parseFloat(Services.getInstance().multiplyString(data.getPrice(), data.getVolume()))));
        }

        BarDataSet refuelCostDataSet = new BarDataSet(refuelCost, getString(R.string.refuel_cost));
        refuelCostDataSet.setColor(Color.BLUE,100);
        refuelCostDataSet.setValueTextColor(Color.BLUE);
        refuelCostDataSet.setValueTextSize(10f);
        refuelCostDataSet.setValueFormatter(new CurrencyFormatter());
        refuelCostDataSet.setColors(ColorTemplate.COLORFUL_COLORS);


        BarData data = new BarData(refuelCostDataSet);
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
        chart.setFitBars(true);

        chart.invalidate(); // refresh
    }
}
