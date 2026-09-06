package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Projectile {

    private float x;
    private float y;
    
    private float width = 30f;
    private float height = 80f;

    private float speed = 1000f; // Bay rất nhanh
    private Texture texture;
    private boolean active;

    public Projectile(float startX, float startY) {
        this.x = startX - width / 2; // Căn giữa đạn theo nòng súng
        this.y = startY;
        this.texture = new Texture("bullet1.png");
        this.active = true;
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
        if (texture != null) {
            texture.dispose();
        }
    }
}
