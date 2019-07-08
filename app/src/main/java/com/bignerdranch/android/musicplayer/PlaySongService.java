package com.bignerdranch.android.musicplayer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class PlaySongService extends Service implements MediaPlayer.OnCompletionListener  {
    private final static int SERVICE_ID = 1001;
    private final static int NOTIFICATION_ID = 1002;
    private final static String TAG = "PlaySongService";
    private final static String SONG = "SONG";
    private MediaPlayer mediaPlayer;
    private int lastPosition = 0;
    private static PlaySongService currentSong = null;

    public static Intent newInstance(Context context, Song song){
        Intent i = new Intent(context, PlaySongService.class);
        i.putExtra(SONG, song);
        return i;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            Song song = intent.getParcelableExtra(SONG);

            Intent i = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(song.getaName())
                    .setContentText(song.getaArtist())
                    .setContentText(song.getaAlbum())
                    .setContentIntent(pi)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.icons8_lounge_music_playlist_80)
                    .setPriority(Notification.PRIORITY_LOW)
                    .build();
            startForeground(SERVICE_ID, notification);

            Uri myUri = Uri.parse(song.getaPath());
            mediaPlayer = App.getApp().getPlayer();
            mediaPlayer.reset();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), myUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Log.d(TAG, "onStartCommand: "+mediaPlayer.getDuration());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return START_STICKY;
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(UserAction action){
        if(action.getState() == UserAction.pause){
            lastPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }else if(action.getState() == UserAction.resume){
            mediaPlayer.seekTo(lastPosition);
            mediaPlayer.start();
        }else if(action.getState() == UserAction.startNewSong){
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "onDestroy: PlaySongService is gone");
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
