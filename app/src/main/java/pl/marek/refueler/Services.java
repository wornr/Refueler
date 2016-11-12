package pl.marek.refueler;

import android.widget.EditText;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pl.marek.refueler.database.Refuel;

public class Services {
    private static Services instance;

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
        String currencyUnit = "zł";
        return value + " " + currencyUnit;
    }

    public String addVolumeUnit(String value) {
        String volumeUnit = "l";
        return value + " " + volumeUnit;
    }

    public String addDistanceUnit(String value) {
        String distanceUnit = "km";
        return value + " " + distanceUnit;
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
        return (float)(date.getTime() - minTimestamp);
    }

    public String floatToDate(float floatDate, long minTimestamp) {
        Date date = new Date();
        date.setTime((long)(floatDate + minTimestamp));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + (month < 9 ? "0" + (month + 1) : (month + 1)) + "-" + (day < 10 ? "0" + day : day);
    }

    public long getMinTimestamp(List<Refuel> refuels) {
        long minTimestamp = 0;

        for(Refuel refuel : refuels) {
            minTimestamp = (minTimestamp < parseDate(refuel.getRefuelDate()).getTime() ?  minTimestamp : parseDate(refuel.getRefuelDate()).getTime());
        }

        return minTimestamp;
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
}
