package com.max.memo3.TestSubject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.max.memo3.Background.MEMO3_BroadcastReceiver;
import com.max.memo3.MainActivity;
import com.max.memo3.R;
import com.max.memo3.Util.util;
import com.max.memo3.databinding.Test6FragBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;
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
        binding.test6Button4.setOnClickListener(this::test6_button4_onclick);
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
//        util.makeSnackbar("asdf");

        //notification
//        util.makeNotification("title","text");
//        util.makeNotification(new util.NotiBuilder("title","text")
//                .setOnPressAction("qaz",null)
//                .setBottomAction(new util.NotiBuilder.NotiBottomActionBuilder("wsx","label"),null,null)
//                .setProgress());

        //vibrator
        util.makeVibrate(500);
    }

    //gen 1 noti
    //skipped : group of notifications && notification channel group
    private void test6_button4_onclick(View view){
        try {
            NotificationManagerCompat manager = NotificationManagerCompat.from(getContext());
            String id = "qwer";
            //change in channel setting need clear app data
            NotificationChannel channel = new NotificationChannel(id,"Test 6", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("zxcv");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{300,4000,3000,1000});
            manager.deleteNotificationChannel(id);
            manager.createNotificationChannel(channel);

            int notiID = 1;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),id)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("title")
                    .setContentText("text")
                    .setContentInfo("info") //dont know what is this
                    .setSubText("subtext")
//                  .setContentText("hfauihgahwugjarghaurghaieurghiuaeguiagiaergbuiaegiuaghiaurgivbauhbioafigahbeguibaeiugrgbauiergbiaebglabg")
//                    .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("haighauwgeriweritgerigiegheuirgoeubhierhioghareuigegoahugoasgagohgiuhiuhfuhuihifguaofshdiauhsdfguihausihgoihuioaf"))
//                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    ;

            //style
//            builder.setStyle(new NotificationCompat.BigTextStyle()
//                    .bigText("sooooooooooooooooooooooo loooooooooooooooooooooooooooooonnnnnnnnnnnnnnnng")
//                    .setBigContentTitle("big title")
//                    .setSummaryText("summary"));
            Person person = new Person.Builder().setName("it's me").build();
//            builder.setStyle(new NotificationCompat.MessagingStyle(person)
//                    .setConversationTitle("conversation title")
//                    .addMessage("1",500000,person)
//                    .addMessage("2",System.currentTimeMillis(), (Person) null)
//                    );
//            builder.setStyle(new NotificationCompat.InboxStyle()
//                    .setBigContentTitle("content title")
//                    .setSummaryText("summary")
//                    .addLine("line1")
//                    .addLine("line2")
//                    );

            //directly start activity onpress
//            Intent intent = new Intent(getContext(),Test0_Main.class);
//            intent.putExtra("qwer","tyui");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
//            stackBuilder.addNextIntentWithParentStack(intent);
//            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingIntent);

            //on press action
            Intent intent = new Intent(getContext(), MEMO3_BroadcastReceiver.class);
            intent.setAction("test6_1");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            //action 1
            Intent actionIntent = new Intent(getContext(), MEMO3_BroadcastReceiver.class);
            actionIntent.setAction("test6_2");
            actionIntent.putExtra("qaz","qaz");
            actionIntent.putExtra("notiid",notiID);
            PendingIntent actionPendingIntent = PendingIntent.getBroadcast(getContext(),0,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action = new NotificationCompat.Action(0,"action1",actionPendingIntent);
//            builder.addAction(action);
//            builder.addAction(0,"action1",actionPendingIntent);

            //action 2
            Intent actionIntent2 = new Intent(getContext(), MEMO3_BroadcastReceiver.class);
            actionIntent2.setAction("test6_3");
            actionIntent2.putExtra("notiid",notiID);
            PendingIntent actionPendingIntent2 = PendingIntent.getBroadcast(getContext(),0,actionIntent2,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action2 = new NotificationCompat.Action(0,"action2",actionPendingIntent2);
//            builder.addAction(action2);
//            builder.addAction(0,"action2",actionPendingIntent2);

            //action 3
//            Intent actionIntent3 = new Intent(getContext(), MEMO3_BroadcastReceiver.class);
//            actionIntent3.setAction("test6_4");
//            actionIntent3.putExtra("notiid",notiID);
//            PendingIntent actionPendingIntent3 = PendingIntent.getBroadcast(getContext(),0,actionIntent3,PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Action action3 = new NotificationCompat.Action(0,"action3",actionPendingIntent3);
//            builder.addAction(action3);
//            builder.addAction(0,"action3",actionPendingIntent3);

            //action 4
            //max 3 actions
//            Intent actionIntent4 = new Intent(getContext(), MEMO3_BroadcastReceiver.class);
//            actionIntent4.setAction("test6_5");
//            actionIntent4.putExtra("notiid",notiID);
//            PendingIntent actionPendingIntent4 = PendingIntent.getBroadcast(getContext(),0,actionIntent4,PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Action action4 = new NotificationCompat.Action(0,"action4",actionPendingIntent4);
//            builder.addAction(action4);

            //reply button
            String s = "test6_text";
            RemoteInput remoteInput = new RemoteInput.Builder(s)
                    .setLabel("label desu")
                    .build();
            Intent replyIntent = new Intent(getContext(),MEMO3_BroadcastReceiver.class);
            replyIntent.putExtra("notiid",notiID);
            replyIntent.setAction("test6_reply");
            PendingIntent replyPendingIntent = PendingIntent.getBroadcast(getContext(),5,replyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(0,"reply",replyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(true)
                    .build();
//            builder.addAction(replyAction);

            //color
            builder.setColorized(true);
            builder.setColor(0x00bfff); //a doesn't matter

            //history
            //index 0 = bottom, max history == 3, max == 5 in messaging style
            CharSequence[] history = {"qwer","asdf","zxv","qw","as","zx","er","df","cv"};
            builder.setRemoteInputHistory(history);

            //badge
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);

            //progress
//            builder.setProgress(100,0,false);
            //for indeterminate, max & progress not matter
//            builder.setProgress(100,0,true);

            //set non dismissible
//            builder.setOngoing(true);

            //set importance (is this working?)
            //high = head up
//            builder.setPriority(NotificationManager.IMPORTANCE_HIGH);

            //urgent (not working)
//            Intent urgentIntent = new Intent(getContext(),Test0_Main.class);
//            PendingIntent urgentPendingIntent = PendingIntent.getActivity(getContext(),0,urgentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setFullScreenIntent(urgentPendingIntent,true);

            //lock screen visibility
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            //set auto dismiss after timeout
//            builder.setTimeoutAfter(5000);

            //vibration  (set vibration in channel)
//            builder.setVibrate(new long[]{500,3000,3000,3000,500});

//            util.makeCountDownTimer_Xinterval(5000, new util.CountDownTimerImplementation() {@Override public void onTick(long ms_untilFinish) {}@Override public void onFinish() {manager.notify(notiID,builder.build());}});
            manager.notify(notiID,builder.build());

            //progress update
//            new Thread(() -> {
//                for (int i = 1; i < 101; i++) {
//                    try {
//                        Thread.sleep(25);
//                        builder.setProgress(100,i,false);
//                        manager.notify(notiID,builder.build());
//                        Thread.sleep(25);
//                    } catch (InterruptedException e) {
//                        util.log(e.getMessage());
//                    }
//                }
//                builder.setProgress(0,0,false);
//                manager.notify(notiID,builder.build());
//            }).start();
        } catch (Throwable e){
            util.log(e.getMessage());
        }
    }
}
