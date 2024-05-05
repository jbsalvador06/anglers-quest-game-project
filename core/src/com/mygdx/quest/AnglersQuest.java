package com.mygdx.quest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.quest.screens.GameScreen;
import com.mygdx.quest.screens.LoadingScreen;
import com.mygdx.quest.screens.MainMenuScreen;
import com.mygdx.quest.screens.SplashScreen;


public class AnglersQuest extends Game {

    public SpriteBatch batch;
    Texture img;

    // public BitmapFont font;
    public static AnglersQuest INSTANCE;

    public static int V_WIDTH = 1280;
    public static int V_HEIGHT = 720;

    public int widthScreen, heightScreen;
    public OrthographicCamera camera;

    public AnglersQuest() {
        INSTANCE = this;
    }

    public AssetManager assets;
    // Screens
    public LoadingScreen loadingScreen;
    public GameScreen gameScreen;
    public SplashScreen splashScreen;
    public MainMenuScreen mainMenuScreen;
    public Object viewport;

	@Override
    public final void create() {

        assets = new AssetManager();
        
        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, widthScreen, heightScreen);

        // Potentially have to initialize font
        // initFont();

        batch = new SpriteBatch();  

        loadingScreen = new LoadingScreen(INSTANCE);
        splashScreen = new SplashScreen(INSTANCE);
        mainMenuScreen = new MainMenuScreen(INSTANCE);
        gameScreen = new GameScreen(camera, INSTANCE);


        setScreen(loadingScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        // font.dispose();
        loadingScreen.dispose();
        splashScreen.dispose();
        mainMenuScreen.dispose();
        gameScreen.dispose();
    }

    // private void initFont() {
    //     FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/PressStart2P-Regular.ttf"));
    //     FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

    //     params.size = 34;
    //     params.color = Color.BLACK;
    //     font = generator.generateFont(params);
    // }

}
