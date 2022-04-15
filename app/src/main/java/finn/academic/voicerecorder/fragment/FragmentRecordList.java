package finn.academic.voicerecorder.fragment;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Objects;

import finn.academic.voicerecorder.MainActivity;
import finn.academic.voicerecorder.MainStream;
import finn.academic.voicerecorder.R;
import finn.academic.voicerecorder.adapter.RecordAdapter;
import finn.academic.voicerecorder.model.Record;

public class FragmentRecordList extends Fragment implements RecordAdapter.RecyclerViewClickInterface {
    private View view;

    private RecyclerView recordsRecyclerView;
    private ArrayList<Record> records;
    private RecordAdapter adapter;

    private RelativeLayout utilRecordLayout;
    private RelativeLayout selectRecordLayout;
    private RelativeLayout searchRecordLayout;
    private RelativeLayout utilLayout;

    private LinearLayout searchRecordBar;
    private LinearLayout playLayout;
    private LinearLayout mainPlayLayout;

    private TextView playHeading, namePlayingRecord, durationPlayingRecord, startTimePlayingRecord, endTimePlayingRecord;
    private SeekBar seekBarRecord;
    private ImageButton playRecord, rewindRecord, forwardRecord, deletePlayingRecord;

    private Button cancelCheckButton;

    private Boolean isShowUtils = false;

    private File[] files;
    private String path = "";
    static MediaPlayer mMediaPlayer;
    private int pos;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record_list, container, false);

        SetUp();
//        initPlayer(0);

        adapter = new RecordAdapter(view.getContext(), records, files, (RecordAdapter.RecyclerViewClickInterface) this);
        recordsRecyclerView.setAdapter(adapter);


        utilRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowUtils) {
                    isShowUtils = false;
                    selectRecordLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_glide_down));
                    searchRecordLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_search_trans));
                    searchRecordLayout.setVisibility(View.GONE);
                    selectRecordLayout.setVisibility(View.GONE);
                } else {
                    isShowUtils = true;
                    searchRecordLayout.setVisibility(View.VISIBLE);
                    selectRecordLayout.setVisibility(View.VISIBLE);
                    selectRecordLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.glide_up));
                    searchRecordLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.show_search_trans));
                }
            }
        });

        selectRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPlayLayout.setVisibility(View.GONE);
                utilLayout.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.glide_up);
                utilLayout.startAnimation(animation);
                adapter.showAllSelecting();

                selectRecordLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_glide_down));
                searchRecordLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_search_trans));
                selectRecordLayout.setVisibility(View.GONE);
                searchRecordLayout.setVisibility(View.GONE);

                isShowUtils = false;
            }
        });

        searchRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRecordBar.setVisibility(View.VISIBLE);
                searchRecordBar.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.glide_down));

                selectRecordLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_glide_down));
                searchRecordLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_search_trans));
                searchRecordLayout.setVisibility(View.GONE);
                selectRecordLayout.setVisibility(View.GONE);

                isShowUtils = false;
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

        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });

        rewindRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos <= 0) {
                    pos = files.length - 1;
                } else {
                    pos--;
                }

                initPlayer(pos);
            }
        });

        forwardRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos < files.length - 1) {
                    pos++;
                } else {
                    pos = 0;

                }
                initPlayer(pos);
            }
        });

        cancelCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_glide_down));
                utilLayout.setVisibility(View.GONE);
                adapter.hideAllSelecting();
            }
        });

        deletePlayingRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File curDirectory = files[pos];
                String path = view.getContext().getExternalFilesDir("/") + "/deletedRecent";
                createFolderIfNotExists(path);
                File destDirectory = new File(path);
                try {
                    moveFile(curDirectory, destDirectory);
                    refreshFragment();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        recordsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (searchRecordBar.getVisibility() != View.GONE) {
                    searchRecordBar.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_glide_up));
                    searchRecordBar.setVisibility(View.GONE);
                }

                if (mainPlayLayout.getVisibility() != View.GONE) {
                    mainPlayLayout.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private void SetUp() {
        recordsRecyclerView = view.findViewById(R.id.recordsRecyclerView);

        utilRecordLayout = view.findViewById(R.id.utilRecordLayout);
        selectRecordLayout = view.findViewById(R.id.selectRecordLayout);
        searchRecordLayout = view.findViewById(R.id.searchRecordLayout);
        utilLayout = view.findViewById(R.id.deleteUtilsLayout);

        searchRecordBar = view.findViewById(R.id.searchRecordBar);
        playLayout = view.findViewById(R.id.playLayout);
        mainPlayLayout = view.findViewById(R.id.mainPlayLayout);

        playHeading = view.findViewById(R.id.playHeading);
        namePlayingRecord = view.findViewById(R.id.namePlayingRecord);

        durationPlayingRecord = view.findViewById(R.id.durationPlayingRecord);
        startTimePlayingRecord = view.findViewById(R.id.startTimePlayingRecord);
        endTimePlayingRecord = view.findViewById(R.id.endTimePlayingRecord);

        playRecord = view.findViewById(R.id.playRecord);
        rewindRecord = view.findViewById(R.id.rewindRecord);
        forwardRecord = view.findViewById(R.id.forwardRecord);

        seekBarRecord = view.findViewById(R.id.seekBarRecord);

        cancelCheckButton = view.findViewById(R.id.cancelCheckButton);
        deletePlayingRecord = view.findViewById(R.id.deletePlayingRecord);

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        records = new ArrayList<>();
        path = this.view.getContext().getExternalFilesDir("/").getAbsolutePath(); //Get the path of records stored
        File directory = new File(path);
        files = directory.listFiles(); //Get all files from path above
        for (int i = 0; i < files.length; i++)
        {
            records.add(new Record(getContext(),files[i].getName(), files[i].lastModified(), getAudioFileLength(this.view.getContext().getExternalFilesDir("/")+"/"+files[i].getName())));
        }


        /*records.add(new Record("Record 1", 0, 360));
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
    public long getAudioFileLength(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        long millSecond = 0;
        try {
            Uri uri = Uri.parse(path);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(view.getContext(), uri);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            millSecond = Long.parseLong(duration);

        }catch (Exception e){
            e.printStackTrace();
        }
        return millSecond;
    }
    private void initPlayer(final int position) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }

        String sname = files[position].getName().replace(".mp3", "").replace(".m4a", "").replace(".wav", "").replace(".m4b", "").replace(".3gp", "").replace("mp4", "");
        namePlayingRecord.setText(sname);

        mMediaPlayer = MediaPlayer.create(view.getContext().getApplicationContext(), Uri.parse(view.getContext().getExternalFilesDir("/")+"/"+files[position].getName())); // create and load mediaplayer with song resources
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
                int curSongPoition = position;
                // code to repeat songs until the
                if (curSongPoition < files.length - 1) {
                    curSongPoition++;
                    initPlayer(curSongPoition);
                } else {
                    curSongPoition = 0;
                    initPlayer(curSongPoition);
                }
            }
        });

        seekBarRecord.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    seekBarRecord.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                            Message msg = new Message();
                            msg.what = mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
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
            String eTime = createTimeLabel(current_position - mMediaPlayer.getDuration());
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

    private void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }
    @Override
    public void onItemClick(int position) {
        initPlayer(position);
        pos = position;
    }

    public static void moveFile(File srcFileOrDirectory, File desFileOrDirectory) throws IOException {
        File newFile = new File(desFileOrDirectory, srcFileOrDirectory.getName());
        try (FileChannel outputChannel = new FileOutputStream(newFile).getChannel(); FileChannel inputChannel = new FileInputStream(srcFileOrDirectory).getChannel()) {
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            deleteRecursive(srcFileOrDirectory);
        }
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : Objects.requireNonNull(fileOrDirectory.listFiles()))
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    public static boolean createFolderIfNotExists(String path) {
        File folder = new File(path);
        if (folder.exists())
            return true;
        else
            return folder.mkdirs();
    }
}
