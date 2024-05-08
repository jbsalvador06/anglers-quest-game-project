package com.mygdx.quest.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.quest.AnglersQuest;

public class MainMenuScreen extends ScreenAdapter {

    private final AnglersQuest game;
    private Skin skin;
    private Stage stage;
    private Image background;
    private Image splashImg;

    private TextButton playButton, exitButton;

    public MainMenuScreen(final AnglersQuest game) {
        this.game = game;
        this.stage = new Stage(new ExtendViewport(AnglersQuest.V_WIDTH, AnglersQuest.V_HEIGHT, game.camera));
    }

    @Override
    public void show() {
        System.out.println("MAIN MENU SCREEN");
        Texture bgTexture = game.assets.get("images\\background.png");

        background = new Image(bgTexture);
        background.setFillParent(true);

        Gdx.input.setInputProcessor(stage);
        stage.clear();

        stage.addActor(background);

        this.skin = new Skin();
        this.skin.addRegions(game.assets.get("skins\\old-skins\\quest-skin.atlas", TextureAtlas.class));
        this.skin.load(Gdx.files.internal("skins\\old-skins\\quest-skin.json"));

        initButton();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("#80b782"));

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void initButton() {
        int buttonWidth = AnglersQuest.V_WIDTH / 8;
        int buttonHeight = AnglersQuest.V_HEIGHT / 8;

        Texture splashTexture = game.assets.get("images\\anglers-quest-header.png", Texture.class);
        splashImg = new Image(splashTexture);
        splashImg.setPosition(stage.getWidth() / 2 - splashImg.getWidth() / 2, stage.getHeight() / 2 - splashImg.getHeight() / 2);

        playButton = new TextButton("Play", skin, "default");
        playButton.setSize(200, 80);
        playButton.addAction(
            sequence(
                alpha(0),
                parallel(
                    fadeIn(0.5f),
                    moveBy(0, -20, .5f, Interpolation.pow5Out)
                )
            ));
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.gameScreen);
            }
        });

        exitButton = new TextButton("Exit", skin, "default");
        exitButton.setSize(200, 80);
        exitButton.addAction(
            sequence(
                alpha(0),
                parallel(
                    fadeIn(0.5f),
                    moveBy(0, -20, .5f, Interpolation.pow5Out)
                )
            ));
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table table  = new Table();
        table.setFillParent(true);
        table.center();
        table.add(splashImg).pad(10);
        table.add(playButton).pad(10).width(buttonWidth).height(buttonHeight);
        table.add(exitButton).pad(10).width(buttonWidth).height(buttonHeight);

        stage.addActor(table);
    }

}
