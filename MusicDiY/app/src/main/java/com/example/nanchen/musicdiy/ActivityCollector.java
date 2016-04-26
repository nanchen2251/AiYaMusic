package com.example.nanchen.musicdiy;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 退出程序的最佳方案，写一个活动收集器
 * Created by nanchen on 2016/4/26.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity:activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
