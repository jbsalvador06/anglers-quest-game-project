package com.mygdx.quest.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.quest.AnglersQuest;

import de.eskalon.commons.screen.ManagedScreenAdapter;

public class SplashScreen extends ManagedScreenAdapter {
    
    final AnglersQuest game;

    public SplashScreen(final AnglersQuest game) {
        this.game = game;
    }

    @Override
    public void show() {
        
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.PURPLE);

        game.getScreenManager().pushScreen(new MainMenuScreen(game), null);
    }

    @Override
    public void resize(int width, int height) {
        
    }

    @Override
    public void dispose() {
        
    }

}
