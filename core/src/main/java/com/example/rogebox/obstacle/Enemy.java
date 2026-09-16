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

    // Vận tốc (không còn dùng vx, vy cho di chuyển ngẫu nhiên)
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

        // Chỉ di chuyển từ trên xuống dưới
        y -= speed * delta;

        // Nếu đi quá đáy màn hình (bay khỏi màn hình), quay lại đỉnh ở một vị trí X ngẫu nhiên
        if (y + height < 0) {
            resetToTop();
        }
    }

    private void resetToTop() {
        this.y = screenHeight;
        this.x = MathUtils.random(0, screenWidth - width);
        this.active = true;
    }

    private void resetPosition() {
        // Luôn xuất hiện ở đỉnh màn hình khi khởi tạo hoặc reset
        resetToTop();
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
