package com.example.rogebox.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    private float x;
    private float y;

    private float baseSpeed = 550f;
    private float currentSpeed = 550f;
    private static final float WIDTH = 130f;
    private static final float HEIGHT = 130f;

    private Texture texture;
    private Texture shieldTexture;
    private boolean dead = false;

    // Attributes & HUD stats (3 core stats: Health, Energy, Coins/Score)
    private int health = 100;
    private int energy = 100; // Năng lượng khởi đầu
    private int coins = 0;    // Coin (Score)

    // Defense 1: Energy Shield & Cooldown
    private boolean shieldActive = false;
    private float shieldTimer = 0f;
    private float shieldCooldownTimer = 0f;
    private static final float SHIELD_COOLDOWN_DURATION = 10.0f; // 10 giây hồi khiên

    // Defense 2: Stun Pulse Skill Cooldown & Active state
    private float stunPulseTimer = 0f;
    private float stunPulseCooldown = 0f;

    // Buff / Debuff Timers (Collision Effects 5)
    private float speedBoostTimer = 0f;
    private float slowDownTimer = 0f;

    // Hit effect flash timer
    private float hitFlashTimer = 0f;
    private static final float HIT_FLASH_DURATION = 0.4f;

    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.texture = new Texture("shipMain.png");
        this.shieldTexture = new Texture("shield_effect.png");
    }

    public void update(float delta, float joystickX, float joystickY, float screenWidth, float screenHeight) {
        if (dead) return;

        // Process Keyboard inputs (WASD / Arrow Keys) alongside Joystick
        float moveX = joystickX;
        float moveY = joystickY;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveX -= 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) moveY += 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveY -= 1.0f;

        // Normalize direction vector if length > 1
        float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
        if (length > 1.0f) {
            moveX /= length;
            moveY /= length;
        }

        // Calculate dynamic movement speed based on Speed Boost & Slow Down effects
        currentSpeed = baseSpeed;
        if (speedBoostTimer > 0) {
            speedBoostTimer -= delta;
            currentSpeed *= 1.5f; // +50% speed boost from Object Z
        }
        if (slowDownTimer > 0) {
            slowDownTimer -= delta;
            currentSpeed *= 0.5f; // -50% slow down from Object Y
        }

        x += moveX * currentSpeed * delta;
        y += moveY * currentSpeed * delta;

        // Screen boundary clamp
        if (x < 0) x = 0;
        if (x > screenWidth - WIDTH) x = screenWidth - WIDTH;
        if (y < 0) y = 0;
        if (y > screenHeight - HEIGHT) y = screenHeight - HEIGHT;

        // Update defense 1 active timer and cooldown
        if (shieldActive) {
            shieldTimer -= delta;
            if (shieldTimer <= 0) {
                shieldActive = false;
                shieldCooldownTimer = SHIELD_COOLDOWN_DURATION; // Kích hoạt hồi chiêu khi hết khiên
            }
        } else if (shieldCooldownTimer > 0) {
            shieldCooldownTimer -= delta;
        }

        // Update defense 2 cooldown
        if (stunPulseCooldown > 0) {
            stunPulseCooldown -= delta;
        }

        // Update hit flash timer
        if (hitFlashTimer > 0) {
            hitFlashTimer -= delta;
        }
    }

    public void render(SpriteBatch batch) {
        if (dead) return;

        // Flash red/transparent on damage hit
        if (hitFlashTimer > 0) {
            float alpha = (Math.abs((float) Math.sin(hitFlashTimer * 25f)) > 0.5f) ? 1.0f : 0.3f;
            batch.setColor(1.0f, 0.4f, 0.4f, alpha);
        } else if (speedBoostTimer > 0) {
            batch.setColor(0.5f, 1.0f, 0.5f, 1.0f); // Green tint on speed boost
        } else if (slowDownTimer > 0) {
            batch.setColor(1.0f, 0.5f, 0.2f, 1.0f); // Orange tint on slow down
        }

        batch.draw(texture, x, y, WIDTH, HEIGHT);
        batch.setColor(Color.WHITE);

        // Render Shield Barrier aura if Defense 1 is active
        if (shieldActive) {
            // Tăng vùng giáp bao quanh (từ +50 lên +80)
            batch.draw(shieldTexture, x - 40f, y - 40f, WIDTH + 80f, HEIGHT + 80f);
        }
    }

    // Defense 1: Energy Shield
    public boolean canUseShield() {
        return !shieldActive && shieldCooldownTimer <= 0;
    }

    public void activateShield(float duration) {
        if (canUseShield()) {
            this.shieldActive = true;
            this.shieldTimer = duration;
        }
    }

    public float getShieldCooldownTimer() {
        return shieldCooldownTimer;
    }

    // Defense 2: Freeze Stun Pulse
    public boolean canUseStunPulse() {
        return stunPulseCooldown <= 0;
    }

    public void triggerStunPulse(float cooldown, float duration) {
        this.stunPulseCooldown = cooldown;
        this.stunPulseTimer = duration;
    }

    // Speed Boost & Slow Down Modifiers (Collision Effects 5)
    public void applySpeedBoost(float duration) {
        this.speedBoostTimer = duration;
        this.slowDownTimer = 0; // Clear slow down
    }

    public void applySlowDown(float duration) {
        this.slowDownTimer = duration;
        this.speedBoostTimer = 0; // Clear speed boost
    }

    public boolean isShieldActive() {
        return shieldActive;
    }

    public int getHealth() { return health; }
    public void addHealth(int amount) { health = Math.min(100, health + amount); }

    public void takeDamage(int amount) {
        if (shieldActive) return; // Shield absorbs all damage

        hitFlashTimer = HIT_FLASH_DURATION;
        health -= amount;

        if (health <= 0) {
            health = 0;
            die();
        }
    }

    public int getCoins() { return coins; }
    public void addCoins(int amount) { coins += amount; }

    // Backward compatibility for gold
    public int getGold() { return coins; }
    public void addGold(int amount) { coins += amount; }

    public int getEnergy() { return energy; }
    public void addEnergy(int amount) { energy = Math.min(100, energy + amount); }
    public void useEnergy(int amount) { energy = Math.max(0, energy - amount); }
    public boolean hasEnergy(int amount) { return energy >= amount; }

    public Rectangle getBounds() {
        return new Rectangle(x + 15f, y + 15f, WIDTH - 30f, HEIGHT - 30f);
    }

    public void die() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return WIDTH; }
    public float getHeight() { return HEIGHT; }
    public float getCenterX() { return x + WIDTH / 2f; }
    public float getCenterY() { return y + HEIGHT / 2f; }
    public float getTopY() { return y + HEIGHT; }

    public void dispose() {
        if (texture != null) texture.dispose();
        if (shieldTexture != null) shieldTexture.dispose();
    }
}
