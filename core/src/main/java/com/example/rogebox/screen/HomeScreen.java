package com.example.rogebox.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.rogebox.RogeBoxGame;

public class HomeScreen implements Screen {

    private final RogeBoxGame game;

    private Stage stage;
    private Skin skin;
    private Texture background;

    public HomeScreen(RogeBoxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load ảnh nền
        background = new Texture("home.png");
        Image bgImage = new Image(background);
        bgImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(bgImage);

        // Tạo Skin programmatically cho các nút bấm (không cần file json)
        createSkin();

        // Sử dụng Table để sắp xếp bố cục UI tự động căn giữa
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Tiêu đề game
        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("title"), Color.WHITE);
        Label titleLabel = new Label("ROGE_BOX", labelStyle);

        // Các nút bấm
        TextButton playButton = new TextButton("PLAY", skin);
        TextButton exitButton = new TextButton("EXIT", skin);

        // Xử lý sự kiện click
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Thêm vào table
        table.add(titleLabel).padBottom(80).row();
        table.add(playButton).size(300, 80).padBottom(30).row();
        table.add(exitButton).size(300, 80);
    }

    private void createSkin() {
        skin = new Skin();

        // Font chữ
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        skin.add("default", font);
        
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        skin.add("title", titleFont);

        // Khởi tạo style cho TextButton và gán trực tiếp Drawable (màu nền)
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = createDrawable(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        textButtonStyle.down = createDrawable(new Color(0.1f, 0.1f, 0.1f, 0.9f));
        textButtonStyle.over = createDrawable(new Color(0.3f, 0.3f, 0.3f, 0.9f));
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.downFontColor = Color.LIGHT_GRAY;
        
        skin.add("default", textButtonStyle);
    }

    private TextureRegionDrawable createDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cập nhật và vẽ Scene2D Stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        
        // Cập nhật lại kích thước ảnh nền khi thay đổi cửa sổ
        if (stage.getActors().size > 0 && stage.getActors().get(0) instanceof Image) {
            stage.getActors().get(0).setSize(width, height);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (background != null) background.dispose();
    }
}
