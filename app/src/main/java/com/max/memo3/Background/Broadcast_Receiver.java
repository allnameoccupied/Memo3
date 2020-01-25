package com.max.memo3.Background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;

import com.max.memo3.Util.util;

import java.util.ArrayList;
import java.util.List;

public class Broadcast_Receiver extends BroadcastReceiver {
    //var
    private static List<String> allActionRegistered = new ArrayList<>();
    private static List<String> allActionListening = new ArrayList<>();
    private static int qwer = 0;

    //func
    public Broadcast_Receiver(){}

    public void registerAction(String action){
        if (allActionRegistered.contains(action) && allActionListening.contains(action)){
            return;
        } else if (allActionRegistered.contains(action) && !allActionListening.contains(action)){
            allActionListening.add(action);
        } else if (!allActionRegistered.contains(action)){
            allActionRegistered.add(action);
            allActionListening.add(action);
            util.APP_CONTEXT.registerReceiver(this,new IntentFilter(action));
        }
    }

    public void unRegisterAction(String action){
        if (!allActionRegistered.contains(action)){
            return;
        } else if (allActionListening.contains(action)){
            allActionListening.remove(action);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!allActionListening.contains(intent.getAction())){return;}
        util.CURR_CONTEXT = context;
        util.log(qwer++);
        util.log("Received = "+intent.getAction());
        switch (intent.getAction()) {
            case "qwer" :
                util.makeToast("qwer");
                break;

            case AudioManager.ACTION_AUDIO_BECOMING_NOISY :
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                break;

            case ConnectivityManager.CONNECTIVITY_ACTION:
                util.log("network changed");
                break;
        }
    }
}
