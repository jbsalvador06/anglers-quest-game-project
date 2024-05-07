package com.mygdx.quest.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import static com.mygdx.quest.utils.Constants.PPM;

public class Tent extends GameEntity {
    
    public Body body;

    public Tent(float width, float height, Body body) {
        super(width, height, body);

        Fixture fixture = body.getFixtureList().first();
        fixture.setUserData(this);
        fixture.setSensor(false);
    }

    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
    }

    @Override
    public void render(SpriteBatch batch) {

    }

}
