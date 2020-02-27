package com.max.memo3.TestSubject;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.max.memo3.Background.MEMO3_BroadcastReceiver;
import com.max.memo3.Background.MEMO3_Service;
import com.max.memo3.R;
import com.max.memo3.Util.util;
import com.max.memo3.databinding.Test7FragBinding;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Intent.EXTRA_CHOSEN_COMPONENT;

public class Test7_Background extends Fragment {
    //var
    private Test_ViewModel viewModel;
    private Test7FragBinding binding;

    //func
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Test_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.test7_frag,container,false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setName("Test7");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.test7Button1.setOnClickListener(this::test7_button1_onclick);
        binding.test7Button2.setOnClickListener(this::test7_button2_onclick);
        binding.test7Button3.setOnClickListener(this::test7_button3_onclick);
        binding.test7Button4.setOnClickListener(this::test7_button4_onclick);
        binding.test7Button5.setOnClickListener(this::test7_button5_onclick);
        binding.test7Button6.setOnClickListener(this::test7_button6_onclick);
        binding.test7Button7.setOnClickListener(this::test7_button7_onclick);
        binding.test7Button8.setOnClickListener(this::test7_button8_onclick);
        binding.test7Button9.setOnClickListener(this::test7_button9_onclick);
    }

    //start service
    private void test7_button1_onclick(View view){
//        util.log("qwer");
        getContext().startService(new Intent(getContext(), MEMO3_Service.class));
    }

    //stop service
    private void test7_button2_onclick(View view){
//        util.log("qwer");
        getContext().stopService(new Intent(getContext(),MEMO3_Service.class));
    }

    //binding
    private MEMO3_Service.MEMO3_ServiceBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            util.log(name.flattenToString()+" connected");
            binder = (MEMO3_Service.MEMO3_ServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            util.log(name.flattenToString()+" disconnected");
//            binder = null;
        }
    };
    //bind service
    private void test7_button3_onclick(View view){
//        util.log("qwer");
        getContext().bindService(new Intent(getContext(), MEMO3_Service.class),connection, Context.BIND_AUTO_CREATE);
    }

    //unbind service
    private void test7_button4_onclick(View view){
//        util.log("qwer");
//        util.log(connection==null);
        try {
            getContext().unbindService(connection);
        } catch (Throwable e){
            util.log(e.getMessage());
        }
    }

    //try bind
    private void test7_button5_onclick(View view){
//        util.log("qwer");
        if (binding!=null){
            util.log(binder.get());
        }
    }

    //work manager
    public static class TestWorker extends Worker{
        public TestWorker(Context context, WorkerParameters parameters){
            super(context,parameters);
            util.log("zxcv");
        }
        @NonNull
        @Override
        public Result doWork() {
            for (int i = 0; i < 5; i++) {
                util.log("asdf");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return Result.success();
        }
    }
    private void test7_button6_onclick(View view){
        util.log("qwer");
        Constraints constraints = new Constraints.Builder()
                .setRequiresDeviceIdle(false)
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(TestWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(5,TimeUnit.SECONDS)
                .build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(TestWorker.class,1, TimeUnit.SECONDS)
                .setInitialDelay(5,TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(getContext().getApplicationContext()).enqueue(request);
//        WorkManager.getInstance(getContext().getApplicationContext())
//                .getWorkInfoByIdLiveData(request.getId())
//                .addListener(() -> util.log("sth"),Runnable::run);
//                .observe(getViewLifecycleOwner(),workInfo -> util.log("qwer"));

        util.log(request.getId());
        WorkManager.getInstance(getContext().getApplicationContext()).cancelWorkById(request.getId());
    }

    //sub broadcast receiver
    //declare BR in manifest la, dont store runnable la
    public static class Test7_BR extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            util.log("test7 received");
//            Intent intent1 = new Intent(context, Test0_Main.class);
//            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
            ComponentName clickedComponent = intent.getParcelableExtra(EXTRA_CHOSEN_COMPONENT);
            util.log(clickedComponent.flattenToString());
        }
    }
    private void test7_button7_onclick(View view){
//        util.log("qwer");
//        util.makeNotification(new util.NotiBuilder("test7 title","test7 text")
//                .setOnPressAction("test7",Test7_BR.class)
//                .setBottomAction(new util.NotiBuilder.NotiBottomActionBuilder("action","label"),null,null));
        util.makeWork_enqueue(TestWorker.class);
    }

    private void test7_button8_onclick(View view){
//        util.log("qwer");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "This is the text to share.");
        intent.setType("text/plain");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,new Intent(getContext(), Test7_BR.class),PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent1 = Intent.createChooser(intent,"title",pendingIntent.getIntentSender());
        startActivity(intent1);
    }

    //get device data
    private void test7_button9_onclick(View view){
        util.getDevice_NetworkData();
//        util.makeThread(() -> {
//            try {
//                Scanner s = new Scanner(new URL("https://api.ipify.org").openStream(), "UTF-8").useDelimiter("\\A");
//                util.log("My current IP address is " + s.next());
//            } catch (Throwable e) {
//                util.log(e.toString());
//            }
//        });
    }
}
