package com.bignerdranch.android.musicplayer;

import android.app.Application;
import android.media.MediaPlayer;

public class App extends Application {
    private static  App app;
    private MediaPlayer mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mPlayer = new MediaPlayer();
    }

    public static App getApp() {
        return app;
    }

    public MediaPlayer getPlayer() {
        return mPlayer;
    }
}
