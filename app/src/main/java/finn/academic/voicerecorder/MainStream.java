package finn.academic.voicerecorder;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import finn.academic.voicerecorder.fragment.FragmentRecordList;
import finn.academic.voicerecorder.fragment.FragmentRecorder;
import finn.academic.voicerecorder.fragment.FragmentSetting;

public class MainStream extends Activity implements View.OnClickListener {
    ImageView startRecording;
    ImageView listRecords;
    ImageView setting;

    TextView actionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stream);

        SetUp();

        startRecording.setOnClickListener(this);
        listRecords.setOnClickListener(this);
        setting.setOnClickListener(this);
    }

    private void SetUp() {
        startRecording = findViewById(R.id.startRecording);
        listRecords = findViewById(R.id.listRecords);
        setting = findViewById(R.id.Setting);

        actionName =  findViewById(R.id.actionName);

        getFragmentManager().beginTransaction().add(R.id.fragmentStream, new FragmentRecorder()).commit();
        startRecording.setBackgroundResource(R.drawable.custom_button_in_fragment);
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;

        if (startRecording.equals(view)) {
            startRecording.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blur_to_clear));
            startRecording.setBackgroundResource(R.drawable.custom_button_in_fragment);
            listRecords.setBackgroundResource(R.color.light_red);
            setting.setBackgroundResource(R.color.light_red);
            actionName.setText(getResources().getString(R.string.recorder));
            fragment = new FragmentRecorder();
        } else if (listRecords.equals(view)) {
            listRecords.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blur_to_clear));
            listRecords.setBackgroundResource(R.drawable.custom_button_in_fragment);
            startRecording.setBackgroundResource(R.color.light_red);
            setting.setBackgroundResource(R.color.light_red);
            actionName.setText(getResources().getString(R.string.player));
            fragment = new FragmentRecordList();
        } else if (setting.equals(view)) {
            setting.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blur_to_clear));
            setting.setBackgroundResource(R.drawable.custom_button_in_fragment);
            startRecording.setBackgroundResource(R.color.light_red);
            listRecords.setBackgroundResource(R.color.light_red);
            actionName.setText(getResources().getString(R.string.setting));
            fragment = new FragmentSetting();
        }

        getFragmentManager().beginTransaction().replace(R.id.fragmentStream, fragment).commit();
    }
}