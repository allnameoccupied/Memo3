package com.max.memo3.TestSubject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.max.memo3.R;
import com.max.memo3.Util.util;

import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

//Timer, Vibrator, Count down timer test
//used util vibrator, count down timer, timer
public class Test2_Timer extends Fragment {
    //var
    private Test_ViewModel viewModel;

    //func
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Test_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test2_frag,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setName("Test2");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.test2_button1).setOnClickListener(this::test2_button1_onclick);
        view.findViewById(R.id.test2_button2).setOnClickListener(this::test2_button2_onclick);
        view.findViewById(R.id.test2_button3).setOnClickListener(this::test2_button3_onclick);
        view.findViewById(R.id.test2_button4).setOnClickListener(this::test2_button4_onclick);
        view.findViewById(R.id.test2_button5).setOnClickListener(this::test2_button5_onclick);
        view.findViewById(R.id.test2_button6).setOnClickListener(this::test2_button6_onclick);
    }

    //amplitude 1-255 only
    private void test2_button1_onclick(View view){
//        ((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE))
//                .vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
//                .vibrate(VibrationEffect.createOneShot(1000,50));
//                .vibrate(VibrationEffect.createOneShot(1000,250));
        util.makeVibrate(1000,50);
    }
    private void test2_button2_onclick(View view){
        util.makeVibrate(1000);
    }
    private void test2_button3_onclick(View view){
        util.makeVibrate(1000,250);
    }
    private void test2_button4_onclick(View view){
        long[] timings = {100,1000,100};
        int[] amplitudes = {50,100,250};
//        ((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE))
//                .vibrate(VibrationEffect.createWaveform(timings,amplitudes,-1));
        util.makeVibrate(timings,amplitudes,-1);
    }
    private void test2_button5_onclick(View view){
        TextView textView = getActivity().findViewById(R.id.test2_textview1);
        textView.setText("5000");
        util.makeCountDownTimer(5000, 1000, new util.CountDownTimerImplementation() {
            @Override
            public void onTick(long ms_untilFinish) {
                textView.setText(Long.toString(ms_untilFinish));
            }

            @Override
            public void onFinish() {
                textView.setText("0");
                util.quickLog("countdown finished");
            }
        });
    }
    private void test2_button6_onclick(View view){
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                util.quickLog("5s passed");
//            }
//        },5000);
        util.makeTimer_Xdaemon(5000, new TimerTask() {
            @Override
            public void run() {
                util.quickLog("5s passed");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        util.stopVibrator();
    }
}
