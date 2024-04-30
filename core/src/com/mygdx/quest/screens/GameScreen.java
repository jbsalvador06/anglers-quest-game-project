package com.mygdx.quest.screens;

import java.util.Map;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.quest.AnglersQuest;
import com.mygdx.quest.entities.Player;
import com.mygdx.quest.utils.Assets;
import com.mygdx.quest.utils.CameraHandler;
import com.mygdx.quest.utils.Constants;
import com.mygdx.quest.utils.Fish;
import com.mygdx.quest.utils.FishParser;
import com.mygdx.quest.utils.Inventory;
import com.mygdx.quest.utils.Shop;
import com.mygdx.quest.utils.TileMapHelper;

import de.eskalon.commons.screen.ManagedScreenAdapter;

public class GameScreen extends ManagedScreenAdapter {

    // Temporary array for random generation
    private String[] fish = {"Red Snapper", "Clownfish", "Blue Tang", "Salmon", "Trout", "Catfish", "Pike", "Bass", "Perch", "Tuna", "Swordfish", "Marlin", "Sturgeon", "Walleye", "Muskellunge", "Northern Pike", "Striped Bass", "Crappie", "Bluefin Tuna", "Wahoo", "Mahi Mahi"};

    private Inventory inventory;
    private Table mainTable;

    private final AnglersQuest game;
    private Assets assets;
    
    private Stage stage;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TileMapHelper tileMapHelper;
    private OrthographicCamera camera;

    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer box2dDebugRenderer;

    private Player player;
    private ShapeRenderer shapeRenderer;

    Circle playerCircle;
    Rectangle testRectangle;

    private Skin skin;
    private Stage uiStage;

    private Shop shop;

    public GameScreen(final AnglersQuest game) {
        this.game = game;
        this.assets = game.assets;
        this.skin = assets.getAssetManager().get(Assets.SKIN);
        this.batch = new SpriteBatch();
        this.mapRenderer = new OrthogonalTiledMapRenderer(assets.getMap(), 1 / Constants.PPM);
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, game.widthScreen / 2, game.heightScreen / 2);

        this.uiStage = new Stage(new FitViewport(camera.viewportWidth, camera.viewportHeight));
  
        assets.loadSkin();
        assets.getAssetManager().finishLoading();
        
        this.mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.left();
        mainTable.top();
        
        this.inventory = new Inventory();
        
        this.stage = new Stage(new FitViewport(game.widthScreen, game.heightScreen, camera));
    }

    @Override
    public void show() {
        System.out.println("Game Screen \n");
        System.out.println(uiStage.getViewport().getWorldWidth() + " " + uiStage.getViewport().getWorldHeight());
        System.out.println(skin);

        Gdx.input.setInputProcessor(uiStage);

        world = new World(new Vector2(0, 0), false);
        box2dDebugRenderer = new Box2DDebugRenderer();

        tileMapHelper = new TileMapHelper(this, game);
        mapRenderer = tileMapHelper.setupMap();
        this.shop = new Shop(player.getInventory());

        shapeRenderer = new ShapeRenderer();

        Map<String, Fish> fishes = FishParser.parseFishJson("core/src/com/mygdx/quest/utils/fish.json");

        Random rand = new Random();

        for (int i = 0; i < 9 ; i++) {
            player.addItem(fishes.get(fish[rand.nextInt(21)]));
        }

        // With UI
        Label inventoryLabel = new Label("Inventory:", skin);
        inventoryLabel.setFontScale(0.7f);
        mainTable.add(inventoryLabel).colspan(3).center();
        mainTable.row();
        inventory.setInventory(player.getInventory());
        inventory.displayInventory(skin, mainTable);

        System.out.println("Before sorting:");
        for (Fish fish : player.getInventory()) {
            System.out.println("Name: " + fish.getName() + " | Location: " + fish.getLocation() + " | Rarity: " + fish.getRarity() + " | Weight: " + fish.getWeight());
        }

        System.out.println("");

        // After sorting
        player.sortInventory();
        System.out.println("After sorting:");
        for (Fish fish : player.getInventory()) {
            System.out.println("Name: " + fish.getName() + " | Location: " + fish.getLocation() + " | Rarity: " + fish.getRarity() + " | Weight: " + fish.getWeight());
        }
        
        uiStage.addActor(mainTable);
        mainTable.setColor(Color.GRAY);
        mainTable.debugAll();

        TextButton buyButton = new TextButton("Buy", skin);
        
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openShop();
            }
        });

        TextButton sellButton = new TextButton("Sell", skin);

        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sellShop();
            }
        });

        mainTable.add(buyButton);
        mainTable.add(sellButton);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("#80b782"));
        stage.getViewport().apply();
        uiStage.getViewport().apply();
        
        update(delta);

        mapRenderer.setView(camera);
        mapRenderer.render();

        stage.act(delta);
        stage.draw();

        uiStage.act(delta);
        uiStage.draw();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        player.render(batch);
        batch.end();

        box2dDebugRenderer.render(world, camera.combined.scl(Constants.PPM));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.setToOrtho(false, width / 2, height / 2);
        uiStage.getViewport().update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void pause() {
        System.out.println("GameScreen Paused");
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        mapRenderer.getMap().dispose();
        stage.dispose();
        box2dDebugRenderer.dispose();
        world.dispose();
        shapeRenderer.dispose();
        assets.dispose();
        skin.dispose();
    }

    private boolean isShopOpen = false;

    private void openShop() {
        if (!isShopOpen) {
            Window window = new Window("Shop", skin);
            TextButton closeShop = new TextButton("X", skin);
            closeShop.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    uiStage.getActors().removeValue(window, true);
                }
            });

            window.add(closeShop).right().top();

            uiStage.addActor(window);
            isShopOpen = true;
        } else {
            isShopOpen = false;
        }
    }

    private void sellShop() {
        shop.sellFish();
    }

    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate(delta);
        
        player.update();
    }

    private void cameraUpdate(float delta) {
        camera.zoom = Constants.zoom;
        // CameraHandler.lockOnTarget(camera, player.getBody().getPosition().scl(Constants.PPM));
        CameraHandler.freeRoam(camera, player.getBody().getPosition().scl(Constants.PPM), stage.getViewport());
        // CameraHandler.limitCamera(camera, player.getBody().getPosition().scl(Constants.PPM), mapWidth, mapHeight);
    }
}
