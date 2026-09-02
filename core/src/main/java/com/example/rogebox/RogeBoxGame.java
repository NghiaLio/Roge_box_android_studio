package com.example.rogebox;

import com.badlogic.gdx.Game;
import com.example.rogebox.screen.SplashScreen;

public class RogeBoxGame extends Game {

    @Override
    public void create() {
        setScreen(new SplashScreen(this));
    }
}
