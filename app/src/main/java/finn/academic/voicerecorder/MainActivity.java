package finn.academic.voicerecorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import finn.academic.voicerecorder.adapter.FolderAdapter;
import finn.academic.voicerecorder.model.Database;
import finn.academic.voicerecorder.model.Folder;
import finn.academic.voicerecorder.util.ListHandler;

public class MainActivity extends Activity implements View.OnClickListener {
    private RecyclerView folderRecyclerView;
    private ArrayList<Folder> folders;
    private FolderAdapter adapter;

    private LinearLayout allRecords;
    private LinearLayout recentlyDeleted;
    private RelativeLayout addAFolder;

    private Button selectBtn;
    private LinearLayout selectAllLayout;
    private Button selectAllBtn;

    private TextView txtFolder;

    private Animation animation;
    private TextView allRecordQuantity, deletedQuantity;

    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetUp();
        SetUpDB();

        adapter = new FolderAdapter(MainActivity.this, folders);
        folderRecyclerView.setAdapter(adapter);

        allRecords.setOnClickListener(this);
        recentlyDeleted.setOnClickListener(this);
        addAFolder.setOnClickListener(this);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = (String) selectBtn.getText();
                if (txt.equals(getResources().getString(R.string.edit))) {
                    setSelectButton();
                    selectAllLayout.setVisibility(View.VISIBLE);
                    selectAllLayout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.select_all_enter));
                    adapter.showAllSelecting();
                } else if (txt.equals(getResources().getString(R.string.cancel))) {
                    selectBtn.setText(getResources().getString(R.string.edit));
                    selectAllLayout.setVisibility(View.GONE);
                    adapter.hideAllSelecting();
                } else {

                }
            }
        });

        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectAllBtn.getText().equals(getResources().getString(R.string.select_all))) {
                    selectAllBtn.setText(getResources().getString(R.string.unselect_all));
                    adapter.setAllChecked();
                } else {
                    selectAllBtn.setText(getResources().getString(R.string.select_all));
                    adapter.setAllUnchecked();
                }
            }
        });
    }

    private void SetUpDB() {
        database = new Database(this, "folder.sqlite", null, 1);
        database.queryData("CREATE TABLE IF NOT EXISTS Folder(Name TEXT PRIMARY KEY, numRecords INTEGER)");

        folders = new ArrayList<>();

        Cursor dataFolders = database.getData("SELECT * FROM Folder");
        while (dataFolders.moveToNext()) {
            Folder folder = new Folder(dataFolders.getString(0), dataFolders.getInt(1));
            folders.add(folder);
        }

        updateVisibilityFolderRecyclerView();
    }

    public void updateVisibilityFolderRecyclerView() {
        if (folders.size() == 0) {
            txtFolder.setVisibility(View.GONE);
            folderRecyclerView.setVisibility(View.GONE);
            selectBtn.setVisibility(View.GONE);
        } else {
            txtFolder.setVisibility(View.VISIBLE);
            folderRecyclerView.setVisibility(View.VISIBLE);
            selectBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setSelectButton() {
        for (Folder folder : folders) {
            if (folder.getSelected()) {
                selectBtn.setText(getResources().getString(R.string.delete));
                return;
            }
        }
        selectBtn.setText(getResources().getString(R.string.cancel));
    }

    private void SetUp() {
        folderRecyclerView = findViewById(R.id.folderRecyclerView);
        allRecords = (LinearLayout) findViewById(R.id.allRecordsLayout);
        recentlyDeleted = (LinearLayout) findViewById(R.id.recentlyDeletedLayout);
        selectBtn = findViewById(R.id.select_button);
        selectAllLayout = findViewById(R.id.selectAllLayout);
        selectAllBtn = findViewById(R.id.selectAllBtn);
        addAFolder = (RelativeLayout) findViewById(R.id.addAFolderLayout);
        txtFolder = findViewById(R.id.myFolderTxtView);
        allRecordQuantity = (TextView) findViewById(R.id.allRecordQuantity);
        deletedQuantity = (TextView) findViewById(R.id.deletedQuantity);

        String path = getApplicationContext().getExternalFilesDir("/") + "/deletedRecent"; //Get the path of records stored
        File directory = new File(path);
        if (directory.listFiles() != null) {
            int delQuantity = directory.listFiles().length;
            path = getApplicationContext().getExternalFilesDir("/").getAbsolutePath();
            directory = new File(path);
            int recQuantity = directory.listFiles().length;

            allRecordQuantity.setText(String.valueOf(recQuantity));
            deletedQuantity.setText(String.valueOf(delQuantity));
        }

        // folders = new ArrayList<>();
        // folders.add(new Folder("Ưa thích", 15));
        // folders.add(new Folder("Du lịch", 4));
        // folders.add(new Folder("Gia đình", 22));
        // folders.add(new Folder("Bạn bè", 48));
        // folders.add(new Folder("Công việc", 19));
        // folders.add(new Folder("Giải trí", 30));
        // folders.add(new Folder("Giải trí", 30));
        // folders.add(new Folder("Giải trí", 30));
        // folders.add(new Folder("Giải trí", 30));
        // folders.add(new Folder("Giải trí", 30));
        // folders.add(new Folder("Giải trí", 30));
        // folders.add(new Folder("Giải trí", 30));
        // folders.add(new Folder("Giải trí", 30));
    }

    @Override
    public void onClick(View view) {
        if (allRecords.equals(view)) {
            goToMainStream();
        } else if (recentlyDeleted.equals(view)) {
            goToRecentlyDelete();
        } else if (addAFolder.equals(view)) {
            showAddFolderDialog();
        }
    }

    private void goToMainStream() {
        startActivity(new Intent(MainActivity.this, MainStream.class));
    }

    private void goToRecentlyDelete() {
        startActivity(new Intent(MainActivity.this, DeletedRecordsActivity.class));
    }

    private void showAddFolderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.diaglog_make_change);
        dialog.setCanceledOnTouchOutside(false);

        Button cancel = dialog.findViewById(R.id.cancelDialogMakeChange);
        Button save = dialog.findViewById(R.id.saveDialogMakeChange);

        TextView txtAlert = dialog.findViewById(R.id.descriptionDialogMakeChange);
        EditText txtName = dialog.findViewById(R.id.newName);

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty() || charSequence.toString().equals("")) {
                    save.setEnabled(false);
                    save.setAlpha(0.2f);
                } else {
                    save.setEnabled(true);
                    save.setAlpha(1.0f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txtName.getText().toString().trim();

                if (ListHandler.containsName(folders, name)) {
                    txtAlert.setText(getResources().getString(R.string.existing_name));
                    txtAlert.setTextColor(getResources().getColor(R.color.light_red));
                } else {
                    database.queryData("INSERT INTO Folder VALUES('" + name + "', 0)");

                    folders.add(new Folder(name, 0));
                    adapter.notifyDataSetChanged();

                    updateVisibilityFolderRecyclerView();

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}