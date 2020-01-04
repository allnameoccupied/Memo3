package com.max.memo3.Util;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
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
import android.hardware.Sensor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.max.memo3.Background.Broadcast_Receiver;
import com.max.memo3.Background.SP_service;
import com.max.memo3.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
    public static Context CURR_CONTEXT;

    public static void INIT(){
        init_Vibrator();
        init_BroadcastReceiver();
        init_Notification();
        init_Calender();
        init_Scheduler();
        init_Realm();
//        init_Firestore();
        init_Service();
        init_Sensor();
    }

    public static void PAUSE(){
        stopToast();
        stopSnackbar();
        stopVibrator();
    }

    public static void DESTROY(){
        destroyRealm();
    }

    //TOAST
    private static Toast TOAST;
    public static void makeToast(CharSequence theThing){
        if (TOAST !=null){
            TOAST.cancel();}
        TOAST = Toast.makeText(CURR_CONTEXT,theThing,Toast.LENGTH_SHORT);
        TOAST.show();
    }
    public static void makeToast(Context context, CharSequence theThing){
        if (TOAST !=null){
            TOAST.cancel();}
        TOAST = Toast.makeText(context,theThing,Toast.LENGTH_SHORT);
        TOAST.show();
    }
    public static void makeToast_wait(CharSequence theThing){
        TOAST = Toast.makeText(CURR_CONTEXT,theThing,Toast.LENGTH_SHORT);
        TOAST.show();
    }
    public static void makeToast_wait(Context context,CharSequence theThing){
        TOAST = Toast.makeText(context,theThing,Toast.LENGTH_SHORT);
        TOAST.show();
    }
    public static boolean isShowing_toast(){
        if (TOAST == null){return false;}
        return TOAST.getView().isShown();
    }
    public static void stopToast(){
        if (TOAST ==null){return;}
        TOAST.cancel();
        TOAST = null;
    }

    public static ToastRunnable makeToast_runnable(CharSequence theThing){
        return new ToastRunnable(theThing);
    }
    public static TimerTask makeToast_timertask(final CharSequence theThing){
        final Handler handler = new Handler();
        return (new TimerTask() {
            @Override
            public void run() {
                handler.post(makeToast_runnable(theThing));
            }
        });
    }
    public static void makeToast_timer(CharSequence theThing, long delay){
        Timer timer = new Timer();
        timer.schedule(makeToast_timertask(theThing),delay);
    }


    //LOG
    public static final String LOG_TAG = "gg<3";
    public static enum SERIOUS_LEVEL {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        ASSERT
    }
    public static void quickLog(String message){
        Log.i(LOG_TAG,message);
    }
    public static void makeLog(SERIOUS_LEVEL level, String message){
        switch (level){
            case VERBOSE:
                Log.v(LOG_TAG,message);
                break;
            case DEBUG:
                Log.d(LOG_TAG,message);
                break;
            case INFO:
                Log.i(LOG_TAG,message);
                break;
            case WARN:
                Log.w(LOG_TAG,message);
                break;
            case ERROR:
                Log.e(LOG_TAG,message);
                break;
            case ASSERT:
                Log.wtf(LOG_TAG,message);
                break;
        }
    }
    public static void quickLog(int message){
        quickLog(Integer.toString(message));
    }
    public static void quickLog(long message){
        quickLog(Long.toString(message));
    }
    public static void quickLog(boolean message){
        quickLog(message?"TRUE":"FALSE");
    }

    //TOAST + LOG
    public static void makeToastLog(SERIOUS_LEVEL level, String message){
        makeLog(level,message);
        makeToast(message);
    }
    public static void makeToastLog(String message){
        quickLog(message);
        makeToast(message);
    }
    public static void makeToastLog(Context context, String message){
        quickLog(message);
        makeToast(context,message);
    }

    //SNACKBAR
    private static Snackbar snackbar;
    private static boolean isShowing_snackbar = false;
    private static final Snackbar.Callback baseCallback_snackbar = new Snackbar.Callback(){
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            isShowing_snackbar = false;
        }
        @Override
        public void onShown(Snackbar sb) {
            super.onShown(sb);
            isShowing_snackbar = true;
            quickLog("base callback log");
        }
    };
    public static void makeSnackbar(View view, CharSequence theThing, CharSequence actionStr, View.OnClickListener onClickListener, Snackbar.Callback callback){
        if (snackbar!=null && isShowing_snackbar){
            snackbar.dismiss();
        }
        snackbar = Snackbar.make(view,theThing, Snackbar.LENGTH_SHORT);
        if (actionStr!=null && onClickListener!=null){
            snackbar.setAction(actionStr,onClickListener);
        }
        snackbar.addCallback(baseCallback_snackbar);
        if (callback!=null){
            snackbar.addCallback(callback);
        }
        snackbar.show();
    }
    public static void makeSnackbar(View view, CharSequence theThing){
        makeSnackbar(view, theThing,null,null,null);
    }
    public static void stopSnackbar(){
        if (snackbar!=null){
            snackbar.dismiss();
            snackbar = null;
            isShowing_snackbar = false;
        } else {
            isShowing_snackbar = false;
        }
    }

    //NOTIFICATION
    private static Map<String,Custom_Notification_Channel> allCustomNotiChannel;
    private static AtomicInteger notification_count = new AtomicInteger(1);
    private static final String DEFAULT_CHANNELID = "MAX_CHANNEL";
    private static void init_Notification(){
        //init default notification channel
        NotificationChannel notificationChannel = new NotificationChannel(DEFAULT_CHANNELID,"MAX_MEMO_Default_Channel", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("Default notification channel for MAX MEMO");
        APP_CONTEXT.getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);

        //init vector
        allCustomNotiChannel = new HashMap<>();
    }
    public static void makeNotificationChannel(String channel_id_string, CharSequence name_charSeq, String description_string, int importance){
        if (channel_id_string == DEFAULT_CHANNELID){return;}
        if (allCustomNotiChannel.containsKey(channel_id_string)){return;}
        NotificationChannel notificationChannel = new NotificationChannel(channel_id_string,name_charSeq,importance);
        notificationChannel.setDescription(description_string);
        CURR_CONTEXT.getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
        Custom_Notification_Channel customChannel = new Custom_Notification_Channel(channel_id_string,name_charSeq,description_string,importance);
        allCustomNotiChannel.put(channel_id_string,customChannel);
    }
    public static int makeNotification(String channel_id_string, CharSequence title_charSeq, CharSequence text_charSeq, int priority){
        int noti_id = notification_count.incrementAndGet();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(CURR_CONTEXT,channel_id_string)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title_charSeq)
                .setContentText(text_charSeq)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text_charSeq))
                .setPriority(priority);
        NotificationManagerCompat.from(CURR_CONTEXT).notify(noti_id,builder.build());
        return noti_id;
    }
    public static int makeNotification(CharSequence title_charSeq, CharSequence text_charSeq){
        return makeNotification(DEFAULT_CHANNELID,title_charSeq,text_charSeq,NotificationManager.IMPORTANCE_DEFAULT);
    }
    public static int makeNotification_wchannel(String channel_id_string, CharSequence name_charSeq, String description_string, int importance,
                                                CharSequence title_charSeq, CharSequence text_charSeq, int priority){
        makeNotificationChannel(channel_id_string,name_charSeq,description_string,importance);
        return makeNotification(channel_id_string,title_charSeq,text_charSeq,priority);
    }

    //VIBRATOR  (max amplitude = 255)
    private static Vibrator vibrator;
    private static void init_Vibrator(){
        vibrator = (Vibrator) APP_CONTEXT.getSystemService(Context.VIBRATOR_SERVICE);
    }
    public static boolean makeVibrator(long ms, int amplitude){
        vibrator.cancel();
        vibrator.vibrate(VibrationEffect.createOneShot(ms,amplitude));
        return true;
    }
    public static boolean makeVibrator(long[] ms, int[] amplitude, int repeat){
        if (ms.length!=amplitude.length){
            return false;
        }
        vibrator.cancel();
        vibrator.vibrate(VibrationEffect.createWaveform(ms,amplitude,repeat));
        return true;
    }
    public static boolean makeVibrator(long[] ms, int repeat){
        vibrator.cancel();
        vibrator.vibrate(VibrationEffect.createWaveform(ms,repeat));
        return true;
    }
    public static void stopVibrator(){
        vibrator.cancel();
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
//                util.quickLog(objectToAdd.toString());
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
//                quickLog("signing into google now");
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
            quickLog("sensor data not found, cant get data");
            quickLog("starting service to get data");
            startService(null,null,true,null,true,null,null,null);
            return null;
        }
        if (sensor_data_used){
            quickLog("data used, starting service to get new data");
            startService(null,null,true,null,true,null,null,null);
            sensor_data_used = false;
            return null;
        }
        sensor_data_used = true;
        return sensor_data;
    }
    public static HashMap<Object, Object> get_SensorDataList(){
        if (sensor_data_used){
            quickLog("data used, starting service to get new data");
            startService(null,null,true,null,true,null,null,null);
            sensor_data_used = false;
            return null;
        }
        for (int type : util.SENSOR_TO_SENSE) {
            SP_service.Sensor_data sensor_data = (SP_service.Sensor_data) SP_service.sensor_value.get(type);
            if (sensor_data == null) {
                quickLog("sensor data not found, cant get data");
                quickLog("starting service to get data");
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
        quickLog("Below is Device Data Information");

        //basic device information
        quickLog("SERIAL: " + Build.SERIAL);
        quickLog("MODEL: " + Build.MODEL);
        quickLog("ID: " + Build.ID);
        quickLog("Manufacture: " + Build.MANUFACTURER);
        quickLog("brand: " + Build.BRAND);
        quickLog("type: " + Build.TYPE);
        quickLog("user: " + Build.USER);
        quickLog("BASE: " + Build.VERSION_CODES.BASE);
        quickLog("INCREMENTAL " + Build.VERSION.INCREMENTAL);
        quickLog("SDK  " + Build.VERSION.SDK);
        quickLog("BOARD: " + Build.BOARD);
        quickLog("BRAND " + Build.BRAND);
        quickLog("HOST " + Build.HOST);
        quickLog("FINGERPRINT: " + Build.FINGERPRINT);
        quickLog("Version Code: " + Build.VERSION.RELEASE);
        quickLog("TIME: " + Build.TIME);
        quickLog("DISPLAY: " + Build.DISPLAY);
        quickLog("TAGS: " + Build.TAGS);
        for (int i = 0; i < Build.SUPPORTED_ABIS.length; ++i) {
            quickLog("SUPPORTED_ABIS: " + Build.SUPPORTED_ABIS[i]);
        }
    }
    public static void get_NetworkData(){
        quickLog("Below is Network Information");

        ConnectivityManager manager = ((ConnectivityManager) APP_CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE));
        //all networks type and availability and connected  (btw, NetworkInfo is deprecated from android Q)
        //for seeing all networks in the phone
        for (Network network : manager.getAllNetworks()){
            NetworkInfo info = manager.getNetworkInfo(network);
            quickLog(info.getTypeName().toUpperCase()+" available? : "+info.isAvailable());
            quickLog(info.getTypeName().toUpperCase()+" connected? : "+info.isConnected());
        }
        //for getting the active, usable network in the phone
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null){
            quickLog("Current active network = null");
        } else {
            quickLog("Current active network = " + info.getTypeName() + ", and it is " + (info.isConnected() ? "" : "NOT ") + "CONNECTED");
        }
        if (info.getTypeName().equals("WIFI")) {
            WifiManager wifiManager = (WifiManager) APP_CONTEXT.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            quickLog("SSID " + wifiInfo.getSSID());
            quickLog("Frequency " + wifiInfo.getFrequency() + WifiInfo.FREQUENCY_UNITS);
            int ipAdr = wifiInfo.getIpAddress();    //local ip only
            String ip = String.format("IP Adrress : %02d.%02d.%02d.%02d", (ipAdr >> 0) & 0xff, (ipAdr >> 8) & 0xff, (ipAdr >> 16) & 0xff, (ipAdr >> 24) & 0xff);
            quickLog(ip);
            quickLog("MAC addr " + wifiInfo.getMacAddress());
            quickLog("Link speed " + wifiInfo.getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS);
            quickLog("Network ID " + wifiInfo.getNetworkId());
            quickLog("Signal level " + WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 1000));
        }
        if (info.getTypeName().equals("MOBILE")) {
            quickLog("IP address "+getIPAddress(true));
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
        quickLog("Below is Battery information");

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
        quickLog("Below is Bluetooth device information");
        BluetoothDevice bluetoothDevice = SP_service.getBluetoothDevice();
        if (bluetoothDevice == null){
            quickLog("No Bluetooth device connected currently");
            return;
        }
        makeToastLog("Name = "+bluetoothDevice.getName());
        quickLog("Address = "+bluetoothDevice.getAddress());
    }
    public static void get_Sensor(){
        quickLog("Below is Sensor Data");
        if (SP_service.sensor_value.size() == 0){
            quickLog("no data in map");
        }
        for (int type : SENSOR_TO_SENSE){
            SP_service.Sensor_data sensor = (SP_service.Sensor_data) SP_service.sensor_value.get(type);
            if (sensor != null) {
                quickLog(sensor.name+" data = "+sensor.value);
            }
        }
    }
//    public static void get_Google(){
//        if (signInAccount == null){
//            quickLog("no ac signed in currently");
//            return;
//        }
//        quickLog("Below is Logined Google Account Information");
//        quickLog("Display name: "+signInAccount.getDisplayName());
//        quickLog("Family name: "+signInAccount.getFamilyName());
//        quickLog("Given name: "+signInAccount.getGivenName());
//        quickLog("Email: "+signInAccount.getEmail());
//        quickLog("ID: "+signInAccount.getId());
//        quickLog("ID token: "+signInAccount.getIdToken());
//        quickLog("Server Auth Code: "+signInAccount.getServerAuthCode());
//    }
//    public static void get_Firebase(){
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (firebaseUser==null){
//            quickLog("firebase not logged in, bye");
//            return;
//        }else {
//            quickLog("Below is Logined Firebase Account Information");
//            quickLog("getDisplayName "+firebaseUser.getDisplayName());
//            quickLog("getEmail "+firebaseUser.getEmail());
//            quickLog("getPhoneNumber "+firebaseUser.getPhoneNumber());
//            quickLog("getProviderId "+firebaseUser.getProviderId());
//            quickLog("getUid "+firebaseUser.getUid());
//            quickLog("getProviders().size() "+firebaseUser.getProviders().size());
//            quickLog("getProviders().get(0) "+firebaseUser.getProviders().get(0));
//            quickLog("isEmailVerified "+firebaseUser.isEmailVerified());
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
