package finn.academic.voicerecorder.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import finn.academic.voicerecorder.R;

public class FragmentSetting extends Fragment {
    private View view;

    private SeekBar seekBarVolume;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch continuousCheck;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch blockCallCheck;

    SharedPreferences sharedPreferences;

    AudioManager audioManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        SetUp();

        continuousCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("continuous", b);
                editor.commit();

                Log.d("continuous", "hahaha");

                if (b) {
                    Toast.makeText(view.getContext(),
                            view.getContext().getResources().getString(R.string.continuous) + " " +
                            view.getContext().getResources().getString(R.string.is_on), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(),
                            view.getContext().getResources().getString(R.string.continuous) + " " +
                                    view.getContext().getResources().getString(R.string.is_off), Toast.LENGTH_SHORT).show();
                }
            }
        });

        blockCallCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("block_call", b);
                editor.commit();

                Log.d("block_call", "hahaha");

                if (b) {
                    Toast.makeText(view.getContext(),
                            view.getContext().getResources().getString(R.string.block_call) + " " +
                                    view.getContext().getResources().getString(R.string.is_on), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(),
                            view.getContext().getResources().getString(R.string.block_call) + " " +
                                    view.getContext().getResources().getString(R.string.is_off), Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.FLAG_SHOW_UI);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        VolumeThread volumeThread = new VolumeThread();
        volumeThread.start();

        return view;
    }

    private void SetUp() {
        audioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
        seekBarVolume = view.findViewById(R.id.seekBarVolume);
        seekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        continuousCheck = view.findViewById(R.id.continuousCheck);
        blockCallCheck = view.findViewById(R.id.blockCallCheck);

        sharedPreferences = view.getContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        continuousCheck.setChecked(sharedPreferences.getBoolean("continuous", false));
        blockCallCheck.setChecked(sharedPreferences.getBoolean("block_call", false));
        seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private class VolumeThread extends Thread {
        @Override
        public void run() {
            while (true) {
                seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        }
    }
}
