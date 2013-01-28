package endafarrell.orla.service.data;


public final class EventFactory {
    public static Event create(String kvkey, String clazz, String kvvalue) {
        if (BloodGlucoseEvent.class.getSimpleName().equals(clazz)) {
            return BloodGlucoseEvent.factory(kvkey, kvvalue);
        } else if (CarbEvent.class.getSimpleName().equals(clazz)) {
            return CarbEvent.factory(kvkey, kvvalue);
        } else if (PumpBasalEvent.class.getSimpleName().equals(clazz)) {
            return PumpBasalEvent.factory(kvkey, kvvalue);
        } else if (PumpBolusEvent.class.getSimpleName().equals(clazz)) {
            return PumpBolusEvent.factory(kvkey, kvvalue);
        } else if (PumpDailyDoseEvent.class.getSimpleName().equals(clazz)) {
            return PumpDailyDoseEvent.factory(kvkey, kvvalue);
        } else if (PumpEvent.class.getSimpleName().equals(clazz)) {
            return PumpEvent.factory(kvkey, kvvalue);
        } else if (TwitterEvent.class.getSimpleName().equals(clazz)) {
            return TwitterEvent.factory(kvkey, kvvalue);
        } else {
            throw new UnknownError("Class \"" + clazz + "\" is unknown.");
        }
    }
}
