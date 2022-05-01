package finn.academic.voicerecorder;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import finn.academic.voicerecorder.adapter.FolderAdapter;
import finn.academic.voicerecorder.model.Database;
import finn.academic.voicerecorder.model.Folder;
import finn.academic.voicerecorder.receiver.BlockOutgoingCall;
import finn.academic.voicerecorder.receiver.VolumeChangedReceiver;
import finn.academic.voicerecorder.util.FileHandler;
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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.PROCESS_OUTGOING_CALLS}, 1);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_PHONE_STATE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ANSWER_PHONE_CALLS}, 1);
        }
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

        refreshRecords();
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

        txtName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    String name = txtName.getText().toString().trim();

                    if (ListHandler.containsName(folders, name)) {
                        txtAlert.setText(getResources().getString(R.string.existing_name));
                        txtAlert.setTextColor(getResources().getColor(R.color.light_red));
                    } else {
                        database.queryData("INSERT INTO Folder VALUES('" + name + "', 0)");

                        folders.add(new Folder(name, 0));
                        folders = new ArrayList<>(folders);
                        adapter.notifyDataSetChanged();

                        updateVisibilityFolderRecyclerView();

                        dialog.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });

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
                    int max = charSequence.length() - 1;
                    if (charSequence.charAt(max >= 0 ? max : 0) == '\n') {
                        txtName.setText(charSequence.subSequence(0, charSequence.length() - 1));
                        save.performClick();
                        return;
                    }

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
                    String path = view.getContext().getExternalFilesDir("/") + "/" + name;
                    FileHandler.createFolderIfNotExists(path);
                    updateVisibilityFolderRecyclerView();

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
    private void refreshRecords(){
        String path = getApplicationContext().getExternalFilesDir("/") + "/deletedRecent"; //Get the path of records stored
        File directory = new File(path);
        if (directory.listFiles() != null) {
            int delQuantity = directory.listFiles().length;
            deletedQuantity.setText(String.valueOf(delQuantity));
        }

        path = getApplicationContext().getExternalFilesDir("/") + "/default";
        directory = new File(path);

        int recQuantity = 0;

        if (directory.listFiles() != null) {
            recQuantity += directory.listFiles().length;
        }

        database = new Database(this, "folder.sqlite", null, 1);
        Cursor dataFolders = database.getData("SELECT SUM(numRecords) FROM Folder");
        if (dataFolders.moveToNext()) {
            recQuantity += dataFolders.getInt(0);
        }
        Log.d("recQuantity Main", String.valueOf(recQuantity));

        allRecordQuantity.setText(String.valueOf(recQuantity));
    }

    private void updateRecordsDB() {
        Cursor dataFolders = database.getData("SELECT * FROM Folder");
        int i = 0;
        while (dataFolders.moveToNext()) {
            String folderName = dataFolders.getString(0);
            String path = this.getExternalFilesDir("/") + "/" + folderName;
            File directory = new File(path);
            if (directory.listFiles() != null) {
                directory = new File(path);
                int recQuantity = directory.listFiles().length;
                database.queryData("UPDATE Folder SET numRecords=" + String.valueOf(recQuantity) + " WHERE Name='" + folderName + "'");
                Folder folder = new Folder(folderName, recQuantity);
                Log.d("recQuantity", String.valueOf(recQuantity));
                folders.set(i, folder);
            }
            i++;
        }
        adapter.notifyDataSetChanged();
        updateVisibilityFolderRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecordsDB();
        refreshRecords();
    }
}