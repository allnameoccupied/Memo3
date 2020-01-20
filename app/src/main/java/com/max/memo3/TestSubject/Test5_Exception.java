package com.max.memo3.TestSubject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.max.memo3.Exception.Test5_Exception1;
import com.max.memo3.Exception.Test5_Exception2;
import com.max.memo3.Exception.Test5_Exception3;
import com.max.memo3.R;
import com.max.memo3.Util.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

//also view model, data binding
public class Test5_Exception extends Fragment {
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
        return inflater.inflate(R.layout.test5_frag,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setName("Test5");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.test5_button1).setOnClickListener(this::test5_button1_onclick);
        view.findViewById(R.id.test5_button2).setOnClickListener(this::test5_button2_onclick);
        view.findViewById(R.id.test5_button3).setOnClickListener(this::test5_button3_onclick);
    }

    //"return" in "catch" will still run "finally"
    private void test5_button1_onclick(View view){
//        util.quickLog("asf");
        try {
            util.quickLog("qwer");
//            int y = 6/0;
//            util.quickLog("asdf");
            throw new Test5_Exception1();
//            throw new Exception("asdf");
        } catch (RuntimeException e){
            util.quickLog(e.getClass()+" "+e.getMessage());
            util.quickLog("runtime");
//            e.printStackTrace();
        } catch (Throwable e){
            util.quickLog(e.getClass()+" "+e.getMessage());
            util.quickLog("throwable");
            return;
//            e.printStackTrace();
        } finally {
            util.quickLog("zxcv");
        }
//        util.quickLog("zxcv");
    }

    //count down throw need to catch at onFinish
    private void test5_button2_onclick(View view){
//        try {
            util.makeCountDownTimer_Xinterval(100, new util.CountDownTimerImplementation() {
                @Override
                public void onTick(long ms_untilFinish) {
                    util.quickLog("qwer");
                }

                @Override
                public void onFinish() {
                    util.quickLog("asdf");
                    try {
                        throw new Test5_Exception1();
//                        throw new Test5_Exception2();
//                        throw new Test5_Exception3();
                    } catch (Throwable e) {
//                        e.printStackTrace();
                        util.quickLog(e.getMessage());
                    }
                }
            });
//        } catch (Throwable e){
//            e.printStackTrace();
//        }
    }

    private void test5_button3_onclick(View view){
        try {
            String text = ((EditText)getActivity().findViewById(R.id.test5_editText1)).getText().toString();
            util.quickLog(viewModel);
            viewModel.setMsgToShow(text!=null?text:"Null");
        } catch (Throwable e){
            util.quickLog(e.getMessage());
        }

    }
}
