package com.max.memo3.TestSubject;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.max.memo3.R;
import com.max.memo3.Util.util;

import java.util.Arrays;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

//Flash light, toggle button
//used util flash
//change color of switch as change in style.xml
public class Test3_Flash extends Fragment {
    //var
    private Test_ViewModel viewModel;
    private ToggleButton toggleButton1;
    private Switch switch1;
    private CameraManager cameraManager;
    private String flashID;

    //func
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Test_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test3_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleButton1 = view.findViewById(R.id.test3_toggleButton1);
        switch1 = view.findViewById(R.id.test3_switch1);
        try {
            cameraManager = ((CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE));
            flashID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        toggleButton1.setOnCheckedChangeListener(this::test3_toggle1_onclick);
        switch1.setOnCheckedChangeListener(this::test3_switch1_onclick);
        view.findViewById(R.id.test3_button1).setOnClickListener(this::test3_button1_onclick);
    }

    //just change will already trigger onCheckedChange
    //true(on) -> true(on) wont trigger
    private void test3_toggle1_onclick(CompoundButton buttonView, boolean isChecked){
//        if (isChecked){
//            util.quickLog("toggle now is ON");
////            switch1.setChecked(false);
////            switchON();
//        } else {
//            util.quickLog("toggle now is OFF");
//        }
//        try {
//            cameraManager.setTorchMode(flashID,isChecked);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
        util.quickLog("toggle now is "+isChecked);
        util.makeFlash(isChecked);
    }
    private void test3_switch1_onclick(CompoundButton buttonView, boolean isChecked){
//        if (isChecked){
//            util.quickLog("switch now is ON");
////            toggleButton1.setChecked(false);
//        } else {
//            util.quickLog("switch now is OFF");
//        }
//        try {
//            cameraManager.setTorchMode(flashID,isChecked);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
        util.quickLog("switch now is "+isChecked);
        util.makeFlash(isChecked);
    }
    private void test3_button1_onclick(View view){
//        util.makeFlash_ON();
//        util.makeTimer_Xdaemon(3000, new TimerTask() {
//            @Override
//            public void run() {
//                util.makeFlash_OFF();
//            }
//        });
        util.makeCountDownTimer_Xinterval(5000, new util.CountDownTimerImplementation() {
            @Override
            public void onTick(long ms_untilFinish) {
                util.makeFlash_ON();
                util.quickLog("Qwer");
            }

            @Override
            public void onFinish() {
                util.makeFlash_OFF();
            }
        });
    }

    private void switchON(){
        CameraManager cameraManager = ((CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE));
        String[] strings;
        try {
            strings = cameraManager.getCameraIdList();
            util.quickLog(strings.length);
//            Arrays.asList(strings).forEach(util::quickLog);
            cameraManager.setTorchMode(strings[0],true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setName("Test3");
    }
}
