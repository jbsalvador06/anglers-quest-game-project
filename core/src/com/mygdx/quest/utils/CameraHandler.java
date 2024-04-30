package com.mygdx.quest.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraHandler {
    
    public static void lockOnTarget(Camera camera, Vector2 target) {
        Vector3 position = camera.position;
        position.x = target.x;
        position.y = target.y;
        camera.position.set(position);
        camera.update();
    }

    public static void freeRoam(Camera camera, Vector2 target, Viewport viewport) {
        Vector3 position = camera.position;
        position.x = target.x;
        position.y = target.y;
        camera.position.set(position);

        float leftLimit = 0;
        float rightLimit = viewport.getWorldWidth();
        float bottomLimit = 0;
        float topLimit = viewport.getWorldHeight();

        if (camera.position.x < leftLimit) {
            camera.position.x = leftLimit;
        } else if (camera.position.x > rightLimit) {
            camera.position.x = rightLimit;
        }
    
        if (camera.position.y < bottomLimit) {
            camera.position.y = bottomLimit; 
        } else if (camera.position.y > topLimit) {
            camera.position.y = topLimit;
        }

        camera.update();
    }

    public static void limitCamera(Camera camera, Vector2 target, float mapWidth, float mapHeight) {
        float camX = Math.min(Math.max(target.x, camera.viewportWidth / 2), mapWidth - camera.viewportWidth / 2);
        float camY = Math.min(Math.max(target.y, camera.viewportHeight / 2), mapHeight - camera.viewportHeight / 2);

        camera.position.set(camX, camY, 0);
        camera.update();
    }

}
