package com.example.nanchen.musicdiy.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nanchen.musicdiy.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by nanchen on 2016/4/12.
 */
public class MyAdapter extends BaseAdapter {

    private List<MusicUnit> oList;
    private Context context;
    private LayoutInflater inflater;
    public MyAdapter(List<MusicUnit> m,Context context) {
        oList=m;
        this.context=context;
        this.inflater= LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return oList.size();
    }

    @Override
    public Object getItem(int position) {
        return oList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(holder==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.list_demo, null);
            holder.img=(ImageView)convertView.findViewById(R.id.img);
            holder.name=(TextView) convertView.findViewById(R.id.name);
            holder.author=(TextView) convertView.findViewById(R.id.author);
            holder.duration=(TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.img.setBackgroundResource(R.drawable.ic_launcher);
        holder.name.setText(oList.get(position).getName());
        holder.author.setText(oList.get(position).getAuthor());
        holder.duration.setText(getTime(oList.get(position).getDuration()));
        return convertView;
    }
    private String getTime(long time){
        SimpleDateFormat forma=new SimpleDateFormat("mm:ss");
        String time2=forma.format(time);
        return time2;
    }
    public class ViewHolder{
        ImageView img;
        TextView name;
        TextView author;
        TextView duration;
    }
}

