package com.max.memo3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.max.memo3.TestSubject.Test0_Main;
import com.max.memo3.Util.util;
import com.max.memo3.databinding.MainFragBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {
    //var
    private MainFragBinding binding;

    //func
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.main_frag,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.mainButton.setOnClickListener(v -> {
            util.log("pressed main button");
            startActivity(new Intent(getContext(), Test0_Main.class));
        });
    }
}
