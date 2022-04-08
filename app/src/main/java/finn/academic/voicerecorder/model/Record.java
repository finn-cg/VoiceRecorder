package finn.academic.voicerecorder.model;

import finn.academic.voicerecorder.TimeAgo;

public class Record {
    private String name;
    private long time;
    private long duration; //in milliseconds

    private Boolean canSelect = false;
    private Boolean isSelected = false;

    public Record(String name, long time, long duration) {
        this.name = name;
        this.time = time;
        this.duration = duration;
    }

    public Boolean getCanSelect() {
        return canSelect;
    }

    public void setCanSelect(Boolean canSelect) {
        this.canSelect = canSelect;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String timeAgo() { return (new TimeAgo()).getTimeAgo(time); }

    public long duration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
