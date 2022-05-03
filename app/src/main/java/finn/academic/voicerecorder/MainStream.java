package finn.academic.voicerecorder;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.FragmentActivity;

import finn.academic.voicerecorder.fragment.FragmentDetail;
import finn.academic.voicerecorder.fragment.FragmentGuide;
import finn.academic.voicerecorder.fragment.FragmentRecordList;
import finn.academic.voicerecorder.fragment.FragmentRecorder;
import finn.academic.voicerecorder.fragment.FragmentSetting;

public class MainStream extends Activity implements View.OnClickListener {
    private ImageView startRecording;
    private ImageView listRecords;
    private ImageView setting;
    private ImageView detailMenu;

    private MenuBuilder menuBuilder;

    private TextView actionName;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stream);

        SetUp();

        startRecording.setOnClickListener(this);
        listRecords.setOnClickListener(this);
        setting.setOnClickListener(this);

        showMenuDetail();
    }

    private void SetUp() {
        startRecording = findViewById(R.id.startRecording);
        listRecords = findViewById(R.id.listRecords);
        setting = findViewById(R.id.Setting);
        detailMenu = findViewById(R.id.detailMenu);

        actionName = findViewById(R.id.actionName);

        getFragmentManager().beginTransaction().add(R.id.fragmentStream, new FragmentRecorder()).commit();
        startRecording.setBackgroundResource(R.drawable.custom_button_in_fragment);

        path = getIntent().getStringExtra("key");
        if (path == null)
            path = "";

    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;

        if (startRecording.equals(view)) {
            startRecording.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_blur_to_clear));
            startRecording.setBackgroundResource(R.drawable.custom_button_in_fragment);
            listRecords.setBackgroundResource(R.color.light_red);
            setting.setBackgroundResource(R.color.light_red);
            actionName.setText(getResources().getString(R.string.recorder));
            fragment = new FragmentRecorder();
        } else if (listRecords.equals(view)) {
            listRecords.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_blur_to_clear));
            listRecords.setBackgroundResource(R.drawable.custom_button_in_fragment);
            startRecording.setBackgroundResource(R.color.light_red);
            setting.setBackgroundResource(R.color.light_red);
            actionName.setText(getResources().getString(R.string.player));
            fragment = new FragmentRecordList();

        } else if (setting.equals(view)) {
            setting.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_blur_to_clear));
            setting.setBackgroundResource(R.drawable.custom_button_in_fragment);
            startRecording.setBackgroundResource(R.color.light_red);
            listRecords.setBackgroundResource(R.color.light_red);
            actionName.setText(getResources().getString(R.string.setting));
            fragment = new FragmentSetting();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.fragment_blur_to_clear, R.animator.fragment_clear_to_blur);
        transaction.replace(R.id.fragmentStream, fragment);
        transaction.commit();
    }

    public String getPath() {
        return path;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // optional depending on your needs
        this.finish();
    }

    @SuppressLint("RestrictedApi")
    private void showMenuDetail() {
        menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menuBuilder);
        detailMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MenuPopupHelper optionMenu = new MenuPopupHelper(MainStream.this, menuBuilder, view);
                optionMenu.setForceShowIcon(true);

                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuDetail:
                                setting.setBackgroundResource(R.color.light_red);
                                startRecording.setBackgroundResource(R.color.light_red);
                                listRecords.setBackgroundResource(R.color.light_red);
                                actionName.setText(getResources().getString(R.string.detail));
                                Fragment fragment = new FragmentDetail();
                                FragmentTransaction transaction = MainStream.this.getFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.animator.fragment_blur_to_clear, R.animator.fragment_clear_to_blur);
                                transaction.replace(R.id.fragmentStream, fragment);
                                transaction.commit();
                                break;
                            case R.id.menuGuide:
                                startActivity(new Intent(MainStream.this, FragmentGuide.class));
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });
                optionMenu.show();
            }
        });
    }
}