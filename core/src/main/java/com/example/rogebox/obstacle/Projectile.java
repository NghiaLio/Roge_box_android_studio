package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Projectile {

    public enum Type {
        BULLET,   // Attack 1: Bắn đạn
        MISSILE,  // Attack 2: Bắn tên lửa
        BOMB      // Attack 3: Bắn bom về phía trước
    }

    private float x;
    private float y;

    private float width = 60f;
    private float height = 100f;

    private float speed = 1000f;
    private float vx = 0f;
    private float vy = 1000f;

    private Type type;
    private Texture texture;
    private boolean active;
    private boolean ownsTexture = false;
    private float bombFuseTimer = 0f;

    public Projectile(float startX, float startY, Texture texture, Type type) {
        this.type = type;
        this.texture = texture;
        this.active = true;

        if (type == Type.BULLET) {
            this.width = 50f;
            this.height = 80f;
            this.speed = 1100f;
            this.vx = 0f;
            this.vy = speed;
        } else if (type == Type.MISSILE) {
            this.width = 60f;
            this.height = 100f;
            this.speed = 900f;
            this.vx = 0f;
            this.vy = speed;
        } else if (type == Type.BOMB) {
            this.width = 75f;
            this.height = 75f;
            this.speed = 650f; // Bắn bom lao về phía trước với vận tốc 650f
            this.vx = 0f;
            this.vy = speed;
            this.bombFuseTimer = 2.0f; // Explodes automatically after 2 seconds if no target hit
        }

        this.x = startX - width / 2;
        this.y = startY;
    }

    public Projectile(float startX, float startY, Texture texture, Type type, float angleDegrees) {
        this(startX, startY, texture, type);
        if (type == Type.MISSILE) {
            double rad = Math.toRadians(angleDegrees);
            this.vx = (float) Math.sin(rad) * speed;
            this.vy = (float) Math.cos(rad) * speed;
        }
    }

    public Projectile(float startX, float startY, Texture texture) {
        this(startX, startY, texture, Type.BULLET);
    }

    public Projectile(float startX, float startY) {
        this.type = Type.BULLET;
        this.width = 50f;
        this.height = 80f;
        this.x = startX - width / 2;
        this.y = startY;
        this.texture = new Texture("bullet1.png");
        this.active = true;
        this.ownsTexture = true;
        this.vx = 0f;
        this.vy = 1000f;
    }

    public void update(float delta, float screenHeight) {
        if (!active) return;

        x += vx * delta;
        y += vy * delta;

        if (type == Type.BOMB) {
            bombFuseTimer -= delta;
            if (bombFuseTimer <= 0) {
                active = false; // Fuse expired -> trigger forward detonation explosion
            }
        }

        // Deactivate if out of screen bounds
        if (y > screenHeight + 100 || y < -100 || x < -100 || x > 3000) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active && texture != null) {
            batch.draw(texture, x, y, width, height);
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
    }

    public Type getType() {
        return type;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getCenterX() { return x + width / 2f; }
    public float getCenterY() { return y + height / 2f; }

    public void dispose() {
        if (ownsTexture && texture != null) {
            texture.dispose();
        }
    }
}
