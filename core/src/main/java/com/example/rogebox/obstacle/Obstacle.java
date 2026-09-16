package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Object Y: Obstacle / Hazard
 * Uses spike_03.png
 */
public class Obstacle {

    private float x;
    private float y;
    private float width = 70f;
    private float height = 70f;
    private float speed = 200f;

    private float screenWidth;
    private float screenHeight;

    private Texture texture;
    private boolean active;

    public Obstacle(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.texture = new Texture("spike_03.png");
        this.active = true;
        resetToTop();
    }

    public void update(float delta) {
        if (!active) return;

        y -= speed * delta;

        if (y + height < 0) {
            resetToTop();
        }
    }

    public void resetToTop() {
        this.y = screenHeight + MathUtils.random(200, 1000);
        this.x = MathUtils.random(50, screenWidth - width - 50);
        this.active = true;
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 10, y + 10, width - 20, height - 20);
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
