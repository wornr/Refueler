package pl.marek.refueler.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marek.refueler.R;
import pl.marek.refueler.Services;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class ChartsFragment extends Fragment {
    @Bind(R.id.chart)
    LineChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.charts_fragment, container, false);
        ButterKnife.bind(this, v);

        initView();

        return v;
    }

    private Date parseDate(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        Date date = null;
        try {
            date = sdf.parse(stringDate);
        } catch(Exception ignored) {}

        return date;
    }

    private float dateToFloat(String stringDate, long minTimestamp) {
        Date date = parseDate(stringDate);
        return (float)(date.getTime() - minTimestamp);
    }

    private String floatToDate(float floatDate, long minTimestamp) {
        Date date = new Date();
        date.setTime((long)(floatDate + minTimestamp));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + (month < 9 ? "0" + (month + 1) : (month + 1)) + "-" + (day < 10 ? "0" + day : day);
    }

    private long getMinTimestamp(List<Refuel> refuels) {
        long minTimestamp = 0;

        for(Refuel refuel : refuels) {
            minTimestamp = (minTimestamp < parseDate(refuel.getRefuelDate()).getTime() ?  minTimestamp : parseDate(refuel.getRefuelDate()).getTime());
        }

        return minTimestamp;
    }

    private void initView() {
        List<Refuel> refuels = Query.all(Refuel.class).get().asList();
        final long minTimestamp = getMinTimestamp(refuels);

/*        List<Entry> refuelPrice = new ArrayList<>();*/
        List<Entry> refuelVolume = new ArrayList<>();
        List<Entry> refuelCost = new ArrayList<>();

        for (Refuel data : refuels) {
/*            refuelPrice.add(new Entry(dateToFloat(data.getRefuelDate(), minTimestamp), Float.parseFloat(data.getPrice())));*/
            refuelVolume.add(new Entry(dateToFloat(data.getRefuelDate(), minTimestamp), Float.parseFloat(data.getVolume())));
            refuelCost.add(new Entry(dateToFloat(data.getRefuelDate(), minTimestamp), Float.parseFloat(Services.getInstance().multiplyString(data.getPrice(), data.getVolume()))));
        }

/*        LineDataSet refuelPriceDataSet = new LineDataSet(refuelPrice, "Cena paliwa");
        refuelPriceDataSet.setColor(Color.GREEN,100);
        refuelPriceDataSet.setValueTextColor(Color.GREEN);
        refuelPriceDataSet.setValueTextSize(10f);*/

        LineDataSet refuelVolumeDataSet = new LineDataSet(refuelVolume, getString(R.string.refueled));
        refuelVolumeDataSet.setColor(Color.RED,100);
        refuelVolumeDataSet.setValueTextColor(Color.RED);
        refuelVolumeDataSet.setValueTextSize(10f);
        refuelVolumeDataSet.setValueFormatter(new VolumeFormatter());

        LineDataSet refuelCostDataSet = new LineDataSet(refuelCost, getString(R.string.refuel_cost));
        refuelCostDataSet.setColor(Color.BLUE,100);
        refuelCostDataSet.setValueTextColor(Color.BLUE);
        refuelCostDataSet.setValueTextSize(10f);
        refuelCostDataSet.setValueFormatter(new CurrencyFormatter());

        List<ILineDataSet> dataSets = new ArrayList<>();
/*        dataSets.add(refuelPriceDataSet);*/
        dataSets.add(refuelVolumeDataSet);
        dataSets.add(refuelCostDataSet);

        LineData data = new LineData(dataSets);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return floatToDate(value, minTimestamp);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);

        chart.invalidate(); // refresh
    }

    public static class CurrencyFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public CurrencyFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + " zł";
        }
    }

    public static class VolumeFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public VolumeFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + " l";
        }
    }
}
