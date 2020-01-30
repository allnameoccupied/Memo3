package com.max.memo3.Background;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.max.memo3.TestSubject.Test0_Main;
import com.max.memo3.Util.util;

import java.util.HashMap;
import java.util.Map;

import androidx.core.app.RemoteInput;

public class MEMO3_BroadcastReceiver extends BroadcastReceiver {
    //TODO move to service
//    private static Map<String,Runnable> actionMap = new HashMap<>();
//    public static void setActionMapElement(String action, Runnable runnable){actionMap.put(action,runnable);}

    @Override
    public void onReceive(Context context, Intent intent) {
        util.log("qwer");
        switch (intent.getAction()){
            case "test6_1":
                util.log("asdf");
//              Intent intent1 = new Intent(context, Test0_Main.class);
                Intent intent1 = new Intent();
//            intent1.setClassName("com.max.memo3.TestSubject","com.max.memo3.TestSubject.Test0_Main");
//            util.log(context.getPackageName());
//            util.log(Test0_Main.class.getName());
                intent1.setClassName(context.getPackageName(),Test0_Main.class.getName());
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                break;
            case "test6_2":
                util.log("zxcv");
                util.log(intent.getExtras().get("qaz"));
                ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int)intent.getExtras().get("notiid"));
                break;
            case "test6_3":
                util.log("tyui");
                ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int)intent.getExtras().get("notiid"));
                break;
            case "test6_4":
                util.log("ghjk");
                ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int)intent.getExtras().get("notiid"));
                break;
            case "test6_5":
                util.log("bnm,");
                ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int)intent.getExtras().get("notiid"));
                break;
            case "test6_reply":
                Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                if (remoteInput!=null){
                    util.log(remoteInput.getCharSequence("test6_text"));

                } else {
                    util.log("remoteInput is null");
                }
                ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int)intent.getExtras().get("notiid"));
                break;
            case "qaz":
                util.log("edc");
                break;
            case "wsx":
                util.log("tgb");
                break;
        }
    }
}
