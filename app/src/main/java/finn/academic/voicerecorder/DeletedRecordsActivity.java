package finn.academic.voicerecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import finn.academic.voicerecorder.adapter.RecordAdapter;
import finn.academic.voicerecorder.model.Record;
import finn.academic.voicerecorder.util.FileHandler;

public class DeletedRecordsActivity extends AppCompatActivity implements RecordAdapter.RecyclerViewClickInterface {
    private RecyclerView recentlyDeletedRecyclerView;
    private ArrayList<Record> records;
    private RecordAdapter adapter;

    private Button editBtn;
    private Button recoverBtn;
    private Button deleteBtn;

    private LinearLayout playLayout;
    private LinearLayout mainPlayLayout;
    private LinearLayout emptyAlert;

    private TextView playHeading;;

    private RelativeLayout utilLayout;

    private ArrayList<File> files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_records);

        SetUp();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editBtn.getText().equals(getResources().getString(R.string.edit))) {
                    editBtn.setText(getResources().getString(R.string.cancel));

                    utilLayout.setVisibility(View.VISIBLE);
                    utilLayout.startAnimation(AnimationUtils.loadAnimation(DeletedRecordsActivity.this, R.anim.glide_up));

                    adapter.showAllSelecting();
                } else {
                    editBtn.setText(getResources().getString(R.string.edit));
                    utilLayout.startAnimation(AnimationUtils.loadAnimation(DeletedRecordsActivity.this, R.anim.hide_glide_down));
                    utilLayout.setVisibility(View.GONE);

                    adapter.hideAllSelecting();
                }
                mainPlayLayout.setVisibility(View.GONE);
            }
        });

        playHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainPlayLayout.getVisibility() != View.VISIBLE) {
                    mainPlayLayout.setVisibility(View.VISIBLE);
                    playLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.show_play));
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showYesNoDialog();
            }
        });

        recentlyDeletedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mainPlayLayout.getVisibility() != View.GONE) {
                    mainPlayLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataRecords();
    }

    private void showYesNoDialog() {
        final Dialog dialog = new Dialog(DeletedRecordsActivity.this);
        dialog.setContentView(R.layout.dialog_overwrite_record);
        dialog.setCanceledOnTouchOutside(false);

        TextView title = dialog.findViewById(R.id.mainTitleDialogMakeChange);
        TextView description = dialog.findViewById(R.id.descriptionDialogMakeChange);

        title.setText(getResources().getString(R.string.delete_record));
        description.setText(getResources().getString(R.string.permanently_deleted));

        Button no = dialog.findViewById(R.id.noDialogMakeChange);
        Button yes = dialog.findViewById(R.id.yesDialogMakeChange);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteBtn.getText().equals(view.getContext().getString(R.string.delete_all))) {
                    if (adapter.deleteAllRecords(true)) {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.per_deleted_all_record_alert), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (adapter.deleteAllSelected(true)) {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.per_deleted_record_alert), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }

                deleteBtn.setText(getResources().getString(R.string.delete_all));
                recoverBtn.setText(getResources().getString(R.string.recover_all));
                updateEmtyElert();

                if (records.size() == 0 || records.isEmpty()) {
                    editBtn.performClick();
                }

                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateEmtyElert() {
        if (records.isEmpty()) {
            emptyAlert.setVisibility(View.VISIBLE);
        } else {
            emptyAlert.setVisibility(View.GONE);
        }
    }

    private void SetUp() {
        recentlyDeletedRecyclerView = findViewById(R.id.recentlyDeletedRecyclerView);

        editBtn = findViewById(R.id.editInDelButton);
        recoverBtn = findViewById(R.id.recoverButton);
        deleteBtn = findViewById(R.id.deleteButton);

        playLayout = findViewById(R.id.playLayout);
        mainPlayLayout = findViewById(R.id.mainPlayLayout);
        emptyAlert = findViewById(R.id.emptyAlert);

        playHeading = findViewById(R.id.playHeading);

        utilLayout = findViewById(R.id.deleteUtilsLayout);

        updateDataRecords();

    }

    private void updateDataRecords() {
        records = new ArrayList<>();
        String path = getApplicationContext().getExternalFilesDir("/") + "/deletedRecent"; //Get the path of records stored
        FileHandler.createFolderIfNotExists(path);
        File directory = new File(path);
        files = new ArrayList<>();
        Collections.addAll(files, directory.listFiles()); //Get all files from path above
        for (int i = 0; i < files.size(); i++)
        {
            records.add(new Record(getApplicationContext(),files.get(i).getName(), files.get(i).lastModified(), getAudioFileLength(path + "/" +files.get(i).getName()), path, ""));
        }
        updateEmtyElert();

        adapter = new RecordAdapter(DeletedRecordsActivity.this, records, files, this);
        recentlyDeletedRecyclerView.setAdapter(adapter);
    }

    public long getAudioFileLength(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        long millSecond = 0;
        try {
            Uri uri = Uri.parse(path);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(getApplicationContext(), uri);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            millSecond = Long.parseLong(duration);

        }catch (Exception e){
            e.printStackTrace();
        }
        return millSecond;
    }

    @Override
    public void onItemClick(int position) {
    }
}