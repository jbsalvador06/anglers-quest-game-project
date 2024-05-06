package com.mygdx.quest.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.quest.entities.Player;
import com.mygdx.quest.entities.Pond;
import com.mygdx.quest.entities.River;
import com.mygdx.quest.screens.GameScreen;

public class TileMapHelper {

    private TiledMap tiledMap;
    private GameScreen gameScreen;

    public TileMapHelper(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer setupMap() {
        // tiledMap = AnglersQuest.assets.getMap();
        tiledMap = new TmxMapLoader().load("maps/map.tmx");
        parseMapObjects(tiledMap.getLayers().get("Obstacles").getObjects());
        parseMapObjects(tiledMap.getLayers().get("WaterBody").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void parseMapObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {

            if (mapObject instanceof PolygonMapObject) {
                createStaticBody((PolygonMapObject) mapObject);
            }

            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
                String rectangleName = mapObject.getName();

                if (rectangleName.equals("Player")) {
                    Body body = BodyHelper.createBody(
                        rectangle.getX() + rectangle.getWidth() / 2, 
                        rectangle.getY() + rectangle.getHeight() / 2, 
                        rectangle.getWidth() / 2,
                        rectangle.getHeight() / 2,
                        false,
                        gameScreen.getWorld()
                    );
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body));
                }

                // To create a water body for fishing
                if (rectangleName.equals("Pond")) {
                    Body body = BodyHelper.createBody(
                        rectangle.getX() + rectangle.getWidth() / 2,
                        rectangle.getY() + rectangle.getHeight() / 2,
                        rectangle.getWidth(),
                        rectangle.getHeight(),
                        true,
                        gameScreen.getWorld()
                    );
                    gameScreen.setPond(new Pond(rectangle.getWidth(), rectangle.getHeight(), body));
                }

                if (rectangleName.equals("River")) {
                    Body body = BodyHelper.createBody(
                        rectangle.getX() + rectangle.getWidth() / 2,
                        rectangle.getY() + rectangle.getHeight() / 2,
                        rectangle.getWidth(),
                        rectangle.getHeight(),
                        true,
                        gameScreen.getWorld()
                    );
                    gameScreen.setRiver(new River(rectangle.getWidth(), rectangle.getHeight(), body));
                }
            }
        }
    }

    private void createStaticBody(PolygonMapObject polygonMapObject) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.createFixture(shape, 1000);
        shape.dispose();        
    }

    private Shape createPolygonShape(PolygonMapObject polygonmaMapObject) {
        float[] vertices = polygonmaMapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];
        
        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / Constants.PPM, vertices[i * 2 + 1] / Constants.PPM);
            worldVertices[i] = current;
        }

        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }

}
