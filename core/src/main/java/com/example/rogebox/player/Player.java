package com.example.rogebox.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public class Player {

    private float x;
    private float y;

    private float speed = 600f;
    private static final float WIDTH = 150f;
    private static final float HEIGHT = 150f;

    private Texture texture;
    private Texture shieldTexture;
    private boolean dead = false;

    // Attributes for HUD
    private int health = 100;
    private int armor = 50;
    private int gold = 0;

    // Defense states
    private boolean shieldActive = false;
    private float shieldTimer = 0f;

    // Hit effect
    private float hitFlashTimer = 0f;
    private static final float HIT_FLASH_DURATION = 0.5f;

    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        // Dùng hình ảnh phi thuyền
        this.texture = new Texture("shipMain.png");
        this.shieldTexture = new Texture("shield_effect.png");
    }

    public void update(float delta, float joystickX, float joystickY, float screenWidth, float screenHeight) {
        if (dead) return;

        // Di chuyển theo 4 hướng dựa trên joystick (giá trị joystickX, joystickY từ -1 đến 1)
        x += joystickX * speed * delta;
        y += joystickY * speed * delta;

        // Giới hạn không cho bay ra ngoài màn hình
        if (x < 0) x = 0;
        if (x > screenWidth - WIDTH) x = screenWidth - WIDTH;
        if (y < 0) y = 0;
        if (y > screenHeight - HEIGHT) y = screenHeight - HEIGHT;

        // Update defense timers
        if (shieldActive) {
            shieldTimer -= delta;
            if (shieldTimer <= 0) {
                shieldActive = false;
            }
        }

        // Update hit flash timer
        if (hitFlashTimer > 0) {
            hitFlashTimer -= delta;
        }
    }

    public void render(SpriteBatch batch) {
        if (dead) return;

        // Nhấp nháy bằng cách thay đổi Alpha (độ trong suốt) dựa trên thời gian
        if (hitFlashTimer > 0) {
            // Tạo hiệu ứng nhấp nháy nhanh (sin wave)
            float alpha = (Math.abs((float) Math.sin(hitFlashTimer * 20f)) > 0.5f) ? 1.0f : 0.3f;
            batch.setColor(1, 1, 1, alpha);
        }

        batch.draw(texture, x, y, WIDTH, HEIGHT);

        // Reset color về mặc định để không ảnh hưởng các vật thể khác
        batch.setColor(Color.WHITE);

        if (shieldActive) {
            batch.draw(shieldTexture, x - 25f, y - 25f, WIDTH + 50f, HEIGHT + 50f);
        }
    }

    public void activateShield(float duration) {
        this.shieldActive = true;
        this.shieldTimer = duration;
    }

    public boolean isShieldActive() {
        return shieldActive;
    }

    public int getHealth() { return health; }
    public void addHealth(int amount) { health = Math.min(100, health + amount); }
    public void takeDamage(int amount) {
        if (shieldActive) return;

        // Kích hoạt hiệu ứng nháy khi nhận sát thương
        hitFlashTimer = HIT_FLASH_DURATION;

        if (armor > 0) {
            armor -= amount / 2;
            health -= amount / 2;
        } else {
            health -= amount;
        }
        if (health <= 0) {
            health = 0;
            die();
        }
    }

    public int getArmor() { return armor; }
    public void addArmor(int amount) { armor = Math.min(100, armor + amount); }

    public int getGold() { return gold; }
    public void addGold(int amount) { gold += amount; }

    public void setSpeed(float speed) { this.speed = speed; }
    public float getSpeed() { return speed; }

    public Rectangle getBounds() {
        // Vùng va chạm hẹp hơn một chút để công bằng
        return new Rectangle(x + 20f, y + 20f, WIDTH - 40f, HEIGHT - 40f);
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

    // Tiện ích để lấy tọa độ nòng súng (bắn đạn từ giữa phi thuyền)
    public float getCenterX() { return x + WIDTH / 2; }
    public float getTopY() { return y + HEIGHT; }

    public void dispose() {
        if (texture != null) texture.dispose();
        if (shieldTexture != null) shieldTexture.dispose();
    }
}
