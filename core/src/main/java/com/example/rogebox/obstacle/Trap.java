package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Object Y: Trap / Spike / Obstacle (Chông bẫy / Chướng ngại vật)
 * On collision with Player: Deals damage & slows down Player movement speed (Effect 1 & 5).
 */
public class Trap {

    private float x;
    private float y;
    private float width = 70f;
    private float height = 70f;
    private float speed = 200f;

    private float screenWidth;
    private float screenHeight;

    private Texture texture;
    private boolean active;

    public Trap(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.texture = new Texture("spike_03.png");
        this.active = true;
        resetToTop();
    }

    public void update(float delta, float currentSpeed) {
        if (!active) return;

        y -= currentSpeed * delta;

        if (y + height < 0) {
            resetToTop();
        }
    }

    public void resetToTop() {
        this.y = screenHeight + MathUtils.random(50, 400);
        this.x = MathUtils.random(0, screenWidth - width);
        this.active = true;
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 5f, y + 5f, width - 10f, height - 10f);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) resetToTop();
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
