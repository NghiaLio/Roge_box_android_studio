package com.example.rogebox;

import com.badlogic.gdx.Game;
import com.example.rogebox.screen.SplashScreen;

public class RogeBoxGame extends Game {

    private boolean soundEnabled = true;
    private boolean musicEnabled = true;

    @Override
    public void create() {
        setScreen(new SplashScreen(this));
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }
}
