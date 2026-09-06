package com.example.rogebox.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    // =========================
    // FRAME
    // =========================

    private static final int FRAME_WIDTH = 256;
    private static final int FRAME_HEIGHT = 256;


    // =========================
    // PLAYER SIZE
    // =========================

    private static final float WIDTH = 450f;
    private static final float HEIGHT = 450f;


    // =========================
    // MOVEMENT
    // =========================

    private float x;
    private float y;

    private float speed = 500f;


    // =========================
    // PHYSICS
    // =========================

    private float velocityY = 0f;

    private float gravity = -1800f;

    private float jumpForce = 1050f;

    private boolean onGround = true;


    // =========================
    // ANIMATION
    // =========================

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> dieAnimation;

    private float stateTime;


    // =========================
    // STATE
    // =========================

    private boolean jumping = false;
    private boolean dead = false;


    // =========================
    // TEXTURES
    // =========================

    private Texture idleSheet;
    private Texture runSheet;
    private Texture jumpSheet;
    private Texture dieSheet;


    public Player(float x, float y) {

        this.x = x;
        this.y = y;

        loadAnimations();
    }


    // =========================================================
    // LOAD ANIMATIONS
    // =========================================================

    private void loadAnimations() {

        // IDLE
        idleSheet = new Texture("idle.png");

        idleAnimation = createAnimation(
            idleSheet,
            5,
            3,
            15,
            0.12f
        );


        // RUN
        runSheet = new Texture("run.png");

        runAnimation = createAnimation(
            runSheet,
            5,
            5,
            23,
            0.10f
        );


        // JUMP
        jumpSheet = new Texture("jump.png");

        jumpAnimation = createAnimation(
            jumpSheet,
            4,
            4,
            16,
            0.10f
        );


        // DIE
        dieSheet = new Texture("die.png");

        dieAnimation = createAnimation(
            dieSheet,
            4,
            4,
            16,
            0.10f
        );


        idleAnimation.setPlayMode(
            Animation.PlayMode.LOOP
        );

        runAnimation.setPlayMode(
            Animation.PlayMode.LOOP
        );

        jumpAnimation.setPlayMode(
            Animation.PlayMode.LOOP
        );

        dieAnimation.setPlayMode(
            Animation.PlayMode.NORMAL
        );
    }


    // =========================================================
    // CREATE ANIMATION
    // =========================================================

    private Animation<TextureRegion> createAnimation(
        Texture texture,
        int columns,
        int rows,
        int frameCount,
        float frameDuration
    ) {

        TextureRegion[][] tmp =
            TextureRegion.split(
                texture,
                FRAME_WIDTH,
                FRAME_HEIGHT
            );

        TextureRegion[] frames =
            new TextureRegion[frameCount];

        int index = 0;

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < columns; col++) {

                if (index >= frameCount) {
                    break;
                }

                frames[index] = tmp[row][col];

                index++;
            }
        }

        return new Animation<>(
            frameDuration,
            frames
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public void update(float delta, float groundY) {

        stateTime += delta;


        if (dead) {
            return;
        }


        // =====================================================
        // GRAVITY
        // =====================================================

        if (!onGround) {

            velocityY += gravity * delta;

            y += velocityY * delta;
        }


        // =====================================================
        // COLLISION WITH GROUND
        // =====================================================

        if (y <= groundY) {

            y = groundY;

            velocityY = 0f;

            onGround = true;

            jumping = false;
        }


        // =====================================================
        // JUMP
        // =====================================================

        if (Gdx.input.justTouched() && onGround) {

            jump();
        }
    }


    // =========================================================
    // JUMP
    // =========================================================

    private void jump() {

        velocityY = jumpForce;

        onGround = false;

        jumping = true;

        stateTime = 0f;
    }


    // =========================================================
    // CURRENT FRAME
    // =========================================================

    private TextureRegion getCurrentFrame() {

        if (dead) {

            return dieAnimation.getKeyFrame(
                stateTime
            );
        }


        if (jumping) {

            return jumpAnimation.getKeyFrame(
                stateTime
            );
        }


        return runAnimation.getKeyFrame(
            stateTime
        );
    }


    // =========================================================
    // RENDER
    // =========================================================

    public void render(SpriteBatch batch) {

        TextureRegion currentFrame =
            getCurrentFrame();

        float drawX = x;
        // Adjust for the 1.5x larger size
        float drawY = y - 150f;
        float drawWidth = WIDTH;
        float drawHeight = HEIGHT;

        if (dead) {
            // Die sprite is too large visually, scale it down slightly
            drawWidth = 250f;
            drawHeight = 250f;
            
            // Center horizontally (450 - 250) / 2 = 100
            drawX = x + 100f;
            
            // Offset vertically to compensate for smaller size
            drawY += 100f;
        } else if (jumping) {
            // Jump size scaled up by 1.5x (from 210 to 315)
            drawWidth = 315f;
            drawHeight = 315f;

            // Center horizontally (450 - 315) / 2 = 67.5
            drawX = x + 67.5f;
            
            // Offset vertically for jump scaled up by 1.5x (from 50 to 75)
            drawY += 75f;
        }

        batch.draw(
            currentFrame,
            drawX,
            drawY,
            drawWidth,
            drawHeight
        );
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return WIDTH;
    }

    public float getHeight() {
        return HEIGHT;
    }

    public void die() {
        if (!dead) {
            dead = true;
            stateTime = 0f;
        }
    }
    
    public boolean isDead() {
        return dead;
    }
    
    public boolean isDeathAnimationFinished() {
        return dead && dieAnimation.isAnimationFinished(stateTime);
    }

    public Rectangle getBounds() {
        float drawX = x;
        float drawY = y - 150f;
        
        if (jumping) {
            float drawWidth = 315f;
            float drawHeight = 315f;
            drawX = x + 67.5f;
            drawY += 75f;
            
            // Tight bounds for jumping character (narrower width)
            return new Rectangle(drawX + 110f, drawY + 20f, drawWidth - 220f, drawHeight - 80f);
        }
        
        // Tight bounds for running character (cutting out lots of transparent space on the sides)
        return new Rectangle(drawX + 160f, drawY, WIDTH - 320f, HEIGHT - 150f);
    }

    // =========================================================
    // DISPOSE
    // =========================================================

    public void dispose() {

        idleSheet.dispose();

        runSheet.dispose();

        jumpSheet.dispose();

        dieSheet.dispose();
    }
}
