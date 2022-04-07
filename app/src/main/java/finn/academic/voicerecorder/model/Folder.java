package finn.academic.voicerecorder.model;

public class Folder {
    private String name;
    private int records;
    private Boolean canSelect = false;
    private Boolean isSelected = false;

    public Folder(String name, int records) {
        this.name = name;
        this.records = records;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getCanSelect() {
        return canSelect;
    }

    public void setCanSelect(Boolean selected) {
        canSelect = selected;
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
