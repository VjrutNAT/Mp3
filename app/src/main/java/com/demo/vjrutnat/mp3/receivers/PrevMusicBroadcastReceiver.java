package com.demo.vjrutnat.mp3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.demo.vjrutnat.mp3.activities.PlayMusicActivity;
import com.demo.vjrutnat.mp3.services.PlayMusicService;
import com.demo.vjrutnat.mp3.utils.AppController;


/**
 * Created by IceMan on 11/29/2016.
 */

public class PrevMusicBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PlayMusicActivity musicActivity = (PlayMusicActivity) AppController.getInstance().getPlayMusicActivity();
        PlayMusicService musicService = (PlayMusicService) AppController.getInstance().getPlayMusicService();
//        musicService.setShowNotification(false);
        if (musicActivity != null) {
            musicActivity.backMusic();

        } else {
            musicService.backMusic();
        }
        musicService.showNotification(true);
//        musicService.setShowNotification(true);
    }
}
