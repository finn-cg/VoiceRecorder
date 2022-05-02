package finn.academic.voicerecorder.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
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

import finn.academic.voicerecorder.MainStream;
import finn.academic.voicerecorder.R;
import finn.academic.voicerecorder.util.FileHandler;
import finn.academic.voicerecorder.view.VisualizerView;

public class FragmentRecorder extends Fragment {
    private View view;
    private RelativeLayout recordButton;
    private RelativeLayout recordButtonInside;
    private ImageView iconRecord;

    private Button pauseRecordButton;

    //private Boolean startRecord;
    private boolean isRecording = false;
    private boolean isPausing = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 101;

    private MediaRecorder mediaRecorder;
    private String recordFile;
    private TextView hourRecord, minuteRecord, secondRecord;
    private String recordPath, recordPathAbs, recordFormat;
    private EditText newRecordName;

    int second = 0, minute = 0, hour = 0;
    volatile boolean running = false;
    public static final int REPEAT_INTERVAL = 40;
    private VisualizerView visualizerView;
    private Handler handler; // Handler for updating the visualizer
    private Thread t;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recorder, container, false);

        Animation animScaleOutside = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_record_outside);
        Animation animScaleInside = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_record_inside);

        recordPath = getPath();
        SetUp();

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isRecording) {
                    editor.putBoolean("is_recording", false);
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
                        editor.putBoolean("is_recording", true);
                        iconRecord.setImageResource(R.drawable.square_stop);
                        view.startAnimation(animScaleOutside);
                        recordButtonInside.startAnimation(animScaleInside);
                        isRecording = true;
                        StartRecording();
                        handler.post(updateVisualizer);
                    }
                }
                editor.commit();
            }
        });

        pauseRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // enable = true => black
                // enable = false => aux_item
                // enable when recording and disable when starting pause
                if (isPausing) {
                    //Resume
                    isPausing = false;
                    mediaRecorder.resume();
                    pauseRecordButton.setText(getResources().getString(R.string.pause));
                    recordButton.setClickable(true);
                    recordButton.startAnimation(animScaleOutside);
                    recordButtonInside.startAnimation(animScaleInside);
                }
                else {
                    //Pause
                    isPausing = true;
                    mediaRecorder.pause();
                    pauseRecordButton.setText(getResources().getString(R.string.resume));
                    recordButton.setClickable(false);
                    recordButton.clearAnimation();
                    recordButtonInside.clearAnimation();
                }
            }
        });

        visualizerView = (VisualizerView) view.findViewById(R.id.visualizer);
        handler = new Handler();
        return view;
    }

    private void SetUp() {
        recordButton = view.findViewById(R.id.recordButtonOutside);
        recordButtonInside = view.findViewById(R.id.recordButtonInside);
        pauseRecordButton = view.findViewById(R.id.pauseRecordButton);
        hourRecord = view.findViewById(R.id.hourRecord);
        minuteRecord = view.findViewById(R.id.minuteRecord);
        secondRecord = view.findViewById(R.id.secondRecord);
        iconRecord = view.findViewById(R.id.iconRecord);
        pauseRecordButton = view.findViewById(R.id.pauseRecordButton);
        pauseRecordButton.setVisibility(View.GONE);

        sharedPreferences = view.getContext().getSharedPreferences("status", Context.MODE_PRIVATE);
    }
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions((Activity) view.getContext(),new String[] {recordPermission}, PERMISSION_CODE);
            return false;

        }
    }
    private void StartRecording() {
        if (!running) {
            startTimer();
            t.start();

            running = true;
        }

        pauseRecordButton.setVisibility(View.VISIBLE);

        if (recordPath.equals(""))
        {
            recordPath = view.getContext().getExternalFilesDir("/")+"/default"; //Set record path
        } else {
            String pathTemp = view.getContext().getExternalFilesDir("/") + "/" + recordPath; //Set record path
            recordPath = pathTemp;
        }
        FileHandler.createFolderIfNotExists(recordPath);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.JAPAN);
        Date now = new Date();
        recordFormat = ".mp3";
        recordFile = "Recording_" + formatter.format(now) + recordFormat;


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
        resetTimer();

        pauseRecordButton.setVisibility(View.GONE);

        mediaRecorder.stop(); //Stop record
        mediaRecorder.release();
        mediaRecorder = null;
        handler.removeCallbacks(updateVisualizer);
        visualizerView.clear();
        showSaveRecordDialog();

    }

    private void showSaveRecordDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.diaglog_make_change);
        dialog.setCanceledOnTouchOutside(false);

        TextView title = dialog.findViewById(R.id.mainTitleDialogMakeChange);
        TextView description = dialog.findViewById(R.id.descriptionDialogMakeChange);

        title.setText(getResources().getString(R.string.new_record));
        description.setText(getResources().getString(R.string.input_record_name));

        Button cancel = dialog.findViewById(R.id.cancelDialogMakeChange);
        Button save = dialog.findViewById(R.id.saveDialogMakeChange);
        save.setEnabled(true);
        save.setAlpha(1);

        newRecordName = (EditText) dialog.findViewById(R.id.newName);

        newRecordName.addTextChangedListener(new TextWatcher() {
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
                        newRecordName.setText(charSequence.subSequence(0, charSequence.length() - 1));
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
                refreshFrag();

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
                refreshFrag();

                File dir = new File(recordPath+"/");
                if (newRecordName.getText().toString().equals("")) {
                    dialog.dismiss();
                }
                else {
                    if(dir.exists()){
                        File from = new File(dir,recordFile);
                        File to = new File(dir,newRecordName.getText().toString() + recordFormat);
                        if(from.exists()) {
                            if (to.exists()) {
                                final Dialog dialog2 = new Dialog(getContext());
                                dialog2.setContentView(R.layout.dialog_overwrite_record);
                                dialog2.setCanceledOnTouchOutside(false);

                                TextView title2 = dialog2.findViewById(R.id.mainTitleDialogMakeChange);
                                TextView description2 = dialog2.findViewById(R.id.descriptionDialogMakeChange);

                                title2.setText(getResources().getString(R.string.exist_record_name));
                                description2.setText(getResources().getString(R.string.overwrite_record));

                                Button no = dialog2.findViewById(R.id.noDialogMakeChange);
                                Button yes = dialog2.findViewById(R.id.yesDialogMakeChange);
                                yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        from.renameTo(to);
                                        dialog.dismiss();
                                        dialog2.dismiss();
                                    }
                                });
                                no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog2.dismiss();
                                    }
                                });
                                dialog2.show();
                            }
                            else {
                                from.renameTo(to);
                                dialog.dismiss();
                            }
                        }
                    }
                }

                // ======== update numRecords
            }
        });
        dialog.show();
    }

    private void startTimer() {
        t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted() && isRecording) {
                    if (!isPausing)
                    {
                        try {
                            Thread.sleep(1000);
                            ((Activity) view.getContext()).runOnUiThread(new Runnable() {
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

                }
                if (isInterrupted()) {
                    return;
                }
            }
        };
    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
                // get the current amplitude
                int x = mediaRecorder.getMaxAmplitude();
                visualizerView.addAmplitude(x); // update the VisualizeView
                visualizerView.invalidate(); // refresh the VisualizerView

                // update in 40 milliseconds
                handler.postDelayed(this, REPEAT_INTERVAL);
        }
    };

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

    private String getPath() {
        MainStream mainStream = (MainStream) getActivity();
        String path = mainStream.getPath();
        return path;
    }

    private void refreshFrag() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            StopRecording();
        }
    }
}
