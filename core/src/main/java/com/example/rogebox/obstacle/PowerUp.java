package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Object Z: PowerUp / Drop Items
 * Uses dedicated PNG textures:
 * - item_health.png: Hồi HP
 * - item_speed.png: Hồi Energy
 * - item_gold.png: Nhặt Coin (Score)
 * - item_weapon.png: Kích hoạt khiên bảo vệ
 */
public class PowerUp {

    public enum Type {
        HEALTH,        // Hồi 20 HP
        ENERGY,        // Hồi 25 Energy
        COIN,          // Nhận 20 Coin/Score
        SHIELD_BOOST   // Kích hoạt khiên hộ mệnh
    }

    private float x;
    private float y;
    private float width = 64f;
    private float height = 64f;
    private float speed = 160f;

    private float screenWidth;
    private float screenHeight;

    private Type type;
    private Texture goldTex;
    private Texture healthTex;
    private Texture energyTex;
    private Texture shieldTex;

    private boolean active;

    public PowerUp(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.goldTex = new Texture("item_gold.png");
        this.healthTex = new Texture("item_health.png");
        this.energyTex = new Texture("item_speed.png"); // Biểu tượng tia năng lượng
        this.shieldTex = new Texture("item_weapon.png"); // Khiên phòng vệ

        this.active = true;
        resetToTop();
    }

    public void update(float delta, float currentSpeed) {
        if (!active) return;

        y -= (speed + currentSpeed * 0.3f) * delta;

        if (y + height < 0) {
            resetToTop();
        }
    }

    public void resetToTop() {
        this.y = screenHeight + MathUtils.random(100, 600);
        this.x = MathUtils.random(20, screenWidth - width - 20);
        this.type = Type.values()[MathUtils.random(0, Type.values().length - 1)];
        this.active = true;
    }

    public void render(SpriteBatch batch) {
        if (!active) return;

        Texture currentTex = goldTex;
        switch (type) {
            case HEALTH:
                currentTex = healthTex;
                break;
            case ENERGY:
                currentTex = energyTex;
                break;
            case COIN:
                currentTex = goldTex;
                break;
            case SHIELD_BOOST:
                currentTex = shieldTex;
                break;
        }

        if (currentTex != null) {
            batch.draw(currentTex, x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) resetToTop();
    }

    public Type getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void dispose() {
        if (goldTex != null) goldTex.dispose();
        if (healthTex != null) healthTex.dispose();
        if (energyTex != null) energyTex.dispose();
        if (shieldTex != null) shieldTex.dispose();
    }
}
