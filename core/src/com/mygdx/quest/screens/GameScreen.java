package com.mygdx.quest.screens;

import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.quest.entities.Player;
import com.mygdx.quest.entities.Pond;
import com.mygdx.quest.utils.CameraHandler;
import com.mygdx.quest.utils.Constants;
import com.mygdx.quest.utils.Fish;
import com.mygdx.quest.utils.FishParser;
import com.mygdx.quest.utils.Inventory;
import com.mygdx.quest.utils.MyContactListener;
import com.mygdx.quest.utils.Shop;
import com.mygdx.quest.utils.TileMapHelper;

import de.eskalon.commons.screen.ManagedScreenAdapter;

public class GameScreen extends ManagedScreenAdapter {

    // Temporary array for random generation
    private String[] fish = {"Large Mouth Bass", "Walleye", "Trout", "Crappie", "Seabass", "Goldfish", "Comet", "Oranda", "Shubunkin", "Mosquito Fish", "Sunfish", "Catfish", "Koi"};

    // For UI elements
    private Stage uiStage;
    private FitViewport uiViewport;

    // Viewport
    private ExtendViewport viewport;
    private final float WORLD_WIDTH = 1920;
    private final float WORLD_HEIGHT = 1080;

    // Box2D
    private World world;
    private Box2DDebugRenderer box2dDebugRenderer;
    private boolean renderDebug = false;
    MyContactListener contactListener;

    // Assets and Map
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TileMapHelper tileMapHelper;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Skin skin;

    // Game Objects
    private Player player;
    private Pond pond;
    private boolean pondKey = false;
    float elapsedTime = 0.0f;

    private Inventory inventory;
    private Table mainTable;

    private Shop shop;

    public GameScreen(OrthographicCamera camera) {
        
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera = camera;
        this.batch = new SpriteBatch();

        this.contactListener = new MyContactListener();

        // For UI elements
        this.uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.uiStage = new Stage(uiViewport);
        this.mainTable = new Table();
        this.skin = new Skin(Gdx.files.internal("assets/skins/quest-skin.json"));

        // Box2D
        this.world = new World(new Vector2(0, 0), false);
        this.world.setContactListener(contactListener);
        this.box2dDebugRenderer = new Box2DDebugRenderer(
            true, true, true, true, true, true
        );

        this.tileMapHelper = new TileMapHelper(this);
        this.orthogonalTiledMapRenderer = tileMapHelper.setupMap();
        
        this.inventory = new Inventory();
        
    }

    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate(delta);

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
        player.update();

        uiStage.act(delta);
        uiStage.draw();

        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Keys.F3)) {
            renderDebug = !renderDebug;
        }
    }

    private void cameraUpdate(float delta) {
        camera.zoom = Constants.zoom;
        CameraHandler.lockOnTarget(camera, player.getBody().getPosition().scl(Constants.PPM));
        // CameraHandler.freeRoam(camera, player.getBody().getPosition());
        // CameraHandler.limitCamera(camera, player.getBody().getPosition().scl(Constants.PPM), mapWidth, mapHeight);
    }

    @Override
    public void show() {
        System.out.println("Game Screen \n");

        Map<String, Fish> fishes = FishParser.parseFishJson("core/src/com/mygdx/quest/utils/fish.json");

        Random rand = new Random();

        for (int i = 0; i < 9 ; i++) {
            player.addItem(fishes.get(fish[rand.nextInt(13)]));
        }

        // Before sorting
        System.out.println("Before sorting:");
        for (Fish fish : player.getInventory()) {
            System.out.println("Name: " + fish.getName() + " | Location: " + fish.getLocation() + " | Rarity: " + fish.getRarity() + " | Weight: " + fish.getWeight() + " | Price: " + fish.getPrice());
        }

        System.out.println("");

        // After sorting
        player.sortInventory();
        System.out.println("After sorting:");
        for (Fish fish : player.getInventory()) {
            System.out.println("Name: " + fish.getName() + " | Location: " + fish.getLocation() + " | Rarity: " + fish.getRarity() + " | Weight: " + fish.getWeight() + " | Price: " + fish.getPrice());
        }

        // uiStage.addActor(mainTable);
        // mainTable.setColor(Color.GRAY);
        // mainTable.debugAll();

        // TextButton buyButton = new TextButton("Buy", skin);
        
        // buyButton.addListener(new ClickListener() {
        //     @Override
        //     public void clicked(InputEvent event, float x, float y) {
        //         openShop();
        //     }
        // });

        // TextButton sellButton = new TextButton("Sell", skin);

        // sellButton.addListener(new ClickListener() {
        //     @Override
        //     public void clicked(InputEvent event, float x, float y) {
        //         sellShop();
        //     }
        // });

        // mainTable.add(buyButton);
        // mainTable.add(sellButton);
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(Color.valueOf("#80b782"));

        orthogonalTiledMapRenderer.render();

        if (renderDebug) {
            box2dDebugRenderer.render(world, camera.combined.scl(Constants.PPM));
        }

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        handleItemInteraction();

        player.render(batch);
        batch.end();

        // box2dDebugRenderer.render(world, camera.combined.scl(Constants.PPM));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPond(Pond pond) {
        this.pond = pond;
    }

    @Override
    public void pause() {
        System.out.println("GameScreen Paused");
    }

    private void handleItemInteraction() {
        if (Gdx.input.isKeyJustPressed(Keys.E)) {
            if (contactListener.pondInteract) {
                pondKey = true;
                elapsedTime = 0.0f;
            }
        }

        // if (pondKey) {
        //     renderFishingWindow();
        //     pondKey = !pondKey;
        // }
    }

    private void renderFishingWindow() {
        Window fishingWindow = new Window("Catch the fish!", skin);
        uiStage.addActor(fishingWindow);
        System.out.println("Fishing Window");
    }

    @Override
    public void dispose() {
        batch.dispose();
        box2dDebugRenderer.dispose();
        orthogonalTiledMapRenderer.dispose();
        world.dispose();
    }

    // private boolean isShopOpen = false;

    // private void openShop() {
    //     if (!isShopOpen) {
    //         Window window = new Window("Shop", skin);
    //         TextButton closeShop = new TextButton("X", skin);
    //         closeShop.addListener(new ClickListener() {
    //             @Override
    //             public void clicked(InputEvent event, float x, float y) {
    //                 uiStage.getActors().removeValue(window, true);
    //             }
    //         });

    //         window.add(closeShop).right().top();

    //         uiStage.addActor(window);
    //         isShopOpen = true;
    //     } else {
    //         isShopOpen = false;
    //     }
    // }

    // private void sellShop() {
    //     shop.sellFish();
    // }
}
