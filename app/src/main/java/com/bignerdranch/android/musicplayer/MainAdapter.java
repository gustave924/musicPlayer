package com.bignerdranch.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.SongViewHolder> {

    private List<Song> mSongs;
    private Context mContext;

    public MainAdapter(List<Song> songs, Context context) {
        mSongs = songs;
        mContext = context;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.activity_main_recycler_viewer_song, viewGroup, false);
        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int i) {
        songViewHolder.bind(mSongs.get(i));
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder{

        private Button mSongNameTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mSongNameTextView = itemView.findViewById(R.id.songNameButton);
        }

        public void bind(final Song song){
            mSongNameTextView.setText(song.getaName());
            mSongNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(song.getaPath());
                    Intent i = PlaySongService.newInstance(mContext, song);
                    EventBus.getDefault().post(new UserAction(UserAction.startNewSong));

                    mContext.startService(i);
                }
            });
        }
    }

}
