package com.mygdx.quest.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.quest.AnglersQuest;
import com.mygdx.quest.entities.Player;
import com.mygdx.quest.entities.Pond;
import com.mygdx.quest.utils.CameraHandler;
import com.mygdx.quest.utils.Constants;
import com.mygdx.quest.utils.Fish;
import com.mygdx.quest.utils.FishParser;
import com.mygdx.quest.utils.MyContactListener;
import com.mygdx.quest.utils.Shop;
import com.mygdx.quest.utils.TileMapHelper;

import de.eskalon.commons.screen.ManagedScreenAdapter;

public class GameScreen extends ManagedScreenAdapter {

    private final AnglersQuest game;

    // Temporary array for random generation
    private String[] fish = { "Large Mouth Bass", "Walleye", "Trout", "Crappie", "Seabass", "Goldfish", "Comet",
            "Oranda", "Shubunkin", "Mosquito Fish", "Sunfish", "Catfish", "Koi" };

    // Shop Items
    private Map<String, Integer> upgrades;
    private boolean isShopWindowOpen = false;
    private boolean isSellWindowOpen = false;

    // For UI elements
    private Stage uiStage;
    private Table mainTable;
    TextButton shopButton, inventoryButton, sellButton;
    private boolean isFishingWindowOpen = false;
    private boolean isPopupOpen = false;

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
    private static Music mainMenuMusic, gameMusic;
    private Sound splashScreenSound;

    // Game Objects
    private Player player;
    private Pond pond;
    private boolean pondKey = false;
    float elapsedTime = 0.0f;
    private boolean isInventoryWindowOpen = false;

    // Inventory
    private ArrayList<Fish> inventory;

    public GameScreen(OrthographicCamera camera, final AnglersQuest game) {

        this.game = game;

        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera = camera;
        this.batch = new SpriteBatch();

        this.contactListener = new MyContactListener();

        // For UI elements
        this.uiStage = new Stage(new ScreenViewport(new OrthographicCamera()));
        this.uiStage.setDebugAll(true);
        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.mainTable.debugAll();

        // Box2D
        this.world = new World(new Vector2(0, 0), false);
        this.world.setContactListener(contactListener);
        this.box2dDebugRenderer = new Box2DDebugRenderer(
                true, true, true, true, true, true);

        this.tileMapHelper = new TileMapHelper(this);
        this.orthogonalTiledMapRenderer = tileMapHelper.setupMap();

        // Inventory
        this.inventory = player.getInventory();

        // Add upgrades to shop
        upgrades = new HashMap<>();
        upgrades.put("Bait", 50);
        upgrades.put("Hook", 100);
        upgrades.put("Tackle Box", 250);
        upgrades.put("Bobber", 350);
        upgrades.put("High Test Fishing Line", 500);

        // Audio
        GameScreen.mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/mainMenuSFX.mp3"));
        splashScreenSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/splashScreenSFX.mp3"));

        mainMenuMusic.setLooping(true);
        mainMenuMusic.setVolume(0.1f);
        splashScreenSound.setVolume(0, 0.1f);

        splashScreenSound.play();
        mainMenuMusic.play();

    }

    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate(delta);

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
        player.update();

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
        // CameraHandler.limitCamera(camera,
        // player.getBody().getPosition().scl(Constants.PPM), mapWidth, mapHeight);
    }

    @Override
    public void show() {
        System.out.println("GAME SCREEN \n");

        Gdx.input.setInputProcessor(uiStage);
        uiStage.clear();

        // For UI elements
        this.skin = new Skin();
        this.skin.addRegions(game.assets.get("assets/skins/old-skins/quest-skin.atlas", TextureAtlas.class));
        this.skin.load(Gdx.files.internal("assets/skins/old-skins/quest-skin.json"));

        shopButton = new TextButton("Shop", skin);
        sellButton = new TextButton("Sell", skin);
        inventoryButton = new TextButton("Inventory", skin);

        initUI();
        initUIButtonListeners();

        // Map<String, Fish> fishes = FishParser.parseFishJson("core/src/com/mygdx/quest/utils/fish.json");

        // Random rand = new Random();

        // for (int i = 0; i < 9; i++) {
        //     player.addItem(fishes.get(fish[rand.nextInt(13)]));
        // }

        // // Before sorting
        // System.out.println("Before sorting:");
        // for (Fish fish : player.getInventory()) {
        //     System.out.println("Name: " + fish.getName() + " | Location: " + fish.getLocation() + " | Rarity: "
        //             + fish.getRarity() + " | Weight: " + fish.getWeight() + " | Price: " + fish.getPrice());
        // }

        // System.out.println("");

        // // After sorting
        // player.sortInventory();
        // System.out.println("After sorting:");
        // for (Fish fish : player.getInventory()) {
        //     System.out.println("Name: " + fish.getName() + " | Location: " + fish.getLocation() + " | Rarity: "
        //             + fish.getRarity() + " | Weight: " + fish.getWeight() + " | Price: " + fish.getPrice());
        // }
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(Color.valueOf("#80b782"));

        // THIS HAS TO BE HERE OR ELSE
        // THE UI WILL RENDER BELOW THE MAP
        uiStage.act(delta);

        orthogonalTiledMapRenderer.render();

        if (renderDebug) {
            box2dDebugRenderer.render(world, camera.combined.scl(Constants.PPM));
        }

        // THIS HAS TO BE HERE OR ELSE
        // THE PLAYER WILL RENDER ABOVE THE UI
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        handleItemInteraction();

        player.render(batch);
        batch.end();

        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

        // For UI elements
        uiStage.getViewport().update(width, height, true);
    }

    private void initUI() {
        shopButton = new TextButton("Shop", skin);
        sellButton = new TextButton("Sell", skin);
        inventoryButton = new TextButton("Inventory", skin);

        mainTable.add(shopButton).expandX();
        mainTable.add(sellButton).expandX();
        mainTable.add(inventoryButton).expandX();

        mainTable.bottom().pad(50);

        uiStage.addActor(mainTable);
    }

    private void initUIButtonListeners() {
        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isShopWindowOpen) {
                    System.out.println("SHOP");
                    isShopWindowOpen = true;

                    initShopUI();
                }
            }
        });
        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isSellWindowOpen) {
                    System.out.println("SELL");
                    isSellWindowOpen = true;

                    initSellUI();
                }
            }
        });
        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isInventoryWindowOpen) {
                    System.out.println("INVENTORY");
                    isInventoryWindowOpen = true;

                    initInventoryUI();
                }
            }
        });
    }

    private void initShopUI() {
        Window shopWindow = new Window("Shop", skin);
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                shopWindow.remove();
                isShopWindowOpen = false;
                System.out.println("CLOSED SHOP WINDOW");
            }
        });

        int count = 0;

        for (Map.Entry<String, Integer> entry : upgrades.entrySet()) {
            String upgradeName = entry.getKey();
            int upgradePrice = entry.getValue();

            TextButton itemButton = new TextButton(upgradeName, skin);
            itemButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Bought: " + upgradeName);
                }
            });

            System.out.println("assets/upgrades/" + upgradeName.toLowerCase() + ".png");
            Texture upgradesTexture = new Texture(
                    Gdx.files.internal((String) "assets/upgrades/" + upgradeName.toLowerCase() + ".png"));
            TextureRegionDrawable resizedTexture = new TextureRegionDrawable(
                    new TextureRegion(upgradesTexture, 0, 0, upgradesTexture.getWidth(), upgradesTexture.getHeight()));
            resizedTexture.setMinWidth(100);
            resizedTexture.setMinHeight(100);
            ImageButton upgradesButton = new ImageButton(skin);

            shopWindow.add(upgradesButton).pad(5);
            count++;

            if (count % 2 == 0) {
                shopWindow.row();
            }
        }

        shopWindow.row();
        shopWindow.add(closeButton).colspan(2);
        shopWindow.pack();
        shopWindow.setPosition(game.widthScreen / 2 - shopWindow.getWidth() / 2,
                game.heightScreen / 2 - shopWindow.getHeight() / 2);
        uiStage.addActor(shopWindow);
    }

    private void initInventoryUI() {
        Window inventoryWindow = new Window("Inventory", skin);
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventoryWindow.remove();
                isInventoryWindowOpen = false;
                System.out.println("CLOSED INVENTORY WINDOW");
            }
        });

        Table inventoryTable = new Table();
        inventoryTable.pad(10);

        if (!player.getInventory().isEmpty()) {
            int count = 0;
            for (Fish fish : player.getInventory()) {
                System.out.println(fish.getImgUrl());
                Texture fishTexture = new Texture(Gdx.files.internal(fish.getImgUrl()));
                TextureRegionDrawable resizedTexture = new TextureRegionDrawable(
                        new TextureRegion(fishTexture, 0, 0, fishTexture.getWidth(), fishTexture.getHeight()));
                resizedTexture.setMinWidth(100);
                resizedTexture.setMinHeight(100);
                ImageButton fishButton = new ImageButton(resizedTexture);
                inventoryTable.add(fishButton).pad(5);
                count++;

                fishButton.addListener(new TextTooltip("Name: " + fish.getName() + "\nPrice: " + fish.getPrice() + "\nDescription: " + fish.getDescription(), skin));

                if (count % 3 == 0) {
                    inventoryTable.row();
                }
            }
        } else {
            Label noItemsLabel = new Label("Inventory is empty :(", skin);
            inventoryTable.add(noItemsLabel);
        }

        inventoryWindow.add(inventoryTable).row();
        inventoryWindow.add(closeButton).colspan(3);
        inventoryWindow.pack();
        inventoryWindow.setPosition(game.widthScreen / 2 - inventoryWindow.getWidth() / 2,
                game.heightScreen / 2 - inventoryWindow.getHeight() / 2);
        uiStage.addActor(inventoryWindow);
    }

    private void initSellUI() {
        Window sellWindow = new Window("Sell", skin);
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sellWindow.remove();
                isSellWindowOpen = false;
                System.out.println("CLOSED SELL WINDOW");
            }
        });

        Table inventoryTable = new Table();
        inventoryTable.pad(10);

        player.sortInventory();
        if (!player.getInventory().isEmpty()) {
            int count = 0;
            for (Fish fish : player.getInventory()) {
                Texture fishTexture = new Texture(Gdx.files.internal(fish.getImgUrl()));
                TextureRegionDrawable resizedTexture = new TextureRegionDrawable(
                        new TextureRegion(fishTexture, 0, 0, fishTexture.getWidth(), fishTexture.getHeight()));
                resizedTexture.setMinWidth(100);
                resizedTexture.setMinHeight(100);
                ImageButton fishButton = new ImageButton(resizedTexture);
                inventoryTable.add(fishButton).pad(5).size(100);
                count++;

                if (count % 3 == 0) {
                    inventoryTable.row();
                }

                fishButton.addListener(new TextTooltip("Name: " + fish.getName() + "\nPrice: " + fish.getPrice() + "\nDescription: " + fish.getDescription(), skin));

                fishButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        fishButton.remove();
                        player.setCoins(fish.getPrice());
                        player.removeItem(fish);
                    }
                });
            }
        } else {
            Label noItemsLabel = new Label("Inventory is empty :(", skin);
            inventoryTable.add(noItemsLabel);
        }

        sellWindow.add(inventoryTable).row();
        sellWindow.add(closeButton).colspan(3);
        sellWindow.pack();
        sellWindow.setPosition(game.widthScreen / 2 - sellWindow.getWidth() / 2,
                game.heightScreen / 2 - sellWindow.getHeight() / 2);
        uiStage.addActor(sellWindow);
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

                Map<String, Fish> fishes = FishParser.parseFishJson("core/src/com/mygdx/quest/utils/fish.json");

                Fish randomFish = FishParser.getRandomFish(fishes);

                if (!isFishingWindowOpen) {
                    System.out.println("FISHING");
                    System.out.println(randomFish.getName() + " " + randomFish.getRarity());
                    if (player.getInventory().size() < 9) {
                        switch (randomFish.getRarity()) {
                            case COMMON:
                                System.out.println(randomFish.getName());
                                isFishingWindowOpen = true;
    
                                player.addItem(randomFish);
                                break;
                            case RARE:
                                System.out.println(randomFish.getName());
                                isFishingWindowOpen = true;
                                
                                player.addItem(randomFish);
                                break;
                            case LEGENDARY:
                                System.out.println(randomFish.getName());
                                isFishingWindowOpen = true;
    
                                player.addItem(randomFish);
                                break;
    
                            default:
                                isFishingWindowOpen = false;
                                break;
                        }
                    } else {
                        if (!isPopupOpen) {
                            System.out.println("POPUP");
                            isPopupOpen = true;
                            Window popup = new Window("", skin);
                            TextButton okayButton = new TextButton("Okay!", skin);
                            
                            okayButton.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    popup.remove();
                                    isPopupOpen = false;
                                }
                            });
                            Label label = new Label("Inventory is full! Sell some items first", skin);

                            popup.add(label);
                            popup.row();
                            popup.add(okayButton);
                            popup.pack();
                            uiStage.addActor(popup);
                        }
                    }

                    // isFishingWindowOpen = true;
                    isFishingWindowOpen = false;
                }
            }
        }
    }

    private void renderFishingWindow() {
        Window fishingWindow = new Window("Catch the fish!", skin);
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fishingWindow.remove();
                isFishingWindowOpen = false;
                System.out.println("CLOSED FISHING WINDOW");
            }
        });

        fishingWindow.add(closeButton);
        fishingWindow.setPosition(game.widthScreen / 2 - fishingWindow.getWidth() / 2, game.heightScreen / 2 - fishingWindow.getHeight() / 2);
        uiStage.addActor(fishingWindow);
    }

    @Override
    public void dispose() {
        batch.dispose();
        box2dDebugRenderer.dispose();
        orthogonalTiledMapRenderer.dispose();
        world.dispose();
        skin.dispose();
        uiStage.dispose();
    }
}
