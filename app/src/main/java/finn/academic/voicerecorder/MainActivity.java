package finn.academic.voicerecorder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import finn.academic.voicerecorder.adapter.FolderAdapter;
import finn.academic.voicerecorder.model.Folder;

public class MainActivity extends Activity implements View.OnClickListener {
    ListView folderListView;
    ArrayList<Folder> folders;
    FolderAdapter adapter;

    LinearLayout allRecords;
    LinearLayout recentlyDeleted;
    RelativeLayout addAFolder;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetUp();

        adapter = new FolderAdapter(this, R.layout.custom_listview_in_main, folders);
        folderListView.setAdapter(adapter);

        allRecords.setOnClickListener(this);
        recentlyDeleted.setOnClickListener(this);
        addAFolder.setOnClickListener(this);
    }

    private void SetUp() {
        folderListView = (ListView) findViewById(R.id.folderListView);
        allRecords = (LinearLayout) findViewById(R.id.allRecordsLayout);
        recentlyDeleted = (LinearLayout) findViewById(R.id.recentlyDeletedLayout);
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
        overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
    }

    private void showAddFolderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.diaglog_add_folder);
        dialog.setCanceledOnTouchOutside(false);

        Button cancel = dialog.findViewById(R.id.cancelAddFolder);
        Button save = dialog.findViewById(R.id.saveNewFolder);

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