package finn.academic.voicerecorder.model;

import finn.academic.voicerecorder.TimeAgo;

public class Record {
    private String name;
    private long time;
    private long duration; //in milliseconds
    private TimeAgo timeAgo;

    public Record(String name, long time, long duration) {
        this.name = name;
        this.time = time;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long timeAgo() { return Long.parseLong(timeAgo.getTimeAgo(time));
    }

    public long duration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
