package com.mygdx.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.quest.screens.MainMenuScreen;
import com.mygdx.quest.utils.Assets;

import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.ManagedScreen;
import de.eskalon.commons.screen.transition.ScreenTransition;


public class AnglersQuest extends ManagedGame<ManagedScreen, ScreenTransition> {

    public int widthScreen, heightScreen;
    public Assets assets;
    public OrthographicCamera camera;
    public Stage stage;
    public Viewport viewport;

    private SpriteBatch batch;
	
	@Override
    public final void create() {
        super.create();
        
        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(widthScreen, heightScreen, camera);
        this.stage = new Stage(viewport);

        this.assets = new Assets();

        this.batch = new SpriteBatch();  
              
        assets.loadSkin();
        assets.getAssetManager().finishLoading();

        this.screenManager.setAutoDispose(true, true);
        this.screenManager.pushScreen(new MainMenuScreen(this), null);
    }

    @Override
    public void render() {
        super.render();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

}
