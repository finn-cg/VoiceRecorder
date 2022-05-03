package finn.academic.voicerecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
    private ArrayList<Record> allRecords;
    private RecordAdapter adapter;

    private Button editBtn;
    private Button recoverBtn;
    private Button deleteBtn;

    private EditText searchFieldDel;

    private LinearLayout playLayout;
    private LinearLayout mainPlayLayout;
    private LinearLayout emptyAlert;

    private TextView playHeading;;

    private RelativeLayout utilLayout;

    private ArrayList<File> files;
    private ArrayList<File> allFiles;
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
                showYesNoDialogForDelete();
            }
        });

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showYesNoDialogForRecover();
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

        searchFieldDel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().contains("\n")) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(recentlyDeletedRecyclerView.getWindowToken(), 0);
                    searchFieldDel.setText(charSequence.toString().replace("\n",""));
                } else {
//                Toast.makeText(view.getContext(),
//                        searchField.getText().toString(),
//                        Toast.LENGTH_SHORT).show();
                    searchRecords();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // empty
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataRecords();
    }

    private void searchRecords() {
        records = new ArrayList<>();
        for (Record element : allRecords) {
            if (element.getName().contains(searchFieldDel.getText().toString())) {
                records.add(element);
            }
        }

        files = new ArrayList<>();
        for (File element : allFiles) {
            if (element.getName().contains(searchFieldDel.getText().toString())) {
                files.add(element);
            }
        }

        adapter = new RecordAdapter(this, records, files, DeletedRecordsActivity.this);
        recentlyDeletedRecyclerView.setAdapter(adapter);
    }

    private void showYesNoDialogForDelete() {
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
                    if (adapter.permanentlyDeleteAllRecord(allFiles, allRecords)) {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.per_deleted_all_record_alert), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (adapter.permanentlyDeleteAllSelected(allFiles, allRecords)) {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.per_deleted_record_alert), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }

                deleteBtn.setText(getResources().getString(R.string.delete_all));
                recoverBtn.setText(getResources().getString(R.string.recover_all));
                updateEmptyElert();
                adapter.hideAllSelecting();

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

    private void showYesNoDialogForRecover() {
        final Dialog dialog = new Dialog(DeletedRecordsActivity.this);
        dialog.setContentView(R.layout.dialog_overwrite_record);
        dialog.setCanceledOnTouchOutside(false);

        TextView title = dialog.findViewById(R.id.mainTitleDialogMakeChange);
        TextView description = dialog.findViewById(R.id.descriptionDialogMakeChange);

        title.setText(getResources().getString(R.string.recover_record));
        description.setText(getResources().getString(R.string.recover_description));

        Button no = dialog.findViewById(R.id.noDialogMakeChange);
        Button yes = dialog.findViewById(R.id.yesDialogMakeChange);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recoverBtn.getText().equals(getResources().getString(R.string.recover_all))) {
                    if (adapter.recoverAllRecords(allFiles, allRecords)) {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.recover_all_alert), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (adapter.recoverAllSelected(allFiles, allRecords)) {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.recover_alert), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), view.getContext()
                                .getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }
                updateEmptyElert();
                adapter.hideAllSelecting();

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

    private void updateEmptyElert() {
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

        searchFieldDel = findViewById(R.id.searchFieldDelete);

        playLayout = findViewById(R.id.playLayout);
        mainPlayLayout = findViewById(R.id.mainPlayLayout);
        emptyAlert = findViewById(R.id.emptyAlert);

        playHeading = findViewById(R.id.playHeading);

        utilLayout = findViewById(R.id.deleteUtilsLayout);

        updateDataRecords();

    }

    private void updateDataRecords() {
        records = new ArrayList<>();
        allRecords = new ArrayList<>();
        String path = getApplicationContext().getExternalFilesDir("/") + "/deletedRecent"; //Get the path of records stored
        FileHandler.createFolderIfNotExists(path);
        File directory = new File(path);
        files = new ArrayList<>();
        allFiles = new ArrayList<>();
        Collections.addAll(files, directory.listFiles()); //Get all files from path above
        Collections.addAll(allFiles, directory.listFiles()); //Get all files from path above
        for (int i = 0; i < files.size(); i++)
        {
            Record r = new Record(getApplicationContext(),files.get(i).getName(), files.get(i).lastModified(), getAudioFileLength(path + "/" +files.get(i).getName()), path, "");
            records.add(r);
            allRecords.add(r);
        }
        updateEmptyElert();

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