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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.rogebox.RogeBoxGame;
import com.example.rogebox.effect.ExplosionEffect;
import com.example.rogebox.obstacle.Enemy;
import com.example.rogebox.obstacle.PowerUp;
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
    private ArrayList<Enemy> enemies;       // Object X (NPC B / Creeps)
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

    // Audio
    private Sound shootSound;
    private Sound bombSound;
    private Sound warningSound;
    private Music backgroundMusic;

    // Restricted Zone Warning Beep Counter (Yêu cầu 1)
    private float restrictedAreaY;
    private int warningBeepSequence = 0;
    private float warningBeepTimer = 0f;

    // UI & Touch Controls
    private Stage stage;
    private Touchpad touchpad;
    private ImageButton soundToggle;
    private ImageButton musicToggle;

    // HUD Stats
    private Label hudLabel;
    private Label warningBannerLabel;
    private BitmapFont font;
    private int score = 0;

    // Attack Cooldowns
    private float bulletTimer = 1.0f;
    private float missileTimer = 1.0f;
    private float bombTimer = 1.0f;

    public GameScreen(RogeBoxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture("background3.png");

        // Textures
        bullet1Tex = new Texture("bullet1.png");
        bullet2Tex = new Texture("projectile_types_2.png");
        bombTex = new Texture("item_bomb.png");
        soundOnTex = new Texture("volume_on.png");
        soundOffTex = new Texture("voulme_off.png");
        musicOnTex = new Texture("music_on.png");
        musicOffTex = new Texture("music_off.png");

        // Audio
        try {
            shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.mp3"));
            bombSound = Gdx.audio.newSound(Gdx.files.internal("bomb.mp3"));
            warningSound = Gdx.audio.newSound(Gdx.files.internal("warning.mp3"));
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
        restrictedAreaY = screenHeight * 0.25f;

        // Player (Object A)
        player = new Player(screenWidth / 2f - 65f, 150f);

        // Object X: Enemies / Creeps (NPC B)
        enemies = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            enemies.add(new Enemy(screenWidth, screenHeight));
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

        // Touchpad Joystick (Yêu cầu 3.1)
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = createCircleDrawable(new Color(1f, 1f, 1f, 0.25f), 180);
        touchpadStyle.knob = createCircleDrawable(new Color(0.2f, 0.8f, 1f, 0.7f), 60);

        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(40, 40, 180, 180);
        stage.addActor(touchpad);

        // Fonts
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        hudLabel = new Label("", labelStyle);
        hudLabel.setPosition(30, Gdx.graphics.getHeight() - 140);
        stage.addActor(hudLabel);

        // Restricted Zone Warning Banner
        BitmapFont warnFont = new BitmapFont();
        warnFont.getData().setScale(2.2f);
        Label.LabelStyle warnStyle = new Label.LabelStyle(warnFont, Color.RED);
        warningBannerLabel = new Label("", warnStyle);
        warningBannerLabel.setPosition(Gdx.graphics.getWidth() / 2f - 200, Gdx.graphics.getHeight() - 60);
        warningBannerLabel.setVisible(false);
        stage.addActor(warningBannerLabel);

        // Sound Toggle Button (Yêu cầu 2: Position flexible, size 64x64)
        ImageButton.ImageButtonStyle soundStyle = new ImageButton.ImageButtonStyle();
        soundStyle.up = new TextureRegionDrawable(new TextureRegion(soundOnTex));
        soundStyle.checked = new TextureRegionDrawable(new TextureRegion(soundOffTex));
        soundToggle = new ImageButton(soundStyle);
        soundToggle.setChecked(!game.isSoundEnabled());
        soundToggle.setSize(64, 64);
        soundToggle.setPosition(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80);
        soundToggle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setSoundEnabled(!soundToggle.isChecked());
            }
        });
        stage.addActor(soundToggle);

        // Music Toggle Button (Yêu cầu 2: Position flexible, size 64x64)
        ImageButton.ImageButtonStyle musicStyle = new ImageButton.ImageButtonStyle();
        musicStyle.up = new TextureRegionDrawable(new TextureRegion(musicOnTex));
        musicStyle.checked = new TextureRegionDrawable(new TextureRegion(musicOffTex));
        musicToggle = new ImageButton(musicStyle);
        musicToggle.setChecked(!game.isMusicEnabled());
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

        // Skill Buttons for Attacks & Defenses (Yêu cầu 3.2 & 3.3 UI Buttons)
        float buttonX = Gdx.graphics.getWidth() - 220;
        float startY = 40;

        TextButton btnBullet = createSkillButton("BULLET", new Color(0.2f, 0.6f, 1f, 0.8f));
        btnBullet.setPosition(buttonX, startY);
        btnBullet.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { fireBulletAttack(); }
        });
        stage.addActor(btnBullet);

        TextButton btnMissile = createSkillButton("MISSILE", new Color(1f, 0.5f, 0.1f, 0.8f));
        btnMissile.setPosition(buttonX + 110, startY);
        btnMissile.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { fireMissileAttack(); }
        });
        stage.addActor(btnMissile);

        TextButton btnBomb = createSkillButton("BOMB", new Color(0.9f, 0.2f, 0.2f, 0.8f));
        btnBomb.setPosition(buttonX, startY + 60);
        btnBomb.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { fireBombAttack(); }
        });
        stage.addActor(btnBomb);

        TextButton btnShield = createSkillButton("SHIELD", new Color(0.2f, 0.9f, 0.4f, 0.8f));
        btnShield.setPosition(buttonX + 110, startY + 60);
        btnShield.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { useDefenseShield(); }
        });
        stage.addActor(btnShield);

        TextButton btnStun = createSkillButton("STUN", new Color(0.8f, 0.2f, 1.0f, 0.8f));
        btnStun.setPosition(buttonX + 55, startY + 120);
        btnStun.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { useDefenseStunPulse(); }
        });
        stage.addActor(btnStun);
    }

    private TextButton createSkillButton(String text, Color color) {
        BitmapFont btnFont = new BitmapFont();
        btnFont.getData().setScale(1.1f);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = createRectDrawable(color, 100, 50);
        style.down = createRectDrawable(color.cpy().mul(0.7f), 100, 50);
        style.font = btnFont;
        style.fontColor = Color.WHITE;
        TextButton button = new TextButton(text, style);
        button.setSize(100, 50);
        return button;
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
        if (bulletTimer >= 0.15f) {
            projectiles.add(new Projectile(player.getCenterX(), player.getTopY(), bullet1Tex, Projectile.Type.BULLET));
            if (game.isSoundEnabled() && shootSound != null) shootSound.play(0.8f);
            bulletTimer = 0f;
        }
    }

    private void fireMissileAttack() {
        if (missileTimer >= 0.4f) {
            projectiles.add(new Projectile(player.getCenterX() - 30, player.getTopY(), bullet2Tex, Projectile.Type.MISSILE, -15f));
            projectiles.add(new Projectile(player.getCenterX() + 30, player.getTopY(), bullet2Tex, Projectile.Type.MISSILE, 15f));
            if (game.isSoundEnabled() && shootSound != null) shootSound.play(1.0f, 0.6f, 0f);
            missileTimer = 0f;
        }
    }

    private void fireBombAttack() {
        if (bombTimer >= 0.5f) {
            // Launch bomb FORWARD from ship nose upwards straight towards enemies!
            projectiles.add(new Projectile(player.getCenterX(), player.getTopY() + 10f, bombTex, Projectile.Type.BOMB));
            if (game.isSoundEnabled() && bombSound != null) bombSound.play(0.7f, 1.2f, 0f);
            bombTimer = 0f;
        }
    }

    // ==========================================
    // DEFENSE MECHANISMS (Yêu cầu 3.3)
    // ==========================================
    private void useDefenseShield() {
        if (player.canUseShield()) {
            player.activateShield(5.0f);
            if (game.isSoundEnabled() && shootSound != null) shootSound.play(1.0f, 1.8f, 0f);
        }
    }

    private void useDefenseStunPulse() {
        if (player.canUseStunPulse()) {
            player.triggerStunPulse(8.0f, 3.5f);
            for (Enemy enemy : enemies) {
                enemy.stun(3.5f);
            }
            if (game.isSoundEnabled() && warningSound != null) warningSound.play(0.6f, 1.5f, 0f);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        if (player.isDead()) {
            game.setScreen(new GameOverScreen(game));
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
        missileTimer += delta;
        bombTimer += delta;

        // Process Keyboard Hotkeys for Desktop Testing (Space, 1, 2, 3, 4)
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

        // Objects X, Z & Restricted Zone Warning Updates
        boolean anyEnemyInRestrictedZone = false;

        // Enemy (Object X) Updates & Collisions
        for (Enemy enemy : enemies) {
            enemy.update(delta, 250f);

            // Restricted Zone Warning Check (Yêu cầu 1)
            if (enemy.getBounds().getY() <= restrictedAreaY) {
                anyEnemyInRestrictedZone = true;
                if (!enemy.hasEnteredRestrictedZone()) {
                    enemy.setHasEnteredRestrictedZone(true);
                    // Start warning sequence (3 to 6 count beeps)
                    warningBeepSequence = 5;
                    warningBeepTimer = 0.4f; // immediate trigger
                }
            }

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
                    player.addGold(15);
                    player.addDiamonds(1);
                    score += 15;
                    break;
                }
            }
        }

        // PowerUp (Object Z) Updates & Collisions (Yêu cầu 3.4)
        for (PowerUp powerUp : powerUps) {
            powerUp.update(delta, 150f);

            // Collision Player A vs Object Z (PowerUp Box) -> Effect 4, 5, 6
            if (powerUp.getBounds().overlaps(player.getBounds())) {
                switch (powerUp.getType()) {
                    case GOLD_DIAMOND: // Effect 4: Gold & Diamonds
                        player.addGold(50);
                        player.addDiamonds(5);
                        score += 50;
                        break;
                    case SPEED_BOOST:  // Effect 5: Speed Boost (+50%)
                        player.applySpeedBoost(3.5f);
                        break;
                    case HEALTH_ARMOR: // Effect 6: Health & Armor restore
                        player.addHealth(25);
                        player.addArmor(25);
                        break;
                    case WEAPON_AMMO:  // Effect 6: Instant Shield & Weapon ammo boost
                        player.activateShield(3.0f);
                        score += 30;
                        break;
                }

                explosionEffects.add(new ExplosionEffect(powerUp.getX() + 30, powerUp.getY() + 30, 90f, 0.4f));
                if (game.isSoundEnabled() && shootSound != null) shootSound.play(1.0f, 1.5f, 0f);
                powerUp.resetToTop();
            }
        }

        // Warning Beep Sound Logic (Phát cảnh báo liên tục 3 đến 6 lần - Yêu cầu 1)
        if (warningBeepSequence > 0) {
            warningBeepTimer += delta;
            if (warningBeepTimer >= 0.4f) {
                if (game.isSoundEnabled() && warningSound != null) {
                    warningSound.play(0.8f);
                }
                warningBeepSequence--;
                warningBeepTimer = 0f;
            }
        }

        warningBannerLabel.setVisible(anyEnemyInRestrictedZone || warningBeepSequence > 0);
        if (anyEnemyInRestrictedZone || warningBeepSequence > 0) {
            warningBannerLabel.setText("WARNING! NPC IN RESTRICTED ZONE!");
        }

        // Explosion Effects Update
        Iterator<ExplosionEffect> expIter = explosionEffects.iterator();
        while (expIter.hasNext()) {
            ExplosionEffect exp = expIter.next();
            exp.update(delta);
            if (exp.isFinished()) expIter.remove();
        }

        // HUD Text Update
        String shieldStatus = "READY";
        if (player.isShieldActive()) {
            shieldStatus = "ACTIVE";
        } else if (player.getShieldCooldownTimer() > 0) {
            shieldStatus = String.format("COOLDOWN (%.1fs)", player.getShieldCooldownTimer());
        }

        hudLabel.setText(String.format("SCORE: %d | GOLD: %d | DIAMONDS: %d\nHP: %d | ARMOR: %d | SHIELD: %s",
                score, player.getGold(), player.getDiamonds(), player.getHealth(), player.getArmor(),
                shieldStatus));

        // ==========================================
        // DRAWING / RENDERING
        // ==========================================
        batch.begin();

        // Background scrolling
        batch.draw(background, 0, backgroundY, screenWidth, backgroundRenderHeight);
        batch.draw(background, 0, backgroundY + backgroundRenderHeight, screenWidth, backgroundRenderHeight);

        // Entities
        for (PowerUp powerUp : powerUps) powerUp.render(batch);
        for (Enemy enemy : enemies) enemy.render(batch);
        for (Projectile p : projectiles) p.render(batch);
        player.render(batch);

        // Particle Explosions
        for (ExplosionEffect exp : explosionEffects) exp.render(batch);

        batch.end();

        // Render UI Stage
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
        for (PowerUp powerUp : powerUps) powerUp.dispose();
        for (Projectile p : projectiles) p.dispose();

        ExplosionEffect.disposeStatic();

        if (shootSound != null) shootSound.dispose();
        if (bombSound != null) bombSound.dispose();
        if (warningSound != null) warningSound.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();

        if (bullet1Tex != null) bullet1Tex.dispose();
        if (bullet2Tex != null) bullet2Tex.dispose();
        if (bombTex != null) bombTex.dispose();
        if (soundOnTex != null) soundOnTex.dispose();
        if (soundOffTex != null) soundOffTex.dispose();
        if (musicOnTex != null) musicOnTex.dispose();
        if (musicOffTex != null) musicOffTex.dispose();
    }
}
