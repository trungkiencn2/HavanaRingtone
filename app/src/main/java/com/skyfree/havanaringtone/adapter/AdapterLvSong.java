package com.skyfree.havanaringtone.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfree.havanaringtone.R;
import com.skyfree.havanaringtone.activity.MainActivity;
import com.skyfree.havanaringtone.model.Song;

import java.util.ArrayList;

/**
 * Created by KienBeu on 4/17/2018.
 */

public class AdapterLvSong extends BaseAdapter{

    private Context mContext;
    private ArrayList<Song> mListSong;
    private MediaPlayer mPlayer;

    public AdapterLvSong(Context mContext, ArrayList<Song> mListSong) {
        this.mContext = mContext;
        this.mListSong = mListSong;
    }

    @Override
    public int getCount() {
        return mListSong.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        mPlayer = new MediaPlayer();

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRow = inflater.inflate(R.layout.item_music, null);
        TextView mTvNameOfSong = (TextView) mRow.findViewById(R.id.tv_name_song);

        Song song = mListSong.get(position);
        mTvNameOfSong.setText(song.getName());

//        mImgPlayStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mPlayer.isPlaying()){
//                    mPlayer.stop();
//                    mPlayer.reset();
//                    mImgPlayStop.setImageResource(R.drawable.btn_play);
//                }else {
//                    try {
//                        AssetFileDescriptor afd = mContext.getAssets().openFd("music/" + MainActivity.mListSong.get(position).getName() + ".mp3");
//                        mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//                        afd.close();
//                        mPlayer.prepare();
//                        mPlayer.start();
//                        mImgPlayStop.setImageResource(R.drawable.btn_pause);
//                    } catch (final Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//            }
//        });
//
//        mImgUse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return mRow;
    }
}
