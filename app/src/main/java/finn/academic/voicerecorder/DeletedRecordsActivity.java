package finn.academic.voicerecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import finn.academic.voicerecorder.adapter.RecordAdapter;
import finn.academic.voicerecorder.model.Record;

public class DeletedRecordsActivity extends AppCompatActivity implements RecordAdapter.RecyclerViewClickInterface {
    private RecyclerView recentlyDeletedRecyclerView;
    private ArrayList<Record> records;
    private RecordAdapter adapter;

    private Button editBtn;
    private Button recoverBtn;
    private Button deleteBtn;

    private LinearLayout playLayout;
    private LinearLayout mainPlayLayout;

    private TextView playHeading;;

    private RelativeLayout utilLayout;

    private File[] files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_records);

        SetUp();

        adapter = new RecordAdapter(DeletedRecordsActivity.this, records, files, this);
        recentlyDeletedRecyclerView.setAdapter(adapter);

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
                setSelectButton();
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

    private void setSelectButton() {
        for (Record record : records) {
            if (record.getSelected()) {
                recoverBtn.setText(getResources().getString(R.string.recover));
                deleteBtn.setText(getResources().getString(R.string.delete));
                return;
            }
        }

        recoverBtn.setText(getResources().getString(R.string.recover_all));
        deleteBtn.setText(getResources().getString(R.string.delete_all));
    }

    private void SetUp() {
        recentlyDeletedRecyclerView = findViewById(R.id.recentlyDeletedRecyclerView);

        editBtn = findViewById(R.id.editInDelButton);
        recoverBtn = findViewById(R.id.recoverButton);
        deleteBtn = findViewById(R.id.deleteButton);

        playLayout = findViewById(R.id.playLayout);
        mainPlayLayout = findViewById(R.id.mainPlayLayout);

        playHeading = findViewById(R.id.playHeading);

        utilLayout = findViewById(R.id.deleteUtilsLayout);

        records = new ArrayList<>();

        records = new ArrayList<>();
        String path = getApplicationContext().getExternalFilesDir("/") + "/deletedRecent"; //Get the path of records stored
        File directory = new File(path);
        files = directory.listFiles(); //Get all files from path above
        for (int i = 0; i < files.length; i++)
        {
            records.add(new Record(getApplicationContext(),files[i].getName(), files[i].lastModified(), getAudioFileLength(getApplicationContext().getExternalFilesDir("/")+"/deletedRecent/"+files[i].getName())));
        }

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