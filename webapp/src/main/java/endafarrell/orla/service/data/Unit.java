package endafarrell.orla.service.data;

public enum Unit {
    /**
     * mmol/L, used for blood glucose readings
     */
    mmol_L,
    /**
     * grams, used for carbs
     */
    g,
    /**
     * U100 insulin units, used for injections
     */
    IU,
    /**
     * kilometers, used for running distances
     */
    km,
    /**
     * For when there's no value: eg tweets
     */
    none,
    /**
     * percent, used for HbA1C readings
     */
    pct
}