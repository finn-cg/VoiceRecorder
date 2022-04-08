package finn.academic.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Set;

import finn.academic.voicerecorder.adapter.RecordAdapter;
import finn.academic.voicerecorder.model.Record;

public class DeletedRecordsActivity extends AppCompatActivity {
    RecyclerView recentlyDeletedRecyclerView;
    ArrayList<Record> records;
    RecordAdapter adapter;

    Button editBtn;
    Button recoverBtn;
    Button deleteBtn;

    RelativeLayout utilLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_records);

        SetUp();

        adapter = new RecordAdapter(DeletedRecordsActivity.this, records);
        recentlyDeletedRecyclerView.setAdapter(adapter);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editBtn.getText().equals(getResources().getString(R.string.edit))) {
                    editBtn.setText(getResources().getString(R.string.cancel));

                    utilLayout.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(DeletedRecordsActivity.this, R.anim.glide_up);
                    utilLayout.startAnimation(animation);

                    adapter.showAllSelecting();
                } else {
                    editBtn.setText(getResources().getString(R.string.edit));
                    utilLayout.setVisibility(View.GONE);

                    adapter.hideAllSelecting();
                }
                setSelectButton();
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
        recoverBtn = findViewById(R.id.recoverInDelButton);
        deleteBtn = findViewById(R.id.deleteInDelButton);

        utilLayout = findViewById(R.id.deleteUtilsLayout);

        records = new ArrayList<>();
        records.add(new Record("Record 1", 0, 360));
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
        records.add(new Record("Record 12", 30, 120));
    }
}