package com.example.rogebox.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ExplosionEffect {

    private float x;
    private float y;
    private float size;
    private float duration;
    private float timer;
    private boolean finished = false;

    private static Texture explosionSheet;
    private static Animation<TextureRegion> explosionAnimation;

    public ExplosionEffect(float x, float y, float size, float duration) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.duration = Math.max(0.2f, duration);
        this.timer = 0f;

        initTexture();
    }

    private static synchronized void initTexture() {
        if (explosionSheet == null) {
            try {
                explosionSheet = new Texture("spritesheet.png");
                TextureRegion[][] tmp = TextureRegion.split(
                    explosionSheet,
                    explosionSheet.getWidth() / 3,
                    explosionSheet.getHeight() / 3
                );
                TextureRegion[] frames = new TextureRegion[9];
                int index = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        frames[index++] = tmp[i][j];
                    }
                }
                explosionAnimation = new Animation<>(0.1f, frames);
            } catch (Exception e) {
                Gdx.app.error("ExplosionEffect", "Failed to load spritesheet.png: " + e.getMessage());
            }
        }
    }

    public void update(float delta) {
        if (finished) return;

        timer += delta;
        if (timer >= duration) {
            finished = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (finished) return;
        if (explosionAnimation == null || explosionSheet == null) return;

        float frameDuration = duration / 9.0f;
        int frameIndex = (int) (timer / frameDuration);
        if (frameIndex < 0) frameIndex = 0;
        if (frameIndex >= 9) frameIndex = 8;

        TextureRegion currentFrame = explosionAnimation.getKeyFrames()[frameIndex];
        if (currentFrame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(currentFrame, x - size / 2f, y - size / 2f, size, size);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public static synchronized void disposeStatic() {
        if (explosionSheet != null) {
            explosionSheet.dispose();
            explosionSheet = null;
            explosionAnimation = null;
        }
    }
}
