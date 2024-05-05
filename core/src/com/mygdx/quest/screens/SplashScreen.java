package com.mygdx.quest.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.quest.AnglersQuest;

public class SplashScreen extends ScreenAdapter {
    
    private final AnglersQuest game;
    private Stage stage;
    private Image splashImg;

    public SplashScreen(final AnglersQuest game) {
        this.game = game;
        this.stage = new Stage(new ExtendViewport(AnglersQuest.V_WIDTH, AnglersQuest.V_HEIGHT, game.camera));
    }

    @Override
    public void show() {
        System.out.println("SPLASH SCREEN");
        Gdx.input.setInputProcessor(stage);

        Runnable transitionRunnable = new Runnable() {
            @Override
            public void run() {
                game.setScreen(game.mainMenuScreen);
            }
        };

        Texture splashTexture = game.assets.get("assets/images/anglers-quest-header.png", Texture.class);
        splashImg = new Image(splashTexture);
        splashImg.setPosition(stage.getWidth() / 2 - splashImg.getWidth() / 2, stage.getHeight() / 2 - splashImg.getHeight() / 2);

        // Effects for splash screen
        splashImg.addAction(
            sequence(
                alpha(0f),
                fadeIn(3f),
                fadeOut(3f),
                run(transitionRunnable))
        );

        stage.addActor(splashImg);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.FOREST);
        
        update(delta);

        skipSplashScreen();

        stage.draw();
    }

    public void update(float delta) {
        stage.act(delta);
    }

    private void skipSplashScreen() {
        if (Gdx.input.isTouched()) {
            game.setScreen(game.mainMenuScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
