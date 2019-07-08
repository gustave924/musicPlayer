package com.bignerdranch.android.musicplayer;

public class UserAction {
    public static final int pause = 0;
    public static final int resume = 1;
    public static final int startNewSong = 2;
    public static final int songCompleted = 3;
    public static final int getProgress = 4;
    public static final int sendProgress = 5;

    private int state;
    private int songDuration;
    private int currentPosition;

    public UserAction(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(int songDuration) {
        this.songDuration = songDuration;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
