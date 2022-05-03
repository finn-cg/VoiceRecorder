package finn.academic.voicerecorder.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import finn.academic.voicerecorder.R;
import finn.academic.voicerecorder.adapter.GuideAdapter;

public class FragmentGuide extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guide);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);

        GuideAdapter guideAdapter = new GuideAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        guideAdapter.addFragment(new FragmentGuideFolder(), "FOLDER");
        guideAdapter.addFragment(new FragmentGuideRecentlyDeleted(), "RECENTLY DELETED");
        guideAdapter.addFragment(new FragmentGuideRecorder(), "RECORDER");
        guideAdapter.addFragment(new FragmentGuidePlayer(), "PLAYER");
        guideAdapter.addFragment(new FragmentGuideSetting(), "SETTING");
        viewPager.setAdapter(guideAdapter);
    }
}
