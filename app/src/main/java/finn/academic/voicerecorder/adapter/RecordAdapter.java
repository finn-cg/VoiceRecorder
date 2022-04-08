package finn.academic.voicerecorder.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import finn.academic.voicerecorder.MainStream;
import finn.academic.voicerecorder.R;
import finn.academic.voicerecorder.model.Folder;
import finn.academic.voicerecorder.model.Record;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Record> records;

    public RecordAdapter(Context context, ArrayList<Record> records) {
        this.context = context;
        this.records = records;
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
        holder.durationRecord.setText(String.valueOf(record.duration()));

        if (record.getCanSelect()) {
            holder.selectFolderBox.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_enter);
            holder.selectFolderBox.startAnimation(animation);
            holder.mainRowRecordLayout.startAnimation(animation);
        } else {
            holder.selectFolderBox.setVisibility(View.GONE);
            holder.mainRowRecordLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_exit));
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

                Button recoverBtn = ((Activity) context).findViewById(R.id.recoverInDelButton);
                Button deleteBtn = ((Activity) context).findViewById(R.id.deleteInDelButton);

                for (Record record : records) {
                    if (record.getSelected()) {
                        recoverBtn.setText(context.getResources().getString(R.string.recover));
                        deleteBtn.setText(context.getResources().getString(R.string.delete));
                        return;
                    }
                }

                recoverBtn.setText(context.getResources().getString(R.string.recover_all));
                deleteBtn.setText(context.getResources().getString(R.string.delete_all));
            }
        });

        holder.mainRowRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (record.getSelected()) {
                    holder.selectFolderBox.setChecked(false);
                } else {
                    holder.selectFolderBox.setChecked(true);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
}
