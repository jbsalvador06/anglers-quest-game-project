package com.mygdx.quest.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.quest.AnglersQuest;

public class LoadingScreen extends ScreenAdapter {
    
    private final AnglersQuest game;

    private ShapeRenderer shapeRenderer;
    private float progress;
    
    // private Stage stage;
    // private ExtendViewport viewport;

    public LoadingScreen(final AnglersQuest game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
    }

    private void queueAssets() {
        game.assets.load("assets/anglers-quest-header.png", Texture.class);
        game.assets.load("assets/skins/quest-skin.atlas", TextureAtlas.class);
        game.assets.load("assets/images/background.png", Texture.class);
    }

    @Override
    public void show() {
        System.out.println("LOADING SCREEN");
        this.progress = 0f;
        queueAssets();
    }

    private void update(float delta) {
        progress = MathUtils.lerp(progress, game.assets.getProgress(), .1f);
        // System.out.println(progress + " " + game.assets.getProgress());

        if (game.assets.update() && progress >= game.assets.getProgress() - 0.001f) {
            game.setScreen(game.splashScreen);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("#80b782"));

        update(delta);

        game.camera.position.set(AnglersQuest.V_WIDTH / 2, AnglersQuest.V_HEIGHT / 2, 0f);
        game.camera.update();
        shapeRenderer.setProjectionMatrix(game.camera.combined);

        shapeRenderer.begin(ShapeType.Filled);

        // float rectWidth = game.camera.viewportWidth / 3;
        // float rectHeight = 32;
        // float centerX = (game.camera.viewportWidth - rectWidth) / 2;
        // float rectY = game.camera.viewportHeight / 3;

        shapeRenderer.setColor(Color.FOREST);
        shapeRenderer.rect(32, game.camera.viewportHeight / 2 - 8, game.camera.viewportWidth - 64, 16);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(32, game.camera.viewportHeight / 2 - 8, progress * (game.camera.viewportWidth - 64), 16);
        shapeRenderer.end();

        // shapeRenderer.setColor(Color.FOREST);
        // shapeRenderer.rect(centerX, rectY, rectWidth, rectHeight);

        // float progressWidth = progress * rectWidth;

        // shapeRenderer.setColor(Color.WHITE);
        // shapeRenderer.rect(centerX, rectY, progressWidth, rectHeight);
        // shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        game.camera.viewportWidth = width;
        game.camera.viewportHeight = height;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
