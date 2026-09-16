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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    // Textures
    private Texture bullet1Tex;
    private Texture bullet2Tex;
    private Texture bullet3Tex;
    private Texture soundOnTex;
    private Texture soundOffTex;
    private Texture musicOnTex;
    private Texture musicOffTex;

    // Audio
    private Sound shootSound;
    private Sound warningSound;
    private Music backgroundMusic;
    private int warningCount = 0;
    private float warningTimer = 0f;

    // UI
    private Stage stage;
    private Touchpad touchpad;
    private ImageButton soundToggle;
    private ImageButton musicToggle;

    // Score & HUD
    private int score = 0;
    private Label hudLabel;
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

        // Load Textures
        bullet1Tex = new Texture("bullet1.png");
        bullet2Tex = new Texture("projectile_types_2.png");
        bullet3Tex = new Texture("projectile_types_3.png");
        soundOnTex = new Texture("volume_on.png");
        soundOffTex = new Texture("voulme_off.png");
        musicOnTex = new Texture("music_on.png");
        musicOffTex = new Texture("music_off.png");

        // Load Audio (Using Gdx.audio)
        try {
            shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.mp3"));
            warningSound = Gdx.audio.newSound(Gdx.files.internal("warning.mp3"));
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f); // Đặt âm lượng vừa phải

            // Ép buộc phát nhạc nếu musicEnabled đang bật
            if (game.isMusicEnabled()) {
                backgroundMusic.play();
                Gdx.app.log("GameScreen", "Music started playing");
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Audio files missing or error: " + e.getMessage());
        }

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

        // Khởi tạo UI (Joystick, Buttons, HUD)
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

        // Font for HUD
        font = new BitmapFont();
        font.getData().setScale(2f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        hudLabel = new Label("", labelStyle);
        hudLabel.setPosition(30, Gdx.graphics.getHeight() - 150);
        stage.addActor(hudLabel);

        // Sound Toggle Button
        ImageButton.ImageButtonStyle soundStyle = new ImageButton.ImageButtonStyle();
        soundStyle.up = new TextureRegionDrawable(new TextureRegion(soundOnTex));
        soundStyle.checked = new TextureRegionDrawable(new TextureRegion(soundOffTex));
        soundToggle = new ImageButton(soundStyle);
        soundToggle.setSize(64, 64);
        soundToggle.setPosition(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80);
        soundToggle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setSoundEnabled(!soundToggle.isChecked());
            }
        });
        stage.addActor(soundToggle);

        // Music Toggle Button
        ImageButton.ImageButtonStyle musicStyle = new ImageButton.ImageButtonStyle();
        musicStyle.up = new TextureRegionDrawable(new TextureRegion(musicOnTex));
        musicStyle.checked = new TextureRegionDrawable(new TextureRegion(musicOffTex));
        musicToggle = new ImageButton(musicStyle);
        musicToggle.setSize(64, 64);
        musicToggle.setPosition(Gdx.graphics.getWidth() - 160, Gdx.graphics.getHeight() - 80);
        musicToggle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setMusicEnabled(!musicToggle.isChecked());
                if (game.isMusicEnabled()) {
                    if (backgroundMusic != null) backgroundMusic.play();
                } else {
                    if (backgroundMusic != null) backgroundMusic.pause();
                }
            }
        });
        stage.addActor(musicToggle);
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
        // SHOOTING INPUT & ATTACK MECHANISMS
        // =========================
        shootTimer += delta;

        boolean shootIntent = false;
        // Kiểm tra đa điểm (multitouch) để vừa chạy vừa bắn
        for (int i = 0; i < 3; i++) {
            if (Gdx.input.isTouched(i)) {
                int touchX = Gdx.input.getX(i);
                int touchY = Gdx.graphics.getHeight() - Gdx.input.getY(i);

                boolean touchOnUI = (touchX >= 50 && touchX <= 250 && touchY >= 50 && touchY <= 250) ||
                                     (touchX >= screenWidth - 250 && touchY >= screenHeight - 250);
                if (!touchOnUI) {
                    shootIntent = true;
                    break;
                }
            }
        }

        if (shootIntent && shootTimer >= shootCooldown) {
            // Cơ chế 1: Bắn cơ bản (Luôn kích hoạt)
            projectiles.add(new Projectile(player.getCenterX(), player.getTopY(), bullet1Tex));

            // Cơ chế 2: Bắn đôi (Chỉ kích hoạt khi SCORE > 100)
            if (score > 100) {
                // Tăng kích thước bằng cách truyền Texture riêng nếu cần,
                // nhưng hiện tại ta tăng kích thước chung trong Projectile.java
                projectiles.add(new Projectile(player.getX() - 20, player.getTopY(), bullet2Tex));
                projectiles.add(new Projectile(player.getX() + player.getWidth() + 20, player.getTopY(), bullet2Tex));
            }

            // Cơ chế 3: Bắn ba (Chỉ kích hoạt khi SCORE > 200)
            if (score > 200) {
                // Tọa độ bắn rộng hơn cho đạn 3
                projectiles.add(new Projectile(player.getCenterX() - 100, player.getTopY() - 40, bullet3Tex));
                projectiles.add(new Projectile(player.getCenterX() + 100, player.getTopY() - 40, bullet3Tex));
            }

            if (game.isSoundEnabled() && shootSound != null) shootSound.play();
            shootTimer = 0f;
        }

        // =========================
        // DEFENSE MECHANISMS
        // =========================
        // 1. Shield (Auto-activate when health < 30)
        if (player.getHealth() < 30 && !player.isShieldActive()) {
            player.activateShield(5f);
        }

        // 2. Slow Motion / Enemy Disable (if shield is active, enemies slow down)
        float currentEnemySpeed = enemySpeed;
        if (player.isShieldActive()) currentEnemySpeed *= 0.5f;

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
        boolean anyEnemyInRestrictedArea = false;
        float restrictedAreaY = screenHeight * 0.2f;

        for (Enemy enemy : enemies) {
            enemy.update(delta, currentEnemySpeed);

            // Check restricted area for warning
            if (enemy.getBounds().getY() < restrictedAreaY) {
                anyEnemyInRestrictedArea = true;
            }

            // Va chạm Player vs Enemy (Object X)
            if (enemy.getBounds().overlaps(player.getBounds())) {
                // Effects implementation:
                // 1. Health Reduction
                // 2. Armor Reduction (handled in takeDamage)
                // 3. Speed Reduction (temporary)
                player.takeDamage(15);
                player.setSpeed(300f);

                enemy.setActive(false);
                enemy.setActive(true);
            }

            // Va chạm Bullet vs Enemy
            for (Projectile p : projectiles) {
                if (p.isActive() && p.getBounds().overlaps(enemy.getBounds())) {
                    p.setActive(false);
                    enemy.setActive(false);
                    enemy.setActive(true);

                    // 4. Gold increase
                    player.addGold(10);
                    // 5. Score increase
                    score += 10;

                    // 6. Bonus Armor on high score
                    if (score % 100 == 0) player.addArmor(10);
                    break;
                }
            }
        }

        // Restore player speed slowly
        if (player.getSpeed() < 600f) {
            player.setSpeed(Math.min(600f, player.getSpeed() + 100f * delta));
        }

        // Warning sound logic (3-6 lần)
        if (anyEnemyInRestrictedArea) {
            warningTimer += delta;
            // Phát âm thanh cảnh báo sau mỗi 0.8 giây
            if (warningTimer >= 0.8f && warningCount < 6) {
                if (game.isSoundEnabled() && warningSound != null) {
                    warningSound.play();
                }
                warningCount++;
                warningTimer = 0f;
            }
        } else {
            // Reset khi không còn địch trong vùng cấm
            warningCount = 0;
            warningTimer = 0f;
        }

        // HUD Update
        hudLabel.setText(String.format("SCORE: %d | GOLD: %d\nHEALTH: %d | ARMOR: %d",
                score, player.getGold(), player.getHealth(), player.getArmor()));

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

        if (shootSound != null) shootSound.dispose();
        if (warningSound != null) warningSound.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();

        if (bullet1Tex != null) bullet1Tex.dispose();
        if (bullet2Tex != null) bullet2Tex.dispose();
        if (bullet3Tex != null) bullet3Tex.dispose();
        if (soundOnTex != null) soundOnTex.dispose();
        if (soundOffTex != null) soundOffTex.dispose();
        if (musicOnTex != null) musicOnTex.dispose();
        if (musicOffTex != null) musicOffTex.dispose();
    }
}
