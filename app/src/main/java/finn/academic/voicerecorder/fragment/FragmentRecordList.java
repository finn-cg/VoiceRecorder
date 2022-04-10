package finn.academic.voicerecorder.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import finn.academic.voicerecorder.R;
import finn.academic.voicerecorder.adapter.RecordAdapter;
import finn.academic.voicerecorder.model.Record;

public class FragmentRecordList extends Fragment {
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

    TextView playHeading;

    Button cancelCheckButton;

    Boolean isShowUtils = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        SetUp(view);

        adapter = new RecordAdapter(view.getContext(), records);
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

        cancelCheckButton = view.findViewById(R.id.cancelCheckButton);

        records = new ArrayList<>();
        records.add(new Record("Record 1", 0, 360));
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
        records.add(new Record("Record 12", 30, 120));
    }
}
