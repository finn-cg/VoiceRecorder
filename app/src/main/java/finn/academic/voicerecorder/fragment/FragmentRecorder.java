package finn.academic.voicerecorder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import finn.academic.voicerecorder.R;

public class FragmentRecorder extends Fragment {
    private RelativeLayout recordButton;
    private RelativeLayout recordButtonInside;
    private ImageView iconRecord;
    private Boolean startRecord;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recorder, container, false);

        startRecord = true;

        Animation animScaleOutside = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_record_outside);
        Animation animScaleInside = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_record_inside);

        SetUp(view);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startRecord) {
                    iconRecord.setImageResource(R.drawable.square_stop);
                    view.startAnimation(animScaleOutside);
                    recordButtonInside.startAnimation(animScaleInside);
                    startRecord = false;
                } else {
                    iconRecord.setImageResource(R.drawable.logo);
                    view.clearAnimation();
                    recordButtonInside.clearAnimation();
                    startRecord = true;
                }
            }
        });

        return view;
    }

    private void SetUp(View view) {
        recordButton = view.findViewById(R.id.recordButtonOutside);
        recordButtonInside = view.findViewById(R.id.recordButtonInside);
        iconRecord = view.findViewById(R.id.iconRecord);
    }
}
