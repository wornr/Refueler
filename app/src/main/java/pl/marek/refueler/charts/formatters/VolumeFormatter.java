package pl.marek.refueler.charts.formatters;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

import pl.marek.refueler.Services;

public class VolumeFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public VolumeFormatter() {
        mFormat = new DecimalFormat("###,###,##0.##");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return Services.getInstance().addVolumeUnit(mFormat.format(value));
    }
}