package com.mygdx.quest.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.quest.AnglersQuest;
import com.mygdx.quest.entities.Player;
import com.mygdx.quest.screens.GameScreen;

public class TileMapHelper {
    
    private final AnglersQuest game;
    private Assets assets;
    private TiledMap tiledMap;
    private GameScreen gameScreen;

    public TileMapHelper(GameScreen gameScreen, final AnglersQuest game) {
        this.game = game;
        this.assets = game.assets;
        this.gameScreen = gameScreen;

        assets.loadMap();
    }

    public OrthogonalTiledMapRenderer setupMap() {
        tiledMap = game.assets.getAssetManager().get(Assets.MAP);
        parseMapObjects(tiledMap.getLayers().get("Obstacles").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void parseMapObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
                String rectangleName = mapObject.getName();

                if (rectangleName.equals("Player")) {
                    Body body = BodyHelper.createBody(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2, rectangle.getWidth() / 2, rectangle.getHeight() / 2, false, gameScreen.getWorld());
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body, game));
                }
            }
        }
    }

}
