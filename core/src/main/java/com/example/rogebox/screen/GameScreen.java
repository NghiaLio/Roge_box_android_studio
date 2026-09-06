package com.example.rogebox.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.rogebox.RogeBoxGame;
import com.example.rogebox.obstacle.Enemy;
import com.example.rogebox.obstacle.Projectile;
import com.example.rogebox.player.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen implements Screen {

    private final RogeBoxGame game;
    private SpriteBatch batch;

    // Background
    private Texture background;
    private float backgroundY = 0f;
    private float backgroundSpeed = 200f; 

    // Entities
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Projectile> projectiles;

    // UI
    private Stage stage;
    private Touchpad touchpad;
    
    // Score
    private int score = 0;
    private Label scoreLabel;
    private BitmapFont font;

    private float enemySpeed = 300f;
    
    // Shoot cooldown
    private float shootTimer = 0f;
    private float shootCooldown = 0.2f;

    public GameScreen(RogeBoxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture("background3.png");

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Khởi tạo Player ở giữa đáy màn hình
        player = new Player(
            screenWidth / 2f - 75f,
            100f
        );

        enemies = new ArrayList<>();
        // Sinh ra 3 kẻ địch ngẫu nhiên ban đầu
        for (int i = 0; i < 3; i++) {
            enemies.add(new Enemy(screenWidth, screenHeight));
        }

        projectiles = new ArrayList<>();

        // Khởi tạo UI (Joystick)
        setupUI();
    }
    
    private void setupUI() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = createDrawable(new Color(1, 1, 1, 0.3f), 200);
        touchpadStyle.knob = createDrawable(new Color(0.8f, 0.8f, 0.8f, 0.8f), 60);

        touchpad = new Touchpad(10, touchpadStyle);
        // Đặt joystick ở góc dưới bên trái
        touchpad.setBounds(50, 50, 200, 200);

        stage.addActor(touchpad);
        
        // Thêm label đếm điểm
        font = new BitmapFont();
        font.getData().setScale(3f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.YELLOW);
        scoreLabel = new Label("SCORE: 0", labelStyle);
        scoreLabel.setPosition(30, Gdx.graphics.getHeight() - 80);
        stage.addActor(scoreLabel);
    }
    
    private TextureRegionDrawable createDrawable(Color color, int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(size / 2, size / 2, size / 2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        if (player.isDead()) {
            game.setScreen(new GameOverScreen(game));
            return;
        }

        // =========================
        // BACKGROUND UPDATE
        // =========================
        backgroundY -= backgroundSpeed * delta;
        float backgroundWidth = screenWidth;
        float backgroundRenderHeight = background.getHeight() * backgroundWidth / background.getWidth();

        if (backgroundY <= -backgroundRenderHeight) {
            backgroundY += backgroundRenderHeight;
        }

        // =========================
        // SHOOTING INPUT
        // =========================
        shootTimer += delta;
        
        boolean shootIntent = false;
        // Kiểm tra đa điểm (multitouch) để vừa chạy vừa bắn
        for (int i = 0; i < 3; i++) {
            if (Gdx.input.isTouched(i)) {
                int touchX = Gdx.input.getX(i);
                int touchY = Gdx.graphics.getHeight() - Gdx.input.getY(i);
                
                boolean touchOnJoystick = touchX >= 50 && touchX <= 250 && touchY >= 50 && touchY <= 250;
                if (!touchOnJoystick) {
                    shootIntent = true;
                    break;
                }
            }
        }
        
        if (shootIntent && shootTimer >= shootCooldown) {
            projectiles.add(new Projectile(player.getCenterX(), player.getTopY()));
            shootTimer = 0f;
        }

        // =========================
        // PLAYER UPDATE
        // =========================
        player.update(delta, touchpad.getKnobPercentX(), touchpad.getKnobPercentY(), screenWidth, screenHeight);

        // =========================
        // PROJECTILE UPDATE
        // =========================
        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();
            p.update(delta, screenHeight);
            if (!p.isActive()) {
                p.dispose();
                projIter.remove();
            }
        }

        // =========================
        // ENEMY & COLLISION UPDATE
        // =========================
        for (Enemy enemy : enemies) {
            enemy.update(delta, enemySpeed);
            
            // Va chạm Player vs Enemy
            if (enemy.getBounds().overlaps(player.getBounds())) {
                player.die();
            }
            
            // Va chạm Bullet vs Enemy
            for (Projectile p : projectiles) {
                if (p.isActive() && p.getBounds().overlaps(enemy.getBounds())) {
                    p.setActive(false); // Xóa đạn
                    enemy.setActive(false); // Địch bị xóa
                    enemy.setActive(true); // Xuất hiện lại ở đỉnh
                    score += 10;
                    scoreLabel.setText("SCORE: " + score);
                    break;
                }
            }
        }

        // =========================
        // DRAW
        // =========================
        batch.begin();
        
        batch.draw(background, 0, backgroundY, backgroundWidth, backgroundRenderHeight);
        batch.draw(background, 0, backgroundY + backgroundRenderHeight, backgroundWidth, backgroundRenderHeight);
        
        for (Projectile p : projectiles) {
            p.render(batch);
        }
        
        player.render(batch);
        
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }

        batch.end();
        
        // Vẽ UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        player.dispose();
        stage.dispose();
        if (font != null) font.dispose();
        for (Enemy enemy : enemies) enemy.dispose();
        for (Projectile p : projectiles) p.dispose();
    }
}
