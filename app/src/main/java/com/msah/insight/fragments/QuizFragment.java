package com.msah.insight.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.msah.insight.R;
import com.msah.insight.views.StepperView;



public class QuizFragment extends Fragment {

    private StepperView mSteppers[] = new StepperView[10];
    private Button mNextBtn0, mNextBtn1, mPrevBtn1, mNextBtn2, mPrevBtn2;

    private int mActivatedColorRes = R.color.green;
    private int mDoneIconRes = R.drawable.check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vertical_stepper, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        LinearLayout linearLayout = view.findViewById(R.id.cont);

        for(int i = 0; i < 10; i++) {
            mSteppers[i] = new StepperView(getActivity());
            mSteppers[i].setIndex(i+1);
            mSteppers[i].setTitle("Question "+ i);
            linearLayout.addView(mSteppers[i]);

        }
       // mSteppers[0].setState(StepperView.STATE_SELECTED);
        StepperView.bindSteppers(mSteppers);
/**
       mNextBtn0 = view.findViewById(R.id.button_next_0);

        mNextBtn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSteppers[0].nextStep();
            }
        });

        view.findViewById(R.id.button_test_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSteppers[0].getErrorText() != null) {
                    mSteppers[0].setErrorText(null);
                } else {
                    mSteppers[0].setErrorText("Test error!");
                }
            }
        });

        mPrevBtn1 = view.findViewById(R.id.button_prev_1);
        mPrevBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSteppers[1].prevStep();
            }
        });

        mNextBtn1 = view.findViewById(R.id.button_next_1);
        mNextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSteppers[1].nextStep();
            }
        });

        mPrevBtn2 = view.findViewById(R.id.button_prev_2);
        mPrevBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSteppers[2].prevStep();
            }
        });

        mNextBtn2 = view.findViewById(R.id.button_next_2);
        mNextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSteppers[2].nextStep();
                Snackbar.make(view, "Finish!", Snackbar.LENGTH_LONG).show();
            }
        });
        **/

        view.findViewById(R.id.btn_change_point_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivatedColorRes == R.color.green) {
                    mActivatedColorRes = R.color.purple_200;
                } else {
                    mActivatedColorRes = R.color.green;
                }
                for (StepperView stepper : mSteppers) {
                    stepper.setActivatedColorResource(mActivatedColorRes);
                }
            }
        });
        view.findViewById(R.id.btn_change_done_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDoneIconRes == R.drawable.check) {
                    mDoneIconRes = R.drawable.ic_addnote;
                } else {
                    mDoneIconRes = R.drawable.check;
                }
                for (StepperView stepper : mSteppers) {
                    stepper.setDoneIconResource(mDoneIconRes);
                }
            }
        });
    }
}