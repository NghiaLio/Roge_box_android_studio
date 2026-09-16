package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Object Z: PowerUp / Reward Chest / Item Box
 * Uses dedicated PNG textures: item_gold.png, item_health.png, item_speed.png, item_weapon.png
 */
public class PowerUp {

    public enum Type {
        GOLD_DIAMOND,
        SPEED_BOOST,
        HEALTH_ARMOR,
        WEAPON_AMMO
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
    private Texture speedTex;
    private Texture weaponTex;

    private boolean active;

    public PowerUp(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.goldTex = new Texture("item_gold.png");
        this.healthTex = new Texture("item_health.png");
        this.speedTex = new Texture("item_speed.png");
        this.weaponTex = new Texture("item_weapon.png");

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
            case GOLD_DIAMOND: currentTex = goldTex; break;
            case SPEED_BOOST:  currentTex = speedTex; break;
            case HEALTH_ARMOR: currentTex = healthTex; break;
            case WEAPON_AMMO:  currentTex = weaponTex; break;
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
        if (speedTex != null) speedTex.dispose();
        if (weaponTex != null) weaponTex.dispose();
    }
}
