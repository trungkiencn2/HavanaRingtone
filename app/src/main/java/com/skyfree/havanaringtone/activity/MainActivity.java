package com.skyfree.havanaringtone.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfree.havanaringtone.R;
import com.skyfree.havanaringtone.adapter.AdapterLvSong;
import com.skyfree.havanaringtone.model.Song;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView mLvMusic;
    private AdapterLvSong mAdapter;
    private ArrayList<Song> mListSong;
    private MediaPlayer mPlayer;
    private ArrayList<String> mListPath;
    private ArrayList<Integer> mListResourceId;
    private ArrayList<String> mListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        addPermission();
        addEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer.release();
        }
    }

    private void initView(){
        mLvMusic = (ListView) findViewById(R.id.lv_music);
        mPlayer = new MediaPlayer();
    }

    private void addEvent(){
        mListSong = new ArrayList<>();
        mListResourceId = new ArrayList<>();
        mListName = new ArrayList<>();
        listRaw();
        for(int i = 0; i<mListSong.size(); i++){
            mListResourceId.add(mListSong.get(i).getSound());
            mListName.add(mListSong.get(i).getName());
        }
        saveAs(mListResourceId, mListName);


        mAdapter = new AdapterLvSong(this, mListSong);
        mLvMusic.setAdapter(mAdapter);
        mLvMusic.setOnItemClickListener(this);
        mLvMusic.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer.release();
        }

        mPlayer = MediaPlayer.create(this, mListSong.get(position).getSound());
        mPlayer.start();

    }

    private void addPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                addEvent();
            } else {
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS}, 11);
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS},11);
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            addEvent();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                // Do stuff here
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    addEvent();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    addPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void listRaw(){
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            try {
                mListSong.add(new Song(fields[count].getName(), fields[count].getInt(fields[count])));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean saveAs(ArrayList<Integer> resSoundId, ArrayList<String> fileName) {
        mListPath = new ArrayList<>();
        byte[] buffer = null;
        for(int i = 0; i<resSoundId.size(); i++){
            InputStream fIn = getBaseContext().getResources().openRawResource(resSoundId.get(i));
            int size = 0;
            try {
                size = fIn.available();
                buffer = new byte[size];
                fIn.read(buffer);
                fIn.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }
            String path = "/mnt/sdcard/yourapp/temp/";
            boolean exists = (new File(path)).exists();
            if (!exists) {
                new File(path).mkdirs();
            }
            mListPath.add(path + fileName.get(i));
            FileOutputStream save;
            try {
                save = new FileOutputStream(path + fileName.get(i));
                save.write(buffer);
                save.flush();
                save.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }
        }
        return true;
    }

    private void setDefaultAll(int index) {
        File k = new File("/mnt/sdcard/yourapp/temp/" + mListName.get(index));

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, mListName.get(index));
        values.put(MediaStore.MediaColumns.SIZE, 215454);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, "Madonna");
        values.put(MediaStore.Audio.Media.DURATION, 2346);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
        getContentResolver().delete(
                uri,
                MediaStore.MediaColumns.DATA + "=\""
                        + k.getAbsolutePath() + "\"", null);
        Uri newUri = this.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(
                this,
                RingtoneManager.TYPE_ALL,
                newUri
        );
    }

    private void setDefaultAlarm(int index) {
        File k = new File("/mnt/sdcard/yourapp/temp/" + mListName.get(index));

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, mListName.get(index));
        values.put(MediaStore.MediaColumns.SIZE, 215454);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, "Madonna");
        values.put(MediaStore.Audio.Media.DURATION, 2346);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
        getContentResolver().delete(
                uri,
                MediaStore.MediaColumns.DATA + "=\""
                        + k.getAbsolutePath() + "\"", null);
        Uri newUri = this.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(
                this,
                RingtoneManager.TYPE_ALARM,
                newUri
        );
    }

    private void setDefaultNotify(int index) {
        File k = new File("/mnt/sdcard/yourapp/temp/" + mListName.get(index));

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, mListName.get(index));
        values.put(MediaStore.MediaColumns.SIZE, 215454);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, "Madonna");
        values.put(MediaStore.Audio.Media.DURATION, 2345);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
        getContentResolver().delete(
                uri,
                MediaStore.MediaColumns.DATA + "=\""
                        + k.getAbsolutePath() + "\"", null);
        Uri newUri = this.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(
                this,
                RingtoneManager.TYPE_NOTIFICATION,
                newUri
        );
    }

    private void setDefaultRingtone(int index) {

        File k = new File("/mnt/sdcard/yourapp/temp/" + mListName.get(index));

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, mListName.get(index));
        values.put(MediaStore.MediaColumns.SIZE, 215454);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, "Madonna");
        values.put(MediaStore.Audio.Media.DURATION, 2345);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
        getContentResolver().delete(
                uri,
                MediaStore.MediaColumns.DATA + "=\""
                        + k.getAbsolutePath() + "\"", null);
        Uri newUri = this.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(
                this,
                RingtoneManager.TYPE_RINGTONE,
                newUri
        );

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        dialogBuilder.setView(dialogView);

        TextView mTvSetAlarm = (TextView) dialogView.findViewById(R.id.tv_set_alarm);
        TextView mTvSetRingtone = (TextView) dialogView.findViewById(R.id.tv_set_ring_tone);
        TextView mTvSetNotify = (TextView) dialogView.findViewById(R.id.tv_set_notify);
        TextView mTvSetForAll = (TextView) dialogView.findViewById(R.id.tv_set_for_all);

        final AlertDialog alertStartDialog = dialogBuilder.create();
        alertStartDialog.show();

        mTvSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultAlarm(position);
                Toast.makeText(MainActivity.this, getString(R.string.you_set_alarm), Toast.LENGTH_SHORT).show();
                alertStartDialog.cancel();
            }
        });

        mTvSetRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultRingtone(position);
                Toast.makeText(MainActivity.this, getString(R.string.you_set_ringtone), Toast.LENGTH_SHORT).show();
                alertStartDialog.cancel();
            }
        });

        mTvSetNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getString(R.string.you_set_notify), Toast.LENGTH_SHORT).show();
                setDefaultNotify(position);
                alertStartDialog.cancel();
            }
        });

        mTvSetForAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultAll(position);
                Toast.makeText(MainActivity.this, getString(R.string.you_set_all), Toast.LENGTH_SHORT).show();
                alertStartDialog.cancel();
            }
        });

        return true;
    }
}
