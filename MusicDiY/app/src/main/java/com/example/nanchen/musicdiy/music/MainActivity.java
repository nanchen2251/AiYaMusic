package com.example.nanchen.musicdiy.music;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.nanchen.musicdiy.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class MainActivity extends Activity implements android.view.View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton left, play, next;
    private ImageButton model;
    private ListView music_list;
    private TextView duration;
    private SeekBar music_process;
    private MyAdapter mAdapter;
    private List<MusicUnit> oList;
    private MusicUnit musicUnit = null;
    private int index;
    private int state;
    private int playerModel = 0x21;
    private SharedPreferences sha;
    private SharedPreferences.Editor oEditor;

    public void init() {
        model = (ImageButton) findViewById(R.id.model);
        left = (ImageButton) findViewById(R.id.left);
        play = (ImageButton) findViewById(R.id.play);
        next = (ImageButton) findViewById(R.id.next);
        music_list = (ListView) findViewById(R.id.music_list);
        duration = (TextView) findViewById(R.id.duration);
        music_process = (SeekBar) findViewById(R.id.music_process);
        model.setOnClickListener(this);
        MyBroadcastActivity activity = new MyBroadcastActivity();
        IntentFilter iFilter = new IntentFilter("com.example.activity");
        registerReceiver(activity, iFilter);
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);

        sha=getSharedPreferences("Date", 0);
        oEditor = sha.edit();
    }

    private AdapterView.OnItemClickListener oClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            index = position;
            musicUnit = oList.get(position);
            Intent intent = new Intent("com.example.service");
            intent.putExtra("music", musicUnit);
            intent.putExtra("newMusic", 1);
            sendBroadcast(intent);
        }
    };

    public class MyBroadcastActivity extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            state = intent.getIntExtra("state", 0x11);
            switch (state) {
                case 0x12:
                    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_pause);
                    play.setImageBitmap(bitmap1);
                    break;
                case 0x13:
                    Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);
                    play.setImageBitmap(bitmap2);
                    break;
            }
            int current = intent.getIntExtra("current", -1);
            int allTime = intent.getIntExtra("allTime", -1);
            if (current != -1) {
                music_process.setProgress((int) (((current * 1.0) / allTime) * 1000));
                duration.setText(String.format("%s/%s", initTime(current), initTime(allTime)));
            }
            int toNext = intent.getIntExtra("toNext", -1);
            Intent intent2 = new Intent("com.example.service");
            if (toNext != -1) {
                if (playerModel == 0x21) {// 循环播放
                    if (index == oList.size() - 1)
                        index = 0;
                    else
                        index++;
                    intent2.putExtra("newMusic", 1);
                    intent2.putExtra("music", oList.get(index));
                }
                if (playerModel == 0x22) {// 单曲播放
                    intent2.putExtra("newMusic", 1);
                    intent2.putExtra("music", oList.get(index));
                }
                if (playerModel == 0x23) {// 随机播放
                    int randomMusic = (int) (Math.random() * oList.size());
                    index = randomMusic;
                    intent2.putExtra("newMusic", 1);
                    intent2.putExtra("music", oList.get(index));
                }
            }
            sendBroadcast(intent2);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        oList = MusicList.getMusicData(MainActivity.this);
        mAdapter = new MyAdapter(oList, MainActivity.this);
        music_list.setAdapter(mAdapter);
        music_list.setOnItemClickListener(oClickListener);
        play.setOnClickListener(this);
        left.setOnClickListener(this);
        next.setOnClickListener(this);
        music_process.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent("com.example.service");
        switch (v.getId()) {
            case R.id.play:
                if (musicUnit == null) {
                    intent.putExtra("music", oList.get(0));
                }
                intent.putExtra("max", 1);
                break;
            case R.id.left:
                if (index == 0)
                    index = oList.size() - 1;
                else
                    index--;
                intent.putExtra("newMusic", 1);
                intent.putExtra("music", oList.get(index));
                break;
            case R.id.next:
                if (index == oList.size() - 1)
                    index = 0;
                else
                    index++;
                intent.putExtra("newMusic", 1);
                intent.putExtra("music", oList.get(index));
                break;
            case R.id.model:
                switch (playerModel) {
                    case 0x21:
                        playerModel=0x22;
                        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.single);
                        model.setImageBitmap(bitmap1);
                        break;
                    case 0x22:
                        playerModel=0x23;
                        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.random);
                        model.setImageBitmap(bitmap2);
                        break;
                    case 0x23:
                        playerModel=0x21;
                        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_revert);
                        model.setImageBitmap(bitmap3);
                        break;
                    default:
                        break;
                }
                oEditor.putInt("index", index);
                oEditor.commit();
        }
        sendBroadcast(intent);
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        Intent intent = new Intent("com.example.service");
        intent.putExtra("current", seekBar.getProgress());
        sendBroadcast(intent);
    }

    private String initTime(int s) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format((long) s);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            oEditor.putInt("state", state);
            oEditor.putInt("index", index);
            oEditor.commit();

            Intent intent =new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 1, "退出");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("你确定要退出吗");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MainActivity.this, MusicService.class);
                        stopService(intent);
                        oEditor.clear();
                        System.exit(0);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;

            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        unregisterReceiver(receiver);
    }
}
