package com.mygdx.quest.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraHandler {

    public static void lockOnTarget(Camera camera, Vector2 target) {
        Vector3 position = camera.position;
        position.x = Math.round(target.x * Constants.PPM * 10) / 10f;
        position.y = Math.round(target.y * Constants.PPM * 10) / 10f;
        camera.position.set(position);
        camera.update();
    }

}
