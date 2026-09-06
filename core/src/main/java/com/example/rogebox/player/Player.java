package com.example.rogebox.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    private float x;
    private float y;

    private float speed = 600f;
    private static final float WIDTH = 150f;
    private static final float HEIGHT = 150f;

    private Texture texture;
    private boolean dead = false;

    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        // Dùng hình ảnh phi thuyền
        this.texture = new Texture("shipMain.png");
    }

    public void update(float delta, float joystickX, float joystickY, float screenWidth, float screenHeight) {
        if (dead) return;

        // Di chuyển theo 4 hướng dựa trên joystick (giá trị joystickX, joystickY từ -1 đến 1)
        x += joystickX * speed * delta;
        y += joystickY * speed * delta;

        // Giới hạn không cho bay ra ngoài màn hình
        if (x < 0) x = 0;
        if (x > screenWidth - WIDTH) x = screenWidth - WIDTH;
        if (y < 0) y = 0;
        if (y > screenHeight - HEIGHT) y = screenHeight - HEIGHT;
    }

    public void render(SpriteBatch batch) {
        if (dead) return; 
        batch.draw(texture, x, y, WIDTH, HEIGHT);
    }

    public Rectangle getBounds() {
        // Vùng va chạm hẹp hơn một chút để công bằng
        return new Rectangle(x + 20f, y + 20f, WIDTH - 40f, HEIGHT - 40f);
    }

    public void die() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return WIDTH; }
    public float getHeight() { return HEIGHT; }
    
    // Tiện ích để lấy tọa độ nòng súng (bắn đạn từ giữa phi thuyền)
    public float getCenterX() { return x + WIDTH / 2; }
    public float getTopY() { return y + HEIGHT; }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
