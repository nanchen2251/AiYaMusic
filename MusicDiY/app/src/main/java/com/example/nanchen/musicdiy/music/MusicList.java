package com.example.nanchen.musicdiy.music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by nanchen on 2016/4/12.
 */
public class MusicList {
    public static List<MusicUnit> getMusicData(Context context) {
        List<MusicUnit> oList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MusicUnit mUnit = new MusicUnit();
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String author = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                if (author.equals("<unknown>"))
                    author = "未知艺术家";
                if (duration > 20000) {
                    mUnit.setName(name);
                    mUnit.setPath(path);
                    mUnit.setDuration(duration);
                    mUnit.setAuthor(author);
                    oList.add(mUnit);
                }
            }
        }
        return oList;
    }
}

