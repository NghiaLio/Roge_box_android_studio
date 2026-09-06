package com.example.rogebox.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {

    private float x;
    private float y;
    
    // Kích thước nhỏ hơn Player
    private float width = 80f;
    private float height = 80f;

    // Vận tốc ngẫu nhiên theo 2 trục
    private float vx;
    private float vy;

    private float screenWidth;
    private float screenHeight;

    private Texture texture;
    private boolean active;

    public Enemy(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.texture = new Texture("bird4.png");
        this.active = true;
        resetPosition();
    }

    public void update(float delta, float speed) {
        if (!active) return;

        // Di chuyển linh hoạt theo 4 hướng
        x += vx * speed * delta;
        y += vy * speed * delta;

        // Xử lý chạm biên (wrap-around)
        // Chạm biên trái -> Xuất hiện biên phải
        if (x + width < 0) {
            x = screenWidth;
            y = MathUtils.random(0, screenHeight - height);
            randomizeVelocity();
        }
        // Chạm biên phải -> Xuất hiện biên trái
        else if (x > screenWidth) {
            x = -width;
            y = MathUtils.random(0, screenHeight - height);
            randomizeVelocity();
        }

        // Chạm đáy -> Xuất hiện đỉnh
        if (y + height < 0) {
            y = screenHeight;
            x = MathUtils.random(0, screenWidth - width);
            randomizeVelocity();
        }
        // Chạm đỉnh (biên trên) -> Xuất hiện đáy (biên dưới)
        else if (y > screenHeight) {
            y = -height;
            x = MathUtils.random(0, screenWidth - width);
            randomizeVelocity();
        }
    }

    private void randomizeVelocity() {
        // Sinh hướng ngẫu nhiên (tránh việc đứng yên)
        do {
            vx = MathUtils.random(-1f, 1f);
            vy = MathUtils.random(-1f, 1f);
        } while (Math.abs(vx) < 0.2f && Math.abs(vy) < 0.2f);
        
        // Chuẩn hóa vector để tốc độ luôn đều
        float length = (float) Math.sqrt(vx * vx + vy * vy);
        vx /= length;
        vy /= length;
    }

    private void resetPosition() {
        // Xuất hiện ngẫu nhiên ở một trong 4 biên
        int edge = MathUtils.random(0, 3);
        switch(edge) {
            case 0: // Top
                this.y = screenHeight;
                this.x = MathUtils.random(0, screenWidth - width);
                break;
            case 1: // Bottom
                this.y = -height;
                this.x = MathUtils.random(0, screenWidth - width);
                break;
            case 2: // Left
                this.x = -width;
                this.y = MathUtils.random(0, screenHeight - height);
                break;
            case 3: // Right
                this.x = screenWidth;
                this.y = MathUtils.random(0, screenHeight - height);
                break;
        }
        randomizeVelocity();
        this.active = true;
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y, width, height);
        }
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

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
