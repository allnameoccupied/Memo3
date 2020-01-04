package com.max.memo3.Background;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;

import com.max.memo3.MainActivity;
import com.max.memo3.Util.util;

import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//service = use own application's own thread, won't stop autoly (in most cases)
//intent service = make and use a new thread, feed intent 1 by 1, will stop when no intent to feed
//job service = service for work in job scheduler to do
public class SP_service extends Service implements SensorEventListener {
    //var
    public static SP_service INSTANCE_OF_THIS;
    private boolean is_need_receiver, is_need_sensor, is_need_data, is_constant_sensor, is_need_lbr;
    private broadcast_receiver_for_service broadcastReceiver;
//    private SensorManager sensorManager;
//    private LocalBroadcastManager localBroadcastManager;
    private local_broadcast_receiver localBroadcastReceiver;
    public static final String SERVICE_DATA_CHANGE = "service_data_change";
    private class broadcast_receiver_for_service extends BroadcastReceiver{
        //var

        //func
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!is_need_receiver){return;}
            if (isInitialStickyBroadcast()){return;}
            switch (intent.getAction()){
                case ConnectivityManager.CONNECTIVITY_ACTION :
                    util.quickLog("successfully response in service");
                    util.makeToastLog(context, "broadcast received");
                    break;
                case Intent.ACTION_POWER_CONNECTED :
                    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
                    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
                    boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;
                    if (isCharging){
                        util.makeToastLog(context,"Phone is now charging");
                    }
                    if (isFull){
                        util.makeToastLog(context,"Phone is now full");
                    }

                    int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
                    boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                    boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                    if (usbCharge){
                        util.makeToastLog(context,"charge through USB");
                    }
                    if (acCharge){
                        util.makeToastLog(context,"charge through AC");
                    }
                    break;
                case Intent.ACTION_POWER_DISCONNECTED :
                    util.makeToastLog("Power disconnected");
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED :
                    util.makeToastLog(context,"Bluetooth device connected");
                    bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    util.makeToastLog(context,"Name = "+bluetoothDevice.getName());
                    util.quickLog("Address = "+bluetoothDevice.getAddress());
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED :
                    util.makeToastLog(context,"Bluetooth device DISconnected");
                    bluetoothDevice = null;
                    break;
                case util.TEST :
                    util.makeToastLog(context,intent.getAction());
//                    Intent intent1 = new Intent("zxcv");
//                    intent1.putExtra("qwer","qwer");
//                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
//                    localBroadcastManager.sendBroadcast(intent1);
                    break;
            }
        }
    }
    public static HashMap<Object, Object> sensor_value = new HashMap<>();
    public class Sensor_data {
        public int type;
        public String name;
        public float value;
    }
    private class local_broadcast_receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                //change functions of the service
                case SERVICE_DATA_CHANGE :
                    //get extras
                    Bundle bundle = intent.getExtras();
                    Boolean temp;
                    //receiver
                    temp = getBooFromInt(bundle.getInt(util.IS_NEED_RECEIVER));
                    if (temp != null){
                        //now need, before no need
                        if (temp && !is_need_receiver){
                            is_need_receiver = temp;
                            //default action first
                            for (String action2 : util.ACTION_TO_RECEIVE) {
                                registerReceiver(broadcastReceiver, new IntentFilter(action2));
                            }
                            //extra action to register
                            String[] extra_actions_to_register = bundle.getStringArray(util.ACTION_TO_RECEIVE_KEY);
                            if (extra_actions_to_register != null) {
                                for (String action2 : extra_actions_to_register) {
                                    registerReceiver(broadcastReceiver, new IntentFilter(action2));
                                }
                            }
                        }
                        //now no need, before need
                        if (!temp && is_need_receiver){
                            is_need_receiver = temp;
                            unregisterReceiver(broadcastReceiver);
                        }
                        //now and before need, update
                        if (temp && is_need_receiver){
                            //extra action to register
                            String[] extra_actions_to_register = bundle.getStringArray(util.ACTION_TO_RECEIVE_KEY);
                            if (extra_actions_to_register != null) {
                                for (String action2 : extra_actions_to_register) {
                                    registerReceiver(broadcastReceiver, new IntentFilter(action2));
                                }
                            }
                        }
                    }
                    //sensor
                    temp = getBooFromInt(bundle.getInt(util.IS_NEED_SENSOR));
                    if (temp != null){
                        //now and before need, update
                        if (temp && is_need_sensor){
                            //init boolean
                            Boolean new_need_data = getBooFromInt(bundle.getInt(util.IS_NEED_DATA));
                            if (new_need_data != null){
                                is_need_data = new_need_data;
                            } else {
                                is_need_data = false;
                            }
                            Boolean new_constant_sensor = getBooFromInt(bundle.getInt(util.IS_CONSTANT_SENSOR));
                            if (new_constant_sensor != null){
                                is_constant_sensor = new_constant_sensor;
                            } else {
                                is_constant_sensor = true;
                            }
                            //extra sensor
                            int[] sensor_to_listen = bundle.getIntArray(util.SENSOR_TO_SENSE_KEY);
                            if (sensor_to_listen != null) {
                                SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                                for (int type : sensor_to_listen) {
                                    Sensor sensor = sensorManager.getDefaultSensor(type);
                                    sensorManager.registerListener(SP_service.INSTANCE_OF_THIS, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                                }
                            }
                        }
                        //now need, before no need
                        if (temp && !is_need_sensor){
                            is_need_sensor = temp;
                            //init var
                            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                            //init boolean
                            Boolean new_need_data = getBooFromInt(bundle.getInt(util.IS_NEED_DATA));
                            if (new_need_data != null){
                                is_need_data = new_need_data;
                            } else {
                                is_need_data = false;
                            }
                            Boolean new_constant_sensor = getBooFromInt(bundle.getInt(util.IS_CONSTANT_SENSOR));
                            if (new_constant_sensor != null){
                                is_constant_sensor = new_constant_sensor;
                            } else {
                                is_constant_sensor = true;
                            }
                            //default sensor first
                            for (int sensor_code : util.SENSOR_TO_SENSE) {
                                Sensor sensor = sensorManager.getDefaultSensor(sensor_code);
                                sensorManager.registerListener(SP_service.INSTANCE_OF_THIS, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                            }
                            //extra sensor
                            int[] sensor_to_listen = bundle.getIntArray(util.SENSOR_TO_SENSE_KEY);
                            if (sensor_to_listen != null) {
                                for (int type : sensor_to_listen) {
                                    Sensor sensor = sensorManager.getDefaultSensor(type);
                                    sensorManager.registerListener(SP_service.INSTANCE_OF_THIS, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                                }
                            }
                        }
                        //now no need, before need
                        if (!temp && is_need_sensor){
                            is_need_sensor = temp;
                            SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
                            sensorManager.unregisterListener(SP_service.INSTANCE_OF_THIS);
                            is_need_data = false;
                            is_constant_sensor = false;
                        }
                    }
                    //local broadcast receiver
                    temp = getBooFromInt(bundle.getInt(util.IS_NEED_LBR));
                    if (temp != null){
                        //now need, before no need
                        if (temp && !is_need_lbr){
                            is_need_lbr = temp;
                            //default action first
                            for (String action2 : util.ACTION_TO_RECEIVE_LBR) {
                                registerReceiver(broadcastReceiver, new IntentFilter(action2));
                            }
                            //extra action to register
                            String[] extra_actions_to_register = bundle.getStringArray(util.ACTION_TO_RECEIVE_LBR_KEY);
                            if (extra_actions_to_register != null) {
                                for (String action2 : extra_actions_to_register) {
                                    registerReceiver(broadcastReceiver, new IntentFilter(action2));
                                }
                            }
                        }
                        //now no need, before need
                        if (!temp && is_need_lbr){
                            is_need_lbr = temp;
                            unregisterReceiver(broadcastReceiver);
                        }
                        //now and before need, update
                        if (temp && is_need_lbr){
                            //extra action to register
                            String[] extra_actions_to_register = bundle.getStringArray(util.ACTION_TO_RECEIVE_LBR_KEY);
                            if (extra_actions_to_register != null) {
                                for (String action2 : extra_actions_to_register) {
                                    registerReceiver(broadcastReceiver, new IntentFilter(action2));
                                }
                            }
                        }
                    }
                    break;
                case util.TEST :
                    util.quickLog("local broadcast received");
//                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(util.APP_CONTEXT);
//                    localBroadcastManager.sendBroadcast(new Intent("sdf"));
                    break;
            }
        }
    }

    //func
        //if service is called 1st time (not running before), will call this
    @Override
    public void onCreate() {
        INSTANCE_OF_THIS = this;

        //register broadcast receiver
        broadcastReceiver = new broadcast_receiver_for_service();
//        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //local broadcast receiver
        localBroadcastReceiver = new local_broadcast_receiver();

        super.onCreate();
    }

        //if service is called before (already running), will call this,
        //so it will be called MULTIPLE TIMES
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Bundle bundle = intent.getExtras();
        Boolean temp;

        //register receiver here
        temp = getBooFromInt(bundle.getInt(util.IS_NEED_RECEIVER));
        if (temp != null) {
            is_need_receiver = temp;
            if (is_need_receiver) {
                //default action first
                for (String action : util.ACTION_TO_RECEIVE) {
                    registerReceiver(broadcastReceiver, new IntentFilter(action));
                }
                //extra action to register
                String[] extra_actions_to_register = bundle.getStringArray(util.ACTION_TO_RECEIVE_KEY);
                if (extra_actions_to_register != null) {
                    for (String action : extra_actions_to_register) {
                        registerReceiver(broadcastReceiver, new IntentFilter(action));
                    }
                }
            }
        }
        registerReceiver(broadcastReceiver, new IntentFilter(util.TEST));

        //register sensor listener here
        temp = getBooFromInt(bundle.getInt(util.IS_NEED_SENSOR));
        if (temp != null) {
            is_need_sensor = temp;
            if (is_need_sensor) {
                //init var
                is_need_data = bundle.getBoolean(util.IS_NEED_DATA);
                is_constant_sensor = bundle.getBoolean(util.IS_CONSTANT_SENSOR);
                SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
                //default sensor first
                for (int sensor_code : util.SENSOR_TO_SENSE) {
                    Sensor sensor = sensorManager.getDefaultSensor(sensor_code);
                    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                //extra sensor
                int[] sensor_to_listen = bundle.getIntArray(util.SENSOR_TO_SENSE_KEY);
                if (sensor_to_listen != null) {
                    for (int type : sensor_to_listen) {
                        Sensor sensor = sensorManager.getDefaultSensor(type);
                        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }
            }
        }

        //register Local Broadcast Receiver
        temp = getBooFromInt(bundle.getInt(util.IS_NEED_LBR));
        if (temp != null) {
            is_need_lbr = temp;
            if (is_need_lbr) {
                //init
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                //default action first
                for (String action : util.ACTION_TO_RECEIVE_LBR) {
                    localBroadcastManager.registerReceiver(localBroadcastReceiver, new IntentFilter(action));

                }
                //extra action
                String[] extra_actions_for_lbr = bundle.getStringArray(util.ACTION_TO_RECEIVE_LBR_KEY);
                if (extra_actions_for_lbr != null) {
                    for (String action : extra_actions_for_lbr) {
                        localBroadcastManager.registerReceiver(localBroadcastReceiver, new IntentFilter(action));
                    }
                }
            }
        }

        //tap noti = open main activity
        Intent link_to_activity = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,link_to_activity,PendingIntent.FLAG_UPDATE_CURRENT);

        //noti that will show when service is running
        //(must, so that user know it is running & allowed to run foreground
        Notification notification = new Notification.Builder(this,"channel_for_service_MEMO")
                .setContentTitle("MEMO")
                .setContentText("MEMO service is running")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);    //start, (id == 0, then cant show, ???)

        util.makeToast(this,"service started");
        util.quickLog("service started");
        return SP_service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static BluetoothDevice bluetoothDevice = null;
    public static BluetoothDevice getBluetoothDevice(){
        if (bluetoothDevice == null){
            return null;
        }
        return bluetoothDevice;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null){
            this.unregisterReceiver(broadcastReceiver);
        }
        if (localBroadcastReceiver != null){
            this.unregisterReceiver(localBroadcastReceiver);
        }
        util.setServiceStarted(false);
    }

    //sensor listener
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (is_need_data) {
            Sensor_data sensor = (Sensor_data) sensor_value.get(event.sensor.getType());
            if (sensor == null) {
                sensor = new Sensor_data();
                sensor.name = event.sensor.getName();
                sensor.type = event.sensor.getType();
                sensor.value = event.values[0];
                sensor_value.put(event.sensor.getType(), sensor);
//                util.quickLog(sensor.name+" "+sensor.type+" "+sensor.value);
            } else {
                sensor.value = event.values[0];
            }
            if (!is_constant_sensor) {
                SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
                sensorManager.unregisterListener(this, event.sensor);
            }
            return;
        }
//        } else {
//            Sensor_data sensor = sensor_value.get(event.sensor.getType());
//            if (sensor == null) {
//                sensor = new Sensor_data();
//                sensor.name = event.sensor.getName();
//                sensor.type = event.sensor.getType();
//                sensor.value = event.values[0];
//                sensor_value.put(event.sensor.getType(), sensor);
////                util.quickLog(sensor.name+" "+sensor.type+" "+sensor.value);
//            } else {
//                sensor.value = event.values[0];
//            }
//        }
        util.quickLog(event.sensor.getName() + " sensor data changed to " + event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        Toast.makeText(this,sensor.getName()+" sensor accuracy changed to "+accuracy,Toast.LENGTH_SHORT).show();
//        Log.i("MAX",sensor.getName()+" sensor accuracy changed to "+accuracy);
    }

    private Boolean getBooFromInt(@Nullable Integer i){
        if (i == null){
            return null;
        }
        if (i == 2){
            return true;
        }
        if (i == 1){
            return false;
        }
        if (i == 0){
            return null;
        }
        return null;
    }
}