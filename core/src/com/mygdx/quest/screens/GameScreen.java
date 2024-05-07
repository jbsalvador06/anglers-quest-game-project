package com.mygdx.quest.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.quest.AnglersQuest;
import com.mygdx.quest.entities.Player;
import com.mygdx.quest.entities.Pond;
import com.mygdx.quest.entities.River;
import com.mygdx.quest.entities.Tent;
import com.mygdx.quest.utils.BodyHelper;
import com.mygdx.quest.utils.CameraHandler;
import com.mygdx.quest.utils.Constants;
import com.mygdx.quest.utils.Fish;
import com.mygdx.quest.utils.FishParser;
import com.mygdx.quest.utils.MyContactListener;
import com.mygdx.quest.utils.TileMapHelper;

import de.eskalon.commons.screen.ManagedScreenAdapter;

public class GameScreen extends ManagedScreenAdapter implements FishingScreen.FishingCallback {

    // ADD INSTRUCTIONS TO FISHING SCREEN
    // ADD MORE SOUNDS
    // ADD UPGRADES
    // ADD STORY ???
    // After collecting all the upgrades/collectibles, point the player towards the tent, then show the player's stats:
    // Time Played
    // Number of Times Fished
    // Fish Caught
    // Fish Discovered
    // Upgrades/Collectibles Found

    private final AnglersQuest game;

    // Shop Items
    private Map<String, Integer> upgrades;
    private boolean isShopWindowOpen = false;
    private boolean isSellWindowOpen = false;
    // CREATE UPGRADES.JSON AND UPGRADES PARSER

    // For UI elements
    private Stage uiStage;
    private Table mainTable;
    TextButton shopButton, inventoryButton, sellButton;
    private boolean isFishingWindowOpen = false;
    private boolean isPopupOpen = false;
    private boolean isUIinitialized = false;
    private boolean isInstructionsOpen = false;
    private boolean isFailedWindowOpen = false;

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
    private BitmapFont font;
    private static Music mainMenuMusic, gameMusic;
    private Sound splashScreenSound;
    private Sound buttonClick;
    private Sound successSFX;
    private Sound failSFX;

    // Game Objects
    private Player player;
    private Pond pond;
    private boolean pondKey = false;
    private River river;
    private boolean riverKey = false;
    private Tent tent;
    private boolean tentKey = false; 
    private boolean isInventoryWindowOpen = false;
    
    float elapsedTime = 0.0f;
    float displayDuration = 5f;

    // Keep track of progress
    private float totalTimePlayed = 0f;
    private int totalTimesFished = 0;
    private ArrayList<Fish> fishCaught = new ArrayList<>();
    private Set<String> fishDiscovered = new HashSet<>();
    private Set<String> upgradesCollected = new HashSet<>();

    private void updateTimePLayed(float delta) {
        totalTimePlayed += delta;
    }

    private void incrementTimesFished() {
        totalTimesFished++;
    }

    private void addCaughtFish(Fish fish) {
        fishCaught.add(fish);
    }

    private void addDiscoveredFish(String fishName) {
        fishDiscovered.add(fishName);
    }

    private void addCollectedUpgrade(String upgrade) {
        upgradesCollected.add(upgrade);
    }

    public GameScreen(OrthographicCamera camera, final AnglersQuest game) {

        this.game = game;

        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera = camera;
        this.batch = new SpriteBatch();

        this.contactListener = new MyContactListener();

        // For UI elements
        this.uiStage = new Stage(new ScreenViewport(new OrthographicCamera()));
        // this.uiStage.setDebugAll(true);
        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        // this.mainTable.debugAll();

        // Box2D
        this.world = new World(new Vector2(0, 0), false);
        this.world.setContactListener(contactListener);
        this.box2dDebugRenderer = new Box2DDebugRenderer(
                true, true, true, true, true, true);

        this.tileMapHelper = new TileMapHelper(this);
        this.orthogonalTiledMapRenderer = tileMapHelper.setupMap();

        // Add upgrades to shop
        upgrades = new HashMap<>();
        upgrades.put("Bait", 50);
        upgrades.put("Hook", 100);
        upgrades.put("Tackle-Box", 250);
        upgrades.put("Bobber", 350);
        upgrades.put("Fishing-Line", 500);

        // Audio
        GameScreen.mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/mainMenuSFX.mp3"));
        splashScreenSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/splashScreenSFX.mp3"));
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/buttonClickSFX.mp3"));
        successSFX = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/successSFX.mp3"));
        failSFX = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/failSFX.mp3"));

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
        CameraHandler.lockOnTarget(camera, player.getBody().getPosition());
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

        if (!isInstructionsOpen) {
            renderFishingInstruction();
        }

        if (!isUIinitialized) {
            mainTable.clear();
            initUI();
            initUIButtonListeners();
            isUIinitialized = false;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(Color.valueOf("#80b782"));

        // UPDATE TIME PLAYED
        updateTimePLayed(delta);

        // THIS HAS TO BE HERE OR ELSE
        // THE UI WILL RENDER BELOW THE MAP
        uiStage.act(delta);

        int[] backgroundLayers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 };
        int [] foregroundLayers = { 19 };
        orthogonalTiledMapRenderer.render(backgroundLayers);

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

        orthogonalTiledMapRenderer.render(foregroundLayers);

        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        game.widthScreen = width;
        game.heightScreen = height;
        // For UI elements
        uiStage.getViewport().update(width, height, true);
    }

    private void initUI() {
        isUIinitialized = true;
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
                    buttonClick.play();
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
                    buttonClick.play();
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
                    buttonClick.play();
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
                buttonClick.play();
            }
        });

        int count = 0;
        Label coins = new Label("Coins: " + player.getCoins(), skin);
        updateCoinsLabel(coins);

        shopWindow.add(coins).colspan(3);
        shopWindow.row();
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

            Texture upgradesTexture = new Texture(
                    Gdx.files.internal((String) "assets/upgrades/" + upgradeName.toLowerCase() + ".png"));
            TextureRegionDrawable resizedTexture = new TextureRegionDrawable(
                    new TextureRegion(upgradesTexture, 0, 0, upgradesTexture.getWidth(), upgradesTexture.getHeight()));
            resizedTexture.setMinWidth(100);
            resizedTexture.setMinHeight(100);
            ImageButton upgradesButton = new ImageButton(resizedTexture);

            TextTooltip upgradesTooltip = new TextTooltip("Name: " + upgradeName + "\nPrice: " + upgradePrice, skin); 
            upgradesTooltip.setInstant(true);
            upgradesButton.addListener(upgradesTooltip);

            upgradesButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.getCoins() >= upgradePrice) {
                        player.setCoins(-upgradePrice);
                        upgrades.remove(upgradeName);
                        upgradesButton.remove();
                        player.addUpgrades(upgradeName);
                        addCollectedUpgrade(upgradeName);
                        updateCoinsLabel(coins);
                    } else {
                        if (!isPopupOpen) {
                            System.out.println("POPUP");
                            isPopupOpen = true;
                            shopWindow.remove();
                            isShopWindowOpen = false;
                            Window popup = new Window("Shop", skin);
                            TextButton okayButton = new TextButton("Okay!", skin);
                            
                            okayButton.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    popup.remove();
                                    isPopupOpen = false;
                                }
                            });
                            Label label = new Label("Not enough coins!", skin);

                            popup.add(label).pad(50);
                            popup.row();
                            popup.add(okayButton);
                            popup.pack();
                            popup.setPosition(game.widthScreen / 2 - popup.getWidth() / 2, game.heightScreen / 2 - popup.getHeight() / 2);
                            uiStage.addActor(popup);
                        }
                    }
                }
            });

            shopWindow.add(upgradesButton).pad(5);
            count++;

            if (count % 3 == 0) {
                shopWindow.row();
            }
        }

        shopWindow.defaults().pad(10);
        shopWindow.row();
        shopWindow.add(closeButton).colspan(3);
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

        Label coins = new Label("Coins: " + player.getCoins(), skin);
        updateCoinsLabel(coins);
        inventoryWindow.add(coins).colspan(3);
        inventoryWindow.row();

        Label fishLabel = new Label("Fish:", skin);
        inventoryWindow.add(fishLabel).colspan(3).row();
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
                inventoryWindow.add(fishButton).pad(5);
                count++;
                
                if (count % 3 == 0) {
                    inventoryWindow.row();
                }

                TextTooltip fishTooltip = new TextTooltip("Name: " + fish.getName() + "\nPrice: " + fish.getPrice() + "\nRarity: " + fish.getRarity() + "\nDescription: \n" + fish.getDescription(), skin, "default");
                fishTooltip.setInstant(true);
                fishButton.addListener(fishTooltip);

            }
        } else {
            Label noItemsLabel = new Label("Inventory is empty :(" + "\nCatch more fish!", skin);
            inventoryWindow.add(noItemsLabel).colspan(3);
        }

        inventoryWindow.row();

        Label upgradesLabel = new Label("Upgrades:", skin);
        inventoryWindow.add(upgradesLabel).colspan(3).row();
        if (!player.getUpgrades().isEmpty()) {
            int count = 0;
            for (String upgrade : player.getUpgrades()) {
                Texture upgradesTexture = new Texture(Gdx.files.internal("assets/upgrades/" + upgrade.toLowerCase() + ".png"));

                TextureRegionDrawable resizedTexture = new TextureRegionDrawable(
                        new TextureRegion(upgradesTexture, 0, 0, upgradesTexture.getWidth(), upgradesTexture.getHeight()));
                resizedTexture.setMinWidth(100);
                resizedTexture.setMinHeight(100);
                ImageButton fishButton = new ImageButton(resizedTexture);
                inventoryWindow.add(fishButton).pad(5);
                count++;

                if (count % 3 == 0) {
                    inventoryWindow.row();
                }

                TextTooltip fishTooltip = new TextTooltip("Name: " + upgrade, skin);
                fishTooltip.setInstant(true);
                fishButton.addListener(fishTooltip);

            }
        } else {
            Label noCollectiblesLabel = new Label("Upgrades are empty :(", skin);
            inventoryWindow.add(noCollectiblesLabel).colspan(3);
        }

        inventoryWindow.defaults().pad(10);
        inventoryWindow.row();
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

        Label coins = new Label("Coins: " + player.getCoins(), skin);
        updateCoinsLabel(coins);
        sellWindow.add(coins).colspan(3);
        sellWindow.row();

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
                sellWindow.add(fishButton).pad(5);
                count++;

                if (count % 3 == 0) {
                    sellWindow.row();
                }

                TextTooltip fishTooltip = new TextTooltip("Name: " + fish.getName() + "\nPrice: " + fish.getPrice() + "\nRarity: " + fish.getRarity() + "\nDescription: \n" + fish.getDescription(), skin);
                fishTooltip.setInstant(true);
                fishButton.addListener(fishTooltip);

                fishButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        fishButton.remove();
                        player.setCoins(fish.getPrice());
                        player.removeItem(fish);
                        updateCoinsLabel(coins);
                    }
                });
            }
        } else {
            Label noItemsLabel = new Label("Inventory is empty :(" + "\nCatch more fish!", skin);
            sellWindow.add(noItemsLabel).colspan(3);
        }

        sellWindow.defaults().pad(10);
        sellWindow.row();
        sellWindow.add(closeButton).colspan(3);
        sellWindow.pack();
        sellWindow.setPosition(game.widthScreen / 2 - sellWindow.getWidth() / 2,
                game.heightScreen / 2 - sellWindow.getHeight() / 2);
        uiStage.addActor(sellWindow);
    }

    private void updateCoinsLabel(Label coinsLabel) {
        coinsLabel.setText("Coins: " + player.getCoins());
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

    public void setRiver(River river) {
        this.river = river;
    }

    public void setTent(Tent tent) {
        this.tent = tent;
    }

    @Override
    public void pause() {
        System.out.println("GameScreen Paused");
    }

    private void renderFishingInstruction() {
        isInstructionsOpen = true;
        Window fishingInstruction = new Window("Instructions:", skin);
        Label instruction = new Label(
            "*Press (E) near a body of water" + 
            "\n*Catch a variety of fish" +
            "\n*Sell them for coins" +
            "\n*Buy some upgrades/collectibles" +
            "\n*REPEAT!" +
            "\n*Be sure to check out the tent", 
            skin);
        TextButton closeButton = new TextButton("Gotcha!", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fishingInstruction.remove();
            }
        });

        fishingInstruction.defaults().pad(10);
        fishingInstruction.add(instruction).row();
        fishingInstruction.add(closeButton).row();
        fishingInstruction.pack();
        fishingInstruction.setPosition(30, game.heightScreen - 30);
        uiStage.addActor(fishingInstruction);
    }

    private void handleItemInteraction() {
        if (Gdx.input.isKeyJustPressed(Keys.E)) {
            if (contactListener.pondInteract || contactListener.riverInteract) {
                pondKey = true;
                riverKey = true;
                
                Map<String, Fish> fishes = FishParser.parseFishJson("core/src/com/mygdx/quest/utils/fish.json");


                if (!isFishingWindowOpen) {
                    System.out.println("FISHING");
                    if (player.getInventory().size() < 9) {
                        if (totalTimesFished < 3) {
                            Fish randomFish = FishParser.getRandomCommonFish(fishes);
                            System.out.println(randomFish.getName() + " " + randomFish.getRarity());

                            System.out.println(randomFish.getRarity());
                            isFishingWindowOpen = true;

                            game.setScreen(new FishingScreen(game, randomFish, this, player));
                        } else {
                            Fish randomFish = FishParser.getRandomFish(fishes);
                            System.out.println(randomFish.getName() + " " + randomFish.getRarity());

                            switch (randomFish.getRarity()) {
                                case COMMON:
                                    System.out.println(randomFish.getRarity());
                                    isFishingWindowOpen = true;
        
                                    game.setScreen(new FishingScreen(game, randomFish, this, player));
    
                                    break;
                                case RARE:
                                    System.out.println(randomFish.getName());
                                    isFishingWindowOpen = true;
                                    
                                    game.setScreen(new FishingScreen(game, randomFish, this, player));
    
                                    break;
                                case LEGENDARY:
                                    System.out.println(randomFish.getName());
                                    isFishingWindowOpen = true;
        
                                    game.setScreen(new FishingScreen(game, randomFish, this, player));
    
                                    break;
        
                                default:
                                    isFishingWindowOpen = false;
                                    break;
                            }
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

                            popup.defaults().pad(10);
                            popup.add(label);
                            popup.row();
                            popup.add(okayButton);
                            popup.pack();
                            popup.setPosition(game.widthScreen / 2 - popup.getWidth() / 2, game.heightScreen / 2 - popup.getHeight() / 2);
                            uiStage.addActor(popup);
                        }
                    }

                    // isFishingWindowOpen = true;
                    isFishingWindowOpen = false;
                }
            }
            if (contactListener.tentInteract) {
                System.out.println("Detected player-tent collision");
                tentKey = true;
                
                Window statisticsWindow = new Window("Statistics", skin);
                statisticsWindow.defaults().pad(10);

                Label timePlayed = new Label("Time Played: " + totalTimePlayed + " seconds", skin);
                Label timesFished = new Label("Number of Times Fished: " + totalTimesFished, skin);
                Label fishCaught = new Label("Fish Caught: " + this.fishCaught.size(), skin);
                Label fishDiscovered = new Label("Fish Discovered: " + this.fishDiscovered.size(), skin);
                Label upgradesCollected = new Label("Upgrades/Collectibles Found: " + this.upgradesCollected.size(), skin);

                statisticsWindow.add(timePlayed).row();
                statisticsWindow.add(timesFished).row();
                statisticsWindow.add(fishCaught).row();
                statisticsWindow.add(fishDiscovered).row();
                statisticsWindow.add(upgradesCollected).row();

                TextButton closeButton = new TextButton("Close", skin);
                closeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        statisticsWindow.remove();
                    }
                });
                statisticsWindow.add(closeButton).row();
                
                // Position and add the window to the stage
                statisticsWindow.pack();
                statisticsWindow.setPosition(game.widthScreen / 2 - statisticsWindow.getWidth() / 2, game.heightScreen / 2 - statisticsWindow.getHeight() / 2);
                uiStage.addActor(statisticsWindow);
            }
        }
    }

    private void renderSuccessWindow(Fish fish) {
        Window successWindow = new Window("Congratulations!", skin);
        Label successLabel = new Label("You have caught a " + fish.getName(), skin);
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                successWindow.remove();
            }
        });
        Label descriptionLabel = new Label("\"" + fish.getDescription() + "\"", skin);
        Label priceLabel = new Label("Price: " + fish.getPrice(), skin);

        Texture fishTexture = new Texture(Gdx.files.internal(fish.getImgUrl()));
        TextureRegionDrawable resizedTexture = new TextureRegionDrawable(
                new TextureRegion(fishTexture, 0, 0, fishTexture.getWidth(), fishTexture.getHeight()));
        resizedTexture.setMinWidth(100);
        resizedTexture.setMinHeight(100);
        Image fishImage = new Image(resizedTexture);

        successWindow.defaults().pad(10);
        successWindow.add(successLabel).row();
        successWindow.add(fishImage).pad(50).row();
        successWindow.add(descriptionLabel).row();
        successWindow.add(priceLabel).row();
        successWindow.add(closeButton);
        successWindow.pack();
        successWindow.setPosition(game.widthScreen / 2 - successWindow.getWidth() / 2, game.heightScreen / 2 - successWindow.getHeight() / 2);
        uiStage.addActor(successWindow);
    }

    private void renderFailedWindow(Fish fish) {

        int coinDeduction = 0;
        if (totalTimesFished < 5) {
            coinDeduction = 0;
        } else {
            switch (fish.getRarity()) {
                case COMMON:
                    coinDeduction = 10;
                    break;
                case RARE:
                    coinDeduction = 15;
                    break;
                case LEGENDARY:
                    coinDeduction = 20;
                    break;
            }
        }

        if (totalTimePlayed < 3) {
            if (player.getCoins() <= 0) {
                isFailedWindowOpen = true;
                // Game over condition
                Window gameOverWindow = new Window("Game Over!", skin);
                Label gameOverLabel = new Label("You have lost all your coins!", skin);
                TextButton restartButton = new TextButton("Restart", skin);
                restartButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        // Reset the game state or go back to the main menu
                        System.out.println("GAME OVER");
                        isFailedWindowOpen = false;
                        game.dispose();
                        game.create();
                        game.setScreen(new LoadingScreen(game));
                    }
                });
        
                gameOverWindow.defaults().pad(10);
                gameOverWindow.add(gameOverLabel).row();
                gameOverWindow.add(restartButton).row();
                gameOverWindow.pack();
                gameOverWindow.setPosition(game.widthScreen / 2 - gameOverWindow.getWidth() / 2, game.heightScreen / 2 - gameOverWindow.getHeight() / 2);
                uiStage.addActor(gameOverWindow);
            }
        }

        if (!isFailedWindowOpen) {
            Window failedWindow = new Window("Better luck next time!", skin);
            Label failedLabel = new Label("You failed to catch a " + fish.getName(), skin);
            TextButton closeButton = new TextButton("Close", skin);
            closeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    failedWindow.remove();
                }
            });

            Texture fishTexture = new Texture(Gdx.files.internal(fish.getImgUrl()));
            TextureRegionDrawable resizedTexture = new TextureRegionDrawable(
                    new TextureRegion(fishTexture, 0, 0, fishTexture.getWidth(), fishTexture.getHeight()));
            resizedTexture.setMinWidth(100);
            resizedTexture.setMinHeight(100);
            Image fishImage = new Image(resizedTexture);

            Label coinDeductionLabel = new Label("You lost " + coinDeduction + " coins!", skin);

            failedWindow.defaults().pad(10);
            failedWindow.add(failedLabel).row();
            failedWindow.add(fishImage).pad(50).row();
            failedWindow.add(coinDeductionLabel).row();
            failedWindow.add(closeButton);
            failedWindow.pack();
            failedWindow.setPosition(game.widthScreen / 2 - failedWindow.getWidth() / 2, game.heightScreen / 2 - failedWindow.getHeight() / 2);
            uiStage.addActor(failedWindow);
        }
    }

    @Override
    public void onFishingCompleted(boolean isFishCaught, Fish randomFish) {
        if (isFishCaught) {
            player.addItem(randomFish);
            System.out.println("Fish caught: " + randomFish.getName());
            incrementTimesFished();
            addCaughtFish(randomFish);
            addDiscoveredFish(randomFish.getName());
            successSFX.play();
            renderSuccessWindow(randomFish);
        } else {
            System.out.println("No fish was caught");
            incrementTimesFished();
            addDiscoveredFish(randomFish.getName());
            failSFX.play();
            renderFailedWindow(randomFish);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        box2dDebugRenderer.dispose();
        orthogonalTiledMapRenderer.dispose();
        world.dispose();
        skin.dispose();
        uiStage.dispose();
        splashScreenSound.dispose();
        successSFX.dispose();
    }
}
