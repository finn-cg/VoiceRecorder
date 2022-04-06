package finn.academic.voicerecorder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import finn.academic.voicerecorder.adapter.FolderAdapter;
import finn.academic.voicerecorder.model.Folder;

public class MainActivity extends Activity implements View.OnClickListener {
    RecyclerView folderRecyclerView;
    ArrayList<Folder> folders;
    FolderAdapter adapter;

    LinearLayout allRecords;
    LinearLayout recentlyDeleted;
    RelativeLayout addAFolder;

    Button selectBtn;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetUp();

        adapter = new FolderAdapter(MainActivity.this, folders);
        folderRecyclerView.setAdapter(adapter);

        allRecords.setOnClickListener(this);
        recentlyDeleted.setOnClickListener(this);
        addAFolder.setOnClickListener(this);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void SetUp() {
        folderRecyclerView = findViewById(R.id.folderRecyclerView);
        allRecords = (LinearLayout) findViewById(R.id.allRecordsLayout);
        recentlyDeleted = (LinearLayout) findViewById(R.id.recentlyDeletedLayout);
        selectBtn = findViewById(R.id.select_button);
        addAFolder = (RelativeLayout) findViewById(R.id.addAFolderLayout);

        folders = new ArrayList<>();
        folders.add(new Folder("Ưa thích", 15));
        folders.add(new Folder("Du lịch", 4));
        folders.add(new Folder("Gia đình", 22));
        folders.add(new Folder("Bạn bè", 48));
        folders.add(new Folder("Công việc", 19));
        folders.add(new Folder("Giải trí", 30));
        folders.add(new Folder("Giải trí", 30));
        folders.add(new Folder("Giải trí", 30));
        folders.add(new Folder("Giải trí", 30));
        folders.add(new Folder("Giải trí", 30));
        folders.add(new Folder("Giải trí", 30));
        folders.add(new Folder("Giải trí", 30));
        folders.add(new Folder("Giải trí", 30));
    }

    @Override
    public void onClick(View view) {
        if (allRecords.equals(view)) {
            goToMainStream();
        } else if (recentlyDeleted.equals(view)) {
            Toast.makeText(this, "Go to recently deleted", Toast.LENGTH_SHORT).show();
        } else if (addAFolder.equals(view)) {
            showAddFolderDialog();
        }
    }

    private void goToMainStream() {
        startActivity(new Intent(MainActivity.this, MainStream.class));
    }

    private void showAddFolderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.diaglog_make_change);
        dialog.setCanceledOnTouchOutside(false);

        Button cancel = dialog.findViewById(R.id.cancelDialogMakeChange);
        Button save = dialog.findViewById(R.id.saveDialogMakeChange);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        dialog.show();
    }
}