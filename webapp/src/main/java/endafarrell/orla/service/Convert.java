package endafarrell.orla.service;

import java.util.List;

public class Convert {
    public static double round(final double value) {
        return round(value,1);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static double[] todoubleArray(List<Double> source) {
        double[] doubles = new double[source.size()];
        int index = 0;
        for (Double d : source) {
            doubles[index++] = d;
        }
        return doubles;
    }

}