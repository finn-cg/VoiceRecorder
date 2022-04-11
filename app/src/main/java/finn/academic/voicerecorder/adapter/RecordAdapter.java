package finn.academic.voicerecorder.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import finn.academic.voicerecorder.R;
import finn.academic.voicerecorder.model.Record;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Record> records;
    private File[] files;

    public RecordAdapter(Context context, ArrayList<Record> records, File[] files) {
        this.context = context;
        this.records = records;
        this.files = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.custom_recyclerview_for_records,
                                parent, false)
        );

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.nameRecord.setText(record.getName());
        holder.timeRecord.setText(record.timeAgo());
        holder.durationRecord.setText(formateMilliSeccond(record.duration()));

        if (record.getCanSelect()) {
            holder.selectFolderBox.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_enter);
            holder.selectFolderBox.startAnimation(animation);
            holder.mainRowRecordLayout.startAnimation(animation);
        } else {
            holder.selectFolderBox.setVisibility(View.GONE);
//            holder.mainRowRecordLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_exit));
        }

        if (record.getSelected()) {
            holder.selectFolderBox.setChecked(true);
        } else {
            holder.selectFolderBox.setChecked(false);
        }

        holder.selectFolderBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                record.setSelected(b);

                Button recoverBtn = ((Activity) context).findViewById(R.id.recoverButton);
                Button deleteBtn = ((Activity) context).findViewById(R.id.deleteButton);

                for (Record record : records) {
                    if (record.getSelected()) {
                        if (recoverBtn != null) {
                            recoverBtn.setText(context.getResources().getString(R.string.recover));
                        }
                        deleteBtn.setText(context.getResources().getString(R.string.delete));
                        return;
                    }
                }

                if (recoverBtn != null) {
                    recoverBtn.setText(context.getResources().getString(R.string.recover_all));
                }
                deleteBtn.setText(context.getResources().getString(R.string.delete_all));
            }
        });

        holder.mainRowRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.selectFolderBox.getVisibility() == View.VISIBLE) {
                    if (record.getSelected()) {
                        holder.selectFolderBox.setChecked(false);
                    } else {
                        holder.selectFolderBox.setChecked(true);
                    }
                } else {
                    LinearLayout playLayout = ((Activity) context).findViewById(R.id.playLayout);
                    LinearLayout mainPlayLayout = ((Activity) context).findViewById(R.id.mainPlayLayout);

                    mainPlayLayout.setVisibility(View.VISIBLE);
                    playLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show_play));
                }
            }
        });

    }

    public void showAllSelecting() {
        for (Record record : records) {
            record.setCanSelect(true);
        }
        this.notifyDataSetChanged();
    }

    public void hideAllSelecting() {
        for (Record record : records) {
            record.setCanSelect(false);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameRecord;
        TextView timeRecord;
        TextView durationRecord;

        CheckBox selectFolderBox;
        LinearLayout mainRowRecordLayout;

        public ViewHolder(@NonNull View recordView) {
            super(recordView);

            nameRecord = recordView.findViewById(R.id.nameRecord);
            timeRecord = recordView.findViewById(R.id.timeRecord);
            durationRecord = recordView.findViewById(R.id.durationRecord);

            selectFolderBox = recordView.findViewById(R.id.selectFolderBox);
            mainRowRecordLayout = recordView.findViewById(R.id.mainRowRecordLayout);

        }

    }

    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    public interface RecyclerViewClickInterface {
        public void onItemClick(int position);
    }
}
