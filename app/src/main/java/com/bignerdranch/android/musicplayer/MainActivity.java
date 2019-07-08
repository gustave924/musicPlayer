package com.bignerdranch.android.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final static int STORAGE_PERMISSION_REQUEST_CODE = 1001;

    private final static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    interface OnUserActions{
        void onPause();
        void onResume();
    }

    private List<Song> mSongList = new ArrayList<>();
    private MainAdapter mAdapter;
    private ImageButton mPauseButton;
    private ImageButton mPlayButton;
    private SeekBar mSeekBar;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView songsRecyclerView = findViewById(R.id.activity_main_songs_recycler_viewer);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MainAdapter(mSongList, this);
        songsRecyclerView.setAdapter(mAdapter);



        mPauseButton =  findViewById(R.id.pauseButton);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new UserAction(UserAction.pause));
                synchronized (EventBus.getDefault()){
                    EventBus.getDefault().notifyAll();
                }
            }
        });
        mPlayButton = findViewById(R.id.playButton);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new UserAction(UserAction.resume));
                synchronized (EventBus.getDefault()){
                    EventBus.getDefault().notifyAll();
                }
            }
        });
        mSeekBar = findViewById(R.id.seekBar);

        /*mSeekBar.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });*/

        MainActivity.this.runOnUiThread(new Runnable() {
            MediaPlayer mMediaPlayer = App.getApp().getPlayer();

            @Override
            public void run() {
                if(mMediaPlayer != null){
                    mSeekBar.setMax(mMediaPlayer.getDuration()/1000);
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                    mSeekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        if(askForPermissions()){
            mSongList.addAll(getAllAudioFromDevice(this));
            mAdapter.notifyDataSetChanged();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        //EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //EventBus.getDefault().register(this);
    }

    public List<Song> getAllAudioFromDevice(final Context context) {
        final List<Song> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection,
                null, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                Song audioModel = new Song();
                String path = c.getString(0);
                String name = c.getString(1);
                String album = c.getString(2);
                String artist = c.getString(3);

                audioModel.setaName(name);
                audioModel.setaAlbum(album);
                audioModel.setaArtist(artist);
                audioModel.setaPath(path);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);
            }
            c.close();
        }

        return tempAudioList;
    }

    public boolean askForPermissions() throws SecurityException{
        if (ContextCompat.checkSelfPermission(this,
             PERMISSIONS[0])
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                   PERMISSIONS[0])) {

            } else {
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS,
                        STORAGE_PERMISSION_REQUEST_CODE);


            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mSongList.addAll(getAllAudioFromDevice(MainActivity.this));
                    mAdapter.notifyDataSetChanged();
                } else {
                    finish();
                }
                return;
            }
        }
    }



}
