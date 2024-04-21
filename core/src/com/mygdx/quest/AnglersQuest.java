package com.mygdx.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.quest.screens.SplashScreen;
import com.mygdx.quest.utils.Assets;

import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.ManagedScreen;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.SlidingTransition;
import de.eskalon.commons.screen.transition.impl.SlidingDirection;


public class AnglersQuest extends ManagedGame<ManagedScreen, ScreenTransition> {

    public int widthScreen, heightScreen;
    public Assets assets;

    private SpriteBatch batch;
    public Object getScreenManager;
	
	@Override
    public final void create() {
        super.create();
        
        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();

        this.assets = new Assets();

        this.batch = new SpriteBatch();  
              
        assets.loadSkin();
        assets.getAssetManager().finishLoading();

        this.screenManager.setAutoDispose(true, true);
        this.screenManager.pushScreen(new SplashScreen(this), new SlidingTransition(batch, SlidingDirection.DOWN, false, 2.5f, Interpolation.bounceOut));
    }

    @Override
    public void render() {
        super.render();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

}
