package finn.academic.voicerecorder.adapter;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import finn.academic.voicerecorder.R;
import finn.academic.voicerecorder.model.Record;
import finn.academic.voicerecorder.util.FileHandler;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Record> records;
    private ArrayList<File> files;
    private RecyclerViewClickInterface recyclerViewClickInterface;

    private ArrayList<Record> deletedRecords;
    private ArrayList<File> deletedFiles;

    public RecordAdapter(Context context, ArrayList<Record> records, ArrayList<File> files,
                         RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.records = records;
        this.files = files;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
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
                    recyclerViewClickInterface.onItemClick(holder.getAdapterPosition());
                    playLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show_play));
                }
            }
        });
    }

    private Boolean deleteRecord(int position) {
        File curDirectory = files.get(position);
        String path = context.getExternalFilesDir("/") + "/deletedRecent";
        FileHandler.createFolderIfNotExists(path);
        File destDirectory = new File(path);
        try {
            File newFile =
                    new File(context.getExternalFilesDir("/") +
                            "/" + records.get(position).getFolder() +
                            "/" + FileHandler.getDeleteName(curDirectory, records.get(position).getFolder()));
            FileHandler.rename(curDirectory, newFile);
            FileHandler.moveFile(newFile, destDirectory);

            deletedFiles.add(files.get(position));
            deletedRecords.add(records.get(position));

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Boolean permanentlyDeleteRecord(int position) {
        File curDirectory = files.get(position);
        deletedFiles.add(files.get(position));
        deletedRecords.add(records.get(position));

        return curDirectory.delete();
    }

    public Boolean deleteAllSelected(Boolean permanent) {
        Boolean res = true;

        deletedFiles = new ArrayList<>();
        deletedRecords = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getSelected()) {
                res = permanent ? permanentlyDeleteRecord(i) : deleteRecord(i);
            }
        }
        files.removeAll(deletedFiles);
        records.removeAll(deletedRecords);

        notifyDataSetChanged();

        return res;
    }

    public Boolean deleteAllRecords(Boolean permanent) {
        Boolean res = true;

        deletedFiles = new ArrayList<>();
        deletedRecords = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            res = permanent ? permanentlyDeleteRecord(i) : deleteRecord(i);
        }
        files.removeAll(deletedFiles);
        records.removeAll(deletedRecords);

        notifyDataSetChanged();

        return res;
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
