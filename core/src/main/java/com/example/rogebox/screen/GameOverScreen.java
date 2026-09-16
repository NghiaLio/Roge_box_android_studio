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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.rogebox.RogeBoxGame;

public class GameOverScreen implements Screen {

    private final RogeBoxGame game;
    private final int finalScore;
    private Stage stage;
    private Skin skin;
    private Texture restartTex;

    public GameOverScreen(RogeBoxGame game) {
        this(game, 0);
    }

    public GameOverScreen(RogeBoxGame game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("title"), Color.RED);
        Label titleLabel = new Label("GAME OVER", labelStyle);

        Label.LabelStyle scoreStyle = new Label.LabelStyle(skin.getFont("default"), Color.GOLD);
        Label scoreLabel = new Label("COINS / SCORE: " + finalScore, scoreStyle);

        restartTex = new Texture("restart.png");
        ImageButton.ImageButtonStyle restartStyle = new ImageButton.ImageButtonStyle();
        restartStyle.up = new TextureRegionDrawable(new TextureRegion(restartTex));
        ImageButton restartButton = new ImageButton(restartStyle);

        TextButton menuButton = new TextButton("MAIN MENU", skin);

        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HomeScreen(game));
            }
        });

        table.add(titleLabel).padBottom(20).row();
        table.add(scoreLabel).padBottom(50).row();
        table.add(restartButton).size(128, 128).padBottom(30).row();
        table.add(menuButton).size(300, 80);
    }

    private void createSkin() {
        skin = new Skin();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        skin.add("default", font);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        skin.add("title", titleFont);

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

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        if (restartTex != null) restartTex.dispose();
    }
}
