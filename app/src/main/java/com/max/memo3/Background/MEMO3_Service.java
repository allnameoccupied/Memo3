package com.max.memo3.Background;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.max.memo3.Util.util;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class MEMO3_Service extends Service {
    //var

    //func
    //if service is called 1st time (not running before), will call this
    @Override
    public void onCreate() {
        super.onCreate();
        util.log("service onCreate");
    }

    //if service is called before (already running), will call this,
    //so it will be called MULTIPLE TIMES
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Bundle bundle = intent.getExtras();
        util.log("service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        util.log("service onDestroy");
    }

    //binder
    private MEMO3_ServiceBinder binder = new MEMO3_ServiceBinder();
    public class MEMO3_ServiceBinder extends Binder{
        @Deprecated public int get(){return 5;}
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        util.log("service onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        util.log("service onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        util.log("service onRebind");
        super.onRebind(intent);
    }
}
