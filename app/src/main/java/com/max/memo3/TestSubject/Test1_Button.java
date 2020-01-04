package com.max.memo3.TestSubject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.max.memo3.R;
import com.max.memo3.Util.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class Test1_Button extends Fragment {
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
        return inflater.inflate(R.layout.test1_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.test1_button1).setOnClickListener(this::button12_onclick_listener);
        view.findViewById(R.id.test1_button2).setOnClickListener(this::button12_onclick_listener);
        view.findViewById(R.id.test1_button3).setOnClickListener(button34_onclick);
        view.findViewById(R.id.test1_button4).setOnClickListener(button34_onclick);
        view.findViewById(R.id.test1_button5).setOnClickListener(this::button5_onclick_listener);
        view.findViewById(R.id.test1_button6).setOnClickListener(this::button6_onclick_listener);
    }

    //xml android:onClick can ONLY be done in activity
    private void button12_onclick(View view){
        switch (view.getId()){
            case R.id.test1_button1:
                util.quickLog("button 1 pressed");
                break;
            case R.id.test1_button2:
                util.quickLog("button 2 pressed");
                break;
        }
    }

    //button 12, onclick listener
    private void button12_onclick_listener(View view){
        switch (view.getId()){
            case R.id.test1_button1:
                util.quickLog("button 1 pressed");
                break;
            case R.id.test1_button2:
                util.quickLog("button 2 pressed");
                break;
        }
    }

    //button 34, onclicklistener variable
    private View.OnClickListener button34_onclick = v -> {
        switch (v.getId()){
            case R.id.test1_button3:
                util.quickLog("button 3 pressed");
                break;
            case R.id.test1_button4:
                util.quickLog("button 4 pressed");
                break;
        }
    };

    //TODO programmatically create a textview with margin in view created fragment
//    private TextView textView;
//    private ArrayList<TextView> textViewList = new ArrayList<>();

    //button5, spawn & show textview
    private void button5_onclick_listener(View view){
        util.quickLog("button 5 pressed");
        TextView tempView = view.getRootView().findViewById(R.id.test1_text1);
        tempView.setText("button 5 pressed");
        tempView.setVisibility(View.VISIBLE);

//        TextView textView;
//        textView = new TextView(getContext());
//        textView.setTextSize(16f);
//        textView.setTextColor(Color.BLUE);
//        textView.setBackgroundColor(Color.GRAY);
//        textView.setPadding(8,8,8,8);
//        textView.setText("button 5 pressed");
//        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
//        int qwer = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getResources().getDisplayMetrics());
//        params.setMargins(0,32,32,0);
//        textView.setLayoutParams(params);
//
//        ConstraintLayout constraintLayout = view.getRootView().findViewById(R.id.test1_frag);
//        ConstraintSet set = new ConstraintSet();
//        set.clone(constraintLayout);
////        util.quickLog(qwer);
////        set.connect(textView.getId(),ConstraintSet.TOP,R.id.test1_text1,ConstraintSet.BOTTOM, qwer);
//        set.applyTo(constraintLayout);
//
//        ((ConstraintLayout)view.getRootView().findViewById(R.id.test1_frag)).addView(textView,params);
//        textViewList.add(textView);
    }

    //button6, despawn & hide textview
    private void button6_onclick_listener(View view){
        util.quickLog("button 6 pressed");
        view.getRootView().findViewById(R.id.test1_text1).setVisibility(View.INVISIBLE);

//        textViewList.forEach(textView -> ((ConstraintLayout)view.getRootView().findViewById(R.id.test1_frag)).removeView(textView));
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setName("Test1");
    }
}
