package endafarrell.orla.service.data.persistence;

public class PersistenceResults {
    public final boolean ok;
    private int countGiven;
    private int countPersists;
    private String archiveAbsolutePath;

    public boolean isOk() {
        return ok;
    }

    public String getArchiveAbsolutePath() {
        return archiveAbsolutePath;
    }

    public long getArchiveByteSize() {
        return archiveByteSize;
    }

    private long archiveByteSize;

    public PersistenceResults(boolean ok) {
        this.ok = ok;
    }

    public void setCountGiven(int count) {
        this.countGiven = count;
    }

    public int getCountGiven() {
        return this.countGiven;
    }

    public void setCountPersists(int persists) {
        this.countPersists = persists;
    }

    public int getCountPersists() {
        return this.countPersists;
    }

    public void setArchiveAbsolutePath(String archiveAbsolutePath) {
        this.archiveAbsolutePath = archiveAbsolutePath;
    }

    public void setArchiveByteSize(long archiveByteSize) {
        this.archiveByteSize = archiveByteSize;
    }
}
