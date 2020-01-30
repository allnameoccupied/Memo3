package com.max.memo3.TestSubject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.max.memo3.R;
import com.max.memo3.Util.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class Test0_frag extends Fragment {
    //var
    private Test_ViewModel viewModel;
    private NavController navController;
    private View view;

    //func
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Test_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.test0_frag,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        view.findViewById(R.id.test0_logTestContent).setOnClickListener(this::test0_logAllTestContent);

        //button to test
        view.findViewById(R.id.test0to1).setOnClickListener(Test0_frag.this::onButtonClick);
        view.findViewById(R.id.test0to2).setOnClickListener(Test0_frag.this::onButtonClick);
        view.findViewById(R.id.test0to3).setOnClickListener(Test0_frag.this::onButtonClick);
        view.findViewById(R.id.test0to4).setOnClickListener(Test0_frag.this::onButtonClick);
        view.findViewById(R.id.test0to5).setOnClickListener(Test0_frag.this::onButtonClick);
        view.findViewById(R.id.test0to6).setOnClickListener(Test0_frag.this::onButtonClick);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.resetMsgToShow();
    }

    private void onButtonClick(View view){
        switch (view.getId()){
            case R.id.test0to1:
                navController.navigate(R.id.test1_Button_nav);
                break;
            case R.id.test0to2:
                navController.navigate(R.id.test2_Timer_nav);
                break;
            case R.id.test0to3:
                navController.navigate(R.id.test3_flash_nav);
                break;
            case R.id.test0to4:
                navController.navigate(R.id.test4_Firestore_nav);
                break;
            case R.id.test0to5:
                navController.navigate(R.id.test5_Exception_nav);
                break;
            case R.id.test0to6:
                navController.navigate(R.id.test6_Realm_nav);
                break;
        }
    }

    private void test0_logAllTestContent(View view){
        util.log("Test 0 : "+"main");
        util.log("Test 1 : "+"button onclick");
        util.log("Test 2 : "+"timer, vibrator, count down timer");
        util.log("Test 3 : "+"flash light, toggle button, style.xml");
        util.log("Test 4 : "+"firestore");
        util.log("Test 5 : "+"exception, view model, data binding");
        util.log("Test 6 : "+"realm, util stuff, notification");
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setName("Test0");
    }
}
