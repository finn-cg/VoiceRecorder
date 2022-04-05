package finn.academic.voicerecorder.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import finn.academic.voicerecorder.R;

public class FragmentRecorder extends Fragment {
    private RelativeLayout recordButton;
    private RelativeLayout recordButtonInside;
    private ImageView iconRecord;
    //private Boolean startRecord;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 101;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer timer;
    private TextView hourRecord, minuteRecord, secondRecord;
    private String recordPath, recordPathAbs, recordFormat;
    private EditText newRecordName;
    int second = 0, minute = 0, hour = 0;
    volatile boolean running = false;
    Thread t;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recorder, container, false);

        //startRecord = true;

        Animation animScaleOutside = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_record_outside);
        Animation animScaleInside = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_record_inside);

        SetUp(view);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                if (startRecord) {
                    iconRecord.setImageResource(R.drawable.square_stop);
                    view.startAnimation(animScaleOutside);
                    recordButtonInside.startAnimation(animScaleInside);
                    StopRecording();
                    startRecord = false;

                } else {
                    iconRecord.setImageResource(R.drawable.logo);
                    view.clearAnimation();
                    recordButtonInside.clearAnimation();
                    startRecord = true;
                }*/
                if (isRecording) {
                    //Stop recording
                    iconRecord.setImageResource(R.drawable.logo);
                    view.clearAnimation();
                    recordButtonInside.clearAnimation();
                    isRecording = false;
                    StopRecording();

                }
                else {
                    //Start recording
                    if (checkPermissions()) {
                        iconRecord.setImageResource(R.drawable.square_stop);
                        view.startAnimation(animScaleOutside);
                        recordButtonInside.startAnimation(animScaleInside);
                        isRecording = true;
                        StartRecording();

                    }
                }
            }
        });

        return view;
    }

    private void SetUp(View view) {
        recordButton = view.findViewById(R.id.recordButtonOutside);
        recordButtonInside = view.findViewById(R.id.recordButtonInside);
        timer = (Chronometer) view.findViewById(R.id.record_timer);
        hourRecord = view.findViewById(R.id.hourRecord);
        minuteRecord = view.findViewById(R.id.minuteRecord);
        secondRecord = view.findViewById(R.id.secondRecord);
        iconRecord = view.findViewById(R.id.iconRecord);

/*        hourRecord.setVisibility(View.VISIBLE);
        minuteRecord.setVisibility(View.VISIBLE);
        secondRecord.setVisibility(View.VISIBLE);
        timer.setVisibility(View.GONE);*/
    }
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),new String[] {recordPermission}, PERMISSION_CODE);
            return false;

        }
    }
    private void StartRecording() {
/*        hourRecord.setVisibility(View.GONE);
        minuteRecord.setVisibility(View.GONE);
        secondRecord.setVisibility(View.GONE);
        timer.setVisibility(View.VISIBLE);*/
/*        timer.setBase(SystemClock.elapsedRealtime()); //Get base on system real time
        timer.start(); //Start timer*/
        if (!running) {
            startTimer();
            t.start();
            running = true;
        }
/*        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s = (int)(time - h*3600000- m*60000)/1000;
                hourRecord.setText(String.valueOf(h));
                minuteRecord.setText(String.valueOf(m));
                secondRecord.setText(String.valueOf(s));
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                secondRecord.setText(s);
                chronometer.setText(t);
            }
        });
        timer.setBase(SystemClock.elapsedRealtime());
        timer.setText("00:00:00");
        timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timer.start();*/


        recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath(); //Set record path
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.JAPAN);
        Date now = new Date();
        recordFile = "Recording_" + formatter.format(now) + ".3gp";
        recordFormat = ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //Get audio source from mic
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //Set output format to 3gp
        recordPathAbs = recordPath + "/" + recordFile;
        mediaRecorder.setOutputFile(recordPathAbs);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //Set audio encoder to AMR_NB

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException ise) {}
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void StopRecording() {
        //timer.stop(); //Stop timer
        /*timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
        hourRecord.setText("0");
        minuteRecord.setText("00");
        secondRecord.setText("00");*/
        resetTimer();

        mediaRecorder.stop(); //Stop record
        mediaRecorder.release();
        mediaRecorder = null;
        showSaveRecordDialog();
    }

    private void showSaveRecordDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_save_record);
        dialog.setCanceledOnTouchOutside(false);

        Button cancel = dialog.findViewById(R.id.cancelSaveRecord);
        Button save = dialog.findViewById(R.id.saveRecord);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                File file = new File(recordPathAbs);
                if (file.exists()){
                    file.delete();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File dir = new File(recordPath+"/");
                newRecordName = (EditText) dialog.findViewById(R.id.newRecordName);
                if(dir.exists()){
                    File from = new File(dir,recordFile);
                    File to = new File(dir,newRecordName.getText().toString() + recordFormat);
                    if(from.exists()) {
                        from.renameTo(to);
                        dialog.dismiss();
                    }

                }
            }
        });
        dialog.show();
    }
    private void startTimer() {
        t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted() && isRecording) {
                    try {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (second <= 60)
                                    second++;
                                if (second >= 0 && second <= 9) {
                                    secondRecord.setText(String.valueOf("0" + second));
                                }
                                else {
                                    if (second == 60) {
                                        secondRecord.setText("00");
                                    }
                                    else
                                        secondRecord.setText(String.valueOf(second));
                                }

                                if (second >= 60) {
                                    second = 0;
                                    minute++;
                                    if (minute >= 0 && minute <= 9) {
                                        minuteRecord.setText(String.valueOf("0" + minute));
                                    }
                                    else {
                                        if (minute == 60) {
                                            minuteRecord.setText("00");
                                        }
                                        else
                                            minuteRecord.setText(String.valueOf(minute));

                                    }

                                }
                                if (minute >= 60) {
                                    minute = 0;
                                    hour++;
                                    hourRecord.setText(String.valueOf(hour));
                                }
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isInterrupted()) {
                    return;
                }
            }
        };
    }
    private void resetTimer() {
        second = 0;
        minute = 0;
        hour = 0;
        secondRecord.setText(String.valueOf("00"));
        minuteRecord.setText(String.valueOf("00"));
        hourRecord.setText(String.valueOf(hour));
        if (running) {
            t.interrupt();
            running = false;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            StopRecording();
        }
    }
}
