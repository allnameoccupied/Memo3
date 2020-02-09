package com.max.memo3.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;

public class util_LBR_DefaultActionHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        util.log("Received = "+intent.getAction());
        switch (intent.getAction()) {
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY :
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                util.log("network changed");
                break;
            case "test":
                util.log("test");
                break;
        }
    }
}
