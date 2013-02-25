package endafarrell.orla.service;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class OrlaDateTimeFormat {

    /**
     * A DateTimeFormat for the pattern "yyyyMMdd'T'HHmmss" which is suitable for archiving on disk.
     */
    public static final DateTimeFormatter ARCHIVER_yyyyMMddTHHmmss = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss");
    public static final DateTimeFormatter JSON_yyyyMMddHHmmssSSSZ = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
    public static final DateTimeFormatter PRETTY_yyyyMMdd = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter PRETTY_DAY_EEE = DateTimeFormat.forPattern("EEE");
    public static final DateTimeFormatter PRETTY_HHmm = DateTimeFormat.forPattern("HH:mm");


//    OrlaDateTimeFormat() {
//        // Note that the ARCHIVER_yyyyMMddTHHmmss is NOT set to UTC to help the filename to stay as expected
//        PRETTY_yyyyMMdd.withZoneUTC();
//        JSON_yyyyMMddHHmmssSSSZ.withZoneUTC();
//        PRETTY_DAY_EEE.withZoneUTC();
//    }
}
