package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Projectile {

    private float x;
    private float y;

    private float width = 80f;
    private float height = 140f;

    private float speed = 1000f; // Bay rất nhanh
    private Texture texture;
    private boolean active;
    private boolean ownsTexture = false;

    public Projectile(float startX, float startY, Texture texture) {
        this.x = startX - width / 2;
        this.y = startY;
        this.texture = texture;
        this.active = true;
    }

    public Projectile(float startX, float startY) {
        this.x = startX - width / 2;
        this.y = startY;
        this.texture = new Texture("bullet1.png");
        this.active = true;
        this.ownsTexture = true;
    }

    public void update(float delta, float screenHeight) {
        if (!active) return;

        // Bay thẳng lên trên
        y += speed * delta;

        // Tắt đạn nếu bay ra khỏi màn hình
        if (y > screenHeight) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
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

    public void dispose() {
        if (ownsTexture && texture != null) {
            texture.dispose();
        }
    }
}
