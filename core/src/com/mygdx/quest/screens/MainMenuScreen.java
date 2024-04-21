package com.mygdx.quest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.quest.AnglersQuest;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import de.eskalon.commons.screen.ManagedScreenAdapter;

public class MainMenuScreen extends ManagedScreenAdapter {

    private final AnglersQuest game;
    private Skin skin;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    private Image image;

    public MainMenuScreen(final AnglersQuest game) {
        this.game = game;
        this.skin = game.assets.getSkin();

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(game.widthScreen, game.heightScreen, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        this.image = new Image(new Texture(Gdx.files.internal("anglers-quest-header.png")));

        this.stage = new Stage(viewport);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Configure title
        String title = "{COLOR=black}{WAVE}Angler's" + "\n{COLOR=black}{WAVE}Quest";

        TypingLabel titleLabel = new TypingLabel(title, skin);
        titleLabel.setAlignment(1);
        titleLabel.setFontScale(5f);

        // Configure buttons
        TextButton playButton = new TextButton("Play", skin);
        TextButton loadButton = new TextButton("Load", skin);
        TextButton creditsButton = new TextButton("Credits", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreenManager().pushScreen(new LoadingScreen(game), null);
            }
        });
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Do something for this please");
            }
        });
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("You have clicked the credits button");
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Arrange the table
        // mainTable.add(titleLabel)
        //     .colspan(4)
        //     .expandX()
        //     .fillX()
        //     .pad(50);
        // mainTable.row();
        mainTable.add(image)
            .colspan(4)
            // .expandX()
            // .fillX()
            .pad(50);
        mainTable.row();
        mainTable.add(playButton)
            .width(game.widthScreen / 8)
            .height(game.heightScreen / 8)
            .pad(10);
        mainTable.add(loadButton)
            .width(game.widthScreen / 8)
            .height(game.heightScreen / 8)
            .pad(10);
        mainTable.add(creditsButton)
            .width(game.widthScreen / 8)
            .height(game.heightScreen / 8)
            .pad(10);
        mainTable.add(exitButton)
            .width(game.widthScreen / 8)
            .height(game.heightScreen / 8)
            .pad(10);
        // mainTable.debugAll();

        stage.addActor(mainTable);
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
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
