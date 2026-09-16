package com.example.rogebox.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.example.rogebox.RogeBoxGame;
import com.example.rogebox.effect.ExplosionEffect;
import com.example.rogebox.obstacle.Enemy;
import com.example.rogebox.obstacle.Obstacle;
import com.example.rogebox.obstacle.PowerUp;
import com.example.rogebox.obstacle.Projectile;
import com.example.rogebox.player.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen implements Screen {

    private final RogeBoxGame game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // Background
    private Texture background;
    private float backgroundY = 0f;
    private float backgroundSpeed = 200f;

    // Entities
    private Player player;
    private ArrayList<Enemy> enemies;       // Object X (NPC B / Creeps)
    private ArrayList<Obstacle> obstacles; // Object Y (Bẫy/Chướng ngại vật)
    private ArrayList<PowerUp> powerUps;   // Object Z (Rương báu / PowerUps)
    private ArrayList<Projectile> projectiles;
    private ArrayList<ExplosionEffect> explosionEffects;

    // Textures
    private Texture bullet1Tex;
    private Texture bullet2Tex;
    private Texture bombTex;
    private Texture soundOnTex;
    private Texture soundOffTex;
    private Texture musicOnTex;
    private Texture musicOffTex;
    private Texture playPauseTex;
    private Texture resumeTex;
    private Texture restartTex;
    private Texture homeTex;
    private Texture buttonTex;
    private Texture coinIconTex;
    private Texture healthIconTex;
    private Texture energyIconTex;
    private Texture shieldIconTex;

    // Audio
    private Sound shootSound;
    private Sound bombSound;
    private Music backgroundMusic;

    // UI & Touch Controls
    private Stage stage;
    private Touchpad touchpad;
    private ImageButton soundToggle;
    private ImageButton musicToggle;

    // HUD Stats (3 core stats: HP, Energy, Coin/Score)
    private Label hpLabel;
    private Label energyLabel;
    private Label coinLabel;
    private BitmapFont fontHUD;

    // Weapon Selection & Cooldowns
    private enum WeaponType { BULLET, MISSILE, BOMB }
    private WeaponType selectedWeapon = WeaponType.BULLET;
    private ImageButton btnShootMain;
    private ImageButton selBullet, selMissile, selBomb, selShield, selStun;

    // Cooldown Max Values
    private float bulletCooldown = 0.15f;
    private float missileMaxCooldown = 2.0f;
    private float bombMaxCooldown = 4.0f;
    private float shieldMaxCooldown = 8.0f;
    private float stunMaxCooldown = 12.0f;

    // Cooldown Current Timers (0 means ready)
    private float missileCDTimer = 0f;
    private float bombCDTimer = 0f;
    private float shieldCDTimer = 0f;
    private float stunCDTimer = 0f;

    private boolean paused = false;
    private float gameOverTimer = 0f;
    private boolean playerExploded = false;
    private ImageButton resumeButton;
    private ImageButton pauseButton;
    private ImageButton restartButton;
    private ImageButton homeButton;

    // Attack Cooldowns
    private float bulletTimer = 1.0f;

    public GameScreen(RogeBoxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        background = new Texture("background3.png");

        // Textures
        bullet1Tex = new Texture("bullet1.png");
        bullet2Tex = new Texture("projectile_types_2.png");
        bombTex = new Texture("item_bomb.png");
        soundOnTex = new Texture("volume_on.png");
        soundOffTex = new Texture("voulme_off.png");
        musicOnTex = new Texture("music_on.png");
        musicOffTex = new Texture("music_off.png");
        playPauseTex = new Texture("play-pause.png");
        resumeTex = new Texture("resume.png");
        restartTex = new Texture("restart.png");
        homeTex = new Texture("home_2.png");
        buttonTex = new Texture("button.png");

        // HUD Icons (3 core stats: HP, Energy, Coin)
        coinIconTex = new Texture("coin.png");
        healthIconTex = new Texture("item_health.png");
        energyIconTex = new Texture("item_speed.png"); // Năng lượng
        shieldIconTex = new Texture("shield_effect.png");

        // Audio
        try {
            shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.mp3"));
            bombSound = Gdx.audio.newSound(Gdx.files.internal("bomb.mp3"));
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.4f);

            if (game.isMusicEnabled()) {
                backgroundMusic.play();
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Audio init error: " + e.getMessage());
        }

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Player (Object A)
        player = new Player(screenWidth / 2f - 65f, 150f);

        // Object X: Enemies / Creeps (NPC B)
        enemies = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            enemies.add(new Enemy(screenWidth, screenHeight));
        }

        // Object Y: Obstacles / Hazards
        obstacles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            obstacles.add(new Obstacle(screenWidth, screenHeight));
        }

        // Object Z: PowerUps / Reward Chests
        powerUps = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            powerUps.add(new PowerUp(screenWidth, screenHeight));
        }

        projectiles = new ArrayList<>();
        explosionEffects = new ArrayList<>();

        setupUI();
    }

    private void setupUI() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Touchpad Joystick
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = createCircleDrawable(new Color(1f, 1f, 1f, 0.25f), 180);
        touchpadStyle.knob = createCircleDrawable(new Color(0.2f, 0.8f, 1f, 0.7f), 60);

        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(40, 40, 180, 180);
        stage.addActor(touchpad);

        // Fonts & HUD
        fontHUD = new BitmapFont();
        fontHUD.getData().setScale(2.2f);
        setupHUD();

        // System Buttons (Top Right)
        setupSystemButtons();

        // Skill Buttons & Main Fire (MOBA Style Cluster)
        setupSkillCluster();
    }

    private void setupHUD() {
        Label.LabelStyle hpStyle = new Label.LabelStyle(fontHUD, Color.GREEN);
        Label.LabelStyle energyStyle = new Label.LabelStyle(fontHUD, Color.CYAN);
        Label.LabelStyle coinStyle = new Label.LabelStyle(fontHUD, Color.GOLD);

        hpLabel = new Label("100", hpStyle);
        energyLabel = new Label("100", energyStyle);
        coinLabel = new Label("0", coinStyle);

        Table hudTable = new Table();
        hudTable.top().left();
        hudTable.setFillParent(true);
        hudTable.pad(100, 40, 0, 0);

        float iconSize = 48f;

        // 1. HP (Máu)
        hudTable.add(new Image(healthIconTex)).size(iconSize).padRight(15);
        hudTable.add(hpLabel).left().row();

        // 2. ENERGY (Năng lượng)
        hudTable.add(new Image(energyIconTex)).size(iconSize).padRight(15).padTop(12);
        hudTable.add(energyLabel).left().padTop(12).row();

        // 3. COIN (Score/Điểm)
        hudTable.add(new Image(coinIconTex)).size(iconSize).padRight(15).padTop(12);
        hudTable.add(coinLabel).left().padTop(12).row();

        stage.addActor(hudTable);
    }

    private void setupSystemButtons() {
        // Pause Button
        ImageButton.ImageButtonStyle pauseButtonStyle = new ImageButton.ImageButtonStyle();
        pauseButtonStyle.up = new TextureRegionDrawable(new TextureRegion(playPauseTex));
        pauseButton = new ImageButton(pauseButtonStyle);
        pauseButton.setSize(64, 64);
        pauseButton.setPosition(30, Gdx.graphics.getHeight() - 70);
        pauseButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { setPaused(true); }
        });
        stage.addActor(pauseButton);

        // Resume Button
        ImageButton.ImageButtonStyle resumeButtonStyle = new ImageButton.ImageButtonStyle();
        resumeButtonStyle.up = new TextureRegionDrawable(new TextureRegion(resumeTex));
        resumeButton = new ImageButton(resumeButtonStyle);
        resumeButton.setSize(128, 128);
        resumeButton.setPosition(Gdx.graphics.getWidth() / 2f - 64, Gdx.graphics.getHeight() / 2f + 140);
        resumeButton.setVisible(false);
        resumeButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { setPaused(false); }
        });
        stage.addActor(resumeButton);

        // Restart Button
        ImageButton.ImageButtonStyle restartButtonStyle = new ImageButton.ImageButtonStyle();
        restartButtonStyle.up = new TextureRegionDrawable(new TextureRegion(restartTex));
        restartButton = new ImageButton(restartButtonStyle);
        restartButton.setSize(128, 128);
        restartButton.setPosition(Gdx.graphics.getWidth() / 2f - 64, Gdx.graphics.getHeight() / 2f);
        restartButton.setVisible(false);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                game.setScreen(new GameScreen(game));
            }
        });
        stage.addActor(restartButton);

        // Home Button
        ImageButton.ImageButtonStyle homeButtonStyle = new ImageButton.ImageButtonStyle();
        homeButtonStyle.up = new TextureRegionDrawable(new TextureRegion(homeTex));
        homeButton = new ImageButton(homeButtonStyle);
        homeButton.setSize(128, 128);
        homeButton.setPosition(Gdx.graphics.getWidth() / 2f - 64, Gdx.graphics.getHeight() / 2f - 140);
        homeButton.setVisible(false);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                game.setScreen(new HomeScreen(game));
            }
        });
        stage.addActor(homeButton);

        // Sound Toggle
        ImageButton.ImageButtonStyle soundStyle = new ImageButton.ImageButtonStyle();
        soundStyle.up = new TextureRegionDrawable(new TextureRegion(soundOnTex));
        soundStyle.checked = new TextureRegionDrawable(new TextureRegion(soundOffTex));
        soundToggle = new ImageButton(soundStyle);
        soundToggle.setChecked(!game.isSoundEnabled());
        soundToggle.setSize(64, 64);
        soundToggle.setPosition(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80);
        soundToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setSoundEnabled(!soundToggle.isChecked());
            }
        });
        stage.addActor(soundToggle);

        // Music Toggle
        ImageButton.ImageButtonStyle musicStyle = new ImageButton.ImageButtonStyle();
        musicStyle.up = new TextureRegionDrawable(new TextureRegion(musicOnTex));
        musicStyle.checked = new TextureRegionDrawable(new TextureRegion(musicOffTex));
        musicToggle = new ImageButton(musicStyle);
        musicToggle.setChecked(!game.isMusicEnabled());
        musicToggle.setSize(64, 64);
        musicToggle.setPosition(Gdx.graphics.getWidth() - 160, Gdx.graphics.getHeight() - 80);
        musicToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean enabled = !musicToggle.isChecked();
                game.setMusicEnabled(enabled);
                if (backgroundMusic != null) {
                    if (enabled) backgroundMusic.play();
                    else backgroundMusic.pause();
                }
            }
        });
        stage.addActor(musicToggle);
    }

    private void setPaused(boolean p) {
        paused = p;
        resumeButton.setVisible(paused);
        restartButton.setVisible(paused);
        homeButton.setVisible(paused);
        pauseButton.setVisible(!paused);
    }

    private void setupSkillCluster() {
        float clusterX = Gdx.graphics.getWidth() - 200; // Dời cụm nút sang trái nhiều hơn
        float clusterY = 100;

        Color blueBg = new Color(0.1f, 0.4f, 0.9f, 0.7f);

        // MAIN SHOOT BUTTON (Large)
        ImageButton.ImageButtonStyle fireStyle = new ImageButton.ImageButtonStyle();
        fireStyle.up = createFramedDrawable(new Color(1f, 0.2f, 0.2f, 0.6f), 160);
        fireStyle.down = createFramedDrawable(new Color(1f, 0.2f, 0.2f, 0.9f), 160);
        fireStyle.imageUp = new TextureRegionDrawable(new TextureRegion(bullet1Tex));

        btnShootMain = new ImageButton(fireStyle);
        btnShootMain.setSize(160, 160);
        btnShootMain.setPosition(clusterX - 80, clusterY -50);
        btnShootMain.getImageCell().size(90).center();
        btnShootMain.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { executeFire(); }
        });
        stage.addActor(btnShootMain);

        // Surrounding Selection Buttons (Orbiting the fire button)
        float radius = 220f;
        int orbitBtnSize = 100; // Tăng kích thước nút vệ tinh từ 80 lên 100

        // Selection 1: Normal BULLET
        selBullet = createCircularSkillButton(bullet1Tex, blueBg, orbitBtnSize);
        setPositionInArc(selBullet, clusterX, clusterY, radius, 165);
        selBullet.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { selectWeapon(WeaponType.BULLET); }
        });
        stage.addActor(selBullet);

        // Selection 2: MISSILE (Viên đạn 2)
        selMissile = createCircularSkillButton(bullet2Tex, blueBg, orbitBtnSize);
        // Phóng to ảnh đạn 2 riêng biệt trong nút
        selMissile.getImageCell().size(orbitBtnSize * 1.2f).center();
        setPositionInArc(selMissile, clusterX, clusterY, radius, 135);
        selMissile.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { selectWeapon(WeaponType.MISSILE); }
        });
        stage.addActor(selMissile);

        // Selection 3: BOMB
        selBomb = createCircularSkillButton(bombTex, blueBg, orbitBtnSize);
        selBomb.getImageCell().size(orbitBtnSize * 0.5f).center();
        setPositionInArc(selBomb, clusterX, clusterY, radius, 105);
        selBomb.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { selectWeapon(WeaponType.BOMB); }
        });
        stage.addActor(selBomb);

        // Utility: SHIELD (Instant Use)
        selShield = createCircularSkillButton(shieldIconTex, blueBg, orbitBtnSize);
        setPositionInArc(selShield, clusterX, clusterY, radius, 75);
        selShield.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { useDefenseShield(); }
        });
        stage.addActor(selShield);

        // Utility: STUN (Instant Use)
        selStun = createCircularSkillButton(energyIconTex, blueBg, orbitBtnSize);
        setPositionInArc(selStun, clusterX, clusterY, radius, 45);
        selStun.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { useDefenseStunPulse(); }
        });
        stage.addActor(selStun);

        selectWeapon(WeaponType.BULLET); // Default
    }

    private void selectWeapon(WeaponType type) {
        selectedWeapon = type;
        // Update main button icon
        Texture icon = bullet1Tex;
        if (type == WeaponType.MISSILE) icon = bullet2Tex;
        if (type == WeaponType.BOMB) icon = bombTex;
        btnShootMain.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(icon));

        // Highlight logic: update backgrounds
        updateSelectionHighlight();
    }

    private void updateSelectionHighlight() {
        Color blueBg = new Color(0.1f, 0.4f, 0.9f, 0.7f);
        Color highlighted = new Color(1f, 1f, 0f, 0.9f); // Yellow for highlight
        int orbitBtnSize = 100;

        selBullet.getStyle().up = createFramedDrawable(selectedWeapon == WeaponType.BULLET ? highlighted : blueBg, orbitBtnSize);
        selMissile.getStyle().up = createFramedDrawable(selectedWeapon == WeaponType.MISSILE ? highlighted : blueBg, orbitBtnSize);
        selBomb.getStyle().up = createFramedDrawable(selectedWeapon == WeaponType.BOMB ? highlighted : blueBg, orbitBtnSize);
    }

    private void executeFire() {
        switch (selectedWeapon) {
            case BULLET: fireBulletAttack(); break;
            case MISSILE: fireMissileAttack(); break;
            case BOMB: fireBombAttack(); break;
        }
    }

    private ImageButton createCircularSkillButton(Texture iconTex, Color bgColor, int size) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = createFramedDrawable(bgColor, size);
        style.down = createFramedDrawable(bgColor.cpy().mul(0.7f), size);
        style.imageUp = new TextureRegionDrawable(new TextureRegion(iconTex));
        ImageButton button = new ImageButton(style);
        button.setSize(size, size);
        button.getImageCell().size(size * 0.7f).center();
        return button;
    }

    private void setPositionInArc(ImageButton button, float centerX, float centerY, float radius, float angleDegrees) {
        float angleRad = (float) Math.toRadians(angleDegrees);
        float x = centerX + radius * (float) Math.cos(angleRad) - button.getWidth() / 2f;
        float y = centerY + radius * (float) Math.sin(angleRad) - button.getHeight() / 2f;
        button.setPosition(x, y);
    }

    private TextureRegionDrawable createFramedDrawable(Color innerColor, int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        // 1. Draw inner background circle
        pixmap.setColor(innerColor);
        pixmap.fillCircle(size / 2, size / 2, (int)(size * 0.48f));

        // 2. Draw the button.png frame on top
        try {
            Pixmap frame = new Pixmap(Gdx.files.internal("button.png"));
            pixmap.drawPixmap(frame, 0, 0, frame.getWidth(), frame.getHeight(), 0, 0, size, size);
            frame.dispose();
        } catch (Exception e) {
            pixmap.setColor(Color.WHITE);
            pixmap.drawCircle(size / 2, size / 2, size / 2 - 1);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private TextureRegionDrawable createCircleDrawable(Color color, int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(size / 2, size / 2, size / 2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private TextureRegionDrawable createRectDrawable(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    // ==========================================
    // ATTACK MECHANISMS (Yêu cầu 3.2)
    // ==========================================
    private void fireBulletAttack() {
        if (bulletTimer >= bulletCooldown) {
            projectiles.add(new Projectile(player.getCenterX(), player.getTopY(), bullet1Tex, Projectile.Type.BULLET));
            if (game.isSoundEnabled() && shootSound != null) shootSound.play(0.8f);
            bulletTimer = 0f;
        }
    }

    private void fireMissileAttack() {
        projectiles.add(new Projectile(player.getCenterX() - 30, player.getTopY(), bullet2Tex, Projectile.Type.MISSILE, -15f));
        projectiles.add(new Projectile(player.getCenterX() + 30, player.getTopY(), bullet2Tex, Projectile.Type.MISSILE, 15f));
        if (game.isSoundEnabled() && shootSound != null) shootSound.play(1.0f, 0.6f, 0f);
    }

    private void fireBombAttack() {
        projectiles.add(new Projectile(player.getCenterX(), player.getTopY() + 10f, bombTex, Projectile.Type.BOMB));
        if (game.isSoundEnabled() && bombSound != null) bombSound.play(0.7f, 1.2f, 0f);
    }

    // ==========================================
    // DEFENSE MECHANISMS (Yêu cầu 3.3)
    // ==========================================
    private void useDefenseShield() {
        if (shieldCDTimer <= 0 && player.canUseShield() && player.hasEnergy(20)) {
            player.activateShield(5.0f);
            player.useEnergy(20);
            if (game.isSoundEnabled() && shootSound != null) shootSound.play(1.0f, 1.8f, 0f);
            shieldCDTimer = shieldMaxCooldown;
        }
    }

    private void useDefenseStunPulse() {
        if (stunCDTimer <= 0 && player.canUseStunPulse() && player.hasEnergy(35)) {
            player.triggerStunPulse(8.0f, 3.5f);
            player.useEnergy(35);
            for (Enemy enemy : enemies) {
                enemy.stun(3.5f);
            }
            if (game.isSoundEnabled() && bombSound != null) bombSound.play(0.6f, 1.5f, 0f);
            stunCDTimer = stunMaxCooldown;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        if (player.isDead()) {
            if (!playerExploded) {
                // Tạo hiệu ứng nổ lớn ngay tại vị trí Player khi chết từ spritesheet
                explosionEffects.add(new ExplosionEffect(player.getCenterX(), player.getCenterY(), 320f, 1.4f));
                if (game.isSoundEnabled() && bombSound != null) bombSound.play(1.0f);
                playerExploded = true;
                gameOverTimer = 1.4f; // Chờ 1.4 giây để hiệu ứng nổ 9 khung hình chạy hết
            }

            // Cập nhật các hiệu ứng nổ để hoạt ảnh chạy mượt mà
            Iterator<ExplosionEffect> expIter = explosionEffects.iterator();
            while (expIter.hasNext()) {
                ExplosionEffect exp = expIter.next();
                exp.update(delta);
                if (exp.isFinished()) expIter.remove();
            }

            gameOverTimer -= delta;
            if (gameOverTimer <= 0) {
                int finalScore = player.getCoins();
                dispose();
                game.setScreen(new GameOverScreen(game, finalScore));
                return; // Ngăn chặn hoàn toàn việc gọi draw() sau khi đã dispose!
            }

            // Trong thời gian chờ nổ, tiếp tục vẽ bối cảnh và hiệu ứng nổ (tàu đã ẩn)
            draw(screenWidth, screenHeight);
            stage.act(delta);
            stage.draw();
            return;
        }

        // Nếu game đang tạm dừng, chỉ vẽ và Act UI, không cập nhật logic game
        if (paused) {
            draw(screenWidth, screenHeight);
            stage.act(delta);
            stage.draw();
            return;
        }

        // Background update
        backgroundY -= backgroundSpeed * delta;
        float backgroundRenderHeight = background.getHeight() * screenWidth / background.getWidth();
        if (backgroundY <= -backgroundRenderHeight) {
            backgroundY += backgroundRenderHeight;
        }

        // Cooldown timers
        bulletTimer += delta;
        if (missileCDTimer > 0) missileCDTimer -= delta;
        if (bombCDTimer > 0) bombCDTimer -= delta;
        if (shieldCDTimer > 0) shieldCDTimer -= delta;
        if (stunCDTimer > 0) stunCDTimer -= delta;

        // Process Keyboard Hotkeys for Desktop Testing (Space, 1, 2, 3, 4, P)
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
            resumeButton.setVisible(paused);
            restartButton.setVisible(paused);
            homeButton.setVisible(paused);
            pauseButton.setVisible(!paused);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) fireBulletAttack();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) fireMissileAttack();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) fireBombAttack();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) useDefenseShield();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) useDefenseStunPulse();

        // Player update (Yêu cầu 3.1)
        player.update(delta, touchpad.getKnobPercentX(), touchpad.getKnobPercentY(), screenWidth, screenHeight);

        // Projectile update
        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();
            p.update(delta, screenHeight);
            if (!p.isActive()) {
                // If a bomb deactivated via timer expiry or enemy hit, spawn large explosion
                if (p.getType() == Projectile.Type.BOMB) {
                    explosionEffects.add(new ExplosionEffect(p.getCenterX(), p.getCenterY(), 220f, 0.6f));
                    if (game.isSoundEnabled() && bombSound != null) bombSound.play();
                }
                p.dispose();
                projIter.remove();
            }
        }

        // Enemy (Object X) Updates & Collisions
        for (Enemy enemy : enemies) {
            enemy.update(delta, 250f);

            // Collision Player A vs Object X (Enemy) -> Effect 1, 2, 3
            if (enemy.getBounds().overlaps(player.getBounds())) {
                player.takeDamage(20);
                explosionEffects.add(new ExplosionEffect(enemy.getX() + 40, enemy.getY() + 40, 100f, 0.4f));
                if (game.isSoundEnabled() && bombSound != null) bombSound.play(0.5f);
                enemy.resetToTop();
            }

            // Collision Projectiles vs Object X (Enemy) -> Effect 3, 4
            for (Projectile p : projectiles) {
                if (p.isActive() && p.getBounds().overlaps(enemy.getBounds())) {
                    if (p.getType() == Projectile.Type.BOMB) {
                        // Bomb explosion deals area damage
                        explosionEffects.add(new ExplosionEffect(p.getCenterX(), p.getCenterY(), 240f, 0.7f));
                        if (game.isSoundEnabled() && bombSound != null) bombSound.play();
                        p.setActive(false);
                    } else {
                        p.setActive(false);
                        explosionEffects.add(new ExplosionEffect(enemy.getX() + 40, enemy.getY() + 40, 80f, 0.3f));
                    }

                    enemy.resetToTop();
                    player.addCoins(15);
                    break;
                }
            }
        }

        // Obstacle (Object Y) Updates & Collisions
        for (Obstacle obstacle : obstacles) {
            obstacle.update(delta);

            // Collision Player A vs Object Y (Spike) -> Giảm khiên hoặc trừ 20 HP
            if (obstacle.getBounds().overlaps(player.getBounds())) {
                if (player.isShieldActive()) {
                    player.activateShield(0); // Bị phá khiên
                }
                player.takeDamage(20);
                explosionEffects.add(new ExplosionEffect(obstacle.getX() + 35, obstacle.getY() + 35, 100f, 0.35f));
                if (game.isSoundEnabled() && shootSound != null) shootSound.play(0.5f, 0.5f, 0f);
                obstacle.resetToTop();
            }
        }

        // PowerUp (Object Z) Updates & Collisions (Thu thập hồi HP, Energy, Coin)
        for (PowerUp powerUp : powerUps) {
            powerUp.update(delta, 150f);

            if (powerUp.getBounds().overlaps(player.getBounds())) {
                switch (powerUp.getType()) {
                    case HEALTH: // Hồi 20 HP từ item rơi xuống
                        player.addHealth(20);
                        break;
                    case ENERGY: // Hồi 25 Energy từ item rơi xuống
                        player.addEnergy(25);
                        break;
                    case COIN: // Thu thập Coin (Score)
                        player.addCoins(20);
                        break;
                    case SHIELD_BOOST: // Nhặt khiên bảo hộ tức thì
                        player.activateShield(4.0f);
                        break;
                }

                explosionEffects.add(new ExplosionEffect(powerUp.getX() + 30, powerUp.getY() + 30, 90f, 0.4f));
                if (game.isSoundEnabled() && shootSound != null) shootSound.play(1.0f, 1.5f, 0f);
                powerUp.resetToTop();
            }
        }

        // Explosion Effects Update
        Iterator<ExplosionEffect> expIter = explosionEffects.iterator();
        while (expIter.hasNext()) {
            ExplosionEffect exp = expIter.next();
            exp.update(delta);
            if (exp.isFinished()) expIter.remove();
        }

        // HUD Text Update (Chỉ giữ 3 thông số: HP, Energy, Coin/Score)
        hpLabel.setText(String.valueOf(player.getHealth()));
        energyLabel.setText(String.valueOf(player.getEnergy()));
        coinLabel.setText(String.valueOf(player.getCoins()));

        // Vẽ game
        draw(screenWidth, screenHeight);

        // Render UI Stage
        stage.act(delta);
        stage.draw();

        // Draw Cooldown Arcs (Overlay on top of stage buttons)
        drawCooldownArcs();
    }

    private void drawCooldownArcs() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(stage.getBatch().getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f); // Black transparent overlay

        // Only draw for defense skills as requested
        drawArc(selShield, shieldCDTimer, shieldMaxCooldown);
        drawArc(selStun, stunCDTimer, stunMaxCooldown);

        shapeRenderer.end();
    }

    private void drawArc(ImageButton btn, float timer, float max) {
        if (timer <= 0) return;
        float percent = timer / max;
        float centerX = btn.getX() + btn.getWidth() / 2f;
        float centerY = btn.getY() + btn.getHeight() / 2f;
        float radius = btn.getWidth() / 2f;
        shapeRenderer.arc(centerX, centerY, radius, 90, 360 * percent, 40);
    }

    private void draw(float screenWidth, float screenHeight) {
        batch.begin();

        // Background scrolling
        float backgroundRenderHeight = background.getHeight() * screenWidth / background.getWidth();
        batch.draw(background, 0, backgroundY, screenWidth, backgroundRenderHeight);
        batch.draw(background, 0, backgroundY + backgroundRenderHeight, screenWidth, backgroundRenderHeight);

        // Entities
        for (PowerUp powerUp : powerUps) powerUp.render(batch);
        for (Obstacle obstacle : obstacles) obstacle.render(batch);
        for (Enemy enemy : enemies) enemy.render(batch);
        for (Projectile p : projectiles) p.render(batch);
        player.render(batch);

        // Particle Explosions
        for (ExplosionEffect exp : explosionEffects) exp.render(batch);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        // Đảm bảo dừng nhạc khi màn hình này không còn hiển thị
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        batch.dispose();
        background.dispose();
        player.dispose();
        stage.dispose();
        if (fontHUD != null) fontHUD.dispose();

        for (Enemy enemy : enemies) enemy.dispose();
        for (Obstacle obstacle : obstacles) obstacle.dispose();
        for (PowerUp powerUp : powerUps) powerUp.dispose();
        for (Projectile p : projectiles) p.dispose();

        ExplosionEffect.disposeStatic();

        if (shootSound != null) shootSound.dispose();
        if (bombSound != null) bombSound.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();

        if (bullet1Tex != null) bullet1Tex.dispose();
        if (bullet2Tex != null) bullet2Tex.dispose();
        if (bombTex != null) bombTex.dispose();
        if (soundOnTex != null) soundOnTex.dispose();
        if (soundOffTex != null) soundOffTex.dispose();
        if (musicOnTex != null) musicOnTex.dispose();
        if (musicOffTex != null) musicOffTex.dispose();
        if (playPauseTex != null) playPauseTex.dispose();
        if (resumeTex != null) resumeTex.dispose();
        if (restartTex != null) restartTex.dispose();
        if (homeTex != null) homeTex.dispose();
        if (buttonTex != null) buttonTex.dispose();

        if (coinIconTex != null) coinIconTex.dispose();
        if (healthIconTex != null) healthIconTex.dispose();
        if (energyIconTex != null) energyIconTex.dispose();
        if (shieldIconTex != null) shieldIconTex.dispose();
    }
}
