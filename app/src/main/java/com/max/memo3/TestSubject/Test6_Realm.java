package com.max.memo3.TestSubject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.max.memo3.R;
import com.max.memo3.Util.util;
import com.max.memo3.databinding.Test6FragBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class Test6_Realm extends Fragment {
    //var
    private Test_ViewModel viewModel;
    private Test6FragBinding binding;

    //func
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Test_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.test6_frag,container,false);
        binding.setUselessInt(5);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setName("Test6");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.test6Button1.setOnClickListener(this::test6_button1_onclick);
    }

    private void test6_button1_onclick(View view){
        util.quickLog("qwer");
    }
}
