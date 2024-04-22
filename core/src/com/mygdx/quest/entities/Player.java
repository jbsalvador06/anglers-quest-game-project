package com.mygdx.quest.entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.quest.AnglersQuest;
import com.mygdx.quest.utils.Constants;
import com.mygdx.quest.utils.Fish;
import com.mygdx.quest.utils.ItemSort;

public class Player extends GameEntity{
    
    private final AnglersQuest game;

    private static final float FRAME_TIME = 1 / 4f;
    private float elapsedTime;
    private TextureAtlas atlas;
    private Animation<TextureRegion> upStill, downStill, leftStill, rightStill;
    private Direction lastDirection = Direction.DOWN;

    private float minX, maxX, minY, maxY;

    private ArrayList<Fish> inventory;
    private int coins;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Player(float width, float height, Body body, final AnglersQuest game) {
        /*
         * The PLAYER needs to have the following:
         * POSITION
         * DIRECTION
         * SPEED
         * RADIUS
         * UPGRADES
         * 
         * SPRITE
         * SOUND
         */
        super(width, height, body);

        this.game = game;
        inventory = new ArrayList<>();

        this.speed = 2.5f;
        game.assets.loadPlayer();
        game.assets.getAssetManager().finishLoading();
        this.atlas = game.assets.getPlayerTileset();
        this.upStill = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("up-still"));
        this.downStill = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("down-still"));
        this.leftStill = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("left-still"));
        this.rightStill = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("right-still"));

    }

    @Override
    public void update() {
        x = body.getPosition().x * Constants.PPM;
        y = body.getPosition().y * Constants.PPM;
        elapsedTime += Gdx.graphics.getDeltaTime();

        boolean isMoving = (velX != 0 || velY != 0);

        x = Math.min(Math.max(x, minX), maxX);
        y = Math.min(Math.max(y, minY), maxY);

        if (isMoving) {
            if (velX > 0) {
                lastDirection = Direction.RIGHT;
            } else if (velX < 0) {
                lastDirection = Direction.LEFT;
            } else if (velY > 0) {
                lastDirection = Direction.UP;
            } else if (velY < 0) {
                lastDirection = Direction.DOWN;
            }
        }

        checkUserInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        switch (lastDirection) {
            // Use the first frame for the direction
            case UP:
                currentFrame = upStill.getKeyFrame(0);
                break;
            case DOWN:
                currentFrame = downStill.getKeyFrame(0);
                break;
            case LEFT:
                currentFrame = leftStill.getKeyFrame(0);
                break;
            case RIGHT:
                currentFrame = rightStill.getKeyFrame(0);
                break;
            default:
                currentFrame = downStill.getKeyFrame(0, true);
        }
        // batch.draw(currentFrame, x, y);
        batch.draw(currentFrame, this.getBody().getPosition().x * Constants.PPM - (currentFrame.getRegionWidth() / 2), this.getBody().getPosition().y * Constants.PPM - (currentFrame.getRegionHeight() - 28 / 2));
    }
    
    public TextureRegion getCurrentFrame() {
        switch (lastDirection) {
            // Return first frame of animation
            case UP:
                return upStill.getKeyFrame(0, true);
            case DOWN:
                return downStill.getKeyFrame(0, true);
            case LEFT:
                return leftStill.getKeyFrame(0, true);
            case RIGHT:
                return rightStill.getKeyFrame(0, true);
            default:
                return downStill.getKeyFrame(0, true);
        }
    }

    private void checkUserInput() {
        velX = 0;
        velY = 0;

        if (Gdx.input.isKeyPressed(Keys.A)) {
            velX = -1;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            velX = 1;
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
            velY = -1;
        }
        if (Gdx.input.isKeyPressed(Keys.W)) {
            velY = 1;
        }

        if (velX > 0) {
            lastDirection = Direction.RIGHT;
        } else if (velX < 0) {
            lastDirection = Direction.LEFT;
        } else if (velY > 0) {
            lastDirection = Direction.UP;
        } else if (velY < 0) {
            lastDirection = Direction.DOWN;
        }

        body.setLinearVelocity(velX * speed, velY * speed);
    }

    public void addItem(Fish fish) {
        inventory.add(fish);
    }

    public void removeItem(Fish fish) {
        inventory.remove(fish);
    }

    public ArrayList<Fish> getInventory() {
        return inventory;
    }
    
    public void sortInventory() {
        inventory = ItemSort.sort(inventory);
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins += coins;
    }

}
