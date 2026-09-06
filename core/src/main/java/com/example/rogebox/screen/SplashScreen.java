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
    private Texture background;
    private Texture plane;
    private ShapeRenderer shapeRenderer;

    private float backgroundY = 0f;

    private float elapsedTime = 0f;
    private float loadingProgress = 0f;

    public SplashScreen(RogeBoxGame game) {
        this.game = game;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Sử dụng nền và phi thuyền làm Splash screen
        background = new Texture("background3.png");
        plane = new Texture("shipMain.png");
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
        // CẬP NHẬT VÀ VẼ NỀN CUỘN
        // =========================
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        backgroundY -= 100f * delta;
        float backgroundRenderHeight = background.getHeight() * screenWidth / background.getWidth();

        if (backgroundY <= -backgroundRenderHeight) {
            backgroundY += backgroundRenderHeight;
        }

        batch.begin();

        batch.draw(background, 0, backgroundY, screenWidth, backgroundRenderHeight);
        batch.draw(background, 0, backgroundY + backgroundRenderHeight, screenWidth, backgroundRenderHeight);

        // Vẽ phi thuyền ở giữa
        float planeWidth = 200f;
        float planeHeight = 200f;
        float planeX = (screenWidth - planeWidth) / 2f;
        // Phi thuyền bay lên dần
        float planeY = (screenHeight / 2f) - 100f + (loadingProgress * 200f);
        
        batch.draw(plane, planeX, planeY, planeWidth, planeHeight);

        batch.end();

        // =========================
        // VẼ LOADING BAR
        // =========================

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
        background.dispose();
        plane.dispose();
        shapeRenderer.dispose();
    }
}
