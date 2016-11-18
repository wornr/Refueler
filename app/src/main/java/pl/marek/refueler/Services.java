package pl.marek.refueler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.MenuItem;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.marek.refueler.database.Refuel;

public class Services {
    private static Services instance;
    private Context context;

    public static void newInstance(Context context) {
        getInstance().context = context;
    }

    public static Services getInstance() {
        if(instance == null)
            instance = new Services();

        return instance;
    }

    public String multiplyString(String a, String b) {
        return multiplyString(a, b, 2);
    }

    public String multiplyString(String a, String b, int precision) {
        return new BigDecimal(a).multiply(new BigDecimal(b)).setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public BigDecimal multiply(String a, String b) {
        return multiply(a, b, 2);
    }

    public BigDecimal multiply(String a, String b, int precision) {
        return new BigDecimal(a).multiply(new BigDecimal(b)).setScale(precision, BigDecimal.ROUND_HALF_UP);
    }

    public String divideString(String a, String b) {
        return divideString(a, b, 2);
    }

    public String divideString(String a, String b, int precision) {
        return new BigDecimal(a).divide(new BigDecimal(b), BigDecimal.ROUND_HALF_UP).setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public BigDecimal divide(String a, String b) {
        return divide(a, b, 2);
    }

    public BigDecimal divide(String a, String b, int precision) {
        return new BigDecimal(a).divide(new BigDecimal(b), BigDecimal.ROUND_HALF_UP).setScale(precision, BigDecimal.ROUND_HALF_UP);
    }

    public String addCurrencyUnit(String value) {
        String currencyUnit = PreferenceManager.getDefaultSharedPreferences(context).getString("currency_unit_preference", null);
        return value + " " + currencyUnit;
    }

    public String addVolumeUnit(String value) {
        String volumeUnit = PreferenceManager.getDefaultSharedPreferences(context).getString("volume_unit_preference", null);
        return value + " " + volumeUnit;
    }

    public String addDistanceUnit(String value) {
        String distanceUnit = PreferenceManager.getDefaultSharedPreferences(context).getString("distance_unit_preference", null);
        return value + " " + distanceUnit;
    }

    public String addCurrencyByVolumeUnit(String value) {
        String currencyUnit = PreferenceManager.getDefaultSharedPreferences(context).getString("currency_unit_preference", null);
        String volumeUnit = PreferenceManager.getDefaultSharedPreferences(context).getString("volume_unit_preference", null);
        return value + " " + currencyUnit + "/" + volumeUnit;
    }

    private Date parseDate(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        Date date = null;
        try {
            date = sdf.parse(stringDate);
        } catch(Exception ignored) {}

        return date;
    }

    public float dateToFloat(String stringDate, long minTimestamp) {
        Date date = parseDate(stringDate);
        return ((float)(date.getTime() - minTimestamp))/86400000;
    }

    public String floatToDate(float floatDate, long minTimestamp) {
        Date date = new Date();
        date.setTime(((long)(floatDate * 86400000)) + minTimestamp);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + (month < 9 ? "0" + (month + 1) : (month + 1)) + "-" + (day < 10 ? "0" + day : day);
    }

    public long getMinTimestamp(List<Refuel> refuels) {
        if(!refuels.isEmpty()) {
            long minTimestamp = parseDate(refuels.get(0).getRefuelDate()).getTime();

            for (Refuel refuel : refuels) {
                System.out.println(dateToFloat(refuel.getRefuelDate(), 0));
                minTimestamp = (minTimestamp < parseDate(refuel.getRefuelDate()).getTime() ? minTimestamp : parseDate(refuel.getRefuelDate()).getTime());
            }

            return minTimestamp;
        } else
            return 0;
    }

    public Date stringToDate(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        Date date = new Date();
        try {
            date = sdf.parse(stringDate);
        } catch(Exception ignored) {}

        return date;
    }

    public int getYear(String stringDate) {
        Date date = stringToDate(stringDate);

        return getYear(date);
    }

    public int getMonth(String stringDate) {
        Date date = stringToDate(stringDate);

        return getMonth(date);
    }

    public int getDay(String stringDate) {
        Date date = stringToDate(stringDate);

        return getDay(date);
    }

    public int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }

    public int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.MONTH);
    }

    public int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String getFormatDate(Date date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-M-dd";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public String getFormatDate(String date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-M-dd";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(stringToDate(date));
    }

    public void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

        item.setIcon(wrapDrawable);
    }
}
