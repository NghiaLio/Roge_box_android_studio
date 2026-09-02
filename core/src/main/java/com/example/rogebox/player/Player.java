package com.example.rogebox.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {

    // =========================
    // FRAME
    // =========================

    private static final int FRAME_WIDTH = 256;
    private static final int FRAME_HEIGHT = 256;


    // =========================
    // PLAYER SIZE
    // =========================

    private static final float WIDTH = 300f;
    private static final float HEIGHT = 300f;


    // =========================
    // MOVEMENT
    // =========================

    private float x;
    private float y;

    private float speed = 300f;


    // =========================
    // PHYSICS
    // =========================

    private float velocityY = 0f;

    private float gravity = -1800f;

    private float jumpForce = 750f;

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
        // Hạ toàn bộ hình ảnh nhân vật (cả chạy và nhảy) xuống 20 pixel để sát đất hơn
        float drawY = y - 100f;
        float drawWidth = WIDTH;
        float drawHeight = HEIGHT;

        if (jumping) {
            // Giảm kích thước jump xuống thêm một chút
            drawWidth = 210f;
            drawHeight = 210f;

            // Căn giữa nhân vật theo chiều ngang (300 - 210) / 2 = 45
            drawX = x + 45f;
            
            // Bù lại phần hụt ở dưới chân do ảnh bị thu nhỏ lại (nâng nhân vật lên)
            drawY += 50f;
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
