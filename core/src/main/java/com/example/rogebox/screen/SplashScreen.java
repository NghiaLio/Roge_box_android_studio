package com.example.rogebox.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.example.rogebox.RogeBoxGame;

public class SplashScreen implements Screen {

    private final RogeBoxGame game;

    private SpriteBatch batch;
    private Texture introImage;
    private ShapeRenderer shapeRenderer;

    private float elapsedTime = 0f;
    private float loadingProgress = 0f;

    public SplashScreen(RogeBoxGame game) {
        this.game = game;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Load ảnh trong thư mục assets
        introImage = new Texture("initialScreen.png");
    }

    @Override
    public void render(float delta) {

        elapsedTime += delta;

        // Loading từ 0 -> 100% trong 3 giây
        loadingProgress = MathUtils.clamp(
            elapsedTime / 3f,
            0f,
            1f
        );

        // Xóa màn hình
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // =========================
        // VẼ ẢNH INTRO
        // =========================

        batch.begin();

        batch.draw(
            introImage,
            0,
            0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        batch.end();

        // =========================
        // VẼ LOADING BAR
        // =========================

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float barWidth = screenWidth * 0.5f;
        float barHeight = 25f;

        float barX = (screenWidth - barWidth) / 2f;
        float barY = screenHeight * 0.08f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background của loading bar
        shapeRenderer.setColor(0f, 0f, 0f, 0.7f);

        shapeRenderer.rect(
            barX,
            barY,
            barWidth,
            barHeight
        );

        // Phần loading
        shapeRenderer.setColor(0.9f, 0.1f, 0.05f, 1f);

        shapeRenderer.rect(
            barX,
            barY,
            barWidth * loadingProgress,
            barHeight
        );

        shapeRenderer.end();

        // =========================
        // CHUYỂN SANG HOME
        // =========================

        if (elapsedTime >= 3f) {

            game.setScreen(
                new HomeScreen(game)
            );
        }
    }

    @Override
    public void resize(int width, int height) {
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
        introImage.dispose();
        shapeRenderer.dispose();
    }
}
