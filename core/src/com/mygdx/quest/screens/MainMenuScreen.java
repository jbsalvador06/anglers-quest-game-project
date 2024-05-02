package com.mygdx.quest.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

    private TextButton playButton, creditsButton, exitButton;

    public MainMenuScreen(final AnglersQuest game) {
        this.game = game;
        this.stage = new Stage(new ExtendViewport(AnglersQuest.V_WIDTH, AnglersQuest.V_HEIGHT, game.camera));
    }

    @Override
    public void show() {
        System.out.println("MAIN MENU SCREEN");
        // For initializing background

        Gdx.input.setInputProcessor(stage);
        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(game.assets.get("assets/skins/quest-skin.atlas", TextureAtlas.class));
        this.skin.load(Gdx.files.internal("assets/skins/quest-skin.json"));

        initButton();

        // Table mainTable = new Table();
        // mainTable.setFillParent(true);
        // mainTable.center();

        // // Configure title
        // String title = "{COLOR=black}{WAVE}Angler's" + "\n{COLOR=black}{WAVE}Quest";

        // TypingLabel titleLabel = new TypingLabel(title, skin);
        // // titleLabel.setAlignment(1);
        // // titleLabel.setFontScale(5f);

        // // Configure buttons
        // TextButton playButton = new TextButton("Play", skin);
        // TextButton loadButton = new TextButton("Load", skin);
        // TextButton creditsButton = new TextButton("Credits", skin);
        // TextButton exitButton = new TextButton("Exit", skin);

        // playButton.addListener(new ClickListener() {
        //     @Override
        //     public void clicked(InputEvent event, float x, float y) {
        //         game.getScreenManager().pushScreen(new LoadingScreen(game), null);
        //     }
        // });
        // loadButton.addListener(new ClickListener() {
        //     @Override
        //     public void clicked(InputEvent event, float x, float y) {
        //         System.out.println("Do something for this please");
        //     }
        // });
        // creditsButton.addListener(new ClickListener() {
        //     @Override
        //     public void clicked(InputEvent event, float x, float y) {
        //         System.out.println("You have clicked the credits button");
        //     }
        // });
        // exitButton.addListener(new ClickListener() {
        //     @Override
        //     public void clicked(InputEvent event, float x, float y) {
        //         Gdx.app.exit();
        //     }
        // });

        // // Arrange the table
        // // mainTable.add(titleLabel)
        // //     .colspan(4)
        // //     .expandX()
        // //     .fillX()
        // //     .pad(50);
        // // mainTable.row();
        // mainTable.add(image)
        //     .colspan(3)
        //     // .expandX()
        //     // .fillX()
        //     .pad(50);
        // mainTable.row();
        // mainTable.add(playButton)
        //     .width(game.widthScreen / 8)
        //     .height(game.heightScreen / 8)
        //     .pad(10);
        // mainTable.add(creditsButton)
        //     .width(game.widthScreen / 8)
        //     .height(game.heightScreen / 8)
        //     .pad(10);
        // mainTable.add(exitButton)
        //     .width(game.widthScreen / 8)
        //     .height(game.heightScreen / 8)
        //     .pad(10);
        // // mainTable.debugAll();

        // stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("#80b782"));

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // viewport.update(width, height);
        // camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        // camera.update();
        // viewport.update(width, height, true);
        // game.widthScreen = width;
        // game.heightScreen = height;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void initButton() {
        int buttonWidth = AnglersQuest.V_WIDTH / 8;
        int buttonHeight = AnglersQuest.V_HEIGHT / 8;


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

        creditsButton = new TextButton("Credits", skin, "default");
        creditsButton.setSize(200, 80);
        creditsButton.addAction(
            sequence(
                alpha(0),
                parallel(
                    fadeIn(0.5f),
                    moveBy(0, -20, .5f, Interpolation.pow5Out)
                )
            ));

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
        table.add(playButton).pad(10).width(buttonWidth).height(buttonHeight);
        table.add(creditsButton).pad(10).width(buttonWidth).height(buttonHeight);
        table.add(exitButton).pad(10).width(buttonWidth).height(buttonHeight);

        stage.addActor(table);
    }

}
