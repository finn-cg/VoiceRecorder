package finn.academic.voicerecorder.model;

public class Folder {
    private String name;
    private int records;

    public Folder(String name, int records) {
        this.name = name;
        this.records = records;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }
}
