package endafarrell.orla.service;


public class Triple<T> {
    public final T a;
    public final T b;
    public final T c;

    public Triple(T a, T b, T c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <T> Triple<T> of(T a, T b, T c) {
        return new Triple<T>(a, b, c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        if (a != null ? !a.equals(triple.a) : triple.a != null) return false;
        if (b != null ? !b.equals(triple.b) : triple.b != null) return false;
        if (c != null ? !c.equals(triple.c) : triple.c != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        result = 31 * result + (c != null ? c.hashCode() : 0);
        return result;
    }
}
