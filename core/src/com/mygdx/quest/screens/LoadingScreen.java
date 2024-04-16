package com.mygdx.quest.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.quest.AnglersQuest;

import de.eskalon.commons.screen.ManagedScreenAdapter;

public class LoadingScreen extends ManagedScreenAdapter {
    
    private final AnglersQuest game;
    private ShapeRenderer shapeRenderer;
    private float progress;
    
    private Stage stage;

    public LoadingScreen(final AnglersQuest game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.progress = 0f;
        this.stage = game.stage;
    }

    @Override
    public void show() {
        queueAssets();
    }

    private void queueAssets() {
        game.assets.loadMap();
        game.assets.loadPlayer();
    }

    private void update(float delta) {
        progress = MathUtils.lerp(progress, game.assets.getAssetManager().getProgress(), .1f);
        System.out.println(progress + " " + game.assets.getAssetManager().getProgress());

        if (game.assets.getAssetManager().update() && progress > game.assets.getAssetManager().getProgress() - 0.001f) {
            game.getScreenManager().pushScreen(new GameScreen(game), null);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("#80b782"));

        update(delta);

        stage.act();
        stage.draw();

        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.FOREST);
        shapeRenderer.rect(32, game.heightScreen / 2 - 8, game.widthScreen - 64, 16);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(32, game.heightScreen / 2 - 8, progress * (game.widthScreen - 64), 16);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        game.widthScreen = width;
        game.heightScreen = height;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
