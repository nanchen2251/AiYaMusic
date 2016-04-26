package com.example.nanchen.musicdiy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * 用于知晓当前是在哪一个活动
 * 活动的最佳写法
 * Created by nanchen on 2016/4/26.
 */
public class BaseActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
