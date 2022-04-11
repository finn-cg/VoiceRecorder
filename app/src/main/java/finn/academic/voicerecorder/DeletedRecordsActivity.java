package finn.academic.voicerecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

public class DeletedRecordsActivity extends AppCompatActivity {
    RecyclerView recentlyDeletedRecyclerView;
    ArrayList<Record> records;
    RecordAdapter adapter;

    Button editBtn;
    Button recoverBtn;
    Button deleteBtn;

    LinearLayout playLayout;
    LinearLayout mainPlayLayout;

    TextView playHeading;;

    RelativeLayout utilLayout;

    File[] files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_records);

        SetUp();

        adapter = new RecordAdapter(DeletedRecordsActivity.this, records, files, (RecordAdapter.RecyclerViewClickInterface) this);
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
        String path = getApplicationContext().getExternalFilesDir("/").getAbsolutePath(); //Get the path of records stored
        File directory = new File(path);
        files = directory.listFiles(); //Get all files from path above
/*        records.add(new Record("Record 1", 0, 360));
        records.add(new Record("Record 2", 5, 123));
        records.add(new Record("Record 3", 360, 20));
        records.add(new Record("Record 4", 255, 35));
        records.add(new Record("Record 5", 16, 100));
        records.add(new Record("Record 6", 90, 80));
        records.add(new Record("Record 7", 120, 55));
        records.add(new Record("Record 8", 100, 44));
        records.add(new Record("Record 9", 1000, 33));
        records.add(new Record("Record 10", 600, 88));
        records.add(new Record("Record 11", 530, 99));
        records.add(new Record("Record 12", 30, 120));*/
    }
}