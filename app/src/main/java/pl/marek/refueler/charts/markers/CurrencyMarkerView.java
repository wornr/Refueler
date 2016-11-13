package pl.marek.refueler.charts.markers;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

import pl.marek.refueler.R;
import pl.marek.refueler.Services;

public class CurrencyMarkerView extends MarkerView {

    private TextView marker;

    public CurrencyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        marker = (TextView) findViewById(R.id.marker);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        DecimalFormat mFormat = new DecimalFormat("###,###,##0.##");

        marker.setText(Services.getInstance().addCurrencyUnit(mFormat.format(e.getY())));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
