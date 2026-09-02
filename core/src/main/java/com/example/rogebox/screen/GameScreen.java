package com.example.rogebox.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.rogebox.RogeBoxGame;
import com.example.rogebox.player.Player;

public class GameScreen implements Screen {

    private final RogeBoxGame game;

    private SpriteBatch batch;

    // =========================
    // BACKGROUND
    // =========================

    private Texture background;

    private float backgroundX = 0f;

    private float backgroundSpeed = 150f;


    // =========================
    // PLAYER
    // =========================

    private Player player;


    public GameScreen(RogeBoxGame game) {
        this.game = game;
    }


    @Override
    public void show() {

        batch = new SpriteBatch();

        background =
            new Texture("background_04.png");


        float screenHeight =
            Gdx.graphics.getHeight();

        float groundY =
            screenHeight * 0.1f;


        player = new Player(
            150f,
            groundY
        );
    }


    @Override
    public void render(float delta) {

        // =========================
        // CLEAR
        // =========================

        Gdx.gl.glClearColor(
            0f,
            0f,
            0f,
            1f
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );


        float screenWidth =
            Gdx.graphics.getWidth();

        float screenHeight =
            Gdx.graphics.getHeight();


        // =========================
        // BACKGROUND
        // =========================

        backgroundX -=
            backgroundSpeed * delta;


        float backgroundHeight =
            screenHeight;


        float backgroundWidth =
            background.getWidth()
                * backgroundHeight
                / background.getHeight();


        if (backgroundX <= -backgroundWidth) {

            backgroundX += backgroundWidth;
        }


        // =========================
        // GROUND
        // =========================

        float groundY =
            screenHeight * 0.31f;


        // =========================
        // PLAYER UPDATE
        // =========================

        player.update(
            delta,
            groundY
        );


        // =========================
        // DRAW
        // =========================

        batch.begin();


        // Background 1
        batch.draw(
            background,
            backgroundX,
            0,
            backgroundWidth,
            backgroundHeight
        );


        // Background 2
        batch.draw(
            background,
            backgroundX + backgroundWidth,
            0,
            backgroundWidth,
            backgroundHeight
        );


        // Background 3
        batch.draw(
            background,
            backgroundX + backgroundWidth * 2,
            0,
            backgroundWidth,
            backgroundHeight
        );


        // Player
        player.render(batch);


        batch.end();
    }


    @Override
    public void resize(
        int width,
        int height
    ) {
    }


    @Override
    public void pause() {
    }


    @Override
    public void resume() {
    }


    @Override
    public void hide() {
    }


    @Override
    public void dispose() {

        batch.dispose();

        background.dispose();

        player.dispose();
    }
}
