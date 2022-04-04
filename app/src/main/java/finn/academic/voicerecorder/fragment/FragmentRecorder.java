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
                    StopRecording();
                    isRecording = false;
                }
                else {
                    //Start recording
                    if (checkPermissions()) {
                        iconRecord.setImageResource(R.drawable.square_stop);
                        view.startAnimation(animScaleOutside);
                        recordButtonInside.startAnimation(animScaleInside);
                        StartRecording();
                        isRecording = true;
                    }
                }
            }
        });

        return view;
    }

    private void SetUp(View view) {
        recordButton = view.findViewById(R.id.recordButtonOutside);
        recordButtonInside = view.findViewById(R.id.recordButtonInside);
        timer = view.findViewById(R.id.record_timer);
        hourRecord = view.findViewById(R.id.hourRecord);
        minuteRecord = view.findViewById(R.id.minuteRecord);
        secondRecord = view.findViewById(R.id.secondRecord);
        iconRecord = view.findViewById(R.id.iconRecord);
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
        timer.setBase(SystemClock.elapsedRealtime()); //Get base on system real time
        timer.start(); //Start timer

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
        timer.stop(); //Stop timer

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

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            StopRecording();
        }
    }
}
