package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Spike {

    private float x;
    private float y;
    private float width;
    private float height;

    private Texture texture;
    private boolean active;

    public Spike(float startX, float groundY) {
        this.x = startX;
        this.y = groundY;
        this.texture = new Texture("spike_03.png");
        
        // Use a reasonable size for the spike
        this.width = 120f;
        this.height = 120f;
        this.active = true;
    }

    public void update(float delta, float speed) {
        if (active) {
            x -= speed * delta;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        // Create a much tighter collision box (focusing on the center spike area)
        // x + 40 and width - 80 leaves only the middle 40 pixels (out of 120) for collision
        return new Rectangle(x + 40f, y, width - 80f, height - 40f);
    }

    public float getX() {
        return x;
    }

    public float getWidth() {
        return width;
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
