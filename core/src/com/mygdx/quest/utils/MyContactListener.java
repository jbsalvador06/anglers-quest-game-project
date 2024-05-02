package com.mygdx.quest.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.quest.entities.Player;
import com.mygdx.quest.entities.Pond;

public class MyContactListener implements ContactListener {
    
    public boolean pondInteract = false;

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if ((fa.getUserData() instanceof Player && fb.getUserData() instanceof Pond) ||
            (fa.getUserData() instanceof Pond && fb.getUserData() instanceof Player)) {
            
            // Pond's body
            Fixture itemFixture = fa.getUserData() instanceof Pond ? fa : fb;
            itemFixture.getUserData();
            
            System.out.println("Detected player-pond contact");
            System.out.println(fa.getUserData());
            System.out.println(fb.getUserData());

            pondInteract = true;
            System.out.println(pondInteract);
        }
    }

    @Override
    public void endContact(Contact contact) {
        pondInteract = false;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        
    }

}
