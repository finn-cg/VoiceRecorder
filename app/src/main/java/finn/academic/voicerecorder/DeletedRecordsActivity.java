package finn.academic.voicerecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

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

    private TextView playHeading, namePlayingRecord, durationPlayingRecord, startTimePlayingRecord, endTimePlayingRecord;

    private RelativeLayout utilLayout;

    private ImageButton playRecord, rewindRecord, forwardRecord, deletePlayingRecord;
    private SeekBar seekBarRecord;
    private int pos = -1;
    static MediaPlayer mMediaPlayer;
    SharedPreferences sharedPreferences;

    private ArrayList<File> files;
    private ArrayList<File> allFiles;

    private ImageView menuPlayingRecord;

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

        recoverBtn.setOnClickListener(new View.OnClickListener() {
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
            }
        });

        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos >= 0) {
                    play();
                }
            }
        });

        rewindRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos <= 0) {
                    pos = files.size() - 1;
                } else {
                    pos--;
                }

                initPlayer(pos);
            }
        });

        forwardRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos < files.size() - 1) {
                    pos++;
                } else {
                    pos = 0;

                }
                initPlayer(pos);
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
                    searchFieldDel.setText(charSequence.toString().replace("\n", ""));
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

        menuPlayingRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecordDetail();
            }
        });
    }

    private void showRecordDetail() {
        if (pos >= 0) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.fragment_record_detail);

            Window window = dialog.getWindow();
            if (window == null) {
                return;
            }

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView recordName = dialog.findViewById(R.id.recordName);
            TextView recordLength = dialog.findViewById(R.id.recordLength);
            TextView recordCreatedTime = dialog.findViewById(R.id.recordCreatedTime);

            recordName.setText(records.get(pos).getName());
            recordLength.setText(RecordAdapter.formateMilliSeccond(records.get(pos).duration()));

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            String time = df.format(records.get(pos).getTime());
            recordCreatedTime.setText(time);

            dialog.show();
        }
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

    private void updateEmptyElert() {
        if (records.isEmpty()) {
            emptyAlert.setVisibility(View.VISIBLE);
            pos = -1;
        } else {
            emptyAlert.setVisibility(View.GONE);
        }
    }

    private void SetUp() {
        recentlyDeletedRecyclerView = findViewById(R.id.recentlyDeletedRecyclerView);
        sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

        editBtn = findViewById(R.id.editInDelButton);
        recoverBtn = findViewById(R.id.recoverButton);
        deleteBtn = findViewById(R.id.deleteButton);

        searchFieldDel = findViewById(R.id.searchFieldDelete);

        playLayout = findViewById(R.id.playLayout);
        mainPlayLayout = findViewById(R.id.mainPlayLayout);
        emptyAlert = findViewById(R.id.emptyAlert);

        playHeading = findViewById(R.id.playHeading);
        namePlayingRecord = findViewById(R.id.namePlayingRecord);

        durationPlayingRecord = findViewById(R.id.durationPlayingRecord);
        startTimePlayingRecord = findViewById(R.id.startTimePlayingRecord);
        endTimePlayingRecord = findViewById(R.id.endTimePlayingRecord);

        utilLayout = findViewById(R.id.deleteUtilsLayout);

        playRecord = findViewById(R.id.playRecord);
        rewindRecord = findViewById(R.id.rewindRecord);
        forwardRecord = findViewById(R.id.forwardRecord);

        seekBarRecord = findViewById(R.id.seekBarRecord);
        deletePlayingRecord = findViewById(R.id.deletePlayingRecord);

        menuPlayingRecord = findViewById(R.id.menuPlayingRecord);

        updateDataRecords();

    }

    private void initPlayer(final int position) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
            seekBarRecord.setProgress(0);
            String sTime = createTimeLabel(0);
            startTimePlayingRecord.setText(sTime);
            String eTime = createTimeLabel(mMediaPlayer.getDuration());
            endTimePlayingRecord.setText(eTime);
        }

        String sname = files.get(position).getName().replace(".mp3", "").replace(".m4a", "").replace(".wav", "").replace(".m4b", "").replace(".3gp", "").replace("mp4", "");
        namePlayingRecord.setText(sname);

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(files.get(position).getPath())); // create and load mediaplayer with song resources
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String totalTime = createTimeLabel(mMediaPlayer.getDuration());
                durationPlayingRecord.setText(totalTime);
                seekBarRecord.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.start();
                playRecord.setBackgroundResource(R.drawable.red_pause);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Continuous
                if (sharedPreferences.getBoolean("continuous", false)) {
                    int curSongPosition = position;
                    // code to repeat songs until the
                    if (curSongPosition < files.size() - 1) {
                        curSongPosition++;
                        initPlayer(curSongPosition);
                    } else {
                        curSongPosition = 0;
                        initPlayer(curSongPosition);
                    }
                } else {
                    releasePlaySession();
                }

            }
        });

        seekBarRecord.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    seekBarRecord.setProgress(progress);
                    String sTime = createTimeLabel(progress);
                    startTimePlayingRecord.setText(sTime);
                    String eTime = createTimeLabel(mMediaPlayer.getDuration() - progress);
                    endTimePlayingRecord.setText(eTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer.isPlaying())
                    pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!mMediaPlayer.isPlaying())
                    play();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {


                while (mMediaPlayer != null) {
                    try {
//                        Log.i("Thread ", "Thread Called");
                        // create new message to send to handler
                        if (mMediaPlayer.isPlaying()) {
                            Thread.sleep(1000);
                            Message msg = new Message();
                            msg.what = mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(msg);

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Log.i("handler ", "handler called");
            int current_position = msg.what;

            seekBarRecord.setProgress(current_position);
            String sTime = createTimeLabel(current_position);
            String eTime = createTimeLabel(mMediaPlayer.getDuration() - current_position);
            startTimePlayingRecord.setText(sTime);
            endTimePlayingRecord.setText(eTime);

        }
    };

    private void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            playRecord.setBackgroundResource(R.drawable.red_pause);
        } else {
            pause();
        }

    }

    private void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            playRecord.setBackgroundResource(R.drawable.red_play);
        }

    }

    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    private void releasePlaySession() {
        playRecord.setBackgroundResource(R.drawable.red_play);
        namePlayingRecord.setText(getResources().getString(R.string.record_name));
        durationPlayingRecord.setText("00:00");
        seekBarRecord.setMax(0);
        startTimePlayingRecord.setText("00:00");
        endTimePlayingRecord.setText("00:00");
        mainPlayLayout.setVisibility(View.GONE);
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
        for (int i = 0; i < files.size(); i++) {
            Record r = new Record(getApplicationContext(), files.get(i).getName(), files.get(i).lastModified(), getAudioFileLength(path + "/" + files.get(i).getName()), path, "");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return millSecond;
    }

    @Override
    public void onItemClick(int position) {
        initPlayer(position);
        pos = position;
    }
}