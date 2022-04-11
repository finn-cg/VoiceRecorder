package finn.academic.voicerecorder.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import finn.academic.voicerecorder.R;
import finn.academic.voicerecorder.adapter.RecordAdapter;
import finn.academic.voicerecorder.model.Record;

public class FragmentRecordList extends Fragment implements RecordAdapter.RecyclerViewClickInterface {
    RecyclerView recordsRecyclerView;
    ArrayList<Record> records;
    RecordAdapter adapter;

    RelativeLayout utilRecordLayout;
    RelativeLayout selectRecordLayout;
    RelativeLayout searchRecordLayout;
    RelativeLayout utilLayout;

    LinearLayout searchRecordBar;
    LinearLayout playLayout;
    LinearLayout mainPlayLayout;

    TextView playHeading, durationPlayingRecord, startTimePlayingRecord;
    SeekBar seekBarRecord;
    ImageButton playRecord;

    Button cancelCheckButton;

    Boolean isShowUtils = false;

    private File[] files;
    String path = "";
    static MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        SetUp(view);
        initPlayer(0);

        adapter = new RecordAdapter(view.getContext(), records, files);
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

        cancelCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.hide_glide_down));
                utilLayout.setVisibility(View.GONE);
                adapter.hideAllSelecting();
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

    private void SetUp(View view) {
        recordsRecyclerView = view.findViewById(R.id.recordsRecyclerView);

        utilRecordLayout = view.findViewById(R.id.utilRecordLayout);
        selectRecordLayout = view.findViewById(R.id.selectRecordLayout);
        searchRecordLayout = view.findViewById(R.id.searchRecordLayout);
        utilLayout = view.findViewById(R.id.deleteUtilsLayout);

        searchRecordBar = view.findViewById(R.id.searchRecordBar);
        playLayout = view.findViewById(R.id.playLayout);
        mainPlayLayout = view.findViewById(R.id.mainPlayLayout);

        playHeading = view.findViewById(R.id.playHeading);
        durationPlayingRecord = view.findViewById(R.id.durationPlayingRecord);
        startTimePlayingRecord = view.findViewById(R.id.startTimePlayingRecord);
        playRecord = view.findViewById(R.id.playRecord);

        seekBarRecord = view.findViewById(R.id.seekBarRecord);

        cancelCheckButton = view.findViewById(R.id.cancelCheckButton);

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        records = new ArrayList<>();
        path = getActivity().getExternalFilesDir("/").getAbsolutePath(); //Get the path of records stored
        File directory = new File(path);
        files = directory.listFiles(); //Get all files from path above

        for (int i = 0; i < files.length; i++)
        {
            String sname = files[i].getName().replace(".mp3", "").replace(".m4a", "").replace(".wav", "").replace(".m4b", "");
            records.add(new Record(getContext(),sname, files[i].lastModified(), getAudioFileLength(getActivity().getExternalFilesDir("/")+"/"+files[i].getName())));
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
            mmr.setDataSource(getActivity(), uri);
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

        mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), Uri.parse(getActivity().getExternalFilesDir("/")+"/"+files[position].getName())); // create and load mediaplayer with song resources
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
            String cTime = createTimeLabel(current_position);
            startTimePlayingRecord.setText(cTime);
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

    @Override
    public void onItemClick(int position) {

    }
}
