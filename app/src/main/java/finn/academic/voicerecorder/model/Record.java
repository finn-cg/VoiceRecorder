package finn.academic.voicerecorder.model;

import android.content.Context;

import finn.academic.voicerecorder.util.TimeAgo;

public class Record {
    private String name;
    private long time;
    private long duration; //in milliseconds
    private Context context;

    private String path;
    private String folder;

    private Boolean canSelect = false;
    private Boolean isSelected = false;

    public Record(Context context, String name, long time, long duration, String path, String folder) {
        this.context = context;
        this.name = name;
        this.time = time;
        this.duration = duration;
        this.path = path;
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String timeAgo() { return (new TimeAgo()).getTimeAgo(context,time); }

    public long duration() {
        return duration;
    }

    public long getTime() {
        return time;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
