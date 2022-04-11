package finn.academic.voicerecorder;

import android.content.Context;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public String getTimeAgo(Context context, long duration) {
        Date now = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - duration);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - duration);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - duration);

        if (seconds < 60) {
            return context.getResources().getString(R.string.justNow);
        }
        else if (minutes == 1) {
            return context.getResources().getString(R.string.aMinAgo);
        }
        else if (minutes > 1 && minutes < 60) {
            return minutes + " " + context.getResources().getString(R.string.manyMinAgo);
        }
        else if (hours == 1) {
            return context.getResources().getString(R.string.anHourAgo);
        }
        else if (hours > 1 && hours < 24) {
            return hours + " " + context.getResources().getString(R.string.manyHourAgo);
        }
        else if (days == 1) {
            return context.getResources().getString(R.string.aDayAgo);
        }
        else {
            return "days ago";//days + " " + context.getResources().getString(R.string.manyDayAgo);
        }
    }
}
