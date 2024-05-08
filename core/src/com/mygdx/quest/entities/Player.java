package com.mygdx.quest.entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.quest.utils.Constants;
import com.mygdx.quest.utils.Fish;
import com.mygdx.quest.utils.ItemSort;

public class Player extends GameEntity{

    private Sound footSteps;
    private long footStepsID;
    private boolean footStepsPlaying = false;

    private static final float FRAME_TIME = 1 / 4f;
    private float elapsedTime;
    private TextureAtlas atlas;
    private Animation<TextureRegion> upStill, downStill, leftStill, rightStill, walkUp, walkDown, walkLeft, walkRight;
    private Direction lastDirection = Direction.DOWN;

    private ArrayList<Fish> inventory;
    private ArrayList<String> upgradesInventory;
    private int coins;
    
    public static Body body;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Player(float width, float height, Body body) {
        super(width, height, body);
        this.speed = 4.5f;
        Player.body = body;
        
        // Animation
        this.atlas = new TextureAtlas("player\\new-new-player\\playerMove.atlas");
        this.upStill = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("up-still"));
        this.downStill = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("down-still"));
        this.leftStill = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("left-still"));
        this.rightStill = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("right-still"));
        this.walkUp = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("up-walk"));
        this.walkDown = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("down-walk"));
        this.walkLeft = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("left-walk"));
        this.walkRight = new Animation<TextureRegion>(FRAME_TIME, atlas.findRegions("right-walk"));

        inventory = new ArrayList<>();
        upgradesInventory = new ArrayList<>();

        footSteps = Gdx.audio.newSound(Gdx.files.internal("sounds\\walkSFX.mp3"));
        footSteps.setVolume(footStepsID, 0.1f);
        
        body.getFixtureList().first().setUserData(this);

    }

    @Override
    public void update() {
        x = body.getPosition().x * Constants.PPM - width / 2;
        y = body.getPosition().y * Constants.PPM - height / 2;
        
        elapsedTime += Gdx.graphics.getDeltaTime();

        boolean isMoving = (velX != 0 || velY != 0);

        if (isMoving) {
            if (Math.abs(velX) > Math.abs(velY)) {
                if (velX > 0) {
                    lastDirection = Direction.RIGHT;
                } else {
                    lastDirection = Direction.LEFT;
                }
            } else {
                if (velY > 0) {
                    lastDirection = Direction.UP;
                } else {
                    lastDirection = Direction.DOWN;
                }
            }

            if (!footStepsPlaying) {
                footStepsID = footSteps.loop(0.6f, 0.8f, 0);
                footStepsPlaying = true;
            }
        } else {
            if (footStepsPlaying) {
                footSteps.stop(footStepsID);
                footStepsPlaying = false;
            }
        }

        checkUserInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        if  (velX > 0) {
            currentFrame = walkRight.getKeyFrame(elapsedTime, true);
        } else if (velX < 0) {
            currentFrame = walkLeft.getKeyFrame(elapsedTime, true);
        } else if (velY > 0) {
            currentFrame = walkUp.getKeyFrame(elapsedTime, true);
        } else if (velY < 0) {
            currentFrame = walkDown.getKeyFrame(elapsedTime, true);
        } else {
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
        }
        
        // batch.draw(currentFrame, x, y);
        batch.draw(currentFrame, this.getBody().getPosition().x * Constants.PPM - (currentFrame.getRegionWidth() / 2), this.getBody().getPosition().y * Constants.PPM - (currentFrame.getRegionHeight() / 5));
    }
    
    public TextureRegion getCurrentFrame() {
        if (velX > 0) {
            return walkRight.getKeyFrame(elapsedTime, true);
        } else if (velX < 0) {
            return walkLeft.getKeyFrame(elapsedTime, true);
        } else if (velY > 0) {
            return walkUp.getKeyFrame(elapsedTime, true);
        } else if (velY < 0) {
            return walkDown.getKeyFrame(elapsedTime, true);
        } else {
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

    public void addUpgrades(String name) {
        upgradesInventory.add(name);
    }

    public ArrayList<String> getUpgrades() {
        return upgradesInventory;
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

    public void dispose() {
        atlas.dispose();
        footSteps.dispose();
    }

}
