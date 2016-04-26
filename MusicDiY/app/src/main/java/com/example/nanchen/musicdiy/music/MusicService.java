package com.example.nanchen.musicdiy.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by nanchen on 2016/4/12.
 */
public class MusicService extends Service {
    private MusicUnit music;
    private MediaPlayer player = new MediaPlayer();
    private int state = 0x11;
    private int isNew;
    private int current, allTime;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        MyBroadcastService cService = new MyBroadcastService();
        IntentFilter filter = new IntentFilter("com.example.service");
        registerReceiver(cService, filter);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent("com.example.activity");
                intent.putExtra("toNext", 1);
                current = 0;
                allTime = 0;
                sendBroadcast(intent);
            }
        });
        super.onCreate();
    }

    public class MyBroadcastService extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isNew = intent.getIntExtra("newMusic", -1);// 未取到值就设为-1
            if (isNew != -1) {
                music = (MusicUnit) intent.getSerializableExtra("music");
                if (music != null) {
                    playMusic(music);
                    state = 0x12;
                }
            }
            int mark = intent.getIntExtra("max", -1);
            if (mark != -1) {
                switch (state) {
                    case 0x11:
                        music = (MusicUnit) intent.getSerializableExtra("music");
                        playMusic(music);
                        state = 0x12;
                        break;
                    case 0x12:
                        player.pause();
                        state = 0x13;
                        break;
                    case 0x13:
                        player.start();
                        state = 0x12;
                        break;
                    default:
                        break;
                }
            }
            Intent intent2 = new Intent("com.example.activity");
            intent2.putExtra("state", state);
            sendBroadcast(intent2);

            int t = intent.getIntExtra("current", -1);
            if (t != -1) {
                current = (int) (((t * 0.1) / 100) * allTime);
                player.seekTo(current);
            }
        }
    }

    private void playMusic(MusicUnit music) {
        if (player != null) {
            player.stop();
            player.reset();
            try {
                player.setDataSource(music.getPath());
                player.prepare();
                player.start();
                allTime = (int) music.getDuration();
                new Thread() {
                    public void run() {
                        Intent intent = new Intent("com.example.activity");
                        while (player.getCurrentPosition() <= allTime) {
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            intent.putExtra("current", player.getCurrentPosition());
                            intent.putExtra("allTime", allTime);
                            sendBroadcast(intent);
                        }
                    };
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        unregisterReceiver(receiver);
    }
}

