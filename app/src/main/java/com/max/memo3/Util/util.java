package com.max.memo3.Util;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.max.memo3.Background.Broadcast_Receiver;
import com.max.memo3.Background.MEMO3_BroadcastReceiver;
import com.max.memo3.Background.SP_service;
import com.max.memo3.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

//all copy from MEMO
//firebase and google signin disabled

//TOAST, snackbar, vibrator, notification, alarm manager & date time, log, exception
public class util {
    //BASE
    public static Context APP_CONTEXT;
    public static FragmentActivity CURR_ACTIVITY;   //fragment act || act
    public static Context CURR_CONTEXT;
    public static View CURR_VIEW;  //view || view group
    private static boolean AppContextseted = false;
    public static void setAppContext(Context context){
        if (!AppContextseted){
            APP_CONTEXT = context;
            AppContextseted = true;
        }
    }
    public static void setCurrActivity(FragmentActivity activity){
        CURR_ACTIVITY = activity;
        CURR_CONTEXT = activity;
        CURR_VIEW = (ViewGroup)((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    //LIFECYCLE
    private static boolean isInited = false;
    public static void INIT(){
        if (isInited){
            return;
        } else {
            init_Notification();
            init_Vibrator();

            init_Camera();
            init_BroadcastReceiver();
            init_Calender();
            init_Scheduler();
//        init_Realm();
//        init_Firestore();
            init_Service();
            init_Sensor();
            isInited = true;
        }
    }
    public static void PAUSE(){
        stopToast();
        stopSnackbar();
        stopVibrator();
    }
//    public static void DESTROY(){
//        destroyRealm();
//    }

    //TOAST
    private static Toast TOAST;
    public static void makeToast_full(Context context, CharSequence theThing, int duration){
        TOAST = Toast.makeText(context,theThing!=null?theThing:"Null",duration);
        TOAST.show();
    }
    public static void makeToast(Context context, CharSequence theThing){
        stopToast();
        makeToast_full(context,theThing,Toast.LENGTH_SHORT);
    }
    public static void makeToast(CharSequence theThing){makeToast(CURR_CONTEXT,theThing);}
    public static void makeToast_diffActivity(FragmentActivity activity, CharSequence theThing){
        activity.runOnUiThread(() -> makeToast(theThing));
    }
    public static void makeToast_later(FragmentActivity activityToRunOn, CharSequence theThing, long delay){
        makeTimer(delay, new TimerTask() {
            @Override
            public void run() {makeToast_diffActivity(activityToRunOn,theThing);}
        });
    }
    public static void makeToast_repeat(FragmentActivity activity, CharSequence theThing, long interval, int repeat){
        makeCountDownTimer(interval * repeat, interval, new CountDownTimerImplementation() {
            @Override public void onTick(long ms_untilFinish) {makeToast_diffActivity(activity,theThing);}
            @Override public void onFinish() {}
        });
    }
    public static void stopToast(){
        if (TOAST == null){return;}
        TOAST.cancel();
        TOAST = null;
    }

    //LOG
    public static final String LOG_TAG = "gg<3";
    public enum SERIOUS_LEVEL {VERBOSE,DEBUG,INFO,WARN,ERROR,ASSERT}
    public static void makeLog(SERIOUS_LEVEL level, String message){
        switch (level){
            case VERBOSE:Log.v(LOG_TAG,message);break;
            case DEBUG:Log.d(LOG_TAG,message);break;
            case INFO:Log.i(LOG_TAG,message);break;
            case WARN:Log.w(LOG_TAG,message);break;
            case ERROR:Log.e(LOG_TAG,message);break;
            case ASSERT:Log.wtf(LOG_TAG,message);break;
        }
    }
    public static void log(String message){
        makeLog(SERIOUS_LEVEL.DEBUG,message!=null?message:"Null");
    }
    public static void log(int message){log(Integer.toString(message));}
    public static void log(long message){log(Long.toString(message));}
    public static void log(boolean message){log(message?"TRUE":"FALSE");}
    public static void log(Object message){log(message.toString());}
    public static void log(){log("log");}

    //TOAST + LOG
    //no toast in other thread or fire later
    public static void makeToastLog(Context context,SERIOUS_LEVEL level, String message){
        makeToast(context,message);
        makeLog(level,message);
    }
    public static void makeToastLog(SERIOUS_LEVEL level, String message){makeToastLog(CURR_CONTEXT,level,message);}
    public static void makeToastLog(Context context, String message){makeToastLog(context,SERIOUS_LEVEL.DEBUG,message);}
    public static void makeToastLog(String message){
        makeToastLog(CURR_CONTEXT,SERIOUS_LEVEL.DEBUG,message);
    }

    //SNACKBAR
    //TODO custom snackbar
    private static Snackbar SNACKBAR;
    private static boolean snackbar_isShowing = false;
    public static void makeSnackbar_full(View view, CharSequence theThing, CharSequence actionStr, SnackbarOnClick snackbarOnClick, SnackbarCallback... snackbarCallbacks){
        stopSnackbar();
        SNACKBAR = Snackbar.make(view, theThing, Snackbar.LENGTH_SHORT);
        if (actionStr != null && snackbarOnClick != null) {
            SNACKBAR.setAction(actionStr, snackbarOnClick);
        }
        SNACKBAR.addCallback(snackbar_Callback_Base);
        if (snackbarCallbacks!=null) {
            for (SnackbarCallback snackbarCallback : snackbarCallbacks) {
                SNACKBAR.addCallback(snackbarCallback);
            }
        }
        SNACKBAR.show();
    }
    public static void makeSnackbar(View view, CharSequence theThing){makeSnackbar_full(view,theThing,null,null, (SnackbarCallback[]) null);}
    public static void makeSnackbar(CharSequence theThing){makeSnackbar(CURR_VIEW,theThing);}
    public static void stopSnackbar(){
        if (SNACKBAR !=null){
            SNACKBAR.dismiss();
            SNACKBAR = null;
            snackbar_isShowing = false;
        } else {
            snackbar_isShowing = false;
        }
    }
    public static boolean isSnackbarShowing(){return snackbar_isShowing;}

    public static abstract class SnackbarOnClick implements View.OnClickListener{
        @Override
        public abstract void onClick(View v);
    }
    public static abstract class SnackbarCallback extends Snackbar.Callback{
        @Override
        public abstract void onShown(Snackbar sb);
        @Override
        public abstract void onDismissed(Snackbar transientBottomBar, int event);
    }
    private static final Snackbar.Callback snackbar_Callback_Base = new SnackbarCallback() {
        @Override public void onShown(Snackbar sb) {
            snackbar_isShowing = true;
//            log("snackbar show");
        }
        @Override public void onDismissed(Snackbar transientBottomBar, int event) {
            snackbar_isShowing = false;
//            log("snackbar dismiss");
        }
    };

    //NOTIFICATION
    private static NotificationManagerCompat notiManager;
    public static final String NOTI_CHANNELID_DEFAULT = "MEMO3_NOTICHANNEL_ID";
    private static final String NOTI_ID_KEY_IN_EXTRAS = "noti_id_key_in_extras";
    private static AtomicInteger notiCount = new AtomicInteger(1);
    private static void init_Notification(){
        //get noti manager
        notiManager = NotificationManagerCompat.from(APP_CONTEXT);

        //make default channel
        NotificationChannel defaultChannel = new NotificationChannel(NOTI_CHANNELID_DEFAULT,"Default Channel", NotificationManager.IMPORTANCE_HIGH);
        defaultChannel.setDescription("Default Notification Channel for MEMO3");
        defaultChannel.enableVibration(true);
        defaultChannel.enableLights(false);
        defaultChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notiManager.createNotificationChannel(defaultChannel);
    }
    public static void makeNotification(NotiBuilder notiBuilder){
        notiManager.notify(notiBuilder.notiID,notiBuilder.build());
    }
    public static void makeNotification(CharSequence title, CharSequence text){
        makeNotification(new NotiBuilder(title,text));
    }
    public static void makeNotification(Notification notification){
        notiManager.notify(notiCount.incrementAndGet(),notification);
    }
    public static class NotiBuilder{
        private static final int NOTI_DEFAULT_COLOUR = 0x00bfff;
        private NotificationCompat.Builder builder;
        private int notiID;
        public NotiBuilder(@NonNull CharSequence contentTitle,@NonNull CharSequence contentText){
            notiID = notiCount.incrementAndGet();
            builder = new NotificationCompat.Builder(APP_CONTEXT,NOTI_CHANNELID_DEFAULT);
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
            builder.setSmallIcon(R.drawable.ic_stat_name);
            builder.setContentTitle(contentTitle);
            builder.setContentText(contentText);
            builder.setColorized(true);
            builder.setColor(NOTI_DEFAULT_COLOUR);
            builder.setAutoCancel(true);
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
            builder.setOngoing(false);
        }

        public NotiBuilder setSubText(CharSequence subText){builder.setSubText(subText);return this;}
        public NotiBuilder setStyle(NotificationCompat.Style style){builder.setStyle(style);return this;}
        public NotiBuilder setAutoCancel(boolean isAutoCancel){builder.setAutoCancel(isAutoCancel);return this;}
        public NotiBuilder setOnPressAction(String action, Runnable runnable, Bundle extraBundle){
            Intent intent = new Intent(APP_CONTEXT, MEMO3_BroadcastReceiver.class);
            intent.setAction(action);
            intent.putExtra(NOTI_ID_KEY_IN_EXTRAS,notiID);
            if (extraBundle != null) {
                intent.putExtras(extraBundle);
            }
            PendingIntent pendingIntent = PendingIntent.getBroadcast(APP_CONTEXT,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            //TODO move to service
//            MEMO3_BroadcastReceiver.setActionMapElement(action,runnable);
            return this;
        }
        public NotiBuilder setOnPressAction(String action, Runnable runnable){return setOnPressAction(action,runnable,null);}
        public NotiBuilder setBottomAction(NotiBottomActionBuilder action1, NotiBottomActionBuilder action2, NotiBottomActionBuilder action3){
            if (action1 != null){
                action1.setNotiID(this.notiID);
                builder.addAction(action1.build());
            }
            if (action2 != null){
                action2.setNotiID(this.notiID);
                builder.addAction(action2.build());
            }
            if (action3 != null){
                action3.setNotiID(this.notiID);
                builder.addAction(action3.build());
            }
            return this;
        }
        public static class NotiBottomActionBuilder{
            int notiId;
            CharSequence action;
            CharSequence notiLabel;
            Bundle extraBundle = null;
            public NotiBottomActionBuilder(@NonNull CharSequence action,@NonNull CharSequence notiLabel){
                this.action = action;
                this.notiLabel = notiLabel;
            }
            protected NotiBottomActionBuilder setNotiID(int ID){this.notiId = ID;return this;}
            public NotiBottomActionBuilder setExtraBundle(Bundle extraBundle){this.extraBundle = extraBundle;return this;}
            protected NotificationCompat.Action build(){
                Intent intent = new Intent(APP_CONTEXT,MEMO3_BroadcastReceiver.class);
                intent.setAction(action.toString());
                intent.putExtra(NOTI_ID_KEY_IN_EXTRAS,notiId);
                if (extraBundle != null){
                    intent.putExtras(extraBundle);
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(APP_CONTEXT,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action(0, notiLabel,pendingIntent);
            }
        }
        public static class NotiBottomActionRemoteInputBuilder extends NotiBottomActionBuilder{
            private CharSequence resultKey;
            private CharSequence label;
            public NotiBottomActionRemoteInputBuilder(@NonNull CharSequence action,@NonNull CharSequence notiTitle,@NonNull CharSequence resultKey,@NonNull CharSequence label){
                super(action, notiTitle);
                this.resultKey = resultKey;
                this.label = label;
            }
            @Override
            protected NotificationCompat.Action build(){
                RemoteInput remoteInput = new RemoteInput.Builder(resultKey.toString()).setLabel(label).build();
                Intent intent = new Intent(APP_CONTEXT,MEMO3_BroadcastReceiver.class);
                intent.setAction(action.toString());
                intent.putExtra(NOTI_ID_KEY_IN_EXTRAS,notiId);
                if (extraBundle != null){
                    intent.putExtras(extraBundle);
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(APP_CONTEXT,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                return new NotificationCompat.Action.Builder(0, notiLabel,pendingIntent).addRemoteInput(remoteInput).setAllowGeneratedReplies(false).build();
            }
        }
        public NotiBuilder setHistory(CharSequence[] history){builder.setRemoteInputHistory(history);return this;}
        public NotiBuilder setProgress(){builder.setProgress(0,0,true);return this;}
        public NotiBuilder setProgress(int max, int now){builder.setProgress(max,now,false);return this;}
        public NotiBuilder setOngoing(boolean isOngoing){builder.setOngoing(isOngoing);return this;}
        public NotiBuilder setTimeoutAfter(long ms){builder.setTimeoutAfter(ms);return this;}
        private Notification build(){return builder.build();}
    }

    //VIBRATOR  (max amplitude = 255)
    private static Vibrator vibrator;
    private static void init_Vibrator(){vibrator = (Vibrator) APP_CONTEXT.getSystemService(Context.VIBRATOR_SERVICE);}
    public static void makeVibrate_full(long ms, int amplitude){
        stopVibrator();
        vibrator.vibrate(VibrationEffect.createOneShot(ms,amplitude));
    }
    public static void makeVibrate(long ms){makeVibrate_full(ms,VibrationEffect.DEFAULT_AMPLITUDE);}
    public static boolean makeVibrate(long[] ms, int[] amplitude, int repeat){
        if (ms.length!=amplitude.length){
            return false;
        }
//        vibrator.cancel();
        stopVibrator();
        vibrator.vibrate(VibrationEffect.createWaveform(ms,amplitude,repeat));
        return true;
    }
    public static boolean makeVibrate(long[] ms, int repeat){
//        vibrator.cancel();
        stopVibrator();
        vibrator.vibrate(VibrationEffect.createWaveform(ms,repeat));
        return true;
    }
    public static void stopVibrator(){vibrator.cancel();}

    //COUNT DOWN TIMER
    //interval will run once on start
    public static abstract class CountDownTimerImplementation{
        public abstract void onTick(long ms_untilFinish);
        public abstract void onFinish();
    }
    public static void makeCountDownTimer(long duration_ms, long interval_ms, CountDownTimerImplementation implementation){
        new CountDownTimer(duration_ms,interval_ms) {
            @Override
            public void onTick(long millisUntilFinished) {
                implementation.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                implementation.onFinish();
            }
        }.start();
    }
    public static void makeCountDownTimer_Xinterval(long duration_ms, CountDownTimerImplementation implementation){
        new CountDownTimer(duration_ms,duration_ms+5) {
            @Override
            public void onTick(long millisUntilFinished) {
                implementation.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                implementation.onFinish();
            }
        }.start();
    }

    //TIMER
    //daemon = end autoly if all other non-daemon ended
    public static void makeTimer(long delay, TimerTask timerTask){
        new Timer(false).schedule(timerTask,delay);
    }
    public static void makeTimer_daemon(long delay, TimerTask timerTask){
        new Timer(true).schedule(timerTask,delay);
    }

    //CAMERA + FLASH
    //TODO camera later
    private static CameraManager cameraManager;
    private static String flashlightID;
    private static boolean flashOnNow = false;
    private static void init_Camera() {
        cameraManager = (CameraManager) APP_CONTEXT.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (APP_CONTEXT.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                flashlightID = cameraManager.getCameraIdList()[0];
            } else {
                makeToast("no flashlight on this device");
            }
        } catch(CameraAccessException e){
            e.printStackTrace();
        }
    }
    public static void makeFlash(boolean set_to_on){
        if (flashlightID==null){
            return;
        }
        try {
            cameraManager.setTorchMode(flashlightID,set_to_on);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        flashOnNow = set_to_on;
    }
    public static void makeFlash_ON(){
        makeFlash(true);
    }
    public static void makeFlash_OFF(){
        makeFlash(false);
    }
    public static void makeFlash_toggle(){
        flashOnNow = !flashOnNow;
        makeFlash(flashOnNow);
    }

    //BROADCAST RECEIVER
    private static Broadcast_Receiver broadcast_receiver;
//    private static LocalBroadcastManager localBroadcastManager;
    private static final String[] ACTIONS_for_BROADCAST_RECEIVER = {
//            ConnectivityManager.CONNECTIVITY_ACTION,
    };
    private static void init_BroadcastReceiver(){
        broadcast_receiver = new Broadcast_Receiver();
        for (String action : ACTIONS_for_BROADCAST_RECEIVER) {
            CURR_CONTEXT.registerReceiver(broadcast_receiver,new IntentFilter(action));
        }
//        localBroadcastManager = LocalBroadcastManager.getInstance(APP_CONTEXT);
    }
    public static void register_BR(String action){
        broadcast_receiver.registerAction(action);
    }
    public static void unregister_BR(String action){
        broadcast_receiver.unRegisterAction(action);
    }

    //DATE TIME PICKER
    private static Calendar calendar;
    public static Calendar getCalender(){
        return calendar;
    }
    private static void init_Calender(){
        calendar = new Calendar.Builder().build();
    }
    public static void makeTimePicker(){
        final Calendar calendar_now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(CURR_CONTEXT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
            }
        },calendar_now.get(Calendar.HOUR_OF_DAY),calendar_now.get(Calendar.MINUTE),true);
        timePickerDialog.show();
    }
    public static void makeTimePicker(final String action){
        final Calendar calendar_now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(CURR_CONTEXT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                LocalBroadcastManager.getInstance(CURR_CONTEXT).sendBroadcast(new Intent(action));
            }
        },calendar_now.get(Calendar.HOUR_OF_DAY),calendar_now.get(Calendar.MINUTE),true);
        timePickerDialog.show();
    }
    public static void makeDatePicker(){
        final Calendar calendar_now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(CURR_CONTEXT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            }
        },calendar_now.get(Calendar.YEAR),calendar_now.get(Calendar.MONTH),calendar_now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    public static void makeDatePicker(final String action){
        final Calendar calendar_now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(CURR_CONTEXT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                LocalBroadcastManager.getInstance(CURR_CONTEXT).sendBroadcast(new Intent(action));
            }
        },calendar_now.get(Calendar.YEAR),calendar_now.get(Calendar.MONTH),calendar_now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    public static void makeDateTimePicker(){
        makeDatePicker();
        makeTimePicker();
    }

    //SCHEDULER
    private static AlarmManager alarmManager;
    private static JobScheduler jobScheduler;
    private static void init_Scheduler(){
        alarmManager = (AlarmManager) APP_CONTEXT.getSystemService(Context.ALARM_SERVICE);
        jobScheduler = (JobScheduler) APP_CONTEXT.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }
    public static int makeSchedule_AlarmManager(Intent intent, BroadcastReceiver receiver, Calendar fire_time){
        Custom_Scheduler_Record_AlarmManager custom_scheduler_record_alarmManager = new Custom_Scheduler_Record_AlarmManager(intent.getAction(),fire_time);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(custom_scheduler_record_alarmManager.getAction());
        APP_CONTEXT.registerReceiver(receiver,intentFilter);
        PendingIntent pendingIntent = custom_scheduler_record_alarmManager.makePendingIntent(intent);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,fire_time.getTimeInMillis(),pendingIntent);
        addToRealm(custom_scheduler_record_alarmManager);
        return custom_scheduler_record_alarmManager.getRequestCode();
    }
    public static int makeSchedule_AlarmManager_Repeat(Intent intent, final BroadcastReceiver receiver, final Calendar fire_time, final int interval_s_int){
        Custom_Scheduler_Record_AlarmManager custom_scheduler_record_alarmManager = new Custom_Scheduler_Record_AlarmManager(intent.getAction(),fire_time);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(custom_scheduler_record_alarmManager.getAction());
        APP_CONTEXT.registerReceiver(receiver,intentFilter);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fire_time.add(Calendar.SECOND,interval_s_int);
                makeSchedule_AlarmManager_Repeat(intent,receiver,fire_time,interval_s_int);
            }
        };
        APP_CONTEXT.registerReceiver(broadcastReceiver,intentFilter);
        PendingIntent pendingIntent = custom_scheduler_record_alarmManager.makePendingIntent(intent);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,fire_time.getTimeInMillis(),pendingIntent);
        addToRealm(custom_scheduler_record_alarmManager);
        return custom_scheduler_record_alarmManager.getRequestCode();
    }
    private static final long DEFAULT_MAX_DELAY_JOBSERVICE = 100;
    public static int makeSchedule_JobService(String action, Calendar fire_time, @Nullable Long max_delay_ms, @Nullable PersistableBundle extras){
        if (max_delay_ms==null){
            max_delay_ms = DEFAULT_MAX_DELAY_JOBSERVICE;
        }
        Custom_Scheduler_Record_JobService record = new Custom_Scheduler_Record_JobService(action,fire_time,max_delay_ms);
        JobInfo jobInfo = record.makeJobInfo(extras);
        jobScheduler.schedule(jobInfo);
        addToRealm(record);
        return jobInfo.getId();
    }
    public static int makeSchedule_JobService_Repeat(String action, Calendar fire_time, @Nullable Long max_delay_ms, @Nullable PersistableBundle extras){
//        if (max_delay_ms==null){
//            max_delay_ms = DEFAULT_MAX_DELAY_JOBSERVICE;
//        }
//        Custom_Scheduler_Record_JobService record = new Custom_Scheduler_Record_JobService(action,fire_time,max_delay_ms);
//        JobInfo jobInfo = record.makeJobInfo_repeat(extras);
//        jobScheduler.schedule(jobInfo);
//        addToRealm(record);
//        return jobInfo.getId();

        //dont work, dont know why
        return 0;
    }
    public static boolean cancelSchedule_AlarmManager(int requestCode){
        //query it
        final RealmResults<Custom_Scheduler_Record_AlarmManager> results =
                realm.where(Custom_Scheduler_Record_AlarmManager.class)
                .equalTo("requestCode",requestCode)
                .findAll();
        if (results.size()!=1){
            return false;
        }
        //cancel alarm
        PendingIntent pendingIntent = results.get(0).makePendingIntent();
        alarmManager.cancel(pendingIntent);
        //remove it from realm
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
        return true;
    }
    public static boolean cancelSchedule_JobService(int jobID){
        jobScheduler.cancel(jobID);
        return true;
    }

    //REALM DATABASE
    private static Realm realm;
    //schema version = 12/7/2019 15:00
    private static void init_Realm(){
        Realm.init(APP_CONTEXT);
        RealmConfiguration config = new RealmConfiguration.Builder()
//                .schemaVersion(3)
//                .migration(new Custom_Realm_Migration())
                .deleteRealmIfMigrationNeeded()     //for developing use
                .build();
        realm = Realm.getInstance(config);
    }
    public static Realm getRealm() {
        return realm;
    }
    public static <T extends RealmObject> void addToRealm(final T objectToAdd){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(objectToAdd);
//                util.log(objectToAdd.toString());
            }
        });
    }
    public static <T extends RealmObject> void deleteFromRealm(final T objectToDelete){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                objectToDelete.deleteFromRealm();
            }
        });
    }
    private static void destroyRealm(){
        realm.close();
    }
    public static void resetRealm(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }

    //FIRESTORE + GOOGLE ACCOUNT
    //beware of change of sha1 key, need update at 1)google credential, 2)firebase auth page (w new google-service.json), 3)id token from google credential
    //get && delete implemented later (later == some time in future)
//    private static FirebaseFirestore firestore;
//    private static GoogleSignInClient signInClient;
//    private static GoogleSignInAccount signInAccount;   //google signed in account
//    private static FirebaseAuth firebaseAuth;
//    private static FirebaseUser firebaseUser;   //firebase signed in account
//    public static final int REQUEST_CODE_FIRESTORE_SIGNIN = 69;
//    private static final String GOOGLE_SIGNIN_ID_TOKEN = "619739072240-shbi9bvto73c666f4buluoqvhd4e2v1j.apps.googleusercontent.com";
//    private static void init_Firestore(){
//        firestore = FirebaseFirestore.getInstance();
//        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(GOOGLE_SIGNIN_ID_TOKEN)
//                .requestEmail()
//                .build();
//        signInClient = GoogleSignIn.getClient(APP_CONTEXT,signInOptions);
//        signInAccount = GoogleSignIn.getLastSignedInAccount(APP_CONTEXT);
//    }
//
//    public static FirebaseFirestore getFirestore() {
//        return firestore;
//    }
//
//    public static SignInButton init_SignIn_Button(final Activity activity, SignInButton signInButton, int button_id){
//        signInButton = activity.findViewById(button_id);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = signInClient.getSignInIntent();
//                activity.startActivityForResult(intent,REQUEST_CODE_FIRESTORE_SIGNIN);
//                log("signing into google now");
//            }
//        });
//        return signInButton;
//    }
//    public static void after_SignIn_Button(Activity activity, Intent intent){
//        try {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
//            signInAccount = task.getResult(ApiException.class);
//            firebaseAuth = FirebaseAuth.getInstance();
//            firebaseUser = firebaseAuth.getCurrentUser();
//            if (firebaseUser != null){
//                makeToastLog(CURR_CONTEXT,"firebase user already existed");
//                return;
//            }
//            AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
//            firebaseAuth.signInWithCredential(credential)
//                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()){
//                                makeToastLog(CURR_CONTEXT,"signed in to firebase");
//                                firebaseUser = firebaseAuth.getCurrentUser();
//                            } else {
//                                makeToastLog(CURR_CONTEXT,"sign in to firebase failed successfully");
//                            }
//                        }
//                    });
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void logoutGoogleFirebase(Activity activity){
//        FirebaseAuth.getInstance().signOut();
//        signInClient.signOut()
//                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        makeToastLog("logout ed");
//                    }
//                });
//    }
//    public static void disconnectGoogle(Activity activity){
//        signInClient.revokeAccess()
//                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        makeToastLog("disconnect ed");
//                    }
//                });
//    }
//    public static void addOrUpdate_Firestore(Interface_for_Firestore_class obj_to_add){
//        firestore.collection(obj_to_add.getClass().getSimpleName())
//                .document(obj_to_add.getTitle())
//                .set(obj_to_add)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        makeToastLog("successfully added");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        makeToastLog("task failed successfully");
//                    }
//                });
//    }
//    public static <T> T get_Firestore(final Class<T> class_to_get, String title){
//        Task<DocumentSnapshot> task = firestore.collection("firestore_test")
//                .document("test")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        makeToastLog("successfully get data");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        makeToastLog("failed to get data");
//                    }
//                });
//        if (task.isSuccessful()) {
//            return task.getResult().toObject(class_to_get);
//        } else {
//            return null;
//        }
//    }

    //REALM + FIRESTORE

    //SERVICE
    public static final String TEST = "test";
    //broadcast receiver
    public static final String IS_NEED_RECEIVER = "need_receiver";
    public static final String ACTION_TO_RECEIVE_KEY = "actions_to_receive";
    public static final String[] ACTION_TO_RECEIVE = {
            ConnectivityManager.CONNECTIVITY_ACTION,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED,
            BluetoothDevice.ACTION_ACL_CONNECTED,
            BluetoothDevice.ACTION_ACL_DISCONNECTED,
            "asdf",
    };
    //sensor
    public static final String IS_NEED_SENSOR = "need_sensor";
    public static final String SENSOR_TO_SENSE_KEY = "sensor_to_sense";
    public static final int[] SENSOR_TO_SENSE = {
//            Sensor.TYPE_AMBIENT_TEMPERATURE,  //my asus doesnt have temperature sensor
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_ACCELEROMETER,
//            Sensor.TYPE_LINEAR_ACCELERATION,
    };
    public static final String IS_NEED_DATA = "need_data";
    public static final String IS_CONSTANT_SENSOR = "constant sensor";
    //local broadcast receiver
    public static final String IS_NEED_LBR = "need_local_broadcast_receiver";
    public static final String ACTION_TO_RECEIVE_LBR_KEY = "actions_for_lbr";
    public static final String[] ACTION_TO_RECEIVE_LBR = {
            SP_service.SERVICE_DATA_CHANGE,
            TEST,
    };
    //service
    private static void init_Service(){
        localBroadcastManager = LocalBroadcastManager.getInstance(APP_CONTEXT);
    }
    private static int booToIntforService(Boolean b){
        if (b == null){
            return 0;
        }
        if (!b){
            return 1;
        }
        if (b){
            return 2;
        }
        return 0;
    }
    private static boolean isStartedService = false;
    public static boolean isServiceStarted(){
        return isStartedService;
    }
    public static void setServiceStarted(boolean b){isStartedService =b;}
    //null == 0, false == 1, true == 2
    public static void startService(Boolean is_need_receiver, String[] extra_action_br,
                                    Boolean is_need_sensor,int[] extra_sensor_type, Boolean is_need_data, Boolean is_constant_sensor,
                                    Boolean is_need_lbr, String[] extra_action_lbr){
//        if (isStartedService){return;}
        Intent intent = new Intent(APP_CONTEXT,SP_service.class);
        //receiver
        if (is_need_receiver) {
            intent.putExtra(IS_NEED_RECEIVER,2);
            intent.putExtra(ACTION_TO_RECEIVE_KEY, extra_action_br);
        } else {
            intent.putExtra(IS_NEED_RECEIVER,1);
        }
        //sensor
        if (is_need_sensor) {
            intent.putExtra(IS_NEED_SENSOR,2);
            intent.putExtra(SENSOR_TO_SENSE_KEY, extra_sensor_type);
            intent.putExtra(IS_NEED_DATA,booToIntforService(is_need_data));
            intent.putExtra(IS_CONSTANT_SENSOR,booToIntforService(is_constant_sensor));
        } else {
            intent.putExtra(IS_NEED_SENSOR,1);
        }
        //local broadcast receiver
        if (is_need_lbr) {
            intent.putExtra(IS_NEED_LBR,2);
            intent.putExtra(ACTION_TO_RECEIVE_LBR_KEY, extra_action_lbr);
        } else {
            intent.putExtra(IS_NEED_LBR,1);
        }

        intent.putExtra(TEST,"received test");
        APP_CONTEXT.startService(intent);
        isStartedService = true;
    }
    public static void startService(){
        startService(true,null,true,null,false,true,true,null);
    }
    public static boolean stopService(){
        if (!isStartedService){
            makeToastLog("service not started, cant stop");
            return false;
        }
        APP_CONTEXT.stopService(new Intent(APP_CONTEXT,SP_service.class));
        makeToastLog("service stopped");
        isStartedService = false;
        return true;
    }
    public static boolean sendBroadcast(String action){
        Intent intent = new Intent(action);
        APP_CONTEXT.sendBroadcast(intent);
        return true;
    }
    public static boolean sendBroadcast(Intent intent){
        APP_CONTEXT.sendBroadcast(intent);
        return true;
    }
    private static LocalBroadcastManager localBroadcastManager;
    public static void sendBroadcastLBR(String action){
        Intent intent = new Intent(action);
        localBroadcastManager.sendBroadcast(intent);
    }
    public static void sendBroadcastLBR(Intent intent){
        localBroadcastManager.sendBroadcast(intent);
    }

    //SENSOR
    private static boolean sensor_data_used = false;
    private static void init_Sensor(){
        //nothing for now
    }
    public static SP_service.Sensor_data get_SensorData(int sensor_type){
        SP_service.Sensor_data sensor_data = (SP_service.Sensor_data) SP_service.sensor_value.get(sensor_type);
        if (sensor_data == null){
            log("sensor data not found, cant get data");
            log("starting service to get data");
            startService(null,null,true,null,true,null,null,null);
            return null;
        }
        if (sensor_data_used){
            log("data used, starting service to get new data");
            startService(null,null,true,null,true,null,null,null);
            sensor_data_used = false;
            return null;
        }
        sensor_data_used = true;
        return sensor_data;
    }
    public static HashMap<Object, Object> get_SensorDataList(){
        if (sensor_data_used){
            log("data used, starting service to get new data");
            startService(null,null,true,null,true,null,null,null);
            sensor_data_used = false;
            return null;
        }
        for (int type : util.SENSOR_TO_SENSE) {
            SP_service.Sensor_data sensor_data = (SP_service.Sensor_data) SP_service.sensor_value.get(type);
            if (sensor_data == null) {
                log("sensor data not found, cant get data");
                log("starting service to get data");
                startService(null,null,true,null,true,null,null,null);
                return null;
            }
        }
        sensor_data_used = true;
        return SP_service.sensor_value;
    }

    //RECYCLER VIEW
    //TODO implement thing according to recycler_test
    private static Vector<MemoRecord> data;
//    private static RecyclerView

    //DEVICE DATA
    public static void get_DeviceData() {
        log("Below is Device Data Information");

        //basic device information
        log("SERIAL: " + Build.SERIAL);
        log("MODEL: " + Build.MODEL);
        log("ID: " + Build.ID);
        log("Manufacture: " + Build.MANUFACTURER);
        log("brand: " + Build.BRAND);
        log("type: " + Build.TYPE);
        log("user: " + Build.USER);
        log("BASE: " + Build.VERSION_CODES.BASE);
        log("INCREMENTAL " + Build.VERSION.INCREMENTAL);
        log("SDK  " + Build.VERSION.SDK);
        log("BOARD: " + Build.BOARD);
        log("BRAND " + Build.BRAND);
        log("HOST " + Build.HOST);
        log("FINGERPRINT: " + Build.FINGERPRINT);
        log("Version Code: " + Build.VERSION.RELEASE);
        log("TIME: " + Build.TIME);
        log("DISPLAY: " + Build.DISPLAY);
        log("TAGS: " + Build.TAGS);
        for (int i = 0; i < Build.SUPPORTED_ABIS.length; ++i) {
            log("SUPPORTED_ABIS: " + Build.SUPPORTED_ABIS[i]);
        }
    }
    public static void get_NetworkData(){
        log("Below is Network Information");

        ConnectivityManager manager = ((ConnectivityManager) APP_CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE));
        //all networks type and availability and connected  (btw, NetworkInfo is deprecated from android Q)
        //for seeing all networks in the phone
        for (Network network : manager.getAllNetworks()){
            NetworkInfo info = manager.getNetworkInfo(network);
            log(info.getTypeName().toUpperCase()+" available? : "+info.isAvailable());
            log(info.getTypeName().toUpperCase()+" connected? : "+info.isConnected());
        }
        //for getting the active, usable network in the phone
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null){
            log("Current active network = null");
        } else {
            log("Current active network = " + info.getTypeName() + ", and it is " + (info.isConnected() ? "" : "NOT ") + "CONNECTED");
        }
        if (info.getTypeName().equals("WIFI")) {
            WifiManager wifiManager = (WifiManager) APP_CONTEXT.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            log("SSID " + wifiInfo.getSSID());
            log("Frequency " + wifiInfo.getFrequency() + WifiInfo.FREQUENCY_UNITS);
            int ipAdr = wifiInfo.getIpAddress();    //local ip only
            String ip = String.format("IP Adrress : %02d.%02d.%02d.%02d", (ipAdr >> 0) & 0xff, (ipAdr >> 8) & 0xff, (ipAdr >> 16) & 0xff, (ipAdr >> 24) & 0xff);
            log(ip);
            log("MAC addr " + wifiInfo.getMacAddress());
            log("Link speed " + wifiInfo.getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS);
            log("Network ID " + wifiInfo.getNetworkId());
            log("Signal level " + WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 1000));
        }
        if (info.getTypeName().equals("MOBILE")) {
            log("IP address "+getIPAddress(true));
        }

        //other useful(?) stuff that can be set in the manager
//        manager.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
//            @Override
//            public void onNetworkActive() {
//                Log.i("MAX","Network is active now");
//            }
//        });
//        manager.registerNetworkCallback(
//                new NetworkRequest.Builder().addCapability(
//                        NetworkCapabilities.NET_CAPABILITY_INTERNET||
//                        NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING||
//                        ).build(),
//                new ConnectivityManager.NetworkCallback()
//                );
//        manager.reportNetworkConnectivity();
//        manager.requestNetwork();
    }
    private static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }
    public static void get_Battery(){
        log("Below is Battery information");

        //because battery stuff broadcast is sticky, can do it like this
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = APP_CONTEXT.registerReceiver(null,filter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;
        if (isCharging){
            makeToastLog("Phone is now charging");
        }
        if (isFull){
            makeToastLog("Phone is now full");
        }

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        if (usbCharge){
            makeToastLog("charge through USB");
        }
        if (acCharge){
            makeToastLog("charge through AC");
        }

        //battery level
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        float batteryPct = level/(float)scale;
        makeToastLog("battery level "+batteryPct);
    }

    //need to start service first, and then connect device
    //so that it can get detail of device
    //cause android cant show currently connected device directly, can only show paired device
    //so only able to listen to broadcast
    public static void get_Bluetooth(){
        if (!isStartedService){
            makeToastLog("service not started, can't get data");
            return;
        }
        log("Below is Bluetooth device information");
        BluetoothDevice bluetoothDevice = SP_service.getBluetoothDevice();
        if (bluetoothDevice == null){
            log("No Bluetooth device connected currently");
            return;
        }
        makeToastLog("Name = "+bluetoothDevice.getName());
        log("Address = "+bluetoothDevice.getAddress());
    }
    public static void get_Sensor(){
        log("Below is Sensor Data");
        if (SP_service.sensor_value.size() == 0){
            log("no data in map");
        }
        for (int type : SENSOR_TO_SENSE){
            SP_service.Sensor_data sensor = (SP_service.Sensor_data) SP_service.sensor_value.get(type);
            if (sensor != null) {
                log(sensor.name+" data = "+sensor.value);
            }
        }
    }
//    public static void get_Google(){
//        if (signInAccount == null){
//            log("no ac signed in currently");
//            return;
//        }
//        log("Below is Logined Google Account Information");
//        log("Display name: "+signInAccount.getDisplayName());
//        log("Family name: "+signInAccount.getFamilyName());
//        log("Given name: "+signInAccount.getGivenName());
//        log("Email: "+signInAccount.getEmail());
//        log("ID: "+signInAccount.getId());
//        log("ID token: "+signInAccount.getIdToken());
//        log("Server Auth Code: "+signInAccount.getServerAuthCode());
//    }
//    public static void get_Firebase(){
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (firebaseUser==null){
//            log("firebase not logged in, bye");
//            return;
//        }else {
//            log("Below is Logined Firebase Account Information");
//            log("getDisplayName "+firebaseUser.getDisplayName());
//            log("getEmail "+firebaseUser.getEmail());
//            log("getPhoneNumber "+firebaseUser.getPhoneNumber());
//            log("getProviderId "+firebaseUser.getProviderId());
//            log("getUid "+firebaseUser.getUid());
//            log("getProviders().size() "+firebaseUser.getProviders().size());
//            log("getProviders().get(0) "+firebaseUser.getProviders().get(0));
//            log("isEmailVerified "+firebaseUser.isEmailVerified());
//        }
//    }

    //sarcastic string generator
    public static String upLowString(String message){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if ((c>64 && c<91) || (c>96 && c<123)){

            }
            builder.append(c);
        }
        return builder.toString();
    }
}
