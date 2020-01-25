package com.max.memo3.TestSubject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.max.memo3.R;
import com.max.memo3.Util.util;
import com.max.memo3.databinding.Test6FragBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

//also for util test
public class Test6_Realm extends Fragment {
    //var
    private Test_ViewModel viewModel;
    private Test6FragBinding binding;
    private Realm realm;

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

        Realm.init(getActivity().getApplicationContext());
//        realm = Realm.getDefaultInstance();
        realm = Realm.getInstance(
                new RealmConfiguration.Builder()
                        .name("test6_config")
                        .inMemory()
                        .build()
                );

        binding.test6Button1.setOnClickListener(this::test6_button1_onclick);
        binding.test6Button2.setOnClickListener(this::test6_button2_onclick);
        binding.test6Button3.setOnClickListener(this::test6_button3_onclick);
    }

    //realm
    private void test6_button1_onclick(View view){
        util.log("qwer");

        Test6_RealmObject1 realmObject1 = new Test6_RealmObject1();
        realmObject1.setInt1(2);
        realmObject1.setS1("qaz");
        util.log(realmObject1.getS1());

        //compare with non existing field cause crash
        RealmResults<Test6_RealmObject1> results1;
        try {
            results1 = realm.where(Test6_RealmObject1.class).lessThan("int1",5).findAll();
            util.log(results1.size());

            results1.addChangeListener((test6_realmObject1s, changeSet) -> util.log("results1 changed "+changeSet.getInsertions().length));
        } catch (Throwable e){
            util.log(e.getMessage());
        }

        realm.beginTransaction();
        Test6_RealmObject1 managed_realmObject1 = realm.copyToRealm(realmObject1);
        Test6_RealmObject1 managed_realmObject2 = realm.createObject(Test6_RealmObject1.class);
        realm.commitTransaction();

        RealmResults<Test6_RealmObject1> results2 = realm.where(Test6_RealmObject1.class).lessThan("int1",5).findAll();
        util.log(results2.size());

        realm.executeTransactionAsync(realm -> realm.createObject(Test6_RealmObject1.class), () -> util.log("async transaction ed"));
    }

    private void test6_button2_onclick(View view){
        util.log("qwer");
        realm.executeTransaction(realm1 -> {
            realm1.deleteAll();
            util.log("realm deleted all data");
        });
    }

    //util test
    private void test6_button3_onclick(View view){
        //toast
//        util.makeToast_repeat(getActivity(),"qwer",5000,3);

        //log
        //toast log
//        util.makeToastLog("qwer");

        //snackbar
//        View view1 = getView()
        util.log(getView());
        //best
        util.log((ViewGroup)((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0));
        util.log(getActivity().getWindow().getDecorView().findViewById(android.R.id.content));
        util.log((ViewGroup)((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0).getRootView());
        Snackbar snackbar = Snackbar.make(
//                getView(),
                (ViewGroup)((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0),
                "qwer",Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
