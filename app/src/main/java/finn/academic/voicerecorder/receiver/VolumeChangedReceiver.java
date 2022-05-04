package finn.academic.voicerecorder.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import finn.academic.voicerecorder.R;

public class VolumeChangedReceiver extends BroadcastReceiver {
    private SeekBar seekBarVolume;
    AudioManager audioManager;

    private Boolean isTouching = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        int volume = (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");

        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        if (seekBarVolume == null) {
            seekBarVolume = ((Activity) context).findViewById(R.id.seekBarVolume);
        }

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.FLAG_SHOW_UI);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouching = false;
            }
        });

        if (!isTouching) {
            seekBarVolume.setProgress(volume);
        }
    }
}
