package com.mygdx.quest.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.quest.AnglersQuest;

import de.eskalon.commons.screen.ManagedScreenAdapter;
import de.eskalon.commons.screen.transition.SlidingTransition;
import de.eskalon.commons.screen.transition.impl.SlidingDirection;

public class SplashScreen extends ManagedScreenAdapter {
    
    final AnglersQuest game;
    private SpriteBatch batch;
    private ExtendViewport viewport;

    public SplashScreen(final AnglersQuest game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.viewport = new ExtendViewport(game.widthScreen, game.heightScreen);
    }

    @Override
    public void show() {
        game.getScreenManager().pushScreen(new MainMenuScreen(game), new SlidingTransition(batch, SlidingDirection.DOWN, false, 2.5f, Interpolation.bounceOut));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.FOREST);
        viewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {

    }

}
