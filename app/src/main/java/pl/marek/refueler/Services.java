package pl.marek.refueler;

import java.math.BigDecimal;

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
}
