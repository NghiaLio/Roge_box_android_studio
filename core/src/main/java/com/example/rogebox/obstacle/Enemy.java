package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Object X: Enemy / Monster / Creep (NPC B)
 * Moves downward toward restricted area at bottom of screen.
 * Can be stunned by Player's Defense 2 skill (Stun Pulse).
 */
public class Enemy {

    private float x;
    private float y;

    private float width = 80f;
    private float height = 80f;

    private float screenWidth;
    private float screenHeight;

    private Texture texture;
    private boolean active;
    
    // Stun state (Defense 2)
    private float stunTimer = 0f;

    // Track if enemy has already entered restricted zone (to handle warning count cleanly)
    private boolean hasEnteredRestrictedZone = false;

    public Enemy(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.texture = new Texture("bird4.png");
        this.active = true;
        resetPosition();
    }

    public void update(float delta, float speed) {
        if (!active) return;

        // If stunned by Defense 2 skill, pause movement
        if (stunTimer > 0) {
            stunTimer -= delta;
            return;
        }

        // Move downward
        y -= speed * delta;

        // Reset if off bottom of screen
        if (y + height < 0) {
            resetToTop();
        }
    }

    public void resetToTop() {
        this.y = screenHeight + MathUtils.random(10, 200);
        this.x = MathUtils.random(0, screenWidth - width);
        this.active = true;
        this.stunTimer = 0f;
        this.hasEnteredRestrictedZone = false;
    }

    private void resetPosition() {
        resetToTop();
    }

    public void render(SpriteBatch batch) {
        if (active) {
            if (stunTimer > 0) {
                // Cyan flash tint when stunned
                batch.setColor(0.3f, 0.8f, 1.0f, 1.0f);
            }
            batch.draw(texture, x, y, width, height);
            if (stunTimer > 0) {
                batch.setColor(Color.WHITE);
            }
        }
    }

    public void stun(float duration) {
        this.stunTimer = duration;
    }

    public boolean isStunned() {
        return stunTimer > 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 10f, y + 10f, width - 20f, height - 20f);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            resetPosition();
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public boolean hasEnteredRestrictedZone() {
        return hasEnteredRestrictedZone;
    }

    public void setHasEnteredRestrictedZone(boolean entered) {
        this.hasEnteredRestrictedZone = entered;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
