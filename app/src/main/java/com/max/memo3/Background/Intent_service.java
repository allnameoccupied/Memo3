package com.max.memo3.Background;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class Intent_service extends IntentService implements SensorEventListener {
    //var
    private SensorManager sensorManager;
    private int finished_sensor_count = 0;
    private int total_sensor_count;
//    private ArrayList<sensor_data_class> sensor_data_list = new ArrayList<>();
    public boolean finished_receiving_data = false;

    //func
    public Intent_service(){
        super("Intent_service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        String action = bundle.getString("action");
//        switch (action) {
//            case "need_sensor" :    //assume sense one time
//                sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
//                int[] sensor_to_listen = bundle.getIntArray("sensors");
//                total_sensor_count = sensor_to_listen.length;
//                for (int sensor_code : sensor_to_listen) {
//                    Sensor sensor = sensorManager.getDefaultSensor(sensor_code);
//                    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
//                }
//                frequently_used_stuff.quick_log("intent service started");
//                while (true){
//                    if (finished_receiving_data)break;
//                }
//                break;
//            case "intent_service_test" :
//                Log.i("MAX","intent service responded");
//                Toast.makeText(this,"intent service responded",Toast.LENGTH_SHORT).show();
//                Intent intent_back = new Intent();
//                intent_back.setAction("ack_from_intent_service");
//                intent_back.putExtra("data","some data");
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent_back);
//                break;
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MAX","intent service finished, self destroying");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        //get + record data
//        sensor_data_class data = new sensor_data_class(event.sensor.getType(),event.sensor.getName(),event.values[0]);
//        sensor_data_list.add(data);
//        sensorManager.unregisterListener(this,event.sensor);
////        Log.i("MAX", event.sensor.getName() + " sensor data changed to " + event.values[0]);
//
//        //if enough, send back to base
//        finished_sensor_count++;
////        frequently_used_stuff.quick_log(finished_sensor_count);
//        if (finished_sensor_count == total_sensor_count){
//            Intent intent = new Intent("data_from_intent_service");
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("data_list",sensor_data_list);
//            intent.putExtras(bundle);
//            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//            finished_receiving_data = true;
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
