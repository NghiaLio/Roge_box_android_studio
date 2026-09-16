package com.example.rogebox.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ExplosionEffect {

    private float x;
    private float y;
    private float size;
    private float maxSize;
    private float duration;
    private float timer;
    private boolean finished = false;

    private static Texture particleTexture;

    public ExplosionEffect(float x, float y, float maxSize, float duration) {
        this.x = x;
        this.y = y;
        this.size = 20f;
        this.maxSize = maxSize;
        this.duration = duration;
        this.timer = 0f;

        if (particleTexture == null) {
            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fillCircle(8, 8, 8);
            particleTexture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    public void update(float delta) {
        timer += delta;
        float progress = timer / duration;
        size = 20f + (maxSize - 20f) * progress;

        if (timer >= duration) {
            finished = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (finished) return;

        float progress = timer / duration;
        float alpha = 1.0f - progress;

        // Render orange-red expanding explosion ring
        batch.setColor(1.0f, 0.5f + 0.5f * (1 - progress), 0.1f, alpha);
        batch.draw(particleTexture, x - size / 2f, y - size / 2f, size, size);

        // Inner flash core
        float innerSize = size * 0.5f;
        batch.setColor(1.0f, 0.9f, 0.4f, alpha);
        batch.draw(particleTexture, x - innerSize / 2f, y - innerSize / 2f, innerSize, innerSize);

        batch.setColor(Color.WHITE);
    }

    public boolean isFinished() {
        return finished;
    }

    public static void disposeStatic() {
        if (particleTexture != null) {
            particleTexture.dispose();
            particleTexture = null;
        }
    }
}
